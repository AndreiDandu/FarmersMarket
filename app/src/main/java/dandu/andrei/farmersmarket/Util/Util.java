package dandu.andrei.farmersmarket.Util;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import dandu.andrei.farmersmarket.Main.MainActivity;
import dandu.andrei.farmersmarket.Users.User;

// trebuie mail validation
public class Util extends Activity{
    private FirebaseAuth auth;
    private FirebaseFirestore fireStoreDB;
    protected DocumentReference userInfo;
    protected String userLocation;
    public Util(){
        auth = FirebaseAuth.getInstance();
        fireStoreDB = FirebaseFirestore.getInstance();
    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    public static void checkForExistingEmail(FirebaseAuth firebaseAuth,String email){
        final boolean isEmailUsed = false;
        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if(task.isSuccessful()){
                    SignInMethodQueryResult result = task.getResult();

                    if (result != null && result.getSignInMethods() != null && result.getSignInMethods().size() > 0) {
                        return;
                    } else {

                    }
                }
            }
        });
    }

    public void getUserLocation() {
        userInfo = fireStoreDB.collection("UsersInfo").document(auth.getCurrentUser().getUid());
        userInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot){
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    userLocation = user.getLocation();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Util.this, "Fail to get User info " , Toast.LENGTH_LONG).show();
            }
        });
    }
    public String getLocation(){
        getUserLocation();
        return  userLocation;
    }
}
