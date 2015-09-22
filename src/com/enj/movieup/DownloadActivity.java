package com.enj.movieup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.enj.common.ENJApplication;
import com.enj.common.ENJValues;
import com.enj.movieup.R;
import com.enj.utils.ENJUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadActivity extends Activity implements OnClickListener {

	private TextView mDownloading;
	private ProgressBar mProgress;
	private TextView mPercentage;

	private int count = 1;
	private int length;
	private int progress;
	private boolean interceptFlag = false;

	private String boardid;
	private String boardname;
	private String contentid;
	private String title;
	private String filename;
	private String name;
	private String ext;
	private String duration;
	private String videosize;
	private String user_id;
	private String drm_key;

	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;

	private static String downloadPath = ENJValues.PATH_ROOT;

	// private static final String saveFileName = downloadPath + "Update.apk";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);

		mDownloading = (TextView) findViewById(R.id.downloading);
		mProgress = (ProgressBar) findViewById(R.id.download_progress);
		mPercentage = (TextView) findViewById(R.id.download_percentage);

		Uri uri = getIntent().getData();

		boardid = uri.getQueryParameter("BoardID");
		boardname = uri.getQueryParameter("boardname");
		contentid = uri.getQueryParameter("contentid");
		title = uri.getQueryParameter("title");
		filename = uri.getQueryParameter("filename");
		name = filename.substring(filename.lastIndexOf("/"));
		ext = filename.substring(filename.lastIndexOf("."));
		duration = uri.getQueryParameter("duration");
		videosize = uri.getQueryParameter("videosize");
		user_id = uri.getQueryParameter("user_id");
		drm_key = uri.getQueryParameter("drm_key");

		Log.i("boardid---", boardid);
		Log.i("boardname---", boardname);
		Log.i("name---", name);
		Log.i("ext---", ext);

		boolean flag = false;
		File root = new File(ENJValues.PATH_ROOT);

		if (!root.isDirectory()) {

			ENJUtils.toast("저장된 파일이 없습니다.");
		} else {

			for (File file : root.listFiles()) {

				if (!file.isDirectory())
					continue;

				if (file.getName().equals("Favorites")) {

					continue;
				} else {

					File file_txt = new File(ENJValues.PATH_ROOT
							+ file.getName());

					for (File file2 : file_txt.listFiles()) {

						if (file2.isDirectory())
							continue;

						if (file2.getName().indexOf(".txt") < 0)
							continue;

						String result = ENJUtils.getString(file2);

						try {
							JSONObject object = new JSONObject(result);
							String path = object.getString("filename");

							if (path != null && !path.equals("")) {

								File video = new File(path);

								if (video.isFile()) {

									if (object.optString("contentid").equals(
											contentid)) {

										if (object.optString("boardid").equals(
												boardid)) {

											if (object.optString("videosize")
													.equals(videosize)) {

												ENJUtils.alert("SAME");
												count = length = Integer
														.parseInt(videosize);
												progress = 100;
												mHandler.sendEmptyMessage(DOWN_UPDATE);
												mHandler.sendEmptyMessage(DOWN_OVER);

												flag = true;

												break;

											} else {
												ENJUtils.toast("ERROR");
											}

										} else {

											File new_folder = new File(downloadPath + boardname + "(" + boardid + ")");
											if (!new_folder.exists()) {
												new_folder.mkdir();
											}
											
											filename = object.optString("filename");
											count = length = Integer
													.parseInt(videosize);
											progress = 100;
											mHandler.sendEmptyMessage(DOWN_UPDATE);
											mHandler.sendEmptyMessage(DOWN_OVER);

											flag = true;
										}

									}

								}
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

				}

				if (flag) {

					break;

				}

			}
		}

		if (!flag) {

			new Thread(downRunnable).start();
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.download_cancel:

			interceptFlag = true;
			finish();
			break;
		case R.id.download_ok:

			Intent intent = new Intent();
			intent.setClass(this, ListActivity.class);
			intent.setType("V");
			intent.putExtra("folder", boardname + "(" + boardid + ")");
			intent.putExtra("boardname", boardname);
			intent.putExtra("path", downloadPath + boardname + "(" + boardid
					+ ")" + name);
			startActivity(intent);

			finish();
			break;
		}
	}

	private Runnable downRunnable = new Runnable() {

		@Override
		public void run() {
			try {

				URL url = new URL(filename);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				// check root folder
				File file = new File(downloadPath);
				if (!file.exists()) {
					file.mkdir();
				}

				// check board folder
				file = new File(downloadPath + boardname + "(" + boardid + ")");
				if (!file.exists()) {
					file.mkdir();
				}

				File downloadFile = new File(downloadPath + boardname + "("
						+ boardid + ")" + name);
				FileOutputStream fos = new FileOutputStream(downloadFile);

				byte buf[] = new byte[1024];
				do {

					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);

					// 다운로드 프로그래스 업데이트
					mHandler.sendEmptyMessage(DOWN_UPDATE);

					if (numread <= 0) {
						// 다운로드 완료시 설치 요청
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}

					fos.write(buf, 0, numread);

				} while (!interceptFlag);

				fos.close();
				is.close();

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:

				mDownloading.setText(count / 1024 + "K/" + length / 1024 + "K");
				mProgress.setProgress(progress);
				mPercentage.setText(progress + "%");
				break;
			case DOWN_OVER:

				JSONObject object = new JSONObject();
				try {

					object.put("contentid", contentid);
					object.put("boardid", boardid);
					object.put("boardname", boardname);
					object.put("title", title);
					object.put("date",
							ENJValues.Formart_Date.format(new Date()));
					object.put("filename", downloadPath + boardname + "("
							+ boardid + ")" + name);
					object.put("duration", duration);
					object.put("videosize", videosize);
					object.put("user_id", user_id);
					object.put("drm_key", drm_key);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				try {
					File downloadFile = new File(downloadPath + boardname + "("
							+ boardid + ")" + name.replace(ext, ".txt"));
					FileOutputStream fos = new FileOutputStream(downloadFile);

					byte[] b = object.toString().getBytes();
					fos.write(b);
					fos.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				findViewById(R.id.download_cancel).setVisibility(View.GONE);
				findViewById(R.id.download_ok).setVisibility(View.VISIBLE);
				break;
			}
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
