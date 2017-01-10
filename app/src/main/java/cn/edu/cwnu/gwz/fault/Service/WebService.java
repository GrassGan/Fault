package cn.edu.cwnu.gwz.fault.Service;

import cn.edu.cwnu.gwz.fault.tools.FormFile;
import cn.edu.cwnu.gwz.fault.tools.SocketHttpRequester;
import cn.edu.cwnu.gwz.fault.tools.StreamTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by GrassGan on 2016/6/8.
 */
public class WebService {
    public static final int POTE = 8080;//端口
    public static final String ADDRESS = "210.41.193.29";//地址

    private static final int LOGIN = 0;
    private static final int CHANGEPASSWORD = 1;
    private static final int GETINFO = 2;
    private static final int GETFAULTLIST = 3;
    private static final int GETFAULTINFO = 4;
    private static final int GETHANDLELIST = 5;
    private static final int GETHANDLEINFO = 6;
    private static final int GETFAULTLIST1 = 7;
    private static final int GETHANDLELIST1 = 8;
    private static final int GETFAULTLIST2 = 9;


    private static final String LOGINPATH = "/Obs_state/user_login.action?json=";//登录
    private static final String CHANGEPASSWORDPATH = "/Obs_state/user_updateInfo.action?json=";//更改密码
    private static final String GETINFOPATH = "/Obs_state/user_getInfo.action?json=";//获取信息
    private static final String REPORTPATH = "/Obs_state/fault_stateFault.action?json=";//障碍申告
    private static final String GETFAULTLISTPATH = "/Obs_state/fault_getFaultList.action?json=";//获取障碍列表
    private static final String GETFAULTINFOPATH = "/Obs_state/fault_getFaultInfo.action?json=";//获取障碍详情
    private static final String GETHANDLELISTPATH = "/Obs_state/faultHandle_getFaultHandle.action?json=";//获取障碍详情
    private static final String GETHANDLEINFOPATH = "/Obs_state/faultHandle_getInfoHandle.action?json=";//获取障碍详情
    private static final String HANDLEPATH = "/Obs_state/faultHandle_dealFault.action?json=";//获取障碍详情
    private static final String GETFAULTLIST1PATH = "/Obs_state/fault_getFaultType.action?json=";
    private static final String GETHANDLELIST1PATH = "/Obs_state/faultHandle_getHandleType.action?json=";
    private static final String GETFAULTLIST2PATH = "/Obs_state/fault_getadminFault.action?json=";

    /**
     * 登录
     * @param phone
     * @param password
     * @return
     * @throws Exception
     */
    public static JSONObject login(String phone, String password) throws Exception {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("uiPhone", phone);
        map.put("uiPassword", password);
        list.add(map);
        JSONArray jsonArr = sendJson(LOGIN, list);
        return jsonArr.getJSONObject(0);
    }

    /**
     * 更改密码
     * @param uisn 服务器主键
     * @param uiPassword 新密码
     * @return
     */
    public static JSONObject changePassword(String uisn, String uiPassword) throws Exception {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("uisn", uisn);
        map.put("uiPassword", uiPassword);
        list.add(map);
        JSONArray jsonArr = sendJson(CHANGEPASSWORD, list);
        return jsonArr.getJSONObject(0);
    }

    public static JSONObject getInfo(String uisn) throws Exception {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("uisn", uisn);
        list.add(map);
        JSONArray jsonArr = sendJson(GETINFO, list);
        return jsonArr.getJSONObject(0);
    }

    /**
     * 障碍申告
     * @param flftSn 障碍类型 1：普通障碍，2:特殊障碍
     * @param fluiSn 服务器主键
     * @param flWord 障碍文字描述
     * @param files 上传的文件集
     * @return 是否上传成功
     * @throws Exception
     */
    public static boolean report(String flftSn, String fluiSn, String flWord, FormFile[] files) throws Exception {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("flftSn", flftSn);
        map.put("fluiSn", fluiSn);
        map.put("flWord", flWord);
        list.add(map);
        String path = REPORTPATH + getJsonArray(list);
        Map<String, String> params = new HashMap<String, String>();
        params.put("json", getJsonArray(list));
        return SocketHttpRequester.post(ADDRESS, POTE, path, params, files);
    }

    public static JSONArray getFaultList(String uisn, int uiauthorize, String pageIndex) throws Exception {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("uisn", uisn);
        map.put("uiauthorize", uiauthorize+"");
        map.put("pageIndex", pageIndex);
        list.add(map);
        JSONArray jsonArray = sendJson(GETFAULTLIST, list);
        return jsonArray;
    }

    public static JSONArray getFaultList(String uisn, int uiauthorize, String pageIndex, Map<String, String> map1) throws Exception {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("uisn", uisn);
        map.put("uiauthorize", uiauthorize+"");
        map.put("pageIndex", pageIndex);
        map.putAll(map1);
        list.add(map);
        JSONArray jsonArray = sendJson(GETFAULTLIST1, list);
        return jsonArray;
    }

    public static JSONArray getFaultList2(String uideptSn, int uiauthorize, String pageIndex, Map<String, String> map1) throws Exception {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("uideptSn", uideptSn);
        map.put("uiauthorize", uiauthorize+"");
        map.put("pageIndex", pageIndex);
        map.putAll(map1);
        list.add(map);
        JSONArray jsonArray = sendJson(GETFAULTLIST2, list);
        return jsonArray;
    }

    public static JSONObject getFaultInfo(String flsn) throws Exception {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("flsn", flsn);
        list.add(map);
        return sendJson(GETFAULTINFO, list).getJSONObject(0);
    }

    public static JSONArray getHandleList(String uisn, int uiauthorize, String pageIndex) throws Exception {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("uisn", uisn);
        map.put("uiauthorize", uiauthorize+"");
        map.put("pageIndex", pageIndex);
        list.add(map);
        JSONArray jsonArray = sendJson(GETHANDLELIST, list);
        return jsonArray;
    }

    public static JSONArray getHandleList(String uisn, int uiauthorize, String pageIndex, Map<String, String> map1) throws Exception {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("uisn", uisn);
        map.put("uiauthorize", uiauthorize+"");
        map.put("pageIndex", pageIndex);
        map.putAll(map1);
        list.add(map);
        JSONArray jsonArray = sendJson(GETHANDLELIST1, list);
        return jsonArray;
    }

    public static JSONObject getHandleInfo(String fdsn) throws Exception {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("fdsn", fdsn);
        list.add(map);
        return sendJson(GETHANDLEINFO, list).getJSONObject(0);
    }

    public static boolean handle(String fdFlsn, String fluiSn, String flWord, FormFile[] files) throws Exception {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("fdFlsn", fdFlsn);//障碍号
        map.put("fdUisn", fluiSn);//服务器主键
        map.put("fdWord", flWord);//处理描述
        list.add(map);
        String path = HANDLEPATH + getJsonArray(list);
        Map<String, String> params = new HashMap<String, String>();
        params.put("json", getJsonArray(list));
        return SocketHttpRequester.post(ADDRESS, POTE, path, params, files);
        //return false;
    }

    public static byte[] download(String filename) throws Exception{
        URL url = new URL("http", ADDRESS, POTE, "/Obs_state/"+filename);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setRequestMethod("GET");
        return StreamTool.read(conn.getInputStream());
    }

    private static JSONArray sendJson(int mode, List<Map<String, String>> list) throws Exception {
        StringBuilder json = new StringBuilder();
        switch (mode) {
            case LOGIN:
                json.append(LOGINPATH);
                break;
            case CHANGEPASSWORD:
                json.append(CHANGEPASSWORDPATH);
                break;
            case GETINFO:
                json.append(GETINFOPATH);
                break;
            case GETFAULTLIST:
                json.append(GETFAULTLISTPATH);
                break;
            case GETFAULTINFO:
                json.append(GETFAULTINFOPATH);
                break;
            case GETHANDLELIST:
                json.append(GETHANDLELISTPATH);
                break;
            case GETHANDLEINFO:
                json.append(GETHANDLEINFOPATH);
                break;
            case GETFAULTLIST1:
                json.append(GETFAULTLIST1PATH);
                break;
            case GETHANDLELIST1:
                json.append(GETHANDLELIST1PATH);
                break;
            case GETFAULTLIST2:
                json.append(GETFAULTLIST2PATH);
                break;
        }
        json.append(getJsonArray(list));
        String path = json.toString();
        HttpURLConnection conn = getHttpURLConnectionMethodPOST(path);
        if (conn.getResponseCode() == 200) {// 解析返回的JSON数据
            InputStream inStream = conn.getInputStream();
            byte[] data = SocketHttpRequester.readStream(inStream);
            String jsonStr = new String(data);
            if (jsonStr == null){
                return null;
            }
            if (jsonStr.equals("")){
                return null;
            }
            JSONArray jsonArr = new JSONArray(jsonStr);
            System.out.println(jsonArr);
            return jsonArr;
        }
        return null;
    }

    /**
     * 通过字符串路径获得连接对象
     *
     * @param path 路径
     * @return HttpURL连接对象
     * @throws Exception
     */
    private static HttpURLConnection getHttpURLConnectionMethodPOST(String path) throws Exception {
        URL url = new URL("http", ADDRESS, POTE, path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("POST");
        return conn;
    }

    /**
     * 构建Json格式的字符串
     *
     * @param list 内容类型为Map<String,String>，该Map对象存放的是Json数据的键值对
     * @return Json格式的字符串
     * @throws JSONException
     */
    private static String getJsonArray(List<Map<String, String>> list) throws JSONException {
        JSONStringer json = new JSONStringer().array();
        for (Map<String, String> map : list) {
            Set<String> keys = map.keySet();
            json = json.object();
            for (String key : keys) {
                json = json.key(key).value(map.get(key));
            }
            json = json.endObject();
        }
        json = json.endArray();
        System.out.println(json.toString()+"***************");
        return json.toString();
    }
}
