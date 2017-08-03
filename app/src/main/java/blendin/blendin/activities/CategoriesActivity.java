package blendin.blendin.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import blendin.blendin.R;
import blendin.blendin.classes.Post;
import blendin.blendin.classes.User;

public class CategoriesActivity extends Activity implements View.OnClickListener{

    int activeCategory;
    ArrayList<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        activeCategory = R.id.category_all;
        switchActiveCategory(activeCategory);

        findViewById(R.id.category_all).setOnClickListener(this);
        findViewById(R.id.category_finance).setOnClickListener(this);
        findViewById(R.id.category_food).setOnClickListener(this);
        findViewById(R.id.category_shopping).setOnClickListener(this);
        findViewById(R.id.category_transport).setOnClickListener(this);
        findViewById(R.id.category_travel).setOnClickListener(this);
        findViewById(R.id.category_other).setOnClickListener(this);

        posts = new ArrayList<>();
        generatePostOne();
        generatePostTwo();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        String s = getResources().getResourceEntryName(id);
        Log.d("###", "onClick: " + s);
        switchActiveCategory(id);
    }

    void switchActiveCategory(int newActive) {
        LinearLayout ll = (LinearLayout) findViewById(activeCategory);
        ll.setBackground(getResources().getDrawable(R.drawable.back_inactive));
        activeCategory = newActive;
        ll = (LinearLayout) findViewById(activeCategory);
        ll.setBackground(getResources().getDrawable(R.drawable.back_active));
    }

    void generatePostOne() {
        User user = new User("3", "Person One", "https://scrambledeggsdotorg.files.wordpress.com/2012/04/one.png");
        Post post = new Post(user, "Finance", "Help with bank transfer", "Something something lorem ipsum");
        posts.add(post);
    }

    void generatePostTwo() {
        User user = new User("5", "Persones Dos", "https://cdn.heavencostumes.com.au/media/catalog/product/cache/1/image/9df78eab33525d08d6e5fb8d27136e95/s/m/smf-29233-tequila-shooter-guy-men_s-mexican-poncho-costume-front-close-r.jpg");
        Post post = new Post(user, "Food", "Need spices for tacos!!", "Something something lorem ipsum");
        posts.add(post);
    }
}
