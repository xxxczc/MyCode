package com.czc.myrongdemo.utils.uiutils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import com.czc.myrongdemo.R;
import com.czc.myrongdemo.activity.MessageListActivity;


public class MCommonUtil {
	private static final String[] MIME_MapTable = new String[] {
			"application/vnd.android.package-archive",// apk
			"application/octet-stream",// exe
			"video/mp4",// mp4
			"video/mpeg",// mpeg
			"video/x-msvideo",// avi
			"video/3gpp",// 3gp
			"audio/x-pn-realaudio",// rmvb
			"audio/x-wav",// wav
			"audio/x-ms-wma",// wma
			"audio/x-ms-wmv",// wmv
			"audio/x-mpeg",// mp3
			"audio/ogg",// ogg
			"audio/mp4",// amr
			"application/msword",// doc
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document",// docx
			"application/vnd.ms-excel",// xls
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",// xlsx
			"application/vnd.ms-powerpoint",// ppt
			"application/vnd.openxmlformats-officedocument.presentationml.presentation",// pptx
			"application/pdf",// pdf
			"image/jpeg",// jpeg,jpg
			"image/bmp",// bmp
			"image/png",// png
			"image/gif",// gif
			"text/plain",// txt,log,.xml
			".wps",// wps
			"application/x-gzip",// gz
			"application/x-tar",// tar
			"application/zip",// zip
			"application/x-rar-compressed",// rar
	};
	
	
	/**
	 * 格式化文件大小
	 * 
	 * @param context
	 * @param fileLength
	 * @return
	 */
	public static String formatFileSize(Context context, long fileLength) {
		return android.text.format.Formatter
				.formatFileSize(context, fileLength);
	}

	/**
	 * 检查文件是否能被发送
	 * 
	 * @param fileType
	 *            文件MIME类型
	 * @return
	 */
	public static boolean fileCanSend(String fileType) {
		boolean canSend = false;
		for (String allowedFile : MIME_MapTable) {
			if (allowedFile.equals(fileType)) {
				canSend = true;
				break;
			}
		}

		return canSend;
	}

	/**
	 * 根据文件类型获取指定图片
	 * 
	 * @param fileName
	 *            文件名字
	 * @return
	 */
	public static int getFileTypeDrawable(String fileName) {
		if (!TextUtils.isEmpty(fileName)) {
			if (fileName.endsWith(".apk")) {
				return R.drawable.ic_file_apk;
			} else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
				return R.drawable.ic_file_doc;
			} else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
				return R.drawable.ic_file_excel;
			} else if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {
				return R.drawable.ic_file_ppt;
			} else if (fileName.endsWith(".pdf")) {
				return R.drawable.ic_file_pdf;
			} else if (fileName.endsWith(".mp4") || fileName.endsWith(".avi")
					|| fileName.endsWith(".mpeg") || fileName.endsWith(".rmvb")
					|| fileName.endsWith(".wmv") || fileName.endsWith(".3gp")) {
				return R.drawable.ic_file_movie;
			} else if (fileName.endsWith(".mp3") || fileName.endsWith(".amr")
					|| fileName.endsWith(".flac") || fileName.endsWith(".ape")
					|| fileName.endsWith(".wav") || fileName.endsWith(".wma")
					|| fileName.endsWith(".ogg")) {
				return R.drawable.ic_file_music;
			} else if (fileName.endsWith(".zip") || fileName.endsWith(".rar")
					|| fileName.endsWith(".tar") || fileName.endsWith(".7z")
					|| fileName.endsWith(".gz")) {
				return R.drawable.ic_file_zip;
			} else if (fileName.endsWith(".txt") || fileName.endsWith(".log")
					|| fileName.endsWith(".xml")) {
				return R.drawable.ic_file_default;
			}
		}
		return R.drawable.ic_file_default;
	}

	public static int dp2px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	public static int px2dp(Context context, float px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

	public static int sp2px(Context context, float sp) {
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (sp * scale + 0.5f);
	}

	public static int px2sp(Context context, float px) {
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (px / scale + 0.5f);
	}

	/**
	 * 判断文件是否是图片
	 * 
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public static boolean isPicture(String fileName) {
		if (!TextUtils.isEmpty(fileName)
				&& (fileName.endsWith(".jpg") || fileName.endsWith(".png")
						|| fileName.endsWith(".jpeg") || fileName
							.endsWith(".bmp"))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 */
	public static boolean isFileExist(String fileDir, String fileName) {
		File file = new File(fileDir + fileName);
		return file.exists();
	}
	
	public static boolean isFileExist(File fileDir, String fileName) {
		File file = new File(fileDir,fileName);
		return file.exists();
	}
	
	public static boolean isFileExist(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

	public interface VoiceFileDownloadListener {
		void onFail();

		void onSuccess(File file);
	}

	

	private static File byte2File(byte[] buf, File fileDir, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			file = new File(fileDir, fileName);
			if (!file.exists())
				file.createNewFile();
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(buf);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return file;
	}
	
	/**
     * 获得当前的版本信息
     *
     * @return
     */
    public static String[] getVersionInfo(Context context) {
        String[] version = new String[2];

        PackageManager packageManager = context.getPackageManager();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version[0] = String.valueOf(packageInfo.versionCode);
            version[1] = packageInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }
    
    /**
     * 得到状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Activity context) {
        int statusHeight = 0;
        Rect frame = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        statusHeight = frame.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }
    
    /**
     * 得到屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Activity context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;

    }

    /**
     * 得到屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Activity context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }
    
    private static final String IMAGE_TYPE = "image/*";
    /**
     * 打开照相机，没有照片存储路径
     * @param activity
     * @param requestCode
     */
    public static void openCamera(Activity activity, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, requestCode);
    }
 
    /**
     * 打开照相机
     * @param activity 当前的activity
     * @param requestCode 拍照成功时activity forResult 的时候的requestCode
     * @param photoFile 拍照完毕时,图片保存的位置
     */
    public static void openCamera(Activity activity, int requestCode, File photoFile) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        activity.startActivityForResult(intent, requestCode);
    }
 
    /**
     * 本地照片调用
     * @param activity
     * @param requestCode
     */
    public static void openPhotos(Activity activity, int requestCode) {
        if (openPhotosNormal(activity, requestCode) && openPhotosBrowser(activity, requestCode) && openPhotosFinally(activity));
    }
 
    /**
     * PopupMenu打开本地相册.
     */
    private static boolean openPhotosNormal(Activity activity, int actResultCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_TYPE);
        try {
            activity.startActivityForResult(intent, actResultCode);
        } catch (android.content.ActivityNotFoundException e) {
            return true;
        }
        return false;
    }
 
    /**
     * 打开其他的一文件浏览器,如果没有本地相册的话
     */
    private static boolean openPhotosBrowser(Activity activity, int requestCode) {
        Toast.makeText(activity, "没有相册软件，运行文件浏览器", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
        intent.setType(IMAGE_TYPE);
        Intent wrapperIntent = Intent.createChooser(intent, null);
        try {
            activity.startActivityForResult(wrapperIntent, requestCode);
        } catch (android.content.ActivityNotFoundException e1) {
            return true;
        }
        return false;
    }
 
    /**
     * 这个是找不到相关的图片浏览器,或者相册
     */
    private static boolean openPhotosFinally(Activity activity) {
        Toast.makeText(activity, "您的系统没有文件浏览器或则相册支持,请安装！", Toast.LENGTH_LONG).show();
        return false;
    }
 
    /**
     * 获取从本地图库返回来的时候的URI解析出来的文件路径
     * @return
     */
    public static String getPhotoPathByLocalUri(Context context, Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

	@SuppressLint("NewApi")
	public static String getFilePathFromKitKat(
			Context context, Uri uri) {
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}

			}else if (isDownloadsDocument(uri)) {	// DownloadsProvider

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}else if (isMediaDocument(uri)) {	// MediaProvider
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}else if ("content".equalsIgnoreCase(uri.getScheme())) {	// MediaStore (and general)
			return getDataColumn(context, uri, null, null);
		}else if ("file".equalsIgnoreCase(uri.getScheme())) {	// File
			return uri.getPath();
		}

		return null;
	}
	
	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}
	

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}
	
	
	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}
	
	
	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

}
