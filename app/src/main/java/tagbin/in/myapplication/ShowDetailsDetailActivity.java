package tagbin.in.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tagbin.in.myapplication.Database.DatabaseOperations;
import tagbin.in.myapplication.Gcm.Config;
import tagbin.in.myapplication.UpcomingRides.SeeUpcomingRides;
import tagbin.in.myapplication.Volley.AppController;


public class ShowDetailsDetailActivity extends AppCompatActivity {
    String cab_no, time, pickup, user_id, status;
    String url = Config.BASE_URL + "startTrip/";
    SharedPreferences SELECTEDRIDEDETAILS_sharedPreferences;
    SharedPreferences Logindetails_sharedPreferences;
    FloatingActionButton fab;

    public static boolean show=false;

    String jernydoneUrl= Config.BASE_URL+"cancel/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showdetails_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        SELECTEDRIDEDETAILS_sharedPreferences = getSharedPreferences(SeeUpcomingRides.SELECTEDRIDEDETAILS, Context.MODE_PRIVATE);
        Logindetails_sharedPreferences=getSharedPreferences(LoginActivity.LOGINDETAILS,Context.MODE_PRIVATE);
        cab_no = SELECTEDRIDEDETAILS_sharedPreferences.getString("cab_no", "");
        time = SELECTEDRIDEDETAILS_sharedPreferences.getString("time", "");
        pickup = SELECTEDRIDEDETAILS_sharedPreferences.getString("pickup", "");
        user_id = SELECTEDRIDEDETAILS_sharedPreferences.getString("user_id", "");
        status = SELECTEDRIDEDETAILS_sharedPreferences.getString("status", "");


        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                makeJsonObjReq();
//                fab.setVisibility(View.INVISIBLE);
//            }
//        });
//        if (show==true){
//            fab.setVisibility(View.VISIBLE);
//        }else fab.setVisibility(View.INVISIBLE);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ShowDetailsDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ShowDetailsDetailFragment.ARG_ITEM_ID));
            ShowDetailsDetailFragment fragment = new ShowDetailsDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.showdetails_detail_container, fragment)
                    .commit();
        }
    }


    private void makeJsonObjReq() {


        final String Auth_key = "ApiKey " + cab_no + ":" + Logindetails_sharedPreferences.getString("key", "");
        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("user_id", user_id);
        postParam.put("username", cab_no);
        postParam.put("lat", StartService.mylat.toString());
        postParam.put("lng", StartService.mylong.toString());
        postParam.put("trip", "Started");


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        show=false;
                        StartService.visible = true;
//                        ShowDetailsDetailFragment.arrived_container.setVisibility(View.VISIBLE);
//                        ShowDetailsDetailFragment.arrBool=true;
                        Intent i = new Intent(ShowDetailsDetailActivity.this, StartService.class);
                        startActivity(i);
                        finish();

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this,SeeUpcomingRides.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cancel, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent i = new Intent(this,SeeUpcomingRides.class);
            startActivity(i);
            finish();
            return true;
        }else if (id == R.id.mnu_cancel){
            cancelRide();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void cancelRide(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);


        // set title
        alertDialogBuilder.setTitle("Are you Sure ?");

        // set dialog message
        alertDialogBuilder
                .setMessage("Click yes to Cancel the Ride!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        makeJsonObjReq("yes");
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }
    private void makeJsonObjReq(String s) {
        SharedPreferences  sharedPreferences = getSharedPreferences(LoginActivity.LOGINDETAILS, Context.MODE_PRIVATE);
      SharedPreferences  sha = getSharedPreferences(SeeUpcomingRides.SELECTEDRIDEDETAILS, Context.MODE_PRIVATE);
        final String user_id=   sha.getString("user_id", "");
        String   cab_no= sha.getString("cab_no", "");
        final String k=sharedPreferences.getString("key", "");
        final String Auth_key="ApiKey "+cab_no+":"+k;
        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("user_id",user_id);
        postParam.put("username", cab_no);
        postParam.put("trip", "cancel");

        JSONObject jsonObject = new JSONObject(postParam);
        Log.d("postpar", jsonObject.toString());


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                jernydoneUrl, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        ShowDetailsDetailFragment.show=false;
                        ShowDetailsDetailFragment.arr_show=true;
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
                headers.put( "charset", "utf-8");
                headers.put("Authorization",Auth_key);
                return headers;
            }



        };



        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

}
