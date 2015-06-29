package thealphalabs.alphaapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.ArrayAdapter;

import java.util.ArrayList;

import thealphalabs.alphaapp.R;
import thealphalabs.alphaapp.model.HomeDataModel;

/**
 * GoogleCardAdapter
 *  HOME 섹션 부분을 그려주기 위해 사용되는 Adapter 클래스이다.
 *
 * @version : 1.0
 * @author  : Sukbeom Kim(sbkim@thealphalabs.com)
 */
public class GoogleCardsAdapter extends ArrayAdapter<HomeDataModel> {

    private final Context mContext;
    private ArrayList<HomeDataModel> dataModel;

    public GoogleCardsAdapter(Context context) {
        mContext = context;
        dataModel = new ArrayList<>();
    }

    public GoogleCardsAdapter(Context context, ArrayList<HomeDataModel> data) {
        mContext = context;
        dataModel = data;

        for (int i = 0; i < data.size(); i++) {
            add(i, data.get(i));
        }
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.fragment_home_card, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.header = (TextView) view.findViewById(R.id.card_header);
            view.setTag(viewHolder);

            viewHolder.imageView = (ImageView) view.findViewById(R.id.card_imageview);
            viewHolder.content = (TextView) view.findViewById(R.id.card_content);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.header.setText(getItem(position).getHeader());
        viewHolder.content.setText(getItem(position).getContent());
        viewHolder.imageView.setImageResource(getItem(position).getResId());

        return view;
    }

    @SuppressWarnings({"PackageVisibleField", "InstanceVariableNamingConvention"})
    private static class ViewHolder {
        TextView header;
        ImageView imageView;
        TextView content;
    }
}