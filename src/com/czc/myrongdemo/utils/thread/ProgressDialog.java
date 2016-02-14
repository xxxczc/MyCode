package com.czc.myrongdemo.utils.thread;



import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.czc.myrongdemo.R;

/**
 * progressDialog基础�? * 
 * @author 
 * 
 */
public class ProgressDialog extends Dialog {
	Context context = null;// 当前活动的context
	private boolean flag;
	View view = null;
	protected ProgressDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.context = context;
		
	}

	/**
	 * 实现dialog的样式显示以及动�?	 * 
	 * @param context
	 *            当前活动的context
	 * @param theme
	 *            显示的样�?	 * @param layout
	 *            布局
	 * @param anim
	 *            旋转动画
	 * @param frontDraw
	 *            文字之前显示的图
	 * @param middleDraw
	 *            文字之后显示的图
	 * @param lastDraw
	 *            任意显示的图片，用作停掉dialog使用
	 * @param msg
	 *            要显示的文字
	 */
	public ProgressDialog(Context context, int theme, int layout, int anim,
			int frontDraw, int middleDraw, int lastDraw, String msg) {

		super(context, theme);
		this.context = context;
		view = LayoutInflater.from(context).inflate(layout, null);
		setWidget(layout, anim, frontDraw, middleDraw, lastDraw, msg);
		setContentView(view);

	}
	/**
	 * 实现dialog的样式显示以及动�?	 * 
	 * @param context
	 *            当前活动的context
	 * @param theme
	 *            显示的样�?	 * @param layout
	 *            布局
	 * @param anim
	 *            旋转动画
	 * @param frontDraw
	 *            文字之前显示的图�?	 * @param middleDraw
	 *            文字之后显示的图�?	 * @param lastDraw
	 *            任意显示的图片，用作停掉dialog使用
	 * @param msg
	 *            要显示的文字
	 * @param flag
	 *            标记
	 */
	public ProgressDialog(Context context, int theme, int layout, int anim,
			int frontDraw, int middleDraw, int lastDraw, String msg,boolean flag) {

		super(context, theme);
		this.context = context;
		this.flag=flag;
		view = LayoutInflater.from(context).inflate(layout, null);
		setWidget(layout, anim, frontDraw, middleDraw, lastDraw, msg);
		setContentView(view);

	}

	/**
	 * 设置控件的显示以及样式变�?	 * 
	 * @param theme
	 *            显示的样�?	 * @param layout
	 *            布局
	 * @param anim
	 *            旋转动画
	 * @param frontDraw
	 *            文字之前显示的图
	 * @param middleDraw
	 *            文字之后显示的图
	 * @param lastDraw
	 *            任意显示的图片，用作停掉dialog使用
	 * @param msg
	 *            要显示的文字
	 */
	private void setWidget(int layout, int anim, int frontDraw, int middleDraw,
			int lastDraw, String msg) {
		ImageView forntLoading = (ImageView) view.findViewById(R.id.imgFront);
		ImageView img_close = (ImageView) view.findViewById(R.id.imgLast);
		try {
			if (frontDraw != 0) {
				forntLoading.setBackgroundResource(frontDraw);
				forntLoading.setVisibility(View.VISIBLE);
			}
			ImageView img_loading = null;
			try {
				img_loading = (ImageView) view.findViewById(R.id.imgMiddle);
				if (middleDraw != 0) {
					img_loading.setBackgroundResource(middleDraw);
					img_loading.setVisibility(View.VISIBLE);
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

			try {
				if (lastDraw != 0) {
					img_close.setBackgroundResource(lastDraw);
					img_close.setVisibility(View.VISIBLE);
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			try {
				TextView tv_msg = (TextView) view.findViewById(R.id.tvMsg);
				if (msg != null && !msg.equals("")) {
					tv_msg.setText(msg);
					tv_msg.setVisibility(View.VISIBLE);
				}
			} catch (NullPointerException e1) {
				e1.printStackTrace();
			}
			try {
				if (anim != 0) {
					RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils
							.loadAnimation(context, anim);
					img_loading.setAnimation(rotateAnimation);
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

			try {
				img_close.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						dismiss();
					}
				});
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}

}