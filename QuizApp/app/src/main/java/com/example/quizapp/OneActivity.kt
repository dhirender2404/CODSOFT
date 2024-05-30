package com.example.quizapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

class OneActivity : AppCompatActivity() {
    private lateinit var txt1 : TextView
    private lateinit var txt2 : TextView
    private lateinit var txt3 : TextView
    private lateinit var rtn1 : RadioButton
    private lateinit var rtn2 : RadioButton
    private lateinit var rtn3 : RadioButton
    private lateinit var rtn4 : RadioButton
    private lateinit var radiogr : RadioGroup
    private lateinit var end : Button
    private val list1 = mutableListOf<String>()
    private val list2 = mutableListOf<String>()
    private var qnum = 0
    private lateinit var ques : String
    private var anum = 0
    private var i = 0
    private var bb = 0
    private var cc = 0
    private var dd = 0
    private var c = 0
    private var pp = 0
    private var  list3 =  mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one)



        // Assuming the file is in the raw resource directory
        val inputStream = resources.openRawResource(R.raw.idiom)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))

        // Arrays to store the split values


        bufferedReader.forEachLine { line ->
            val parts = line.split("=")
            if (parts.size == 2) {
                list1.add(parts[0])
                list2.add(parts[1])
            }
        }

        bufferedReader.close()
        rtn1 = findViewById(R.id.r1)
        rtn2 = findViewById(R.id.r2)
        rtn3 = findViewById(R.id.r3)
        rtn4 = findViewById(R.id.r4)
        txt1 = findViewById(R.id.hi2)
        txt2 = findViewById(R.id.hi3)
        txt3 = findViewById(R.id.hi4)
        radiogr = findViewById(R.id.rg)
        end= findViewById(R.id.en)
        onBackPressedDispatcher.addCallback(this,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val intent = Intent(this@OneActivity,SecondActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
        end.setOnClickListener{

        val t = Intent(this@OneActivity,SixthActivity::class.java)
            val k =c.toString()
            val kk = (i-1).toString()
            val per = pp.toString()
            t.putExtra("gq",kk)
            t.putExtra("game",k)
            t.putExtra("per",per)
            startActivity(t)
            finish()
        }


        replay()


    }

    private fun replay() {
        while(i < 5){ i++
            break}
        qnum = Random.nextInt(list1.size)
        anum = Random.nextInt(list2.size)
        bb = Random.nextInt(list2.size)
        cc = Random.nextInt(list2.size)
        dd = Random.nextInt(list2.size)
        ques = list1[qnum]
        list3 = mutableListOf(list2[qnum],list2[bb],list2[cc],list2[dd])
        list3.shuffle()
        txt2.text =""
        txt3.text = ""
        txt1.text = "Q:$i $ques"
        rtn1.text = list3[0]
        rtn2.text = list3[1]
        rtn3.text = list3[2]
        rtn4.text = list3[3]
        if(i>1){
            pp = (c * 100)/(i-1)}


        radiogr.setOnCheckedChangeListener(null)
        radiogr.clearCheck()
        radiogr.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { _, checkedId ->
                val radio: RadioButton = findViewById(checkedId)
                if (radio.text == list2[qnum]) {
                    txt2.text = "Correct"
                    txt2.setTextColor(Color.parseColor("#90ee90"))
                    txt3.text = "Answer: " + list2[qnum]
                    c += 1

                    Handler(Looper.getMainLooper()).postDelayed({
                        replay()
                    }, 1000)



                } else {
                    txt2.text = "Incorrect"
                    txt2.setTextColor(Color.parseColor("#FF474C"))
                    txt3.text = "Answer: " + list2[qnum]
                    Handler(Looper.getMainLooper()).postDelayed({
                        replay()
                    }, 1000)
                }
            }
        )


    }
}
