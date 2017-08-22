/*
* Custom Adapter for RecyclerLayout displaying comments in PostActivity.
*/

package blendin.blendin.classes;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import blendin.blendin.R;
import blendin.blendin.activities.CategoriesActivity;
import blendin.blendin.activities.PostActivity;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    ArrayList<Comment> comments;

    public CommentAdapter(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public ImageView photoView;
        public TextView nameView;
        public TextView contentView;
        public TextView timeAgoView;
        public TextView locationView;

        public final Context context;

        // Holds all necessary views
        public ViewHolder(View view) {
            super(view);

            this.view = view;
            photoView = (ImageView) view.findViewById(R.id.author_photo);
            nameView = (TextView) view.findViewById(R.id.author_name);
            contentView = (TextView) view.findViewById(R.id.content);
            timeAgoView = (TextView) view.findViewById(R.id.timestamp);
            locationView = (TextView) view.findViewById(R.id.location);
            context = view.getContext();
        }
    }

    //Initialize a new post view - called when RecyclerView starts
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        // Set up one comment's template to be updated later
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_comment, parent, false);

        return new CommentAdapter.ViewHolder(view);
    }

    // Fill newly initialized or already existing view with new data
    // (to save resources, RecyclerView only keeps a few fragment views
    // and when the view is scrolled, changes the content of existing views)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Comment comment = comments.get(position);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference authorReference = database.getReference("users").child(comment.authorID);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Log.d("###","onChildAdded for " + comment.content);
                if (dataSnapshot.getKey().equals("name")) {
                    String authorName = (String) dataSnapshot.getValue();
                    holder.nameView.setText(authorName);
                }
                else if (dataSnapshot.getKey().equals("photoURL")){
                    String authorPhotoURL = (String) dataSnapshot.getValue();
                    Picasso.with(holder.context)
                            .load(authorPhotoURL)
                            //.resize(width,height).noFade()
                            .into(holder.photoView);
                }
            }
            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override public void onCancelled(DatabaseError databaseError) {}
        };

        authorReference.addChildEventListener(listener);

        holder.contentView.setText(comment.content);
        CharSequence ago = DateUtils.getRelativeTimeSpanString(comment.timestamp,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        holder.timeAgoView.setText(ago);

        Geocoder geoCoder = new Geocoder(holder.context, Locale.getDefault());
        try {
            List<Address> list = geoCoder.getFromLocation(comment.latitude, comment.longitude, 1);
            if (list != null) {
                if (list.size() > 0) {
                    String location = list.get(0).getLocality();
                    holder.locationView.setText(location);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.context, PostActivity.class);
                intent.putExtra("post", post);
                holder.context.startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

}
