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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.Profile;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import blendin.blendin.R;
import blendin.blendin.classes.Post;
import blendin.blendin.classes.PostAdapter;
import blendin.blendin.classes.PostChildEventListener;

public class CategoriesActivity extends Activity {

    private int activeCategory; // Number of the currently active category
    public static ArrayList<Post> selectedPosts; // Posts of the current selected category

    private RecyclerView recyclerView;
    public static RecyclerView.Adapter postAdapter;

    FirebaseDatabase database;
    DatabaseReference postsReference;
    ArrayList<DatabaseReference> usedReferences;
    ArrayList<ChildEventListener> activeListeners;

    private String[] categories = {"All", "Finance", "Food", "Other", "Shopping", "Transport", "Travel"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        View.OnClickListener onCategoryClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switchActiveCategory(id);
            }
        };

        // Make category labels clickable
        findViewById(R.id.category_all).setOnClickListener(onCategoryClickListener);
        findViewById(R.id.category_finance).setOnClickListener(onCategoryClickListener);
        findViewById(R.id.category_food).setOnClickListener(onCategoryClickListener);
        findViewById(R.id.category_shopping).setOnClickListener(onCategoryClickListener);
        findViewById(R.id.category_transport).setOnClickListener(onCategoryClickListener);
        findViewById(R.id.category_travel).setOnClickListener(onCategoryClickListener);
        findViewById(R.id.category_other).setOnClickListener(onCategoryClickListener);

        // Set up the RecyclerView of the posts
        recyclerView = (RecyclerView) findViewById(R.id.posts_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        database = FirebaseDatabase.getInstance();
        postsReference = database.getReference("posts");
        activeListeners = new ArrayList<>();
        usedReferences = new ArrayList<>();
        // "All" is the default category
        activeCategory = R.id.category_all;
        switchActiveCategory(activeCategory);

        // TODO: download supported languages in user picked language

        //script for downloading language names
        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Translate translate = TranslateOptions.newBuilder().setApiKey(getString(R.string.GOOGLE_API_KEY)).build().getService();
                Translate.LanguageListOption target = Translate.LanguageListOption.targetLanguage("en");
                List<Language> languages = translate.listSupportedLanguages(target);
                String output1 = "";
                for (Language language : languages) {
                    output1 += "<item>" + language.getName() + "</item>\n";
                }
                String output2 = "";
                for (Language language : languages) {
                    output2 += "<item>" + language.getCode() + "</item>\n";
                }
                Log.d("###", output1);
                Log.d("###", output2);
            }
        });
        thread.start();*/
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
            Profile profile = Profile.getCurrentProfile();
            String userID = profile.getId();
            intent.putExtra("userID", userID);
            startActivity(intent);
            return true;
        }

        // TODO: settings
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        selectedPosts = new ArrayList<>();

        String category = (String) findViewById(activeCategory).getTag();
        setChildEventListener(category);

        // Make it use a custom adapter
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
