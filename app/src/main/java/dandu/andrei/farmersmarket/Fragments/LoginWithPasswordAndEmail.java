package dandu.andrei.farmersmarket.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
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
import dandu.andrei.farmersmarket.MainActivity;
import dandu.andrei.farmersmarket.R;
import dandu.andrei.farmersmarket.Util;

public class LoginWithPasswordAndEmail extends AppCompatActivity {

    @BindView(R.id.input_password)
    protected EditText  inputPassword;
    @BindView(R.id.input_layout_password)
    protected TextInputLayout   inputLayoutPassword;
    @BindView(R.id.btn_signup)
    protected Button btnSignUp;
    private FirebaseAuth firebaseAuth;
    private String inputEmail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_user);
        Toast.makeText(this,"LoginWithPasswordAndEmail",Toast.LENGTH_LONG).show();
        firebaseAuth = FirebaseAuth.getInstance();
        ButterKnife.bind(this);
        inputEmail = checkForIntentData();
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

    }

    private String checkForIntentData() {
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
        String password = inputPassword.getText().toString();
        firebaseAuth.signInWithEmailAndPassword(inputEmail,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginWithPasswordAndEmail.this, "login with succes", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginWithPasswordAndEmail.this,MainActivity.class));
                }else{
                    //set error label no conection with snackbar and wrong password
                    Toast.makeText(LoginWithPasswordAndEmail.this, "Problems", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }
}