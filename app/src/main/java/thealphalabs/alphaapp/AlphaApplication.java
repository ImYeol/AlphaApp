package thealphalabs.alphaapp;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by AlphaLabs on 15. 6. 8.
 */
public class AlphaApplication extends Application {

    private final String TAG="AlphaApplication";

    private IDataTransferService DataTransferService;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            DataTransferService=IDataTransferService.Stub.asInterface(service);
//            try {
//
//            } catch (RemoteException e) {
//                // TODO Auto-generated catch block
//                Log.d(TAG,e.getMessage());
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            DataTransferService=null;
        }

    };
    @Override
    public void onCreate() {
        super.onCreate();
        // Services
//        startService(new Intent(this, DataTransferManager.class));
//        bindService(new Intent(this, DataTransferManager.class), mConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
//        unbindService(mConnection);
//        stopService(new Intent(this, DataTransferManager.class));
    }

    public IDataTransferService getDataTransferService()
    {
        return this.DataTransferService;
    }
}
