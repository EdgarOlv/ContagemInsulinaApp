package com.example.contagemglicemia

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.widget.ViewPager2
import com.example.contagemglicemia.dao.FirebaseDB
import com.example.contagemglicemia.dao.MyDatabaseManager
import com.example.contagemglicemia.databinding.ActivityMainBinding
import com.example.contagemglicemia.modules.Config.ConfigFragment
import com.example.contagemglicemia.modules.Firebase.FirebaseModule
import com.example.contagemglicemia.modules.Home.HomeFragment
import com.example.contagemglicemia.modules.Report.ReportFragment
import com.example.contagemglicemia.utils.Event
import com.example.contagemglicemia.utils.EventObserver
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseDb: FirebaseDB
    private lateinit var dbManager: MyDatabaseManager

    private val _countValue = MutableLiveData<Event<Int>>()
    val countValue: LiveData<Event<Int>> = _countValue

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

        auth = FirebaseAuth.getInstance()
        dbManager = MyDatabaseManager(this)

        setupListeners()
        setupFirebase()
        verifyUnsyncGlicemy()
    }

    private fun verifyUnsyncGlicemy() {
        val qtd = dbManager.countUnsyncedGlicemy(this, firebaseDb)
        if (qtd > 0) {
            binding.drawerAppbar.counterGlicemy.visibility = View.VISIBLE
            binding.drawerAppbar.countItens.text = qtd.toString()
        }
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

        binding.drawerAppbar.home.setOnClickListener {
            viewPager.setCurrentItem(0, true)
        }

        binding.drawerAppbar.sync.setOnClickListener {
            if (isInternetAvailable(this) && auth.currentUser != null) {
                dbManager.verifyAndUpdateUnsync(this, firebaseDb)
                setCountGlicemy(0)
                Toast.makeText(this, "Enviado com sucesso!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Sem internet, ou faça login", Toast.LENGTH_SHORT).show()
            }
        }

        countValue.observe(
            this,
            EventObserver { countGlicemy ->
                if (countGlicemy > 0) {
                    binding.drawerAppbar.counterGlicemy.visibility = View.VISIBLE
                    binding.drawerAppbar.sync.visibility = View.VISIBLE
                    binding.drawerAppbar.countItens.text = countGlicemy.toString()
                }else{

                    binding.drawerAppbar.sync.visibility = View.GONE
                }
            },
        )
    }

    fun setCountGlicemy(value: Int) {
        _countValue.postValue(Event(value))
    }

    private fun setupFirebase() {
        FirebaseApp.initializeApp(this)

        auth = FirebaseAuth.getInstance()

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        firebaseDb = FirebaseDB()

        //firebaseDb.ReceberListNuvem(this)
    }

    companion object {
        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val activeNetwork =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

                return when {
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    // Other transports, like Bluetooth or Ethernet, may be added as needed
                    else -> false
                }
            } else {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                return activeNetworkInfo != null && activeNetworkInfo.isConnected
            }
        }
    }
}
