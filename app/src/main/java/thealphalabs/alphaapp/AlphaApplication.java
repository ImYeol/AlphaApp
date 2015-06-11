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

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        transferService.StopBluetoothConnection();
        transferService.StartWifiP2PConnection();
    }

    public DataTransferService getDataTransferService()
    {
        return transferService;
    }
}
