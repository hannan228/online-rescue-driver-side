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
        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("Caller Data");

        imageRegister = findViewById(R.id.profileImage_register);
        FullnameRegister = findViewById(R.id.nameEditTextID_register);
        phoneNumberRegister = findViewById(R.id.CNICEditTextID_register);
        emailRegister = findViewById(R.id.phoneEditTextNoID_register);
        addressRegister = findViewById(R.id.addressEditTextID_register);
        passwordRegister = findViewById(R.id.password_register);
        registerButtonRegister = findViewById(R.id.loginButtonID_register);

        towardsLoginTextView = (TextView) findViewById(R.id.towardsLoginTextView_register);

        towardsLoginTextView.setPaintFlags(towardsLoginTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//        registerTextView.setText(Html.fromHtml("<u>underlined</u> text"));
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
                final String email = emailRegister.getText().toString();
                final String password = passwordRegister.getText().toString();

                if (email != null && password != null) {
                    if (!TextUtils.isEmpty(emailRegister.getText().toString())
                            && !TextUtils.isEmpty(passwordRegister.getText().toString())) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "something went wrong here...", Toast.LENGTH_LONG)
                                            .show();
                                } else {
                                    startPosting();
                                }
                            }
                        });
                    } else {

                    }
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
        final String mEmail = emailRegister.getText().toString().trim();
        final String maddress = addressRegister.getText().toString().trim();

        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("Caller Data").child(mNumber);
        if (!TextUtils.isEmpty(mName) && !TextUtils.isEmpty(mNumber)
                && mImageUri != null && !TextUtils.isEmpty(maddress)){


            // start uploading...
            StorageReference filepath = mStorageRef.child("Caller")
                    .child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                    DatabaseReference newPost = mPostDatabase.push();

                    Map<String, String> dataToSave = new HashMap<>();
                    dataToSave.put("phoneNumber", mNumber);
                    dataToSave.put("name", mName);
                    dataToSave.put("address", maddress);
                    dataToSave.put("email", mEmail);
                    dataToSave.put("image", downloadUrl.toString());
                    dataToSave.put("timeStamp", String.valueOf(System.currentTimeMillis()));
//                    dataToSave.put("userId", mUser.getUid());

                    newPost.setValue(dataToSave);
                    mProgress.dismiss();
                    Toast.makeText(RegisterActivity.this,"registered ",Toast.LENGTH_LONG).show();


                    Intent ntent = new Intent(RegisterActivity.this, DashBoardLayout.class);
                    ntent.putExtra("phone Number",mNumber);
                    startActivity(ntent);
                    finish();
                }
            });
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
