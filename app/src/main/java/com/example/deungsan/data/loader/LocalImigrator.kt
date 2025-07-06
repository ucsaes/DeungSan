package com.example.deungsan.data.loader

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import android.util.Log

fun copyAssetsFolder(context: Context, assetFolderName: String, targetFolderName: String) {
    val assetManager = context.assets
    val files = assetManager.list(assetFolderName) ?: return
    val targetDir = File(context.filesDir, targetFolderName)

    if (!targetDir.exists()) {
        targetDir.mkdirs()
    }

    for (fileName in files) {
        val inputStream = assetManager.open("$assetFolderName/$fileName")
        val outFile = File(targetDir, fileName)
        val outputStream = FileOutputStream(outFile)

        inputStream.copyTo(outputStream)

        inputStream.close()
        outputStream.close()
    }
}

fun copyJsonIfNotExists(context: Context) {
    val file = File(context.filesDir, "reviews.json")
    if (true) {
        val assetManager = context.assets
        val inputStream = assetManager.open("reviews.json")
        val outputStream = FileOutputStream(file)

        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
    }
    copyAssetsFolder(context, "reviews", "reviews")
}