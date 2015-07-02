package com.enj.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.enj.adapter.FolderAdapter;
import com.enj.common.ENJApplication;
import com.enj.common.ENJValues;
import com.enj.movieup.ActionSheet;
import com.enj.movieup.ActionSheet.OnActionSheetSelected;
import com.enj.movieup.ListActivity;
import com.enj.movieup.R;
import com.enj.utils.ENJUtils;
import com.enj.widget.DelSlideListView;
import com.enj.widget.ListViewonSingleTapUpListenner;
import com.enj.widget.OnDeleteListioner;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class FolderFragment extends Fragment implements OnDeleteListioner,
		ListViewonSingleTapUpListenner, OnActionSheetSelected,
		OnCancelListener, OnItemClickListener {

	DelSlideListView mListView;
	FolderAdapter mFolderAdapter;

	ArrayList<HashMap<String, Object>> listItems;

	int delID = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View root = inflater
				.inflate(R.layout.fragment_folder, container, false);

		mListView = (DelSlideListView) root.findViewById(R.id.list);
		mFolderAdapter = new FolderAdapter(ENJApplication.getContext(),
				getFolder());
		mListView.setAdapter(mFolderAdapter);
		mListView.setDeleteListioner(this);
		mListView.setSingleTapUpListenner(this);
		mFolderAdapter.setOnDeleteListioner(this);

		mListView.setOnItemClickListener(this);

		return root;
	}

	public ArrayList<HashMap<String, Object>> getFolder() {

		listItems = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map;

		File root = new File(ENJValues.PATH_ROOT);

		if (!root.isDirectory()) {

			ENJUtils.toast("저장된 파일이 없습니다.");
		} else {

			for (File file : root.listFiles()) {

				if (!file.isDirectory())
					continue;

				map = new HashMap<String, Object>();
				map.put("ItemText", file.getName());

				if (file.getName().equals("Favorites")) {
					listItems.add(0, map);
				} else {
					listItems.add(map);
				}

			}
		}

		return listItems;
	}

	public ArrayList<HashMap<String, Object>> getData() {

		listItems = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map;

		File root = new File(ENJValues.PATH_ROOT);

		for (File file : root.listFiles()) {

			if (!file.isDirectory())
				continue;

			map = new HashMap<String, Object>();
			map.put("ItemText", file.getName());
			listItems.add(map);
		}

		return listItems;
	}

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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		String folder = listItems.get(position).get("ItemText").toString();
		((ListActivity) getActivity()).changeToFileFragment(folder);

		Log.i("RRR", position + "|" + folder);
	}

	@Override
	public boolean isCandelete(int position) {
		return true;
	}

	@Override
	public void onDelete(int ID) {

		delID = ID;
		ActionSheet.showSheet(getActivity(), this, this);
	}

	@Override
	public void onBack() {

	}

	@Override
	public void onCancel(DialogInterface dialog) {

	}

	@Override
	public void onClick(int whichButton) {

		switch (whichButton) {
		case 0:

			String folder = listItems.get(delID).get("ItemText").toString();

			File file = new File(ENJValues.PATH_ROOT + folder);

			if (!file.delete()) {

				ENJUtils.toast("빈 폴더만 삭제 할 수 있습니다.");
			} else {

				listItems.remove(delID);
				mListView.deleteItem();
				mFolderAdapter.notifyDataSetChanged();
			}

			break;
		case 1:

			break;
		}
	}

	@Override
	public void onSingleTapUp() {

		ENJUtils.alert("onSingleTapUp");
	}

}
