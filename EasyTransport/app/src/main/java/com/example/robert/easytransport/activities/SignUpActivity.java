package com.example.robert.easytransport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.robert.easytransport.R;
import com.example.robert.easytransport.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Robert on 10/30/2017.
 */

public class SignUpActivity extends AppCompatActivity{

    @BindView(R.id.email)
    EditText inputEmail;
    @BindView(R.id.password)
    EditText inputPassword;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.registerscreen_logo)
    ImageView imageView;

    View v;
    FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);

        v = this.getWindow().getDecorView().getRootView();

        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.register)
    public void signUp(){
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(v.getContext(), getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(v.getContext(), getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            inputPassword.setError(getString(R.string.too_short_password));
            Toast.makeText(v.getContext(), getString(R.string.too_short_password), Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(v.getContext(), getString(R.string.auth_failed) + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(SignUpActivity.this, getString(R.string.create_email_password)+task.isSuccessful(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(v.getContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }



    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        Utils.closeKeyboard(SignUpActivity.this, findViewById(R.id.registerscreen));
    }
}
