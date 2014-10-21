/**
 * jayjay
 * 오후 5:49:35 2014. 8. 11.
 */
package com.wizturn.manager.dialog;

import com.wizturn.sample.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * @author jayjay
 *
 */
public class DialogAuthentication extends DialogFragment {
	private final String LOG_TAG = DialogAuthentication.class.getSimpleName();
	public static final String FRAGMENT_TAG = "dialog_auth";
	
	private OnClickListener listener;
	private EditText edittext;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_authentication, null);
		edittext = (EditText)view.findViewById(R.id.password);
		edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
		edittext.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6)});
		
		builder.setTitle(R.string.authentication)
		.setView(view)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d(LOG_TAG, "PositiveButton.onClick() : listener : " + listener);
				
				if(listener != null) {
					listener.onOkayButtonClicked(
							DialogAuthentication.this, edittext.getText().toString());
				}
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d(LOG_TAG, "NegativeButton.onClick() : listener : " + listener);
				
				if(listener != null) {
					listener.onCancelButtonClicked(
							DialogAuthentication.this, edittext.getText().toString());
				}
			}
		});
		
		return builder.create();
	}
	
	public void setOnClickListener(OnClickListener listener) {
		this.listener = listener;
	}	
}
