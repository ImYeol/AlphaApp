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
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import thealphalabs.Interface.WifiDeviceListCallback;
import thealphalabs.util.IntentSender;
import thealphalabs.wifidirect.FileTransferService;
import thealphalabs.wifidirect.RarpImpl;

/**
 * Created by yeol on 15. 6. 11.
 */
public class WifiDirectConnectionManager {
    private final static String TAG="WifiDirectConnectionManager";

    public static final String WIFI_TRANSFER_PORT="wifi_port";
    public static final String WIFI_INFO="wifi_info";
    public static final String FILE_NAME="file_name";
    public static final String ClIENT_RESULT="client_result";

    private static final int port= 7950;

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
    private Intent clientServiceIntent;

    private boolean TransferActivation=false;
    private boolean IsDeviceConnected=false;
    private static String FileToSend;

    private String mP2pInterfaceName;
    private Timer mArpTableObservationTimer;

    private boolean IsAppBoot=false;

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
      //  if(mWifiManager.isWifiEnabled() == false)

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

    public void connectTo(String deviceName,String FileName) {
        FileToSend=FileName;
        connectTo(deviceName);
    }

    public void connectTo(String deviceName) {
        for(WifiP2pDevice device : wifiDevices) {
            if(deviceName.equals(device.deviceName)) {
                connectToPeer(device);
            }
        }
    }

    public void connectToDeviceIP(String ip) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = ip;
        Log.d(TAG,"WifiP2P connect IP!!!");
        mWifiP2pManager.connect(wifichannel, config, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                Log.d(TAG, "success to connect wifidevice IP");
                //if(IsDeviceConnected)
                //   startTransferService();
                //setClientStatus("Connection to " + targetDevice.deviceName + " sucessful");
            }

            public void onFailure(int reason) {
                //setClientStatus("Connection to " + targetDevice.deviceName + " failed");
                Log.d(TAG, "failed to connect wifidevice " + reason);
                IsDeviceConnected=false;
            }
        });
    }
    public void connectToPeer(final WifiP2pDevice wifiPeer) {
        this.targetDevice = wifiPeer;

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = wifiPeer.deviceAddress;
        Log.d(TAG,"WifiP2P connect !!!");
        mWifiP2pManager.connect(wifichannel, config, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                Log.d(TAG, "success to connect wifidevice");
                IsAppBoot=true;
                startTransfer();
                //if(IsDeviceConnected)
                 //   startTransferService();
                //setClientStatus("Connection to " + targetDevice.deviceName + " sucessful");
            }

            public void onFailure(int reason) {
                //setClientStatus("Connection to " + targetDevice.deviceName + " failed");
                Log.d(TAG, "failed to connect wifidevice " + reason);
                IsDeviceConnected=false;
            }
        });
    }

    class WifiDirectBroadcastReceiver extends BroadcastReceiver {

        private final String TAG = "WifiDirectReceiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // 와이파이 다이렉트가 켜진 경우
                    Log.d(TAG,"wifi state enabled");

                } else {
                    // 와이파이 다이렉트가 꺼진 경우
                    Log.d(TAG,"wifi state unable");
                }

            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                Log.d(TAG,"wifi peer changed");
                 mWifiP2pManager.requestPeers(wifichannel, new WifiP2pManager.PeerListListener() {

                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        ArrayList<String> localDevices=new ArrayList<String>();
                        wifiDevices.clear();
                        wifiDevices.addAll(peers.getDeviceList());
                        for(WifiP2pDevice device:wifiDevices) {
                            localDevices.add(device.deviceName);
                        }
                        if(mDeviceListCallback !=null)
                            mDeviceListCallback.getWifiDevices(localDevices);
                    }
                });
                //update UI with list of peers
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                Log.d(TAG,"wifi connection changed");
                NetworkInfo networkState = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                wifiInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
                targetDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);

                if(networkState.isConnected())
                {
                    Log.d(TAG,"networkstat is connected");
                    IsDeviceConnected=true;
                    //set client state so that all needed fields to make a transfer are ready
                    startTransfer();
                }
                else
                {
                    //set variables to disable file transfer and reset client back to original state
                    mWifiP2pManager.cancelConnect(wifichannel, null);
                    IsDeviceConnected=false;
                    Settings.System.putInt(mContext.getContentResolver(),"show_touches",0);
                }
                //activity.setClientStatus(networkState.isConnected());

                // Respond to new connection or disconnections
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                Log.d(TAG, "Device state is changed");
                mWifiP2pManager.discoverPeers(wifichannel,new ActionListenerAdapter());
            }
        }
    }

    private void startTransferService(){
        clientServiceIntent = new Intent(mContext, FileTransferService.class);
        clientServiceIntent.putExtra(FILE_NAME, FileToSend);
        clientServiceIntent.putExtra(WIFI_TRANSFER_PORT, port);
        clientServiceIntent.putExtra(WIFI_INFO, wifiInfo);
        clientServiceIntent.putExtra(ClIENT_RESULT, new ResultReceiver(null) {
            @Override
            protected void onReceiveResult(int resultCode, final Bundle resultData) {

                if(resultCode == port )
                {
                    if (resultData == null) {
                        //Client service has shut down, the transfer may or may not have been successful. Refer to message
                        Log.d(TAG,"failed to transfer or not");
                    }
                    TransferActivation=false;
                }

            }
        });
        TransferActivation=true;
        IntentSender.getInstance().startService(mContext,clientServiceIntent);
        
    }

    public void stopTransferService(){
        if(clientServiceIntent ==null)
            clientServiceIntent=new Intent(mContext, FileTransferService.class);
        IntentSender.getInstance().stopService(mContext,clientServiceIntent);
    }

    class ActionListenerAdapter implements WifiP2pManager.ActionListener {

        public void onSuccess() {
            Log.d(TAG,"discover success");
        }

        // 失敗
        public void onFailure(int reason) {
            Log.d(TAG,"discover failed");
        }
    }

  /*  private boolean checkResource(){
        //if()
    }*/
    private void startTransfer() {

        mWifiP2pManager.requestGroupInfo(wifichannel, new WifiP2pManager.GroupInfoListener() {
            // requestGroupInfo()実行後、非同期応答あり
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                Log.d(TAG, "onGroupInfoAvailable():");
                if (group == null) {
                    Log.d(TAG, "group is NULL!");
                    return;
                }
                // connect
                if (group.isGroupOwner()) { // G.O. don't know client IP, so check /proc/net/arp
                    mP2pInterfaceName = group.getInterface();

                    mArpTableObservationTimer = new Timer();
                    ArpTableObservationTask task = new ArpTableObservationTask();
                    mArpTableObservationTimer.scheduleAtFixedRate(task, 10, 1*1000); // 10ms後から1秒間隔でarpファイルをチェック
                } else { // this device is not G.O. get G.O. address
                   requestConnectionInfo();
                }
            }
        });
    }

    private void requestConnectionInfo(){
        mWifiP2pManager.requestConnectionInfo(wifichannel, new WifiP2pManager.ConnectionInfoListener() {
            // requestConnectionInfo()実行後、非同期応答あり
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                Log.d(TAG,"　onConnectionInfoAvailable():");

                Log.d(TAG,"groupFormed:" + info.groupFormed);
                Log.d(TAG,"IsGroupOwner:" + info.isGroupOwner);
                Log.d(TAG,"groupOwnerAddress:" + info.groupOwnerAddress);

                if (!info.groupFormed) {
                    Log.d(TAG,"  not yet groupFormed!");
                    return;
                }

                if (info.isGroupOwner) {
                    Log.d(TAG,"  I'm G.O.? Illegal State!!");
                    return;
                } else {
                    wifiInfo=info;
                    if(IsAppBoot)
                        startTransferService();
                }
            }
        });
    }

    private int mArpRetryCount = 0;
    private final int MAX_ARP_RETRY_COUNT = 60;

    class ArpTableObservationTask extends TimerTask {
        @Override
        public void run() {
            // arpテーブル読み込み
            RarpImpl rarp = new RarpImpl();
            String source_ip = rarp.execRarp(mP2pInterfaceName);

            // リトライ
            if (source_ip == null) {
                Log.d(TAG, "retry:" + mArpRetryCount);
                if (++mArpRetryCount > MAX_ARP_RETRY_COUNT) {
                    mArpTableObservationTimer.cancel();
                    return;
                }
                return;
            }
            mArpTableObservationTimer.cancel();
            connectToDeviceIP(source_ip);
        }
    }


}
