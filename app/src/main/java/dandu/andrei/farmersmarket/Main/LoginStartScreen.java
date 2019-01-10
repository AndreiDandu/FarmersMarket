package dandu.andrei.farmersmarket.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;
import butterknife.OnClick;
import dandu.andrei.farmersmarket.HandleLogin.CheckEmailInDataBase;
import dandu.andrei.farmersmarket.R;
import dandu.andrei.farmersmarket.Util.Util;
import dandu.andrei.farmersmarket.loginWithGoogle.SignedIn;

public class LoginStartScreen extends Activity {
   FirebaseAuth firebaseAuth;
   ConstraintLayout constraintLayout;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_start_screen);

        ButterKnife.bind(LoginStartScreen.this);
        firebaseAuth = FirebaseAuth.getInstance();
        constraintLayout = findViewById(R.id.constraint_layout_id);

    }
    @OnClick(R.id.sign_in_with_email_btn)
    public void onClickEmailButton(){
        startActivity(new Intent(LoginStartScreen.this, CheckEmailInDataBase.class));

    }
    @OnClick(R.id.sign_in_with_google_btn)
    public void onClickGoogleButton(){
        startActivity(new Intent(LoginStartScreen.this, SignedIn.class));
    }
    @OnClick(R.id.sign_in_with_facebook_btn)
    public void onClickFacebookButton(){
        Toast.makeText(this, "Not working momentarily!", Toast.LENGTH_SHORT).show();
    }
    //TODO: To be deleted
    @OnClick(R.id.skip_btn_id)
    public void onClickSkipButton() {
        if (Util.isNetworkAvailable(this)) {
            firebaseAuth.signInWithEmailAndPassword("testSkipUser1@gmail.com", "icecube01").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginStartScreen.this, "Login succesful", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LoginStartScreen.this, MainActivity.class));
                    } else {
                        Toast.makeText(LoginStartScreen.this, "Fail", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }else{
            final Snackbar make = Snackbar.make(constraintLayout, "No internet conection!", Snackbar.LENGTH_LONG);
            make.setAction("DISMISS", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    make.dismiss();
                }
            });
            make.show();
        }
    }

}
