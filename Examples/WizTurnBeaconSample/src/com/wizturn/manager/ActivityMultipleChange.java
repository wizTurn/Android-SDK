/**
 * jayjay
 * 오후 4:08:01 2014. 9. 26.
 */
package com.wizturn.manager;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import com.wizturn.manager.dialog.DialogAuthentication;
import com.wizturn.manager.dialog.DialogSettingAdvertisementInterval;
import com.wizturn.manager.dialog.DialogSettingMajor;
import com.wizturn.manager.dialog.DialogSettingMinor;
import com.wizturn.manager.dialog.DialogSettingPassword;
import com.wizturn.manager.dialog.DialogSettingTxPower;
import com.wizturn.manager.dialog.DialogSettingUUID;
import com.wizturn.sample.R;
import com.wizturn.sdk.central.BluetoothGattWriter;
import com.wizturn.sdk.central.CentralManager;
import com.wizturn.sdk.peripheral.MultiplePropertyChangeBuilder;
import com.wizturn.sdk.peripheral.OnChangeMultiplePropertyListener;
import com.wizturn.sdk.peripheral.OnConnectListener;
import com.wizturn.sdk.peripheral.Peripheral;
import com.wizturn.sdk.peripheral.PeripheralAccessListener;
import com.wizturn.sdk.peripheral.PeripheralChangeEvent;
import com.wizturn.sdk.peripheral.PeripheralEvent;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author jayjay
 *
 */
public class ActivityMultipleChange extends FragmentActivity implements 
	Observer {
	private final String TAG = ActivityMultipleChange.class.getSimpleName();
	private final int DIALOG_AUTHENTICATION = 0;
	private final int DIALOG_SETTING_ADVERTISEMENT_INTERVAL = 1;
	private final int DIALOG_SETTING_TXPOWER = 2;
	private final int DIALOG_SETTING_MAJOR = 3;
	private final int DIALOG_SETTING_MINOR = 4;
	private final int DIALOG_SETTING_UUID = 5;
	private final int DIALOG_SETTING_PASSWORD = 6;
	
	private int spinnerPosition = 0;
	private Peripheral peripheral;
	private MultiplePropertyChangeBuilder mpcBuilder;
	private Timer connectingAniTimer;
	
	Handler handler = new Handler();
	
	// Views
	private Toast toast;
	private Spinner spinner;
	private TextView textChangeList;
	private TextView textPeripheralInfo;
	private MenuItem connectMenuItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiple_change);
		
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
		
		getDataFromIntent();
		setViews();
		connect();
		startConnectingAnimation();
	}
	
	private void connect() {
		CentralManager.getInstance().connectWithDelay(peripheral, onConnectListener);
	}
	
	private void getDataFromIntent() {
		peripheral = getIntent().getParcelableExtra("peripheral");
		peripheral = CentralManager.getInstance().getPeripheral(peripheral.getBDAddress());
		peripheral.addObserver(this);
	}
	
	private void setViews() {
		textChangeList = (TextView)findViewById(R.id.textview_peripheral_change_list);
		textChangeList.setMovementMethod(new ScrollingMovementMethod());
		
		setSpinner();
		setPeripheralInfo(peripheral);
		setChangeButton();
		setCommitButton();
	}
	
	private void setChangeButton() {
		Button button = (Button)findViewById(R.id.button_change);
		button.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// add change action
				switch(spinnerPosition) {
					case 0:	// uuid
						showDialogFragment(DIALOG_SETTING_UUID);
						break;
					case 1:	// major
						showDialogFragment(DIALOG_SETTING_MAJOR);
						break;
					case 2:	// minor
						showDialogFragment(DIALOG_SETTING_MINOR);
						break;
					case 3:	// txPower
						showDialogFragment(DIALOG_SETTING_TXPOWER);
						break;
					case 4:	// advertisement time interval
						showDialogFragment(DIALOG_SETTING_ADVERTISEMENT_INTERVAL);
						break;
					case 5:	// password
						showDialogFragment(DIALOG_SETTING_PASSWORD);
						break;					
				}
			}
		});
	}
	
	private void setCommitButton() {
		Button button = (Button)findViewById(R.id.button_commit);
		button.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(mpcBuilder != null) {
					mpcBuilder.commit(true);
					mpcBuilder = null;
				}
			}
		});
	}
	
	private void setPeripheralInfo(Peripheral peripheral) {
		Log.d(TAG, "setPeripheralInfo() : peripheral : " + peripheral);
		StringBuilder builder = new StringBuilder();
		builder.append("UUID : " + peripheral.getProximityUUID());
		builder.append("\nBD Namae : " + peripheral.getBDName());
		builder.append("\nBD Address : " + peripheral.getBDAddress());		
		builder.append("\nMajor : " + peripheral.getMajor());
		builder.append("\nMinor : " + peripheral.getMinor());
		builder.append("\nRSSI : " + peripheral.getRssi() + "dBm");
		builder.append("\nMeasured power : " + peripheral.getMeasuredPower() + "dBm");
		builder.append("\nDistance : " + peripheral.getDistance() +"m");
		builder.append("\nProximity : " + peripheral.getProximity());
		
		if(peripheral.getCharacteristics() != null) {
			builder.append("\nTxPower : " + peripheral.getCharacteristics().getTxPower() + "dBm");
			builder.append("\nInterval : " + peripheral.getCharacteristics().
					getAdvertisementIntervalTime() + "Hz");
			builder.append("\nBattery Level : " + peripheral.getCharacteristics().
					getBatteryPercent() + "%");
			builder.append("\nHardware Version : " + peripheral.getCharacteristics().
					getHardwareVersion());
			builder.append("\nSoftware Version : " + peripheral.getCharacteristics().
					getSoftwareVersion());
		}
		
		textPeripheralInfo = (TextView)findViewById(R.id.textview_peripheral_info);
		textPeripheralInfo.setText(builder.toString());
		textPeripheralInfo.setMovementMethod(new ScrollingMovementMethod());		
	}
	
	private void setSpinner() {
		spinner = (Spinner)findViewById(R.id.spinner);
		ArrayAdapter<CharSequence>adapter = ArrayAdapter.createFromResource(this, 
				R.array.properties, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				spinnerPosition = position;
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				spinnerPosition = 0;
			}
		});
	}
	
	@Override
	public void update(Observable observable, Object data) {
		if(observable instanceof Peripheral) {
			final Peripheral peripheral = (Peripheral)observable;
			runOnUiThread(new Runnable() {				
				@Override
				public void run() {
					setPeripheralInfo(peripheral);
				}
			});			
		}
	}
	
	@Override
	protected void onDestroy() {
		if(peripheral != null) {
			peripheral.deleteObserver(this);
		}
		
		if(CentralManager.getInstance().isConnected()) {
			CentralManager.getInstance().disconnectWithDelay();
		}
		
		super.onDestroy();
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
	
	private void showDialogFragment(int dialogId) {
		Log.d(TAG, "showDialogFragment() : id : " + dialogId);
		
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
		}		
	}
	
	private OnConnectListener onConnectListener = new OnConnectListener() {			
		@Override
		public void onDisconnected(final Peripheral peripheral) {
			runOnUiThread(new Runnable() {
				public void run() {					
					CentralManager.getInstance().disconnectWithDelay();
					
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
						Log.e(TAG, "onDisconnected() : exception : " + e.getMessage());
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
						Log.e(TAG, "onError() : exception : " + e.getMessage());
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
										
						if(needAuthentication) {
							showDialogFragment(DIALOG_AUTHENTICATION);
						}																			
					} catch(Exception e) {
						Log.e(TAG, "onConnected() : exception : " + e.getMessage());
					}
				}
			});				
		}
	};
	
	private com.wizturn.manager.dialog.OnClickListener dialogOnClickListener = 
		new com.wizturn.manager.dialog.OnClickListener() {
			
			@Override
			public void onOkayButtonClicked(DialogFragment fragment, Object data) {
				if(!CentralManager.getInstance().isConnected()) {
					toast.setText(R.string.error_is_not_connected);
					return;
				}
				
				final String tag = fragment.getTag();
				final BluetoothGattWriter writer = CentralManager.getInstance().
						getBluetoothGattWriter();
				// you don't need to register an instance of OnWriteListener everytime like following.
				// only one time is enough.
				writer.setPeripheralAccessListener(accessListener);
				if(mpcBuilder == null) {
					mpcBuilder = writer.createMultiplePropertyChangeBuilder(
							onChangeMultiplePropertyListener);
				}
				
				boolean result = true;
				if(tag.equals(DialogAuthentication.FRAGMENT_TAG)) {					
					writer.authenticate((String)data);
				} else if(tag.equals(DialogSettingMajor.FRAGMENT_TAG)) {
					result = mpcBuilder.addChangeAction(MultiplePropertyChangeBuilder.CHANGE_MAJOR, 
							Integer.parseInt((String)data));
				} else if(tag.equals(DialogSettingMinor.FRAGMENT_TAG) ) {
					result = mpcBuilder.addChangeAction(MultiplePropertyChangeBuilder.CHANGE_MINOR, 
							Integer.parseInt((String)data));
				} else if(tag.equals(DialogSettingUUID.FRAGMENT_TAG)) {
					result = mpcBuilder.addChangeAction(MultiplePropertyChangeBuilder.CHANGE_PROXIMITY_UUID, 
							(String)data);
				} else if(tag.equals(DialogSettingTxPower.FRAGMENT_TAG)) {					
					result = mpcBuilder.addChangeAction(MultiplePropertyChangeBuilder.CHANGE_TX_POWER, 
							(Integer)data);
				} else if(tag.equals(DialogSettingAdvertisementInterval.FRAGMENT_TAG)) {
					result = mpcBuilder.addChangeAction(MultiplePropertyChangeBuilder.CHANGE_ADVERTISEMENT_INTERVAL, 
							(Float)data);
				} else if(tag.equals(DialogSettingPassword.FRAGMENT_TAG)) {
					result = mpcBuilder.addChangeAction(MultiplePropertyChangeBuilder.CHANGE_PASSWORD, 
							(String)data);
				}
				
				if(!result) {
					toast.setText(R.string.error_cannot_add_change_action);
					toast.show();
				}
				
				textChangeList.setText(mpcBuilder.snapshot());
			}
			
			@Override
			public void onCancelButtonClicked(DialogFragment fragment, Object data) {				
				final String tag = fragment.getTag();
				final BluetoothGattWriter writer = CentralManager.getInstance().
						getBluetoothGattWriter();
				
				if(tag.equals(DialogAuthentication.FRAGMENT_TAG)) {					
				}
			}
	};
		
	private PeripheralAccessListener accessListener = new PeripheralAccessListener() {		
		@Override
		public void onChangingCompleted(final Peripheral peripheral, 
				final PeripheralChangeEvent event) {
			Log.d(TAG, "accessListener.onChangingCompleted() : peripheral : " + peripheral + 
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
				case PeripheralChangeEvent.CHANGE_UNKNWON:
					break;
			}			
		}
		
		@Override
		public void onChangingFailed(final Peripheral peripheral, 
				final PeripheralChangeEvent event) {
			Log.d(TAG, "accessListener.onChangingFailed() : peripheral : " + peripheral + 
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
				case PeripheralChangeEvent.CHANGE_UNKNWON:
					break;
			}
		}
		
		@Override
		public void onPasswordChangingCompleted(final Peripheral peripheral) {
			Log.d(TAG, "accessListener.onPasswordChangingCompleted() : peripheral : " + 
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
			Log.d(TAG, "accessListener.onPasswordChangingFailed() : peripheral : " + 
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
			Log.d(TAG, "accessListener.onAuthenticatingCompleted() : peripheral : " + 
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
			Log.d(TAG, "accessListener.onAuthenticatingFailed() : peripheral : " + 
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
		}

		@Override
		public void onReadingFailed(Peripheral peripheral, PeripheralEvent event) {
		}
	};
	
	private OnChangeMultiplePropertyListener onChangeMultiplePropertyListener = 
			new OnChangeMultiplePropertyListener() {		
		@Override
		public void onChangeMultiplePropertyFailed(
				MultiplePropertyChangeBuilder builder, int indexFaileAt,
				int rollbackState) {
			Log.d(TAG, "onChangeMultiplePropertyFailed() : indexFailedAt : " + indexFaileAt + 
					", rollbackState : " + rollbackState);
			toast.setText(R.string.toast_multiple_property_failure);
			toast.show();
		}
		
		@Override
		public void onChangeMultiplePropertyCompleted(
				MultiplePropertyChangeBuilder builder) {
			Log.d(TAG, "onChangeMultiplePropertyCompleted() : builder : " + builder);
			toast.setText(R.string.toast_multiple_property_success);
			toast.show();
		}
	};
}
