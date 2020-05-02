package lucky.clover.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import lucky.clover.R


class MapFragment : Fragment() {

    private lateinit var mMapView: MapView
    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        mMapView = root.findViewById(R.id.text_map)
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(activity!!.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mMapView.getMapAsync { googleMap ->
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

        return root
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
}
