package cn.edu.cwnu.gwz.fault.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import cn.edu.cwnu.gwz.fault.tools.FormFile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

public class NetConfig {

    /**
     * 登录接口
     *
     * @param phone    账号
     * @param password 密码
     * @param context  上下文对象
     * @return 0：用户不存在，1：账号停用，2：密码错误，3：登录成功，4：联网出错
     */
    public static int handleLogin(String phone, String password, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        try {
            JSONObject jsonObject = WebService.login(phone, password);
            if (jsonObject.getInt("isexit") == 1) {
                return 0;
            }
            if (jsonObject.getInt("isexit") == 3) {
                return 2;
            }
            if (jsonObject.getInt("uiStatus") == 0) {
                return 1;
            }
            if (jsonObject.getInt("isexit") == 2) {
                editor.putInt("uiAuthorize", jsonObject.getInt("uiAuthorize"));//账户类型：1为障碍申告，2为障碍处理，3为系统管理
                editor.putInt("uiDeptSn", jsonObject.getInt("uiDeptSn"));//所属部门
                editor.putString("uiPassword", jsonObject.getString("uiPassword"));
                editor.putString("uiPhone", phone);//电话号码
                editor.putString("token", jsonObject.getString("token"));
                editor.putString("uiSn", jsonObject.getString("uiSn"));//服务器用主键
                editor.putString("uiName", jsonObject.getString("uiName"));//员工工号
                editor.commit();
                return 3;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 4;
    }

    public static boolean handleChangePassword(Context context, String password) {
        SharedPreferences preferences = context.getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        try {
            JSONObject jsonObject = WebService.changePassword(preferences.getString("uiSn", ""), password);
            if (jsonObject.getInt("isexit") == 0) {
                return false;
            }
            if (jsonObject.getInt("isexit") == 1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static JSONObject handleGetInfo(Context context){
        SharedPreferences preferences = context.getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        try {
            return WebService.getInfo(preferences.getString("uiSn",""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 障碍申告接口
     * @param context 上下文对象
     * @param flftSn 障碍类型 1：普通障碍，2:特殊障碍
     * @param text 障碍文字描述
     * @param file 上传的文件集
     * @return
     */
    public static boolean handleReport(Context context, String flftSn, String text, FormFile[] file){
        SharedPreferences preferences = context.getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        try {
            return WebService.report(flftSn, preferences.getString("uiSn",""), text, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static JSONArray handleGetFaultList(Context context , String pageIndex){
        SharedPreferences preferences = context.getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        try {
            return WebService.getFaultList(preferences.getString("uiSn",""), preferences.getInt("uiAuthorize",0), pageIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray handleGetFaultList(Context context, String pageIndex, int uiAuthorize){
        SharedPreferences preferences = context.getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        try {
            return WebService.getFaultList(preferences.getString("uiSn",""), uiAuthorize, pageIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray handleGetFaultList(Context context , String pageIndex, Map<String, String> map){
        SharedPreferences preferences = context.getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        try {
            return WebService.getFaultList(preferences.getString("uiSn",""), preferences.getInt("uiAuthorize",0), pageIndex, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray handleGetFaultList(Context context , String pageIndex, Map<String, String> map, int uiAuthorize){
        SharedPreferences preferences = context.getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        try {
            return WebService.getFaultList(preferences.getString("uiSn",""), uiAuthorize, pageIndex, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray handleGetFaultList2(Context context , String pageIndex, Map<String, String> map){
        SharedPreferences preferences = context.getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        try {
            return WebService.getFaultList2(preferences.getInt("uiDeptSn",-1) + "", preferences.getInt("uiAuthorize",0), pageIndex, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject handleGetFaultInfo(String flsn){
        try {
            return WebService.getFaultInfo(flsn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray handleGetHandleList(Context context , String pageIndex){
        SharedPreferences preferences = context.getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        try {
            return WebService.getHandleList(preferences.getString("uiSn",""), preferences.getInt("uiAuthorize",0), pageIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray handleGetHandleList(Context context , String pageIndex, Map<String, String> map){
        SharedPreferences preferences = context.getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        try {
            return WebService.getHandleList(preferences.getString("uiSn",""), preferences.getInt("uiAuthorize",0), pageIndex, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject handleGetHandleInfo(String fdsn){
        try {
            return WebService.getHandleInfo(fdsn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean handleHandle(Context context, String fdFlsn, String fdWord, FormFile[] file){
        SharedPreferences preferences = context.getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        try {
            return WebService.handle(fdFlsn, preferences.getString("uiSn",""), fdWord, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String handleDownload(Context context, String path){
        try {
            byte[] bytes = WebService.download(path);
            if (bytes.length < 2) {
                return null;
            }
            String dir = context.getFilesDir().toString();
            String fileName = path.replaceAll("files\\/","");
            File file = new File(dir,fileName);
            FileOutputStream out = new FileOutputStream(file);
            out.write(bytes);
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
