package com.wizturn.manager;

import java.util.Scanner;

import com.wizturn.sample.R;
import com.wizturn.sdk.central.Central;
import com.wizturn.sdk.central.CentralManager;
import com.wizturn.sdk.peripheral.Peripheral;
import com.wizturn.sdk.peripheral.PeripheralScanListener;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * @author jongjoo
 */
public class ActivityMain extends Activity {
	private final String LOG_TAG = ActivityMain.class.getSimpleName();
	private final int REQUEST_ENABLE_BT = 1000;
	
	private String mode;
	
	private PeripheralListAdapter listAdapter;
	private CentralManager centralManager;
	
	// views
	private Menu menu;
	private ListView listView;
	private SwipeRefreshLayout swipeRefreshLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
	}
	
	private void init() {
		getExtras();
		setCentralManager();
		terminateIfNotBLE();
		setViews();
		turnOnBluetooth();
	}
	
	private void getExtras() {
		mode = getIntent().getStringExtra("mode");		
	}
	
	private void setCentralManager() {
		centralManager = CentralManager.getInstance();
		centralManager.init(getApplicationContext());
		centralManager.setPeripheralScanListener(new PeripheralScanListener() {			
			@Override
			public void onPeripheralScan(Central central, final Peripheral peripheral) {
				Log.d(LOG_TAG, "onPeripheralScan() : peripheral : " + peripheral);
				runOnUiThread(new Runnable() {
					public void run() {						
						listAdapter.addOrUpdateItem(peripheral);
					}
				});				
			}			
		});
	}
	
	private void terminateIfNotBLE() {
		if(!centralManager.isBLESupported()) {
			Toast.makeText(this, R.string.error_ble_not_support, Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	private void turnOnBluetooth() {
		if(!centralManager.isBluetoothEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}
	
	private void setViews() {
		setListView();
		setSwipeRefreshLayout();
	}
	
	private void setListView() {
		listView = (ListView)findViewById(R.id.listview);
		listAdapter = new PeripheralListAdapter(this, mode);
		listView.setAdapter(listAdapter);
	}
	
	private void setSwipeRefreshLayout() {
		swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);		
		swipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_bright, 
				android.R.color.holo_green_light, 
				android.R.color.holo_orange_light, 
				android.R.color.holo_red_light);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {			
			@Override
			public void onRefresh() {
				if(centralManager.isScanning())
					return;
				
				runOnUiThread(new Runnable() {
					public void run() {
						swipeRefreshLayout.setRefreshing(false);
					}
				});
			}
		});		
	}
	
	private void scan() {
		if(!centralManager.isBluetoothEnabled()) {
			turnOnBluetooth();
			return;
		}
		
		swipeRefreshLayout.setRefreshing(true);
		final MenuItem scanItem = menu.findItem(R.id.scan);
		scanItem.setTitle(R.string.actionbar_stop);
		final MenuItem clearItem = menu.findItem(R.id.clear);
		clearItem.setEnabled(false);		
		
		centralManager.startScanning();
	}
	
	private void stop() {
		swipeRefreshLayout.setRefreshing(false);
		final MenuItem item = menu.findItem(R.id.scan);
		item.setTitle(R.string.actionbar_scan);
		final MenuItem clearItem = menu.findItem(R.id.clear);
		clearItem.setEnabled(true);
		
		centralManager.stopScanning();
	}
	
	private void clear() {
		listAdapter.clear();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		
		getMenuInflater().inflate(R.menu.main_activity_actionbar, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.scan:
				if(centralManager.isScanning()) { 
					stop();					
				} else {
					scan();					
				}
				return true;				
			case R.id.clear:
				clear();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
	@Override
	protected void onDestroy() {
		if(centralManager.isScanning()) {
			centralManager.stopScanning();
		}
		
		centralManager.close();
		
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_ENABLE_BT) {
	        if (resultCode == RESULT_OK) {
	        	// TODO Okay. Now bluetooth is on. do something 
	        }
	    }
	}
}
