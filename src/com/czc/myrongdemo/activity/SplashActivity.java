package com.czc.myrongdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.czc.myrongdemo.Api;
import com.czc.myrongdemo.R;
import com.czc.myrongdemo.utils.MLog;

public class SplashActivity extends Activity{
	
	private SharedPreferences sp;
	
	private final int HASLOGIN = 1;
	private final int HASNOTLOGIN = 0;
//	private ThreadWithDialogTask task;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
//		task = new ThreadWithDialogTask();
		sp = this.getSharedPreferences(Api.SPNAME, MODE_PRIVATE);
		boolean isFirstLogin = sp.getBoolean("isFirstLogIn", true);
		
		MLog.d("isFirstLogIn::"+isFirstLogin);
		if(!isFirstLogin){
			sp.edit().putBoolean("isLogin", false).commit();
			handler.sendEmptyMessage(HASLOGIN);
//			task.RunWithoutDialog(this, new AutoLogin() , true);
		}
		else
			handler.sendEmptyMessageDelayed(HASNOTLOGIN, 3000);
		
	}
	
	
	private Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HASLOGIN:
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
				finish();
				break;
			case HASNOTLOGIN:
				startActivity(new Intent(getApplicationContext(), LoginActivity.class));
				finish();
				break;

			default:
				break;
			}
			
		};
	};
	
	
	

}
