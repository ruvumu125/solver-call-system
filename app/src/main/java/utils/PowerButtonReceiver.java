package utils;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.widget.Toast;

import com.solver.callbutton.MainActivity;

public class PowerButtonReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {

            Toast.makeText(context, "Screen turned on", Toast.LENGTH_SHORT).show();

            if (isAppInForeground(context)) {
                Toast.makeText(context, "App is in foreground", Toast.LENGTH_SHORT).show();

                Intent videoIntent = new Intent(context, MainActivity.class);
                videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Required to start an activity from outside an activity context
                videoIntent.putExtra("fromReceiver", true); // Pass extra to identify the source
                context.startActivity(videoIntent);
            }
        }
    }


    private boolean isAppInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    appProcess.processName.equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}

