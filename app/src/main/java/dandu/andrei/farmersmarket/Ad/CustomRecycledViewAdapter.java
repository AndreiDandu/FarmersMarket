package dandu.andrei.farmersmarket.Ad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dandu.andrei.farmersmarket.Main.MapsActivity;
import dandu.andrei.farmersmarket.R;
import dandu.andrei.farmersmarket.Util.ExpiringAds;
import dandu.andrei.farmersmarket.Util.ListFilter;
import dandu.andrei.farmersmarket.Util.Util;

public class CustomRecycledViewAdapter extends RecyclerView.Adapter<CustomRecycledViewAdapter.MyViewHolder> implements Filterable {
    public List<Ad> listWithAds;
    private Context context;
    private String uid;
    private final OnItemClickListener listener;
    private ListFilter  filter;
    private List<Ad> filterAds;

    public interface OnItemClickListener {
        void onLongClick(Ad ad, int v, View view,ConstraintLayout foreground);
        void onClickListener(Ad ad, int v, View view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView txtTitle;
        @BindView(R.id.description_id)
        TextView txtDescription;
        @BindView(R.id.input_location_list)
        TextView txtInputLocation;
        @BindView(R.id.price_id)
        TextView txtPrice;
        @BindView(R.id.thumbnail)
        ImageView imageView;
        @BindView(R.id.expiration_field_id)
        TextView expiration;

       public  RelativeLayout background;
       public ConstraintLayout foreground;

        public MyViewHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.view_background);
            foreground = itemView.findViewById(R.id.view_foreground);

            ButterKnife.bind(this, itemView);

        }

        public void bind(final Ad ad, final OnItemClickListener listener, final int pos, final View view, final ConstraintLayout foreground) {

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongClick(ad, pos, view,foreground);
                    return false;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickListener(ad,pos,view);
                }
            });
        }
    }

    public CustomRecycledViewAdapter(List<Ad> listWithAds, Context context,String uid, OnItemClickListener listener) {
        this.listWithAds = listWithAds;
        this.context = context;
        this.uid = uid;
        this.listener = listener;
        this.filterAds = listWithAds;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_constraint, parent, false);

        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final Ad ad = listWithAds.get(position);

        holder.bind(listWithAds.get(position), listener, position, holder.itemView,holder.foreground);
        List<String> uriPhoto = ad.getUriPhoto();

        if (!uriPhoto.isEmpty()) {
            StorageReference url = Util.getUrl(uriPhoto.get(0));

            Glide.with(context).load(url).into(holder.imageView);
        }
        else {
           Glide.with(context).load(R.drawable.no_photo_available).into(holder.imageView);
        }
        String price = context.getResources().getString(R.string.price_text, String.valueOf(ad.getPrice()));

        holder.txtInputLocation.setPaintFlags(holder.txtInputLocation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.txtInputLocation.setText(ad.getLocation());
        holder.txtTitle.setText(ad.getTitle());
        holder.txtDescription.setText(ad.getDescription());

        setExpiration(holder, ad);

        holder.txtPrice.setText(price);
        holder.itemView.setTag(ad.getId());
        holder.txtInputLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, MapsActivity.class));
                Toast.makeText(context, "Clicked on Location", Toast.LENGTH_LONG).show();
            }
        });
    }
    //TODO move to util
    private void setExpiration(@NonNull MyViewHolder holder, Ad ad) {
        int i = setExp(ad);
        if(i != -1 && i != 4) {
            if (i == 0) {
                holder.expiration.setText("Anuntul expira astazi");
            } else {
                holder.expiration.setText("Anuntul expira in " + i + " zile");
            }
        }
        if(i == -1){
            holder.expiration.setText("Anuntul este expirat");
        }
        if(i == 4){
            holder.expiration.setText("  ");
        }
    }

    @Override
    public int getItemCount() {
        return listWithAds.size();
    }

    public void delete(int pos) {
        listWithAds.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos,listWithAds.size());
    }
    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new ListFilter(filterAds,this);
        }
        return  filter;
    }

    public int setExp(Ad ad) {
        int adExpiringDate = 4;
        if (ad.getUid().equals(uid)) {
             adExpiringDate = ExpiringAds.getAdExpiringDate(ad);
        }
        return adExpiringDate;
    }
}
