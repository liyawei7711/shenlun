package huaiye.com.vim.ui.meet.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.huaiye.cmf.sdp.SdpMessageCmStartSessionRsp;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingStatusInfo;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseFragment;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.ui.meet.MeetNewActivity;
import huaiye.com.vim.ui.meet.MultiPlayerHelper;
import huaiye.com.vim.ui.meet.views.MultiViewLayout;

/**
 * Created by LENOVO on 2019/3/6.
 */
@BindLayout(R.layout.meet_multiple_layout)
public class MeetMemberNewFragment extends AppBaseFragment {
    @BindView(R.id.multiViewLayout)
    MultiViewLayout multiViewLayout;

    MultiPlayerHelper multiPlayerHelper;
    //    private MultiPlayHelper multiPlayHelper;
    private ArrayList<CGetMeetingInfoRsp.UserInfo> mDataList;
    private CGetMeetingInfoRsp mCGetMeetingInfoRsp;
    private boolean isCloseVideo;//true---音频入会

    /**
     * 延迟初始化相关
     */
    private boolean isResumed = false;
    private boolean isSetVisible = false;
    private SdpMessageCmStartSessionRsp sessionRsp;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);

        getNavigate().setVisibility(View.GONE);
        isCloseVideo = ((MeetNewActivity) getActivity()).isCloseVideo();
        multiPlayerHelper = new MultiPlayerHelper(multiViewLayout);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        if (null != messageEvent) {
            switch (messageEvent.what) {
                case AppUtils.EVENT_MESSAGE_CHANGE_VIDEO_STATE:
                    isCloseVideo = (Boolean) messageEvent.obj1;
                    if (null != multiPlayerHelper) {
                        multiPlayerHelper.closeMyselfVideo(isCloseVideo);
                    }
                    break;
                case AppUtils.EVENT_MESSAGE_CHANGE_VIDEO_STATE_OTHERS:
                    boolean videoState = (Boolean) messageEvent.obj1;
                    if (null != multiPlayerHelper) {
                        multiPlayerHelper.addPeopelVideoState(messageEvent.argStr0, videoState);
                    }
                    break;
            }


        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);

        if (multiPlayerHelper != null) {
            multiPlayerHelper.refreshData(null, "");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(multiPlayerHelper == null) {
            multiPlayerHelper = new MultiPlayerHelper(multiViewLayout);
        }
        isResumed = true;
        refreshUser(mCGetMeetingInfoRsp, mDataList);
    }

    @Override
    public void onPause() {
        super.onPause();
        isResumed = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isSetVisible = isVisibleToUser;

        if(multiPlayerHelper != null) {
            multiPlayerHelper.changeUserPreview(isSetVisible);
        }

        refreshUser(mCGetMeetingInfoRsp, mDataList);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void refreshUser(CGetMeetingInfoRsp infoRsp, ArrayList<CGetMeetingInfoRsp.UserInfo> list) {
//        if (BuildConfig.DEBUG){
//            return;
//        }
        this.mCGetMeetingInfoRsp = infoRsp;
        this.mDataList = list;
        if (multiPlayerHelper == null || this.mCGetMeetingInfoRsp == null || this.mDataList == null) {
            return;
        }
        if (isResumed && !isSetVisible) {
            multiPlayerHelper.refreshData(mDataList, mCGetMeetingInfoRsp.strKeynoteSpeakerTokenID, !isCloseVideo);
        }

        if (isResumed && isSetVisible) {
            multiPlayerHelper.refreshData(mDataList, mCGetMeetingInfoRsp.strKeynoteSpeakerTokenID, !isCloseVideo);
        }

    }


    public static ArrayList<CGetMeetingInfoRsp.UserInfo> convertUser(ArrayList<CNotifyMeetingStatusInfo.User> users) {
        if (users == null) {
            return null;
        }
        ArrayList<CGetMeetingInfoRsp.UserInfo> newUserList = new ArrayList<>(users.size());
        for (int i = 0; i < users.size(); i++) {
            CGetMeetingInfoRsp.UserInfo newUser = new CGetMeetingInfoRsp.UserInfo();
            CNotifyMeetingStatusInfo.User oldUser = users.get(i);
            newUser.strUserID = oldUser.strUserID;
            newUser.strUserDomainCode = oldUser.strUserDomainCode;
            newUser.strUserName = oldUser.strUserName;
            newUser.strUserTokenID = oldUser.strUserTokenID;
            newUser.nDevType = oldUser.nDevType;
            newUser.nMicStatus = oldUser.nMicStatus;
            newUser.nJoinStatus = oldUser.nPartType == CNotifyMeetingStatusInfo.User.QUIT_MEET ? 0 : 2;
            newUserList.add(newUser);
        }
        return newUserList;
    }


    public void setCallId(SdpMessageCmStartSessionRsp sessionRsp) {
        this.sessionRsp = sessionRsp;
        if(multiPlayerHelper != null) {
            multiPlayerHelper.startPlay(sessionRsp);
        }
    }
}
