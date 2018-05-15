package id.kopas.berkarya.alarmnotify4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

// jika alarm init false jalankan cencel alarm dan
// set ulang alarm lalu set init true (sama seperti BootReceiver)
public class MainActivity extends AppCompatActivity  implements Constants {

    private static boolean sIsAlarmInit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!sIsAlarmInit) {
            updateAlarmStatus();
            sIsAlarmInit = true;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateAlarmStatus() {
        AlarmReceiver init = new AlarmReceiver();
        init.cancelAlarm(getApplicationContext());
        Log.d("MainActivity","onCreate.updateAlarmStatus.cancelAlarm");
        init.setAlarm(getApplicationContext());
        Log.d("MainActivity","onCreate.updateAlarmStatus.setAlarm");
    }
}
