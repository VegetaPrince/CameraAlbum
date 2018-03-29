package com.vegeta.cameraalbum;

import android.app.Activity;

/**
 * 图片框架静态操作对象
 * Created by LiQi on 2017/3/16.
 */

public class PhotoStaticUtils {
    public static PhotoConfig.PhotographBuilder getPhotographBuilder(Activity activity) {
        return new PhotoConfig.PhotographBuilder(activity);
    }
}
