package thealphalabs.bluetooth;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import thealphalabs.alphaapp.IDataTransferService;
import thealphalabs.controller.BluetoothController;
import thealphalabs.controller.ServiceController;
import thealphalabs.util.IntentSender;

/**
 * Created by yeol on 15. 6. 9.
 */
public class BluetoothTransferHelper {

    private Context context;
    private final String TAG="BlueToothTransferHelper";
    private IDataTransferService mTransferSerivce;

    private ServiceConnection mConn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mTransferSerivce=IDataTransferService.Stub.asInterface(service);
            Log.d(TAG,"service connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mTransferSerivce=null;
        }
    };
    public BluetoothTransferHelper(Context context) {
        this.context=context;
    }

    public void StartConnection() {
        Intent localIntent=new Intent(context,BluetoothTransferService.class);
        IntentSender.getInstance().bindService(context,localIntent,mConn,Context.BIND_AUTO_CREATE);
    }

    public void transferMouseData(float x,float y, int action) {
        try {
            mTransferSerivce.transferMouseData(x,y,action);
        } catch (RemoteException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void transferStringData(String text) {
        try {
            mTransferSerivce.transferStringData(text);
        } catch (RemoteException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void transferNotificationData(String title, String text) {
        try {
            mTransferSerivce.transferNotificationData(title, text);
        } catch (RemoteException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    // @file_size   size in bytes.
    public void transferFileData(byte[] bytes, long file_size) {
        try {
            mTransferSerivce.transferFileData(bytes, file_size);
        } catch (RemoteException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void transferGyroData(float x, float y, float z) {
        if(mTransferSerivce == null)
            return;
        try {
            if (mTransferSerivce != null)
                mTransferSerivce.transferGyroData(x,y,z);
            else
                Log.e(TAG, "mTransferService is null");
        } catch (RemoteException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void transferAccelData(float x, float y, float z) {
        if(mTransferSerivce == null)
            return;
        try {
            if (mTransferSerivce != null)
                mTransferSerivce.transferAccelData(x, y, z);
            else
                Log.e(TAG, "mTransferService is null");
        } catch (RemoteException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void StopConnection() {
        IntentSender.getInstance().unbindService(context, mConn);
    }

    public void connectTo(String paramAddress) {
        Log.d(TAG, "connectTo : " + paramAddress);
        try {
            mTransferSerivce.connectTo(paramAddress);
        } catch (RemoteException e) {
            Log.d(TAG,"connectTo :"+ e.getMessage());
        }
    }
}
