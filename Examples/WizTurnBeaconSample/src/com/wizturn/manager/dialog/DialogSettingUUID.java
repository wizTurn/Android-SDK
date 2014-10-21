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

public class DialogSettingUUID extends DialogFragment {
	private final String LOG_TAG = DialogSettingUUID.class.getSimpleName();
	public static final String FRAGMENT_TAG = "dialog_uuid";
	
	private OnClickListener listener;
	private View parent;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				
		LayoutInflater inflater = getActivity().getLayoutInflater();
		parent = inflater.inflate(R.layout.dialog_setting_uuid, null);
		setEditTexts(parent);
		
		builder.setTitle(R.string.uuid)
		.setView(parent)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(listener != null) {
					EditText edittext = null;
					StringBuilder builder = new StringBuilder();
					
					edittext = (EditText)parent.findViewById(R.id.edittext_uuid1);
					builder.append(edittext.getText().toString());
					edittext = (EditText)parent.findViewById(R.id.edittext_uuid2);
					builder.append(edittext.getText().toString());
					edittext = (EditText)parent.findViewById(R.id.edittext_uuid3);
					builder.append(edittext.getText().toString());
					edittext = (EditText)parent.findViewById(R.id.edittext_uuid4);
					builder.append(edittext.getText().toString());
					edittext = (EditText)parent.findViewById(R.id.edittext_uuid5);
					builder.append(edittext.getText().toString());
										
					listener.onOkayButtonClicked(DialogSettingUUID.this, 
							builder.toString().toLowerCase());
				}
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(listener != null) {
					String uuid = null;
					listener.onCancelButtonClicked(DialogSettingUUID.this, uuid);
				}
			}
		});
		
		return builder.create();
	}
	
	private void setEditTexts(View parent) {
		EditText edittext = null;
		
		String uuid = getArguments().getString("uuid");
		
		edittext = (EditText)parent.findViewById(R.id.edittext_uuid1);
		edittext.setText(uuid.substring(0, 8));
		edittext = (EditText)parent.findViewById(R.id.edittext_uuid2);
		edittext.setText(uuid.substring(9, 13));
		edittext = (EditText)parent.findViewById(R.id.edittext_uuid3);
		edittext.setText(uuid.substring(14, 18));
		edittext = (EditText)parent.findViewById(R.id.edittext_uuid4);
		edittext.setText(uuid.substring(19, 23));
		edittext = (EditText)parent.findViewById(R.id.edittext_uuid5);
		edittext.setText(uuid.substring(24));
	}

	public void setOnClickListener(OnClickListener listener) {
		this.listener = listener;
	}	
}
