package cn.edu.cwnu.gwz.fault.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GrassGan on 2016/7/3.
 */
public class DateTools {

    public static int getYear(){
        Date date =new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        return new Integer(df.format(date));
    }

    public static int getMonth(){
        Date date =new Date();
        SimpleDateFormat df = new SimpleDateFormat("MM");
        return new Integer(df.format(date));
    }

    public static int getDayOfMonth(){
        Date date =new Date();
        SimpleDateFormat df = new SimpleDateFormat("dd");
        return new Integer(df.format(date));
    }
}
