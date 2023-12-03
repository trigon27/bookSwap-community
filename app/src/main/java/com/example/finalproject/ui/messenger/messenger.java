package com.example.finalproject.ui.messenger;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.finalproject.databinding.FragmentMessengerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class messenger extends Fragment {

    private FragmentMessengerBinding binding;
    private static final String TAG="CHATS TAG";
    private FirebaseAuth firebaseAuth;
    private String myUid;
    private Context mContext;
    private ArrayList<Modelchats> chatsArrayList;
    private Adapterchats adapterchats;

    @Override
    public void onAttach(@NonNull Context context) {
        this.mContext=context;
        super.onAttach(context);
    }

    public messenger(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding=FragmentMessengerBinding.inflate(inflater,container,false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();
        myUid=firebaseAuth.getUid();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        binding.messageListRecyclerView.setLayoutManager(layoutManager);
        loadChats();
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                try {
                    String query= s.toString();
                    adapterchats.getFilter().filter(query);

                }catch (Exception e){
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loadChats() {
        chatsArrayList=new ArrayList<>();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    String chatKey = ""+ds.getKey();
                    Log.d(TAG,"onDataChange: chatKey: "+chatKey);

                    if (chatKey.contains(myUid)) {
                        Log.d(TAG, "onDataChange: Contains");
                        Modelchats modelchats=new Modelchats();
                        modelchats.setChatKey(chatKey);
                        chatsArrayList.add(modelchats);
                    }
                    else {
                            Log.d(TAG,"onDataChange: Not Contains");
                        }
                    }
                adapterchats=new Adapterchats(mContext,chatsArrayList);
                binding.messageListRecyclerView.setAdapter(adapterchats);
//                sort();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sort() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Collections.sort(chatsArrayList, (model1, model2) -> Long.compare(model2.getTimestamp(), model1.getTimestamp()));

                adapterchats.notifyDataSetChanged();
            }
            },1060);

        }
}