package com.enj.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.util.Log;

public class ENJUtils {

	public static String getString(File file) {

		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}

		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(inputStream, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		BufferedReader reader = new BufferedReader(inputStreamReader);
		StringBuffer sb = new StringBuffer("");
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				// sb.append("\n");
			}

			reader.close();
			inputStreamReader.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public static void alert(String msg) {

		Log.i("JRL Test---", msg);
	}
}
