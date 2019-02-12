package dandu.andrei.farmersmarket.Ad;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import dandu.andrei.farmersmarket.R;
import dandu.andrei.farmersmarket.Util.Util;

public class AdPicsAdapter extends RecyclerView.Adapter<AdPicsAdapter.MyViewHolder> {
    private List<AdBitmapImage> adBitmapList;
    private Context context;
    private final OnItemClickListener listener;
    public interface OnItemClickListener {
        void onClickListener(AdBitmapImage ad, int v, View view);
    }

    public AdPicsAdapter(List<AdBitmapImage> bitmapList,Context context, OnItemClickListener listener){
        this.adBitmapList = bitmapList;
        this.context = context;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.ad_bitmap_view_id);
        }
        public void bind(final AdBitmapImage bitmapImage, final OnItemClickListener listener, final int pos, final View view) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickListener(bitmapImage,pos,view);
                }
            });
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
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        AdBitmapImage adBitmapImage = adBitmapList.get(position);
        holder.bind(adBitmapImage, listener, position, holder.itemView);
        if(adBitmapImage.getBitmap() != null) {
            Glide.with(context).load(adBitmapImage.getBitmap()).into(holder.imageView);
        }else if(adBitmapImage.getStringUri() != null){
            StorageReference url = Util.getUrl(adBitmapImage.getStringUri());
            Glide.with(context).load(url).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return adBitmapList.size();
    }

    public void delete(int pos) {
        adBitmapList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos,adBitmapList.size());
    }


}
