package com.example.finalproject.ui.messenger;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalproject.R;
import com.example.finalproject.ReadWriteUserDetails;
import com.example.finalproject.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class chatActivity extends AppCompatActivity {
    String ownerUid = "";
    private ActivityChatBinding binding;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    private String myUid = "";
    private String myName="";
    private String chatPath = "";
    private Uri imageUri = null;
    private String receiptFckToken="";
    public static final String MESSAGE_TYPE_TEXT = "text";
    public static final String MESSAGE_TYPE_IMAGE = "image";
    public static final String NOTIFICATION_MESSAGE_TYPE="NEW MESSAGE";
    public static final String service_key="AAAAj-VV9_o:APA91bFewrfeW6s3ytG020kKJ1C-fImnW8_lDYSDmHetJs2ey7oZwMnMVt6LmzlKAWpv0RO46yHxaeTqp0QdX6GCYyFLUZP-nGBFy3tAvZI1GfSnu5HZ2hub2i72Up2sWnT8Lesgla4D";
    private static final String TAG = "CHAT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait.....");
        progressDialog.setCanceledOnTouchOutside(false);
        myUid = firebaseAuth.getUid();
        ownerUid = getIntent().getStringExtra("ownerUid");
        chatPath = chatPath(ownerUid, myUid);
        Log.d(TAG, "onCreate : ownerUid :" + ownerUid);
        Log.d(TAG, "onCreate :  myUid :" + myUid);
        Log.d(TAG, "onCreate : chatPath:" + chatPath);
        // Initialize RecyclerView

        // Set the layout manager (LinearLayoutManager for a vertical list)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Optional: Scrolls to the end of the list
        binding.messageRecyclerView.setLayoutManager(layoutManager);

        // Create the adapter and set it to the RecyclerView
        ArrayList<ModelChat> chatArrayList = new ArrayList<>();
        AdapterChat adapterChat = new AdapterChat(chatActivity.this,chatArrayList);
        binding.messageRecyclerView.setAdapter(adapterChat);
        loadMyinfo();
        loadOnwerDetails();
        loadmessage();
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePickDialoug();
            }


        });
        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void loadMyinfo()
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Register_Users");
        ref.child(""+firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                        myName = readUserDetails.full_name;
                        Log.d(TAG,"my name :"+myName);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadmessage() {


        Log.d(TAG, "LoadMessages:");
        ArrayList<ModelChat> chatArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.child(chatPath)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                ModelChat modelChat = ds.getValue(ModelChat.class);
                                chatArrayList.add(modelChat);
                            } catch (Exception e) {
                                Log.e(TAG, "LoadMessages:onDataChange: ", e);
                            }
                        }
                        AdapterChat adapterChat=new AdapterChat(chatActivity.this,chatArrayList);
                        binding.messageRecyclerView.setAdapter(adapterChat);
                    }

                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });
    }



    private void loadOnwerDetails()
    {
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Register_Users");
        referenceProfile.child(ownerUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                    String Name = readUserDetails.full_name;
                    receiptFckToken=""+snapshot.child("fcmToken").getValue();
                    Log.d(TAG,"receiptToken in LoadOnwer :"+receiptFckToken);
                    binding.toolProfileName.setText(Name);
                    storageReference = FirebaseStorage.getInstance().getReference("Display_pics");
                    StorageReference fileReference=storageReference.child(ownerUid+"."+"jpg");

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri)
                                    .error(R.drawable.baseline_person_off_24)
                                    .into(binding.ownerProfileImageView);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                        }
                    });

                }
                catch (Exception e)
                {
                    Toast.makeText(chatActivity.this, "something went wrong!!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void imagePickDialoug() {
        PopupMenu popupMenu = new PopupMenu(this, binding.galleryButton);
        popupMenu.getMenu().add(Menu.NONE,1,1,"Camera");
        popupMenu.getMenu().add(Menu.NONE,2,2,"Gallery");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId=item.getItemId();
                if(itemId==1)
                {
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
                        requestCameraPermissions.launch(new String[]{Manifest.permission.CAMERA});
                    }
                    else
                    {
                        requestCameraPermissions.launch(new String[]{Manifest.permission.CAMERA});
                    }
                }
                else if (itemId==2)
                {
                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)
                    {
                        pickImageGallery();
                    }
                    else
                    {
                    requestStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                }
                return false;
            }
        });
            }

    private ActivityResultLauncher<String[]> requestCameraPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
          new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result)
        {
            boolean areAllGranted=true;
            for(Boolean isGranted:result.values())
            {
                areAllGranted=areAllGranted && isGranted;
            }
            if (areAllGranted){
                PickImageCamera();
            }
            else {
                Toast.makeText(chatActivity.this, "Camera and Storage permissions are denied...!", Toast.LENGTH_SHORT).show();
            }
        }

        }
    );

    private ActivityResultLauncher<String> requestStoragePermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {

                public void onActivityResult(Boolean isGranted) {
                    Log.d(TAG, "onActivityResult: isGranted: " + isGranted);
                    //let's check if permission is granted or not
                    if (isGranted) {
                        //Storage Permission granted, we can now launch gallery to pick image
                        pickImageGallery();
                    } else {
                        Toast.makeText(chatActivity.this, "permission denied", Toast.LENGTH_SHORT).show();
                        //Storage Permission dennied, we can't launch gallery to pick image
                    }
                }
            }
 );

    private void PickImageCamera() {
        Log.d(TAG, "pickImageCamera: ");

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "CHAT_IMAGE_TEMP");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "CHAT_IMAGE_TEMP_DESCRIPTION");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);

    }
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                //Check if image is coptured or not
                    if (result.getResultCode() == Activity.RESULT_OK)
                    {
                        Log.d(TAG, "onActivityResult: imageUri: " + imageUri);
                        uploadToFirebaseStorage();
                    } else 
                    {
                        Toast.makeText(chatActivity.this, "cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void pickImageGallery()
    {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: imageUri: " + imageUri);
                        uploadToFirebaseStorage();
                    } else {
                        Toast.makeText(chatActivity.this, "cancelled..", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
    private void uploadToFirebaseStorage() {
        Log.d(TAG, "uploadToFirebaseStorage: ");
        progressDialog.setMessage("Uploading image...!");
        progressDialog.show();

        long timestamp = getTimeStamp();
        String filePathAndName = "ChatImages/" + timestamp;

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(imageUri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {


                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot)
                    {
                          double progress =(100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                          progressDialog.setMessage("uploading image. Progress : "+(int)progress+"%");
                    }
                }
                ).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                        while (!uriTask.isSuccessful());

                        String imageUrl = uriTask.getResult().toString();

                        if (uriTask.isSuccessful())
                        {
                            sendMessage(MESSAGE_TYPE_IMAGE,imageUrl,timestamp);
                            Toast.makeText(chatActivity.this, "image uploaded successfully", Toast.LENGTH_SHORT).show();
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                        Toast.makeText(chatActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void validateData(){
        Log.d(TAG, "validateData: ");

        String message = binding.messageEditText.getText().toString().trim();

        long timestamp =getTimeStamp();

        if (message.isEmpty()){

            Toast.makeText(this, "Enter a message to send", Toast.LENGTH_SHORT).show();
        } else
        {
            sendMessage(MESSAGE_TYPE_TEXT,message,timestamp);
        }
    }
    private void sendMessage(String messageType, String message, long timestamp) {
        Log.d(TAG, "sendMessage: messageType: " + messageType);
        Log.d(TAG,  "sendMessage: message: "+message) ;
        Log.d(TAG,  "sendMessage: timestamp: "+timestamp) ;
//        progressDialog.setMessage("sending message...!");
//        progressDialog.show();
        DatabaseReference refChat= FirebaseDatabase.getInstance().getReference("Chats");
        String keyId = "" + refChat.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("messageId", ""+keyId);
        hashMap.put("messageType", ""+messageType) ;
        hashMap.put("message",""+message);
        hashMap.put("fromUid",""+myUid);
        hashMap.put("toUid", ""+ownerUid);
        hashMap.put("timestamp",timestamp);

        refChat.child(chatPath)
                .child(keyId)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused)
                    {
                        binding.messageEditText.setText("");
                        progressDialog.dismiss();
                        if (messageType.equals(MESSAGE_TYPE_TEXT)){
                            prepareNotification(message);
                        }
                        else {
                            prepareNotification("send photos");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(chatActivity.this, "failed to send message due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

        public static String chatPath(String ownerUid, String yourUid)
    {

        String[] arrayUids =new String[]{ownerUid,yourUid};
        Arrays.sort(arrayUids);
        String chatPath=arrayUids[0]+ "_" +arrayUids[1];
        return chatPath;

    }
    public static long getTimeStamp()
    {
        return System.currentTimeMillis();
    }

    private void prepareNotification(String message) {
        Log.d(TAG,"prepareNotification: ");

        JSONObject notificationJo = new JSONObject();
        JSONObject notificationDataJo = new JSONObject();
        JSONObject notificationNotificationJo = new JSONObject();
        try {
            notificationDataJo.put( "notificationType","" +NOTIFICATION_MESSAGE_TYPE) ;
            notificationDataJo.put("senderUid","" + firebaseAuth.getUid());

            notificationNotificationJo. put("title",""+myName);
            notificationJo.put("body",""+message);
            notificationJo.put("sound","default");
            notificationJo.put( "to",""+receiptFckToken);
            notificationJo.put("notification",notificationNotificationJo) ;
            notificationJo.put( "data",notificationDataJo) ;

        }catch (Exception e){
            Log.d(TAG,e.getMessage());
        }
        SendFcmNotification(notificationJo);
    }

    private void SendFcmNotification(JSONObject notificationJo)
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
              "https: //fcm.googleapis.com/fcm/send",
                notificationJo,
                response -> {
                    Log.d(TAG,"response :"+response);
                },
                error -> {
                    Log.d(TAG,"error :"+error);
                }
        ){
        public Map<String, String> getHeaders() throws AuthFailureError {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "“key="+service_key);

        return headers;
        }
        };
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }
}