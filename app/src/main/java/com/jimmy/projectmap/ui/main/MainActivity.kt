package com.jimmy.projectmap.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jimmy.projectmap.R

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val host = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navController = host.navController

        val topBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(topBar)
        setupActionBarWithNavController(navController)

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.setupWithNavController(navController)

        // optional: sinkron judul
        navController.addOnDestinationChangedListener { _, dest, _ ->
            supportActionBar?.title = dest.label
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_top_appbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_suppliers -> {
                navController.navigate(R.id.suppliersFragment)
                true
            }
            R.id.action_import -> {
                navController.navigate(R.id.importReceiptFragment)
                true
            }
            R.id.action_settings -> {
                navController.navigate(R.id.settingsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp() || super.onSupportNavigateUp()
}