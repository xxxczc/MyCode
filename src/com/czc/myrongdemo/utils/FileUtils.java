package com.czc.myrongdemo.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.czc.myrongdemo.Api;
import com.czc.myrongdemo.bean.MessageBean;
import com.google.gson.Gson;

public class FileUtils {

	/**
	 * 下载某一个小时内的所有用户的聊天历史记录
	 * 
	 * @param messageUri
	 *            post请求的uri地址
	 * @param date
	 *            表单参数:时间要下载的哪一个小时的聊天记录
	 */
	public static synchronized void downLoadHistoryMessage(
			final String messageUri, final String mdate) {

		MLog.i("downLoadHistoryMessage------------");
//		ThreadPoolManager.getInstance().getNetThreadPool()
//				.execute(new Runnable() {
//					@Override
//					public void run() {
//						
//					};
//				});
		
		try {
			String date = "date=" + mdate;
			HttpURLConnection postHttpConnection = HttpUtil
					.createPostHttpConnection("82hegw5uhlxox",
							"1jcL68gBen5FY", messageUri);
			OutputStream outputStream = postHttpConnection
					.getOutputStream();
			outputStream.write(date.getBytes("UTF-8"));
			outputStream.flush();
			outputStream.close();

			// postHttpConnection.connect();
			int code = postHttpConnection.getResponseCode();
			Log.i("czc", code + "******");
			if (code == 200) {
				InputStream inputStream = postHttpConnection
						.getInputStream();
				byte[] readInputStream = HttpUtil
						.readInputStream(inputStream);

				String messageJson = new String(readInputStream);
				parseMessageJson(messageJson);

			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析返回的下载地址所在的json字符串
	 * 
	 * @param messageJson
	 *            融云返回的历史记录下载地址json
	 */
	private static void parseMessageJson(String messageJson) {
		Gson gson = new Gson();
		MessageBean messageBean = gson.fromJson(messageJson, MessageBean.class);
		final String messageUrl = messageBean.getUrl();
		final String date = messageBean.getDate();

		// 每执行一次downLoadHistroyMessage方法日期就减去一小时

		Api.mdate = date;
		// 判断获取的messageUrl 为空的话,改变date,请求融云获得messagejson数据
		if (TextUtils.isEmpty(messageUrl)) {
			MLog.d("messageUrl:::" + messageUrl.toString());
			FileUtils.downLoadHistoryMessage(Api.HISTORYMESSAGEURI,
					DateUtils.dateMinus(date));
			return;
		}
		FileUtils.downLoadFile(messageUrl, Environment
				.getExternalStorageDirectory().getPath()
				+ "/czc"
				+ date
				+ ".zip");
	};

	public static synchronized void downLoadFile(final String messageUrl,
			final String path) {

		// ThreadPoolManager.getInstance().getNetThreadPool()
		// .execute(new Runnable() {
		//
		// @Override
		// public void run() {
		//
		//
		// }
		// });

		try {

			URL url = new URL(messageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);

			int code = conn.getResponseCode();
			if (code == 200) {
				// 请求成功
				InputStream is = conn.getInputStream();
				File file = new File(path);
				OutputStream os = new FileOutputStream(file);
				int len = 0;
				byte[] bys = new byte[1024];
				while ((len = is.read(bys)) != -1) {
					os.write(bys, 0, len);
				}
				os.close();
				is.close();

				MLog.i("下载成功");
				ZipUtils.getInstance().unZipFiles(path, Api.MSGSDDIR);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void readLogfileToJsonfile(final String filepath) {
		// ThreadPoolManager.getInstance().getFileThreadPool()
		// .execute(new Runnable() {
		// @Override
		// public void run() {
		try {
			File file = new File(filepath);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					filepath + ".json")));

			while (true) {
				String readLine = reader.readLine();
				if (readLine != null) {
					String jsonString = readLine.substring(readLine
							.indexOf("{"));
					writer.write(jsonString.toCharArray());
					writer.newLine();
				} else {
					break;
				}
			}
			reader.close();
			writer.flush();
			writer.close();
			file.delete();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// };
		// });
	}

}
