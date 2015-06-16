package thealphalabs.alphaapp;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import thealphalabs.bluetooth.BluetoothTransferHelper;
import thealphalabs.bluetooth.BluetoothTransferService;
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
        startService(new Intent(this, BluetoothTransferService.class));
        mBluetoothTransferHelper=new BluetoothTransferHelper(getBaseContext());
      //  mWifiTransferHelper=new WifiTransferHelper(getBaseContext());
        mBluetoothTransferHelper.StartConnection();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        stopService(new Intent(this, BluetoothTransferService.class));
    }

    public BluetoothTransferHelper getBluetoothHelper() {
        return mBluetoothTransferHelper;
    }

    public WifiTransferHelper getWifiHelper() {
        return mWifiTransferHelper;
    }
}
