package com.enj.movieup;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import com.enj.common.ENJValues;
import com.enj.movieup.R;
import com.enjsoft.util.EnjCert;
import com.enjsoft.util.EnjCert.OnCertCompleteListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.System;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.SurfaceHolder.Callback;
import android.view.WindowManager.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements Callback,
		OnBufferingUpdateListener, OnInfoListener, OnVideoSizeChangedListener,
		OnCompletionListener, OnSeekBarChangeListener, OnCheckedChangeListener,
		OnClickListener, OnCertCompleteListener {
	private static final String TAG = MainActivity.class.toString();

	private static String authid;
	private static String key = "";

	private static int old_position = 0;
	private static boolean isSeek = false;

	SurfaceView mSurfaceView;
	SurfaceHolder holder;
	MediaPlayer mMediaPlayer;

	private static Uri uri;
	private int mVideoWidth = 0;
	private int mVideoHeight = 0;

	ProgressBar loadingBar;
	ImageView hoverImageView;
	String imageUrl = null;

	RelativeLayout overLay;

	DisplayMetrics metrics;

	int mWidth = 0;
	int mHeight = 0;

	int x1 = 0, y1 = 0;
	int x2 = 0, y2 = 0;

	float trans_X = 0;
	float trans_Y = 0;

	float oldDist;

	TextView currentTime;
	SeekBar seek;
	int mProgress;
	int mVolume;
	int mBrightness;
	TextView duration;
	ToggleButton mPlayButton;

	AudioManager mAudioManager;

	int brightness;
	int currentVolume;

	SeekBar seek_brightness;
	SeekBar seek_volume;

	boolean move = false;

	long oldTime = 0;

	FrameLayout.LayoutParams lp;

	EnjCert m_ec;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		metrics = this.getResources().getDisplayMetrics();

		Log.i("VERSION", Build.VERSION.SDK_INT + "");

		init();

		Intent intent = getIntent();
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {

			Log.i("intent.getDataString", intent.getDataString());
			String scheme = intent.getData().getScheme();
			Log.i("Scheme", scheme);

			if(scheme.equals(ENJValues.SCHEME_ENJ) ){
				
				String type = intent.getData().getQueryParameter("type");
				if(type != null && type.equals("download")){
					
					Intent newIntent = new Intent();
					newIntent.setClass(this, DownloadActivity.class);
					startActivity(newIntent);
					return;
				}
			}
			
			boolean ssl = false;
			if (scheme.equals("sftask-streamssl") || scheme.equals("https")
					|| scheme.equals("enjps")) {
				ssl = true;
			}

			if (intent.getDataString().indexOf("?") > 0) {

				uri = Uri.parse(intent.getDataString().substring(0,
						intent.getDataString().indexOf("?")));
			} else {
				uri = intent.getData();
			}

			if (intent.getData().getQueryParameter("key") != null) {
				key = intent.getData().getQueryParameter("key");
			}

			if (intent.getData().getQueryParameter("titleimg") != null) {
				imageUrl = intent.getData().getQueryParameter("titleimg");
			}

			m_ec.Open(ssl, uri.getHost(), uri.getPort());

		} else {

			// showCloseDialog();
		}

	}

	private void init() {

		overLay = (RelativeLayout) findViewById(R.id.overlay);

		seek_brightness = (SeekBar) findViewById(R.id.player_overlay_brightness);
		seek_brightness.setRotation(-90.0f);

		seek_volume = (SeekBar) findViewById(R.id.player_overlay_volume);
		seek_volume.setRotation(-90.0f);

		currentTime = (TextView) findViewById(R.id.player_overlay_currenttime);
		seek = (SeekBar) findViewById(R.id.player_overlay_seek);
		seek.setOnSeekBarChangeListener(this);
		duration = (TextView) findViewById(R.id.player_overlay_duration);

		loadingBar = (ProgressBar) findViewById(R.id.player_overlay_loading);
		hoverImageView = (ImageView) findViewById(R.id.hover);

		m_ec = new EnjCert();

		m_ec.setOnCertCompleteListener(this);// 인증 결과에 대한 이벤트를 수신하기 위해서 리스너 설정

		mSurfaceView = (SurfaceView) findViewById(R.id.videoview);
		// mSurfaceView.setZOrderOnTop(true);
		lp = (FrameLayout.LayoutParams) mSurfaceView.getLayoutParams();
		holder = mSurfaceView.getHolder();
		holder.setFormat(PixelFormat.TRANSPARENT);
		holder.addCallback(MainActivity.this);

		/*
		 * 재생 버튼
		 */
		mPlayButton = (ToggleButton) findViewById(R.id.player_overlay_play);
		mPlayButton.setOnCheckedChangeListener(this);

		/*
		 * 전체화면 버튼
		 */
		ToggleButton mToggleButton2 = (ToggleButton) findViewById(R.id.player_overlay_fix);
		mToggleButton2.setOnCheckedChangeListener(this);

		/*
		 * 밝기 설정
		 */
		brightness = getScreenBrightness();
		seek_brightness.setProgress((brightness - 20) * 15 / 235);

		seek_brightness.setOnSeekBarChangeListener(this);

		/*
		 * 볼륨 설정
		 */
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		currentVolume = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);

		seek_volume.setMax(maxVolume);
		seek_volume.setProgress(currentVolume);
		seek_volume.setOnSeekBarChangeListener(this);

	}

	@Override
	public void onOnCertComplete() {

		authid = m_ec.GetAuthID();
		Log.i("authID ---", authid);
		Log.i("tempUrl ---", uri.toString());

		if (imageUrl != null) {
			mHandler.sendMessage(mHandler.obtainMessage(2, View.VISIBLE));
			new Thread(mRunnable).start();
		} else {
			playVideo();
		}

	};

	Runnable mRunnable = new Runnable() {

		@Override
		public void run() {

			if (!imageUrl.substring(0, 4).equals("http")) {

				Uri tempUri = getIntent().getData();

				if (tempUri.getScheme().equals("sftask-streamssl")) {

					imageUrl = "https://" + tempUri.getAuthority() + "/"
							+ tempUri.getPathSegments().get(0) + "img/"
							+ imageUrl + "?selfcert=" + authid;
				} else {
					imageUrl = "http://" + tempUri.getAuthority() + "/"
							+ tempUri.getPathSegments().get(0) + "img/"
							+ imageUrl + "?selfcert=" + authid;
				}

			}

			Log.i("imageUrl ---", imageUrl);

			URL myFileUrl = null;
			Bitmap bitmap = null;
			try {
				myFileUrl = new URL(imageUrl);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			try {
				HttpURLConnection conn = (HttpURLConnection) myFileUrl
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Message msg = Message.obtain();
			msg.obj = bitmap;
			mHandler.sendMessage(msg);

		}
	};

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1) {
				showComfirmDialog();
				return;
			} else if (msg.what == 2) {

				loadingBar.setVisibility((Integer) msg.obj);
			} else {

				loadingBar.setVisibility(View.GONE);
				hoverImageView.setImageBitmap((Bitmap) msg.obj);
				hoverImageView.setVisibility(View.VISIBLE);
				overLay.setVisibility(View.VISIBLE);
			}

		};
	};

	/**
	 * 경고창
	 */
	private void showCloseDialog() {

		View diaView = View.inflate(this, R.layout.dialog_view, null);

		final Dialog dialog = new Dialog(MainActivity.this, R.style.dialog);
		dialog.setCancelable(false);
		dialog.setContentView(diaView);
		dialog.show();

		diaView.findViewById(R.id.dialog_close).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						dialog.dismiss();
						MainActivity.this.finish();

					}
				});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		metrics = this.getResources().getDisplayMetrics();

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:

			mWidth = mSurfaceView.getWidth();
			mHeight = mSurfaceView.getHeight();

			trans_X = mSurfaceView.getTranslationX();
			trans_Y = mSurfaceView.getTranslationY();

			x1 = (int) event.getX(0);
			y1 = (int) event.getY(0);

			if (event.getPointerCount() == 2) {

				x2 = (int) event.getX(1);
				y2 = (int) event.getY(1);

				oldDist = spacing(event);
			}

			break;
		case MotionEvent.ACTION_MOVE:

			if (event.getPointerCount() == 1) {

				if (mSurfaceView.getWidth() > metrics.widthPixels) {

					if (Math.abs(event.getX() - x1) > 50) {
						move = true;
					}

					float left = trans_X + (int) (event.getX(0) - x1);

					if (left > (mSurfaceView.getWidth() - metrics.widthPixels) / 2) {
						trans_X = left = (mSurfaceView.getWidth() - metrics.widthPixels) / 2;
						x1 = (int) event.getX(0);
					}

					if (left < -(mSurfaceView.getWidth() - metrics.widthPixels) / 2) {
						trans_X = left = -(mSurfaceView.getWidth() - metrics.widthPixels) / 2;
						x1 = (int) event.getX(0);
					}

					mSurfaceView.setTranslationX(left);

				}
				if (mSurfaceView.getHeight() > metrics.heightPixels) {

					if (Math.abs(event.getY() - y1) > 50) {
						move = true;
					}

					float top = trans_Y + (int) (event.getY(0) - y1);

					if (top > (mSurfaceView.getHeight() - metrics.heightPixels) / 2) {
						trans_Y = top = (mSurfaceView.getHeight() - metrics.heightPixels) / 2;
						y1 = (int) event.getY(0);
					}

					if (top < -(mSurfaceView.getHeight() - metrics.heightPixels) / 2) {
						trans_Y = top = -(mSurfaceView.getHeight() - metrics.heightPixels) / 2;
						y1 = (int) event.getY(0);
					}

					mSurfaceView.setTranslationY(top);

				}

				if (mSurfaceView.getWidth() <= metrics.widthPixels
						&& mSurfaceView.getHeight() <= metrics.heightPixels) {

					if (Math.abs(event.getY() - y1) < 20
							&& Math.abs(event.getX() - x1) < 20)
						return true;

					move = true;

					int ret = getOneDragDirection((int) event.getX(0),
							(int) event.getY(0), x1, y1);

					switch (ret) {
					case 1:
						if (metrics.widthPixels / 2 > event.getX(0)) {

							x1 = (int) event.getX(0);
							y1 = (int) event.getY(0);

							if (brightness <= 255) {
								brightness += 20;
								setScreenBrightness(brightness);

								seek_brightness
										.setOnSeekBarChangeListener(null);
								seek_brightness
										.setProgress((brightness - 20) * 15 / 235);
								seek_brightness
										.setOnSeekBarChangeListener(this);

								((TextView) findViewById(R.id.alert_b))
										.setVisibility(View.VISIBLE);
								((TextView) findViewById(R.id.alert_b))
										.setText(""
												+ seek_brightness.getProgress());
							}

						} else {

							if (Math.abs(event.getY(0) - y1) > 20) {

								x1 = (int) event.getX(0);
								y1 = (int) event.getY(0);

								mAudioManager
										.setStreamVolume(
												AudioManager.STREAM_MUSIC,
												mAudioManager
														.getStreamVolume(AudioManager.STREAM_MUSIC) + 1,
												0);
								//
								// mAudioManager.adjustStreamVolume(
								// AudioManager.STREAM_MUSIC,
								// AudioManager.ADJUST_RAISE,
								// AudioManager.FX_FOCUS_NAVIGATION_UP);
								seek_volume
										.setProgress(mAudioManager
												.getStreamVolume(AudioManager.STREAM_MUSIC));

								if (mAudioManager
										.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {

									((TextView) findViewById(R.id.alert_v))
											.setVisibility(View.GONE);
									((TextView) findViewById(R.id.alert_v0))
											.setVisibility(View.VISIBLE);

								} else {

									((TextView) findViewById(R.id.alert_v0))
											.setVisibility(View.GONE);
									((TextView) findViewById(R.id.alert_v))
											.setVisibility(View.VISIBLE);
									((TextView) findViewById(R.id.alert_v))
											.setText(""
													+ mAudioManager
															.getStreamVolume(AudioManager.STREAM_MUSIC));
								}
							}
						}
						break;
					case 2:
						if (metrics.widthPixels / 2 > event.getX(0)) {

							x1 = (int) event.getX(0);
							y1 = (int) event.getY(0);

							if (brightness >= 20) {
								brightness -= 20;
								setScreenBrightness(brightness);
								seek_brightness
										.setOnSeekBarChangeListener(null);
								seek_brightness
										.setProgress((brightness - 20) * 15 / 235);
								seek_brightness
										.setOnSeekBarChangeListener(this);
								((TextView) findViewById(R.id.alert_b))
										.setVisibility(View.VISIBLE);
								((TextView) findViewById(R.id.alert_b))
										.setText(""
												+ seek_brightness.getProgress());
							}

						} else {
							if (Math.abs(event.getY(0) - y1) > 20) {

								x1 = (int) event.getX(0);
								y1 = (int) event.getY(0);

								mAudioManager
										.setStreamVolume(
												AudioManager.STREAM_MUSIC,
												mAudioManager
														.getStreamVolume(AudioManager.STREAM_MUSIC) - 1,
												0);
								// mAudioManager.adjustStreamVolume(
								// AudioManager.STREAM_MUSIC,
								// AudioManager.ADJUST_LOWER,
								// AudioManager.FX_FOCUS_NAVIGATION_UP);
								seek_volume
										.setProgress(mAudioManager
												.getStreamVolume(AudioManager.STREAM_MUSIC));

								if (mAudioManager
										.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {

									((TextView) findViewById(R.id.alert_v))
											.setVisibility(View.GONE);
									((TextView) findViewById(R.id.alert_v0))
											.setVisibility(View.VISIBLE);

								} else {

									((TextView) findViewById(R.id.alert_v0))
											.setVisibility(View.GONE);
									((TextView) findViewById(R.id.alert_v))
											.setVisibility(View.VISIBLE);
									((TextView) findViewById(R.id.alert_v))
											.setText(""
													+ mAudioManager
															.getStreamVolume(AudioManager.STREAM_MUSIC));
								}

							}
						}
						break;
					case 3:

						if (Math.abs(event.getX() - x1) > 20) {

							mMediaPlayer.seekTo(mMediaPlayer
									.getCurrentPosition() - 1000);
							mMediaPlayer.start();
							x1 = (int) event.getX(0);
							y1 = (int) event.getY(0);
						}

						break;
					case 4:

						if (Math.abs(event.getX() - x1) > 20) {
							mMediaPlayer.seekTo(mMediaPlayer
									.getCurrentPosition() + 1000);
							mMediaPlayer.start();
							x1 = (int) event.getX(0);
							y1 = (int) event.getY(0);
						}

						break;
					}
				}

			} else if (event.getPointerCount() == 2) {

				move = true;

				float newDist = spacing(event);
				float scale = newDist / oldDist;

				if (mWidth * scale > mVideoWidth * 4) {

					lp.width = mVideoWidth * 4;
					lp.height = mVideoHeight * 4;
					mWidth = mSurfaceView.getWidth();
					mHeight = mSurfaceView.getHeight();
					oldDist = spacing(event);
				} else if (mWidth * scale < mVideoWidth * 0.5) {

					lp.width = (int) (mVideoWidth * 0.5);
					lp.height = (int) (mVideoHeight * 0.5);
					mWidth = mSurfaceView.getWidth();
					mHeight = mSurfaceView.getHeight();
					oldDist = spacing(event);
				} else {

					lp.width = (int) (mWidth * scale);
					lp.height = (int) (mHeight * scale);
				}

				if (mSurfaceView.getTranslationX() > (mSurfaceView.getWidth() - metrics.widthPixels) / 2) {
					mSurfaceView
							.setTranslationX((mSurfaceView.getWidth() - metrics.widthPixels) / 2);
				}

				if (mSurfaceView.getTranslationX() < -(mSurfaceView.getWidth() - metrics.widthPixels) / 2) {
					mSurfaceView
							.setTranslationX(-(mSurfaceView.getWidth() - metrics.widthPixels) / 2);
				}

				if (mSurfaceView.getTranslationY() > (mSurfaceView.getHeight() - metrics.heightPixels) / 2) {
					mSurfaceView
							.setTranslationY((mSurfaceView.getHeight() - metrics.heightPixels) / 2);
				}

				if (mSurfaceView.getTranslationY() < -(mSurfaceView.getHeight() - metrics.heightPixels) / 2) {
					mSurfaceView
							.setTranslationY(-(mSurfaceView.getHeight() - metrics.heightPixels) / 2);
				}

				if (mSurfaceView.getWidth() < metrics.widthPixels
						|| mSurfaceView.getHeight() < metrics.heightPixels) {

					mSurfaceView.setTranslationX(0);
					mSurfaceView.setTranslationY(0);
				}

				lp.gravity = Gravity.CENTER;
				mSurfaceView.setLayoutParams(lp);

			} else {

				move = true;
			}

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:

			if (event.getPointerCount() == 1) {

				findViewById(R.id.alert_b).setVisibility(View.GONE);
				// findViewById(R.id.alert_p).setVisibility(View.GONE);
				findViewById(R.id.alert_v).setVisibility(View.GONE);
				findViewById(R.id.alert_v0).setVisibility(View.GONE);

				if ((new Date().getTime() - oldTime) < 300) {

					handler.removeCallbacks(showRunnable);

					if (mMediaPlayer != null) {

						if (mMediaPlayer.isPlaying()) {
							mMediaPlayer.pause();
						} else {
							playVideo();
						}
						mPlayButton.setChecked(!mMediaPlayer.isPlaying());
					}

					oldTime = 0;

					return true;
				}

				oldTime = new Date().getTime();

				if (!move) {

					handler.postDelayed(showRunnable, 300);
				}

				move = false;

			} else if (event.getPointerCount() == 2) {

				trans_X = mSurfaceView.getTranslationX();
				trans_Y = mSurfaceView.getTranslationY();

				x1 = (int) event.getX(0);
				y1 = (int) event.getY(0);
			}

			break;

		}

		return true;
	}

	Handler handler = new Handler();

	Runnable updateThread = new Runnable() {

		public void run() {

			if (mMediaPlayer.isPlaying()) {
				hoverImageView.setVisibility(View.GONE);

				// Log.i("updateThread >>> ",
				// mMediaPlayer.getCurrentPosition()
				// + "|"
				// + old_position
				// + "|"
				// + (mMediaPlayer.getCurrentPosition() - old_position));

				if (mMediaPlayer.getCurrentPosition() > old_position) {

					loadingBar.setVisibility(View.GONE);

				} else {

					loadingBar.setVisibility(View.VISIBLE);
				}

			}

			currentTime.setText(ShowTime(mMediaPlayer.getCurrentPosition()));
			duration.setText(ShowTime(mMediaPlayer.getDuration()));

			seek.setMax(mMediaPlayer.getDuration());

			if (!isSeek) {

				seek.setProgress(mMediaPlayer.getCurrentPosition());
			}

			old_position = mMediaPlayer.getCurrentPosition();
			handler.postDelayed(updateThread, 200);
			// handler.removeCallbacks(updateThread);

		}
	};

	Runnable showRunnable = new Runnable() {

		public void run() {

			if (overLay.getVisibility() == View.VISIBLE) {

				overLay.setVisibility(View.GONE);
			} else {

				if (mMediaPlayer != null) {
					mPlayButton.setChecked(!mMediaPlayer.isPlaying());
				}

				overLay.setVisibility(View.VISIBLE);
			}

			seek_brightness.setVisibility(View.INVISIBLE);
			seek_volume.setVisibility(View.INVISIBLE);

		}
	};

	/**
	 * 두손 간격
	 * 
	 * @param event
	 * @return
	 */
	private float spacing(MotionEvent event) {

		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * 제스처 방향
	 * 
	 * @param newX
	 * @param newY
	 * @param oldX
	 * @param oldY
	 * @return
	 */
	public int getOneDragDirection(int newX, int newY, int oldX, int oldY) {

		int ret = -1;

		int y = newY - oldY;
		int x = newX - oldX;

		if (Math.abs(y) > Math.abs(x)) {

			if (y < 0) {
				ret = 1;
			} else {
				ret = 2;
			}
		} else {
			if (x < 0) {
				ret = 3;
			} else {
				ret = 4;
			}
		}

		return ret;
	}

	/**
	 * 화면 밝기 설정
	 * 
	 * @param paramInt
	 */
	private void setScreenBrightness(int paramInt) {

		if (paramInt > 235 || paramInt < 0)
			return;

		Window localWindow = getWindow();
		LayoutParams localLayoutParams = localWindow.getAttributes();
		float f = (paramInt + 20) / 255.0F;
		localLayoutParams.screenBrightness = f;
		localWindow.setAttributes(localLayoutParams);
	}

	private void playVideo() {
		try {

			if (mMediaPlayer == null) {

				if (uri == null)
					return;

				mHandler.sendMessage(mHandler.obtainMessage(2, View.VISIBLE));

				if (!uri.getLastPathSegment().equals("playlist.m3u8")) {
					uri = Uri.parse(uri.toString() + "/playlist.m3u8");
				}

				if (uri.getScheme().equals("sftask-streamssl")
						|| uri.getScheme().equals("enjps")) {

					uri = Uri.parse("https:" + uri.getSchemeSpecificPart());
				} else if (uri.getScheme().equals("sftask-stream")
						|| uri.getScheme().equals("enjp")) {

					uri = Uri.parse("http:" + uri.getSchemeSpecificPart());
				}

				uri = Uri.parse(uri.toString() + "?selfcert=" + authid
						+ "&key=" + key);

				Log.i("videoUrl ---", uri.toString());

				new Thread(new Runnable() {

					@Override
					public void run() {
						
						
						mMediaPlayer = MediaPlayer.create(MainActivity.this,
								uri, holder);
						
						if(mMediaPlayer == null){
							finish();
							return;
						}
						
						mMediaPlayer
								.setAudioStreamType(AudioManager.STREAM_MUSIC);
						mMediaPlayer
								.setOnBufferingUpdateListener(MainActivity.this);
						mMediaPlayer.setOnInfoListener(MainActivity.this);
						mMediaPlayer
								.setOnVideoSizeChangedListener(MainActivity.this);
						mMediaPlayer.setOnCompletionListener(MainActivity.this);
						mMediaPlayer.start();

						handler.post(updateThread);
					}
				}).start();

			} else {

				mMediaPlayer.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
			 Log.e(TAG, "error: " + e.getMessage(), e);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {

		if (mMediaPlayer != null) {
			handler.removeCallbacks(updateThread);
			mMediaPlayer.release();
			mMediaPlayer = null;
		}

	}

	public void setSurfaceView() {

		DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
		// DisplayMetrics displayMetrics = new DisplayMetrics();
		// getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		double device = (double) displayMetrics.widthPixels
				/ displayMetrics.heightPixels;
		double video = (double) mVideoWidth / mVideoHeight;

		Log.i("displayMetrics", device + "|" + video);

		if (device > video) {

			lp.width = (int) (mVideoWidth * ((double) displayMetrics.heightPixels / mVideoHeight));
			lp.height = displayMetrics.heightPixels;

		} else {

			lp.width = displayMetrics.widthPixels;
			lp.height = (int) (mVideoHeight * ((double) displayMetrics.widthPixels / mVideoWidth));
		}

		lp.gravity = Gravity.CENTER;
		mSurfaceView.setLayoutParams(lp);
		// loadingBar.setVisibility(View.GONE);

	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {

		Log.i("onInfo", mp.getVideoWidth() + " | " + mp.getVideoHeight()
				+ " | " + what);

		if (mp.getVideoWidth() > 0 && mp.getVideoHeight() > 0
				&& mVideoWidth == 0 && mVideoHeight == 0) {

			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();
			setSurfaceView();
		}

		switch (what) {
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:

			// loadingBar.setVisibility(View.VISIBLE);

			break;

		case MediaPlayer.MEDIA_INFO_BUFFERING_END:

			// loadingBar.setVisibility(View.GONE);
			hoverImageView.setVisibility(View.GONE);

			break;
		}

		return false;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {

		Log.i("onBufferingUpdate",
				mp.getVideoWidth() + "|" + mp.getVideoHeight());

		if (mp.getVideoWidth() > 0 && mp.getVideoHeight() > 0
				&& mVideoWidth == 0 && mVideoHeight == 0) {

			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();

			setSurfaceView();
		}

	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

		Log.i("onVideoSizeChanged",
				mp.getVideoWidth() + "|" + mp.getVideoHeight() + "|" + width
						+ "|" + height);

		if (width > 0 && height > 0 && mVideoWidth == 0 && mVideoHeight == 0) {

			mVideoWidth = width;
			mVideoHeight = height;

			setSurfaceView();
		}

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.i("onCompletion", mp.getVideoWidth() + "|" + mp.getVideoHeight());
		finish();

	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

		switch (arg0.getId()) {
		case R.id.player_overlay_seek:
			mProgress = arg1;
			break;
		case R.id.player_overlay_brightness:

			setScreenBrightness(235 * arg1 / 15);
			brightness = 235 * arg1 / 15 + 20;
			break;
		case R.id.player_overlay_volume:

			mVolume = arg1;
			mAudioManager
					.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, 0);
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		isSeek = true;
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {

		switch (arg0.getId()) {
		case R.id.player_overlay_seek:

			if (mMediaPlayer == null)
				return;
			mMediaPlayer.seekTo(mProgress);

			break;

		}
		isSeek = false;
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

		switch (arg0.getId()) {
		case R.id.player_overlay_fix:

			mSurfaceView.setTranslationX(0);
			mSurfaceView.setTranslationY(0);

			if (mVideoWidth == 0)
				return;

			DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

			if (arg1) {

				// lp.width = FrameLayout.LayoutParams.MATCH_PARENT;
				// lp.height = FrameLayout.LayoutParams.MATCH_PARENT;
				lp.width = displayMetrics.widthPixels;
				lp.height = displayMetrics.heightPixels;

			} else {

				double device = (double) displayMetrics.widthPixels
						/ displayMetrics.heightPixels;
				double video = (double) mVideoWidth / mVideoHeight;

				Log.i("displayMetrics", device + "|" + video);

				if (device > video) {

					lp.width = (int) (mVideoWidth * ((double) displayMetrics.heightPixels / mVideoHeight));
					lp.height = displayMetrics.heightPixels;

				} else {

					lp.width = displayMetrics.widthPixels;
					lp.height = (int) (mVideoHeight * ((double) displayMetrics.widthPixels / mVideoWidth));
				}

			}

			lp.gravity = Gravity.CENTER;
			mSurfaceView.setLayoutParams(lp);

			break;

		case R.id.player_overlay_play:

			if (arg1) {

				if (mMediaPlayer != null) {
					mMediaPlayer.pause();
				}

			} else {

				playVideo();
			}

			break;
		}
	}

	/**
	 * 화면 모드(화면 회전 방식 조회)
	 * 
	 * @return
	 */
	private int getScreenMode() {

		int screenMode = 0;
		try {
			screenMode = System.getInt(getContentResolver(),
					System.SCREEN_BRIGHTNESS_MODE);
		} catch (Exception localException) {

		}
		return screenMode;
	}

	/**
	 * 화면 밝기 조회
	 * 
	 * @return
	 */
	private int getScreenBrightness() {

		int screenBrightness = 255;
		try {
			screenBrightness = System.getInt(getContentResolver(),
					System.SCREEN_BRIGHTNESS);
		} catch (Exception localException) {

		}
		return screenBrightness;
	}

	/**
	 * 버튼 클릭
	 */
	@Override
	public void onClick(View arg0) {

		int orient = getRequestedOrientation();

		switch (arg0.getId()) {

		case R.id.player_overlay_rotation:

			DisplayMetrics displayMetrics = this.getResources()
					.getDisplayMetrics();

			if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}

			break;

		case R.id.player_overlay_back:

			finish();
			break;

		case R.id.player_overlay_brightness_btn:

			if (seek_brightness.getVisibility() == View.VISIBLE) {

				seek_brightness.setVisibility(View.INVISIBLE);
			} else {
				Log.i("onClick", (brightness - 20) * 15 / 235 + "");
				seek_brightness.setProgress((brightness - 20) * 15 / 235);
				seek_brightness.setVisibility(View.VISIBLE);
			}

			break;

		case R.id.player_overlay_volume_btn:

			if (seek_volume.getVisibility() == View.VISIBLE) {

				seek_volume.setVisibility(View.INVISIBLE);
			} else {
				seek_volume.setProgress(mAudioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC));
				seek_volume.setVisibility(View.VISIBLE);
			}
			break;

		case R.id.player_overlay_prev:

			if (mMediaPlayer == null)
				return;

			mMediaPlayer
					.seekTo(mMediaPlayer.getCurrentPosition() / 1000 * 1000 - 10000);

			break;

		case R.id.player_overlay_forward:

			if (mMediaPlayer == null)
				return;
			mMediaPlayer
					.seekTo(mMediaPlayer.getCurrentPosition() / 1000 * 1000 + 10000);

			break;
		case R.id.player_overlay_send:

			Log.i("asdf", "~~~~~~~~~~");

			if (mMediaPlayer == null)
				return;

			mMediaPlayer.pause();

			sendQuestionDialog();

			break;
		}

	}

	/**
	 * 경고창
	 */
	private void sendQuestionDialog() {

		View diaView = View.inflate(this, R.layout.dialog_send, null);

		final EditText username = (EditText) diaView
				.findViewById(R.id.dialog_username);
		final EditText decs = (EditText) diaView.findViewById(R.id.dialog_desc);

		final Dialog dialog = new Dialog(MainActivity.this, R.style.dialog);
		dialog.setCancelable(false);
		dialog.setContentView(diaView);
		dialog.show();

		diaView.findViewById(R.id.dialog_ok).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						if (username.getText().toString().equals("")
								|| decs.getText().toString().equals("")) {
							Toast.makeText(MainActivity.this, "입력하세요",
									Toast.LENGTH_SHORT).show();
							return;
						}

						new Thread(new Runnable() {

							@Override
							public void run() {

								URL url;
								try {
									url = new URL(
											"http://demo.movieupserver.com/board/putquestion.aspx");
									HttpURLConnection httpURLConnection = (HttpURLConnection) url
											.openConnection();

									httpURLConnection.setDoOutput(true);
									httpURLConnection.setRequestMethod("POST");
									httpURLConnection.setRequestProperty(
											"Charset", "UTF-8");
									httpURLConnection.setUseCaches(false);
									httpURLConnection
											.setRequestProperty("Content-Type",
													"application/x-www-form-urlencoded");

									httpURLConnection.connect();
									DataOutputStream out = new DataOutputStream(
											httpURLConnection.getOutputStream());
									String content = "username="
											+ URLEncoder.encode(username
													.getText().toString(),
													"UTF-8")
											+ "&desc="
											+ URLEncoder.encode(decs.getText()
													.toString(), "UTF-8");

									out.writeBytes(content);
									out.flush();
									out.close();

									InputStream inStream = httpURLConnection
											.getInputStream();
									inStream.close();
									dialog.dismiss();
									mHandler.sendEmptyMessage(1);
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
						}).start();
					}
				});

		diaView.findViewById(R.id.dialog_close).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						dialog.dismiss();
						mMediaPlayer.start();
					}
				});

	}

	/**
	 * 경고창
	 */
	private void showComfirmDialog() {

		View diaView = View.inflate(this, R.layout.dialog_view, null);
		((TextView) diaView.findViewById(R.id.content)).setText("Complete");
		final Dialog dialog = new Dialog(MainActivity.this, R.style.dialog);
		dialog.setCancelable(false);
		dialog.setContentView(diaView);
		dialog.show();

		diaView.findViewById(R.id.dialog_close).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						dialog.dismiss();
						mMediaPlayer.start();
					}
				});

	}

	/**
	 * 화면 회전 이벤트
	 */
	public void onConfigurationChanged(Configuration conf) {
		super.onConfigurationChanged(conf);

		mSurfaceView.setTranslationX(0);
		mSurfaceView.setTranslationY(0);

		if (mVideoWidth == 0)
			return;

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		double device = (double) displayMetrics.widthPixels
				/ displayMetrics.heightPixels;
		double video = (double) mVideoWidth / mVideoHeight;

		Log.i("displayMetrics", device + "|" + video);

		if (device > video) {

			lp.width = (int) (mVideoWidth * ((double) displayMetrics.heightPixels / mVideoHeight));
			lp.height = displayMetrics.heightPixels;

		} else {

			lp.width = displayMetrics.widthPixels;
			lp.height = (int) (mVideoHeight * ((double) displayMetrics.widthPixels / mVideoWidth));
		}

		lp.gravity = Gravity.CENTER;
		mSurfaceView.setLayoutParams(lp);

	}

	/**
	 * 미리세컨드 2 시/분/초
	 * 
	 * @param time
	 * @return
	 */
	public String ShowTime(int time) {

		time /= 1000;
		int second = time % 60;
		int minute = time / 60;
		int hour = minute / 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(updateThread);
		handler.removeCallbacks(showRunnable);

		if (mMediaPlayer != null) {
			mMediaPlayer.pause();
		}

		m_ec.Close();
	};

	@Override
	protected void onPause() {
		super.onPause();
		if (mMediaPlayer != null) {
			mMediaPlayer.pause();
		}

	}

}
