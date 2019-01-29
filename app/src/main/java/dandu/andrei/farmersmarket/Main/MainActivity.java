package dandu.andrei.farmersmarket.Main;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dandu.andrei.farmersmarket.Ad.Ad;
import dandu.andrei.farmersmarket.Ad.AdActivity;
import dandu.andrei.farmersmarket.Ad.AdSimpleView;
import dandu.andrei.farmersmarket.Ad.CustomRecycledViewAdapter;
import dandu.andrei.farmersmarket.FollowersAds.FollowersMain;
import dandu.andrei.farmersmarket.Util.MyFirebaseMessagingService;
import dandu.andrei.farmersmarket.R;
import dandu.andrei.farmersmarket.Util.RecyclerItemTouchHelper;
import dandu.andrei.farmersmarket.Util.Util;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth auth;
    private FirebaseFirestore fireStoreDB;
    private ArrayList<Ad> adList = new ArrayList<>();
    private RecyclerView recyclerViewList;
    private CustomRecycledViewAdapter adapter;
    protected FirebaseStorage firebaseStorage;
    private ActionMode actionMode;
    private Map<Ad, Integer> mapWithAdAndPos = new HashMap<>();
    private List<View> listWithViews = new ArrayList<>();
    private SearchView searchView;
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
                startActivity(new Intent(MainActivity.this, AdActivity.class));
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
        setUserInNavDrawer(navigationView, email);

        navigationView.setNavigationItemSelectedListener(this);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        MyFirebaseMessagingService.sendRegistrationToServer(token);
                    }
                });
    }

    public void setListItems() {
        getAllAds();
        getAd();

        onClickAndLongClickItems();
        //folosirea listei in acitivitatea principala
        recyclerViewList = findViewById(R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewList.setLayoutManager(layoutManager);
        recyclerViewList.setHasFixedSize(true);
        DividerItemDecoration div = new DividerItemDecoration(recyclerViewList.getContext(), layoutManager.getOrientation());

        recyclerViewList.addItemDecoration(div);
        recyclerViewList.setItemAnimator(new DefaultItemAnimator());
        recyclerViewList.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this, MainActivity.this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerViewList);
        //TODO de verificat ca ii de doua ori
        adapter.notifyDataSetChanged();


    }


    protected void onClickAndLongClickItems() {
        adapter = new CustomRecycledViewAdapter(adList, this, this, new CustomRecycledViewAdapter.OnItemClickListener() {
            @Override
            public void onLongClick(final Ad ad, final int pos, final View view) {
                if (ad.getUid().equals(auth.getCurrentUser().getUid())) {
                    if (actionMode == null) {
                        actionMode = startActionMode(modelCallBack);
                    }
                    view.setSelected(true);
                    ad.setSelected(true);
                    if (mapWithAdAndPos.containsKey(ad)) {
                        view.setSelected(false);
                        mapWithAdAndPos.remove(ad);
                    } else {
                        mapWithAdAndPos.put(ad, pos);
                    }
                    listWithViews.add(view);
                }
            }

            @Override
            public void onClickListener(Ad ad, int v, View view) {
                if (!ad.getUid().equals(auth.getCurrentUser().getUid())) {
                    Toast.makeText(MainActivity.this,"Clicked on other user ads",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this , AdSimpleView.class);
                    intent.putExtra("Ad",ad);
                    startActivity(intent);
                }
            }
        });
    }

    private Ad getAd() {
        Intent i = getIntent();
        Ad ad = i.getExtras().getParcelable("Ad");

        if (ad != null) {
            Util.addAd(ad);
        }
        return ad;
    }

    public void getAllAds() {
        adList.clear();
        CollectionReference ads = fireStoreDB.collection("Ads");
        ads.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Ad ad = documentSnapshot.toObject(Ad.class);
                    ad.setId(documentSnapshot.getId());
                    adList.add(ad);
                }
                //TODO
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Fail to read ads from DB", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUserInNavDrawer(NavigationView navigationView, String email) {
        View headerView = navigationView.getHeaderView(0);
        TextView viewById = (TextView) headerView.findViewById(R.id.textView);
        ImageView viewById1 = headerView.findViewById(R.id.profile_picture_main_activity);
        Util.getUserPicture(MainActivity.this, viewById1);
        viewById.setText(email);
        MenuItem item = navigationView.getMenu().findItem(R.id.nav_following_ad);
        item.setActionView(R.layout.menu_dot);
        updateCounter(34);
    }
    private void updateCounter(int count) {
        ((TextView) findViewById(R.id.tv_nav_drawer_count)).setText("23");
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });
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
                    startActivity(new Intent(MainActivity.this, LoginStartScreen.class));
                    finish();
                }
            });
            return true;
        }
        if (id == R.id.account_settings) {
            startActivity(new Intent(MainActivity.this, AccountInfo.class));
        }
        if (id == R.id.action_search) {
            return true;
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
            startActivityToFollowersMain();
        } else if (id == R.id.nav_account_info) {
            //TODO new activity cu datele uitlizatorului plus cate anunturi are puse
        } else if (id == R.id.nav_other_ad) {
            getOthersAds();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startActivityToFollowersMain() {
        Intent intent = new Intent(MainActivity.this,FollowersMain.class);
        intent.putExtra("uid",auth.getCurrentUser().getUid());
        startActivity(intent);
    }

    public void getMyAds() {
        adList.clear();
        CollectionReference ads = fireStoreDB.collection("Ads");
        ads.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
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
                Toast.makeText(MainActivity.this, "Fail to read ads from DB", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void getOthersAds() {
        adList.clear();
        CollectionReference ads = fireStoreDB.collection("Ads");
        ads.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Ad ad = documentSnapshot.toObject(Ad.class);
                    if (!ad.getUid().equals(auth.getCurrentUser().getUid())) {

                        adList.add(ad);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Fail to read ads from DB", Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private ActionMode.Callback modelCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("Options");
            mode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int itemId = item.getItemId();
            switch (itemId) {
                case R.id.delete:
                    for (Map.Entry<Ad, Integer> adAndPosition : mapWithAdAndPos.entrySet()) {
                        Util.deleteAd(adAndPosition.getKey());
                        Util.deletePicturesFromAd(adAndPosition.getKey());
                        adapter.delete(adAndPosition.getValue());
                        // modelCallBack.onDestroyActionMode(mode);
                    }

                    break;
                case R.id.edit:
                    if (!mapWithAdAndPos.isEmpty() && mapWithAdAndPos.size() < 2) {
                        Intent intent = new Intent(MainActivity.this, AdViewActivity.class);
                        for (Map.Entry<Ad, Integer> adAndPosition : mapWithAdAndPos.entrySet()) {
                            intent.putExtra("Ad", adAndPosition.getKey());
                        }
                        startActivity(intent);
                    }
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            if (!listWithViews.isEmpty()) {
                for (View view : listWithViews) {
                    view.setSelected(false);
                }
            }
            mapWithAdAndPos.clear();
            Log.d(TAG, "Action mode destroyed");
        }
    };

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CustomRecycledViewAdapter.MyViewHolder) {
            if (direction == 4) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("0765524844"));
                // startActivity(intent);
                adapter.notifyItemChanged(position);

            }
            if (direction == 8) {
                Toast.makeText(this, "Follow user " + " position" + position, Toast.LENGTH_LONG).show();
                String provider = adList.get(position).getUid();
                String currentUser = auth.getCurrentUser().getUid();
                if (!currentUser.equals(provider)) {
                    fireStoreDB.collection("UsersInfo").document(currentUser).update("followers." + provider, true);
                    //createFollowedUser();
                }
                adapter.notifyItemChanged(position);
                //salvat id-ul... la user-ul care trebuie urmarit
            }

        }
    }


}