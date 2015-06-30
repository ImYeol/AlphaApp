package thealphalabs.wifidirect;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import thealphalabs.Interface.WifiDeviceListCallback;
import thealphalabs.alphaapp.R;
import thealphalabs.util.Constants;
import thealphalabs.wifip2p.WifiDirectConnectionManager;

/**
 * Created by yeol on 15. 6. 29.
 */
public class WifiDeviceListDialog extends Activity implements WifiDeviceListCallback {

    private static final String TAG="WifiDialog";
    public final int fileRequestID = 98;
    public final int port = 7236;

    private String FilePathToTransfer;
    private ListView WifiDeviceListView;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> DeviceList=new ArrayList<String>();
    private WifiDirectConnectionManager mWifiDirectConnectionManager;
    private Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_device_list_dialog);
        cancelBtn=(Button)findViewById(R.id.wifi_dialog_cancel);
        FilePathToTransfer=getIntent().getExtras().getString(Constants.FILE_PATH_APK);
        if(CheckIfFileExist(FilePathToTransfer) == false) {
            showToast("File not exist");
            finish();
        }
        WifiDeviceListView=(ListView)findViewById(R.id.wifi_devices_list);
        mWifiDirectConnectionManager = WifiDirectConnectionManager.getInstance();
        mWifiDirectConnectionManager.registerWifiDeviceCallback(this);

        DeviceList.addAll(mWifiDirectConnectionManager.getWifiDevices());
        mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, DeviceList);
        WifiDeviceListView.setAdapter(mAdapter);
        WifiDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView localListView = (ListView) parent;
                String item = (String) localListView.getItemAtPosition(position);
                Log.d(TAG,"onItemClick:"+item);
                mWifiDirectConnectionManager.connectTo(item, FilePathToTransfer);
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private boolean CheckIfFileExist(String FileName){
        File file=new File(FileName);
        return file.exists();
    }

    private void showToast(String s) {
        Toast toast = Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWifiDirectConnectionManager.unRegisterWifiDeviceCallback();
    }

    @Override
    public void getWifiDevices(ArrayList<String> devices) {
        DeviceList.clear();
        for(String name : devices) {
            DeviceList.add(name);
        }
        mAdapter.notifyDataSetChanged();
    }
}
