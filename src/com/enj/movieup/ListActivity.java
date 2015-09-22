package com.enj.movieup;

import com.enj.common.ENJApplication;
import com.enj.fragment.DetailFragment;
import com.enj.fragment.FileFragment;
import com.enj.fragment.FolderFragment;
import com.enj.utils.ENJUtils;

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

		if (savedInstanceState == null) {

			setDefaultFragment();
		}
	}

	private void setDefaultFragment() {

		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();

		String type = getIntent().getType();
		if (type.equals("D")) {

			mTitle.setText("Download");
			mFragment = new FolderFragment();
		} else if (type.equals("F")) {

			mTitle.setText("Favorites");
			mFragment = new FileFragment();

			Bundle bundle = new Bundle();
			bundle.putString("folder", "Favorites");
			mFragment.setArguments(bundle);

		} else if (type.equals("V")) {

//			mTitle.setText(getIntent().getExtras().getString("boardname"));
			mFragment = new FileFragment();

			Bundle bundle = new Bundle();
			bundle.putString("folder",
					getIntent().getExtras().getString("folder"));
			bundle.putString("type", "V");
			bundle.putString("path", getIntent().getExtras().getString("path"));
			mFragment.setArguments(bundle);
		}

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

	public void changeToDetailFragment(String path) {

		Bundle bundle = new Bundle();
		bundle.putString("path", path);

		mFragment = new DetailFragment();
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

			ENJUtils.alert(getFragmentManager().getBackStackEntryCount() + "");
			if (getFragmentManager().getBackStackEntryCount() == 1) {

				String type = getIntent().getType();
				if (type.equals("D")) {

					mTitle.setText("Download");
				} else {
					mTitle.setText("Favorites");
				}
			} else if (getFragmentManager().getBackStackEntryCount() == 0) {
				finish();
			}

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
