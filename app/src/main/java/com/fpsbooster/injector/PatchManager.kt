package com.fpsbooster.injector

import android.content.Context
import com.fpsbooster.model.GameConfig
import com.fpsbooster.network.ConfigService
import com.fpsbooster.shizuku.ShizukuHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PatchManager(private val context: Context) {

    /**
     * Patch faylni o'yin papkasiga o'rnatadi
     */
    suspend fun installPatch(gameConfig: GameConfig, onProgress: (String) -> Unit): Result<String> = withContext(Dispatchers.IO) {
        try {
            // 1. Shizuku ruxsatini tekshirish
            if (!ShizukuHelper.hasPermission()) {
                return@withContext Result.failure(Exception("Shizuku ruxsati berilmagan! Avval ruxsat bering."))
            }

            onProgress("Patch fayl yuklab olinmoqda...")
            val tempFile = File(context.cacheDir, gameConfig.fileName)
            val downloadResult = ConfigService.downloadPatchFile(gameConfig.downloadUrl, tempFile)

            if (downloadResult.isFailure) {
                return@withContext Result.failure(downloadResult.exceptionOrNull() ?: Exception("Yuklab olishda xatolik"))
            }

            onProgress("O'yin papkasiga nusxalanmoqda...")
            val targetDirPath = "/storage/emulated/0/Android/data/${gameConfig.packageName}/${gameConfig.targetDir}"
            val targetFilePath = "$targetDirPath/${gameConfig.fileName}"

            // Papka mavjud bo'lmasa yaratish
            ShizukuHelper.runShellCommand("mkdir -p \"$targetDirPath\"")

            // Keshdagi faylni o'yin papkasiga ko'chirish
            val copyCmd = "cp \"${tempFile.absolutePath}\" \"$targetFilePath\" && chmod 666 \"$targetFilePath\""
            val shellResult = ShizukuHelper.runShellCommand(copyCmd)

            // Vaqtinchalik faylni tozalash
            tempFile.delete()

            if (shellResult.isSuccess) {
                Result.success("Muvaffaqiyatli o'rnatildi: ${gameConfig.fileName}")
            } else {
                Result.failure(shellResult.exceptionOrNull() ?: Exception("Faylni ko'chirishda xatolik"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * O'rnatilgan patch faylni o'chirib, o'yinni asl holatiga qaytaradi
     */
    suspend fun removePatch(gameConfig: GameConfig): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!ShizukuHelper.hasPermission()) {
                return@withContext Result.failure(Exception("Shizuku ruxsati berilmagan!"))
            }

            val targetFilePath = "/storage/emulated/0/Android/data/${gameConfig.packageName}/${gameConfig.targetDir}/${gameConfig.fileName}"
            val removeCmd = "rm -f \"$targetFilePath\""
            val shellResult = ShizukuHelper.runShellCommand(removeCmd)

            if (shellResult.isSuccess) {
                Result.success("Patch muvaffaqiyatli olib tashlandi (Asl holat tiklandi)!")
            } else {
                Result.failure(shellResult.exceptionOrNull() ?: Exception("O'chirishda xatolik"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
