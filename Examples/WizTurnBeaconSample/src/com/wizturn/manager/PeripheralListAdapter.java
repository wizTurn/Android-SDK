package com.wizturn.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

import com.wizturn.sample.R;
import com.wizturn.sdk.peripheral.Peripheral;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PeripheralListAdapter extends BaseAdapter {
	private final String LOG_TAG = PeripheralListAdapter.class.getSimpleName();
	private final String COLON = " : ";
	private final String unknown;
	
	final private String mode;
	private boolean isConnectingButtonTouching = false;
	
	private ArrayList<Peripheral> items = new ArrayList<Peripheral>();
	private HashMap<String, Peripheral> itemMap = new HashMap<String, Peripheral>();
	private LayoutInflater inflater;
	
	public PeripheralListAdapter(Context context, String mode) {
		this.mode = mode; 
		unknown = context.getString(R.string.unknown);
		inflater = LayoutInflater.from(context);
	}
	
	public synchronized void addOrUpdateItem(Peripheral peripheral) {
		if(itemMap.containsKey(peripheral.getBDAddress())) {
			itemMap.get(peripheral.getBDAddress()).setRssi(peripheral.getRssi());			
		} else {
			items.add(peripheral);
			itemMap.put(peripheral.getBDAddress(), peripheral);
		}
		
		// Regardless of a matching device is found or not, 
    	// the following method must be invoked. by Jayjay
    	if(!isConnectingButtonTouching) {
    		notifyDataSetChanged();
    	}		
	}
	
	public void clear() {
		items.clear();
		itemMap.clear();
		
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		final ViewHolder holder;
		final Context context = parent.getContext();
		
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.peripheral_list_row, null);
			
			holder = new ViewHolder();
			holder.textUuid = (TextView)convertView.findViewById(R.id.text_uuid);
			holder.textBdName = (TextView)convertView.findViewById(R.id.text_bd_name);
			holder.textBdAddress = (TextView)convertView.findViewById(R.id.text_bd_address);
			holder.textMajor = (TextView)convertView.findViewById(R.id.text_major);
			holder.textMinor = (TextView)convertView.findViewById(R.id.text_minor);
			holder.textRssi = (TextView)convertView.findViewById(R.id.text_rssi);
			holder.buttonConnect = (Button)convertView.findViewById(R.id.button_connect);
			holder.imageIBeacon = (ImageView)convertView.findViewById(R.id.image_ibeacon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		final Peripheral peripheral = items.get(position);
		if(peripheral != null) {
			String uuid = peripheral.getProximityUUID() == null ? unknown : 
				peripheral.getProximityUUID();
			holder.textUuid.setText(context.getString(R.string.uuid) + 
					COLON + uuid);
			String bdName = peripheral.getBDName() == null ? unknown : 
				peripheral.getBDName();
			holder.textBdName.setText(context.getString(R.string.bd_name) + 					
					COLON + bdName);
			holder.textBdAddress.setText(context.getString(R.string.bd_address) + 
					COLON + peripheral.getBDAddress());
			String major = peripheral.getMajor() == -1 ? unknown : 
				String.valueOf(peripheral.getMajor());
			holder.textMajor.setText(context.getString(R.string.major) + 
					COLON + major);
			String minor = peripheral.getMinor() == -1 ? unknown :
				String.valueOf(peripheral.getMinor());
			holder.textMinor.setText(context.getString(R.string.minor) + 
					COLON + minor);
			holder.textRssi.setText(context.getString(R.string.rssi) + 
					COLON + String.valueOf(peripheral.getRssi()) + 
					context.getString(R.string.unit_dbm));
			
			if(peripheral.isIBeacon()) {
				holder.imageIBeacon.setVisibility(View.VISIBLE);				
			} else {
				holder.imageIBeacon.setVisibility(View.INVISIBLE);
			}
			
			holder.buttonConnect.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(LOG_TAG, "buttonConnect.onClickListener() : peripheral : " + 
							peripheral.hashCode());
					Intent intent = null;
					if(mode.equalsIgnoreCase("normal")) {
						intent = new Intent(context, ActivityNormalConnectChange.class);						
					} else if(mode.equalsIgnoreCase("delayed")){
						intent = new Intent(context, ActivityDelayedConnectChange.class);
					} else if(mode.equalsIgnoreCase("group")) {
						intent = new Intent(context, ActivityMultipleChange.class);
					}
					
					intent.putExtra("peripheral", peripheral);
					context.startActivity(intent);
				}
			});
			holder.buttonConnect.setOnTouchListener(new View.OnTouchListener() {				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					final int action = event.getAction();
					
					if(action == MotionEvent.ACTION_DOWN) {
						isConnectingButtonTouching = true;
						holder.buttonConnect.setTextColor(Color.WHITE);
					} else if(action == MotionEvent.ACTION_UP ||
							action == MotionEvent.ACTION_CANCEL) {
						isConnectingButtonTouching = false;
						holder.buttonConnect.setTextColor(Color.BLACK);
					}
					
					return false;
				}
			});			
		}
		
		return convertView;
	}

	private class ViewHolder {
		public TextView textUuid;
		public TextView textBdName;
		public TextView textBdAddress;
		public TextView textMajor;
		public TextView textMinor;
		public TextView textRssi;
		public Button buttonConnect;
		public ImageView imageIBeacon;
	}
}
