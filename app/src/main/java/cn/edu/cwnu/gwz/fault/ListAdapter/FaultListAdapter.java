package cn.edu.cwnu.gwz.fault.ListAdapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.edu.cwnu.gwz.fault.R;

public class FaultListAdapter extends BaseAdapter {

    private JSONArray jsonArray;//用于绑定的数据
    private int resource;//绑定的条目界面
    private LayoutInflater inflater;

    public FaultListAdapter(Context context, JSONArray jsonArray, int resource) {
        this.jsonArray = jsonArray;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return jsonArray.get(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonArray.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject == null) {
            return view;
        }
        if (view == null) {
            view = inflater.inflate(resource, null);//生成条目界面对象
        }
        TextView textView = (TextView) view.findViewById(R.id.item_activity_faultlist_textView);
        TextView textView1 = (TextView) view.findViewById(R.id.item_activity_faultlist_textView1);
        TextView textView2 = (TextView) view.findViewById(R.id.item_activity_faultlist_textView2);
        TextView textView3 = (TextView) view.findViewById(R.id.item_activity_faultlist_textView3);
        TextView textView4 = (TextView) view.findViewById(R.id.item_activity_faultlist_textView4);
        TextView textView5 = (TextView) view.findViewById(R.id.item_activity_faultlist_textView5);


        try {

            textView.setText("故障号: " + jsonObject.getString("flSn"));//故障号
            textView1.setText("障碍类型: " + jsonObject.getString("flftSn"));//障碍类型
            textView2.setText("申告日期: " + jsonObject.getString("flDate").split("\\.")[0]);//申告日期
            textView3.setText("描述: " + jsonObject.getString("flWord"));//故障文字描述
            textView4.setText("发送人员: " + jsonObject.getString("fluiSn"));//发送人员
            textView5.setText("处理情况: " + ( jsonObject.getInt("flStatus") == 0 ? "未处理" : "已处理"));//处理情况
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
}
