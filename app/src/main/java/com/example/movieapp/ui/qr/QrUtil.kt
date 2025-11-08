package com.example.movieapp.ui.qr

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

// Since I didn't know how to implement the QR code function, chatgpt has helped me with this
// For example, recommending me Zxing lbirary (zebra crosser)


/**
 *Utility function for generating QR code bitmaps using the ZXing library.
 *This function converts a given text (JSON data) into a scannable QR code image
 *It is mainly used to create QR representations of a users favorite movie list, which can later
 *be shared and scanned by other users within the app
 */
fun generateQrBitmap(text: String, size: Int = 1024): Bitmap {
    val hints = mapOf(
        EncodeHintType.MARGIN to 0
    )
    val matrix: BitMatrix =
        MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)

    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    for (y in 0 until size) {
        for (x in 0 until size) {
            bmp.setPixel(x, y, if (matrix.get(x, y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
        }
    }
    return bmp
}