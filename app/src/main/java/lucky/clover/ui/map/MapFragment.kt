package lucky.clover.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
    }

    private var mPermissionGranted: Boolean = false
    private lateinit var mMapView: MapView
    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "Map: instantiating...")
        val root = inflater.inflate(R.layout.fragment_map, container, false)

        mMapView = root.findViewById(R.id.text_map)
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        getLocationPermission()

        if (mPermissionGranted) {
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
            mPermissionGranted = true
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
        Log.d(TAG, "Map: got permission $requestCode")
        if (requestCode == LOCATION_PERMISSION_RESULT_CODE) {
            mPermissionGranted = false
            if (grantResults.isNotEmpty()) {
                for (grantResult in grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mPermissionGranted = false
                        Log.d(TAG, "Map: revoked")
                        return
                    }
                }

                Log.d(TAG, "Map: granted")
                mPermissionGranted = true
                initMap()
            }
        }
    }

    override fun onResume() {
        super.onResume();
        mMapView.onResume();
    }

    override fun onPause() {
        super.onPause();
        mMapView.onPause();
    }

    override fun onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    override fun onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Toast.makeText(this.requireContext(), "Map is ready", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Map: ready")
        mMap = googleMap

        // For showing a move to my location button
        mMap.isMyLocationEnabled = true

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
}
