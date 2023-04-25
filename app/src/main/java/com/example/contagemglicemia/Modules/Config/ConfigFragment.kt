package com.example.contagemglicemia.Modules.Config

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.contagemglicemia.DAO.MyDatabaseHelper
import com.example.contagemglicemia.DAO.MyDatabaseManager
import com.example.contagemglicemia.databinding.FragmentConfigBinding
import java.io.*

class ConfigFragment : Fragment() {

    private lateinit var binding: FragmentConfigBinding
    private lateinit var dbManager: MyDatabaseManager

    var glicemaAlvo = 110
    var fatorSensibilidade = 37
    var relacaoCarboidrato = 10

    val PICK_FILE_REQUEST_CODE = -1
    var DATABASE_NAME = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbManager = MyDatabaseManager(requireContext())

        try {
            dbManager.getConfigData().forEach {
                when (it.id) {
                    1 -> fatorSensibilidade = it.value
                    2 -> glicemaAlvo = it.value
                    3 -> relacaoCarboidrato = it.value
                }
            }
            dbManager.getAlimentoData().forEach {
                when (it.id) {
                    1 -> binding.campoFood1.setText(it.qtd_carboidrato.toString())
                    2 -> binding.campoFood2.setText(it.qtd_carboidrato.toString())
                    3 -> binding.campoFood3.setText(it.qtd_carboidrato.toString())
                }
            }
        } catch (e: Exception) {
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
            when (it.id) {
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
        binding.buttonSaveConfig.setOnClickListener {
            // TODO: Pegar cada campo e salvar em objeto e em banco
            Toast.makeText(context, "Botao Salvar cliado", Toast.LENGTH_SHORT).show()
        }

        binding.buttonExport.setOnClickListener{
            exportDatabase(requireContext(), MyDatabaseHelper(requireContext()).nameDatabase())
        }

        binding.buttonImport.setOnClickListener{
            DATABASE_NAME = MyDatabaseHelper(requireContext()).nameDatabase()
            importDatabase()
        }

        binding.buttonDeleteAll.setOnClickListener{
            dbManager.deleteAllData()
            Toast.makeText(context, "Banco de dados limpo com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }

    fun exportDatabase(context: Context, dbName: String) {
        try {
            val file = File(context.getExternalFilesDir(null), dbName)
            val input = FileInputStream(context.getDatabasePath(dbName))
            val output = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var length: Int
            while (input.read(buffer).also { length = it } > 0) {
                output.write(buffer, 0, length)
            }
            output.flush()
            output.close()
            input.close()
            Toast.makeText(context, "Banco de dados exportado com sucesso!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erro ao exportar o banco de dados", Toast.LENGTH_SHORT).show()
        }
    }

    private fun importDatabase() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/octet-stream"
        }
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                try {
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val dbFile = File(requireContext().getDatabasePath(DATABASE_NAME).path)
                    dbFile.parentFile.mkdirs()
                    dbFile.createNewFile()
                    inputStream?.use { input ->
                        FileOutputStream(dbFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    Toast.makeText(requireContext(), "Banco de dados importado com sucesso!", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Erro ao importar banco de dados", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}
