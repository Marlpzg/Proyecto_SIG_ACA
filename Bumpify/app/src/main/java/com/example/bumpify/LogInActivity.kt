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
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bumpify.model.UserModel
import com.example.bumpify.model.UserReq
import com.example.bumpify.repository.Repository
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.scottyab.aescrypt.AESCrypt
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.net.SocketTimeoutException


class LogInActivity : AppCompatActivity() {


    lateinit var usuario : EditText
    lateinit var contra: EditText
    private lateinit var viewModel: MainViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: MainViewModelFactory
    private lateinit var btnIniciar: View

    //Modelo donde se guardan las respuestas del servidor
    data class Req(@SerializedName("data") val user: UserModel)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        usuario = findViewById<EditText>(R.id.txtUsuario1)
        contra = findViewById<EditText>(R.id.txtContra1)

        // Muestra snackbar, el mensaje es recibido por medio de un intent.
        // El intent es enviado desde SignInActivity.
        var intent = intent
        if(intent.hasExtra("mensaje")){
            var mensaje = intent.getStringExtra("mensaje")
            val contexto = findViewById<View>(R.id.logincontainer)
            val snack = Snackbar.make(contexto, mensaje.toString(), Snackbar.LENGTH_INDEFINITE);
            snack.setAction("Aceptar", View.OnClickListener { snack.dismiss() })
            snack.show()
        }


    }

    override fun onStart() {
        super.onStart()
        //Configuración para envir datos a la API
        repository  = Repository()
        viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        try {
            viewModel.getUsu.observe(this, Observer { response ->


                val res: Req = Gson().fromJson(response.body()?.user, Req::class.java)

                //Validación de la respuesta de la API
                if(res.user.email == "-" && res.user.gender == "-"){
                    val contexto = findViewById<View>(R.id.logincontainer)
                    val snack = Snackbar.make(contexto,"Usuario o contraseña incorrectos", Snackbar.LENGTH_INDEFINITE);
                    snack.setAction("Aceptar",View.OnClickListener { btnIniciar.isEnabled = true })
                    snack.show()

                }else if (res.user.email == "$" && res.user.gender == "$"){

                    val contexto = findViewById<View>(R.id.logincontainer)
                    val snack = Snackbar.make(contexto,"Ocurrió un error inesperado", Snackbar.LENGTH_INDEFINITE);
                    snack.setAction("Aceptar",View.OnClickListener { btnIniciar.isEnabled = true })
                    snack.show()

                }else{
                    writeToFile(res.user.username)
                    btnIniciar.isEnabled = true
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }

            })
        }catch (ex: IOException){
            val contexto = findViewById<View>(R.id.logincontainer)
            val snack = Snackbar.make(contexto,"Sin conexión a internet", Snackbar.LENGTH_INDEFINITE);
            snack.setAction("Aceptar",View.OnClickListener { btnIniciar.isEnabled = true })
            snack.show()
        }

    }


    /**
     * Encripta la contraseña ingresada en el EditText
     * Envia los datos al servidor
     * Muestra error en caso de no lograr hacer el envío al servidor
     */
    fun iniciarSesion(v: View) {
        btnIniciar = v
        var usertext = usuario.text.toString()
        var contratext = contra.text.toString()
        btnIniciar.isEnabled = false
        AESCrypt.encrypt(contratext, "hello world")
        //val getUser = UserSignIn(usertext, encrypt(contratext, "1234567812345678"))
        try {

            viewModel.getUsu(usertext, AESCrypt.encrypt(contratext, "hello world"))
        }catch (e: IOException){
            val contexto = findViewById<View>(R.id.logincontainer)
            val snack = Snackbar.make(contexto,"Sin conexión a internet", Snackbar.LENGTH_INDEFINITE);
            snack.setAction("Aceptar",View.OnClickListener { btnIniciar.isEnabled = true })
            snack.show()
        }



    }


    /**
     * Se encarga de abrir la actividad SignInActivity
     */
    fun abrirSignIn(v: View){
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    /**
     * Función para escribir a un archivo
     */
    fun writeToFile(str: String) {
        val dir = File(filesDir, "mydir")

        if (!dir.exists()) {
            dir.mkdir()
        }

        try {
            Log.d("TAG", dir.toString())
            val gpxfileold = File(dir, "sesion.txt")
            gpxfileold.delete()
            val gpxfile = File(dir, "sesion.txt")
            val writer = FileWriter(gpxfile)
            writer.append(str)
            writer.flush()
            writer.close()
            Log.d("TAG", "Se guardó correctamente")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}