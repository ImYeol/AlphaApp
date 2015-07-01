package thealphalabs.alphaapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.hardware.display.DisplayManager;

import thealphalabs.Interface.ServiceControllerInterface;
import thealphalabs.alphaapp.view.MouseView;
import thealphalabs.controller.MouseViewController;
import thealphalabs.controller.ServiceController;

public class FragmentScreenMirror extends Fragment {

    private static final String TAG="FragmentScreenMirror";
    private ServiceControllerInterface mMouseViewController;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_screen_mirror, container, false);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        //startService();
        Settings.System.putInt(getActivity().getContentResolver(),"show_touches",1);
        startWFDSetting();
    }

    @Override
    public void onPause() {
        super.onPause();
        //stopService();
    }

    private void startWFDSetting(){
        try
        {
            Log.d(TAG, "open WiFi display settings in HTC");
            startActivity(new Intent("com.htc.wifidisplay.CONFIGURE_MODE_NORMAL"));
        } catch (Exception e)
        {
            try
            {
                Log.d(TAG, "open WiFi display settings in Samsung");
                startActivity(new Intent("com.samsung.wfd.LAUNCH_WFD_PICKER_DLG"));
            }catch (Exception e2)
            {
                Log.d(TAG, "open WiFi display settings in stock Android");
                startActivity(new Intent("android.settings.WIFI_DISPLAY_SETTINGS"));
            }
        }
    }
    private void startService(){
        mMouseViewController= MouseViewController.getInstance(getActivity().getBaseContext());
        ServiceController.registerService(ServiceController.MOUSEVIEW_SERVICE,mMouseViewController);
        ServiceController.startService(ServiceController.MOUSEVIEW_SERVICE);
    }

    private void stopService(){
        ServiceController.stopService(ServiceController.MOUSEVIEW_SERVICE);
        ServiceController.unregisterService(ServiceController.MOUSEVIEW_SERVICE);
    }


}
