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
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var dbManager: MyDatabaseManager
    var TAG = "HomeFragment"

    var glicemaAlvo = 0
    var fatorSensibilidade = 0
    var relacaoCarboidrato = 0
    var resultadoInsulina = 0.0
    var resultadoTexto = ""

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

        binding.checkboxAlimentar.isChecked = true
        getActualAlimentation()

        bindingCalcular(view)

        binding.checkboxAlimentar.setOnClickListener {
            if (binding.checkboxAlimentar.isChecked) {
                selectAlimentation()
            }
        }
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

    private fun getActualAlimentation() {
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
        binding.textViewTipoRefeicao.setText(refeicao.name)
    }

    private fun bindingCalcular(view: View) {
        binding.buttonCalcular.setOnClickListener() {
            val valorDigitado = binding.editText.text.toString().toInt()
            val campoResultado = binding.textViewResultadoInsulina

            try {
                if (valorDigitado != 0) {
                    resultadoInsulina = 0.0
                    resultadoTexto = ""
                    if (binding.checkboxCorrigir.isChecked) {
                        val result = ((valorDigitado.toDouble() - glicemaAlvo) / fatorSensibilidade)
                        resultadoInsulina += result
                        resultadoTexto += String.format("Correção = %.2f UI \n", result)
                    }
                    if (binding.checkboxAlimentar.isChecked) {
                        val result = (alimentSelected.qtd_carboidrato.toDouble() / relacaoCarboidrato)
                        resultadoInsulina += result
                        resultadoTexto += String.format("Alimento = %.2f UI\n", result)
                    }
                    if (binding.checkboxMalhar.isChecked) {
                        val result = acaoTreino(valorDigitado.toDouble())
                        resultadoTexto += "e $result \n"
                        resultadoInsulina = resultadoInsulina / 2
                    }

                    dbManager.insertGlycemia(valorDigitado, resultadoInsulina.toInt())
                    resultadoTexto += String.format("Aplicar %.2f UI", resultadoInsulina)
                    campoResultado.setText(resultadoTexto)

                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            } catch (e: Exception) {
                campoResultado.setText("Erro")
            }
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
    /*fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }*/

    companion object {
        fun newInstance() = HomeFragment()
    }
}
