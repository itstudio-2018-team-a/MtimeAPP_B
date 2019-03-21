package com.example.lenovo.mtime.bean;

public class User {

    private String User_Name;
    private String User_Account;
    private String Password;
    private String HeadImage_url;
    private String Email;
    private String status;

    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public String getUser_Account() {
        return User_Account;
    }

    public void setUser_Account(String user_Account) {
        User_Account = user_Account;
    }

    //关于密码的操作后期记得加以限制
    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getHeadImage_url() {
        return HeadImage_url;
    }

    public void setHeadImage_url(String headImage_url) {
        HeadImage_url = headImage_url;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
