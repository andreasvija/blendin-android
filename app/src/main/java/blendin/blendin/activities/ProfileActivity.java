package blendin.blendin.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import blendin.blendin.R;

public class ProfileActivity extends Activity {

    //LoginButton loginButton;
    //CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String userID = getIntent().getExtras().getString("userID");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference authorReference = database.getReference("users").child(userID);

        ChildEventListener userListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("name")) {
                    String authorName = (String) dataSnapshot.getValue();
                    ((TextView) findViewById(R.id.name)).setText(authorName);
                }
                else if (dataSnapshot.getKey().equals("photoURL")) {
                    String authorPhotoURL = (String) dataSnapshot.getValue();
                    Picasso.with(getParent())
                            .load(authorPhotoURL)
                            //.resize(width,height).noFade()
                            .into((ImageView) findViewById(R.id.picture));
                }
                else if (dataSnapshot.getKey().equals("location")) {
                    String location = (String) dataSnapshot.getValue();
                    ((TextView) findViewById(R.id.lives_in_field)).setText(location);
                }
                /*else if (dataSnapshot.getKey().equals("languages")) {
                    List<String> languages = (List<String>) dataSnapshot.getValue();
                    ((TextView) findViewById(R.id.lives_in_field)).setText("");
                }*/
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        };

        authorReference.addChildEventListener(userListener);

        if (false/*userID.equals(Profile.getCurrentProfile().getId())*/) {

            findViewById(R.id.messenger_button).setVisibility(View.INVISIBLE);

            findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginManager.getInstance().logOut();
                    FirebaseAuth.getInstance().signOut();

                    Intent broadcastIntent = new Intent("finish_activity");
                    sendBroadcast(broadcastIntent);
                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
            });
        }
        else {
            findViewById(R.id.logout_button).setVisibility(View.GONE);
            
        }
    }
}
