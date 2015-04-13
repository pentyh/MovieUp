package com.enj.fragment;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.enj.adapter.FileAdapter;
import com.enj.common.ENJApplication;
import com.enj.common.ENJValues;
import com.enj.movieup.ListActivity;
import com.enj.movieup.MainActivity;
import com.enj.movieup.R;
import com.enj.utils.ENJUtils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FileFragment extends Fragment implements OnClickListener,
		OnItemClickListener {

	private String mFolder;

	private int mIndex = 0;

	ListView mListView;
	FileAdapter mFileAdapter;

	ArrayList<HashMap<String, Object>> listItems;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mFolder = getArguments().getString("folder", "");

		View root = inflater.inflate(R.layout.fragment_file, container, false);

		if (!mFolder.equals("")) {

			root.findViewById(R.id.list_file_play).setOnClickListener(this);

			mListView = (ListView) root.findViewById(R.id.list);
			mFileAdapter = new FileAdapter(ENJApplication.getContext(),
					getData());
			mFileAdapter.setSelectedPosition(mIndex);
			mListView.setAdapter(mFileAdapter);

			mListView.setOnItemClickListener(this);
		}

		return root;
	}

	public ArrayList<HashMap<String, Object>> getData() {

		listItems = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map;

		File root = new File(ENJValues.FILE_PATH + mFolder);
		for (File file : root.listFiles()) {

			if (file.isDirectory())
				continue;

			if (file.getName().lastIndexOf(".txt") != -1) {

				String result = ENJUtils.getString(file);

				try {
					JSONObject object = new JSONObject(result);

					String path = object.getString("filename");

					if (path != null && !path.equals("")) {

						File video = new File(path);

						if (video.isFile()) {

							map = new HashMap<String, Object>();
							map.put("ItemTitle", object.getString("title"));
							map.put("ItemDownload", object.getString("date"));
							map.put("ItemFilename", file.getName());
							map.put("ItemPath", object.getString("filename"));
							listItems.add(map);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

		}

		return listItems;
	}

	FileFilter txtFileFilter = new FileFilter() {

		@Override
		public boolean accept(File pathname) {

			if (pathname.getName().lastIndexOf(".txt") != -1)
				return false;
			else
				return true;

		}
	};

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.list_file_play:

			String path = listItems.get(mIndex).get("ItemPath").toString();
			
			ENJUtils.alert(path);

			Intent intent = new Intent(ENJValues.SCHEME_ENJ);
			intent.setClass(ENJApplication.getContext(), MainActivity.class);
			intent.setData(Uri.parse(path));
			startActivity(intent);

			break;

		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		mIndex = position;
		((ListActivity) getActivity()).mTitle.setText(listItems.get(position)
				.get("ItemTitle").toString());

		mFileAdapter.setSelectedPosition(mIndex);
		mFileAdapter.notifyDataSetInvalidated();

		// ((ListActivity) getActivity()).changeFragment();
		//
		// String folder = listItems.get(position).get("ItemText").toString();
		//
		// Log.i("RRR", position + "|" + folder);
	}

}
