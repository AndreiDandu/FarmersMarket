package dandu.andrei.farmersmarket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import dandu.andrei.farmersmarket.HandleLogin.CheckEmailInDataBase;
import dandu.andrei.farmersmarket.loginWithGoogle.SignedIn;

public class LoginStartScreen extends Activity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_start_screen);
        ButterKnife.bind(LoginStartScreen.this);
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
}
