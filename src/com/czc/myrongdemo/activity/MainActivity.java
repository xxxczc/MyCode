package com.czc.myrongdemo.activity;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation.ConversationType;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.litepal.crud.DataSupport;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.czc.myrongdemo.Api;
import com.czc.myrongdemo.App;
import com.czc.myrongdemo.R;
import com.czc.myrongdemo.RongCloudEvent;
import com.czc.myrongdemo.bean.MsgInfor;
import com.czc.myrongdemo.bean.TeacherBean;
import com.czc.myrongdemo.utils.HttpUtil;
import com.czc.myrongdemo.utils.LogInUtils;
import com.czc.myrongdemo.utils.MLog;
import com.czc.myrongdemo.utils.ThreadPoolManager;
import com.czc.myrongdemo.utils.thread.ThreadWithDialogListener;
import com.czc.myrongdemo.utils.thread.ThreadWithDialogTask;
import com.google.gson.Gson;
//first commit

//second commit

public class MainActivity extends ActionBarActivity implements OnClickListener {
	
	//'czc2'的token
	private final String token = "L7ZoysKumvi5F62RcYG8tCOhTo+32Dy51Uk+XH63bC5iPf+UmHjD7D+xO//X0e2dISM3yvYR/GCAU1r0d9gtrQ==";
	//czc1的token
//	private final String token = "dBNEwb4GffUnZjCibMF7uSOhTo+32Dy51Uk+XH63bC7BKlkhK8cLGuvgixfDbI9mISM3yvYR/GBKk3aYE4B6fQ==";
	private SharedPreferences sp;
	private ThreadWithDialogTask task;
	public static final String ACTION_DMEO_RECEIVE_MESSAGE = "action_demo_receive_message";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		connect(token);
		sp = getSharedPreferences(Api.SPNAME, MODE_PRIVATE);
		task =new ThreadWithDialogTask();
		MLog.d("isLogin::"+sp.getBoolean("isLogin", false));
		if (!sp.getBoolean("isLogin", false)) {
			task.RunWithoutDialog(this, new AutoLogin(), true);
		}
		this.findViewById(R.id.connect_rong).setClickable(false);
		this.findViewById(android.R.id.button1).setOnClickListener(this);
		this.findViewById(android.R.id.button2).setOnClickListener(this);
		this.findViewById(R.id.testSessionId).setOnClickListener(this);
		this.findViewById(R.id.getHistoryMsg).setOnClickListener(this);

	}
	
	private void connect(String token) {

	    if (getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {

	        /**
	         * IMKit SDK调用第二步,建立与服务器的连接
	         */
	    	
	    	MLog.d("connect::::"+token);
	    	
	        RongIM.connect(token, new RongIMClient.ConnectCallback() {

	            /**
	             * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
	             */
	            @Override
	            public void onTokenIncorrect() {
	            	Toast.makeText(MainActivity.this, "链接错误", 0).show();
	                MLog.d( "--onTokenIncorrect");
	            }

	            /**
	             * 连接融云成功
	             * @param userid 当前 token
	             */
	            @Override
	            public void onSuccess(String userid) {
	            	Toast.makeText(MainActivity.this, userid+"链接成功", 0).show();

	               MLog.d("--onSuccess" + userid);
	               RongCloudEvent.getInstance().setOtherListener();
	            }

	            /**
	             * 连接融云失败
	             * @param errorCode 错误码，可到官网 查看错误码对应的注释
	             */
	            @Override
	            public void onError(RongIMClient.ErrorCode errorCode) {
	            	Toast.makeText(MainActivity.this, "错误码::"+errorCode, 0).show();
	                MLog.d("--onError" + errorCode);
	            }
	        });
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.connect_rong:
			connect(token);
			break;
		case android.R.id.button1:
			RongIM.getInstance().startConversationList(MainActivity.this);
			break;
		case android.R.id.button2:
			RongIM.getInstance().startConversation(MainActivity.this,
					ConversationType.PRIVATE, Api.targetUserId, "hahah");

			// RongIM.getInstance().startPrivateChat(MainActivity.this, "czc1",
			// "title");
			break;
		case R.id.getHistoryMsg:
			Toast.makeText(MainActivity.this, "dian ji le button3", 0).show();
			// 要传递的参数,时间:2015-12-18-10:00这一个小时内的所有用户的聊天记录;
			// String date = "date="+mdate;
			
			RongIM.getInstance().startPrivateChat(MainActivity.this, Api.targetUserId, "");

//			Intent intent = new Intent(this, MessageListActivity.class);
//			startActivity(intent);
			break;
		case R.id.testSessionId:

//			testRequest();
			textDataSupport();
			break;

		default:
			break;
		}

	}

	private void textDataSupport() {
		List<MsgInfor> msgInforListDB = DataSupport.limit(10).find(MsgInfor.class);
		Toast.makeText(this, msgInforListDB.size(), 0).show();
		
	}

	/**
	 * 测试用,可删除
	 */
	private void testRequest() {
		ThreadPoolManager.getInstance().getNetThreadPool()
				.execute(new Runnable() {

					@Override
					public void run() {
						HttpClient client = new DefaultHttpClient();
						HttpPost httpPost = new HttpPost(Api.LOGINURI);
						List<NameValuePair> params = new ArrayList<NameValuePair>();

						params.add(new BasicNameValuePair("account", "12345678"));

						params.add(new BasicNameValuePair("password", "123456"));
						params.add(new BasicNameValuePair("userType",
								Api.USERTYPE));
						UrlEncodedFormEntity entity = null;
						try {
							MLog.d("dhfklalksdjflkakdj");
							entity = new UrlEncodedFormEntity(params, "UTF-8");
							httpPost.setEntity(entity);
							HttpResponse response = client.execute(httpPost);
							MLog.d("code:::"
									+ response.getStatusLine().getStatusCode());
							if (response.getStatusLine().getStatusCode() == 200) {
								InputStream is = response.getEntity()
										.getContent();
								String json = new String(HttpUtil
										.readInputStream(is));
								MLog.d(json);

								Gson gson = new Gson();
								TeacherBean teacherBean = gson.fromJson(json,
										TeacherBean.class);

								MLog.d("teacherBean.status:::"
										+ teacherBean.status);
								@SuppressWarnings("deprecation")
								List<Cookie> cookies = ((AbstractHttpClient) client)
										.getCookieStore().getCookies();
								for (Cookie cookie : cookies) {

									;
									String cookieString = cookie.getName()
											+ "=" + cookie.getValue()
											+ ";domain=" + cookie.getDomain();
									MLog.d("cookie:::" + cookieString);
									MLog.d(cookie.toString());
								}

							}

						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});
	}

	private class AutoLogin implements ThreadWithDialogListener {
		private TeacherBean teacherBean;
		private String userName;
		private String password;
		
		//在子线程中执行的方法,返回true会执行OnTaskDone,返回false会执行OnTaskDismissed
		@Override
		public boolean TaskMain() {
			userName = sp.getString("mUserName", "");
			password = sp.getString("mPassWord", "");
			String userType = Api.USERTYPE;
			teacherBean = LogInUtils.logInAndGetBean(userName, password,
					userType);
			return true;
		}
		
		//在主线程中执行的方法
		@Override
		public boolean OnTaskDone() {
			if (teacherBean != null) {
				if (teacherBean.status !=null && teacherBean.status.equals("success")) {
					sp.edit().putBoolean("isLogin", true).commit();

					Toast.makeText(MainActivity.this, userName + "登陆成功",
							Toast.LENGTH_SHORT).show();
				} else {
					sp.edit().putBoolean("isLogin", true).commit();
					Toast.makeText(MainActivity.this, "账号不存在",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(MainActivity.this, "服务器异常", Toast.LENGTH_SHORT)
						.show();
			}

			return true;
		}

		@Override
		public boolean OnTaskDismissed() {
			return false;
		}
	}
}
