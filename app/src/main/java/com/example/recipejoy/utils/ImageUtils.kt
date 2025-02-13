package com.example.recipejoy.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageUtils {
    fun saveImageToInternalStorage(context: Context, imageUri: Uri): String? {
        return try {
            val fileName = "recipe_${UUID.randomUUID()}.jpg"
            val directory = File(context.filesDir, "recipe_images")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val file = File(directory, fileName)

            context.contentResolver.openInputStream(imageUri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteImage(imagePath: String) {
        try {
            File(imagePath).delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}