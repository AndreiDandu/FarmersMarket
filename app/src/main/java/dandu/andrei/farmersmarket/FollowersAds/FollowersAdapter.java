package dandu.andrei.farmersmarket.FollowersAds;

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
import dandu.andrei.farmersmarket.Users.User;
import dandu.andrei.farmersmarket.Util.Util;
import dandu.andrei.farmersmarket.Main.MapsActivity;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.MyViewHolder> {
    public List<User> listWithUser;
    private Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.follower_name_id)
        TextView txtFollowerName;
        @BindView(R.id.follower_phone_number_id)
        TextView getTxtFollowerPhone;
        @BindView(R.id.follower_location_id)
        TextView txtFollowerLocation;
        @BindView(R.id.follower_picture_id)
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    public FollowersAdapter(List<User> listWithUser, Context context) {
        this.listWithUser = listWithUser;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_with_followers, parent, false);

        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder,final int position) {
        User user = listWithUser.get(position);

        String uriPhoto = user.getUriPhoto();
        //todo delay mare
        if (uriPhoto != null && !uriPhoto.equals("")) {
            StorageReference url = Util.getUrl(uriPhoto);

            Glide.with(context).load(url).into(holder.imageView);
        }
        holder.txtFollowerLocation.setPaintFlags(holder.txtFollowerLocation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.txtFollowerLocation.setText(user.getLocation());
        holder.txtFollowerName.setText("Nume: "+ user.getFullName());
        holder.getTxtFollowerPhone.setText("Telefon " + user.getPhoneNumber());
        holder.txtFollowerLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, MapsActivity.class));
                Toast.makeText(context, "Clicked on Location", Toast.LENGTH_LONG).show();
            }
        });
        //TODO check tag problem
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user1 = listWithUser.get(position);
                String uid = user1.getUid();
                Intent intent = new  Intent(context,FollowersAds.class);
                intent.putExtra("uid", uid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listWithUser.size();
    }

    public void delete(int pos) {
        listWithUser.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, listWithUser.size());
    }
}