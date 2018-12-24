package dandu.andrei.farmersmarket.Ad;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dandu.andrei.farmersmarket.R;

public class CustomRecycledViewAdapter extends RecyclerView.Adapter<CustomRecycledViewAdapter.MyViewHolder> {
    private List<Ad> listWithAds;
    private Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(Ad ad);
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
        public void bind(final Ad ad, final OnItemClickListener listener) {
            //name.setText(item.name);
            // Picasso.with(itemView.getContext()).load(item.imageUrl).into(image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(ad);
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
        holder.bind(listWithAds.get(position),listener);
        List<String> uriPhoto = ad.getUriPhoto();
        if(!uriPhoto.isEmpty()){

            Uri parse = Uri.parse(uriPhoto.get(0));
            Glide.with(context).load(parse).into(holder.imageView);
        }
        String price = context.getResources().getString(R.string.price_text, String.valueOf(ad.getPrice()));
        holder.txtInputLocation.setPaintFlags(holder.txtInputLocation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        holder.txtTitle.setText(ad.getTitle());
        holder.txtDescription.setText(ad.getDescription());
        holder.txtInputLocation.setText("Buzias");
        holder.txtPrice.setText(price);

        holder.txtInputLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Clicked on Location",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listWithAds.size();
    }


}
