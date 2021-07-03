package com.example.bumpify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class OptionsActivity : AppCompatActivity() {
    lateinit var btnBump : Button
    lateinit var btnCarCrash : Button
    lateinit var btnThief : Button
    lateinit var  btnAccident : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
        btnAccident = findViewById(R.id.btnAccident)
        btnThief = findViewById(R.id.btnThief)
        btnCarCrash = findViewById(R.id.btnCrash)
        btnBump = findViewById(R.id.btnbump)
        btnThief.setOnClickListener(View.OnClickListener {
            var intent = Intent(this, ReportActivity::class.java)
            intent.putExtra("ID", 1)
            startActivity(intent)

        })
        btnBump.setOnClickListener(View.OnClickListener {
            var intent = Intent(this, ReportActivity::class.java)
            intent.putExtra("ID", 2)
            startActivity(intent)

        })
        btnAccident.setOnClickListener(View.OnClickListener {
            var intent = Intent(this, ReportActivity::class.java)
            intent.putExtra("ID", 3)
            startActivity(intent)

        })
        btnCarCrash.setOnClickListener(View.OnClickListener {
            var intent = Intent(this, ReportActivity::class.java)
            intent.putExtra("ID", 4)
            startActivity(intent)

        })

    }

}