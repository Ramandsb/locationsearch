package tagbin.in.myapplication.UpcomingRides;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.List;

import tagbin.in.myapplication.Database.DatabaseOperations;
import tagbin.in.myapplication.Database.TableData;
import tagbin.in.myapplication.R;
import tagbin.in.myapplication.ShowDetailsDetailActivity;
import tagbin.in.myapplication.StartService;
import tagbin.in.myapplication.StartTrip;
import tagbin.in.myapplication.TestActivity;

public class SeeUpcomingRides extends AppCompatActivity {

    private RecyclerView mRecyclerview;
    private MyAdapter mAdapter;
    private List<DataItems> arrayList = new ArrayList<DataItems>();
    private List<DataItems> databaselist = new ArrayList<DataItems>();
    Button click;
    DataItems dataItems;
    DatabaseOperations dop;
    public  static String SELECTEDRIDEDETAILS="rideDetails";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_upcoming_rides);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerview= (RecyclerView) findViewById(R.id.Reclist);
        sharedPreferences = getSharedPreferences(SELECTEDRIDEDETAILS,MODE_PRIVATE);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mAdapter = new MyAdapter(this);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.setHasFixedSize(false);
        mRecyclerview.setLayoutManager(linearLayoutManager);
         dop = new DatabaseOperations(this);
        databaselist= dop.readData(dop);
        mAdapter.setData((ArrayList<DataItems>) databaselist, true);
        arrayList = new ArrayList();
        ItemClickSupport.addTo(mRecyclerview).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                dataItems = databaselist.get(position);
                String cab_no = dataItems.getCab_no();
                String time = dataItems.getTime();
                String user_id = dataItems.getTo_loc();
                String pickup = dataItems.getPick();
                String status = dataItems.getStatus();
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.clear();
                editor.putString("cab_no",cab_no);
                editor.putString("time",time);
                editor.putString("user_id",user_id);
                editor.putString("pickup",pickup);
                editor.putString("status",status);
                editor.commit();
                Intent intent = new Intent(SeeUpcomingRides.this, ShowDetailsDetailActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(SeeUpcomingRides.this, "clicked" + position + "  //" + dataItems.getCab_no(), Toast.LENGTH_LONG).show();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i= 0;i<10;i++){
                    dop.putInformation(dop,""+i,"Time :"+i+":"+i,""+i,"Gurgaon :"+i,""+i,"pending");
                }
                databaselist= dop.readData(dop);
                mAdapter.setData((ArrayList<DataItems>) databaselist, true);
            }
        });
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
       // fab2.setVisibility(View.GONE);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dop.putStatus(dop,"accept",""+3);
                databaselist= dop.readData(dop);
                mAdapter.setData((ArrayList<DataItems>) databaselist, true);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, StartService.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaselist= dop.readData(dop);
        mAdapter.setData((ArrayList<DataItems>) databaselist, true);
    }
}
