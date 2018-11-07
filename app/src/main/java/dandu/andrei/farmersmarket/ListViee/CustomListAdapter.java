package dandu.andrei.farmersmarket.ListViee;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import dandu.andrei.farmersmarket.R;


public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Ad> adItems;

    public CustomListAdapter(Activity activity, List<Ad> adItems) {
        this.activity = activity;
        this.adItems = adItems;
    }

    @Override
    public int getCount() {
        return adItems.size();
    }

    @Override
    public Object getItem(int location) {
        return adItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        TextView genre = (TextView) convertView.findViewById(R.id.genre);
        TextView year = (TextView) convertView.findViewById(R.id.releaseYear);

        // getting movie data for the row
        Ad m = adItems.get(position);

        // thumbnail image

        // title
        title.setText(m.getDescription());

        // rating
        rating.setText("Rating: " + String.valueOf(m.getPrice()));


        genre.setText(m.getDescription());

        // release year
        year.setText(String.valueOf(m.getPrice()));

        return convertView;
    }

}