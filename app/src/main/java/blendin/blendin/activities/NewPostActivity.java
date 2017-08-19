package blendin.blendin.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        Profile profile = Profile.getCurrentProfile();
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
        
    }
}
