package dandu.andrei.farmersmarket.Users;
//telescope paatter https://www.vojtechruzicka.com/avoid-telescoping-constructor-pattern/
public class User {
    private String email;
    private String fullName;
    private String password;
    private String location;
    private String street;
    private int zipCode;
    private int phoneNumber;

    public User() {
    }

    public User( String fullName,String email, String password,int zipCode,int phoneNumber, String street, String location) {

        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.zipCode = zipCode;
        this.phoneNumber = phoneNumber;
        this.street = street;
        this.location = location;

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
    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
