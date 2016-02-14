package com.czc.myrongdemo.adapter;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;

import com.czc.myrongdemo.bean.PhotoAibum;
import com.czc.myrongdemo.bean.PhotoItem;
import com.czc.myrongdemo.view.photoView.PhotoGridItem;

public class PhotoAdappter extends BaseAdapter   {
	private Context context;
	private PhotoAibum aibum;
	private ArrayList<PhotoItem> gl_arr;
	public PhotoAdappter(Context context, PhotoAibum aibum,ArrayList<PhotoItem> gl_arr) {
		this.context = context;
		this.aibum = aibum;
		this.gl_arr=gl_arr;
	}

	@Override
	public int getCount() {
		if (gl_arr==null) {
			return aibum.getBitList().size();
		}else{
			return gl_arr.size();
		}
		
	}

	@Override
	public PhotoItem getItem(int position) {
		if(gl_arr==null){
			return aibum.getBitList().get(position);
		}else{
			return gl_arr.get(position);
		}
		
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PhotoGridItem item;
		if(convertView == null){
			item = new PhotoGridItem(context);
			 item.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,  
                     LayoutParams.MATCH_PARENT));
		}else{
			item = (PhotoGridItem)convertView;
		}
		// 通过ID 加载缩略图
		if (gl_arr==null) {
			Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),  aibum.getBitList().get(position).getPhotoID(), Thumbnails.MICRO_KIND, null);
			item.SetBitmap(bitmap);
	        boolean flag = aibum.getBitList().get(position).isSelect();
			item.setChecked(flag);
		}else{
			Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),  gl_arr.get(position).getPhotoID(), Thumbnails.MICRO_KIND, null);
			item.SetBitmap(bitmap);
		}
		return item;
	}
}
