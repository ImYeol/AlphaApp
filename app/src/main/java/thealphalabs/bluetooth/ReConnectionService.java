package thealphalabs.bluetooth;

import android.content.Context;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import thealphalabs.controller.BluetoothController;
import thealphalabs.util.ConnectionInfo;

/**
 * Created by yeol on 15. 6. 15.
 */
public class ReConnectionService {

    private static ReConnectionService instance;
    private Context mContext;
    private Timer mReConnectiongTimer;
    private final String TAG="ReConnectionService";
    private BluetoothController mBltManager;


    public static ReConnectionService getInstance(Context paramContext,BluetoothController paramBltManager)
    {
        if (instance == null)
            instance = new ReConnectionService(paramContext,paramBltManager);
        return instance;
    }

    public ReConnectionService(Context paramContext, BluetoothController paramBltManager)
    {
        super();
        mContext = paramContext;
        mBltManager=paramBltManager;
    }


    /**
     * 1분 마다 다시 연결요청을 한다
     *
     *
     */
    public void autoReconnect()
    {
        if(mBltManager.getState() != BluetoothController.STATE_NONE)
            return ;
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "autoReconnect with state = " + mBltManager.getState());
                String localAddress=ConnectionInfo.getInstance(mContext).getDeviceAddress();
                if(localAddress != null)
                    mBltManager.connectTo(localAddress);
            }
        };
        mReConnectiongTimer = new Timer();
        mReConnectiongTimer.schedule(task, 5000, 10000);// 매 분마다 다시 연결한다
    }


    /**
     * 자동 연결요청 취소
     */
    public void stopReconnect()
    {
        Log.d(TAG,"stopReconnect");

        if (mReConnectiongTimer != null)
        {
            mReConnectiongTimer.cancel();
        }
    }
}
