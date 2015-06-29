package thealphalabs.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;

import thealphalabs.Interface.ServiceControllerInterface;
import thealphalabs.alphaapp.R;
import thealphalabs.wifidirect.WifiDirectBdReceiver;

/**
 * WifiDirectController
 *  Wifi-Direct 위한 컨트롤러
 *      1. Wifi 가 켜져 있는지 상태 확인하고 관련 정보를 저장한다.
 *
 *  @author     Sukbeom Kim
 *  @version    1.0
 */
public class WifiDirectController implements ServiceControllerInterface {
    private static final String TAG = "WifiDirectController";

    public final static int WiFI_DIRECT_SET  = 6;
    public static boolean   flag             = false;            // Wifi direct가 켜져있으면 flag를 on으로 한다.
    public static boolean   serviceIsEnabled = false;            // 서비스의 재시작 여부를 판단하기 위한 flag

    private static WifiDirectController instance = null;          // 외부에서 Controller를 얻어오기 위한 instance
    public  static WifiDirectController getInstance() {
        assert instance == null;
        return instance;
    }
    public  static WifiDirectController getInstance(Context context) {
        if (instance == null) {
            instance = new WifiDirectController(context);
        }
        return instance;
    }

    // 와이파이 다이렉트를 위한 필요 멤버변수들
    private WifiP2pManager         mWifiManager;
    public  WifiP2pManager         getWifiManager() { return mWifiManager; }

    private WifiP2pManager.Channel mChannel;
    public  WifiP2pManager.Channel getChannel()      { return mChannel; }

    private WifiDirectBdReceiver mReceiver;
    public  WifiDirectBdReceiver      getReceiver()     { return mReceiver; }

    private IntentFilter           mIntentFilter;

    // UI 위한 ProgressDialog
    ArrayList<ProgressDialog> queueOfProgressDialog;

    // 상태 정보를 위한 변수들

    Context mContext;

    public WifiDirectController(Context context) {
        mContext    = context;

        initialize();
    }

    public void initialize() {
        instance         = this;
        serviceIsEnabled = true;

        mWifiManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel     = mWifiManager.initialize(mContext, Looper.getMainLooper(), null);
        mReceiver    = new WifiDirectBdReceiver(mWifiManager, mChannel, mContext);

        mIntentFilter   = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        queueOfProgressDialog = new ArrayList<>();
    }

    @Override
    protected void finalize() throws Throwable {
        stopWifiDirectService();

        super.finalize();
    }

    public void startWifiDirectService() {
        mContext.registerReceiver(mReceiver, mIntentFilter);
    }

    public void stopWifiDirectService() {
        mContext.unregisterReceiver(mReceiver);
    }

    @Override
    public void start() {
        /**
         * @description
         *  WifiDirectController 를 통해 사용될 ProgressDialog를 등록하면 나중에 BroadcastReceiver를 통해
         *  대신 dismiss 해준다.
         */

//        final ProgressDialog progressDialog = new ProgressDialog(mContext, ProgressDialog.STYLE_SPINNER);
//        progressDialog.setCancelable(false);
//        progressDialog.setTitle(R.string.progress_wifidirect_title);

        // WifiDirectController 에게 디바이스를 on 할 준비를 부탁한다.
//        WifiDirectController.prepareDeviceOn(progressDialog);
//
//        if (onFlag) {
//            progressDialog.setMessage(mContext.getResources().getString(R.string.progress_wifidirect_on));
//            progressDialog.show();
//
//            // ProgressDialog를 띄우고 wifi direct가 완전히 준비될 때까지 기다린다.
//            // 와이파이를 켠다.
//            try {
//                wifiManager.setWifiEnabled(true);
//            } catch (Exception e) {
//                Log.e(TAG, "Failed to turn on wifi");
//            }
//
//            expand(index);
//        }
//        else {
//            progressDialog.setMessage(mContext.getResources().getString(R.string.progress_wifidirect_off));
//            progressDialog.show();
//
//            // 와이파이를 끈다.
//            try {
//                wifiManager.setWifiEnabled(false);
//            } catch(Exception e) {
//                Log.e(TAG, "Failed to turn off wifi");
//            }
//
//            collapse(index);
//        }
    }

    @Override
    public void stop() {
        flag = false;
        stopWifiDirectService();
    }

    @Override
    public void resume() {
        // resume
    }

    @Override
    public void pause() {
        flag = false;
    }

    @Override
    public void ready() {
        startWifiDirectService();
    }

    /**
     * isEnabled()
     *  isEnabled(), turnOn(), turnOff() 로 flag 변수를 조작한다.
     * @return boolean type으로 flag를 조사하여 wifi direct가 켜져 있으면 true
     */
    @Override
    public boolean isEnabled() {
        return flag;
    }

    /**
     * prepareDeviceOn()
     *
     *  사용자가 Wifi direct 를 사용하기 위해 Wifi 를 키는 경우, UI 와 Thread 처리를 위해
     * 사용되는 함수이다. Broadcast receiver 에서 이후 wifi direct가 완전히 enable 상태가 되면
     * dialog의 UI 의 잠금상태를 해제한다.
     */
    public static void prepareDeviceOn(ProgressDialog dialog) {
        getInstance().queueOfProgressDialog.add(dialog);
    }

    /**
     * turnOn(), turnOff()
     *
     *  broadcast receiver 에서 호출하며 설정 fragment에서 progressDialog로의 UI 잠금을 푼다.
     */
    public static void turnOn() {
        flag = true;

        if (instance.queueOfProgressDialog.size() != 0) {
            ProgressDialog dialog = instance.queueOfProgressDialog.get(0);
            dialog.dismiss();
            getInstance().queueOfProgressDialog.remove(0);
        }
        Log.d(TAG, "wifi direct is enabled");
    }

    public static void turnOff() {
        flag = false;

        if (instance.queueOfProgressDialog.size() != 0) {
            ProgressDialog dialog = instance.queueOfProgressDialog.get(0);
            dialog.dismiss();
            instance.queueOfProgressDialog.remove(0);
        }
    }
}

