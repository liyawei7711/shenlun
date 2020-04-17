package huaiye.com.vim.ui.meet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._api.ApiTalk;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserCancelJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingStatusInfo;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyTalkbackStatusInfo;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AlarmMediaPlayer;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.rx.CommonSubscriber;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.dao.msgs.AppMessages;
import huaiye.com.vim.dao.msgs.ChatMessageBean;
import huaiye.com.vim.ui.talk.TalkActivity;
import huaiye.com.vim.ui.talk.TalkVoiceActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * author: admin
 * date: 2017/12/29
 * version: 0
 * mail: secret
 * desc: InviteAlarmActivity
 */
@BindLayout(R.layout.activity_invitealarm)
public class InviteAlarmActivity extends AppBaseActivity {

    @BindView(R.id.tv_username)
    TextView tv_username;
    @BindView(R.id.tv_description)
    TextView tv_description;

    @BindExtra
    String strInviteName;
    @BindExtra
    long millis;
    @BindExtra
    String strMeetingName;
    @BindExtra
    SdkBaseParams.MediaMode mExpectedMediaMode;

    // ================== 会议邀请 ======================
    @BindExtra
    int nMeetID;
    @BindExtra
    String strMeetDomainCode;
    @BindExtra
    String strInviteUserDomainCode;
    @BindExtra
    String strInviteUserTokenID;

    // ================== 对讲邀请 ======================
    @BindExtra
    int nTalkID;
    @BindExtra
    String strTalkDomainCode;
    @BindExtra
    int requiredMediaMode;
    @BindExtra
    String strInviteUserId;
    @BindExtra
    String strInviteUserDomain;

    @BindExtra
    ChatMessageBean chatMessageBean;

    @BindExtra
    boolean isCloseVideo;

    Boolean isMaster;

    ArrayList<CNotifyInviteUserJoinMeeting> currentData = new ArrayList<>();
    ArrayList<CNotifyUserJoinTalkback> currentTalkData = new ArrayList<>();
    RxUtils rxUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initActionBar() {
        rxUtils = new RxUtils();
    }

    @Override
    public void doInitDelay() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getNavigate().setVisibility(View.GONE);
        EventBus.getDefault().register(this);

        playAlarmMP3();

        rxUtils.doDelay(30 * 1000, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                finish();
            }
        }, "time_out");

        tv_username.setText(strInviteName);
        if (null != chatMessageBean) {
            tv_description.setText("邀请你查看视频");
        } else if (strMeetingName == null) {
            if (requiredMediaMode == SdkBaseParams.MediaMode.Audio.value()) {
                tv_description.setText("邀请你语音通话");

            } else {
                tv_description.setText("邀请你视频通话");
            }
        } else {
            requestDatas();
        }

    }

    @OnClick({R.id.view_ignore, R.id.view_accept})
    void onViewClicked(View v) {
        AppMessages.get().del(millis);

        stopAlarmMP3();
        switch (v.getId()) {
            case R.id.view_ignore:
                // 拒绝邀请
                if (null == chatMessageBean) {
                    reject();
                }
                finish();
                break;
            case R.id.view_accept:
                // 接受邀请
                accept();
                finish();
                break;
        }
    }

    /**
     * 拒绝
     */
    void reject() {
        if (!TextUtils.isEmpty(strMeetDomainCode)) {
            // 会议邀请
            HYClient.getModule(ApiMeet.class).joinMeeting(SdkParamsCenter.Meet.JoinMeet()
                    .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                    .setMeetID(nMeetID)
                    .setMeetDomainCode(strMeetDomainCode), null);
        } else {
            // 对讲邀请
            HYClient.getModule(ApiTalk.class).joinTalking(SdkParamsCenter.Talk.JoinTalk()
                    .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                    .setTalkId(nTalkID)
                    .setTalkDomainCode(strTalkDomainCode), null);
        }
    }

    /**
     * 接受
     */
    void accept() {
        if (null != chatMessageBean) {
            Intent intent = new Intent(this, VideoPushLookActivity.class);
            intent.putExtra("chatMessageBean", chatMessageBean);
            startActivity(intent);
        } else if (!TextUtils.isEmpty(strMeetDomainCode)) {
            // 会议邀请
            Intent intent = new Intent(this, MeetNewActivity.class);
            intent.putExtra("strMeetDomainCode", strMeetDomainCode);
            intent.putExtra("nMeetID", nMeetID);
            intent.putExtra("mMediaMode", mExpectedMediaMode);
            if (!TextUtils.isEmpty(strInviteUserDomainCode)) {
                intent.putExtra("strInviteUserDomainCode", strInviteUserDomainCode);
            }
            if (!TextUtils.isEmpty(strInviteUserTokenID)) {
                intent.putExtra("strInviteUserTokenID", strInviteUserTokenID);
            }
            intent.putExtra("strInviteUserId", strInviteUserId);
            intent.putExtra("isCloseVideo", isCloseVideo);
            intent.putExtra("isMeetStarter", isMaster);
            startActivity(intent);
        } else {
            // 对讲邀请
            Intent intent;
            if (requiredMediaMode == SdkBaseParams.MediaMode.Audio.value()) {
                intent = new Intent(this, TalkVoiceActivity.class);
            } else {
                intent = new Intent(this, TalkActivity.class);
            }
            intent.putExtra("strTalkDomainCode", strTalkDomainCode);
            intent.putExtra("nTalkID", nTalkID);
            intent.putExtra("strInviteName", strInviteName);
            intent.putExtra("strInviteUserId", strInviteUserId);
            intent.putExtra("strInviteUserDomain", strInviteUserDomain);

            startActivity(intent);
        }
    }

    void requestDatas() {
        HYClient.getModule(ApiMeet.class).requestMeetDetail(
                SdkParamsCenter.Meet.RequestMeetDetail()
                        .setMeetDomainCode(strMeetDomainCode)
                        .setnListMode(1)
                        .setMeetID(nMeetID), new SdkCallback<CGetMeetingInfoRsp>() {
                    @Override
                    public void onSuccess(CGetMeetingInfoRsp cGetMeetingInfoRsp) {
                        tv_description.setText("邀请你参加" + strMeetingName + ",邀请(" + (cGetMeetingInfoRsp.listUser.size() - 1) + ")人");
                        if (cGetMeetingInfoRsp.strMainUserID.equals(HYClient.getSdkOptions().User().getUserId())) {
                            isMaster = true;
                        } else {
                            isMaster = false;
                        }
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast(ErrorMsg.getMsg(ErrorMsg.get_meet_info_err_code));
                    }
                });
    }

    @Override
    public void onTalkInvite(CNotifyUserJoinTalkback data, long millis) {
//        super.onTalkInvite(data);
        if (data == null) {
            return;
        }

        AppMessages.get().del(millis);
        currentTalkData.add(data);
        stopTalk();
    }

    private void stopTalk() {
        for (CNotifyUserJoinTalkback temp : currentTalkData) {
            HYClient.getModule(ApiTalk.class).joinTalking(SdkParamsCenter.Talk.JoinTalk()
                    .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                    .setTalkId(temp.nTalkbackID)
                    .setTalkDomainCode(temp.strTalkbackDomainCode), null);
        }
        currentTalkData.clear();

    }
    public void onMeetInviteCancel(CNotifyInviteUserCancelJoinMeeting data, long millis) {
        if(data == null) {
            return;
        }

        for(CNotifyInviteUserJoinMeeting temp : currentData) {
            if(temp.nMeetingID == data.nMeetingID && temp.strMeetingDomainCode.equals(data.strMeetingDomainCode)) {
                currentData.remove(temp);
                finish();
                return;
            }
        }

        if(nMeetID == data.nMeetingID && strMeetDomainCode.equals(data.strMeetingDomainCode)) {
            finish();
        }
    }
    @Override
    public void onMeetInvite(final CNotifyInviteUserJoinMeeting data, final long millisLocal) {
        if (data == null) {
            return;
        }

        Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CommonSubscriber<Long>() {
                    @Override
                    public void onNext(Long o) {
                        super.onNext(o);
                        //不删除,让用户在消息里可以看到这次消息
//                        AppMessages.get().del(millisLocal);
                        currentData.add(data);
                        stopMeet();
                    }
                });
    }

    private void stopMeet() {
        for (CNotifyInviteUserJoinMeeting temp : currentData) {
            HYClient.getModule(ApiMeet.class)
                    .joinMeeting(SdkParamsCenter.Meet.JoinMeet()
                            .setAgreeMode(SdkBaseParams.AgreeMode.Refuse)
                            .setMeetID(temp.nMeetingID)
                            .setMeetDomainCode(temp.strMeetingDomainCode), null);
        }
        currentData.clear();
    }

    void playAlarmMP3() {
        AlarmMediaPlayer.get().playAlarm();

    }

    void stopAlarmMP3() {
        AlarmMediaPlayer.get().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rxUtils.clearAll();
        EventBus.getDefault().unregister(this);
        stopAlarmMP3();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyMeetingStatusInfo cNotifyMeetingStatusInfo) {
        if (nMeetID == cNotifyMeetingStatusInfo.nMeetingID &&
                cNotifyMeetingStatusInfo.isMeetFinished()) {
            showToast("会议已结束");
            stopAlarmMP3();
            finish();
            HYClient.getModule(ApiMeet.class).observeMeetingStatus(null);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyTalkbackStatusInfo cNotifyTalkbackStatusInfo) {
        if (nTalkID == cNotifyTalkbackStatusInfo.nTalkbackID &&
                cNotifyTalkbackStatusInfo.isTalkingStopped()) {
            showToast("对讲已结束");
            stopAlarmMP3();
            finish();

            HYClient.getModule(ApiTalk.class).observeTalkingStatus(null);
        }
    }


}
