package thealphalabs.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by yeol on 15. 6. 11.
 */
public class BluetoothManager {

    private UUID MY_UUID = UUID.fromString("D04E3068-E15B-4482-8306-4CABFA1726E7");
    private AcceptThread mAcceptThread;
    private BluetoothAdapter mBluetoothAdapter;
    private final String TAG="BluetoothManager";
    private BluetoothServerSocket mBluetoothServerSocket;
    private OutputStream mOutStream;
    private int mState;
    private DataOutputStream mDataOutputStream;

    private final ReadWriteLock lock=new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    // State constants
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device



    public BluetoothManager{
        mState=STATE_NONE;
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    }
    public void listening() {
        Log.d(TAG, "Starting BluetoothManager...");

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);
       // mIsServiceStopped = false;
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

    public void SendEventDataToGlass(float x,float y,int type)
    {
        if( getState() == STATE_CONNECTED )
        {
            try {
                mDataOutputStream.writeInt(type);
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

    public void Destroy() {
        Log.d(TAG, "BltManager Destroy--");
        if(mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        setState(STATE_NONE);
    }

    private class AcceptThread extends Thread {

        public AcceptThread() {
            BluetoothServerSocket tmp;
            try {
                tmp = mBluetoothAdapter
                        .listenUsingRfcommWithServiceRecord(
                                "ClassicBluetoothServer", MY_UUID);
            } catch (IOException e) {
                Log.d(TAG, "getting listend sock is error");
            }
            mBluetoothServerSocket=tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mBluetoothServerSocket.accept(); // blocking call
                } catch (IOException e) {
                    Log.v(TAG, e.getMessage());
                    break;
                }
                Log.d(TAG, "socket is accepted");
                // If a connection was accepted
                if (socket != null) {
                    // Do work in a separate thread
                    setState(STATE_CONNECTED);
                    OutputStream tmpOut=null;
                    try{
                        tmpOut=socket.getOutputStream();
                    } catch (IOException e) {
                        Log.d(TAG,"getOutputStream error :"+e.getMessage());
                    }
                    mOutStream=tmpOut;
                    getWrapperStream();
                }
            }
        }

        public void getWrapperStream() {
            mDataOutputStream= new DataOutputStream(mOutStream);
        }

        public void cancel(){
            Log.d(TAG, "cancel " + this);
            try {
                if(mBluetoothServerSocket != null)
                    mBluetoothServerSocket.close();

            } catch (IOException e) {
                Log.e(TAG, "close() of server failed" + e.toString());
            }
        }
    }

}
