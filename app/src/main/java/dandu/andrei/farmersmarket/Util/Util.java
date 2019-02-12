package dandu.andrei.farmersmarket.Util;


import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dandu.andrei.farmersmarket.Ad.Ad;
import dandu.andrei.farmersmarket.Users.User;

// trebuie mail validation
public class Util extends Activity{
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseFirestore fireStoreDB = FirebaseFirestore.getInstance();
    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static String TAG = Util.class.getSimpleName();
    private static DocumentReference userInfo;
    private static User users;
    public Util(){
    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    public static void checkForExistingEmail(String email){
        final boolean isEmailUsed = false;
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if(task.isSuccessful()){
                    SignInMethodQueryResult result = task.getResult();

                    if (result != null && result.getSignInMethods() != null && result.getSignInMethods().size() > 0) {
                        return ;
                    }
                }
            }
        });
    }
    public static void getUserPicture(final Context context,final ImageView img) {
       final Uri photoUrlFromProvider = auth.getCurrentUser().getPhotoUrl();
       if(photoUrlFromProvider != null) {
           Glide.with(context).load(photoUrlFromProvider).into(img);
       }
        DocumentReference userInfo = fireStoreDB.collection("UsersInfo").document(auth.getCurrentUser().getUid());
        userInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot){
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                     String profileUriPicture = user.getUriPhoto();

                     if(profileUriPicture == null && photoUrlFromProvider != null){
                         profileUriPicture = photoUrlFromProvider.toString();
                     }
                    Glide.with(context).load(profileUriPicture).into(img);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Fail to get User info " , Toast.LENGTH_LONG).show();
            }
        });
    }

    public static LiveData<String> getUserLocation() {
        final MutableLiveData<String> location = new MutableLiveData<>();

        userInfo = fireStoreDB.collection("UsersInfo").document(auth.getCurrentUser().getUid());
        userInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot){
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    location.setValue(user.getLocation());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(Util.this, "Fail to get User info " , Toast.LENGTH_LONG).show();
            }
        });
        return location;
    }
    public static LiveData<User> getUser(String uid) {
        final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        userInfo = fireStoreDB.collection("UsersInfo").document(uid);
        userInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    userMutableLiveData.setValue(user);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              //  Toast.makeText(MainActivity.this, "Fail to get User info ", Toast.LENGTH_LONG).show();
            }
        });
        return userMutableLiveData;
    }
    public static void deletePicturesFromAd(Ad ad){
        ArrayList<String> uriPhotos = ad.getUriPhoto();
        if(uriPhotos != null && uriPhotos.size() != 0) {
            for (String uriPhoto : uriPhotos) {
                StorageReference referenceFromUrl = storage.getReferenceFromUrl(uriPhoto);
                referenceFromUrl.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Photo's deleted");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       Log.d(TAG, "Fail on photo delete");
                    }
                });
            }
        }
    }
    public static void deleteAd(final Ad ad) {
        CollectionReference ads = fireStoreDB.collection("Ads");
        ads.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Ad adFromFireStore = document.toObject(Ad.class);
                        if (adFromFireStore.getTitle().equals(ad.getTitle())) {
                            DocumentReference docRef = fireStoreDB.collection("Ads").document(document.getId());
                            docRef.delete();
                            Log.d(TAG,"Ad deleted");

                        }
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Ad cannot be deleted");
            }
        });
    }

    //salvarea anuntului in baza de date
    public static void addAd(final Ad ad) {
        final String uid = auth.getCurrentUser().getUid();
        ad.setUid(uid);
        fireStoreDB.collection("Ads").add(ad).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
              Log.d(TAG, "AD save with success in DB");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Fail to save Ad in DB");
            }
        });
    }

    //Get uri for glide to load
    public static StorageReference getUrl(String uri){
        return storage.getReferenceFromUrl(uri);
    }

    public static String getTimeStamp(){
        DateFormat ISO_8601_FORMAT = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);
        return ISO_8601_FORMAT.format(new Date());
    }

    public static boolean isValidEmail(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void deletePictures(String uri) {
        if (uri != null) {
            StorageReference referenceFromUrl = storage.getReferenceFromUrl(uri);
            referenceFromUrl.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Photo's deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Fail on photo delete");
                }
            });
        }

    }
}
