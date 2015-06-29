package thealphalabs.alphaapp;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import thealphalabs.controller.BluetoothController;
import thealphalabs.controller.SensorController;
import thealphalabs.alphaapp.view.CustomEditText;
import thealphalabs.bluetooth.BluetoothTransferHelper;
import thealphalabs.controller.ServiceController;
import thealphalabs.util.EventDataType;

/*
 * Author:  Sukbeom Kim
 * E-mail:  sbkim@thealphalabs.com
 */

// 컨트롤러 담당 Fragment
public class FragmentController extends Fragment {
    private final String TAG = "FragmentController";
    private final int BUTTON_TYPE_HOME = 0;
    private final int BUTTON_TYPE_BACK = 1;
    private final int BUTTON_TYPE_KBD = 2;

    // distinguish if Click IS
    private static final int MAX_CLICK_DISTANCE=5;
    private static final float MAX_CLICK_DURATION=200;
    private long startClickTime;

    private CustomEditText editText;
    private float TouchX=0;
    private float TouchY=0;

    private float tempX = 0;
    private float tempY = 0;
    private BluetoothTransferHelper mBltHelper;

    private static boolean mScrolling=false;

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

        ServiceController.resumeService(ServiceController.ACCLSENSOR_SERVICE);
        ServiceController.resumeService(ServiceController.GYROSENSOR_SERVICE);

        mBltHelper=((AlphaApplication)getActivity().getApplication()).getBluetoothHelper();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ServiceController.resumeService(ServiceController.ACCLSENSOR_SERVICE);
        ServiceController.resumeService(ServiceController.GYROSENSOR_SERVICE);
    }

    @Override
    public void onPause() {
        super.onPause();

        ServiceController.pauseService(ServiceController.GYROSENSOR_SERVICE);
        ServiceController.pauseService(ServiceController.ACCLSENSOR_SERVICE);
    }

    /**
     * onDestroy()
     *  자이로와 엑셀로미터 센서를 pause하는 이유는 사용자가 설정한 값(flag)을 초기화하지
     *  않기 위해서 stopService 대신 pauseService를 호출한다.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        ServiceController.pauseService(ServiceController.GYROSENSOR_SERVICE);
        ServiceController.pauseService(ServiceController.ACCLSENSOR_SERVICE);
    }

    // Touch Event Listener for "TOUCH PAD" layout
    // 컨트롤러 중앙에 위치한 터치패드 공간의 터치이벤트 처리를 위한 리스너
    private class TouchEventListener implements View.OnTouchListener {
        private final String TAG = "TouchEventListener";
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            // 이전과의 좌표를 비교하여 소수점 이하의 이벤트는 보내지 않는다.
      /*      if ( Math.abs(tempX - motionEvent.getX()) < 1 &&
                    Math.abs(tempY - motionEvent.getY()) < 1)
            {
                // 차이가 1 미만인 경우
                return true;
            }
            else {
                tempX = motionEvent.getX();
                tempY = motionEvent.getY();
            }*/

            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                TouchX=motionEvent.getX();
                TouchY=motionEvent.getY();
                startClickTime = Calendar.getInstance().getTimeInMillis();
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_POINTER_2_DOWN) {
                Log.d(TAG,"pointer down");
                mScrolling=true;
                mBltHelper.transferMouseData(motionEvent.getX(1) / view.getWidth(),
                        motionEvent.getY(1) / view.getHeight(),
                        EventDataType.EventDataFlag.Motion_Two_Touch_Down);
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_POINTER_1_UP) {
                mBltHelper.transferMouseData(motionEvent.getX(1)/view.getWidth(),motionEvent.getY(1)/view.getHeight(),
                        EventDataType.EventDataFlag.Motion_Two_Touch_UP);
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {

                if(mScrolling == false) {
                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                    float x=motionEvent.getX();
                    float y=motionEvent.getY();
                    if(IsClick(clickDuration,getDistance(TouchX,TouchY,x,y))) {  // one point click
                        mBltHelper.transferMouseData(x / view.getWidth(), y / view.getHeight(), MotionEvent.ACTION_UP);
                    }
                    else {   // one point move
                        TouchX=x;
                        TouchY=y;
                    }
                }
                mScrolling=false;

            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

                if (mScrolling) {  // two touch is for scrolling
                    if(motionEvent.getPointerCount() >= 2) {
                        Log.d(TAG, "two touch x:" + motionEvent.getX(1) + " y:" + motionEvent.getY(1));
                        mBltHelper.transferMouseData(
                                motionEvent.getX(1) / view.getWidth(), motionEvent.getY(1) / view.getHeight(),
                                EventDataType.EventDataFlag.Motion_Two_Touch_Move);
                    }
                }
                else {   // one point down
                    float TermX=motionEvent.getX()-TouchX;
                    float TermY=motionEvent.getY()-TouchY;
                    TouchX=motionEvent.getX();
                    TouchY=motionEvent.getY();
                    mBltHelper.transferMouseData(
                            TermX / view.getWidth(), TermY / view.getHeight(), motionEvent.getAction());
                }
            }
            return true;
        }
        private float getDistance(float x1,float y1,float x2,float y2) {
            float dx = x1 - x2;
            float dy = y1 - y2;
            return Math.abs(dx+dy);
        }

        private boolean IsClick(long duration,float distance) {
            return duration < MAX_CLICK_DURATION && distance < MAX_CLICK_DISTANCE ;
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

}
