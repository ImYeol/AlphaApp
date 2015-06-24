package thealphalabs.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Handler;

/**
 * Created by yeol on 15. 6. 11.
 */
public class BluetoothManager {

    private UUID MY_UUID = UUID.fromString("D04E3068-E15B-4482-8306-4CABFA1726E7");
    private BluetoothAdapter mBluetoothAdapter;
    private final String TAG="BluetoothManager";
    private int mState;
    private ConnectThread mConnectThread;
    private ReConnectionService mReConnectionService;
    private ArrayList<BluetoothDevice> mBltDevices;
    private DataOutputStream mDataOutputStream;
    private OutputStream mOutStream;
    private Context mContext;
    private static BluetoothManager instance;

    private final ReadWriteLock lock=new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    // State constants
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_CONNECTING = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device


    private BluetoothManager(Context paramContext){
        mState=STATE_NONE;
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        mContext=paramContext;
        mReConnectionService=ReConnectionService.getInstance(mContext,this);

    }

    public static BluetoothManager getInstance(Context paramContext) {
        if(instance == null) {
            instance=new BluetoothManager(paramContext);
        }
        return instance;
    }

    public synchronized void connect(BluetoothDevice device) {
        int localState=getState();

        Log.d(TAG, "Connecting to: " + device + ", current state = " + localState);

        if (localState == STATE_CONNECTED)
            return ;

        // Cancel any thread attempting to make a connection
        if (localState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
        mReConnectionService.stopReconnect();

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    public void connectTo(String paramAddress) {
        Log.d(TAG, "Service - connect to " + paramAddress);

        // Get the BluetoothDevice object
        if(mBluetoothAdapter != null) {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(paramAddress);
            connect(device);
        }
    }

    public void setState(int state){
        writeLock.lock();
        mState=state;
        writeLock.unlock();
    }

    public int getState(){
        int state;
        readLock.lock();
        state=mState;
        readLock.unlock();
        return state;
    }

    public void Destroy() {
        Log.d(TAG, "BltManager Destroy--");
        if(mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        setState(STATE_NONE);
    }
    
    public void SendEventDataToGlass(float x,float y,int type,int pressure)
    {
        if( getState() == STATE_CONNECTED )
        {
            try {
                mDataOutputStream.writeInt(type);
                mDataOutputStream.writeInt(pressure);
                mDataOutputStream.writeFloat(x);
                mDataOutputStream.writeFloat(y);
                mDataOutputStream.flush();
            } catch (IOException e) {
                Log.d(TAG,"sendData Error : "+e.getMessage());
            }
        }
    }
    public void SendEventDataToGlass(float x,float y,float z,int type)
    {
        if( getState() == STATE_CONNECTED )
        {
            try {
                mDataOutputStream.writeInt(type);
                mDataOutputStream.writeFloat(x);
                mDataOutputStream.writeFloat(y);
                mDataOutputStream.writeFloat(z);
                mDataOutputStream.flush();
            } catch (IOException e) {
                Log.d(TAG,"sendData Error : "+e.getMessage());
            }
        }
    }
    public void SendEventDataToGlass(String text,int type)
    {
        if( getState() == STATE_CONNECTED )
        {
            try {
                mDataOutputStream.writeInt(type);
                mDataOutputStream.writeUTF(text);
                mDataOutputStream.flush();
            } catch (IOException e) {
                Log.d(TAG,"sendData Error : "+e.getMessage());
            }
        }
    }
    public void SendFileDataToGlass(byte[] bytes, long file_size, int type) {
        if( getState() == STATE_CONNECTED )
        {
            try {
                mDataOutputStream.writeInt(type);
                mDataOutputStream.writeLong(file_size);
                mDataOutputStream.write(bytes);
                mDataOutputStream.flush();
            } catch (IOException e) {
                Log.d(TAG,"sendData Error : "+e.getMessage());
            }
        }
    }

    public void autoReconnect() {
        mReConnectionService.autoReconnect();
    }

    public void stopReconnect() {
        mReConnectionService.stopReconnect();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
            setState(STATE_CONNECTING);
        }

        public void run() {
            mBluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                return;
            }
            setState(STATE_CONNECTED);
            getStream();
            mConnectThread=null;
            mReConnectionService.stopReconnect();
        }
        public void getStream() {
            OutputStream tmpOut;
            try {
                tmpOut = mmSocket.getOutputStream();
                mOutStream = tmpOut;
                mDataOutputStream=new DataOutputStream(mOutStream);
            } catch (IOException e) {
                Log.d(TAG,"socket.getInputStream : "+e.getMessage());
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }	// End of class ConnectThread

    public void connectionFailed() {
        setState(STATE_NONE);
        mReConnectionService.autoReconnect();
    }
}
