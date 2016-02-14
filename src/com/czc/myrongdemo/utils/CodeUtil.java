package com.czc.myrongdemo.utils;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class CodeUtil {

	public static String hexSHA1(String value) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(value.getBytes("utf-8"));
			byte[] digest = md.digest();
			return byteToHexString(digest);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String byteToHexString(byte[] bytes) {
		return String.valueOf(Hex.encodeHex(bytes));
	}
	
	

	public static String bitMap2Base64(String path) {
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();

		bitmap.compress(CompressFormat.PNG, 100, bStream);

		byte[] bytes = bStream.toByteArray();

		String bitmap2Base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
		
		return bitmap2Base64;
	}

}
