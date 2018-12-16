package dandu.andrei.farmersmarket.Ad;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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
    @BindView(R.id.ad_title_edit_id) protected EditText title;
    @BindView(R.id.ad_description_edit_id) protected EditText adDescription;
    @BindView(R.id.ad_quantity_edit_id) protected EditText quantity;
    @BindView(R.id.ad_price_field_id) protected EditText price;
    @BindView(R.id.ad_image_view1_id) protected ImageView image1;
    @BindView(R.id.ad_image_view2_id) protected ImageView image2;

    private ProgressDialog pDialog;
    private List<Ad> adList = new ArrayList<>();
    private List<Bitmap> bitmapList = new ArrayList<>();
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    byte[] bytes;
    private Uri uploadSessionUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_layout);

        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

    }
    @OnClick(R.id.ad_submit_btn_id)
    protected void addAd() {

        Ad ad = new Ad();
        ad.setTitle(title.getText().toString());
        ad.setDescription(adDescription.getText().toString());
        ad.setPrice(Integer.parseInt(price.getText().toString()));
        ad.setQuantity(Integer.parseInt(quantity.getText().toString()));

        if(uploadSessionUri != null){
            ad.setUriPhoto(uploadSessionUri.toString());
        }
        adList.add(ad);

        Intent i = new Intent(this,MainActivity.class);
        i.putExtra("Ad", ad);
        startActivity(i);
    }

    @OnClick(R.id.ad_pics_btn_id)
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
                        bytes  = stream.toByteArray();

                        bitmapList.add(scaled);

                    }
                    addBitmapToImageView();
                    uploadPic();
                }
            }
            break;
        }
    }

    private void addBitmapToImageView() {
        if(!bitmapList.isEmpty()){
            if(bitmapList.size() > 1) {
                image1.setImageBitmap(bitmapList.get(0));
                image2.setImageBitmap((bitmapList.get(1)));
            }else{
                image1.setImageBitmap(bitmapList.get(0));
            }
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

    public Uri uploadPic() {
        if (bytes != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            UploadTask uploadTask = ref.putBytes(bytes);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    progressDialog.dismiss();
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri result = task.getResult();
                        uploadSessionUri = result;
                    }
                }
            });
//            ref.putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onComplete(UploadTask.TaskSnapshot task) {
//                    //not good
//                    task.getMetadata().getD
//                    progressDialog.dismiss();
//                    Toast.makeText(AdActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
//                }
//            })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//                            Toast.makeText(AdActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
//                                    .getTotalByteCount());
//                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
//                        }
//                    });

        }
    return  uploadSessionUri;

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
