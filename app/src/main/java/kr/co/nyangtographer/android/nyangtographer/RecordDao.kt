package kr.co.nyangtographer.android.nyangtographer

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RecordDao {
    @Query("SELECT * FROM record")
    fun getAll(): LiveData<List<Record>>

    @Insert
    fun insert(vararg record: Record)

    @Delete
    fun delete(record: Record)

    @Query("DELETE FROM record")
    fun nukeTable()

    @Update
    fun update(record: Record)

    @Query("SELECT * FROM record WHERE id = :search_id")
    fun findById(search_id: Int): LiveData<Record>

    //해당 스테이지의 모든 튜플들을 가져온다.
    @Query("SELECT * FROM record WHERE stage_number = :stage_number")
    fun findListByStageNumber(stage_number: Int) : LiveData<List<Record>>

    // 해당 스테이지(입력) 에 통과하지 못한것만 가져온다.
    @Query("SELECT * FROM record WHERE stage_number= :stage_number and pass = 'FALSE'")
    fun findNPassStageListByStageNumber(stage_number: Int): LiveData<List<Record>>
}