package dandu.andrei.farmersmarket.Main;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dandu.andrei.farmersmarket.R;
import dandu.andrei.farmersmarket.Users.User;

public class AccoutInfo extends Activity {
    @BindView(R.id.input_email) protected EditText inputEmail;
    @BindView(R.id.input_layout_email) protected TextInputLayout inputLayoutEmail;
    @BindView(R.id.input_full_name) protected EditText inputName;
    @BindView(R.id.input_layout_full_name) protected TextInputLayout inputLayoutName;
    @BindView(R.id.input_location) protected EditText inputLocation;
    @BindView(R.id.input_layout_location) protected TextInputLayout inputLayoutLocation;
    @BindView(R.id.input_street) protected EditText inputStreetName;
    @BindView(R.id.input_layout_street_name) protected TextInputLayout inputLayoutStreetName;
    @BindView(R.id.input_phone) protected EditText inputPhoneNumber;
    @BindView(R.id.input_layout_phone_number) protected TextInputLayout inputLayoutPhoneNumber;
    @BindView(R.id.input_zipcode) protected EditText inputZipcode;
    @BindView(R.id.input_layout_zipcode) protected TextInputLayout inputLayoutZipcode;
    protected FirebaseFirestore firebaseFirestore;
    protected FirebaseAuth auth;
    protected DocumentReference userInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_info_layout);
        ButterKnife.bind(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        getUserInfo();
    }

    private void getUserInfo() {
        userInfo = firebaseFirestore.collection("UserInfo").document(auth.getCurrentUser().getUid());
        userInfo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot result = task.getResult();
                if (result != null && result.exists()) {
                    User user = result.toObject(User.class);
                    setUserInfo(user);
                }
            }
        });
    }
    private void setUserInfo(User user) {
        inputEmail.setText(user.getEmail());
        inputName.setText(user.getFullName());
        inputLocation.setText(user.getLocation());
        inputStreetName.setText(user.getStreet());
        inputPhoneNumber.setText(user.getPhoneNumber());
        inputZipcode.setText(user.getZipCode());
    }
    @OnClick(R.id.btn_signup)
    public void updateUserInfoBtn(){
        userInfo.update("email",inputEmail.getText());
        userInfo.update("fullName",inputName.getText());
        userInfo.update("location",inputLocation.getText());
        userInfo.update("street",inputStreetName.getText());
        userInfo.update("zipCode",inputZipcode.getText());
        userInfo.update("phoneNumber",inputPhoneNumber.getText());
    }
}






















