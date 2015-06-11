package thealphalabs.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import thealphalabs.Interface.EventData;
import thealphalabs.alphaapp.EventDataType;
import thealphalabs.alphaapp.IDataTransferService;
import thealphalabs.util.ConnectionInfo;

/**
 * Created by yeol on 15. 6. 9.
 */
public class BluetoothTransferService extends Service{

    public final static String TAG="BLTService";
    private BluetoothAdapter mBluetoothAdapter;
    private final IBinder mBinder=new EventDataTransferBinder();
    private Context mContext;
    private ConnectionInfo mConnectionInfo=null;
    private BluetoothManager mBltManager;


    private class EventDataTransferBinder extends IDataTransferService.Stub {

        @Override
        public void PrapareToTransfer() throws RemoteException {
            if(mBltManager == null){
                SetupBTManager();
            }
            mBltManager.listening();
        }

        @Override
        public void transferMouseData(float x, float y) throws RemoteException {
            int type= EventDataType.EventMouse;
            mBltManager.SendEventDataToGlass(x,y,type);
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
        mContext=getApplicationContext();
        Init();
    }

    private void Init(){
        Log.d(TAG, "# Service : initialize ---");

        // Get connection info instance
  //      mConnectionInfo = ConnectionInfo.getInstance(mContext);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            // BT is not on, need to turn on manually.
            // Activity will do this.
        } else {
            SetupBTManager();
        }
    }
    public void SetupBTManager()
    {
        Log.d(TAG,"make bluetooth manager");
        if(mBltManager == null){
            mBltManager = new BluetoothManager();
        }
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

    }
    /*   public void connectDevice(String address) {
        Log.d(TAG, "Service - connect to " + address);

        // Get the BluetoothDevice object
        if(mBluetoothAdapter != null) {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

            if(device != null && mBltManager != null) {
                mBltManager.connect(device);
            }
        }
    }
    public void connectDevice(BluetoothDevice device) {
        if(device != null && mBtManager != null) {
            mBltManager.connect(device);
        }
    }*/
}
