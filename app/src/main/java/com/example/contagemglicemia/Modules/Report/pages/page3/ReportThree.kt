package com.example.contagemglicemia.Modules.Report.pages.page3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.contagemglicemia.Modules.Report.pages.page2.ReportTwo
import com.example.contagemglicemia.databinding.FragmentReportTwoBinding

class ReportThree: Fragment() {
    private lateinit var binding: FragmentReportTwoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportTwoBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }



    companion object {
        fun newInstance() = ReportThree()
    }
}