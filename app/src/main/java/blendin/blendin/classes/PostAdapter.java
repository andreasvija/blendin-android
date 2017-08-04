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

    //holds all necessary views
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);
        }
    }

    //Initialize a new post view
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_post, parent, false);
        //Only sets up post template, TODO: modify template in enterPostdata()
        enterPostData();
        return new PostAdapter.ViewHolder(view);
    }

    //Fill recycled view with new data
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        enterPostData();
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private void enterPostData() {

    }
}