package thealphalabs.alphaapp.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import thealphalabs.alphaapp.R;

/**
 * Created by yeol on 15. 7. 1.
 */
public class MouseView extends View {

    private static final int KUMA_SIZE = 48;
    private Bitmap kuma;
    private int w;
    private int h;
    private int x;
    private int y;
    private Handler handler=new Handler(Looper.getMainLooper());
    private static final String TAG="MouseView";

    public MouseView(Context context) {

        super(context);

        //    requestWindowFeature(Window.FEATURE_NO_TITLE);

        kuma = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.transparent_round);

    }

//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        super.dispatchTouchEvent(event);
//
//        this.x= (int)event.getX();
//        this.y= (int)event.getY();
//
//        Log.d(TAG, "onTouchEvent");
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                invalidate();
//            }
//        });
//        // super.onTouchEvent(event);
//
//        return false;
//    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        this.w = w;
        this.h = h;
        this.x = (w - KUMA_SIZE) / 2;
        this.y = (h - KUMA_SIZE) / 2;
    }

    @Override

    protected void onDraw(Canvas canvas) {
        Log.d(TAG,"onDraw");
        canvas.drawBitmap(kuma, x, y, null);

    }

}
