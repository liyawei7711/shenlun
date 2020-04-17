package huaiye.com.vim.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import huaiye.com.vim.R;
import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.callback.HTTPCallback;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * author: admin
 * date: 2018/02/22
 * version: 0
 * mail: secret
 * desc: MusicService
 */

public class MusicService extends Service {
    private final static String TAG = MusicService.class.getSimpleName();
    private MediaPlayer mMediaPlayer;

    public class MusicBinder extends Binder {
        MusicBinder getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicBinder.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AssetFileDescriptor fd = null;
        try {
            fd = getAssets().openFd("silent.mp3");
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            mMediaPlayer.prepare();
            mMediaPlayer.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startPlayMusic();
            }
        }).start();
        return START_STICKY;
    }

    private void startPlayMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    private void stopPlayMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayMusic();
        // 重启自己
        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        startService(intent);
    }
}
