package com.example.contagemglicemia.modules.Report.pages.page1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contagemglicemia.dao.MyDatabaseManager
import com.example.contagemglicemia.model.Glicemia
import com.example.contagemglicemia.utils.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReportOneViewModel : ViewModel() {

    private val _listGlicemy = MutableLiveData<Event<List<Glicemia>>>()
    val listGlicemy: LiveData<Event<List<Glicemia>>> = _listGlicemy

    fun getAllGlicemy(dbManager: MyDatabaseManager) {
        CoroutineScope(Dispatchers.IO).launch {
            val registros = dbManager.getAllGlicemy().sortedByDescending { it.date }
            _listGlicemy.postValue(Event(registros))
        }
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReportOneViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReportOneViewModel() as T
            }
            throw IllegalArgumentException("Classe ViewModel inválida")
        }
    }
}
