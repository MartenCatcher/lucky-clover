package lucky.clover

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG: String = "MainActivity"
        private const val ERROR_DIALOG_REQUEST = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkMapService()) {
            init()
        }
    }

    private fun init() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_map,
            R.id.navigation_camera,
            R.id.navigation_photos
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun checkMapService(): Boolean {
        Log.d(TAG, "checkMapService: starting...")

        val status: Int = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)

        when {
            status == ConnectionResult.SUCCESS -> {
                Log.d(TAG, "checkMapService: the service is available")
                return true
            }
            GoogleApiAvailability.getInstance().isUserResolvableError(status) -> {
                Log.d(TAG, "checkMapService: trying to resolve the issue $status")
                val dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, status, ERROR_DIALOG_REQUEST)
                dialog.show()
            }
            else -> {
                Toast.makeText(this, "Can't make map request", Toast.LENGTH_SHORT).show()
            }
        }
        return false
    }
}
