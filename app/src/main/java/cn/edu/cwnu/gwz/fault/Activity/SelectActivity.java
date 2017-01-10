package cn.edu.cwnu.gwz.fault.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;

import cn.edu.cwnu.gwz.fault.R;
import cn.edu.cwnu.gwz.fault.Service.NetConfig;
import cn.edu.cwnu.gwz.fault.tools.Out;

public class SelectActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout myInfoLayout, changePasswordLayout, outLayout, faultLayout, faultListLayout, handleListLayout, record_Btn;
    TextView fault_text, handleLiat_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        myInfoLayout = (LinearLayout) findViewById(R.id.select_layout_myinfo_btn);
        changePasswordLayout = (LinearLayout) findViewById(R.id.select_layout_changepassword_btn);
        faultLayout = (LinearLayout) findViewById(R.id.select_layout_report_btn);
        faultListLayout = (LinearLayout) findViewById(R.id.select_layout_faultlist_btn);
        handleListLayout = (LinearLayout) findViewById(R.id.select_layout_handlelist_btn);
        outLayout = (LinearLayout) findViewById(R.id.select_layout_out_btn);

        fault_text = (TextView) findViewById(R.id.select_layout_fault_text);
        handleLiat_text = (TextView) findViewById(R.id.select_layout_handlelist_text);

        changePasswordLayout.setOnClickListener(this);
        myInfoLayout.setOnClickListener(this);
        faultListLayout.setOnClickListener(this);
        outLayout.setOnClickListener(this);

        SharedPreferences preferences = getSharedPreferences("myinfo", Context.MODE_PRIVATE);
//        int i = preferences.getInt("uiAuthorize", -1);
        int i = 1;
        if (i == 1){//申告人员
            handleListLayout.setVisibility(View.GONE);
            handleLiat_text.setVisibility(View.GONE);
            faultLayout.setOnClickListener(this);
        }else if (i == 2){//申告处理人员
            faultLayout.setOnClickListener(this);
            handleListLayout.setOnClickListener(this);
        } else if (i == 3){//管理人员
            faultLayout.setVisibility(View.GONE);
            fault_text.setVisibility(View.GONE);
            handleListLayout.setOnClickListener(this);
        }else {
            Out.outToLogin(SelectActivity.this);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                File dir = new File(SelectActivity.this.getFilesDir().toString());
                if (dir.isDirectory()) {
                    String[] children = dir.list();
                    for (int i = 0; i < children.length; i++) {
                        File file = new File(dir, children[i]);
                        if (!file.isDirectory()){
                            file.delete();
                        }
                    }
                }
            }
        }).start();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.select_layout_myinfo_btn:
                chackInfo();
                break;
            case R.id.select_layout_changepassword_btn:
                changePassword();
                break;
            case R.id.select_layout_report_btn:
                Intent intent = new Intent(this,ReportActivity.class);
                startActivity(intent);
                break;
            case R.id.select_layout_faultlist_btn:
                chackFaultList();
                break;
            case R.id.select_layout_handlelist_btn:
                handleHistory();
                break;
            case R.id.select_layout_out_btn:
                new AlertDialog.Builder(this)
                        .setTitle("确定退出登录？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                outToLogin();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
                break;
        }
    }

    private void handleHistory(){
        Intent intent = new Intent(this, HandleHistoryActivity.class);
        startActivity(intent);
    }

    private void outToLogin(){
//        Intent intent = new Intent();
//        intent.setClass(SelectActivity.this, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //注意本行的FLAG设置
//        SharedPreferences preferences = getSharedPreferences("myinfo", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.clear();
//        editor.commit();
//        startActivity(intent);
//        finish();
        Out.outToLogin(SelectActivity.this);
    }

    private void changePassword(){
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    private void chackInfo(){
        LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dialog_userinfo, null);
        ChackInfoTask task = new ChackInfoTask(layout);
        task.execute();
        new AlertDialog.Builder(this)
                .setTitle("个人信息")
                .setView(layout)
                .setPositiveButton("确定", null)
                .create()
                .show();
    }

    private void chackFaultList(){
        Intent intent = new Intent(this,FaultListActivity.class);
        startActivity(intent);
    }

    class ChackInfoTask extends AsyncTask<Void, Void, Boolean> {

        final LinearLayout layout;
        ChackInfoTask(LinearLayout layout){
            this.layout = layout;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            final JSONObject object = NetConfig.handleGetInfo(SelectActivity.this);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int i = object.getInt("uiAuthorize");
                        int j = object.getInt("uiStatus");
                        String uiAuthorize = "";
                        String uiStatus = "";
                        if (i == 1){
                            uiAuthorize = "障碍申告";
                        }else if (i == 2){
                            uiAuthorize = "障碍处理";
                        }else {
                            uiAuthorize = "系统管理";
                        }
                        if (j == 0){
                            uiStatus = "停用";
                        }else {
                            uiStatus = "正常使用";
                        }
                        ((ProgressBar)layout.findViewById(R.id.dialog_userInfo_progressbar)).setVisibility(View.GONE);
                        ((TextView)layout.findViewById(R.id.dialog_userInfo_uiauthorize)).setText(uiAuthorize);
                        ((TextView)layout.findViewById(R.id.dialog_userInfo_uideptsnname)).setText(object.getString("uiDeptSnName"));
                        ((TextView)layout.findViewById(R.id.dialog_userInfo_uimemo)).setText(object.getString("uiMemo"));
                        ((TextView)layout.findViewById(R.id.dialog_userInfo_uiname)).setText(object.getString("uiName"));
                        ((TextView)layout.findViewById(R.id.dialog_userInfo_uiphone)).setText(object.getString("uiPhone"));
                        ((TextView)layout.findViewById(R.id.dialog_userInfo_uistatus)).setText(uiStatus);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
            return null;
        }
    }
}
