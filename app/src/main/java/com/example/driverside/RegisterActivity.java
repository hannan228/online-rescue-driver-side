package com.example.driverside;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private CircleImageView imageRegister;
    private EditText FullnameRegister,passwordRegister,phoneNumberRegister,emailRegister,addressRegister;
    private Button registerButtonRegister;

    private DatabaseReference mPostDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseUser mUser;
    private ProgressDialog mProgress;
    private static final int GALLERY_CODE = 1;
    private Uri mImageUri;
    private StorageReference mStorageRef;

    private TextView towardsLoginTextView;
    private static final String TAG = "RegisterActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide Action bar and Title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();


        mProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    return;
                }
            }
        };
        mStorageRef = FirebaseStorage.getInstance().getReference();


        mPostDatabase = FirebaseDatabase.getInstance().getReference("Caller Data");

        imageRegister = findViewById(R.id.profileImage_register);
        FullnameRegister = findViewById(R.id.nameEditTextID_register);
        phoneNumberRegister = findViewById(R.id.CNICEditTextID_register);
        emailRegister = findViewById(R.id.phoneEditTextNoID_register);
        addressRegister = findViewById(R.id.addressEditTextID_register);
        passwordRegister = findViewById(R.id.password_register);
        registerButtonRegister = findViewById(R.id.loginButtonID_register);

        towardsLoginTextView = (TextView) findViewById(R.id.towardsLoginTextView_register);

        towardsLoginTextView.setPaintFlags(towardsLoginTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        towardsLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginScreen.class);
                startActivity(intent);
                finish();
            }
        });

        imageRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });

        registerButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String email = emailRegister.getText().toString();
                final String password = passwordRegister.getText().toString();
                final String mName = FullnameRegister.getText().toString().trim();
                final String mNumber = phoneNumberRegister.getText().toString().trim();
                final String maddress = addressRegister.getText().toString().trim();


                if (email != null && password != null) {
                    email = "res"+email;
                    if (!TextUtils.isEmpty(emailRegister.getText().toString())
                            && !TextUtils.isEmpty(passwordRegister.getText().toString()) && !TextUtils.isEmpty(mName) && !TextUtils.isEmpty(mNumber)
                            && !TextUtils.isEmpty(maddress)) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "something wrong", Toast.LENGTH_LONG)
                                            .show();
                                } else { startPosting();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(RegisterActivity.this,"Fill all field",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Fill all field",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            imageRegister.setImageURI(mImageUri);
        }else{
            Toast.makeText(RegisterActivity.this,"something went wrong...",Toast.LENGTH_LONG).show();
        }
    }

    private void startPosting() {
        mProgress.setMessage("creating account please wait.. ");
        mProgress.show();

        final String mName = FullnameRegister.getText().toString().trim();
        final String mNumber = phoneNumberRegister.getText().toString().trim();
        String email = emailRegister.getText().toString().trim();
        final String mEmail = "res"+email;
        final String maddress = addressRegister.getText().toString().trim();
        if (!TextUtils.isEmpty(mName) && !TextUtils.isEmpty(mNumber)
                && !TextUtils.isEmpty(maddress)) {

            final int i = mEmail.indexOf(".");

            // start uploading...
            if (mImageUri == null) {
                DatabaseReference newPost = mPostDatabase.child(mEmail.substring(0, i)).child("profile detail").child("wese");
                Map<String, String> dataToSave = new HashMap<>();
                dataToSave.put("phoneNumber", mNumber);
                dataToSave.put("name", mName);
                dataToSave.put("address", maddress);
                dataToSave.put("email", mEmail);
                dataToSave.put("image", "https://firebasestorage.googleapis.com/v0/b/rescue-1122-e6da7.appspot.com/o?name=Caller%20Data%2Fimage%3A4796&uploadType=resumable&upload_id=AAANsUm11ahQkw3Rqfcl-OP5n5j3U3rfSXv7hzN23NEZ6mJj-Ye2mquZ45ieglH_rwgV3R7UTR5V9TN-jfckgKO04-8&upload_protocol=resumable");
                dataToSave.put("timeStamp", String.valueOf(java.lang.System.currentTimeMillis()));
                //  dataToSave.put("userId", mUser.getUid());

                newPost.setValue(dataToSave);
                mProgress.dismiss();
                Toast.makeText(RegisterActivity.this, "registered ", Toast.LENGTH_LONG).show();
                Intent ntent = new Intent(RegisterActivity.this, DashBoardLayout.class);
                startActivity(ntent);
                finish();
            } else {
                StorageReference filepath = mStorageRef.child("Caller Data")
                        .child(mImageUri.getLastPathSegment());
                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        DatabaseReference newPost = mPostDatabase.child("res"+(mEmail.substring(0, i))).child("profile detail").child("wese");
                        Map<String, String> dataToSave = new HashMap<>();
                        dataToSave.put("phoneNumber", mNumber);
                        dataToSave.put("name", mName);
                        dataToSave.put("address", maddress);
                        dataToSave.put("email", mEmail);
                        dataToSave.put("image", (taskSnapshot.getUploadSessionUri()).toString());
                        dataToSave.put("timeStamp", String.valueOf(java.lang.System.currentTimeMillis()));
                        //  dataToSave.put("userId", mUser.getUid());

                        newPost.setValue(dataToSave);
                        mProgress.dismiss();
                        Toast.makeText(RegisterActivity.this, "registered ", Toast.LENGTH_LONG).show();

                        Intent ntent = new Intent(RegisterActivity.this, DashBoardLayout.class);
                        startActivity(ntent);
                        finish();
                    }
                });
            }
//            gs://rescue-1122-e6da7.appspot.com/Caller%20Data/image%3A4796
//            gs://rescue-1122-e6da7.appspot.com/Caller%20Data/image%3A4848
//
//                    if (imageRegister== null){
//                         downloadUrls = ;
//                    }else {
//                        //Uri downloadUrl = taskSnapshot.getUploadSessionUri();
//                    }

        }else {
            Toast.makeText(RegisterActivity.this,"please Fill all field",Toast.LENGTH_LONG).show();
        }
    }// end on startPointing

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}// end of class
