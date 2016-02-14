package com.czc.myrongdemo.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.czc.myrongdemo.R;
import com.czc.myrongdemo.adapter.FaceAdapter;
import com.czc.myrongdemo.adapter.ViewPagerAdapter;
import com.czc.myrongdemo.bean.ChatEmoji;
import com.czc.myrongdemo.utils.FaceConversionUtil;

public class MessageInputLayout extends RelativeLayout implements
		View.OnClickListener, OnItemClickListener {
	private ImageView iv_face;
	private Button btn_send;
	private ImageView iv_more;
	private ImageView iv_voice;
	private Button btn_voice;
	private RelativeLayout bottomHideLayout;
	private LinearLayout moreLayout, faceLayout;
	private LinearLayout edit_layout;
	private LinearLayout ll_pic, ll_camera, ll_location,ll_file;
	private Context context;
	private EditText et_sendmessage;
	private OnOperationListener oListener;
	/** 表情页的监听事件 */
	private OnCorpusSelectedListener mListener;

	/** 显示表情页的viewpager */
	private ViewPager vp_face;

	/** 表情页界面集合 */
	private ArrayList<View> pageViews;

	/** 游标显示布局 */
	private LinearLayout layout_point;

	/** 游标点集合 */
	private ArrayList<ImageView> pointViews;

	/** 表情集合 */
	private List<List<ChatEmoji>> emojis;

	/** 表情数据填充器 */
	private List<FaceAdapter> faceAdapters;

	/** 当前表情页 */
	private int current = 0;

	public MessageInputLayout(Context context) {
		super(context);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.message_input, this);
	}

	public MessageInputLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.message_input, this);
	}

	public MessageInputLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.message_input, this);
	}

	public void setOnCorpusSelectedListener(OnCorpusSelectedListener listener) {
		mListener = listener;
	}

	/**
	 * 表情选择监听
	 * 
	 */
	public interface OnCorpusSelectedListener {
		void onCorpusSelected(ChatEmoji emoji);

		void onCorpusDeleted();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		emojis = FaceConversionUtil.getInstace().emojiLists;
		onCreate();
	}

	private void onCreate() {
		initView();
		Init_viewPager();
		Init_Point();
		Init_Data();
	}

	private void initView() {
		et_sendmessage = (EditText) findViewById(R.id.et_sendmessage);
		btn_send = (Button) findViewById(R.id.btn_send);
		iv_face = (ImageView) findViewById(R.id.iv_face);
		iv_more = (ImageView) findViewById(R.id.iv_more);
		iv_voice = (ImageView) findViewById(R.id.iv_voice);
		btn_voice = (Button) findViewById(R.id.btn_voice);
		bottomHideLayout = (RelativeLayout) findViewById(R.id.bottomHideLayout);
		edit_layout = (LinearLayout) findViewById(R.id.edit_layout);
		ll_pic = (LinearLayout) findViewById(R.id.ll_pic);
		ll_camera = (LinearLayout) findViewById(R.id.ll_camera);
		ll_location = (LinearLayout) findViewById(R.id.ll_location);
		ll_file = (LinearLayout) findViewById(R.id.ll_take_file);
		moreLayout = (LinearLayout) findViewById(R.id.moreLayout);
		faceLayout = (LinearLayout) findViewById(R.id.faceLayout);

		layout_point = (LinearLayout) findViewById(R.id.iv_image);
		vp_face = (ViewPager) findViewById(R.id.faceCategroyViewPager);
		iv_voice.setOnClickListener(this);
		iv_face.setOnClickListener(this);
		et_sendmessage.setOnClickListener(this);
		iv_more.setOnClickListener(this);
		ll_pic.setOnClickListener(this);
		ll_camera.setOnClickListener(this);
		ll_location.setOnClickListener(this);
		ll_file.setOnClickListener(this);

		et_sendmessage.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (hasFocus) {
					hideFaceLayout();
				}
				// showKeyboard(context);
			}
		});

		et_sendmessage.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s != null && !"".equals(s.toString().trim())) {
					iv_more.setVisibility(View.GONE);
					btn_send.setEnabled(true);
					btn_send.setVisibility(View.VISIBLE);
				} else {
					iv_more.setVisibility(View.VISIBLE);
					if (iv_more.getVisibility() == View.VISIBLE) {
						btn_send.setVisibility(View.GONE);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}

	/**
	 * 初始化显示表情的viewpager
	 */
	private void Init_viewPager() {
		pageViews = new ArrayList<View>();
		// 左侧添加空页
		View nullView1 = new View(context);
		// 设置透明背景
		nullView1.setBackgroundColor(Color.TRANSPARENT);
		pageViews.add(nullView1);
		// 中间添加表情页
		faceAdapters = new ArrayList<FaceAdapter>();
		for (int i = 0; i < emojis.size(); i++) {
			GridView view = new GridView(context);
			FaceAdapter adapter = new FaceAdapter(context, emojis.get(i));
			view.setAdapter(adapter);
			faceAdapters.add(adapter);
			view.setOnItemClickListener(this);
			view.setNumColumns(7);
			view.setBackgroundColor(Color.TRANSPARENT);
			view.setHorizontalSpacing(1);
			view.setVerticalSpacing(1);
			view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			view.setCacheColorHint(0);
			view.setPadding(5, 0, 5, 0);
			view.setSelector(new ColorDrawable(Color.TRANSPARENT));
			view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			view.setGravity(Gravity.CENTER);
			pageViews.add(view);
		}

		// 右侧添加空页面
		View nullView2 = new View(context);
		// 设置透明背景
		nullView2.setBackgroundColor(Color.TRANSPARENT);
		pageViews.add(nullView2);
	}

	public float getRawSize(int unit, float value) {
		Resources res = this.getResources();
		return TypedValue.applyDimension(unit, value, res.getDisplayMetrics());
	}

	/**
	 * 初始化游标
	 */
	private void Init_Point() {
		pointViews = new ArrayList<ImageView>();
		ImageView imageView;
		for (int i = 0; i < pageViews.size(); i++) {
			imageView = new ImageView(context);
			imageView.setBackgroundResource(R.drawable.d1);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = (int) getRawSize(
					TypedValue.COMPLEX_UNIT_DIP, 5);
			layoutParams.rightMargin = (int) getRawSize(
					TypedValue.COMPLEX_UNIT_DIP, 5);
			layoutParams.width = (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP,
					8);
			layoutParams.height = (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP,
					8);
			layout_point.addView(imageView, layoutParams);
			if (i == 0 || i == pageViews.size() - 1) {
				imageView.setVisibility(View.GONE);
			}
			if (i == 1) {
				imageView.setBackgroundResource(R.drawable.d2);
			}
			pointViews.add(imageView);

		}
	}

	/**
	 * 填充数据
	 */
	private void Init_Data() {
		vp_face.setAdapter(new ViewPagerAdapter(pageViews));

		vp_face.setCurrentItem(1);
		current = 0;
		vp_face.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				current = arg0 - 1;
				// 描绘分页点
				draw_Point(arg0);
				// 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
				if (arg0 == pointViews.size() - 1 || arg0 == 0) {
					if (arg0 == 0) {
						vp_face.setCurrentItem(arg0 + 1);// 第二屏 会再次实现该回调方法实现跳转.
						pointViews.get(1).setBackgroundResource(R.drawable.d2);
					} else {
						vp_face.setCurrentItem(arg0 - 1);// 倒数第二屏
						pointViews.get(arg0 - 1).setBackgroundResource(
								R.drawable.d2);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	/**
	 * 绘制游标背景
	 */
	public void draw_Point(int index) {
		for (int i = 1; i < pointViews.size(); i++) {
			if (index == i) {
				pointViews.get(i).setBackgroundResource(R.drawable.d2);
			} else {
				pointViews.get(i).setBackgroundResource(R.drawable.d1);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ChatEmoji emoji = (ChatEmoji) faceAdapters.get(current).getItem(
				position);
		if (emoji.getId() == R.drawable.face_del_icon) {
			int selection = et_sendmessage.getSelectionStart();
			String text = et_sendmessage.getText().toString();
			if (selection > 0) {
				String text2 = text.substring(selection - 1);
				if ("]".equals(text2)) {
					int start = text.lastIndexOf("[");
					int end = selection;
					et_sendmessage.getText().delete(start, end);
					return;
				}
				et_sendmessage.getText().delete(selection - 1, selection);
			}
		}
		if (!TextUtils.isEmpty(emoji.getCharacter())) {
			if (mListener != null)
				mListener.onCorpusSelected(emoji);
			SpannableString spannableString = FaceConversionUtil.getInstace()
					.addFace(getContext(), emoji.getId(), emoji.getCharacter());
			et_sendmessage.append(spannableString);
		}

	}

	public void showFaceLayout() {
		hideKeyboard(this.context);

		postDelayed(new Runnable() {
			@Override
			public void run() {
				moreLayout.setVisibility(View.GONE);
				faceLayout.setVisibility(View.VISIBLE);
				bottomHideLayout.setVisibility(View.VISIBLE);
			}
		}, 50);
	}

	public void showMoreLayout() {
		hideKeyboard(this.context);

		postDelayed(new Runnable() {
			@Override
			public void run() {
				moreLayout.setVisibility(View.VISIBLE);
				faceLayout.setVisibility(View.GONE);
				bottomHideLayout.setVisibility(View.VISIBLE);
			}
		}, 50);
	}

	public void hideFaceLayout() {
		moreLayout.setVisibility(View.GONE);
		faceLayout.setVisibility(View.GONE);
		bottomHideLayout.setVisibility(View.GONE);
	}

	/**
	 * 隐藏软键盘
	 * 
	 * @param activity
	 */
	public static void hideKeyboard(Context context) {
		Activity activity = (Activity) context;
		if (activity != null) {
			InputMethodManager imm = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive() && activity.getCurrentFocus() != null) {
				imm.hideSoftInputFromWindow(activity.getCurrentFocus()
						.getWindowToken(), 0);
			}
		}
	}

	/**
	 * 显示软键盘
	 * 
	 * @param activity
	 */
	public static void showKeyboard(Context context) {
		Activity activity = (Activity) context;
		if (activity != null) {
			// 获取输入控制管理器服务
			InputMethodManager imm = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInputFromInputMethod(activity.getCurrentFocus()
					.getWindowToken(), 0);
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_voice:
			if (edit_layout.getVisibility() == VISIBLE) {
				hideFaceLayout();
				hideKeyboard(context);
				iv_voice.setImageResource(R.drawable.rc_message_bar_keyboard);
				edit_layout.setVisibility(View.GONE);
				btn_voice.setVisibility(View.VISIBLE);
			} else if (edit_layout.getVisibility() == GONE) {
				iv_voice.setImageResource(R.drawable.rc_message_bar_vioce_icon);
				edit_layout.setVisibility(View.VISIBLE);
				btn_voice.setVisibility(View.GONE);
			}
			break;
		case R.id.iv_face:
			if (faceLayout.getVisibility() == VISIBLE) {
				hideFaceLayout();
				showKeyboard(context);
			} else {
				showFaceLayout();
			}
			break;
		case R.id.et_sendmessage:
			hideFaceLayout();
			break;
		case R.id.iv_more:
			if (moreLayout.getVisibility() == VISIBLE) {
				hideFaceLayout();
				// showKeyboard(context);
			} else {
				if (btn_voice.getVisibility() == VISIBLE) {
					iv_voice.setImageResource(R.drawable.rc_message_bar_vioce_icon);
					btn_voice.setVisibility(View.GONE);
					edit_layout.setVisibility(View.VISIBLE);
				}
				showMoreLayout();
			}
			break;
		case R.id.ll_pic:
			oListener.selectPic();
			break;
		case R.id.ll_camera:
			oListener.selectCamera();
			break;
		case R.id.ll_location:
			oListener.selectLocation();
			break;
		case R.id.ll_take_file:
			oListener.selectFile();
			break;
		
		}
	}

	public interface OnOperationListener {
		public void selectPic();

		public void selectCamera();

		public void selectLocation();
		
		public void selectFile();
	}

	public void setOnOperationListener(OnOperationListener listener) {
		oListener = listener;
	}
}
