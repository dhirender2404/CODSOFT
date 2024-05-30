package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.quizapp.ui.theme.QuizAppTheme

class FirstActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var btn1 : Button
    private lateinit var btn3 : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        btn1 = findViewById(R.id.b1)
        btn3 = findViewById(R.id.b3)


        btn1.setOnClickListener(this)
        btn3.setOnClickListener(this)
        onBackPressedDispatcher.addCallback(this,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                finish()
            }
        })



    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.b1 -> {
               val intent = Intent(
                    this@FirstActivity,SecondActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.b3 -> {
                showdialog()
            }

        }


    }

    private fun showdialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("EXIT")
        builder.setMessage("Do you want to exit?")
        builder.setPositiveButton("Yes") { _, _ ->
            finish()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.cancel()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

    }




}
