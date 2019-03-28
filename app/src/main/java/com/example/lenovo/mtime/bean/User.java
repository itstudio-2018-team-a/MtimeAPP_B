package com.example.lenovo.mtime.bean;

public class User {

    private String username;
    private String User_id;
    //private String Password;
    private String head;
    private String email;
    private String status;

    public String getUser_Name() {
        return username;
    }

    public void setUser_Name(String user_Name) {
        username = user_Name;
    }

    public String getUser_Account() {
        return User_id;
    }

    public void setUser_Account(String user_Account) {
        User_id = user_Account;
    }

    //关于密码的操作后期记得加以限制
   // public String getPassword() {
       // return Password;
    //}

    //public void setPassword(String password) {
       // Password = password;
    //}

    public String getHeadImage_url() {
        return head;
    }

    public void setHeadImage_url(String headImage_url) {
        head = headImage_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
