package com.example.contagemglicemia.Home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.contagemglicemia.databinding.FragmentHomeBinding
import java.math.RoundingMode
import java.text.DecimalFormat

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    var glicemaAlvo = 110
    var fatorSensibilidade = 37.0
    var resultadoInsulina = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Carregar dados config (viewmodel)
        //Carregar Firebase
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

        binding.buttonCalcular.setOnClickListener() {
            Log.i("TAG", "onCreate: Foi 2")

            val valorDigitado = binding.editText.text.toString().toInt()
            val campoResultado = binding.textViewResultadoInsulina
            try {
                if (valorDigitado != 0) {
                    resultadoInsulina = ((valorDigitado - glicemaAlvo) / fatorSensibilidade)
                    campoResultado.setText(roundOffDecimal(resultadoInsulina).toString())

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
