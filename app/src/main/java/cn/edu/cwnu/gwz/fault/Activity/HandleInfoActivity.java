package cn.edu.cwnu.gwz.fault.Activity;

import android.content.DialogInterface;
import android.content.Intent;
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

import org.json.JSONException;
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
import cn.edu.cwnu.gwz.fault.tools.FormatTime;
import cn.edu.cwnu.gwz.fault.tools.HttpBitmap;

public class HandleInfoActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnPreparedListener {

    TextView text1, text2, text3, text4, text5, text6, text7, image_text, sound_text, video_text;
    ImageView image_Btn, sound_Btn, video_Btn;
    VideoView videoView;
    Set<String> imagePath;
    LinearLayout lodingVideo;
    String soundPath = "", videoPath = "";
    String aPath = "http://" + WebService.ADDRESS + ":" + WebService.POTE + "/Obs_state/";
    Thread thread;
    String fdsn, flsn;
    List<Bitmap> bitmapList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fault);
        Intent intent = getIntent();
        text1 = (TextView) findViewById(R.id.activity_fault_flSn);
        text2 = (TextView) findViewById(R.id.activity_fault_flftSn);
        text3 = (TextView) findViewById(R.id.activity_fault_flDate);
        text4 = (TextView) findViewById(R.id.activity_fault_flWord);
        text5 = (TextView) findViewById(R.id.activity_fault_flUiSn);
        text6 = (TextView) findViewById(R.id.activity_fault_fluiSn);
        text7 = (TextView) findViewById(R.id.activity_fault_flStatus);

        image_text = (TextView) findViewById(R.id.activity_fault_playimage_text);
        video_text = (TextView) findViewById(R.id.activity_fault_playvideo_text);
        sound_text = (TextView) findViewById(R.id.activity_fault_playsound_text);

        image_Btn = (ImageView) findViewById(R.id.activity_fault_playimage);
        sound_Btn = (ImageView) findViewById(R.id.activity_fault_playsound);
        video_Btn = (ImageView) findViewById(R.id.activity_fault_playvideo);

        lodingVideo = (LinearLayout) findViewById(R.id.activity_fault_loadvideo);
        videoView = (VideoView) findViewById(R.id.activity_fault_videoView);

        fdsn = intent.getStringExtra("fdsn");
        text1.setText("处理号: " + fdsn);

        image_Btn.setOnClickListener(this);
        sound_Btn.setOnClickListener(this);
        video_Btn.setOnClickListener(this);
        image_Btn.setClickable(false);
        sound_Btn.setClickable(false);
        video_Btn.setClickable(false);

        imagePath = new HashSet<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("处理详情");

        GetHaandleTask task = new GetHaandleTask();
        task.execute();

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
        }
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
            //mp.setDataSource(this.getFilesDir().toString() + "/test.mp3");
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
        videoView.setOnPreparedListener(this);
        //}
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 10000) {
                    lodingVideo.setVisibility(View.GONE);
                    videoView.setVisibility(View.VISIBLE);
                }
                if (msg.what == 10001) {
                    lodingVideo.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.VISIBLE);
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

    @Override
    public void onPrepared(MediaPlayer mp) {
//        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//            int currentPosition, duration;
//
//            public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                // 获得当前播放时间和当前视频的长度
//                currentPosition = videoView.getCurrentPosition();
//                duration = videoView.getDuration();
//                int time = ((currentPosition * 100) / duration);
//                // 设置进度条的主要进度，表示当前的播放时间
//                SeekBar seekBar = new SeekBar(EsActivity.this);
//                seekBar.setProgress(time);
//                // 设置进度条的次要进度，表示视频的缓冲进度
//                seekBar.setSecondaryProgress(percent);
//            }
//        });
    }

    class GetHaandleTask extends AsyncTask<Void, Void, Boolean> {

        String flftSn = "";
        String flDate = "";
        String flWord = "";
        String flUiSn = "";
        String flPictiure = "";//所有图片路径，用‘|’分割
        String flStatus = "";

        //GetHaandleTask(String flSn_str) {
            //this.flSn_str = flSn_str;
        //}

        @Override
        protected Boolean doInBackground(Void... voids) {
            JSONObject jsonObject = NetConfig.handleGetHandleInfo(fdsn);
            System.out.println(jsonObject);
            try {
                flftSn = jsonObject.getString("fdFlsn");//类型
                flWord = jsonObject.getString("flWord");//障碍描述
                flUiSn = jsonObject.getString("fdUisn");//处理人员名字
                flsn = jsonObject.getString("flsn");//障碍号
                flPictiure = jsonObject.getString("fdPictiure");//ok
                flStatus = jsonObject.getString("fdWord");//处理描述
                flDate = jsonObject.getString("fdDate");//处理时间
                videoPath = aPath + jsonObject.getString("fdVideo");//ok
                soundPath = aPath + jsonObject.getString("fdVoice");//ok
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (!flPictiure.equals("")) {
                String[] imagepath = flPictiure.split("\\|");
                for (int i = 0; i < imagepath.length; i++) {
                    imagePath.add(aPath + imagepath[i]);
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    HandleInfoActivity.this.text1.setText("障碍类型: " + GetHaandleTask.this.flftSn);
                    HandleInfoActivity.this.text2.setText("处理号: " + HandleInfoActivity.this.fdsn);
                    HandleInfoActivity.this.text3.setText("障碍号: " + HandleInfoActivity.this.flsn);
                    HandleInfoActivity.this.text4.setText("障碍描述: " + GetHaandleTask.this.flWord);
                    HandleInfoActivity.this.text5.setText("处理描述: " + GetHaandleTask.this.flStatus);
                    HandleInfoActivity.this.text6.setText("处理人员: " + GetHaandleTask.this.flUiSn);
                    HandleInfoActivity.this.text7.setText("处理时间: " + GetHaandleTask.this.flDate.split("\\.")[0]);
                    if (!imagePath.isEmpty()) {
                        image_Btn.setVisibility(View.VISIBLE);
                        image_text.setVisibility(View.VISIBLE);
                        image_Btn.setClickable(true);
                    }
                    if (!videoPath.equals(aPath)) {
                        System.out.println("videoPath != \"\"   videoPath = " + videoPath);
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
                final Bitmap bitmap = HttpBitmap.getHttpBitmap(path);
                if (bitmapList != null) {
                    bitmapList.add(bitmap);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView imageView = new ImageView(HandleInfoActivity.this);
                        imageView.setImageBitmap(bitmap);
                        layout.addView(imageView);
                    }
                });

            }
            return null;
        }
    }

}
