package thealphalabs.alphaapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import java.util.ArrayList;

import thealphalabs.alphaapp.adapter.ListAdapterOfSetting;
import thealphalabs.alphaapp.view.ListItemOfController;

/**
 * Created by sukbeom on 15. 6. 15.
 */
public class FragmentSetting extends Fragment {
    private final String TAG = "FragmentController";
    private ArrayList<ListItemOfController> listOfController;
    private ListAdapterOfSetting adapter;
    private ListView listview;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initializeItem();
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        listview = (ListView) view.findViewById(R.id.listview_controller);

        adapter = new ListAdapterOfSetting(getActivity(), listOfController);

        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(adapter);
        alphaInAnimationAdapter.setAbsListView(listview);
        alphaInAnimationAdapter.getViewAnimator().setInitialDelayMillis(100);
        listview.setAdapter(alphaInAnimationAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private void initializeItem() {
        // 멤버 변수 초기화
        // 1. 리스트에 들어갈 ListItemOfController 타입의 아이템들을 모두 초기화한다.
        listOfController = new ArrayList<>();
        listOfController.add(new ListItemOfController(R.drawable.ic_bluetooth_black_48dp,
                "Bluetooth", getString(R.string.desc_bluetooth), false));
        listOfController.add(new ListItemOfController(R.drawable.ic_gamepad_black_48dp,
                "Sensor", getString(R.string.desc_accelerator), false));
        listOfController.add(new ListItemOfController(R.drawable.ic_data_usage_black_48dp,
                "Gyro Sensor", getString(R.string.desc_gyrosensor), false));
        listOfController.add(new ListItemOfController(R.drawable.ic_notifications_black_48dp,
                "Notification", getString(R.string.notification_service_desc), false));
    }
}
