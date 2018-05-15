package id.kopas.berkarya.alarmnotify4;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

// kirim notifikasi dan alarm berdasarkan waktu
public class SchedulingService extends IntentService implements Constants {
    public SchedulingService() {super("SchedulingService");}

    private NotificationManager mNotificationManager;

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("SchedulingService","onHandleIntent");

        String notifName = intent.getStringExtra(EXTRA_NOTIF_NAME);
        String notifDesc = intent.getStringExtra(EXTRA_NOTIF_DESC);
        String notifDay = intent.getStringExtra(EXTRA_NOTIF_DAY);

        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTimeInMillis(System.currentTimeMillis());

        sendNotification(notifName, notifDesc); //It is time for %s prayers.
        Log.d("SchedulingService","onHandleIntent.sendNotification");
        // Release the wake lock provided by the BroadcastReceiver.
        AlarmReceiver.completeWakefulIntent(intent);
        Log.d("SchedulingService","onHandleIntent.AlarmReceiver");
        // END_INCLUDE(service_onhandle)
    }

    // Post a notification indicating whether a doodle was found.
    private void sendNotification(String title, String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        //Buat Notif
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(title)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //Tambah Suara dan Getar Notif
        long[] vibrate = {500, 500, 500, 500, 500, 500, 500, 500, 500};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if (alarmSound == null) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }

        builder.setSound(alarmSound);
        builder.setVibrate(vibrate);

        builder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
