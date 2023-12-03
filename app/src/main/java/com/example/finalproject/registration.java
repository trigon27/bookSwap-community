package com.example.finalproject;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class registration extends AppCompatActivity {
    EditText editTextMail,editTextPass,full_name;
    Button registerButton;
    TextView loginRedirect;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    EditText register_phone;


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        mAuth=FirebaseAuth.getInstance();
        editTextMail=findViewById(R.id.register_in_email);
        editTextPass=findViewById(R.id.register_in_password);
        registerButton=findViewById(R.id.log_in_button);
        loginRedirect=findViewById(R.id.registerRediretText);
        progressBar=findViewById(R.id.registerProgressBar);
        loginRedirect=findViewById(R.id.registerRediretText);
        register_phone=findViewById(R.id.register_in_phone_no);
        full_name=findViewById(R.id.register_full_name);
        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I=new Intent(getApplicationContext(), login.class);
                startActivity(I);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(view.VISIBLE);
                String email,password,phone_no,full_Name;
                email=String.valueOf(editTextMail.getText());
                password=String.valueOf(editTextPass.getText());
                phone_no=String.valueOf(register_phone.getText());
                full_Name=String.valueOf(full_name.getText());
                if (TextUtils.isEmpty(full_Name))
                {
                    Toast.makeText(registration.this, "Please provide your Name", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(view.GONE);
                    return;
                }
                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(registration.this, "Please provide your email", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(view.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password))
                {
                    Toast.makeText(registration.this, "Please provide the password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(view.GONE);
                    return;
                }
                if (TextUtils.isEmpty(phone_no))
                {
                    Toast.makeText(registration.this, "Please provide the phone number", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(view.GONE);
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(view.GONE);
                            if(task.isSuccessful())
                            {
                                FirebaseUser user= mAuth.getCurrentUser();
                                Toast.makeText(registration.this, "Account Created", Toast.LENGTH_SHORT).show();
                                //storing name of user in firebase object
                                UserProfileChangeRequest userProfileChangeRequest=new UserProfileChangeRequest.Builder().setDisplayName(full_Name).build();
                                user.updateProfile(userProfileChangeRequest);

                                ReadWriteUserDetails writeUserDetails=new ReadWriteUserDetails(full_Name,email,phone_no);
                                DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Register_Users");
                                referenceProfile.child(user.getUid()).setValue(writeUserDetails);

                                registerFCMToken();

                                Intent intent_log=new Intent(getApplicationContext(),login.class);
                                intent_log.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent_log);
                                finish();

                            }
                            else
                            {
                             try
                             {
                                 throw task.getException();
                             }
                             catch (FirebaseAuthWeakPasswordException e)
                             {
                                 editTextPass.setError("Your password is too weak, kindly use strong password");
                                 editTextPass.requestFocus();
                             }
                             catch (FirebaseAuthInvalidCredentialsException e)
                             {
                                 editTextMail.setError("Your email is invalid or already in use, kindly change your email");
                                 editTextMail.requestFocus();
                             }
                             catch (FirebaseAuthUserCollisionException e)
                             {
                                 editTextMail.setError("Your email is already Register, kindly change your email");
                                 editTextMail.requestFocus();
                             }
                             catch (Exception e)
                             {
                                 Log.e(TAG,e.getMessage());
                                 Toast.makeText(registration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                             }
                             progressBar.setVisibility(View.GONE);
                            }
                            }
                        });
            }
        });
    }

    private void registerFCMToken() {
        String myUid = mAuth.getUid();
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String token) {
                        Log.d(TAG, "onSuccess: token: " + token);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("fcmToken", token);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Register_Users");
                        ref.child(myUid)
                                .updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "Token updated... ");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Token fail... ");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


}