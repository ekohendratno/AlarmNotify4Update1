package id.kopas.berkarya.alarmnotify4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

// membatalkan dan set ulang alarm saat komputer dihidupkan kembali
public class BootReceiver extends BroadcastReceiver {
    AlarmReceiver notifAlarm = new AlarmReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootReceiver","onReceive");
        String action = intent.getAction();
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            notifAlarm.setAlarm(context);
            Log.d("BootReceiver","onReceive.setAlarm");
        }
    }
}
