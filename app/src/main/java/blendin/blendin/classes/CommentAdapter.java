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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    public CommentAdapter(ArrayList<Comment> commentList) {
        comments = commentList;
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

        Picasso.with(holder.context)
                .load(comment.author.photoURL)
                //.resize(width,height).noFade()
                .into(holder.photoView);
        holder.nameView.setText(comment.author.name);
        holder.contentView.setText(comment.content);
        CharSequence ago = DateUtils.getRelativeTimeSpanString(comment.timestamp,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        holder.timeAgoView.setText(ago);

        Geocoder geoCoder = new Geocoder(holder.context, Locale.getDefault());
        try {
            List<Address> list = geoCoder.getFromLocation(comment.latitude, comment.longitude, 1);
            if (list != null & list.size() > 0) {
                String location = list.get(0).getLocality();
                holder.locationView.setText(location);
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
