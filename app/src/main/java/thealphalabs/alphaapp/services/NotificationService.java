package thealphalabs.alphaapp.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sukbeom on 15. 6. 19.
 */

public class NotificationService extends AccessibilityService {
    private final String TAG = "NotificationService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "NotificationService onCreate() called");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {


        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            // Notification이 발생한 경우
            Log.d(TAG, "Notification Information:\n"
                    + "Package name : " + accessibilityEvent.getPackageName() + "\n"
                    + "Description : " + accessibilityEvent.getContentDescription() + "\n"
                    + "Text : " + accessibilityEvent.getText().toString());


            Notification notification = (Notification) accessibilityEvent.getParcelableData();
            if (notification == null) {
                Log.e(TAG, "notification is null");
                return;
            }
            RemoteViews views = notification.contentView;
            Class secretClass = views.getClass();

            try {
                Map<Integer, String> text = new HashMap<Integer, String>();

                Field outerFields[] = secretClass.getDeclaredFields();
                for (int i = 0; i < outerFields.length; i++) {
                    if (!outerFields[i].getName().equals("mActions")) continue;

                    outerFields[i].setAccessible(true);

                    ArrayList<Object> actions = (ArrayList<Object>) outerFields[i]
                            .get(views);
                    for (Object action : actions) {
                        Field innerFields[] = action.getClass().getDeclaredFields();

                        Object value = null;
                        Integer type = null;
                        Integer viewId = null;
                        for (Field field : innerFields) {
                            field.setAccessible(true);
                            if (field.getName().equals("value")) {
                                value = field.get(action);
                            } else if (field.getName().equals("type")) {
                                type = field.getInt(action);
                            } else if (field.getName().equals("viewId")) {
                                viewId = field.getInt(action);
                            }
                        }

                        if (type == 9 || type == 10) {
                            text.put(viewId, value.toString());
                        }
                    }

                    System.out.println("title is: " + text.get(16908310));
                    System.out.println("info is: " + text.get(16909082));
                    System.out.println("text is: " + text.get(16908358));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.feedbackType = 1;
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }
}
