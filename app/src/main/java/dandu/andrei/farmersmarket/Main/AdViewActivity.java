package dandu.andrei.farmersmarket.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import dandu.andrei.farmersmarket.ListViee.Ad;
import dandu.andrei.farmersmarket.R;

class AdViewActivity extends Activity {
    @BindView(R.id.ad_title_edit_id) protected EditText title;
    @BindView(R.id.ad_description_edit_id) protected EditText adDescription;
    @BindView(R.id.ad_quantity_edit_id) protected EditText quantity;
    @BindView(R.id.ad_price_field_id) protected EditText price;
    @BindView(R.id.ad_image_view1_id) protected ImageView image1;
    @BindView(R.id.ad_image_view2_id) protected ImageView image2;

    protected FirebaseFirestore firebaseFirestore;
    protected FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_layout);
        ButterKnife.bind(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        setValuesFromAd();
        //add textWatcher on EditText
    }

    private void setValuesFromAd() {
        Ad ad = getDataFromMainActivity();
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
        Ad ad = intent.getExtras().getParcelable("Ad");

        return  ad;
    }

    public void updateAdToFireStore(){
     //   DocumentReference ads = firebaseFirestore.collection("Ads")
     //           .document(firebaseAuth.getCurrentUser().getUid()).update();

    }
}
