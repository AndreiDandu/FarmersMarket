package dandu.andrei.farmersmarket.Ad;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import dandu.andrei.farmersmarket.R;

public class AdPicsAdapter extends RecyclerView.Adapter<AdPicsAdapter.MyViewHolder> {
    private List<AdBitmapImage> adBitmapList;
    Context context;

    public AdPicsAdapter(List<AdBitmapImage> bitmapList,Context context){
        this.adBitmapList = bitmapList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.ad_bitmap_view_id);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ad_bitmap_layout, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AdBitmapImage adBitmapImage = adBitmapList.get(position);
        if(adBitmapImage.getBitmap() != null) {
            Glide.with(context).load(adBitmapImage.getBitmap()).into(holder.imageView);
        }else if(adBitmapImage.getStringUri() != null){
            //TODO Make interface for firebasestorage
            FirebaseStorage storage;
            storage = FirebaseStorage.getInstance();
            StorageReference referenceFromUrl = storage.getReferenceFromUrl(adBitmapImage.getStringUri());
            Glide.with(context).load(referenceFromUrl).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return adBitmapList.size();
    }




}
