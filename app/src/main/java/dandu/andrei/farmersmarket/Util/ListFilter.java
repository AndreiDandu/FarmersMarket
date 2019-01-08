package dandu.andrei.farmersmarket.Util;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import dandu.andrei.farmersmarket.Ad.Ad;
import dandu.andrei.farmersmarket.Ad.CustomRecycledViewAdapter;

public class ListFilter extends Filter {


        private CustomRecycledViewAdapter adapter;
        private List<Ad> filterList;

        public ListFilter(List<Ad> filterList, CustomRecycledViewAdapter adapter)
        {
            this.adapter = adapter;
            this.filterList = filterList;
        }

    //FILTERING OCURS
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        if (constraint != null && constraint.length() > 0) {

            constraint = constraint.toString().toUpperCase();
            ArrayList<Ad> filteredAds = new ArrayList<>();

            for (Ad ads : filterList) {
                if (ads.getTitle().toUpperCase().contains(constraint)) {
                    filteredAds.add(ads);
                }
        }
            results.count = filteredAds.size();
            results.values = filteredAds;
        } else {
            results.count = filterList.size();
            results.values = filterList;

        }

        return results;
    }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            adapter.listWithAds= (ArrayList<Ad>) results.values;
            adapter.notifyDataSetChanged();
        }
    }

