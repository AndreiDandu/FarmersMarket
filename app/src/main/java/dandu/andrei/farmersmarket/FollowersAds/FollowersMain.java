package dandu.andrei.farmersmarket.FollowersAds;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dandu.andrei.farmersmarket.R;
import dandu.andrei.farmersmarket.Users.User;

public class FollowersMain extends AppCompatActivity {
    private User simpleUser;
    private ArrayList<String> listWithFollowersId = new ArrayList<>();
    private ArrayList<User> listWithUsers = new ArrayList<>();
    private FollowersAdapter adapter;
    private RecyclerView recyclerViewList;
    private DocumentReference userInfo;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follower_main);
        firebaseFirestore = FirebaseFirestore.getInstance();
        String uid = getIntentData();
        getUser(uid);
    }

    public void getUser(String uid) {
        userInfo = firebaseFirestore.collection("UsersInfo").document(uid);
        userInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    createFollowedUser(user);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //  Toast.makeText(MainActivity.this, "Fail to get User info ", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void createFollowedUser(User user) {
        Map<String, Boolean> followers = user.getFollowers();
        for (Map.Entry<String, Boolean> follower : followers.entrySet()) {
            String uid = follower.getKey();
            listWithFollowersId.add(uid);
        }
        getUsers();
    }

    private void getUsers() {
        listWithUsers.clear();
        for (final String followersId : listWithFollowersId) {
            userInfo = firebaseFirestore.collection("UsersInfo").document(followersId);
            userInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);

                        user.setUid(followersId);
                        listWithUsers.add(user);
                        createListItems();
                    }
                    adapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //  Toast.makeText(MainActivity.this, "Fail to get User info ", Toast.LENGTH_LONG).show();
                }
            });

        }
    }
    private void createListItems() {
        adapter = new FollowersAdapter(listWithUsers,this);
        recyclerViewList = findViewById(R.id.list_with_followers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewList.setLayoutManager(layoutManager);
        recyclerViewList.setHasFixedSize(true);
        DividerItemDecoration div = new DividerItemDecoration(recyclerViewList.getContext(), layoutManager.getOrientation());

        recyclerViewList.addItemDecoration(div);
        recyclerViewList.setItemAnimator(new DefaultItemAnimator());
        recyclerViewList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    private String getIntentData() {
        String uid = "";
        Intent intentExtra = getIntent();
        if (intentExtra.hasExtra("uid")) {
            Bundle extras = intentExtra.getExtras();
            if (extras.containsKey("uid")) {
                uid = extras.getString("uid");
            }
        }
        return uid;
    }
}
