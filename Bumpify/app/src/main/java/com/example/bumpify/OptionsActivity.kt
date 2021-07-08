/*
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
 */

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
    lateinit var  btnAssesination : CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
        btnAccident = findViewById(R.id.btnAccident)
        btnThief = findViewById(R.id.btnThief)
        btnCarCrash = findViewById(R.id.btnCrash)
        btnBump = findViewById(R.id.btnbump)
        btnAssesination = findViewById(R.id.btnAssesination)

        //Preparar un intent para enviar datos a la actividad ReportActivity
        val intent = getIntent()
        val latitude = intent.getDoubleExtra("latitude", 1.1)
        val longitude = intent.getDoubleExtra("longitude", 1.1)

        //Sección de código para colocar un evento Click a los cardview
        btnThief.setOnClickListener(View.OnClickListener {

            enviarDatos(1, latitude, longitude, "robo", R.drawable.robber)

        })
        btnBump.setOnClickListener(View.OnClickListener {
            enviarDatos(2, latitude, longitude, "Bache", R.drawable.bump)

        })
        btnAccident.setOnClickListener(View.OnClickListener {
            enviarDatos(3, latitude, longitude, "Obstaculo", R.drawable.triangle)

        })
        btnAssesination.setOnClickListener(View.OnClickListener {
            enviarDatos(4, latitude, longitude, "Asesinato", R.drawable.asesinato)

        })
        btnCarCrash.setOnClickListener(View.OnClickListener {
            enviarDatos(5, latitude, longitude, "Choque", R.drawable.crash)

        })

    }
    /**
     * Envia los datos proporcionados a la actividad ReportActivity
     * @param id Int recibe el id del tipo de reporte
     * @param latitude Double recibe la latitud
     * @param longitude Double recibe la longitud
     * @param nombre String recibe el nombre del reporte
     * @param imagen Int recibe la imagen a colocar en el reporte
     *
     * */
    fun enviarDatos(id: Int, latitude: Double, longitude: Double, nombre: String, imagen: Int){
        var intent = Intent(this, ReportActivity::class.java)
        Log.d("latitude", latitude.toString())
        Log.d("longitude", longitude.toString())

        intent.putExtra("id", id)
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        intent.putExtra("name", nombre)
        intent.putExtra("image", imagen)
        startActivity(intent)
    }

    fun goBack(v: View){
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}