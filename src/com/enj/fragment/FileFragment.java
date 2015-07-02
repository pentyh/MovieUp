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
import com.enj.movieup.FileActionSheet.OnActionSheetSelected;
import com.enj.movieup.FileActionSheet;
import com.enj.movieup.ListActivity;
import com.enj.movieup.MainActivity;
import com.enj.movieup.R;
import com.enj.utils.ENJUtils;
import com.enj.widget.DelSlideListView;
import com.enj.widget.ListViewonSingleTapUpListenner;
import com.enj.widget.OnDeleteListioner;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class FileFragment extends Fragment implements OnDeleteListioner,
		ListViewonSingleTapUpListenner, OnActionSheetSelected,
		OnCancelListener, OnClickListener, OnItemClickListener {

	private String mFolder;

	private int mIndex = 0;

	DelSlideListView mListView;
	FileAdapter mFileAdapter;

	ArrayList<HashMap<String, Object>> listItems;

	int delID = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mFolder = getArguments().getString("folder", "");

		((ListActivity) getActivity()).mTitle.setText(mFolder);
		View root = inflater.inflate(R.layout.fragment_file, container, false);

		if (!mFolder.equals("")) {

			mListView = (DelSlideListView) root.findViewById(R.id.list);
			mFileAdapter = new FileAdapter(ENJApplication.getContext(),
					getData());
			mFileAdapter.setSelectedPosition(mIndex);
			mListView.setAdapter(mFileAdapter);

			mListView.setDeleteListioner(this);
			mListView.setSingleTapUpListenner(this);
			mFileAdapter.setOnDeleteListioner(this);

			mListView.setOnItemClickListener(this);

			root.findViewById(R.id.list_file_all).setOnClickListener(this);
			root.findViewById(R.id.list_file_play).setOnClickListener(this);
			root.findViewById(R.id.list_file_open).setOnClickListener(this);
		}

		return root;
	}

	public ArrayList<HashMap<String, Object>> getData() {

		listItems = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map;

		File root = new File(ENJValues.PATH_ROOT + mFolder);
		if (!root.exists())
			return listItems;

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
							map.put("ItemCheck", false);

							File favorites = new File(ENJValues.PATH_FAVORITES);
							if (favorites.exists()) {

								for (String filename : favorites.list()) {

									if (file.getName().equals(filename)) {
										map.put("ItemCheck", true);
									}
								}
							}

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

		if (listItems.size() == 0) {

			ENJUtils.toast("파일이 존재하지 않습니다");
			return;
		}

		switch (v.getId()) {
		case R.id.list_file_all:

			ArrayList<String> arrayList = new ArrayList<String>();

			for (HashMap<String, Object> map : listItems) {

				arrayList.add(map.get("ItemPath").toString());
			}

			Intent arrintent = new Intent(ENJValues.SCHEME_ENJS);
			arrintent.setClass(ENJApplication.getContext(), MainActivity.class);
			arrintent.putStringArrayListExtra("paths", arrayList);
			startActivity(arrintent);

			break;
		case R.id.list_file_play:

			String path = listItems.get(mIndex).get("ItemPath").toString();

			Intent intent = new Intent(ENJValues.SCHEME_ENJ);
			intent.setClass(ENJApplication.getContext(), MainActivity.class);
			intent.setData(Uri.parse(path));
			startActivity(intent);

			break;

		case R.id.list_file_open:

			String path_txt = listItems.get(mIndex).get("ItemPath").toString();
			path_txt = path_txt.substring(0, path_txt.lastIndexOf("."))
					+ ".txt";

			((ListActivity) getActivity()).changeToDetailFragment(path_txt);

			break;

		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		mIndex = position;
		mFileAdapter.setSelectedPosition(mIndex);
		mFileAdapter.notifyDataSetChanged();

		// ((ListActivity) getActivity()).changeFragment();
		// String folder = listItems.get(position).get("ItemText").toString();
		// Log.i("RRR", position + "|" + folder);
	}

	@Override
	public void onCancel(DialogInterface dialog) {

	}

	@Override
	public void onClick(int whichButton) {

		switch (whichButton) {
		case 0:

			String path = listItems.get(delID).get("ItemPath").toString();

			File file = new File(path);

			String path_txt = path.substring(0, path.lastIndexOf(".")) + ".txt";

			File file_txt = new File(path_txt);

			File file_favorites = new File(ENJValues.PATH_FAVORITES
					+ file_txt.getName());

			if (file_favorites.exists())
				file_favorites.delete();

			if (!mFolder.equals(ENJValues.FAVORITES)) {

				if (file.exists())
					file.delete();

				if (file_txt.exists())
					file_txt.delete();
			}

			listItems.remove(delID);
			mListView.deleteItem();
			mFileAdapter.notifyDataSetChanged();

			break;
		case 1:

			break;
		}
	}

	@Override
	public void onSingleTapUp() {

	}

	@Override
	public boolean isCandelete(int position) {
		return true;
	}

	@Override
	public void onDelete(int ID) {

		delID = ID;
		FileActionSheet.showSheet(getActivity(), this, this);
	}

	@Override
	public void onBack() {

	}

}
