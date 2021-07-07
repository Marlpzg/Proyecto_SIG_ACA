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

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.bumpify.repository.Repository
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.File
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {
    private val FINE_LOCATION_PERMISSIONS_CODE = 3
    private lateinit var map : MapView
    private lateinit var mLocationOverlay: MyLocationNewOverlay
    private lateinit var runnable: Runnable
    private var flag = true
    private lateinit var runnableBlinking: Runnable
    private var colorString: String = "FFFFFF"
    private val types  = arrayOf("Asalto","Bache","Obstáculo","Asesinato","Choque")

    private lateinit var viewModel: MainViewModel
    data class Req(@SerializedName("data") val data: Array<Point>,@SerializedName("dangerLevel") val danger: Double)
    data class Point(@SerializedName("coords") val coor: Array<Double>,@SerializedName("type") val type: Int,@SerializedName("desc") val desc: String,@SerializedName("date") val date: Date,@SerializedName("votesNum") val votes: Int)

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.getItemId()
        if (id == R.id.logout_menu) {
            flag=false
            writeToFile()
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //handle permissions first, before map is created. not depicted here
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You have already granted this permission!",
                Toast.LENGTH_SHORT).show();
        } else {
            requestLocationPermission();
        }

        //load/initialize the osmdroid configuration, this can be done
        // This won't work unless you have imported this: org.osmdroid.config.Configuration.*
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, if you abuse osm's
        //tile servers will get you banned based on this string.

        //inflate and create the map
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.myToolbar))



        map = findViewById<MapView>(R.id.map)
        map.minZoomLevel = 16.0
        map.maxZoomLevel = 18.0
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(applicationContext), map)
        mLocationOverlay.enableMyLocation()
        mLocationOverlay.enableFollowLocation()
        map.overlays.add(mLocationOverlay)


        map.zoomToBoundingBox(mLocationOverlay.bounds, false)

        map.invalidate()

        var intent = intent
        if(intent.hasExtra("mensaje")){
            var mensaje = intent.getStringExtra("mensaje")
            val contexto = findViewById<View>(R.id.mainactivitycontainer)
            val snack = Snackbar.make(contexto, mensaje.toString(), Snackbar.LENGTH_INDEFINITE);
            snack.setAction("Aceptar", View.OnClickListener { snack.dismiss() })
            snack.show()
        }

    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Permisos de ubicación necesario")
                .setMessage("Usaremos tu ubicación para poder ubicar correctamente los incidentes que reportes")
                .setPositiveButton("ok",
                    DialogInterface.OnClickListener { dialog, which ->
                        ActivityCompat.requestPermissions(
                            this@MainActivity, arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ), FINE_LOCATION_PERMISSIONS_CODE
                        )
                    })
                .setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .create().show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                FINE_LOCATION_PERMISSIONS_CODE
            )
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == FINE_LOCATION_PERMISSIONS_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show()
            } else {
                //Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun quemar() = runBlocking { // this: CoroutineScope
        launch { // launch a new coroutine and continue
            delay(5000L)
        }

    }

    fun comprobar(){

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        var mHandler = Handler()
        var mHandlerBlinking = Handler()

        var alphaVar = 0
        var incr = true
        val view: View = this.findViewById<View>(R.id.map).rootView
        var r = ""
        var g = ""
        var b = ""

        var cal = Calendar.getInstance()
        var dateformat = SimpleDateFormat("dd/MM/yyyy\nhh:mm a")
        runnableBlinking = Runnable {
            if(flag){
                r = ""+colorString[0]+colorString[1]
                g = ""+colorString[2]+colorString[3]
                b = ""+colorString[4]+colorString[5]
                view.setBackgroundColor(Color.rgb(r.toInt(16)+alphaVar,g.toInt(16)+alphaVar,b.toInt(16)+alphaVar))
                if (incr) {
                    alphaVar += 5
                    if (alphaVar >= 30) incr=false
                }else {
                    alphaVar -= 5
                    if (alphaVar <= 0) incr=true
                }

                mHandlerBlinking.postDelayed(runnableBlinking,100L)
            }
        }
        mHandlerBlinking.postDelayed(runnableBlinking,100L)

        runnable = Runnable {
            if(flag){
                viewModel.getPost("{\"lat\": "+mLocationOverlay.myLocation.latitude+", \"lon\": "+mLocationOverlay.myLocation.longitude+"}")
                mHandler.postDelayed(runnable,5000L)
            }
        }
        mLocationOverlay.runOnFirstFix(runnable)

        val reportbtn: View = findViewById(R.id.report)

        viewModel.myResponse.observe(this, { response ->

            val points: Req = Gson().fromJson(response.points, Req::class.java)

            val textView: TextView = findViewById(R.id.tv_danger)

            if(points.danger <= 2.00){
                textView.text = "SEGURO"
                textView.setTextColor(Color.parseColor("#0068c9"))
                colorString="0068c9"
            }else if(points.danger > 2.00 && points.danger <= 5.00){
                textView.text = "MAYORMENTE SEGURO"
                textView.setTextColor(Color.parseColor("#00a336"))
                colorString="00a336"
            }else if(points.danger > 5.00 && points.danger <= 8.00){
                textView.text = "MODERADO"
                textView.setTextColor(Color.parseColor("#e6da00"))
                colorString="e6da00"
            }else if(points.danger > 8.00 && points.danger <= 11.00){
                textView.text = "POCO SEGURO"
                textView.setTextColor(Color.parseColor("#e06c00"))
                colorString="e06c00"
            }else if(points.danger > 11.00 && points.danger <= 16.00){
                textView.text = "INSEGURO"
                textView.setTextColor(Color.parseColor("#c40700"))
                colorString="c40700"
            }else if(points.danger > 16.00){
                textView.text = "BUZO QUE SE MUERE"
                textView.setTextColor(Color.parseColor("#400909"))
                colorString="400909"
            }


            map.overlays.forEach {
                if (it is Marker && it.id == "Marker") {
                    map.overlays.remove(it)
                }
            }
            for (p in points.data) {
                var marker = Marker(map)
                marker.position = GeoPoint(p.coor[1], p.coor[0])

                when(p.type){
                    1 -> marker.icon = ContextCompat.getDrawable(this, R.mipmap.ladron_marker)
                    2 -> marker.icon = ContextCompat.getDrawable(this, R.mipmap.bache_marker)
                    3 -> marker.icon = ContextCompat.getDrawable(this, R.mipmap.obstaculo_marker)
                    4 -> marker.icon = ContextCompat.getDrawable(this, R.mipmap.asesinato_marker)
                    5 -> marker.icon = ContextCompat.getDrawable(this, R.mipmap.choque_marker)
                }

                cal.time = p.date
                cal.add(Calendar.HOUR,-6)

                marker.title = " - "+types[p.type-1]+" - \n"+ (if(p.desc.isNotEmpty()) p.desc else "Sin descripción")+"\n \n"+ dateformat.format(cal.time)+"\n \nVotos: "+ p.votes
                marker.id = "Marker"
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                map.overlays.add(marker)
            }

            if (!reportbtn.isEnabled){
                reportbtn.isEnabled = true
            }

        })
    }

    override fun onResume() {
        super.onResume()

        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause()  //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onStart() {
        super.onStart()

        val locationbtn: View = findViewById(R.id.mylocation)
        val reportbtn: View = findViewById(R.id.report)
        reportbtn.isEnabled = false

        flag=true
        comprobar()
        locationbtn.setOnClickListener {
            mLocationOverlay.enableFollowLocation()
            requestLocationPermission()
        }

        map.invalidate()
    }

    override fun onDestroy() {
        super.onDestroy()

        flag = false
    }

    /*override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }*/

    fun abrirReporte(v: View){

        val intent = Intent(this, OptionsActivity::class.java)
        intent.putExtra("latitude", mLocationOverlay.myLocation.latitude)
        intent.putExtra("longitude", mLocationOverlay.myLocation.longitude)
        startActivity(intent)
    }

    //Función para escribir a un archivo
    fun writeToFile() {
        val dir = File(filesDir, "mydir")

        if (!dir.exists()) {
            dir.mkdir()
        }

        try {
            Log.d("TAG", dir.toString())
            val gpxfileold = File(dir, "sesion.txt")
            gpxfileold.delete()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /*private fun requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            permissionsToRequest.add(permission);
        }
    }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }*/
}
