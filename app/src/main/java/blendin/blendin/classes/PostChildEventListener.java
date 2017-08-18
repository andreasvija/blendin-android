package blendin.blendin.classes;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import blendin.blendin.activities.CategoriesActivity;

public class PostChildEventListener implements ChildEventListener {

    //public String category;

    public PostChildEventListener(/*String category*/) {
        //this.category = category;
    }

    // Triggered for every child upon listener instantiation and for every added child
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Post post = dataSnapshot.getValue(Post.class);
        CategoriesActivity.selectedPosts.add(post);
        CategoriesActivity.postAdapter.notifyDataSetChanged();
    }
    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
    @Override public void onChildRemoved(DataSnapshot dataSnapshot) {}
    @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
    @Override public void onCancelled(DatabaseError databaseError) {}
}
