package blendin.blendin.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import blendin.blendin.R;

public class CategoriesActivity extends Activity implements View.OnClickListener{

    int activeCategory;

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
}
