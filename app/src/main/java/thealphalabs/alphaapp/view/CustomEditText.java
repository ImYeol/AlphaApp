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

            // 서버로 텍스트 보내고 난 뒤에 텍스트 초기화
            setText("");
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onEditorAction(int actionCode) {
        super.onEditorAction(actionCode);

        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
        setVisibility(View.GONE);
        Log.d(TAG, "[From onEditorAction]User input : " + getText());

        setText("");
    }
}
