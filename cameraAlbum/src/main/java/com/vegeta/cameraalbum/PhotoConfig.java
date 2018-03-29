package com.vegeta.cameraalbum;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * 图片操作处理配置对象
 * Created by LiQi on 2017/3/16.
 */

public class PhotoConfig {
    /**
     * 默认压缩图片存储文件夹路径
     */
    private String mCompressPath = "LiQi/photograph/compresspath";
    /**
     * 拍照暂时存储默认路径
     */
    private String mImagePath = "LiQi/photograph/imagepath";
    /**
     * 默认图片压缩大小(默认压缩到1M)
     */
    private long mImageSize = 1024 * 1024;
    /**
     * 图片处理依赖操作的Activity
     */
    private Activity mActivity;
    /**
     * 获取处理好的图片路径接口
     */
    private OnGetDataPhotoListener<File> getDataPhotoListener;
    /**
     * 是否删除没有压缩的拍照照片（默认删除）
     */
    private boolean isDelePGImage = true;

    private PhotoConfig() {

    }

    String getCompressPath() {
        return mCompressPath;
    }

    String getImagePath() {
        return mImagePath;
    }

    long getImageSize() {
        return mImageSize;
    }

    Activity getActivity() {
        return mActivity;
    }

    boolean isDelePGImage() {
        return isDelePGImage;
    }

    OnGetDataPhotoListener<File> getOnPhotographGetDataListener() {
        return getDataPhotoListener;
    }

    public static class PhotographBuilder {

        private String mCompressPath;

        private String mImagePath;

        private long mImageSize;

        private OnGetDataPhotoListener<File> mOnPhotographGetDataListener;

        private Activity mActivity;

        private boolean isDelePGImage = true;

        PhotographBuilder(@NonNull Activity activity) {
            mActivity = activity;
        }

        public PhotographBuilder setCompressPath(String compressPath) {
            mCompressPath = compressPath;
            return this;
        }

        public PhotographBuilder setImagePath(String imagePath) {
            mImagePath = imagePath;
            return this;
        }

        public PhotographBuilder setImageSize(long imageSize) {
            mImageSize = imageSize;
            return this;
        }

        public PhotographBuilder setOnPhotographGetDataListener(OnGetDataPhotoListener<File> onPhotographGetDataListener) {
            mOnPhotographGetDataListener = onPhotographGetDataListener;
            return this;
        }

        public OnDisposeOuterListener builder() {
            return PhotoDispose.getPhotographDispose().init(init());
        }

        public PhotographBuilder setDelePGImage(boolean delePGImage) {
            isDelePGImage = delePGImage;
            return this;
        }

        private PhotoConfig init() {
            PhotoConfig photoConfig = new PhotoConfig();
            if (null != mCompressPath && !"".equals(mCompressPath))
                photoConfig.mCompressPath = mCompressPath;
            if (null != mImagePath && !"".equals(mImagePath))
                photoConfig.mImagePath = mImagePath;
            if (mImageSize > 0)
                photoConfig.mImageSize = mImageSize;
            photoConfig.mActivity = mActivity;
            photoConfig.getDataPhotoListener = mOnPhotographGetDataListener;
            photoConfig.isDelePGImage = isDelePGImage;
            return photoConfig;
        }
    }
}
