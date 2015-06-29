package thealphalabs.wifidirect;/*
 WiFi Direct File Transfer is an open source application that will enable sharing 
 of data between Android devices running Android 4.0 or higher using a WiFi direct
 connection without the use of a separate WiFi access point.This will enable data 
 transfer between devices without relying on any existing network infrastructure. 
 This application is intended to provide a much higher speed alternative to Bluetooth
 file transfers. 

 Copyright (C) 2012  Teja R. Pitla
 Contact: teja.pitla@gmail.com

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

import thealphalabs.controller.WifiDirectController;

public class WifiDirectBdReceiver extends BroadcastReceiver {
    private final String TAG = "WifiDirectBdReceiver";

    // Wifi direct 관련 멤버변수
    private Channel               channel;
    private WifiP2pManager        manager;
    private Context               mContext;

    // 액티비티 관련 변수
    private Activity              mActivity;
    public  void setActivity(Activity pActivity) { mActivity = pActivity; }
    public  Activity getActivity() { return mActivity; }

    public WifiDirectBdReceiver(WifiP2pManager pWifiManager, Channel pChannel, Context pContext) {
        manager  = pWifiManager;
        channel  = pChannel;
        mContext = pContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
//                activity.setClientWifiStatus("Wifi Direct is enabled");
                // 와이파이 다이렉트가 켜진 경우
                WifiDirectController.turnOn();
            } else {
//                activity.setClientWifiStatus("Wifi Direct is not enabled");
                // 와이파이 다이렉트가 꺼진 경우
                WifiDirectController.turnOff();
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {

                public void onPeersAvailable(WifiP2pDeviceList peers) {

                    ((WifiDirect_DeviceList)mActivity).displayPeers(peers);

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

                //activity.setTransferStatus(true);
//                activity.setNetworkToReadyState(true, wifiInfo, device);
//                activity.setClientStatus("Connection Status: Connected");


            }
            else
            {
                //set variables to disable file transfer and reset client back to original state

//                activity.setTransferStatus(false);
//                activity.setClientStatus("Connection Status: Disconnected");


                manager.cancelConnect(channel, null);
            }
            //activity.setClientStatus(networkState.isConnected());

            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            Log.d(TAG, "Device state is changed");
        }
    }
}