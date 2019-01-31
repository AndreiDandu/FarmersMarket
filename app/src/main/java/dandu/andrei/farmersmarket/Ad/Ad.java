package dandu.andrei.farmersmarket.Ad;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Ad implements Parcelable {


    private String id;
    private String uid;
    private String title;
    private String description;
    private int price;
    private int quantity;
    private boolean isSelected;
    private ArrayList<String> uriPhoto =  new ArrayList<>();
    private String location;



    private String timestamp;
    public Ad(){}

    public Ad(String title, String description, int price, int quantity) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSelected(boolean isSelected){
        this.isSelected = isSelected;
    }
    public boolean getIsSelected(){
        return isSelected;
    }
    public ArrayList<String> getUriPhoto() {
        return uriPhoto;
    }

    public void setUriPhoto(ArrayList<String> uriPhoto) {
        this.uriPhoto = uriPhoto;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public Ad(Parcel in ){
        readFromParceable(in);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private void readFromParceable(Parcel in) {
        in.readStringList(uriPhoto);
        uid = in.readString();
        title = in.readString();
        description = in.readString();
        quantity = in.readInt();
        price =in.readInt();
        location = in.readString();
        timestamp=in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(uriPhoto);
        dest.writeString(uid);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(quantity);
        dest.writeInt(price);
        dest.writeString(location);
        dest.writeString(timestamp);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){

        @Override
        public Ad createFromParcel(Parcel in) {
            return new Ad(in);
        }

        @Override
        public Ad[] newArray(int size) {
            return new Ad[size];
        }
    };
}
