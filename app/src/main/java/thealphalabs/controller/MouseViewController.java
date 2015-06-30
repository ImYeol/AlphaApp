package thealphalabs.controller;

import android.content.Context;
import android.support.annotation.Nullable;

import thealphalabs.Interface.ServiceControllerInterface;

/**
 * Created by yeol on 15. 6. 29.
 */
public class MouseViewController implements ServiceControllerInterface {

    private static final String TAG     = "MouseViewController";
    private static MouseViewController instance;
    private Context mContext;


    public  static MouseViewController getInstance(Context context) {
        if (instance == null) {
            instance = new MouseViewController(context);
        }
        return instance;
    }

    public  static MouseViewController getInstance() {
        assert instance == null;
        return instance;
    }

    MouseViewController(@Nullable Context context) {
        mContext = context;
        //init();
    }
    @Override
    public void start() {
        
    }

    @Override
    public void stop() {

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
