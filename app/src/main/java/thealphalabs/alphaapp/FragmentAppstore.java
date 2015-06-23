package thealphalabs.alphaapp;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.astuetz.PagerSlidingTabStrip;

import java.io.File;
import java.util.ArrayList;

import thealphalabs.alphaapp.adapter.SensorController;

public class FragmentAppstore extends Fragment implements View.OnClickListener {
    private final String TAG = "FragmentAppstore";
    private Button apk_btn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_appstore, container, false);

        // Initialize the ViewPager and set an adapter
//        ViewPager pager = (ViewPager) v.findViewById(R.id.pager);
//        pager.setAdapter(new TestAdapter(getSupportFragmentManager()));

        // Bind the tabs to the ViewPager
//        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) v.findViewById(R.id.tabs);
//        tabs.setViewPager(pager);


        apk_btn = (Button) v.findViewById(R.id.apk_btn);
        apk_btn.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick");

        ApplicationInfo app = getActivity().getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;


        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        Log.d(TAG, "path = " + filePath);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
        startActivity(intent);
    }
}
