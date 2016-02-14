package com.czc.myrongdemo.utils;

import java.util.List;

import android.text.TextUtils;
import android.util.Log;

public class MLog {
	/** 日志输出级别NONE */

	public static final int LEVEL_NONE = 0;

	/** 日志输出级别V */

	public static final int LEVEL_VERBOSE = 1;

	/** 日志输出级别D */

	public static final int LEVEL_DEBUG = 2;

	/** 日志输出级别I */

	public static final int LEVEL_INFO = 3;

	/** 日志输出级别W */

	public static final int LEVEL_WARN = 4;

	/** 日志输出级别E */

	public static final int LEVEL_ERROR = 5;

	/** 日志输出时的TAG */

	private static String mTag = "czc";

	/** 当前级别：是否允许输出log */

	private static int mCurrentLevel = LEVEL_ERROR;
	// private static int mCurrentLevel = LEVEL_NONE;

	/** 用于记时的变量 */

	private static long mTimestamp = 0;

	/** 以级别为 d 的形式输出LOG */

	public static void v(String msg) {
		// 5 >=1
		if (mCurrentLevel >= LEVEL_VERBOSE) {

			Log.v(mTag, msg);

		}

	}

	/** 以级别为 d 的形式输出LOG */

	public static void d(String msg) {

		if (mCurrentLevel >= LEVEL_DEBUG) {

			Log.d(mTag, msg);

		}

	}

	/** 以级别为 i 的形式输出LOG */

	public static void i(String msg) {
		// 5>=3
		if (mCurrentLevel >= LEVEL_INFO) {

			Log.i(mTag, msg);

		}

	}

	/** 以级别为 w 的形式输出LOG */

	public static void w(String msg) {

		if (mCurrentLevel >= LEVEL_WARN) {

			Log.w(mTag, msg);

		}

	}

	/** 以级别为 w 的形式输出Throwable */

	public static void w(Throwable tr) {

		if (mCurrentLevel >= LEVEL_WARN) {

			Log.w(mTag, "", tr);

		}

	}

	/** 以级别为 w 的形式输出LOG信息和Throwable */

	public static void w(String msg, Throwable tr) {

		if (mCurrentLevel >= LEVEL_WARN && null != msg) {

			Log.w(mTag, msg, tr);

		}

	}

	/** 以级别为 e 的形式输出LOG */

	public static void e(String msg) {

		if (mCurrentLevel >= LEVEL_ERROR) {

			Log.e(mTag, msg);

		}

	}

	/** 以级别为 e 的形式输出Throwable */

	public static void e(Throwable tr) {

		if (mCurrentLevel >= LEVEL_ERROR) {

			Log.e(mTag, "", tr);

		}

	}

	/** 以级别为 e 的形式输出LOG信息和Throwable */

	public static void e(String msg, Throwable tr) {

		if (mCurrentLevel >= LEVEL_ERROR && null != msg) {

			Log.e(mTag, msg, tr);

		}

	}

	/**
	 * 
	 * 以级别为 e 的形式输出msg信息,附带时间戳，用于输出一个时间段起始点
	 * 
	 * @param msg
	 *            需要输出的msg
	 */

	public static void msgStartTime(String msg) {

		mTimestamp = System.currentTimeMillis();

		if (!TextUtils.isEmpty(msg)) {

			e("[Started：" + mTimestamp + "]" + msg);

		}

	}

	/** 以级别为 e 的形式输出msg信息,附带时间戳，用于输出一个时间段结束点* @param msg 需要输出的msg */

	public static void elapsed(String msg) {

		long currentTime = System.currentTimeMillis();

		long elapsedTime = currentTime - mTimestamp;

		mTimestamp = currentTime;

		e("[Elapsed：" + elapsedTime + "]" + msg);

	}

	public static <T> void printList(List<T> list) {

		if (list == null || list.size() < 1) {

			return;

		}

		int size = list.size();

		i("---begin---");

		for (int i = 0; i < size; i++) {

			i(i + ":" + list.get(i).toString());

		}

		i("---end---");

	}

	public static <T> void printArray(T[] array) {

		if (array == null || array.length < 1) {

			return;

		}

		int length = array.length;

		i("---begin---");

		for (int i = 0; i < length; i++) {

			i(i + ":" + array[i].toString());

		}

		i("---end---");

	}

	public static void d(String mTag, String msg) {
		if (mCurrentLevel >= LEVEL_DEBUG) {
			Log.d(mTag, msg);
		}
	}
}