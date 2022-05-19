package kr.co.nyangtographer.android.nyangtographer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Record::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao

    private class AppDatabaseCallback(private val context: Context, private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let {
                database ->
                scope.launch {
                    poplulateDataBase(database.recordDao())
                }
            }
        }

        suspend fun poplulateDataBase(recordDao: RecordDao) {
            var drawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.bread_01_answer)
            var questImg :Bitmap? = drawable?.toBitmap()
            if (questImg != null) questImg = Bitmap.createScaledBitmap(questImg, 120, 120, false)
            recordDao.insert(Record(1, 1, 1,"식빵냥", null, questImg, 0,false))
            drawable = ContextCompat.getDrawable(context, R.drawable.lay_01_answer)
            questImg = drawable?.toBitmap()
            if (questImg != null) questImg = Bitmap.createScaledBitmap(questImg, 120, 120, false)
            recordDao.insert(Record(2,2, 1,"기절냥", null, questImg, 0, false))

            drawable = ContextCompat.getDrawable(context, R.drawable.sit_01_answer)
            questImg = drawable?.toBitmap()
            if (questImg != null) questImg = Bitmap.createScaledBitmap(questImg, 120, 120, false)
            recordDao.insert(Record(3, 3,1,"꾹꾹이냥", null, questImg, 0,false))
        }

    }
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nyang.db"
                ).addCallback(AppDatabaseCallback(context, scope))
                 .build()
                INSTANCE = instance
                instance
                //.fallbackToDestructiveMigration()
                // .addCallback(AppDatabaseCallback(scope))
                //.createFromAsset("nyangdb.db")
            }
        }
    }
}