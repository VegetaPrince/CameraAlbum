package com.vegeta.cameraalbum;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * SDK低于19相册处理对象
 * 
 * @author Liqi
 * 
 * @param <T>
 */
 class PhotoSDKBottomListener<T> implements OnDisposePhotoSDKListener<T> {
	private OnDisposePhotoListener<T> onDisposePhotoListener;
	private Activity activity;

	PhotoSDKBottomListener(Activity activity, OnDisposePhotoListener<T> onDisposePhotoListener) {
		this.activity = activity;
		this.onDisposePhotoListener = onDisposePhotoListener;
	}

	@Override
	public T getPhotoData(Uri uri) {
		if (null != activity) {
			String[] proj = { MediaStore.Images.Media.DATA };

			// 好像是android多媒体数据库的封装接口，具体的看Android文档

			Cursor cursor = activity.getContentResolver().query(uri, proj,
					null, null, null);
			// 按我个人理解 这个是获得用户选择的图片的索引值

			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

			// 将光标移至开头 ，这个很重要，不小心很容易引起越界

			cursor.moveToFirst();

			// 最后根据索引值获取图片路径

			String path = cursor.getString(column_index);
			cursor.close();
			if (null != onDisposePhotoListener) {
				return onDisposePhotoListener.getDisposePhotoData(path);
			}
			return null;
		} else
			return null;
	}

}
