package thealphalabs.alphaapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * Created by yeol on 15. 6. 8.
 */
public class DataTransferManager extends Service {

    private final IBinder mBinder=new EventDataBinder(this);
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
    public void transferMouseData(float x, float y) {

    }

    public void transferStringData(String text) {

    }

    public void transferGyroData(float x, float y, float z)  {

    }

    public void transferAccelData(float x, float y, float z)  {

    }
}
