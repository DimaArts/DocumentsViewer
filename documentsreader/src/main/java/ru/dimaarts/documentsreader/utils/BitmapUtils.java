package ru.dimaarts.documentsreader.utils;

import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.opengl.GLES10;

/**
 * Created by Дмитрий on 03.04.2016.
 */
public final class BitmapUtils {
    public static int getMaxTextureSize() {
        int[] maxTextureSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
        if (maxTextureSize[0] != 0)
            return maxTextureSize[0];
        else
            return 2048;
    }

    public static Point bitmapSize(String path) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opt);
        return new Point(opt.outWidth, opt.outHeight);
    }

    public static int calculateInSampleSize(int bitmapWidth, int bitmapHeight, int reqWidth, int reqHeight)
    {
        double inSampleSize = 1D;

        if (bitmapHeight > reqHeight || bitmapWidth > reqWidth)
        {
            inSampleSize *= 2;

            while (((float) bitmapHeight / inSampleSize) > reqHeight || ((float) bitmapWidth / inSampleSize) > reqWidth)
            {
                inSampleSize *= 2;
            }
        }

        return (int)inSampleSize;
    }
}
