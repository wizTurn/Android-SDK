package com.wizturn.manager.dialog;

import com.wizturn.sample.R;
import com.wizturn.sdk.time.SleepTime;
import com.wizturn.sdk.time.SleepTimeImpl;
import com.wizturn.sdk.time.Time;
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
import android.widget.ToggleButton;

public class DialogSettingSleepTime extends DialogFragment {
	public static final String TAG = DialogSettingSleepTime.class.getSimpleName();
	private boolean isNordicBased = false;
	
	private OnClickListener listener;
	private ToggleButton toggleButton;
	private TimePicker timePickerStartTime;
	private TimePicker timePickerEndTime;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		SleepTime sleepTime = getArguments().getParcelable("sleep_time");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_setting_sleeptime, null);
		toggleButton = (ToggleButton)view.findViewById(R.id.toggle_button);
		timePickerStartTime = (TimePicker)view.findViewById(R.id.timer_picker_starttime);
		timePickerEndTime = (TimePicker)view.findViewById(R.id.timer_picker_endtime);
		
		toggleButton.setChecked(sleepTime.isEnabled());
		timePickerStartTime.setCurrentHour(sleepTime.getStartTimeHour());
		timePickerStartTime.setCurrentMinute(sleepTime.getStartTimeMinute());
		timePickerEndTime.setCurrentHour(sleepTime.getEndTimeHour());
		timePickerEndTime.setCurrentMinute(sleepTime.getEndTimeMinute());
	
		builder.setTitle(R.string.sleep_time)
		.setView(view)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(listener != null) {
					SleepTimeImpl sleepTime = new SleepTimeImpl();
					sleepTime.setIsEnabled(toggleButton.isChecked());
					sleepTime.setStartTimeHour(timePickerStartTime.getCurrentHour());
					sleepTime.setStartTimeMinute(timePickerStartTime.getCurrentMinute());
					sleepTime.setEndTimeHour(timePickerEndTime.getCurrentHour());
					sleepTime.setEndTimeMinute(timePickerEndTime.getCurrentMinute());
					Log.d(TAG, "sleeptime : " + sleepTime);
					listener.onOkayButtonClicked(DialogSettingSleepTime.this, sleepTime);
				}
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(listener != null) {
					SleepTimeImpl sleepTime = new SleepTimeImpl();
					sleepTime.setIsEnabled(toggleButton.isChecked());
					sleepTime.setStartTimeHour(timePickerStartTime.getCurrentHour());
					sleepTime.setStartTimeMinute(timePickerStartTime.getCurrentMinute());
					sleepTime.setEndTimeHour(timePickerEndTime.getCurrentHour());
					sleepTime.setEndTimeMinute(timePickerEndTime.getCurrentMinute());
					Log.d(TAG, "sleeptime : " + sleepTime);
					listener.onCancelButtonClicked(DialogSettingSleepTime.this, sleepTime);
				}
			}
		});
		
		return builder.create();
	}
	
	public void setOnClickListener(OnClickListener listener) {
		this.listener = listener;
	}
}
