package id.kopas.berkarya.alarmnotify4;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

// jika waktu jalankan SchedulingService dan menampilkan Activity layar
public class AlarmReceiver extends WakefulBroadcastReceiver implements Constants {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver","onReceive");
        String notifName = intent.getStringExtra(EXTRA_NOTIF_NAME);
        String notifDesc = intent.getStringExtra(EXTRA_NOTIF_DESC);
        String notifDay = intent.getStringExtra(EXTRA_NOTIF_DAY);
        long notifTime = intent.getLongExtra(EXTRA_NOTIF_TIME, -1);

        Log.d("AlarmReceiver","onReceive.day:"+String.valueOf(System.currentTimeMillis())+"/"+String.valueOf(notifTime));

        //jika time tidak sama dengan -1 dan time tidak lebih dari lime menit maka bernilai true
        boolean timePassed = (notifTime != -1 && Math.abs(System.currentTimeMillis() - notifTime) > FIVE_MINUTES);


        if (!timePassed) {
            Intent service = new Intent(context, SchedulingService.class);
            service.putExtra(EXTRA_NOTIF_NAME, notifName);
            service.putExtra(EXTRA_NOTIF_DESC, notifDesc);
            service.putExtra(EXTRA_NOTIF_TIME, notifTime);
            service.putExtra(EXTRA_NOTIF_DAY, notifDay);

            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, service);
            Log.d("AlarmReceiver","onReceive.startWakefulService");
            // END_INCLUDE(alarm_onreceive)
            /*
            // START THE ALARM ACTIVITY
            Intent newIntent = new Intent(context, RingAlarmActivity.class);
            Log.d("AlarmReceiver", "Alarm Receiver Got " + notifName);
            newIntent.putExtra(EXTRA_NOTIF_NAME, notifName);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);*/
        }
        //SET THE NEXT ALARM
        setAlarm(context);
        Log.d("AlarmReceiver","onReceive.setAlarm");

    }

    public void setAlarm(Context context) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTimeInMillis(System.currentTimeMillis());
        // Set the alarm's trigger time to 8:30 a.m.

        Calendar then = Calendar.getInstance(TimeZone.getDefault());
        then.setTimeInMillis(System.currentTimeMillis());

        //Database time schedule
        ArrayList<Task> notifSchedules = new ArrayList<>();
        notifSchedules.add(new Task("3","23:22","Judul Notifikasi 1","Keterangan Isi"));
        notifSchedules.add(new Task("3","23:23","Judul Notifikasi 3","Keterangan Isi"));
        notifSchedules.add(new Task("4","00:01","Judul Notifikasi 2","Keterangan Isi"));


        //String notifNames[] = {"20:35","20:37","20:39","20:42"};

        boolean nextAlarmFound = false;
        String dayOfNotifFound = null;
        String nameOfNotifFound = null;
        String descOfNotifFound = null;
        for (Task notif : notifSchedules) {
            then = getCalendarFromPrayerTime(then, notif.getDay(), notif.getTime());

            if (then.after(now)) {
                // this is the alarm to set
                nameOfNotifFound = notif.getTitle();
                descOfNotifFound = notif.getDesc();
                dayOfNotifFound = notif.getDay();
                nextAlarmFound = true;
                break;
            }
        }


        if (!nextAlarmFound) {
            for (Task notif : notifSchedules) {
                then = getCalendarFromPrayerTime(then, notif.getDay(), notif.getTime());

                if (then.before(now)) {
                    // this is the next day.
                    nameOfNotifFound = notif.getTitle();
                    descOfNotifFound = notif.getDesc();
                    dayOfNotifFound = notif.getDay();
                    nextAlarmFound = true;
                    then.add(Calendar.DAY_OF_YEAR, 1);
                    break;
                }
            }
        }

        if (!nextAlarmFound) {
            return; //something went wrong, abort!
        }


        intent.putExtra(EXTRA_NOTIF_NAME, nameOfNotifFound);
        intent.putExtra(EXTRA_NOTIF_DESC, descOfNotifFound);
        intent.putExtra(EXTRA_NOTIF_DAY, dayOfNotifFound);
        intent.putExtra(EXTRA_NOTIF_TIME, then.getTimeInMillis());

        alarmIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            //lollipop_mr1 is 22, this is only 23 and above
            alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, then.getTimeInMillis(), alarmIntent);
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //JB_MR2 is 18, this is only 19 and above.
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, then.getTimeInMillis(), alarmIntent);
        } else {
            //available since api1
            alarmMgr.set(AlarmManager.RTC_WAKEUP, then.getTimeInMillis(), alarmIntent);
        }

        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }


    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr == null) {
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        if (alarmMgr != null) {
            if (alarmIntent == null) {
                Intent intent = new Intent(context, AlarmReceiver.class);
                alarmIntent = PendingIntent.getBroadcast(context, ALARM_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            }
            alarmMgr.cancel(alarmIntent);
        }

        // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }


    private Calendar getCalendarFromPrayerTime(Calendar cal, String day, String nameTime) {
        String[] time = nameTime.split(":");
        cal.set(Calendar.DAY_OF_WEEK, Integer.valueOf(day));
        cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
        cal.set(Calendar.MINUTE, Integer.valueOf(time[1]));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
}
