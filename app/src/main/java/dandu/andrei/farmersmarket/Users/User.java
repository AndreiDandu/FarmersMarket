package dandu.andrei.farmersmarket.Users;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String email;
    private String fullName;
    private String password;
    private String location;
    private String street;
    private int zipCode;
    private String phoneNumber;
    private String uriPhoto;
    private Map<String,Boolean> followers = new HashMap<>();
    private String uid;

    public User() {
    }

    public User( String fullName,String email, String password,int zipCode,String phoneNumber, String street, String location) {

        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.zipCode = zipCode;
        this.phoneNumber = phoneNumber;
        this.street = street;
        this.location = location;
    }
    public void setUid(String uid){
        this.uid = uid;
    }
    public String getUid(){
        return uid;
    }
    public Map<String, Boolean> getFollowers() {
        return followers;
    }

    public void setFollowers(Map<String, Boolean> followers) {
        this.followers = followers;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getUriPhoto() {
        return uriPhoto;
    }

    public void setUriPhoto(String uriPhoto) {
        this.uriPhoto = uriPhoto;
    }

}
