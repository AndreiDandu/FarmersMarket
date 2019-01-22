package dandu.andrei.farmersmarket.HandleLogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dandu.andrei.farmersmarket.Main.MainActivity;
import dandu.andrei.farmersmarket.R;
import dandu.andrei.farmersmarket.Users.User;
import dandu.andrei.farmersmarket.Validators.EmailValidator;
import dandu.andrei.farmersmarket.Validators.PasswordValidator;

public class SignInWithUserInfo extends Activity {

    @BindView(R.id.input_email) protected EditText inputEmail;
    @BindView(R.id.input_layout_email) protected TextInputLayout inputLayoutEmail;
    @BindView(R.id.input_password) protected EditText inputPassword;
    @BindView(R.id.input_layout_password) protected TextInputLayout inputLayoutPassword;
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

    @BindView(R.id.btn_signup) protected Button btnSignUp;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_layout);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        Toast.makeText(this,"SignInWithEmailUSerNameAndPassword",Toast.LENGTH_LONG).show();
        ButterKnife.bind(this);
        inputEmail.addTextChangedListener(new EmailValidator(inputEmail,inputLayoutEmail));
        inputPassword.addTextChangedListener(new PasswordValidator(inputPassword,inputLayoutPassword));
        //TODO: check again for email
    }
    public User getUser(){
        int zipcode= Integer.parseInt(inputZipcode.getText().toString());
        //TODO make string from phonenumber
        String phoneNumber = inputPhoneNumber.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String fullName = inputName.getText().toString();
        String streetName = inputStreetName.getText().toString();
        String location = inputLocation.getText().toString();

        User user = new User(fullName,email,password,zipcode,phoneNumber,streetName,location);

        return user;
    }
    public void addUser() {
        User user = getUser();
        String uid = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore.collection("UsersInfo").document(uid).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SignInWithUserInfo.this, "User save with succes in DB", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignInWithUserInfo.this, "Fail to save user in DB", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.btn_signup)
    protected void createUser() {
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(SignInWithUserInfo.this, "User created with succes", Toast.LENGTH_LONG).show();
                        //make intent for MainActivity and send user and email
                        addUser();
                        startActivity(new Intent(SignInWithUserInfo.this,MainActivity.class));

                    } else {
                        Toast.makeText(SignInWithUserInfo.this, "User creation failed", Toast.LENGTH_LONG).show();
                    }
                }
            });

    }
}
