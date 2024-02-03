package com.example.contagemglicemia.modules.Home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contagemglicemia.dao.FirebaseDB
import com.example.contagemglicemia.dao.MyDatabaseGSheets
import com.example.contagemglicemia.dao.MyDatabaseManager
import com.example.contagemglicemia.model.Alimento
import com.example.contagemglicemia.model.Glicemia
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {

    private lateinit var dbManager: MyDatabaseManager
    private var dbGsheets: MyDatabaseGSheets? = null
    private lateinit var firebaseDb: FirebaseDB

    var glicemaAlvo = 0
    var fatorSensibilidade = 0
    var relacaoCarboidrato = 0
    var resultadoInsulina = 0.0
    var resultadoTexto = ""
    var listOfAliments: List<Alimento> = mutableListOf()
    var alimentSelected = Alimento(0, "", "", 0)

    fun iniciaDadosBanco(context: Context) {
        dbManager = MyDatabaseManager(context)
        firebaseDb = FirebaseDB()
        dbGsheets = MyDatabaseGSheets(context)
        dbGsheets?.connectDB(context)

        if (dbManager.getConfigData().isEmpty()) {
            dbManager.startDataDatabase()
            dbManager.getConfigData().forEach {
                when (it.id) {
                    1 -> fatorSensibilidade = it.value
                    2 -> glicemaAlvo = it.value
                    3 -> relacaoCarboidrato = it.value
                }
            }
            listOfAliments = dbManager.getAlimentoData()
        } else {
            dbManager.getConfigData().forEach {
                when (it.id) {
                    1 -> fatorSensibilidade = it.value
                    2 -> glicemaAlvo = it.value
                    3 -> relacaoCarboidrato = it.value
                }
            }
        }
    }

    fun getActualAlimentation(): Alimento {
        val opcoes = dbManager.getAlimentoData()

        val calendar = Calendar.getInstance()
        val horaAtual = calendar.get(Calendar.HOUR_OF_DAY)

        val refeicao: Alimento = when {
            horaAtual < 9 -> opcoes[0]
            horaAtual < 12 -> opcoes[1]
            horaAtual < 15 -> opcoes[2]
            horaAtual < 18 -> opcoes[3]
            horaAtual < 20 -> opcoes[4]
            else -> opcoes[5]
        }
        alimentSelected = refeicao
        return alimentSelected
    }

    fun calcularGlicemia(
        valorDigitado: Int,
        check1: Boolean,
        check2: Boolean,
        check3: Boolean,
        context: Context,
    ): String {
        try {
            if (valorDigitado != 0) {
                resultadoInsulina = 0.0
                resultadoTexto = ""
                if (check1) {
                    val result = ((valorDigitado.toDouble() - glicemaAlvo) / fatorSensibilidade)
                    resultadoInsulina += result
                    resultadoTexto += String.format("Correção = %.2f UI \n", result)
                }
                if (check2) {
                    val result = (alimentSelected.qtd_carboidrato.toDouble() / relacaoCarboidrato)
                    var test1 = alimentSelected.qtd_carboidrato.toDouble()
                    var test2 = relacaoCarboidrato
                    resultadoInsulina += result
                    resultadoTexto += String.format("Alimento = %.2f UI\n", result)
                }
                if (check3) {
                    val result = acaoTreino(valorDigitado.toDouble())
                    resultadoInsulina = resultadoInsulina / 2
                    resultadoTexto += String.format("Aplicar %.2f UI \n", resultadoInsulina)
                    resultadoTexto += "e $result "
                } else {
                    resultadoTexto += String.format("Aplicar %.2f UI", resultadoInsulina)
                }

                val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                val date = Date()
                val dateString = dateFormat.format(date)

                val newGlicemia = Glicemia(0, valorDigitado, dateString, resultadoInsulina.toInt(), "")

                dbManager.insertGlycemia(newGlicemia)

                firebaseDb.InserirEmNuvem(context, newGlicemia)

                return resultadoTexto
            }
        } catch (e: Exception) {
            return "Erro" + e
        }
        return "Erro"
    }

    fun acaoTreino(valorGlicemia: Double): String {
        if (valorGlicemia <= 70.0) {
            return "Adiar o treino"
        }
        if (valorGlicemia > 70.0 && valorGlicemia <= 100.0) {
            return "25g a 50g Carb. antes do treino"
        }
        if (valorGlicemia > 100.0 && valorGlicemia <= 180.0) {
            return "20g a 50g Carb. antes do treino"
        }
        if (valorGlicemia > 180.0 && valorGlicemia <= 300.0) {
            return "15g antes do treino"
        }
        if (valorGlicemia > 300.0) {
            return "Adiar o treino"
        } else {
            return ""
        }
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel() as T
            }
            throw IllegalArgumentException("Classe ViewModel inválida")
        }
    }
}
