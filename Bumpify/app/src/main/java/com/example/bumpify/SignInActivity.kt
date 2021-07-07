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
    data class Req(@SerializedName("data") val mensaje: String)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        txtnombre = findViewById<EditText>(R.id.txtNombre)
        txtapellido = findViewById<EditText>(R.id.txtApellido)
        txtcorreo = findViewById<EditText>(R.id.txtCorreo)
        txtusuario = findViewById<EditText>(R.id.txtUsuario)
        txtcontra = findViewById<EditText>(R.id.txtContra)
        rbngeneroM = findViewById<RadioButton>(R.id.rdbMasculino)
        rbngeneroF = findViewById<RadioButton>(R.id.rdbFemenino)
        radioGroup = findViewById<RadioGroup>(R.id.RadioGroupSignIn)
    }

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

        if(!validartamanoinput(1, nombre, 20)){
            txtnombre.setError("El nombre debe estar comprendido entre 1 y 20 caracteres")
            nombreFlag = false
        }else{
            txtnombre.error = null
            nombreFlag = true
        }


        if (!validartamanoinput(1, apellido, 20)){
            txtapellido.setError("El Apellido debe estar comprendido entre 1 y 20 caracteres")
            apellidoFlag = false
        }else{
            txtapellido.error = null
            apellidoFlag = true
        }

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

        if (!validartamanoinput(5, usuario, 30)){
            txtusuario.setError("El usuario debe estar comprendido entre 5 y 30 caracteres")
            usuarioFlag = false

        }else{
            txtusuario.error = null
            usuarioFlag = true
        }

        if (!validartamanoinput(8, contra, 50)){
            txtcontra.setError("La contraseña debe estar comprendido entre 8 y 50 caracteres")
            contraFlag = false
        }else{
            txtcontra.error = null
            contraFlag = true
        }

        if (!rbngeneroF.isChecked && !rbngeneroM.isChecked){
            rbngeneroF.setError("Seleccione un genero")
            generoFlag = false
        }else{
            rbngeneroF.error = null
            generoFlag = true
        }


        if(nombreFlag && apellidoFlag && correoFlag && usuarioFlag && contraFlag && generoFlag) {
            val myPost = User(nombre, apellido, correo, usuario, AESCrypt.encrypt(contra, "hello world"), genero)
            viewModel.pushUser(myPost)
            var intent = Intent(this, LogInActivity::class.java)
            intent.putExtra("mensaje", "Se ha registrado el usuario correctamente")
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        repository  = Repository()
        viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        viewModel.myRespuesta.observe(this, Observer { response ->

            val res: SignInActivity.Req = Gson().fromJson(response.body()?.res,SignInActivity.Req::class.java)
            Log.d("Respuesta: ", response.toString())

        })

    }

    fun validartamanoinput(tamaniominimo: Int, cadena: String, tamaniomaximo: Int):Boolean{
        if(cadena.trim().length < tamaniominimo){
            return false;
        }else if(cadena.trim().length > tamaniomaximo){
            return false
        }
        return true
    }
    fun encrypt(input: String, password: String): String {
        //1. Create a cipher object
        val cipher = Cipher.getInstance("AES")
        //2. Initialize cipher
        //The key you specified
        val keySpec = SecretKeySpec(password.toByteArray(),"AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        //3. Encryption and decryption
        val encrypt = cipher.doFinal(input.toByteArray());
        return Base64.encode(encrypt, DEFAULT).toString()
    }

    fun abrirLogIn(v: View){
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
    }

}