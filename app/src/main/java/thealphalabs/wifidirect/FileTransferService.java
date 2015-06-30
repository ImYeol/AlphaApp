package thealphalabs.wifidirect;

import android.app.IntentService;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import thealphalabs.wifip2p.WifiDirectConnectionManager;

/**
 * Created by yeol on 15. 6. 29.
 */
public class FileTransferService extends IntentService {

    private final static String TAG="FileTransferService";
    private boolean serviceEnabled;
    private int port;
    private String FileName;
    private ResultReceiver clientResult;
    private WifiP2pInfo wifiInfo;


    public FileTransferService() {
        super("FileTransferService");
        serviceEnabled=true;
    }

    private void getIntentExtras(Intent intent){
        port = intent.getExtras().getInt(WifiDirectConnectionManager.WIFI_TRANSFER_PORT);
        FileName = intent.getExtras().getString(WifiDirectConnectionManager.FILE_NAME);
        clientResult = (ResultReceiver) intent.getExtras().get(WifiDirectConnectionManager.ClIENT_RESULT);
        wifiInfo = (WifiP2pInfo) intent.getExtras().get(WifiDirectConnectionManager.WIFI_INFO);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        getIntentExtras(intent);

        Log.d(TAG,"called IntentService");
     /*   if(!wifiInfo.isGroupOwner)
        { */
            InetAddress targetIP = wifiInfo.groupOwnerAddress;
            Socket clientSocket = null;
            OutputStream outputStream = null;

            try {

                clientSocket = new Socket(targetIP, port);
                outputStream = clientSocket.getOutputStream();
                DataOutputStream dataOutputStream=new DataOutputStream(outputStream);
                byte[] buffer = new byte[4092];

                File file=new File(FileName);
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);

                dataOutputStream.writeUTF(file.getName());
                dataOutputStream.writeLong(file.length());

                int bytesRead=0;
                while((bytesRead=bis.read(buffer, 0, buffer.length))>0)
                {
                    if(bytesRead == -1)
                    {
                        Log.d(TAG,"failed to read file");
                        break;
                    }
                    dataOutputStream.write(buffer,0,bytesRead);
                    Log.d(TAG,"sending:"+bytesRead);
                }
                dataOutputStream.flush();

                fis.close();
                bis.close();
                outputStream.close();
                dataOutputStream.close();

                clientSocket.close();

            } catch (IOException e) {
                Log.d(TAG,"IO exception:"+e.getMessage());
                signalActivity(e.getMessage());
            }
            catch(Exception ex)
            {
                Log.d(TAG,"exception ex:"+ex.getMessage());
                signalActivity(ex.getMessage());
            }
   //     }
    /*    else
        {
            Log.d(TAG,"it is owner");
          //  signalActivity("This device is a group owner, therefore the IP address of the " +
          //          "target device cannot be determined. File transfer cannot continue");
        } */
        clientResult.send(port, null);
    }


    public void signalActivity(String message)
    {
        Bundle b = new Bundle();
        b.putString("message", message);
        clientResult.send(port, b);
    }


    public void onDestroy()
    {
        serviceEnabled = false;
        stopSelf();
    }
}
