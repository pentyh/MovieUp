package com.enj.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.enj.common.ENJApplication;
import com.enj.movieup.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FolderAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<HashMap<String, Object>> data;

	private int selectedPosition = -1;

	public FolderAdapter(Context context,
			ArrayList<HashMap<String, Object>> arrayList) {

		data = arrayList;
		this.mInflater = LayoutInflater.from(context);

	}

	public void setData(ArrayList<HashMap<String, Object>> arrayList) {
		data = arrayList;
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public void setSelectedPosition(int position) {

		selectedPosition = position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View vi = convertView;
		ViewHolder holder;

		if (convertView == null) {

			vi = mInflater.inflate(R.layout.item_folder, null);
			holder = new ViewHolder();

			holder.box = (LinearLayout) vi.findViewById(R.id.box);
			holder.text = (TextView) vi.findViewById(R.id.ItemText);

			vi.setTag(holder);
		} else {

			holder = (ViewHolder) vi.getTag();
		}

		holder.text.setText(data.get(position).get("ItemText").toString());

		if (selectedPosition == position) {

			holder.box.setBackgroundColor(ENJApplication.getContext()
					.getResources().getColor(android.R.color.holo_green_dark));
		} else {

			holder.box.setBackgroundResource(R.drawable.ic_list_folder_item_bg);
		}

		return vi;
	}

	public final class ViewHolder {

		LinearLayout box;
		TextView text;
	}
}
