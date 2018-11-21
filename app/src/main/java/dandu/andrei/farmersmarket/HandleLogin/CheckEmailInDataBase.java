package dandu.andrei.farmersmarket.HandleLogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dandu.andrei.farmersmarket.R;

public class CheckEmailInDataBase extends Activity {

    private FirebaseAuth firebaseAuth;
    @BindView(R.id.input_email)
    protected EditText email_field;
    @BindView(R.id.input_layout_email)
    protected TextInputLayout email_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_checker);
        firebaseAuth = FirebaseAuth.getInstance();
        Toast.makeText(this,"CheckEmailInDataBase",Toast.LENGTH_LONG).show();
        ButterKnife.bind(this);
        email_field.addTextChangedListener(new MyTextWatcher(email_field,email_layout));
    }

    @OnClick(R.id.next_button)
    public void onClickNextButton() {

            final String email = email_field.getText().toString();
            firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(this, new OnCompleteListener<SignInMethodQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                    if (task.isSuccessful()) {
                        SignInMethodQueryResult result = task.getResult();

                        if (result != null && result.getSignInMethods() != null && result.getSignInMethods().size() > 0) {
                            Intent intentExtra = new Intent(CheckEmailInDataBase.this,LoginWithPasswordAndEmail.class);
                            intentExtra.putExtra("email", email);
                            startActivity(intentExtra);
                        } else {
                            email_layout.setError("Email not found");
                            Toast.makeText(CheckEmailInDataBase.this, "Mail not found --Try again---", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CheckEmailInDataBase.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                }
            });
    }

    @OnClick(R.id.sign_in_button_email_checker)
    public void onClickCreateUser() {
        startActivity(new Intent(CheckEmailInDataBase.this,SignInWithEmailUserNameAndPassword.class));
    }

}

