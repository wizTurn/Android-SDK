package com.wizturn.manager.dialog;

import com.wizturn.sample.R;
import com.wizturn.sdk.time.Time;
import com.wizturn.sdk.time.TimeImpl;
import com.wizturn.sdk.utils.DeviceInfoHelper;
import com.wizturn.sdk.utils.TimeIntervalConverter;
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
import android.widget.TimePicker;

public class DialogSettingCurrentTime extends DialogFragment {
	public static final String TAG = DialogSettingCurrentTime.class.getSimpleName();

	private OnClickListener listener;
	private TimePicker timePicker;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Time time = getArguments().getParcelable("current_time");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_one_timepicker, null);
		timePicker = (TimePicker)view.findViewById(R.id.timer_picker);
		timePicker.setCurrentHour(time.getHour());
		timePicker.setCurrentMinute(time.getMinute());

		builder.setTitle(R.string.current_time)
		.setView(view)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(listener != null) {
					int hour = timePicker.getCurrentHour();
					int minute = timePicker.getCurrentMinute();
					listener.onOkayButtonClicked(DialogSettingCurrentTime.this, new TimeImpl(hour, minute));
				}
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(listener != null) {
					int hour = timePicker.getCurrentHour();
					int minute = timePicker.getCurrentMinute();
					listener.onCancelButtonClicked(DialogSettingCurrentTime.this, new TimeImpl(hour, minute));
				}
			}
		});
		
		return builder.create();
	}
	
	public void setOnClickListener(OnClickListener listener) {
		this.listener = listener;
	}
}
