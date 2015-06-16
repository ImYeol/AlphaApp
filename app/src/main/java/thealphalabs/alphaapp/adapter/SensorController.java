package thealphalabs.alphaapp.adapter;

import android.os.Handler;

import thealphalabs.alphaapp.bluetooth.BluetoothService;

/**
 * Created by sukbeom on 15. 6. 15.
 */
public class SensorController {
    public static BluetoothService bltService;
    public static class BluetoothController {
        public static boolean flag;

        public static void setFlag(boolean v) {flag = v;}
    }

    public static class GyroController {
        public static boolean flag;

        public static void setFlag(boolean v) {flag = v;}
    }

    public static class AccelController {
        public static boolean flag;

        public static void setFlag(boolean v) {flag = v;}
    }
}
