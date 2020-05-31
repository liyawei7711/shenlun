package huaiye.com.vim.common.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huaiye.sdk.HYClient;
import com.ttyy.commonanno.Injectors;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import huaiye.com.vim.R;

/**
 * author: admin
 * date: 2017/09/21
 * version: 0
 * mail: secret
 * desc: MediaRecordProgress
 */

@BindLayout(R.layout.view_progress)
public class MediaRecordProgress extends LinearLayout implements SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.tv_start_time)
    TextView tv_start_time;
    @BindView(R.id.bar_progress)
    SeekBar bar_progress;
    @BindView(R.id.tv_stop_time)
    TextView tv_stop_time;

    TextureView texture_video;

    public MediaRecordProgress(Context context) {
        this(context, null);
    }

    public MediaRecordProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaRecordProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Injectors.get().injectView(this);
        bar_progress.setOnSeekBarChangeListener(this);
    }

    public void attachVideoTexture(TextureView textureView) {
        this.texture_video = textureView;
    }

    //设置当前时间
    public void setCurrentTime(int currTime) {
        tv_start_time.setText(formatTime(currTime));
        bar_progress.setProgress(currTime);
    }

    //设置总时间
    public void setMaxTime(int maxTime) {
        bar_progress.setMax(maxTime);
        tv_stop_time.setText(formatTime(maxTime));
    }

    //改变进度条
    protected void changeProgress(int progress) {
        if (isFromUser) {
            HYClient.getHYPlayer().setProgressEx(progress, texture_video);
        }
    }

    private String formatTime(int timeSec) {
        int hour = timeSec / 3600;
        int min = (timeSec % 3600) / 60 + hour * 60;
        int sec = timeSec % 60;
        return String.format("%02d:%02d", min, sec);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isFromUser = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isFromUser = false;
    }

    boolean isFromUser;

    /*========================== SeekBar事件 ===================================*/
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (isFromUser) {
            changeProgress(progress);
        }
    }

}
