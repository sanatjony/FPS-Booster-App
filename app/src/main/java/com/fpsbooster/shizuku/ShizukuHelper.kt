package com.fpsbooster.shizuku

import android.content.pm.PackageManager
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader

object ShizukuHelper {

    private const val REQUEST_CODE_SHIZUKU_PERMISSION = 1001

    fun isShizukuAvailable(): Boolean {
        return Shizuku.pingBinder()
    }

    fun hasPermission(): Boolean {
        if (!isShizukuAvailable()) return false
        return if (Shizuku.isPre_V11()) {
            false
        } else {
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermission(onRequestResult: (Boolean) -> Unit) {
        if (hasPermission()) {
            onRequestResult(true)
            return
        }

        val listener = object : Shizuku.OnRequestPermissionResultListener {
            override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                if (requestCode == REQUEST_CODE_SHIZUKU_PERMISSION) {
                    Shizuku.removeRequestPermissionResultListener(this)
                    onRequestResult(grantResult == PackageManager.PERMISSION_GRANTED)
                }
            }
        }

        Shizuku.addRequestPermissionResultListener(listener)
        Shizuku.requestPermission(REQUEST_CODE_SHIZUKU_PERMISSION)
    }

    /**
     * Shizuku orqali tizim shell (ADB) buyrug'ini bajaradi.
     * Bu orqali Android 11+ da /Android/data/ papkasiga to'liq kirish mumkin.
     */
    fun runShellCommand(command: String): Result<String> {
        return try {
            val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))

            val output = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }

            val errorOutput = StringBuilder()
            while (errorReader.readLine().also { line = it } != null) {
                errorOutput.append(line).append("\n")
            }

            val exitCode = process.waitFor()
            if (exitCode == 0) {
                Result.success(output.toString().trim())
            } else {
                Result.failure(Exception("Shell xatoligi ($exitCode): ${errorOutput.toString().trim()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
