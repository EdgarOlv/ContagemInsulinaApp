package com.example.contagemglicemia.DAO

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.contagemglicemia.Model.Alimento
import com.example.contagemglicemia.Model.Configuracao
import com.example.contagemglicemia.Model.Glicemia
import java.text.SimpleDateFormat
import java.util.*

class MyDatabaseManager(context: Context) {

    private val dbHelper = MyDatabaseHelper(context)

    fun insertGlycemia(value: Int) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val date = Date()
        val dateString = dateFormat.format(date)

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("valor", value)
            put("data", dateString)
        }
        db.insert("glicemias", null, values)
        //db.close()
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
                list.add(Alimento(id, id_nome,nome, valor))
            }
        }
        return list
    }
}