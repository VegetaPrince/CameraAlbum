package com.vegeta.cameraalbum;

/**
 * 路径转换其它对象接口
 * 
 * @author Liqi
 * 
 * @param <T>
 */
public interface OnDisposePhotoListener<T> {
	/**
	 * 把路径转换成其它对象
	 * 
	 * @param path
	 * @return
	 */
	public T getDisposePhotoData(String path);
}
