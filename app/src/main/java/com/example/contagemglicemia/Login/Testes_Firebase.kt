package com.example.contagemglicemia.Login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.contagemglicemia.Modules.Report.ReportFragment
import com.example.contagemglicemia.Modules.Report.pages.page1.ReportOne
import com.example.contagemglicemia.Modules.Report.pages.page2.ReportTwo
import com.example.contagemglicemia.R
import com.example.contagemglicemia.ViewPagerAdapter
import com.example.contagemglicemia.databinding.FragmentReportBinding
import com.example.contagemglicemia.databinding.FragmentTestesFirebaseBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class Testes_Firebase : Fragment() {

    private lateinit var binding: FragmentTestesFirebaseBinding
    lateinit var tabs: TabLayout
    private lateinit var viewPager: ViewPager2

    private val titleTabList = arrayOf(
        "Auth",
        "Database"
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
        binding = FragmentTestesFirebaseBinding.inflate(inflater, container, false)
        val view = binding.root

        CallAuth()

        val fragmentList = mutableListOf(
            LoginFragment.newInstance(),
            FirestoreDatabase.newInstance()
        )
        val viewPagerAdapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        viewPager = binding.viewPagerFirebase
        viewPager.adapter = viewPagerAdapter

        tabs = binding.tabsFirebase
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = titleTabList[position]
        }.attach()

        return view
    }

    private fun CallAuth() {
        // Toast.makeText(context, "Item 1", Toast.LENGTH_SHORT).show()
        val fragment = LoginFragment()
    }

    companion object {
        fun newInstance() = Testes_Firebase()
    }
}