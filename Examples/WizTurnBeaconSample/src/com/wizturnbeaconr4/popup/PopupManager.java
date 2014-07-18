package com.wizturnbeaconr4.popup;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wizturnbeaconr4.R;
import com.wizturnbeaconr4.widget.Slide;

public class PopupManager implements OnClickListener {
	
	private volatile static PopupManager mInstance;
	private Dialog mCurrentPopup;
    private Timer mTimer;
    private boolean mTimerRun;
    private TimerTask mNoticeClose;
	private OnClickListener mOnClickListener;
	
    private PopupManager(){}
    
    public static PopupManager getInstance() {
        if ( mInstance == null ) {
            synchronized ( PopupManager.class ) {
                if ( mInstance == null ) {
                    mInstance = new PopupManager();
                }
            }
        }
        return mInstance;
    }
    
    public boolean isShowing() {
        if ( mCurrentPopup == null )
            return false;
        else
            return mCurrentPopup.isShowing();
    }

    public Dialog getCurrentShowingPopup() {
        if ( isShowing() )
            return mCurrentPopup;
        return null;
    }

    public void dismiss() {
        if ( getCurrentShowingPopup() != null ) {
            if ( getCurrentShowingPopup() != null )
            {
                try
                {
                    if ( mTimer != null )
                    {
                        mTimer.cancel();
                        mTimer = null;
                    }

                    if ( mNoticeClose != null )
                    {
                        mNoticeClose.cancel();
                        mNoticeClose = null;
                    }

                    getCurrentShowingPopup().dismiss();
                }
                catch ( IllegalArgumentException e )
                {
                    // TODO: handle exception
                }
            }
        }
    }
    
    public void showAuthenticate(final Context context, final IPopupAuthenticate listener){
    	if( listener == null ) throw new Error("You must specify IPopupAuthenticate");
    	else {
    		LayoutInflater inflater = LayoutInflater.from(context);
        	final View view = inflater.inflate(R.layout.popup_authenticate, null);
        	
        	((ImageButton) view.findViewById(R.id.exitBtn)).setOnClickListener(this);
        	
        	((TextView) view.findViewById(R.id.popupTitleTxt)).setText("Enter password");
        	final EditText bodyTxt = (EditText) view.findViewById(R.id.popupBodyTxt);
        	
        	dismiss();
        	
            mCurrentPopup = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
            mCurrentPopup.setContentView(view);
            mCurrentPopup.setCancelable(false);
            
            ((Button) view.findViewById(R.id.popupConfirmBtn)).setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				if(bodyTxt.getText().length() != 6) {
    					showAlert(context, "You must enter 6 characters", new OnClickListener() {
							@Override
							public void onClick(View v) {
								showAuthenticate(context, listener);
							}
						});
    				} else {
    					listener.onDismiss(mCurrentPopup, bodyTxt.getText().toString());
    				}
    			}
    		});
        	((Button) view.findViewById(R.id.popupCancelBtn)).setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				listener.onCancel(mCurrentPopup);
    			}
    		});
            
            mCurrentPopup.show();
            bodyTxt.setFocusableInTouchMode(true);
    	}
    }
    
    public void showAlert(Context context, int resId){
    	String msg = context.getResources().getString(resId);
    	showAlert(context, msg, null);
    }
    
    public void showAlert(Context context, String msg){
    	showAlert(context, msg, null);
    }
    
    public void showAlert(Context context, String msg, final OnClickListener clickListener){
    	LayoutInflater inflater = LayoutInflater.from(context);
    	final View view = inflater.inflate(R.layout.popup_notify, null);
    	
    	((ImageButton) view.findViewById(R.id.exitBtn)).setOnClickListener(this);
    	
    	if( clickListener != null )
    		((Button) view.findViewById(R.id.popupBtn)).setOnClickListener(clickListener);
    	else
    		((Button) view.findViewById(R.id.popupBtn)).setOnClickListener(this);
    	
    	TextView bodyTxt = (TextView) view.findViewById(R.id.popupBodyTxt);
    	bodyTxt.setText(msg);
    	
    	dismiss();
    	
        mCurrentPopup = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mCurrentPopup.setContentView(view);
        mCurrentPopup.setCancelable(false);
        mCurrentPopup.show();
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()) { 
		
		case R.id.exitBtn: 
		case R.id.popupBtn: 
			dismiss();
			break;
		}
	}
}
