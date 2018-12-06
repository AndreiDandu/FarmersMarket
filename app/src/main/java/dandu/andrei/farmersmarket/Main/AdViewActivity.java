package dandu.andrei.farmersmarket.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dandu.andrei.farmersmarket.ListViee.Ad;
import dandu.andrei.farmersmarket.R;

public class AdViewActivity extends Activity {
    @BindView(R.id.ad_title_edit_id) protected EditText title;
    @BindView(R.id.ad_description_edit_id) protected EditText adDescription;
    @BindView(R.id.ad_quantity_edit_id) protected EditText quantity;
    @BindView(R.id.ad_price_field_id) protected EditText price;
    @BindView(R.id.ad_image_view1_id) protected ImageView image1;
    @BindView(R.id.ad_image_view2_id) protected ImageView image2;

    protected FirebaseFirestore firebaseFirestore;
    protected FirebaseAuth firebaseAuth;
    protected Ad ad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_layout);
        ButterKnife.bind(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        ad = getDataFromMainActivity();
        setValuesFromAd();
        //add textWatcher on EditText
    }

    private void setValuesFromAd() {

        if (ad != null) {
            title.setText(ad.getTitle());
            adDescription.setText(ad.getDescription());
            quantity.setText(String.valueOf(ad.getQuantity()));
            price.setText(String.valueOf(ad.getPrice()));
            Glide.with(this).load(ad.getUriPhoto()).into(image1);
        }
    }

    private Ad getDataFromMainActivity() {
        Intent intent = getIntent();
        Ad adFromMain = intent.getExtras().getParcelable("Ad");

        return  adFromMain;
    }
    @OnClick(R.id.ad_submit_btn_id)
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
