package kr.co.aiotlab.ble_practice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Vector;

public class BeaconAdapter extends BaseAdapter {

    private Vector<Beacon> beacons;
    private LayoutInflater layoutInflater;

    public BeaconAdapter(Vector<Beacon> beacons, LayoutInflater layoutInflater) {
        this.beacons = beacons;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public int getCount() {
        return beacons.size();
    }

    @Override
    public Object getItem(int position) {
        return beacons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BeaconHolder beaconHolder;
        if (convertView == null) {
            beaconHolder = new BeaconHolder();
            convertView = layoutInflater.inflate(R.layout.item, parent, false);
            beaconHolder.address = convertView.findViewById(R.id.address);
            beaconHolder.rssi = convertView.findViewById(R.id.rssi);
            beaconHolder.time = convertView.findViewById(R.id.time);
            convertView.setTag(beaconHolder);
        } else {
            beaconHolder = (BeaconHolder)convertView.getTag();
        }

        beaconHolder.time.setText("시간 :" + beacons.get(position).getNow());
        beaconHolder.address.setText("MAC Addr :"+beacons.get(position).getAddress());
        beaconHolder.rssi.setText("RSSI :"+beacons.get(position).getRssi() + "dBm");
        return convertView;
    }

    private class BeaconHolder {
        TextView address;
        TextView rssi;
        TextView time;

    }
}
