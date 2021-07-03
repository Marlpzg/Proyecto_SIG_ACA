package com.example.bumpify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.gsm.GsmCellLocation
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bumpify.model.User
import com.example.bumpify.model.UserModel
import com.example.bumpify.model.UserReq
import com.example.bumpify.model.UserSignIn
import com.example.bumpify.repository.Repository
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.scottyab.aescrypt.AESCrypt
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class LogInActivity : AppCompatActivity() {
    lateinit var usuario : EditText
    lateinit var contra: EditText
    private lateinit var viewModel: MainViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: MainViewModelFactory
    private lateinit var btnIniciar: View

    data class Req(@SerializedName("data") val user: UserModel)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        usuario = findViewById<EditText>(R.id.txtUsuario1)
        contra = findViewById<EditText>(R.id.txtContra1)
    }

    override fun onStart() {
        repository  = Repository()
        viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel.getUsu.observe(this, Observer { response ->
            val res: Req = Gson().fromJson(response.body()?.user, Req::class.java)

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
                btnIniciar.isEnabled = true
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }

        })
        super.onStart()
    }



    fun iniciarSesion(v: View) {
        btnIniciar = v
        var usertext = usuario.text.toString()
        var contratext = contra.text.toString()
        btnIniciar.isEnabled = false
        AESCrypt.encrypt(contratext, "hello world")
        //val getUser = UserSignIn(usertext, encrypt(contratext, "1234567812345678"))
        viewModel.getUsu(usertext, AESCrypt.encrypt(contratext, "hello world"))

    }

    fun abrirSignIn(v: View){
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

}