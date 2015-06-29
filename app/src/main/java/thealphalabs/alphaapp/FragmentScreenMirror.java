package thealphalabs.alphaapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.hardware.display.DisplayManager;

import thealphalabs.Interface.ServiceControllerInterface;
import thealphalabs.controller.MouseViewController;

public class FragmentScreenMirror extends Fragment {

    private ServiceControllerInterface mMouseViewController;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_screen_mirror, container, false);

        return v;
    }

    private void init(){
        mMouseViewController=new MouseViewController();
    }


}
