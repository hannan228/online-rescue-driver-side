package com.example.driverside;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.driverside.Model.Registration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ProgressDialog mProgress1;

    private CircleImageView profileImage;
    private TextView namePofile,addressP,phoneNumberP,emailP;
    private String mName,mPhoneNumber,mEmail,mAddress;
    private String mImage;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String subEmail = mUser.getEmail();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Caller Data").child(subEmail.substring(0,subEmail.indexOf("."))).child("profile detail");

        Log.d(TAG, "onCreate: "+myRef);

        mProgress1 = new ProgressDialog(ProfileActivity.this);
        namePofile = findViewById(R.id.CNICEditTextID_profile);
        addressP = findViewById(R.id.nameEditTextID_profile);
        phoneNumberP = findViewById(R.id.phonNoID_profile);
        emailP = findViewById(R.id.passwordID_profile);
        profileImage = findViewById(R.id.profileImage_profile);

    }  // end of on create


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout){
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this,LoginScreen.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mProgress1.setMessage("please wait fetching your data.. ");
        mProgress1.show();

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Registration registration = dataSnapshot.getValue(Registration.class);

                Log.d(TAG, "onChildAdded: "+registration);


                // showing progress dialog

                //fetching data from database
                mName = registration.getName();
                mPhoneNumber = registration.getPhoneNumber();
                mEmail = registration.getEmail();
                mAddress = registration.getAddress();
                mImage =registration.getImage();

                namePofile.setText(mName);
                phoneNumberP.setText(mPhoneNumber);
                emailP.setText(mEmail);
                addressP.setText(mAddress);


                ///profileImage.setImageResource(R.drawable.accidentview);


                //Glide.with(getApplicationContext()).load(registration.getImage()).into(profileImage);

                //Picasso.get().load(registration.getImage()).into(profileImage);
                //Picasso.with(getApplicationContext()).load(mImage).into(profileImage);
                //profileImage.setImageResource(mImage);
                mProgress1.dismiss();
//                Toast.makeText(ProfileActivity.this,"1st here"+mImage,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }// end of onStart
}