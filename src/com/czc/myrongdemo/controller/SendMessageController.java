package com.czc.myrongdemo.controller;

import io.rong.imlib.model.Message.MessageDirection;
import io.rong.imlib.model.Message.SentStatus;
import io.rong.message.ImageMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.czc.myrongdemo.Api;
import com.czc.myrongdemo.R;
import com.czc.myrongdemo.activity.MessageListActivity;
import com.czc.myrongdemo.adapter.MessageListAdapter;
import com.czc.myrongdemo.bean.Content;
import com.czc.myrongdemo.bean.MsgInfor;
import com.czc.myrongdemo.fragment.MessageFragment;
import com.czc.myrongdemo.utils.DateUtils;
import com.czc.myrongdemo.utils.MLog;
import com.czc.myrongdemo.view.MessageInputLayout;
import com.czc.myrongdemo.view.MessageInputLayout.OnOperationListener;
import com.czc.myrongdemo.view.XListView;
import com.czc.myrongdemo.view.photoView.PhotoAlbumActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SendMessageController implements OnClickListener {

	private static final int SCROLLLISTVIEW = 0;
	private View messageFragmentView;
	private MessageInputLayout messageInputLayout;
	private Button btnSend;
	private EditText etSendMessage;
	private MessageListActivity messageListActivity;
	private Context mContext;
	private String mPhontoPath;
	private MessageFragment messageFragment;
	private XListView xListView;

	private SendMessageHandler mHandler = new SendMessageHandler(this);
	private MessageListAdapter messageListAdapter;
	private RecordVoiceBtn btnVoice;
	private static SendMessageController sendMessageController;

	public SendMessageController(View messageFragmentView,
			MessageListActivity messageListActivity,
			MessageFragment messageFragment) {
		this.messageFragmentView = messageFragmentView;
		this.messageListActivity = messageListActivity;
		this.messageFragment = messageFragment;
		mContext = messageListActivity.getApplicationContext();
		initView();
		initDate();
	}

	// public static SendMessageController getInstance(View messageFragment,
	// MessageListActivity messageListActivity){
	// if(sendMessageController == null){
	// MLog.d("SendMessageController==null");
	// sendMessageController = new SendMessageController(messageFragment,
	// messageListActivity);
	// }
	// return sendMessageController;
	// }

	private void initView() {
		MLog.d("messageFragmentView::" + messageFragmentView.toString());
		xListView = messageFragment.getxListView();
		messageInputLayout = (MessageInputLayout) messageFragmentView
				.findViewById(R.id.message_input_layout);
		btnVoice = (RecordVoiceBtn) messageInputLayout
				.findViewById(R.id.btn_voice);

		btnSend = (Button) messageInputLayout.findViewById(R.id.btn_send);
		etSendMessage = (EditText) messageFragmentView
				.findViewById(R.id.et_sendmessage);

		btnSend.setOnClickListener(this);
		btnVoice.initConv(mHandler);
	}

	private void initDate() {
		MLog.d("SendMessageController的initDate执行了");
		setListenner();
		messageListActivity.setController(this);

	}

	public void setMessageListAdapter(MessageListAdapter messageListAdapter) {
		this.messageListAdapter = messageListAdapter;

	}

	private void setListenner() {

		MLog.d("SenmessageController 的setListenner执行了");
		messageInputLayout.setOnOperationListener(new OnOperationListener() {

			@Override
			public void selectPic() {
				Intent intent = new Intent(mContext, PhotoAlbumActivity.class);
				messageListActivity.startPickPicActivity(mHandler, intent);
			}

			@Override
			public void selectLocation() {
				// TODO Auto-generated method stub
			}

			@Override
			public void selectCamera() {
				takePhonto();
			}

			@Override
			public void selectFile() {
				Intent intent = new Intent ();
				//可能要添加数据
				messageListActivity.startPickFileActivity(mHandler,intent);
				
			}

		});
	}

	/**
	 * 发送按钮的点击事件
	 */
	@Override
	public void onClick(View v) {
		String messageText = etSendMessage.getText().toString().trim();

		if (TextUtils.isEmpty(messageText)) {
			return;
		}

		Content content = new Content();
		content.setContent(messageText);
		content.saveThrows();

		MsgInfor msgInfor = new MsgInfor();
		msgInfor.setFromUserId(Api.muserId);
		msgInfor.setTargetId(Api.targetUserId);
		msgInfor.setTargetType("1");
		msgInfor.setGroupId("");
		msgInfor.setClassname(Api.TXTMSG);
		msgInfor.setContent(content);
		msgInfor.setDateTime(getCurrentTime());
		msgInfor.setMessageDirection(MessageDirection.SEND.getValue());
		msgInfor.setSentStatus(SentStatus.SENDING.getValue());

		msgInfor.saveThrows();
		messageListAdapter.addMsgInfor(msgInfor);
		etSendMessage.setText("");

		xListView.setSelection(xListView.getBottom());
	}

	private void takePhonto() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			String dir = "sdcard/czcRongDemo/pictures/";
			File destDir = new File(dir);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
			File file = new File(dir, new DateFormat().format("yyyyMMddhhmmss",
					Calendar.getInstance(Locale.CHINA)) + ".jpg");
			setPhotoPath(file.getAbsolutePath());
			MLog.d("getPhotoPath:::" + getPhotoPath());
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			try {
				messageListActivity.startPickCameraActivity(mHandler, intent,
						this);
			} catch (ActivityNotFoundException anf) {
				anf.printStackTrace();
				Toast.makeText(mContext,
						mContext.getString(R.string.camera_not_prepared),
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(mContext,
					mContext.getString(R.string.sdcard_not_exist_toast),
					Toast.LENGTH_SHORT).show();
		}

	}

	public class SendMessageHandler extends Handler {

		private WeakReference<SendMessageController> mController;

		public SendMessageHandler(SendMessageController sendMessageController) {
			super();
			mController = new WeakReference<SendMessageController>(
					sendMessageController);
		}

		public SendMessageHandler(Callback callback) {
			super(callback);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			SendMessageController controller = mController.get();
			if (controller != null) {
				switch (msg.what) {
				case Api.UPLOAD_IMG_CALLBACK:
					Intent intent = (Intent) msg.obj;
					// 原图路径
					String imgPath = intent.getStringExtra("localPath");
					// 缩略图路径
					String tempPath = intent.getStringExtra("tempPath");
					Content imageContent = new Content();
					imageContent.setThumUri(tempPath);
					imageContent.setLocalPath(imgPath);
					// content.setContent("");
					// content.setContent(CodeUtil.bitMap2Base64(tempPath));
					// MLog.d(CodeUtil.bitMap2Base64(tempPath));
					imageContent.saveThrows();

					MsgInfor imageMsgInfor = new MsgInfor();
					imageMsgInfor.setFromUserId(Api.muserId);
					imageMsgInfor.setTargetId(Api.targetUserId);
					imageMsgInfor.setClassname(Api.IMGMSG);
					imageMsgInfor.setGroupId("");
					imageMsgInfor.setDateTime(getCurrentTime());
					imageMsgInfor.setTargetType("1");
					imageMsgInfor.setContent(imageContent);
					imageMsgInfor.setMessageDirection(MessageDirection.SEND
							.getValue());
					imageMsgInfor.setSentStatus(SentStatus.SENDING.getValue());

					imageMsgInfor.saveThrows();
					controller.messageListAdapter.addMsgInfor(imageMsgInfor);
					xListView.setSelection(xListView.getBottom());

					break;
				case Api.UPLOAD_VOICE_CALLBACK:

					String voicePath = (String) msg.obj;
					String duration = String.valueOf(msg.arg1);

					MLog.d("voicePath::" + voicePath);
					Content voiceContent = new Content();
					voiceContent.setDuration(duration);
					voiceContent.setUrl(voicePath);
					voiceContent.saveThrows();

					MsgInfor voiceMsgInfor = new MsgInfor();
					voiceMsgInfor.setContent(voiceContent);
					voiceMsgInfor.setFromUserId(Api.muserId);
					voiceMsgInfor.setTargetId(Api.targetUserId);
					voiceMsgInfor.setClassname(Api.VOICEMSG);
					voiceMsgInfor.setGroupId("");
					voiceMsgInfor.setTargetType("1");
					voiceMsgInfor.setDateTime(getCurrentTime());
					voiceMsgInfor.setMessageDirection(MessageDirection.SEND
							.getValue());
					voiceMsgInfor.setSentStatus(SentStatus.SENDING.getValue());

					voiceMsgInfor.saveThrows();
					controller.messageListAdapter.addMsgInfor(voiceMsgInfor);
					xListView.setSelection(xListView.getBottom());

					break;
				case Api.UPLOAD_FILE_CALLBACK:
					
//					ZywxMessage fileMsg = new ZywxMessage();
//					fileMsg.setFileSize(fileSize);
//					fileMsg.setSenderUserId(controller.mCurrentUserInfo
//							.getUserId());
//					fileMsg.setSenderUserIcon(controller.mCurrentUserInfo
//							.getPortrait());
//					fileMsg.setSenderUserName(controller.mCurrentUserInfo
//							.getName());
//					fileMsg.setDepartName(controller.mCurrentUserInfo
//							.getDepartName());
//
//					fileMsg.setServerFile(false);
//
//					final StringBuilder fileName = new StringBuilder();
//					try {
//						String file[] = realFilePath.split("/");
//						fileName.append(file[file.length - 1]);
//					} catch (Exception e) {
//						fileName.append("未知文件");
//					}
//
//					fileMsg.setContent(fileName.toString());
//					fileMsg.setMessageType(ZywxMessageType.FILE.getName());
//					fileMsg.setLocalUri(realFilePath);
//					fileMsg.setConversationId(controller.mTargetId);
//					fileMsg.setSentStatus(SentStatus.SENDING.getValue());
//					fileMsg.setMessageDirection(MessageDirection.SEND
//							.getValue());
//					fileMsg.setSendTime(System.currentTimeMillis());
//					AppContext.getInstance().saveZywxMessage(fileMsg);
//					controller.mChatAdapter.addMsgToList(fileMsg);
//					controller.mChatView.setToBottom();
					
					
					String realFilePath = (String) msg.obj;
					long fileSize = msg.getData().getLong("fileSize", 0);
					
					final StringBuilder fileName = new StringBuilder();
					try {
						String file[] = realFilePath.split("/");
						fileName.append(file[file.length - 1]);
					} catch (Exception e) {
						fileName.append("未知文件");
					}
					
					Content fileContent = new Content();
					fileContent.setFileSize(fileSize);
					fileContent.setContent(fileName.toString());
					fileContent.setLocalUri(realFilePath);
					fileContent.saveThrows();
					
					MsgInfor fileMsg = new MsgInfor();
					fileMsg.setContent(fileContent);
					fileMsg.setFromUserId(Api.muserId);
					fileMsg.setTargetId(Api.targetUserId);
					fileMsg.setClassname(Api.IMGTEXTMSG);
					fileMsg.setGroupId("");
					fileMsg.setTargetType("1");
					fileMsg.setDateTime(getCurrentTime());
					fileMsg.setMessageDirection(MessageDirection.SEND
							.getValue());
					fileMsg.setSentStatus(SentStatus.SENDING.getValue());
					
					fileMsg.saveThrows();
					controller.messageListAdapter.addMsgInfor(fileMsg);
					xListView.setSelection(xListView.getBottom());
					break;
				case SCROLLLISTVIEW:
					MLog.v("SCROLLLISTVIEW");
					int last = xListView.getLastVisiblePosition();
					//xlistView 加了头和脚
					int count = messageListAdapter.getCount();
					
					MLog.v("last="+last+"&&count"+count);
					if (last == count - 1){
						MLog.v("AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL");
						xListView.setTranscriptMode(
								AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
					}else if (last < count - 1){
						xListView.setTranscriptMode(
								AbsListView.TRANSCRIPT_MODE_NORMAL);
					}
						
					break;

				default:
					break;
				}
			}

		}

	}

	public String getPhotoPath() {
		return mPhontoPath;
	}

	public void setPhotoPath(String mPhontoPath) {
		this.mPhontoPath = mPhontoPath;
	}

	/**
	 * 获取并格式化当前系统时间
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	private String getCurrentTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String date1 = sf.format(date);
		return date1;
	}

	public void updateMessageList(io.rong.imlib.model.Message message) {

		MLog.d("message:" + message.toString() + "  message.getSenderUserId::"
				+ message.getSenderUserId());
		if (message != null && message.getSenderUserId() != null
				&& message.getSenderUserId().equals(Api.targetUserId)) {

			int position = messageListAdapter.findPosition(message
					.getMessageId());

			MLog.v("messageListAdapter.findPosition::" + position);

			MsgInfor msgInfor = new MsgInfor();
			msgInfor.setFromUserId(message.getSenderUserId());
			msgInfor.setTargetId(Api.muserId);
			msgInfor.setTargetType("1");
			msgInfor.setGroupId("");
			msgInfor.setDateTime(DateUtils.formateDate(message.getSentTime()));
			msgInfor.setMessageDirection(message.getMessageDirection()
					.getValue());
			msgInfor.setMessageReceivedStatue(message.getReceivedStatus()
					.getFlag());

			MLog.d("message.geContent::"
					+ (message.getContent() instanceof TextMessage));
			if (message.getContent() instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message.getContent();

				Content content = new Content();
				content.setContent(textMessage.getContent());
				content.saveThrows();

				msgInfor.setClassname(Api.TXTMSG);
				msgInfor.setContent(content);

				MLog.d("textMessage.getContent::" + textMessage.getContent());

			}

			if (message.getContent() instanceof ImageMessage) {

				MLog.d("message.getContent() instanceof ImageMessage执行了");
				ImageMessage imageMessage = (ImageMessage) message.getContent();

				Content imageContent = new Content();
				imageContent.setImageUri(imageMessage.getRemoteUri() + "");
				imageContent.setLocalPath(imageMessage.getLocalUri() + "");
				imageContent.setThumUri(imageMessage.getThumUri() + "");
				imageContent.setContent(null);
				imageContent.saveThrows();
				// content.setContent("");
				// content.setContent(CodeUtil.bitMap2Base64(tempPath));
				// MLog.d(CodeUtil.bitMap2Base64(tempPath));
				msgInfor.setClassname(Api.IMGMSG);
				msgInfor.setContent(imageContent);

				MLog.d("thumUri:" + imageMessage.getThumUri() + "localUri:"
						+ imageMessage.getLocalUri());

			}

			if (message.getContent() instanceof VoiceMessage) {
				VoiceMessage voiceMessage = (VoiceMessage) message.getContent();

				String voicePath = String.valueOf(voiceMessage.getUri());
				String duration = String.valueOf(voiceMessage.getDuration());
				MLog.d("voicePath::" + voicePath);
				Content voiceContent = new Content();
				voiceContent.setDuration(duration);
				voiceContent.setUrl(voicePath);
				voiceContent.saveThrows();

				msgInfor.setClassname(Api.VOICEMSG);
				msgInfor.setContent(voiceContent);
			}
			
			if(message.getContent() instanceof RichContentMessage){
				RichContentMessage fileMessage = (RichContentMessage) message.getContent();
				MLog.i("fileName::"+fileMessage.getContent()+"fileUrl::"+fileMessage.getUrl()+"fileSize::"+fileMessage.getExtra());
				String  fileName = fileMessage.getContent();
				String fileUrl = fileMessage.getUrl();
				long fileSize = Long.valueOf(fileMessage.getExtra().replace("\"",""));//将获得的字符串中的"号替换成空
				
				Content fileContent  = new Content();
				fileContent.setContent(fileName);
				fileContent.setFileSize(fileSize);
				fileContent.setLocalUri(fileUrl);
				fileContent.saveThrows();
				
				msgInfor.setClassname(Api.IMGTEXTMSG);
				msgInfor.setContent(fileContent);
			}

			if (position == -1) {
				msgInfor.saveThrows();
				messageListAdapter.addMsgInfor(msgInfor);
				mHandler.obtainMessage(SCROLLLISTVIEW, msgInfor).sendToTarget();
			}
		}

	}

}
