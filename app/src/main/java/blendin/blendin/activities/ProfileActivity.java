package blendin.blendin.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import blendin.blendin.R;

public class ProfileActivity extends Activity {

    LoginButton loginButton;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Profile profile = Profile.getCurrentProfile();
        String id = profile.getId();
        String name = profile.getName();
        String photoURL = profile.getProfilePictureUri(500,500).toString();


        ((TextView) findViewById(R.id.name)).setText(name);
        Picasso.with(getParent())
                .load(photoURL)
                //.resize(width,height).noFade()
                .into((ImageView) findViewById(R.id.picture));

        /*new GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/" + id,
            null,
            HttpMethod.GET,
            new GraphRequest.Callback() {
                public void onCompleted(GraphResponse response) {
                    Log.d("###", response.toString());
                    response.getJSONObject().get("graphObject");
                }
            }
        ).executeAsync();*/

        findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();

                Intent broadcastIntent = new Intent("finish_activity");
                sendBroadcast(broadcastIntent);
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

    }
}
