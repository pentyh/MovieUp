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

public class FileAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<HashMap<String, Object>> data;

	private int selectedPosition = 0;

	public FileAdapter(Context context,
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

			vi = mInflater.inflate(R.layout.item_file, null);
			holder = new ViewHolder();

			holder.box = (LinearLayout) vi.findViewById(R.id.box);
			holder.itemtitle = (TextView) vi.findViewById(R.id.itemtitle);
			holder.itemdownload = (TextView) vi.findViewById(R.id.itemdownload);
			holder.itemfilename = (TextView) vi.findViewById(R.id.itemfilename);

			vi.setTag(holder);
		} else {

			holder = (ViewHolder) vi.getTag();
		}

		holder.itemtitle
				.setText(data.get(position).get("ItemTitle").toString());
		holder.itemdownload.setText(data.get(position).get("ItemDownload")
				.toString());
		holder.itemfilename.setText(data.get(position).get("ItemFilename")
				.toString());

		if (selectedPosition == position) {

			holder.box
					.setBackgroundResource(R.drawable.ic_list_file_item_bg_focus);
		} else {
			holder.box.setBackgroundResource(R.drawable.ic_list_file_item_bg);
		}

		// Drawable nav_up=getResources().getDrawable(R.drawable.button_nav_up);
		// nav_up.setBounds(0, 0, nav_up.getMinimumWidth(),
		// nav_up.getMinimumHeight());
		// textview1.setCompoundDrawables(null, null, nav_up, null);

		return vi;
	}

	public final class ViewHolder {

		LinearLayout box;
		TextView itemtitle;
		TextView itemdownload;
		TextView itemfilename;
	}
}
