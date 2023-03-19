package com.example.contagemglicemia.Modules.Home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.example.contagemglicemia.DAO.MyDatabaseManager
import com.example.contagemglicemia.databinding.FragmentHomeBinding
import java.math.RoundingMode
import java.text.DecimalFormat

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var dbManager: MyDatabaseManager
    var glicemaAlvo = 0
    var fatorSensibilidade = 0
    var resultadoInsulina = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbManager = MyDatabaseManager(requireContext())
        dbManager.getConfigData().forEach {
            when(it.id){
                1 -> fatorSensibilidade = it.value
                2 -> glicemaAlvo = it.value
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

        bindingCalcular(view)
    }

    private fun bindingCalcular(view: View) {
        binding.buttonCalcular.setOnClickListener() {
            val valorDigitado = binding.editText.text.toString().toInt()
            val campoResultado = binding.textViewResultadoInsulina
            try {
                if (valorDigitado != 0) {
                    resultadoInsulina = ((valorDigitado.toDouble() - glicemaAlvo) / fatorSensibilidade)
                    campoResultado.setText(roundOffDecimal(resultadoInsulina).toString())
                    dbManager.insertGlycemia(valorDigitado)
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
