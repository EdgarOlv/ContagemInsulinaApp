package com.example.contagemglicemia.modules.Home

import android.R
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.contagemglicemia.dao.FirebaseDB
import com.example.contagemglicemia.dao.MyDatabaseManager
import com.example.contagemglicemia.databinding.FragmentHomeBinding
import com.example.contagemglicemia.model.Alimento
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var dbManager: MyDatabaseManager
    private lateinit var viewModel: HomeViewModel
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseDb: FirebaseDB

    var alimentSelected = Alimento(0, "", "", 0)
    var lastValue = 0
    private lateinit var campoResultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbManager = MyDatabaseManager(requireContext())
        firebaseDb = FirebaseDB()

        db = FirebaseFirestore.getInstance()

        setupViewModel()
        viewModel.iniciaDadosBanco(requireContext())

        firebaseDb.ReceberListNuvem(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.checkboxCorrigir.isChecked = true
        binding.checkboxAlimentar.isChecked = true

        alimentSelected = viewModel.getActualAlimentation()
        binding.textViewTipoRefeicao.setText(alimentSelected.name)

        bindingCalcular(view)

        binding.checkboxAlimentar.setOnClickListener {
            if (binding.checkboxAlimentar.isChecked) {
                selectAlimentation()
            }
        }

        campoResultado = binding.textViewResultadoInsulina
    }

    private fun setupViewModel() {
        val viewModelFactory = HomeViewModel.Factory()
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(HomeViewModel::class.java)
    }

    private fun selectAlimentation() {
        val opcoes = dbManager.getAlimentoData()
        val builder = AlertDialog.Builder(requireContext())

        builder.setAdapter(
            ArrayAdapter(requireContext(), R.layout.simple_list_item_1, opcoes.map { it.name }),
        ) { dialog, which ->
            alimentSelected = opcoes[which]
            binding.textViewTipoRefeicao.setText(alimentSelected.name)
        }
        builder.show()
    }

    private fun bindingCalcular(view: View) {
        binding.buttonCalcular.setOnClickListener() {
            val check1 = binding.checkboxCorrigir.isChecked
            val check2 = binding.checkboxAlimentar.isChecked
            val check3 = binding.checkboxMalhar.isChecked

            try {
                val valorDigitado = binding.editText.text.toString().toInt()

                if (valorDigitado != lastValue) {
                    campoResultado.clearComposingText()
                    campoResultado.text = ""
                    binding.textViewResultadoInsulina.text = ""

                    try {
                        binding.textViewResult.visibility = View.VISIBLE

                        campoResultado.text = null
                        campoResultado.text = viewModel.calcularGlicemia(
                            valorDigitado,
                            check1,
                            check2,
                            check3,
                            requireContext(),
                        )

                        lastValue = valorDigitado
                        val imm =
                            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    } catch (e: Exception) {
                        campoResultado.setText("Erro")
                    }
                }
            } catch (E: Exception) {
                Toast.makeText(requireContext(), "Insira um valor de glicemia", Toast.LENGTH_SHORT).show()
                // Snakbar.make(view,"Insira um valor de glicemia" , Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
