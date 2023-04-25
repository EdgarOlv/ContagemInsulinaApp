package com.example.contagemglicemia.Modules.Report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.contagemglicemia.Modules.Report.pages.page1.ReportOne
import com.example.contagemglicemia.R
import com.example.contagemglicemia.databinding.FragmentReportBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var fab: FloatingActionButton
    private lateinit var toggle: ActionBarDrawerToggle

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
        drawerLayout = binding.drawerLayout
        navView = binding.navigationView
        fab = binding.fabMenu

        fab.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            fab.visibility = View.GONE
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_1 -> {
                    Toast.makeText(context, "Item 1", Toast.LENGTH_SHORT).show()
                    val fragment = ReportOne()
                    childFragmentManager.beginTransaction().replace(R.id.content_frame, fragment)
                        .commit()
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.menu_item_2 -> {
                    // código para lidar com o clique no itm 2do menu
                    true
                }
                // adicione mais itens do menu aqui
                else -> false
            }
        }

        toggle = object : ActionBarDrawerToggle(requireActivity(), drawerLayout, null, 0, 0) {
            override fun onDrawerClosed(view: View) {
                fab.visibility = View.VISIBLE
            }
        }

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        return binding.root
    }
}
