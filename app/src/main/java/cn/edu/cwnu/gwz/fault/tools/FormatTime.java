package cn.edu.cwnu.gwz.fault.tools;

/**
 * Created by GrassGan on 2016/6/17.
 */
public class FormatTime {
    public static String msToMin(int ms){
        int s = msToS(ms);
        int min = s/60;
        s = s % 60;
        return String.format("%02d", min) + ":" + String.format("%02d", s);
    }

    public static int msToS(int ms){
        return ms/1000;
    }
}
