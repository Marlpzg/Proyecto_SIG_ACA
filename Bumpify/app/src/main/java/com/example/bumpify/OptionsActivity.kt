package com.example.bumpify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.cardview.widget.CardView

class OptionsActivity : AppCompatActivity() {
    lateinit var btnBump : CardView
    lateinit var btnCarCrash : CardView
    lateinit var btnThief : CardView
    lateinit var  btnAccident : CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
        btnAccident = findViewById(R.id.btnAccident)
        btnThief = findViewById(R.id.btnThief)
        btnCarCrash = findViewById(R.id.btnCrash)
        btnBump = findViewById(R.id.btnbump)

        val intent = getIntent()
        val latitude = intent.getDoubleExtra("latitude", 1.1)
        val longitude = intent.getDoubleExtra("longitude", 1.1)

        btnThief.setOnClickListener(View.OnClickListener {
            enviarDatos(1, latitude, longitude)

        })
        btnBump.setOnClickListener(View.OnClickListener {
            enviarDatos(2, latitude, longitude)

        })
        btnAccident.setOnClickListener(View.OnClickListener {
            enviarDatos(3, latitude, longitude)

        })
        btnCarCrash.setOnClickListener(View.OnClickListener {
            enviarDatos(4, latitude, longitude)

        })

    }
    fun enviarDatos(id: Int, latitude: Double, longitude: Double){
        var intent = Intent(this, ReportActivity::class.java)
        Log.d("latitude", latitude.toString())
        Log.d("longitude", longitude.toString())

        intent.putExtra("id", id)
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        startActivity(intent)
    }

}