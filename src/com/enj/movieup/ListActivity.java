package com.enj.movieup;

import com.enj.common.ENJApplication;
import com.enj.fragment.FileFragment;
import com.enj.fragment.FolderFragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ListActivity extends Activity implements OnClickListener {

	public TextView mTitle;

	Fragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		mTitle = (TextView) findViewById(R.id.list_title);
		mTitle.setText("Download");

		// if (mFrameLayout != null) {
		// FragmentTransaction transaction = getFragmentManager()
		// .beginTransaction();
		// mTitle.setText("Download");
		// mFragment = new FolderFragment();
		// mFragment.setArguments(getIntent().getExtras());
		// transaction.add(R.id.fragment_container, mFragment, mFragment
		// .getClass().toString());
		// transaction.commit();
		// }

		if (savedInstanceState == null) {

			setDefaultFragment();
		}
	}

	private void setDefaultFragment() {

		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		mFragment = new FolderFragment();
		transaction.replace(R.id.fragment_container, mFragment);
		transaction.commit();
	}

	public void changeToFileFragment(String folder) {

		Bundle bundle = new Bundle();
		bundle.putString("folder", folder);

		mFragment = new FileFragment();
		mFragment.setArguments(bundle);
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.fragment_container, mFragment, mFragment
				.getClass().toString());
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.list_back:

			getFragmentManager().getBackStackEntryCount();
			getFragmentManager().popBackStack();

			break;
		case R.id.list_close:

			finish();
			break;

		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		ENJApplication.pushActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ENJApplication.removeActivity(this);
	}

}
