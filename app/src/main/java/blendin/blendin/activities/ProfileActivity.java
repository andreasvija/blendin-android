package blendin.blendin.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import blendin.blendin.R;

public class ProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String userID = getIntent().getExtras().getString("userID");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference authorReference = database.getReference("users").child(userID);

        // Get all of the user's data
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
                else if (dataSnapshot.getKey().equals("languages")) {
                    ArrayList<String> languages = (ArrayList<String>) dataSnapshot.getValue();
                    String text = "";
                    for (int i = 0; i < languages.size(); i++) {
                        text += languages.get(i);
                        if (i+1 < languages.size()) {
                            text += ", ";
                        }
                    }
                    ((TextView) findViewById(R.id.language_field)).setText(text);
                }
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        };

        authorReference.addChildEventListener(userListener);

        // If user is viewing their own profile, hide message button and activate logout button,
        // otherwise hide logout button
        if (userID.equals(Profile.getCurrentProfile().getId())) {

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
