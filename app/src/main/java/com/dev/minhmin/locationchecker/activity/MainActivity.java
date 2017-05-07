package com.dev.minhmin.locationchecker.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dev.minhmin.locationchecker.R;
import com.dev.minhmin.locationchecker.adapter.ListCheckerAdapter;
import com.dev.minhmin.locationchecker.model.Checker;
import com.dev.minhmin.locationchecker.service.LocationUpdatesService;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Minh min on 4/27/2017.
 */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private ListView lvChecker;
    private ArrayList<Checker> listCheckers = new ArrayList<>();
    private ListCheckerAdapter adapter;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private Button btnAdd;
    private String userID;
    private TextView tvAccName, tvAccEmail;
    private CircleImageView ivAccImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("ahihi", "start main activity");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //kiem tra xem con tai khoan nao dang nhap ko
                if (firebaseAuth.getCurrentUser() == null) {
                    finish();
                }
            }
        };
        userID = user.getUid();
        lvChecker = (ListView) findViewById(R.id.abc);
        btnAdd = (Button) findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);
        adapter = new ListCheckerAdapter(this, listCheckers);
        lvChecker.setAdapter(adapter);
        lvChecker.setOnItemClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v = navigationView.getHeaderView(0);
        tvAccName = (TextView) v.findViewById(R.id.tv_account_name);
        tvAccEmail = (TextView) v.findViewById(R.id.tv_account_email);
        ivAccImage = (CircleImageView) v.findViewById(R.id.iv_account_image);

        tvAccName.setText(user.getDisplayName());
        tvAccEmail.setText(user.getEmail());
        Glide.with(getApplicationContext()).load(user.getPhotoUrl())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivAccImage);
        ref.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    DatabaseReference mRef = ref.child(userID).child(userID);
                    Map<String, Object> newLocation = new HashMap<>();
                    newLocation.put("x", 0);
                    newLocation.put("y", 0);
                    newLocation.put("name", "Your Divice");
                    mRef.setValue(newLocation);
                } else {
                    listCheckers.clear();
                    for (DataSnapshot i : dataSnapshot.getChildren()) {
                        Checker c = i.getValue(Checker.class);
                        c.setId(i.getKey());
                        listCheckers.add(c);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.e("ahihi", user.getUid());
        if (!isMyServiceRunning(LocationUpdatesService.class)) {
            Log.e("ahihi", "service not running, start service");
            Intent intent = new Intent(MainActivity.this, LocationUpdatesService.class);
            startService(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.putExtra("key", listCheckers.get(i).getId());
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View v = this.getLayoutInflater().inflate(R.layout.dialog_addchecker, null);
        builder.setView(v)
                .setTitle("Add new checker")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText edtCheckerID = (EditText) v.findViewById(R.id.edt_checker_id);
                        EditText edtCheckerName = (EditText) v.findViewById(R.id.edt_checker_name);
                        String id = edtCheckerID.getText().toString();
                        if (id.equals("")) {
                            dialogInterface.cancel();
                        }
                        Map<String, Object> newChecker = new HashMap<>();
                        String name = edtCheckerName.getText().toString();
                        if (name.equals("")) {
                            name = "Checker";
                        }
                        newChecker.put("name", name);
                        newChecker.put("x", 0);
                        newChecker.put("y", 0);
                        ref.child(userID).child(id).setValue(newChecker);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_add_new) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final View v = this.getLayoutInflater().inflate(R.layout.dialog_addchecker, null);
            builder.setView(v)
                    .setTitle("Add new checker")
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText edtCheckerID = (EditText) v.findViewById(R.id.edt_checker_id);
                            EditText edtCheckerName = (EditText) v.findViewById(R.id.edt_checker_name);
                            String id = edtCheckerID.getText().toString();
                            if (id.equals("")) {
                                dialogInterface.cancel();
                            }
                            Map<String, Object> newChecker = new HashMap<>();
                            String name = edtCheckerName.getText().toString();
                            if (name.equals("")) {
                                name = "Checker";
                            }
                            newChecker.put("name", name);
                            newChecker.put("x", 0);
                            newChecker.put("y", 0);
                            ref.child(userID).child(id).setValue(newChecker);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (id == R.id.nav_logout) {
            signOut();
        } else if (id == R.id.nav_information) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Information")
                    .setMessage("Bai tap nhom nhom 5\n" +
                            "Tran Anh Minh\n" +
                            "Bui Thi Thuy Duong\n" +
                            "Bui Thanh Loc")
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
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
        if (id == R.id.action_logout) {
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }
}
