package thealphalabs.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import thealphalabs.Interface.ServiceControllerInterface;
import thealphalabs.alphaapp.AlphaApplication;
import thealphalabs.alphaapp.R;
import thealphalabs.bluetooth.BluetoothBdReceiver;
import thealphalabs.bluetooth.BluetoothTransferService;
import thealphalabs.bluetooth.DeviceListActivity;
import thealphalabs.bluetooth.ReConnectionService;

/**
 * @author Sukbeom Kim
 *
 * 블루투스 관련 서비스를 관리한다.
 * 블루투스에 대한 상태 정보 뿐만 아니라 블루투스에 대한 BroadcastReceiver를 갖고 있다.
 *
 * 기존의 BluetoothTransferService가 갖고 있던 autoconnect 기능이 이 클래스로 옮기고
 * BluetoothTransferService와의 호환성을 고려해서 수정하였다.
 */
public class BluetoothController implements ServiceControllerInterface{
    private static final String TAG     = "BluetoothController";
    private static BluetoothController instance;

    private Context mContext;

    // 이하 변수들은 모두 블루투스 관련 변수들
    // Set bluetooth protocol : Serial port service protocol
    public static final UUID    MY_UUID                =
            UUID.fromString("D04E3068-E15B-4482-8306-4CABFA1726E7");
//            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static final int     REQUEST_CONNECT_DEVICE = 1;
    public static final int     REQUEST_ENABLE_BT      = 2;

    // 블루투스 관련 멤버변수들
    private BluetoothAdapter    btAdapter;
    // 상태 변수
    private int mState;

    private ReConnectionService mReConnectionService;
    private DataOutputStream    mDataOutputStream;
    private OutputStream        mOutStream;

    // 블루투스 연결 관련 쓰레드
    private ConnectThread   mConnectThread;

    // 블루투스 관련 Broadcast receiver
    BluetoothBdReceiver     mReceiver;
    IntentFilter            mIntentFilter;

    // 블루투스 통신을 통한 데이터 전송 서비스

    // BluetoothAdapter 클래스 내에 있는 값과 호환성 위해 수정됨.
    public  static final int STATE_DISCONNECTED  = 0;
    public  static final int STATE_CONNECTING    = 1;
    public  static final int STATE_CONNECTED     = 2;
    public  static final int STATE_DISCONNECTING = 3;
    public  static final int STATE_NONE       = 4; // we're doing nothing
    public  static final int STATE_LISTEN     = 5; // now listening for incoming connections

    private ProgressDialog mDialog;
    public  static  Button mConnectBtn;

    //** Constructor와 GetInstance()
    BluetoothController(Context pContext) {
        mContext    = pContext;
        btAdapter   = BluetoothAdapter.getDefaultAdapter();
        mReConnectionService = new ReConnectionService(mContext, this);

        // Braodcast receiver를 등록한다.
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        mIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        mReceiver   = new BluetoothBdReceiver(mContext);
        mContext.registerReceiver(mReceiver, mIntentFilter);
    }

    public static BluetoothController getInstance(@Nullable Context pContext)
    {
        if(instance == null)
        {
            instance = new BluetoothController(pContext);
        }
        return instance;
    }

    public static BluetoothController getInstance() {
        assert instance == null;
        return instance;
    }

    //** 멤버 변수에 대한 Getter 와 Setter

    // 블루투스 상태 설정
    public synchronized void setState(int state) {
        if (state == getState()) return;
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
    }

    // 블루투스 상태 얻어오는 함수
    public synchronized int getState() {
        return mState;
    }

    //** 블루투스 통신 제어 메서드들

    // 블루투스를 활성화하기 위해 호출되는 메서드
    public void enableBluetooth()
    {
        Log.d(TAG, "Check the enabled Bluetooth");

        if (btAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth is enabled.");
        }
        else {
            Log.d(TAG, "Request for enabling bluetooth service.");
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity)mContext).startActivityForResult(i, REQUEST_ENABLE_BT);
        }
    }

    // 블루투스를 끄기 위해 호출되는 메서드
    public void disableBluetooth()
    {
        Log.d(TAG, "Disable bluetooth");

        btAdapter.disable();
    }

    /**
     * 블루투스 장치를 스캔하기 위해 호출되는 함수
     * 함수가 호출되면 DeviceListActivity가 실행되고 여기서 선택된 디바이스 정보는 GlassAppMain의
     * OnActivityResult에서 처리된다.
     */
    public void scanDevice() {
        if (isEnabled()) {
            Intent serverIntent = new Intent(mContext, DeviceListActivity.class);
            ((Activity) mContext).startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        }
    }

    public void getDeviceInfo(Intent data) {
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        Log.d(TAG, "Get Device Info : " + address);
        ((AlphaApplication)((Activity)mContext).getApplication()).getBluetoothHelper().connectTo(address);
    }

    /**
     * initThread
     *  블루투스를 연결하기 전에 관련된 쓰레드를 초기화한다.
     */
    private synchronized void initConnection() throws IOException {
        if (mDataOutputStream != null) {
            mDataOutputStream.close();
        }
        if (mOutStream != null) {
            mOutStream.close();
        }
    }

    // ConnectThread 초기화 device의 모든 연결 제거
    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "connect() to" + device);

        // 실행되고 있는 connect thread를 검사한다.
        if (mConnectThread != null) {
            // 실행되고 있는 thread가 있으면 종료하고 완전히 종료될 때까지 기다린다.
            mConnectThread.cancel();
        }

        while (mConnectThread != null) ;

        // 쓰레드 종료가 완료되면, 연결을 초기화 시킨다.
        // (스트림 닫음)
        try {
            initConnection();
        } catch (IOException e) {
            Log.e(TAG, "Failed to connect() " + e);
        }

        // 초기화가 완료되면 쓰레드를 새로 생성한다.
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    public synchronized void connectTo(String paramAddress) {
        // Get the BluetoothDevice object
        if(btAdapter != null) {
            BluetoothDevice device = btAdapter.getRemoteDevice(paramAddress);
            connect(device);
        }
    }

    public synchronized void disconnect() throws IOException {
        Log.d(TAG, "Disconnecting..");

        setState(STATE_DISCONNECTING);

        if (mConnectThread != null) {
            mConnectThread.cancel();
        }

        if (mOutStream != null)
            mOutStream.close();
        if (mDataOutputStream != null)
            mDataOutputStream.close();

        setState(STATE_DISCONNECTED);

        ReConnectionService.getInstance(mContext, this).stopReconnect();
    }

    // 모든 thread stop
    public synchronized void stopThread() {
        Log.d(TAG, "stop");

        try {
            initConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setState(STATE_NONE);
    }

    //** 연결 쓰레드 관련 메서드

    // 연결을 잃었을 때
    private void connectionLost() {
        Log.d(TAG, "Connection lost");
        setState(STATE_LISTEN);
    }

    //** ServiceControllerInterface 인터페이스 구현

    /**
     * start()
     *
     * TransferService에서 직접 멤버변수를 등록하기 때문에 Controller에서 초기화를 하기위해
     * 직접 따로 작업할 필요가 없음.
     * 여기서는 관련 멤버변수가 제대로 초기화되어있는지 검사한다.
     */
    @Override
    public void start() {
        Log.d(TAG, "start() called");

        // 블루투스 장치 확인
        if (!isEnabled()) {
            enableBluetooth();
        }
        // Trnasfer service 확인 및 시작
        if (BluetoothTransferService.getInstance() == null) {
            Log.e(TAG, "BluetoothTransferService is null");

            // 서비스가 실행중이지 않을 경우에는 다시 실행시킨다.
            mContext.startService(new Intent(mContext, BluetoothTransferService.class));
        }
    }

    @Override
    public void stop() {
        Log.d(TAG, "stop() called");

        if (BluetoothTransferService.getInstance() != null) {
            mContext.stopService(new Intent(mContext, BluetoothTransferService.class));
        }

        // Reconnect 가 실행중이면 종료
        mReConnectionService.stopReconnect();

        // 블루투스 연결 관련 쓰레드를 모두 종료시킨다.
        stopThread();

        // 블루투스 장치를 끈다.
        disableBluetooth();

        // 블루투스 Broadcast receiver를 종료한다.
        mContext.unregisterReceiver(mReceiver);
    }

    @Override
    public void resume() {
        Log.d(TAG, "resume() called");

        if (BluetoothTransferService.getInstance() == null) {
            // 서비스가 실행중이지 않을 경우에는 다시 실행시킨다.
            mContext.startService(new Intent(mContext, BluetoothTransferService.class));
        }
    }

    @Override
    public void pause() {
        // 아직 블루투스 컨트롤러가 pause 되는 경우는 없음.
        // 추후 사용을 위해서 남겨둔다.
    }

    @Override
    public void ready() {
        // 블루투스가 초기화 될 때를 정의한다.

        // 블루투스가 켜져 있는지 아닌지 알 수 없으므로 TransferService만 초기화한다.
        // Trnasfer service 확인 및 시작
        if (BluetoothTransferService.getInstance() == null) {
            Log.e(TAG, "BluetoothTransferService is null");

            // 서비스가 실행중이지 않을 경우에는 다시 실행시킨다.
            mContext.startService(new Intent(mContext, BluetoothTransferService.class));
        }
    }

    /**
     * isEnabled()
     * @return  장치가 켜져있다면 true를 반환한다.
     */
    @Override
    public boolean isEnabled() {
        return btAdapter.isEnabled();
    }

    // 쓰레드 정의
    private class ConnectThread extends Thread {
        private final String TAG = "ConnectThread";
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final int    MAX_TRY    = 3;

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
            boolean success = false;
            btAdapter.cancelDiscovery();

            for (int i = 0; i < MAX_TRY; i++) {
                try {
                    Log.d(TAG, "Trying to connect...");
                    mmSocket.connect();
                    success = true;

                    break;
                } catch (IOException e) {
                    Log.d(TAG, "Connection failed : " + e);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            if (!success) {
                connectionFailed();

                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                return;
            }
            else {

                Log.d(TAG, "Succeed to connect bluetooth device.");

                setState(STATE_CONNECTED);
                getStream();
                mConnectThread = null;
            }
        }

        public void getStream() {
            OutputStream tmpOut;
            try {
                tmpOut = mmSocket.getOutputStream();
                mOutStream = tmpOut;
                mDataOutputStream=new DataOutputStream(mOutStream);
            } catch (IOException e) {
                Log.d(TAG, "socket.getInputStream : " + e.getMessage());
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }

            mConnectThread = null;
        }
    }	// End of class ConnectThread

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

    public synchronized void connectionFailed() {
        setState(STATE_NONE);
    }
}
