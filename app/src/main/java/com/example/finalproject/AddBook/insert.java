package com.example.finalproject.AddBook;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class insert{
    private String bookTitle;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String key;
    private String bookPrice;
    private  String editBookDesc;
    private String bookCoverUri;


    private String ownerId;

    public String getBookCoverUri() {
        return bookCoverUri;
    }

    public void setBookCoverUri(String bookCoverUri) {
        this.bookCoverUri = bookCoverUri;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public void setBookPrice(String bookPrice) {
        this.bookPrice = bookPrice;
    }

    public void setEditBookDesc(String editBookDesc) {
        this.editBookDesc = editBookDesc;
    }


    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    //    public insert(String bookTitle, String bookPrice, String editBookDesc) {
//        this.bookTitle = bookTitle;
//        this.bookPrice = bookPrice;
//        this.editBookDesc=editBookDesc;
//    }
    public insert(){

    }

    public String getEditBookDesc() {
        return editBookDesc;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookPrice() {
        return bookPrice;
    }
}
