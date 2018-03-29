package com.vegeta.cameraalbum;

import android.net.Uri;

/**
 * Uri转换其它对象接口
 * 
 * @author Liqi
 * 
 * @param <T>
 */
public interface OnDisposePhotoSDKListener<T> {
	/**
	 * 把Uri转换成其它对象
	 * 
	 * @param uri
	 * @return
	 */
	public T getPhotoData(Uri uri);
}
