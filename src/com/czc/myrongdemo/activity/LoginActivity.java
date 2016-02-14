package com.czc.myrongdemo.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.czc.myrongdemo.Api;
import com.czc.myrongdemo.R;
import com.czc.myrongdemo.bean.TeacherBean;
import com.czc.myrongdemo.utils.HttpUtil;
import com.czc.myrongdemo.utils.LogInUtils;
import com.czc.myrongdemo.utils.MLog;
import com.czc.myrongdemo.utils.ThreadPoolManager;
import com.czc.myrongdemo.utils.thread.ThreadWithDialogListener;
import com.czc.myrongdemo.utils.thread.ThreadWithDialogTask;
import com.czc.myrongdemo.view.LoadingDialog;
import com.czc.myrongdemo.view.RoundImageView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class LoginActivity extends Activity implements OnClickListener,
		Handler.Callback {
	private static final int HANDLER_LOGIN_SUCCESS = 1;
	private static final int HANDLER_LOGIN_FAILURE = 2;
	private static final int HANDLER_LOGIN_ERROR = 3;
	private EditText etUserName;
	private EditText etPassWord;
	private Button btnSignIn;
	private LoadingDialog mLoadingDialog;
	private SharedPreferences sp;
	private RoundImageView ivHeadImage;

	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private Handler mHandler;
	private ThreadWithDialogTask task;
	
	private  int LOGIN_STAT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		task = new ThreadWithDialogTask();
		initView();
		initData();
	}

	private void initView() {
		etUserName = (EditText) this.findViewById(R.id.app_username_et);
		etPassWord = (EditText) this.findViewById(R.id.app_password_et);
		btnSignIn = (Button) this.findViewById(R.id.app_sign_in_bt);
		btnSignIn.setOnClickListener(this);
		mLoadingDialog = new LoadingDialog(this);
		mHandler = new Handler(this);
		ivHeadImage = (RoundImageView) this.findViewById(R.id.iv_head_image);
		imageLoader = ImageLoader.getInstance();
//		imageLoader.init(ImageLoaderConfiguration.createDefault(App.context));
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.pictures_no)
				.showImageOnFail(R.drawable.pictures_no)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		
		sp = this.getSharedPreferences(Api.SPNAME, MODE_PRIVATE);

	}

	private void initData() {
		String mUserName = sp.getString("mUserName", "");
		String mPassWord = sp.getString("mPassWord", "");
		String headImageUri = sp.getString("headImageUri", "");
		etUserName.setText(mUserName);
		etPassWord.setText(mPassWord);
		if(!TextUtils.isEmpty(headImageUri)){
			String headImageName = headImageUri.substring(headImageUri
					.lastIndexOf(File.separator) + 1);
			imageLoader.displayImage("file:///" + Api.HISTORYMESSAGEDIR
					+ File.separator + headImageName, ivHeadImage, options);
		}
		
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.app_sign_in_bt) {

			final String mUserName = etUserName.getText().toString().trim();
			final String mPassWord = etPassWord.getText().toString().trim();
			MLog.d(mUserName + "******" + mPassWord);
			if (TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mPassWord)) {
				Toast.makeText(getApplicationContext(), "用户名或者密码不能为空", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			if (!mLoadingDialog.isShowing()) {
				mLoadingDialog.show();
			}

			httpClienLogin(mUserName, mPassWord, Api.USERTYPE);

			// logIn(mUserName, mPassWord);
		}
	}

	private void httpClienLogin(final String mUserName,
			final String mPassWord, final String mUserType) {
		
		task.RunWithoutDialog(this, new ThreadWithDialogListener() {
			private TeacherBean teacherBean;
			@Override
			public boolean TaskMain() {
				MLog.d("mUserName:"+mUserName+";mPassWord::"+mPassWord+";mUserType::"+mUserType);
				
				teacherBean = LogInUtils.logInAndGetBean(mUserName, mPassWord, mUserType);
				MLog.d(teacherBean.status+"&&&&&&&&&&");
				return true;
			}
			
			@Override
			public boolean OnTaskDone() {
				
				MLog.d("teacherBean;;;;;;"+teacherBean.toString());
				if (teacherBean != null) {
					
					if(teacherBean.status == null){
						LOGIN_STAT = HANDLER_LOGIN_SUCCESS;
						handleLoginStat(LOGIN_STAT);
						sp.edit().putBoolean("isLogin", true)
						.commit();
						sp.edit().putBoolean("isFirstLogIn", false)
						.commit();
						
					}else if (teacherBean.status.equals("success")) {
						sp.edit().putString("mUserName", mUserName)
								.commit();
						sp.edit().putString("mPassWord", mPassWord)
								.commit();
						sp.edit()
								.putString("token",
										teacherBean.data.token)
								.commit();
						sp.edit()
								.putString("headImageUri",
										teacherBean.data.headImg)
								.commit();

						sp.edit().putBoolean("isFirstLogIn", false)
								.commit();
						sp.edit().putBoolean("isLogin", true)
								.commit();
						LOGIN_STAT = HANDLER_LOGIN_SUCCESS;
						handleLoginStat(LOGIN_STAT);
					}else if (teacherBean.status.equals("error")) {
//						mHandler.obtainMessage(HANDLER_LOGIN_ERROR)
//								.sendToTarget();
						LOGIN_STAT = HANDLER_LOGIN_SUCCESS;
						handleLoginStat(LOGIN_STAT);
						sp.edit().putBoolean("isLogin", true)
						.commit();
						sp.edit().putBoolean("isFirstLogIn", false)
						.commit();
						
					}
					
					
				}
				return true;
			}
			
			

			@Override
			public boolean OnTaskDismissed() {
				// TODO Auto-generated method stub
				return true;
			}
		}, true);

	}

	
	private void handleLoginStat(int LOGIN_STAT) {
		if (LOGIN_STAT == HANDLER_LOGIN_SUCCESS) {
			if (mLoadingDialog != null)
				mLoadingDialog.dismiss();
			Toast.makeText(this, etUserName.getText()+"登陆成功", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(getApplicationContext(),
					MainActivity.class));
			finish();

		} else if (LOGIN_STAT == HANDLER_LOGIN_FAILURE) {
			if (mLoadingDialog != null)
				mLoadingDialog.dismiss();
			Toast.makeText(getApplicationContext(), "服务器异常", Toast.LENGTH_SHORT).show();
		} else if (LOGIN_STAT == HANDLER_LOGIN_ERROR) {

			MLog.d("HANDLER_LOGIN_ERROR");

			if (mLoadingDialog != null) {
				mLoadingDialog.dismiss();
			}
		}

		
	}
	
	
	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == HANDLER_LOGIN_SUCCESS) {
			if (mLoadingDialog != null)
				mLoadingDialog.dismiss();
			Toast.makeText(this, etUserName.getText()+"登陆成功", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(getApplicationContext(),
					MainActivity.class));
			finish();

		} else if (msg.what == HANDLER_LOGIN_FAILURE) {
			if (mLoadingDialog != null)
				mLoadingDialog.dismiss();
			Toast.makeText(getApplicationContext(), "服务器异常", Toast.LENGTH_SHORT).show();
		} else if (msg.what == HANDLER_LOGIN_ERROR) {

			MLog.d("HANDLER_LOGIN_ERROR");

			if (mLoadingDialog != null) {
				mLoadingDialog.dismiss();
			}
		}

		return false;
	}
	
	
	
	
	@Deprecated
	private void logIn(final String mUserName, final String mPassWord) {
		// 联网请求服务器
		ThreadPoolManager.getInstance().getNetThreadPool()
				.execute(new Runnable() {

					@Override
					public void run() {
						HashMap<String, Object> requestParamsMap = new HashMap<String, Object>();
						requestParamsMap.put("account", mUserName);
						requestParamsMap.put("password", mPassWord);
						requestParamsMap.put("userType", Api.USERTYPE);
						StringBuffer params = new StringBuffer();
						organizParams(requestParamsMap, params);

						try {
							HttpURLConnection conn = HttpUtil
									.createPostHttpConnection(Api.LOGINURI);
							conn.setDoInput(true);
							conn.setDoOutput(true);
							OutputStream os = conn.getOutputStream();
							os.write(params.toString().getBytes());
							os.flush();
							os.close();

							if (conn.getResponseCode() == 200) {

								InputStream is = conn.getInputStream();
								String json = new String(HttpUtil
										.readInputStream(is));

								MLog.d(json);

								Gson gson = new Gson();
								TeacherBean teacherBean = gson.fromJson(json,
										TeacherBean.class);

								MLog.d("teacherBean.status:::"
										+ teacherBean.status);
								if (teacherBean.status.equals("success")) {
									sp.edit().putString("mUserName", mUserName)
											.commit();
									sp.edit().putString("mPassWord", mPassWord)
											.commit();
									sp.edit()
											.putString("token",
													teacherBean.data.token)
											.commit();
									sp.edit().putBoolean("isLogIn", true);
									mHandler.obtainMessage(
											HANDLER_LOGIN_SUCCESS)
											.sendToTarget();
								}

								if (teacherBean.status.equals("error")) {
									mHandler.obtainMessage(HANDLER_LOGIN_ERROR)
											.sendToTarget();
								}

							}
						} catch (IOException e) {
							mHandler.obtainMessage(HANDLER_LOGIN_FAILURE)
									.sendToTarget();
							e.printStackTrace();
						} catch (Exception e) {
							mHandler.obtainMessage(HANDLER_LOGIN_FAILURE)
									.sendToTarget();
							e.printStackTrace();
						}
					}

				});
	}
	
	@Deprecated
	private void organizParams(HashMap<String, Object> requestParamsMap,
			StringBuffer params) {
		// 阻止请求参数
		@SuppressWarnings("rawtypes")
		Iterator it = requestParamsMap.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry element = (Map.Entry) it.next();
			params.append(element.getKey());
			params.append("=");
			params.append(element.getValue());
			params.append("&");
		}
		if (params.length() > 0) {
			params.deleteCharAt(params.length() - 1);
		}

		MLog.d(params.toString());
	}

}
