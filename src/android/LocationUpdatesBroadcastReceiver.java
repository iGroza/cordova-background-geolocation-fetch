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

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ru.nsstms.wetwars.location.Utils.getDeviceInfo;
import static ru.nsstms.wetwars.location.Utils.locationToJson;

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
public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "BgBroadcastReceiver";

    public static final String ACTION_PROCESS_UPDATES = "ru.nsstms.wetwars.location.action.PROCESS_UPDATES";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 142;
    private static final long UPDATE_INTERVAL = 10000; // Every 5 seconds.
    private static final long FASTEST_UPDATE_INTERVAL = 5000; // Every 30 seconds
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 5; // Every 5 minutes.
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive geolocation" + intent.toString());
        if (intent != null) {
            final String action = intent.getAction();
            if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
                createLocationRequest();
                Log.i(TAG, "Boot : registered for location updates");
                LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                Intent i = new Intent(context, this.getClass());
                PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, pi);
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, pi);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, pi);
            } else {
                Log.d(TAG, "action: " + action);
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    try {
                        List<Location> locations = result.getLocations();
                        Utils.setLocationUpdatesResult(context, locations);
                        // Utils.sendNotification(context, Utils.getLocationResultTitle(context, locations));
                        Location lastLocation = locations.get(locations.size() - 1);
                        String uuid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                        JSONObject data = new JSONObject()
                                .put("uuid", uuid)
                                .put("location", locationToJson(lastLocation))
                                .put("device", getDeviceInfo(context));
                        new HttpRequest().execute(data, new JSONObject().put("url", context.getString(R.string.server_url)));
                        Log.i(TAG, Utils.getLocationUpdatesResult(context));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Bundle bundle = intent.getExtras();
                    if (bundle.containsKey("location")) {
                        try {
                            Location location = (Location) bundle.get("location");
                            List<Location> locations = new ArrayList<Location>();
                            locations.add(location);
                            Utils.setLocationUpdatesResult(context, locations);
                            // Utils.sendNotification(context, Utils.getLocationResultTitle(context, locations));
                            Location lastLocation = locations.get(locations.size() - 1);
                            String uuid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                            JSONObject data = new JSONObject()
                                    .put("uuid", uuid)
                                    .put("location", locationToJson(lastLocation))
                                    .put("device", getDeviceInfo(context));
                            new HttpRequest().execute(data, new JSONObject().put("url", context.getString(R.string.server_url)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(TAG, bundle.toString());
                    }
                }
            }
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);
    }

    public static class HttpRequest extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... data) {
            Log.d(TAG, "request data: " + data[0].toString());
            HttpUrl.Builder urlBuilder = null;
            try {
                urlBuilder = HttpUrl.parse(data[1].getString("url")).newBuilder();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = urlBuilder.build().toString();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, data[0].toString());

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e("HttpService", "onFailure() Request was: " + request);
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response r) throws IOException {
                    Log.e("response ", "onResponse(): " + r.body().string());
                }
            });
            return null;
        }
    }
}
