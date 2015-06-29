package thealphalabs.alphaapp;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import thealphalabs.bluetooth.BluetoothTransferService;
import thealphalabs.bluetooth.DeviceListActivity;
import thealphalabs.controller.AcclSensorController;
import thealphalabs.controller.BluetoothController;
import thealphalabs.controller.GyroSensorController;
import thealphalabs.controller.NotificationController;
import thealphalabs.controller.SensorController;
import thealphalabs.controller.ServiceController;
import thealphalabs.controller.WifiDirectController;
import thealphalabs.notification.NotificationService;

public class GlassAppMain extends MaterialNavigationDrawer {
    private final String TAG = "GlassAppMain";

    /**
     * initServiceController
     * @desc 서비스(블루투스, 센서, Notification, Wifi direct 등)를 위한 Controller를 초기화한다.
     */
    private void initServiceController() {
        // Service Controller에 각 서비스 컨트롤러를 추가한다.
        ServiceController.registerService(ServiceController.BLUETOOTH_SERVICE,    BluetoothController.getInstance(this));
        ServiceController.registerService(ServiceController.GYROSENSOR_SERVICE,   GyroSensorController.getInstance(this));
        ServiceController.registerService(ServiceController.ACCLSENSOR_SERVICE,   AcclSensorController.getInstance(this));
        ServiceController.registerService(ServiceController.NOTIFICATION_SERVICE, NotificationController.getInstance(this));
        ServiceController.registerService(ServiceController.WIFIDIRECT_SERVICE,   WifiDirectController.getInstance(this));
    }

    @Override
    public void init(Bundle savedInstanceState) {
        // 헤더 이미지 설정
        this.setDrawerHeaderImage(R.drawable.mat2);

        // 섹션 추가
        this.addSection(newSection("Home", R.drawable.ic_home_black_48dp,
                new FragmentHome()).setSectionColor(Color.parseColor("#48a0b2")));
        this.addSection(newSection("Controller", R.drawable.ic_mouse_black_48dp,
                new FragmentController()).setSectionColor(Color.parseColor("#ccbb14")));
        this.addSection(newSection("Screen Mirroring", R.drawable.ic_settings_cell_black_48dp,
                new FragmentScreenMirror()).setSectionColor(Color.parseColor("#03a9f4")));
        this.addSection(newSection("Appstore", R.drawable.ic_shopping_basket_black_48dp,
                new FragmentAppstore()).setSectionColor(Color.parseColor("#9c27b0")));
        this.addSection(newSection("Setting", R.drawable.ic_settings_black_48dp,
                new FragmentSetting()).setSectionColor(Color.parseColor("#FF99CC00")));
        this.addSection(newSection("About", R.drawable.ic_help_outline_black_48dp,
                new FragmentAbout()).setSectionColor(Color.parseColor("#03a9f4")));

        // 처음 화면으로 보여줄 섹션 설정
        setDefaultSectionLoaded(0);

        // 서비스 컨트롤러 초기화 및 서비스 준비
        initServiceController();
        ServiceController.readyAllServices();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case BluetoothController.REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    // 사용자가 블루투스 디바이스를 선택한 경우 해당 디바이스에 연결시도
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothController.getInstance().connectTo(address);
                }
                break;

            case BluetoothController.REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    BluetoothController.getInstance().scanDevice();
                }
                break;

            case NotificationService.NOTIFICATION_SET:
                ServiceController.readyService(ServiceController.NOTIFICATION_SERVICE);
                break;

            case WifiDirectController.WiFI_DIRECT_SET:
                Log.d(TAG, "ActivityResult with WIFI_DIRECT");

                break;
        }
    }

    @Override
    protected void onResume() {
        /*
         * 사용자가 Notification(Accessibility) 를 직접 끄지 않는 경우,
         * 프로세스가 계속해서 상주하게 된다. 때문에 사용자가 다시 앱을 실행할 때,
         * NotificationService 를 제외한 다른 서비스들을 다시 실행시켜 주어야 한다.
         */
//        if (BluetoothTransferService.isEnabled == false) {
//            startService(new Intent(this, BluetoothTransferService.class));
//        }
        ServiceController.resumeAllServices();

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        ServiceController.stopAllServices();
//        stopService(new Intent(this, BluetoothTransferService.class));
//        stopService(new Intent(this, NotificationService.class));
//        WifiDirectController.getInstance().stopWifiDirectService();

        super.onDestroy();

//        BluetoothTransferService.isEnabled = false;
    }
}
