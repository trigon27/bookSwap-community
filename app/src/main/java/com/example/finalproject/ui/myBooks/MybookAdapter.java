package com.example.finalproject.ui.myBooks;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.AddBook.insert;
import com.example.finalproject.AddBook.singleBook;
import com.example.finalproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MybookAdapter extends RecyclerView.Adapter<MybookAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<insert> myBookList;

    public MybookAdapter(Context context, ArrayList<insert> myBookList) {
        this.mContext = context;
        this.myBookList = myBookList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mybookrow, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        insert currentBook = myBookList.get(position);
        holder.bookPrice.setText(currentBook.getBookPrice());
        holder.bookTitle.setText(currentBook.getBookTitle());
        Picasso.get().load(currentBook.getBookCoverUri()).placeholder(R.drawable.border).into(holder.showBookCover);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();

                if (adapterPosition != RecyclerView.NO_POSITION) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Confirm Deletion")
                            .setMessage("Are you sure you want to delete this book?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Delete the item from the database and notify the adapter
                                    insert bookToDelete = myBookList.get(adapterPosition);
                                    DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference("Inserts").child(bookToDelete.getKey());
                                    deleteRef.removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Remove the item from the list and notify adapter
                                                    myBookList.remove(currentBook);
                                                    notifyItemRemoved(adapterPosition);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss(); // Dismiss the dialog
                                }
                            })
                            .show();
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent =new Intent(view.getContext(), singleBook.class);
                intent.putExtra("singleBookCover",currentBook.getBookCoverUri());
                intent.putExtra("singleBookTitle",currentBook.getBookTitle());
                intent.putExtra("singleBookPrice",currentBook.getBookPrice());
                intent.putExtra("singleBookDesc",currentBook.getEditBookDesc());
                intent.putExtra("ownerId",currentBook.getOwnerId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myBookList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitle, bookPrice;
        ImageView showBookCover;
        Button deleteButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.myBookTitle);
            bookPrice = itemView.findViewById(R.id.mybookprice);
            showBookCover = itemView.findViewById(R.id.myBookCover);
            deleteButton = itemView.findViewById(R.id.deleteButton);

        }
    }
}
