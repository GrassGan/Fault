package cn.edu.cwnu.gwz.fault.Activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.edu.cwnu.gwz.fault.ListAdapter.HandleHistoryAdapter;
import cn.edu.cwnu.gwz.fault.R;
import cn.edu.cwnu.gwz.fault.Service.NetConfig;
import cn.edu.cwnu.gwz.fault.tools.DateTools;

public class HandleHistoryActivity extends AppCompatActivity  implements android.view.GestureDetector.OnGestureListener, View.OnTouchListener{

    private ListView listView;
    HandleHistoryAdapter adapter;
//    private TextView pageView, titleView;
//    private Button jump;
    ImageView lastPage_btn, nextPage_btn;
    private ProgressBar progressBar;
    private JSONArray jsonArray;
    private int page = 0;
    private int totalPage;
    GestureDetector detector;
    GetFaultListTask task;
    String title;
    int startYear, startMonth, startDay, endYear, endMonth, endDay;
    String keyWord = "";
    Boolean timeB = false, keyWordB = false;
    int type = 0;
    int uiauthorize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faultlist);
        SharedPreferences preferences = getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        uiauthorize = preferences.getInt("uiAuthorize", -1);
        if (uiauthorize == 2 || uiauthorize == 3){
            title = "处理历史";
        }
        listView = (ListView) findViewById(R.id.activity_faultlist_listView);
        progressBar = (ProgressBar) findViewById(R.id.activity_faultlist_progressbar);
//        pageView = (TextView) findViewById(R.id.activity_faultlist_page);
//        jump = (Button) findViewById(R.id.activity_faultlist_jump);
        lastPage_btn = (ImageView) findViewById(R.id.activity_faultlist_lastpage);
        nextPage_btn = (ImageView) findViewById(R.id.activity_faultlist_nextpage);
        lastPage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HandleHistoryActivity.this.lastPage();
            }
        });
        nextPage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HandleHistoryActivity.this.nextPage();
            }
        });
//        GetFaultListTask task = new GetFaultListTask();
        task = new GetFaultListTask();
        task.execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Intent intent = new Intent(HandleHistoryActivity.this, HandleInfoActivity.class);
                    intent.putExtra("fdsn", jsonArray.getJSONObject(i).getString("fdsn"));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        listView.setOnTouchListener(this);
        detector = new GestureDetector(this,this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (true) {
            getMenuInflater().inflate(R.menu.menu_list, menu);
        }
        MenuItem item1 = menu.findItem(R.id.menu_list_other);
        item1.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_list_jump:
                final LinearLayout layout = (LinearLayout) LayoutInflater.from(HandleHistoryActivity.this).inflate(R.layout.dialog_jump, null);
                new AlertDialog.Builder(HandleHistoryActivity.this)
                        .setTitle("页面跳转")
                        .setView(layout)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String pageStr = ((EditText)(layout.findViewById(R.id.dialog_jump_num))).getText().toString();
                                if (pageStr != null){
                                    if (!"".equals(page)){
                                        int pageInt = Integer.parseInt(pageStr) - 1;
                                        if (pageInt < 0){
                                            pageInt = 0;
                                        }else if (pageInt > totalPage){
                                            pageInt = totalPage;
                                        }
                                        HandleHistoryActivity.this.page = pageInt;
                                        System.out.println(HandleHistoryActivity.this.page);
                                        jump();
                                    }
                                }
                            }
                        })
                        .create()
                        .show();
                break;
            case R.id.menu_list_timerequirement:
                final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, final int startYear, final int startMonth, final int startDay) {
                        final DatePickerDialog datePickerDialog1 = new DatePickerDialog(HandleHistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int endYear, int endMonth, int endDay) {
                                item.setTitle("时间：" + startYear + "-" + (startMonth + 1) + "-" + startDay + " / " + endYear + "-" + (endMonth + 1) + "-" + endDay);
                                HandleHistoryActivity.this.startYear = startYear;
                                HandleHistoryActivity.this.startMonth = startMonth + 1;
                                HandleHistoryActivity.this.startDay = startDay;
                                HandleHistoryActivity.this.endYear = endYear;
                                HandleHistoryActivity.this.endMonth = endMonth + 1;
                                HandleHistoryActivity.this.endDay = endDay;
                                timeB = true;
                                page = 0;
                                jump();
                            }
                        }, startYear, startMonth, startDay);
                        Date date = new Date(startYear - 1900 , startMonth, startDay);
                        Calendar rightNow = Calendar.getInstance();
                        rightNow.setTime(date);
                        rightNow.add(Calendar.DAY_OF_YEAR,1);
                        datePickerDialog1.getDatePicker().setMinDate(rightNow.getTime().getTime());
                        datePickerDialog1.setTitle("截止日期");
                        datePickerDialog1.show();
                    }
                }, DateTools.getYear(), DateTools.getMonth() - 1, DateTools.getDayOfMonth());
                datePickerDialog.setTitle("起始日期");
                datePickerDialog.show();
                break;
            case R.id.menu_list_keywordrequirement:
                final LinearLayout layout1 = (LinearLayout) LayoutInflater.from(HandleHistoryActivity.this).inflate(R.layout.dialog_keyword, null);
                new AlertDialog.Builder(HandleHistoryActivity.this)
                        .setTitle("关键字")
                        .setView(layout1)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText text = (EditText) layout1.findViewById(R.id.dialog_keyword_text);
                                String str = text.getText().toString();
                                String title = str;
                                if (str.length() > 6){
                                    title = str.substring(0 ,3) + "···" + str.substring(str.length() - 3 ,str.length());
                                }
                                item.setTitle("关键字：" + title);
                                keyWord = str;
                                keyWordB = true;
                                page = 0;
                                jump();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
                break;
            case R.id.menu_list_type:
                type = (type + 1) % 4;
                if (type == 0){
                    item.setTitle("障碍类型：全部");
                }else if (type == 1){
                    item.setTitle("障碍类型：应急抢修");
                }else if (type == 2){
                    item.setTitle("障碍类型：工程支撑");
                }else if (type == 3){
                    item.setTitle("障碍类型：资源守护");
                }
                page = 0;
                jump();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void jump(){
        progressBar.setVisibility(View.VISIBLE);
        listView.setAdapter(null);
        task = new GetFaultListTask();
        task.execute();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float minMove = 300;         //最小滑动距离
        float minVelocity = 5;      //最小滑动速度
        float beginX = e1.getX();
        float endX = e2.getX();
        if(beginX-endX>minMove&&Math.abs(velocityX)>minVelocity){   //左滑
            nextPage();
        }else if(endX-beginX>minMove&&Math.abs(velocityX)>minVelocity) {   //右滑
            lastPage();
        }
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return detector.onTouchEvent(motionEvent);
    }

    private void lastPage(){
        if (page < 1){
            return;
        }
        page--;
        task = new GetFaultListTask();
        task.execute();
    }

    private void nextPage(){
        if (page >= totalPage){
            return;
        }
        page++;
        task = new GetFaultListTask();
        task.execute();
    }

    private void changePageView(){
        setTitle(title + " (" + (page + 1) + "/" + (totalPage + 1) + "页)");
    }

    class GetFaultListTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (timeB || keyWordB || (type != 0)){
                Map<String, String> map = new HashMap<>();

                if (timeB){
                    map.put("starTime", startYear + "-" + startMonth + "-" + startDay);
                    map.put("endTime", endYear + "-" + endMonth + "-" + endDay);
                }

                if (keyWordB){
                    map.put("word", keyWord);
                }

                if (type == 1){
                    map.put("type", 1 + "");
                }else if (type == 2){
                    map.put("type", 2 + "");
                }else if (type == 3){
                    map.put("type", 1002 + "");
                }

                jsonArray = NetConfig.handleGetHandleList(HandleHistoryActivity.this, page + "", map);
            }else {
                jsonArray = NetConfig.handleGetHandleList(HandleHistoryActivity.this , page + "");
            }


            if (jsonArray != null) {
                try {
                    int p = jsonArray.getJSONObject(0).getInt("pageIndex");
                    totalPage = (p - 1) / 10;
                    if (p == 0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                //listView.setAdapter(adapter);
                                changePageView();
                            }
                        });
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter = new HandleHistoryAdapter(HandleHistoryActivity.this, jsonArray, R.layout.item_listview_faultlist);//新建自定义适配器
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        listView.setAdapter(adapter);
                        changePageView();
                    }
                });
            }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HandleHistoryActivity.this, "联网出错", Toast.LENGTH_LONG ).show();
                    }
                });
            }
            return null;
        }
    }
}
