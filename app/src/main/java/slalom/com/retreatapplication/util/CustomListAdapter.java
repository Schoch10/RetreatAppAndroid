package slalom.com.retreatapplication.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import slalom.com.retreatapplication.R;

/**
 * Created by jill.burgess on 8/7/15.
 */
public class CustomListAdapter extends BaseAdapter {

    private Activity activityContext;
    private String[][] itemList;
    private Integer[] imgId;

    public CustomListAdapter(Activity context, String[][] itemList, Integer[] imgId) {
        this.activityContext = context;
        this.itemList = itemList;
        this.imgId = imgId;
    }

    @Override
    public int getCount() {
        return imgId.length;
    }

    @Override
    public String[] getItem(int position) {
        return itemList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activityContext.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.agenda_item, parent, false);
        }

        TextView textTitle = (TextView)convertView.findViewById(R.id.agenda_item_name);
        TextView textDetail = (TextView)convertView.findViewById(R.id.agenda_item_location);
        TextView textDetailTime = (TextView)convertView.findViewById(R.id.agenda_item_time);

        String title = itemList[position][0];
        String detail = itemList[position][1];
        String time = itemList[position][2];

        textTitle.setText(title);
        textDetail.setText(detail);
        textDetailTime.setText(time);
        return convertView;
    }
}