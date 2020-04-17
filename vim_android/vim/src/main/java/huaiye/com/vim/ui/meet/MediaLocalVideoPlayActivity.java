package huaiye.com.vim.ui.meet;

import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.media.player.HYPlayer;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.msg.SdkMsgNotifyPlayStatus;
import com.huaiye.sdk.media.player.sdk.mix.VideoCallbackWrapper;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.views.MediaRecordProgress;

/**
 * author: admin
 * date: 2017/09/15
 * version: 0
 * mail: secret
 * desc: MediaLocalVideoPlayActivity
 */
@BindLayout(R.layout.activity_local_play)
public class MediaLocalVideoPlayActivity extends AppBaseActivity {

    @BindView(R.id.video_texture)
    TextureView video_texture;
    @BindView(R.id.view_progress)
    MediaRecordProgress view_progress;
    @BindView(R.id.iv_play_status)
    ImageView iv_play_status;

    @BindExtra
    String path;

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(getString(R.string.video_record_local_video))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        view_progress.attachVideoTexture(video_texture);
        HYClient.getHYPlayer().startPlay(Player.Params.TypeVideoOfflineRecord()
                .setResourcePath(path)
                .setPreview(video_texture)
                .setMixCallback(new VideoCallbackWrapper() {
                    @Override
                    public void onSuccess(VideoParams param) {
                        super.onSuccess(param);
                    }

                    @Override
                    public void onGetVideoRange(VideoParams param, int start, int end) {
                        super.onGetVideoRange(param, start, end);
                        view_progress.setMaxTime(end);
                    }

                    @Override
                    public void onVideoProgressChanged(VideoParams param, HYPlayer.ProgressType type, int current, int total) {
                        super.onVideoProgressChanged(param, type, current, total);
                        view_progress.setCurrentTime(current);
                    }

                    @Override
                    public void onVideoStatusChanged(VideoParams param, SdpMessageBase msg) {
                        super.onVideoStatusChanged(param, msg);
                        if (msg instanceof SdkMsgNotifyPlayStatus) {
                            SdkMsgNotifyPlayStatus status = (SdkMsgNotifyPlayStatus) msg;
                            if (status.isStopped()
                                    && !isFinishing()) {

                                if (!status.isOperationFromUser()) {
                                    showToast(getString(R.string.play_end));
                                    finish();

                                }

                            }
                        }


                    }

                    @Override
                    public void onError(VideoParams param, SdkCallback.ErrorInfo errorInfo) {
                        super.onError(param, errorInfo);
                        showToast(errorInfo + "");
                    }
                }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isVideoPaused) {
            HYClient.getHYPlayer().pausePlayEx(false, video_texture);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        HYClient.getHYPlayer().pausePlayEx(true, video_texture);
    }

    boolean isVideoPaused = false;

    @OnClick(R.id.iv_play_status)
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_play_status:
                isVideoPaused = HYClient.getHYPlayer().togglePausePlayEx(video_texture)
                        .isPlayPausedEx(video_texture);
                if (isVideoPaused) {
                    iv_play_status.setImageResource(R.drawable.ic_play_start);
                } else {
                    iv_play_status.setImageResource(R.drawable.ic_play_pause);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        HYClient.getHYPlayer().stopPlayEx(null, video_texture);
        super.onBackPressed();
    }
}
