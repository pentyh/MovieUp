package com.enj.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.enj.movieup.R;
import com.enj.widget.OnDeleteListioner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FolderAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<HashMap<String, Object>> data;

	private int selectedPosition = -1;

	private OnDeleteListioner mOnDeleteListioner;
	private boolean delete = false;

	public FolderAdapter(Context context,
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

			vi = mInflater.inflate(R.layout.item_folder, null);
			holder = new ViewHolder();

			holder.text = (TextView) vi.findViewById(R.id.ItemText);
			holder.delete_action = (TextView) vi
					.findViewById(R.id.delete_action);

			vi.setTag(holder);
		} else {

			holder = (ViewHolder) vi.getTag();
		}

		final OnClickListener mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {

				if (mOnDeleteListioner != null)
					mOnDeleteListioner.onDelete(position);
			}
		};

		holder.delete_action.setOnClickListener(mOnClickListener);
		holder.text.setText(data.get(position).get("ItemText").toString());

		// if (selectedPosition == position) {
		//
		// holder.box.setBackgroundColor(ENJApplication.getContext()
		// .getResources().getColor(android.R.color.holo_green_dark));
		// } else {
		//
		// holder.box.setBackgroundResource(R.drawable.ic_list_folder_item_bg);
		// }

		return vi;
	}

	public static class ViewHolder {

		LinearLayout box;
		TextView text;

		public TextView delete_action;
	}
}
