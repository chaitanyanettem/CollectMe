package chaitanya.im.collectme;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class AssetDetailActivity extends AppCompatActivity {

    Firebase myFirebaseRef;
    Intent intent = new Intent();
    private final String TAG = this.getClass().getName();
    CollapsingToolbarLayout colToolbar;
    EditText assetNameEditText;
    EditText assetCategoryEditText;
    String assetID;
    String assetName;
    String assetCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        assetID = intent.getStringExtra("assetID");
        assetName = intent.getStringExtra("assetName");
        assetCategory = intent.getStringExtra("assetCategory");

        setContentView(R.layout.activity_asset_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        colToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        assetNameEditText = (EditText) findViewById(R.id.asset_name);
        assetCategoryEditText = (EditText) findViewById(R.id.asset_category);
        myFirebaseRef = new Firebase("https://flickering-torch-8914.firebaseio.com/assets/"+assetID);

        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d(TAG, "" + snapshot.toString());
                AssetListDataModel asset = snapshot.getValue(AssetListDataModel.class);
                assetName = asset.getItemName();
                assetCategory = asset.getCategory();
                populateDetails();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        setSupportActionBar(toolbar);

        if (assetID != null)
            populateDetails();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void populateDetails() {
        colToolbar.setTitle(assetName);
        assetNameEditText.setText(assetName);
        assetCategoryEditText.setText(assetCategory);
    }
}
