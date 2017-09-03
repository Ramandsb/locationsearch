package tagbin.in.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import junit.framework.TestCase;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tagbin.in.myapplication.Database.DatabaseOperations;
import tagbin.in.myapplication.Gcm.Config;
import tagbin.in.myapplication.UpcomingRides.SeeUpcomingRides;
import tagbin.in.myapplication.Volley.AppController;

public class StartTrip extends AppCompatActivity {
    String cab_no,time,user_id,pickup,status;
    SharedPreferences SELECTEDRIDEDETAILS_sharedPreferences;
    SharedPreferences Logindetails_sharedPreferences;
    String url="";
    String start_trip_url= Config.BASE_URL + "driver_journey_start/";
    String job_url = Config.BASE_URL + "driver_job/";
    DatabaseOperations dop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        SELECTEDRIDEDETAILS_sharedPreferences = getSharedPreferences(SeeUpcomingRides.SELECTEDRIDEDETAILS, Context.MODE_PRIVATE);
        Logindetails_sharedPreferences=getSharedPreferences(LoginActivity.LOGINDETAILS,Context.MODE_PRIVATE);
        cab_no = SELECTEDRIDEDETAILS_sharedPreferences.getString("cab_no", "");
        time = SELECTEDRIDEDETAILS_sharedPreferences.getString("time", "");
        pickup = SELECTEDRIDEDETAILS_sharedPreferences.getString("pickup", "");
        user_id = SELECTEDRIDEDETAILS_sharedPreferences.getString("user_id", "");
        status = SELECTEDRIDEDETAILS_sharedPreferences.getString("status", "");
        TextView cab= (TextView) findViewById(R.id.mcab_no);
        TextView tim= (TextView) findViewById(R.id.mtime);
        TextView to= (TextView) findViewById(R.id.mto_location);
        TextView pickk= (TextView) findViewById(R.id.mpick);
        cab.setText(cab_no);
        tim.setText(time);
        to.setText(user_id);
        pickk.setText(pickup);
        dop = new DatabaseOperations(this);
        FloatingActionButton accept = (FloatingActionButton) findViewById(R.id.Accept);
        FloatingActionButton reject = (FloatingActionButton) findViewById(R.id.Reject);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeJsonObjReq("true");
                dop.putStatus(dop,"accept",user_id);
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeJsonObjReq("false");
                dop.putStatus(dop, "reject", user_id);
                dop.deleteRow(dop, user_id);
            }
        });
    }

    public void starttrip(View view){
        makeJsonObjReq("starttrip");
    }
    public void arrived(View view){
        makeJsonObjReq("arrived");
    }

    private void makeJsonObjReq(final String choice) {

        final String Auth_key = "ApiKey " + cab_no + ":" + Logindetails_sharedPreferences.getString("key", "");
        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("user_id", user_id);
        postParam.put("username", cab_no);
        if (choice.equals("starttrip")) {
            postParam.put("lat", StartService.mylat.toString());
            postParam.put("lng", StartService.mylong.toString());
            postParam.put("trip", "Started");
            url=start_trip_url;
        }else if (choice.equals("arrived")){
            postParam.put("arrived","true");
        }else {
            postParam.put("success",choice);
           url= job_url;
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response",response.toString());
                        StartService.visible = true;
                        if (choice.equals("starttrip")) {
                            Intent i = new Intent(StartTrip.this, StartService.class);
                            startActivity(i);
                            finish();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error", "Error: " + error.getMessage());
                Log.d("error", error.toString());
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("charset", "utf-8");
                headers.put("Authorization", Auth_key);
                return headers;
            }


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }



}
