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
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import dandu.andrei.farmersmarket.Ad.Ad;
import dandu.andrei.farmersmarket.Ad.CustomRecycledViewAdapter;
import dandu.andrei.farmersmarket.R;

public class FollowersAds extends AppCompatActivity  {
    private ArrayList<Ad> adList = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private String uid;
    private RecyclerView recyclerViewList;
    private CustomRecycledViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follower_main);

        firebaseFirestore = FirebaseFirestore.getInstance();
        uid = getIntentData();
        setListItems();
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

    private void getFollowerAds() {
        adList.clear();
        CollectionReference ads = firebaseFirestore.collection("Ads");
        ads.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Ad ad = documentSnapshot.toObject(Ad.class);
                    if (ad.getUid().equals(uid)) {

                        adList.add(ad);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FollowersAds.this, "Fail to read ads from DB", Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void onClickAndLongClickItems() {
        adapter = new CustomRecycledViewAdapter(adList, this, this, new CustomRecycledViewAdapter.OnItemClickListener() {
            @Override
            public void onLongClick(final Ad ad, final int pos, final View view) {
//                if (ad.getUid().equals(auth.getCurrentUser().getUid())) {
//                    if (actionMode == null) {
//                        actionMode = startActionMode(modelCallBack);
//                    }
//                    view.setSelected(true);
//                    ad.setSelected(true);
//                    if (mapWithAdAndPos.containsKey(ad)) {
//                        view.setSelected(false);
//                        mapWithAdAndPos.remove(ad);
//                    } else {
//                        mapWithAdAndPos.put(ad, pos);
//                    }
//                    listWithViews.add(view);
//                }
            }

            @Override
            public void onClickListener(Ad ad, int v, View view) {

            }
        });
    }

    public void setListItems() {
        getFollowerAds();
        onClickAndLongClickItems();
        recyclerViewList = findViewById(R.id.list_with_followers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(FollowersAds.this);
        recyclerViewList.setLayoutManager(layoutManager);
        recyclerViewList.setHasFixedSize(true);
        DividerItemDecoration div = new DividerItemDecoration(recyclerViewList.getContext(), layoutManager.getOrientation());

        recyclerViewList.addItemDecoration(div);
        recyclerViewList.setItemAnimator(new DefaultItemAnimator());
        recyclerViewList.setAdapter(adapter);
        //TODO de verificat ca ii de doua ori
        adapter.notifyDataSetChanged();

    }
}