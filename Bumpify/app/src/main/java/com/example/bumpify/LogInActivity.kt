package com.example.bumpify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.gsm.GsmCellLocation
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bumpify.model.User
import com.example.bumpify.model.UserModel
import com.example.bumpify.model.UserReq
import com.example.bumpify.model.UserSignIn
import com.example.bumpify.repository.Repository
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class LogInActivity : AppCompatActivity() {
    lateinit var usuario : EditText
    lateinit var contra: EditText
    private lateinit var viewModel: MainViewModel
    data class Req(@SerializedName("data") val user: UserModel)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        usuario = findViewById<EditText>(R.id.txtUsuario1)
        contra = findViewById<EditText>(R.id.txtContra1)
    }

    fun iniciarSesion(v: View) {
        var usertext = usuario.text.toString()
        var contratext = contra.text.toString()

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        val getUser = UserSignIn(usertext, encrypt(contratext, "1234567812345678"))

        viewModel.getUsu(usertext)
        viewModel.getUsu.observe(this, Observer { response ->
            val pass: Req = Gson().fromJson(response.user, Req::class.java)
            Log.d("respuesta:", pass.toString())
        })
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
        return Base64.encode(encrypt, Base64.DEFAULT).toString()
    }

    
}