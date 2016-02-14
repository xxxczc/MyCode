package com.czc.myrongdemo.adapter;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.SendImageMessageCallback;
import io.rong.imlib.RongIMClient.SendMessageCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.Message.MessageDirection;
import io.rong.imlib.model.Message.SentStatus;
import io.rong.message.ImageMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.text.SpannableString;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.czc.myrongdemo.Api;
import com.czc.myrongdemo.App;
import com.czc.myrongdemo.R;
import com.czc.myrongdemo.activity.ShowPictureActivity;
import com.czc.myrongdemo.bean.MsgInfor;
import com.czc.myrongdemo.utils.FaceConversionUtil;
import com.czc.myrongdemo.utils.HttpUtil;
import com.czc.myrongdemo.utils.MLog;
import com.czc.myrongdemo.utils.Md5Utils;
import com.czc.myrongdemo.utils.ThreadPoolManager;
import com.czc.myrongdemo.utils.uiutils.MCommonUtil;
import com.czc.myrongdemo.utils.uiutils.MUIToast;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

public class MessageListAdapter extends BaseAdapter {

	private List<MsgInfor> msgInforList;
	private String flag;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private final int TYPETXT = 0;
	private final int TYPEIMAGTXT = 1;
	private final int TYPEIMAG = 2;
	private final int TYPEVOICE = 3;
	private MediaPlayer mp = new MediaPlayer();

	private Context mContext;

	private Activity mActivity;
	private Gson gson = new Gson();

	public MessageListAdapter() {
		super();
	}

	public MessageListAdapter(Context context, List<MsgInfor> msgInforList) {
		mContext = context;
		MLog.d(mContext.toString() + "********");
		mActivity = (Activity) context;
		new ImageSize(100, 100);
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(App.context));
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.pictures_no)
				.showImageOnFail(R.drawable.pictures_no).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();

		this.msgInforList = msgInforList;
	}

	public void addMsgInfor(MsgInfor msgInfor) {
		if (msgInfor != null && msgInforList != null) {
			msgInforList.add(msgInfor);
		}
		notifyDataSetChanged();
	}
	
	public int findPosition(long id) {
		int index = getCount();
		int position = -1;
		do {
			if (index-- <= 0)
				break;
			if (getItemId(index) != id)
				continue;
			position = index;
			break;
		} while (true);
		return position;
	}

	@Override
	public int getItemViewType(int position) {

		if (msgInforList != null && msgInforList.size() != 0) {
			String msgType = msgInforList.get(position).getClassname();
			if(msgType != null){
				if (msgType.equals(Api.TXTMSG)) {
					return TYPETXT;
				}
				if (msgType.equals(Api.IMGTEXTMSG)) {
					return TYPEIMAGTXT;
				}
				if (msgType.equals(Api.IMGMSG)) {
					return TYPEIMAG;
				}
				if (msgType.equals(Api.VOICEMSG)) {
					return TYPEVOICE;
				}
			}
			
			return -1;

		} else {
			return -1;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 4;
	}

	@Override
	public int getCount() {
		if (msgInforList != null) {

			return msgInforList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		if (msgInforList != null) {
			return msgInforList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		MLog.d("msgInforList::" + msgInforList.size());
		ViewHolder vh;

		if (convertView == null) {
			vh = new ViewHolder();
			convertView = View.inflate(App.context, R.layout.msg_list_item,
					null);
			vh.rll = (RelativeLayout) convertView
					.findViewById(R.id.chat_item_receiver);
			vh.rlr = (RelativeLayout) convertView
					.findViewById(R.id.chat_item_sender);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		MsgInfor msgInfor = msgInforList.get(position);

		MLog.d("msgInfor.toString()" + msgInfor.toString());
		// 发送的消息显示
		if (Api.muserId.equals(msgInfor.getFromUserId())) {

			vh.rll.setVisibility(View.GONE);
			vh.rlr.setVisibility(View.VISIBLE);
			flag = "send";
			showItemView(vh, msgInfor, position);
		} else {
			// 接收的消息显示
			vh.rll.setVisibility(View.VISIBLE);
			vh.rlr.setVisibility(View.GONE);
			flag = "receive";
			showItemView(vh, msgInfor, position);
		}
		return convertView;
	}

	public class ViewHolder {
		public RelativeLayout rll;
		public RelativeLayout rlr;
	}

	/**
	 * 显示itemView
	 * 
	 * @param vh
	 * @param msgInfor
	 */
	private void showItemView(ViewHolder vh, final MsgInfor msgInfor,
			final int position) {
		if (flag != null && flag.equals("send")) {
			switch (getItemViewType(position)) {
			case TYPETXT:
				vh.rlr.findViewById(R.id.layout_chat_item_send_image)
						.setVisibility(View.GONE);
				vh.rlr.findViewById(R.id.layout_chat_item_send_voice)
						.setVisibility(View.GONE);
				vh.rlr.findViewById(R.id.layout_chat_item_send_file).setVisibility(View.GONE);

				LinearLayout llSendText = (LinearLayout) vh.rlr
						.findViewById(R.id.layout_chat_item_send_text);
				TextView tvSendText = (TextView) llSendText
						.findViewById(R.id.msg_content);
				TextView tvSendTime = (TextView) llSendText
						.findViewById(R.id.send_time_txt);
				
				//可以显示表情
				SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(mContext, msgInfor.getContent().getContent());
				tvSendText.setText(spannableString);
				
				tvSendTime.setText(msgInfor.getDateTime());
				sendingTextMessage(msgInfor, position);
				break;
			case TYPEIMAGTXT:
				//TODO
				vh.rlr.findViewById(R.id.layout_chat_item_send_text)
						.setVisibility(View.GONE);
				vh.rlr.findViewById(R.id.layout_chat_item_send_voice)
						.setVisibility(View.GONE);

				vh.rlr.findViewById(R.id.layout_chat_item_send_image).setVisibility(View.GONE);
				
				LinearLayout llSendFile = (LinearLayout) vh.rlr.findViewById(R.id.layout_chat_item_send_file);
				
				TextView tvDisplayFileName = (TextView) llSendFile.findViewById(R.id.display_file_tv);
				TextView tvFileSize = (TextView) llSendFile.findViewById(R.id.tv_file_size);
				ImageView ivSendImage = (ImageView) llSendFile
						.findViewById(R.id.picture_iv);
				
				TextView tvSendTime1 = (TextView) llSendFile
						.findViewById(R.id.send_time_txt);
				//显示文件名
				String fileName = msgInfor.getContent().getContent();
				tvDisplayFileName.setText(fileName);
				//显示文件大小
				tvFileSize.setText(MCommonUtil.formatFileSize(mContext, msgInfor.getContent().getFileSize()));
				//显示发送时间
				tvSendTime1.setText(msgInfor.getDateTime());
				ivSendImage.setImageResource(MCommonUtil.getFileTypeDrawable(fileName));
				sendingFile(msgInfor,position);
				break;
			case TYPEIMAG:
				vh.rlr.findViewById(R.id.layout_chat_item_send_text)
						.setVisibility(View.GONE);
				vh.rlr.findViewById(R.id.layout_chat_item_send_voice)
						.setVisibility(View.GONE);
				vh.rlr.findViewById(R.id.layout_chat_item_send_file).setVisibility(View.GONE);

				MLog.d("msgInfor.content.imageUri:"
						+ msgInfor.getContent().getImageUri());
				LinearLayout llSendImag = (LinearLayout) vh.rlr
						.findViewById(R.id.layout_chat_item_send_image);

				llSendImag.findViewById(R.id.imagetext_tv).setVisibility(
						View.GONE);
				TextView tvSendTime2 = (TextView) llSendImag
						.findViewById(R.id.send_time_txt);
				ImageView ivSendImage2 = (ImageView) llSendImag
						.findViewById(R.id.picture_iv);
				TextView tvProgress = (TextView) llSendImag
						.findViewById(R.id.progress_tv);

				tvSendTime2.setText(msgInfor.getDateTime());
				showImage(msgInfor, ivSendImage2, tvProgress, position);
				break;
			case TYPEVOICE:
				vh.rlr.findViewById(R.id.layout_chat_item_send_image)
						.setVisibility(View.GONE);
				vh.rlr.findViewById(R.id.layout_chat_item_send_text)
						.setVisibility(View.GONE);
				vh.rlr.findViewById(R.id.layout_chat_item_send_file).setVisibility(View.GONE);

				LinearLayout llSendVoice3 = (LinearLayout) vh.rlr
						.findViewById(R.id.layout_chat_item_send_voice);

				TextView tvSendText3 = (TextView) llSendVoice3
						.findViewById(R.id.msg_content);
				TextView tvSendTime3 = (TextView) llSendVoice3
						.findViewById(R.id.send_time_txt);
				TextView tvSendVocie = (TextView) llSendVoice3
						.findViewById(R.id.voice_length_tv);

				ImageView ivSendVoice = (ImageView) llSendVoice3
						.findViewById(R.id.voice_iv);

				ivSendVoice.setImageResource(R.drawable.send_3);

				tvSendVocie.setText(msgInfor.getContent().getDuration());
				// tvSendText3.setBackgroundResource(R.anim.voice_send);
				tvSendTime3.setText(msgInfor.getDateTime());
				sendingVocie(msgInfor, position);

				showVoiceView(msgInfor, position, tvSendText3, ivSendVoice);
				break;

			default:
				Toast.makeText(mContext, "错误的消息类型-1", 0).show();
				break;
			}

		}

		if (flag != null && flag.equals("receive")) {

			switch (getItemViewType(position)) {
			case TYPETXT:
				vh.rll.findViewById(R.id.layout_chat_item_receive_image)
						.setVisibility(View.GONE);
				vh.rll.findViewById(R.id.layout_chat_item_receive_voice)
						.setVisibility(View.GONE);
				vh.rll.findViewById(R.id.layout_chat_item_receive_file).setVisibility(View.GONE);

				LinearLayout llReceiveText = (LinearLayout) vh.rll
						.findViewById(R.id.layout_chat_item_receive_text);
				TextView tvReceiveText = (TextView) llReceiveText
						.findViewById(R.id.msg_content);
				TextView tvReceiveTime = (TextView) llReceiveText
						.findViewById(R.id.send_time_txt);
				
				//显示表情
				SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(mContext, msgInfor.getContent().getContent());
				tvReceiveText.setText(spannableString);
				
				tvReceiveTime.setText(msgInfor.getDateTime());
				break;
			case TYPEIMAGTXT:
				vh.rll.findViewById(R.id.layout_chat_item_receive_text)
						.setVisibility(View.GONE);
				vh.rll.findViewById(R.id.layout_chat_item_receive_voice)
						.setVisibility(View.GONE);
				vh.rll.findViewById(R.id.layout_chat_item_receive_image).setVisibility(View.GONE);
				LinearLayout llReceiveFile = (LinearLayout) vh.rll.findViewById(R.id.layout_chat_item_receive_file);
				
				ImageView ivReceiveImage = (ImageView) llReceiveFile
						.findViewById(R.id.picture_iv);
				TextView tvFileName = (TextView) llReceiveFile.findViewById(R.id.display_file_tv);
				TextView tvFileSize = (TextView) llReceiveFile.findViewById(R.id.tv_file_size);
				TextView tvReceiveTime1 = (TextView) llReceiveFile.findViewById(R.id.send_time_txt);
				
				//显示文件名
				String fileName = msgInfor.getContent().getContent();
				tvFileName.setText(fileName);
				//显示文件大小
				long fileSize = msgInfor.getContent().getFileSize();
				tvFileSize.setText(MCommonUtil.formatFileSize(mContext, fileSize));
				//显示文件图片
				ivReceiveImage.setImageResource(MCommonUtil.getFileTypeDrawable(fileName));
				tvReceiveTime1.setText(msgInfor.getDateTime());
				break;
			case TYPEIMAG:
				vh.rll.findViewById(R.id.layout_chat_item_receive_text)
						.setVisibility(View.GONE);
				vh.rll.findViewById(R.id.layout_chat_item_receive_voice)
						.setVisibility(View.GONE);
				vh.rll.findViewById(R.id.layout_chat_item_receive_file).setVisibility(View.GONE);

				LinearLayout llReceiveText2 = (LinearLayout) vh.rll
						.findViewById(R.id.layout_chat_item_receive_image);

				llReceiveText2.findViewById(R.id.imagetext_tv).setVisibility(
						View.GONE);
				TextView tvReceiveTime2 = (TextView) llReceiveText2
						.findViewById(R.id.send_time_txt);
				ImageView ivReceiveImage2 = (ImageView) llReceiveText2
						.findViewById(R.id.picture_iv);

				showImage(msgInfor, ivReceiveImage2, null, position);
				tvReceiveTime2.setText(msgInfor.getDateTime());
				break;
			case TYPEVOICE:
				vh.rll.findViewById(R.id.layout_chat_item_receive_image)
						.setVisibility(View.GONE);
				vh.rll.findViewById(R.id.layout_chat_item_receive_text)
						.setVisibility(View.GONE);
				vh.rll.findViewById(R.id.layout_chat_item_receive_file).setVisibility(View.GONE);

				LinearLayout llReceiveVoice = (LinearLayout) vh.rll
						.findViewById(R.id.layout_chat_item_receive_voice);
				TextView tvReceiveText3 = (TextView) llReceiveVoice
						.findViewById(R.id.msg_content);
				TextView tvReceiveTime3 = (TextView) llReceiveVoice
						.findViewById(R.id.send_time_txt);
				TextView tvReceiveVoice = (TextView) llReceiveVoice
						.findViewById(R.id.voice_length_tv);

				ImageView ivReceiveVoice = (ImageView) llReceiveVoice
						.findViewById(R.id.voice_iv);
				ivReceiveVoice.setBackgroundResource(R.drawable.receive_3);
				showVoiceView(msgInfor, position, tvReceiveText3,
						ivReceiveVoice);

				tvReceiveTime3.setText(msgInfor.getDateTime());
				tvReceiveVoice.setText(msgInfor.getContent().getDuration());
				break;

			default:
				break;
			}

		}

	}
	
	
	


	private void showImage(final MsgInfor msgInfor, ImageView imageView,
			TextView tvProgress, final int position) {
		if (imageView != null) {
			// 点击预览图片
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					MLog.d(msgInfor.getContent().getImageUri() + "&&"
							+ msgInfor.getContent().getLocalPath() + "&&"
							+ msgInfor.getContent().getThumUri());
					MLog.d("position:::" + position);

					Intent intent = new Intent(mContext,
							ShowPictureActivity.class);
					if (msgInfor.getContent().getLocalPath() != null)
						intent.putExtra("photo", msgInfor.getContent()
								.getLocalPath());
					else
						intent.putExtra("thumbnail", msgInfor.getContent()
								.getThumUri());
					mContext.startActivity(intent);

				}
			});
		}
		
		if (msgInfor != null && msgInfor.getContent() != null) {
			if (msgInfor.getContent().getImageUri() != null) {
				String imageUri = msgInfor.getContent().getImageUri();
				// 如果图片的uri不为空,先从本地取
				MLog.d("imageUri&&&&&::::" + imageUri + msgInfor.getDateTime());

				new File(Api.IMAGSDDIR).mkdir();
				String fileName = "";
				if (imageUri.contains(".png")) {
					fileName = imageUri.substring(imageUri.indexOf(".com/")
							+ ".com/".length());
				} else {

					fileName = imageUri.substring(imageUri.indexOf(".com/")
							+ ".com/".length(), imageUri.indexOf("?"))
							+ ".jpeg";
				}

				String filePath = Api.IMAGSDDIR + File.separator + fileName;
				File file = new File(filePath);
				// 本地如果没有,缓存至本地,并从网络加载图片
				if (!file.exists()) {
					// 缓存图片至本地
					cacheImagToSDCard(msgInfor, file);

					MLog.d("msgInfor.getContent():::" + msgInfor.getContent());
					if (msgInfor.getContent().getContent() != null) {
						byte[] data = Base64.decode(msgInfor.getContent()
								.getContent(), Base64.DEFAULT);
						Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
								data.length);
						imageView.setImageBitmap(bitmap);
					} else {
						MLog.v("imaegLoader从网络加载图片了"+imageUri);
						// TODO 如果图片内容为空(接收到的图片)
						imageLoader.displayImage(imageUri, imageView, options);
						this.notifyDataSetChanged();
					}

				} else {
					MLog.i("&&&&&&&&" + filePath);
					MLog.d("file has existed&&&&&&&&&&&");
					// 第二次直接加载本地缓存的图片
					imageLoader.displayImage("file:///" + filePath, imageView,
							options);
				}
				// 将缓存至本地图片的路径给msgInfor
				msgInfor.getContent().setLocalPath(filePath);
				return;
			}

			// 第二次显示发送的图片时,imageUri== null but localPath!= null
			if (msgInfor.getContent().getLocalPath() != null) {
				// 如果存的是本地地址,直接加载,不用缓存
				imageLoader.displayImage("file:///"
						+ msgInfor.getContent().getLocalPath(), imageView,
						options);
				if (msgInfor.getSentStatus() == SentStatus.SENDING.getValue()) {
					sendingImageMessage(msgInfor, position, tvProgress);
					return;
				}
			}
		}

		

	}
	

	/**
	 * 发送文件
	 * @param msgInfor
	 */
	private void sendingFile(final MsgInfor msgInfor,final int position) {
		if(msgInfor != null && msgInfor.getSentStatus() == SentStatus.SENDING.getValue()){
			String fileUrl = msgInfor.getContent().getLocalUri();
			String fileName= msgInfor.getContent().getContent();
			String targetId = msgInfor.getTargetId();
			String fileSize = String.valueOf(msgInfor.getContent().getFileSize());
			RichContentMessage richContentMessage = new RichContentMessage("文件", msgInfor.getContent().getContent(), msgInfor.getContent().getLocalUri());
			richContentMessage.setUrl(fileUrl);
			richContentMessage.setExtra(gson.toJson(fileSize));
			RongIMClient.getInstance().sendMessage(ConversationType.PRIVATE, targetId, richContentMessage, "", "", new RongIMClient.SendMessageCallback() {
				
				@Override
				public void onSuccess(Integer arg0) {
					MLog.d("--sendingFile--",
							"SendMessageCallback onSuccess:"
									+ arg0);
					refreshSend(SentStatus.SENT, msgInfor);
					refreshList(SentStatus.SENT, position);
				}
				
				@Override
				public void onError(Integer integer, ErrorCode errorcode) {
					
				MLog.d("--sendingFile--",
						"SendMessageCallback errorCode:"
								+ errorcode);
				}
			});
			
			
		}
		
	}

	private void sendingImageMessage(final MsgInfor msgInfor,
			final int position, final TextView tvProgress) {
		if (msgInfor != null) {

			String imagePath = msgInfor.getContent().getLocalPath();
			String targetId = msgInfor.getTargetId();
			if (msgInfor.getSentStatus() == SentStatus.SENDING.getValue()) {
				ImageMessage imageMessage = ImageMessage.obtain(
						Uri.fromFile(new File(imagePath)),// 服务器缩略图URL
						Uri.fromFile(new File(imagePath)));// 图片原图地址
				imageMessage.setExtra("helloExtra");
				RongIM.getInstance()
						.getRongIMClient()
						.sendImageMessage(ConversationType.PRIVATE, targetId,
								imageMessage, "", "",
								new SendImageMessageCallback() {

									@Override
									public void onSuccess(Message message) {
										MLog.d("SendImageMessageCallback onSuccess:"
												+ message);
										tvProgress.setVisibility(View.GONE);
										refreshSend(SentStatus.SENT, msgInfor);
										refreshList(SentStatus.SENT, position);
									}

									@Override
									public void onProgress(Message message,
											final int progress) {
										MLog.d("SendImageMessageCallback progress:"
												+ progress);
										tvProgress.setVisibility(View.VISIBLE);
										mActivity.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												tvProgress
														.setText((int) (progress)
																+ "%");
											}
										});

									}

									@Override
									public void onError(Message message,
											ErrorCode errorCode) {
										MLog.d("SendImageMessageCallback onError,errorCode:"
												+ errorCode);
										showUpLoadFailedToast();
									}

									@Override
									public void onAttached(Message message) {
										MLog.d("SendImageMessageCallback onAttached,message:"
												+ message);

									}
								});

			}
		}

	}

	private void cacheImagToSDCard(final MsgInfor msgInfor, final File file) {

		ThreadPoolManager.getInstance().getNetThreadPool()
				.execute(new Runnable() {

					@Override
					public void run() {
						try {

							FileOutputStream fos = new FileOutputStream(file);
							InputStream fis = HttpUtil.downLoadImage(msgInfor
									.getContent().getImageUri());
							byte[] bytes = new byte[1024 * 1024];
							int temp = 0;
							while ((temp = fis.read(bytes)) != -1) {
								fos.write(bytes, 0, temp);
							}
							fos.flush();
							fis.close();
							fos.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});

	}

	private void sendingVocie(final MsgInfor msgInfor, final int position) {
		if (msgInfor != null) {
			String voiceUrl = msgInfor.getContent().getUrl();

			MLog.d("voiceLocalPath::" + voiceUrl);
			String duration = msgInfor.getContent().getDuration();
			String targetId = msgInfor.getTargetId();
			if (msgInfor.getSentStatus() == SentStatus.SENDING.getValue()) {
				final VoiceMessage voiceMessage = VoiceMessage.obtain(
						Uri.parse(voiceUrl), Integer.valueOf(duration));
				voiceMessage.setExtra(gson.toJson("helloExtra"));

				RongIM.getInstance()
						.getRongIMClient()
						.sendMessage(ConversationType.PRIVATE, targetId,
								voiceMessage, "", "",
								new SendMessageCallback() {

									@Override
									public void onSuccess(Integer arg0) {
										refreshSend(SentStatus.SENT, msgInfor);
										refreshList(SentStatus.SENT, position);
										MLog.e("sendingvoiceSuccess" + arg0);
									}

									@Override
									public void onError(Integer arg0,
											ErrorCode arg1) {
										MLog.e("sendingvoiceError=" + arg0
												+ "&&&&ErrorCode=" + arg1);
										showUpLoadFailedToast();

									}
								});
			}
		}

	}

	private void showVoiceView(final MsgInfor msgInfor, final int position,
			TextView tvVoiceContent, final ImageView ivVoice) {
		tvVoiceContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 区分是从融云获取的历史记录还是本机发送的声音文件
				// 如果是本地发的语音

				MLog.d("sgInfor.getSentStatus()::" + msgInfor.getSentStatus());
				MLog.d("SentStatus.SENT.getValue()::"
						+ SentStatus.SENT.getValue());
				MLog.d("msgInfor.getSentStatus() == SentStatus.SENT.getValue()"
						+ (msgInfor.getSentStatus() == SentStatus.SENT
								.getValue()));
				if (msgInfor.getSentStatus() == SentStatus.SENT.getValue()) {
					String voicePath = msgInfor.getContent().getUrl();
					MLog.d("msgInfor.getContent().getUrl()::"
							+ msgInfor.getContent().getUrl());
					playVoice(voicePath, ivVoice);
					return;
				}

				if (msgInfor.getMessageDirection() == MessageDirection.RECEIVE
						.getValue()) {

					String voicePath = msgInfor.getContent().getUrl();
					playVoice(voicePath, ivVoice);
					return;
				}

				MLog.d(position + "++++++++++++");
				// 先从本地取声音文件
				new File(Api.VOICESDDIR).mkdir();
				final String voicePath = Api.VOICESDDIR + File.separator
						+ Md5Utils.encode(msgInfor.getDateTime()) + ".amr";
				final File file = new File(voicePath);
				if (file.exists()) {
					MLog.d("从本地取的声音文件");
					playVoice(voicePath, ivVoice);

				} else {
					// 解析json,将音频缓存至本地
					ThreadPoolManager.getInstance().getFileThreadPool()
							.execute(new Runnable() {
								@Override
								public void run() {

									byte[] voiceDecode = Base64.decode(msgInfor
											.getContent().getContent(),
											Base64.DEFAULT);
									MLog.d("voiceDecode:::::::"
											+ new String(voiceDecode));
									try {
										OutputStream os = new FileOutputStream(
												file);
										os.write(voiceDecode);
										os.flush();
										os.close();
									} catch (FileNotFoundException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									}

								}
							});
					// 并播放
					playVoice(voicePath, ivVoice);
				}

			}
		});
	}

	private void playVoice(String fileName, final ImageView ivVoice) {
		ivVoice.setImageResource(R.anim.voice_send);
		final AnimationDrawable animationDrawable = (AnimationDrawable) ivVoice
				.getDrawable();
		try {
			MLog.d("filename++++" + fileName);
			mp.setDataSource(fileName);
			mp.prepare();
			if (animationDrawable != null) {
				animationDrawable.start();
			}
			mp.start();
			mp.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.reset();
					animationDrawable.stop();
					ivVoice.clearAnimation();
					ivVoice.setImageResource(R.drawable.send_3);
				}
			});
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("deprecation")
	private void sendingTextMessage(final MsgInfor msgInfor, final int position) {
		if (msgInfor != null) {
			String content = msgInfor.getContent().getContent();
			String targetId = msgInfor.getTargetId();
			if (msgInfor.getSentStatus() != 0) {
				// SentStatus.SENDING.getValue()=10
				if (msgInfor.getSentStatus() == SentStatus.SENDING.getValue()) {
					TextMessage textMessage = TextMessage.obtain(content);
					textMessage.setExtra(gson.toJson("helloExtra"));
					MLog.d("sendingTextMessage" + "&TargetId::" + targetId);
					RongIM.getInstance()
							.getRongIMClient()
							.sendMessage(Conversation.ConversationType.PRIVATE,
									targetId, textMessage, "", "",
									new RongIMClient.SendMessageCallback() {

										@Override
										public void onSuccess(Integer arg0) {
											MLog.d("SendMessageCallback onSuccess arg0:"
													+ arg0);
											refreshSend(SentStatus.SENT,
													msgInfor);
											refreshList(SentStatus.SENT,
													position);
										}

										@Override
										public void onError(Integer integer,
												ErrorCode errorcode) {
											MLog.d("SendMessageCallback onError arg0:"
													+ integer
													+ ",arg1:"
													+ errorcode);
											showUpLoadFailedToast();
										}
									});
				}
			}
		}

	}

	private void refreshSend(SentStatus sent, MsgInfor msgInfor) {
		Api.updateMsgSentState(sent, msgInfor);

	}

	private void refreshList(SentStatus sent, int position) {
		msgInforList.get(position).setSentStatus(sent.getValue());
	}

	private void showUpLoadFailedToast() {
		MUIToast.toast(mContext, "上传融云失败");

	}

	

}
