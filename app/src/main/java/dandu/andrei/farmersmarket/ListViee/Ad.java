package dandu.andrei.farmersmarket.ListViee;

import android.os.Parcel;
import android.os.Parcelable;

public class Ad implements Parcelable {
    private String description;
    private int price;
    private int quantity;
    public Ad(){}
    public Ad(String description, int price, int quantity) {
        this.description = description;
        this.price = price;
        this.quantity = quantity;
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

    @Override
    public int describeContents() {
        return 0;
    }
    public Ad(Parcel in ){
        readFromParceable(in);
    }

    private void readFromParceable(Parcel in) {
        description = in.readString();
        quantity = in.readInt();
        price =in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeInt(quantity);
        dest.writeInt(price);
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
