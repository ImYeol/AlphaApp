package thealphalabs.alphaapp;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import thealphalabs.notification.NotificationService;
import thealphalabs.bluetooth.BluetoothTransferHelper;
import thealphalabs.wifip2p.WifiDirectConnectionManager;
import thealphalabs.wifip2p.WifiTransferHelper;

/**
 * Created by yeol on 15. 6. 8.
 */
public class AlphaApplication extends Application {

    private final String TAG="AlphaApplication";
    private BluetoothTransferHelper mBluetoothTransferHelper;
    private WifiTransferHelper mWifiTransferHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }
    public void init() {
        Log.d(TAG, "Init the bluetoothTransfer Helper");

        // 서비스 실행 부분
        // 나머지 서비스는 Manifest 에서 enabled 설정되어 앱 실행시 자동으로 실행된다.
        startService(new Intent(this, NotificationService.class));

        mBluetoothTransferHelper=new BluetoothTransferHelper(getBaseContext());
        mBluetoothTransferHelper.StartConnection();

        WifiDirectConnectionManager.getInstance(this).registerBroadCastReceiver();
    }



    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public BluetoothTransferHelper getBluetoothHelper() {
        if (mBluetoothTransferHelper == null) {
            mBluetoothTransferHelper = new BluetoothTransferHelper(this.getBaseContext());
        }
        return mBluetoothTransferHelper;
    }

    public WifiTransferHelper getWifiHelper() {
        return mWifiTransferHelper;
    }
}
