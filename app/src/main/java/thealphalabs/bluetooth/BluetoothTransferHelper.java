package thealphalabs.bluetooth;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import thealphalabs.Interface.TransferHelperInterface;
import thealphalabs.alphaapp.IDataTransferService;
import thealphalabs.util.IntentSender;

/**
 * Created by yeol on 15. 6. 9.
 */
public class BluetoothTransferHelper implements TransferHelperInterface {

    private static BluetoothTransferHelper instance;
    private Context context;
    private IDataTransferService mTransferSerivce;
    private final String TAG="BlueToothTransferHelper";

    private ServiceConnection mConn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mTransferSerivce=IDataTransferService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mTransferSerivce=null;
        }
    };
    private BluetoothTransferHelper(){

    }

    public static BluetoothTransferHelper getInstance(){
        if(instance == null )
            instance= new BluetoothTransferHelper();
        return instance;
    }

    public void StartConnection(Context context){
        this.context=context;
        Intent localIntent=new Intent(context,BluetoothTransferService.class);
        IntentSender.getInstance().startService(context,localIntent);
        IntentSender.getInstance().bindService(context,localIntent,mConn,Context.BIND_AUTO_CREATE);
    }

    @Override
    public void StopConnection(Context context) {
        Intent localIntent=new Intent(context,BluetoothTransferService.class);
        IntentSender.getInstance().unbindService(context,mConn);
        IntentSender.getInstance().stopService(context, localIntent);
    }

    public void transferMouseData(float x, float y) {
        try {
            mTransferSerivce.transferMouseData(x,y);
        } catch (RemoteException e) {
            Log.d(TAG,e.getMessage());
        }
    }

    public void transferStringData(String text) {
        try {
            mTransferSerivce.transferStringData(text);
        } catch (RemoteException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void transferGyroData(float x, float y, float z) {
        try {
            mTransferSerivce.transferGyroData(x,y,z);
        } catch (RemoteException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void transferAccelData(float x, float y, float z) {
        try {
            mTransferSerivce.transferAccelData(x,y,z);
        } catch (RemoteException e) {
            Log.d(TAG, e.getMessage());
        }
    }
}
