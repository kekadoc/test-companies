package com.kekadoc.test.companies

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG: String = "MainActivity-TAG"
    }

    private var navController: NavController? = null
    private var menuItemRefresh: MenuItem? = null
    private var viewModel: ActivityViewModel? = null

    fun setAccessRefreshAction(access: Boolean) {
        menuItemRefresh?.isVisible = access
    }

    fun navigate(destination: Int, data: Bundle? = null) {
        navController?.navigate(destination, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(ActivityViewModel::class.java)
        navController = findNavController(R.id.nav_host_fragment).apply {
            val appBarConfiguration = AppBarConfiguration(graph)
            NavigationUI.setupActionBarWithNavController(this@MainActivity, this, appBarConfiguration)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)

        this.menuItemRefresh = menu?.findItem(R.id.menu_item_refresh)?.setOnMenuItemClickListener {
            viewModel?.refreshData()
            true
        }
        return true
    }

}