package tagbin.in.myapplication.Gcm;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tagbin.in.myapplication.Database.DatabaseOperations;
import tagbin.in.myapplication.LoginActivity;
import tagbin.in.myapplication.NotificationPublisher;
import tagbin.in.myapplication.R;
import tagbin.in.myapplication.StartService;
import tagbin.in.myapplication.UpcomingRides.SeeUpcomingRides;
import tagbin.in.myapplication.Volley.AppController;

public class WakeUpAct extends Activity {
    PowerManager pm;
    PowerManager.WakeLock wl;
    KeyguardManager km;
    KeyguardManager.KeyguardLock kl;
    TextView cabtv, timetv, fromtv, totv;
    String url = Config.BASE_URL + "accept/";
    String username;
    TextView tvDialog;
    ProgressBar progressBar;
    AlertDialog alert;
    TextView messageView;
    StartService startService;
    DatabaseOperations dop;
    String gothr, gotmin, gotsec;
    Uri sound;

    SharedPreferences ridestatusShared;
    public  static final String RIDESTATUS="ride_status";

    long nethr, netmin, nets;
    long inmilli;
    int current_houre_Int,current_minuts_int,current_sec_int,pickup_houre_Int,pickup_minuts_Int,pickup_sec_Int;

     MediaPlayer mp;
    String cab_no, user_id,from , pickup_time , pickup_address, pickup_houre , pickup_minuts,pickup_sec,current_houre ,current_minuts ,current_sec ;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_wake_up);
        Log.i("INFO", "onCreate() in DismissLock");
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("INFO");
        customDialog();
        ridestatusShared =getSharedPreferences(RIDESTATUS,MODE_PRIVATE);
        dop = new DatabaseOperations(this);
        tvDialog = (TextView) findViewById(R.id.tvdialog);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("message");
            cabtv = (TextView) findViewById(R.id.mcab_no);
            timetv = (TextView) findViewById(R.id.mtime);
            fromtv = (TextView) findViewById(R.id.from);
            totv = (TextView) findViewById(R.id.to);
            try {
                //////////////////////

                JSONObject jsonObject = new JSONObject(value);
                 cab_no = (String) jsonObject.get("cab_no");
                 user_id = (String) jsonObject.get("user_id");
//                 from = (String) jsonObject.get("from");
                 pickup_time = (String) jsonObject.get("pickup_time");
                 pickup_address = (String) jsonObject.get("pickup_address");
                pickup_houre = (String) jsonObject.get("pickup_houre");
                 pickup_houre_Int = Integer.parseInt(pickup_houre);
                 pickup_minuts = (String) jsonObject.get("pickup_minuts");
                 pickup_minuts_Int = Integer.parseInt(pickup_minuts);
                 pickup_sec = (String) jsonObject.get("pickup_sec");
                 pickup_sec_Int = Integer.parseInt(pickup_sec);
                 current_houre = (String) jsonObject.get("current_houre");
                 current_houre_Int = Integer.parseInt(current_houre);
                 current_minuts = (String) jsonObject.get("current_minuts");
                 current_minuts_int = Integer.parseInt(current_minuts);
                 current_sec = (String) jsonObject.get("current_sec");
                 current_sec_int = Integer.parseInt(current_sec);
                cabtv.setText(pickup_time);
                timetv.setText(pickup_address);
                fromtv.setText(pickup_address);
                long timeinmili=  calculatemilisecs(current_houre_Int,current_minuts_int,current_sec_int,pickup_houre_Int,pickup_minuts_Int,pickup_sec_Int);
                String timinmilli=Long.toString(timeinmili);
                dop.putInformation(dop, cab_no, pickup_time, user_id, pickup_address, timinmilli,"pending");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("Split values",cab_no+"//"+user_id+"//"+pickup_time+"//"+pickup_houre+"//"+pickup_minuts+"//"+pickup_sec+"//"+current_houre+"//"+current_minuts+"//"+current_sec);

            Log.d("vsl", value);
        }
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "INFO");
        wl.acquire(); //wake up the screen
        kl.disableKeyguard();// dismiss the keyguard
        mp = MediaPlayer.create(this, R.raw.ring);
        mp.start();

        startService = new StartService();
        FloatingActionButton accept = (FloatingActionButton) findViewById(R.id.Accept);
        FloatingActionButton reject = (FloatingActionButton) findViewById(R.id.Reject);
        final SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.LOGINDETAILS, Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                showDialog();
                makeJsonObjReq("true");
                dop.putStatus(dop,"accept",user_id);
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                showDialog();
                makeJsonObjReq("false");
                StartService.visible = false;
                dop.putStatus(dop,"reject",user_id);
                dop.deleteRow(dop,user_id);
            }
        });

    }

    public void customDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View customView = inflater.inflate(R.layout.dialog, null);
        builder.setView(customView);
        messageView = (TextView) customView.findViewById(R.id.tvdialog);
        progressBar = (ProgressBar) customView.findViewById(R.id.progress);
        alert = builder.create();

    }

    public void showDialog() {

        alert.show();
    }

    public void dismissDialog() {
        alert.dismiss();
    }

    public void displayErrors(VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            progressBar.setVisibility(View.GONE);
            messageView.setText("Connection failed");
        } else if (error instanceof AuthFailureError) {
            progressBar.setVisibility(View.GONE);
            messageView.setText("AuthFailureError");
        } else if (error instanceof ServerError) {
            progressBar.setVisibility(View.GONE);
            messageView.setText("ServerError");
        } else if (error instanceof NetworkError) {
            messageView.setText("NetworkError");
        } else if (error instanceof ParseError) {
            progressBar.setVisibility(View.GONE);
            messageView.setText("ParseError");
        }
    }

    public void redirectTomap() {
        Intent i = new Intent(WakeUpAct.this, StartService.class);
        startActivity(i);
        WakeUpAct.this.finish();
    }

    public void redirectTologin() {
        Intent i = new Intent(WakeUpAct.this, LoginActivity.class);
        startActivity(i);
        WakeUpAct.this.finish();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        wl.release();
        mp.stop();
         //when the activiy pauses, we should realse the wakelock
    }

    @Override
    protected void onStop() {
        super.onStop();
        mp.stop();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        wl.acquire();//must call this!
    }

    private void makeJsonObjReq(String response) {
        SharedPreferences  sharedPreferences = getSharedPreferences(LoginActivity.LOGINDETAILS, Context.MODE_PRIVATE);
        final String Auth_key="ApiKey "+cab_no+":"+sharedPreferences.getString("auth_key","");
        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("username", username);
        postParam.put("success", response);
        postParam.put("user_id", user_id);
        JSONObject jsonObject = new JSONObject(postParam);
        Log.d("postpar", jsonObject.toString());


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("response", response.toString());
                        try {


                            if (response.getString("success").equals("true")) {

                                dismissDialog();
                                if (username.equals("")) {
                                    redirectTologin();


                                } else {
                                    redirectTomap();

                                }

                            } else {
                                dismissDialog();
                                if (username.equals("")) {
                                    redirectTologin();
                                } else {
                                    redirectTomap();
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error", "Error: " + error.getMessage());
                displayErrors(error);
                Log.d("error", error.toString());
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
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

    private void scheduleNotification(Notification notification, long triggertime) {


        Log.d("Notification","set");
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime() + triggertime;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, futureInMillis, pendingIntent);
        Log.d("alarm set", "");
    }

    private Notification getNotification(String content) {
        Intent resultIntent = new Intent(this, SeeUpcomingRides.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SeeUpcomingRides.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("getNotification","get");
        PendingIntent resultPendingIntent = stackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT
                        | PendingIntent.FLAG_ONE_SHOT);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Your is Job Starting Soon");
        builder.setContentText(content);
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
        builder.setContentIntent(resultPendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        return builder.build();
    }

    public long calculatemilisecs(int chr, int cmin, int cs, int phr, int pmin, int ps) {
        nethr = phr - chr;
        netmin = (pmin-30) - cmin;
        nets = ps - cs;
        long totaltimesec = nethr * 3600 + netmin * 60 + nets;
        long x = Math.abs(-totaltimesec);
        inmilli = x * 1000;
        scheduleNotification(getNotification("Job Starting in 15mins"), inmilli);
        Log.d("calImmilisec", inmilli + " ");
        return inmilli;
    }

}