package thealphalabs.controller;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.util.Log;

import thealphalabs.Interface.ServiceControllerInterface;
import thealphalabs.alphaapp.AlphaApplication;
import thealphalabs.alphaapp.GlassAppMain;

/**
 * @author  Sukbeom Kim
 */
public class AcclSensorController extends SensorController {
    private static final    String TAG = "AcclSensController";
    private static boolean  flag;
    private static void     setFlag(boolean v) {flag = v;}

    private static AcclSensorController instance;
    public  static AcclSensorController getInstance(Context context) {
        if (instance == null) {
            instance = new AcclSensorController(context);
        }
        return instance;
    }

    public  static AcclSensorController getInstance() {
        assert instance == null;
        return instance;
    }

    private Context mContext;

    AcclSensorController(@Nullable Context context) {
        mContext = context;

        init();
    }

    @Override
    public void start() {
        mSensorManager.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        setFlag(true);
    }

    @Override
    public void stop() {
        mSensorManager.unregisterListener(mSensorListener);
        setFlag(false);
    }

    @Override
    public void resume() {
        mSensorManager.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void pause() {
        mSensorManager.unregisterListener(mSensorListener);
    }

    @Override
    public void ready() {
        stop();
    }

    @Override
    public boolean isEnabled() {
        return flag;
    }

    private void init() {
        mSensorManager   = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor          =  mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorListener  =  new SensorController.SensorListener(
                ((AlphaApplication) ((Activity)mContext).getApplication()).getBluetoothHelper(), Sensor.TYPE_ACCELEROMETER);
    }
}
