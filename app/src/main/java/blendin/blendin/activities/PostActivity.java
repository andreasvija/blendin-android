/*
* Activity for displaying a post and its comments.
*/

package blendin.blendin.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import blendin.blendin.R;
import blendin.blendin.classes.Comment;
import blendin.blendin.classes.LocationReceiver;
import blendin.blendin.classes.LocationRequester;
import blendin.blendin.classes.Post;
import blendin.blendin.classes.CommentAdapter;
import blendin.blendin.classes.TranslateButton;

public class PostActivity extends Activity implements LocationReceiver {

    private Post post; // The post being viewed
    private ArrayList<Comment> comments; // Comments under the post

    private RecyclerView.Adapter commentAdapter;

    private FirebaseDatabase database;
    private DatabaseReference postCommentsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Get data for the post the activity is supposed to show
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            post = (Post) extras.get("post");

            database = FirebaseDatabase.getInstance();
            DatabaseReference authorReference = database.getReference("users").child(post.getAuthorID());

            // Fetch required data about the author
            ChildEventListener userListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getKey().equals("name")) {
                        String authorName = (String) dataSnapshot.getValue();
                        ((TextView) findViewById(R.id.author_name)).setText(authorName);
                    }
                    else if (dataSnapshot.getKey().equals("photoURL")) {
                        String authorPhotoURL = (String) dataSnapshot.getValue();
                        Picasso.with(getParent())
                                .load(authorPhotoURL)
                                //.resize(width,height).noFade()
                                .into((ImageView) findViewById(R.id.author_photo));
                    }
                }
                @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override public void onCancelled(DatabaseError databaseError) {}
            };

            authorReference.addChildEventListener(userListener);

            TextView titleView = ((TextView) findViewById(R.id.title));
            titleView.setText(post.getTitle());
            TextView contentView = ((TextView) findViewById(R.id.content));
            contentView.setText(post.getContent());

            CharSequence ago = DateUtils.getRelativeTimeSpanString(post.getTimestamp(),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            ((TextView) findViewById(R.id.timestamp)).setText(ago);

            // Try to put the location data into words
            Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> list = geoCoder.getFromLocation(post.getLatitude(), post.getLongitude(), 1);

                if (list != null && list.size() > 0) {
                    String location = list.get(0).getLocality();
                    ((TextView) findViewById(R.id.location)).setText(location);
                }
                else {
                    ((TextView) findViewById(R.id.location)).setText("Not available");
                }
            } catch (IOException e) {
                e.printStackTrace();
                ((TextView) findViewById(R.id.location)).setText("Not available");
            }

            // Set up the RecyclerView of the posts
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.comments_view);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            // Make it use a custom Adapter
            comments = new ArrayList<>();
            commentAdapter = new CommentAdapter(comments);
            recyclerView.setAdapter(commentAdapter);

            postCommentsReference = database.getReference("comments").child(post.getId());

            // Add every comment to the ArrayList
            ChildEventListener commentsListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    comments.add(comment);
                    commentAdapter.notifyDataSetChanged();
                }
                @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override public void onCancelled(DatabaseError databaseError) {}

            };
            postCommentsReference.addChildEventListener(commentsListener);

            // Clicking on the author's name sends user to their profile
            findViewById(R.id.author_name).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.putExtra("userID", post.getAuthorID());
                    startActivity(intent);
                }
            });

            // Initialize the custom listener on the translate button
            ((TranslateButton) findViewById(R.id.translate_button)).startListening(this, titleView, contentView);
        }

        final LocationReceiver thisReference = this;

        findViewById(R.id.sendCommentButton).setOnClickListener(new View.OnClickListener() {
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
            createAndUploadComment(location.getLatitude(), location.getLongitude());
        }
        else {
            Log.d("###", "PostActivity received a null location for new comment.");
        }
    }

    void createAndUploadComment(double latitude, double longitude) {
        Profile profile = Profile.getCurrentProfile();
        String authorID = profile.getId();

        EditText contentBox = (EditText) findViewById(R.id.commentText);
        String content = contentBox.getText().toString();
        contentBox.setText("");

        DatabaseReference commentReference = postCommentsReference.push();
        String id = commentReference.getKey();

        Comment comment = new Comment(id, authorID, content, latitude, longitude);
        commentReference.setValue(comment);
        incrementCommentCount();
    }

    void incrementCommentCount() {
        final DatabaseReference postReference = database.getReference("posts").child(post.getCategory()).child(post.getId());

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.getKey().equals("commentCount")) {

                    if (dataSnapshot.getValue() != null) {
                        long currentCount = (long) dataSnapshot.getValue();
                        postReference.child("commentCount").setValue(currentCount + 1);
                    }
                }
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {Log.d("###", "onCancelled");}
        };

        postReference.addChildEventListener(listener);
    }
}
