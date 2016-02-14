package com.czc.myrongdemo.activity;

import java.io.File;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.czc.myrongdemo.Api;
import com.czc.myrongdemo.R;
import com.czc.myrongdemo.controller.SendMessageController;
import com.czc.myrongdemo.controller.SendMessageController.SendMessageHandler;
import com.czc.myrongdemo.fragment.MessageFragment;
import com.czc.myrongdemo.utils.BitmapLoader;
import com.czc.myrongdemo.utils.MLog;
import com.czc.myrongdemo.utils.uiutils.MCommonUtil;
import com.czc.myrongdemo.view.photoView.PhotoAlbumActivity;

import de.greenrobot.event.EventBus;

public class MessageListActivity extends FragmentActivity {

	private MessageFragment messageFragment;
	private FragmentTransaction transaction;

	private SendMessageHandler mHandler;

	private SendMessageController mController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_activity);
		initview();

		EventBus.getDefault().register(this);
	}

	// 接收到消息后更新列表
	public void onEventMainThread(
			final io.rong.imkit.model.Event.OnReceiveMessageEvent event) {
		MLog.i("OnReceiveMessageEvent ," + event.getMessage().getSenderUserId()
				+ ",msgId:" + event.getMessage().getMessageId() + ",objName:"
				+ event.getMessage().getObjectName() + ",targetID:"
				+ event.getMessage().getTargetId() + ",messageContent:"
				+ event.getMessage().getContent() + ",messageDirection:"
				+ event.getMessage().getMessageDirection().getValue()+",messageExtra::::"
				+event.getMessage().getExtra()+ ",messageReceivedStatues:"
				+ event.getMessage().getReceivedStatus().getFlag());
		mController.updateMessageList(event.getMessage());
		MLog.d("mController.updateMessageList执行了");
	}

	private void initview() {

		MLog.d("MesageListActivity__----------------");
		messageFragment = MessageFragment.getInstance();
		transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.message_framelayout, messageFragment).commit();

		// View messageFragmentView = messageFragment.getMessageFragmentView();
		// mController = SendMessageController.getInstance(messageFragmentView,
		// this);
	}

	/**
	 * 打开相机
	 * 
	 * @param mHandler
	 * @param intent
	 * @param sendMessageController
	 */
	public void startPickCameraActivity(SendMessageHandler mHandler,
			Intent intent, SendMessageController sendMessageController) {
		this.mHandler = mHandler;
		// mController = sendMessageController;
		this.startActivityForResult(intent, Api.REQUESTCODE_CAMARA_PHOTO);

	}

	/**
	 * 打开图库
	 * 
	 */
	public void startPickPicActivity(SendMessageHandler mHandler, Intent intent) {
		this.mHandler = mHandler;
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this,
					this.getString(R.string.sdcard_not_exist_toast),
					Toast.LENGTH_SHORT).show();
		} else {
			intent.setClass(this, PhotoAlbumActivity.class);
			startActivityForResult(intent, 1);
		}
	}

	/**
	 * 选择文件
	 * 
	 * @param mHandler2
	 * @param intent
	 */
	public void startPickFileActivity(SendMessageHandler mHandler, Intent intent) {
		this.mHandler = mHandler;
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this,
					this.getString(R.string.sdcard_not_exist_toast),
					Toast.LENGTH_SHORT).show();
		} else {
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			this.startActivityForResult(intent, Api.REQUEST_CODE_TAKE_FILE);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		MLog.d("requestCode:::::::::" + requestCode);
		if (requestCode == Api.REQUESTCODE_CAMARA_PHOTO) {
			try {
				String originPath = mController.getPhotoPath();
				MLog.d("originPath::" + originPath + "%%%%%%%%%");
				Bitmap bitmap = BitmapLoader.getBitmapFromFile(originPath, 720,
						1280);
				String thumbnailPath = BitmapLoader.saveBitmapToLocal(bitmap);
				Intent intent = new Intent();
				intent.putExtra("tempPath", thumbnailPath);
				intent.putExtra("localPath", originPath);

				handleCameraRefresh(intent);
			} catch (NullPointerException e) {
				Toast.makeText(this,
						this.getString(R.string.camera_not_prepared),
						Toast.LENGTH_SHORT).show();
				MLog.e("onActivityResult unexpected result", e);
			} catch (Exception e) {
				Toast.makeText(this,
						this.getString(R.string.camera_not_prepared),
						Toast.LENGTH_SHORT).show();
				MLog.e("onActivityResult unexpected result", e);
			}
		}

		if (resultCode == Api.REQUEST_CODE_SELECT_PICTURE) {

			// 获得上一个Activity返回来的数据
			List pathList = data.getExtras().getStringArrayList("paths");
			MLog.d("pathList:::" + pathList.toString());

			if (pathList != null && pathList.size() > 0) {
				String originPath = (String) pathList.get(0);
				Bitmap bitmap = BitmapLoader.getBitmapFromFile(originPath, 720,
						1280);
				String thumbnailPath = BitmapLoader.saveBitmapToLocal(bitmap);
				Intent intent = new Intent();
				intent.putExtra("tempPath", thumbnailPath);
				intent.putExtra("localPath", originPath);
				handlePictureRefresh(intent);
			}
		}

		if (requestCode == Api.REQUEST_CODE_TAKE_FILE) {
			MLog.d("onActivityResult take file");
			if (data != null) {
				handlePickFile(data);
			} else {
				Toast.makeText(this, R.string.sdcard_not_exist_toast, 0).show();
			}
		}

		// super.onActivityResult(requestCode, resultCode, data);

	}

	/**
	 * 处理选择文件的msg
	 * 
	 * @param data
	 */
	private void handlePickFile(Intent data) {

		String realFilePath = MCommonUtil.getFilePathFromKitKat(this,
				data.getData());
		MLog.i("所选文件的路径是:"+realFilePath);
		try {
			File file = new File(realFilePath);
			if (file.exists() && file.isFile()) {
				long length = file.length();
				if (length > 10 * 1024 * 1024) {
					Toast.makeText(this, "文件过大，请上传小于10M的文件", 0).show();
				} else {
					Message message = mHandler.obtainMessage(
							Api.UPLOAD_FILE_CALLBACK, realFilePath);
					
					Bundle bundle = new Bundle();
					bundle.putLong("fileSize", length);
					message.setData(bundle);
					mHandler.sendMessage(message);
				}
			}
		} catch (Exception e) {
			Toast.makeText(this, "文件异常，发送失败", 0).show();
			MLog.e("pick file", e);
			e.printStackTrace();
		}
	}

	/**
	 * 处理选择图片的消息
	 * 
	 * @param data
	 */
	private void handlePictureRefresh(Intent data) {
		MLog.d("handlePictureRefresh");
		// 判断是否在当前会话中发图片
		Message message = mHandler.obtainMessage(Api.UPLOAD_IMG_CALLBACK, data);
		// 发送的消息全在SendMessageHandler类中处理
		mHandler.sendMessage(message);

	}

	/**
	 * 处理相机发送图片，刷新界面
	 * 
	 * @param data
	 */
	private void handleCameraRefresh(Intent data) {
		MLog.d("handleCameraRefresh");
		// 判断是否在当前会话中发图片
		Message message = mHandler.obtainMessage(Api.UPLOAD_IMG_CALLBACK, data);
		// 发送的消息全在SendMessageHandler类中处理
		mHandler.sendMessage(message);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		transaction.remove(messageFragment);
		EventBus.getDefault().unregister(this);
		MLog.d("MessageListActivityDestroy::::::::::");
	}

	public void setController(SendMessageController sendMessageController) {
		mController = sendMessageController;

	}

}
