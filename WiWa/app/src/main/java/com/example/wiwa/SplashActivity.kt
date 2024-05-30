package com.example.wiwa

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val logo = findViewById<ImageView>(R.id.splashLogo)
        Handler().postDelayed({
            val set = AnimatorInflater
                .loadAnimator(this@SplashActivity, R.animator.logo_animate) as AnimatorSet
            set.setTarget(logo)
            set.start()
            Handler().postDelayed({
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            },600)
        },1000)


    }
}