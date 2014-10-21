package com.wizturn.manager.dialog;

import com.wizturn.sample.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

public class DialogSettingAdvertisementInterval extends DialogFragment {
	private final String LOG_TAG = DialogSettingAdvertisementInterval.class.getSimpleName();
	public static final String FRAGMENT_TAG = "dialog_advint";
	
	private OnClickListener listener;
	private NumberPicker numberPicker;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		String[] values = getResources().getStringArray(R.array.advertisement_time_interval);
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
		
		if(interval == 0.2f) {
			value = 0;
		} else if(interval == 0.3f) {
			value = 1;
		} else if(interval == 0.5f) {
			value = 2;
		} else if(interval == 1.0f) {
			value = 3;
		} else if(interval == 2.0f) {
			value = 4;
		} else if(interval == 5.0f) {
			value = 5;
		} else if(interval == 10.0f) {
			value = 6;
		} else if(interval == 15.0f) {
			value = 7;
		} else if(interval == 20.0f) {
			value = 8;
		}
		
		numberPicker.setValue(value);
	}
	
	private float getIntervalFromIndexValue(int indexValue) {
		switch(indexValue) {
			case 0:
				return 0.2f;
			case 1:
				return 0.3f;
			case 2:
				return 0.5f;
			case 3:
				return 1.0f;
			case 4:
				return 2.0f;
			case 5:
				return 5.0f;
			case 6:
				return 10.0f;
			case 7:
				return 15.0f;
			case 8:
				return 20.0f;
			default:
				return -1;
		}
	}
	
	public void setOnClickListener(OnClickListener listener) {
		this.listener = listener;
	}	
}
