package com.czc.myrongdemo;

import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.ConversationBehaviorListener;
import io.rong.imkit.RongIM.OnSendMessageListener;
import io.rong.imkit.RongIM.SentMessageErrorCode;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.RongIMClient.OnReceiveMessageListener;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.util.Log;
import android.view.View;

import com.czc.myrongdemo.activity.MainActivity;
import com.czc.myrongdemo.activity.PhotoActivity;


public class RongCloudEvent implements ConversationBehaviorListener, Callback,
		OnReceiveMessageListener, OnSendMessageListener {

	private static RongCloudEvent mRongCloudInstance;

	public static RongCloudEvent getInstance() {
		return mRongCloudInstance;
	}

	/**
	 * 初始化 RongCloud.
	 * 
	 * @param context
	 *            上下文。
	 */
	public static void init(Context context) {

		if (mRongCloudInstance == null) {

			synchronized (RongCloudEvent.class) {

				if (mRongCloudInstance == null) {
					mRongCloudInstance = new RongCloudEvent(context);
				}
			}
		}
	}

	private Context mContext;
	private Handler mHandler;
	protected String TAG = "TAG";

	/**
	 * 构造方法。
	 * 
	 * @param context
	 *            上下文。
	 */
	private RongCloudEvent(Context context) {
		mContext = context;
		initDefaultListener();
		// 这句有用吗????????
		mHandler = new Handler(this);
	}

	private void initDefaultListener() {
		RongIM.setConversationBehaviorListener(this);// 设置会话界面操作的监听器。
		
	}

	public void setOtherListener() {
//		RongIM.getInstance().setCurrentUserInfo(new UserInfo("czc2","czc2", null));
		RongIM.getInstance().getRongIMClient()
				.setOnReceiveMessageListener(this);// 设置消息接收监听器。
		RongIM.getInstance().setSendMessageListener(this);//设置消息发送的监听器

	}

	/**
     * 长按会话列表 item 后执行。
     *
     * @param context      上下文。
     * @param view         触发点击的 View。
     * @param conversation 长按会话条目。
     * @return 返回 true 不再执行融云 SDK 逻辑，返回 false 先执行融云 SDK 逻辑再执行该方法。
     */
    public boolean onConversationLongClick(Context context, View view, UIConversation conversation) {
        return false;
    }

	/**
	 * 接收消息的监听器：OnReceiveMessageListener 的回调方法，接收到消息后执行。
	 * 
	 * @param message
	 *            接收到的消息的实体信息。
	 * @param left
	 *            剩余未拉取消息数目。
	 */
	@Override
	public boolean onReceived(Message message, int left) {
		MessageContent messageContent = message.getContent();

		if (messageContent instanceof TextMessage) {// 文本消息
			TextMessage textMessage = (TextMessage) messageContent;
			Log.d(TAG, "onReceived-TextMessage:" + textMessage.getContent());
			if (textMessage.getContent().equals("11")) {
				Log.e(TAG, "---onReceived--111111--");
				return false;
			}
		} else if (messageContent instanceof ImageMessage) {// 图片消息
			ImageMessage imageMessage = (ImageMessage) messageContent;
			Log.d(TAG, "onReceived-ImageMessage:" + imageMessage.getRemoteUri());
			Log.d(TAG, "onReceived-ImageMessage:"+imageMessage.getLocalUri()+"&&"+imageMessage.getThumUri());
		} else if (messageContent instanceof VoiceMessage) {// 语音消息
			VoiceMessage voiceMessage = (VoiceMessage) messageContent;
			Log.d(TAG, "onReceived-voiceMessage:"
					+ voiceMessage.getUri().toString());
		} else if (messageContent instanceof RichContentMessage) {// 图文消息
			RichContentMessage richContentMessage = (RichContentMessage) messageContent;
			Log.d(TAG,
					"onReceived-RichContentMessage:"
							+ richContentMessage.getContent());
		} else if (messageContent instanceof ContactNotificationMessage) {// 好友添加消息
			ContactNotificationMessage contactContentMessage = (ContactNotificationMessage) messageContent;
			Log.d(TAG, "onReceived-ContactNotificationMessage:getExtra;"
					+ contactContentMessage.getExtra());
			Log.d(TAG, "onReceived-ContactNotificationMessage:+getmessage:"
					+ contactContentMessage.getMessage().toString());
			Intent in = new Intent();
			in.setAction(MainActivity.ACTION_DMEO_RECEIVE_MESSAGE);
			in.putExtra("rongCloud", contactContentMessage);
			in.putExtra("has_message", true);
			mContext.sendBroadcast(in);
		} else {
			Log.d(TAG, "onReceived-其他消息，自己来判断处理");
		}

		return false;
	}
	
	

	@Override
	public boolean onMessageClick(Context context, View view, Message message) {

		Log.e(TAG, "----onMessageClick");
		Log.e(TAG,message.getContent()+"");

		/**
		 * demo 代码 开发者需替换成自己的代码。
		 */
		if (message.getContent() instanceof RichContentMessage) {
			RichContentMessage mRichContentMessage = (RichContentMessage) message
					.getContent();
			Log.d("Begavior", "extra:" + mRichContentMessage.getExtra());

		} else if (message.getContent() instanceof ImageMessage) {
			Log.i(TAG, "点击了图片");
//			ImageMessage imageMessage = (ImageMessage) message.getContent();
//			Intent intent = new Intent(context, PhotoActivity.class);
//
//			intent.putExtra(
//					"photo",
//					imageMessage.getLocalUri() == null ? imageMessage
//							.getRemoteUri() : imageMessage.getLocalUri());
//			if (imageMessage.getThumUri() != null)
//				//传缩略图的uri
//				intent.putExtra("thumbnail", imageMessage.getThumUri());
//
//			context.startActivity(intent);
		}

		Log.d("Begavior",
				message.getObjectName() + ":" + message.getMessageId());

		return false;
	}

	

	@Override
	public boolean onMessageLongClick(Context arg0, View arg1, Message arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onUserPortraitLongClick(Context arg0, ConversationType arg1,
			UserInfo arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleMessage(android.os.Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onUserPortraitClick(Context arg0, ConversationType arg1,
			UserInfo arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Message onSend(Message message) {
		Log.i(TAG, "**********"+message+"*********");
		return message;
	}

	@Override
	public boolean onSent(Message message, SentMessageErrorCode sentMessageErrorCode) {
		if (message.getSentStatus() == Message.SentStatus.FAILED) {

            if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_CHATROOM) {//不在聊天室

            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_DISCUSSION) {//不在讨论组

            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_GROUP) {//不在群组

            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.REJECTED_BY_BLACKLIST) {//你在他的黑名单中
                Log.i(TAG,"你在放的黑名单中");
            }
        }


        MessageContent messageContent = message.getContent();

        if (messageContent instanceof TextMessage) {//文本消息
            TextMessage textMessage = (TextMessage) messageContent;
            Log.d(TAG, "onSent-TextMessage:" + textMessage.getContent());
        } else if (messageContent instanceof ImageMessage) {//图片消息
            ImageMessage imageMessage = (ImageMessage) messageContent;
            Log.d(TAG, "onSent-ImageMessage:" + imageMessage.getRemoteUri());
        } else if (messageContent instanceof VoiceMessage) {//语音消息
            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
            Log.d(TAG, "onSent-voiceMessage:" + voiceMessage.getUri().toString());
        } else if (messageContent instanceof RichContentMessage) {//图文消息
            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
            Log.d(TAG, "onSent-RichContentMessage:" + richContentMessage.getContent());
        } else {
            Log.d(TAG, "onSent-其他消息，自己来判断处理");
        }
        return false;
	}

	@Override
	public boolean onMessageLinkClick(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	

}
