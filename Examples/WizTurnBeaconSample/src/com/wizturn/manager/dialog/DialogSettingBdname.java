/**
 * jayjay
 * 오후 1:41:02 2014. 12. 17.
 */
package com.wizturn.manager.dialog;

import com.wizturn.sample.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * @author jayjay
 *
 */
public class DialogSettingBdname extends DialogFragment {
	public static final String TAG = DialogSettingBdname.class.getSimpleName();
	
	private OnClickListener listener;
	private EditText edittext;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_one_edittext, null);
		edittext = (EditText)view.findViewById(R.id.edittext);
		
		edittext.setText(getArguments().getString("bdname"));
		edittext.setInputType(InputType.TYPE_CLASS_TEXT);
		
		builder.setTitle(R.string.bd_name)
		.setView(view)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(listener != null) {
					listener.onOkayButtonClicked(
							DialogSettingBdname.this, edittext.getText().toString());
				}
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(listener != null) {
					listener.onCancelButtonClicked(
							DialogSettingBdname.this, edittext.getText().toString());
				}
			}
		});
		
		return builder.create();
	}
	
	public void setOnClickListener(OnClickListener listener) {
		this.listener = listener;
	}	
}
