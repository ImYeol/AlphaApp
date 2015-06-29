package thealphalabs.wifip2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

import thealphalabs.Interface.WifiDeviceListCallback;
import thealphalabs.controller.WifiDirectController;

/**
 * Created by yeol on 15. 6. 11.
 */
public class WifiDirectConnectionManager {
    private final static String TAG="WifiDirectConnectionManager";

    private static ArrayList<WifiP2pDevice> wifiDevices=new ArrayList<WifiP2pDevice>();
    private static WifiDirectConnectionManager instance;
    private Context mContext;
    private WifiP2pManager mWifiP2pManager;
    private WifiManager mWifiManager;
    private WifiP2pManager.Channel  wifichannel;
    private WifiP2pDevice targetDevice;
    private WifiP2pInfo wifiInfo;
    private WifiDirectBroadcastReceiver wifiClientReceiver;
    private IntentFilter wifiClientReceiverIntentFilter;
    private WifiDeviceListCallback mDeviceListCallback;

    private boolean IsScreenCast=false;

    public static WifiDirectConnectionManager getInstance(Context context) {
        if (instance == null) {
            instance = new WifiDirectConnectionManager(context);
        }
        return instance;

    }

    public static WifiDirectConnectionManager getInstance() {
        assert instance == null;
        return instance;
    }

    private WifiDirectConnectionManager(@Nullable Context context) {
        mContext = context;
        init();

    }

    private void init() {
        mWifiP2pManager = (WifiP2pManager)mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        mWifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        wifichannel = mWifiP2pManager.initialize(mContext, mContext.getMainLooper(), null);
       // registerBroadCastReceiver();
    }

    public void TurnOnWifi(){
        if(mWifiManager.isWifiEnabled() == false)

    }

    public void TurnOffWifi() {

    }
    public void registerBroadCastReceiver() {

        wifiClientReceiver = new WifiDirectBroadcastReceiver();
        wifiClientReceiverIntentFilter = new IntentFilter();
        wifiClientReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiClientReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiClientReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiClientReceiverIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mContext.registerReceiver(wifiClientReceiver, wifiClientReceiverIntentFilter);

    }

    public void registerWifiDeviceCallback(WifiDeviceListCallback paramCallback) {
        this.mDeviceListCallback=paramCallback;
    }

    public void unRegisterWifiDeviceCallback(){
        this.mDeviceListCallback=null;
    }

    public void unRegisterBroadCastReceiver() {
        mContext.unregisterReceiver(wifiClientReceiver);
    }

    public ArrayList<String> getWifiDevices() {
        ArrayList<String> localDevices=new ArrayList<String>();
        for(WifiP2pDevice device:wifiDevices) {
            localDevices.add(device.deviceName);
        }
        return localDevices;
    }

    public void connectTo(String deviceName) {
        for(WifiP2pDevice device : wifiDevices) {
            if(deviceName.equals(device.deviceName)) {
                connectToPeer(device);
            }
        }
    }

    public void connectToPeer(final WifiP2pDevice wifiPeer) {
        this.targetDevice = wifiPeer;

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = wifiPeer.deviceAddress;
        Log.d(TAG,"WifiP2P connect !!!");
        mWifiP2pManager.connect(wifichannel, config, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                Log.d(TAG, "success to connect wifidevice");
                //setClientStatus("Connection to " + targetDevice.deviceName + " sucessful");
            }

            public void onFailure(int reason) {
                //setClientStatus("Connection to " + targetDevice.deviceName + " failed");
                Log.d(TAG, "failed to connect wifidevice " + reason);
            }
        });
    }

    class WifiDirectBroadcastReceiver extends BroadcastReceiver {

        private final String TAG = "WifiDirectBroadcastReceiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // 와이파이 다이렉트가 켜진 경우

                } else {
                    // 와이파이 다이렉트가 꺼진 경우
                }

            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

                 mWifiP2pManager.requestPeers(wifichannel, new WifiP2pManager.PeerListListener() {

                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        ArrayList<String> localDevices=new ArrayList<String>();
                        wifiDevices.clear();
                        for(WifiP2pDevice device:peers) {
                            wifiDevices.add(device);
                            localDevices.add(device.deviceName);
                        }
                        mDeviceListCallback.getWifiDevices(localDevices);
                    }
                });
                //update UI with list of peers
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                NetworkInfo networkState = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                WifiP2pInfo wifiInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
                WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);

                if(networkState.isConnected())
                {
                    //set client state so that all needed fields to make a transfer are ready
                }
                else
                {
                    //set variables to disable file transfer and reset client back to original state
                    mWifiP2pManager.cancelConnect(wifichannel, null);
                }
                //activity.setClientStatus(networkState.isConnected());

                // Respond to new connection or disconnections
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                Log.d(TAG, "Device state is changed");
            }
        }
    }

}
