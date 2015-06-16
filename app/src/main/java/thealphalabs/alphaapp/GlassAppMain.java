package thealphalabs.alphaapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import thealphalabs.alphaapp.dummy.DeviceListActivity;
import thealphalabs.bluetooth.BluetoothTransferHelper;
import thealphalabs.wifip2p.WifiTransferHelper;


public class GlassAppMain extends Activity {

    private EditText edit;
    private Button button;
    private FrameLayout f;
    private BluetoothTransferHelper helper;
    private final String TAG="Main";
    private BluetoothAdapter mBTAdapter;
    private TextView _text1;

    private final int REQUEST_ENABLE_BT=100;
    private final int REQUEST_CONNECT_DEVICE_INSECURE=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glass_app_main);
        edit=(EditText)findViewById(R.id.editText);
        button=(Button)findViewById(R.id.button);
        f=(FrameLayout)findViewById(R.id.layout);

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBTAdapter == null)
        {
            Log.d(TAG,"adapter is null");
        }
        else
        {
            if (!mBTAdapter.isEnabled())
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        helper=((AlphaApplication)getApplication()).getBluetoothHelper();
        f.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                helper.transferMouseData(event.getX(),event.getY(),event.getAction());
                return true;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=edit.getText().toString();
                helper.transferStringData(s);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("MainActivity.java | onActivityResult", "|==" + requestCode + "|" + resultCode + "(ok = " + RESULT_OK + ")|" + data);
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == REQUEST_ENABLE_BT)
        {
            discovery();
        }
        else if (requestCode == REQUEST_CONNECT_DEVICE_INSECURE)
        {
            String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
            Log.i("MainActivity.java | onActivityResult", "|==" + address + "|");
            if (TextUtils.isEmpty(address))
                return;
            helper.connectTo(address);
        }
    }

    private void discovery()
    {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
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
}
