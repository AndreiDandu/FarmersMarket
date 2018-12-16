package dandu.andrei.farmersmarket.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import dandu.andrei.farmersmarket.Ad.Ad;
import dandu.andrei.farmersmarket.Ad.AdActivity;
import dandu.andrei.farmersmarket.Ad.CustomListAdapter;
import dandu.andrei.farmersmarket.R;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth auth;
    private FirebaseFirestore fireStoreDB;
    private ArrayList<Ad> adList = new ArrayList<Ad>();
    private ListView listView;
    private CustomListAdapter adapter;
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


        setListItems();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        setUserInNavDrawer(navigationView,email);
        navigationView.setNavigationItemSelectedListener(this);

    }
    public void setListItems(){
        getAllAds();
        getAd();
        listView =  findViewById(R.id.list);
        adapter = new CustomListAdapter(adList,getApplicationContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object listItem = listView.getItemAtPosition(i);
                Ad ad = (Ad) listItem;

                Intent intent = new Intent(MainActivity.this,AdViewActivity.class);
                intent.putExtra("Ad",ad);
                startActivity(intent);

                Toast.makeText(getBaseContext(),"o",Toast.LENGTH_SHORT).show();
            }
        });


    }
    private Ad getAd() {
        Intent i = getIntent();
        Ad ad = i.getExtras().getParcelable("Ad");

        if (ad != null) {
            //adList.add(ad); always add form database
            addAd(ad);
        }
        return ad;
    }
    //mutate in alta clasa
    public void addAd(final Ad ad) {
            final String uid = auth.getCurrentUser().getUid();
            ad.setUid(uid);
            fireStoreDB.collection("Ads").add(ad).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(MainActivity.this, "AD save with succes in DB", Toast.LENGTH_SHORT).show();

                adList.add(ad);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Fail to save Ad in DB", Toast.LENGTH_SHORT).show();
                }
            });
    }

    public void getAllAds() {
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
        // Inflate the menu; this adds items to the action bar if it is present.
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

        if (id == R.id.nav_camera) {
            startActivity(new Intent(MainActivity.this,UploadPicture.class));
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
