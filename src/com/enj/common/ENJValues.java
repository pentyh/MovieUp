package com.enj.common;

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
}
