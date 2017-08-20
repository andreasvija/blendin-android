/*
* Activity for authenticating user. Is the launching activity.
*/

package blendin.blendin.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import blendin.blendin.R;

public class LoginActivity extends Activity {

    LoginButton loginButton;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token != null) { // If user is already logged in, the access token exists
            enterApp();
        }

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile", "user_location", "user_hometown");
        // If using in a fragment
        //loginButton.setFragment(this);

        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //AccessToken token = loginResult.getAccessToken();
                //Log.d("###debug", "facebook:onSuccess" + loginResult + token);
                enterApp();
            }

            @Override
            public void onCancel() {
                //Log.d("###debug", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                //Log.d("###debug", "facebook:onError", exception);
            }
        });
    }

    // Take the result and make the CallbackManager handle it
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // Start CategoriesActivity and end LoginActivity
    void enterApp () {
        Intent intent = new Intent(getApplicationContext(), CategoriesActivity.class);
        startActivity(intent);
        finish();
    }
}
