package thealphalabs.controller;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import thealphalabs.Interface.ServiceControllerInterface;
import thealphalabs.alphaapp.AlphaApplication;
import thealphalabs.alphaapp.GlassAppMain;

/**
 * @author Sukbeom Kim
 */
public class GyroSensorController extends SensorController {
    private boolean flag;
    private void    setFlag(boolean v) {flag = v;}

    private Context mContext;

    private static GyroSensorController instance;
    public  static GyroSensorController getInstance(Context context) {
        if (instance == null) {
            instance = new GyroSensorController(context);
        }
        return instance;
    }
    public  static GyroSensorController getInstance() {
        assert instance == null;
        return instance;
    }

    /**
     * Constructor로써 필요한 변수들을 초기화한다.
     * @param context
     */
    GyroSensorController(Context context) {
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

    /**
     * Activity 전환 시 onResume() 으로부터 호출될 수 있다.
     * 때문에 flag 설정을 제외한 나머지 부분을 설정한다.
     */
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

    //** 필요 메서드

    /**
     * init()
     *
     * SensorManager와 Sensor Listener 등을 초기화한다.
     */
    private void init() {
        mSensorManager   = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor          =  mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorListener  =  new SensorController.SensorListener(
                ((AlphaApplication) ((Activity)mContext).getApplication()).getBluetoothHelper(), Sensor.TYPE_GYROSCOPE);
    }
}
