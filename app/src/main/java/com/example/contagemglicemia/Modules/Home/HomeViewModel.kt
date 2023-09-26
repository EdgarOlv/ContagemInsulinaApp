package com.example.contagemglicemia.Modules.Home

import android.content.Context
import android.graphics.Color
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contagemglicemia.DAO.MyDatabaseGSheets
import com.example.contagemglicemia.DAO.MyDatabaseManager
import com.example.contagemglicemia.Model.Alimento
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {

    private lateinit var dbManager: MyDatabaseManager
    private var dbGsheets: MyDatabaseGSheets? = null
    //val database = FirebaseFirestore.getInstance()

    //val glicemiaRef = database.getReference("glicemias")

    var glicemaAlvo = 0
    var fatorSensibilidade = 0
    var relacaoCarboidrato = 0
    var resultadoInsulina = 0.0
    var resultadoTexto = ""
    var listOfAliments: List<Alimento> = mutableListOf()
    var alimentSelected = Alimento(0, "", "", 0)

    fun iniciaDadosBanco(context: Context) {
        dbManager = MyDatabaseManager(context)
        dbGsheets = MyDatabaseGSheets(context)
        dbGsheets?.connectDB(context)

        if (dbManager.getConfigData().isNullOrEmpty()) {
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

    fun calcularGlicemia(
        valorDigitado: Int,
        check1: Boolean,
        check2: Boolean,
        check3: Boolean,
        context: Context,
        db: FirebaseFirestore
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

                dbManager.insertGlycemia(valorDigitado, resultadoInsulina.toInt())

                InserirEmNuvem(context, valorDigitado, db)

                return resultadoTexto
            }
        } catch (e: Exception) {
            return "Erro" + e
        }
        return "Erro"
    }

    private fun InserirEmNuvem(
        context: Context,
        valorDigitado: Int,
        db: FirebaseFirestore
    ) {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = Date()
            val dateString = dateFormat.format(date)

            /*dbGsheets?.insertGlicemiaNuvem(
                context,
                Glicemia(0, valorDigitado, dateString, resultadoInsulina.toInt())
            )*/
/*
            val glicemy = hashMapOf(
                "value" to valorDigitado,
                "date" to dateString.toString(),
                "insulin_apply" to resultadoInsulina.toInt()
            )

            db.collection("glicemia")
                .add(glicemy)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(context, "Inserido com sucesso!! \n ID:${documentReference.id} ", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Erro ao inserir!", Toast.LENGTH_SHORT).show()
                }*/
            //db.child("users").child(userId).setValue(user)


        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao inserir na nuvem", Toast.LENGTH_SHORT).show()
        }
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
