package kr.co.nyangtographer.android.nyangtographer

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import java.nio.file.Path
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    var allRecord: LiveData<List<Record>> = repository.allRecord

    var clickStageRecordList : LiveData<List<Record>>? = null
    var clickRecord : LiveData<Record>? = null
    var imagePath : String? = null

    suspend fun insert(record: Record) {
        repository.insert(record)
    }

    suspend fun update(record : Record) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(record)
    }

    fun getNPassStageRecordList(stageNumber : Int) : LiveData<List<Record>> {
        return repository.findNPassStageListByStageNumber(stageNumber)
    }

    fun findById(id: Int) : LiveData<Record> {
        return repository.findById(id)
    }

    fun setClickStageRecordList(stageNumber : Int) {
        clickStageRecordList = repository.findListByStageNumber(stageNumber)
    }

    fun setClickRecord(id : Int) {
        clickRecord = repository.findById(id)
    }

}