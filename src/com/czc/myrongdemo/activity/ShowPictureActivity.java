package com.czc.myrongdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Toast;

import com.czc.myrongdemo.R;
import com.czc.myrongdemo.view.ZoomImageView;
import com.czc.myrongdemo.view.ZoomImageView.OnSingleClickListener;

public class ShowPictureActivity extends Activity implements /*OnSingleClickListener,*/ OnTouchListener {
	private ZoomImageView zoomImageView;
	private float startX;
	private float startY;
	private float upX;
	private float upY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// 去除title
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 去掉Activity上面的状态栏

		setContentView(R.layout.activity_show_picture);
		initView();
		initData();
	}

	private void initView() {
		zoomImageView = (ZoomImageView) this.findViewById(R.id.zoom_image_view);

	}

	private void initData() {
		Intent intent = getIntent();
		String imagePath = intent.getStringExtra("photo") == null ? intent
				.getStringExtra("thumbnail") : intent.getStringExtra("photo");
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
		zoomImageView.setImageBitmap(bitmap);
		zoomImageView.setOnTouchListener(this);
//		zoomImageView.setOnSingleClickListener(this);

	}


	@Override
	public boolean onTouch(View view, MotionEvent motionevent) {
		switch (motionevent.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = motionevent.getX();
			startY = motionevent.getY();
			break;
		case MotionEvent.ACTION_UP:
			upX = motionevent.getX();
			upY = motionevent.getY();
			if(Math.abs(startX - upX)<5 && Math.abs(startY - upY)<5){
				this.finish();
			}
			break;

		default:
			break;
		}
		return false;
	}

//	@Override
//	public void onSingleClick(View v) {
//		this.finish();
//		
//	}
	
}
