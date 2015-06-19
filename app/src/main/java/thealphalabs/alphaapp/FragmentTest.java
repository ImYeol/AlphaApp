package thealphalabs.alphaapp;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by sukbeom on 15. 6. 19.
 */
public class FragmentTest extends Fragment implements View.OnClickListener{
    private final String TAG = "FragmentTest";
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 블루투스 아이템항목
        View v = inflater.inflate(R.layout.activity_fragment_test, container, false);
        Button btn = (Button) v.findViewById(R.id.test_btn);
        btn.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick");

//      Notification 생성
        NotificationManager nm = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.ic_launcher, "Nomal Notification", System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE ;
        notification.number = 13;
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, new Intent(getActivity(), GlassAppMain.class), PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(getActivity(), "Nomal Title", "Nomal Summary", pendingIntent);

        nm.notify(1234, notification);
    }
}
