package com.creatures.facebooksignup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    TextView fb_tv_username,fb_tv_user_id,fb_tv_email_id;
    LinearLayout linearLayout_info;
    CircleImageView fb_imageView_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        loginButton=(LoginButton)findViewById(R.id.login_button);

        fb_tv_user_id=(TextView)findViewById(R.id.fb_text_view_user_id);
        fb_tv_username=(TextView)findViewById(R.id.fb_text_view_user_name);
        fb_tv_email_id=(TextView)findViewById(R.id.fb_text_view_email_id);
        linearLayout_info=(LinearLayout)findViewById(R.id.linear_layout_info);

        fb_imageView_profile=(CircleImageView)findViewById(R.id.fb_image_view_profile);

        callbackManager = CallbackManager.Factory.create();

        loginButton.setPermissions(Arrays.asList("email","user_birthday","user_gender","user_hometown"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        Toast.makeText(MainActivity.this, " Successful ", Toast.LENGTH_SHORT).show();
                        linearLayout_info.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, " Unsuccessful ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(MainActivity.this, " Error ", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            if (currentAccessToken == null)
            {
                Toast.makeText(MainActivity.this, "User Logout", Toast.LENGTH_SHORT).show();
                linearLayout_info.setVisibility(View.GONE);
            }
            else
            {
                load_user_profile(currentAccessToken);
            }

        }
    };

    private void load_user_profile(AccessToken newAccessToken)
    {


        GraphRequest graphRequest = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                String user_id;

                if (response.getError() != null)
                {
                    Toast.makeText(MainActivity.this, "Error in Fetching Data", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String user_lastname = object.optString("last_name");
                    String user_firstname = object.optString("first_name");
                    String user_email = response.getJSONObject().optString("email");
                    user_id = object.optString("id");

                    String fullname = "User Fullname: "+user_firstname+" "+user_lastname;
                    String final_email_id="User Email ID: "+user_email;
                    String final_user_id="User ID: "+user_id;


                    fb_tv_email_id.setText(final_email_id);
                    fb_tv_username.setText(fullname);
                    fb_tv_user_id.setText(final_user_id);

                    String profile_url="https://graph.facebook.com/"+user_id+"/picture?return_ssl_resources=1";
                    Picasso.get().load(profile_url).into(fb_imageView_profile);

                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "last_name,first_name,email,id");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();



    }
}