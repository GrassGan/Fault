package cn.edu.cwnu.gwz.fault.tools;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GrassGan on 2016/6/16.
 */
public class FileTools {

    public static String getNewName(Context context){
        SharedPreferences preferences = context.getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        int num = preferences.getInt("fileNum", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("fileNum", num + 1);
        editor.commit();
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyMMddhhmmss");
        return preferences.getString("uiPhone", "") + "_" + sDateFormat.format(new Date()) + "_" + num;
    }

    public static void Copy(File oldfile, String newPath)
    {
        try{
            int bytesum = 0;
            int byteread = 0;
            //File     oldfile     =     new     File(oldPath);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldfile);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("error ");
            e.printStackTrace();
        }
    }

}
