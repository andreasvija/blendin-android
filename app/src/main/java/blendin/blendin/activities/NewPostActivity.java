package blendin.blendin.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import blendin.blendin.R;
import blendin.blendin.classes.Comment;
import blendin.blendin.classes.Post;
import blendin.blendin.classes.User;

public class NewPostActivity extends Activity implements View.OnClickListener {

    Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        profile = Profile.getCurrentProfile();
        String name = profile.getName();
        String photoURL = profile.getProfilePictureUri(50,50).toString();

        ((TextView) findViewById(R.id.author_name)).setText(name);
        Picasso.with(getParent())
                .load(photoURL)
                //.resize(width,height).noFade()
                .into((ImageView) findViewById(R.id.author_photo));

        findViewById(R.id.sendPostButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String authorID = profile.getId();
        String name = profile.getName();
        String photoURL = profile.getProfilePictureUri(200,200).toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userReference = database.getReference("users").child(authorID);
        userReference.child("id").setValue(authorID);
        userReference.child("name").setValue(name);
        userReference.child("photoURL").setValue(photoURL);

        Spinner categorySpinner = (Spinner) findViewById(R.id.post_category);
        String category = categorySpinner.getSelectedItem().toString();
        EditText titleBox = (EditText) findViewById(R.id.title_box);
        String title = titleBox.getText().toString();
        EditText contentBox = (EditText) findViewById(R.id.content_box);
        String content = contentBox.getText().toString();

        Post post = new Post(authorID, category, title, content);
        DatabaseReference postReference = database.getReference("posts").child(category).push();
        post.id = postReference.getKey();
        postReference.setValue(post);

        finish();
    }
}
