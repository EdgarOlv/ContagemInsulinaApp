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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.contagemglicemia.dao.FirebaseDB
import com.example.contagemglicemia.dao.MyDatabaseManager
import com.example.contagemglicemia.databinding.DetailGlicemyBinding
import com.example.contagemglicemia.databinding.FragmentReportOneBinding
import com.example.contagemglicemia.model.Glicemia
import com.example.contagemglicemia.modules.Report.pages.page1.adapter.RegistroGlicemiaAdapter

class ReportOne : Fragment() {

    private lateinit var binding: FragmentReportOneBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var dbManager: MyDatabaseManager
    val TAG = "ReportOne"
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var firebaseDb: FirebaseDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        dbManager = MyDatabaseManager(requireContext())
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

        firebaseDb.ReceberListNuvem(requireContext())
        val registros = dbManager.getAllGlicemy()

        val adapter = RegistroGlicemiaAdapter(registros) { registro ->
            Log.i(TAG, "onViewCreated: Clicou no registro ${registro.id}")
            showGlicemyDetailsDialog(registro)
        }
        recyclerView.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener {
            firebaseDb.ReceberListNuvem(requireContext())

            val registros = dbManager.getAllGlicemy()
            val adapter = RegistroGlicemiaAdapter(registros) { registro ->
                Log.i(TAG, "onViewCreated: Clicou no registro ${registro.id}")
            }
            recyclerView.adapter = adapter
            swipeRefreshLayout.isRefreshing = false
        }
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
        valueDate.text = glicemy.date
        valueObs.text = glicemy.observation

        builder.setPositiveButton("Fechar", null)
        builder.show()

        val serialNumber: String = Build.SERIAL

        Log.i(TAG, serialNumber)
    }

    companion object {
        fun newInstance() = ReportOne()
    }
}
