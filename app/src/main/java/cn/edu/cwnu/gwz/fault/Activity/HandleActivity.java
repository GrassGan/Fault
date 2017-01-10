package cn.edu.cwnu.gwz.fault.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.czt.mp3recorder.MP3Recorder;
import com.jmolsmobile.landscapevideocapture.VideoCaptureActivity;
import com.jmolsmobile.landscapevideocapture.configuration.CaptureConfiguration;
import com.jmolsmobile.landscapevideocapture.configuration.PredefinedCaptureConfigurations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import cn.edu.cwnu.gwz.fault.R;
import cn.edu.cwnu.gwz.fault.Service.NetConfig;
import cn.edu.cwnu.gwz.fault.tools.BitmapTools;
import cn.edu.cwnu.gwz.fault.tools.FileTools;
import cn.edu.cwnu.gwz.fault.tools.FormFile;
import cn.edu.cwnu.gwz.fault.tools.FormatTime;
import cn.edu.cwnu.gwz.fault.tools.Out;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class HandleActivity extends AppCompatActivity implements View.OnClickListener {

    Set<String> imagePaths = new HashSet<>();
    String videoPath, soundPath;
    EditText editText;
    ImageView addImage_btn, cheakImage_btn, recordSound_btn, playSound_btn, recordVideo_btn, playVideo_btn;
    Button submit_btn;
    VideoView videoView;
    TextView recordSound_text, recordVideo_text, playVideo_text, kindView, submitText;
    MP3Recorder mRecorder;
    ProgressBar progressBar;
    RadioGroup radioGroup;
    RadioButton radioButton1, radioButton2;
    ReportTask task;
    boolean isRecording = false;
    Handler handler;
    AlertDialog dialog;
    String fdFlsn;
    PowerManager.WakeLock m_wklk;


    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    MenuItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Intent intent = getIntent();
        fdFlsn = intent.getStringExtra("fdFlsn");
        SharedPreferences p = getSharedPreferences("myinfo", Context.MODE_PRIVATE);
        editText = (EditText) findViewById(R.id.activity_report_text);
        addImage_btn = (ImageView) findViewById(R.id.activity_report_addimage);
        addImage_btn.setOnClickListener(this);
        cheakImage_btn = (ImageView) findViewById(R.id.activity_report_chackimage);
        cheakImage_btn.setOnClickListener(this);
        recordSound_btn = (ImageView) findViewById(R.id.activity_report_soundrecord);
        recordSound_btn.setOnClickListener(this);
        playSound_btn = (ImageView) findViewById(R.id.activity_report_playsound);
        playSound_btn.setEnabled(false);
        playSound_btn.setOnClickListener(this);
        recordVideo_btn = (ImageView) findViewById(R.id.activity_report_videorecord);
        recordVideo_btn.setOnClickListener(this);
        playVideo_btn = (ImageView) findViewById(R.id.activity_report_playvideo);
        playVideo_btn.setOnClickListener(this);
        videoView = (VideoView) findViewById(R.id.activity_report_videoView);
        submit_btn = (Button) findViewById(R.id.activity_report_submit);
        submit_btn.setOnClickListener(this);
        submit_btn.setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.activity_report_progressbar);
        recordSound_text= (TextView) findViewById(R.id.activity_report_soundrecord_text);
        recordVideo_text= (TextView) findViewById(R.id.activity_report_videorecord_text);
        playVideo_text= (TextView) findViewById(R.id.activity_report_playvideo_text);
        radioGroup= (RadioGroup) findViewById(R.id.activity_report_radiogroup);
        radioButton1= (RadioButton) findViewById(R.id.activity_report_radiogroup_radio1);
        radioButton2= (RadioButton) findViewById(R.id.activity_report_radiogroup_radio2);
        preferences = getSharedPreferences("handleMSG", Context.MODE_PRIVATE);

        if (p.getInt("uiAuthorize", 1) != 1){
            kindView = (TextView) findViewById(R.id.activity_report_kindtext);
            kindView.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
        }

        editor = preferences.edit();

        playVideo_btn.setEnabled(false);
        playVideo_btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (videoView.getVisibility() == VideoView.VISIBLE){
                    videoView.setVisibility(View.GONE);
                    playVideo_text.setText("播放视频");
                }
                return true;
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int i = msg.what;
                if (i % 2 == 0){
                    recordSound_btn.setImageResource(R.drawable.soundrecording);
                }else {
                    recordSound_btn.setImageResource(R.drawable.soundrecording2);
                }
            }
        };

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("障碍处理");
        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        m_wklk = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "cn");
    }

    private void onSubmit(boolean b){
        editText.setEnabled(!b);
        addImage_btn.setEnabled(!b);
        recordSound_btn.setEnabled(!b);
        recordVideo_btn.setEnabled(!b);
        radioGroup.setEnabled(!b);
        radioButton1.setEnabled(!b);
        radioButton2.setEnabled(!b);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (true) {
            getMenuInflater().inflate(R.menu.menu_report, menu);
        }
        item = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_report_btn:
                //showProgress(true);
                if (isRecording){
                    Toast.makeText(this,"正在录音",Toast.LENGTH_LONG).show();
                    break;
                }
                task = new ReportTask(editText.getText().toString(), radioButton2.isChecked(), item);
                task.execute();
                onSubmit(true);
                item.setEnabled(false);
                item.setTitle("正在提交");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.activity_report_addimage:

                editor.putInt("imageNum", 0);
                editor.commit();

                ArrayList<String> list = new ArrayList<>();
                Intent intent = new Intent(this, MultiImageSelectorActivity.class);
                // whether show camera
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                // max select image amount
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 5);
                // select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
                // default select images (support array list)
                intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, list);
                startActivityForResult(intent, 1);
                break;
            case R.id.activity_report_chackimage:
                ScrollView scrollView = new ScrollView(this);
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                final Bitmap[] bitmaps = new Bitmap[imagePaths.size()];
                int i = 0;
                for (String path : imagePaths) {
                    Bitmap bitmap = BitmapTools.bitmapNotOOM(this, path);
                    bitmaps[i] = bitmap;
                    i++;
                    ImageView imageView = new ImageView(this);
                    imageView.setImageBitmap(bitmap);
                    layout.addView(imageView);
                }
                scrollView.addView(layout);
                dialog = new AlertDialog.Builder(this)
                        .setTitle("查看已选图片(共" + imagePaths.size() + "张)")
                        .setView(scrollView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                for (int i2 = 0; i2 < bitmaps.length; i2++){
                                    bitmaps[i2].recycle();
                                    bitmaps[i2] = null;
                                }
                                System.gc();
                            }
                        })
                        .setCancelable(false)
                        .create();
                dialog.show();
                break;
            case R.id.activity_report_soundrecord:
                if (!isRecording) {
                    String name = FileTools.getNewName(this);
                    mRecorder = new MP3Recorder(new File(this.getFilesDir(), name+".mp3"));
                    soundPath = this.getFilesDir().toString() + "/" + name + ".mp3";
                    try {
                        mRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    recordSound_btn.setImageResource(R.drawable.soundrecording);
                    addImage_btn.setEnabled(false);
                    recordVideo_btn.setEnabled(false);
                    playSound_btn.setEnabled(false);
                    isRecording = true;
                    item.setEnabled(false);
                    recordSound_text.setText("正在录音");
                    new Thread(new Runnable() {
                        int i = 0;
                        @Override
                        public void run() {
                            while (isRecording) {
                                handler.sendEmptyMessage(i);
                                i++;
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    m_wklk.acquire(); //设置保持唤醒
                }else {
                    mRecorder.stop();
                    isRecording = false;
                    recordSound_btn.setImageResource(R.drawable.mic);
                    playSound_btn.setEnabled(true);
                    addImage_btn.setEnabled(true);
                    recordVideo_btn.setEnabled(true);
                    playSound_btn.setClickable(true);
                    item.setEnabled(true);
                    recordSound_text.setText("重新录音");
                    m_wklk.release();
                }
                break;
            case R.id.activity_report_playsound:
                final MediaPlayer mp = new MediaPlayer();
                LinearLayout layout1 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dialog_playsound, null);
                final SeekBar seekBar_sound = (SeekBar) layout1.findViewById(R.id.dialog_playsound_progressbar);
                final TextView time = (TextView) layout1.findViewById(R.id.dialog_playsound_time);
                Button playSound = (Button) layout1.findViewById(R.id.dialog_playsound_play);
                Button pauseSound = (Button) layout1.findViewById(R.id.dialog_playsound_pause);

                try {
                    mp.setDataSource(soundPath);
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final int duration = mp.getDuration();
                final Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {

                        time.setText(FormatTime.msToMin(msg.what) + " / " + FormatTime.msToMin(duration));
                        super.handleMessage(msg);
                    }
                };

                seekBar_sound.setMax(duration);
                seekBar_sound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {mp.seekTo(seekBar.getProgress());}
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

                dialog = new AlertDialog.Builder(this)
                        .setTitle("播放录音")
                        .setView(layout1)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mp.stop();
                            }
                        })
                        .setCancelable(false)
                        .create();
                dialog.show();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (mp.isPlaying()) {
                            int nowTime = mp.getCurrentPosition();
                            seekBar_sound.setProgress(nowTime);
                            handler.sendEmptyMessage(nowTime);
                            if (!dialog.isShowing()){
                                dialog.dismiss();
                            }
                        }
                    }
                }, 0, 10);
                break;
            case R.id.activity_report_videorecord:
                final CaptureConfiguration config = new CaptureConfiguration(PredefinedCaptureConfigurations.CaptureResolution.RES_480P,
                        PredefinedCaptureConfigurations.CaptureQuality.MEDIUM,
                        CaptureConfiguration.NO_DURATION_LIMIT,
                        CaptureConfiguration.NO_FILESIZE_LIMIT,
                        true);
                final String filename = FileTools.getNewName(this) + ".mp4";
                final Intent intent1 = new Intent(this, VideoCaptureActivity.class);
                intent1.putExtra(VideoCaptureActivity.EXTRA_CAPTURE_CONFIGURATION, config);
                intent1.putExtra(VideoCaptureActivity.EXTRA_OUTPUT_FILENAME, filename);
                int size = imagePaths.size();
                Object[] arr = imagePaths.toArray();
                editor.putInt("imageNum", size);
                for (int i1 = 0; i1 < size; i1++) {
                    editor.putString("imageNum" + i1, (String) arr[i1]);
                }
                editor.putString("soundPath", soundPath);
                editor.commit();
                startActivityForResult(intent1, 101);
                break;


            case R.id.activity_report_playvideo:
                MediaController mediaco = new MediaController(this);
                File file=new File(videoPath);
                if(videoPath != ""){
                    //VideoView与MediaController进行关联
                    videoView.setVideoPath(videoPath);
                    videoView.setMediaController(mediaco);
                    mediaco.setMediaPlayer(videoView);
                    //让VideoView获取焦点
                    videoView.requestFocus();
                    videoView.setVisibility(View.VISIBLE);
                    playVideo_text.setText("刷新视频\n(长按关闭)");
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                // 获取返回的图片列表
                List<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                // 处理你自己的逻辑
                imagePaths.clear();
                for (String path : paths) {
                    imagePaths.add(path);
                }
            }
        }
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                videoPath = data.getStringExtra(VideoCaptureActivity.EXTRA_OUTPUT_FILENAME);
                editor.putString("videoPath", videoPath);
                editor.commit();
                playVideo_btn.setEnabled(true);
                recordVideo_text.setText("重录视频");
                //statusMessage = String.format(getString(R.string.status_capturesuccess), filename);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //statusMessage = getString(R.string.status_capturecancelled);
            } else if (resultCode == VideoCaptureActivity.RESULT_ERROR) {
                //statusMessage = getString(R.string.status_capturefailed);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int size = preferences.getInt("imageNum", 0);
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                imagePaths.add(preferences.getString("imageNum" + i, null));
            }
        }
        videoPath = preferences.getString("videoPath", "");
        soundPath = preferences.getString("soundPath", "");

        if (videoPath != null){
            if ("".equals(videoPath)){
                playVideo_btn.setEnabled(true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        editor.clear();
        editor.commit();
        super.onDestroy();
    }

//    private void showProgress(boolean b) {
//        if (b) {
//            submit_btn.setVisibility(View.GONE);
//            progressBar.setVisibility(View.VISIBLE);
//        } else {
//
//            progressBar.setVisibility(View.GONE);
//            submit_btn.setVisibility(View.VISIBLE);
//        }
//    }

    class ReportTask extends AsyncTask<Void, Void, Boolean> {

        final String text;
        final boolean flftSn;
        final MenuItem item;

        ReportTask(String text, boolean flftSn, MenuItem item) {
            this.text = text;
            this.flftSn = flftSn;
            this.item = item;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            int j = 0;
            if (!videoPath.equals("")){
                j++;
            }
            if (!soundPath.equals("")) {
                j++;
            }
            FormFile[] files = new FormFile[imagePaths.size() + j];
            int i = 0;
            for (String path : imagePaths) {
                File imageFile = new File(path);
                if (imageFile.exists()) {
                    String[] arr = path.split("\\.");
                    String end = arr[arr.length - 1];
                    String newPath = HandleActivity.this.getFilesDir().toString() + "/" + FileTools.getNewName(HandleActivity.this) + "." + end;
                    FileTools.Copy(imageFile, newPath);
                    File newFile = new File(newPath);
                    files[i++] = new FormFile(newFile, "upload", "image/gif");
                }
            }
            File soundFile = new File(soundPath);
            if (soundFile.exists()){
                files[i++] = new FormFile(soundFile, "upload", "audio/mpeg");
            }
            File videoFile = new File(videoPath);
            if (videoFile.exists()){
                files[i++] = new FormFile(videoFile, "upload", "video/mp4");
            }
            if (NetConfig.handleHandle(HandleActivity.this, fdFlsn, text, files)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HandleActivity.this, "提交成功", Toast.LENGTH_LONG).show();
                        Out.outToSelect(HandleActivity.this);
                    }
                });
                return true;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HandleActivity.this, "提交失败", Toast.LENGTH_LONG).show();
                    item.setEnabled(true);
                }
            });
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success){
//                HandleActivity.this.progressBar.setVisibility(View.GONE);
//                submit_btn.setText("提交成功");
//                submit_btn.setEnabled(false);
//                submit_btn.setClickable(false);
//                submit_btn.setVisibility(View.VISIBLE);
                return;
            }
            //showProgress(false);
            item.setEnabled(true);
            item.setTitle("提交");
            onSubmit(false);
        }

        @Override
        protected void onCancelled() {
            //showProgress(false);
            item.setEnabled(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRecording) {
            mRecorder.stop();
            isRecording = false;
            recordSound_btn.setImageResource(R.drawable.mic);
            playSound_btn.setEnabled(true);
            addImage_btn.setEnabled(true);
            recordVideo_btn.setEnabled(true);
            item.setEnabled(true);
            recordSound_text.setText("重新录音");
            m_wklk.release();
            editor.putString("videoPath", videoPath);
        }
        int size = imagePaths.size();
        Object[] arr = imagePaths.toArray();
        editor.putInt("imageNum", size);
        for (int i1 = 0; i1 < size; i1++) {
            editor.putString("imageNum" + i1, (String) arr[i1]);
        }
        editor.putString("soundPath", soundPath);
        editor.commit();
    }
}
