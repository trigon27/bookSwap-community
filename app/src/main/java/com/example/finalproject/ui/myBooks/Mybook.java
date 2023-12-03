package com.example.finalproject.ui.myBooks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.finalproject.AddBook.insert;
import com.example.finalproject.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Mybook extends Fragment  {

    private RecyclerView recyclerView;
    private ArrayList<insert> myBookList;
    private MybookAdapter myBookAdapter;
    DatabaseReference databaseReference;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mybook, container, false);

        recyclerView = root.findViewById(R.id.recycleViewMyBook);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        databaseReference = FirebaseDatabase.getInstance().getReference("Inserts");
        myBookList = new ArrayList<>();
        myBookAdapter = new MybookAdapter(requireContext(), myBookList);
        recyclerView.setAdapter(myBookAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        swipeRefreshLayout = root.findViewById(R.id.myBookswipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Call the method to refresh your data
                loadData();
                // Disable the refresh indicator after the refresh is complete
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        loadData();

        return root;
    }
    private void loadData(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myBookList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    insert Insert = dataSnapshot.getValue(insert.class);
                    if (Insert.getOwnerId() != null && Insert.getOwnerId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        myBookList.add(Insert);
                    }
                }

                myBookAdapter.notifyDataSetChanged();
                if (myBookList.isEmpty()) {
                    showAddBooksSnackbar();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }
    private void showAddBooksSnackbar() {
        View view = getView();
        if (view != null) {
            Snackbar.make(view, "No Books to show", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        }
    }
}
