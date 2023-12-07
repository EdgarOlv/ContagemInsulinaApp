package com.example.contagemglicemia.modules.Report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.contagemglicemia.modules.Report.pages.page1.ReportOne
import com.example.contagemglicemia.modules.Report.pages.page2.ReportTwo
import com.example.contagemglicemia.modules.Report.pages.page3.ReportThree
import com.example.contagemglicemia.R
import com.example.contagemglicemia.ViewPagerAdapter
import com.example.contagemglicemia.databinding.FragmentReportBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding
    lateinit var tabs: TabLayout
    private lateinit var viewPager: ViewPager2

    private val titleTabList = arrayOf(
        "Relatório",
        "Gráfico",
        "Análise"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        val view = binding.root

        // menuButton = binding.menuButton

        /*menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            //menuButton.visibility = View.GONE
            Toast.makeText(requireContext(), "Botão clicado!", Toast.LENGTH_SHORT).show()

        }*/

        CallReportData()

        val fragmentList = mutableListOf(
            ReportOne.newInstance(),
            ReportTwo.newInstance(),
            ReportThree.newInstance()
        )
        val viewPagerAdapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        viewPager = binding.viewPagerReport
        viewPager.adapter = viewPagerAdapter

        tabs = binding.tabsChoiceReport
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = titleTabList[position]
        }.attach()

        binding.tabsChoiceReport.setOnClickListener {
            val itemId = it.id

            val position = when (itemId) {
                R.id.glicemia -> 0
                R.id.configuracao -> 1
                R.id.relatorio -> 2
                R.id.firebase -> 3
                else -> throw IllegalArgumentException("Unknown menu item")
            }

            viewPager.setCurrentItem(position, true)

            true
        }

        return view
    }

    private fun CallReportData() {
        // Toast.makeText(context, "Item 1", Toast.LENGTH_SHORT).show()
        val fragment = ReportOne()
    }

    companion object {
        fun newInstance() = ReportFragment()
    }
}
