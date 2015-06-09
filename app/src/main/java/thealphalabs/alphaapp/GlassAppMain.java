package thealphalabs.alphaapp;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Field;


public class GlassAppMain extends FragmentActivity implements View.OnClickListener {
    private final String TAG = "GlassAppMain";
    private String tabs[] = {"Controller", "Appstore", "About"};

    // Member variables for fragment
    private int mCurrentFragmentIndex;
    public final static int FRAGMENT_CONTROLLER = 0;
    public final static int FRAGMENT_APPSTORE   = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glass_app_main);

        // ActionBar
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(43, 68, 140)));
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_actionbar);

        // Fragment
        Button bt_controller = (Button) findViewById(R.id.bt_controller);
        Button bt_appstore   = (Button) findViewById(R.id.bt_appstore);
        bt_controller.setOnClickListener(this);
        bt_appstore.setOnClickListener(this);

        mCurrentFragmentIndex = FRAGMENT_CONTROLLER;
        fragmentReplace(mCurrentFragmentIndex);

    }

    public void fragmentReplace(int reqNewFragmentIndex) {
        Fragment newFragment;

        newFragment = getFragment(reqNewFragmentIndex);
        final FragmentTransaction transaction =
                        getFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_content, newFragment);
        transaction.commit();
    }
    private Fragment getFragment(int idx) {
        Fragment newFragment = null;

        switch (idx) {
            case FRAGMENT_CONTROLLER:
                newFragment = new FragmentController();
                break;
            case FRAGMENT_APPSTORE:
                newFragment = new FragmentAppstore();
                break;
            default:
                Log.d(TAG, "Unexpected case");
                break;
        }
        return newFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_controller:
                mCurrentFragmentIndex = FRAGMENT_CONTROLLER;
                fragmentReplace(mCurrentFragmentIndex);
                break;
            case R.id.bt_appstore:
                mCurrentFragmentIndex = FRAGMENT_APPSTORE;
                fragmentReplace(mCurrentFragmentIndex);
                break;
        }
    }
}
