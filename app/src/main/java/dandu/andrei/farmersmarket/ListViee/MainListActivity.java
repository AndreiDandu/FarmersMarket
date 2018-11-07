package dandu.andrei.farmersmarket.ListViee;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dandu.andrei.farmersmarket.Main.MainActivity;
import dandu.andrei.farmersmarket.R;

public class MainListActivity extends AppCompatActivity {
    private static final String TAG = MainListActivity.class.getSimpleName();

    @BindView(R.id.description_field_id) protected EditText adDescription;
    @BindView(R.id.quantity_field_id) protected EditText quantity;
    @BindView(R.id.price_field_id) protected EditText price;
    @BindView(R.id.image_view_ad_id) protected ImageView image;

    private ProgressDialog pDialog;
    private List<Ad> adList = new ArrayList<>();
    private ListView listView;
    private CustomListAdapter adapter;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_layout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(this);
        recyclerView.setAdapter(myAdapter);

        ButterKnife.bind(this);


    }
    @OnClick(R.id.submit_ad_btn_id)
    protected void addAd() {

        Ad ad = new Ad();
        ad.setDescription("sdasdas");
        ad.setPrice(Integer.parseInt("12"));
        ad.setQuantity(Integer.parseInt("1"));
        adList.add(ad);
        myAdapter.notifyDataSetChanged();
        Intent i = new Intent(this,MainActivity.class);
        i.putExtra("Ad", (Serializable) ad);
        startActivity(i);
    //update pics here
    }
    @OnClick(R.id.add_picture_btn)
    protected void addPicsWithPix(){
        Pix.start(MainListActivity.this,100,1);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (100): {
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    myAdapter.addImage(returnValue);
                    /*for (String s : returnValue) {
                        Log.e("val", " ->  " + s);
                    }*/
                }
            }
            break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(MainListActivity.this, 100, 5);
                } else {
                    Toast.makeText(MainListActivity.this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
