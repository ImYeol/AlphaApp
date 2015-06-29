package thealphalabs.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import thealphalabs.Interface.ServiceControllerInterface;
import thealphalabs.notification.NotificationService;

/**
 * @author  Sukbeom Kim
 */
public class NotificationController implements ServiceControllerInterface {
    private final static String TAG = "NotificationController";

    public static boolean flag;
    public static void setFlag(boolean v) {flag = v;}

    private static NotificationController instance;
    public  static NotificationController getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationController(context);
        }
        return instance;
    }
    public  static NotificationController getInstance() {
        assert instance == null;
        return instance;
    }

    private Context             mContext;

    NotificationController(Context context) {
        mContext = context;
    }

    @Override
    public void start() {
        if (NotificationService.isAccessibilitySettingsOn(mContext)) {
            // 접근성 서비스 On 상태일 때
        }
        else {
            // 접근성 서비스 Off 상태일 때
            // 사용자의 Notification 설정이 꺼져 있는 경우 설정할 수 있도록 창을 띄워준다.
            try
            {
                Intent i = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                mContext.startActivity(i);
            }
            catch (Exception e)
            {
                Log.e(TAG, "Exception due to SDK version");
                Intent i = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                ((Activity)mContext).startActivityForResult(i, NotificationService.NOTIFICATION_SET);
            }

            // 사용자가 Notification을 켜지 않았을 수도 있으므로 다시 확인한다.
            // 이 때, 사용자가 notification 켰다면 스위치를 켠다.
            if (NotificationService.isAccessibilitySettingsOn(mContext)) {
                NotificationController.setFlag(true);
            } else {
                NotificationController.setFlag(false);
            }
        }
    }

    @Override
    public void stop() {
        // Notification을 직접 프로그래밍으로 끌 수는 없다.
        // 상태 플래그만 false로 설정한다.
        setFlag(false);
    }

    @Override
    public void resume() {
        // resume
    }

    @Override
    public void pause() {
        setFlag(false);
    }

    @Override
    public void ready() {
        mContext.startService(new Intent(mContext, NotificationService.class));

        if (NotificationService.isAccessibilitySettingsOn(mContext)) {
            setFlag(true);
        }
        else {
            setFlag(false);
        }
    }

    @Override
    public boolean isEnabled() {
        return flag;
    }
}