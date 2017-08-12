/*
* Custom Adapter for the RecyclerLayout displaying the posts.
*/

package blendin.blendin.classes;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import blendin.blendin.R;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    ArrayList<Post> posts;

    public PostAdapter(ArrayList<Post> postList) {
        posts = postList;
    }

    // Holds all necessary views
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);
        }
    }

    //Initialize a new post view - called when RecyclerView starts
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // Set up one post's template to be updated later
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_post, parent, false);

        updateViewContent();

        return new PostAdapter.ViewHolder(view);
    }

    // Fill existing view with new data - to save resources,
    // RecyclerView only keeps a few fragment views
    // and when the view is scrolled, changes the content of existing views
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        updateViewContent();
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Updates the data in template or recycled view
    private void updateViewContent() {
        //TODO: modify existing view here
    }
}