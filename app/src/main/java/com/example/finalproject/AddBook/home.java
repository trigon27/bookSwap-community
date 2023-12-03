package com.example.finalproject.AddBook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.finalproject.R;
import com.example.finalproject.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class home extends Fragment {

    private FragmentHomeBinding binding;
    RecyclerView recyclerView;
    ArrayList<insert> list;
    DatabaseReference databaseReference;
    adapter myAdapter;
    Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    FirebaseAuth authProfile=FirebaseAuth.getInstance();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = requireContext();
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Call the method to refresh your data
                refreshRecyclerView();

                // Disable the refresh indicator after the refresh is complete
                swipeRefreshLayout.setRefreshing(false);
            }

            private void refreshRecyclerView()
            {
                list.clear();

                // Notify the adapter that the data set has changed
                myAdapter.notifyDataSetChanged();

                // You can also load new data here if needed
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            insert Insert = dataSnapshot.getValue(insert.class);
                            list.add(Insert);
                        }
                        myAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });
        setHasOptionsMenu(true);
        databaseReference = FirebaseDatabase.getInstance().getReference("Inserts");
        list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycleViewHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        myAdapter = new adapter(context, list);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setNestedScrollingEnabled(false);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    insert Insert = dataSnapshot.getValue(insert.class);
                    list.add(Insert);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.searchBar);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission
                filter(query);
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search query text change
                filter(newText);
                return true;
            }

            private void filter(String query) {
                ArrayList<insert> filteredList = new ArrayList<>();

                for (insert item : list) {
                    if (item.getBookTitle().toLowerCase().contains(query.toLowerCase())) {
                        filteredList.add(item);
                    }
                }

                myAdapter.setFilteredList(filteredList);
            }
        });
    }

}
