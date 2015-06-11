package thealphalabs.alphaapp;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by yeol on 15. 6. 8.
 */
public class AlphaApplication extends Application {

    private final String TAG="AlphaApplication";
    private final DataTransferService transferService= DataTransferService.getInstance(this);
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            DataTransferService=IDataTransferService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            DataTransferService=null;
        }

    };
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unbindService(mConnection);
        stopService(new Intent(this, DataTransferService.class));
    }

    public DataTransferService getDataTransferService()
    {
        return transferService;
    }
}
