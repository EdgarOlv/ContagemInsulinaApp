package com.example.contagemglicemia.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "mydatabase.db"
        private const val DATABASE_VERSION = 4


    }

    fun nameDatabase(): String{
        return DATABASE_NAME
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE_GLYCEMIA = "CREATE TABLE if not exists ${TableGlicemia.TABLE_NAME} (" +
                "${TableGlicemia.ID} INTEGER PRIMARY KEY," +
                "${TableGlicemia.VALUE} INTEGER," +
                "${TableGlicemia.DATE} TEXT," +
                "${TableGlicemia.INSULIN_APPLY} INTEGER," +
                "${TableGlicemia.OBS} TEXT)"

        db.execSQL(CREATE_TABLE_GLYCEMIA)

        val CREATE_TABLE_CONFIG = "CREATE TABLE if not exists ${TableConfig.TABLE_NAME} (" +
                "${TableConfig.ID} INTEGER PRIMARY KEY," +
                "${TableConfig.NAME} TEXT," +
                "${TableConfig.VALUE} INTEGER)"

        db.execSQL(CREATE_TABLE_CONFIG)

        val CREATE_TABLE_FOOD = "CREATE TABLE if not exists ${TableFood.TABLE_NAME} (" +
                "${TableFood.ID} INTEGER PRIMARY KEY," +
                "${TableFood.ID_NAME} TEXT," +
                "${TableFood.NAME} TEXT," +
                "${TableFood.QTD_CARBO} INTEGER)"

        db.execSQL(CREATE_TABLE_FOOD)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Aqui você pode atualizar a tabela do banco de dados, caso necessário
        val DROP_TABLE = "DROP TABLE IF EXISTS ${TableGlicemia.TABLE_NAME}"
        db.execSQL(DROP_TABLE)
        val DROP_TABLE2 = "DROP TABLE IF EXISTS ${TableConfig.TABLE_NAME}"
        db.execSQL(DROP_TABLE2)
        val DROP_TABLE3 = "DROP TABLE IF EXISTS ${TableFood.TABLE_NAME}"
        db.execSQL(DROP_TABLE3)
        onCreate(db)
    }


}
