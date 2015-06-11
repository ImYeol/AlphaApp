// IDataTransferService.aidl
package thealphalabs.alphaapp;

// Declare any non-default types here with import statements

oneway interface IDataTransferService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void PrapareToTransfer();
    void transferMouseData(float x,float y);
    void transferStringData(String text);
    void transferGyroData(float x,float y,float z);
    void transferAccelData(float x,float y,float z);

}
