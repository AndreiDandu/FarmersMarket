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
//    @BindView(R.id.input_email) protected EditText inputEmail;
//    @BindView(R.id.input_layout_email) protected TextInputLayout inputLayoutEmail;
//    @BindView(R.id.input_full_name) protected EditText inputName;
//    @BindView(R.id.input_layout_full_name) protected TextInputLayout inputLayoutName;
//    @BindView(R.id.input_location) protected EditText inputLocation;
//    @BindView(R.id.input_layout_location) protected TextInputLayout inputLayoutLocation;
//    @BindView(R.id.input_street) protected EditText inputStreetName;
//    @BindView(R.id.input_layout_street_name) protected TextInputLayout inputLayoutStreetName;
//    @BindView(R.id.input_phone) protected EditText inputPhoneNumber;
//    @BindView(R.id.input_layout_phone_number) protected TextInputLayout inputLayoutPhoneNumber;
//    @BindView(R.id.input_zipcode) protected EditText inputZipcode;
//    @BindView(R.id.input_layout_zipcode) protected TextInputLayout inputLayoutZipcode;
    protected FirebaseFirestore firebaseFirestore;
    protected FirebaseAuth auth;
    protected DocumentReference userInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
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
                   // setUserInfo(user);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AccountInfo.this, "Fail to get User info " , Toast.LENGTH_LONG).show();
            }
        });
    }
//    private void setUserInfo(User user) {
//        inputEmail.setText(user.getEmail());
//        inputName.setText(user.getFullName());
//        inputLocation.setText(user.getLocation());
//        inputStreetName.setText(user.getStreet());
//        inputPhoneNumber.setText(String.valueOf(user.getPhoneNumber()));
//        inputZipcode.setText(String.valueOf(user.getZipCode()));
//    }
   // @OnClick(R.id.btn_submit_info)
//    public void updateUserInfoBtn() {
//        if (userInfo != null) {
//            userInfo.update("email", inputEmail.getText().toString());
//            userInfo.update("fullName", inputName.getText().toString());
//            userInfo.update("location", inputLocation.getText().toString());
//            userInfo.update("street", inputStreetName.getText().toString());
//            userInfo.update("zipCode",Integer.parseInt( inputZipcode.getText().toString()));
//            userInfo.update("phoneNumber", Integer.parseInt(inputPhoneNumber.getText().toString()));
//            startActivity(new Intent(AccountInfo.this,MainActivity.class));
//        }
//    }
}






















