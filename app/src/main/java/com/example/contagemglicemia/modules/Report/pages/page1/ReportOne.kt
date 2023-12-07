package com.example.contagemglicemia.modules.Report.pages.page1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.contagemglicemia.dao.FirebaseDB
import com.example.contagemglicemia.dao.MyDatabaseManager
import com.example.contagemglicemia.databinding.FragmentReportOneBinding
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

    companion object {
        fun newInstance() = ReportOne()
    }
}
