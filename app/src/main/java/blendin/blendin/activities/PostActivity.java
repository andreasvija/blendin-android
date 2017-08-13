package blendin.blendin.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateUtils;

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

            /*Picasso.with(this)
                    .load(post.author.photoURL)
                    //.resize(width,height).noFade()
                    .into(holder.photoView);
            holder.nameView.setText(post.author.name);
            holder.titleView.setText(post.title);
            holder.answersView.setText(String.valueOf(post.getCommentCount()) + " " + "answers");
            CharSequence ago = DateUtils.getRelativeTimeSpanString(post.timestamp,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            holder.timeAgoView.setText(ago);*/


        }
    }

}
