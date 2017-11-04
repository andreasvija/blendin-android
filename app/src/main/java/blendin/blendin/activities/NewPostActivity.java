package blendin.blendin.activities;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import blendin.blendin.R;
import blendin.blendin.classes.Post;

public class NewPostActivity extends Activity {

    private Profile profile;
    private FusedLocationProviderClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        profile = Profile.getCurrentProfile();
        String name = profile.getName();
        String photoURL = profile.getProfilePictureUri(500,500).toString();

        ((TextView) findViewById(R.id.author_name)).setText(name);
        Picasso.with(getParent())
                .load(photoURL)
                //.resize(width,height).noFade()
                .into((ImageView) findViewById(R.id.author_photo));

        findViewById(R.id.sendPostButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ascertainCorrectLocationSettings();
            }
        });
    }

    void ascertainCorrectLocationSettings() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                Log.d("###", "onSuccess");
                getLocation();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("###", "onFailure");
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(NewPostActivity.this, 100);
                        } catch (IntentSender.SendIntentException sendEx) {
                            Log.d("###", sendEx.getMessage());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d("###", "Location settings are not satisfied. Settings change unavailable.");
                        //return;
                }
                //ascertainCorrectLocationSettings();
            }
        });
    }

    void getLocation() {

        locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d("###", location.toString());
                    createAndUploadPost(location.getLatitude(), location.getLongitude());
                }
            }
        });
    }

    void createAndUploadPost(double latitude, double longitude) {

        String authorID = profile.getId();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        Spinner categorySpinner = (Spinner) findViewById(R.id.post_category);
        String category = categorySpinner.getSelectedItem().toString();
        EditText titleBox = (EditText) findViewById(R.id.title_box);
        String title = titleBox.getText().toString();
        EditText contentBox = (EditText) findViewById(R.id.content_box);
        String content = contentBox.getText().toString();

        DatabaseReference postReference = database.getReference("posts").child(category).push();
        String id = postReference.getKey();

        Post post = new Post(id, authorID, category, title, content, latitude, longitude);
        postReference.setValue(post);

        finish();
    }
}
