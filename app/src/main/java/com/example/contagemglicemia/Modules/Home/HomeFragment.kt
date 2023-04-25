package com.example.contagemglicemia.Modules.Home

import android.R
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.example.contagemglicemia.DAO.MyDatabaseManager
import com.example.contagemglicemia.Model.Alimento
import com.example.contagemglicemia.databinding.FragmentHomeBinding
import java.math.RoundingMode
import java.text.DecimalFormat

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var dbManager: MyDatabaseManager
    var TAG = "HomeFragment"

    // private lateinit var viewModel: HomeViewModel
    var glicemaAlvo = 0
    var fatorSensibilidade = 0
    var relacaoCarboidrato = 0
    var resultadoInsulina = 0.0

    var listOfAliments: List<Alimento> = mutableListOf()
    var alimentSelected = Alimento(0, "", "", 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbManager = MyDatabaseManager(requireContext())
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

        // val viewModelFactory = HomeViewModel.Factory()
        // viewModel = ViewModelProvider(this, viewModelFactory)            .get(HomeViewModel::class.java)

        // Carregar dados config (viewmodel)
        // Carregar Firebase
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lateinit var checkBox: CheckBox

        checkBox = binding.checkboxCorrigir
        checkBox.isChecked = true

        bindingCalcular(view)

        binding.checkboxAlimentar.setOnClickListener {
            if (binding.checkboxAlimentar.isChecked) {
                selectAlimentation()
            }
        }

        // val alimento = viewModel.getAlimentation(requireContext())
    }

    private fun selectAlimentation() {
        val opcoes = dbManager.getAlimentoData()
        val builder = AlertDialog.Builder(requireContext())

        builder.setAdapter(
            ArrayAdapter(requireContext(), R.layout.simple_list_item_1, opcoes.map { it.name })
        ) { dialog, which ->
            alimentSelected = opcoes[which]
            binding.textViewTipoRefeicao.setText(alimentSelected.name)
        }
        builder.show()
    }

    private fun bindingCalcular(view: View) {
        binding.buttonCalcular.setOnClickListener() {
            val valorDigitado = binding.editText.text.toString().toInt()
            val campoResultado = binding.textViewResultadoInsulina

            try {
                if (valorDigitado != 0) {

                    resultadoInsulina = 0.0
                    if (binding.checkboxCorrigir.isChecked) {
                        resultadoInsulina +=
                            ((valorDigitado.toDouble() - glicemaAlvo) / fatorSensibilidade)
                    }
                    if (binding.checkboxAlimentar.isChecked) {
                        resultadoInsulina +=
                            (alimentSelected.qtd_carboidrato.toDouble() / relacaoCarboidrato)
                    }
                    if (binding.checkboxMalhar.isChecked) {
                    }

                    dbManager.insertGlycemia(valorDigitado, resultadoInsulina.toInt())
                    campoResultado.setText(String.format("Aplicar %.2f UI", resultadoInsulina))

                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            } catch (e: Exception) {
                campoResultado.setText("Erro")
            }
        }
    }

    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }
}
