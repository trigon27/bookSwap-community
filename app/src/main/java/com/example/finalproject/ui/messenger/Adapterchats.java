package com.example.finalproject.ui.messenger;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalproject.R;
import com.example.finalproject.ReadWriteUserDetails;
import com.example.finalproject.databinding.RowChatsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Adapterchats extends RecyclerView.Adapter<Adapterchats.HolderChats> implements Filterable
{
    RowChatsBinding binding;
    FirebaseAuth firebaseAuth;
    private String myUid;
    private StorageReference storageReference;
    private static final String TAG="Adapter_chats_tag";
    Context context;
    public ArrayList<Modelchats> chatsArrayList;
    private FilterChats filter;
    private ArrayList<Modelchats> filterList;

    public Adapterchats(Context context, ArrayList<Modelchats> chatsArrayList) {
        this.context = context;
        this.chatsArrayList = chatsArrayList;
        this.filterList=chatsArrayList;
        firebaseAuth=FirebaseAuth.getInstance();
        myUid=firebaseAuth.getUid();
    }

    @NonNull
    @Override
    public HolderChats onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=RowChatsBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderChats(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderChats holder, int position) {
    Modelchats modelchats=chatsArrayList.get(position);
    loadLastMessage(modelchats,holder);
    //open chat box
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String ownerUid= modelchats.ownerUid;
            if (ownerUid!=null) {
                Intent intent = new Intent(context, chatActivity.class);
                intent.putExtra("ownerUid", ownerUid);
                context.startActivity(intent);
            }
        }
    });
    }

    private void loadLastMessage(Modelchats modelchats, HolderChats holder) {
        String chatKey = modelchats.getChatKey();
        Log.d(TAG, "loadLastMessage: chatKey: " + chatKey);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.child(chatKey).orderByChild("timestamp").limitToLast(1) // Order by timestamp and limit to the last message
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Retrieve the last message
                            DataSnapshot lastMessageSnapshot = snapshot.getChildren().iterator().next();

                            String fromUid = "" + lastMessageSnapshot.child("fromUid").getValue();
                            String message = "" + lastMessageSnapshot.child("message").getValue();
                            String messageId = "" + lastMessageSnapshot.child("messageId").getValue();
                            String messageType = "" + lastMessageSnapshot.child("messageType").getValue();
                            long timestamp = (Long) lastMessageSnapshot.child("timestamp").getValue();
                            String toUid = "" + lastMessageSnapshot.child("toUid").getValue();

                            String formattedDate = formatTimestampDate(timestamp);

                            modelchats.setMessage(message);
                            modelchats.setMessageId(messageId);
                            modelchats.setMessageType(messageType);
                            modelchats.setFromUid(fromUid);
                            modelchats.setTimestamp(timestamp);
                            modelchats.setToUid(toUid);

                            holder.chatlistDate.setText(formattedDate);

                            if (messageType.equals(chatActivity.MESSAGE_TYPE_TEXT)) {
                                holder.ChatlistLastmessage.setText(message);
                            } else {
                                holder.ChatlistLastmessage.setText("send Attachment");
                            }

                            loadOwnerDetails(modelchats, holder);
                        } else {
                            // No messages in this chat
                            holder.ChatlistLastmessage.setText("No messages yet.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle onCancelled
                    }
                });
    }


    private void loadOwnerDetails(Modelchats modelchats, HolderChats holder)
    {
        String fromId=modelchats.getFromUid();
        String toId=modelchats.getToUid();

        String ownerUid;
        if (fromId.equals(myUid)){
            ownerUid=toId;
        }
        else {
            ownerUid=fromId;
        }
        modelchats.setOwnerUid(ownerUid);
        Log.d(TAG,"from uid"+fromId);
        Log.d(TAG,"my uid"+toId);
        Log.d(TAG,"owner uid"+ownerUid);

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Register_Users");
        ref.child(ownerUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                        String Name = readUserDetails.full_name;
                        modelchats.setName(Name);
                        storageReference = FirebaseStorage.getInstance().getReference("Display_pics");
                        StorageReference fileReference=storageReference.child(ownerUid+"."+"jpg");

                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri profileUrl=uri;
                                modelchats.setProfileImageUrl(String.valueOf(profileUrl));
                                try {
                                    Glide.with(context)
                                            .load(profileUrl)
                                            .placeholder(R.drawable.baseline_person_24)
                                            .into(holder.chatListProfilePic);

                                }catch (Exception e){
                                    Toast.makeText(context, "failed to load image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        holder.nameTv.setText(Name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return chatsArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter=new FilterChats(this,filterList);
        }
        return filter;
    }

    class HolderChats extends RecyclerView.ViewHolder{
        ImageView chatListProfilePic;
        TextView nameTv,ChatlistLastmessage,chatlistDate;
       public HolderChats(@NonNull View itemView) {
           super(itemView);
           chatListProfilePic=itemView.findViewById(R.id.chatListprofilepic);
           nameTv=itemView.findViewById(R.id.nameTv);
           ChatlistLastmessage=itemView.findViewById(R.id.ChatlistLastmessage);
           chatlistDate=itemView.findViewById(R.id.chatlistDate);
       }
   }
    public static String formatTimestampDate(Long timestamp) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);

        String date = DateFormat.format("dd/MM/yyyy hh:mm:a", calendar).toString();

        return date;

    }
}
