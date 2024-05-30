package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SixthActivity : AppCompatActivity() {

    private lateinit var t1: TextView
    private lateinit var t2: TextView
    private lateinit var i1: ImageView
    private lateinit var ib1: Button
    private lateinit var ib2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sixth)

        t1 = findViewById(R.id.si3)
        t2 = findViewById(R.id.si4)
        i1 = findViewById(R.id.si2)
        ib1 = findViewById(R.id.si5)
        ib2 = findViewById(R.id.si6)


        val intent = intent
        val  str = intent.getStringExtra("game")
        val st = intent.getStringExtra("gq")
        val per = intent.getStringExtra("per")
        t1.text = "Score::$str/$st"
        var com = per.toString().toInt()

        if (com == 0 && st == "0")
        { t2.text = "Not Answered::"+per+"%"
            i1.setImageResource(R.drawable.emoji1)
        }
        if (com > 0 && com <= 30)
        {t2.text = "Poor::"+per+"%"
            i1.setImageResource(R.drawable.emoji2)
        }
        if (com > 30 && com <= 45) {
            t2.text = "Fair::"+per+"%"
            i1.setImageResource(R.drawable.emoji3)
        }
        if (com > 45 && com <= 60) {
            t2.text = "Average::" + per + "%"
            i1.setImageResource(R.drawable.emoji4)
        }
        if (com > 60 && com <= 90) {
            t2.text = "Good::" + per + "%"
            i1.setImageResource(R.drawable.emoji5)
        }
        if (com > 90) {
            t2.text = "Excellent::" + per + "%"
            i1.setImageResource(R.drawable.emoji6)
        }
        ib1.setOnClickListener {
            val t = Intent(this@SixthActivity, FirstActivity::class.java)
            startActivity(t)
            finish()
        }

        ib2.setOnClickListener {
            val t = Intent(this@SixthActivity, OneActivity::class.java)
            startActivity(t)
            finish()
        }
    }



}