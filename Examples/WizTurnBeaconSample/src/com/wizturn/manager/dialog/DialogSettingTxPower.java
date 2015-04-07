package com.wizturn.manager.dialog;

import com.wizturn.sample.R;
import com.wizturn.sdk.utils.DeviceInfoHelper;
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
import android.widget.NumberPicker;

public class DialogSettingTxPower extends DialogFragment {
	private final String LOG_TAG = DialogSettingTxPower.class.getSimpleName();
	public static final String FRAGMENT_TAG = "dialog_txpower";
	private boolean isNordicBased = false;
	
	private OnClickListener listener;
	private NumberPicker numberPicker;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String[] values = null;
		if(DeviceInfoHelper.isNordicModel(getArguments().getString("beacon_model"))) {
			values = getResources().getStringArray(R.array.txpower_for_nordic_based);
			isNordicBased = true;
		} else {
			values = getResources().getStringArray(R.array.txpower);
			isNordicBased = false;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_one_numberpicker, null);		
		setNumberPicker(view, values);		
		
		builder.setTitle(R.string.tx_power)
		.setView(view)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(listener != null) {
					listener.onOkayButtonClicked(
							DialogSettingTxPower.this, 
							getTxPowerFromIndexValue(numberPicker.getValue()));
				}
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(listener != null) {
					listener.onCancelButtonClicked(
							DialogSettingTxPower.this, 
							getTxPowerFromIndexValue(numberPicker.getValue()));
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
				
		int txpowerIndex = getArguments().getInt("txpower_index");
		int value = -1;
		Log.d(LOG_TAG, "setNumberPicker() : txPowerIndex : " + txpowerIndex);
		
		if(isNordicBased) {
			for(int i = 0; i < TxPowerConverterForNordicBased.TXPOWER_ARRAY.length; i++) {
				if(TxPowerConverterForNordicBased.TXPOWER_ARRAY[i] == txpowerIndex) {
					value = i;
					break;
				}
			}
		} else {
			for(int i = 0; i < TxPowerConverter.TXPOWER_ARRAY.length; i++) {
				if(TxPowerConverter.TXPOWER_ARRAY[i] == txpowerIndex) {
					value = i;
					break;
				}
			}
		}
		
		numberPicker.setValue(value);
	}
	
	private int getTxPowerFromIndexValue(int indexValue) {
		try {
			if(isNordicBased) {
				return TxPowerConverterForNordicBased.TXPOWER_ARRAY[indexValue];
			} else {
				return TxPowerConverter.TXPOWER_ARRAY[indexValue];
			}	
		} catch(Exception e) {
			return -1;
		}
	}
	
	public void setOnClickListener(OnClickListener listener) {
		this.listener = listener;
	}	
}
