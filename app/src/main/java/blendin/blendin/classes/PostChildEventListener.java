/*
* Firebase ChildEventListener to lessen repetition in CategoriesActivity.setChildEventListener()
*/

package blendin.blendin.classes;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import blendin.blendin.activities.CategoriesActivity;

public class PostChildEventListener implements ChildEventListener {

    public PostChildEventListener() {

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
