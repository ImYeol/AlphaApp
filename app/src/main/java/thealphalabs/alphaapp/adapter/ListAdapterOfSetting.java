package thealphalabs.alphaapp.adapter;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.itemmanipulation.expandablelistitem.ExpandableListItemAdapter;

import java.util.ArrayList;

import thealphalabs.alphaapp.BluetoothActivity;
import thealphalabs.alphaapp.R;
import thealphalabs.alphaapp.bluetooth.BluetoothService;
import thealphalabs.alphaapp.notification.NotificationController;
import thealphalabs.alphaapp.view.ListItemOfController;

public class ListAdapterOfSetting extends ExpandableListItemAdapter {
    private final String TAG = "ListAdapterOfSetting";
    private Context mContext;
    private ArrayList<ListItemOfController> itemList;
    private ArrayList<SwitchCompatListener> listeners = new ArrayList<>();

    public ListAdapterOfSetting(Context context, ArrayList<ListItemOfController> itemList) {
        super(context);
        this.setExpandCollapseListener(new ItemToggleListener(this));
        this.itemList = itemList;
        this.mContext = context;

        for (int i = 0; i < itemList.size(); i++) {
            add(i, itemList.get(i));
            listeners.add(new SwitchCompatListener(this, i, null));
        }
    }
    @NonNull
    @Override
    public View getTitleView(int i, View view, @NonNull ViewGroup viewGroup) {
        Log.d(TAG, "getTitleView called");
        ListItemOfController target   = itemList.get(i);
        LayoutInflater       inflater = (LayoutInflater) mContext.getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        View title_view = inflater.inflate(R.layout.listitem_setting_title, viewGroup, false);

        TextView     title    = (TextView) title_view.findViewById(R.id.listitem_title_title);
        TextView     desc     = (TextView) title_view.findViewById(R.id.listitem_title_desc);
        ImageView    icon     = (ImageView) title_view.findViewById(R.id.listitem_title_icon);
        SwitchCompat switcher = (SwitchCompat) title_view.findViewById(R.id.listitem_title_switch);

        title.setText(target.getTitle());
        desc.setText(target.getDesc());
        icon.setImageResource(target.getIconResId());
        switcher.setOnClickListener(listeners.get(i));
        listeners.get(i).setSwitcher(switcher);

        // 각 아이템별 스위치 설정
        switch (i) {
            case 0:
                // 블루투스
                if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    switcher.setChecked(true);
                    SensorController.BluetoothController.setFlag(true);
                    expand(i);
                }
                break;
            case 1:
                // 센서
                if (SensorController.AccelController.flag) {
                    switcher.setChecked(true);
                }
                break;
            case 2:
                // 자이로
                if (SensorController.GyroController.flag) {
                    switcher.setChecked(true);
                }
                break;
            case 3:
                // Notification
                if (NotificationController.flag) {
                    switcher.setChecked(true);
                }
                break;
        }
        return title_view;
    }

    @NonNull
    @Override
    public View getContentView(int i, View view, @NonNull ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View v = null;

        /*
         * 실제로 사용되는 레이아웃은 listitem_controller_bluetooth이다.
         */
        switch (i) {
            case 0:
                // 블루투스 아이템항목
                v = inflater.inflate(R.layout.listitem_setting_bluetooth, viewGroup, false);
                Button btn = (Button) v.findViewById(R.id.bluetooth_connect_btn);
                btn.setOnClickListener(new ConnectBtnListener(mContext));
                break;
            case 1:
                // 센서 정보
                v = inflater.inflate(R.layout.listitem_setting_dummy, viewGroup, false);
                break;
            case 2:
                v = inflater.inflate(R.layout.listitem_setting_dummy, viewGroup, false);
                break;
            case 3:
                v = inflater.inflate(R.layout.listitem_setting_dummy, viewGroup, false);
                break;
            default:
                Log.e(TAG, "Unexpected error");
        }

        return v;
    }

    public boolean getSwitchValue(int position) {
        return listeners.get(position).getSwitcher().isChecked();
    }

    private class ConnectBtnListener implements View.OnClickListener {
        private Context mContext;
        ConnectBtnListener(Context context) {
            mContext = context;
        }
        @Override
        public void onClick(View view) {
            Log.d("ConnectButton", "button clicked");
            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                SensorController.bltService.scanDevice();
            }
        }
    }

    private class ItemToggleListener implements ExpandableListItemAdapter.ExpandCollapseListener {
        private final String TAG = "ItemToggleListener";
        private ExpandableListItemAdapter adapter;

        public ItemToggleListener(ExpandableListItemAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onItemExpanded(int i) {
            Log.d(TAG, "onItemExpanded");
            ListItemOfController target = (ListItemOfController) getItem(i);
            target.setExpanded(true);

        }

        @Override
        public void onItemCollapsed(int i) {
            Log.d(TAG, "onItemCollapsed");
            ListItemOfController target = (ListItemOfController) getItem(i);
            target.setExpanded(false);
        }
    }

    private class SwitchCompatListener implements View.OnClickListener {
        private final String TAG = "SwitchCompatListener";
        private ListAdapterOfSetting adapter;
        private SwitchCompat switcher;
        private int index;

        public SwitchCompatListener(ListAdapterOfSetting adapter, int index, SwitchCompat switcher) {
            super();
            this.adapter = adapter;
            this.index = index;
            this.switcher = switcher;
        }

        public SwitchCompat getSwitcher() {
            return this.switcher;
        }

        public void setSwitcher(SwitchCompat target) {
            this.switcher = target;
        }

        @Override
        public void onClick(View view) {
            boolean onFlag = adapter.getSwitchValue(index);
            Log.d(TAG, "onClick() is called with index = " + index + ", onFlag = " + onFlag);

            // onFlag가 true인 경우에 센서플래그를 킨다.
            switch (index) {
                case 0:
                    // 블루투스
                    if (SensorController.BluetoothController.flag == false) {
                        SensorController.bltService.enableBluetooth();
                        SensorController.BluetoothController.setFlag(true);
                        expand(index);
                    }
                    else {
//                        사용자가 블루투스를 끄려고 하는 경우
                        BluetoothAdapter.getDefaultAdapter().disable();
                        SensorController.BluetoothController.setFlag(false);
                        collapse(index);
                    }
                    break;
                case 1:
                    // 엑셀레이터
                    SensorController.AccelController.setFlag(onFlag);
                    break;
                case 2:
                    // 자이로 센서
                    SensorController.GyroController.setFlag(onFlag);
                case 3:
                    // Notification
                    NotificationController.setFlag(onFlag);
                    break;
                default:
                    // 예외 처리
                    Log.e(TAG, "Unexpected case with index = " + index + " in onClick()");
            }
        }
    }
}
