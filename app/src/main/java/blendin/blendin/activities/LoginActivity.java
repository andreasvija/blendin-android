/*
* Activity for authenticating user. Is the launching activity.
*/

package blendin.blendin.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import blendin.blendin.R;
import blendin.blendin.classes.User;

public class LoginActivity extends Activity {

    private CallbackManager callbackManager;

    private FirebaseAuth firebaseAuth;
    private static DatabaseReference userReference;

    private static String location;
    private static String hometown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // If user is already logged in
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            enterApp();
        }
        else {
            // Set up login button for logging in
            LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions("public_profile", "user_location");
            //id, name, profile pic; location; backup location; app language

            callbackManager = CallbackManager.Factory.create();

            // Callback registration
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    connectLoginToFirebase(loginResult.getAccessToken());
                }
                @Override
                public void onCancel() {}
                @Override
                public void onError(FacebookException exception) {}
            });
        }
    }

    // Take the result and make the CallbackManager handle it
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // Connect Facebook login with Firebase auth system
    void connectLoginToFirebase(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        Task<AuthResult> task = firebaseAuth.signInWithCredential(credential);

        OnCompleteListener listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    checkForPreviousData();
                }
            }
        };

        task.addOnCompleteListener(this, listener);
    }

    // Check if there is already data for user's languages
    void checkForPreviousData() {
        Profile profile = Profile.getCurrentProfile();
        String id = profile.getId();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userReference = database.getReference("users").child(id).child("languages");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    askForLanguages();
                }
                else {
                    Log.d("###", dataSnapshot.getValue().toString());
                    enterApp();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        userReference.addListenerForSingleValueEvent(valueEventListener);
    }

    // Ask for the languages the user knows
    void askForLanguages() {
        final ArrayList<String> knownLanguages = new ArrayList<>();
        final ArrayList<String> allLanguages = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.language_names_array)));

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        // The Android Dialog is missing theme resources if the app theme is not an Appcompat one
        builder.setTitle("Check all languages you know")
                .setPositiveButton("Select",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (knownLanguages.size() > 0) {
                                    updateUserData(knownLanguages);
                                    dialog.dismiss();
                                }
                                else {
                                    askForLanguages();
                                }
                            }
                        })
                .setMultiChoiceItems(R.array.language_names_array,
                        null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    knownLanguages.add(allLanguages.get(which));
                                }
                                else {
                                    knownLanguages.remove(allLanguages.get(which));
                                }
                            }
                        });
        builder.show();
    }

    void updateUserData(ArrayList<String> languages) {

        Profile profile = Profile.getCurrentProfile();
        String id = profile.getId();
        String name = profile.getName();
        String photoURL = profile.getProfilePictureUri(200,200).toString();
        // If location can not be found, this is shown instead
        String userLocation = "Not available";

        User user = new User(id, name, photoURL, userLocation, languages);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userReference = database.getReference("users").child(id);
        userReference.setValue(user);

        AccessToken token = AccessToken.getCurrentAccessToken();
        String query = "/" + id + "?fields=location,hometown,locale";

        GraphRequest.Callback callback = new GraphRequest.Callback() {

            public void onCompleted(GraphResponse response) {

                JSONObject json = response.getJSONObject();
                try {
                    location = json.getJSONObject("location").getString("name");
                    hometown = json.getJSONObject("hometown").getString("name");
                    String userLocation = "Not available";

                    if (location != null) {
                        if (!location.equals("")) {
                            userLocation = location;
                        }
                    }
                    else if (hometown != null) {
                        if (!hometown.equals("")) {
                            userLocation = hometown;
                        }
                    }
                    userReference.child("location").setValue(userLocation);

                } catch (Exception e) {
                    Log.d("###", e.toString());
                    e.printStackTrace();
                }
                enterApp();
            }
        };
        GraphRequest request = new GraphRequest(token, query, null, HttpMethod.GET, callback);
        request.executeAsync();
    }

    // Start CategoriesActivity and end LoginActivity
    void enterApp () {
        Intent intent = new Intent(getApplicationContext(), CategoriesActivity.class);
        startActivity(intent);
        finish();
    }
}
