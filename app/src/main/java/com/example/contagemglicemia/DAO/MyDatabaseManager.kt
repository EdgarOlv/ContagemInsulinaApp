package com.example.contagemglicemia.DAO

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.contagemglicemia.Model.Glicemia
import java.util.*

class MyDatabaseManager(context: Context) {

    private val dbHelper = MyDatabaseHelper(context)

    fun insertGlycemi(value: Int) {
        val date = Date() // Data atual
        val dateInMillis = date.time // Converter para milissegundos
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("valor", value)
            put("data", dateInMillis)
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
}