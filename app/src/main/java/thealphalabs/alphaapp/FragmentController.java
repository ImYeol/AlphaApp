package thealphalabs.alphaapp;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

import thealphalabs.alphaapp.adapter.ListAdapterOfSetting;
import thealphalabs.alphaapp.adapter.SensorController;
import thealphalabs.alphaapp.dummy.DummyContent;
import thealphalabs.alphaapp.view.CustomEditText;
import thealphalabs.alphaapp.view.ListItemOfController;
import thealphalabs.bluetooth.BluetoothTransferHelper;

// 컨트롤러 담당 Fragment
public class FragmentController extends Fragment {
    private final String TAG = "FragmentController";
    private final int BUTTON_TYPE_HOME = 0;
    private final int BUTTON_TYPE_BACK = 1;
    private final int BUTTON_TYPE_KBD = 2;

    // Sensor manager
    private SensorManager sensorManager;
    private Sensor sensorAccel;
    private Sensor sensorGyro;
    private SensorListener gyroListener;
    private SensorListener accelListener;

    private CustomEditText editText;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controller, container, false);
        // 인스턴스 초기화 및 각 구성요소에 대한 포인터 변수 초기화
        LinearLayout touchpad = (LinearLayout) view.findViewById(R.id.controller_touchpad);
        ImageButton btn_home = (ImageButton) view.findViewById(R.id.controller_home_btn);
        ImageButton btn_back = (ImageButton) view.findViewById(R.id.controller_back_btn);
        ImageButton btn_kbd  = (ImageButton) view.findViewById(R.id.controller_kbd_btn);

        TextView desc = (TextView) view.findViewById(R.id.controller_description);

        // Add listener
        touchpad.setOnTouchListener(new TouchEventListener());
        btn_home.setOnClickListener(new ButtonEventListener(BUTTON_TYPE_HOME));
        btn_back.setOnClickListener(new ButtonEventListener(BUTTON_TYPE_BACK));
        btn_kbd.setOnClickListener(new ButtonEventListener(BUTTON_TYPE_KBD));

        editText = (CustomEditText) view.findViewById(R.id.text_kbd);

        // 블루투스 장치 상태에 따라서 사용자에게 보여주는 메세지를 다르게 출력
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            desc.setText(getString(R.string.controller_desc));
        }
        else {
            desc.setText(getString(R.string.controller_desc_fail));
        }

        // SensorManager 인스턴스 초기화
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorGyro  = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelListener = new SensorListener(((AlphaApplication) getActivity().getApplication()).getBluetoothHelper(),
                Sensor.TYPE_ACCELEROMETER);
        gyroListener = new SensorListener(((AlphaApplication) getActivity().getApplication()).getBluetoothHelper(),
                        Sensor.TYPE_GYROSCOPE);

        if (SensorController.AccelController.flag) {
            sensorManager.registerListener(accelListener, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (SensorController.GyroController.flag) {
            sensorManager.registerListener(gyroListener, sensorGyro, SensorManager.SENSOR_DELAY_NORMAL);
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (SensorController.AccelController.flag) {
            sensorManager.unregisterListener(accelListener);
        }
        if (SensorController.GyroController.flag) {
            sensorManager.unregisterListener(gyroListener);
        }
    }

    // Touch Event Listener for "TOUCH PAD" layout
    // 컨트롤러 중앙에 위치한 터치패드 공간의 터치이벤트 처리를 위한 리스너
    private class TouchEventListener implements View.OnTouchListener {
        private final String TAG = "TouchEventListener";
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
        //    Log.d(TAG, "x = " + motionEvent.getX() + ", y = " + motionEvent.getY());
            ((AlphaApplication)getActivity().getApplication()).getBluetoothHelper().transferMouseData(
                    motionEvent.getX()/view.getWidth(),motionEvent.getY()/view.getHeight(), motionEvent.getAction());
            return true;
        }
    }

    // 각각의 버튼 처리를 위한 리스너
    private class ButtonEventListener implements View.OnClickListener {
        private final String TAG = "ButtonEventListener";
        private int btn_type;

        ButtonEventListener(int type) {
            this.btn_type = type;
        }
        @Override
        public void onClick(View view) {

            Log.d(TAG, "TYPE " + btn_type + " is clicked!");
            switch (btn_type) {
                case 0:
                    // TYPE 0: Home button
                    break;

                case 1:
                    // TYPE 1: Back Button
                    break;
                case 2:
                    // TYPE 2: Keyboard button
                    // 키보드 버튼 눌렀을 때 키보드 띄운뒤 사용자가 입력한 텍스트를 보냄
                    // (보내는 것은 editText 내에서 자체적으로 실행)
                    editText.setVisibility(View.VISIBLE);
                    editText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                    imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);

                    break;
            }
        }


    }

    // 센서 이벤트 리스너
    private class SensorListener implements SensorEventListener {
        final String TAG = "SensorListener";
        BluetoothTransferHelper transfer;
        int sensor_type;

        SensorListener(BluetoothTransferHelper helper, int sensor_type) {
            this.transfer = helper;
            this.sensor_type = sensor_type;
        }
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
        //    Log.d(TAG, "onSensorChanged, type = " + sensor_type);
            if (sensor_type == Sensor.TYPE_GYROSCOPE) {
                transfer.transferGyroData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
            }
            else if (sensor_type == Sensor.TYPE_ACCELEROMETER) {
                transfer.transferAccelData(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }
}
