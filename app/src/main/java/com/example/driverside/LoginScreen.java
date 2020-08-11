package com.example.driverside;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginScreen extends AppCompatActivity {

    private TextView registerTextView;
    private Button loginButton;
    private EditText mEmail,mPassword;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide Action bar and Title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login_screen);
        getSupportActionBar().hide();

        mEmail = findViewById(R.id.email_login);
        mPassword = findViewById(R.id.password_login);

        loginButton = (Button) findViewById(R.id.loginButtonID_login);
        registerTextView = (TextView) findViewById(R.id.registerTextViewID_login);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
//                    Toast.makeText(LoginScreen.this, "user sign in", Toast.LENGTH_LONG)
//                            .show();
                    startActivity(new Intent(LoginScreen.this,DashBoardLayout.class));
                    finish();
                } else {
                    Toast.makeText(LoginScreen.this, "Sig in or register first", Toast.LENGTH_LONG).show();
                }
            }
        };

        ////registerTextView clickListener
        registerTextView.setPaintFlags(registerTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//        registerTextView.setText(Html.fromHtml("<u>underlined</u> text"));
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //LoginScreen clickListener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                if (email != null && password != null) {
                    email = "res"+email;
                    if(!TextUtils.isEmpty(mEmail.getText().toString())
                            && !TextUtils.isEmpty(mPassword.getText().toString())){
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginScreen.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginScreen.this, "Not registered or internet problem", Toast.LENGTH_LONG)
                                        .show();

                            } else {
                                Toast.makeText(LoginScreen.this, "Login Success", Toast.LENGTH_LONG)
                                        .show();
                                Intent intent = new Intent(LoginScreen.this, DashBoardLayout.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                    }else {
                        Toast.makeText(LoginScreen.this, "Fill all field first", Toast.LENGTH_LONG)
                                .show();
                    }
                }else {
                    Toast.makeText(LoginScreen.this, "Fill all field first", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuthListener != null){
            mAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginScreen.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}// end of class
