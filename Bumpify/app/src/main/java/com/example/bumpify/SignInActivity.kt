package com.example.bumpify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Base64.DEFAULT

import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bumpify.model.Post
import com.example.bumpify.model.User
import com.example.bumpify.repository.Repository
import java.time.Duration
import java.util.Base64.getEncoder
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class SignInActivity : AppCompatActivity() {
    lateinit var txtnombre: EditText
    lateinit var txtapellido: EditText
    lateinit var txtcorreo: EditText
    lateinit var txtusuario: EditText
    lateinit var txtcontra: EditText
    lateinit var rbngeneroF: RadioButton
    lateinit var rbngeneroM: RadioButton

    lateinit var genero:String

    private lateinit var viewModel: MainViewModel
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
    }

    fun enviarDatos(v: View){
        var nombre = txtnombre.text.toString()
        var apellido = txtapellido.text.toString()
        var correo = txtcorreo.text.toString()
        var usuario = txtusuario.text.toString()
        var contra = txtcontra.text.toString()
        if(rbngeneroM.isChecked){
            genero = "M"
        }else if(rbngeneroF.isChecked){
            genero = "F"
        }
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        val myPost = User(nombre, apellido, correo, usuario, encrypt(contra, "1234567812345678"), genero)
        viewModel.pushUser(myPost)
        viewModel.myResponse.observe(this, Observer { response ->
            Log.d("usuario", response.toString())
        })
    }
    fun validartamanoinput(tamaniominimo: Int, cadena: String, tamaniomaximo: Int){
        if(cadena.length < tamaniominimo){
            Toast.makeText(this, "Su cadena debe ser mayor a "+tamaniominimo.toString(), Toast.LENGTH_LONG)
        }else if(cadena.length > tamaniomaximo){
            Toast.makeText(this, "Su cadena debe ser mayor a "+tamaniominimo.toString(), Toast.LENGTH_LONG)
        }
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

}