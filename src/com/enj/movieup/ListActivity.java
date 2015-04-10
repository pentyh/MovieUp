package com.enj.movieup;

import com.enj.common.ENJApplication;

import android.app.Activity;
import android.os.Bundle;

public class ListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
	}

	@Override
	protected void onResume() {
		super.onResume();
		ENJApplication.pushActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		ENJApplication.removeActivity(this);

	};
}
