/*
* Custom Adapter for the RecyclerLayout displaying the posts.
*/

package blendin.blendin.classes;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import blendin.blendin.R;
import blendin.blendin.activities.CategoriesActivity;
import blendin.blendin.activities.PostActivity;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    ArrayList<Post> posts;

    public PostAdapter(ArrayList<Post> postList) {
        posts = postList;
    }

    // Holds all necessary views
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public ImageView photoView;
        public TextView nameView;
        public TextView titleView;
        //public TextView detailsView;
        public TextView answersView;
        public TextView timeAgoView;

        public final Context context;

        public ViewHolder(View view) {
            super(view);

            this.view = view;
            photoView = (ImageView) view.findViewById(R.id.author_photo);
            nameView = (TextView) view.findViewById(R.id.author_name);
            titleView = (TextView) view.findViewById(R.id.title);
            //detailsView = (TextView) view.findViewById(R.id.details);
            answersView = (TextView) view.findViewById(R.id.comment_count);
            timeAgoView = (TextView) view.findViewById(R.id.timestamp);
            context = view.getContext();
        }
    }

    //Initialize a new post view - called when RecyclerView starts
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // Set up one post's template to be updated later
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_post, parent, false);

        return new PostAdapter.ViewHolder(view);
    }

    // Fill newly initialized or already existing view with new data
    // (to save resources, RecyclerView only keeps a few fragment views
    // and when the view is scrolled, changes the content of existing views)
    @Override
    public void onBindViewHolder( final ViewHolder holder, final int position) {

        final Post post = posts.get(position);

        Picasso.with(holder.context)
                .load(post.author.photoURL)
                //.resize(width,height).noFade()
                .into(holder.photoView);
        holder.nameView.setText(post.author.name);
        holder.titleView.setText(post.title);
        holder.answersView.setText(String.valueOf(post.getCommentCount()) + " " + "answers");
        CharSequence ago = DateUtils.getRelativeTimeSpanString(post.timestamp,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        holder.timeAgoView.setText(ago);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.context, PostActivity.class);
                intent.putExtra("post", post);
                holder.context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

}