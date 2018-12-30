package dandu.andrei.farmersmarket.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dandu.andrei.farmersmarket.Ad.Ad;
import dandu.andrei.farmersmarket.Ad.AdBitmapImage;
import dandu.andrei.farmersmarket.Ad.AdPicsAdapter;
import dandu.andrei.farmersmarket.Ad.BitmapOffsetDecoration;
import dandu.andrei.farmersmarket.R;

public class AdViewActivity extends Activity {
    @BindView(R.id.ad_view_activity_title_id) protected EditText title;
    @BindView(R.id.ad_view_activity_description_id) protected EditText adDescription;
    @BindView(R.id.ad_view_activity_quantity_id) protected EditText quantity;
    @BindView(R.id.ad_view_activity_price_id) protected EditText price;

    protected FirebaseFirestore firebaseFirestore;
    protected FirebaseAuth firebaseAuth;
    protected Ad ad;
    protected List<AdBitmapImage> bitmapList = new ArrayList<>();
    protected AdPicsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_layout);
        ButterKnife.bind(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        ad = getDataFromMainActivity();
        setEditTextNonEditable();
        setValuesFromAd();
        //add textWatcher on EditText
        //TODO add delete and ad picture button function
    }
//if is not user's ad then dont make it editable
    private void setEditTextNonEditable() {
        if(!ad.getUid().equals(firebaseAuth.getCurrentUser().getUid())){
            title.setEnabled(false);
            adDescription.setEnabled(false);
            quantity.setEnabled(false);
            price.setEnabled(false);
        }
    }

    private void setValuesFromAd() {

        if (ad != null) {
            title.setText(ad.getTitle());
            adDescription.setText(ad.getDescription());
            quantity.setText(String.valueOf(ad.getQuantity()));
            price.setText(String.valueOf(ad.getPrice()));
            ArrayList<String> uriPhotos = ad.getUriPhoto();
            for (String uriPhoto:uriPhotos) {
                AdBitmapImage img = new AdBitmapImage(uriPhoto);
                bitmapList.add(img);
            }
            setAdapter();
        }
    }
    public void setAdapter(){
        RecyclerView recyclerView = findViewById(R.id.ad_recyclerView);
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
    @OnClick(R.id.ad_view_submit_btn_id)
    public void updateAdToFireStore() {
        CollectionReference ads = firebaseFirestore.collection("Ads");
        ads.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Ad adFromFireStore = document.toObject(Ad.class);
                        if (adFromFireStore.getTitle().equals(ad.getTitle())) {
                            DocumentReference docRef = firebaseFirestore.collection("Ads").document(document.getId());

                            docRef.update("description", adDescription.getText().toString());
                            docRef.update("price", Integer.parseInt(price.getText().toString()));
                            docRef.update("quantity", Integer.parseInt(quantity.getText().toString()));
                            docRef.update("title", title.getText().toString());

                            Toast.makeText(AdViewActivity.this,"Ad updated",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(AdViewActivity.this,MainActivity.class));
                        }
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdViewActivity.this,"Error",Toast.LENGTH_LONG).show();
            }
        });


    }
}
