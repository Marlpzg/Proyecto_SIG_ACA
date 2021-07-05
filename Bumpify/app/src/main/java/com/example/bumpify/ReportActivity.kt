package com.example.bumpify

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bumpify.model.ReportModel
import com.example.bumpify.repository.Repository
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.scottyab.aescrypt.AESCrypt
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class ReportActivity : AppCompatActivity() {
     lateinit var ID: String
     lateinit var txtDescripcion: EditText
    lateinit var latitude: String
    lateinit var longitude: String


    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        val intent = getIntent()
        ID = intent.getIntExtra("id", 1).toString()
        latitude = intent.getDoubleExtra("latitude", 1.1).toString()
        longitude = intent.getDoubleExtra("longitude", 1.1).toString()

        Log.d("latitude", latitude.toString())
        Log.d("longitude", longitude.toString())
        txtDescripcion = findViewById<EditText>(R.id.txtDescripcion)

    }

    override fun onStart() {

        super.onStart()
    }

    fun enviarReporte(v: View){
        var gps : Location

        var descripcion = txtDescripcion.text.toString()
        var lat = latitude.toDouble();
        var long = longitude.toDouble();
        var user = readFromFile()
        var type = ID.toInt()
        val repository  = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        val myReport = ReportModel(type,descripcion,lat,long,user)
        viewModel.pushReport(myReport)

        viewModel.myReport.observe(this, Observer { response ->
            Log.d("Reporte:", response.toString())

        })
    }

    //Función para leer de un archivo
    fun readFromFile(): String {
        try {
            val dir = File(filesDir, "mydir/sesion.txt")
            val gpxfile = FileInputStream(dir)
            val reader = BufferedReader(InputStreamReader(gpxfile))
            val linea = reader.readLine()

            if(linea.isNotEmpty()){
                return linea
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "error"
    }
}