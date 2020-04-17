package huaiye.com.vim.ui.meet;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.cmf.sdp.SdpMessageCmStartSessionRsp;
import com.huaiye.cmf.sdp.SdpUITask;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.media.capture.HYCapture;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._api.ApiTalk;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.meet.ParamsJoinMeetMultiPlay;
import com.huaiye.sdk.sdkabi.abilities.meet.callback.CallbackJoinMeet;
import com.huaiye.sdk.sdkabi.abilities.meet.callback.CallbackQuitMeet;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CInviteUserMeetingRsp;
import com.huaiye.sdk.sdpmsgs.meet.CJoinMeetingRsp;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserCancelJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyKickUserMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingPushVideo;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingRaiseInfo;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingStatusInfo;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyPeerUserMeetingInfo;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyTalkbackStatusInfo;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;
import com.huaiye.sdk.sdpmsgs.whiteboard.CNotifyUpdateWhiteboard;
import com.huaiye.sdk.sdpmsgs.whiteboard.CNotifyWhiteboardStatus;
import com.huaiye.sdk.sdpmsgs.whiteboard.CStopWhiteboardShareRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.bus.NewChatMessage;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.common.dialog.LogicDialog;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.AppMessages;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContactsNew;
import huaiye.com.vim.ui.contacts.sharedata.ConvertContacts;
import huaiye.com.vim.ui.meet.adapter.MeetAdapter;
import huaiye.com.vim.ui.meet.basemodel.RaiseMessage;
import huaiye.com.vim.ui.meet.fragments.MeetMemberNewFragment;
import huaiye.com.vim.ui.meet.fragments.SpeakerFragment;
import huaiye.com.vim.ui.talk.TalkActivity;
import huaiye.com.vim.ui.talk.TalkVoiceActivity;

import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * Created by ywt on 2019/3/4.
 * 加入会议后的页面
 */
@BindLayout(R.layout.activity_meet_new)
public class MeetNewActivity extends AppBaseActivity implements SdpUITask.SdpUIListener {
    @BindView(R.id.meet_viewpage)
    ViewPager meet_viewpage;

    @BindExtra
    public int nMeetID;
    @BindExtra
    public String strMeetDomainCode;
    @BindExtra
    String strInviteUserDomainCode;
    @BindExtra
    String strInviteUserId;
    @BindExtra
    String strInviteUserTokenID;
    @BindExtra
    boolean isWatch;
    //匿名人员不能邀请
    @BindExtra
    boolean closeInvisitor;
    @BindExtra
    public boolean isCloseVideo;
    @BindExtra
    boolean isCloseVoice;
    @BindExtra
    boolean isMeetStarter;
    @BindExtra
    SdkBaseParams.MediaMode mMediaMode;

    int count = 0;//邀请总数
    int cuntCount = 0;//返回数
    int cuntCountSuccess = 0;//返回成功数

    private MeetAdapter mMeetAdapter;
    private SdpUITask mSdpUITask;
    //    private boolean isClickClose;
    private LogicDialog dialog;
    private CNotifyInviteUserJoinMeeting currentMeetingInvite;
    private CNotifyUserJoinTalkback currentTalkInvite;
    private CGetMeetingInfoRsp mCurrentGetMeetingInfoRsp;
    private RequestOptions requestFriendHeadOptions;

    public static SdpMessageCmStartSessionRsp sessionRsp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        initHeadRequestOption();
        startSpeakerLound();
        if (isMeetStarter) {
            EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_CREATE_MEETTING_SUCCESS));
        }
        Log.d("test", "MeetNewActivity onCreate");
    }

    private void initHeadRequestOption() {
        requestFriendHeadOptions = new RequestOptions();
        requestFriendHeadOptions.centerCrop()
                .dontAnimate()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.default_image_personal)
                .error(R.drawable.default_image_personal)
                .optionalTransform(new CircleCrop());
    }

    public RequestOptions getRequestOptions() {
        return requestFriendHeadOptions;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("test", "MeetNewActivity onResume");
        HYClient.getHYCapture().onCaptureFront();
        HYClient.getHYPlayer().onPlayFront();
        /*rxUtils.doDelay(200, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                ToggleBackgroundState(false);
            }
        }, "toggle");*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("test", "MeetNewActivity onPause");
    }


    @Override
    protected void onDestroy() {
        if (isMeetStarter) {
            EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_CLOSE_MEETTING));
        }
        super.onDestroy();
        clearContacts();

        destruct();

        stopSpeakerLound();

        endEncrypt();

        HYClient.getHYCapture().setCameraConferenceMode(HYCapture.CameraConferenceMode.PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        EventBus.getDefault().unregister(this);
//        HYClient.getHYCapture().stopCapture(null);
//        mMeetInviteFragment = null;
        /*currentFragment = null;
        mMeetMembersFragment = null;
        mMeetLayoutFragment = null;
        mMeetBoardFragment = null;*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.releaseInstance();
        }

    }

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);

        EventBus.getDefault().register(this);
        mSdpUITask = new SdpUITask();
        mSdpUITask.setSdpMessageListener(this);
        mSdpUITask.registerSdpNotify(CNotifyPeerUserMeetingInfo.SelfMessageId);
        mSdpUITask.registerSdpNotify(CNotifyMeetingRaiseInfo.SelfMessageId);
        mSdpUITask.registerSdpNotify(CNotifyUpdateWhiteboard.SelfMessageId);

        HYClient.getHYCapture().setCameraConferenceMode(HYCapture.CameraConferenceMode.PORTRAIT);
    }

    @Override
    public void doInitDelay() {
        requestInfo();
    }

    /**
     * 获取会议信息
     */
    private void requestInfo() {
        HYClient.getModule(ApiMeet.class)
                .requestMeetDetail(SdkParamsCenter.Meet.RequestMeetDetail()
                                .setnListMode(1)
                                .setMeetID(nMeetID)
                                .setMeetDomainCode(strMeetDomainCode),
                        new SdkCallback<CGetMeetingInfoRsp>() {
                            @Override
                            public void onSuccess(CGetMeetingInfoRsp cGetMeetingInfoRsp) {
                                mCurrentGetMeetingInfoRsp = cGetMeetingInfoRsp;
                                startJoineMeet();
//                                isMeetStarter = cGetMeetingInfoRsp.strMainUserID.equals(String.valueOf(AppDatas.Auth().getUserID()));
//                                if (mMeetAdapter == null) {
//                                    mMeetAdapter = new MeetAdapter(MeetNewActivity.this.getSupportFragmentManager(), String.valueOf(nMeetID));
//                                    meet_viewpage.setAdapter(mMeetAdapter);
//                                }
//                                for (CGetMeetingInfoRsp.UserInfo item : mCurrentGetMeetingInfoRsp.listUser) {
//                                    Log.d("VIMApp", "strUserTokenID = " + item.strUserTokenID);
//                                    Log.d("VIMApp", "strUserID = " + item.strUserID);
//                                    Log.d("VIMApp", "strUserDomainCode = " + item.strUserDomainCode);
//                                }
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                showToast("获取会议信息失败");
                                finish();
                            }
                        });
    }

    @Override
    public void onSdpMessage(SdpMessageBase sdpMessageBase, int i) {
        switch (sdpMessageBase.GetMessageType()) {
            case CNotifyPeerUserMeetingInfo.SelfMessageId:
                CNotifyPeerUserMeetingInfo peerInfo = (CNotifyPeerUserMeetingInfo) sdpMessageBase;
                if (peerInfo.nMeetingID != nMeetID
                        || !peerInfo.strMeetingDomainCode.equals(strMeetDomainCode)) {
                    return;
                }

                if (peerInfo.nIsAgree == 0) {
                    // 对方拒绝
                    onRefuseMeetActivity(peerInfo);
                } else if (peerInfo.nIsAgree == 1) {
                    // 对方同意
                    onAgreeMeetActivity(peerInfo);
                } else if (peerInfo.nIsAgree == 2) {
                    // 对方无人接听
                    onNoResponseActivity(peerInfo);
                } else if (peerInfo.nIsAgree == 3) {
                    // 对方离线
                    onUserOfflineActivity(peerInfo);
                } else if (peerInfo.nIsAgree == 4) {
                    // 重复邀请
                }
                break;
            case CNotifyMeetingRaiseInfo.SelfMessageId:
                CNotifyMeetingRaiseInfo raiseInfo = (CNotifyMeetingRaiseInfo) sdpMessageBase;

                if (raiseInfo.nMeetingID != nMeetID
                        || !raiseInfo.strMeetingDomainCode.equals(strMeetDomainCode)) {
                    return;
                }
                onUserRaiseOfMeetActivity(raiseInfo);
                break;
            case CNotifyUpdateWhiteboard.SelfMessageId:
                CNotifyUpdateWhiteboard info = (CNotifyUpdateWhiteboard) sdpMessageBase;
                if (info.nMeetingID != nMeetID) {
                    return;
                }
//                mMeetBoardFragment.notiyUpdate(info);
                break;
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
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }

    }

    @Override
    public void onMeetInvite(final CNotifyInviteUserJoinMeeting data, final long millis) {
        if (data == null) {
            quitMeet(false);
            return;
        }
        if (data.nMeetingStatus != 1) {
            return;
        }
        if (dialog != null && dialog.isShowing()) {
            HYClient.getModule(ApiMeet.class).joinMeeting(SdkParamsCenter.Meet.JoinMeet()
                    .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                    .setMeetID(data.nMeetingID)
                    .setMeetDomainCode(data.strMeetingDomainCode), null);
            return;
        }
        currentMeetingInvite = data;
        final CNotifyInviteUserJoinMeeting temp = data;

        // 会议中来会议邀请，对话框提示
        dialog = getLogicDialog()
                .setTitleText("邀请")
                .setMessageText(data.strInviteUserName + "邀请你参加会议，是否接受？")
                .setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppMessages.get().del(millis);
                        HYClient.getModule(ApiMeet.class).joinMeeting(SdkParamsCenter.Meet.JoinMeet()
                                .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                                .setMeetID(data.nMeetingID)
                                .setMeetDomainCode(data.strMeetingDomainCode), null);
                    }
                })
                .setConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppMessages.get().del(millis);

                        closeBoardView(false);
                        stopWhiteBoard();
                        endEncrypt();
                        HYClient.getModule(ApiMeet.class)
                                .quitMeeting(SdkParamsCenter.Meet.QuitMeet()
                                        .setQuitMeetType(SdkBaseParams.QuitMeetType.Quit)
                                        .setMeetDomainCode(strMeetDomainCode)
                                        .setStopCapture(HYClient.getSdkOptions().encrypt().isEncryptBind() ? true : false)
                                        .setMeetID(nMeetID), new CallbackQuitMeet() {
                                    @Override
                                    public boolean isContinueOnStopCaptureError(ErrorInfo errorInfo) {
                                        return true;
                                    }

                                    @Override
                                    public void onSuccess(Object o) {
                                        changeCurrentMeet(temp);
                                    }

                                    @Override
                                    public void onError(ErrorInfo errorInfo) {
                                    }
                                });
                        /*if (isWatch) {
                            HYClient.getHYPlayer().stopPlayEx(new SdkCallback<VideoParams>() {
                                @Override
                                public void onSuccess(VideoParams params) {
                                    changeCurrentMeet(temp);
                                }

                                @Override
                                public void onError(ErrorInfo errorInfo) {

                                }
                            }, texture_video);
                        } else {
                            closeBoardView(false);
                            stopWhiteBoard();
                            HYClient.getModule(ApiMeet.class)
                                    .quitMeeting(SdkParamsCenter.Meet.QuitMeet()
                                            .setQuitMeetType(SdkBaseParams.QuitMeetType.Quit)
                                            .setMeetDomainCode(strMeetDomainCode)
                                            .setStopCapture(false)
                                            .setMeetID(nMeetID), new CallbackQuitMeet() {
                                        @Override
                                        public boolean isContinueOnStopCaptureError(ErrorInfo errorInfo) {
                                            return true;
                                        }

                                        @Override
                                        public void onSuccess(Object o) {
                                            changeCurrentMeet(temp);
                                        }

                                        @Override
                                        public void onError(ErrorInfo errorInfo) {
                                        }
                                    });
                        }*/

                    }
                });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                currentMeetingInvite = null;
                currentTalkInvite = null;
            }
        });
        dialog.show();
    }

    @Override
    public void onTalkInvite(final CNotifyUserJoinTalkback data, final long millis) {
        if (data == null) {
            return;
        }

        if (dialog != null && dialog.isShowing()) {
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
        dialog = getLogicDialog()
                .setTitleText("邀请")
                .setMessageText(data.strFromUserName + "邀请您对讲，要切换到对讲嘛？")
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
                })
                .setConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppMessages.get().del(millis);
                        endEncrypt();
                        HYClient.getModule(ApiMeet.class)
                                .quitMeeting(SdkParamsCenter.Meet.QuitMeet()
                                        .setQuitMeetType(SdkBaseParams.QuitMeetType.Quit)
                                        .setMeetDomainCode(strMeetDomainCode)
                                        .setStopCapture(HYClient.getSdkOptions().encrypt().isEncryptBind() ? true : false)
                                        .setMeetID(nMeetID), new CallbackQuitMeet() {
                                    @Override
                                    public boolean isContinueOnStopCaptureError(ErrorInfo errorInfo) {
                                        return true;
                                    }

                                    @Override
                                    public void onSuccess(Object o) {

                                        // 对讲邀请
                                        Intent intent;
                                        if (data.getRequiredMediaMode() == SdkBaseParams.MediaMode.Audio) {
                                            intent = new Intent(MeetNewActivity.this, TalkVoiceActivity.class);
                                        } else {
                                            intent = new Intent(MeetNewActivity.this, TalkActivity.class);
                                        }
                                        intent.putExtra("strInviteName", temp.strFromUserName);
                                        intent.putExtra("strTalkDomainCode", temp.strTalkbackDomainCode);
                                        intent.putExtra("strInviteUserId", temp.strFromUserID);
                                        intent.putExtra("strInviteUserDomain", temp.strFromUserDomainCode);
                                        intent.putExtra("nTalkID", temp.nTalkbackID);
                                        intent.putExtra("millis", millis);
                                        startActivity(intent);
                                        finish();

                                    }

                                    @Override
                                    public void onError(ErrorInfo errorInfo) {
                                        showToast("切换失败");
                                    }
                                });
                    }
                });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                currentMeetingInvite = null;
                currentTalkInvite = null;
            }
        });
        dialog.show();
    }

    /**
     * 关闭白板界面
     *
     * @param isOwner
     */
    private void closeBoardView(boolean isOwner) {
        /*MeetActivity.this.status = null;
        iv_change.setVisibility(View.GONE);
        exitWhiteBoardDeal();
        menu_meet_media.setWhiteBoardTxt("本地共享", true, isOwner);
        if (shareLocalPopupWindow != null) {
            shareLocalPopupWindow.changeStatue("开启白板");
        }*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyMeetingStatusInfo info) {
        if (currentMeetingInvite == null) {
            return;
        }
        if (info.nMeetingID == currentMeetingInvite.nMeetingID
                && info.isMeetFinished() && dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyTalkbackStatusInfo info) {
        if (currentTalkInvite == null) {
            return;
        }
        if (info.nTalkbackID == currentTalkInvite.nTalkbackID
                && info.isTalkingStopped() && dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RaiseMessage msg) {
        if (msg.code == 1) {
            ((SpeakerFragment) mMeetAdapter.
                    getItem(0)).deleteRaiseUser(msg.userId);
        } else {
            ((SpeakerFragment) mMeetAdapter.
                    getItem(0)).clearRaiseUsers();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NewChatMessage msg) {
        if (isResume) {
            Logger.log("收到新的聊天消息");
        }
    }


    /**
     * 改变不同会议
     *
     * @param temp
     */
    private void changeCurrentMeet(CNotifyInviteUserJoinMeeting temp) {
        isMeetStarter = temp.isSelfMeetCreator();
        strMeetDomainCode = temp.strMeetingDomainCode;
        nMeetID = temp.nMeetingID;
        mMediaMode = temp.getRequiredMediaMode();
        strInviteUserId = temp.strInviteUserId;
        strInviteUserDomainCode = temp.strInviteUserDomainCode;

        requestInfo();
    }

    boolean isSuccess;
    int counts = 0;

    private void startJoineMeet() {
        counts = 0;
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            if (isMeetStarter) {
                ArrayList<CGetMeetingInfoRsp.UserInfo> allTemp = new ArrayList<>();
                for (CGetMeetingInfoRsp.UserInfo temp : mCurrentGetMeetingInfoRsp.listUser) {
                    if (!temp.strUserID.equals(AppAuth.get().getUserID())) {
                        counts++;
                        allTemp.add(temp);
                    }
                }
                for (CGetMeetingInfoRsp.UserInfo temp : allTemp) {
                    EncryptUtil.startEncrypt(true, temp.strUserID, temp.strUserDomainCode,
                            nMeetID + "", strMeetDomainCode, new SdkCallback<SdpMessageCmStartSessionRsp>() {
                                @Override
                                public void onSuccess(SdpMessageCmStartSessionRsp sessionRsp) {
                                    counts = 0;
                                    isSuccess = true;
                                    MeetNewActivity.this.sessionRsp = sessionRsp;
                                    joinMeet();
                                }

                                @Override
                                public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                    counts--;
                                    if (isSuccess) {
                                        return;
                                    }
                                    if (isFinishing()) {
                                        return;
                                    }
                                    if (counts == 0) {
                                        showToast("加入会议失败");
                                        finish();
                                    }
                                }
                            });
                }
            } else {
                EncryptUtil.startEncrypt(false, strInviteUserId, strInviteUserDomainCode,
                        nMeetID + "", strMeetDomainCode, new SdkCallback<SdpMessageCmStartSessionRsp>() {
                            @Override
                            public void onSuccess(SdpMessageCmStartSessionRsp sessionRsp) {
                                MeetNewActivity.this.sessionRsp = sessionRsp;
                                joinMeet();
                            }

                            @Override
                            public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                if (isFinishing()) {
                                    return;
                                }
                                showToast("加入会议失败");
                                finish();
                            }
                        });
            }
        } else {
            if (nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                finish();
                return;
            }
            joinMeet();
        }
    }

    /**
     * 加入会议
     */
    public void joinMeet() {
        ParamsJoinMeetMultiPlay pjmmp = SdkParamsCenter.Meet.JoinMeetMultiPlay()
                .setAgreeMode(SdkBaseParams.AgreeMode.Agree)
                .setIsAutoStopCapture(true)
                .setMediaMode(mMediaMode)
                .setMeetID(nMeetID)
                .setCameraIndex(SP.getInteger(AppUtils.STRING_KEY_camera) == 1 ? HYCapture.Camera.Foreground : HYCapture.Camera.Background)
                .setMeetDomainCode(strMeetDomainCode)
                .setStrInviteUserDomainCode(strInviteUserDomainCode)
                .setStrInviteUserTokenID(strInviteUserTokenID)
                .setMeetScreen(SdkBaseParams.MeetScreen.Single)
                .setnIsOnlyAudio(isCloseVideo ? 1 : 0);
//                        .setPlayerPreview(texture_video)
        if (sessionRsp != null) {
            pjmmp.setCallId(sessionRsp.m_nCallId);
        }
        HYClient.getModule(ApiMeet.class).joinMeetingMultiPlay(pjmmp, new CallbackJoinMeet() {
            @Override
            public void onAgreeMeet(CNotifyPeerUserMeetingInfo info) {
                Log.e("SDKMSG", "onAgreeMeet");
                if (isFinishing()) {
                    return;
                }
                        /*multiPlayHelper.userJoin(info.strUserID);
                        showInfo(info.strToUserName + " 同意加入会议");*/
                showToast(info.strToUserName + " 同意加入会议");
                if (mMeetAdapter == null) {
                    return;
                }
//                        ((MeetMemberNewFragment) mMeetAdapter.
//                                getItem(1))
//                                .getMultiPlayHelper()
//                                .userJoin(info.strUserID);

            }

            @Override
            public void onRefuseMeet(CNotifyPeerUserMeetingInfo info) {
                Log.e("SDKMSG", "onRefuseMeet...");
                if (isFinishing()) {
                    return;
                }
//                        showInfo(info.strToUserName + " 拒绝加入会议");
                showToast(info.strToUserName + " 拒绝加入会议");
            }

            @Override
            public void onNoResponse(CNotifyPeerUserMeetingInfo info) {
                Log.e("SDKMSG", "onNoResponse...");
                if (isFinishing()) {
                    return;
                }
//                        showInfo(info.strToUserName + " 无响应");
                showToast(info.strToUserName + " 无响应");
            }

            @Override
            public void onUserOffline(CNotifyPeerUserMeetingInfo info) {
                Log.e("SDKMSG", "onUserOffline...");
                if (isFinishing()) {
                    return;
                }
//                        showInfo(info.strToUserName + " 不在线");
                showToast(info.strToUserName + " 不在线");
            }

            @Override
            public void onRepeatInvitation(CNotifyPeerUserMeetingInfo info) {
                Log.e("SDKMSG", "onRepeatInvitation...");
                if (isFinishing()) {
                    return;
                }
//                        showToast(info.strToUserName + " 重复邀请");
                showToast(info.strToUserName + " 重复邀请");
            }

            @Override
            public void onMeetStatusChanged(CNotifyMeetingStatusInfo info) {
                Log.e("SDKMSG", " onMeetStatusChanged...");
                if (isFinishing() || info == null) {
                    return;
                }
                if (info.isMeetFinished()) {
                    Log.e("VIMApp", " Meet Has Finished...");
                    showToast("会议已结束");
                    finish();
                } else {
//                            showToast("onMeetStatusChanged... ");
                    //获取用户列表,看看有没有人员变化
                    mCurrentGetMeetingInfoRsp = convertUser(info);
//                            isMeetStarter = mCurrentGetMeetingInfoRsp.strMainUserID.equals(AppDatas.Auth().getUserID());
                    refreshFragment(true);
                    Fragment fragment1 = mMeetAdapter.getItem(1);
                    if (fragment1 == null) {
                        return;
                    }
                    MeetMemberNewFragment meetMemberNewFragment = (MeetMemberNewFragment) fragment1;
                    meetMemberNewFragment
                            .refreshUser(mCurrentGetMeetingInfoRsp, MeetMemberNewFragment.convertUser(info.lstMeetingUser));

                    for (CNotifyMeetingStatusInfo.User user : info.lstMeetingUser) {
                        if (user.strUserID.equals(String.valueOf(AppDatas.Auth().getUserID()))) {
                            //判断是否禁言
                            if (user.nMuteStatus == 1 || isCloseVoice) {
                                ((SpeakerFragment) mMeetAdapter.
                                        getItem(0)).closeMic(user.nMuteStatus == 1);
                            } else {
                                ((SpeakerFragment) mMeetAdapter.
                                        getItem(0)).openMic(false);
                            }

                            break;
                        }
                    }
                }
            }

            @Override
            public void onKickedFromMeet(CNotifyKickUserMeeting info) {
                Log.e("SDKMSG", "onKickedFromMeet...");
                if (isFinishing()) {
                    return;
                }
                        /*getInfoDialog().setContent("你被踢出了会议")
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                }).show();*/
                showToast("你被踢出了会议");
                finish();
            }

            @Override
            public void onUserRaiseOfMeet(CNotifyMeetingRaiseInfo info) {

            }

            @Override
            public void onVideoLimiteMeet(CNotifyMeetingPushVideo info) {

            }

            @Override
            public void onCaptureStatusChanged(SdpMessageBase msg) {

            }

            @Override
            public void onMeetFinished() {

            }

            @Override
            public void onReceiverWhiteboardStatus(CNotifyWhiteboardStatus status) {

            }

            @Override
            public void onAfterSuccess(CGetMeetingInfoRsp meetInfo) {
                Log.e("SDKMSG", " onAfterSuccess... " + meetInfo.listUser.size());
                        /*multiPlayHelper.refreshUser(userList);
                        multiPlayHelper.notifyChanged();*/

                mCurrentGetMeetingInfoRsp = meetInfo;
                isMeetStarter = meetInfo.strMainUserID.equals(String.valueOf(AppDatas.Auth().getUserID()));
                refreshFragment(false);


                Fragment fragment1 = mMeetAdapter.getItem(1);
                if (fragment1 == null) {
                    return;
                }
                MeetMemberNewFragment meetMemberNewFragment = (MeetMemberNewFragment) fragment1;
                meetMemberNewFragment
                        .refreshUser(mCurrentGetMeetingInfoRsp, meetInfo.listUser);
                meetMemberNewFragment.setCallId(sessionRsp);


                if (meetInfo.nMuteStatus == 1 || isCloseVoice) {
                    ((SpeakerFragment) mMeetAdapter.
                            getItem(0)).closeMic(meetInfo.nMuteStatus == 1);
                } else {
                    ((SpeakerFragment) mMeetAdapter.
                            getItem(0)).openMic(true);
                }
            }

            @Override
            public void onSuccess(CJoinMeetingRsp resp) {
                Log.e("SDKMSG", " onSuccess...");
//                        showInfo("进入会议成功...");
                showToast("成功进入会议");
                refreshFragment(false);
                ((SpeakerFragment) mMeetAdapter.getItem(0)).startTime();
                if (isCloseVideo) {
                    ((SpeakerFragment) mMeetAdapter.
                            getItem(0)).closeVideo();
                } else {
                    ((SpeakerFragment) mMeetAdapter.
                            getItem(0)).openVideo();
                }
                MessageEvent nMessageEvent = new MessageEvent(AppUtils.EVENT_MESSAGE_CHANGE_VIDEO_STATE);
                nMessageEvent.obj1 = isCloseVideo;
                EventBus.getDefault().post(nMessageEvent);

                        /*view_media_menu.updateStatus();
                        HYClient.getHYCapture().setCaptureVideoOn(AppSettings.get().getMeetSettings().isMeetVideoShow());
                        HYClient.getHYCapture().setCaptureAudioOn(AppSettings.get().getMeetSettings().isMeetVoiceShow());*/

            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                Log.e("SDKMSG", "onError...>>> " + errorInfo.toString());
//                        isInMeeting = false;
                if (isFinishing()) {
                    return;
                }
                        /*getErrorDialog().setContent("加入会议失败 " + errorInfo.toString())
                                .setFinishClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                }).show();*/
                showToast("加入会议失败");
                finish();
            }

        });
    }

    private void refreshFragment(boolean isRef) {
        try {
            if (mMeetAdapter == null) {
                mMeetAdapter = new MeetAdapter(MeetNewActivity.this.getSupportFragmentManager(), String.valueOf(nMeetID));
                meet_viewpage.setAdapter(mMeetAdapter);
            }
            if (isRef) {
                ((SpeakerFragment) mMeetAdapter.
                        getItem(0)).
                        setMeetingInfo(mCurrentGetMeetingInfoRsp, getMeetingUserList());
            }
        } catch (Exception e) {

        }
    }

    private ArrayList<CGetMeetingInfoRsp.UserInfo> getMeetingUserList() {
        if (mCurrentGetMeetingInfoRsp == null || mCurrentGetMeetingInfoRsp.listUser == null) {
            return null;
        }
        ArrayList<CGetMeetingInfoRsp.UserInfo> list = new ArrayList<CGetMeetingInfoRsp.UserInfo>();
        for (CGetMeetingInfoRsp.UserInfo user : mCurrentGetMeetingInfoRsp.listUser) {
            if (user.nJoinStatus == 2) {
                list.add(user);
            }
        }
        return list;
    }


    /**
     * 退出会议
     *
     * @param isFinish
     */
    public void quitMeet(boolean isFinish) {
//        isClickClose = true;

        /*if (isWatch) {
            quitWatch();
        }*/
        stopWhiteBoard();
        endEncrypt();
        HYClient.getModule(ApiMeet.class)
                .quitMeeting(SdkParamsCenter.Meet.QuitMeet()
                        .setQuitMeetType(isFinish ? SdkBaseParams.QuitMeetType.Finish : SdkBaseParams.QuitMeetType.Quit)
                        .setMeetDomainCode(strMeetDomainCode)
                        .setStopCapture(true)
                        .setMeetID(nMeetID), null);


        finish();
    }

    private void endEncrypt() {
        if (sessionRsp != null) {
            EncryptUtil.endEncrypt(sessionRsp.m_nCallId);
        }
    }

    /**
     * 关闭白板
     */
    public void stopWhiteBoard() {
        exitWhiteBoardDeal();
        HYClient.getModule(ApiMeet.class).stopWhiteBoard(SdkParamsCenter.Meet.StopWhiteBoard().setnMeetingID(nMeetID),
                new SdkCallback<CStopWhiteboardShareRsp>() {
                    @Override
                    public void onSuccess(CStopWhiteboardShareRsp cStopWhiteboardShareRsp) {
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                    }
                });
    }

    private void onRefuseMeetActivity(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
        if (isMeetStarter) {
            showToast(cNotifyPeerUserMeetingInfo.strToUserName + " 拒绝入会");
        }
    }

    private void onUserOfflineActivity(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
        if (isMeetStarter) {
//                            showToast(cNotifyPeerUserMeetingInfo.strToUserName + " 不在线");
        }
    }

    private void onNoResponseActivity(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
        if (isMeetStarter) {
            showToast(cNotifyPeerUserMeetingInfo.strToUserName + " 不响应");
        }
    }

    private void onAgreeMeetActivity(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
        if (cNotifyPeerUserMeetingInfo.strUserID.equals(HYClient.getSdkOptions().User().getUserId())
                && cNotifyPeerUserMeetingInfo.strToUserDomainCode.equals(HYClient.getSdkOptions().User().getDomainCode())) {
        } else {
            showToast(cNotifyPeerUserMeetingInfo.strToUserName + " 同意入会");
            /*if (mMeetMembersFragment != null)
                mMeetMembersFragment.refUser();*/
        }
    }

    private void exitWhiteBoardDeal() {
        /*if (mMeetBoardFragment != null) {
            mMeetBoardFragment.exiteWhiteBoard();
        }*/
    }

    private void onUserRaiseOfMeetActivity(CNotifyMeetingRaiseInfo cNotifyMeetingRaiseInfo) {
        showToast(cNotifyMeetingRaiseInfo.strUserName + " 举手了 ");
        ((SpeakerFragment) mMeetAdapter.
                getItem(0)).addRaiseUser(cNotifyMeetingRaiseInfo.strUserID);
        /*if (mMeetMembersFragment == null) return;

        mMeetMembersFragment.changeDataHandUp(cNotifyMeetingRaiseInfo);
        if (!mMeetMembersFragment.isVisible()) {
            menu_meet_media.showHandUpRed(true);
        }*/
    }

    /**
     * 清除人员
     */
    private void clearContacts() {
        ChoosedContactsNew.get().clear();
    }

    private void destruct() {
        if (mSdpUITask != null) {
            mSdpUITask.exit();
            mSdpUITask = null;
        }
    }

    public boolean isCloseVideo() {
        return isCloseVideo;
    }

    public void setUserCloseVideo(boolean isUserCloseVideo) {
        isCloseVideo = isUserCloseVideo;
    }


    public void setUserCloseVoice(boolean isUserClose) {
        isCloseVoice = isUserClose;
    }

    public boolean isMeetStarter() {
        return isMeetStarter;
    }

    public boolean isWatch() {
        return isWatch;
    }

    public boolean closeInvisitor() {
        return closeInvisitor;
    }

    public CGetMeetingInfoRsp convertUser(CNotifyMeetingStatusInfo info) {
        if (info == null) {
            return null;
        }
        if (mCurrentGetMeetingInfoRsp == null) {
            mCurrentGetMeetingInfoRsp = new CGetMeetingInfoRsp();
        }
        mCurrentGetMeetingInfoRsp.strKeynoteSpeakerTokenID = info.strKeynoteSpeakerTokenID;
        mCurrentGetMeetingInfoRsp.strKeynoteSpeakerDomainCode = info.strKeynoteSpeakerDomainCode;
        mCurrentGetMeetingInfoRsp.strKeynoteSpeakerUserID = info.strKeynoteSpeakerUserID;
        mCurrentGetMeetingInfoRsp.nStatus = info.nMeetingStatus;

        ArrayList<CGetMeetingInfoRsp.UserInfo> newUserList = new ArrayList<>(info.lstMeetingUser.size());
        for (int i = 0; i < info.lstMeetingUser.size(); i++) {
            CGetMeetingInfoRsp.UserInfo newUser = new CGetMeetingInfoRsp.UserInfo();
            CNotifyMeetingStatusInfo.User oldUser = info.lstMeetingUser.get(i);
            newUser.strUserID = oldUser.strUserID;
            newUser.strUserDomainCode = oldUser.strUserDomainCode;
            newUser.strUserName = oldUser.strUserName;
            newUser.strUserTokenID = oldUser.strUserTokenID;
            newUser.nDevType = oldUser.nDevType;
            newUser.nMicStatus = oldUser.nMicStatus;
            newUser.nJoinStatus = oldUser.nPartType == CNotifyMeetingStatusInfo.User.QUIT_MEET ? 0 : 2;
            newUser.nUserRole = oldUser.nUserRole;
            newUserList.add(newUser);
        }
        mCurrentGetMeetingInfoRsp.listUser = newUserList;
        return mCurrentGetMeetingInfoRsp;
    }

    /**
     * 邀请返回
     */
    void onInviteClicked() {
        count = 0;
        cuntCount = 0;
        cuntCountSuccess = 0;
        ArrayList<CStartMeetingReq.UserInfo> users = ConvertContacts.ConvertContactsToMeetUserInfo(ChoosedContactsNew.get().getContacts());

        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            for (CStartMeetingReq.UserInfo temp : users) {
                if (!HYClient.getSdkOptions().User().getUserId().equals(temp.strUserID)) {
                    EncryptUtil.startEncrypt(true, temp.strUserID, temp.strUserDomainCode,
                            nMeetID + "", strMeetDomainCode, new SdkCallback<SdpMessageCmStartSessionRsp>() {
                                @Override
                                public void onSuccess(SdpMessageCmStartSessionRsp sessionRsp) {
                                    count++;
                                    ArrayList<CStartMeetingReq.UserInfo> usersReal = new ArrayList<>();
                                    usersReal.add(temp);
                                    realInvisitor(usersReal);
                                }

                                @Override
                                public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                }
                            });
                }
            }
        } else {
            if (nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                finish();
                return;
            }
            realInvisitor(ConvertContacts.ConvertContactsToMeetUserInfo(ChoosedContactsNew.get().getContacts()));
        }
    }

    private void realInvisitor(ArrayList<CStartMeetingReq.UserInfo> users) {
        HYClient.getModule(ApiMeet.class).inviteUser(SdkParamsCenter.Meet.InviteMeet()
                        .setMeetDomainCode(strMeetDomainCode)
                        .setMeetID(nMeetID)
                        .setUsers(users),
                new SdkCallback<CInviteUserMeetingRsp>() {
                    @Override
                    public void onSuccess(CInviteUserMeetingRsp cInviteUserMeetingRsp) {
                        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                            cuntCount++;
                            cuntCountSuccess++;
                            if (count == cuntCount) {
                                showToast("已邀请选中人员");
                            }
                        } else {
                            if (nEncryptIMEnable) {
                                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                finish();
                                return;
                            }
                            showToast("已邀请选中人员");
                        }
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                            cuntCount++;
                            if (count == cuntCount && cuntCountSuccess > 0) {
                                showToast("已邀请选中人员");
                            } else {
                                showToast(ErrorMsg.getMsg(ErrorMsg.invite_user_err_code));
                            }
                        } else {
                            if (nEncryptIMEnable) {
                                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                finish();
                                return;
                            }
                            showToast(ErrorMsg.getMsg(ErrorMsg.invite_user_err_code));
                        }
                    }
                });
        clearContacts();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                onInviteClicked();
            }
        }
    }

    @Override
    public void onBackPressed() {
        /*if (mMeetMembersFragment.isVisible() ||
                mMeetLayoutFragment.isVisible() ||
                mMeetBoardFragment.isVisible()) {
            hideAll();
            return;
        }*/
        final LogicDialog dialog = getLogicDialog().setMessageText(isMeetStarter ? "解散或退出会议?" : "是否退出本次会议?");
        if (isMeetStarter) {
            dialog.setConfirmText("退出");
            dialog.setCancelText("解散");
        } else {
            dialog.setConfirmText("退出");
            dialog.setCancelText("取消");
        }
        dialog.setConfirmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitMeet(false);
            }
        }).setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMeetStarter) {
                    quitMeet(isMeetStarter);
                } else {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
        }).show();
    }

}
