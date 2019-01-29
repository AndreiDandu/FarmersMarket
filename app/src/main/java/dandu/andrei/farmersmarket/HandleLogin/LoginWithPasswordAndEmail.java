package dandu.andrei.farmersmarket.HandleLogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dandu.andrei.farmersmarket.Main.MainActivity;
import dandu.andrei.farmersmarket.R;
import dandu.andrei.farmersmarket.Validators.PasswordValidator;


public class LoginWithPasswordAndEmail extends AppCompatActivity {

    @BindView(R.id.input_password)
    protected EditText  inputPassword;
    @BindView(R.id.input_layout_password)
    protected TextInputLayout  inputLayoutPassword;
    @BindView(R.id.btn_signup)
    protected Button btnSignUp;
    @BindView(R.id.btn_reset_password)
    protected Button btnResetPassword;
    private FirebaseAuth firebaseAuth;
    private String inputEmail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_user);
        Toast.makeText(this,"LoginWithPasswordAndEmail",Toast.LENGTH_LONG).show();
        firebaseAuth = FirebaseAuth.getInstance();
        ButterKnife.bind(this);
        inputEmail = getIntentData();
        inputPassword.requestFocus();
        inputPassword.addTextChangedListener(new PasswordValidator(inputPassword,inputLayoutPassword));

    }

    private String getIntentData() {
        String email = "";
        Intent intentExtra = getIntent();
        if (intentExtra.hasExtra("email")) {
            Bundle extras = intentExtra.getExtras();
            if (extras.containsKey("email")) {
                email = extras.getString("email");
            }
        }
        return email;
    }

    @OnClick(R.id.btn_signup)
    protected void loginUser() {
        if (inputPassword.getText() != null && !inputPassword.getText().toString().equals("")) {
            String password = inputPassword.getText().toString();
            firebaseAuth.signInWithEmailAndPassword(inputEmail, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginWithPasswordAndEmail.this, "Login with success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginWithPasswordAndEmail.this, MainActivity.class));
                    } else {

                        Toast.makeText(LoginWithPasswordAndEmail.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    @OnClick(R.id.btn_reset_password)
    protected void resetPasswordWithEmail(){

        firebaseAuth.sendPasswordResetEmail(inputEmail).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginWithPasswordAndEmail.this,"Email was sent",Toast.LENGTH_LONG).show();
                }
            }

        });
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

}