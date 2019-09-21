package kr.devx.satcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences appPreferences = context.getSharedPreferences("SETTING", Context.MODE_PRIVATE);
            int masterNotification = appPreferences.getInt("NOTIFICATION_MASTER", 0);
            int masterFloating = appPreferences.getInt("FLOATING_MASTER", 0);
            int masterLockscreen = appPreferences.getInt("LOCKSCREEN_MASTER", 0);
            int bootEnabled = appPreferences.getInt("SERVICE_BOOT", 0);
            if (bootEnabled == 1 && (masterNotification == 1 || masterFloating == 1 || masterLockscreen == 1)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.d("SATCOUNTER","BackgroundService : on BootReceiver : startForegroundService");
                    context.startForegroundService(new Intent(context, BackgroundService.class));
                } else {
                    Log.d("SATCOUNTER","BackgroundService : on BootReceiver : startService");
                    context.startService(new Intent(context, BackgroundService.class));
                }
            }
        }
    }

}
