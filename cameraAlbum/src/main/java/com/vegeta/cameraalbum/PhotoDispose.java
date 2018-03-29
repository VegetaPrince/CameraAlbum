package com.vegeta.cameraalbum;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.Date;

/**
 * Photograph操作处理对象
 * （打开图库和打开照相机兼容到android 7.0。自动压缩到指定大小。）
 * 如有疑问请联系我
 * 联系QQ：543945827
 *
 * @author Liqi
 */
class PhotoDispose implements OnDisposePhotoListener<File>, OnDisposeOuterListener {
    private static PhotoDispose mPhotoDispose;
    /**
     * 图库的标记19版本以下
     */
    private final int SDK_19_BOTTOM = 0x1;
    /**
     * 图库的标记19版本以上
     */
    private final int SDK_19_TOP = 0x2;
    /**
     * 相机的标记
     */
    private final int SDK_PHOTOGRAPH = 0x3;
    /**
     * 拍照成功，存储文件对象
     */
    private File mImageFile;
    /**
     * 默认系统存储路径
     */
    private String mSystemPath;
    /**
     * 配置对象
     */
    private PhotoConfig mPhotoConfig;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (mPhotoConfig != null) {
                OnGetDataPhotoListener<File> getDataPhotoListener = mPhotoConfig.getOnPhotographGetDataListener();
                if (null != getDataPhotoListener)
                    getDataPhotoListener.getPhotoData(msg.obj == null ? null : (File) msg.obj);
            }
        }
    };

    private PhotoDispose() {

    }

    static PhotoDispose getPhotographDispose() {
        return mPhotoDispose = null == mPhotoDispose ? new PhotoDispose() : mPhotoDispose;
    }

    OnDisposeOuterListener init(@NonNull PhotoConfig photoConfig) {
        this.mPhotoConfig = photoConfig;
        mSystemPath = getSystemPath();
        return mPhotoDispose;
    }

    /**
     * 打开照相机
     */
    @Override
    public void startCamera() {
        camera();
    }

    /**
     * 打开相册
     */
    @Override
    public void startPhoto() {
        photo();
    }

    /**
     * 处理activity界面中图片回调操作
     *
     * @param requestCode
     * @param data
     */
    @Override
    public void onActivityResult(final int requestCode, final Intent data) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (null != mPhotoConfig) {
                    Activity activity = mPhotoConfig.getActivity();
                    File file = null;
                    switch (requestCode) {
                        case SDK_19_BOTTOM:
                            if (null != data)
                                file = getFile(new PhotoSDKBottomListener<>(activity, PhotoDispose.this), data.getData());
                            break;
                        case SDK_19_TOP:
                            if (null != data)
                                file = getFile(new PhotoSDKTopListener<>(activity, PhotoDispose.this), data.getData());
                            break;
                        case SDK_PHOTOGRAPH:
                            if (null != mImageFile)
                                // 压缩拍照照片
                                file = getDisposePhotoData(mImageFile.getPath());
                            if (mPhotoConfig.isDelePGImage()) {
                                if (null != file) {
                                    // 删掉没有压缩的照片
                                    mImageFile.delete();
                                    mImageFile = null;
                                }
                            }
                            break;
                    }
                    Message message = mHandler.obtainMessage();
                    message.obj = file;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    @Override
    public void clear() {
        mPhotoConfig = null;
    }

    /**
     * 采用策略模式去获取File
     *
     * @param onPhotoSDKDisposeListener 获取File的接口
     * @param uri                       uri地址
     * @return
     */
    private File getFile(OnDisposePhotoSDKListener<File> onPhotoSDKDisposeListener, Uri uri) {
        return onPhotoSDKDisposeListener.getPhotoData(uri);
    }

    /**
     * 相册调用
     */
    private void photo() {
        Activity activity = mPhotoConfig.getActivity();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setType("image/*");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            activity.startActivityForResult(intent, SDK_19_TOP);
        } else {
            activity.startActivityForResult(intent, SDK_19_BOTTOM);
        }
    }

    /**
     * 相机调用
     */
    private void camera() {
        Activity activity = mPhotoConfig.getActivity();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mImageFile = getPath("", mPhotoConfig.getImagePath());
        Uri mOutPutFileUri;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            String packageName = activity.getApplication().getPackageName();
            //7.0需要
            mOutPutFileUri = FileProvider.getUriForFile(activity,packageName+".photograph.utils", mImageFile);
        } else {
            mOutPutFileUri = Uri.fromFile(mImageFile);
        }
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // 拍照图片地址存储地址写入
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);
        activity.startActivityForResult(intent, SDK_PHOTOGRAPH);
    }

    @Override
    public File getDisposePhotoData(String path) {
        if (mPhotoConfig == null)
            return null;

        File file = getPath(DisposeImage.getImageName(path), mPhotoConfig.getCompressPath());
        // 判断用户选择的图片是否已经压缩过
        if (!file.exists()) {
            Bitmap bitmap = DisposeImage.acquireBitmap(path, 0, 0);
            // 压缩图片
            file = bitmapToFile(bitmap, file.getPath());
            if (null != bitmap) {
                // 把获取的bitmap对象回收掉。防止内存溢出
                bitmap.recycle();
                System.gc();
            }
        }
        return file;
    }

    /**
     * 把bitmap转换成File对象
     *
     * @param bitmap
     * @param path   压缩图片存储路径
     * @return
     */
    private File bitmapToFile(Bitmap bitmap, String path) {
        if (mPhotoConfig == null)
            return null;

        if (null != bitmap) {
            byte[] bytes = DisposeImage.compressBmpFromByte(bitmap, mPhotoConfig.getImageSize());
            return DisposeImage.acquireByteFile(bytes, path);
        }
        return null;
    }

    /**
     * 获取图片压缩存储之后存储路径
     *
     * @param imageName 图片名字
     * @param path      存储图片的文件地址
     * @return
     */
    private File getPath(String imageName, String path) {
        if ("".equals(imageName)) {
            Date date = new Date();
            long time = date.getTime();
            imageName = String.valueOf(time) + ".jpg";
        }
        File file = new File(mSystemPath + File.separator + path);
        // 判断存储图片的文件夹是否存在
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(file.getPath(), imageName);
    }


    /**
     * 获取手机存储图片保存路径
     *
     * @return
     */
    private String getSystemPath() {
        String path;
        // 判断是否安装有SD卡
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().toString();
        } else {
            path = mPhotoConfig.getActivity().getFilesDir().toString();
        }
        return path;
    }
}
