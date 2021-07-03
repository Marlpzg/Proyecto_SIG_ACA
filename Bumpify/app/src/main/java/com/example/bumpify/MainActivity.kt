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

import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.bumpify.repository.Repository
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.TileStates
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.*


class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map : MapView
    private lateinit var mLocationOverlay: MyLocationNewOverlay
    private lateinit var runnable: Runnable

    private lateinit var viewModel: MainViewModel
    data class Req(@SerializedName("data") val data: Array<Point>)
    data class Point(@SerializedName("coords") val coor: Array<Double>,@SerializedName("type") val type: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //handle permissions first, before map is created. not depicted here

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

        runnable = Runnable {
            viewModel.getPost("{\"lat\": "+mLocationOverlay.myLocation.latitude+", \"lon\": "+mLocationOverlay.myLocation.longitude+"}")
            mHandler.postDelayed(runnable,5000L)
        }
        mLocationOverlay.runOnFirstFix(runnable)

        viewModel.myResponse.observe(this, { response ->

            val points: Req = Gson().fromJson(response.points, Req::class.java)
            map.overlays.forEach {
                if (it is Marker && it.id == "Marker") {
                    map.overlays.remove(it)
                }
            }
            for (p in points.data) {
                var marker = Marker(map)
                marker.position = GeoPoint(p.coor[1], p.coor[0])
                //marker.icon = ContextCompat.getDrawable(this, R.drawable.marker_icon)
                marker.title = p.type.toString()
                marker.id = "Marker"
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                map.overlays.add(marker)
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

        comprobar()

        val locationbtn: View = findViewById(R.id.mylocation)

        locationbtn.setOnClickListener {
            mLocationOverlay.enableFollowLocation()
        }

        map.invalidate()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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
