package kr.devx.satcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) || intent.getAction().equals(Intent.ACTION_SCREEN_ON))) {
            Intent screenIntent = new Intent(context, BackgroundService.class);
            screenIntent.putExtra("ACTION", intent.getAction());
            /*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(screenIntent);
            } else {
                context.startService(screenIntent);
            }
            */
            context.startService(screenIntent);
            Log.d("SATCOUNTER","BackgroundService : on ScreenReceiver : startService");
        }
    }
}
