package thealphalabs.wifidirect;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import thealphalabs.alphaapp.view.MouseView;

/**
 * Created by yeol on 15. 7. 1.
 */
public class MouseViewService extends Service {

    private static final String TAG="MouseViewService";
    private View mView;
    private WindowManager.LayoutParams mParams;
    private WindowManager mManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG,"onCreate()");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand()");
        registerMouseView();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLowMemory() {
        Log.d(TAG,"onLowMemory");
        super.onLowMemory();
        unregisterMouseView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
        unregisterMouseView();
    }

    private void registerMouseView(){
        Log.d(TAG,"registerMouseView");
        mView=new MouseView(MouseViewService.this);
        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.TOP | Gravity.LEFT;

        mManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mManager.addView(mView, mParams);

    }

    private void unregisterMouseView(){
        Log.d(TAG,"unregisterMouseView");
        if(mView != null) {
            mManager.removeView(mView);
            mView=null;
        }
    }
}
