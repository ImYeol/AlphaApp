package thealphalabs.notification;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import thealphalabs.alphaapp.AlphaApplication;
import thealphalabs.controller.NotificationController;

/**
 * Created by sukbeom on 15. 6. 19.
 */

public class NotificationService extends AccessibilityService {
    private final String TAG = "NotificationService";
    public final static int NOTIFICATION_SET = 5;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
           // Notification이 발생한 경우

            if (accessibilityEvent.getPackageName().toString().equals("android")) return;

            Log.d(TAG, "Notification Information:\n"
                    + "Package name : " + accessibilityEvent.getPackageName() + "\n"
                    + "Description : " + accessibilityEvent.getContentDescription() + "\n"
                    + "Text : " + accessibilityEvent.getText().toString());

            Notification notification = (Notification) accessibilityEvent.getParcelableData();
            if (notification == null) {
                // Toast로 메세지가 뜨는 경우도 여기에 포함
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

                    /// 이 부분이 실질적으로 데이터를 받아오는 부분 ///
                    System.out.println("title is: " + text.get(16908310));
                    System.out.println("info is: " + text.get(16909082));
                    System.out.println("text is: " + text.get(16908358));

                    sendNotificationData(text.get(16908310), text.get(16908358));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");
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

    private void sendNotificationData(String title, String text) {
        if (NotificationController.getInstance().isEnabled()) {
            // Notification 설정이 켜져 있을 때에만 데이터를 보낸다.
            ((AlphaApplication) getApplication()).getBluetoothHelper().transferNotificationData(title, text);
        }
    }

    /**
     * isAccessibilitySettingsOn
     *  Notification이 켜져있는지 확인한다.
     * @param mContext
     * @return  Notification이 켜져있다면 true를 반환한다.
     */
    public static boolean isAccessibilitySettingsOn(Context mContext) {
        String TAG = "NotificationService";
        int accessibilityEnabled = 0;

        final String service    = mContext.getPackageName() + "/" + NotificationService.class.getName();

        Log.d(TAG, "service name = " + service);
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    if (accessabilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.d(TAG, "***ACCESSIBILIY IS DISABLED***");
        }

        return accessibilityFound;
    }
}
