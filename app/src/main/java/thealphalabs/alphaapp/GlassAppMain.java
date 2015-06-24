package thealphalabs.alphaapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import thealphalabs.alphaapp.adapter.SensorController;
import thealphalabs.alphaapp.bluetooth.BluetoothService;
import thealphalabs.alphaapp.notification.NotificationController;
import thealphalabs.alphaapp.services.NotificationService;
import thealphalabs.bluetooth.BluetoothTransferService;

public class GlassAppMain extends MaterialNavigationDrawer {
    private final String TAG = "GlassAppMain";

    @Override
    public void init(Bundle savedInstanceState) {

        // set the header image
        this.setDrawerHeaderImage(R.drawable.mat2);

        // create sections
        this.addSection(newSection("Home", R.drawable.ic_home_black_48dp,
                new FragmentHome()).setSectionColor(Color.parseColor("#48a0b2")));
        this.addSection(newSection("Controller", R.drawable.ic_mouse_black_48dp,
                new FragmentController()).setSectionColor(Color.parseColor("#ccbb14")));
        this.addSection(newSection("Screen Mirroring", R.drawable.ic_settings_cell_black_48dp,
                new FragmentScreenMirror()).setSectionColor(Color.parseColor("#03a9f4")));
        this.addSection(newSection("Appstore", R.drawable.ic_shopping_basket_black_48dp,
                new FragmentAppstore()).setSectionColor(Color.parseColor("#9c27b0")));
        this.addSection(newSection("Setting", R.drawable.ic_settings_black_48dp,
                new FragmentSetting()).setSectionColor(Color.parseColor("#9c27b0")));
        this.addSection(newSection("About", R.drawable.ic_help_outline_black_48dp,
                new FragmentAbout()).setSectionColor(Color.parseColor("#03a9f4")));

        // Test
        this.addSection(newSection("TEST", R.drawable.ic_home_black_48dp,
                new FragmentTest()).setSectionColor(Color.parseColor("#0a9f04")));

        setDefaultSectionLoaded(0);

        // Initialize SensorController
        SensorController.bltService = new BluetoothService(this, new Handler());
        SensorController.AccelController.setFlag(true);
        SensorController.GyroController.setFlag(true);
        // Initialize Notification Controller
        NotificationController.setFlag(true);

        // create bottom section
//        this.addBottomSection(newSection("Homepage", R.drawable.ic_settings_black_24dp,new Intent(this,Settings.class)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case BluetoothService.REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    SensorController.bltService.getDeviceInfo(data);
                }
                break;
            case BluetoothService.REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "Request to enable bluetooth");
                    SensorController.bltService.scanDevice();
                } else {
                    Log.d(TAG, "Bluetooth is not enabled");
                }
                break;
            case NotificationService.NOTIFICATION_SET:
                Log.d(TAG, "Notification Service is set");
                if (NotificationService.isAccessibilitySettingsOn(getApplicationContext())) {
                    Log.d(TAG, "Notification is enabled.");
                    NotificationController.setFlag(true);
                }
                else {
                    Log.d(TAG, "Notification is not enabled.");
                    NotificationController.setFlag(false);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume" + BluetoothTransferService.isEnabled);
        //
        // 사용자가 Notification(Accessibility) 를 직접 끄지 않는 경우,
        // 프로세스가 계속해서 상주하게 된다. 때문에 사용자가 다시 앱을 실행할 때,
        // NotificationService 를 제외한 다른 서비스들을 다시 실행시켜 주어야 한다.

        if (BluetoothTransferService.isEnabled == false) {
            Log.d(TAG, "Service restart");
            startService(new Intent(this, BluetoothTransferService.class));
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        stopService(new Intent(this, BluetoothTransferService.class));
        stopService(new Intent(this, NotificationService.class));

        super.onDestroy();

        BluetoothTransferService.isEnabled = false;
    }
}
