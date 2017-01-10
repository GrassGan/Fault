package cn.edu.cwnu.gwz.fault.tools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by GrassGan on 2016/6/19.
 */
public class BitmapTools {

    public static Bitmap bitmapNotOOM(Activity activity, String absolutePath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(absolutePath, options);
        int picWidth  = options.outWidth;
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        options.inSampleSize = (int)(picWidth / (screenWidth/1.5));
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(absolutePath, options);
    }

}
