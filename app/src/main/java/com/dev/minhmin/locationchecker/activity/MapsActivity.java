package com.dev.minhmin.locationchecker.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dev.minhmin.locationchecker.R;
import com.dev.minhmin.locationchecker.model.Checker;
import com.dev.minhmin.locationchecker.utils.DataCenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private GoogleMap mMap;
    private String key;
    private ImageView _btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        key = DataCenter.getDeviceKey(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        _btnBack = (ImageView) findViewById(R.id.btn_back);
        _btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//                return false;
//            }
//        });
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
        Log.e("Location Tracker: ", "Map ready");
        mMap = googleMap;
        mDatabase = FirebaseDatabase.getInstance().getReference().child(user.getUid());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMap.clear();
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    Checker c = i.getValue(Checker.class);
                    Log.e("Location Tracker: ", c.getX() + ", " + c.getY());
                    LatLng point = new LatLng(c.getX(), c.getY());
                    mMap.addMarker(new MarkerOptions().position(point).title(c.getName()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Location Tracker", "Error load data");
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                marker.showInfoWindow();
                return true;
            }
        });
    }


}
