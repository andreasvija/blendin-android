/*
* Activity for displaying a post and its comments.
*/

package blendin.blendin.activities;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import blendin.blendin.R;
import blendin.blendin.classes.Comment;
import blendin.blendin.classes.Post;
import blendin.blendin.classes.CommentAdapter;

public class PostActivity extends Activity {

    private Post post; // The post being viewed
    private ArrayList<Comment> comments; // Comments under the post

    private RecyclerView recyclerView;
    private RecyclerView.Adapter commentAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //post = (Post) getIntent().getSerializableExtra("post");
            post = (Post) extras.get("post");
            //comments = post.comments;

            /*Picasso.with(this).load(post.author.photoURL)
                    //.resize(width,height).noFade()
                    .into((ImageView) findViewById(R.id.author_photo));
            ((TextView) findViewById(R.id.author_name)).setText(post.author.name);*/
            ((TextView) findViewById(R.id.title)).setText(post.title);
            ((TextView) findViewById(R.id.content)).setText(post.content);
            //findViewById(R.id.).answersView.setText(String.valueOf(post.getCommentCount()) + " " + "answers");
            CharSequence ago = DateUtils.getRelativeTimeSpanString(post.timestamp,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            ((TextView) findViewById(R.id.timestamp)).setText(ago);

            Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> list = geoCoder.getFromLocation(post.latitude, post.longitude, 1);
                if (list != null & list.size() > 0) {
                    String location = list.get(0).getLocality();
                    ((TextView) findViewById(R.id.location)).setText(location);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Set up the RecyclerView of the posts
            recyclerView = (RecyclerView) findViewById(R.id.comments_view);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            // Make it use a custom Adapter
            commentAdapter = new CommentAdapter(comments);
            recyclerView.setAdapter(commentAdapter);
        }
    }

}
