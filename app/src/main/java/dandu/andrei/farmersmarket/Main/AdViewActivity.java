package dandu.andrei.farmersmarket.Main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dandu.andrei.farmersmarket.Ad.Ad;
import dandu.andrei.farmersmarket.Ad.AdActivity;
import dandu.andrei.farmersmarket.Ad.AdBitmapImage;
import dandu.andrei.farmersmarket.Ad.AdPicsAdapter;
import dandu.andrei.farmersmarket.Ad.BitmapOffsetDecoration;
import dandu.andrei.farmersmarket.R;
import dandu.andrei.farmersmarket.Util.Util;

public class AdViewActivity extends AppCompatActivity {
    @BindView(R.id.ad_view_activity_title_id) protected EditText title;
    @BindView(R.id.ad_view_activity_description_id) protected EditText adDescription;
    @BindView(R.id.ad_view_activity_quantity_id) protected EditText quantity;
    @BindView(R.id.ad_view_activity_price_id) protected EditText price;
    private ProgressDialog pDialog;
    protected FirebaseFirestore firebaseFirestore;
    protected FirebaseAuth firebaseAuth;
    protected Ad ad;
    protected List<AdBitmapImage> bitmapList = new ArrayList<>();
    protected Map<AdBitmapImage,Integer> listWithSelectedBitmap = new HashMap<>();
    protected AdPicsAdapter adapter;
    private List<byte[]> listOfBytes = new ArrayList<>();
    private ArrayList<String> uriPhoto;
    FirebaseStorage storage;
    StorageReference storageReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_layout);
        ButterKnife.bind(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        ad = getDataFromMainActivity();
        uriPhoto = ad.getUriPhoto();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
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
            quantity.setText(ad.getQuantity());
            price.setText(String.valueOf(ad.getPrice()));
            ArrayList<String> uriPhotos = ad.getUriPhoto();
            for (String uriPhoto : uriPhotos) {
                AdBitmapImage img = new AdBitmapImage(uriPhoto);
                bitmapList.add(img);
            }
            setAdapter();
        }
    }
    public void setAdapter(){
        RecyclerView recyclerView = findViewById(R.id.ad_recyclerView);
        adapter = new AdPicsAdapter(bitmapList, getApplicationContext(), new AdPicsAdapter.OnItemClickListener() {
            @Override
            public void onClickListener(AdBitmapImage bitmapImage, int v, View view) {
                Toast.makeText(AdViewActivity.this,"Image Clicked",Toast.LENGTH_LONG).show();

                if(listWithSelectedBitmap.containsKey(bitmapImage)){
                    view.setBackgroundColor(Color.TRANSPARENT);
                    listWithSelectedBitmap.remove(bitmapImage);
                }else{
                    view.setBackgroundColor(Color.GRAY);
                    listWithSelectedBitmap.put(bitmapImage,v);
                }

            }
        });
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
        uploadPic();
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
                            docRef.update("quantity", quantity.getText().toString());
                            docRef.update("title", title.getText().toString());
                            docRef.update("uriPhoto",getLastPictures());
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

    @OnClick(R.id.delete_pics)
    public void deletePics(){
        for (Map.Entry<AdBitmapImage, Integer> imageAndPos : listWithSelectedBitmap.entrySet()) {
            String stringUri = imageAndPos.getKey().getStringUri();
            Util.deletePictures(stringUri);
            adapter.delete(imageAndPos.getValue());

        }

    }
    private ArrayList<String> getLastPictures(){
        for (Map.Entry<AdBitmapImage, Integer> imageAndPos : listWithSelectedBitmap.entrySet()) {
            uriPhoto.remove(imageAndPos.getKey().getStringUri());
        }
        return uriPhoto;
    }
    @OnClick(R.id.ad_addPics_btn_id)
    protected void addPicsWithPix() {
        Pix.start(AdViewActivity.this, 100, 5);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (100): {
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    for (String returnV : returnValue) {
                        File f = new File(returnV);
                        Bitmap d = new BitmapDrawable(this.getResources(), f.getAbsolutePath()).getBitmap();

                        Bitmap scaled = com.fxn.utility.Utility.getScaledBitmap(512, d);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        scaled.compress(Bitmap.CompressFormat.JPEG, 80 ,stream);
                        listOfBytes.add(stream.toByteArray());
                        AdBitmapImage bitmapImage = new AdBitmapImage(scaled);
                        bitmapList.add(bitmapImage);
                        adapter.notifyDataSetChanged();

                    }

                }
            }
            break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(AdViewActivity.this, 100, 5);
                } else {
                    Toast.makeText(AdViewActivity.this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void uploadPic() {
        if (!listOfBytes.isEmpty()) {
            adapter.notifyDataSetChanged();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            for (byte[] bytess : listOfBytes) {
                final StorageReference filePath = storageReference.child("images/" + UUID.randomUUID().toString());
                uriPhoto.add(filePath.toString());
                UploadTask uploadTask = filePath.putBytes(bytess);
                uploadTask
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(AdViewActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(AdViewActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
