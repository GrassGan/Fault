package cn.edu.cwnu.gwz.fault.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import cn.edu.cwnu.gwz.fault.R;
import cn.edu.cwnu.gwz.fault.Service.NetConfig;
import cn.edu.cwnu.gwz.fault.Service.WebService;
import cn.edu.cwnu.gwz.fault.tools.BitmapTools;
import cn.edu.cwnu.gwz.fault.tools.FormatTime;

public class FaultActivity extends AppCompatActivity implements View.OnClickListener{

    TextView flSn, flftSn, flDate, flWord, flUiSn, fluiSn, flStatus, image_text, sound_text, video_text;
    ImageView image_Btn, sound_Btn, video_Btn;
    VideoView videoView;
    Set<String> imagePath;
    LinearLayout lodingVideo, handleView;
    String soundPath = "", videoPath = "";
    String aPath = "http://" + WebService.ADDRESS +":"+ WebService.POTE + "/Obs_state/";
    Thread thread;
    String fdFlsn;
    boolean isShow = false;
    List<Bitmap> bitmapList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fault);


        Intent intent = getIntent();
        flSn = (TextView) findViewById(R.id.activity_fault_flSn);
        flftSn = (TextView) findViewById(R.id.activity_fault_flftSn);
        flDate = (TextView) findViewById(R.id.activity_fault_flDate);
        flWord = (TextView) findViewById(R.id.activity_fault_flWord);
        flUiSn = (TextView) findViewById(R.id.activity_fault_flUiSn);
        fluiSn = (TextView) findViewById(R.id.activity_fault_fluiSn);
        flStatus = (TextView) findViewById(R.id.activity_fault_flStatus);

        image_text = (TextView) findViewById(R.id.activity_fault_playimage_text);
        video_text = (TextView) findViewById(R.id.activity_fault_playvideo_text);
        sound_text = (TextView) findViewById(R.id.activity_fault_playsound_text);

        image_Btn = (ImageView) findViewById(R.id.activity_fault_playimage);
        sound_Btn = (ImageView) findViewById(R.id.activity_fault_playsound);
        video_Btn = (ImageView) findViewById(R.id.activity_fault_playvideo);

        lodingVideo = (LinearLayout) findViewById(R.id.activity_fault_loadvideo);
        videoView = (VideoView) findViewById(R.id.activity_fault_videoView);

        flSn.setText("故障号: " + intent.getStringExtra("flSn"));
        fdFlsn = intent.getStringExtra("flSn");

        image_Btn.setOnClickListener(this);
        sound_Btn.setOnClickListener(this);
        video_Btn.setOnClickListener(this);
        image_Btn.setClickable(false);
        sound_Btn.setClickable(false);
        video_Btn.setClickable(false);

        video_Btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (videoView.getVisibility() == VideoView.VISIBLE){
                    isShow = false;
                    video_text.setText("播放视频");
                }
                return true;
            }
        });

        imagePath = new HashSet<>();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("障碍详情");

        GetFaultTask task = new GetFaultTask(intent.getStringExtra("flSn"));
        task.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        SharedPreferences preferences = getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        if (preferences.getInt("uiAuthorize", 1) != 1){
            getMenuInflater().inflate(R.menu.menu_report, menu);
            menu.getItem(0).setTitle("处理");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_report_btn:
                handle();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.activity_fault_playimage:
                seeImage();
                break;
            case R.id.activity_fault_playsound:
                playSound();
                break;
            case R.id.activity_fault_playvideo:
                playVideo();
                break;
            case R.id.activity_fault_handle:
                handle();
                break;
        }
    }

    private void handle() {
        Intent intent = new Intent(FaultActivity.this, HandleActivity.class);
        intent.putExtra("fdFlsn", fdFlsn);
        startActivity(intent);
    }

    private void seeImage() {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(layout);
        final GetImageTask task = new GetImageTask(layout);
        task.execute();

        new AlertDialog.Builder(this)
                .setTitle("查看图片(共" + imagePath.size() + "张)")
                .setView(scrollView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        task.cancel(true);
                        for (Bitmap bitmap : bitmapList){
                            if (bitmap != null) {
                                bitmap.recycle();
                                bitmap = null;
                            }
                        }
                        bitmapList = null;
                        System.gc();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void playSound() {
        final MediaPlayer mp = new MediaPlayer();
        LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dialog_playsound, null);
        final SeekBar seekBar_sound = (SeekBar) layout.findViewById(R.id.dialog_playsound_progressbar);
        final TextView time = (TextView) layout.findViewById(R.id.dialog_playsound_time);
        Button playSound = (Button) layout.findViewById(R.id.dialog_playsound_play);
        Button pauseSound = (Button) layout.findViewById(R.id.dialog_playsound_pause);

        try {
            System.out.println("soundPath:" + soundPath);
            Uri uri = Uri.parse(soundPath);
            mp.setDataSource(this, uri);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final int duration = mp.getDuration();
        seekBar_sound.setMax(duration);
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                time.setText(FormatTime.msToMin(msg.what) + " / " + FormatTime.msToMin(duration));
                super.handleMessage(msg);
            }
        };
        seekBar_sound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
            }
        });
        playSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
            }
        });
        pauseSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.pause();
            }
        });
        time.setText(FormatTime.msToMin(duration));

        new AlertDialog.Builder(this)
                .setTitle("播放录音")
                .setView(layout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mp.stop();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (mp.isPlaying()) {
                    int nowTime = mp.getCurrentPosition();
                    seekBar_sound.setProgress(nowTime);
                    handler.sendEmptyMessage(nowTime);
                }
            }
        }, 0, 10);
    }

    private void playVideo() {
        isShow = true;
        video_text.setText("刷新视频\n(长按关闭)");
        lodingVideo.setVisibility(View.VISIBLE);
        MediaController mediaco = new MediaController(this);
        //videoView.setVideoPath(file.getAbsolutePath());
        videoView.setVideoURI(Uri.parse(videoPath));
        //VideoView与MediaController进行关联
        videoView.setMediaController(mediaco);
        mediaco.setMediaPlayer(videoView);
        //让VideoView获取焦点
        videoView.requestFocus();
        videoView.start();
        //}
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (isShow) {
                    if (msg.what == 10000) {
                        lodingVideo.setVisibility(View.GONE);
                        videoView.setVisibility(View.VISIBLE);
                    }
                    if (msg.what == 10001) {
                        lodingVideo.setVisibility(View.VISIBLE);
                        videoView.setVisibility(View.VISIBLE);
                    }
                }else {
                    lodingVideo.setVisibility(View.GONE);
                    videoView.pause();
                    videoView.setVisibility(View.GONE);
                }
            }
        };
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(10000);
                while (true) {
                    try {
                        Thread.sleep(500);
                        if (videoView.getCurrentPosition() == videoView.getDuration()){
                            handler.sendEmptyMessage(10000);
                            continue;
                        }
                        if (!videoView.isPlaying()) {
                            if (videoView.getCurrentPosition() < videoView.getDuration()){
                                handler.sendEmptyMessage(10000);
                                continue;
                            }
                            handler.sendEmptyMessage(10001);
                        }else {
                            handler.sendEmptyMessage(10000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onDestroy() {
        if (thread != null) {
            Thread moribund = thread;
            thread = null;
            moribund.interrupt();
        }
        super.onDestroy();
    }

    class GetFaultTask extends AsyncTask<Void, Void, Boolean> {

        final String flSn_str;
        String flSn = "";//故障号
        String flftSn = "";//障碍类型
        String flDate = "";//时间
        String flWord = "";//描述
        String flUiSn = "";//发送人员编号
        String fluiSn = "";//发送人员名字
        String flPictiure = "";//所有图片路径，用‘|’分割
        String flStatus = "";//处理状态，0为未处理，1为已处理

        GetFaultTask(String flSn_str) {
            this.flSn_str = flSn_str;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            JSONObject jsonObject = NetConfig.handleGetFaultInfo(flSn_str);
            try {
                flSn = jsonObject.getString("flSn");
                flftSn = jsonObject.getString("flftSn");
                flWord = jsonObject.getString("flWord");
                flUiSn = jsonObject.getString("flUiSn");
                fluiSn = jsonObject.getString("fluiSn");
                flPictiure = jsonObject.getString("flPictiure");
                flStatus = jsonObject.getString("flStatus");
                flDate = jsonObject.getString("flDate");
                videoPath = aPath + jsonObject.getString("flVideo");
                soundPath = aPath + jsonObject.getString("flVoice");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!flPictiure.equals("")) {
                String[] imagepath = flPictiure.split("\\|");
                for (int i = 0; i < imagepath.length; i++) {
                    imagePath.add(imagepath[i]);
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FaultActivity.this.flSn.setText("障碍号: " + GetFaultTask.this.flSn);
                    FaultActivity.this.flftSn.setText("类型: " + GetFaultTask.this.flftSn);
                    FaultActivity.this.flWord.setText("文字描述: " + GetFaultTask.this.flWord);
                    FaultActivity.this.flUiSn.setText("申告人员编号: " + GetFaultTask.this.flUiSn);
                    FaultActivity.this.flStatus.setText("处理情况: " + ("1".equals(GetFaultTask.this.flStatus) ? "已处理" : "未处理"));
                    FaultActivity.this.flDate.setText("申告时间: " + GetFaultTask.this.flDate.split("\\.")[0]);
                    FaultActivity.this.fluiSn.setText("申告人员名字: " + GetFaultTask.this.fluiSn);
                    if (!imagePath.isEmpty()) {
                        image_Btn.setVisibility(View.VISIBLE);
                        image_text.setVisibility(View.VISIBLE);
                        image_Btn.setClickable(true);
                    }
                    if (!videoPath.equals(aPath)) {
                        video_Btn.setVisibility(View.VISIBLE);
                        video_text.setVisibility(View.VISIBLE);
                        video_Btn.setClickable(true);
                    }
                    if (!soundPath.equals(aPath)) {
                        sound_Btn.setVisibility(View.VISIBLE);
                        sound_text.setVisibility(View.VISIBLE);
                        sound_Btn.setClickable(true);
                    }
                }
            });
            return null;
        }
    }

    class GetImageTask extends AsyncTask<Void, Void, Boolean> {

        //ScrollView scrollView;
        LinearLayout layout;

        GetImageTask(LinearLayout layout) {
            //this.scrollView = scrollView;;
            this.layout = layout;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            bitmapList = new ArrayList<>();

            for (String path : imagePath) {
                String fileName = NetConfig.handleDownload(FaultActivity.this,path);
                if (fileName == null){
                    continue;
                }
                final Bitmap bitmap = BitmapTools.bitmapNotOOM(FaultActivity.this, FaultActivity.this.getFilesDir().toString()+"/"+fileName);
                if (bitmapList != null) {
                    bitmapList.add(bitmap);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView imageView = new ImageView(FaultActivity.this);
                        imageView.setImageBitmap(bitmap);
                        layout.addView(imageView);
                    }
                });

            }
            return null;
        }
    }
}
