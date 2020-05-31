package huaiye.com.vim.ui.meet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.media.MediaStatus;
import com.huaiye.sdk.media.player.HYPlayer;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.sdk.mix.VideoCallbackWrapper;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.views.MediaRecordProgress;

/**
 * author: admin
 * date: 2017/09/20
 * version: 0
 * mail: secret
 * desc: MeetRealPlayActivity
 */
@BindLayout(R.layout.activity_meet_playback)
public class MeetPlaybackActivity extends AppBaseActivity {

    @BindView(R.id.texture_video)
    TextureView texture_video;
    @BindView(R.id.view_progress)
    MediaRecordProgress view_progress;
    @BindView(R.id.iv_play_status)
    ImageView iv_play_status;

    @BindExtra
    String strMeetDomainCode;
    @BindExtra
    int nMeetID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNavigate().setVisibility(View.GONE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        view_progress.attachVideoTexture(texture_video);

        HYClient.getHYPlayer().startPlay(Player.Params.TypeMeetRecord()
                .setMeetDomainCode(strMeetDomainCode)
                .setMeetID(nMeetID)
                .setPreview(texture_video)
                .setListMode(1)
                .setMixCallback(new VideoCallbackWrapper() {

                    @Override
                    public void onSuccess(VideoParams param) {
                        super.onSuccess(param);
                    }

                    @Override
                    public void onError(VideoParams param, SdkCallback.ErrorInfo errorInfo) {
                        super.onError(param, errorInfo);
                        if (errorInfo.getCode() == 1010100003) {
                            showToast("无会议记录");
                        } else {
                            showToast(ErrorMsg.getMsg(ErrorMsg.start_play_err_code));
                        }
                        delayFinish();
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
                        switch (MediaStatus.get(msg)) {
                            case VIDEO_STATUS:
                                if (MediaStatus.VideoStatus.isStopped(msg)
                                        && !MediaStatus.VideoStatus.isActionFromUser(msg)) {
                                    showToast("播放完成");
                                    delayFinish();
                                }
                                break;
                            case VIDEO_STREAM:

                                break;
                        }
                    }
                }));
    }

    private void delayFinish() {
        new RxUtils().doDelay(1500, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                finish();
            }
        }, "finish");
    }

    @Override
    protected void initActionBar() {

    }

    @Override
    public void doInitDelay() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        HYClient.getHYPlayer().onPlayFront();
        if (!isVideoPaused) {
            HYClient.getHYPlayer().pausePlayEx(false, texture_video);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        HYClient.getHYPlayer().onPlayBackground();
        HYClient.getHYPlayer().pausePlayEx(true, texture_video);
    }

    boolean isVideoPaused = false;

    @OnClick(R.id.iv_play_status)
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_play_status:
                isVideoPaused = HYClient.getHYPlayer().togglePausePlayEx(texture_video)
                        .isPlayPausedEx(texture_video);
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
        super.onBackPressed();
        HYClient.getHYPlayer().stopPlayEx(null, texture_video);
    }
}
