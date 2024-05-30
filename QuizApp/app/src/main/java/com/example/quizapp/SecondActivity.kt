package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.quizapp.ui.theme.QuizAppTheme

class SecondActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var ctn1 : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        ctn1=findViewById(R.id.c1)


        ctn1.setOnClickListener(this)

        onBackPressedDispatcher.addCallback(this,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val t = Intent(this@SecondActivity, FirstActivity::class.java)
                startActivity(t)
                finish()
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.c1 -> {
               val t = Intent(this@SecondActivity,OneActivity::class.java)
                startActivity(t)
                finish()

            }
        }
    }
}