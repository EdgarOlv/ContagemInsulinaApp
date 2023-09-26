package com.example.contagemglicemia

import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.contagemglicemia.Login.Testes_Firebase
import com.example.contagemglicemia.Modules.Config.ConfigFragment
import com.example.contagemglicemia.Modules.Home.HomeFragment
import com.example.contagemglicemia.Modules.Report.ReportFragment
import com.example.contagemglicemia.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)


        supportActionBar?.title =
            Html.fromHtml("<font color=\"#FFFFFF\">Contagem de Glicemia</font>")

        val fragmentList = mutableListOf(
            HomeFragment.newInstance(),
            ConfigFragment.newInstance(),
            ReportFragment.newInstance(),
            Testes_Firebase.newInstance()
        )
        val viewPagerAdapter = ViewPagerAdapter(
            fragmentList,
            this.supportFragmentManager,
            lifecycle
        )
        viewPager = binding.viewPagerGlicemy
        viewPager.adapter = viewPagerAdapter

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

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val menuItem = binding.bottomNavigationView.menu.getItem(position)
                menuItem.isChecked = true
            }
        })
    }

    companion object {
        var viewPager: ViewPager2? = null
    }
}
