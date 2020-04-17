package huaiye.com.vim.ui.meet;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.huaiye.cmf.audiomanager.AppAudioManager;
import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.cmf.sdp.SdpMessageCmStartSessionRsp;
import com.huaiye.cmf.sdp.SdpUITask;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.exts.GesturedTextureLayer;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.sdk.VideoStartCallback;
import com.huaiye.sdk.media.player.sdk.VideoSupportMeetCallback;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CInviteUserMeetingRsp;
import com.huaiye.sdk.sdpmsgs.meet.CMeetingPicZoomRsp;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingStatusInfo;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyPeerUserMeetingInfo;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;
import com.huaiye.sdk.sdpmsgs.whiteboard.CNotifyUpdateWhiteboard;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Set;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.FinishMeet;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppBaseFragment;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.ui.contacts.ContactsChoiceByAllFriendActivity;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContacts;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContactsNew;
import huaiye.com.vim.ui.contacts.sharedata.ConvertContacts;
import huaiye.com.vim.ui.meet.fragments.MeetBoardFragment;
import huaiye.com.vim.ui.meet.fragments.MeetMembersLayoutFragment;
import huaiye.com.vim.ui.meet.fragments.MeetMembersNewFragment;
import huaiye.com.vim.ui.meet.views.MeetMediaMenuTopView;
import huaiye.com.vim.ui.meet.views.MeetMediaMenuView;

import static huaiye.com.vim.common.AppUtils.XIAOMI;
import static huaiye.com.vim.common.AppUtils.dip2Px;
import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * author: admin
 * date: 2017/12/29
 * version: 0
 * mail: secret
 * desc: MeetActivity
 */
@BindLayout(R.layout.activity_watch)
public class MeetWatchActivity extends AppBaseActivity implements SdpUITask.SdpUIListener {

    @BindView(R.id.layer_gesture)
    GesturedTextureLayer layer_gesture;
    @BindView(R.id.texture_video)
    TextureView texture_video;

    @BindView(R.id.menu_meet_media)
    MeetMediaMenuView menu_meet_media;
    @BindView(R.id.menu_meet_media_top)
    MeetMediaMenuTopView menu_meet_media_top;

    @BindView(R.id.content)
    View content;

    private AppBaseFragment currentFragment;
    MeetMembersNewFragment mMeetMembersFragment;
    MeetMembersLayoutFragment mMeetLayoutFragment;

    @BindExtra
    public String strMeetDomainCode;
    @BindExtra
    public int nMeetID;

    int count = 0;//邀请总数
    int cuntCount = 0;//返回数
    int cuntCountSuccess = 0;//返回成功数

    protected AppAudioManager audio;

    SdpUITask mSdpUITask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);
        EventBus.getDefault().register(this);

        mSdpUITask = new SdpUITask();
        mSdpUITask.setSdpMessageListener(this);
        mSdpUITask.registerSdpNotify(CNotifyMeetingStatusInfo.SelfMessageId);

        mMeetMembersFragment = new MeetMembersNewFragment();
        mMeetMembersFragment.setMeetDomaincode(strMeetDomainCode);

        mMeetLayoutFragment = new MeetMembersLayoutFragment();
        mMeetLayoutFragment.setMeetDomaincode(strMeetDomainCode);

        setIdandOther(null);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        try {
            ft.add(R.id.content, mMeetMembersFragment)
                    .add(R.id.content, mMeetLayoutFragment)
                    .hide(mMeetMembersFragment)
                    .hide(mMeetLayoutFragment)
                    .commit();
        } catch (Exception ignored) {

        }

        menu_meet_media.setVideoEnable(false);
        menu_meet_media.hideMasterView(true);
        menu_meet_media.hideInvisitor();
        menu_meet_media.isWatch(true);

        menu_meet_media_top.showLeft(true);

        addListeners();

        joinWatch();

    }

    @Override
    public void doInitDelay() {

    }

    void addListeners() {
        if (layer_gesture == null) return;
        layer_gesture.setHelperEnable(true);
        layer_gesture.setEventCallback(new GesturedTextureLayer.Callback() {
            @Override
            public void onDoubleTap(int x, int y) {
                // 画面缩放
                Point point = new Point();
                point.x = x;
                point.y = y;

                if (HYClient.getHYPlayer().Meet(texture_video) == null) {
                    return;
                }

                HYClient.getHYPlayer().Meet(texture_video)
                        .zoom(point, new SdkCallback<CMeetingPicZoomRsp>() {
                            @Override
                            public void onSuccess(CMeetingPicZoomRsp resp) {

                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                showToast(errorInfo.getMessage());
                            }

                        });

            }

            @Override
            public void onSingleTap(int x, int y) {
                // 显示菜单
                if (menu_meet_media.getVisibility() == View.VISIBLE) {
                    hideMenu();
                } else if (mMeetMembersFragment != null && mMeetLayoutFragment != null &&
                        (mMeetMembersFragment.isVisible()
                                || mMeetLayoutFragment.isVisible())) {

                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out);
                    if (mMeetMembersFragment.isVisible()) {
                        menu_meet_media.showHandUpRed(false);
                        ft.hide(mMeetMembersFragment);
                    } else if (mMeetLayoutFragment.isVisible()) {
                        mMeetLayoutFragment.setEnable(false);
                        ft.hide(mMeetLayoutFragment);
                    }
                    try {
                        ft.commit();
                    } catch (Exception e) {
                    }

                } else {
                    menu_meet_media.setVisibility(View.VISIBLE);
                    menu_meet_media_top.setVisibility(View.VISIBLE);

                    new RxUtils<>().doDelay(5000, new RxUtils.IMainDelay() {
                        @Override
                        public void onMainDelay() {
                            menu_meet_media.setVisibility(View.GONE);
                            menu_meet_media_top.setVisibility(View.GONE);
                        }
                    }, "hide");
                }


            }

            @Override
            public void onScroll(int startX, int startY, int endX, int endY) {
            }

            @Override
            public void onPointError() {
            }
        });

    }

    /**
     * 观摩
     */
    private void joinWatch() {

        if (!Build.MODEL.equalsIgnoreCase(XIAOMI) && audio == null) {
            audio = AppAudioManager.create(AppUtils.ctx, true);
            audio.start(new AppAudioManager.AudioManagerEvents() {
                @Override
                public void onAudioDeviceChanged(AppAudioManager.AudioDevice audioDevice, Set<AppAudioManager.AudioDevice> set) {
                }
            });

        }

        HYClient.getHYPlayer().startPlay(Player.Params.TypeMeetReal()
                .setMeetDomainCode(strMeetDomainCode)
                .setMeetID(nMeetID)
                .setPreview(texture_video)
                .setStartCallback(new VideoStartCallback() {
                    @Override
                    public void onSuccess(VideoParams videoParams) {
                        requestInfo();
                        if (menu_meet_media_top != null) {
                            menu_meet_media_top.startTime();
                        }
                    }

                    @Override
                    public void onError(VideoParams videoParams, SdkCallback.ErrorInfo errorInfo) {
                        showToast(errorInfo.getMessage());
                        finish();
                    }
                })
                .setSupportMeetCallback(new VideoSupportMeetCallback() {
                    @Override
                    public void onMeetStatusChanged(CNotifyMeetingStatusInfo cNotifyMeetingStatusInfo) {

                    }

                    @Override
                    public void onObserveStatusSuccess(VideoParams videoParams) {
                    }

                    @Override
                    public void onObserveStatusFail(VideoParams videoParams, SdkCallback.ErrorInfo errorInfo) {
                    }
                }));

        menu_meet_media.setCallback(new MeetMediaMenuView.Callback() {

            @Override
            public void onMeetExitClicked() {
                // 退出会议
                onBackPressed();
            }

            @Override
            public void onMemberListClicked() {
                // 人员列表
                hideMenu();
                changeFragment(mMeetMembersFragment);
            }

            @Override
            public void onMeetInviteClicked() {
                // 会议邀请
                HYClient.getModule(ApiMeet.class).requestMeetDetail(
                        SdkParamsCenter.Meet.RequestMeetDetail()
                                .setMeetDomainCode(strMeetDomainCode)
                                .setnListMode(1)
                                .setMeetID(nMeetID), new SdkCallback<CGetMeetingInfoRsp>() {
                            @Override
                            public void onSuccess(final CGetMeetingInfoRsp cGetMeetingInfoRsp) {
                                new RxUtils<ArrayList<CGetMeetingInfoRsp.UserInfo>>()
                                        .doOnThreadObMain(new RxUtils.IThreadAndMainDeal<ArrayList<CGetMeetingInfoRsp.UserInfo>>() {

                                            @Override
                                            public ArrayList<CGetMeetingInfoRsp.UserInfo> doOnThread() {
                                                ArrayList<CGetMeetingInfoRsp.UserInfo> tempAll = new ArrayList();
                                                for (CGetMeetingInfoRsp.UserInfo temp : cGetMeetingInfoRsp.listUser) {
                                                    if (temp.nJoinStatus == 2) {
                                                        tempAll.add(temp);
                                                    }
                                                }

                                                return tempAll;
                                            }

                                            @Override
                                            public void doOnMain(ArrayList<CGetMeetingInfoRsp.UserInfo> data) {
                                                ChoosedContacts.get().clear();
                                                ChoosedContacts.get().setOnMeetUsersInfo(data);
                                                Intent intent = new Intent(getSelf(), ContactsChoiceByAllFriendActivity.class);
                                                intent.putExtra("titleName", "邀请参会");
                                                intent.putExtra("isSelectUser", true);
                                                intent.putExtra("needAddSelf", false);
                                                startActivityForResult(intent, 1000);
                                            }
                                        });
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                showToast(ErrorMsg.getMsg(ErrorMsg.get_meet_info_err_code));
                            }
                        });

            }

            @Override
            public void onInnerCameraClicked() {
            }

            @Override
            public void onPlayerVoiceClicked(boolean isVoiceOpened) {
                // 静音
                HYClient.getHYPlayer().setAudioOnEx(isVoiceOpened, texture_video);
            }

            @Override
            public void onPlayerVideoClicked(boolean isVideoOpened) {
                HYClient.getHYCapture().setCaptureVideoOn(isVideoOpened);
            }

            @Override
            public void showLayoutChange() {
                hideMenu();
                mMeetLayoutFragment.requestLayoutInfo();
                changeFragment(mMeetLayoutFragment);
            }

            @Override
            public void onYuLanClick() {

            }

            @Override
            public void showSharePop(View view) {
            }

            @Override
            public void showMorePop(View view) {

            }

            @Override
            public void onHandUp() {
            }

            @Override
            public void startRecordSuccess() {
                menu_meet_media_top.isRecord(true);
            }

            @Override
            public void onControlClick() {

            }

            @Override
            public void onCaptureVoiceClicked() {
                menu_meet_media.toggleCaptureAudio();
            }
        });

    }

    @Override
    public void onMeetInvite(CNotifyInviteUserJoinMeeting data, long millis) {
        super.onMeetInvite(data, millis);

        if (data == null) {
            return;
        }
        if (data.nMeetingStatus != 1) {
            return;
        }

        onBackPressed();
    }

    @Override
    public void onTalkInvite(CNotifyUserJoinTalkback data, long millis) {
        super.onTalkInvite(data, millis);
        if (data == null) {
            return;
        }
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        destruct();
        HYClient.getHYPlayer().stopPlayEx(null, texture_video);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HYClient.getHYPlayer().onPlayFront();
    }

    @Override
    protected void onPause() {
        super.onPause();
        HYClient.getHYPlayer().onPlayBackground();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        clearContacts();

        if (audio != null) {
            audio.stop();
            audio = null;
        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        currentFragment = null;
        mMeetMembersFragment = null;
        mMeetLayoutFragment = null;

        EventBus.getDefault().unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final CNotifyMeetingStatusInfo info) {
        if (info.nMeetingStatus == 2) {
            showToast("会议已结束");
            EventBus.getDefault().post(new FinishMeet());
            onBackPressed();
        } else {
            if (mMeetMembersFragment != null) {
                mMeetMembersFragment.changeOneKey(info.isMeetMute());
                mMeetMembersFragment.refUser();
            }
        }
    }

    private void clearContacts() {
        ChoosedContacts.get().clear();
        ChoosedContacts.get().clearMeetUsers();
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

    /**
     * 邀请返回
     */
    void onInviteClicked() {
        count = 0;
        cuntCount = 0;
        cuntCountSuccess = 0;

        ArrayList<CStartMeetingReq.UserInfo> users = ChoosedContacts.get().convertContacts(ChoosedContacts.get().getContacts( false));
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            for(CStartMeetingReq.UserInfo temp : users) {
                if(!HYClient.getSdkOptions().User().getUserId().equals(temp.strUserID)) {
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
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                finish();
                return;
            }
            realInvisitor(ChoosedContacts.get().convertContacts(ChoosedContacts.get().getContacts( false)));
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
                            if(nEncryptIMEnable) {
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
                            if(nEncryptIMEnable) {
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

    public void hideAll() {
        if (isFinishing()) {
            return;
        }

        if (currentFragment instanceof MeetMembersNewFragment) {
            menu_meet_media.showHandUpRed(false);
        }

        currentFragment = null;

        try {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.hide(mMeetMembersFragment)
                    .hide(mMeetLayoutFragment)
                    .commit();
        } catch (Exception e) {
        }
    }

    /**
     * 隐藏菜单
     */
    private void hideMenu() {
        if (menu_meet_media != null) {
            menu_meet_media.setVisibility(View.GONE);
//            menu_meet_media.getHandler().removeCallbacksAndMessages(null);
        }
        if (menu_meet_media_top != null) {
            menu_meet_media_top.setVisibility(View.GONE);
//            menu_meet_media_top.getHandler().removeCallbacksAndMessages(null);
        }
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

                                setIdandOther(cGetMeetingInfoRsp);

                                if (menu_meet_media != null) {
                                    if (cGetMeetingInfoRsp.nRecordID == 0) {
                                        menu_meet_media.canStartRecord(true);
                                    } else {
                                        menu_meet_media.canStartRecord(false);
                                    }

                                    menu_meet_media.hideMasterView(true);
                                }

                                if (menu_meet_media_top != null) {
                                    menu_meet_media_top.showName(cGetMeetingInfoRsp.strMeetingName + "(ID:" + nMeetID + ")");
                                }
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {

                            }
                        });
    }

    /**
     * 设置相关的id参数
     */
    private void setIdandOther(CGetMeetingInfoRsp rsp) {
        if (mMeetMembersFragment != null) {
            mMeetMembersFragment.setMeetID(nMeetID);
            mMeetMembersFragment.setIsMeetStarter(true, AppDatas.Auth().getUserLoginName());
        }
        if (mMeetLayoutFragment != null) mMeetLayoutFragment.setMeetID(nMeetID);
        if (rsp != null && mMeetMembersFragment != null) {
            mMeetMembersFragment.setIsMeetStarter(true, rsp.strMainUserID);
        }
    }

    /**
     * 展示fragment
     *
     * @param fragment
     */
    private void changeFragment(AppBaseFragment fragment) {
        currentFragment = fragment;
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) content.getLayoutParams();
        if (fragment instanceof MeetMembersLayoutFragment ||
                fragment instanceof MeetBoardFragment) {
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            lp.width = dip2Px(this, 250);
        }

        if (content != null)
            content.setLayoutParams(lp);

        try {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out);
            ft.show(fragment);
            ft.commit();
        } catch (Exception e) {

        }
    }

    @Override
    public void onSdpMessage(SdpMessageBase sdpMessageBase, int i) {
        switch (sdpMessageBase.GetMessageType()) {
            case CNotifyPeerUserMeetingInfo.SelfMessageId:
                if (mMeetMembersFragment != null) {
                    mMeetMembersFragment.refUser();
                }
                break;
            case CNotifyUpdateWhiteboard.SelfMessageId:
                break;
        }
    }

    private void destruct() {
        if (mSdpUITask != null) {
            mSdpUITask.exit();
            mSdpUITask = null;
        }
    }

}
