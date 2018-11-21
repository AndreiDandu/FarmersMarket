package dandu.andrei.farmersmarket.ListViee;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dandu.andrei.farmersmarket.R;


public class CustomListAdapter extends ArrayAdapter<Ad> {
    private Context context;
    private LayoutInflater inflater;
    private List<Ad> adItems;
    private ImageView view;

    public CustomListAdapter( ArrayList<Ad> adItems ,Context context) {
        super(context,R.layout.list_row,adItems);
        this.context = context;
        this.adItems = adItems;
    }
    private static class ViewHolder{
       TextView txtTitle;
       TextView txtDescription;
       TextView txtInputLocation;
       TextView txtPrice;
       ImageView imageView;

    }
    @Override
    public int getCount() {
        return adItems.size();
    }

    @Override
    public Ad getItem(int location) {
        return adItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Ad ad = adItems.get(position);
        ViewHolder viewHolder ;
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_row, parent,false);

            viewHolder.txtTitle = convertView.findViewById(R.id.title);
            viewHolder.txtDescription = convertView.findViewById(R.id.description_id);
            viewHolder.txtInputLocation = convertView.findViewById(R.id.input_location_list);
            viewHolder.txtPrice = convertView.findViewById(R.id.price_id);
            viewHolder.imageView = convertView.findViewById(R.id.thumbnail);
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String uriPhoto = ad.getUriPhoto();
        if(uriPhoto != null){
            Uri parse = Uri.parse(uriPhoto);
            Glide.with(getContext()).load(parse).into(viewHolder.imageView);
        }

        viewHolder.txtTitle.setText(ad.getTitle());
        viewHolder.txtDescription.setText(ad.getDescription());
        viewHolder.txtInputLocation.setText(String.valueOf(ad.getQuantity()));
        viewHolder.txtPrice.setText(String.valueOf(ad.getPrice()));

        return convertView;
    }

}