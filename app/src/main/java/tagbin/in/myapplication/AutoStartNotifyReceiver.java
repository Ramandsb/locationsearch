package tagbin.in.myapplication;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Chetan on 10/17/2015.
 */
public class AutoStartNotifyReceiver extends BroadcastReceiver {

    private final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if(intent.getAction().equals(BOOT_COMPLETED_ACTION)){
            Intent myIntent = new Intent(context, NotifyService.class);
            context.startService(myIntent);
        }

    }

}