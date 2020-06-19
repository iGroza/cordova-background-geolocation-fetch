/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package loc;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import org.json.JSONObject

/**
 * Receiver for handling location updates.
 * <p>
 * For apps targeting API level O
 * {@link PendingIntent#getBroadcast(Context, int, Intent, int)} should be used when
 * requesting location updates. Due to limits on background services,
 * {@link PendingIntent#getService(Context, int, Intent, int)} should not be used.
 * <p>
 * Note: Apps running on "O" devices (regardless of targetSdkVersion) may receive updates
 * less frequently than the interval specified in the
 * {@link LocationRequest} when the app is no longer in the
 * foreground.
 */
abstract class LocationUpdatesBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "BgBroadcastReceiver";

    companion object {
        val ACTION_PROCESS_UPDATES = "location.action.PROCESS_UPDATES";
    }

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 142;
    private val UPDATE_INTERVAL = 10000; // Every 5 seconds.
    private val FASTEST_UPDATE_INTERVAL = 5000; // Every 30 seconds
    private val MAX_WAIT_TIME = UPDATE_INTERVAL * 5; // Every 5 minutes.
    private lateinit var mLocationRequest: LocationRequest;
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onReceive(context: Context, intent: Intent?) {
        Log.i(TAG, "onReceive geolocation" + intent.toString());

        val action = intent!!.getAction()
        if (ACTION_PROCESS_UPDATES.equals(action)) {
            var jsonLocation: JSONObject?
            val result = LocationResult.extractResult(intent);
            if (result != null) {
                val locations: List<Location> = result.getLocations();
                val lastLocation = locations.get(locations.size - 1);
                jsonLocation = lastLocation.toJson()
            } else {
                val bundle = intent!!.getExtras();
                val location: Location = bundle!!.get("location") as Location
                jsonLocation = location.toJson()

            }
            Log.i(TAG, jsonLocation.toString());
        }
    }
}

fun Location.toJson(): JSONObject {
    return JSONObject()
            .put("latitude", this.getLatitude())
            .put("longitude", this.getLongitude())
            .put("altitude", this.getAltitude())
            .put("timestamp", this.getTime())
            .put("provider", this.getProvider())
            .put("accuracy", this.getAccuracy())
            .put("speed", this.getSpeed());
}

