package ru.dimaarts.documentsreader.utils;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

/**
 * Created by rodionovss on 20.07.2016.
 */
public class ScreenUtils {
    public static Point getScreenSize(Activity activity) {
        Point size = new Point();
        if(activity == null) return size;
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        return size;
    }
}
