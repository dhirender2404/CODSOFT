package com.example.wiwa


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    lateinit var email : TextView
    private var MY_PERMISSIONS_REQUEST_READ_LOCATION = 1
    private lateinit var tool : androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_READ_LOCATION)

        } else {

            if (savedInstanceState == null) {
                drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
                val navController = this.findNavController(R.id.myNavHostFragment)

                tool = findViewById(R.id.tool)
                setSupportActionBar(tool)
                NavigationUI.setupWithNavController(findViewById<NavigationView>(R.id.navView), navController)
            }
        }

        val navigationView = findViewById<NavigationView>(R.id.navView)
        val drawer = findViewById<DrawerLayout>(R.id.drawerLayout)
        val hView = navigationView.getHeaderView(0)
        val text = hView.findViewById<TextView>(R.id.textView20)

        text.text = "Welcome User"

        navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.about ->{
                   val i = Intent(this, AboutActivity::class.java)
                    drawer.closeDrawers()
                   startActivity(i)

                }
            }
            false
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
