package kr.co.nyangtographer.android.nyangtographer

import androidx.lifecycle.LiveData

class Repository(private val dao : RecordDao) {
    // private val dao = mDatabase.recordDao()

    val allRecord : LiveData<List<Record>> = dao.getAll()

    companion object {
        private var sInstance: Repository? = null
        fun getInstance(database: AppDatabase) : Repository {
            return sInstance
                ?: synchronized(this) {
                    val instance = Repository(database.recordDao())
                    sInstance = instance
                    instance
                }
        }
    }

    suspend fun update(record : Record) {
        dao.update(record)
    }

    suspend fun insert(record : Record) {
        dao.insert(record)
    }

    suspend fun delete(record : Record) {
        dao.delete(record)
    }

    fun findNPassStageListByStageNumber(stageNumber : Int) =
        dao.findNPassStageListByStageNumber(stageNumber)

    fun findListByStageNumber(stageNumber : Int) = dao.findListByStageNumber(stageNumber)

    fun findById(id : Int) = dao.findById(id)
}