package thealphalabs.alphaapp.adapter;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import thealphalabs.alphaapp.R;

/**
 * ListAdapterOfWifiDirect
 *
 *  Wifi Direct 장치 스캔시에 사용될 리스트뷰를 위한 Adapter class
 */
public class ListAdapterOfWifiDirect extends ArrayAdapter<String> {
    Context mContext;
    ArrayMap<String, String> itemList;

    public ListAdapterOfWifiDirect(Context context, int resource, ArrayMap<String, String> model) {
        super(context, resource);
        mContext = context;
        itemList = model;

        for (int i = 0; i < itemList.size(); i++) {
            String key = itemList.keyAt(0);
            add(key);
        }
    }


    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public String getItem(int position) {
        return itemList.keyAt(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.listitem_wdlist, parent, false);

        TextView dev_name = (TextView) v.findViewById(R.id.listitem_wd_name);
        dev_name.setText("Test");

        return v;
    }
}
