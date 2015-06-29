package thealphalabs.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import thealphalabs.alphaapp.IDataTransferService;
import thealphalabs.controller.BluetoothController;
import thealphalabs.util.EventDataType;

/**
 * Created by yeol on 15. 6. 9.
 */
public class BluetoothTransferService extends Service {
    public final static String TAG = "BLT_TransferService";

    private static BluetoothTransferService instance;
    public  static BluetoothTransferService getInstance() {
        if (instance == null) {
            Log.e(TAG, "[FATAL] Service is not running.");
        }
        return instance;
    }

    private BluetoothAdapter    mBluetoothAdapter;
    private BluetoothController mBltController;

    private final IBinder mBinder = new EventDataTransferBinder();

    private class EventDataTransferBinder extends IDataTransferService.Stub {

        @Override
        public void connectTo(String paramAddress) throws RemoteException {
            mBltController.connectTo(paramAddress);
        }

        @Override
        public void transferMouseData(float x, float y, int pressure) throws RemoteException {
            int type = EventDataType.EventMouse;
            if (isAvailableToTransfer()) {
                try {
                    mBltController.SendEventDataToGlass(x, y, type, pressure);
                } catch(Exception e) {
                    Log.e(TAG, "Failed to transfer Mouse data" + e);
                    if (mBltController == null) {
                        Log.d(TAG, "Null");
                    }
                }
            }
        }

        @Override
        public void transferStringData(String text) throws RemoteException {
            int type= EventDataType.EventText;
            if (isAvailableToTransfer())
                mBltController.SendEventDataToGlass(text,type);
        }

        @Override
        public void transferGyroData(float x, float y, float z) throws RemoteException {
            int type= EventDataType.EventGyro;
            if (isAvailableToTransfer())
                mBltController.SendEventDataToGlass(x,y,z,type);
        }

        @Override
        public void transferAccelData(float x, float y, float z) throws RemoteException {
            int type= EventDataType.EventAccel;
            if (isAvailableToTransfer())
                mBltController.SendEventDataToGlass(x,y,z,type);
        }

        @Override
        public void transferNotificationData(String title, String text) {
            int type = EventDataType.EventNotification;
            if (isAvailableToTransfer())
                mBltController.SendEventDataToGlass(title + "\t\t" + text, type);
        }

        @Override
        public void transferFileData(byte[] bytes, long file_size) {
            int type = EventDataType.EventFileTransfer;
            if (isAvailableToTransfer())
                mBltController.SendFileDataToGlass(bytes, file_size, type);
        }

    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        // 서비스 초기화
        Init();

        // 멤버 변수 초기화
        instance = this;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
    }

    private void Init(){
        Log.d(TAG, "# Service : initialize ---");

        // Get local Bluetooth adapter
        mBluetoothAdapter   = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return;
        }

        mBltController      = BluetoothController.getInstance();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private boolean isAvailableToTransfer() {
        if (BluetoothController.getInstance().getState() != BluetoothController.STATE_CONNECTED) {
            return false;
        }
        return true;
    }
}
