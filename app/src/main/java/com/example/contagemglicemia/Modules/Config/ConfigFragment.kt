package com.example.contagemglicemia.Modules.Config

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.contagemglicemia.DAO.MyDatabaseHelper
import com.example.contagemglicemia.DAO.MyDatabaseManager
import com.example.contagemglicemia.databinding.FragmentConfigBinding

class ConfigFragment : Fragment() {

    private lateinit var binding: FragmentConfigBinding
    lateinit var db: SQLiteDatabase
    private lateinit var dbHelper: MyDatabaseHelper

    private lateinit var dbManager: MyDatabaseManager

    var glicemaAlvo = 110
    var fatorSensibilidade = 37.0
    var relacaoCarboidrato = 10
    var resultadoInsulina = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startDatabase()
    }

    private fun startDatabase() {
        //dbHelper = MyDatabaseHelper(requireContext())
        //db = dbHelper.writableDatabase
        //startDataDatabase()
        dbManager = MyDatabaseManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentConfigBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.campoGlicemiaAlvo.setText(glicemaAlvo.toString())
        binding.campoFatorSensibilidade.setText(fatorSensibilidade.toString())
        binding.campoRelacaoCarboidrato.setText(relacaoCarboidrato.toString())

        /*val values = ContentValues().apply {
            put("valor", 100)
        }*/
        dbManager.insertGlycemi(120)
    //db.insert("glicemias", null, values)
    }

    fun startDataDatabase() {
        db = dbHelper.writableDatabase

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
        db.close()
    }
}
