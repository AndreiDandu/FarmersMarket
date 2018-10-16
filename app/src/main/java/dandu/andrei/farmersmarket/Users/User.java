package dandu.andrei.farmersmarket.Users;
//telescope paatter https://www.vojtechruzicka.com/avoid-telescoping-constructor-pattern/
public class User {
    private String email;
    private String fullName;
    private String password;
    private String location;
    private String address;
    private int zipCode;

    public User(String email, String fullName, String password) {
        this.email = email;
        this.fullName = fullName;
        this.password = password;
    }



    public User(String email, String fullName, String password, String location, String address, int zipCode) {
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.location = location;
        this.address = address;
        this.zipCode = zipCode;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }


}
