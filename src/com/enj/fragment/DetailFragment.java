package com.enj.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import com.enj.common.ENJValues;
import com.enj.movieup.R;
import com.enj.utils.ENJUtils;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore.Video;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.VideoView;

public class DetailFragment extends Fragment implements OnClickListener,
		OnCheckedChangeListener {

	private String mPath;

	private ToggleButton mFavorites;
	private VideoView mVideoView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View root = inflater
				.inflate(R.layout.fragment_detail, container, false);

		mFavorites = (ToggleButton) root.findViewById(R.id.detail_favorites);

		mPath = getArguments().getString("path", "");

		ENJUtils.alert(mPath);

		if (mPath.equals("")) {

		} else {

			File info = new File(mPath);

			if (!info.exists()) {

			} else {

				String json = ENJUtils.getString(info);

				try {
					JSONObject object = new JSONObject(json);

					File video = null;
					String path = object.optString("filename", "");

					if (path.equals("")) {

					} else {

						video = new File(path);
						if (video.exists()) {

							File folder = new File(ENJValues.PATH_FAVORITES);

							if (folder.exists()) {

								for (String filename : folder.list()) {

									if (filename.equals(info.getName())) {
										mFavorites.setChecked(true);
										break;
									}
								}
							}

							mFavorites.setOnCheckedChangeListener(this);

							mVideoView = ((VideoView) root
									.findViewById(R.id.detail_video));

							mVideoView.setVideoPath(path);
							mVideoView.setMediaController(new MediaController(
									getActivity()));

							Bitmap thumb = ThumbnailUtils.createVideoThumbnail(
									path, Video.Thumbnails.FULL_SCREEN_KIND);
							((ImageView) root.findViewById(R.id.detail_thumb))
									.setImageBitmap(thumb);
							((ImageView) root.findViewById(R.id.detail_thumb))
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											v.setVisibility(View.GONE);
											mVideoView.start();
										}
									});

						}

					}

					((TextView) root.findViewById(R.id.detail_title))
							.setText(object.optString("title"));

					String date = new Date(object.optString("date"))
							.toLocaleString();
					((TextView) root.findViewById(R.id.detail_date))
							.setText(date);
					((TextView) root.findViewById(R.id.detail_filename))
							.setText(video.getName());

					long duration = object.optLong("duration");

					String durationString = ENJValues.Format_time
							.format(new Date(duration - ENJValues.Time_Zone));
					ENJUtils.alert(durationString + "");

					((TextView) root.findViewById(R.id.detail_duration))
							.setText(durationString);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}

		return root;
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		File info = new File(mPath);
		File folder = new File(ENJValues.PATH_FAVORITES);

		if (isChecked) {

			if (!folder.exists()) {
				folder.mkdir();
			}

			String path = ENJValues.PATH_FAVORITES + info.getName();
			String json = ENJUtils.getString(info);
			ENJUtils.writeFile(path, json);

		} else {

			File file = new File(ENJValues.PATH_FAVORITES + info.getName());
			if (file.exists()) {
				file.delete();
			}

		}
	}
}
