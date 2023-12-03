package com.example.finalproject.ui.myprofile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.finalproject.R;
import com.example.finalproject.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class myProfile extends Fragment {

    private TextView textViewWelcome,textViewFullName,textViewEmail,textViewMobile;
    private ProgressBar progressBar;
    private String fullName,email,mobile;
    private ImageView imageView;
    private SwipeRefreshLayout swipeContainer;
  FirebaseUser firebaseUser;
    FirebaseAuth profileAuth;
    private StorageReference storageReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {   swipeContainer=view.findViewById(R.id.pullToRefresh);
        swipeToRefresh(swipeContainer);
        super.onViewCreated(view, savedInstanceState);
        textViewWelcome=view.findViewById(R.id.textView_show_welcome);
        textViewFullName=view.findViewById(R.id.textView_show_full_name);
        textViewEmail=view.findViewById(R.id.textView_show_email);
        textViewMobile=view.findViewById(R.id.textView_show_mobile);
        progressBar=view.findViewById(R.id.progress_bar);
        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        profileAuth = FirebaseAuth.getInstance();
        //upload pic
        imageView=view.findViewById(R.id.profilePic);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(getContext(), upload_profile.class);
                startActivity(intent);
            }
        });
     firebaseUser= authProfile.getCurrentUser();
        if(firebaseUser==null)
        {
            Toast.makeText(getContext(), "Something went wrong! Users details are not available at the moment", Toast.LENGTH_LONG).show();
        }
        else
        {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }

    private void swipeToRefresh(SwipeRefreshLayout swipeContainer)
    {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar.setVisibility(View.VISIBLE);
                showUserProfile(firebaseUser);
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void showUserProfile(FirebaseUser firebaseUser)
    {
        String userID=firebaseUser.getUid();
        //extracting data from firebase
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Register_Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails=snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails!=null)
                {
                    fullName=readUserDetails.full_name;
                    email=readUserDetails.email;
                    mobile=readUserDetails.phone_no;

                    textViewWelcome.setText(" welcome "+fullName);
                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewMobile.setText(mobile);

        try {
                    storageReference = FirebaseStorage.getInstance().getReference("Display_pics");
                    StorageReference fileReference=storageReference.child(firebaseUser.getUid()+"."+"jpg");

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).placeholder(R.drawable.progress_animation)
                           .error(R.drawable.baseline_person_off_24)
                           .into(imageView);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Upload your profile picture", Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception e)
        {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }


                }else
                {
                    Toast.makeText(getContext(), "Something went wrong! ", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(getContext(), "Something went wrong! ", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}