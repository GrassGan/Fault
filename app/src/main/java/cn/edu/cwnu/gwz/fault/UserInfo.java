package cn.edu.cwnu.gwz.fault;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by GrassGan on 2016/6/5.
 */
public class UserInfo {

    public static boolean setInfo(Context context){
        SharedPreferences preferences = context.getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.commit();
        return true;
    }

    public static String getToken(Context context){
        SharedPreferences preferences = context.getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        return preferences.getString("token","");
    }
}
