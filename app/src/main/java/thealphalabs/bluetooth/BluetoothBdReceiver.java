package thealphalabs.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import thealphalabs.controller.BluetoothController;
import thealphalabs.util.ConnectionInfo;

/**
 * Created by chaoxifer on 6/28/15.
 */
public class BluetoothBdReceiver extends BroadcastReceiver {
    private final String TAG = "BltBdReceiver";

    private Context         mContext;
    private ConnectionInfo  mConnectionInfo;

    public BluetoothBdReceiver(Context context) {
        mContext        = context;
        mConnectionInfo = ConnectionInfo.getInstance(mContext);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action   = intent.getAction();


        BluetoothDevice device  = null;
        String          address = null;

        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
        {
            try {
                device  = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                address = device.getAddress();
            } catch (Exception e) {
                Log.e(TAG, "Exception : " + e);
            }

            String lastRequestAddress = mConnectionInfo.getDeviceAddress();

            if(ShouldBeConnected(lastRequestAddress,address)) {
                mConnectionInfo.setDeviceAddress(address);
                mConnectionInfo.setDeviceName(device.getName());
            }

            BluetoothController.getInstance().setState(BluetoothController.STATE_CONNECTED);
        }

        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
        {
            try {
                device  = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                address = device.getAddress();
            } catch (Exception e) {
                Log.e(TAG, "Exception : " + e);
            }

            reconnect(context, address);
        }

        else if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
            int state   = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,
                    BluetoothAdapter.STATE_DISCONNECTED);
            Log.d(TAG, "Bluetooth connection state is changed to " + state);

            BluetoothController.getInstance().setState(state);
        }
    }

    private boolean ShouldBeConnected(String paramLastRequestAddress,String paramCurAddress) {
        return TextUtils.isEmpty(paramLastRequestAddress)
                || !paramLastRequestAddress.equals(paramCurAddress);
    }

    private synchronized void reconnect(Context paramContext, String paramAddress) {
        String lastConnectAddress = mConnectionInfo.getDeviceAddress();
        if (TextUtils.isEmpty(lastConnectAddress))
            return;

        // 연결이 끊기면 1분 마다 스캔을 다시 한다.
        if (paramAddress.equals(lastConnectAddress)) {
            if (BluetoothController.getInstance().getState() == BluetoothController.STATE_NONE) {
                Log.d(TAG, "reconnect is needed.");
//                BluetoothController.getInstance().autoReconnect();
            }
        }
    }
}