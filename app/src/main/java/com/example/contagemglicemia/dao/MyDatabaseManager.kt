package com.example.contagemglicemia.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.contagemglicemia.model.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyDatabaseManager(context: Context) {

    private val dbHelper = MyDatabaseHelper(context)

    fun startDataDatabase() {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("nome", "F.S.")
            put("valor", 37)
        }
        val values2 = ContentValues().apply {
            put("nome", "Glicemia Alvo")
            put("valor", 110)
        }
        val values3 = ContentValues().apply {
            put("nome", "Carboidrato")
            put("valor", 10)
        }
        db.insert("config", null, values)
        db.insert("config", null, values2)
        db.insert("config", null, values3)

        val values4 = ContentValues().apply {
            put("nome", "Cafe da Manhã")
            put("id_nome", "Cafe")
            put("qtd_carboidrato", 50)
        }
        val values5 = ContentValues().apply {
            put("nome", "Lanche da Manhã")
            put("id_nome", "LancheM")
            put("qtd_carboidrato", 15)
        }
        val values6 = ContentValues().apply {
            put("nome", "Almoço")
            put("id_nome", "Almoço")
            put("qtd_carboidrato", 55)
        }
        val values7 = ContentValues().apply {
            put("nome", "Lanche da Tarde")
            put("id_nome", "LancheT")
            put("qtd_carboidrato", 30)
        }
        val values8 = ContentValues().apply {
            put("nome", "Jantar")
            put("id_nome", "Jantar")
            put("qtd_carboidrato", 45)
        }
        val values9 = ContentValues().apply {
            put("nome", "Ceia")
            put("id_nome", "Ceia")
            put("qtd_carboidrato", 40)
        }
        db.insert("alimentos", null, values4)
        db.insert("alimentos", null, values5)
        db.insert("alimentos", null, values6)
        db.insert("alimentos", null, values7)
        db.insert("alimentos", null, values8)
        db.insert("alimentos", null, values9)
        db.close()
    }

    fun insertGlycemia(glicemia: Glicemia) {

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("valor", glicemia.value)
            put("data", glicemia.date)
            put("insulina_aplicada", glicemia.insulina_apply)
        }
        db.insert("glicemias", null, values)
        // db.close()
    }

    fun deleteData(id: Long) {
        val db = dbHelper.writableDatabase
        db.delete("mytable", "_id=?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteAllData() {
        val db = dbHelper.writableDatabase
        db.delete("glicemias", null, null)
        db.close()
    }

    fun updateData(id: Long, name: String, age: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("age", age)
        }
        db.update("mytable", values, "_id=?", arrayOf(id.toString()))
        db.close()
    }

    fun convertStringToDate(dateString: String): Date {
        val format = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        return format.parse(dateString) ?: Date()
    }

    fun getAllGlicemy(): List<Glicemia> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, valor, data, insulina_aplicada FROM glicemias ORDER BY data DESC", null)
        var list = mutableListOf<Glicemia>()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow("id"))
                val valor = getInt(getColumnIndexOrThrow("valor"))
                val data = getString(getColumnIndexOrThrow("data"))
                val dateConverted = dateFormat.parse(data)

                val insulin_aplicada = getInt(getColumnIndexOrThrow("insulina_aplicada"))
                list.add(Glicemia(id, valor, data.toString(), insulin_aplicada))
            }
        }

        return list
    }

    fun getAllGlicemyDates(): List<String> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT data FROM glicemias", null)
        val datesList = mutableListOf<String>()

        with(cursor) {
            while (moveToNext()) {
                val data = getString(getColumnIndexOrThrow("data"))
                datesList.add(data)
            }
        }

        return datesList
    }

    fun getConfigData(): List<Configuracao> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, nome, valor FROM config", null)
        var list = mutableListOf<Configuracao>()

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow("id"))
                val nome = getString(getColumnIndexOrThrow("nome"))
                val valor = getInt(getColumnIndexOrThrow("valor"))
                list.add(Configuracao(id, nome, valor))
            }
        }
        return list
    }

    fun getAlimentoData(): List<Alimento> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, id_nome, nome, qtd_carboidrato FROM alimentos", null)
        var list = mutableListOf<Alimento>()

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow("id"))
                val id_nome = getString(getColumnIndexOrThrow("id_nome"))
                val nome = getString(getColumnIndexOrThrow("nome"))
                val valor = getInt(getColumnIndexOrThrow("qtd_carboidrato"))
                list.add(Alimento(id, id_nome, nome, valor))
            }
        }
        return list
    }

    fun updateCarboAlimento(carboAlimento: CarboAlimento) {
        val db = dbHelper.writableDatabase
        val refeicoes = mapOf(
            "Cafe" to carboAlimento.cafe,
            "LancheM" to carboAlimento.lancheM,
            "Almoco" to carboAlimento.almoco,
            "LancheT" to carboAlimento.lancheT,
            "Jantar" to carboAlimento.jantar,
            "Ceia" to carboAlimento.ceia
        )

        for ((refeicao, quantidade) in refeicoes) {
            val values = ContentValues().apply {
                put("qtd_carboidrato", quantidade)
            }
            db.update("alimentos", values, "id_nome=?", arrayOf(refeicao))
        }
        db.close()
    }

    fun updateConfig(configModel: ConfigModel) {
        val db = dbHelper.writableDatabase
        val configs = mapOf(
            "1" to configModel.glicemiaAlvo,
            "2" to configModel.fatorSensibilidade,
            "3" to configModel.relacaoCarbo
        )

        for ((config, quantidade) in configs) {
            val values = ContentValues().apply {
                put("valor", quantidade)
            }
            db.update("config", values, "id=?", arrayOf(config))
        }
        db.close()
    }
}
