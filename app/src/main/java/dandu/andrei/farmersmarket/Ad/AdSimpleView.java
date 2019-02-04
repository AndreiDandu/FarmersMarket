package dandu.andrei.farmersmarket.Ad;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dandu.andrei.farmersmarket.R;

public class AdSimpleView extends AppCompatActivity {
    @BindView(R.id.ad_title_id_simple) protected EditText title;
    @BindView(R.id.ad_description_id_simple) protected EditText adDescription;
    @BindView(R.id.ad_quantity_id_simple) protected EditText quantity;
    @BindView(R.id.ad_price_id_simple) protected EditText price;
    @BindView(R.id.ad_view_date) protected TextView date;

    protected List<AdBitmapImage> bitmapList = new ArrayList<>();
    protected AdPicsAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_view);
        ButterKnife.bind(this);
        Ad dataFromMainActivity = getDataFromMainActivity();
        setEditTextNonEditable();
        setInfoFromAd(dataFromMainActivity);


    }
    private void setEditTextNonEditable() {
            title.setEnabled(false);
            adDescription.setEnabled(false);
            quantity.setEnabled(false);
            price.setEnabled(false);
        }


    private void setInfoFromAd(Ad dataFromMainActivity) {
        if(dataFromMainActivity != null){
            title.setText(dataFromMainActivity.getTitle());
            adDescription.setText(dataFromMainActivity.getDescription());
            quantity.setText(String.valueOf(dataFromMainActivity.getQuantity()));
            price.setText(dataFromMainActivity.getPrice());
            date.setText("Adaugat in data de: \n" + dataFromMainActivity.getTimestamp());
            ArrayList<String> uriPhotos = dataFromMainActivity.getUriPhoto();
            for (String uriPhoto : uriPhotos) {
                AdBitmapImage img = new AdBitmapImage(uriPhoto);
                bitmapList.add(img);
            }
            setAdapter();
        }

    }
    public void setAdapter(){
        RecyclerView recyclerView = findViewById(R.id.ad_activity_recyclerView_id_simple);
        adapter = new AdPicsAdapter(bitmapList,getApplicationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        BitmapOffsetDecoration itemDecoration = new BitmapOffsetDecoration(getApplication().getBaseContext(),R.dimen.picture_offset);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
    }
    private Ad getDataFromMainActivity() {
        Intent intent = getIntent();
        Ad adFromMain = intent.getExtras().getParcelable("Ad");

        return  adFromMain;
    }
}
