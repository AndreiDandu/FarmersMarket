package dandu.andrei.farmersmarket.Main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import dandu.andrei.farmersmarket.Ad.Ad;
import dandu.andrei.farmersmarket.R;
import dandu.andrei.farmersmarket.Users.User;
import dandu.andrei.farmersmarket.Util.ExpiringAds;

public class UserInfoAds extends AppCompatActivity {
    @BindView(R.id.user_info_ads_email_id) protected EditText inputEmail;
    @BindView(R.id.user_info_ads_full_name_id) protected EditText inputName;
    @BindView(R.id.user_info_ads_location_id) protected EditText inputLocation;
    @BindView(R.id.user_info_ads_street_id) protected EditText inputStreetName;
    @BindView(R.id.user_info_ads_phone_id) protected EditText inputPhoneNumber;
    @BindView(R.id.user_info_ads_zipcode_id) protected EditText inputZipcode;
    @BindView(R.id.user_info_ads_publications_id) protected TextView publication;
    @BindView(R.id.user_info_ads_exp_id) protected TextView adsExp;
    @BindView(R.id.user_info_ads_following_id) protected TextView followings;
    @BindView(R.id.user_info_ads_followed_id) protected TextView followed;
    @BindView(R.id.user_info_ads_image_id) protected ImageView userProfilePictureView;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    protected DocumentReference userInfo;
    private ArrayList<Ad> myAdsList = new ArrayList<>();
    private List<String> followedList = new ArrayList<>();
    private ArrayList<Ad> expiringAds = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_ads);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        ButterKnife.bind(this);
        getUserInfo();
        getAllUsers();
        getMyAds();
    }

    private void setFollowedByUSers() {
        if(followedList != null){
            followed.setText("Urmaritori : " + followedList.size());
        }
    }


    private void getUserInfo() {

        userInfo = firebaseFirestore.collection("UsersInfo").document(firebaseAuth.getCurrentUser().getUid());
        userInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot){
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    setUserInfo(user);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserInfoAds.this, "Fail to get User info " , Toast.LENGTH_LONG).show();
            }
        });
    }
    private void setUserInfo(User user) {

        inputEmail.setText(user.getEmail());
        inputName.setText(user.getFullName());
        inputLocation.setText(user.getLocation());
        inputStreetName.setText(user.getStreet());
        inputPhoneNumber.setText(user.getPhoneNumber());
        inputZipcode.setText(String.valueOf(user.getZipCode()));
        int followers = getFollowers(user);
        followings.setText("Vanzatori urmariti: "+ followers);
        if(!user.getUriPhoto().isEmpty()) {
            Glide.with(this).load(user.getUriPhoto()).into(userProfilePictureView);
        }else{
            Glide.with(this).load(R.drawable.no_photo_available).into(userProfilePictureView);
        }


    }
    private int getFollowers(User user){
        Map<String, Boolean> followers = user.getFollowers();
       return followers.size();

    }
    public void getMyAds() {
        CollectionReference ads = firebaseFirestore.collection("Ads");
        ads.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Ad ad = documentSnapshot.toObject(Ad.class);
                    if (ad.getUid().equals(firebaseAuth.getCurrentUser().getUid())) {

                        myAdsList.add(ad);

                    }
                }
               setFieldText(myAdsList);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserInfoAds.this, "Fail to read ads from DB", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setFieldText(ArrayList<Ad> myAdsList) {
        expiringAds.clear();
        if(myAdsList !=null){
            publication.setText("Anunturi publicate: "+ myAdsList.size());
        }
        expiringAds = ExpiringAds.getExpiringAds(myAdsList);
        adsExp.setText("Anunturi expirate: " + expiringAds.size());
    }
    private void getAllUsers() {
       CollectionReference users = firebaseFirestore.collection("UsersInfo");
        users.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                    User user = document.toObject(User.class);
                    Map<String, Boolean> followers = user.getFollowers();
                    getFollower(followers);
                }
            }

        });

    }

    private void getFollower(Map<String, Boolean> followers) {
        for (Map.Entry<String, Boolean> values : followers.entrySet()) {
            String userID = values.getKey();
            if (userID.equals(firebaseAuth.getCurrentUser().getUid())) {
                followedList.add(userID);
                setFollowedByUSers();
            }
        }
    }

}
