package thealphalabs.alphaapp;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import java.util.ArrayList;

import thealphalabs.alphaapp.adapter.ListAdapterOfSetting;
import thealphalabs.alphaapp.view.ListItemOfController;

// 컨트롤러 담당 Fragment
public class FragmentController extends Fragment {
    private final String TAG = "FragmentController";
    private final int BUTTON_TYPE_HOME = 1;
    private final int BUTTON_TYPE_BACK = 2;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controller, container, false);
        // 인스턴스 초기화 및 각 구성요소에 대한 포인터 변수 초기화
        LinearLayout touchpad = (LinearLayout) view.findViewById(R.id.controller_touchpad);
        ImageButton btn_home = (ImageButton) view.findViewById(R.id.controller_home_btn);
        ImageButton btn_back = (ImageButton) view.findViewById(R.id.controller_back_btn);
        TextView desc = (TextView) view.findViewById(R.id.controller_description);

        // Add listener
        touchpad.setOnTouchListener(new TouchEventListener());
        btn_home.setOnClickListener(new ButtonEventListener(BUTTON_TYPE_HOME));
        btn_back.setOnClickListener(new ButtonEventListener(BUTTON_TYPE_BACK));

        // 블루투스 장치 상태에 따라서 사용자에게 보여주는 메세지를 다르게 출력
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            desc.setText(getString(R.string.controller_desc));
        }
        else {
            desc.setText(getString(R.string.controller_desc_fail));
        }

        return view;
    }

    // Touch Event Listener for "TOUCH PAD" layout
    // 컨트롤러 중앙에 위치한 터치패드 공간의 터치이벤트 처리를 위한 리스너
    private class TouchEventListener implements View.OnTouchListener {
        private final String TAG = "TouchEventListener";
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.d(TAG, "x = " + motionEvent.getX() + ", y = " + motionEvent.getY());
            ((AlphaApplication)getActivity().getApplication()).getBluetoothHelper().transferMouseData(
                    motionEvent.getX(), motionEvent.getY(), (int)motionEvent.getPressure());
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
            // Button 1 : HOME Button
            if (btn_type == 0) {
                ;
            }
            // Button 2 : Back Button
            else {
                ;
            }
        }
    }
}
