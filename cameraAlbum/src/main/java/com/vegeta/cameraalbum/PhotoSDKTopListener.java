package com.vegeta.cameraalbum;

import android.app.Activity;
import android.net.Uri;

/**
 * SDK高于19相册处理对象
 * 
 * @author Liqi
 * 
 * @param <T>
 */
class PhotoSDKTopListener<T> implements OnDisposePhotoSDKListener<T> {
	private OnDisposePhotoListener<T> onDisposePhotoListener;
	private Activity activity;

	PhotoSDKTopListener(Activity activity,
						OnDisposePhotoListener<T> onDisposePhotoListener) {
		this.onDisposePhotoListener = onDisposePhotoListener;
		this.activity = activity;
	}

	@Override
	public T getPhotoData(Uri uri) {
		if (null != activity) {
			String path = GetPhotoAddressUtil.getPath(activity, uri);
			if (null != onDisposePhotoListener) {
				return onDisposePhotoListener.getDisposePhotoData(path);
			} else
				return null;
		} else
			return null;
	}

}
