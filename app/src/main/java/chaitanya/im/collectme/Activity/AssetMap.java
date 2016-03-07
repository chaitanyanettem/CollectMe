package chaitanya.im.collectme.Activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import chaitanya.im.collectme.DataModel.AssetListDataModel;
import chaitanya.im.collectme.R;

public class AssetMap extends FragmentActivity implements OnMapReadyCallback {

    private volatile GoogleMap mMap = null;
    private final String TAG = this.getClass().getName();
    Firebase assetsRef = new Firebase("https://flickering-torch-8914.firebaseio.com/assets");
    ArrayList<Marker> markers = new ArrayList<>();
    private static ArrayList<AssetListDataModel> _data = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        assetsRef.addValueEventListener(listener);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addMarkers();
    }

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG, "onDataChange - " + dataSnapshot.getChildrenCount() + " assets.");
            _data.clear();
            for (DataSnapshot assetSnapshot: dataSnapshot.getChildren()) {
                AssetListDataModel asset = assetSnapshot.getValue(AssetListDataModel.class);
                _data.add(asset);
            }
            addMarkers();
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            Log.d(TAG, "The read failed: " + firebaseError.getMessage());
        }
    };

    void addMarkers() {
        if (mMap == null)
            return;
        else if (_data.size()==0)
            return;

        mMap.clear();
        for (AssetListDataModel temp: _data) {
            if(temp.latitude>=0){
                LatLng tempPos = new LatLng(temp.getLatitude(), temp.getLongitude());
                Marker tempMarker = mMap.addMarker(new MarkerOptions().position(tempPos).title(temp.getItemName()));
                markers.add(tempMarker);
            }
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 20; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cu);
    }

}
