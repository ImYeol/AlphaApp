package thealphalabs.alphaapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import thealphalabs.alphaapp.AlphaApplication;

/**
 * Created by sukbeom on 15. 6. 22.
 */
public class CustomEditText extends EditText{
    private final String TAG = "CustomEditText";
    private Context mContext;

    public CustomEditText(Context context) {
        super(context);
        mContext = context;
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }


    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {

            // 처리
            this.setVisibility(GONE);
            Log.d(TAG, "[From onKeyPreIme]User input : " + getText());

            // 여기서는 Visibility 만 GONE 으로 처리한다.
            // 사용자가 실수로 키보드만 닫을 수 있기 때문에, 이 경우 텍스트는 서버로 보내지 않고
            // 그대로 남겨둔다.
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onEditorAction(int actionCode) {
        // 사용자가 DONE 버튼을 클릭했을 때에만 서버로 전송한다.
        super.onEditorAction(actionCode);

        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
        setVisibility(View.GONE);
        Log.d(TAG, "[From onEditorAction]User input : " + getText());

        // 서버로 텍스트 보내고 난 뒤에 텍스트 초기화
        sendTextData(getText().toString());
        setText("");
    }

    private void sendTextData(String data) {
        Log.d(TAG, "sendTextData" + data);
        ((AlphaApplication)mContext.getApplicationContext()).getBluetoothHelper().transferStringData(data);
    }
}
