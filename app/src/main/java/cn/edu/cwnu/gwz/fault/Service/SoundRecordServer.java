package cn.edu.cwnu.gwz.fault.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.czt.mp3recorder.MP3Recorder;

import java.io.File;
import java.io.IOException;

/**
 * Created by GrassGan on 2016/7/12.
 */
public class SoundRecordServer extends Service{

    MP3Recorder mRecorder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        String name = intent.getStringExtra("name");
        mRecorder = new MP3Recorder(new File(this.getFilesDir(), name+".mp3"));
        try {
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        mRecorder.stop();
        super.onDestroy();
    }
}
