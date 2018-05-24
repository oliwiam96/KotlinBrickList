package com.oliwia.bricksapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    val REQUEST_CODE = 10000
    val REQUEST_CODE_SETTINGS = 10001
    var prefix  = "http://fcds.cs.put.poznan.pl/MyWeb/BL/"
    var extension = ".xml"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> {
                startSettingsActivity()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if((requestCode == REQUEST_CODE)
                && (resultCode == Activity.RESULT_OK)){
            if(data != null){
                if(data.hasExtra("colorInt")){
                    var colorInt =  data.extras.getString("colorInt").toInt()
                }
                if(data.hasExtra("precision")){
                    var precision = data.extras.getString("precision").toInt()
                }

            }
        } else if((requestCode == REQUEST_CODE_SETTINGS)
                && (resultCode == Activity.RESULT_OK)){
            if(data != null) {
                if (data.hasExtra("prefix")) {
                    prefix = data.extras.getString("prefix")
                }
                if (data.hasExtra("extension")) {
                    extension = data.extras.getString("extension")
                }
            }
            nav_view.setCheckedItem(R.id.nav_myProjects)

        }
    }

    fun startSettingsActivity(){
        val i = Intent(this, SettingsActivity::class.java)
        i.putExtra("prefix", prefix)
        i.putExtra("extension", extension)
        startActivityForResult(i, REQUEST_CODE_SETTINGS)
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_add -> {
                // Handle the camera action
                Toast.makeText(this, "Oliwia Masian\n127324", Toast.LENGTH_LONG).show()
                val i = Intent(this, AddActivity::class.java)
                i.putExtra("Parametr", "Twoje dane")
                startActivityForResult(i, REQUEST_CODE)

            }
            R.id.nav_myProjects-> {

            }
            R.id.nav_settings-> {
                startSettingsActivity()
            }
            R.id.nav_save-> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
