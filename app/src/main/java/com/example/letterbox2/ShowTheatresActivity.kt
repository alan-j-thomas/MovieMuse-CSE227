package com.example.letterbox2

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ShowTheatresActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var googleMap: GoogleMap
    lateinit var fusedLocation: FusedLocationProviderClient
    var location: String = ""

    var latitude: Double = 0.0
    var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_theatres)


        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }
    override fun onMapReady(p0: GoogleMap) {
        p0.clear()
        location = intent.getStringExtra("location").toString()
        getLocationFromUserInput(location)
        googleMap = p0
        val LatLng= LatLng(latitude,longitude)

        p0.addMarker(MarkerOptions().position(LatLng).title("Marker Location"))
        val cameraUpdate= CameraUpdateFactory.newLatLngZoom(LatLng,9f)
        p0.moveCamera(cameraUpdate)

    }
    fun getLocationFromUserInput(location: String) {
        val geocoder = Geocoder(this)
        val list: MutableList<Address>? = geocoder.getFromLocationName(location, 5)
        if (list.isNullOrEmpty()) {
            return
        }
        latitude = list[0].latitude
        longitude = list[0].longitude
    }
}