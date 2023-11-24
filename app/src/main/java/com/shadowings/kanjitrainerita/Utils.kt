package com.shadowings.kanjitrainerita

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Composable
fun annotateRecursively(
    placeHolderList: List<Pair<String, SpanStyle>>,
    originalText: String
): AnnotatedString {
    var annotatedString = buildAnnotatedString { append(originalText) }
    for (item in placeHolderList) {
        annotatedString = buildAnnotatedString {
            val startIndex = annotatedString.indexOf(item.first)
            val endIndex = startIndex + item.first.length
            append(annotatedString)
            addStyle(style = item.second, start = startIndex, end = endIndex)
        }
    }
    return annotatedString
}

fun boldTextInAsterisks(input: String): String {
    val regex = Regex("\\*(.*?)\\*")
    val replacedText = input.replace(regex) {
        "<b>${it.groups[1]?.value}</b>"
    }
    return replacedText
}

fun getGrayscaleBuffer(bitmap: Bitmap): ByteBuffer {
    val width = bitmap.width
    val height = bitmap.height
    val mImgData: ByteBuffer = ByteBuffer
        .allocateDirect(width * height * 4)
    mImgData.order(ByteOrder.nativeOrder())
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
    for (pixel in pixels) {
        val color = Color.red(pixel)
        val byte = color.toByte()
        mImgData.putFloat(color.toFloat())
    }
    return mImgData
}

fun assetFilePath(context: Context, asset: String): String {
    val file = File(context.filesDir, asset)

    try {
        val inpStream: InputStream = context.assets.open(asset)
        try {
            val outStream = FileOutputStream(file, false)
            val buffer = ByteArray(4 * 1024)
            var read: Int

            while (true) {
                read = inpStream.read(buffer)
                if (read == -1) {
                    break
                }
                outStream.write(buffer, 0, read)
            }
            outStream.flush()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}