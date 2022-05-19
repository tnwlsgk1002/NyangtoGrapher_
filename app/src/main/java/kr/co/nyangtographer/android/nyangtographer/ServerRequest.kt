package kr.co.nyangtographer.android.nyangtographer

import android.graphics.Bitmap
import android.graphics.Path
import android.os.Environment
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ServerRequest {
    var similarity: Int = -1
    suspend fun prediction(id: Int, path: String?) {
        //통신
        if (path != null) {
            val file = File(path)
            runBlocking {
                val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)

                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                val ansId = RequestBody.create("text/plain".toMediaTypeOrNull(), id.toString())
                println("=======body : $body, ansId : $ansId============")
                println("!!!!!!!${APIserviceImplemention.service.getRes(ansId, body)}")
                val service = APIserviceImplemention.service.getRes(ansId, body)
                // body 가 Multipart.Part

                launch {
                    service.enqueue(object : Callback<SIM> {
                        override fun onResponse(
                            call: Call<SIM>,
                            response: Response<SIM>
                        ) {
                            var sim: SIM? = response.body()
                            sim?.let {
                                similarity = sim?.similar
                            }
                            println("sim : $sim")
                        }

                        override fun onFailure(call: Call<SIM>, t: Throwable) {
                            println("error : ${t.message.toString()}")
                            similarity = -1
                        }
                    })
                    delay(8000)
                }
            }
        }
        //file.delete()
    }

    suspend fun bitmapToFile(bitmap: Bitmap, path: String): File {
        Environment.getDataDirectory()
        var file = File(Environment.getDataDirectory().toString() + File.separator + path)
        var out: OutputStream? = null
        try {
            file.createNewFile()
            out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
        } finally {
            out?.close()
        }
        return file
    }

}