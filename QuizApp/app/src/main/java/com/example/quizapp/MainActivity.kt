package com.example.quizapp

import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {


    private lateinit var progressBar: ProgressBar
    private var i = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.insetsController?.hide(WindowInsets.Type.statusBars())
        progressBar = findViewById(R.id.Load_bar)


        while (i <= 100) {

            i += 1
            progressBar.progress = i

        }
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(
                this@MainActivity,FirstActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)


    }



}