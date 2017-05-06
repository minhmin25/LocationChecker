package com.dev.minhmin.locationchecker.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.dev.minhmin.locationchecker.service.LocationUpdatesService;
import com.dev.minhmin.locationchecker.utils.Utils;

/**
 * Created by Minh min on 4/27/2017.
 */

public class BootUpReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    private LocationUpdatesService mService = null;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            Utils.sBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            Utils.sBound = false;
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Location Tracker: ", "Boot up/Start Service");
        if (intent.getAction().equals(ACTION)) {
//            Intent myIntent = new Intent(context, LocationUpdatesService.class);
//            context.startService(myIntent);
//            context.bindService(new Intent(context, LocationUpdatesService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
//            mService.requestLocationUpdates();
            Intent mIntent = new Intent(context, LocationUpdatesService.class);
            context.startService(mIntent);
        }

    }


}
