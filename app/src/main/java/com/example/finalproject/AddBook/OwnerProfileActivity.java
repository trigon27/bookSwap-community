package com.example.finalproject.AddBook;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.example.finalproject.ReadWriteUserDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OwnerProfileActivity extends AppCompatActivity {
    private TextView textViewFullName,textViewEmail,textViewMobile;
    private String fullName,email,mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_profile);
        textViewFullName=findViewById(R.id.ownerNameTextView);
        textViewEmail=findViewById(R.id.ownerEmailTextView);
        textViewMobile=findViewById(R.id.ownerPhoneTextView);
        showUserProfile();
    }

    private void showUserProfile()
    {
        String ownerUidID=getIntent().getStringExtra("ownerUidSingle");
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Register_Users");
        referenceProfile.child(ownerUidID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                    fullName = readUserDetails.full_name;
                    email = readUserDetails.email;
                    mobile = readUserDetails.phone_no;

                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewMobile.setText(mobile);
                }
                catch (Exception e)
                {
                    Toast.makeText(OwnerProfileActivity.this, "something went wrong!!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}