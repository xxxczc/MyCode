package com.czc.myrongdemo;

import io.rong.imkit.RongIM;

import org.litepal.LitePalApplication;
import org.litepal.tablemanager.Connector;

import android.app.ActivityManager;
import android.content.Context;

import com.czc.myrongdemo.utils.FaceConversionUtil;
import com.czc.myrongdemo.utils.MLog;
import com.czc.myrongdemo.utils.ThreadPoolManager;

public class App extends LitePalApplication {

	public static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		MLog.d("AppOncreat");
		context = getApplicationContext();
		
		Connector.getDatabase();
		
		if (getApplicationInfo().packageName
				.equals(getCurProcessName(getApplicationContext()))
				|| "io.rong.push"
						.equals(getCurProcessName(getApplicationContext()))) {

			 RongIM.init(getApplicationContext());
			 RongCloudEvent.init(this);

		}

		ThreadPoolManager.getInstance().getFileThreadPool()
				.execute(new Runnable() {

					@Override
					public void run() {
						MLog.e("获取表情数据");
						FaceConversionUtil.getInstace().getFileText(
								App.this);

					}
				});
	}

	public static Object getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
				.getRunningAppProcesses()) {

			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

}
