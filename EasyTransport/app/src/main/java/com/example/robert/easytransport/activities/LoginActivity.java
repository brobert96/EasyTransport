package com.example.robert.easytransport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.robert.easytransport.MainActivity;
import com.example.robert.easytransport.R;
import com.example.robert.easytransport.Utils.Utils;
import com.example.robert.easytransport.data.SharedPrefs;
import com.example.robert.easytransport.fragments.MainFragment;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Robert on 10/30/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private static final int G_SIGN_IN = 2;
    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.email)
    EditText inputEmail;

    @BindView(R.id.password)
    EditText inputPassword;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.loginscreen_logo)
    ImageView imageView;

    @BindView(R.id.google_sign_in)
    SignInButton googleButton;

    @BindView(R.id.signIn)
    Button signInButton;

    @BindView(R.id.facebook_login_button)
    LoginButton facebookLoginButon;

    View v;
    private FirebaseAuth auth;
    private GoogleSignInOptions gso;
    private GoogleSignInClient googleSignInClient;
    private static final String splashName = SplashActivity.class.getSimpleName();
    private String password;
    private String email;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            updateUI(MainActivity.class);
        }
        setContentView(R.layout.loginfragment_layout);

        ButterKnife.bind(this);

        googleButton.setClickable(true);
        signInButton.setClickable(true);

        callbackManager = CallbackManager.Factory.create();

        v = this.getWindow().getDecorView().getRootView();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("228673432104-6stku8sa4cndvbk9vsgf6t1362ev75jp.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);



        facebookLoginButon.setReadPermissions("email", "public_profile");
        facebookLoginButon.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccesToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "faceboko:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError");
            }
        });
    }

    @OnClick(R.id.google_sign_in)
    public void googleSingIn(){
        googleButton.setClickable(false);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            firebaseAuthWithGoogle(account);
        }else{
            Intent googleIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(googleIntent, G_SIGN_IN);
        }
    }

    @OnClick(R.id.continue_no_account)
    public void startWithoutAccount(){
        updateUI(MainActivity.class);
        finish();
    }

    @OnClick(R.id.resetPassword)
    public void resetPassword(){
        updateUI(ResetPassword.class);
    }

    @OnClick(R.id.signUp)
    public void signUpIntent(){
        updateUI(SignUpActivity.class);
    }
    @OnClick(R.id.signIn)
    public void signIn(){
            signInButton.setClickable(false);
            Utils.closeKeyboard(getApplicationContext(), v);
            email = inputEmail.getText().toString();
            password = inputPassword.getText().toString();


            if (TextUtils.isEmpty(email)) {
                Toast.makeText(v.getContext(), getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(v.getContext(), getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                    Toast.makeText(v.getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    signInButton.setClickable(true);
                                    progressBar.setVisibility(View.GONE);
                            }else{
                                updateUI(MainActivity.class);
                                finish();
                            }
                        }
                    });

    }



    private void handleFacebookAccesToken(AccessToken token){
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "signIn With facebook success");
                    FirebaseUser user = auth.getCurrentUser();
                    updateUI(MainActivity.class);
                    progressBar.setVisibility(View.GONE);
                    finish();
                }else{
                    Log.d(TAG, "sign in with facebook failed");
                    Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(SharedPrefs.getsInstance(getApplicationContext()).getCalledFromAddBusStationActivity()) {
            SharedPrefs.getsInstance(getApplicationContext()).setCalledFromAddBusStationActivity(false);
            updateUI(MainActivity.class);
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.closeKeyboard(LoginActivity.this, v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == G_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Log.d(TAG, task.toString()+"result Task");
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, account.getEmail() + "email");
                firebaseAuthWithGoogle(account);
            }catch (ApiException e){
                Log.d(TAG, e.getStatusCode()+"Failed");
            }
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            updateUI(MainActivity.class);
                            progressBar.setVisibility(View.GONE);
                            finish();
                        }else{
                            Toast.makeText(v.getContext(), getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            Log.d(TAG, task.getException().getMessage() + "login task");
                            googleButton.setClickable(true);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void updateUI(Class destination){
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(LoginActivity.this, destination);
        startActivity(intent);
        progressBar.setVisibility(View.GONE);
        finish();
    }
}
