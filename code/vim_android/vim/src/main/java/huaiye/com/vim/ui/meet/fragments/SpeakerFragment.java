package huaiye.com.vim.ui.meet.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huaiye.cmf.AVPlayerParameters;
import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.msg.SdkMsgNotifyStreamStatus;
import com.huaiye.sdk.media.player.sdk.mix.VideoCallbackWrapper;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.huaiye.sdk.media.player.sdk.params.user.UserReal;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.meet.ParamsUpdateMicStatus;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CMeetingUserRaiseRsp;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingStatusInfo;
import com.huaiye.sdk.sdpmsgs.meet.CUpdateMicStatusRsp;
import com.huaiye.sdk.sdpmsgs.whiteboard.CNotifyWhiteboardStatus;
import com.huaiye.sdk.sdpmsgs.whiteboard.CStartWhiteboardShareRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppBaseFragment;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.common.constant.SPConstant;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.models.MeetSpeakMain;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.ContactsApi;
import huaiye.com.vim.models.contacts.bean.ContactsBean;
import huaiye.com.vim.ui.contacts.ContactsChoiceByAllFriendActivity;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContactsNew;
import huaiye.com.vim.ui.contacts.sharedata.ConvertContacts;
import huaiye.com.vim.ui.meet.ChooseFilesActivity;
import huaiye.com.vim.ui.meet.ChoosePhotoActivity;
import huaiye.com.vim.ui.meet.MeetControlActivity;
import huaiye.com.vim.ui.meet.MeetNewActivity;
import huaiye.com.vim.ui.meet.ShareLocalPopupWindow;
import huaiye.com.vim.ui.meet.views.MeetMediaMenuTopView;
import huaiye.com.vim.ui.meet.views.MeetMediaMenuView;
import huaiye.com.vim.ui.meet.views.MorePopupWindow;

import static huaiye.com.vim.common.AppUtils.STRING_KEY_capture;
import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * Created by ywt on 2019/3/4.
 */
@BindLayout(R.layout.activity_meet)
public class SpeakerFragment extends AppBaseFragment {
    private final int AUTO_HIDE = 100;
    /*@BindView(R.id.layer_gesture)
    GesturedTextureLayer layer_gesture;*/
    @BindView(R.id.meet_user_name)
    TextView meet_user_name;
    @BindView(R.id.meet_identity)
    TextView meet_identity;
    @BindView(R.id.meet_switch)
    ImageView meet_switch;
    @BindView(R.id.video_layout)
    FrameLayout video_layout;
    @BindView(R.id.texture_video)
    TextureView texture_video;
    //    @BindView(R.id.texture_preview)
//    TextureView texture_preview;
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
    @BindView(R.id.msg_layout)
    LinearLayout msg_layout;
    @BindView(R.id.msg_user)
    TextView msg_user;
    @BindView(R.id.msg_content)
    TextView msg_content;
    @BindView(R.id.texture_self_video)
    TextureView texture_self_video;
    @BindView(R.id.video_close_layout)
    LinearLayout video_close_layout;
    @BindView(R.id.meet_member_name)
    TextView meet_member_name;
    @BindView(R.id.meet_head_img)
    ImageView meet_head_img;

    // 是否是推送模式
    boolean isInPicturePushMode;
    private ShareLocalPopupWindow shareLocalPopupWindow;
    private MorePopupWindow mMorePopupWindow;
    private CNotifyWhiteboardStatus whiteboardStatus;
    private CGetMeetingInfoRsp mCGetMeetingInfoRsp;
    private ArrayList<CGetMeetingInfoRsp.UserInfo> mUserList;
    /**
     * 主讲人是自己是才适用
     */
    private ArrayList<String> mRaiseUsers = new ArrayList<>();
    private boolean isCloseVideo;//true---音频入会
    boolean preViewVideo;
    boolean otherViewVideo;


    private VideoParams mainSpeakerParams;
    /**
     * 根据服务器的信息找到适合的主讲人
     */
    private MeetSpeakMain mMainSpeaker;

    /**
     * 服务器端的主讲人tokenID
     */
    private String strKeynoteSpeakerTokenID;


    /**
     * 延迟初始化相关
     */
    private boolean isResumed = false;
    private boolean isSetVisible = false;

    private MediaProjectionManager mProjectionManager;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            menu_meet_media.setVisibility(View.GONE);
            menu_meet_media_top.setVisibility(View.GONE);
            if (mMorePopupWindow != null) {
                mMorePopupWindow.dismiss();
            }
        }
    };

    @Nullable
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getNavigate().setVisibility(View.GONE);
        isCloseVideo = ((MeetNewActivity) getActivity()).isCloseVideo();
        preViewVideo = !((MeetNewActivity) getActivity()).isCloseVideo();
        otherViewVideo = false;
        if (menu_meet_media != null) {
//            menu_meet_media.setVideoEnable(isCloseVideo);
            menu_meet_media.hideMasterView(((MeetNewActivity) getActivity()).isMeetStarter());
            menu_meet_media_top.showLeft(((MeetNewActivity) getActivity()).isWatch());
            if (((MeetNewActivity) getActivity()).closeInvisitor()) menu_meet_media.hideInvisitor();
        }
        shareLocalPopupWindow = new ShareLocalPopupWindow(getActivity());
        shareLocalPopupWindow.setConfirmClickListener(mConfirmClickListener);
        shareLocalPopupWindow.init();
        mMorePopupWindow = new MorePopupWindow(getActivity());
        mMorePopupWindow.setMoreItemClickListener(mMoreItemClickListener);
//        ViewGroup.LayoutParams params = texture_video.getLayoutParams();
//        mBigVideoWidth = params.width;
//        mBigVideoHeight = params.height;
        addListeners();
        Log.d("test", "SpeakerFragment onViewCreated");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeMessages(AUTO_HIDE);
        stopPlay();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isSetVisible = isVisibleToUser;
        changeUserPreview(isSetVisible);
        refresh();
    }

    private void changeUserPreview(boolean isSetVisible) {
        if (isSetVisible) {
            if (mMainSpeaker != null) {
                if (mMainSpeaker.getStrUserID().equals(AppAuth.get().getUserID())) {
                    if (preViewVideo) {
                        HYClient.getHYCapture().setPreviewWindow(texture_video);
                    } else {
                        HYClient.getHYCapture().setPreviewWindow(null);
                    }
                } else {
                    if (preViewVideo) {
                        texture_self_video.setVisibility(View.VISIBLE);
                        ViewGroup.LayoutParams params = texture_self_video.getLayoutParams();
                        calcCaptureViewSize(texture_self_video, params.width);
                        HYClient.getHYCapture().setPreviewWindow(texture_self_video);
                    } else {
                        texture_self_video.setVisibility(View.GONE);
                        HYClient.getHYCapture().setPreviewWindow(null);
                    }
                }
            }
        } else {
            HYClient.getHYCapture().setPreviewWindow(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("test", "SpeakerFragment onResume");
        isResumed = true;
        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("test", "SpeakerFragment onPause");
        isResumed = false;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //水平布局
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int heigth = dm.heightPixels;
            int width = dm.widthPixels;
            Log.d(getClass().getName(), "LANDSCAPE heigth=" + heigth);
            Log.d(getClass().getName(), "LANDSCAPE width=" + width);
            if (mMainSpeaker != null && mMainSpeaker.getStrUserID().equals(AppAuth.get().getUserID() + "")) {
                Log.d(getClass().getName(), "onConfigurationChanged resize main speaker");
                calcCaptrueViewWidth(texture_video, heigth);
            } else {
                Log.d(getClass().getName(), "onConfigurationChanged resize self");
                ViewGroup.LayoutParams params = texture_self_video.getLayoutParams();
                int tempWidth = params.height;
                params.height = params.width;
                params.width = tempWidth;
                texture_self_video.setLayoutParams(params);
            }
        } else {
            //垂直布局
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int heigth = dm.heightPixels;
            int width = dm.widthPixels;
            Log.d(getClass().getName(), "PORTRAIT heigth=" + heigth);
            Log.d(getClass().getName(), "PORTRAIT width=" + width);
            if (mMainSpeaker != null && mMainSpeaker.getStrUserID().equals(AppAuth.get().getUserID() + "")) {
                calcCaptureViewSize(texture_video, width);
            } else {
                ViewGroup.LayoutParams params = texture_self_video.getLayoutParams();
                int tempWidth = params.height;
                params.height = params.width;
                params.width = tempWidth;
                texture_self_video.setLayoutParams(params);
            }
        }
    }


    /**
     * 延迟加载后,第一次初始化
     */
    private void refresh() {
        if (isResumed && isSetVisible) {
            startPlay();
        } else {
            stopPlay();
        }
    }


    public synchronized void setMeetingInfo(CGetMeetingInfoRsp getMeetingInfoRsp, ArrayList<CGetMeetingInfoRsp.UserInfo> list) {
        if (menu_meet_media_top != null && getMeetingInfoRsp.nMeetingID > 0) {
            menu_meet_media_top.showName(String.valueOf(getMeetingInfoRsp.nMeetingID));
        }
        mCGetMeetingInfoRsp = getMeetingInfoRsp;
        mUserList = list;
        deleteLoseRaiseUsers();
        refresh();
    }

//    private void stopLosePlayer() {
//        Iterator map1it = mVoicePlayers.entrySet().iterator();
//        while (map1it.hasNext()) {
//            Map.Entry<String, UserReal> entry = (Map.Entry<String, UserReal>) map1it.next();
//            boolean find = false;
//            for (CGetMeetingInfoRsp.UserInfo user : mUserList) {
//                if (entry.getKey().equals(user.strUserID)) {
//                    find = true;
//                    break;
//                }
//            }
//            if (!find && !entry.getKey().equals(mCurrentBigVideoUserId)) {
//                //删除已退出会议的音频播放器，大画面播放不在这里停止,在startPlay中停止
//                HYClient.getHYPlayer().stopPlay(null, entry.getValue());
//            }
//        }
//    }

    private void deleteLoseRaiseUsers() {
        if (mRaiseUsers == null || mRaiseUsers.size() <= 0) {
            return;
        }
        ArrayList<String> tempUsers = new ArrayList<>();
        for (String userId : mRaiseUsers) {
            boolean find = false;
            for (CGetMeetingInfoRsp.UserInfo user : mUserList) {
                if (userId.equals(user.strUserID)) {
                    find = true;
                    break;
                }
            }
            if (!find) {
                tempUsers.add(userId);
            }
        }
        if (tempUsers.size() > 0) {
            mRaiseUsers.removeAll(tempUsers);
        }
    }


    private MeetSpeakMain choiceMainSpeaker(CGetMeetingInfoRsp resp) {
        MeetSpeakMain meetSpeakMain = null;
        CGetMeetingInfoRsp.UserInfo mainSpeaker = null;
        if (TextUtils.isEmpty(resp.strKeynoteSpeakerTokenID)) {
            meetSpeakMain = chooseMainSpeakFromList(resp);
        } else {
            for (int i = 0; i < resp.listUser.size(); i++) {
                if (resp.strKeynoteSpeakerTokenID.equals(resp.listUser.get(i).strUserTokenID)) {
                    mainSpeaker = resp.listUser.get(i);
                    break;
                }
            }
            //如果没找到,说明即主讲人已经不在当前会议中了
            if (mainSpeaker == null) {
                meetSpeakMain = chooseMainSpeakFromList(resp);
            } else {
                this.strKeynoteSpeakerTokenID = resp.strKeynoteSpeakerTokenID;
                meetSpeakMain = new MeetSpeakMain(mainSpeaker, true);

            }
        }
        return meetSpeakMain;
    }


    /**
     * 根据会议信息找到合适的主讲人
     * 1 有主讲人就用主讲人
     * 2 没有主讲人就用列表里的非自己的第一个
     *
     * @param resp
     * @return
     */
    private MeetSpeakMain choiceMainSpeaker(CNotifyMeetingStatusInfo resp) {
        MeetSpeakMain meetSpeakMain;
        CNotifyMeetingStatusInfo.User mainSpeaker = null;
        if (TextUtils.isEmpty(resp.strKeynoteSpeakerTokenID)) {
            meetSpeakMain = chooseMainSpeakFromList(resp);
        } else {
            for (int i = 0; i < resp.lstMeetingUser.size(); i++) {
                CNotifyMeetingStatusInfo.User user = resp.lstMeetingUser.get(i);
                if ((user.nPartType == 0 || user.nPartType == 2) && resp.strKeynoteSpeakerTokenID.equals(user.strUserTokenID)) {
                    mainSpeaker = resp.lstMeetingUser.get(i);
                    break;
                }
            }
            if (mainSpeaker == null) {
                meetSpeakMain = chooseMainSpeakFromList(resp);
            } else {
                this.strKeynoteSpeakerTokenID = resp.strKeynoteSpeakerTokenID;
                meetSpeakMain = new MeetSpeakMain(mainSpeaker, true);
            }
        }

        return meetSpeakMain;

    }


    private MeetSpeakMain chooseMainSpeakFromList(CGetMeetingInfoRsp resp) {
        MeetSpeakMain meetSpeakMain = null;
        CGetMeetingInfoRsp.UserInfo mainSpeaker = null;
        ArrayList<CGetMeetingInfoRsp.UserInfo> onlineUser = new ArrayList<>();
        CGetMeetingInfoRsp.UserInfo mySelf = null;
        for (int i = 0; i < resp.listUser.size(); i++) {
            if (resp.listUser.get(i).nJoinStatus == 2) {
                if (resp.listUser.get(i).strUserID.equals(AppAuth.get().getUserID() + "")) {
                    mySelf = resp.listUser.get(i);
                } else {
                    onlineUser.add(resp.listUser.get(i));
                }
            }
        }
        if (onlineUser.size() > 0) {
            mainSpeaker = onlineUser.get(0);
        } else {
            mainSpeaker = mySelf;
        }
        meetSpeakMain = new MeetSpeakMain(mainSpeaker, false);
        return meetSpeakMain;
    }

    /**
     * 从列表中选择主播放的选择逻辑,除了自己的第一个
     *
     * @param resp
     * @return
     */
    private MeetSpeakMain chooseMainSpeakFromList(CNotifyMeetingStatusInfo resp) {
        MeetSpeakMain meetSpeakMain = null;
        CNotifyMeetingStatusInfo.User mainSpeaker = null;
        ArrayList<CNotifyMeetingStatusInfo.User> onlineUser = new ArrayList<>();
        CNotifyMeetingStatusInfo.User mySelf = null;
        for (int i = 0; i < resp.lstMeetingUser.size(); i++) {
            if (resp.lstMeetingUser.get(i).nPartType == 2
                    || resp.lstMeetingUser.get(i).nPartType == 0) {
                if (resp.lstMeetingUser.get(i).strUserID.equals(AppAuth.get().getUserID())) {
                    mySelf = resp.lstMeetingUser.get(i);
                } else {
                    onlineUser.add(resp.lstMeetingUser.get(i));
                }
            }
        }
        if (onlineUser.size() > 0) {
            mainSpeaker = onlineUser.get(0);
        } else {
            mainSpeaker = mySelf;
        }
        meetSpeakMain = new MeetSpeakMain(mainSpeaker, false);
        return meetSpeakMain;
    }

    private void performPlayMain(MeetSpeakMain willPlayMainSpeak, MeetSpeakMain currentMainPlayer, ArrayList<CGetMeetingInfoRsp.UserInfo> listUser) {
        playMainSpeaker(willPlayMainSpeak, currentMainPlayer);
        mMainSpeaker = willPlayMainSpeak;
    }


    private void playMainSpeaker(final MeetSpeakMain mainSpeaker, MeetSpeakMain previousMainSpeak) {
        if (mainSpeaker == null || TextUtils.isEmpty(mainSpeaker.getStrUserID())) {
            return;
        }

        //当前的主讲人和之前的一样,就不需要干什么了
        if (previousMainSpeak != null && mainSpeaker.getStrUserID().equals(previousMainSpeak.getStrUserID())) {
            if(previousMainSpeak.getStrUserID().equals(AppAuth.get().getUserID())) {
                if (preViewVideo) {
                    video_close_layout.setVisibility(View.GONE);
                } else {
                    video_close_layout.setVisibility(View.VISIBLE);
                }
            } else {
                if (otherViewVideo) {
                    video_close_layout.setVisibility(View.GONE);
                } else {
                    video_close_layout.setVisibility(View.VISIBLE);
                }
            }
            return;
        }

        //之前的主讲人是自己,停止预览准备播放新的主讲人
        if (previousMainSpeak != null && previousMainSpeak.getStrUserID().equals(AppAuth.get().getUserID())) {
            HYClient.getHYCapture().setPreviewWindow(null);
            previousMainSpeak = null;
        }

        //之前有主讲人,且不相同就先停止播放之前的,然后再播放新的
        if (previousMainSpeak != null && !mainSpeaker.getStrUserID().equals(previousMainSpeak.getStrUserID())) {
            if(!previousMainSpeak.getStrUserID().equals(AppAuth.get().getUserID())) {
                if(preViewVideo) {
                    HYClient.getHYCapture().setPreviewWindow(null);
                }
            }
            HYClient.getHYPlayer().stopPlayEx(new SdkCallback<VideoParams>() {
                @Override
                public void onSuccess(VideoParams videoParams) {
                    realPlayMainSpeaker(mainSpeaker);
                }

                @Override
                public void onError(ErrorInfo errorInfo) {

                }
            }, texture_video);
            return;
        }
        realPlayMainSpeaker(mainSpeaker);
    }


    /**
     * 执行播放主讲人操作
     *
     * @param mainSpeaker
     */
    private void realPlayMainSpeaker(MeetSpeakMain mainSpeaker) {
        if (mainSpeaker == null || TextUtils.isEmpty(mainSpeaker.getStrUserID())) {
            return;
        }
        if (mainSpeaker.isNeedShowDesc()) {
            meet_identity.setVisibility(View.VISIBLE);
            meet_identity.setText(mainSpeaker.getDesc());
        } else {
            meet_identity.setVisibility(View.GONE);
        }
        meet_user_name.setText(mainSpeaker.getStrUserName());
        if (mainSpeaker.getStrUserID().equals("" + AppAuth.get().getUserID())) {

            calcCaptureViewSize(texture_video, AppUtils.getScreenWidth());
            if (preViewVideo) {
                video_close_layout.setVisibility(View.GONE);
            } else {
                video_close_layout.setVisibility(View.VISIBLE);
            }

            HYClient.getHYCapture().setPreviewWindow(texture_video);
            return;
        }
        final UserReal videoParams = Player.Params.TypeUserReal();
        video_close_layout.setVisibility(View.VISIBLE);

        videoParams.setUserDomainCode(mainSpeaker.getStrUserDomainCode())
                .setUserTokenID(mainSpeaker.getStrUserTokenID())
                .setPreview(texture_video)
                .setMixCallback(new VideoCallbackWrapper() {
                    @Override
                    public void onVideoStatusChanged(VideoParams param, SdpMessageBase msg) {
                        super.onVideoStatusChanged(param, msg);
                        if (msg instanceof SdkMsgNotifyStreamStatus) {
                            SdkMsgNotifyStreamStatus streamStatus = (SdkMsgNotifyStreamStatus) msg;
                            MessageEvent nMessageEvent = new MessageEvent(AppUtils.EVENT_MESSAGE_CHANGE_VIDEO_STATE_OTHERS);
                            nMessageEvent.argStr0 = mainSpeaker.getStrUserID();
                            nMessageEvent.obj1 = !streamStatus.isVideoOn;
                            EventBus.getDefault().post(nMessageEvent);
                            otherViewVideo = streamStatus.isVideoOn;
                            if (streamStatus.isVideoOn) {
                                texture_video.setVisibility(View.VISIBLE);
                                video_close_layout.setVisibility(View.GONE);
                            } else {
                                video_close_layout.setVisibility(View.VISIBLE);
                            }

                        }
                    }

                    @Override
                    public void onSuccess(VideoParams param) {
                        Logger.log("setMixCallback---onSuccess");
                    }

                    @Override
                    public void onError(VideoParams param, SdkCallback.ErrorInfo errorInfo) {
                        Logger.log("setMixCallback---onError");
                    }

                });
        //这里只播放画面,声音在MeetMemberNewFragment里播放
        videoParams.setAudioOn(false);
        videoParams.getHYPlayerConfig().setTranscode(false);//按照服务器是否开启降码
        videoParams.getHYPlayerConfig().setTranscodeScaleSize(AVPlayerParameters.TRANSCODING_SCALE_HALF);
        mainSpeakerParams = videoParams;

        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            videoParams.getHYPlayerConfig().setPlayerCallID(MeetNewActivity.sessionRsp.m_nCallId);
        }
        HYClient.getHYPlayer().startPlay(videoParams);
    }


    private void startPlay() {
        if (mCGetMeetingInfoRsp == null) {
            return;
        }
        MeetSpeakMain mainSpeaker = choiceMainSpeaker(mCGetMeetingInfoRsp);


        performPlayMain(mainSpeaker, mMainSpeaker, mCGetMeetingInfoRsp.listUser);

        showPreView(mainSpeaker);

        initHeadView(mainSpeaker);


    }

    private void showPreView(MeetSpeakMain mainSpeaker) {
        if (!mainSpeaker.getStrUserID().equals(AppAuth.get().getUserID() + "")) {
            if(preViewVideo) {
                texture_self_video.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = texture_self_video.getLayoutParams();
                calcCaptureViewSize(texture_self_video, params.width);
                if(HYClient.getHYCapture().getPreviewWindow() != texture_self_video) {
                    HYClient.getHYCapture().setPreviewWindow(texture_self_video);
                }
            } else {
                texture_self_video.setVisibility(View.GONE);
                HYClient.getHYCapture().setPreviewWindow(null);
            }
        } else {
            if(preViewVideo) {
                HYClient.getHYCapture().setPreviewWindow(texture_video);
            } else {
                //主讲人和自己相同的话 ,播放的时候做好了,这边什么都不需要做了
                texture_self_video.setVisibility(View.GONE);
            }
        }
    }

    private void initHeadView(MeetSpeakMain mainSpeaker) {
        if (null == mainSpeaker) {
            return;
        }

        String headPic;
        if (!mainSpeaker.getStrUserID().equals(AppAuth.get().getUserID() + "")) {
            headPic = AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(mainSpeaker.getStrUserID(), mainSpeaker.getStrUserDomainCode());
        } else {
            headPic = AppDatas.Auth().getHeadUrl(AppDatas.Auth().getUserID() + SPConstant.STR_HEAD_URL);
        }

        if (!TextUtils.isEmpty(headPic)) {
            Glide.with(getContext())
                    .load(AppDatas.Constants().getAddressWithoutPort() + headPic)
                    .apply(((MeetNewActivity) getActivity()).getRequestOptions())
                    .into(meet_head_img);
        } else {
            String mapKey = mainSpeaker.getStrUserDomainCode();
            List<String> mapValue = new ArrayList<>();
            mapValue.add(mainSpeaker.getStrUserID());
            ContactsApi.get().requestUserInfoList(mapKey, mapValue, new ModelCallback<ContactsBean>() {
                @Override
                public void onSuccess(ContactsBean contactsBean) {

                    if (null != contactsBean && null != contactsBean.userList && contactsBean.userList.size() == 1) {
                        AppDatas.MsgDB().getFriendListDao().insertAll(contactsBean.userList);
                        String headPic = AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(mainSpeaker.getStrUserID(), mainSpeaker.getStrUserDomainCode());
                        Glide.with(getContext())
                                .load(AppDatas.Constants().getAddressWithoutPort() + headPic)
                                .apply(((MeetNewActivity) getActivity()).getRequestOptions())
                                .into(meet_head_img);
                    }
                }
            });
        }

    }


    private void calcCaptureViewSize(TextureView textureView, int width) {
        if (textureView == null) {
            return;
        }
        int height = width * 4 / 3;
        switch (SP.getString(STRING_KEY_capture)) {
            case AppUtils.STRING_KEY_VGA:
                height = width * 4 / 3;
                break;
            case AppUtils.STRING_KEY_HD720P:
            case AppUtils.STRING_KEY_HD1080P:
                height = width * 16 / 9;
                break;
        }
        ViewGroup.LayoutParams params = textureView.getLayoutParams();
        params.width = width;
        params.height = height;
        textureView.setLayoutParams(params);
    }

    private void calcCaptrueViewWidth(TextureView textureView, int height) {
        if (textureView == null) {
            return;
        }
        int width = height * 4 / 3;
        switch (SP.getString(STRING_KEY_capture)) {
            case AppUtils.STRING_KEY_VGA:
                width = height * 4 / 3;
                break;
            case AppUtils.STRING_KEY_HD720P:
            case AppUtils.STRING_KEY_HD1080P:
                width = height * 16 / 9;
                break;
        }
        ViewGroup.LayoutParams params = textureView.getLayoutParams();
        params.width = width;
        params.height = height;
        textureView.setLayoutParams(params);
    }

    private void stopPlay() {
        Log.d("VIMApp", "SpeakerFragment stopPlay");
        if (mMainSpeaker != null) {
            if (!mMainSpeaker.getStrUserID().equals(AppAuth.get().getUserID() + "")) {
                HYClient.getHYPlayer().stopPlay(new SdkCallback<VideoParams>() {
                    @Override
                    public void onSuccess(VideoParams videoParams) {
                        Log.d("VIMApp", "SpeakerFragment stop video success");
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        Log.d("VIMApp", "SpeakerFragment stop video failed");
                    }
                }, mainSpeakerParams);
            }
        }
        mMainSpeaker = null;


    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.video_close_layout:
                case R.id.video_layout:
                case R.id.texture_video:
                    if (isInPicturePushMode) {
                        isInPicturePushMode = false;
                    } else {
                        // 显示菜单
                        if (menu_meet_media.getVisibility() == View.VISIBLE) {
                            mHandler.removeMessages(AUTO_HIDE);
                            hideMenu();
                        } else {
                            mHandler.removeMessages(AUTO_HIDE);
                            mHandler.sendEmptyMessageDelayed(AUTO_HIDE, 5000);
                            menu_meet_media.setVisibility(View.VISIBLE);
                            menu_meet_media_top.setVisibility(View.VISIBLE);
                            /*rxUtils.doDelay(5000, new RxUtils.IMainDelay() {
                                @Override
                                public void onMainDelay() {
                                    if ((shareLocalPopupWindow != null && shareLocalPopupWindow.isShowing())
                                            || (mMorePopupWindow != null && mMorePopupWindow.isShowing())) {
                                        return;
                                    }
                                    menu_meet_media.setVisibility(View.GONE);
                                    menu_meet_media_top.setVisibility(View.GONE);
                                }
                            }, "hide");*/
                        }
                    }
                    break;
                case R.id.meet_switch:
                    HYClient.getHYCapture().toggleInnerCamera();
                    break;
                default:
                    break;
            }
        }
    };

    private void addListeners() {
        video_close_layout.setOnClickListener(mOnClickListener);
        video_layout.setOnClickListener(mOnClickListener);
        texture_video.setOnClickListener(mOnClickListener);
        meet_switch.setOnClickListener(mOnClickListener);

        menu_meet_media_top.setCallback(new MeetMediaMenuTopView.Callback() {
            @Override
            public void onMeetExitClicked() {
                ((MeetNewActivity) getActivity()).onBackPressed();
            }
        });
        menu_meet_media.setCallback(new MeetMediaMenuView.Callback() {

            @Override
            public void onYuLanClick() {
                /*if (texture_preview.getVisibility() == View.INVISIBLE) {
                    texture_preview.setVisibility(View.VISIBLE);
                    HYClient.getHYCapture().setPreviewWindow(texture_preview);
                } else if (texture_preview.getVisibility() == View.GONE) {
                    texture_preview.setVisibility(View.VISIBLE);
                } else {
                    texture_preview.setVisibility(View.GONE);
                }*/
            }

            @Override
            public void onMeetExitClicked() {
                // 退出会议
                ((MeetNewActivity) getActivity()).onBackPressed();
            }

            @Override
            public void onMemberListClicked() {
                // 人员列表
                hideMenu();

//                changeFragment(mMeetMembersFragment);
            }

            @Override
            public void onMeetInviteClicked() {
                // 会议邀请
                HYClient.getModule(ApiMeet.class).requestMeetDetail(
                        SdkParamsCenter.Meet.RequestMeetDetail()
                                .setMeetDomainCode(((MeetNewActivity) getActivity()).strMeetDomainCode)
                                .setnListMode(1)
                                .setMeetID(((MeetNewActivity) getActivity()).nMeetID), new SdkCallback<CGetMeetingInfoRsp>() {
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
                                                ChoosedContactsNew.get().clear();
                                                ChoosedContactsNew.get().setContacts(ConvertContacts.ConvertMeetUserInfoToContacts(data));
                                                Intent intent = new Intent(getActivity(), ContactsChoiceByAllFriendActivity.class);
                                                intent.putExtra("titleName", "邀请参会");
                                                getActivity().startActivityForResult(intent, 1000);
                                            }
                                        });
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                AppBaseActivity.showToast(ErrorMsg.getMsg(ErrorMsg.get_meet_info_err_code));
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
                preViewVideo = isVideoOpened;
                HYClient.getHYCapture().setCaptureVideoOn(isVideoOpened);
//                ((MeetNewActivity) getActivity()).setUserCloseVideo(isCloseVideo);
                MessageEvent nMessageEvent = new MessageEvent(AppUtils.EVENT_MESSAGE_CHANGE_VIDEO_STATE);
                nMessageEvent.obj1 = !isVideoOpened;
                EventBus.getDefault().post(nMessageEvent);
                if (mMainSpeaker != null) {
                    if (mMainSpeaker.getStrUserID().equals(AppAuth.get().getUserID() + "")) {
                        //主讲人是自己关闭摄像收不到回调，所以在这里处理图片展示
                        if (isVideoOpened) {
                            texture_video.setVisibility(View.VISIBLE);
                            video_close_layout.setVisibility(View.GONE);
                        } else {
                            texture_video.setVisibility(View.GONE);
                            video_close_layout.setVisibility(View.VISIBLE);
                            meet_member_name.setText(mMainSpeaker.getStrUserName());
                        }
                    } else {
                        if (isVideoOpened) {
                            texture_self_video.setVisibility(View.VISIBLE);
                            ViewGroup.LayoutParams params = texture_self_video.getLayoutParams();
                            calcCaptureViewSize(texture_self_video, params.width);
                            HYClient.getHYCapture().setPreviewWindow(texture_self_video);
                        } else {
                            texture_self_video.setVisibility(View.GONE);
                            HYClient.getHYCapture().setPreviewWindow(null);
                        }
                    }
                }
            }

            @Override
            public void showLayoutChange() {
                hideMenu();
                /*mMeetLayoutFragment.requestLayoutInfo();
                changeFragment(mMeetLayoutFragment);*/
            }

            @Override
            public void showSharePop(View view) {
                if (whiteboardStatus != null) {
                    toggleWhiteBoard();
                } else if (shareLocalPopupWindow != null) {
                    shareLocalPopupWindow.showView(view);
                }
            }

            @Override
            public void showMorePop(View view) {
                if (mMorePopupWindow != null) {
                    mMorePopupWindow.showView(view);
                }
            }

            @Override
            public void onHandUp() {
                HYClient.getModule(ApiMeet.class).raiseHandsInMeeting(
                        SdkParamsCenter.Meet.UserRaise()
                                .setStrMeetingDomainCode(((MeetNewActivity) getActivity()).strMeetDomainCode)
                                .setnMeetingID(((MeetNewActivity) getActivity()).nMeetID), new SdkCallback<CMeetingUserRaiseRsp>() {

                            @Override
                            public void onSuccess(CMeetingUserRaiseRsp info) {
                                AppBaseActivity.showToast("举手成功");
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                AppBaseActivity.showToast(ErrorMsg.getMsg(ErrorMsg.raise_hands_err_code));
                            }
                        });
            }

            @Override
            public void startRecordSuccess() {
                menu_meet_media_top.isRecord(true);
            }

            @Override
            public void onControlClick() {
                if (mCGetMeetingInfoRsp == null) {
                    return;
                }
            /*if (!mCGetMeetingInfoRsp.strMainUserID.equals(HYClient.getSdkOptions().User().getUserId())) {
                Toast.makeText(getActivity(), "只有主讲人才能发起会议控制", Toast.LENGTH_SHORT).show();
                return;
            }*/
                Intent intent = new Intent(getActivity(), MeetControlActivity.class);
                intent.putExtra("strMeetDomainCode", mCGetMeetingInfoRsp.strMainUserDomainCode);
                intent.putExtra("nMeetID", mCGetMeetingInfoRsp.nMeetingID);
                intent.putStringArrayListExtra("mRaiseUsers", mRaiseUsers);
                startActivity(intent);
                menu_meet_media.setVisibility(View.GONE);
                menu_meet_media_top.setVisibility(View.GONE);
            }

            @Override
            public void onCaptureVoiceClicked() {
                boolean isAudioOn = menu_meet_media.toggleCaptureAudio();
                ((MeetNewActivity) getActivity()).setUserCloseVoice(!isAudioOn);
                notifyMicState(isAudioOn);
            }
        });
    }

    /**
     * 打开关闭白板
     */
    private void toggleWhiteBoard() {
        if (whiteboardStatus == null) {
            startWhiteBoard(0, 0);
        } else if (whiteboardStatus.strInitiatorDomainCode.equals(HYClient.getSdkOptions().User().getDomainCode()) &&
                whiteboardStatus.strInitiatorTokenID.equals(HYClient.getSdkOptions().User().getUserTokenId())) {
            ((MeetNewActivity) getActivity()).stopWhiteBoard();
        } else {
            AppBaseActivity.showToast("你不是共享发起人，无权操作");
        }
    }

    /**
     * 开启白本
     */
    public void startWhiteBoard(final int type, final int choose) {
        HYClient.getModule(ApiMeet.class)
                .startWhiteBoard(SdkParamsCenter.Meet.StartWhiteBoard()
                                .setnMeetingID(((MeetNewActivity) getActivity()).nMeetID)
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
                                AppBaseActivity.showToast(ErrorMsg.getMsgWhiterBoard(errorInfo.getCode()));
                                if (errorInfo.getCode() == ErrorMsg.white_board_has_exist_code) {
                                    startFileAndPhoto(choose);
                                }
                            }
                        });
    }

    private void startFileAndPhoto(int choose) {
        if (choose == 1) {
            Intent intent = new Intent(getActivity(), ChooseFilesActivity.class);
            intent.putExtra("nMeetID", ((MeetNewActivity) getActivity()).nMeetID);
            startActivityForResult(intent, 1001);
        } else {
            Intent intent = new Intent(getActivity(), ChoosePhotoActivity.class);
            intent.putExtra("nMeetID", ((MeetNewActivity) getActivity()).nMeetID);
            startActivityForResult(intent, 1001);
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

    private ShareLocalPopupWindow.ConfirmClickListener mConfirmClickListener = new ShareLocalPopupWindow.ConfirmClickListener() {
        @Override
        public void onShareImg() {
            if (whiteboardStatus != null) {
                AppBaseActivity.showToast("白板已开启");
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
            if (whiteboardStatus != null) {
                AppBaseActivity.showToast("白板已开启");
                return;
            }
            startWhiteBoard(1, 1);
        }

        @Override
        public void onCancel() {
            menu_meet_media.closeShare();
            menu_meet_media.setVisibility(View.GONE);
            menu_meet_media_top.setVisibility(View.GONE);
        }
    };

    private MorePopupWindow.MoreItemClickListener mMoreItemClickListener = new MorePopupWindow.MoreItemClickListener() {
        @Override
        public void onAddPerson() {
            // 会议邀请
            HYClient.getModule(ApiMeet.class).requestMeetDetail(
                    SdkParamsCenter.Meet.RequestMeetDetail()
                            .setMeetDomainCode(((MeetNewActivity) getActivity()).strMeetDomainCode)
                            .setnListMode(1)
                            .setMeetID(((MeetNewActivity) getActivity()).nMeetID), new SdkCallback<CGetMeetingInfoRsp>() {
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
                                            ChoosedContactsNew.get().clear();
                                            ChoosedContactsNew.get().setContacts(ConvertContacts.ConvertMeetUserInfoToContacts(data));
                                            Intent intent = new Intent(getActivity(), ContactsChoiceByAllFriendActivity.class);
                                            intent.putExtra("titleName", "邀请参会");
                                            getActivity().startActivityForResult(intent, 1000);

                                            mMorePopupWindow.dismiss();
                                            menu_meet_media.setVisibility(View.GONE);
                                            menu_meet_media_top.setVisibility(View.GONE);
                                        }
                                    });
                        }

                        @Override
                        public void onError(ErrorInfo errorInfo) {
                            AppBaseActivity.showToast(ErrorMsg.getMsg(ErrorMsg.get_meet_info_err_code));
                        }
                    });
        }

        @Override
        public void onControl() {
            if (mCGetMeetingInfoRsp == null) {
                return;
            }
            /*if (!mCGetMeetingInfoRsp.strMainUserID.equals(HYClient.getSdkOptions().User().getUserId())) {
                Toast.makeText(getActivity(), "只有主讲人才能发起会议控制", Toast.LENGTH_SHORT).show();
                return;
            }*/
            Intent intent = new Intent(getActivity(), MeetControlActivity.class);
            intent.putExtra("strMeetDomainCode", mCGetMeetingInfoRsp.strMainUserDomainCode);
            intent.putExtra("nMeetID", mCGetMeetingInfoRsp.nMeetingID);
            intent.putStringArrayListExtra("mRaiseUsers", mRaiseUsers);
            startActivity(intent);
            mMorePopupWindow.dismiss();
            menu_meet_media.setVisibility(View.GONE);
            menu_meet_media_top.setVisibility(View.GONE);
        }

        @Override
        public void onShare() {
            mMorePopupWindow.dismiss();
            menu_meet_media.setVisibility(View.GONE);
            menu_meet_media_top.setVisibility(View.GONE);
//            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//                mProjectionManager = (MediaProjectionManager)getContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//                startActivityForResult(mProjectionManager.createScreenCaptureIntent(), 0x100);
//            }

        }

        @Override
        public void onChat() {
            mMorePopupWindow.dismiss();
            menu_meet_media.setVisibility(View.GONE);
            menu_meet_media_top.setVisibility(View.GONE);
        }

        @Override
        public void onCancel() {
            menu_meet_media.closeMore();
            menu_meet_media.setVisibility(View.GONE);
            menu_meet_media_top.setVisibility(View.GONE);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0x100) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    return;
                }
//                final MediaProjection mProjection = mProjectionManager.getMediaProjection(resultCode, data);
//                HYCapture.Config config =  HYClient.getHYCapture().getCapturingConfig();
//                config.setEnableProjection(true);
//                config.setMediaProjection(mProjection);
//                HYClient.getHYCapture().switchCaptureConfig(config);
            }
        }
    }

    public void startTime() {
        if (menu_meet_media_top != null)
            menu_meet_media_top.startTime();
    }

    /**
     * 自己音频入会才会调用
     */
    public void closeVideo() {
        video_close_layout.setVisibility(View.VISIBLE);
        HYClient.getHYCapture().setCaptureVideoOn(false);
        menu_meet_media.closeVideo();
    }

    /**
     * 自己视频入会才会调用
     */
    public void openVideo() {
        video_close_layout.setVisibility(View.GONE);
        HYClient.getHYCapture().setCaptureVideoOn(true);
        menu_meet_media.openVideo();
    }

    public void closeMic(boolean isJingyan) {
        menu_meet_media.closeMic(isJingyan);
        if (!HYClient.getHYCapture().isCaptureAudioOn()) {
            return;
        }
        HYClient.getHYCapture().setCaptureAudioOn(false);
        notifyMicState(false);
    }

    public void openMic(boolean firstIn) {
        if (firstIn) {
            HYClient.getHYCapture().setCaptureAudioOn(true);
            menu_meet_media.openMic();
            notifyMicState(true);
        } else {
            if (HYClient.getHYCapture().isCaptureAudioOn()) {
                return;
            }
            HYClient.getHYCapture().setCaptureAudioOn(true);
            menu_meet_media.openMic();
            notifyMicState(true);
        }
    }

    public void addRaiseUser(String userId) {
        if (mRaiseUsers.contains(userId)) {
            return;
        }
        mRaiseUsers.add(userId);
    }

    public void deleteRaiseUser(String userId) {
        if (!mRaiseUsers.contains(userId)) {
            return;
        }
        mRaiseUsers.remove(userId);
    }

    public void clearRaiseUsers() {
        mRaiseUsers.clear();
    }

    private void notifyMicState(boolean isOpen) {
        Log.d("VIMApp", "notifyMicState");
        if (mCGetMeetingInfoRsp == null) {
            return;
        }
        ParamsUpdateMicStatus params = SdkParamsCenter.Meet.UpdateMicStatus()
                .setStrMeetingDomainCode(mCGetMeetingInfoRsp.strDomainCode)
                .setnMeetingID(mCGetMeetingInfoRsp.nMeetingID)
                .setnMicStatus(isOpen ? ParamsUpdateMicStatus.OPEN : ParamsUpdateMicStatus.CLOSE);
        HYClient.getModule(ApiMeet.class).setMeetingUpdateMicStatus(params, new SdkCallback<CUpdateMicStatusRsp>() {

            @Override
            public void onSuccess(CUpdateMicStatusRsp cUpdateMicStatusRsp) {
                Log.d("VIMApp", "notifyMicState success");
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                Log.d("VIMApp", "notifyMicState error");
            }
        });
    }

}
