package huaiye.com.vim.ui.talk;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaiye.cmf.JniIntf;
import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.cmf.sdp.SdpMessageCmStartSessionRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.media.capture.HYCapture;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._api.ApiTalk;
import com.huaiye.sdk.sdkabi._model.UserModel;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.talk.ParamsJoinTalk;
import com.huaiye.sdk.sdkabi._params.talk.ParamsStartTalk;
import com.huaiye.sdk.sdkabi.abilities.talk.callback.CallbackJoinTalk;
import com.huaiye.sdk.sdkabi.abilities.talk.callback.CallbackQuitTalk;
import com.huaiye.sdk.sdkabi.abilities.talk.callback.CallbackStartTalk;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserCancelJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingStatusInfo;
import com.huaiye.sdk.sdpmsgs.talk.CJoinTalkbackRsp;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyKickUserTalkback;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyTalkbackPeerUserOption;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyTalkbackStatusInfo;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserSpeakSet;
import com.huaiye.sdk.sdpmsgs.talk.CQuitTalkbackRsp;
import com.huaiye.sdk.sdpmsgs.talk.CStartTalkbackReq;
import com.huaiye.sdk.sdpmsgs.talk.CStartTalkbackRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.bus.PhoneStatus;
import huaiye.com.vim.common.AlarmMediaPlayer;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.DoubleClickListener;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.utils.WeiXinDateFormat;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.msgs.AppMessages;
import huaiye.com.vim.ui.meet.MeetActivity;
import huaiye.com.vim.ui.meet.MeetNewActivity;

import static huaiye.com.vim.common.AppUtils.STRING_KEY_VGA;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_camera;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_capture;
import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * author: admin
 * date: 2018/01/17
 * version: 0
 * mail: secret
 * desc: TalkActivity
 */
@BindLayout(R.layout.activity_talk)
public class TalkActivity extends AppBaseActivity implements View.OnClickListener {

    @BindView(R.id.texture_bigger)
    TextureView texture_bigger;
    @BindView(R.id.texture_smaller)
    TextureView texture_smaller;
    @BindView(R.id.ll_bottom)
    View ll_bottom;
    @BindView(R.id.iv_mic)
    ImageView iv_mic;
    @BindView(R.id.iv_speaker)
    ImageView iv_speaker;
    @BindView(R.id.tv_speaker)
    TextView tv_speaker;
    //    @BindView(R.id.iv_change_camera)
    ImageView iv_change_camera;
    //    @BindView(R.id.ll_call_status)
    View ll_call_status;
    //    @BindView(R.id.tv_name)
    TextView tv_name;
    //    @BindView(R.id.ll_camera)
    View ll_camera;
    @BindView(R.id.iv_camera)
    ImageView iv_camera;

    @BindExtra
    String strInviteUserId;
    @BindExtra
    String strInviteUserDomain;

    //    @BindExtra
    boolean isTalkStarter;
    boolean isSpeakerOn;
    //    @BindExtra
    CStartTalkbackReq.ToUser toUser;
    //    @BindExtra
    int nTalkID;
    //    @BindExtra
    String strTalkDomainCode;
    SdpMessageCmStartSessionRsp sessionRsp;

    boolean isChangeTalk;
    CNotifyInviteUserJoinMeeting currentMeetingInvite;
    CNotifyUserJoinTalkback currentTalkInvite;
    RxUtils rxUtils;
    boolean isTalking = false;
    long time = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        startSpeakerLound();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        isTalkStarter = getIntent().getBooleanExtra("isTalkStarter", false);
        strTalkDomainCode = getIntent().getStringExtra("strTalkDomainCode");
        nTalkID = getIntent().getIntExtra("nTalkID", 0);
        toUser = (CStartTalkbackReq.ToUser) getIntent().getSerializableExtra("toUser");
        super.onCreate(savedInstanceState);

        findViewById(R.id.ll_speaker).setOnClickListener(this);
        findViewById(R.id.ll_mic).setOnClickListener(this);
        iv_change_camera = (ImageView) findViewById(R.id.iv_change_camera);
        iv_change_camera.setOnClickListener(this);
        ll_camera = findViewById(R.id.ll_camera);
        ll_camera.setOnClickListener(this);
        findViewById(R.id.tv_end_talk).setOnClickListener(this);
        ll_call_status = findViewById(R.id.ll_call_status);
        tv_name = (TextView) findViewById(R.id.tv_name);
        Point point = AppUtils.getPoint(texture_bigger);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) texture_bigger.getLayoutParams();
        layoutParams.width = point.x;
        layoutParams.height = point.y;
        texture_bigger.setLayoutParams(layoutParams);
    }

    @Override
    protected void initActionBar() {
        rxUtils = new RxUtils();
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void doInitDelay() {
        isSpeakerOn = true;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EventBus.getDefault().register(this);
        HYClient.getHYCapture().setCameraConferenceMode(HYCapture.CameraConferenceMode.PORTRAIT);

        if (SP.getString(STRING_KEY_capture).equals(STRING_KEY_VGA)) {
            texture_smaller.getLayoutParams().width = AppUtils.getSize(120);
        } else {
            texture_smaller.getLayoutParams().width = AppUtils.getSize(90);
        }

        if (isTalkStarter) {
            createTalk();
        } else {
            joinTalk();
        }


        texture_smaller.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                if (v.getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT) {
                    switchPreviewPos();
                }
            }
        });

        texture_bigger.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                if (v.getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT) {
                    switchPreviewPos();
                }
            }
        });
    }


    /**
     * 交换预览窗口
     */
    void switchPreviewPos() {

        ViewGroup.LayoutParams smallerParams = texture_smaller.getLayoutParams();
        ViewGroup.LayoutParams biggerParams = texture_bigger.getLayoutParams();

        RelativeLayout parent = (RelativeLayout) texture_smaller.getParent();
        texture_smaller.setLayoutParams(biggerParams);
        texture_bigger.setLayoutParams(smallerParams);

        if (smallerParams.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            // 采集预览全屏
            // 采集->右上角  播方->底部全屏

            parent.bringChildToFront(texture_smaller);
            parent.bringChildToFront(ll_bottom);

        } else {
            // 播放预览全屏
            // 采集->底部全屏 播放->右上角
            parent.bringChildToFront(texture_bigger);
            parent.bringChildToFront(ll_bottom);

        }

    }

    void joinTalk() {
        HYClient.getHYCapture().stopCapture(null);
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            EncryptUtil.startEncrypt(false, strInviteUserId, strInviteUserDomain,
                    "", "", new SdkCallback<SdpMessageCmStartSessionRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmStartSessionRsp sessionRsp) {
                            TalkActivity.this.sessionRsp = sessionRsp;
                            joinTalkReal();
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            showToast(ErrorMsg.getMsg(ErrorMsg.join_talk_err_code));
                            stopMp3();
                        }
                    });
        } else {
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                finish();
                return;
            }
            joinTalkReal();
        }
    }

    void joinTalkReal() {
        ParamsJoinTalk pjk = SdkParamsCenter.Talk.JoinTalk()
                .setCaptureVideoScaleType(SdkBaseParams.VideoScaleType.ASPECT_CROP)
                .setCapturePreview(texture_smaller)
                .setPlayerPreview(texture_bigger)
                .setMediaMode(SdkBaseParams.MediaMode.AudioAndVideo)
                .setCaptureTrunkMessage("AndroidSdk Capture From Talk")
                .setIsAutoStopCapture(true)
                .setCameraIndex(SP.getInteger(STRING_KEY_camera) == 1 ? HYCapture.Camera.Foreground : HYCapture.Camera.Background)
                .setAgreeMode(SdkBaseParams.AgreeMode.Agree)
                .setTalkDomainCode(strTalkDomainCode)
                .setTalkId(nTalkID)
                .setCaptureOrientation(HYCapture.CaptureOrientation.SCREEN_ORIENTATION_PORTRAIT);
        if (sessionRsp != null) {
            pjk.setCallId(sessionRsp.m_nCallId);
        }
        HYClient.getModule(ApiTalk.class).joinTalking(pjk, new CallbackJoinTalk() {
            @Override
            public TextureView onPlayerPreviewNotEnough() {
                return null;
            }

            @Override
            public void onTalkStatusChanged(CNotifyTalkbackStatusInfo cNotifyTalkbackStatusInfo) {

            }

            @Override
            public void onUserRealPlayError(UserModel userModel, ErrorInfo errorInfo) {

            }

            @Override
            public void onKickedFromTalk(CNotifyKickUserTalkback cNotifyKickUserTalkback) {

            }

            @Override
            public void onCaptureStatusChanged(SdpMessageBase sdpMessageBase) {

            }

            @Override
            public void onUserVideoStatusChanged(UserModel userModel, SdpMessageBase sdpMessageBase) {

            }

            @Override
            public void onUserSpeakerStatusChanged(CNotifyUserSpeakSet cNotifyUserSpeakSet) {

            }

            @Override
            public void onTalkFinished() {
                endEncrypt();
            }

            @Override
            public void onSuccess(CJoinTalkbackRsp cJoinTalkbackRsp) {
                iv_speaker.setImageResource(R.drawable.btn_mianti);
                tv_speaker.setText(getString(R.string.talk_notice1));
                isSpeakerOn = true;

                startSpeakerLound();
                iv_mic.setBackgroundResource(R.drawable.ic_mic_on);

                isChangeTalk = false;
                setSpeakOn();
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                showToast(ErrorMsg.getMsg(ErrorMsg.join_talk_err_code));
                stopMp3();
            }
        });
    }

    private void setSpeakOn() {
        rxUtils.doDelay(500, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                HYClient.getHYAudioMgr().from(getSelf()).setSpeakerphoneOn(true);
            }
        }, "speakon");
    }

    void createTalk() {
        tv_name.setText(toUser.strToUserName);
        ll_call_status.setVisibility(View.VISIBLE);
        HYClient.getHYCapture().stopCapture(null);

        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            EncryptUtil.startEncrypt(true, toUser.strToUserID, toUser.strToUserDomainCode,
                    "", "", new SdkCallback<SdpMessageCmStartSessionRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmStartSessionRsp sessionRsp) {
                            TalkActivity.this.sessionRsp = sessionRsp;
                            startTalk();
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            showToast(ErrorMsg.getMsg(ErrorMsg.start_talk_err_code));
                            stopMp3();
                        }
                    });
        } else {
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                finish();
                return;
            }
            startTalk();
        }

    }

    private void startTalk() {
        ParamsStartTalk psk = SdkParamsCenter.Talk.StartTalk()
                .setTalkMode(SdkBaseParams.TalkMode.Normal)
                .setTalkName(AppDatas.Auth().getUserName())
                .setAutoStopCapture(true)
                .setCaptureOrientation(HYCapture.CaptureOrientation.SCREEN_ORIENTATION_PORTRAIT)
                .setOpenRecord(false)
                .setCameraIndex(SP.getInteger(STRING_KEY_camera) == 1 ? HYCapture.Camera.Foreground : HYCapture.Camera.Background)
                .setCapturePreview(texture_smaller)
                .setPlayerPreview(texture_bigger)
                .addInvitedUserInfo(toUser);
        if (sessionRsp != null) {
            psk.setCallId(sessionRsp.m_nCallId);
        }
        HYClient.getModule(ApiTalk.class).startTalking(psk, new CallbackStartTalk() {
            @Override
            public void onTalkStatusChanged(CNotifyTalkbackStatusInfo cNotifyTalkbackStatusInfo) {

            }

            @Override
            public void onRefuseTalk(CNotifyTalkbackPeerUserOption cNotifyTalkbackPeerUserOption) {
                showToast(getString(R.string.common_notice56));
                if (isTalkStarter) {
                    EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_VIDEO_REFUSE, AppUtils.getString(R.string.single_chat_video_voice_refuse_other)));
                }
                stopAlarmMP3();
                finish();
            }

            @Override
            public void onAgreeTalk(CNotifyTalkbackPeerUserOption cNotifyTalkbackPeerUserOption) {
                isTalking = true;
                stopAlarmMP3();
                ll_call_status.setVisibility(View.GONE);
                startSpeakerLound();
                setSpeakOn();
            }

            @Override
            public TextureView onPlayerPreviewNotEnough() {
                return null;
            }

            @Override
            public void onUserRealPlayError(UserModel userModel, ErrorInfo errorInfo) {

            }

            @Override
            public void onNoResponse(CNotifyTalkbackPeerUserOption cNotifyTalkbackPeerUserOption) {
                showToast(getString(R.string.common_notice57));
                if (isTalkStarter) {
                    EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_VIDEO_CANCLE, AppUtils.getString(R.string.single_chat_video_voice_cancle)));
                }
                createError();
            }

            @Override
            public void onUserOffline(CNotifyTalkbackPeerUserOption cNotifyTalkbackPeerUserOption) {
                showToast(getString(R.string.common_notice58));
                if (isTalkStarter) {
                    EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_VIDEO_CANCLE, AppUtils.getString(R.string.single_chat_video_voice_cancle)));
                }
                createError();
            }

            @Override
            public void onUserQuitTalk(CNotifyTalkbackPeerUserOption cNotifyTalkbackPeerUserOption) {
                showToast(getString(R.string.common_notice59));
                createError();
            }

            @Override
            public void onCaptureStatusChanged(SdpMessageBase sdpMessageBase) {

            }

            @Override
            public void onUserVideoStatusChanged(UserModel userModel, SdpMessageBase sdpMessageBase) {

            }

            @Override
            public void onUserSpeakerStatusChanged(CNotifyUserSpeakSet cNotifyUserSpeakSet) {

            }

            @Override
            public void onTalkFinished() {
                endEncrypt();
            }

            @Override
            public void onSuccess(CStartTalkbackRsp cStartTalkbackRsp) {
                playAlarmMP3();
                time = System.currentTimeMillis();
                isTalking = false;
                strTalkDomainCode = cStartTalkbackRsp.strTalkbackDomainCode;
                nTalkID = cStartTalkbackRsp.nTalkbackID;
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                showToast(ErrorMsg.getMsg(ErrorMsg.start_talk_err_code));
                stopMp3();
            }
        });
    }

    /**
     * 停止关闭
     */
    private void stopMp3() {
        rxUtils.doDelay(1200, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                stopAlarmMP3();
                finish();
            }
        }, "stopmp3");
    }

    /**
     * 创建出错
     */
    private void createError() {
        stopAlarmMP3();
        endTalk();
        finish();

    }

    void endTalk() {
        HYClient.getModule(ApiTalk.class).quitTalking(SdkParamsCenter.Talk.QuitTalk()
                .setTalkDomainCode(strTalkDomainCode)
                .setTalkId(nTalkID)
                .setStopCapture(true), null);
    }

    void endEncrypt() {
        if (sessionRsp != null) {
            EncryptUtil.endEncrypt(sessionRsp.m_nCallId);
        }
    }

    public void ToggleBackgroundState(final boolean enterBackground) {
        try {

            JniIntf.SetCapturerPreviewTexture(enterBackground ? null
                    : texture_smaller.getSurfaceTexture());

//            int nPlayerSessionId = JniIntf.GetPlaySession();
//            JniIntf.SetPlayerSurface(nPlayerSessionId, enterBackground ? null
//                    : new Surface(texture_bigger.getSurfaceTexture()));
        } catch (Exception e) {
        }

    }

    public void onMeetInviteCancel(CNotifyInviteUserCancelJoinMeeting data, long millis) {
        if (data == null) {
            return;
        }
        if (currentMeetingInvite == null) {
            return;
        }
        if (currentMeetingInvite.nMeetingID == data.nMeetingID &&
                currentMeetingInvite.strMeetingDomainCode.equals(data.strMeetingDomainCode)) {
            if (mLogicDialog != null && mLogicDialog.isShowing()) {
                mLogicDialog.dismiss();
            }
        }
    }

    @Override
    public void onMeetInvite(final CNotifyInviteUserJoinMeeting data, final long millis) {
        if (data == null) {
            return;
        }
        if (data.nMeetingStatus != 1) {
            return;
        }
        if (mLogicDialog.isShowing()) {
            HYClient.getModule(ApiMeet.class).joinMeeting(SdkParamsCenter.Meet.JoinMeet()
                    .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                    .setMeetID(data.nMeetingID)
                    .setMeetDomainCode(data.strMeetingDomainCode), null);
            return;
        }
        currentMeetingInvite = data;
        final CNotifyInviteUserJoinMeeting temp = data;
        // 会议中来会议邀请，对话框提示
        mLogicDialog.setMessageText(temp.strInviteUserName + getString(R.string.common_notice21))
                .setTitleText(getString(R.string.common_notice20))
                .setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppMessages.get().del(millis);
                        HYClient.getModule(ApiMeet.class).joinMeeting(SdkParamsCenter.Meet.JoinMeet()
                                .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                                .setMeetID(temp.nMeetingID)
                                .setMeetDomainCode(temp.strMeetingDomainCode), null);
                    }
                })
                .setConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppMessages.get().del(millis);
                        endEncrypt();
                        HYClient.getModule(ApiTalk.class).quitTalking(SdkParamsCenter.Talk.QuitTalk()
                                .setTalkDomainCode(strTalkDomainCode)
                                .setTalkId(nTalkID)
                                .setStopCapture(true), new CallbackQuitTalk() {
                            @Override
                            public boolean isContinueOnStopCaptureError(ErrorInfo errorInfo) {
                                return true;
                            }

                            @Override
                            public void onSuccess(CQuitTalkbackRsp cQuitTalkbackRsp) {
                                stopAlarmMP3();

                                Intent intent = new Intent(getSelf(), MeetNewActivity.class);
                                intent.putExtra("strMeetDomainCode", temp.strMeetingDomainCode);
                                intent.putExtra("nMeetID", temp.nMeetingID);
                                intent.putExtra("millis", millis);
                                intent.putExtra("mMediaMode", temp.getRequiredMediaMode());
                                intent.putExtra("strInviteUserDomainCode", temp.strInviteUserDomainCode);
                                intent.putExtra("strInviteUserTokenID", temp.strInviteUserTokenID);
                                intent.putExtra("strInviteUserId", temp.strInviteUserId);
                                startActivity(intent);

                                finish();
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                showToast(ErrorMsg.getMsg(ErrorMsg.get_meet_info_err_code));
                            }
                        });
                    }
                });
        mLogicDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                currentMeetingInvite = null;
                currentTalkInvite = null;
            }
        });
        mLogicDialog.show();
    }

    @Override
    public void onTalkInvite(final CNotifyUserJoinTalkback data, final long millis) {
        if (data == null) {
            createError();
            return;
        }

        if (mLogicDialog.isShowing()) {
            // 会议中不接受对讲
            HYClient.getModule(ApiTalk.class).joinTalking(SdkParamsCenter.Talk.JoinTalk()
                    .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                    .setTalkId(data.nTalkbackID)
                    .setTalkDomainCode(data.strTalkbackDomainCode), null);
            return;
        }
        currentTalkInvite = data;
        final CNotifyUserJoinTalkback temp = data;
        // 会议中来会议邀请，对话框提示
        mLogicDialog.setTitleText(getString(R.string.common_notice20))
                .setMessageText(data.strFromUserName + getString(R.string.common_notice22))
                .setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 会议中不接受对讲
                        AppMessages.get().del(millis);
                        HYClient.getModule(ApiTalk.class).joinTalking(SdkParamsCenter.Talk.JoinTalk()
                                .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                                .setTalkId(temp.nTalkbackID)
                                .setTalkDomainCode(temp.strTalkbackDomainCode), null);
                    }
                }).setConfirmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarmMP3();

                AppMessages.get().del(millis);


                if (data.getRequiredMediaMode() == SdkBaseParams.MediaMode.Audio) {
                    endTalk();
                    Intent intent = new Intent(TalkActivity.this, TalkVoiceActivity.class);
                    intent.putExtra("strTalkDomainCode", data.strTalkbackDomainCode);
                    intent.putExtra("nTalkID", data.nTalkbackID);
                    intent.putExtra("strInviteName", data.strFromUserName);
                    intent.putExtra("strInviteUserId", data.strFromUserID);
                    intent.putExtra("strInviteUserDomain", data.strFromUserDomainCode);
                    startActivity(intent);
                    finish();
                    return;
                }


                isChangeTalk = true;
                String tempDomainCode = strTalkDomainCode;
                int tempnTalkID = nTalkID;

                strTalkDomainCode = temp.strTalkbackDomainCode;
                nTalkID = temp.nTalkbackID;
                endEncrypt();
                HYClient.getModule(ApiTalk.class).quitTalking(SdkParamsCenter.Talk.QuitTalk()
                        .setTalkDomainCode(tempDomainCode)
                        .setTalkId(tempnTalkID)
                        .setStopCapture(true), new CallbackQuitTalk() {
                    @Override
                    public boolean isContinueOnStopCaptureError(ErrorInfo errorInfo) {
                        return false;
                    }

                    @Override
                    public void onSuccess(CQuitTalkbackRsp cQuitTalkbackRsp) {
                        isTalkStarter = false;
                        toUser = null;

                        joinTalk();
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast(getString(R.string.common_notice23));
                    }
                });
            }
        });
        mLogicDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                currentMeetingInvite = null;
                currentTalkInvite = null;
            }
        });
        mLogicDialog.show();

    }

    @Override
    public void onClick(View v) {
        onViewClicked(v);
    }

    //    @OnClick({R.id.ll_mic, R.id.ll_speaker, R.id.tv_end_talk, R.id.iv_change_camera, R.id.ll_camera})
    void onViewClicked(View v) {
        if (v.getId() == R.id.tv_end_talk) {
            onBackPressed();
            return;
        }
        if (!HYClient.getHYPlayer().isVideoRendering(texture_bigger)) {
            showToast(getString(R.string.common_notice60));
            return;
        }
        switch (v.getId()) {
            case R.id.ll_mic:
                boolean isMicOn = HYClient.getHYCapture().toggleCaptureAudio();
                if (isMicOn) {
                    iv_mic.setImageResource(R.drawable.btn_jingyin);
                } else {
                    iv_mic.setImageResource(R.drawable.btn_jingyinquxiao);
                }
                break;
            case R.id.ll_speaker:
                isSpeakerOn = !isSpeakerOn;
                if (isSpeakerOn) {
                    HYClient.getHYAudioMgr().from(getSelf()).setSpeakerphoneOn(true);
                    iv_speaker.setImageResource(R.drawable.btn_mianti);
                    tv_speaker.setText(getString(R.string.talk_notice1));
                } else {
                    HYClient.getHYAudioMgr().from(getSelf()).setSpeakerphoneOn(false);
                    iv_speaker.setImageResource(R.drawable.btn_mitiquxiao);
                    tv_speaker.setText(getString(R.string.talk_notice2));
                }
                break;
            case R.id.iv_change_camera:
//                showToast("没有前置摄像头哦");
                // 摄像头切换
                HYClient.getHYCapture().toggleInnerCamera();
                break;
            case R.id.ll_camera:
                if (HYClient.getHYCapture().isCaptureVideoOn()) {
                    HYClient.getHYCapture().setCaptureVideoOn(false);
                    iv_camera.setImageResource(R.drawable.btn_huiyi_shexiangtou_guanbi);
                } else {
                    HYClient.getHYCapture().setCaptureVideoOn(true);
                    iv_camera.setImageResource(R.drawable.btn_huiyi_shexiangtou);
                }
                break;
            default:
                break;
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onDestroy() {
        if (isTalkStarter && isTalking) {
            time = System.currentTimeMillis() - time;
            callBackVideoTime();
        }
        stopSpeakerLound();

        rxUtils.clearAll();

        stopAlarmMP3();

        endEncrypt();

        HYClient.getHYCapture().setCameraConferenceMode(HYCapture.CameraConferenceMode.PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.releaseInstance();
        }
    }

    void playAlarmMP3() {
        AlarmMediaPlayer.get().playAlarm();

    }

    void stopAlarmMP3() {
        AlarmMediaPlayer.get().stop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PhoneStatus status) {
        if (status.isBusy) {
            createError();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyMeetingStatusInfo info) {
        if (currentMeetingInvite == null) {
            return;
        }
        if (info.nMeetingID == currentMeetingInvite.nMeetingID
                && info.isMeetFinished() && mLogicDialog.isShowing()) {
            mLogicDialog.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyTalkbackStatusInfo info) {
        if (info.nTalkbackID == nTalkID && info.isTalkingStopped() && !isChangeTalk) {
            stopAlarmMP3();
            finish();
            showToast(getString(R.string.talk_end));
        }

        if (currentTalkInvite == null) {
            return;
        }
        if (info.nTalkbackID == currentTalkInvite.nTalkbackID
                && info.isTalkingStopped() && mLogicDialog.isShowing()) {
            mLogicDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        HYClient.getHYCapture().onCaptureFront();
        HYClient.getHYPlayer().onPlayFront();
        rxUtils.doDelay(30, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                ToggleBackgroundState(false);
            }
        }, "toggle");
    }

    @Override
    protected void onPause() {
        super.onPause();
        HYClient.getHYCapture().onCaptureBackground();
        HYClient.getHYPlayer().onPlayBackground();
        ToggleBackgroundState(true);
    }

    @Override
    public void onBackPressed() {
        if(isTalkStarter && isTalking) {
            getLogicDialog()
                    .setTitleText(getString(R.string.notice))
                    .setMessageText(getString(R.string.talk_notice))
                    .setConfirmClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isTalkStarter && !isTalking) {
                                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_VIDEO_CANCLE, AppUtils.getString(R.string.single_chat_video_voice_cancle)));
                            }
                            createError();
                        }
                    })
                    .show();
        } else {
            if (isTalkStarter) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_VIDEO_CANCLE, AppUtils.getString(R.string.single_chat_video_voice_cancle)));
            }
            createError();
        }

    }


    private void callBackVideoTime() {
        EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_VIDEO_SUCCESS, WeiXinDateFormat.getTime(time, "mm:ss")));
    }
}
