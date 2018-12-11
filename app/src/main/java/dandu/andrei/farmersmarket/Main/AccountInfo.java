package dandu.andrei.farmersmarket.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dandu.andrei.farmersmarket.R;
import dandu.andrei.farmersmarket.Users.User;

public class AccountInfo extends Activity {
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
    protected FirebaseFirestore firebaseFirestore;
    protected FirebaseAuth auth;
    protected DocumentReference userInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accout_user_info_layout);
        ButterKnife.bind(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        getUserInfo();
    }

    private void getUserInfo() {
        userInfo = firebaseFirestore.collection("UsersInfo").document(auth.getCurrentUser().getUid());
        userInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot){
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    setUserInfo(user);
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
        inputPhoneNumber.setText(String.valueOf(user.getPhoneNumber()));
        inputZipcode.setText(String.valueOf(user.getZipCode()));
    }
    @OnClick(R.id.user_submit_button_id)
    public void updateUserInfoBtn() {
        if (userInfo != null) {
            userInfo.update("email", inputEmail.getText().toString());
            userInfo.update("fullName", inputName.getText().toString());
            userInfo.update("location", inputLocation.getText().toString());
            userInfo.update("street", inputStreetName.getText().toString());
            userInfo.update("zipCode",Integer.parseInt( inputZipcode.getText().toString()));
            userInfo.update("phoneNumber", Integer.parseInt(inputPhoneNumber.getText().toString()));
            startActivity(new Intent(AccountInfo.this,MainActivity.class));
        }
    }
    @OnClick(R.id.user_profile_image)
    public void uploadProfilePicture(){

    }
}






















