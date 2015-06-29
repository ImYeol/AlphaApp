package thealphalabs.alphaapp.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.provider.Settings;
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

import thealphalabs.alphaapp.R;
import thealphalabs.controller.BluetoothController;
import thealphalabs.controller.SensorController;
import thealphalabs.controller.NotificationController;
import thealphalabs.controller.ServiceController;
import thealphalabs.notification.NotificationService;
import thealphalabs.controller.WifiDirectController;
import thealphalabs.alphaapp.view.ListItemOfController;
import thealphalabs.wifidirect.WifiDirect_DeviceList;

/**
 * 설정 Fragment 의 ListView 아이템을 위한 Adapter 클래스
 * @version : 1.0
 * @author  : Sukbeom Kim(sbkim@thealphalabs.com)
 */
public class ListAdapterOfSetting extends ExpandableListItemAdapter {
    private final String TAG = "ListAdapterOfSetting";

    private Context mContext;
    private ArrayList<ListItemOfController> itemList;
    private ArrayList<SwitchCompatListener> listeners = new ArrayList<>();

    /**
     * ListAdapterOfSetting: Constructor
     *
     * @param context   context 저장을 위한 파라미터
     * @param itemList  어댑터 클래스가 호출되기 전 itemList 가 준비되어 있어야 한다.
     *
     */
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

    /**
     * listView 에서 한 행(row)에서 title view 로 출력될 View를 정의하여 리턴한다.
     * 처음 설정 fragment 가 로드되었을 때 보여질 뷰를 설정한다.
     *
     * @param   i         listView 에서 아이템의 index
     * @param   view      view
     * @param   viewGroup viewGroup
     * @return  row 에 해당하는 View
     */
    @NonNull
    @Override
    public View getTitleView(int i, View view, @NonNull ViewGroup viewGroup) {
        ListItemOfController target   = itemList.get(i);
        LayoutInflater       inflater = (LayoutInflater) mContext.getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        View title_view = inflater.inflate(R.layout.listitem_setting_title, viewGroup, false);

        // 아이템 리스트에 있는 내용들로 View 의 구성 요소들을 셋팅한다.
        TextView     title    = (TextView) title_view.findViewById(R.id.listitem_title_title);
        TextView     desc     = (TextView) title_view.findViewById(R.id.listitem_title_desc);
        ImageView    icon     = (ImageView) title_view.findViewById(R.id.listitem_title_icon);
        SwitchCompat switcher = (SwitchCompat) title_view.findViewById(R.id.listitem_title_switch);

        // 타이틀, 설명, 아이콘 등록
        title.  setText(target.getTitle());
        desc.   setText(target.getDesc());
        icon.   setImageResource(target.getIconResId());

        // 이벤트 리스너 등록
        // 1. 스위치 리스너 등록
        //  각 리스너들은 스위치에 대한 레퍼런스를 갖는다(리스너에서 스위치를 변경가능하도록 하기 위해)
        switcher.setOnClickListener(listeners.get(i));
        listeners.get(i).setSwitcher(switcher);

        // 2. 뷰 클릭 리스너 등록
        //  기본적으로 title view 클릭 시 toggle 되는 것을 막고 switch 를 대신 제어하기 위해 등록
        title_view.setOnClickListener(new ItemClickListener(i, switcher));

        // 각 아이템별 스위치 설정
        switch (i) {
            case 0:
                // 블루투스
                if (ServiceController.isServiceEnabled(ServiceController.BLUETOOTH_SERVICE)) {
                    switcher.setChecked(true);
                    expand(i);
                }
                else {
                    switcher.setChecked(false);
                }
                break;
            case 1:
                // 엑셀로미터 센서
                if (ServiceController.isServiceEnabled(ServiceController.ACCLSENSOR_SERVICE)) {
                    switcher.setChecked(true);
                }
                else {
                    switcher.setChecked(false);
                }
                break;
            case 2:
                // 자이로 센서
                if (ServiceController.isServiceEnabled(ServiceController.GYROSENSOR_SERVICE)) {
                    switcher.setChecked(true);
                }
                break;
            case 3:
                // Notification
                if (ServiceController.isServiceEnabled(ServiceController.NOTIFICATION_SERVICE)) {
                    switcher.setChecked(true);
                }
                else {
                    switcher.setChecked(false);
                }
                break;
            case 4:
                // Wifi direct
                if (ServiceController.isServiceEnabled(ServiceController.WIFIDIRECT_SERVICE)) {
                    switcher.setChecked(true);
                    expand(i);
                }
                else {
                    switcher.setChecked(false);
                }
                break;
        }
        return title_view;
    }


    /**
     * getContentView
     *
     *  listView 의 아이템에서(row) content view 로 출력될 View를 정의하여 리턴한다.
     *
     * @param   i         listView 에서 아이템의 index (row index)
     * @param   view      view
     * @param   viewGroup viewGroup
     * @return  row(content view) 에 해당하는 View
     */
    @NonNull
    @Override
    public View getContentView(int i, View view, @NonNull ViewGroup viewGroup) {
        View v = null;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        // 블루투스 부분을 제외한 나머지 부분은 dummy 사용
        switch (i) {
            case 0:
                // 블루투스 아이템항목
                v = inflater.inflate(R.layout.listitem_setting_bluetooth, viewGroup, false);
                Button btn = (Button) v.findViewById(R.id.bluetooth_connect_btn);
                btn.setOnClickListener(new ConnectBtnListener(mContext, ConnectBtnListener.BTN_BLUETOOTH));

                BluetoothController.mConnectBtn = btn;
                break;
            case 1:
            case 2:
            case 3:
                v = inflater.inflate(R.layout.listitem_setting_dummy, viewGroup, false);
                break;
            case 4:
                v = inflater.inflate(R.layout.listitem_setting_wifidirect, viewGroup, false);
                btn = (Button) v.findViewById(R.id.wifidirect_connect_btn);
                btn.setOnClickListener(new ConnectBtnListener(mContext, ConnectBtnListener.BTN_WIFIDIRECT));
                break;
            default:
                Log.e(TAG, "Unhandled index of content view item.");
        }

        return v;
    }

    /**
     * getSwitchValue
     *  position(index) 에 있는 switch 값을 가져온다.
     * @param   position
     * @return  position 에 있는 switchcompat의 값 (체크: true)
     */
    public boolean getSwitchValue(int position) {
        return listeners.get(position).getSwitcher().isChecked();
    }

    /**
     * ConnectBtnListener
     *  블루투스 연결을 위한 Connect 버튼 리스너 클래스
     */
    private class ConnectBtnListener implements View.OnClickListener {
        public static final int BTN_BLUETOOTH  = 0;
        public static final int BTN_WIFIDIRECT = 1;

        int type;

        ConnectBtnListener(Context context, int type) {
            mContext    = context;
            this.type   = type;
        }
        @Override
        public void onClick(View view) {
            switch (type) {
                case BTN_BLUETOOTH:
                    BluetoothController.getInstance().scanDevice();
                    break;
                case BTN_WIFIDIRECT:
                    Intent i = new Intent(mContext, WifiDirect_DeviceList.class);
                    ((Activity)mContext).startActivityForResult(i, BluetoothController.REQUEST_CONNECT_DEVICE);
                    break;
            }

        }
    }

    /**
     * ItemToggleListener
     *  ListView의 각 행 (row) 를 클릭했을 때 펼쳐지는지 닫히는지에 대한 이벤트 리스너
     */
    private class ItemToggleListener implements ExpandableListItemAdapter.ExpandCollapseListener {
        private final String TAG = "ItemToggleListener";
        private ExpandableListItemAdapter adapter;

        public ItemToggleListener(ExpandableListItemAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onItemExpanded(int i) {
            ListItemOfController target = (ListItemOfController) getItem(i);
            target.setExpanded(true);

        }

        @Override
        public void onItemCollapsed(int i) {
            ListItemOfController target = (ListItemOfController) getItem(i);
            target.setExpanded(false);
        }
    }

    /**
     * ItemClickListener
     *  TitleView의 OnClickListener를 위해 만든 클래스이다.
     *  타이틀뷰를 클릭했을 때 contentView 가 toggle 되는 것을 막고 스위치를 제어하기 위한 클래스
     */
    private class ItemClickListener implements View.OnClickListener {
        private final String TAG = "ItemClickListener";

        // 아이템의 index 를 변수로 갖는다
        private int          mIndex;
        private SwitchCompat mSwitch;

        ItemClickListener(int pIndex, SwitchCompat pSwitch) {
            mIndex  = pIndex;
            mSwitch = pSwitch;
        }
        @Override
        public void onClick(View v) {
            // 토글 후 스위치에 등록된 리스너를 호출한다.
            mSwitch.toggle();
            mSwitch.callOnClick();
        }
    }

    /**
     * SwitchCompatListener
     *  각 titleView 에서 Switchcompat을 위한 스위치 리스너
     */
    private class SwitchCompatListener implements View.OnClickListener {
        private final String TAG = "SwitchCompatListener";

        private ListAdapterOfSetting    adapter;
        private SwitchCompat            switcher;
        private int                     index;

        public SwitchCompatListener(ListAdapterOfSetting adapter, int index, SwitchCompat switcher) {
            super();
            this.adapter    = adapter;
            this.index      = index;
            this.switcher   = switcher;
        }

        /* switch 에 대한 getter 와 setter */
        public SwitchCompat getSwitcher() {
            return this.switcher;
        }

        public void setSwitcher(SwitchCompat target) {
            this.switcher = target;
        }

        /**
         * onClick      리스너 onClick 메서드 부분
         * @param view  view -
         */
        @Override
        public void onClick(View view) {
            boolean onFlag = adapter.getSwitchValue(index);

            switch (index) {
                case 0:
                    // 블루투스
                    ServiceController.controlService(onFlag, ServiceController.BLUETOOTH_SERVICE);
                    toggle(index);
                    break;
                case 1:
                    // 엑셀레이터
                    ServiceController.controlService(onFlag, ServiceController.ACCLSENSOR_SERVICE);
                    break;
                case 2:
                    // 자이로 센서
                    ServiceController.controlService(onFlag, ServiceController.GYROSENSOR_SERVICE);
                    break;
                case 3:
                    // Notification
                    ServiceController.controlService(onFlag, ServiceController.NOTIFICATION_SERVICE);
                    break;
                case 4:
                    // Wifi direct 제어
                    ServiceController.controlService(onFlag, ServiceController.WIFIDIRECT_SERVICE);
                    toggle(index);
                    break;
                default:
                    // 예외 처리
                    Log.e(TAG, "Unexpected case with index = " + index + " in onClick()");
            }
        }
    }
}
