package dandu.andrei.farmersmarket.Ad;

import android.graphics.Bitmap;

public class AdBitmapImage {
    private Bitmap bitmap;
    private String uri;

    public AdBitmapImage(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    public AdBitmapImage(String uri){
        this.uri = uri;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getStringUri(){
        return uri;
    }
    public void setStringUri(String uri){
        this.uri =uri;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
