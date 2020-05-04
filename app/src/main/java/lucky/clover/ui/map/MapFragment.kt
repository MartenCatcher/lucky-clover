package lucky.clover.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import lucky.clover.R

class MapFragment : Fragment(), OnMapReadyCallback {
    companion object {
        private const val TAG: String = "MapFragment"
        private const val LOCATION_PERMISSION_RESULT_CODE = 8001
        private const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        private const val ZOOM = 15f
    }

    private var mLocationPermissionGranted: Boolean = false
    private lateinit var mMapView: MapView
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: instantiating...")
        val root = inflater.inflate(R.layout.fragment_map, container, false)

        mMapView = root.findViewById(R.id.text_map)
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume()

        getLocationPermission()

        if (mLocationPermissionGranted) {
            initMap()
        }

        return root
    }

    private fun initMap() {
        MapsInitializer.initialize(requireActivity().applicationContext)
        mMapView.getMapAsync(this)
    }

    private fun isPermissionGranted(permission: String): Boolean {
        val appContext = this.requireContext().applicationContext
        return ContextCompat.checkSelfPermission(
            appContext,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLocationPermission() {
        if (isPermissionGranted(FINE_LOCATION) && isPermissionGranted(COARSE_LOCATION)) {
            mLocationPermissionGranted = true
        } else {
            Log.d(TAG, "Map: permissions called")
            requestPermissions(
                arrayOf(FINE_LOCATION, COARSE_LOCATION),
                LOCATION_PERMISSION_RESULT_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_RESULT_CODE) {
            mLocationPermissionGranted = false
            if (grantResults.isNotEmpty()) {
                for (grantResult in grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = false
                        Log.d(TAG, "onRequestPermissionsResult: permissions revoked")
                        return
                    }
                }

                Log.d(TAG, "onRequestPermissionsResult: permissions granted")
                mLocationPermissionGranted = true
                initMap()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Toast.makeText(this.requireContext(), "Map is ready", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Map: ready")
        mMap = googleMap

        mMap.isMyLocationEnabled = true

        if (mLocationPermissionGranted) {
            getDeviceLocation()
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = false
        }
        
        // For dropping a marker at a point on the Map
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(
            MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description")
        )

        // For zooming automatically to the location of the marker
        val cameraPosition =
            CameraPosition.Builder().target(sydney).zoom(12f).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting location...")

        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this.requireContext())

        try {
            if (mLocationPermissionGranted) {
                val lastLocation = mFusedLocationProviderClient.lastLocation
                lastLocation.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "getDeviceLocation: current location is set")
                        val location: Location? = task.result
                        location?.let { it -> LatLng(it.latitude, it.longitude) }?.let { it -> moveCamera(it) }
                    } else {
                        Log.d(TAG, "getDeviceLocation: current location is unknown")
                        Toast.makeText(
                            this.requireContext(),
                            "Unable to get current location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Log.d(TAG, "getDeviceLocation: permissions aren't granted")
            }
        } catch (e: SecurityException) {
            Log.d(TAG, "getDeviceLocation: SecurityException $e")
        }
    }

    private fun moveCamera(latLng: LatLng) {
        Log.d(TAG, "moveCamera: coordinates $latLng")
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM))
    }
}
