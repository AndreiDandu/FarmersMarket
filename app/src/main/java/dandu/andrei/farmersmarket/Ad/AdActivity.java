package dandu.andrei.farmersmarket.Ad;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dandu.andrei.farmersmarket.Main.MainActivity;
import dandu.andrei.farmersmarket.R;

public class AdActivity extends AppCompatActivity {
    private static final String TAG = AdActivity.class.getSimpleName();
    @BindView(R.id.ad_title_id_text) protected EditText title;
    @BindView(R.id.ad_description_id_text) protected EditText adDescription;
    @BindView(R.id.ad_quantity_id_text) protected EditText quantity;
    @BindView(R.id.ad_price_id_text) protected EditText price;

    private ProgressDialog pDialog;
    private List<AdBitmapImage> bitmapList = new ArrayList<>();
    private ArrayList<String> uriList = new ArrayList<>();
    private List<byte[]> listOfBytes = new ArrayList<>();
    FirebaseStorage storage;
    StorageReference storageReference;

    private AdPicsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_activity_layout_with_recycle);
        setAdapter();

        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

    }
    public void setAdapter(){
        RecyclerView recyclerView = findViewById(R.id.ad_recyclerView);
        adapter = new AdPicsAdapter(bitmapList,getApplicationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.ad_submitBtn_id)
    protected void addAd() {
        Ad ad = new Ad();
        ad.setTitle(title.getText().toString());
        ad.setDescription(adDescription.getText().toString());
        ad.setPrice(Integer.parseInt(price.getText().toString()));
        ad.setQuantity(Integer.parseInt(quantity.getText().toString()));

        if(!uriList.isEmpty()){
            ad.setUriPhoto(uriList);
        }
        Intent i = new Intent(this,MainActivity.class);
        i.putExtra("Ad", ad);
        startActivity(i);
    }

    @OnClick(R.id.ad_addPics_btn_id)
    protected void addPicsWithPix() {
        Pix.start(AdActivity.this, 100, 5);
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

                    }
                    uploadPic();
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
                    Pix.start(AdActivity.this, 100, 5);
                } else {
                    Toast.makeText(AdActivity.this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
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

            final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            for (byte[] bytess : listOfBytes) {
                UploadTask uploadTask = ref.putBytes(bytess);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        progressDialog.dismiss();
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri result = task.getResult();
                            uriList.add(result.toString());
                        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}