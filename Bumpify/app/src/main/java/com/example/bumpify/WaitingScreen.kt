package com.example.bumpify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class WaitingScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_screen)
        val iv_logo = findViewById<ImageView>(R.id.iv_logo)
        iv_logo.alpha = 0f;
        iv_logo.animate().setDuration(4000).alpha(1f).withEndAction {
            if (readFromFile()){
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }else{
                val i = Intent(this, LogInActivity::class.java)
                startActivity(i)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }
    }

    //Función para leer de un archivo
    fun readFromFile(): Boolean {
        try {
            val dir = File(filesDir, "mydir/sesion.txt")
            val gpxfile = FileInputStream(dir)
            val reader = BufferedReader(InputStreamReader(gpxfile))
            val linea = reader.readLine()

            if(linea == "true"){
                return true
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return false
    }
}