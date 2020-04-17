package huaiye.com.vim.ui.chat;

import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMReq;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.media.player.HYPlayer;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.msg.SdkMsgNotifyPlayStatus;
import com.huaiye.sdk.media.player.sdk.mix.VideoCallbackWrapper;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.ChatMessageBase;
import huaiye.com.vim.dao.msgs.UserInfo;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;


@BindLayout(R.layout.activity_chat_yuehoujifeng_video)
public class YueHouJiFengVideoActivity extends AppBaseActivity {

    @BindView(R.id.video_texture_yuehoujifeng_content)
    TextureView videoTextureYuehoujifengContent;
    @BindView(R.id.video_yuehoujifeng_time)
    TextView videoYuehoujifengTime;
    @BindView(R.id.video_loading)
    ProgressBar videoLoading;

    private Disposable mDisposable;
    File file;
    @BindExtra
    ChatMessageBase chatMessage;
    @BindExtra
    boolean isGroup;
    @BindExtra
    String strGroupID;
    @BindExtra
    String strUserID;
    @BindExtra
    String strUserDomainCode;
    @BindExtra
    String strGroupDomain;
    @BindExtra
    ArrayList<UserInfo> usersTrans;
    ArrayList<SdpMessageCmProcessIMReq.UserInfo> users = new ArrayList<>();
    File fC;

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void doInitDelay() {
        if (isGroup) {
            for (UserInfo temp : usersTrans) {
                if (!temp.strUserID.equals(AppAuth.get().getUserID()) && users.isEmpty()) {
                    SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
                    info.strUserDomainCode = temp.strUserDomainCode;
                    info.strUserID = temp.strUserID;
                    users.add(info);
                }
            }
        } else {
            SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
            info.strUserDomainCode = strUserDomainCode;
            info.strUserID = strUserID;
            users.add(info);
        }

        if (null != chatMessage) {
            if (chatMessage.bEncrypt == 1) {
                if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                    unEncrypt(chatMessage);
                } else {
                    if(nEncryptIMEnable) {
                        EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                        finish();
                        return;
                    }
                    showToast(getString(R.string.jiami_notice5));
                }
            } else {
                startPlay(chatMessage.localFilePath);
            }
        }
    }

    private void startPlay(String str) {
        file = new File(str);
        HYClient.getHYPlayer().startPlay(Player.Params.TypeVideoOfflineRecord()
                .setResourcePath(str)
                .setPreview(videoTextureYuehoujifengContent)
                .setMixCallback(new VideoCallbackWrapper() {
                    @Override
                    public void onSuccess(VideoParams param) {
                        super.onSuccess(param);
                    }

                    @Override
                    public void onGetVideoRange(VideoParams param, int start, int end) {
                        super.onGetVideoRange(param, start, end);
                        videoYuehoujifengTime.setText(end + "''");
                        chatMessage.fireTime = end;
                        coutTime();
                    }

                    @Override
                    public void onVideoProgressChanged(VideoParams param, HYPlayer.ProgressType type, int current, int total) {
                        super.onVideoProgressChanged(param, type, current, total);
                    }

                    @Override
                    public void onVideoStatusChanged(VideoParams param, SdpMessageBase msg) {
                        super.onVideoStatusChanged(param, msg);
                        if (msg instanceof SdkMsgNotifyPlayStatus) {
                            SdkMsgNotifyPlayStatus status = (SdkMsgNotifyPlayStatus) msg;
                            if (status.isStopped()
                                    && !isFinishing()) {

                                if (!status.isOperationFromUser()) {
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

    private void unEncrypt(ChatMessageBase data) {
        File file = new File(EncryptUtil.getNewFile(data.localFilePath));
        if (file.exists()) {
            startPlay(file.getAbsolutePath());
        } else {
            EncryptUtil.encryptFile(data.localFilePath, file.getAbsolutePath(),
                    false, isGroup, isGroup ? strGroupID : "", isGroup ? strGroupDomain : "",
                    isGroup ? "" : strUserID, isGroup ? "" : strUserDomainCode, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                            startPlay(resp.m_strData);
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            showToast(getString(R.string.jiami_notice5));
                        }
                    }
            );
        }

    }

    private void coutTime() {

        mDisposable = Flowable.intervalRange(0, chatMessage.fireTime + 1, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        videoYuehoujifengTime.setText(String.valueOf(chatMessage.fireTime - aLong) + "''");
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        videoYuehoujifengTime.setText("0''");
                        finish();
                    }
                })
                .subscribe();
    }

    @Override
    protected void onDestroy() {
        HYClient.getHYPlayer().stopPlayEx(null, videoTextureYuehoujifengContent);
        deleteFile();
        showToast(AppUtils.getString(R.string.string_name_yuehoujifeng_tip));
        MessageEvent nMessageEvent = new MessageEvent(AppUtils.EVENT_MESSAGE_YUEHOUJIFENG);
        nMessageEvent.obj1 = chatMessage;
        EventBus.getDefault().post(nMessageEvent);
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    private void deleteFile() {
        if (null != chatMessage && !TextUtils.isEmpty(chatMessage.localFilePath)) {
            File file = new File(chatMessage.localFilePath);
            if (null != file && file.exists()) {
                file.delete();
            }
        }

        if (file != null && file.exists()) {
            file.delete();
        }
    }

}
