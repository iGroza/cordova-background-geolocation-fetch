package loc

import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.transistorsoft.locationmanager.location.SingleLocationRequest
import com.transistorsoft.locationmanager.location.SingleLocationRequest.getPendingIntent
import android.app.PendingIntent

import android.content.Intent





class LocationUpdatesManager(val context: Context) {
    private var locationManager: LocationManager
    private var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest

    private val UPDATE_INTERVAL: Long = 10000 // Every 5 seconds.

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value, but they may be less frequent.
     */
            private val FASTEST_UPDATE_INTERVAL: Long = 5000 // Every 30 seconds

    /**
     * The max time before batched results are delivered by location services. Results may be
     * delivered sooner than this interval.
     */
    private val MAX_WAIT_TIME = UPDATE_INTERVAL * 5 // Every 5 minutes.

    init {
        this.locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        this.locationRequest = createLocatonRequest()
    }

    private fun createLocatonRequest(): LocationRequest {
        val locationRequest = LocationRequest()
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setMaxWaitTime(MAX_WAIT_TIME);
        return locationRequest
    }

    fun requestLocatonPermissions(permissionCallback: (status: String) -> Unit) {

    }

    fun startTracking() {
        try {
            Log.i(TAG, "Starting location updates")
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent())
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL, 1, getPendingIntent())
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_INTERVAL, 1, getPendingIntent())
        } catch (e: java.lang.SecurityException) {
            e.printStackTrace()
        }
    }

    fun stopTracking() {
        Log.i(TAG, "Removing location updates");
        fusedLocationProviderClient.removeLocationUpdates(getPendingIntent());
        locationManager.removeUpdates(getPendingIntent());
    }

    private fun getPendingIntent(): PendingIntent? {
        // Note: for apps targeting API level 25 ("Nougat") or lower, either
        // PendingIntent.getService() or PendingIntent.getBroadcast() may be used when requesting
        // location updates. For apps targeting API level O, only
        // PendingIntent.getBroadcast() should be used. This is due to the limits placed on services
        // started in the background in "O".

        val intent: Intent? = Intent(this, LocationUpdatesBroadcastReceiver::class.java)
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES)
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}