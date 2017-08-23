/*
* Activity for authenticating user. Is the launching activity.
*/

package blendin.blendin.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import blendin.blendin.R;
import blendin.blendin.classes.User;

public class LoginActivity extends Activity {

    LoginButton loginButton;
    CallbackManager callbackManager;

    FirebaseAuth firebaseAuth;
    static DatabaseReference userReference;

    static String location;
    static String hometown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            enterApp();
        }

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "user_location", "user_hometown", "user_locale");
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

    // Take the result and make the CallbackManager handle it
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    void connectLoginToFirebase(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        Task<AuthResult> task = firebaseAuth.signInWithCredential(credential);
        OnCompleteListener listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    enterApp();
                }
            }
        };
        task.addOnCompleteListener(this, listener);
    }

    // Start CategoriesActivity and end LoginActivity
    void enterApp () {
        updateUserData();

        Intent intent = new Intent(getApplicationContext(), CategoriesActivity.class);
        startActivity(intent);
        finish();
    }

    void updateUserData() {
        Profile profile = Profile.getCurrentProfile();
        String id = profile.getId();
        String name = profile.getName();
        String photoURL = profile.getProfilePictureUri(200,200).toString();
        String userLocation = "Not available";

        User user = new User(id, name, photoURL, userLocation, new ArrayList<String>());

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
            }
        };
        GraphRequest request = new GraphRequest(token, query, null, HttpMethod.GET, callback);
        request.executeAsync();
    }
}
