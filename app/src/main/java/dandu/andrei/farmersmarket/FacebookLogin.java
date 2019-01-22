package dandu.andrei.farmersmarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dandu.andrei.farmersmarket.Main.MainActivity;

public class FacebookLogin extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    LoginButton loginButton;
    CallbackManager mCallbackManager;
    String TAG ="Hey";
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_ui);
        loginButton = (LoginButton) findViewById(R.id.sign_in_with_facebook_btn1);

        mAuth = FirebaseAuth.getInstance();

        mCallbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.i(TAG,"Hello"+loginResult.getAccessToken().getToken());
                //  Toast.makeText(MainActivity.this, "Token:"+loginResult.getAccessToken(), Toast.LENGTH_SHORT).show();

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener(){


            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user!=null){
                    name = user.getDisplayName();
                    Toast.makeText(FacebookLogin.this,""+user.getDisplayName(),Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(FacebookLogin.this,"something went wrong",Toast.LENGTH_LONG).show();
                }


            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(FacebookLogin.this, "Success",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(FacebookLogin.this,MainActivity.class));
                        }else{
                            Toast.makeText(FacebookLogin.this, "Authentication error",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });
    }
}