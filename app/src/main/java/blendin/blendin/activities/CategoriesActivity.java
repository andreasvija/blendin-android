/*
* Activity for displaying posts by categories. The central screen of the app.
*/

package blendin.blendin.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import blendin.blendin.R;
import blendin.blendin.classes.Comment;
import blendin.blendin.classes.Post;
import blendin.blendin.classes.PostAdapter;
import blendin.blendin.classes.User;

public class CategoriesActivity extends Activity implements View.OnClickListener {

    int activeCategory; // Number of the currently active category
    ArrayList<Post> allPosts; // All posts in the system
    ArrayList<Post> selectedPosts; // Posts of the current selected category

    private RecyclerView recyclerView;
    private RecyclerView.Adapter postAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        allPosts = new ArrayList<>();
        getPosts();

        // "All" is the default category
        activeCategory = R.id.category_all;
        switchActiveCategory(activeCategory);

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
        postAdapter = new PostAdapter(selectedPosts);
        recyclerView.setAdapter(postAdapter);

    }

    // Get all posts from backend
    void getPosts() {
        //generatePostOne();
        //generatePostTwo();
    }

    /*void generatePostOne() {
        User user = new User("3", "Person One",
                "https://scrambledeggsdotorg.files.wordpress.com/2012/04/one.png");

        ArrayList<Comment> comments = new ArrayList<>();
        Comment one = new Comment(user, "one one");
        Comment two = new Comment(user, "one two");
        comments.add(one);
        comments.add(two);

        Post post = new Post(user, "Finance", "Help with bank transfer",
                "Something something lorem ipsum", comments);
        allPosts.add(post);
    }

    void generatePostTwo() {
        User user = new User("5", "Persones Dos",
                "https://cdn.heavencostumes.com.au/media/catalog/product/cache/1/image/9df78eab33525d08d6e5fb8d27136e95/s/m/smf-29233-tequila-shooter-guy-men_s-mexican-poncho-costume-front-close-r.jpg");

        ArrayList<Comment> comments = new ArrayList<>();
        Comment uno = new Comment(user, "uno uno");
        comments.add(uno);

        Post post = new Post(user, "Food", "Need spices for tacos!!",
                "Something something lorem ipsum", comments);
        allPosts.add(post);
    }*/

    // On category click switch to that category
    @Override
    public void onClick(View view) {
        int id = view.getId();
        //String s = getResources().getResourceEntryName(id);
        //Log.d("###", "onClick: " + s);
        switchActiveCategory(id);
        postAdapter = new PostAdapter(selectedPosts);
        recyclerView.setAdapter(postAdapter);
        //postAdapter.notifyDataSetChanged();
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

        String category = (String) findViewById(activeCategory).getTag();
        selectedPosts = new ArrayList<>();

        if (category.equals("All")) {
            for (Post p : allPosts) {
                selectedPosts.add(p);
            }
        }

        else {
            for (Post p : allPosts) {
                if (p.category.equals(category)) {
                    selectedPosts.add(p);
                }
            }
        }
    }
}
