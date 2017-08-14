package blendin.blendin.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import blendin.blendin.R;
import blendin.blendin.classes.Post;

public class PostActivity extends Activity {

    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //post = (Post) getIntent().getSerializableExtra("post");
            post = (Post) extras.get("post");

            Picasso.with(this).load(post.author.photoURL)
                    //.resize(width,height).noFade()
                    .into((ImageView) findViewById(R.id.author_photo));
            ((TextView) findViewById(R.id.author_name)).setText(post.author.name);
            ((TextView) findViewById(R.id.title)).setText(post.title);
            ((TextView) findViewById(R.id.content)).setText(post.content);
            //findViewById(R.id.).answersView.setText(String.valueOf(post.getCommentCount()) + " " + "answers");
            CharSequence ago = DateUtils.getRelativeTimeSpanString(post.timestamp,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            ((TextView) findViewById(R.id.timestamp)).setText(ago);
            
        }
    }

}
