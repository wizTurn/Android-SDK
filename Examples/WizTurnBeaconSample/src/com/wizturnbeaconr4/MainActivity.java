package com.wizturnbeaconr4;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener{

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.main_start).setOnClickListener(this);
		
		PackageInfo pi = null;

		try {

		pi = getPackageManager().getPackageInfo(getPackageName(), 0);

		} catch (NameNotFoundException e) {

		// TODO Auto-generated catch block

		e.printStackTrace();

		}

		String verSion = pi.versionName;
		
		((TextView)findViewById(R.id.versionTxt)).setText("Ver " + verSion);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(MainActivity.this , WizTurnBeaconList.class);
		startActivity(intent);
	}
}


