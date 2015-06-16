package thealphalabs.wifip2p;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import thealphalabs.Interface.TransferHelperInterface;
import thealphalabs.alphaapp.IDataTransferService;
import thealphalabs.util.IntentSender;

/**
 * Created by yeol on 15. 6. 9.
 */
public class WifiTransferHelper {
    private static WifiTransferHelper instance;
    private Context context;
    private IDataTransferService mTransferSerivce;
    private ServiceConnection mConn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mTransferSerivce= IDataTransferService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mTransferSerivce=null;
        }
    };
    private WifiTransferHelper(){

    }

    public static WifiTransferHelper getInstance(){
        if(instance == null )
            instance= new WifiTransferHelper();
        return instance;
    }

    public void StartConnection(Context context) {
        Intent localIntent=new Intent(context,WifiTransferService.class);
    //    IntentSender.getInstance().bindService(context,new Intent(context,WifiTransferService.class),mConn);
    }
}
