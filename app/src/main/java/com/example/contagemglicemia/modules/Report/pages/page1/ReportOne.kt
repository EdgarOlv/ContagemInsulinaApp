package com.example.contagemglicemia.modules.Report.pages.page1

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.contagemglicemia.dao.FirebaseDB
import com.example.contagemglicemia.dao.MyDatabaseManager
import com.example.contagemglicemia.databinding.DetailGlicemyBinding
import com.example.contagemglicemia.databinding.FragmentReportOneBinding
import com.example.contagemglicemia.model.Glicemia
import com.example.contagemglicemia.modules.Report.pages.page1.adapter.RegistroGlicemiaAdapter
import com.example.contagemglicemia.utils.EventObserver

class ReportOne : Fragment() {

    private lateinit var binding: FragmentReportOneBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var dbManager: MyDatabaseManager
    private lateinit var viewModel: ReportOneViewModel
    val TAG = "ReportOne"
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var firebaseDb: FirebaseDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        dbManager = MyDatabaseManager(requireContext())
        setupViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentReportOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recycleViewGlicemy
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        swipeRefreshLayout = binding.swipeRefreshLayout
        firebaseDb = FirebaseDB()

        // firebaseDb.ReceberListNuvem(requireContext())

        viewModel.getAllGlicemy(dbManager)

        setupObserver()

        setupSwipe()
    }

    private fun setupSwipe() {
        swipeRefreshLayout.setOnRefreshListener {
            firebaseDb.ReceberListNuvem(requireContext())

            viewModel.getAllGlicemy(dbManager)

            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupObserver() {
        viewModel.listGlicemy.observe(
            viewLifecycleOwner,
            EventObserver { list ->
                val adapter = RegistroGlicemiaAdapter(list) { registro ->
                    Log.i(TAG, "onViewCreated: Clicou no registro ${registro.id}")
                    showGlicemyDetailsDialog(registro)
                }
                recyclerView.adapter = adapter
            },
        )
    }

    private fun setupViewModel() {
        val viewModelFactory = ReportOneViewModel.Factory()
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ReportOneViewModel::class.java)
    }

    private fun showGlicemyDetailsDialog(glicemy: Glicemia) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Detalhes da Glicemia")

        val dialogView = DetailGlicemyBinding.inflate(LayoutInflater.from(requireContext()))
        builder.setView(dialogView.root)

        val valueGlicemy: TextView = dialogView.valueGlicemy
        val valueDate: TextView = dialogView.valueDate
        val valueObs: TextView = dialogView.valueObs

        valueGlicemy.text = glicemy.value.toString()
        valueDate.text = glicemy.date.toString()
        valueObs.text = glicemy.observation

        builder.setPositiveButton("Fechar", null)
        builder.show()

        // val serialNumber: String = Build.ID
        val serialNumber: String = Build.DEVICE

        // Log.i(TAG, serialNumber)
    }

    companion object {
        fun newInstance() = ReportOne()
    }
}
