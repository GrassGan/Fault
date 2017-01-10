package cn.edu.cwnu.gwz.fault.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import cn.edu.cwnu.gwz.fault.Activity.LoginActivity;
import cn.edu.cwnu.gwz.fault.Activity.SelectActivity;

/**
 * Created by GrassGan on 2016/6/16.
 */
public class Out {
    public static void outToLogin(Activity activity){
        Intent intent = new Intent();
        intent.setClass(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //注意本行的FLAG设置
        SharedPreferences preferences = activity.getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        activity.startActivity(intent);
        activity.finish();
    }

    public static void outToSelect(Activity activity){
        Intent intent = new Intent();
        intent.setClass(activity, SelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //注意本行的FLAG设置
        activity.startActivity(intent);
        activity.finish();
    }
}
