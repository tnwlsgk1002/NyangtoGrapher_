package kr.co.nyangtographer.android.nyangtographer

import android.graphics.Bitmap
import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.Job

@Entity(tableName = "record")
data class Record(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var answer_id: Int = 0,
    var stage_number: Int? = null,
    var title: String? = null,
    var challenge_img: Bitmap? = null,
    var quest_img: Bitmap? = null,
    var similarity: Int = 0,
    var pass: Boolean = false
)
