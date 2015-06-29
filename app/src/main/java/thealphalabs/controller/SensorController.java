package thealphalabs.controller;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import thealphalabs.Interface.ServiceControllerInterface;
import thealphalabs.bluetooth.BluetoothTransferHelper;

/**
 * SensorController
 *
 *  Controller 섹션에서 SensorController 부분을 참조하여 데이터 전송여부를 결정한다.
 *  실제로는 flag만 설정하여 전송 여부를 설정한다.
 *
 * @version : 1.0
 * @author  : Sukbeom Kim(sbkim@thealphalabs.com)
 */
abstract public class SensorController implements ServiceControllerInterface {
    // 센서 컨트롤러의 필수 멤버변수들
    protected Sensor          mSensor;
    protected SensorManager   mSensorManager;
    protected SensorListener  mSensorListener;

    // 센서 이벤트 리스너
    protected class SensorListener implements SensorEventListener {
        final String TAG = "SensorListener";

        BluetoothTransferHelper transfer;

        int sensor_type;

        SensorListener(BluetoothTransferHelper helper, int sensor_type) {
            this.transfer = helper;
            this.sensor_type = sensor_type;
        }
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (!isAvailableToTransfer(sensor_type)) {
                return;
            }

            if (sensor_type == Sensor.TYPE_GYROSCOPE) {
                try {
                    transfer.transferGyroData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to transfer gyro data: " + e);
                }
            }
            else if (sensor_type == Sensor.TYPE_ACCELEROMETER) {
                try {
                    transfer.transferAccelData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to transfer accel data: " + e);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

        /**
         * isAvailableToTransfer
         *  현재 블루투스 상태와 각 센서의 전송 여부를 판단하여 리턴값을 정한다.
         *  1. 블루투스가 CONNECTED 상태가 아닌 경우,
         *  2. 센서의 전송 여부가 FALSE 로 설정되어 있는 경우
         *
         *  에 False를 리턴하고 다른 경우에는 True를 리턴한다.
         */
        private boolean isAvailableToTransfer(int sensor_type) {
            // 블루투스 상태 확인
            if (!(BluetoothController.getInstance().getState() == BluetoothController.STATE_CONNECTED)) {
                // 블루투스가 연결된 상태가 아니라면 바로 false 리턴
                return false;
            }

            switch (sensor_type)
            {
                case Sensor.TYPE_GYROSCOPE:
                    if (!ServiceController.isServiceEnabled(ServiceController.GYROSENSOR_SERVICE))
                        return false;
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    if (!ServiceController.isServiceEnabled(ServiceController.ACCLSENSOR_SERVICE))
                        return false;
                    break;
            }

            return true;
        }
    }
}
