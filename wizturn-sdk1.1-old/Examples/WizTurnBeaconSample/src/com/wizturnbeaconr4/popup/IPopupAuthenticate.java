package com.wizturnbeaconr4.popup;

import android.content.DialogInterface;

public interface IPopupAuthenticate {
	/**
     * This method will be invoked when the dialog is dismissed.
     * 
     * @param dialog The dialog that was dismissed will be passed into the
     *            method.
     * @param password Password input.
     */
    public void onDismiss(DialogInterface dialog, String password);
    
    /**
     * This method will be invoked when the dialog is canceled.
     * 
     * @param dialog The dialog that was canceled will be passed into the
     *            method.
     */
    public void onCancel(DialogInterface dialog);
}
