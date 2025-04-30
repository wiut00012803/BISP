package com.example.MobileApp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.MobileApp.R
import com.google.android.material.appbar.MaterialToolbar
import com.example.MobileApp.util.Prefs
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, MainFragment())
                .commit()
        }

        val bn = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bn.setOnItemSelectedListener { item ->
            val frag = when (item.itemId) {
                R.id.nav_main -> MainFragment()
                R.id.nav_profile -> ProfileFragment()
                R.id.nav_history -> HistoryFragment()
                else -> OnSiteFragment()
            }
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, frag)
                .commit()
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                Prefs.clear(this)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}