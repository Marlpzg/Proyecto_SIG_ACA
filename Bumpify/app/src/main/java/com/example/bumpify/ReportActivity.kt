package com.example.bumpify

import android.content.Intent
import android.location.Location
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bumpify.model.ReportModel
import com.example.bumpify.repository.Repository
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
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
    lateinit var txtplaceholder: TextView
    lateinit var ivplaceholder: ImageView
    var imageid: Int = 0
    var reportname: String = ""

    private lateinit var viewModel: MainViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: MainViewModelFactory


    data class Req(@SerializedName("data") val mensaje: String, @SerializedName("codigo") val codigo: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        val intent = getIntent()
        txtplaceholder = findViewById<TextView>(R.id.tv_placeholder)
        ivplaceholder = findViewById<ImageView>(R.id.iv_report_holder)
        ID = intent.getIntExtra("id", 1).toString()
        latitude = intent.getDoubleExtra("latitude", 1.1).toString()
        longitude = intent.getDoubleExtra("longitude", 1.1).toString()
        reportname = intent.getStringExtra("name").toString()
        imageid = intent.getIntExtra("image", 1)
        var completetext = "Reportando un "+reportname
        txtplaceholder.text = completetext
        ivplaceholder.setImageResource(imageid)
        Log.d("latitude", latitude.toString())
        Log.d("longitude", longitude.toString())
        txtDescripcion = findViewById<EditText>(R.id.txtDescripcion)

    }

    override fun onStart() {
        super.onStart()

       repository  = Repository()
       viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        viewModel.myRespuesta.observe(this, Observer { response ->
            val res: SignInActivity.Req = Gson().fromJson(response.body()?.res,SignInActivity.Req::class.java)
            if(res.codigo == 500){
                val contexto = findViewById<View>(R.id.ReportContainer)
                val snack = Snackbar.make(contexto,res.mensaje, Snackbar.LENGTH_INDEFINITE);
                snack.setAction("Aceptar",View.OnClickListener { snack.dismiss()})
                snack.show()
            }else{
                var intent = Intent(this, MainActivity::class.java)
                intent.putExtra("mensaje", res.mensaje)
                startActivity(intent)
            }

        })
    }

    fun enviarReporte(v: View){
        var gps : Location

        var descripcion = txtDescripcion.text.toString()
        var lat = latitude.toDouble();
        var long = longitude.toDouble();
        var user = readFromFile()
        var type = ID.toInt()


        val myReport = ReportModel(type,descripcion,lat,long,user)
        viewModel.pushReport(myReport)


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