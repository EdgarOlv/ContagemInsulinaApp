package com.example.contagemglicemia

import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.contagemglicemia.dao.FirebaseDB
import com.example.contagemglicemia.databinding.ActivityMainBinding
import com.example.contagemglicemia.modules.Config.ConfigFragment
import com.example.contagemglicemia.modules.Firebase.FirebaseModule
import com.example.contagemglicemia.modules.Home.HomeFragment
import com.example.contagemglicemia.modules.Report.ReportFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDb: FirebaseDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        supportActionBar?.title =
            Html.fromHtml("<font color=\"#FFFFFF\">Contagem de Glicemia</font>")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentList = mutableListOf(
            HomeFragment.newInstance(),
            ConfigFragment.newInstance(),
            ReportFragment.newInstance(),
            FirebaseModule.newInstance(),
        )
        val viewPagerAdapter = ViewPagerAdapter(
            fragmentList,
            this.supportFragmentManager,
            lifecycle,
        )
        viewPager = binding.viewPagerGlicemy
        viewPager.adapter = viewPagerAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val menuItem = binding.bottomNavigationView.menu.getItem(position)
                menuItem.isChecked = true
            }
        })

        setupListeners()
        setupFirebase()
    }

    private fun setupListeners() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            val itemId = it.itemId

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
    }

    private fun setupFirebase() {
        FirebaseApp.initializeApp(this)

        auth = FirebaseAuth.getInstance()

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        firebaseDb = FirebaseDB()

        firebaseDb.ReceberListNuvem(this)
    }

}
