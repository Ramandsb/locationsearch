package tagbin.in.myapplication;

import tagbin.in.myapplication.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SplashActivity extends Activity {
    private ProgressBar progressBar;
    private Handler mHandler = new Handler();
    AlertDialog alert;
    TextView messageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        customDialog();



    }
    public void customDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog, null);
        builder.setView(customView);
        messageView = (TextView)customView.findViewById(R.id.tvdialog);
        alert = builder.create();

    }

    public void showDialog(){
        alert.show();
    }
    public void dismissDialog(){
        alert.dismiss();
    }

    @Override
    public void onStart() {
        // When the aplication starts, show the progressbar for 2 seconds. After that, execute loadHomeActivity runnable.
        long mStartTime = 0;
        if (mStartTime == 0L) {
            mStartTime = System.currentTimeMillis();
            mHandler.removeCallbacks(loadHomeActivity);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            progressBar.setProgress(0);
            mHandler.postDelayed(loadHomeActivity, 3000);
        }
        super.onStart();
    }

    // A runnable executed when the progressbar finishes which starts the HomeActivity.
    private Runnable loadHomeActivity = new Runnable() {
        public void run() {
            SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.LOGINDETAILS, Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");


            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                turnGPSOn();
            } else {
                if (username.equals("")) {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashActivity.this, StartService.class);
                    startActivity(i);
                    finish();

                }
            }
        }

    };

    public void turnGPSOn()
    {

        startActivity(new Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        Toast.makeText(this,"Enable GPS",Toast.LENGTH_LONG).show();


    }


}
