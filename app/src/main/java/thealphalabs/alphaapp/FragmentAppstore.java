package thealphalabs.alphaapp;

import android.app.Fragment;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
        ApplicationInfo app = getActivity().getApplicationContext().getApplicationInfo();
        final File target = new File("/mnt/sdcard/Download/Catch_5.2.11.apk");

        try
        {
            final InputStream inputStream = new FileInputStream(target);
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();

            Log.d(TAG, "file size = " + String.valueOf(target.length()));

            byte[] b = new byte[(int) target.length()];  // 1024 as buffer size

            int bytesRead   = 0;
            int count       = 0;

            try {
                bytesRead = inputStream.read(b);

                bos.write(b, 0, bytesRead);

                Log.d(TAG, "finish to create byte array (" + bytesRead + ")");

            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "count = " + count + ". This is same as " + count * 1024 + " bytes and outputstream size = " + bos.size());

            ((AlphaApplication)getActivity().getApplication()).getBluetoothHelper().transferFileData(bos.toByteArray(), bos.size());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    private byte[] fileToByteArray(File f) {
        byte[] byteArray = null;
        try
        {
            InputStream inputStream = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            Log.d(TAG, "file size = " + Integer.parseInt(String.valueOf(f.length()/1024)));
            byte[] b = new byte[Integer.parseInt(String.valueOf(f.length()/1024))];
            int bytesRead =0;

            while ((bytesRead = inputStream.read(b)) != -1)
            {
                bos.write(b);
            }

            byteArray = bos.toByteArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return byteArray;
    }
}
