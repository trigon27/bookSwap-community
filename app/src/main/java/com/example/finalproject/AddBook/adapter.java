package com.example.finalproject.AddBook;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class adapter extends RecyclerView.Adapter<adapter.MyviewHolder> {
   Context Acontext;
    ArrayList<insert> list;

    public void setFilteredList(ArrayList<insert> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }
    public adapter(Context context, ArrayList<insert> list) {
        this.Acontext=context;
        this.list = list;
    }


    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        return new MyviewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {
        insert Insert=list.get(position);
        holder.bookPrice.setText(Insert.getBookPrice());
        holder.bookTitle.setText(Insert.getBookTitle());
        Picasso.get().load(Insert.getBookCoverUri()).placeholder(R.drawable.border).into(holder.showBookCover);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent =new Intent(view.getContext(), singleBook.class);
                intent.putExtra("singleBookCover",Insert.getBookCoverUri());
                intent.putExtra("singleBookTitle",Insert.getBookTitle());
                intent.putExtra("singleBookPrice",Insert.getBookPrice());
                intent.putExtra("singleBookDesc",Insert.getEditBookDesc());
                intent.putExtra("ownerId",Insert.getOwnerId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  static class MyviewHolder extends RecyclerView.ViewHolder {
        TextView bookTitle,bookPrice;
        ImageView showBookCover;
        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle=itemView.findViewById(R.id.bookTitle);
            bookPrice=itemView.findViewById(R.id.bookPrice);
            showBookCover=itemView.findViewById(R.id.bookCover);
            }


    }
    }
