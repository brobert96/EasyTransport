package com.example.robert.easytransport.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.robert.easytransport.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Robert on 10/30/2017.
 */

public class ResetPassword extends AppCompatActivity {

    @BindView(R.id.email)
    EditText inputEmail;
    FirebaseAuth auth;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    View v;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_activity);

        v = this.getWindow().getDecorView().getRootView();

        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.resetButton)
    public void sendResetEmail(){
        String email = inputEmail.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(ResetPassword.this, getString(R.string.forgot_password_email), Toast.LENGTH_LONG).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(v.getContext(), getString(R.string.reset_email_sent), Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(v.getContext(), getString(R.string.reset_email_failed), Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
