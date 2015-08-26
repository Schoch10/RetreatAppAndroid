package slalom.com.retreatapplication.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import slalom.com.retreatapplication.R;
import slalom.com.retreatapplication.model.Location;

/**
 * Created by senthilrajav on 8/10/15.
 */
public class CustomArrayAdapter extends ArrayAdapter<Location> {
    // declaring our ArrayList of items
    private Activity activityContext;
    private ArrayList<Location> objects;
    private Location location;
    private TextView textTitle;
    private TextView textDetail;

    /* here we must override the constructor for ArrayAdapter
    * the only variable we care about now is ArrayList<Item> objects,
    * because it is the list of objects we want to display.
    */
    public CustomArrayAdapter(Activity context, int textViewResourceId, ArrayList<Location> objects) {
        super(context, textViewResourceId, objects);
        this.activityContext = context;
        this.objects = objects;
    }

    /*
     * we are overriding the getView method here - this is what defines how each
     * list item will look.
     */
    public View getView(int position, View convertView, ViewGroup parent){

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.trending_list_item, parent, false);
        }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        location = objects.get(position);

        if (location != null) {

            textTitle = (TextView)v.findViewById(R.id.location_name);
            textDetail = (TextView)v.findViewById(R.id.check_in_count);
            //buttonRefresh = (Button)v.findViewById(R.id.buttonRefresh);

            textTitle.setText(location.getLocationName().toUpperCase());
            textDetail.setText("" + location.getCheckin());
        }
        // the view must be returned to our activity
        return v;
    }
}
