package chaitanya.im.collectme.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import chaitanya.im.collectme.Adapters.AssetListAdapter;
import chaitanya.im.collectme.DataModel.AssetListDataModel;
import chaitanya.im.collectme.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView.Adapter _adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView _assetList;
    private DrawerLayout drawer;
    private NavigationView navView;
    Context context = this;
    private static ArrayList<AssetListDataModel> _data = new ArrayList<>();
    Firebase myFirebaseRef = new Firebase("https://flickering-torch-8914.firebaseio.com/assets");
    private final String TAG = this.getClass().getName();
    FloatingActionButton fabAddNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fabAddNew = (FloatingActionButton) findViewById(R.id.addNewFab);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.nav_view);

        _assetList = (RecyclerView) findViewById(R.id.movie_posters);
        _assetList.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        _assetList.setLayoutManager(layoutManager);
        _assetList.setItemAnimator(new DefaultItemAnimator());
        _assetList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && fabAddNew.isShown())
                    fabAddNew.hide();
                else if (dy < 0 && !fabAddNew.isShown())
                    fabAddNew.show();
            }
        });

        //Firebase myFirebaseRef = this.myFirebaseRef.child("assets");

        /*data.add(new AssetListDataModel("asdf","123", "Tree", "1"));
        data.add(new AssetListDataModel("kjg","786", "Signage", "1"));
        data.add(new AssetListDataModel("ghj","098", "Meter reading", "1"));
        data.add(new AssetListDataModel("mnbv","765", "Meter reading", "1"));
        myFirebaseRef.push().setValue(data.get(0));
        myFirebaseRef.push().setValue(data.get(1));
        myFirebaseRef.push().setValue(data.get(2));
        myFirebaseRef.push().setValue(data.get(3));*/

        _adapter = new AssetListAdapter(_data, this, new AssetListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AssetListDataModel asset) {
                Intent intent = new Intent(context, AssetDetailActivity.class);
                Log.d(TAG, asset.toString());
                intent.putExtra("assetID", asset.getId());
                intent.putExtra("assetName", asset.getItemName());
                intent.putExtra("assetCategory", asset.getCategory());
                intent.putExtra("assetExtraInfo", asset.getExtraInfo());
                intent.putExtra("latitude", asset.getLatitude());
                intent.putExtra("longitude", asset.getLongitude());
                intent.putExtra("dateCreated", asset.getDateCreated());
                context.startActivity(intent);
            }
        });
        _assetList.setAdapter(_adapter);
        myFirebaseRef.addValueEventListener(listener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fabAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AssetDetailActivity.class);
                context.startActivity(intent);
            }
        });

        navView.setNavigationItemSelectedListener(this);
        navView.setCheckedItem(R.id.nav_asset_list);
    }

    protected void onResume(){
        super.onResume();
        navView.setCheckedItem(R.id.nav_asset_list);
    }

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG , "onDataChange - " + dataSnapshot.getChildrenCount() + " assets.");
            _data.clear();
            for (DataSnapshot assetSnapshot: dataSnapshot.getChildren()) {
                AssetListDataModel asset = assetSnapshot.getValue(AssetListDataModel.class);
                _data.add(asset);
            }
            _adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            Log.d(TAG, "The read failed: " + firebaseError.getMessage());
        }
    };

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if(_data.size() > 0) {
                Intent intent = new Intent(this, AssetMap.class);
                Toast.makeText(this, "Starting Asset Mapview",
                        Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "You have to first add some assets",
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_asset_list) {

        }
        if (id == R.id.nav_asset_map) {
            if(_data.size() > 0) {
                Intent intent = new Intent(this, AssetMap.class);
                drawer.closeDrawer(GravityCompat.START);
                Toast.makeText(this, "Starting Asset Mapview",
                        Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
            else {
                drawer.closeDrawer(GravityCompat.START);
                Toast.makeText(this, "You have to first add some assets",
                        Toast.LENGTH_SHORT).show();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
