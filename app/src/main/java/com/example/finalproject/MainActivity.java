package com.example.finalproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.finalproject.AddBook.adapter;
import com.example.finalproject.AddBook.insert;
import com.example.finalproject.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.navigation.NavigationView;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private View nav_header;
    FirebaseAuth authProfile = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser;
    private static final String TAG = "main TAG";
    private StorageReference storageReference;
    adapter myAdapter;
    ArrayList<insert> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.finalproject.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkConnection();
        list = new ArrayList<>();
        myAdapter = new adapter(this, list);

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_addBook, R.id.nav_messenger, R.id.nav_myProfile, R.id.nav_myBooks)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation item clicks here
                int id = item.getItemId();

                if (id == R.id.nav_logout) {
                    logoutUser(); // Call the logout method
                    return true;
                }

                // Close the drawer after handling item click
                drawer.closeDrawer(GravityCompat.START);

                // Handle other navigation items
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);

                // Clear the previous fragment(s) from the back stack
                navController.popBackStack(navController.getGraph().getStartDestination(), false);

                // Navigate to the selected fragment
                navController.navigate(id);
                return true;
            }


            private void logoutUser() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Logout");
                builder.setMessage("Are you sure you want to Logout? ");
                builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                    authProfile.signOut();
                    Toast.makeText(MainActivity.this, "User Logged out", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });
                builder.show();
            }
        });


        //nav header

        firebaseUser = authProfile.getCurrentUser();
        nav_header = navigationView.getHeaderView(0);
        if (firebaseUser != null) {
//            askNotificationPermission();
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Register_Users");
            referenceProfile.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                    try {
                        storageReference = FirebaseStorage.getInstance().getReference("Display_pics");
                        StorageReference fileReference = storageReference.child(firebaseUser.getUid() + "." + "jpg");

                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            ImageView imageviewPic = nav_header.findViewById(R.id.header_img);
                            Picasso.get().load(uri)
                                    .error(R.drawable.baseline_person_off_24)
                                    .into(imageviewPic);
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error getting download URL: " + e.getMessage());
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    TextView userName = (TextView) nav_header.findViewById(R.id.header_name);
                    userName.setText(readUserDetails.full_name);

                    TextView userEmail = (TextView) nav_header.findViewById(R.id.header_email);
                    userEmail.setText(firebaseUser.getEmail());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork == null || !activeNetwork.isConnectedOrConnecting()) {
                // No internet connection
                Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }
//    private void askNotificationPermission() {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//
//            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
//                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
//            }
//        }
//    }
//
//    private ActivityResultLauncher<String> requestNotificationPermission = registerForActivityResult(
//            new ActivityResultContracts.RequestPermission(),
//            new ActivityResultCallback<Boolean>() {
//                @Override
//                public void onActivityResult(Boolean isGranted) {
//                    Log.d(TAG, "onActivityResult: Notification Permission STATUS: " + isGranted);
//
//                }
//            }
//    );

}

