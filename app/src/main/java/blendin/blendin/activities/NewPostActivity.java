package blendin.blendin.activities;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import blendin.blendin.R;
import blendin.blendin.classes.LocationReceiver;
import blendin.blendin.classes.LocationRequester;
import blendin.blendin.classes.Post;

public class NewPostActivity extends Activity implements LocationReceiver {

    private Profile profile;

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

        final LocationReceiver thisReference = this;

        findViewById(R.id.sendPostButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationRequester.requestLocation(thisReference);
                // received in receiveLocation()
            }
        });
    }

    @Override
    public void receiveLocation(Location location) {
        if (location != null) {
            createAndUploadPost(location.getLatitude(), location.getLongitude());
        }
        else {
            Log.d("###", "NewPostActivity received a null location for new post.");
        }
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
