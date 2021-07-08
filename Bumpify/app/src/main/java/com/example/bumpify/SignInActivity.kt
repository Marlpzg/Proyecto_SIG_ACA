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
import android.util.Base64
import android.util.Base64.DEFAULT

import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bumpify.model.Post
import com.example.bumpify.model.User
import com.example.bumpify.model.UserModel
import com.example.bumpify.repository.Repository
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.scottyab.aescrypt.AESCrypt
import retrofit2.HttpException
import java.time.Duration
import java.util.Base64.getEncoder
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.login.LoginException

class SignInActivity : AppCompatActivity() {
    lateinit var txtnombre: EditText
    lateinit var txtapellido: EditText
    lateinit var txtcorreo: EditText
    lateinit var txtusuario: EditText
    lateinit var txtcontra: EditText
    lateinit var rbngeneroF: RadioButton
    lateinit var rbngeneroM: RadioButton
    lateinit var radioGroup:RadioGroup

    lateinit var genero:String


    private lateinit var viewModel: MainViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: MainViewModelFactory

    //Modelo donde se guardan las respuestas del servidor
    data class Req(@SerializedName("data") val mensaje: String, @SerializedName("codigo") val codigo: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        //Inicialización de los inputs de la activity
        txtnombre = findViewById<EditText>(R.id.txtNombre)
        txtapellido = findViewById<EditText>(R.id.txtApellido)
        txtcorreo = findViewById<EditText>(R.id.txtCorreo)
        txtusuario = findViewById<EditText>(R.id.txtUsuario)
        txtcontra = findViewById<EditText>(R.id.txtContra)
        rbngeneroM = findViewById<RadioButton>(R.id.rdbMasculino)
        rbngeneroF = findViewById<RadioButton>(R.id.rdbFemenino)
        radioGroup = findViewById<RadioGroup>(R.id.RadioGroupSignIn)
    }

    /**
     * Encripta la contraseña ingresada en el EditText
     * Envia los datos al servidor
     * Muestra error en caso de no lograr hacer el envío al servidor
     */
    fun enviarDatos(v: View){
        var nombre = txtnombre.text.toString()
        var apellido = txtapellido.text.toString()
        var correo = txtcorreo.text.toString()
        var usuario = txtusuario.text.toString()
        var contra = txtcontra.text.toString()

        var nombreFlag: Boolean = false
        var apellidoFlag: Boolean = false
        var correoFlag: Boolean = false
        var usuarioFlag: Boolean = false
        var contraFlag: Boolean = false
        var generoFlag: Boolean = false

        if(rbngeneroM.isChecked){
            genero = "M"
        }else if(rbngeneroF.isChecked){
            genero = "F"
        }

        //Validación de inputs
        if(!validartamanoinput(1, nombre, 20)){
            txtnombre.setError("El nombre debe estar comprendido entre 1 y 20 caracteres")
            nombreFlag = false
        }else{
            txtnombre.error = null
            nombreFlag = true
        }

        //Validación de inputs
        if (!validartamanoinput(1, apellido, 20)){
            txtapellido.setError("El Apellido debe estar comprendido entre 1 y 20 caracteres")
            apellidoFlag = false
        }else{
            txtapellido.error = null
            apellidoFlag = true
        }
        //Validación de inputs
        if (!validartamanoinput(1, correo, 300)) {
            txtcorreo.setError("El correo debe estar comprendido entre 1 y 300 caracteres")
            correoFlag = false
        }else if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            txtcorreo.setError("El formato del campo correo es incorrecto")
            correoFlag = false
        }else{
            txtcorreo.error = null
            correoFlag = true
        }
        //Validación de inputs
        if (!validartamanoinput(5, usuario, 30)){
            txtusuario.setError("El usuario debe estar comprendido entre 5 y 30 caracteres")
            usuarioFlag = false

        }else{
            txtusuario.error = null
            usuarioFlag = true
        }
        //Validación de inputs
        if (!validartamanoinput(8, contra, 50)){
            txtcontra.setError("La contraseña debe estar comprendido entre 8 y 50 caracteres")
            contraFlag = false
        }else{
            txtcontra.error = null
            contraFlag = true
        }
        //Validación de inputs
        if (!rbngeneroF.isChecked && !rbngeneroM.isChecked){
            rbngeneroF.setError("Seleccione un genero")
            generoFlag = false
        }else{
            rbngeneroF.error = null
            generoFlag = true
        }

        //Si todos los inputs estan correctos, se intenta hacer un envio de datos al servidor
        if(nombreFlag && apellidoFlag && correoFlag && usuarioFlag && contraFlag && generoFlag) {
            val myPost = User(nombre, apellido, correo, usuario, AESCrypt.encrypt(contra, "hello world"), genero)
            viewModel.pushUser(myPost)

        }

    }

    override fun onStart() {
        super.onStart()

        //Configuración para envir datos a la API
        repository  = Repository()
        viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        viewModel.myRespuesta.observe(this, Observer { response ->

            val res: SignInActivity.Req = Gson().fromJson(response.body()?.res,SignInActivity.Req::class.java)

            //Validación de la respuesta de la API
            if(res.codigo == 500){
                val contexto = findViewById<View>(R.id.signincontainer)
                val snack = Snackbar.make(contexto,res.mensaje, Snackbar.LENGTH_INDEFINITE);
                snack.setAction("Aceptar",View.OnClickListener { snack.dismiss()})
                snack.show()
            }else{
                var intent = Intent(this, LogInActivity::class.java)
                intent.putExtra("mensaje", "Se ha registrado el usuario correctamente")
                startActivity(intent)
            }
            Log.d("Respuesta: ", res.codigo.toString())

        })

    }
    /**
     * Retorna un booleano para verificar si esta validado o no el input
     * Se utiliza para validar la longitud del input segun los parametros ingresados
     * @param tamaniominimo int longitud minimo de la cadena
     * @param tamaniomaximo int longitud máximo de la cadena
     * @param cadena String cadena a validar*/
    fun validartamanoinput(tamaniominimo: Int, cadena: String, tamaniomaximo: Int):Boolean{
        if(cadena.trim().length < tamaniominimo){
            return false;
        }else if(cadena.trim().length > tamaniomaximo){
            return false
        }
        return true
    }
    /**
     * Función para abrir la actividad LogInActivity
     * */
    fun abrirLogIn(v: View){
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
    }

}