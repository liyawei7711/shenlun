package huaiye.com.vim.common;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

import com.huaiye.cmf.audiomanager.AppAudioManager;

import java.util.Set;

import huaiye.com.vim.VIMApp;

public class AppAudioManagerWrapper {

    AppAudioManager audio;

    public AppAudioManagerWrapper(){

    }

    public void start(){
        if (!AppUtils.audioDevice.contains(Build.MODEL) && audio == null) {
            audio = AppAudioManager.create(AppUtils.ctx, true);
            audio.start(new AppAudioManager.AudioManagerEvents() {
                @Override
                public void onAudioDeviceChanged(AppAudioManager.AudioDevice audioDevice, Set<AppAudioManager.AudioDevice> set) {
                }
            });
            sameCallVolume();
        }
    }

    public void stop(){
        if (audio != null) {
            audio.stop();
            audio = null;
        }
    }

    /**
     * 将通话音量与媒体播放音量大小同步
     */
    private static void sameCallVolume(){
        AudioManager mAudioManager = (AudioManager) VIMApp.getInstance().getSystemService(Context.AUDIO_SERVICE);
        int musicVolume = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        int maxMusic = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC);
        int maxCall = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_VOICE_CALL);
        int needVoiceVolume = musicVolume * maxCall / maxMusic;
//        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,needVoiceVolume,AudioManager.FLAG_PLAY_SOUND);
        //使用flag 0 可以不会有声音震动的反馈
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,needVoiceVolume,0);
    }

}
