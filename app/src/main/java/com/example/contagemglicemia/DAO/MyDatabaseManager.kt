package com.example.contagemglicemia.DAO

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.contagemglicemia.Model.Alimento
import com.example.contagemglicemia.Model.Configuracao
import java.text.SimpleDateFormat
import java.util.*

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

    fun insertGlycemia(value: Int, resultadoInsulina: Double) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val date = Date()
        val dateString = dateFormat.format(date)

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("valor", value)
            put("data", dateString)
            put("insulina_aplicada", resultadoInsulina)
        }
        db.insert("glicemias", null, values)
        // db.close()
    }

    fun deleteData(id: Long) {
        val db = dbHelper.writableDatabase
        db.delete("mytable", "_id=?", arrayOf(id.toString()))
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

    fun queryData(): Cursor {
        val db = dbHelper.readableDatabase
        val cursor = db.query("mytable", null, null, null, null, null, null)
        return cursor
    }

    fun getGlicemy() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT data FROM exemplo WHERE id = ?", arrayOf("1"))
        if (cursor.moveToFirst()) {
            val dateString = cursor.getString(0)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val date = dateFormat.parse(dateString)
        }
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
}
