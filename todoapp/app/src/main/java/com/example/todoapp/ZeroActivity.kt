package com.example.todoapp

import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity


class ZeroActivity : AppCompatActivity() {


    private lateinit var progressBar: ProgressBar
    private var i = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zero)

        window.insetsController?.hide(WindowInsets.Type.statusBars())
        progressBar = findViewById(R.id.Load_bar)


        while (i <= 100) {

            i += 1
            progressBar.progress = i

        }
        Handler(Looper.getMainLooper()).postDelayed({
            val t = Intent(
                this@ZeroActivity, OneActivity::class.java)
            startActivity(t)
            finish()
        }, 2000)


    }



}