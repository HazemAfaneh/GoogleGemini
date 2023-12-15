package com.hazem.googlegemini

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

fun Uri.toBitmap(context: Context):Bitmap{
    val inputStream = context.contentResolver.openInputStream(this)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    inputStream?.close()
    return bitmap.resizeBitmapTo1MB()
}
fun Bitmap.resizeBitmapTo1MB(): Bitmap {
    var targetWidth = this.width
    var targetHeight = this.height
    var inSampleSize = 1

    while (true) {
        val byteSize = targetWidth * targetHeight * Bitmap.Config.ARGB_8888.ordinal
        if (byteSize <= 1024 * 1024) { // 1 MB in bytes
            break
        }
        inSampleSize *= 2
        targetWidth /= 2
        targetHeight /= 2
    }

    return Bitmap.createScaledBitmap(this, targetWidth, targetHeight, true)
}