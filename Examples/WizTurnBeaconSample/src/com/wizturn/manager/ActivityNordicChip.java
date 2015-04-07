package com.wizturn.manager;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import com.wizturn.manager.dialog.DialogAuthentication;
import com.wizturn.manager.dialog.DialogLEDMode;
import com.wizturn.manager.dialog.DialogSettingAdvertisementInterval;
import com.wizturn.manager.dialog.DialogSettingBdname;
import com.wizturn.manager.dialog.DialogSettingCurrentTime;
import com.wizturn.manager.dialog.DialogSettingMajor;
import com.wizturn.manager.dialog.DialogSettingMinor;
import com.wizturn.manager.dialog.DialogSettingPassword;
import com.wizturn.manager.dialog.DialogSettingSleepTime;
import com.wizturn.manager.dialog.DialogSettingTxPower;
import com.wizturn.manager.dialog.DialogSettingUUID;
import com.wizturn.manager.dialog.OnClickListener;
import com.wizturn.sample.R;
import com.wizturn.sdk.peripheral.PeripheralChangeEvent;
import com.wizturn.sdk.central.BluetoothGattWriter;
import com.wizturn.sdk.central.CentralManager;
import com.wizturn.sdk.peripheral.OnConnectListener;
import com.wizturn.sdk.peripheral.Peripheral;
import com.wizturn.sdk.peripheral.Peripheral.Characteristics;
import com.wizturn.sdk.peripheral.PeripheralAccessListener;
import com.wizturn.sdk.peripheral.PeripheralEvent;
import com.wizturn.sdk.time.SleepTime;
import com.wizturn.sdk.time.Time;
import com.wizturn.sdk.utils.DeviceInfoHelper;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityNordicChip extends FragmentActivity implements 
	Observer, android.view.View.OnClickListener {
	private final String LOG_TAG = ActivityNordicChip.class.getSimpleName();
	private final int DIALOG_AUTHENTICATION = 0;
	private final int DIALOG_SETTING_ADVERTISEMENT_INTERVAL = 1;
	private final int DIALOG_SETTING_TXPOWER = 2;
	private final int DIALOG_SETTING_MAJOR = 3;
	private final int DIALOG_SETTING_MINOR = 4;
	private final int DIALOG_SETTING_UUID = 5;
	private final int DIALOG_SETTING_PASSWORD = 6;
	private final int DIALOG_SETTING_CURRENT_TIME = 7;
	private final int DIALOG_SETTING_SLEEP_TIME = 8;
	private final int DIALOG_SETTING_BDNAME = 9;
	private final int DIALOG_LED_MODE = 10;
	
	private String unitDBM;
	private String unitDistance;
	private String unknown;
	
	private Peripheral peripheral;
	private CentralManager centralManager;
	private Timer connectingAniTimer;
	
	Handler handler = new Handler();
	
	// Views
	private Toast toast;
	private MenuItem connectMenuItem;
	private TextView textMajor;
	private TextView textMinor;
	private TextView textRssi;
	private TextView textMeasuredPower;
	private TextView textDistance;
	private TextView textProximity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect_nordic);
			
		init();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.connect_activity_actionbar, menu);
		connectMenuItem = menu.findItem(R.id.connect);
		connectMenuItem.setEnabled(false);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	private void init() {
		toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
		unitDBM = getString(R.string.unit_dbm);
		unitDistance = getString(R.string.unit_distance);
		unknown = getString(R.string.unknown);
		
		setCentralManager();
		getDataFromIntent();
		setViews();
		connect();
	}
	
	private void setCentralManager() {
		centralManager = CentralManager.getInstance();
	}
	
	private void getDataFromIntent() {
		peripheral = getIntent().getParcelableExtra("peripheral");
		
		if(peripheral == null) {
			toast.setText(R.string.error_null_peripheral);
			toast.show();			
			finish();
		}
		
		// The passed peripheral object is not real object which is cached. 
		// It's serialized one. Here we need an real instance of Peripheral class which is cached.
		peripheral = centralManager.getPeripheral(peripheral.getBDAddress());
		// The activity now observes the peripheral to update realtime changing variables.
		peripheral.addObserver(this);
		Log.d(LOG_TAG, "getDataFromIntent() : peripheral : " + peripheral.hashCode()); 
	}
	
	private void setViews() {
		setGroup1();
		setGroup2();
		setGroup3();
		setGroup4();
	}
	
	private void connect() {
		startConnectingAnimation();
		
		centralManager.connectWithDelay(peripheral, onConnectListener);
	}
	
	private void setGroup1() {	
		Log.d(LOG_TAG, "setGroup1() is called...");
		
		TextView textview = null;
		
		textview = (TextView)findViewById(R.id.text_uuid_value);
		String uuid = peripheral.getProximityUUID() == null ? unknown : 
			peripheral.getProximityUUID();
		textview.setText(uuid);
		textview = (TextView)findViewById(R.id.text_bdname_value);
		String bdName = peripheral.getBDName() == null ? unknown :
			peripheral.getBDName();
		textview.setText(bdName);
		textview = (TextView)findViewById(R.id.text_bdaddress_value);
		textview.setText(peripheral.getBDAddress());
		textMajor = (TextView)findViewById(R.id.text_major_value);
		String major = peripheral.getMajor() == -1 ? unknown : 
			String.valueOf(peripheral.getMajor());
		textMajor.setText(String.valueOf(major));
		textMinor = (TextView)findViewById(R.id.text_minor_value);
		String minor = peripheral.getMinor() == -1 ? unknown : 
			String.valueOf(peripheral.getMinor());
		textMinor.setText(String.valueOf(minor));
		
		textRssi = (TextView)findViewById(R.id.text_rssi_value);
		textRssi.setText(String.valueOf(peripheral.getRssi()) + unitDBM);
		textMeasuredPower= (TextView)findViewById(R.id.text_measured_power_value);
		String measuredPower = peripheral.getMeasuredPower() == -999 ?
			unknown : String.valueOf(peripheral.getMeasuredPower());
		textMeasuredPower.setText(measuredPower + unitDBM);
		textDistance = (TextView)findViewById(R.id.text_distance_value);
		String distance = peripheral.getMeasuredPower() == -999 ?
				unknown : String.valueOf(peripheral.getDistance()) + unitDistance;
		textDistance.setText(distance);
		textProximity = (TextView)findViewById(R.id.text_proximity_value);
		String proximity = peripheral.getMeasuredPower() == -999 ?
				unknown : peripheral.getProximity().toString();
		textProximity.setText(proximity);
		
		ViewGroup layout = null;
		layout = (ViewGroup)findViewById(R.id.layout_uuid);
		layout.setOnClickListener(this);
		layout = (ViewGroup)findViewById(R.id.layout_major);
		layout.setOnClickListener(this);
		layout = (ViewGroup)findViewById(R.id.layout_minor);
		layout.setOnClickListener(this);
		layout = (ViewGroup)findViewById(R.id.layout_bdname);
		layout.setOnClickListener(this);
	}
	
	private void setGroup2() {
		Log.d(LOG_TAG, "setGroup2() is called...");
		
		if(peripheral.getCharacteristics() == null)
			return;
		
		TextView textview = null;
		
		textview = (TextView)findViewById(R.id.text_txpower_value);
		textview.setText(String.valueOf(peripheral.getCharacteristics().getTxPower()) + unitDBM);
		textview = (TextView)findViewById(R.id.text_interval_value);
		textview.setText(String.valueOf(
				peripheral.getCharacteristics().getAdvertisementIntervalTime()) + "Hz");
		textview = (TextView)findViewById(R.id.text_battery_level_value);
		textview.setText(String.valueOf(peripheral.getCharacteristics().getBatteryPercent()) + "%");
		
		ViewGroup layout = null;
		layout = (ViewGroup)findViewById(R.id.layout_txpower);
		layout.setOnClickListener(this);
		layout = (ViewGroup)findViewById(R.id.layout_advertisement_time_interval);
		layout.setOnClickListener(this);
		
		// Nordic model handling
		final Characteristics characteristic = peripheral.getCharacteristics();
		View layoutCurrentTime = findViewById(R.id.layout_current_time);				
		View layoutSleepTime = findViewById(R.id.layout_sleep_time);		
		layoutCurrentTime.setOnClickListener(this);
		layoutCurrentTime.setOnLongClickListener(new View.OnLongClickListener() {				
			@Override
			public boolean onLongClick(View v) {
				if(!centralManager.isConnected()) {
					toast.setText(R.string.error_is_not_connected);
					return true;
				}
				
				final BluetoothGattWriter writer = centralManager.getBluetoothGattWriter();
				writer.setPeripheralAccessListener(accessListener);
				writer.readCurrentTime();
				
				return true;
			}
		});
		layoutSleepTime.setOnClickListener(this);
		textview = (TextView)findViewById(R.id.text_current_time_value);
		textview.setText(getZeroPaddedNumber(characteristic.getCurrentTime().getHour()) + ":" +
				getZeroPaddedNumber(characteristic.getCurrentTime().getMinute()));
		textview = (TextView)findViewById(R.id.text_sleep_time_value);
		textview.setText((characteristic.getSleepTime().isEnabled() == true ? "enabled  " : "disabled  ") +
				getZeroPaddedNumber(characteristic.getSleepTime().getStartTimeHour()) + ":" +
				getZeroPaddedNumber(characteristic.getSleepTime().getStartTimeMinute()) + "~" +
				getZeroPaddedNumber(characteristic.getSleepTime().getEndTimeHour()) + ":" +
				getZeroPaddedNumber(characteristic.getSleepTime().getEndTimeMinute()));
		
		View layoutLedMode = findViewById(R.id.layout_led_mode);
		layoutLedMode.setOnClickListener(this);

		final String ledMode;
		switch(characteristic.getLedMode()) {
			case -1:
				ledMode = "not supported";
				break;
			case 0:
				ledMode = "turn off[mode : 0]";
				break;
			case 1:
				ledMode = "turn on[mode : 1]";
				break;
			case 2:
				ledMode = "turn on and make blink[mode : 2]";
				break;
			default:
				ledMode = "error";
		}
		textview = ((TextView)findViewById(R.id.text_led_mode_value));
		textview.setText(ledMode);
	}
	
	private void setGroup3() {
		Log.d(LOG_TAG, "setGroup3() is called...");
		
		if(peripheral.getCharacteristics() == null)
			return;
				
		TextView textview = null;
		
		textview = (TextView)findViewById(R.id.text_hardware_version_value);
		textview.setText(peripheral.getCharacteristics().getHardwareVersion());
		textview = (TextView)findViewById(R.id.text_firmware_version_value);
		textview.setText(peripheral.getCharacteristics().getSoftwareVersion());
		// Nordic model handling
		textview = (TextView)findViewById(R.id.text_model_number_value);
		textview.setText(peripheral.getModelNumber());
		textview = (TextView)findViewById(R.id.text_manufacturer_name_value);
		textview.setText(peripheral.getManufacturerName());
	}
	
	private void setGroup4() {
		Log.d(LOG_TAG, "setGroup4() is called...");
		
		ViewGroup layout = null;
		layout = (ViewGroup)findViewById(R.id.layout_password);
		layout.setOnClickListener(this);
	}
	
	protected String getZeroPaddedNumber(int number) {
		if(number >=0 && number <=9) {
			return "0" + String.valueOf(number);
		} else {
			return String.valueOf(number);
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		if(observable instanceof Peripheral) {
			final Peripheral peripheral = (Peripheral)observable;
			runOnUiThread(new Runnable() {				
				@Override
				public void run() {
					setGroup1();
					setGroup2();
				}
			});			
		}
	}

	@Override
	protected void onDestroy() {
		if(peripheral != null) {
			peripheral.deleteObserver(this);
		}
		
		if(centralManager.isConnected()) {
			centralManager.disconnectWithDelay();
		}
		
		super.onDestroy();
	}
	
	private void showDialogFragment(int dialogId) {
		Log.d(LOG_TAG, "showDialogFragment() : id : " + dialogId);
		
		switch(dialogId) {
			case DIALOG_AUTHENTICATION:
			{
				DialogAuthentication dialog = new DialogAuthentication();
				dialog.setOnClickListener(dialogOnClickListener);
				dialog.show(getSupportFragmentManager(), DialogAuthentication.FRAGMENT_TAG);			
				break;
			}
			case DIALOG_SETTING_ADVERTISEMENT_INTERVAL:
			{
				if(peripheral.getCharacteristics() == null)
					return;
				
				DialogSettingAdvertisementInterval dialog = new DialogSettingAdvertisementInterval();
				Bundle args = new Bundle();				
				args.putFloat("interval", peripheral.getCharacteristics().
						getAdvertisementIntervalTime());
				args.putString("beacon_model", peripheral.getModelNumber());
				dialog.setArguments(args);
				dialog.setOnClickListener(dialogOnClickListener);
				dialog.show(getSupportFragmentManager(), DialogSettingAdvertisementInterval.FRAGMENT_TAG);
				break;
			}
			case DIALOG_SETTING_TXPOWER:
			{
				if(peripheral.getCharacteristics() == null)
					return;
				
				DialogSettingTxPower dialog = new DialogSettingTxPower();
				Bundle args = new Bundle();
				args.putInt("txpower_index", peripheral.getCharacteristics().getTxPower());
				args.putString("beacon_model", peripheral.getModelNumber());
				dialog.setArguments(args);
				dialog.setOnClickListener(dialogOnClickListener);
				dialog.show(getSupportFragmentManager(), DialogSettingTxPower.FRAGMENT_TAG);
				break;
			}
			case DIALOG_SETTING_MAJOR:
			{
				DialogSettingMajor dialog = new DialogSettingMajor();
				Bundle args = new Bundle();
				args.putInt("major", peripheral.getMajor());
				dialog.setArguments(args);
				dialog.setOnClickListener(dialogOnClickListener);
				dialog.show(getSupportFragmentManager(), DialogSettingMajor.FRAGMENT_TAG);
				break;
			}
			case DIALOG_SETTING_MINOR:
			{
				DialogSettingMinor dialog = new DialogSettingMinor();
				Bundle args = new Bundle();
				args.putInt("minor", peripheral.getMinor());
				dialog.setArguments(args);
				dialog.setOnClickListener(dialogOnClickListener);
				dialog.show(getSupportFragmentManager(), DialogSettingMinor.FRAGMENT_TAG);
				break;
			}
			case DIALOG_SETTING_UUID:
			{
				DialogSettingUUID dialog = new DialogSettingUUID();
				Bundle args = new Bundle();
				args.putString("uuid", peripheral.getProximityUUID());
				dialog.setArguments(args);
				dialog.setOnClickListener(dialogOnClickListener);
				dialog.show(getSupportFragmentManager(), DialogSettingUUID.FRAGMENT_TAG);
				break;
			}
			case DIALOG_SETTING_PASSWORD:
			{
				DialogSettingPassword dialog = new DialogSettingPassword();
				dialog.setOnClickListener(dialogOnClickListener);
				dialog.show(getSupportFragmentManager(), DialogSettingPassword.FRAGMENT_TAG);
				break;
			}
			case DIALOG_SETTING_CURRENT_TIME:
			{
				if(peripheral.getCharacteristics() == null)
					return;
				
				DialogSettingCurrentTime dialog = new DialogSettingCurrentTime();
				Bundle args = new Bundle();				
				args.putParcelable("current_time", peripheral.getCharacteristics().getCurrentTime());				
				dialog.setArguments(args);
				dialog.setOnClickListener(dialogOnClickListener);
				dialog.show(getSupportFragmentManager(), DialogSettingCurrentTime.TAG);
				break;
			}
			case DIALOG_SETTING_SLEEP_TIME:
			{
				if(peripheral.getCharacteristics() == null)
					return;
				
				DialogSettingSleepTime dialog = new DialogSettingSleepTime();
				Bundle args = new Bundle();				
				args.putParcelable("sleep_time", peripheral.getCharacteristics().getSleepTime());
				dialog.setArguments(args);
				dialog.setOnClickListener(dialogOnClickListener);
				dialog.show(getSupportFragmentManager(), DialogSettingSleepTime.TAG);
				break;
				
			}
			case DIALOG_SETTING_BDNAME:
			{
				if(peripheral.getCharacteristics() == null)
					return;
				
				DialogSettingBdname dialog = new DialogSettingBdname();
				Bundle args = new Bundle();				
				args.putString("bdname", peripheral.getBDName());
				dialog.setArguments(args);
				dialog.setOnClickListener(dialogOnClickListener);
				dialog.show(getSupportFragmentManager(), DialogSettingBdname.TAG);
				break;
			}
			case DIALOG_LED_MODE:
			{
				if(peripheral.getCharacteristics() == null)
					return;
				
				DialogLEDMode dialog = new DialogLEDMode();
				Bundle args = new Bundle();
				args.putInt("led_mode", peripheral.getCharacteristics().getLedMode());
				dialog.setArguments(args);
				dialog.setOnClickListener(dialogOnClickListener);
				dialog.show(getSupportFragmentManager(), DialogLEDMode.TAG);
				break;
			}
		}		
	}
	
	private void dismissDialogFragment(int dialogId) {
		switch(dialogId) {
			case DIALOG_AUTHENTICATION:
			{
				DialogFragment fragment = (DialogFragment)getSupportFragmentManager().
				findFragmentByTag(DialogAuthentication.FRAGMENT_TAG);
				fragment.dismiss();	
				break;
			}
			case DIALOG_SETTING_ADVERTISEMENT_INTERVAL:
			{
				break;
			}
		}
	}			
	
	private void startConnectingAnimation() {
		if(connectingAniTimer != null) {
			connectingAniTimer.cancel();
			connectingAniTimer = null;
		}
		
		connectingAniTimer = new Timer();
		connectingAniTimer.scheduleAtFixedRate(new TimerTask() {
			private int count = 0;
			
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						if(connectMenuItem == null)
							return;
						
						String periods = "";
						
						for(int i = 1; i <= count; i++) {
							periods += "."; 
						}						
						
						connectMenuItem.setTitle(getString(R.string.actionbar_connecting) + periods);
						
						if(count >= 3)
							count = 0;
						else
							count++;
					}
				});
			}
		}, 0, 300);
	}
	
	public void stopConnectingAnimation() {
		connectingAniTimer.cancel();
	}
	
	private com.wizturn.manager.dialog.OnClickListener dialogOnClickListener = 
		new com.wizturn.manager.dialog.OnClickListener() {
			
			@Override
			public void onOkayButtonClicked(DialogFragment fragment, Object data) {
				Log.d(LOG_TAG, "onOkayButtonClicked()1 : clicked dialog : " + fragment.getTag());
				
				if(!centralManager.isConnected()) {
					toast.setText(R.string.error_is_not_connected);
					return;
				}
				
				final String tag = fragment.getTag();
				final BluetoothGattWriter writer = centralManager.getBluetoothGattWriter();
				// you don't need to register an instance of OnWriteListener everytime like following.
				// only one time is enough.
				writer.setPeripheralAccessListener(accessListener);
				
				Log.d(LOG_TAG, "onOkayButtonClicked()2 : clicked dialog : " + tag);
				
				if(tag.equals(DialogAuthentication.FRAGMENT_TAG)) {
					// all right, let's authenticate via CentralManager					
					writer.authenticateWithDelay((String)data);
				} else if(tag.equals(DialogSettingMajor.FRAGMENT_TAG)) {
					writer.changeMajorWithDelay(Integer.parseInt((String)data));
				} else if(tag.equals(DialogSettingMinor.FRAGMENT_TAG)) {
					writer.changeMinorWithDelay(Integer.parseInt((String)data));
				} else if(tag.equals(DialogSettingUUID.FRAGMENT_TAG)) {
					writer.changeProximityUUIDWithDelay((String)data);
				} else if(tag.equals(DialogSettingTxPower.FRAGMENT_TAG)) {					
					writer.changeTxPowerWithDelay((Integer)data);
				} else if(tag.equals(DialogSettingAdvertisementInterval.FRAGMENT_TAG)) {
					writer.changeAdvertisementTimeIntervalWithDelay((Float)data);
				} else if(tag.equals(DialogSettingPassword.FRAGMENT_TAG)) {
					writer.changePasswordWithDelay((String)data);
				} else if(tag.equals(DialogSettingCurrentTime.TAG)) {
					writer.changeCurrentTimeWithDelay((Time)data);
				} else if(tag.equals(DialogSettingSleepTime.TAG)) {
					writer.changeSleepTimeWithDelay((SleepTime)data);
				} else if(tag.equals(DialogSettingBdname.TAG)) {					
					writer.changeBdNameWithDelay((String)data);
				} else if(tag.equals(DialogLEDMode.TAG)) {
					writer.changeLEDModeWithDelay((Integer)data);
				}
			}
			
			@Override
			public void onCancelButtonClicked(DialogFragment fragment, Object data) {				
				final String tag = fragment.getTag();
				final BluetoothGattWriter writer = centralManager.getBluetoothGattWriter();
				
				if(tag.equals(DialogAuthentication.FRAGMENT_TAG)) {					
				}
			}
	};
	
	private OnConnectListener onConnectListener = new OnConnectListener() {			
		@Override
		public void onDisconnected(final Peripheral peripheral) {
			runOnUiThread(new Runnable() {
				public void run() {
					if(centralManager != null)
						centralManager.disconnectWithDelay();
					
					toast.setText(R.string.error_disconneted);
					toast.show();						
					try {
						stopConnectingAnimation();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								connectMenuItem.setTitle(R.string.actionbar_disconnected);
							}
						}, 100);
					} catch(Exception e) {
						Log.e(LOG_TAG, "onDisconnected() : exception : " + e.getMessage());
					}
				}
			});
		}
		
		@Override
		public void onError(final Peripheral peripheral) {
			runOnUiThread(new Runnable() {
				public void run() {
					toast.setText(R.string.error_connection_wrong);
					toast.show();
					
					try {
						stopConnectingAnimation();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								connectMenuItem.setTitle(R.string.actionbar_disconnected);
							}
						}, 100);					
					} catch(Exception e) {
						Log.e(LOG_TAG, "onError() : exception : " + e.getMessage());
					}
				}
			});
				
		}
		
		@Override
		public void onConnected(final Peripheral peripheral,
				final boolean needAuthentication) {
			runOnUiThread(new Runnable() {
				public void run() {
					toast.setText(R.string.toast_connected);
					toast.show();

					try {
						stopConnectingAnimation();
						connectMenuItem.setTitle(R.string.actionbar_connected);
						
						setGroup2();
						setGroup3();
						if(needAuthentication) {
							showDialogFragment(DIALOG_AUTHENTICATION);
						}																			
					} catch(Exception e) {
						Log.e(LOG_TAG, "onConnected() : exception : " + e.getMessage());
					}
				}
			});				
		}
	};
			
	private PeripheralAccessListener accessListener = new PeripheralAccessListener() {		
		@Override
		public void onChangingCompleted(final Peripheral peripheral, 
				final PeripheralChangeEvent event) {
			Log.d(LOG_TAG, "accessListener.onChangingCompleted() : peripheral : " + peripheral + 
					", writeEvent : " + event);
			final int changeEvent = event.getEvent();
			switch(changeEvent) {
				case PeripheralChangeEvent.CHANGE_ADVERTISEMENT_INTERVAL:
					break;
				case PeripheralChangeEvent.CHANGE_MAJOR:
					break;
				case PeripheralChangeEvent.CHANGE_MINOR:
					break;
				case PeripheralChangeEvent.CHANGE_PROXIMITY_UUID:
					break;
				case PeripheralChangeEvent.CHANGE_TX_POWER:
					if(DeviceInfoHelper.isNordicModel(peripheral.getModelNumber())) {					
						centralManager.getBluetoothGattWriter().readMeasuredPower();
					}
					break;
				case PeripheralChangeEvent.CHANGE_PASSWORD:
					break;
				case PeripheralChangeEvent.CHANGE_CURRENT_TIME:					
					break;
				case PeripheralChangeEvent.CHANGE_SLEEP_TIME:
					break;
				case PeripheralChangeEvent.CHANGE_BDNAME:
					if(centralManager.isScanning()) {
						centralManager.stopScanning();
						centralManager.startScanning();
					}
					break;
				case PeripheralChangeEvent.CHANGE_LED_MODE:
					break;
			}
			
			// updates ui
			runOnUiThread(new Runnable() {
				public void run() {
					setGroup1();
					setGroup2();
					setGroup3();
				}
			});
		}
		
		@Override
		public void onChangingFailed(final Peripheral peripheral, 
				final PeripheralChangeEvent event) {
			Log.d(LOG_TAG, "accessListener.onChangingFailed() : peripheral : " + peripheral + 
					", writeEvent : " + event);
			final int changeEvent = event.getEvent();
			switch(changeEvent) {
				case PeripheralChangeEvent.CHANGE_ADVERTISEMENT_INTERVAL:
					break;
				case PeripheralChangeEvent.CHANGE_MAJOR:
					break;
				case PeripheralChangeEvent.CHANGE_MINOR:
					break;
				case PeripheralChangeEvent.CHANGE_PROXIMITY_UUID:
					break;
				case PeripheralChangeEvent.CHANGE_TX_POWER:
					break;
				case PeripheralChangeEvent.CHANGE_PASSWORD:
					break;
				case PeripheralChangeEvent.CHANGE_CURRENT_TIME:					
					break;
				case PeripheralChangeEvent.CHANGE_SLEEP_TIME:
					break;
				case PeripheralChangeEvent.CHANGE_BDNAME:
					break;
				case PeripheralChangeEvent.CHANGE_LED_MODE:
					break;
			}
		}
		
		@Override
		public void onPasswordChangingCompleted(final Peripheral peripheral) {
			Log.d(LOG_TAG, "accessListener.onPasswordChangingCompleted() : peripheral : " + 
					peripheral);
			runOnUiThread(new Runnable() {
				public void run() {
					toast.setText(R.string.toast_password_change_success);
					toast.show();
				}
			});	
		}
		
		@Override
		public void onPasswordChangingFailed(final Peripheral peripheral) {
			Log.d(LOG_TAG, "accessListener.onPasswordChangingFailed() : peripheral : " + 
					peripheral);
			runOnUiThread(new Runnable() {
				public void run() {
					toast.setText(R.string.toast_password_change_failure);
					toast.show();
				}
			});	
		}
		
		@Override
		public void onAuthenticatingCompleted(final Peripheral peripheral) {
			Log.d(LOG_TAG, "accessListener.onAuthenticatingCompleted() : peripheral : " + 
					peripheral);
			runOnUiThread(new Runnable() {
				public void run() {
					toast.setText(R.string.toast_authorization_success);
					toast.show();
				}
			});
		}
		
		@Override
		public void onAuthenticatingFailed(final Peripheral peripheral) {
			Log.d(LOG_TAG, "accessListener.onAuthenticatingFailed() : peripheral : " + 
					peripheral);
			runOnUiThread(new Runnable() {
				public void run() {
					toast.setText(R.string.toast_authorization_failure);
					toast.show();
				}
			});			
		}

		@Override
		public void onReadingCompleted(Peripheral peripheral,
				PeripheralEvent event) {
			Log.d(LOG_TAG, "accessListener.onReadingCompleted() : peripheral : " + 
					peripheral);
			
			String message = null;
			final int changeEvent = event.getEvent();			
			switch(changeEvent) {
				case PeripheralEvent.READ_CURRENT_TIME:
					message = "Reading current time";
					break;
				case PeripheralEvent.READ_MEASURED_POWER:
					message = "Reading measured power";
					break;
			}
			
			final String changeMessage = message;
			runOnUiThread(new Runnable() {
				public void run() {
					setGroup1();
					setGroup2();
					setGroup3();
					
					if(changeMessage != null) {
						toast.setText(changeMessage + " succeeded...");
						toast.show();
					}
				}
			});
		}

		@Override
		public void onReadingFailed(Peripheral peripheral, PeripheralEvent event) {
			Log.d(LOG_TAG, "accessListener.onReadingFailed() : peripheral : " + 
					peripheral);
		}
	};

	@Override
	public void onClick(View v) {
		Log.d(LOG_TAG, "onClick() : v : " + v);
		
		if(!centralManager.isConnected()) {
			toast.setText(R.string.error_is_not_connected);
			return;
		}
		
		switch(v.getId()) {
			case R.id.layout_advertisement_time_interval:
				showDialogFragment(DIALOG_SETTING_ADVERTISEMENT_INTERVAL);
				break;
			case R.id.layout_txpower:
				showDialogFragment(DIALOG_SETTING_TXPOWER);
				break;
			case R.id.layout_major:
				showDialogFragment(DIALOG_SETTING_MAJOR);
				break;
			case R.id.layout_minor:
				showDialogFragment(DIALOG_SETTING_MINOR);
				break;
			case R.id.layout_uuid:
				showDialogFragment(DIALOG_SETTING_UUID);
				break;
			case R.id.layout_password:
				showDialogFragment(DIALOG_SETTING_PASSWORD);
				break;
			case R.id.layout_current_time:
				showDialogFragment(DIALOG_SETTING_CURRENT_TIME);
				break;
			case R.id.layout_sleep_time:
				showDialogFragment(DIALOG_SETTING_SLEEP_TIME);
				break;
			case R.id.layout_bdname:
				showDialogFragment(DIALOG_SETTING_BDNAME);
				break;
			case R.id.layout_led_mode:
				showDialogFragment(DIALOG_LED_MODE);
				break;
		}		
	}
}
