package chaitanya.im.collectme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import chaitanya.im.collectme.Adapters.AssetListAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView.Adapter _adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView _assetList;
    Context context = this;
    private static ArrayList<AssetListDataModel> _data = new ArrayList<>();
    Firebase myFirebaseRef = new Firebase("https://flickering-torch-8914.firebaseio.com/assets");
    private final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _assetList = (RecyclerView) findViewById(R.id.movie_posters);
        _assetList.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        _assetList.setLayoutManager(layoutManager);
        _assetList.setItemAnimator(new DefaultItemAnimator());

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
                intent.putExtra("assetID", asset.getId());
                intent.putExtra("assetName", asset.getItemName());
                intent.putExtra("assetCategory", asset.getCategory());
                context.startActivity(intent);
            }
        });
        _assetList.setAdapter(_adapter);
        myFirebaseRef.addValueEventListener(listener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_discover) {
            // Handle the camera action
        } else if (id == R.id.nav_favourites) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
