package thealphalabs.alphaapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


public class GlassAppMain extends FragmentActivity implements View.OnClickListener, View.OnTouchListener {
    private final String TAG = "GlassAppMain";
    private IDataTransferService dataTransferService = ((AlphaApplication)getApplication()).getDataTransferService();
    // Member variables for fragment
    int mCurrentFragmentIndex;
    public final static int FRAGMENT_CONTROLLER = 0;
    public final static int FRAGMENT_APPSTORE   = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glass_app_main);

        // Fragment
        Button bt_controller = (Button) findViewById(R.id.bt_controller);
        Button bt_appstore   = (Button) findViewById(R.id.bt_appstore);
        bt_controller.setOnClickListener(this);
        bt_appstore.setOnClickListener(this);

        mCurrentFragmentIndex = FRAGMENT_CONTROLLER;
        fragmentReplace(mCurrentFragmentIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_glass_app_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void onControllerFragmentInteraction(String string)
    {
        Log.d(TAG, "controllerFragment");
    }

    public void onAppstroeFragmentInteraction(String string)
    {
        Log.d(TAG, "appstoreFragment");
    }

    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "x = " + event.getX() + ", y = " + event.getY());

        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "x = " + event.getX() + ", y = " + event.getY());

        try {
            dataTransferService.transferMouseData(event.getX(), event.getY());
        }
        catch (RemoteException ex) {
            Log.e(TAG, "RemoteException occurred: " + ex);
        }
        return super.onTouchEvent(event);
    }
}
