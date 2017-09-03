package tagbin.in.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tagbin.in.myapplication.Gcm.Config;
import tagbin.in.myapplication.Volley.AppController;
import tagbin.in.myapplication.Volley.CustomRequest;

/**
 * Created by Chetan on 10/16/2015.
 */
public class NotifyService extends Service {

    final static String ACTION = "NotifyServiceAction";
    final static String STOP_SERVICE = "";
    final static int RQS_STOP_SERVICE = 1;
    String apikey = "AIzaSyDCppQZn2Ug7VbZB80u3aI8epA6P7ebuGM";
    String macAddress;
    SharedPreferences sharedPreferences;
   static boolean request=false;


//    NotifyServiceReceiver notifyServiceReceiver;

    private static final int MY_NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private Notification myNotification;
    private final String myBlog = "http://android-er.blogspot.com/";

    public static final String BROADCAST_ACTION = "Hello World";
    private static final int TWO_MINUTES = 1000 * 30 * 1;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;
    String Longitude, Latitude;
    //    String url="http://leanmenu.com/api/";
    String url = Config.BASE_URL+"location/";

    Intent intent;
    int counter = 0;


    @Override
    public void onCreate() {
//        notifyServiceReceiver = new NotifyServiceReceiver();
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        sharedPreferences = getSharedPreferences(LoginActivity.LOGINDETAILS, Context.MODE_PRIVATE);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        WifiManager wimanager = (WifiManager) NotifyService.this.getSystemService(Context.WIFI_SERVICE);
        macAddress = wimanager.getConnectionInfo().getMacAddress();
        Log.d("mac", macAddress);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
//        registerReceiver(notifyServiceReceiver, intentFilter);

//        Toast.makeText(NotifyService.this, "service toast", Toast.LENGTH_SHORT).show();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, listener);


//        return super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
//        this.unregisterReceiver(notifyServiceReceiver);
        super.onDestroy();
        sendBroadcast(new Intent("YouWillNeverKillMe"));
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    //    public class NotifyServiceReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context arg0, Intent arg1) {
//            // TODO Auto-generated method stub
//            int rqs = arg1.getIntExtra("RQS", 0);
//            if (rqs == RQS_STOP_SERVICE){
//                stopSelf();
//            }
//        }
//    }
    public class MyLocationListener implements LocationListener {


        public void onLocationChanged(final Location loc) {
            Log.i("*****************", "Location changed");
            if (isBetterLocation(loc, previousBestLocation)) {
                String latiString = Double.toString(loc.getLatitude());
                String longiString = Double.toString(loc.getLongitude());
                Log.d("Lat LOng", loc.getLatitude() + "  " + loc.getLongitude());
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                sendBroadcast(intent);
                Log.d("values to check", latiString + "  " + longiString);
                Intent i = new Intent("LOCATION_UPDATED");
                i.putExtra("myLat", latiString);
                i.putExtra("myLong", longiString);
                sendBroadcast(i);
                SharedPreferences sharedPref = getApplication().getSharedPreferences(LoginActivity.LOGINDETAILS,MODE_PRIVATE);
               String user=sharedPref.getString("username", "");
//                String Auth_key=sharedPref.getString("auth_key","");
//                if (Auth_key.equals("")) {
//                   Log.d("Auth key","Invalid");
//
//
//                }else{
//                    makeRequest(latiString, longiString,Auth_key);
//                }
//            makeRequest(latiString, longiString);
                if (user.equals("")){
                    request=false;
                }else {
                    request=true;
                }

                if (request){
                    makeJsonObjReq(latiString, longiString);
                }



            }
        }

        public void onProviderDisabled(String provider) {
//            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider) {
//            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras) {

        }


    }



    private void makeJsonObjReq(final String lati,final String longi) {
      String cab_no=  sharedPreferences.getString("username","");
        final String Auth_key="ApiKey "+cab_no+":"+sharedPreferences.getString("auth_key","");
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("cab_no", cab_no);
        postParam.put("lat", lati);
        postParam.put("lng", longi);


        JSONObject jsonObject = new JSONObject(postParam);
        Log.d("postpar", jsonObject.toString());



        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error", "Error: " + error.getMessage());

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
//                final String Auth_key="ApiKey "+cab_no+":"+sharedPreferences.getString("auth_key","");
                headers.put("Content-Type", "application/json");
                headers.put( "charset", "utf-8");
                headers.put("Authorization",Auth_key);
                return headers;
            }



        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }


}
