package com.example.contagemglicemia.modules.Config

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.contagemglicemia.dao.MyDatabaseManager
import com.example.contagemglicemia.model.Alimento
import java.util.*

class ConfigViewModel : ViewModel() {

    private lateinit var dbManager: MyDatabaseManager

    fun getAlimentation(context: Context): Alimento {
        val currentTime = Calendar.getInstance().time
        val calendar = Calendar.getInstance()
        calendar.time = currentTime
        // val arrayAliments: Array<Alimento> = arrayOfNulls<Alimento>()
        val arrayAliments = arrayOfNulls<Alimento>(10)

        var alimentoReturned = Alimento(0, "", "", 0)
        dbManager = MyDatabaseManager(context)
        var qtdUsada = 0
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        // TODO: Colocar com condições if, para poder utilizar variaves e usuario poder ajustar os horarios

        dbManager.getAlimentoData().forEach {
            when (it.id) {
                1 -> arrayAliments[1] = it
                2 -> arrayAliments[2] = it
                3 -> arrayAliments[3] = it
                4 -> arrayAliments[4] = it
                5 -> arrayAliments[5] = it
                6 -> arrayAliments[6] = it
            }
        }
        when (hourOfDay) {
            in 1..9 -> {
                // café da manhã
                alimentoReturned = arrayAliments[1] ?: alimentoReturned
            }
            in 9..11 -> {
                // lanche manha
                alimentoReturned = arrayAliments[2] ?: alimentoReturned
            }
            in 11..14 -> {
                // almoço
                alimentoReturned = arrayAliments[3] ?: alimentoReturned
            }
            in 14..17 -> {
                // lanche da tarde
                alimentoReturned = arrayAliments[4] ?: alimentoReturned
            }
            in 17..20 -> {
                // jantar
                alimentoReturned = arrayAliments[5] ?: alimentoReturned
            }
            in 20..24 -> {
                // Ceia
                alimentoReturned = arrayAliments[6] ?: alimentoReturned
            }
            else -> {
                // horário de descanso
            }
        }
        return alimentoReturned
    }

    /*class Factory(

    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(
                ) as T
            }
            throw IllegalArgumentException("Classe viewModel inválido")
        }
    }*/
}
