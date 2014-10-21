package com.wizturn.manager.dialog;

import android.support.v4.app.DialogFragment;

public interface OnClickListener {
	public void onOkayButtonClicked(
			DialogFragment fragment, Object data);
	public void onCancelButtonClicked(
			DialogFragment fragment, Object data);
}
