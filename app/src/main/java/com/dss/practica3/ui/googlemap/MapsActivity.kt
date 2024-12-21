package com.dss.practica3.ui.googlemap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.dss.practica3.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


// FragmentActivity o AppCompatActivity o Fragment()?
//class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient // Google Play Services Location Provider
    private lateinit var locationRequest: LocationRequest
    private var currentLocationMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(com.dss.practica3.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Comprobar permisos de ubicación
//        checkLocationPermission()

//        // Inicializar FusedLocationProviderClient
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//
//        // Configurar las actualizaciones de ubicación
//         LocationRequest.Builder(locationRequest)
//             .setIntervalMillis(5000)
//             .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
//             .setMinUpdateIntervalMillis(2000)
//             .build()

    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        return inflater.inflate(com.dss.practica3.R.layout.activity_main, container, false)
//    }
//
//    fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityMapsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//
//    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Maracena and move the camera
        val maracena = LatLng(37.20845336833197, -3.6315063638540823)
        mMap.addMarker(MarkerOptions().position(maracena).title("Marcador en el Kebab Maracena"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(maracena))

//        // Habilitar el botón de ubicación en el mapa
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return checkLocationPermission()
//        }
//        mMap.isMyLocationEnabled = true
//
//        // Comenzar a recibir actualizaciones de ubicación
//        startLocationUpdates()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startLocationUpdates()
        } else {
            // Maneja el caso donde el permiso es rechazado
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // El permiso ya ha sido otorgado
                startLocationUpdates()
            }
            else -> {
                // Solicitar permiso
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            mainLooper
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            // Obtener la última ubicación
            val location = locationResult.lastLocation
            val currentLatLng = location?.let { LatLng(it.latitude, location.longitude) }

            if (currentLatLng == null) {
                return
            }

            // Actualizar el marcador en el mapa
            if (currentLocationMarker == null) {
                // Agregar un marcador nuevo si no existe
                currentLocationMarker = mMap.addMarker(
                    MarkerOptions().position(currentLatLng).title("Ubicación actual")
                )
            } else {
                // Mover el marcador existente
                currentLocationMarker?.position = currentLatLng
            }

            // Mover la cámara al marcador
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detener las actualizaciones de ubicación al cerrar la actividad
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}