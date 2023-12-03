package com.example.finalproject.AddBook;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.finalproject.MainActivity;
import com.example.finalproject.R;
import com.example.finalproject.databinding.FragmentAddbookBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class addbook extends Fragment {
    Button addBook;
    EditText editBookPrice,editBookTitle,editBookDesc;
    ImageView editBookCover,showBookCover;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    private FirebaseStorage firebaseStorage;
    Uri imageUri;
    RelativeLayout relativeLayout;

    private FragmentAddbookBinding binding;
    SwipeRefreshLayout swipeRefreshLayout;
    String ownerId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddbookBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
                swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutAddBook);

                addBook=view.findViewById(R.id.buttonaddBook);

                progressDialog=new ProgressDialog(getContext());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("please wait....");
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Book Uploading");
                progressDialog.setCanceledOnTouchOutside(false);

                editBookTitle=view.findViewById(R.id.editBookTitle);
                editBookCover=view.findViewById(R.id.editBookCover);
                showBookCover =view.findViewById(R.id.showBookCover);
                editBookDesc=view.findViewById(R.id.editBookDescription);
                editBookPrice=view.findViewById(R.id.editBookPrice);
                relativeLayout=view.findViewById(R.id.relative);
                databaseReference= FirebaseDatabase.getInstance().getReference();
//                database=FirebaseDatabase.getInstance();
                firebaseStorage=FirebaseStorage.getInstance();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                editBookTitle.setText("");
                editBookPrice.setText("");
                editBookDesc.setText("");
                showBookCover.setImageURI(null);
                relativeLayout.setVisibility(View.GONE);
                editBookCover.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String title = editBookTitle.getText().toString().trim();
                    String desc = editBookDesc.getText().toString().trim();
                    String price = editBookPrice.getText().toString().trim();
                    if (imageUri == null) {
                        Toast.makeText(getContext(), "Please Upload the Book cover", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (title.isEmpty()) {
                        Toast.makeText(getContext(), "Please give Title of book", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (desc.isEmpty()) {
                        Toast.makeText(getContext(), "Please tell more about the book", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (price.isEmpty()) {
                        Toast.makeText(getContext(), "Please give price of book", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        InsertData();
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }


        });
        editBookCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try {
                    uploadBookCover();
                    relativeLayout.setVisibility(View.VISIBLE);
                    editBookCover.setVisibility(View.GONE);
                }
                catch (Exception e)
                {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    private void uploadBookCover()
    {
        Dexter.withContext(getContext())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    Intent intent=new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent,101);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101 && resultCode== RESULT_OK)
        {
            imageUri=data.getData();
            showBookCover.setImageURI(imageUri);
        }
    }

    private void openHome() {
        Intent i=new Intent(getContext(), MainActivity.class);
        startActivity(i);
        getActivity().finish();
    }


    private void InsertData() {
        progressDialog.show();
        String id=databaseReference.push().getKey();

        final StorageReference reference =firebaseStorage.getReference().child("Inserts").child(System.currentTimeMillis()+"");
        reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        insert Insert =new insert();
                        String downloadUrl = uri.toString();
                        Insert.setBookCoverUri(downloadUrl);
                        Insert.setBookPrice(editBookPrice.getText().toString());
                        Insert.setBookTitle(editBookTitle.getText().toString());
                        Insert.setEditBookDesc(editBookDesc.getText().toString());
                        ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Insert.setOwnerId(ownerId);
                        Insert.setKey(id);

                        databaseReference.child("Inserts").child(id).setValue(Insert)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "books details is added", Toast.LENGTH_SHORT).show();
                                            openHome();
                                            progressDialog.dismiss();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "books details is not added", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                });
            }
        });


    }
}