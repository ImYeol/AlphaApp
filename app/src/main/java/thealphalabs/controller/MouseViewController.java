package thealphalabs.controller;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import thealphalabs.Interface.ServiceControllerInterface;
import thealphalabs.wifidirect.MouseViewService;

/**
 * Created by yeol on 15. 6. 29.
 */
public class MouseViewController implements ServiceControllerInterface {

    private static final String TAG     = "MouseViewController";
    private static MouseViewController instance;
    private Context mContext;
    private Intent intent;
    private boolean IsStarted=false;

    public  static MouseViewController getInstance(Context context) {
        if (instance == null) {
            instance = new MouseViewController(context);
        }
        return instance;
    }

    public  static MouseViewController getInstance() {
        //assert instance == null;
        return instance;
    }

    MouseViewController(@Nullable Context context) {
        mContext = context;
        //init();
    }

    public boolean IsServiceStarted(){
        Log.d(TAG,"IsServiceStarted:"+IsStarted);
        return IsStarted;
    }

    @Override
    public void start() {
        Log.d(TAG,"start()");
        intent=new Intent(mContext, MouseViewService.class);
        mContext.startService(intent);
        IsStarted=true;
    }

    @Override
    public void stop() {
        Log.d(TAG,"stop()");
        if(intent != null)
            intent=new Intent(mContext, MouseViewService.class);
        mContext.stopService(intent);
        intent=null;
        IsStarted=false;
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void ready() {

    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
