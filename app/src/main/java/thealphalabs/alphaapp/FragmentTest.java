package thealphalabs.alphaapp;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import thealphalabs.wifidirect.WifiDirectBroadcastReceiver;

/**
 * Created by sukbeom on 15. 6. 19.
 */
public class FragmentTest extends Fragment implements View.OnClickListener{
    private final String TAG = "FragmentTest";
    IntentFilter intentFilter = new IntentFilter();
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    WifiDirectBroadcastReceiver mReceiver;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Notification 발생시키는 버튼
        View v = inflater.inflate(R.layout.activity_fragment_test, container, false);
        Button btn = (Button) v.findViewById(R.id.test_btn);
        btn.setOnClickListener(this);

        if (isAccessibilitySettingsOn(getActivity())) {
            Log.d(TAG, "Accessibility is enabled");
        }
        else {
            Log.d(TAG, "Accessibility is off");
            try {
                Intent i = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivity(i);
            }
            catch (Exception e) {
                Log.e(TAG, "Exception occur because of SDK version");
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        }


        // Wifi Direct 테스트
        manager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(getActivity(), Looper.getMainLooper(), null);


        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            // 양쪽에서 동시에 discovery
            @Override
            public void onSuccess() {
                Log.d(TAG, "discovery success");
            }

            @Override
            public void onFailure(int i) {
                Log.d(TAG, "discovery fail");
            }
        });

        getActivity().registerReceiver(mReceiver, intentFilter);



        return v;
    }

    // To check if service is enabled
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + mContext.getPackageName() + ".services" + ".NotificationService";
        Log.v(TAG, "service name = " + service);
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILIY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    Log.v(TAG, "-------------- > accessabilityService :: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILIY IS DISABLED***");
        }

        return accessibilityFound;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick");

//        manager.requestPeers();
////      Notification 생성
//        NotificationManager nm = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification;
//        notification = new Notification(R.drawable.ic_launcher, "Nomal Notification", System.currentTimeMillis());
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE ;
//        notification.number = 13;
//        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, new Intent(getActivity(), GlassAppMain.class), PendingIntent.FLAG_UPDATE_CURRENT);
//        notification.setLatestEventInfo(getActivity(), "Nomal Title", "Nomal Summary", pendingIntent);
//
//        nm.notify(1234, notification);


    }
}
