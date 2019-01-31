package dandu.andrei.farmersmarket.Main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dandu.andrei.farmersmarket.R;
import dandu.andrei.farmersmarket.Users.User;

public class AccountInfo extends AppCompatActivity {
    @BindView(R.id.user_email_id) protected EditText inputEmail;
    @BindView(R.id.user_email_layout_id) protected TextInputLayout inputLayoutEmail;
    @BindView(R.id.user_full_name_id) protected EditText inputName;
    @BindView(R.id.user_full_name_layout_id) protected TextInputLayout inputLayoutName;
    @BindView(R.id.user_location_id) protected EditText inputLocation;
    @BindView(R.id.user_location_layout_id) protected TextInputLayout inputLayoutLocation;
    @BindView(R.id.user_street_name_id) protected EditText inputStreetName;
    @BindView(R.id.user_street_name_layout_id) protected TextInputLayout inputLayoutStreetName;
    @BindView(R.id.user_phone_number_id) protected EditText inputPhoneNumber;
    @BindView(R.id.user_phone_number_layout_id) protected TextInputLayout inputLayoutPhoneNumber;
    @BindView(R.id.user_zipCode_id) protected EditText inputZipcode;
    @BindView(R.id.user_zipCode_layout_id) protected TextInputLayout inputLayoutZipcode;
    @BindView(R.id.user_profile_image) protected ImageView userProfilePictureView;

    protected FirebaseFirestore firebaseFirestore;
    protected FirebaseAuth auth;
    protected DocumentReference userInfo;
    protected StorageReference storageReference;
    protected  byte[] bytes;
    protected String profilePictureUri;
    protected boolean isChanged;
    protected User users;
    protected FirebaseStorage firebaseStorage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accout_user_info_layout);
        ButterKnife.bind(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        getUserInfo();
    }
    private void getDataFromAccount(){
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser.getProviderData().size() > 3 ){
           String providerId = auth.getCurrentUser().getProviderData().get(1).getProviderId();
           if(providerId.contains("google")){
               Uri photoUrl = currentUser.getPhotoUrl();
               Glide.with(this).load(photoUrl).into(userProfilePictureView);
           }
       }
    }
    private void getUserInfo() {

        userInfo = firebaseFirestore.collection("UsersInfo").document(auth.getCurrentUser().getUid());
        userInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot){
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    setUserInfo(user);
                    users = user;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AccountInfo.this, "Fail to get User info " , Toast.LENGTH_LONG).show();
            }
        });
    }
    private void setUserInfo(User user) {

        inputEmail.setText(user.getEmail());
        inputName.setText(user.getFullName());
        inputLocation.setText(user.getLocation());
        inputStreetName.setText(user.getStreet());
        inputPhoneNumber.setText(user.getPhoneNumber());
        inputZipcode.setText(String.valueOf(user.getZipCode()));
        Glide.with(this).load(user.getUriPhoto()).into(userProfilePictureView);

    }
    @OnClick(R.id.user_submit_button_id)
    public void updateUserInfoBtn() {
        if (userInfo != null) {
            userInfo.update("email", inputEmail.getText().toString());
            userInfo.update("fullName", inputName.getText().toString());
            userInfo.update("location", inputLocation.getText().toString());
            userInfo.update("street", inputStreetName.getText().toString());
            userInfo.update("zipCode",Integer.parseInt( inputZipcode.getText().toString()));
            userInfo.update("phoneNumber", inputPhoneNumber.getText().toString());
            if(isChanged) {
                userInfo.update("uriPhoto", profilePictureUri);
                deletePreviousProfilePicture();
            }
            startActivity(new Intent(AccountInfo.this,MainActivity.class));
        }
    }

    private void deletePreviousProfilePicture() {
        String uriPhoto = users.getUriPhoto();
        if(uriPhoto != null) {
            StorageReference referenceFromUrl = firebaseStorage.getReferenceFromUrl(uriPhoto);
            if (storageReference != null) {
                referenceFromUrl.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(AccountInfo.class.getName(), "Profile picture deleted");
                    }
                });
            }
        }
    }

    @OnClick(R.id.user_upload_picture_id)
    protected void getProfilePicture(){
        Pix.start(AccountInfo.this, 100, 1);
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
                        bytes = stream.toByteArray();
                        Glide.with(this).load(scaled).into(userProfilePictureView);
                        isChanged = true;
                    }

                }
                uploadProfilePicture();
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
                    Pix.start(AccountInfo.this, 100, 1);
                } else {
                    Toast.makeText(AccountInfo.this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    public void uploadProfilePicture() {
        if (bytes != null && bytes.length != 0){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("users/" + UUID.randomUUID().toString());

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
                        if (task.isSuccessful()) {
                            Uri result = task.getResult();
                            profilePictureUri = result.toString();
                        }
                    }
                });
            }
        }

    }






















