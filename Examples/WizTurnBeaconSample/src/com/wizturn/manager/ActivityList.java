/**
 * jayjay
 * 오후 5:16:04 2014. 8. 18.
 */
package com.wizturn.manager;

import java.util.ArrayList;

import com.wizturn.sample.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * @author jayjay
 *
 */
public class ActivityList extends Activity {
	private final String LOG_TAG = ActivityList.class.getSimpleName();
	private String[] stringArray = new String[] {
			"Scan/Normal connect&change", "Scan/Delayed connect&change",
			"Scan/Changing multiple properties by a single method call",
			"Control Nordic based beacons(LB2030C, LB3000, LB2050)"
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		setListView();
	}
	
	private void setListView() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
		
		final ListView listview = (ListView)findViewById(R.id.listview);		
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = null;
				
				switch(position) {
					case 0:
						intent = new Intent(ActivityList.this, ActivityMain.class);
						intent.putExtra("mode", "normal");
						break;
					case 1:
						intent = new Intent(ActivityList.this, ActivityMain.class);
						intent.putExtra("mode", "delayed");
						break;
					case 2:
						intent = new Intent(ActivityList.this, ActivityMain.class);
						intent.putExtra("mode", "group");
						break;
					case 3:
						intent = new Intent(ActivityList.this, ActivityMain.class);
						intent.putExtra("mode", "nordic_chip_based");
						break;
				}
				
				startActivity(intent);
			}			
		});
	}
}
