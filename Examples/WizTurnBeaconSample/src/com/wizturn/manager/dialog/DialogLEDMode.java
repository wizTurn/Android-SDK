/**
 * jayjay
 * 오후 4:55:00 2015. 2. 2.
 */
package com.wizturn.manager.dialog;

import com.wizturn.sample.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * @author jayjay
 *
 */
public class DialogLEDMode extends DialogFragment {
	public static final String TAG = DialogLEDMode.class.getSimpleName();
	final int ID_START_NUMBER = 2333;
	final String[] modes = 
		{"turn off[mode : 0]", "turn on[mode : 1]", "turn on and make blink[mode : 2]"};
	
	protected OnClickListener listener;	
	protected RadioGroup radioGroup;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.dialog_radio_buttons, null);
		radioGroup = (RadioGroup)view.findViewById(R.id.container);
		addRadioButtons(radioGroup);
		builder.setTitle(R.string.led_mode).
			setView(view).
			setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(listener != null) {
						listener.onOkayButtonClicked(DialogLEDMode.this, 
								radioGroup.getCheckedRadioButtonId() - ID_START_NUMBER);
					}
				}
			}).
			setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(listener != null) {
						listener.onCancelButtonClicked(DialogLEDMode.this, 
								radioGroup.getCheckedRadioButtonId() - ID_START_NUMBER);
					}
				}
			});
		
		return builder.create();
	}
	
	public void setOnClickListener(OnClickListener listener) {
		this.listener = listener;
	}
	
	protected void addRadioButtons(RadioGroup parent) {
		final int count = modes.length;
		
		for(int i = 0; i < count; i++) {
			RadioButton button = new RadioButton(getActivity());
			button.setId(ID_START_NUMBER + i);
			button.setText(modes[i]);
			parent.addView(button);
		}
		
		int mode = getArguments().getInt("led_mode");
		parent.check(ID_START_NUMBER + mode);
	}
}
