package com.enj.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * HMIS Preference
 * 
 * @author Enjsoft
 * 
 */
public class ENJPrefernece {

	private static final String TAG = ENJPrefernece.class.toString();

	// public static String getPreferenceString(){
	// String pref="";
	//
	// HIMSMenuItem item =
	// TGApplication.getSelectMenu(HIMSMenuControl.MENU_1ST);
	// pref = String.format("%d", item.getID());
	//
	// item = TGApplication.getSelectMenu(HIMSMenuControl.MENU_2ND);
	// if(item == null) {
	// return "";
	// }
	// pref += String.format("%d", item.getID());
	//
	// item = TGApplication.getSelectMenu(HIMSMenuControl.MENU_3RD);
	// if(item == null) {
	// return "";
	// }
	// pref += String.format("%d", item.getID());
	//
	// return pref;
	// }

	/**
	 * Preference
	 * 
	 * @param key
	 * @param value
	 */
	public static void save(String key, String value) {
		Log.i(TAG, "save pref = " + key + " val = " + value);
		SharedPreferences prefs = ENJApplication
				.getContext()
				.getSharedPreferences(ENJValues.PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * Preference
	 * 
	 * @param key
	 * @param value
	 */
	public static void save(String key, byte[] value) {
		Log.i(TAG, "save pref = " + key + " val = " + value);
		SharedPreferences prefs = ENJApplication
				.getContext()
				.getSharedPreferences(ENJValues.PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, new String(value));
		editor.commit();
	}

	/**
	 * Preference
	 * 
	 * @param key
	 * @param value
	 */
	public static void save(String key, int value) {
		Log.i(TAG, "save pref = " + key + " val = " + value);
		SharedPreferences prefs = ENJApplication
				.getContext()
				.getSharedPreferences(ENJValues.PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * Preference boolean
	 * 
	 * @param key
	 * @param value
	 */
	public static void save(String key, boolean value) {
		SharedPreferences prefs = ENJApplication
				.getContext()
				.getSharedPreferences(ENJValues.PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * Preference
	 * 
	 * @param key
	 * @param value
	 */
	public static String load(String key, String defValue) {
		SharedPreferences prefs = ENJApplication
				.getContext()
				.getSharedPreferences(ENJValues.PREF_NAME, Context.MODE_PRIVATE);

		String load = prefs.getString(key, defValue);
		Log.i(TAG, "load pref = " + key + " val = " + load);
		return load;
	}

	/**
	 * Preference
	 * 
	 * @param key
	 * @param value
	 */
	public static int load(String key, int defValue) {
		SharedPreferences prefs = ENJApplication
				.getContext()
				.getSharedPreferences(ENJValues.PREF_NAME, Context.MODE_PRIVATE);

		int load = prefs.getInt(key, defValue);
		Log.i(TAG, "load pref = " + key + " val = " + load);
		return prefs.getInt(key, defValue);
	}

	/**
	 * Preference boolean
	 * 
	 * @param key
	 * @param value
	 */
	public static boolean load(String key, boolean defValue) {
		SharedPreferences prefs = ENJApplication
				.getContext()
				.getSharedPreferences(ENJValues.PREF_NAME, Context.MODE_PRIVATE);
		return prefs.getBoolean(key, defValue);
	}

	public static byte[] load(String key) {
		SharedPreferences prefs = ENJApplication
				.getContext()
				.getSharedPreferences(ENJValues.PREF_NAME, Context.MODE_PRIVATE);
		String str = prefs.getString(key, "");
		if (str.isEmpty()) {
			return null;
		}
		return str.getBytes();
	}
}
