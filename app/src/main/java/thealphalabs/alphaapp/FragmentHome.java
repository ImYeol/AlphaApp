package thealphalabs.alphaapp;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;

import java.util.ArrayList;

import thealphalabs.alphaapp.adapter.GoogleCardsAdapter;
import thealphalabs.alphaapp.model.HomeDataModel;

/**
 * Created by sukbeom on 15. 6. 15.
 */
public class FragmentHome extends Fragment implements OnDismissCallback {
    private static final int INITIAL_DELAY_MILLIS = 300;

    private GoogleCardsAdapter mGoogleCardsAdapter;
    private ArrayList<HomeDataModel> home_data;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ListView listView = (ListView) view.findViewById(R.id.fragment_home_listview);

        // Home Fragment에 나타낼 데이터 모델 준비
        home_data = new ArrayList<>();
        prepareDataModel(home_data);

        mGoogleCardsAdapter = new GoogleCardsAdapter(getActivity().getBaseContext(), home_data);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new SwipeDismissAdapter(mGoogleCardsAdapter, this));
        swingBottomInAnimationAdapter.setAbsListView(listView);

        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

        listView.setAdapter(swingBottomInAnimationAdapter);


        return view;
    }

//   추후에 notification center로 사용
    @Override
    public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
//        유저가 해당 아이템을 옆으로 swipe 했을 때(Dismiss 하지 않고 남겨둔다)
        for (int position : reverseSortedPositions) {
            mGoogleCardsAdapter.remove(position);
        }
    }

    private void prepareDataModel(ArrayList<HomeDataModel> data) {
        data.add(new HomeDataModel("AlphaLabs의 알파글래스 출시!", "드디어 AlphaLabs의 알파글래스 프로토타입이 완성되었습니다.", R.mipmap.alphalabs_prototype));
    }
}
