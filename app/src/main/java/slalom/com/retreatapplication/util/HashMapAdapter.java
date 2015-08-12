package slalom.com.retreatapplication.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import slalom.com.retreatapplication.R;

/**
 * Created by senthilrajav on 8/11/15.
 */
public class HashMapAdapter  extends BaseAdapter {
    private Map<String, Integer> mData = new HashMap<String, Integer>();
    private String[] mKeys;
    private Activity activityContext;
    private TextView textDetail;

    public HashMapAdapter(Activity context, HashMap<String, Integer> data){
        mData  = data;
        mKeys = mData.keySet().toArray(new String[data.size()]);
        this.activityContext = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(mKeys[position]);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        String key = mKeys[pos];
        String Value = getItem(pos).toString();

        //do your view stuff here
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activityContext.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.image_list, parent, false);
        }
        TextView textTitle = (TextView)convertView.findViewById(R.id.item);
        textDetail = (TextView)convertView.findViewById(R.id.textView1);

        textTitle.setText(key);
        textDetail.setText("Checked In: " + Value);

        // Button click listener
        textDetail.setOnClickListener(new View.OnClickListener() {
            // When Button is clicked
            public void onClick(View v) {
                // Disable the button to avoid playing of song multiple times
                textDetail.setEnabled(false);
                // Trigger Async Task (onPreExecute method)
                new TPartyTask().execute("getCheckIns", activityContext);
            }
        });

        return convertView;
    }
}
