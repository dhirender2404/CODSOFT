package com.example.wiwa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_activity)
        val nav = findViewById<BottomNavigationView>(R.id.nav5)
        nav.setOnItemReselectedListener{ item->
            when(item.itemId){
                R.id.backHome->{
                    val t = Intent(this, MainActivity::class.java)
                    startActivity(t)
                }
            }
        }
    }
}
