package dandu.andrei.farmersmarket.Main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import dandu.andrei.farmersmarket.Ad.Ad;
import dandu.andrei.farmersmarket.Ad.AdActivity;
import dandu.andrei.farmersmarket.Ad.CustomRecycledViewAdapter;
import dandu.andrei.farmersmarket.R;
import dandu.andrei.farmersmarket.Users.User;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth auth;
    private FirebaseFirestore fireStoreDB;
    private ArrayList<Ad> adList = new ArrayList<>();
    private RecyclerView listView;
    private CustomRecycledViewAdapter adapter;
    protected DocumentReference userInfo;
    protected String profileUriPicture;
    protected FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AdActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String email = currentUser.getEmail();
        fireStoreDB = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        setListItems();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        setUserInNavDrawer(navigationView,email);
        getUserPicture();
        navigationView.setNavigationItemSelectedListener(this);

    }
    //TODO move to Util
    private void getUserPicture() {
        userInfo = fireStoreDB.collection("UsersInfo").document(auth.getCurrentUser().getUid());
        userInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot){
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    profileUriPicture = user.getUriPhoto();
                    ImageView viewById1 = (ImageView) findViewById(R.id.profile_picture_main_activity);
                    Glide.with(MainActivity.this).load(profileUriPicture).into(viewById1);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Fail to get User info " , Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setListItems(){
        getAllAds();
        getAd();
        listView =  findViewById(R.id.list);
        onClickAndLongClickItems();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);
        listView.setHasFixedSize(true);
        DividerItemDecoration div = new DividerItemDecoration(listView.getContext(),layoutManager.getOrientation());
        listView.addItemDecoration(div);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
    protected void onClickAndLongClickItems(){

        adapter = new CustomRecycledViewAdapter(adList, this, new CustomRecycledViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Ad ad) {
                Intent intent = new Intent(MainActivity.this,AdViewActivity.class);
                intent.putExtra("Ad",ad);
                startActivity(intent);

                Toast.makeText(getBaseContext(),"Clicked on ad",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(final Ad ad,final int pos) {
                if (ad.getUid().equals(auth.getCurrentUser().getUid())) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    deleteAd(ad);
                                    deletePicturesFromAd(ad);
                                    adapter.delete(pos);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };


                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                }
            }
        });

    }
    public void deletePicturesFromAd(Ad ad){
        ArrayList<String> uriPhotos = ad.getUriPhoto();
        if(uriPhotos != null && uriPhotos.size() != 0) {
            for (String uriPhoto : uriPhotos) {
                StorageReference referenceFromUrl = firebaseStorage.getReferenceFromUrl(uriPhoto);
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
    public void deleteAd(final Ad ad) {
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
                            Toast.makeText(MainActivity.this, "Ad deleted", Toast.LENGTH_LONG).show();

                        }
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Ad cannot be deleted", Toast.LENGTH_LONG).show();
            }
        });
    }
    private Ad getAd() {
        Intent i = getIntent();
        Ad ad = i.getExtras().getParcelable("Ad");

        if (ad != null) {
            addAd(ad);
        }
        return ad;
    }

    public void addAd(final Ad ad) {
            final String uid = auth.getCurrentUser().getUid();
            ad.setUid(uid);
            fireStoreDB.collection("Ads").add(ad).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(MainActivity.this, "AD save with succes in DB", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Fail to save Ad in DB", Toast.LENGTH_SHORT).show();
                }
            });
    }

    public void getAllAds() {
        adList.clear();
        CollectionReference ads = fireStoreDB.collection("Ads");
        ads.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    Ad ad = documentSnapshot.toObject(Ad.class);
                    adList.add(ad);
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Fail to read ads from DB",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void setUserInNavDrawer(NavigationView navigationView, String email){
        View headerView = navigationView.getHeaderView(0);
        TextView viewById = (TextView) headerView.findViewById(R.id.textView);

        viewById.setText(email);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.sign_out) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    startActivity(new Intent(MainActivity.this,LoginStartScreen.class));
                    finish();
                }
            });
            return true;
        }
        if(id == R.id.account_settings){
            startActivity(new Intent(MainActivity.this,AccountInfo.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_new_ad) {
            getAllAds();
        } else if (id == R.id.nav_my_ads) {
            getMyAds();
        } else if (id == R.id.nav_following_ad) {

        } else if (id == R.id.nav_account_info) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void getMyAds() {
        adList.clear();
        CollectionReference ads = fireStoreDB.collection("Ads");
        ads.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Ad ad = documentSnapshot.toObject(Ad.class);
                    if (ad.getUid().equals(auth.getCurrentUser().getUid())) {

                        adList.add(ad);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Fail to read ads from DB",Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
