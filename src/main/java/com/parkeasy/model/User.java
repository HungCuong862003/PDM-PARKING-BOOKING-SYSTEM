package main.java.com.parkeasy.model;


public class User {
    private int userID;
    private String UserName;
    private String phone;
    private String email;
    private String password;


    public User(int userID,String phone, String UserName, String email, String password) {
        this.userID = userID;
        this.UserName = UserName;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public int getuserID() {
        return userID;
    }

    public void setuserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
