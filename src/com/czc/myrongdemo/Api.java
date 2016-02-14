package com.czc.myrongdemo;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.Message.SentStatus;

import java.util.List;

import org.litepal.crud.DataSupport;

import android.content.ContentValues;
import android.os.Environment;

import com.czc.myrongdemo.bean.MsgInfor;
import com.czc.myrongdemo.utils.DateUtils;
import com.czc.myrongdemo.utils.MLog;

public class Api {

	public static final String HISTORYMESSAGEURI = "https://api.cn.ronghub.com/message/history.json";
	public static final String HISTORYMESSAGEDIR = Environment.getExternalStorageDirectory()+"/czcMessages";
	public static final String LOGINURI ="http://weixin.mzywx.com/daxiang/app/user/login";
	public static final String USERTYPE = "1";
	/**
	 * handler发送上传图片的消息
	 */
	public static final int UPLOAD_IMG_CALLBACK = 81;
	
	/**
	 * 相机activity返回的请求码
	 */
	public static final int REQUESTCODE_CAMARA_PHOTO = 80;
	/**
	 *选择图片activity返回的请求码
	 */
	public static final int REQUEST_CODE_SELECT_PICTURE = 82;
	/**
	 * 选择文件activity返回的请求码
	 */
	public static final int REQUEST_CODE_TAKE_FILE = 84;
	
	public static final int UPLOAD_VOICE_CALLBACK = 86;
	public static final int UPLOAD_FILE_CALLBACK = 88;
	
	/**
	 * 文本信息
	 */
	public static final String TXTMSG = "RC:TxtMsg";

	/**
	 * 图文信息
	 */
	public static final String IMGTEXTMSG = "RC:ImgTextMsg";
	/**
	 * 声音消息
	 */
	public static final String VOICEMSG = "RC:VcMsg";
	/**
	 * 图片消息
	 */
	public static final String IMGMSG = "RC:ImgMsg";
	// public static final String TXTMSG ="RC:TxtMsg";
	
	public static final String FILEMSG ="FileMsg";
	
	public static String targetUserId="czc1";
//	public static String targetUserId="czc2";

	public static String muserId ="czc2";
//	public static String muserId ="czc1";

	public static String mdate = DateUtils.getCurrDate();
	public String outPath;
	
	/**
	 * 所有历史消息本地json缓存
	 */
	public static final String MSGSDDIR = Environment
			.getExternalStorageDirectory() + "/czcMessages";
	/**
	 *所有图片本地缓存目录 
	 */
	public static final String IMAGSDDIR = Environment
			.getExternalStorageDirectory() + "/czcMessages"+"/czcImag";
	/**
	 * 所有声音文件本地缓存目录
	 */
	public static final String VOICESDDIR = Environment
			.getExternalStorageDirectory() + "/czcMessages"+"/czcVoice";
	public static final String SPNAME = "loginsp";
	public static final String SESSIONID = "SHAREJSESSIONID";
	public static final String APPKEY = "82hegw5uhlxox";
	
	
	
	
	
	
	

	public void setUserId(String userId) {

		this.muserId = userId;
	}

	// 从融云的数据库中去取数据
	public static List<Message> getHistoryMessageFromDB() {
		List<Message> historyMessages = RongIM.getInstance().getRongIMClient()
				.getHistoryMessages(ConversationType.PRIVATE, "czc1", -1, 10);

		for (Message message : historyMessages) {
			MLog.d(message.toString());
		}
		return historyMessages;
	}

	public static void updateMsgSentState(SentStatus sent, MsgInfor msgInfor) {
		ContentValues values = new ContentValues();
		values.put("sentStatus", sent.getValue());
		DataSupport.update(MsgInfor.class, values, msgInfor.getId());
		
	}
	
	
	

}
