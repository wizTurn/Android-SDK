package com.wizturn.manager.dialog;

import com.wizturn.sample.R;
import com.wizturn.sdk.utils.DeviceInfoHelper;
import com.wizturn.sdk.utils.TimeIntervalConverter;
import com.wizturn.sdk.utils.TimeIntervalConverterForNordicBased;
import com.wizturn.sdk.utils.TxPowerConverter;
import com.wizturn.sdk.utils.TxPowerConverterForNordicBased;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

public class DialogSettingAdvertisementInterval extends DialogFragment {
	private final String LOG_TAG = DialogSettingAdvertisementInterval.class.getSimpleName();
	public static final String FRAGMENT_TAG = "dialog_advint";
	private boolean isNordicBased = false;
	
	private OnClickListener listener;
	private NumberPicker numberPicker;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String[] values = null;
		if(DeviceInfoHelper.isNordicModel(getArguments().getString("beacon_model"))) {
			values = getResources().getStringArray(R.array.advertisement_time_interval_for_nordic_based);
			isNordicBased = true;
		} else {
			values = getResources().getStringArray(R.array.advertisement_time_interval);
			isNordicBased = false;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_one_numberpicker, null);
		setNumberPicker(view, values);				
		
		builder.setTitle(R.string.interval)
		.setView(view)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(listener != null) {
					listener.onOkayButtonClicked(
							DialogSettingAdvertisementInterval.this, 
							getIntervalFromIndexValue(numberPicker.getValue()));
				}
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(listener != null) {
					listener.onCancelButtonClicked(
							DialogSettingAdvertisementInterval.this, 
							getIntervalFromIndexValue(numberPicker.getValue()));
				}
			}
		});
		
		return builder.create();
	}
	
	private void setNumberPicker(View parent, String[] values) {
		numberPicker = (NumberPicker)parent.findViewById(R.id.number_picker);
		numberPicker.setMinValue(0);
		numberPicker.setMaxValue(values.length - 1);
		numberPicker.setDisplayedValues(values);
		
		float interval = getArguments().getFloat("interval");
		int value = -1;
		Log.d(LOG_TAG, "setNumberPicker() : interval : " + interval);
		
		if(isNordicBased) {
			for(int i = 0; i < TimeIntervalConverterForNordicBased.INTERVAL_ARRAY.length; i++) {
				if(TimeIntervalConverterForNordicBased.INTERVAL_ARRAY[i] == interval) {
					value = i;
					break;
				}
			}
		} else {
			for(int i = 0; i < TimeIntervalConverter.INTERVAL_ARRAY.length; i++) {
				if(TimeIntervalConverter.INTERVAL_ARRAY[i] == interval) {
					value = i;
					break;
				}
			}
		}
		
		numberPicker.setValue(value);
	}
	
	private float getIntervalFromIndexValue(int indexValue) {
		try {
			if(isNordicBased) {
				return TimeIntervalConverterForNordicBased.INTERVAL_ARRAY[indexValue];
			} else {
				return TimeIntervalConverter.INTERVAL_ARRAY[indexValue];
			}
		} catch(Exception e) {
			return -1;
		}
	}
	
	public void setOnClickListener(OnClickListener listener) {
		this.listener = listener;
	}	
}
