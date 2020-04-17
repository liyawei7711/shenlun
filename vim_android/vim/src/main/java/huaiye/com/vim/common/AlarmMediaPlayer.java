package huaiye.com.vim.common;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;

import com.huaiye.sdk.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import huaiye.com.vim.VIMApp;

/**
 * 要注意AppAudioManager,start开始之后再使用AppAudioManager会造成状态混乱
 */
public class AlarmMediaPlayer implements MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener {
    public static final int SOURCE_CALL_VOICE = 1;
    public static final int SOURCE_PTT_VOICE = 2;
    public static final int SOURCE_ALARM_VOICE = 3;
    public static final int SOURCE_ERROR_VOICE = 4;
    public static final int SOURCE_SOS_VOICE = 5;
    public static final int SOURCE_PERSON_VOICE = 6;
    public static final int SOURCE_ZHILLING_VOICE = 7;
    public static final int SOURCE_CUSTOM = 200;


    private MediaPlayer mMediaPlayer;
    private Context mContext;

    private String TAG = AlarmMediaPlayer.class.getSimpleName();

    private static AlarmMediaPlayer mInstance;
    private boolean isPlaying;
    AudioManager mAudioManager;
    private PlayBean currentPlayBean;

    private int previousMode;
    private boolean previousSpeakon;
    private ArrayList<PlayerListener> playerListeners;


    public static AlarmMediaPlayer get() {
        if (mInstance == null) {
            synchronized (AlarmMediaPlayer.class) {
                if (mInstance == null) {
                    mInstance = new AlarmMediaPlayer(VIMApp.getInstance());
                }
            }
        }
        return mInstance;
    }

    private AlarmMediaPlayer(Context context) {
        this.mContext = context;
        mAudioManager = (AudioManager) VIMApp.getInstance().getSystemService(Context.AUDIO_SERVICE);
        playerListeners = new ArrayList<>();
    }

    public void playAlarm(){
        play(false,SOURCE_CALL_VOICE,null);
    }

    public void play(int type) {
        play(false, type, null, null);
    }


    public void play(boolean stopPrevious, int sourceType, String customSourcePath) {
        play(stopPrevious, sourceType, customSourcePath, null);
    }

    public void play(boolean stopPrevious, int sourceType, String customSourcePath, PlayerListener listener) {
        PlayBean playBean = new PlayBean();
        playBean.stopPrevious = stopPrevious;
        playBean.sourceType = sourceType;
        playBean.customSourcePath = customSourcePath;
        playBean.playerListener = listener;
        play(playBean);
    }

    private void play(PlayBean playBean) {
        Logger.log(TAG + " Play start " + playBean.sourceType);

        //多次播放只播放第一次,其他的不进行操作
        if (mMediaPlayer != null && (mMediaPlayer.isPlaying() || isPlaying)) {
            if (playBean.stopPrevious) {
                Logger.log(TAG + " Play isAlarmPlaying but stop Previous" + playBean.sourceType);
                stop();
            } else {
                Logger.log(TAG + " Play isAlarmPlaying" + playBean.sourceType);
                return;
            }
        }

        currentPlayBean = playBean;
        if (mMediaPlayer == null) {
            Logger.log(TAG + " Play create " + playBean.sourceType);
            mMediaPlayer = createPlayer();
        }


        Logger.log(TAG + " Play will play " + playBean.sourceType);

        requestFocus();
        try {
            setPlayerSource(mMediaPlayer, playBean);
            mMediaPlayer.prepare();
            //每次都从头开始,这样提示音就是一致的
            mMediaPlayer.seekTo(0);
            isPlaying = true;
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);

        } catch (IOException e) {
            Logger.log(TAG + " Play error " + e);
            e.printStackTrace();
        }
    }


    /**
     * 播放暂停,每次都释放重新创建
     */
    public void stop() {
        isPlaying = false;
        currentPlayBean = null;
        if (mMediaPlayer != null) {
            releaseFocus();
            Logger.log(TAG + " Play stop not null " + mMediaPlayer.isPlaying());
            mMediaPlayer.pause();
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * 必须调用destroy来释放MediaPlayer
     */

    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }


    public PlayBean getCurrentPlayBean() {
        return currentPlayBean;
    }


    private MediaPlayer createPlayer() {
        MediaPlayer player = new MediaPlayer();
        //接通时前，音频播放走媒体音量
        if (Build.VERSION.SDK_INT >= 21) {
            AudioAttributes.Builder builder = new AudioAttributes.Builder();
            builder.setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC);
            player.setAudioAttributes(builder.build());
        } else {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        return player;

    }

    private void setPlayerSource(MediaPlayer player, PlayBean playBean) {
        AssetManager am = mContext.getAssets();//获得该应用的AssetManager
        AssetFileDescriptor afd = null;

        try {
            switch (playBean.sourceType) {
                case SOURCE_CALL_VOICE:
                    afd = am.openFd("call.wav");
                    player.setLooping(true); //循环播放
                    break;
                case SOURCE_SOS_VOICE:
                    afd = am.openFd("sos.wav");
                    player.setLooping(true);
                    break;
                case SOURCE_PERSON_VOICE:
                    afd = am.openFd("person.wav");
                    player.setLooping(false);
                    break;
                case SOURCE_ZHILLING_VOICE:
                    afd = am.openFd("zhiling.wav");
                    player.setLooping(false);
                    break;
                case SOURCE_ERROR_VOICE:
                    afd = am.openFd("error.wav");
                    player.setLooping(false);
                    break;
                case SOURCE_PTT_VOICE:
                    afd = am.openFd("ptt.wav");
                    player.setLooping(false);
                    break;
                case SOURCE_ALARM_VOICE:
                    afd = am.openFd("alarm.wav");
                    player.setLooping(false);
                    break;
                case SOURCE_CUSTOM:
                    //todo 需要关闭?
                    File file = new File(playBean.customSourcePath);
                    FileInputStream fis = new FileInputStream(file);
                    player.setDataSource(fis.getFD());
                    return;

            }
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
                    afd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void addPlayerListener(PlayerListener playerListener) {
        playerListeners.add(playerListener);
    }

    public void removePlayerListener(PlayerListener playerListener) {
        if (playerListener != null) {
            playerListeners.remove(playerListener);
        }
    }


    private void requestFocus() {
        Logger.log(TAG + " mAudioManager requestFocus ");
        mAudioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                Logger.log(TAG + " mAudioManager requestAudioFocus " + focusChange);
            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        previousMode = mAudioManager.getMode();
        previousSpeakon = mAudioManager.isSpeakerphoneOn();
        mAudioManager.setMode(AudioManager.MODE_RINGTONE);
        mAudioManager.setSpeakerphoneOn(true);
    }

    private void releaseFocus() {
        mAudioManager.setMode(previousMode);
        mAudioManager.setSpeakerphoneOn(previousSpeakon);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Logger.log(TAG + " Play onCompletion");
        for (PlayerListener oneListener : playerListeners) {
            oneListener.onComplete(currentPlayBean);
        }
        if (currentPlayBean != null && currentPlayBean.playerListener != null) {
            currentPlayBean.playerListener.onComplete(currentPlayBean);
        }
        if(!mp.isLooping()){
            stop();
        }
        mAudioManager.abandonAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
            }
        });

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Logger.log(TAG + " Play onError");
        for (PlayerListener oneListener : playerListeners) {
            oneListener.onError(currentPlayBean);
        }
        if (currentPlayBean != null && currentPlayBean.playerListener != null) {
            currentPlayBean.playerListener.onComplete(currentPlayBean);
        }

        stop();
        mAudioManager.abandonAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
            }
        });
        return true;
    }


    public static class PlayBean {
        public boolean stopPrevious;
        public int sourceType;
        public String customSourcePath;
        public PlayerListener playerListener;
    }

    public interface PlayerListener {
        void onComplete(PlayBean playBean);

        void onError(PlayBean playBean);

    }
}
