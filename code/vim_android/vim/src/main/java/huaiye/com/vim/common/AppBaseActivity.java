package huaiye.com.vim.common;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaiye.cmf.audiomanager.AppAudioManager;
import com.huaiye.cmf.sdp.SdpMsgFRAlarmNotify;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiAuth;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi.common.PushService;
import com.huaiye.sdk.sdpmsgs.auth.CNotifyUserKickout;
import com.huaiye.sdk.sdpmsgs.auth.CSetKeepAliveIntervalRsp;
import com.huaiye.sdk.sdpmsgs.auth.CUserRegisterRsp;
import com.huaiye.sdk.sdpmsgs.face.CServerNotifyAlarmInfo;
import com.huaiye.sdk.sdpmsgs.meet.CGetMbeConfigParaRsp;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserCancelJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;
import com.huaiye.sdk.sdpmsgs.social.CQueryUserListRsp;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;
import com.ttyy.commonanno.Finder;
import com.ttyy.commonanno.Injectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import huaiye.com.vim.R;
import huaiye.com.vim.common.dialog.LogicDialog;
import huaiye.com.vim.common.dialog.ZeusLoadView;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.utils.StatusbarUtil;
import huaiye.com.vim.common.views.NavigateView;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.msgs.ChatMessageBean;
import huaiye.com.vim.push.MessageObserver;
import huaiye.com.vim.push.MessageReceiver;
import huaiye.com.vim.services.HomeWatcherReceiver;
import huaiye.com.vim.ui.auth.StartActivity;
import huaiye.com.vim.ui.guide.GuideActivity;
import huaiye.com.vim.ui.guide.WelcomeActivity;
import huaiye.com.vim.ui.home.MainActivity;
import huaiye.com.vim.ui.meet.InviteAlarmActivity;
import huaiye.com.vim.ui.meet.MeetCreateByAllFriendActivity;
import huaiye.com.vim.ui.meet.MeetCreateOrderActivity;
import huaiye.com.vim.ui.meet.MeetDetailActivity;
import huaiye.com.vim.ui.meet.MeetNewActivity;
import huaiye.com.vim.ui.meet.NickJoinMeetActivity;
import huaiye.com.vim.ui.meet.OrderMeetDetailActivity;
import huaiye.com.vim.ui.meet.VideoRecordUploadActivity;

import static huaiye.com.vim.common.AppUtils.XIAOMI;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: AppBaseActivity
 */

public abstract class AppBaseActivity extends FragmentActivity implements MessageObserver {
    public final static String TAG = "AppBaseActivity";

    protected NavigateView navigate;
    protected boolean isClose;
    protected static Snackbar snackbar;
    protected LogicDialog mLogicDialog;
    public ZeusLoadView mZeusLoadView;
    private ExitAppReceiver mExitAppReceiver;
    protected Map<String, CQueryUserListRsp.UserInfo> map = new HashMap<>();
    protected int MbeConfigParaValue = -1;
    private AppAudioManager audio;

    protected boolean isResume;

    protected boolean isNeedSecureSnap = false;

    private HomeWatcherReceiver mHomeKeyReceiver = null;

    protected void registerHomeKeyReceiver(Context context) {
        mHomeKeyReceiver = new HomeWatcherReceiver();
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        context.registerReceiver(mHomeKeyReceiver, homeFilter);
    }

    protected void unregisterHomeKeyReceiver(Context context) {
        if (null != mHomeKeyReceiver) {
            context.unregisterReceiver(mHomeKeyReceiver);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (!isNeedSecureSnap) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        super.onCreate(savedInstanceState);
        Logger.info("open_activity " + this.getClass().getSimpleName());
        setStatusbarAndTitleColor();
        // Content 初始化
        LinearLayout contentView = new LinearLayout(this);
        contentView.setOrientation(LinearLayout.VERTICAL);

        FrameLayout userContent = new FrameLayout(this);
        Injectors.get().inject(Finder.View, userContent, this);

        navigate = new NavigateView(this);

        contentView.addView(navigate, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        contentView.addView(userContent, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        setContentView(contentView);

        initToast();

        mZeusLoadView = new ZeusLoadView(this);
        mZeusLoadView.setCancelable(true);

        MessageReceiver.get().subscribe(this);

        mLogicDialog = new LogicDialog(this);

        if (this instanceof MeetCreateByAllFriendActivity
                || this instanceof OrderMeetDetailActivity
                || this instanceof MeetCreateOrderActivity) {
            getMeetConfig();
        }

        mExitAppReceiver = new ExitAppReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.huaiye.mc.exitapp");
        registerReceiver(mExitAppReceiver, intentFilter);

        initActionBar();
        init();
    }

    private void init() {
        new RxUtils().doDelay(10, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                doInitDelay();
            }
        }, "init");
    }

    private void initToast() {
        try {
            snackbar = Snackbar.make(navigate, "", Snackbar.LENGTH_SHORT);
            View view_snack = snackbar.getView();
            TextView tv = view_snack.findViewById(android.support.design.R.id.snackbar_text);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else {
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        } catch (Exception e) {

        }
    }

    protected abstract void initActionBar();

    public abstract void doInitDelay();

    @Override
    protected void onResume() {
        super.onResume();
        initToast();
        isResume = true;
        if (this.getClass().getSimpleName().equals("MeetActivity")) {
            HYClient.getHYAudioMgr().from(getSelf()).setSpeakerphoneOn(true);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 后台接受消息
        if (AppBaseActivity.this instanceof StartActivity ||
                AppBaseActivity.this instanceof WelcomeActivity ||
                AppBaseActivity.this instanceof GuideActivity ||
                AppBaseActivity.this instanceof NickJoinMeetActivity) {
            return;
        }
        PushService.actionStop(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (AppBaseActivity.this instanceof StartActivity ||
                AppBaseActivity.this instanceof WelcomeActivity ||
                AppBaseActivity.this instanceof GuideActivity ||
                AppBaseActivity.this instanceof NickJoinMeetActivity) {
            return;
        }
        try {
            PushService.actionStart(this, new PushService.IKeepLiveListener() {
                @Override
                public void onReceiverMsg(CSetKeepAliveIntervalRsp cSetKeepAliveIntervalRsp, boolean b) {

                    if (isClose && AppBaseActivity.this instanceof MainActivity) {
                        return;
                    }
                    if (cSetKeepAliveIntervalRsp.nResultCode == 1720200007) {
                        needLoad(AppUtils.getString(R.string.string_name_without_network));
                    }
                }
            });
        } catch (Exception e) {

        }

    }

    @SuppressLint("NewApi")
    @Override
    protected void onDestroy() {
        MessageReceiver.get().unSubscribe(this);

        unregisterReceiver(mExitAppReceiver);
        PushService.actionStop(this);
        super.onDestroy();
    }

    public static void showToast(String text) {
        if (snackbar == null) {
            return;
        }
        if (!AppUtils.isHide) {
            snackbar.setText(text)
                    .show();
        }
    }

    public LogicDialog getLogicDialog() {
        return mLogicDialog;
    }

    public NavigateView getNavigate() {
        return navigate;
    }

    public AppBaseActivity getSelf() {
        return this;
    }

    @Override
    public void onKickedOut(CNotifyUserKickout data) {

    }

    @Override
    public void onNetworkStatusChanged(SdkBaseParams.ConnectionStatus data, CQueryUserListRsp.UserInfo usrInfo) {
        if (this.getClass().getSimpleName().equals("LoginActivity") || this.getClass().getSimpleName().equals("StartActivity")) {
            return;
        }
        switch (data) {
            case Connected:
                if (usrInfo != null && !usrInfo.isUserOnline()) {
                    HYClient.getModule(ApiAuth.class)
                            .login(SdkParamsCenter.Auth.Login()
                                    .setAddress(AppDatas.Constants().getAddressIP(), AppDatas.Constants().getSiePort())
                                    .setUserId(String.valueOf(AppDatas.Auth().getUserID()))
                                    .setUserName(AppDatas.Auth().getUserName()), new SdkCallback<CUserRegisterRsp>() {
                                @Override
                                public void onSuccess(CUserRegisterRsp cUserRegisterRsp) {
                                }

                                @Override
                                public void onError(final ErrorInfo errorInfo) {
                                    needLoad(AppUtils.getString(R.string.string_name_without_network));
                                }
                            });
                }
                break;
            case Disconnected:
                needLoad(AppUtils.getString(R.string.string_name_without_network));
                break;
            case Connecting:
                if (!AppUtils.isHide)
                    showToast(AppUtils.getString(R.string.string_name_relogin));
                break;
        }
    }

    public void needLoad(String tip) {
        final LogicDialog logicDialog = new LogicDialog(AppBaseActivity.this);
        logicDialog.setCancelable(false);
        logicDialog.setCanceledOnTouchOutside(false);
        logicDialog.setMessageText(tip);
        logicDialog.setCancelButtonVisibility(View.GONE);
        logicDialog.setConfirmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToLogin();
            }
        }).show();
    }

    public void jumpToLogin() {
        HYClient.getHYCapture().stopCapture(null);
        Intent intent = new Intent(AppBaseActivity.this, StartActivity.class);
        intent.putExtra("from", "");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onTalkInvite(CNotifyUserJoinTalkback data, long millis) {
        if (data == null) {
            return;
        }
        Intent intent = new Intent(this, InviteAlarmActivity.class);
        intent.putExtra("strInviteName", data.strFromUserName);
        intent.putExtra("mExpectedMediaMode", data.getRequiredMediaMode());
        intent.putExtra("nTalkID", data.nTalkbackID);
        intent.putExtra("millis", millis);
        intent.putExtra("strTalkDomainCode", data.strTalkbackDomainCode);
        intent.putExtra("requiredMediaMode", data.getRequiredMediaMode().value());
        intent.putExtra("strInviteUserId", data.strFromUserID);
        intent.putExtra("strInviteUserDomain", data.strFromUserDomainCode);
        intent.putExtra("isCloseVideo", data.nVoiceIntercom == 1 ? true : false);

        startActivity(intent);
    }

    @Override
    public void onVideoPushInvite(ChatMessageBean chatMessageBean) {
        if (chatMessageBean == null) {
            return;
        }
        Intent intent = new Intent(this, InviteAlarmActivity.class);
        intent.putExtra("chatMessageBean", chatMessageBean);
        startActivity(intent);
    }

    @Override
    public void onMeetInvite(CNotifyInviteUserJoinMeeting data, long millis) {
        if (data == null) {
            return;
        }
        if (data.nMeetingStatus != 1) {
            return;
        }
        if (data.isSelfMeetCreator()) {
            // 自己创建的会议，直接进入
//            Intent intent = new Intent(this, MeetActivity.class);
            Intent intent = new Intent(this, MeetNewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("isMeetStarter", true);
            intent.putExtra("strMeetDomainCode", data.strMeetingDomainCode);
            intent.putExtra("nMeetID", data.nMeetingID);
            intent.putExtra("millis", millis);
            intent.putExtra("mMediaMode", data.getRequiredMediaMode());
            intent.putExtra("strInviteUserDomainCode", data.strInviteUserDomainCode);
            intent.putExtra("strInviteUserTokenID", data.strInviteUserTokenID);
            intent.putExtra("strInviteUserId", data.strInviteUserId);
            intent.putExtra("isCloseVideo", data.nVoiceIntercom == 1 ? true : false);
            startActivity(intent);
            return;
        }

        Intent intent = new Intent(this, InviteAlarmActivity.class);
        intent.putExtra("strInviteName", data.strInviteUserName);
        intent.putExtra("mExpectedMediaMode", data.getRequiredMediaMode());
        intent.putExtra("nMeetID", data.nMeetingID);
        intent.putExtra("strMeetingName", data.strMeetingName);
        intent.putExtra("millis", millis);
        intent.putExtra("strMeetDomainCode", data.strMeetingDomainCode);
        intent.putExtra("strInviteUserDomainCode", data.strInviteUserDomainCode);
        intent.putExtra("strInviteUserTokenID", data.strInviteUserTokenID);
        intent.putExtra("strInviteUserId", data.strInviteUserId);
        intent.putExtra("strInviteUserDomain", data.strInviteUserDomainCode);
        intent.putExtra("isCloseVideo", data.nVoiceIntercom == 1 ? true : false);
        startActivity(intent);
    }

    @Override
    public void onMeetInviteCancel(CNotifyInviteUserCancelJoinMeeting data, long millis) {
        if (data == null) {
            return;
        }
        if (data.nMeetingID != 1) {
            return;
        }

    }


    public void requestOnLine(final boolean value) {
        HYClient.getModule(ApiSocial.class)
                .getUsers(
                        SdkParamsCenter.Social.GetUsers().setDomainCode(HYClient.getSdkOptions().User().getDomainCode())
                        , new SdkCallback<ArrayList<CQueryUserListRsp.UserInfo>>() {
                            @Override
                            public void onSuccess(ArrayList<CQueryUserListRsp.UserInfo> userInfos) {
                                map.clear();
                                for (CQueryUserListRsp.UserInfo user : userInfos) {
                                    map.put(user.strUserID, user);
                                }

                                afterOnLineUser(value);
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                            }
                        });
    }

    protected void afterOnLineUser(boolean value) {

    }

    private void getMeetConfig() {
        HYClient.getModule(ApiMeet.class)
                .getMeetConfigureInfo(SdkParamsCenter.Meet.GetMeetConfig(),
                        new SdkCallback<CGetMbeConfigParaRsp>() {
                            @Override
                            public void onSuccess(CGetMbeConfigParaRsp cGetMbeConfigParaRsp) {
                                MbeConfigParaValue = Integer.parseInt(cGetMbeConfigParaRsp.lstMbeConfigParaInfo.get(0).strMbeConfigParaValue);
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {

                            }
                        });
    }

    @Override
    public void onServerFaceAlarm(CServerNotifyAlarmInfo data) {

    }

    @Override
    public void onLocalFaceAlarm(SdpMsgFRAlarmNotify data) {

    }

    protected void startSpeakerLound() {
        if (!Build.MODEL.equalsIgnoreCase(XIAOMI) && audio == null) {
            Logger.log("HYClient AppAudioManager INIT START............");
            audio = AppAudioManager.create(AppUtils.ctx, true);
            audio.start(new AppAudioManager.AudioManagerEvents() {
                @Override
                public void onAudioDeviceChanged(AppAudioManager.AudioDevice audioDevice, Set<AppAudioManager.AudioDevice> set) {
                }
            });

        }
    }

    protected void startSpeakerSmall() {
        if (!Build.MODEL.equalsIgnoreCase(XIAOMI) && audio == null) {
            Logger.log("HYClient AppAudioManager INIT START............");
            audio = AppAudioManager.create(AppUtils.ctx, false);
            audio.start(new AppAudioManager.AudioManagerEvents() {
                @Override
                public void onAudioDeviceChanged(AppAudioManager.AudioDevice audioDevice, Set<AppAudioManager.AudioDevice> set) {
                }
            });

        }
    }

    protected void stopSpeakerLound() {
        if (audio != null) {
            audio.stop();
            audio = null;
        }
    }

    /**
     * 退出应用广播Receiver
     */
    class ExitAppReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.huaiye.mc.exitapp")) {
                finish();
            }
        }
    }

    private void setStatusbarAndTitleColor() {
        if (this instanceof MeetDetailActivity
                || this instanceof WelcomeActivity
                || this instanceof StartActivity
                || this instanceof VideoRecordUploadActivity) {
            StatusbarUtil.transparencyBar(this);

        } else {
            if (this instanceof MainActivity) {
                StatusbarUtil.transparencyDisplayModeBar(this, true);
            }
            StatusbarUtil.StatusBarByColor(this, R.color.color_30313C);
        }
    }
}
