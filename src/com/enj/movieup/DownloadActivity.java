package com.enj.movieup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.enj.common.ENJApplication;
import com.enj.common.ENJValues;
import com.enj.movieup.R;

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

	private ProgressBar mProgress;
	private TextView mPercentage;

	private int progress;
	private boolean interceptFlag = false;

	private String boardname;
	private String title;
	private String filename;
	private String name;
	private String ext;
	private String duration;
	private String videosize;

	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;

	private static String downloadPath = ENJValues.PATH_ROOT;

	// private static final String saveFileName = downloadPath + "Update.apk";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);

		mProgress = (ProgressBar) findViewById(R.id.download_progress);
		mPercentage = (TextView) findViewById(R.id.download_percentage);

		Uri uri = getIntent().getData();

		boardname = uri.getQueryParameter("boardname");
		title = uri.getQueryParameter("title");
		filename = uri.getQueryParameter("filename");
		name = filename.substring(filename.lastIndexOf("/"));
		ext = filename.substring(filename.lastIndexOf("."));
		duration = uri.getQueryParameter("duration");
		videosize = uri.getQueryParameter("videosize");

		Log.i("--------", boardname);
		Log.i("--------", name);
		Log.i("--------", ext);

		new Thread(downRunnable).start();
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
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File file = new File(downloadPath);
				if (!file.exists()) {
					file.mkdir();
				}

				file = new File(downloadPath + boardname);
				if (!file.exists()) {
					file.mkdir();
				}

				File downloadFile = new File(downloadPath + boardname + name);
				FileOutputStream fos = new FileOutputStream(downloadFile);

				int count = 1;
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

				mProgress.setProgress(progress);
				mPercentage.setText(progress + "%");
				break;
			case DOWN_OVER:

				JSONObject object = new JSONObject();
				try {
					object.put("boardname", boardname);
					object.put("title", title);
					object.put("date", new Date().toString());
					object.put("filename", downloadPath + boardname + name);
					object.put("duration", duration);
					object.put("videosize", videosize);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				Log.i("iiiiiii", downloadPath + boardname + name);
				try {
					File downloadFile = new File(downloadPath + boardname
							+ name.replace(ext, ".txt"));
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
