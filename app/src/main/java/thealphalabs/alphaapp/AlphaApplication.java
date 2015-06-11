package thealphalabs.alphaapp;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by AlphaLabs on 15. 6. 8.
 */
public class AlphaApplication extends Application {

    private final String TAG="AlphaApplication";
<<<<<<< Updated upstream

    private IDataTransferService DataTransferService;
=======
    private final DataTransferService transferService= DataTransferService.getInstance(this);
>>>>>>> Stashed changes
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            DataTransferService=IDataTransferService.Stub.asInterface(service);
<<<<<<< Updated upstream
//            try {
//
//            } catch (RemoteException e) {
//                // TODO Auto-generated catch block
//                Log.d(TAG,e.getMessage());
//            }
=======
>>>>>>> Stashed changes
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            DataTransferService=null;
        }

    };
    @Override
    public void onCreate() {
        super.onCreate();
<<<<<<< Updated upstream
        // Services
//        startService(new Intent(this, DataTransferManager.class));
//        bindService(new Intent(this, DataTransferManager.class), mConnection, BIND_AUTO_CREATE);
=======
>>>>>>> Stashed changes
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
<<<<<<< Updated upstream
//        unbindService(mConnection);
//        stopService(new Intent(this, DataTransferManager.class));
=======
        unbindService(mConnection);
        stopService(new Intent(this, DataTransferService.class));
>>>>>>> Stashed changes
    }

    public DataTransferService getDataTransferService()
    {
        return transferService;
    }
}
