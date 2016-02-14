package com.czc.myrongdemo.utils;

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

import com.czc.myrongdemo.Api;
import com.czc.myrongdemo.bean.TeacherBean;
import com.google.gson.Gson;

public class LogInUtils {

	@SuppressWarnings("deprecation")
	public static TeacherBean logInAndGetBean(final String mUserName,
			final String mPassWord, final String mUserType)
			{
		TeacherBean teacherBean = new TeacherBean();
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(Api.LOGINURI);
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("account", mUserName));

		params.add(new BasicNameValuePair("password", mPassWord));
		params.add(new BasicNameValuePair("userType", mUserType));
		UrlEncodedFormEntity entity = null;
		MLog.d("dhfklalksdjflkakdj");
		try {
			entity = new UrlEncodedFormEntity(params, "UTF-8");
			
			httpPost.setEntity(entity);
			HttpResponse response = client.execute(httpPost);
			MLog.d("code:::" + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {
				InputStream is = response.getEntity().getContent();
				String json = new String(HttpUtil.readInputStream(is));
				MLog.d(json);

				Gson gson = new Gson();
				teacherBean = gson.fromJson(json, TeacherBean.class);

				MLog.d("teacherBean.status:::" + teacherBean.status);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		

//		getCookies(client);
		return teacherBean;
	}
	
	
	private static void getCookies(HttpClient client) {
		@SuppressWarnings("deprecation")
		List<Cookie> cookies = ((AbstractHttpClient) client).getCookieStore()
				.getCookies();
		for (Cookie cookie : cookies) {

//			sp .edit().putString(Api.SESSIONID, cookie.getValue()).commit();
//			String cookieString = cookie.getName() + "=" + cookie.getValue()
//					+ ";domain=" + cookie.getDomain();
//			MLog.d("cookie:::" + cookieString);
//			MLog.d(cookie.toString());
		}
	}
}
