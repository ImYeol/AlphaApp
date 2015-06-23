package thealphalabs.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

import thealphalabs.alphaapp.IDataTransferService;
import thealphalabs.util.ConnectionInfo;
import thealphalabs.util.Constants;
import thealphalabs.util.EventDataType;
import thealphalabs.util.IntentSender;

/**
 * Created by yeol on 15. 6. 9.
 */
public class BluetoothTransferService extends Service{

    public final static String TAG="BLTService";
    private BluetoothAdapter mBluetoothAdapter;
    private ConnectionInfo mConnectionInfo=null;
    private BluetoothManager mBltManager;
    private BluetoothConnectionReceiver mBluetoothConnectionReceiver;
    private final IBinder mBinder=new EventDataTransferBinder();

    private class EventDataTransferBinder extends IDataTransferService.Stub {

        @Override
        public void connectTo(String paramAddress) throws RemoteException {
            mBltManager.connectTo(paramAddress);
        }

        @Override
        public void transferMouseData(float x, float y, int pressure) throws RemoteException {
            int type= EventDataType.EventMouse;
            mBltManager.SendEventDataToGlass(x,y,type,pressure);
        }

        @Override
        public void transferStringData(String text) throws RemoteException {
            int type= EventDataType.EventText;
            mBltManager.SendEventDataToGlass(text,type);
        }

        @Override
        public void transferGyroData(float x, float y, float z) throws RemoteException {
            int type= EventDataType.EventGyro;
            mBltManager.SendEventDataToGlass(x,y,z,type);
        }

        @Override
        public void transferAccelData(float x, float y, float z) throws RemoteException {
            int type= EventDataType.EventAccel;
            mBltManager.SendEventDataToGlass(x,y,z,type);
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Init();
    }

    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
    }

    private void Init(){
        Log.d(TAG, "# Service : initialize ---");

        // Get connection info instance
        mConnectionInfo = ConnectionInfo.getInstance(this);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return;
        }
        if(!mBluetoothAdapter.isEnabled()) {
            // BT is not on, need to turn on manually.
            // Activity will do this.
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBtIntent);
        } else {
            SetupBTManager();
        }
        registerBluetoothConnectionReceiver();
    }
    public void SetupBTManager() {
        Log.d(TAG, "make bluetooth manager");
        if(mBltManager == null){
            mBltManager=BluetoothManager.getInstance(this);
        }
    }

    public void registerBluetoothConnectionReceiver() {
        mBluetoothConnectionReceiver=new BluetoothConnectionReceiver();
        IntentFilter localIntentFilter=new IntentFilter();
        localIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        localIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        this.registerReceiver(mBluetoothConnectionReceiver, localIntentFilter);
    }

    public void unRegisterBluetoothConnectionReceiver() {
        this.unregisterReceiver(mBluetoothConnectionReceiver);
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        DestroyService();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        DestroyService();
        return super.onUnbind(intent);
    }

    public void DestroyService() {
        Log.d(TAG,"Destroy Service --");
        mBluetoothAdapter=null;
        if(mBltManager != null)
            mBltManager.Destroy();
        mBltManager = null;
        unRegisterBluetoothConnectionReceiver();

    }

    public class BluetoothConnectionReceiver extends BroadcastReceiver {

        private final String TAG="BLTReceiver";

        public BluetoothConnectionReceiver() {
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String address = device.getAddress();
            Log.d(TAG,"onReceive action:"+action+" name:"+device.getName());

            if (TextUtils.isEmpty(address))
                return;

            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
            {
                mBltManager.Destroy();
                reconnect(context, address);
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
            {
                String lastRequestAddress = mConnectionInfo.getDeviceAddress();
                if(ShouldBeConnected(lastRequestAddress,address)) {
                    mConnectionInfo.setDeviceAddress(address);
                    mConnectionInfo.setDeviceName(device.getName());
                }
                mBltManager.setState(BluetoothManager.STATE_CONNECTED);
            }
        }

        private boolean ShouldBeConnected(String paramLastRequestAddress,String paramCurAddress) {
            return TextUtils.isEmpty(paramLastRequestAddress)
                    || !paramLastRequestAddress.equals(paramCurAddress);
        }

        private void reconnect(Context paramContext, String paramAddress) {
            String lastConnectAddress = mConnectionInfo.getDeviceAddress();
            if (TextUtils.isEmpty(lastConnectAddress))
                return;

            // 연결이 끊기면 1분 마다 스캔을 다시 한다.
            if (paramAddress.equals(lastConnectAddress)) {
                Log.d(TAG,"autoReconnect");
                mBltManager.autoReconnect();
            }
        }

    }

}
