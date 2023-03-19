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
    var fatorSensibilidade = 37
    var relacaoCarboidrato = 10
    var resultadoInsulina = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbManager = MyDatabaseManager(requireContext())
        dbManager.getConfigData().forEach {
            when(it.id){
                1 -> fatorSensibilidade = it.value
                2 -> glicemaAlvo = it.value
                3 -> relacaoCarboidrato = it.value
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentConfigBinding.inflate(inflater, container, false)
        dbManager.getAlimentoData().forEach {
            when(it.id){
                1 -> binding.campoFood1.setText(it.qtd_carboidrato.toString())
                2 -> binding.campoFood2.setText(it.qtd_carboidrato.toString())
                3 -> binding.campoFood3.setText(it.qtd_carboidrato.toString())
                4 -> binding.campoFood4.setText(it.qtd_carboidrato.toString())
                5 -> binding.campoFood5.setText(it.qtd_carboidrato.toString())
                6 -> binding.campoFood6.setText(it.qtd_carboidrato.toString())
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.campoGlicemiaAlvo.setText(glicemaAlvo.toString())
        binding.campoFatorSensibilidade.setText(fatorSensibilidade.toString())
        binding.campoRelacaoCarboidrato.setText(relacaoCarboidrato.toString())


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
