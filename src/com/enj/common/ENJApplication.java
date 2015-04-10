package com.enj.common;

import java.util.Locale;
import java.util.Stack;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

public class ENJApplication extends Application {

	public static ENJApplication mApplication;
	public static Context mContext;

	private byte mVersion = 0x00;
	public static Resources mResources;
	public static Stack<Activity> mStack;

	public static String SOCKET_IP;

	public static String PW = null;

	public static Bitmap BG = null;
	public static Bitmap IMAGE = null;
	public static String NAME;
	public static String POSITION;
	public static String COM;

	public static long Login_Time_Old = 0;

	public static boolean LOCK = false;

	public static long mMillis = 0;

	public ENJApplication() {
		mApplication = this;
	}

	public void init() {
		mContext = getApplicationContext();
		setLocale();
		if (mStack == null) {
			mStack = new Stack<Activity>();
		}

	}

	public static byte getVersion() {
		return mApplication.mVersion;
	}

	public static byte getVersion2() {
		// DisplayMetrics.
		return mApplication.mVersion;
	}

	public static Activity getCurrentActivity() {

		Activity activity = null;
		if (!mStack.isEmpty()) {
			return mStack.lastElement();
		}
		return activity;
	}

	public static void pushActivity(Activity activity) {
		if (!mStack.contains(activity)) {
			mStack.push(activity);
		}
	}

	public static void removeActivity(Activity activity) {
		if (mStack.contains(activity) == false) {
			return;
		}
		mStack.pop();
	}

	public static void moveActivity(int pos) {
		int size = mStack.size();
		for (int i = pos; i < size; i++) {
			mStack.pop().finish();
		}
	}

	public static Context getContext() {
		if (mContext == null) {
			mContext = mApplication.getApplicationContext();
		}
		return mContext;
	}

	/**
	 * 로케일 설정
	 * 
	 * @param val
	 *            국가
	 */
	public static void setLocale() {

		Context context = ENJApplication.getContext();
		Resources standardResources = context.getResources();
		AssetManager assets = standardResources.getAssets();
		DisplayMetrics metrics = standardResources.getDisplayMetrics();
		Configuration config = new Configuration(
				standardResources.getConfiguration());

		config.locale = Locale.KOREAN;

		mResources = null;
		mResources = new Resources(assets, metrics, config);
	}

	/**
	 * 로케일 리턴
	 * 
	 * @return
	 */
	public static Resources getLocale() {
		return mResources;
	}
}
