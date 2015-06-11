package thealphalabs.alphaapp;


import android.content.Context;

import thealphalabs.bluetooth.BluetoothTransferHelper;
import thealphalabs.wifip2p.WifiTransferHelper;

/**
 * Created by yeol on 15. 6. 8.
 */
public class DataTransferService {

    private static BluetoothTransferHelper mBltTransferHelper;
    private static WifiTransferHelper mWifiTransferHelper;
    private static DataTransferService ManagerInstance;
    private Context context;

    public DataTransferService(Context context){
        this.context=context;
        mBltTransferHelper= BluetoothTransferHelper.getInstance();
        mWifiTransferHelper = WifiTransferHelper.getInstance();
    }

    public static DataTransferService getInstance(Context context){
        if(ManagerInstance == null )
            ManagerInstance= new DataTransferService(context);
        return ManagerInstance;
    }

    public void transferMouseData(float x, float y) {

        mBltTransferHelper.transferMouseData(x,y);
    }

    public void transferStringData(String text) {

        mBltTransferHelper.transferStringData(text);
    }

    public void transferGyroData(float x, float y, float z)  {
        mBltTransferHelper.transferGyroData(x,y,z);
    }

    public void transferAccelData(float x, float y, float z) {
        mBltTransferHelper.transferAccelData(x,y,z);
    }

    public void transferAPKData()
    {

    }
    public void StartBluetoothConnection() {
        mBltTransferHelper.StartConnection(context);
    }
    public void StartWifiP2PConnection() {

        mWifiTransferHelper.StartConnection(context);
    }

    public void StopBluetoothConnection(){

        mBltTransferHelper.StopConnection(context);
    }
    public void StopWifiP2PConnection(){

        mWifiTransferHelper.StopConnection(context);
    }

}
