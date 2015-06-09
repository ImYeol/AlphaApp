package thealphalabs.alphaapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.RemoteException;
import android.text.method.Touch;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentController extends Fragment {
    private final String TAG = "FragmentController";

    // Member for event handler
    private btnClickListener btnL;
    private TouchEventListener touchL;
    private SensorManager sensorManager;
    private SensorEventListener accL;
    private SensorEventListener gyroL;
    private Sensor accSensor;
    private Sensor gyroSensor;

    private boolean toggleKeyboard;

    // Data transfer service
    private IDataTransferService dataTransferService;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toggleKeyboard = false;
        dataTransferService = ((AlphaApplication) getActivity().getApplication()).getDataTransferService();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_controlle, container, false);

        touchL = new TouchEventListener();
        view.setOnTouchListener(touchL);

        // Button
        btnL = new btnClickListener();
        Button toggleKeyboard = (Button)  view.findViewById(R.id.btn_keyboard);
        toggleKeyboard.setOnClickListener(btnL);

        // Sensor
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accL = new accListener();

        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gyroL = new gyroListener();

        sensorManager.registerListener(accL, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(gyroL, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);

        final EditText editText = (EditText) view.findViewById(R.id.inputbox);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_NEXT ||
                        keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    receiveUserInput(editText.getText().toString());

                    editText.setText("");
                    editText.setVisibility(View.INVISIBLE);
                    InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(getView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

                    return true;
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        sensorManager.unregisterListener(accL);
        sensorManager.unregisterListener(gyroL);
    }

    private void receiveUserInput(String input) {
        Log.d(TAG, "User input is " + input);
//        try {
//            dataTransferService.transferStringData(input);
//        }
//        catch(RemoteException ex) {
//            Log.e(TAG, "RemoteException occur: " + ex);
//        }
    }

    private class TouchEventListener implements View.OnTouchListener {
        private final String TAG = "touchEventListener";
        public boolean onTouch(View v, MotionEvent event) {
            Log.d(TAG, "x = " + event.getX() + ", y = " + event.getY());
////        try {
////            dataTransferService.transferMouseData(event.getX(), event.getY());
////        }
////        catch (RemoteException ex) {
////            Log.e(TAG, "RemoteException occurred: " + ex);
////        }
            return true;
        }
    }

   // Class for button click(keyboard toggle)
    private class btnClickListener implements View.OnClickListener {
        private final String TAG = "btnClickListener";
        public void onClick(View v) {
            toggleKeyboard = !toggleKeyboard;

            final EditText editText = (EditText) getView().findViewById(R.id.inputbox);
            InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (toggleKeyboard) {
                editText.setVisibility(View.VISIBLE);

                editText.requestFocus();
                keyboard.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
            else {
                editText.setText("");
                editText.setVisibility(View.INVISIBLE);
                keyboard.hideSoftInputFromWindow(getView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }
    }

    // Classes for SensorEventListener
    private class accListener implements SensorEventListener {
        private final String TAG = "accListener";
        public void onSensorChanged(SensorEvent event) {
            Log.d(TAG, "[accelometer event] x = " + event.values[0] + ", y = " + event.values[1] + ", z = " + event.values[2]);
            // When the IDataTransferService is fully implemented, uncomment following block:
//        try {
//            dataTransferService.transferAccelData(event.getX(), event.getY());
//        }
//        catch (RemoteException ex) {
//            Log.e(TAG, "RemoteException occurred: " + ex);
//        }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    private class gyroListener implements SensorEventListener {
        private final String TAG = "gyroListener";
        public void onSensorChanged(SensorEvent event) {
            Log.d(TAG, "[gyroscope event] x = " + event.values[0] + ", y = " + event.values[1] + ", z = " + event.values[2]);

            // When the IDataTransferService is fully implemented, uncomment following block:
//            try {
//                dataTransferService.transferGyroData(event.getX(), event.getY());
//            }
//            catch (RemoteException ex) {
//                Log.e(TAG, "RemoteException occurred: " + ex);
//            }
        }
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
