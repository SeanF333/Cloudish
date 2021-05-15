package Else;

import java.io.Serializable;

public class User implements Serializable {

    private String id, email, fullname, imageurl, phone, isprivate, username;
    public Boolean isSelected;

    public User(String id, String email, String fullname, String imageurl, String phone, String isprivate, String username) {
        this.id = id;
        this.email = email;
        this.fullname = fullname;
        this.imageurl = imageurl;
        this.phone = phone;
        this.isprivate = isprivate;
        this.username = username;
        this.isSelected = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIsprivate() {
        return isprivate;
    }

    public void setIsprivate(String isprivate) {
        this.isprivate = isprivate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
