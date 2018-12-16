package dandu.andrei.farmersmarket.Ad;

import android.graphics.Bitmap;

public class AdBitmapImage {
    private Bitmap bitmap;

    public AdBitmapImage(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
