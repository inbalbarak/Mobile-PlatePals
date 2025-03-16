package com.example.platepals

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.widget.Toolbar

class HomeActivity : AppCompatActivity() {
    var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment: NavHostFragment? = supportFragmentManager.findFragmentById(R.id.main_nav_host) as? NavHostFragment
        navController = navHostFragment?.navController
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_bar)

        navController?.let {
            NavigationUI.setupActionBarWithNavController(
                activity = this,
                navController = it
            )

            NavigationUI.setupWithNavController(bottomNavigationView, it)

            bottomNavigationView.setOnItemSelectedListener { item ->
                // Pop back to the start destination of the graph
                it.popBackStack(it.graph.startDestinationId ?: 0, false)

                // Then navigate to the selected item
                NavigationUI.onNavDestinationSelected(item, it)
                true
            }
        }
    }
}
