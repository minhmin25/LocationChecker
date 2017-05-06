package com.dev.minhmin.locationchecker.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.dev.minhmin.locationchecker.R;
import com.dev.minhmin.locationchecker.adapter.ListCheckerAdapter;
import com.dev.minhmin.locationchecker.model.Checker;
import com.dev.minhmin.locationchecker.service.LocationUpdatesService;
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

/**
 * Created by Minh min on 4/27/2017.
 */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView lvChecker;
    private ArrayList<Checker> listCheckers = new ArrayList<>();
    private ListCheckerAdapter adapter;
    private FirebaseUser user;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private Button btnAdd;
    private String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("ahihi", "start main activity");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        lvChecker = (ListView) findViewById(R.id.lv_checker);
        btnAdd = (Button) findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);
        adapter = new ListCheckerAdapter(this, listCheckers);
        lvChecker.setAdapter(adapter);
        lvChecker.setOnItemClickListener(this);

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
                        Log.e("get data from database", c.toString());
                        listCheckers.add(c);
                    }
                    adapter.notifyDataSetChanged();
                    Log.e("ahihi", adapter.toString());
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
    }
}
