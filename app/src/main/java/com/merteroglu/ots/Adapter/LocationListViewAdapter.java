package com.merteroglu.ots.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.merteroglu.ots.Model.Location;
import com.merteroglu.ots.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class LocationListViewAdapter extends ArrayAdapter {
    private final Context context;
    private List locations = new ArrayList();

    public LocationListViewAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    @Override
    public void add(@Nullable Object object) {
        super.add(object);
        locations.add(object);
    }

    @Override
    public int getCount() {
        return locations.size();
    }

    public void clearAll(){
        locations.clear();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return locations.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        LocationHolder locationHolder;

        if(row == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.locationlistview_item,parent,false);
            locationHolder = new LocationHolder();

            locationHolder.actionIcon = row.findViewById(R.id.actionIcon);
            locationHolder.txtTime = row.findViewById(R.id.txtTime);
            locationHolder.txtLocation = row.findViewById(R.id.txtLocation);

            row.setTag(locationHolder);
        }else{
            locationHolder = (LocationHolder) row.getTag();
        }

        final Location location = (Location) this.getItem(position);

        locationHolder.txtLocation.setText(location.getLocation().getLatitude() + " , " + location.getLocation().getLongitude());

        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm");
        locationHolder.txtTime.setText(sdf.format(location.getTime().toDate()));

        if(location.isAction())
            locationHolder.actionIcon.setBackground(ContextCompat.getDrawable(this.context,R.drawable.on_bus_ico));
        else
            locationHolder.actionIcon.setBackground(ContextCompat.getDrawable(this.context,R.drawable.off_bus_ico));

        return row;
    }

    static class LocationHolder{
        ImageView actionIcon;
        TextView txtTime,txtLocation;
    }

}
