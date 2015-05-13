package com.enj.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.enj.common.ENJApplication;

import android.util.Log;
import android.widget.Toast;

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

	public static void writeFile(String path, String json) {

		try {
			File file = new File(path);
			FileOutputStream fos = new FileOutputStream(file);

			byte[] b = json.getBytes();
			fos.write(b);
			fos.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public static void toast(String msg) {

		Toast.makeText(ENJApplication.getContext(), msg, Toast.LENGTH_SHORT)
				.show();
	}

	public static void alert(String msg) {

		Log.i("JRL Test---", msg);
	}
}
