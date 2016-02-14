package com.czc.myrongdemo.utils.thread;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.czc.myrongdemo.R;

/**
 * ThreadWithDialogTask �?ProgressDialog线程任务管理类： 功能�?前台UI显示ProgressDialog�?
 * 后台启动线程完成指定工作,并根据任务完�?用户取消两个情况回调对应方法 其中‘指定工作�?�?
 * 两个‘回调方法�?均�?过实现ThreadWithDialogListener接口来定义�? �?��，需以ThreadWithDialogTask
 * interface配合使用
 * 
 * 使用方法 1. 通过继承 ThreadWithDialogTask来定�?a. �?��在线程中完成的工作，即TaskMain方法 b.
 * 两个回调方法�?任务被成功执行的响应�?OnTaskDone 任务被用户被取消的响�?�?OnTaskDismissed 例子�?class MyTask
 * implements ProgressDialogThreadTask { boolean TaskMain() { �?��耗时的工�?} //定义任务
 * 
 * public boolean OnTaskDone() { //在此处理如果任务 return true; }
 * 
 * public boolean OnTaskDismissed() { //在此处理如果任务被取�?return true; } }
 * 
 * 2. 新建�?��ProgressDialog实例�?ThreadWithDialogTask myPDT = new
 * ThreadWithDialogTask();
 * 
 * 3. 启动任务 myPDT.Run(context, //正确的上下文 new MyTask(), //要执行的任务和响�?
 * "�?��耗时的工作正在进行，请稍�?.." //进度框上显示的消�?)
 * 
 * 
 * �?��具体的使用例子：
 * 
 * class MyActivity extends Activity implements ProgressDialogThreadTask {
 * ThreadWithDialogTask myPDT = new ThreadWithDialogTask;
 * //定义ProgressDialog线程任务管理类实�?
 * 
 * protected void onCreate(Bundle savedInstanceState) { { //显示ProgressDialog�?
 * 启动线程任务 myPTD.Run(this, //这里上下文是MyActivity自己 this,
 * //要执行的任务和响应也是MyActivity自己实现�?"�?��耗时的工作正在进行，请稍�?." ); }
 * 
 * boolean TaskMain() { //子线程执行�?时的任务}
 * 
 * public boolean OnTaskDone() { //在此处理如果任务 return true; }
 * 
 * public boolean OnTaskDismissed() { //在此处理如果任务被取�?return true; } }
 * 
 * @author born
 * 
 */
public class ThreadWithDialogTask {
	
	/**
	 * 
	 * @param activity
	 *            : 当前活动的activity （android有的activity为非活动，需要取其getParent)
	 * @param listener
	 *            : 其中定义了需要完成的任务
	 * @param msg
	 *            �?在进度对话框中显示的消息
	 */
	public boolean RunWithMsg(Activity activity, ThreadWithDialogListener listener,
			String msg) {
		Log.d("ThreadWithDialogTask", "ThreadWithDialogTask.Run...");

		try {
			
			ProgressDialog dialog = new ProgressDialog(activity, 
					R.style.dialog, R.layout.progress_dialog,
					R.anim.rotate_refresh, 0, R.drawable.refresh, 0,
					msg, false);
			dialog.show();
			ProgressThread thread = new ProgressThread(listener, dialog);
			dialog.setOnDismissListener(thread);
			dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消�?
		
			thread.start();
			

		} catch (Exception e) {
			Log.d("ThreadWithDialogTask",
					"!!!!! ThreadWithDialogTask - Error exception : "
							+ e.toString());
			e.printStackTrace();

		}

		return true;
	}
	
	public boolean RunWithMsg(Activity activity, ThreadWithDialogListener listener,
			int R_Id) {
		return RunWithMsg(activity, listener, activity.getResources().getString(R_Id));
	}
	
	/**
	 * 
	 * @param activity
	 *            : 当前活动的activity （android有的activity为非活动，需要取其getParent)
	 * @param listener
	 *            : 其中定义了需要完成的任务
	 */
	public boolean RunWithDialog(Activity activity, ThreadWithDialogListener listener) {
		Log.d("ThreadWithDialogTask", "ThreadWithDialogTask.Run...");

		try {			
			ProgressDialog dialog = new ProgressDialog(activity, 
					R.style.dialog, R.layout.progress_dialog,
					R.anim.rotate_refresh, 0, R.drawable.refresh, 0,
					"", false);
			ProgressThread thread = new ProgressThread(listener, dialog);
			dialog.setOnDismissListener(thread);
			dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消�?
			dialog.show();
			thread.start();

		} catch (Exception e) {
			Log.d("ThreadWithDialogTask",
					"!!!!! ThreadWithDialogTask - Error exception : "
							+ e.toString());
			e.printStackTrace();

		}

		return true;
	}
	
	/**
	 * 
	 * @param activity
	 *            : 当前活动的context （android有的activity为非活动，需要取其getParent)
	 * @param listener
	 *            : 其中定义了需要完成的任务
	 * @return : boolean
	 * 				:activity为非活动时，是否强制执行主线程方�?	 */
	public boolean RunWithoutDialog(Activity activity, ThreadWithDialogListener listener, boolean flag) {
		Log.d("ProgressDialogThread", "ProgressDialogThread.Run...");

		try {			
			ProgressThread thread = new ProgressThread(listener, activity, flag);
			thread.start();

		} catch (Exception e) {
			Log.d("ProgressDialogThread",
					"!!!!! ProgressDialogThread - Error exception : "
							+ e.toString());
			e.printStackTrace();

		}

		return true;
	}

	/**
	 * 启动线程跑ThreadWithDialogListener.TaskMain的管理类�?
	 * 
	 * @author born
	 * 
	 */
	public static class ProgressThread extends Thread implements
			OnDismissListener {

		boolean bTaskOk = false;
		ThreadWithDialogListener pListener;
		//带提示框
		ProgressDialog pDialog = null;
		
		//不带提示
		Handler pHandler = null;
		Activity pActivity = null;
		//是否强制执行主线程方�?		
		boolean pFlag = false;
		
		ProgressThread(ThreadWithDialogListener myListener, ProgressDialog myDialog) {
			pListener = myListener;
			pDialog = myDialog;
			
			pHandler = null;
			pActivity = null;
			pFlag = false;
		}
		
		ProgressThread(ThreadWithDialogListener myListener, Activity activity, boolean flag) {
			pListener = myListener;
			pDialog = null;
			
			pHandler = new Handler();
			pActivity = activity;
			pFlag = flag;
		}

		public void run() {

			Log.d("ThreadWithDialogTask", "ProgressThread.run...");

			Looper.prepare();
			bTaskOk = pListener.TaskMain();
			if (pDialog != null && pDialog.isShowing()) {
				pDialog.dismiss();
			}else if(pHandler != null){
				if((pActivity != null && !pActivity.isFinishing()) || pFlag){
					pHandler.post(new Runnable() {
						@Override
						public void run() {
							if (!bTaskOk) {
								pListener.OnTaskDismissed();
							} else {
								pListener.OnTaskDone();
							}
						}
					});
				}
			}

			Looper.loop();
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			// 线程处理完毕进来这里，在这里处理你要do的事
			if (bTaskOk) {
				pListener.OnTaskDone();

			} else {
				pListener.OnTaskDismissed();
			}
		}
	}
}
