package thealphalabs.alphaapp;

import android.os.RemoteException;

/**
 * Created by yeol on 15. 6. 8.
 */
public class EventDataBinder extends IDataTransferService.Stub {

    private DataTransferManager manager;
    public EventDataBinder(DataTransferManager manager)
    {
        this.manager=manager;
    }
    @Override
    public void transferMouseData(float x, float y) throws RemoteException {
        manager.transferMouseData(x,y);
    }

    @Override
    public void transferStringData(String text) throws RemoteException {
        manager.transferStringData(text);
    }

    @Override
    public void transferGyroData(float x, float y, float z) throws RemoteException {
        manager.transferGyroData(x,y,z);
    }

    @Override
    public void transferAccelData(float x, float y, float z) throws RemoteException {
        manager.transferAccelData(x,y,z);
    }

}
