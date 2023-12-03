package com.example.finalproject.AddBook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.example.finalproject.ui.messenger.chatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class singleBook extends AppCompatActivity {

    TextView singleBookTitle,singleBookPrice,singleBookDesc,singlebookOwner;
    ImageView singleBookCover,leftIcon;
    Button chatButton;
    String ownerUid;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_book);

        singleBookTitle=findViewById(R.id.singleBookTitle);
        singleBookPrice=findViewById(R.id.singleBookPrice);
        singleBookDesc=findViewById(R.id.singleBookDesc);
        singlebookOwner=findViewById(R.id.singlebookOwner);
        singleBookCover=findViewById(R.id.singleBookCover);
        leftIcon=findViewById(R.id.left_back);
        chatButton = findViewById(R.id.chatButton);

        String ownerId = getIntent().getStringExtra("ownerId");

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (ownerId.equals(currentUserId)) {
            singlebookOwner.setVisibility(View.GONE);
            chatButton.setVisibility(View.GONE);
        } else {
            singlebookOwner.setVisibility(View.VISIBLE);
            chatButton.setVisibility(View.VISIBLE);
        }

        Picasso.get().load(getIntent().getStringExtra("singleBookCover")).into(singleBookCover);
        singleBookTitle.setText(getIntent().getStringExtra("singleBookTitle"));
        singleBookPrice.setText(getIntent().getStringExtra("singleBookPrice"));
        singleBookDesc.setText(getIntent().getStringExtra("singleBookDesc"));

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
        singlebookOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve the owner's UID from the book details
                 ownerUid = getIntent().getStringExtra("ownerId");

                // Start the OwnerProfileActivity and pass the owner's UID as an extra
                Intent ownerProfileIntent = new Intent(singleBook.this, OwnerProfileActivity.class);
                ownerProfileIntent.putExtra("ownerUidSingle", ownerUid);
                startActivity(ownerProfileIntent);
            }
        });
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ownerUid = getIntent().getStringExtra("ownerId");
                Intent i=new Intent(singleBook.this, chatActivity.class);
                i.putExtra("ownerUid",ownerUid);
                startActivity(i);
            }
        });
    }

}