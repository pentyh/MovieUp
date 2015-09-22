package com.enj.common;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import android.os.Environment;

public class ENJValues {

	// SharedPreferences
	public static String PREF_NAME = "ENJ_PREF";

	// SharedPreferences Key
	public static String KEY_DOWNLOAD = "DOENLOAD";

	// 다운로드주
	public static final String PATH_ROOT = Environment
			.getExternalStorageDirectory() + "/MovieUp/";
	public static final String FAVORITES = "Favorites";
	public static final String PATH_FAVORITES = PATH_ROOT + FAVORITES + "/";

	// sheme
	public static final String SCHEME_ENJ = "enjp";
	public static final String SCHEME_ENJS = "enjps";

	/* Format */
	public static int Time_Zone = TimeZone.getDefault().getRawOffset();
	public static final SimpleDateFormat Formart_Date = new SimpleDateFormat(
			"yyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat Format_time = new SimpleDateFormat(
			"HH:mm:ss");
}
