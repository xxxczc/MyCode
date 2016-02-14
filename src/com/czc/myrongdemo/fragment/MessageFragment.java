package com.czc.myrongdemo.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.litepal.crud.DataSupport;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.czc.myrongdemo.Api;
import com.czc.myrongdemo.R;
import com.czc.myrongdemo.activity.MessageListActivity;
import com.czc.myrongdemo.adapter.MessageListAdapter;
import com.czc.myrongdemo.bean.MsgInfor;
import com.czc.myrongdemo.bean.MsgInforComparator;
import com.czc.myrongdemo.controller.SendMessageController;
import com.czc.myrongdemo.models.ParseMsgInfor;
import com.czc.myrongdemo.utils.DateUtils;
import com.czc.myrongdemo.utils.FileUtils;
import com.czc.myrongdemo.utils.MLog;
import com.czc.myrongdemo.utils.thread.ThreadWithDialogListener;
import com.czc.myrongdemo.utils.thread.ThreadWithDialogTask;
import com.czc.myrongdemo.view.XListView;
import com.czc.myrongdemo.view.XListView.IXListViewListener;

public class MessageFragment extends Fragment implements IXListViewListener,
		OnTouchListener {

	private XListView xListView;
	private View messageFragmentView;

	private List<MsgInfor> msgInforList;
	private MessageListAdapter messageListAdapter;

	private boolean isFirstRefresh = true;

	private ThreadWithDialogTask task;

	private SendMessageController sendMessageController;
	private static MessageFragment messageFragment;
	/**
	 * 数据库分页查询的页码
	 */
	private int pageIndex = 0;

	public MessageFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		MLog.d("MessageFragment-------");
		messageFragmentView = View.inflate(getActivity(),
				R.layout.message_fragment, null);
		
		initView();
		initData();

		MLog.d(getActivity().toString() + "^^^^^^^^^^^");

		return messageFragmentView;
	}

	public static MessageFragment getInstance() {
		if (messageFragment == null) {
			messageFragment = new MessageFragment();
		}
		return messageFragment;
	}

	private void initView() {

		xListView = (XListView) messageFragmentView
				.findViewById(R.id.messageListView);
		xListView.setPullRefreshEnable(true);
		xListView.setPullLoadEnable(false);
	}

	private void initData() {
		// Bundle bundle = getArguments();
		// String downMessageUri = bundle.getString("uri");
		// 给ListView设置数据

		task = new ThreadWithDialogTask();
		msgInforList = new ArrayList<MsgInfor>();

		MLog.d("isFirstRefresh::" + isFirstRefresh);
		messageListAdapter = new MessageListAdapter(getContext(), msgInforList);

		xListView.setXListViewListener(this);
		xListView.setAdapter(messageListAdapter);
		// onRefresh();
		MLog.d("xListView.getCount():::" + xListView.getCount());
		// xListView.setOnTouchListener(this);
		xListView.setOnRefreshing();
		this.setxListView(xListView);
		
		sendMessageController = new SendMessageController(messageFragmentView,
				(MessageListActivity) getActivity(),this);
		sendMessageController.setMessageListAdapter(messageListAdapter);
	}

	/**
	 * 正在刷新
	 */
	@Override
	public void onRefresh() {


		task.RunWithoutDialog(getActivity(), new ThreadWithDialogListener() {

			private List<MsgInfor> msgInforListDB;

			@Override
			public boolean TaskMain() {
				
				// 先从数据库中获取数据
				msgInforListDB = DataSupport.order("id desc").limit(10).offset(10 * pageIndex)
						.find(MsgInfor.class);
				MLog.d("msgInforListDB.size()=" + msgInforListDB.size()
						+ "&&pageIndex=" + pageIndex);
				
				if (msgInforListDB != null && msgInforListDB.size() != 0) {
					pageIndex++;
					for (int i = 0; i < msgInforListDB.size(); i++) {
						MLog.d("msgInforListDB.get(i).getContents()"
								+ msgInforListDB.get(i).getContents().size());
						msgInforListDB.get(i).setContent(
								msgInforListDB.get(i).getContents().get(0));
					}
					String lastDateTime = DataSupport.findLast(MsgInfor.class).getDateTime();
					Api.mdate = DateUtils.formateDBDate(lastDateTime);
					isFirstRefresh = false;
					
				}else if (isFirstRefresh) {
					// 如果数据库中没有了,再请求网络获取融云的历史记录
					FileUtils.downLoadHistoryMessage(Api.HISTORYMESSAGEURI,
							DateUtils.getCurrDate());

				} else {
					
					MLog.d("DateUtils.dateMinus(Api.mdate)::::"
							+ DateUtils.dateMinus(Api.mdate));
					FileUtils.downLoadHistoryMessage(Api.HISTORYMESSAGEURI,
							DateUtils.dateMinus(Api.mdate));
					isFirstRefresh = false;
				}
				return true;
			}

			@Override
			public boolean OnTaskDone() {
				if(msgInforListDB != null && msgInforListDB.size() != 0){
					msgInforList.addAll(msgInforListDB);
					Collections.sort(msgInforList, new MsgInforComparator());
					xListView.setSelection(msgInforListDB.size());
				} else {
					List<MsgInfor> moreMsgInforList = ParseMsgInfor
							.parseMsgInfor(Api.HISTORYMESSAGEDIR
									+ File.separator
									+ DateUtils.formateDate(Api.mdate)
									+ ".json");
					if (moreMsgInforList != null
							&& moreMsgInforList.size() != 0) {
						msgInforList.addAll(moreMsgInforList);
						Collections
								.sort(msgInforList, new MsgInforComparator());
						xListView.setSelection(moreMsgInforList.size());
					}
				}
				messageListAdapter.notifyDataSetChanged();
				loadFinish();
				return true;
			}

			@Override
			public boolean OnTaskDismissed() {
				// TODO Auto-generated method stub
				return true;
			}
		}, isFirstRefresh);

		// FileUtils.downLoadHistoryMessage(Api.HISTORYMESSAGEURI, Api.mdate);
		// handler.sendEmptyMessageDelayed(ONREFRESH, 2000);

	}

	private void loadFinish() {
		xListView.stopRefresh();
		// xListView.stopLoadMore();
		xListView.setRefreshTime("刚刚");
		MLog.d("loadFinish::::::");
	}

	@Override
	public void onLoadMore() {

		// handler.sendEmptyMessageDelayed(ONLOADMORE, 2000);

	}

	// private final int ONREFRESH = 100;
	// private final int ONLOADMORE = 101;

//	private Handler handler = new Handler() {
//		public void handleMessage(Message msg) {
//			ArrayList<MsgInfor> msgInforListDB = (ArrayList<MsgInfor>) msg.obj;
//			msgInforList.addAll(msgInforListDB);
//			messageListAdapter.notifyDataSetChanged();
//			xListView.setSelection(msgInforListDB.size());
//			loadFinish();
//		};
//	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		pageIndex = 0;
		isFirstRefresh = true;
		msgInforList.clear();
		Api.mdate = DateUtils.getCurrDate();
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		switch (motionEvent.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			hideSoftKeyBoard(view);
			break;

		default:
			break;
		}

		return false;
	}

	private void hideSoftKeyBoard(View view) {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}

	}

	public void setMessageFragment(View messageFragment) {
		this.messageFragmentView = messageFragment;
	}

	public XListView getxListView() {
		return xListView;
	}

	public void setxListView(XListView xListView) {
		this.xListView = xListView;
	}
	
	

}
