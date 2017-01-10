package cn.edu.cwnu.gwz.fault.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import cn.edu.cwnu.gwz.fault.R;

import cn.edu.cwnu.gwz.fault.Service.NetConfig;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText oldPassword;
    EditText newPassword;
    EditText confirmPassword;
    Button submit;
    ProgressBar progressBar;
    ChangePassWordTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        oldPassword = (EditText) findViewById(R.id.activity_changepassword_oldpassword);
        newPassword = (EditText) findViewById(R.id.activity_changepassword_newpassword);
        confirmPassword = (EditText) findViewById(R.id.activity_changepassword_confirmpassword);
        submit = (Button) findViewById(R.id.activity_changepassword_submit);
        progressBar = (ProgressBar) findViewById(R.id.activity_changepassword_progressbar);
        submit.setVisibility(View.GONE);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SharedPreferences preferences = getSharedPreferences("myinfo", Context.MODE_PRIVATE);
//                int i = 0;
//                if ("".equals(oldPassword.getText().toString())){
//                    oldPassword.setError("此处不能为空");
//                    i++;
//                }
//                if ("".equals(newPassword.getText().toString())){
//                    newPassword.setError("此处不能为空");
//                    i++;
//                }
//                if ("".equals(confirmPassword.getText().toString())){
//                    confirmPassword.setError("此处不能为空");
//                    i++;
//                }
//                if (!preferences.getString("uiPassword","").equals(oldPassword.getText().toString())){
//                    oldPassword.setError("密码错误");
//                    i++;
//                }
//                if (!newPassword.getText().toString().equals(confirmPassword.getText().toString())){
//                    confirmPassword.setError("密码不一致");
//                    i++;
//                }
//                if (i != 0){
//                    return;
//                }
//                showProgress(true);
//                task = new ChangePassWordTask(confirmPassword.getText().toString());
//                task.execute();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("修改密码");
    }

    private void onSubmit(boolean b){
        oldPassword.setEnabled(!b);
        newPassword.setEnabled(!b);
        confirmPassword.setEnabled(!b);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (true) {
            getMenuInflater().inflate(R.menu.menu_report, menu);
        }
        menu.getItem(0).setTitle("确认修改");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_report_btn:
                SharedPreferences preferences = getSharedPreferences("myinfo", Context.MODE_PRIVATE);
                int i = 0;
                if ("".equals(oldPassword.getText().toString())){
                    oldPassword.setError("此处不能为空");
                    i++;
                }
                if ("".equals(newPassword.getText().toString())){
                    newPassword.setError("此处不能为空");
                    i++;
                }
                if ("".equals(confirmPassword.getText().toString())){
                    confirmPassword.setError("此处不能为空");
                    i++;
                }
                if (!preferences.getString("uiPassword","").equals(oldPassword.getText().toString())){
                    oldPassword.setError("密码错误");
                    i++;
                }
                if (newPassword.getText().toString().length() < 6){
                    newPassword.setError("密码太短");
                    i++;
                }
                if (!newPassword.getText().toString().equals(confirmPassword.getText().toString())){
                    confirmPassword.setError("密码不一致");
                    i++;
                }
                if (i != 0){
                    break;
                }
                //showProgress(true);
                item.setEnabled(false);
                item.setTitle("正在提交");
                onSubmit(true);
                task = new ChangePassWordTask(confirmPassword.getText().toString(), item);
                task.execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class ChangePassWordTask extends AsyncTask<Void, Void, Boolean> {

        private final String password;
        private final MenuItem item;
        ChangePassWordTask(String password, MenuItem item){
            this.password =password;
            this.item = item;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (NetConfig.handleChangePassword(ChangePasswordActivity.this,password)){
                SharedPreferences preferences = ChangePasswordActivity.this.getSharedPreferences("myinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("uiPassword",password);
                editor.commit();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChangePasswordActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                        ChangePasswordActivity.this.finish();
                    }
                });
            }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChangePasswordActivity.this, "修改失败", Toast.LENGTH_LONG).show();
                        onSubmit(false);
                    }
                });
            }
            return null;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            if (success == null){
                item.setEnabled(true);
                item.setTitle("确认修改");
                onSubmit(false);
                return;
            }
            if (success){
                return;
            }
            item.setEnabled(true);
            item.setTitle("确认修改");
            onSubmit(false);
        }

        @Override
        protected void onCancelled() {
            //showProgress(false);
            item.setEnabled(false);
        }
    }

//    private void showProgress(boolean b) {
//        if (b){
//            submit.setVisibility(View.GONE);
//            progressBar.setVisibility(View.VISIBLE);
//        }else {
//            submit.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.GONE);
//        }
//    }
}
