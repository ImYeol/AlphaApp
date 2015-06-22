package thealphalabs.alphaapp.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by sukbeom on 15. 6. 19.
 */
public class NotificationReceiver extends BroadcastReceiver {
    private final String TAG = "NotificationReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
    }
}
