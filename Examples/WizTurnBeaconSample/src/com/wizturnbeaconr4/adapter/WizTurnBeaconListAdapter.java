package com.wizturnbeaconr4.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wizturn.sdk.entity.WizTurnBeacons;
import com.wizturnbeaconr4.R;


public class WizTurnBeaconListAdapter extends ArrayAdapter{
	
	LayoutInflater inflater = null;
	ArrayList<WizTurnBeacons> wizTrunBeacon_items;

	public WizTurnBeaconListAdapter(Context context, int textViewResourceId,
			ArrayList<WizTurnBeacons> mList) {
		super(context, textViewResourceId,  mList);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		wizTrunBeacon_items = mList;
	}
	
	public boolean contains(String macAddr) {
		for (int i = 0; i < wizTrunBeacon_items.size(); i++) {
			if (wizTrunBeacon_items.get(i) != null && wizTrunBeacon_items.get(i).getMacAddress().equals(macAddr)) {
				return true;
			}
		}
		return false;
	}
	
	//beacon add Item
	public void addItem(WizTurnBeacons item){
		wizTrunBeacon_items.add(item);
	}
	
	//beacon get Item
	public WizTurnBeacons getItem(int position) {
		return wizTrunBeacon_items.get(position);
	}
	
	//beaconList Clear
	public void clearItem() {
		wizTrunBeacon_items.clear();
	}
	
	public View getView(int position, View v, ViewGroup parent) {
		if(v==null){
			v = inflater.inflate(R.layout.array_scanlist, null);
		}
		
		TextView mSSID = (TextView)v.findViewById(R.id.scanList_SSID);
		TextView mUUID = (TextView)v.findViewById(R.id.scanList_UUID);
		TextView mMacAddr= (TextView)v.findViewById(R.id.scanList_MacAddr);
		TextView mMajor = (TextView)v.findViewById(R.id.scanList_Major);
		TextView mMinor= (TextView)v.findViewById(R.id.scanList_Minor);
		
		mSSID.setText("SSID: " + wizTrunBeacon_items.get(position).getName());
		mMacAddr.setText("MacAddr: " + wizTrunBeacon_items.get(position).getMacAddress());
		mUUID.setText("UUID: " + wizTrunBeacon_items.get(position).getProximityUUID());
		mMajor.setText("Major: " + wizTrunBeacon_items.get(position).getMajor());
		mMinor.setText("Minor: " + wizTrunBeacon_items.get(position).getMinor());
		
		return v;
		
	}

	

}
