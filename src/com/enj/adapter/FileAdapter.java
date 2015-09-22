package com.enj.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.enj.common.ENJValues;
import com.enj.movieup.R;
import com.enj.utils.ENJUtils;
import com.enj.widget.OnDeleteListioner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FileAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<HashMap<String, Object>> data;

	private int selectedPosition = 0;

	private OnDeleteListioner mOnDeleteListioner;
	private boolean delete = false;

	public FileAdapter(Context context,
			ArrayList<HashMap<String, Object>> arrayList) {

		data = arrayList;
		this.mInflater = LayoutInflater.from(context);

	}

	public void setData(ArrayList<HashMap<String, Object>> arrayList) {
		data = arrayList;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setOnDeleteListioner(OnDeleteListioner mOnDeleteListioner) {
		this.mOnDeleteListioner = mOnDeleteListioner;
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public void setSelectedPosition(int position) {

		selectedPosition = position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		View vi = convertView;
		ViewHolder holder;

		if (convertView == null) {

			vi = mInflater.inflate(R.layout.item_file, null);
			holder = new ViewHolder();

			holder.box = (LinearLayout) vi.findViewById(R.id.box);
			holder.itemtitle = (TextView) vi.findViewById(R.id.itemtitle);
			holder.itemfavorites = (CheckBox) vi
					.findViewById(R.id.itemfavorites);
			holder.itemdownload = (TextView) vi.findViewById(R.id.itemdownload);
			holder.itemfilename = (TextView) vi.findViewById(R.id.itemfilename);

			holder.delete_action = (TextView) vi
					.findViewById(R.id.delete_action);

			vi.setTag(holder);
		} else {

			holder = (ViewHolder) vi.getTag();
		}

		holder.itemtitle
				.setText(data.get(position).get("ItemTitle").toString());

		// File favorites = new File(ENJValues.PATH_FAVORITES);
		//
		// if (favorites.exists()) {
		//
		// for (String filename : favorites.list()) {
		//
		// if (data.get(position).get("ItemFilename").equals(filename)) {
		// holder.itemfavorites.setChecked(true);
		// } else {
		// holder.itemfavorites.setChecked(false);
		// }
		// }
		// } else {
		// holder.itemfavorites.setChecked(false);
		// }

		holder.itemfavorites.setChecked(((Boolean) data.get(position).get(
				"ItemCheck")));
		holder.itemfavorites.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				boolean isChecked = ((CheckBox) v).isChecked();
				data.get(position).put("ItemCheck", isChecked);

				ENJUtils.alert(data.get(position).get("ItemFilename")
						.toString());
				ENJUtils.alert("position : " + position);

				String path_txt = data.get(position).get("ItemPath").toString();
				path_txt = path_txt.substring(0, path_txt.lastIndexOf("."))
						+ ".txt";

				File info = new File(path_txt);
				File folder = new File(ENJValues.PATH_FAVORITES);

				if (isChecked) {

					if (!folder.exists()) {
						folder.mkdir();
					}

					String path = ENJValues.PATH_FAVORITES + info.getName();
					String json = ENJUtils.getString(info);
					ENJUtils.writeFile(path, json);
				} else {

					File file = new File(ENJValues.PATH_FAVORITES
							+ info.getName());
					if (file.exists()) {
						file.delete();
					}
				}
			}
		});

		// holder.itemfavorites
		// .setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		//
		// ENJUtils.alert(data.get(position).get("ItemFilename")
		// .toString());
		// ENJUtils.alert("position : " + position);
		//
		// String path_txt = data.get(position).get("ItemPath")
		// .toString();
		// path_txt = path_txt.substring(0,
		// path_txt.lastIndexOf("."))
		// + ".txt";
		//
		// File info = new File(path_txt);
		// File folder = new File(ENJValues.PATH_FAVORITES);
		//
		// if (isChecked) {
		//
		// if (!folder.exists()) {
		// folder.mkdir();
		// }
		//
		// String path = ENJValues.PATH_FAVORITES
		// + info.getName();
		// String json = ENJUtils.getString(info);
		// ENJUtils.writeFile(path, json);
		// } else {
		//
		// File file = new File(ENJValues.PATH_FAVORITES
		// + info.getName());
		// if (file.exists()) {
		// file.delete();
		// }
		// }
		// }
		// });

		holder.itemdownload.setText(data.get(position).get("ItemDownload")
				.toString());

		holder.itemfilename.setText(data
				.get(position)
				.get("ItemFilename")
				.toString()
				.substring(
						0,
						data.get(position).get("ItemFilename").toString()
								.lastIndexOf(".")));
		HashMap<String, Object> hashMap = data.get(position);
		holder.itemfilename.setText(ENJValues.Format_time.format(new Date(Long
				.valueOf(hashMap.get("ItemDuration").toString())
				- ENJValues.Time_Zone)));

		final OnClickListener mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {

				if (mOnDeleteListioner != null)
					mOnDeleteListioner.onDelete(position);
			}
		};

		holder.delete_action.setOnClickListener(mOnClickListener);

		// if (selectedPosition == position) {
		//
		// holder.box
		// .setBackgroundResource(R.drawable.ic_list_file_item_bg_focus);
		// } else {
		holder.box.setBackgroundResource(R.drawable.ic_list_file_item_bg);
		// }

		if (!data.get(position).get("ItemDRM").toString().equals(""))
			holder.itemfavorites
					.setBackgroundResource(R.drawable.selector_favorites_drm);
		return vi;
	}

	public final class ViewHolder {

		LinearLayout box;
		TextView itemtitle;
		CheckBox itemfavorites;
		TextView itemdownload;
		TextView itemfilename;

		public TextView delete_action;
	}
}
