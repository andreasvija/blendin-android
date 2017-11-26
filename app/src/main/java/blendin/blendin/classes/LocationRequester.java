/*
* Class for verifying location setting and getting the user's location
*/

package blendin.blendin.classes;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class LocationRequester {

    public static void requestLocation(final LocationReceiver receiver) {

        final Activity contextActivity = (Activity) receiver;

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));
        SettingsClient client = LocationServices.getSettingsClient(contextActivity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(contextActivity, new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressWarnings("MissingPermission")
            // location settings have already been confirmed if the OnSuccessListener is notified
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                Log.d("###", "Location settings are correct");
                FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(contextActivity);
                // get last recorded location
                locationClient.getLastLocation().addOnSuccessListener(contextActivity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                            Log.d("###", "Location is " + location.toString());
                            receiver.receiveLocation(location);
                        }
                        else { // no previous location is stored (or something else went wrong)
                            Log.d("###", "No previous location is stored or something else happened.");

                            // request for a new location

                            LocationRequest locationRequest = new LocationRequest();
                            final FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(contextActivity);

                            final LocationCallback locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    for (Location location : locationResult.getLocations()) {
                                        if (location != null) {
                                            client.removeLocationUpdates(this);
                                            Log.d("###", "Requested location and received: " + location.toString());
                                            receiver.receiveLocation(location);
                                        }
                                        else {
                                            Log.d("###", "Requested location which turned out to be null");
                                        }
                                    }
                                }
                            };

                            client.requestLocationUpdates(locationRequest, locationCallback, null);
                        }
                    }
                });
            }
        });

        task.addOnFailureListener(contextActivity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                int statusCode = ((ApiException) e).getStatusCode();

                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(contextActivity, 100);

                        } catch (IntentSender.SendIntentException sendEx) {
                            Log.d("###", "Failed to show dialog");
                            sendEx.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied, and this can not be fixed
                }
            }
        });
    }
}
