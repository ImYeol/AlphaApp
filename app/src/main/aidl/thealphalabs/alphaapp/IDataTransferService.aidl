// IDataTransferService.aidl
package thealphalabs.alphaapp;

// Declare any non-default types here with import statements

interface IDataTransferService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void connectTo(String address);
    oneway void transferMouseData(float x,float y,int pressure);
    oneway void transferStringData(String text);
    oneway void transferGyroData(float x,float y,float z);
    oneway void transferAccelData(float x,float y,float z);
    oneway void transferNotificationData(String title, String text);
    oneway void transferFileData(out byte[] bytes, long file_size);
}
