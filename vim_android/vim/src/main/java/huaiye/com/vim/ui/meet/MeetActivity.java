package huaiye.com.vim.ui.meet;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.huaiye.cmf.JniIntf;
import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.cmf.sdp.SdpMessageCmStartSessionRsp;
import com.huaiye.cmf.sdp.SdpUITask;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.exts.GesturedTextureLayer;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.media.capture.HYCapture;
import com.huaiye.sdk.media.player.HYPlayer;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.sdk.VideoStartCallback;
import com.huaiye.sdk.media.player.sdk.VideoSupportMeetCallback;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.huaiye.sdk.sdkabi._api.ApiIO;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._api.ApiTalk;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.meet.ParamsJoinMeetMultiPlay;
import com.huaiye.sdk.sdkabi.abilities.io.callback.CallbackStartFileConvert;
import com.huaiye.sdk.sdkabi.abilities.meet.callback.CallbackJoinMeet;
import com.huaiye.sdk.sdkabi.abilities.meet.callback.CallbackQuitMeet;
import com.huaiye.sdk.sdpmsgs.io.CStartFileConvertRsp;
import com.huaiye.sdk.sdpmsgs.io.NotifyFileConvertStatus;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CInviteUserMeetingRsp;
import com.huaiye.sdk.sdpmsgs.meet.CJoinMeetingRsp;
import com.huaiye.sdk.sdpmsgs.meet.CMeetingPicZoomRsp;
import com.huaiye.sdk.sdpmsgs.meet.CMeetingUserRaiseRsp;
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
import com.huaiye.sdk.sdpmsgs.whiteboard.CStartWhiteboardShareRsp;
import com.huaiye.sdk.sdpmsgs.whiteboard.CStopWhiteboardShareRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.bus.PhoneStatus;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppBaseFragment;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.common.dialog.LogicDialog;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.msgs.AppMessages;
import huaiye.com.vim.models.auth.bean.Upload;
import huaiye.com.vim.ui.contacts.ContactsChoiceByAllFriendActivity;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContacts;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContactsNew;
import huaiye.com.vim.ui.contacts.sharedata.ConvertContacts;
import huaiye.com.vim.ui.meet.fragments.MeetBoardFragment;
import huaiye.com.vim.ui.meet.fragments.MeetMembersLayoutFragment;
import huaiye.com.vim.ui.meet.fragments.MeetMembersNewFragment;
import huaiye.com.vim.ui.meet.views.MeetMediaMenuTopView;
import huaiye.com.vim.ui.meet.views.MeetMediaMenuView;
import huaiye.com.vim.ui.meet.views.MorePopupWindow;
import huaiye.com.vim.ui.talk.TalkActivity;
import huaiye.com.vim.ui.talk.TalkVoiceActivity;

import static huaiye.com.vim.common.AppUtils.dip2Px;
import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * author: admin
 * date: 2017/12/29
 * version: 0
 * mail: secret
 * desc: MeetActivity
 * <p>
 * 废弃
 */
@BindLayout(R.layout.activity_meet)
public class MeetActivity extends AppBaseActivity implements SdpUITask.SdpUIListener {

    @BindView(R.id.layer_gesture)
    GesturedTextureLayer layer_gesture;
    @BindView(R.id.texture_video)
    TextureView texture_video;
    @BindView(R.id.texture_preview)
    TextureView texture_preview;
    @BindView(R.id.content)
    View content;
    @BindView(R.id.iv_change)
    View iv_change;
    @BindView(R.id.iv_jinyan)
    View iv_jinyan;

    @BindView(R.id.menu_meet_media)
    MeetMediaMenuView menu_meet_media;
    @BindView(R.id.menu_meet_media_top)
    MeetMediaMenuTopView menu_meet_media_top;

    MeetBoardFragment mMeetBoardFragment;
    MeetMembersNewFragment mMeetMembersFragment;
    MeetMembersLayoutFragment mMeetLayoutFragment;

    // 是否是推送模式
    boolean isInPicturePushMode;

    @BindExtra
    boolean isWatch;
    //匿名人员不能邀请
    @BindExtra
    boolean closeInvisitor;
    @BindExtra
    boolean isMeetStarter;
    @BindExtra
    public String strMeetDomainCode;
    @BindExtra
    String strInviteUserDomainCode;
    @BindExtra
    String strInviteUserId;
    @BindExtra
    String strInviteUserTokenID;
    @BindExtra
    public int nMeetID;
    @BindExtra
    boolean isCloseVoice;
    @BindExtra
    boolean isCloseVideo;
    @BindExtra
    SdkBaseParams.MediaMode mMediaMode;

    int count = 0;//邀请总数
    int cuntCount = 0;//返回数
    int cuntCountSuccess = 0;//返回成功数

    private AppBaseFragment currentFragment;
    private CNotifyWhiteboardStatus status;
    LogicDialog dialog;
    CNotifyInviteUserJoinMeeting currentMeetingInvite;
    CNotifyUserJoinTalkback currentTalkInvite;
    ShareLocalPopupWindow shareLocalPopupWindow;
    private MorePopupWindow mMorePopupWindow;

    SdpMessageCmStartSessionRsp sessionRsp;
    SdpUITask mSdpUITask;
    RxUtils rxUtils;
    boolean isClickClose;

    boolean hasBusy;

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
        mSdpUITask.registerSdpNotify(CNotifyPeerUserMeetingInfo.SelfMessageId);
        mSdpUITask.registerSdpNotify(CNotifyMeetingRaiseInfo.SelfMessageId);
        mSdpUITask.registerSdpNotify(CNotifyUpdateWhiteboard.SelfMessageId);
        rxUtils = new RxUtils();

        HYClient.getHYCapture().setCameraConferenceMode(HYCapture.CameraConferenceMode.LANDSCAPE);

        mMeetBoardFragment = new MeetBoardFragment();
        mMeetBoardFragment.setMeetDomaincode(strMeetDomainCode);

        mMeetMembersFragment = new MeetMembersNewFragment();
        mMeetMembersFragment.setMeetDomaincode(strMeetDomainCode);

        mMeetLayoutFragment = new MeetMembersLayoutFragment();
        mMeetLayoutFragment.setMeetDomaincode(strMeetDomainCode);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        try {
            ft.add(R.id.content, mMeetMembersFragment)
                    .add(R.id.content, mMeetLayoutFragment)
                    .add(R.id.content, mMeetBoardFragment)
                    .hide(mMeetMembersFragment)
                    .hide(mMeetLayoutFragment)
                    .hide(mMeetBoardFragment)
                    .commit();
        } catch (Exception ignored) {

        }

        setIdandOther(null);

        if (menu_meet_media != null) {

            menu_meet_media.setVideoEnable(isCloseVideo);
            menu_meet_media.hideMasterView(isMeetStarter);
            menu_meet_media_top.showLeft(isWatch);

            if (closeInvisitor) menu_meet_media.hideInvisitor();
        }


        addListeners();

        if (isWatch) {
            joinWatch();
        } else {
            startJoineMeet();
        }
    }

    private void startJoineMeet() {
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable &&
                !HYClient.getSdkOptions().User().getUserId().equals(strInviteUserId)) {
            EncryptUtil.startEncrypt(isMeetStarter, strInviteUserId, strInviteUserDomainCode,
                    nMeetID + "", strMeetDomainCode, new SdkCallback<SdpMessageCmStartSessionRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmStartSessionRsp sessionRsp) {
                            MeetActivity.this.sessionRsp = sessionRsp;
                            joinMeet();
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            if (isFinishing()) {
                                return;
                            }
                            showToast(ErrorMsg.getMsg(ErrorMsg.get_meet_info_err_code));
                        }
                    });
        } else {
            if(nEncryptIMEnable && !HYClient.getSdkOptions().encrypt().isEncryptBind()) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                finish();
                return;
            }
            joinMeet();
        }
    }

    @Override
    public void doInitDelay() {

    }

    /**
     * 设置相关的id参数
     */
    private void setIdandOther(CGetMeetingInfoRsp rsp) {
        if (mMeetBoardFragment != null) mMeetBoardFragment.setMeetID(nMeetID);
        if (mMeetMembersFragment != null) mMeetMembersFragment.setMeetID(nMeetID);
        if (mMeetLayoutFragment != null) mMeetLayoutFragment.setMeetID(nMeetID);
        if (rsp != null && mMeetMembersFragment != null) {
            mMeetMembersFragment.setIsMeetStarter(rsp.strMainUserID.equals(AppDatas.Auth().getUserLoginName()), rsp.strMainUserID);
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
                                isMeetStarter = cGetMeetingInfoRsp.strMainUserID.equals(AppDatas.Auth().getUserLoginName());
                                if (menu_meet_media != null) {
                                    if (cGetMeetingInfoRsp.nRecordID == 0 && isMeetStarter) {
                                        menu_meet_media.canStartRecord(true);
                                    } else {
                                        menu_meet_media.canStartRecord(false);
                                    }

                                    menu_meet_media.hideMasterView(isMeetStarter);
                                }

                                setIdandOther(cGetMeetingInfoRsp);

                                if (menu_meet_media_top != null) {
                                    menu_meet_media_top.showName(cGetMeetingInfoRsp.strMeetingName + "(ID:" + nMeetID + ")");
                                }
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {

                            }
                        });
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
                if (isInPicturePushMode) {
                    isInPicturePushMode = false;

                } else {
                    // 显示菜单
                    if (menu_meet_media.getVisibility() == View.VISIBLE) {
                        hideMenu();
                    } else if (mMeetMembersFragment != null && mMeetLayoutFragment != null &&
                            mMeetBoardFragment != null && (mMeetMembersFragment.isVisible()
                            || mMeetLayoutFragment.isVisible()
                            || mMeetBoardFragment.isVisible())) {

                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction()
                                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out);
                        if (mMeetMembersFragment.isVisible()) {
                            menu_meet_media.showHandUpRed(false);
                            ft.hide(mMeetMembersFragment);
                        } else if (mMeetLayoutFragment.isVisible()) {
                            mMeetLayoutFragment.setEnable(false);
                            ft.hide(mMeetLayoutFragment);
                        } else if (mMeetBoardFragment.isVisible()) {
                            ft.hide(mMeetBoardFragment);
                        }
                        try {
                            ft.commit();
                        } catch (Exception e) {
                        }

                    } else {
                        menu_meet_media.setVisibility(View.VISIBLE);
                        menu_meet_media_top.setVisibility(View.VISIBLE);

                        rxUtils.doDelay(5000, new RxUtils.IMainDelay() {
                            @Override
                            public void onMainDelay() {
                                if (shareLocalPopupWindow != null && shareLocalPopupWindow.isShowing()) {
                                    return;
                                }
                                menu_meet_media.setVisibility(View.GONE);
                                menu_meet_media_top.setVisibility(View.GONE);
                            }
                        }, "hide");
                    }
                }
            }

            @Override
            public void onScroll(int startX, int startY, int endX, int endY) {
                // 画面交换
//                Point point1 = new Point();
//                point1.x = startX;
//                point1.y = startY;
//
//                Point point2 = new Point();
//                point2.x = endX;
//                point2.y = endY;
//
//                HYClient.getHYPlayer().Meet(texture_video).swap(point1, point2,
//                        new SdkCallback<CMeetingPicSwapRsp>() {
//                            @Override
//                            public void onSuccess(CMeetingPicSwapRsp resp) {
//                                showToast("画面交换成功");
//                            }
//
//                            @Override
//                            public void onError(ErrorInfo errorInfo) {
//                                showToast(errorInfo.getMessage());
//                            }
//
//                        });
            }

            @Override
            public void onPointError() {
                // 坐标错误
//                showToast("坐标不合理，请重新取坐标");
            }
        });

        menu_meet_media.setCallback(new MeetMediaMenuView.Callback() {

            @Override
            public void onYuLanClick() {
                if (texture_preview.getVisibility() == View.INVISIBLE) {
                    texture_preview.setVisibility(View.VISIBLE);
                    HYClient.getHYCapture().setPreviewWindow(texture_preview);
                } else if (texture_preview.getVisibility() == View.GONE) {
                    texture_preview.setVisibility(View.VISIBLE);
                } else {
                    texture_preview.setVisibility(View.GONE);
                }
            }

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
                                                intent.putExtra("titleName", getString(R.string.title_notice3));
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
                // 摄像头切换
                HYClient.getHYCapture().toggleInnerCamera();
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
            public void showSharePop(View view) {
                if (status != null) {
                    toggleWhiteBoard();
                } else if (shareLocalPopupWindow != null) {
                    shareLocalPopupWindow.showView(view);
                }
            }

            @Override
            public void showMorePop(View view) {

            }

            @Override
            public void onHandUp() {
                HYClient.getModule(ApiMeet.class).raiseHandsInMeeting(
                        SdkParamsCenter.Meet.UserRaise()
                                .setStrMeetingDomainCode(strMeetDomainCode)
                                .setnMeetingID(nMeetID), new SdkCallback<CMeetingUserRaiseRsp>() {

                            @Override
                            public void onSuccess(CMeetingUserRaiseRsp info) {
                                showToast(getString(R.string.meet_handup_success));
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                showToast(ErrorMsg.getMsg(ErrorMsg.raise_hands_err_code));
                            }
                        });
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
     * 加入会议
     */
    void joinMeet() {
        if (shareLocalPopupWindow == null) {
            shareLocalPopupWindow = new ShareLocalPopupWindow(this);
            shareLocalPopupWindow.setConfirmClickListener(new ShareLocalPopupWindow.ConfirmClickListener() {
                @Override
                public void onShareImg() {
                    if (status != null) {
                        showToast(getString(R.string.meet_whiteboard_success));
                        return;
                    }
                    startWhiteBoard(1, 2);

                }

                @Override
                public void onOpenWhiteBoard() {
                    toggleWhiteBoard();
                }

                @Override
                public void onShareFile() {
                    if (status != null) {
                        showToast(getString(R.string.meet_whiteboard_success));
                        return;
                    }
                    startWhiteBoard(1, 1);
                }

                @Override
                public void onCancel() {
                    menu_meet_media.setVisibility(View.GONE);
                    menu_meet_media_top.setVisibility(View.GONE);
                }
            });
        }
        shareLocalPopupWindow.init();
        if (mMorePopupWindow == null) {
            mMorePopupWindow = new MorePopupWindow(this);
            mMorePopupWindow.setMoreItemClickListener(new MorePopupWindow.MoreItemClickListener() {
                @Override
                public void onAddPerson() {
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
                                                    ChoosedContactsNew.get().setContacts(ConvertContacts.ConvertMeetUserInfoToContacts(data));
                                                    Intent intent = new Intent(MeetActivity.this, ContactsChoiceByAllFriendActivity.class);
                                                    intent.putExtra("titleName", getString(R.string.title_notice3));
                                                    MeetActivity.this.startActivityForResult(intent, 1000);
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
                public void onControl() {

                }

                @Override
                public void onShare() {

                }

                @Override
                public void onChat() {

                }

                @Override
                public void onCancel() {

                }
            });
        }

        if (menu_meet_media != null) menu_meet_media.isWatch(isWatch);

        if (menu_meet_media_top != null) menu_meet_media_top.showLeft(isWatch);


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
                .setnIsOnlyAudio(isCloseVideo ? 1 : 0)
                .setPlayerPreview(texture_video);
        if (sessionRsp != null) {
            HYPlayer.Config.getRealPlayConfig().setPlayerCallID(sessionRsp.m_nCallId);
            pjmmp.setCallId(sessionRsp.m_nCallId);
        }
        HYClient.getModule(ApiMeet.class).joinMeetingMultiPlay(pjmmp, new CallbackJoinMeet() {
            @Override
            public void onAgreeMeet(CNotifyPeerUserMeetingInfo info) {
                Log.e("VIMApp", "onAgreeMeet");
                if (isFinishing()) {
                    return;
                }
                        /*multiPlayHelper.userJoin(info.strUserID);
                        showInfo(info.strToUserName + getString(R.string.meet_agree));*/
                showToast(info.strToUserName + getString(R.string.meet_agree));

            }

            @Override
            public void onRefuseMeet(CNotifyPeerUserMeetingInfo info) {
                if (isFinishing()) {

                    return;
                }
//                        showInfo(info.strToUserName + getString(R.string.meet_disagree));
                showToast(info.strToUserName + getString(R.string.meet_disagree));
            }

            @Override
            public void onNoResponse(CNotifyPeerUserMeetingInfo info) {
                if (isFinishing()) {
                    return;
                }
//                        showInfo(info.strToUserName + getString(R.string.meet_wuxiangying));
                showToast(info.strToUserName + getString(R.string.meet_wuxiangying));
            }

            @Override
            public void onUserOffline(CNotifyPeerUserMeetingInfo info) {
                if (isFinishing()) {
                    return;
                }
//                        showInfo(info.strToUserName + getString(R.string.meet_offline));
                showToast(info.strToUserName + getString(R.string.meet_offline));
            }

            @Override
            public void onRepeatInvitation(CNotifyPeerUserMeetingInfo info) {
                if (isFinishing()) {
                    return;
                }
//                        showToast(info.strToUserName + getString(R.string.meet_chongfuyaoqing));
                showToast(info.strToUserName + getString(R.string.meet_chongfuyaoqing));
            }

            @Override
            public void onMeetStatusChanged(CNotifyMeetingStatusInfo info) {
                if (isFinishing()) {
                    return;
                }
                if (info.isMeetFinished()) {
                            /*isInMeeting = false;
                            getInfoDialog().setContent(getString(R.string.meet_has_end))
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            finish();
                                        }
                                    }).show();*/
                    showToast(getString(R.string.meet_has_end));
                    finish();
                } else {
//                            showToast("onMeetStatusChanged... ");
                    //获取用户列表,看看有没有人员变化
                            /*if (multiPlayHelper != null) {
                                multiPlayHelper.refreshUser(MultiPlayHelper.convertUser(info.lstMeetingUser));
                                multiPlayHelper.notifyChanged();
                            }*/
                }
            }

            @Override
            public void onKickedFromMeet(CNotifyKickUserMeeting info) {
                if (isFinishing()) {
                    return;
                }
                        /*getInfoDialog().setContent(getString(R.string.meet_kitout_bei))
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                }).show();*/
                showToast(getString(R.string.meet_kitout_bei));
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
            public void onAfterSuccess(CGetMeetingInfoRsp cGetMeetingInfoRsp) {

            }

            @Override
            public void onSuccess(CJoinMeetingRsp resp) {
//                        showInfo("进入会议成功...");
                showToast(getString(R.string.meet_jinruhuiyi_success));
                        /*view_media_menu.updateStatus();
                        HYClient.getHYCapture().setCaptureVideoOn(AppSettings.get().getMeetSettings().isMeetVideoShow());
                        HYClient.getHYCapture().setCaptureAudioOn(AppSettings.get().getMeetSettings().isMeetVoiceShow());*/

            }

            @Override
            public void onError(ErrorInfo errorInfo) {
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
                showToast(getString(R.string.meet_join_error));
            }

        });
        /*ParamsJoinMeet paramsJoinMeet = SdkParamsCenter.Meet.JoinMeet()
                .setMeetDomainCode(strMeetDomainCode)
                .setMeetID(nMeetID)
                .setAgreeMode(SdkBaseParams.AgreeMode.Agree)
                .setCameraIndex(SP.getInteger(STRING_KEY_camera) == 1 ? HYCapture.Camera.Foreground : HYCapture.Camera.Background)
                .setMediaMode(mMediaMode)
                .setCaptureOrientation(HYCapture.CaptureOrientation.SCREEN_ORIENTATION_LANDSCAPELEFT)
                .setMeetScreen(SdkBaseParams.MeetScreen.Multiple)
                .setIsAutoStopCapture(true)
                .setPlayerPreview(texture_video)
                .setnIsOnlyAudio(isCloseVideo ? 1 : 0);

        HYClient.getHYCapture().stopCapture(null);
        HYClient.getModule(ApiMeet.class)
                .joinMeeting(paramsJoinMeet, new CallbackJoinMeet() {
                    @Override
                    public void onAgreeMeet(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
                    }

                    @Override
                    public void onRefuseMeet(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
                    }

                    @Override
                    public void onNoResponse(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
                    }

                    @Override
                    public void onUserOffline(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
                    }

                    @Override
                    public void onRepeatInvitation(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
                    }

                    @Override
                    public void onMeetStatusChanged(final CNotifyMeetingStatusInfo cNotifyMeetingStatusInfo) {
                        meetStatusChange(cNotifyMeetingStatusInfo);
                    }

                    @Override
                    public void onKickedFromMeet(CNotifyKickUserMeeting cNotifyKickUserMeeting) {
                        // 被踢出会议
                        AppMessages.get().add(MessageData.from(cNotifyKickUserMeeting));

                        showToast("你被踢出会议");
                        delayFinish();
                    }

                    @Override
                    public void onUserRaiseOfMeet(CNotifyMeetingRaiseInfo cNotifyMeetingRaiseInfo) {
                        onUserRaiseOfMeetActivity(cNotifyMeetingRaiseInfo);
                    }

                    @Override
                    public void onVideoLimiteMeet(CNotifyMeetingPushVideo info) {
                        if (info.nVideolimited == 1) {
                            showToast("你已离开画面");
                        } else {
                            showToast("你已进入画面");
                        }
                    }

                    @Override
                    public void onCaptureStatusChanged(SdpMessageBase msg) {
                        if (msg instanceof  CNotifyReconnectStatus){
                            CNotifyReconnectStatus cNotifyReconnectStatus = (CNotifyReconnectStatus) msg;
                            if (cNotifyReconnectStatus.getConnectionStatus() == SdkBaseParams.ConnectionStatus.Connecting
                                    || cNotifyReconnectStatus.getConnectionStatus() == SdkBaseParams.ConnectionStatus.Disconnected){
                                if (menu_meet_media_top != null){
                                    SdpMsgCaptureQualityNotify newMsg = new SdpMsgCaptureQualityNotify();
                                    newMsg.m_nCurQuality = -1;
                                    menu_meet_media_top.changeQuality( newMsg);
                                }
                            }
                            return;
                        }

                        switch (MediaStatus.get(msg)) {
                            case CAPTURE_QUALITY:
                                if (menu_meet_media_top != null)
                                    menu_meet_media_top.changeQuality((SdpMsgCaptureQualityNotify) msg);
                                break;
                        }
                    }

                    @Override
                    public void onMeetFinished() {
                    }

                    @Override
                    public void onReceiverWhiteboardStatus(CNotifyWhiteboardStatus status) {
                        boolean isOwner = status.strInitiatorDomainCode.equals(HYClient.getSdkOptions().User().getDomainCode()) &&
                                status.strInitiatorTokenID.equals(HYClient.getSdkOptions().User().getUserTokenId());
                        if (mMeetBoardFragment != null)
                            mMeetBoardFragment.changeOpenStatus(status.nStatus == 1);
                        if (status.nStatus == 1) {
                            openBoardView(status, isOwner);
                        } else {
                            closeBoardView(isOwner);
                        }
                    }

                    @Override
                    public void onAfterSuccess(ArrayList<CGetMeetingInfoRsp.UserInfo> arrayList) {

                    }

                    @Override
                    public void onSuccess(CJoinMeetingRsp cJoinMeetingRsp) {
                        startSpeakerLound();
                        requestInfo();

                        setIdandOther(null);

                        if (TextUtils.isEmpty(SP.getString(STRING_KEY_player))) {
                            SP.putString(STRING_KEY_player, STRING_KEY_VGA);
                        }
                        if (SP.getString(STRING_KEY_player).equals(STRING_KEY_HD)) {
                            HYClient.getHYPlayer().Meet(texture_video).setPlayQuality(SdkBaseParams.PlayQuality.HD);
                        } else {//VGA
                            HYClient.getHYPlayer().Meet(texture_video).setPlayQuality(SdkBaseParams.PlayQuality.VGA);
                        }

                        if (menu_meet_media_top != null)
                            menu_meet_media_top.startTime();

                        if (menu_meet_media == null) return;

                        menu_meet_media.changeSound(true);

                        if (isCloseVoice) {
                            menu_meet_media.closeVoice();
                        } else {
                            menu_meet_media.openVoice();
                        }
                        if (isCloseVideo) {
                            menu_meet_media.closeVideo();
                        } else {
                            menu_meet_media.openVideo();
                        }

                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        if (errorInfo.getCode() == ErrorMsg.meet_not_exit_code ||
                                errorInfo.getCode() == ErrorMsg.meet_not_koten_code) {
                            showToast(ErrorMsg.getMsg(ErrorMsg.meet_not_exit_code));
                        } else {
                            showToast(ErrorMsg.getMsg(ErrorMsg.joine_err_code));
                        }
                        delayFinish();
                    }
                });*/
    }

    /**
     * 打开关闭白板
     */
    private void toggleWhiteBoard() {
        if (status == null) {
            startWhiteBoard(0, 0);
        } else if (status.strInitiatorDomainCode.equals(HYClient.getSdkOptions().User().getDomainCode()) &&
                status.strInitiatorTokenID.equals(HYClient.getSdkOptions().User().getUserTokenId())) {
            stopWhiteBoard();
        } else {
            showToast(getString(R.string.meet_wuquanxian_caozuo));
        }
    }

    /**
     * 结束
     */
    private void delayFinish() {
        rxUtils.doDelay(1200, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                finish();
            }
        }, "finish");
    }

    private void onUserOfflineActivity(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
        if (isMeetStarter) {
//                            showToast(cNotifyPeerUserMeetingInfo.strToUserName + getString(R.string.meet_offline));
        }
    }

    private void onNoResponseActivity(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
        if (isMeetStarter) {
            showToast(cNotifyPeerUserMeetingInfo.strToUserName + getString(R.string.meet_wuxiangying));
        }
    }

    private void onAgreeMeetActivity(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
        if (cNotifyPeerUserMeetingInfo.strUserID.equals(HYClient.getSdkOptions().User().getUserId())
                && cNotifyPeerUserMeetingInfo.strToUserDomainCode.equals(HYClient.getSdkOptions().User().getDomainCode())) {
        } else {
//                            showToast(cNotifyPeerUserMeetingInfo.strToUserName + " 同意会议");
            if (mMeetMembersFragment != null)
                mMeetMembersFragment.refUser();
        }
    }

    private void onRefuseMeetActivity(CNotifyPeerUserMeetingInfo cNotifyPeerUserMeetingInfo) {
        if (isMeetStarter) {
            showToast(cNotifyPeerUserMeetingInfo.strToUserName + getString(R.string.meet_disagree));
        }
    }

    private void onUserRaiseOfMeetActivity(CNotifyMeetingRaiseInfo cNotifyMeetingRaiseInfo) {
        showToast(cNotifyMeetingRaiseInfo.strUserName + getString(R.string.meet_jushou));

        if (mMeetMembersFragment == null) return;

        mMeetMembersFragment.changeDataHandUp(cNotifyMeetingRaiseInfo);
        if (!mMeetMembersFragment.isVisible()) {
            menu_meet_media.showHandUpRed(true);
        }
    }

    private void meetStatusChange(final CNotifyMeetingStatusInfo cNotifyMeetingStatusInfo) {
        if (cNotifyMeetingStatusInfo.nMeetingStatus == 2) {
            quitMeet(isMeetStarter);

            showToast(getString(R.string.meet_has_end));
        } else {
            if (mMeetMembersFragment != null) {
                mMeetMembersFragment.changeOneKey(cNotifyMeetingStatusInfo.isMeetMute());
                mMeetMembersFragment.refUser();
            }
            new RxUtils<CNotifyMeetingStatusInfo.User>()
                    .doOnThreadObMain(new RxUtils.IThreadAndMainDeal<CNotifyMeetingStatusInfo.User>() {
                        @Override
                        public CNotifyMeetingStatusInfo.User doOnThread() {
                            CNotifyMeetingStatusInfo.User tempAll = null;
                            for (CNotifyMeetingStatusInfo.User temp : cNotifyMeetingStatusInfo.lstMeetingUser) {
                                if (temp.strUserID.equals(AppDatas.Auth().getUserLoginName())) {
                                    tempAll = temp;
                                    break;
                                }
                            }
                            return tempAll;
                        }

                        @Override
                        public void doOnMain(CNotifyMeetingStatusInfo.User tempAll) {
                            if (tempAll.nMuteStatus == SdkBaseParams.MuteStatus.Mute.value()) {
                                iv_jinyan.setVisibility(View.GONE);
                            } else {
                                iv_jinyan.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
    }

    /**
     * 观摩
     */
    private void joinWatch() {
        if (menu_meet_media != null) {
            menu_meet_media.isWatch(isWatch);
        }
        startSpeakerLound();
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
                        meetStatusChange(cNotifyMeetingStatusInfo);
                    }

                    @Override
                    public void onObserveStatusSuccess(VideoParams videoParams) {
                    }

                    @Override
                    public void onObserveStatusFail(VideoParams videoParams, SdkCallback.ErrorInfo errorInfo) {
                    }
                }));
    }

    /**
     * 打开白板界面
     *
     * @param status
     * @param isOwner
     */
    private void openBoardView(CNotifyWhiteboardStatus status, boolean isOwner) {
        MeetActivity.this.status = status;
        iv_change.setVisibility(View.VISIBLE);
        menu_meet_media.setWhiteBoardTxt(getString(R.string.title_notice6), false, isOwner);
        if (shareLocalPopupWindow != null && isOwner) {
            shareLocalPopupWindow.changeStatue(getString(R.string.meet_close_whiteboard));
        }

        hideMenu();

        if (mMeetBoardFragment != null)
            mMeetBoardFragment.openWhiteBoard(false, null, false);

        changeFragment(mMeetBoardFragment);
    }

    /**
     * 关闭白板界面
     *
     * @param isOwner
     */
    private void closeBoardView(boolean isOwner) {
        MeetActivity.this.status = null;
        iv_change.setVisibility(View.GONE);
        exitWhiteBoardDeal();
        menu_meet_media.setWhiteBoardTxt(getString(R.string.title_notice6), true, isOwner);
        if (shareLocalPopupWindow != null) {
            shareLocalPopupWindow.changeStatue(getString(R.string.meet_open_whiteboard));
        }
    }

    /**
     * 退出会议
     *
     * @param isFinish
     */
    public void quitMeet(boolean isFinish) {
        isClickClose = true;

        if (isWatch) {
            quitWatch();
        }
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
            HYPlayer.Config.getRealPlayConfig().setPlayerCallID(-1);
            EncryptUtil.endEncrypt(sessionRsp.m_nCallId);
        }
    }

    private void quitWatch() {
        HYClient.getHYPlayer().stopPlayEx(null, texture_video);
        finish();
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

        //观摩态，自己邀请自己，同时是这个会议的时候，直接进入
        if (isWatch
                && (data.strInviteUserTokenID.equals(AppDatas.Auth().getData("tokenId")) || data.strInviteUserTokenID.equals(AppDatas.Auth().getUserLoginName()))
                && nMeetID == data.nMeetingID) {
            AppMessages.get().del(millis);
            closeBoardView(false);

            HYClient.getHYPlayer().stopPlayEx(new SdkCallback<VideoParams>() {
                @Override
                public void onSuccess(VideoParams params) {
                    changeCurrentMeet(temp);
                }

                @Override
                public void onError(ErrorInfo errorInfo) {

                }
            }, texture_video);

            return;
        }
        // 会议中来会议邀请，对话框提示
        dialog = getLogicDialog()
                .setTitleText(getString(R.string.common_notice20))
                .setMessageText(data.strInviteUserName + getString(R.string.common_notice21))
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

                        if (isWatch) {
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
                        }

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
     * 改变不同会议
     *
     * @param temp
     */
    private void changeCurrentMeet(CNotifyInviteUserJoinMeeting temp) {
        menu_meet_media_top.isRecord(false);
        isMeetStarter = temp.isSelfMeetCreator();
        isWatch = false;
        strMeetDomainCode = temp.strMeetingDomainCode;
        nMeetID = temp.nMeetingID;
        mMediaMode = temp.getRequiredMediaMode();
        strInviteUserId = temp.strInviteUserId;
        strInviteUserDomainCode = temp.strInviteUserDomainCode;

        startJoineMeet();
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
                .setTitleText(getString(R.string.common_notice20))
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
                                            intent = new Intent(MeetActivity.this, TalkVoiceActivity.class);
                                        } else {
                                            intent = new Intent(MeetActivity.this, TalkActivity.class);
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
                                        showToast(getString(R.string.common_notice23));
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
     * 开启白本
     */
    public void startWhiteBoard(final int type, final int choose) {
        HYClient.getModule(ApiMeet.class)
                .startWhiteBoard(SdkParamsCenter.Meet.StartWhiteBoard()
                                .setnMeetingID(nMeetID)
                                .setnShareType(type),
                        new SdkCallback<CStartWhiteboardShareRsp>() {
                            @Override
                            public void onSuccess(CStartWhiteboardShareRsp cStartWhiteboardShareRsp) {
                                if (type == 0) {
                                    return;
                                }
                                startFileAndPhoto(choose);
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                showToast(ErrorMsg.getMsgWhiterBoard(errorInfo.getCode()));
                                if (errorInfo.getCode() == ErrorMsg.white_board_has_exist_code) {
                                    startFileAndPhoto(choose);
                                }
                            }
                        });
    }

    private void startFileAndPhoto(int choose) {
        if (choose == 1) {
            Intent intent = new Intent(MeetActivity.this, ChooseFilesActivity.class);
            intent.putExtra("nMeetID", nMeetID);
            startActivityForResult(intent, 1001);
        } else {
            Intent intent = new Intent(MeetActivity.this, ChoosePhotoActivity.class);
            intent.putExtra("nMeetID", nMeetID);
            startActivityForResult(intent, 1001);
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

    @Override
    protected void onResume() {
        super.onResume();
        HYClient.getHYCapture().onCaptureFront();
        HYClient.getHYPlayer().onPlayFront();
        rxUtils.doDelay(200, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                ToggleBackgroundState(false);
            }
        }, "toggle");
    }

    @OnClick({R.id.iv_change})
    public void onClickChange(View view) {
        if (view.getId() == R.id.iv_change) {

            if (mMeetBoardFragment.isVisible()) {
                hideAll();
            } else {
                changeFragment(mMeetBoardFragment);
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        HYClient.getHYCapture().onCaptureBackground();
        HYClient.getHYPlayer().onPlayBackground();
        ToggleBackgroundState(true);
    }

    public void ToggleBackgroundState(boolean isShow) {
        try {
            JniIntf.SetCapturerPreviewTexture(isShow ? null
                    : texture_preview.getSurfaceTexture());
        } catch (Exception e) {
        }
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
                    .hide(mMeetBoardFragment)
                    .commit();
        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        if (mMeetMembersFragment.isVisible() ||
                mMeetLayoutFragment.isVisible() ||
                mMeetBoardFragment.isVisible()) {
            hideAll();
            return;
        }
        final LogicDialog dialog = getLogicDialog().setMessageText(isMeetStarter ? getString(R.string.common_notice24) : getString(R.string.common_notice25));
        if (isMeetStarter) {
            dialog.setConfirmText(getString(R.string.quite));
            dialog.setCancelText(getString(R.string.jiesan));
        } else {
            dialog.setConfirmText(getString(R.string.quite));
            dialog.setCancelText(getString(R.string.cancel));
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PhoneStatus status) {
        if (status.isBusy) {
            hasBusy = true;
            menu_meet_media.closeVoice();
        } else {
            menu_meet_media.reSetVoice();

            if (hasBusy) {
                hasBusy = false;
                if (rxUtils != null) {
                    rxUtils.doDelayOn(300, new RxUtils.IMainDelay() {
                        @Override
                        public void onMainDelay() {
                            stopSpeakerLound();
                            startSpeakerLound();
                        }
                    });
                }
            }

        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                onInviteClicked();
            } else {
                Upload upload = (Upload) data.getSerializableExtra("updata");
                HYClient.getModule(ApiIO.class)
                        .startFileConvert(SdkParamsCenter.IO.StartFileConvert()
                                        .setStrFilePath(upload.file1_name),
                                new CallbackStartFileConvert() {
                                    @Override
                                    public void startResult(CStartFileConvertRsp cStartFileConvertRsp) {
                                        if (cStartFileConvertRsp.nResultCode != 0) {
                                            status = null;
                                            showToast(getString(R.string.common_notice26));
                                            exitWhiteBoardDeal();
                                        }
                                    }

                                    @Override
                                    public void convertResult(NotifyFileConvertStatus notifyFileConvertStatus) {
                                        if (notifyFileConvertStatus.isConvertSuccess()) {
                                            if (mMeetBoardFragment != null)
                                                mMeetBoardFragment.openWhiteBoard(true, notifyFileConvertStatus, true);
                                        } else {
                                            status = null;
                                            showToast(getString(R.string.common_notice26));
                                            exitWhiteBoardDeal();
                                        }
                                    }

                                    @Override
                                    public void onSuccess(Object o) {

                                    }

                                    @Override
                                    public void onError(ErrorInfo errorInfo) {
                                        exitWhiteBoardDeal();
                                    }
                                });
            }
        }
    }

    private void exitWhiteBoardDeal() {
        if (mMeetBoardFragment != null) {
            mMeetBoardFragment.exiteWhiteBoard();
        }
    }

    /**
     * 邀请返回
     */
    void onInviteClicked() {
        count = 0;
        cuntCount = 0;
        cuntCountSuccess = 0;

        ArrayList<CStartMeetingReq.UserInfo> users = ChoosedContacts.get().convertContacts(ChoosedContacts.get().getContacts(false));
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
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                finish();
                return;
            }
            realInvisitor(ChoosedContacts.get().convertContacts(ChoosedContacts.get().getContacts(false)));
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
                                showToast(getString(R.string.meet_yiyaoqing));
                            }
                        } else {
                            if(nEncryptIMEnable) {
                                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                finish();
                                return;
                            }
                            showToast(getString(R.string.meet_yiyaoqing));
                        }
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                            cuntCount++;
                            if (count == cuntCount && cuntCountSuccess > 0) {
                                showToast(getString(R.string.meet_yiyaoqing));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!isClickClose) {
            quitWatch();
        }

        clearContacts();

        rxUtils.clearAll();

        destruct();

        stopSpeakerLound();

        HYClient.getHYCapture().setCameraConferenceMode(HYCapture.CameraConferenceMode.PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        EventBus.getDefault().unregister(this);
//        HYClient.getHYCapture().stopCapture(null);
//        mMeetInviteFragment = null;
        currentFragment = null;
        mMeetMembersFragment = null;
        mMeetLayoutFragment = null;
        mMeetBoardFragment = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.releaseInstance();
        }

    }

    /**
     * 清除人员
     */
    private void clearContacts() {
        ChoosedContacts.get().clear();
        ChoosedContacts.get().clearMeetUsers();
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
                mMeetBoardFragment.notiyUpdate(info);
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
