package dandu.andrei.farmersmarket.Ad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dandu.andrei.farmersmarket.R;
import dandu.andrei.farmersmarket.Util.Util;
import dandu.andrei.farmersmarket.loginWithGoogle.MapsActivity;

public class CustomRecycledViewAdapter extends RecyclerView.Adapter<CustomRecycledViewAdapter.MyViewHolder> {
    private List<Ad> listWithAds;
    private Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(Ad ad);
        void onLongClick(Ad ad,int v);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.title) TextView txtTitle;
        @BindView(R.id.description_id) TextView txtDescription;
        @BindView(R.id.input_location_list) TextView txtInputLocation;
        @BindView(R.id.price_id) TextView txtPrice;
        @BindView(R.id.thumbnail) ImageView imageView;

        public MyViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this,itemView);

        }
        public void bind(final Ad ad, final OnItemClickListener listener,final int pos) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(ad);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongClick(ad,pos);
                    return false;
                }
            });
        }
    }
    public CustomRecycledViewAdapter(List<Ad> listWithAds, Context context, OnItemClickListener listener){
        this.listWithAds = listWithAds;
        this.context = context;
        this.listener =listener;

    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Ad ad = listWithAds.get(position);
        holder.bind(listWithAds.get(position),listener,position);
        List<String> uriPhoto = ad.getUriPhoto();
        if(!uriPhoto.isEmpty()){

            StorageReference url = Util.getUrl(uriPhoto.get(0));
            Glide.with(context).load(url).into(holder.imageView);
        }
        String price = context.getResources().getString(R.string.price_text, String.valueOf(ad.getPrice()));
        holder.txtInputLocation.setPaintFlags(holder.txtInputLocation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        String userLocation = Util.getUserLocation();
        holder.txtTitle.setText(ad.getTitle());
        holder.txtDescription.setText(ad.getDescription());
        if(userLocation != null) {
            holder.txtInputLocation.setText(userLocation);
        }
        holder.txtPrice.setText(price);

        holder.txtInputLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,MapsActivity.class));
                Toast.makeText(context,"Clicked on Location",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listWithAds.size();
    }

    public void delete(int pos){
        listWithAds.remove(pos);
        notifyItemRemoved(pos);
        notifyDataSetChanged();

    }
}
