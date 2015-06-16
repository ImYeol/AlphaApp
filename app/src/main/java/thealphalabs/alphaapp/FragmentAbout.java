package thealphalabs.alphaapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by chaoxifer on 6/12/15.
 */
public class FragmentAbout extends Fragment {
    private final String TAG = "FragmentAbout";
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        final Context context = getActivity().getBaseContext();

        // 패키지 버전 얻어오는 부분
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView version = (TextView) view.findViewById(R.id.about_version);
        version.setText(pInfo.versionName);

        // License TextView
        TextView license = (TextView) view.findViewById(R.id.about_license);
        license.setOnClickListener(new LicenseBtnListener(context));
        return view;
    }

    class LicenseBtnListener implements View.OnClickListener {
        private Context context;

        LicenseBtnListener(Context context) {
            this.context = context;
        }
        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick : ");
            Intent i = new Intent(context, new LicenseActivity().getClass());
            startActivity(i);
        }
    }
}
