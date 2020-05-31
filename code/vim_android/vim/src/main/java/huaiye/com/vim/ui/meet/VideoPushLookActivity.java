package huaiye.com.vim.ui.meet;

import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.cmf.sdp.SdpMessageCmStartSessionRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.msg.SdkMsgNotifyPlayStatus;
import com.huaiye.sdk.media.player.sdk.mix.VideoCallbackWrapper;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.dao.msgs.ChatMessageBean;
import huaiye.com.vim.ui.talk.TalkActivity;

import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

@BindLayout(R.layout.activity_video_push_look)
public class VideoPushLookActivity extends AppBaseActivity {

    @BindView(R.id.video_push_look_texture)
    TextureView videoPushLookTexture;
    @BindView(R.id.video_push_look_back)
    ImageView videoPushLookBack;

    @BindExtra
    ChatMessageBean chatMessageBean;

    private String strName;
    private String strDomainCode;
    private String strUserTokenID;
    private SdpMessageCmStartSessionRsp sessionRsp;


    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void doInitDelay() {
        if (null == chatMessageBean) {
            finish();
            return;
        } else {
            analysisContent(chatMessageBean.content);
        }
        startSpeakerLound();
        startPlay();
    }

    private void analysisContent(String content) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(content);
            if (null != jsonObject) {
                if (jsonObject.has("strName")) {
                    strName = jsonObject.getString("strName");
                }
                if (jsonObject.has("strDomainCode")) {
                    strDomainCode = jsonObject.getString("strDomainCode");
                }
                if (jsonObject.has("strUserTokenID")) {
                    strUserTokenID = jsonObject.getString("strUserTokenID");
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            showToast(AppUtils.getString(R.string.video_watch_false));
            finish();
            return;
        }

    }

    private void startPlay() {
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            EncryptUtil.startEncrypt(false, chatMessageBean.fromUserId, chatMessageBean.fromUserDomain,
                    "", "", new SdkCallback<SdpMessageCmStartSessionRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmStartSessionRsp sessionRsp) {
                            VideoPushLookActivity.this.sessionRsp = sessionRsp;
                            startPlayer(sessionRsp.m_nCallId);
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            showToast(AppUtils.getString(R.string.video_watch_false));
                            close();
                        }
                    });
        } else {
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                finish();
                return;
            }
            startPlayer(-1);
        }

    }

    private void startPlayer(int caller) {
        VideoParams videoParams = Player.Params.TypeUserReal()
                .setUserDomainCode(strDomainCode)
                .setUserTokenID(strUserTokenID)
                .setPreview(videoPushLookTexture)
                .setMixCallback(videoCallbackWrapper);
        if (sessionRsp != null) {
            videoParams.getHYPlayerConfig().setPlayerCallID(sessionRsp.m_nCallId);
        }
        HYClient.getHYPlayer().startPlay(videoParams);
    }

    VideoCallbackWrapper videoCallbackWrapper = new VideoCallbackWrapper() {
        @Override
        public void onSuccess(VideoParams param) {
            super.onSuccess(param);

        }

        @Override
        public void onError(VideoParams param, SdkCallback.ErrorInfo errorInfo) {
            super.onError(param, errorInfo);
            showToast(AppUtils.getString(R.string.video_watch_false));
            close();
        }

        @Override
        public void onVideoStatusChanged(VideoParams param, SdpMessageBase msg) {
            super.onVideoStatusChanged(param, msg);
            switch (msg.GetMessageType()) {
                case SdkMsgNotifyPlayStatus.SelfMessageId:
                    SdkMsgNotifyPlayStatus playStatus = (SdkMsgNotifyPlayStatus) msg;

                    if (playStatus.isStopped()) {
                        if (!playStatus.isOperationFromUser()) {
                            showToast(AppUtils.getString(R.string.duifang_close));
                            close();
                        }
                    }

                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        stopPlayer();
        stopSpeakerLound();
        super.onDestroy();
    }

    public void stopPlayer() {
        HYClient.getHYPlayer().stopPlayEx(null, videoPushLookTexture);
    }

    @OnClick(R.id.video_push_look_back)
    public void close() {
        finish();
    }
}
