/*
* Activity for displaying a post and its comments.
*/

package blendin.blendin.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import blendin.blendin.R;
import blendin.blendin.classes.Comment;
import blendin.blendin.classes.Post;
import blendin.blendin.classes.CommentAdapter;
import blendin.blendin.classes.User;

public class PostActivity extends Activity implements View.OnClickListener {

    private Post post; // The post being viewed
    private ArrayList<Comment> comments; // Comments under the post

    private RecyclerView recyclerView;
    private RecyclerView.Adapter commentAdapter;
    private RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference postCommentsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //post = (Post) getIntent().getSerializableExtra("post");
            post = (Post) extras.get("post");
            //comments = post.comments;

            database = FirebaseDatabase.getInstance();
            DatabaseReference authorReference = database.getReference("users").child(post.authorID);

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

            ((TextView) findViewById(R.id.title)).setText(post.title);
            ((TextView) findViewById(R.id.content)).setText(post.content);

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
            comments = new ArrayList<>();
            commentAdapter = new CommentAdapter(comments);
            recyclerView.setAdapter(commentAdapter);

            postCommentsReference = database.getReference("comments").child(post.id);

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

            findViewById(R.id.author_name).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.putExtra("userID", post.authorID);
                    startActivity(intent);
                }
            });

            findViewById(R.id.translate_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    //String languageCode;
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this, R.style.MyDialogTheme);
                    // The Android Dialog is missing theme resources if the app theme is not an Appcompat one
                    builder.setTitle("Choose language to translate into")
                            .setPositiveButton("Translate",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            TextView titleView = (TextView) ((ViewGroup) v.getParent().getParent().getParent()).getChildAt(1);
                                            TextView contentView = (TextView) ((ViewGroup) v.getParent().getParent().getParent().getParent().getParent()).getChildAt(1);
                                            ListView listView = ((AlertDialog)dialog).getListView();
                                            int position = listView.getCheckedItemPosition();
                                            String languageName = (String) listView.getAdapter().getItem(position);
                                            ArrayList<String> names = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.language_names_array)));
                                            ArrayList<String> codes = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.language_codes_array)));
                                            String languageCode = codes.get(names.indexOf(languageName));
                                            titleView.setText(languageCode);
                                            contentView.setText(languageCode);
                                        }
                            })
                            .setNegativeButton("cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                            })
                            .setSingleChoiceItems(R.array.language_names_array,
                                    0,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String[] codes = getResources().getStringArray(R.array.language_codes_array);
                                            //languageCode = codes[which];
                                            Log.d("###", "Chosen: " + codes[which]);
                                        }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

        findViewById(R.id.sendCommentButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        Profile profile = Profile.getCurrentProfile();
        String authorID = profile.getId();

        EditText contentBox = (EditText) findViewById(R.id.commentText);
        String content = contentBox.getText().toString();
        contentBox.setText("");

        Comment comment = new Comment(authorID, content);
        DatabaseReference commentReference = postCommentsReference.push();
        comment.id = commentReference.getKey();
        commentReference.setValue(comment);

        incrementCommentCount();
    }

    void incrementCommentCount() {
        final DatabaseReference postReference = database.getReference("posts").child(post.category).child(post.id);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Log.d("###", "onChildAdded");

                if (dataSnapshot.getKey().equals("commentCount")) {

                    //Log.d("###", "Key is " + dataSnapshot.getKey());
                    if (dataSnapshot.getValue() == null) {
                        //Log.d("###", "Value is null");
                        //commentCountReference.setValue("pls y");
                    } else {
                        long currentCount = (long) dataSnapshot.getValue();
                        postReference.child("commentCount").setValue(currentCount + 1);
                        //Log.d("###", "Value should now be " + String.valueOf(currentCount+1));
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
