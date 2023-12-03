package com.example.finalproject;

public class ReadWriteUserDetails {
    public String full_name;
    public String email;
    public String phone_no;

    public ReadWriteUserDetails()
    {
    }


    public ReadWriteUserDetails(String full_name, String email, String phone_no) {
        this.full_name= full_name;
        this.phone_no=phone_no;
        this.email=email;

    }
}
