package com.martirio.firebase_entregable.Vista.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.martirio.firebase_entregable.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import com.twitter.sdk.android.core.identity.TwitterAuthClient;


public class MainActivity extends AppCompatActivity {

    private ImageButton loginBtn;
    TwitterAuthClient twitterAuthClient;
    TwitterApiClient twitterApiClient;
    private ProgressBar unProgressBar;
    private ImageButton fakeFbLogin;

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private ShareButton shareButton;
    ProgressDialog progress;
    private String idFacebook, nombreFacebook, nombreMedioFacebook, apellidoFacebook, sexoFacebook, imagenFacebook, nombreCompletoFacebook, emailFacebook;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //INICIALIZO TWITTER
        Twitter.initialize(this);
        mAuth= FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);
        unProgressBar=(ProgressBar)findViewById(R.id.progress_bar);




       twitterAuthClient = new TwitterAuthClient();
        //INICIALIZAR FIREBASE

        mAuthListener=new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= firebaseAuth.getCurrentUser();
                if (user!=null){
                    String mail;
                    String foto;
                    if (user.getEmail() == null||user.getEmail().isEmpty()){mail=user.getDisplayName();}
                    else{mail=user.getEmail();}
                    if (user.getPhotoUrl()==null){foto="sinFoto";}
                    else{foto=user.getPhotoUrl().toString();}
                    ingresarLogueado(MainActivity.this, mail, foto);
                    Toast.makeText(MainActivity.this, "Bienvenido! "+mail, Toast.LENGTH_SHORT).show();
                    unProgressBar.setVisibility(View.GONE);


                }
                else{

                }
            }
        };







        loginBtn = (ImageButton) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                twitterAuthClient.authorize(MainActivity.this, new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> result) {
                        //success
                        handleTwitterSession(result.data);
                        unProgressBar.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void failure(TwitterException exception) {
                        //failure
                        Toast.makeText(MainActivity.this, "Logeo fallido", Toast.LENGTH_SHORT).show();
                        unProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        idFacebook = nombreFacebook = nombreMedioFacebook = apellidoFacebook = sexoFacebook = nombreCompletoFacebook = emailFacebook ="";
        imagenFacebook="nada";

        //for facebook
        //FACEBOOK

        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("error", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());

                unProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
                Log.d("error", "facebook:onCancel");
                unProgressBar.setVisibility(View.GONE);
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("error", "facebook:onError", error);
                unProgressBar.setVisibility(View.GONE);
                // ...
            }
        });



        fakeFbLogin=(ImageButton)findViewById(R.id.fakeLogin);
        fakeFbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
                unProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }



    public void ingresarLogueado(Activity unaActivity, String mail, String imagenUsuario){
        Intent unIntent = new Intent(unaActivity, SegundaActivity.class);
        Bundle bundle=new Bundle();

        unIntent.putExtras(bundle);
        finish();
        startActivity(unIntent);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result
        twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private void handleTwitterSession(TwitterSession session) {

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {



                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public void LogoutFirebase(){
        FirebaseAuth.getInstance().signOut();
    }




    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("tag1", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("tag2", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("tag3", "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
/*
    private boolean estaLogueadoEnFacebook(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
    private boolean estaLogueadoEnTwitter(){
        Session activeSession =TwitterCore.getInstance().getSessionManager().getActiveSession();
        return activeSession!=null;
    }
*/



}
