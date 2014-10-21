package com.wizturn.manager.dialog;

import com.wizturn.sample.R;
import com.wizturn.sdk.utils.TxPowerConverter;

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
	
	private OnClickListener listener;
	private NumberPicker numberPicker;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		String[] values = getResources().getStringArray(R.array.txpower);
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
		switch(txpowerIndex) {
			case -23:
				value = 0;
				break;
			case -19:
				value = 1;
				break;
			case -16:
				value = 2;
				break;
			case -12:
				value = 3;
				break;
			case -9:
				value = 4;
				break;
			case -5:
				value = 5;
				break;
			case 0:
				value = 6;
				break;
			case 4:
				value = 7;
				break;
		}
		
		numberPicker.setValue(value);
	}
	
	private int getTxPowerFromIndexValue(int indexValue) {
		switch(indexValue) {
			case 0:	// -23dBm
				return -23;				
			case 1:	// -19dBm
				return -19;
			case 2:	// -16dBm
				return -16;			
			case 3:	// -12dBm
				return -12;				
			case 4:	// -9dBm
				return -9;				
			case 5:	// -5dBm
				return -5;				
			case 6:	// 0dBm
				return 0;				
			case 7:	// 4dBm
				return 4;				
			default:
				return -1;
		}
	}
	
	public void setOnClickListener(OnClickListener listener) {
		this.listener = listener;
	}	
}
