/*
* Activity for displaying posts by categories. The central screen of the app.
*/

package blendin.blendin.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import blendin.blendin.R;
import blendin.blendin.classes.Comment;
import blendin.blendin.classes.Post;
import blendin.blendin.classes.PostAdapter;
import blendin.blendin.classes.PostChildEventListener;
import blendin.blendin.classes.User;

public class CategoriesActivity extends Activity implements View.OnClickListener {

    int activeCategory; // Number of the currently active category
    //public static ArrayList<Post> allPosts; // All posts in the system
    public static ArrayList<Post> selectedPosts; // Posts of the current selected category

    private RecyclerView recyclerView;
    public static RecyclerView.Adapter postAdapter;
    private RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference postsReference;
    //ChildEventListener childEventListener;
    ArrayList<DatabaseReference> usedReferences;
    ArrayList<ChildEventListener> activeListeners;

    public String[] categories = {"All", "Finance", "Food", "Other", "Shopping", "Transport", "Travel"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        //allPosts = new ArrayList<>();
        //getPosts();

        // Make category labels clickable
        findViewById(R.id.category_all).setOnClickListener(this);
        findViewById(R.id.category_finance).setOnClickListener(this);
        findViewById(R.id.category_food).setOnClickListener(this);
        findViewById(R.id.category_shopping).setOnClickListener(this);
        findViewById(R.id.category_transport).setOnClickListener(this);
        findViewById(R.id.category_travel).setOnClickListener(this);
        findViewById(R.id.category_other).setOnClickListener(this);

        // Set up the RecyclerView of the posts
        recyclerView = (RecyclerView) findViewById(R.id.posts_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Make it use a custom Adapter
        //postAdapter = new PostAdapter(selectedPosts);
        //recyclerView.setAdapter(postAdapter);

        database = FirebaseDatabase.getInstance();
        postsReference = database.getReference("posts");
        activeListeners = new ArrayList<>();
        usedReferences = new ArrayList<>();
        // "All" is the default category
        activeCategory = R.id.category_all;
        switchActiveCategory(activeCategory);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add) {
            Intent intent = new Intent(this, NewPostActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_profile) {

            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals("finish_activity")) {
                        finish();
                    }
                }
            };
            registerReceiver(broadcastReceiver, new IntentFilter("finish_activity"));

            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // On category click switch to that category
    @Override
    public void onClick(View view) {
        int id = view.getId();
        //String s = getResources().getResourceEntryName(id);
        //Log.d("###", "onClick: " + s);
        switchActiveCategory(id);
    }

    // Changes highlighted category and the data used in RecyclerView
    void switchActiveCategory(int newActive) {
        // Set the old active category's background to normal
        LinearLayout ll = (LinearLayout) findViewById(activeCategory);
        ll.setBackground(getResources().getDrawable(R.drawable.back_inactive));

        // Set the new active category's background to highlighted
        activeCategory = newActive;
        ll = (LinearLayout) findViewById(activeCategory);
        ll.setBackground(getResources().getDrawable(R.drawable.back_active));

        //String category = (String) findViewById(activeCategory).getTag();
        selectedPosts = new ArrayList<>();

        String category = (String) findViewById(activeCategory).getTag();
        setChildEventListener(category);

        postAdapter = new PostAdapter(selectedPosts);
        recyclerView.setAdapter(postAdapter);
    }

    public void setChildEventListener(String category) {

        for (int i = 0; i < usedReferences.size(); i++) {
            usedReferences.get(i).removeEventListener(activeListeners.get(i));
        }

        activeListeners = new ArrayList<>();
        usedReferences = new ArrayList<>();

        if (category.equals("All")) {
            for (String c : categories) {
                DatabaseReference ref = postsReference.child(c);
                ChildEventListener lis = new PostChildEventListener();
                ref.addChildEventListener(lis);
                usedReferences.add(ref);
                activeListeners.add(lis);
            }
        }
        else {
            DatabaseReference ref = postsReference.child(category);
            ChildEventListener lis = new PostChildEventListener();
            ref.addChildEventListener(lis);
            usedReferences.add(ref);
            activeListeners.add(lis);
        }
    }
}
