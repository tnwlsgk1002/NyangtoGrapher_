package kr.co.nyangtographer.android.nyangtographer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {
    // Bitmap -> ByteArray 변환
    @TypeConverter
    fun toByteArray(bitmap : Bitmap?) : ByteArray? {
        val outputStream = ByteArrayOutputStream()
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            return outputStream.toByteArray()
        } else {
            return null
        }
    }

    // ByteArray -> Bitmap 변환
    @TypeConverter
    fun toBitmap(bytes : ByteArray?) : Bitmap? {
        if (bytes != null ) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
        else {
            return null
        }
    }
}
