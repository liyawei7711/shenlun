package huaiye.com.vim.ui.meet;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.cmf.sdp.SdpMessageCmStartSessionRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.msg.SdkMsgNotifyStreamStatus;
import com.huaiye.sdk.media.player.sdk.mix.VideoCallbackWrapper;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.huaiye.sdk.media.player.sdk.params.user.UserReal;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingStatusInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.common.constant.SPConstant;
import huaiye.com.vim.common.views.ChildPlayerView;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.ContactsApi;
import huaiye.com.vim.models.contacts.bean.ContactsBean;
import huaiye.com.vim.ui.meet.views.MultiViewLayout;

import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

public class MultiPlayerHelper {
    private ArrayList<UserInfoWrapper> oldUserList;


    private MultiViewLayout parentView;


    private MultiPlayListener multiPlayListener;

    private int mCurrentPage;

    /**
     * 缓存视频状态(是否可以播放状态)
     */
    private Map map = new HashMap<String, Boolean>();

    public MultiPlayerHelper(MultiViewLayout parentView) {
        oldUserList = new ArrayList<>(100);
        mCurrentPage = 0;
        this.parentView = parentView;
        this.parentView.setOnPageListener(new MultiViewLayout.onPageListener() {
            @Override
            public void onPageChange(int currentPage) {
                mCurrentPage = currentPage;
                int numStart = currentPage * MultiViewLayout.ONE_PAGE_NUM;
                int numEnd = numStart + MultiViewLayout.ONE_PAGE_NUM;
                if (oldUserList != null) {
                    for (int i = 0; i < oldUserList.size(); i++) {
                        UserInfoWrapper userInfoWrapper = oldUserList.get(i);
                        if (i >= numStart && i < numEnd) {
                            if (!userInfoWrapper.showVideo) {
                                userInfoWrapper.showVideo = true;
                                userInfoWrapper.status = UserInfoWrapper.STATUS_NORMAL_VIDEO_CHANGE;
                            }
                        } else {
                            if (userInfoWrapper.showVideo) {
                                userInfoWrapper.showVideo = false;
                                userInfoWrapper.status = UserInfoWrapper.STATUS_NORMAL_VIDEO_CHANGE;
                            }
                        }

                        putMapStatus(userInfoWrapper.userInfo.strUserID, userInfoWrapper.showVideo, "onPageChange");
                    }
                    for (int i = 0; i < oldUserList.size(); i++) {
                        notifyItemChanged(i);
                    }
                }
            }
        });
    }

    private void putMapStatus(String strUserID, boolean showVideo, String from) {
        map.put(strUserID, showVideo);
    }

    public boolean isShowVideo(String strUserID, boolean defaultType) {
        if (map.containsKey(strUserID)) {
            return (boolean) map.get(strUserID);
        } else {
            return defaultType;
        }
    }

    public void refreshData(ArrayList<CGetMeetingInfoRsp.UserInfo> users, String speakUserTokenIdFromServer) {
        refreshData(users, speakUserTokenIdFromServer, true);
    }


    public void refreshData(ArrayList<CGetMeetingInfoRsp.UserInfo> users, String speakUserTokenIdFromServer, boolean showVideo) {

        ArrayList<UserWithVideoStatus> userWithVideoStatus = new ArrayList<>();
        if (users != null) {
            for (CGetMeetingInfoRsp.UserInfo oneUser : users) {
                UserWithVideoStatus oneUserVideo = new UserWithVideoStatus();
                oneUserVideo.userInfo = oneUser;
                oneUserVideo.showVideo = isShowVideo(oneUser.strUserID, showVideo);
                userWithVideoStatus.add(oneUserVideo);
            }
        }
        int numStart = mCurrentPage * MultiViewLayout.ONE_PAGE_NUM;
        int numEnd = numStart + MultiViewLayout.ONE_PAGE_NUM;
        for (int i = 0; i < userWithVideoStatus.size(); i++) {
            if (i >= numStart && i < numEnd) {
                //do nothing
            } else {
                userWithVideoStatus.get(i).showVideo = false;
            }
        }
        refreshDataWithVideoStatus(userWithVideoStatus, speakUserTokenIdFromServer, showVideo);
    }

    private void refreshDataWithVideoStatus(ArrayList<UserWithVideoStatus> users, String speakUserTokenIdFromServer, boolean defalutType) {
        if (users == null || users.size() == 0) {
            stopAll();
            oldUserList.clear();
            parentView.removeAllViews();
            return;
        }
        ArrayList<UserWithVideoStatus> filterUser = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            UserWithVideoStatus item = users.get(i);
            if (item.userInfo.nJoinStatus == 2) {
                filterUser.add(item);
            }
        }
        if (filterUser == null || filterUser.size() == 0) {
            stopAll();
            oldUserList.clear();
            parentView.removeAllViews();
            return;
        }

        Collections.sort(filterUser, new Comparator<UserWithVideoStatus>() {
            @Override
            public int compare(UserWithVideoStatus o1, UserWithVideoStatus o2) {
                return o1.userInfo.nSequence < o2.userInfo.nSequence ? -1 : 1;
            }
        });
        //把所有的旧的都标记为待删除
        for (int i = 0; i < oldUserList.size(); i++) {
            UserInfoWrapper item = oldUserList.get(i);
            item.status = UserInfoWrapper.STATUS_DELETE;
        }

        //user存在于老的和新的就没有变化
        //user只存在于新的,说明是新增的
        ArrayList<UserInfoWrapper> newAddList = new ArrayList<>();
        for (int i = 0; i < filterUser.size(); i++) {
            boolean find = false;
            UserWithVideoStatus newUserInfo = filterUser.get(i);
            for (int j = 0; j < oldUserList.size(); j++) {
                if (newUserInfo.userInfo.strUserID.equals(oldUserList.get(j).userInfo.strUserID)) {
                    find = true;
                    oldUserList.get(j).status = UserInfoWrapper.STATUS_NORMAL;
                    if (oldUserList.get(j).showVideo != newUserInfo.showVideo) {
                        oldUserList.get(j).status = UserInfoWrapper.STATUS_NORMAL_VIDEO_CHANGE;
                        oldUserList.get(j).showVideo = newUserInfo.showVideo;
                    }
                    break;
                }
            }
            if (!find) {
                UserInfoWrapper wrapper = new UserInfoWrapper(newUserInfo.userInfo, speakUserTokenIdFromServer);
                wrapper.status = UserInfoWrapper.STATUS_NEW_ADD;
                wrapper.showVideo = newUserInfo.showVideo;
                newAddList.add(wrapper);
            }
        }
        oldUserList.addAll(newAddList);

        for (int i = 0; i < oldUserList.size(); i++) {
            if (i < 4) {
                boolean showVideo = defalutType;
                if (map.containsKey(oldUserList.get(i).userInfo.strUserID)) {
                    showVideo = (boolean) map.get(oldUserList.get(i).userInfo.strUserID);
                }
                putMapStatus(oldUserList.get(i).userInfo.strUserID, showVideo, "refreshDataWithVideoStatus");
            } else {
                putMapStatus(oldUserList.get(i).userInfo.strUserID, false, "refreshDataWithVideoStatus");
            }
            notifyItemChanged(i);
        }

        java.util.Iterator<UserInfoWrapper> iterator = oldUserList.iterator();
        while (iterator.hasNext()) {
            UserInfoWrapper item = iterator.next();
            if (item.status == UserInfoWrapper.STATUS_DELETE) {
                iterator.remove();
            } else {
                item.status = UserInfoWrapper.STATUS_NORMAL;
            }
        }
    }


    public void up() {
//        if (parentView.getChildCount() == 0){
//            return;
//        }
//        if (parentView.getChildCount() <= 4){
//            return;
//        }
//
//        int oneItemHeight = parentView.getChildAt(0).getHeight();
//        int currentScrollY = scrollView.getScrollY();
//        int currentPos = currentScrollY/oneItemHeight;
//        int maxScroll = (parentView.getChildCount()-4)*oneItemHeight;
//        int targetHeight = oneItemHeight*(currentPos+1);
//        if (targetHeight <= maxScroll){
//            scrollView.scrollTo(0,targetHeight);
//        }

    }

    public void down() {
//        if (parentView.getChildCount() == 0){
//            return;
//        }
//        if (parentView.getChildCount() <= 4){
//            return;
//        }
//
//        int oneItemHeight = parentView.getChildAt(0).getHeight();
//        int currentScrollY = scrollView.getScrollY();
//        int currentPos = currentScrollY/oneItemHeight;
//        int minScroll = 0;
//        int targetScroll = oneItemHeight*(currentPos-1);
//        if (targetScroll >= minScroll){
//            scrollView.scrollTo(0,targetScroll);
//        }
    }


    public void closeMyselfVideo(boolean closeMyself) {
        putMapStatus(AppDatas.Auth().getUserID(), !closeMyself, "closeMyselfVideo");
    }

    public void addPeopelVideoState(String userId, boolean isCloseVideo) {
        if (!TextUtils.isEmpty(userId)) {
            putMapStatus(userId, !isCloseVideo, "addPeopelVideoState");
        }
    }


    private void notifyItemChanged(int pos) {
        UserInfoWrapper newUserInfo = oldUserList.get(pos);
        switch (newUserInfo.status) {
            case UserInfoWrapper.STATUS_DELETE:
                if (newUserInfo.targetView != null) {
                    final View targetView = newUserInfo.targetView;
                    ChildPlayerView childPlayerView = targetView.findViewById(R.id.child_player_view);
                    TextureView textureView = childPlayerView.getTextureView();
                    if (newUserInfo.videoParams != null) {
                        HYClient.getHYPlayer().stopPlay(new SdkCallback<VideoParams>() {
                            @Override
                            public void onSuccess(VideoParams videoParams) {
                                parentView.removeView(targetView);
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {

                            }
                        }, newUserInfo.videoParams);
                    } else {
                        HYClient.getHYPlayer().stopPlayEx(new SdkCallback<VideoParams>() {
                            @Override
                            public void onSuccess(VideoParams videoParams) {
                                parentView.removeView(targetView);
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {

                            }
                        }, textureView);
                    }

                }
                break;
            case UserInfoWrapper.STATUS_NEW_ADD:
                addOneItem(newUserInfo);
                break;
            case UserInfoWrapper.STATUS_NORMAL:
                if (newUserInfo.targetView == null) {
                    addOneItem(newUserInfo);
                }
                break;
            case UserInfoWrapper.STATUS_NORMAL_VIDEO_CHANGE:
                if (newUserInfo.targetView == null) {
                    addOneItem(newUserInfo);
                } else {
                    final View targetView = newUserInfo.targetView;
                    ChildPlayerView childPlayerView = targetView.findViewById(R.id.child_player_view);
                    View ivNoVideo = targetView.findViewById(R.id.video_close_layout);
                    TextureView textureView = childPlayerView.getTextureView();
                    boolean needUseCacheState = false;
                    boolean localState = false;
                    if (map.containsKey(newUserInfo.userInfo.strUserID)) {
                        needUseCacheState = true;
                        localState = (boolean) map.get(newUserInfo.userInfo.strUserID);
                    }
                    if (newUserInfo.userInfo.strUserID.equals(AppAuth.get().getUserID() + "")) {
                        if (newUserInfo.showVideo && needUseCacheState && localState) {
                            resizePreview((View) textureView.getParent(), textureView);
                            HYClient.getHYCapture().setPreviewWindow(textureView);
                            ivNoVideo.setVisibility(View.GONE);
                        } else {
                            HYClient.getHYCapture().setPreviewWindow(null);
                            ivNoVideo.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (newUserInfo.videoParams != null) {
                            if (newUserInfo.showVideo && needUseCacheState && localState) {
                                ivNoVideo.setVisibility(View.GONE);
                                HYClient.getHYPlayer().setPreviewWindow(newUserInfo.videoParams, textureView);
                            } else {
                                ivNoVideo.setVisibility(View.VISIBLE);
                                HYClient.getHYPlayer().setPreviewWindow(newUserInfo.videoParams, null);
                            }
                        }
                    }
                }
                break;
        }

    }


    /**
     * 增加个新的播放item
     *
     * @param newUserInfo
     */
    private void addOneItem(final UserInfoWrapper newUserInfo) {
        final ViewGroup newChild = (ViewGroup) LayoutInflater.from(parentView.getContext()).inflate(R.layout.item_meet_little_video, parentView, false);
        parentView.addView(newChild);
        ChildPlayerView childPlayerView = newChild.findViewById(R.id.child_player_view);
        TextureView textureView = childPlayerView.getTextureView();
        View ivNoVideo = newChild.findViewById(R.id.video_close_layout);
        newUserInfo.targetView = newChild;
        TextView tv = newChild.findViewById(R.id.tv_name);
        tv.setText(newUserInfo.userInfo.strUserName);
        TextView tvDesc = newChild.findViewById(R.id.tv_desc);
        if (newUserInfo.needShowDesc) {
            tvDesc.setVisibility(View.VISIBLE);
            tvDesc.setText(newUserInfo.desc);
        } else {
            tvDesc.setVisibility(View.GONE);
        }

        ImageView meet_head_img = newChild.findViewById(R.id.meet_head_img);

        setHeadViewPic(meet_head_img, newUserInfo);
        newChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newChild.isFocused() && multiPlayListener != null) {
                    multiPlayListener.onClickListener(newUserInfo);
                }
            }
        });
        childPlayerView.setChildViewCloseListener(new ChildPlayerView.ChildViewListener() {
            @Override
            public void onChildViewClose(ChildPlayerView childView) {

            }

            @Override
            public void onChildViewDoubleClick(ChildPlayerView view) {
                if (multiPlayListener != null) {
                    multiPlayListener.onClickListener(newUserInfo);
                }
            }
        });
        play(newUserInfo, textureView, ivNoVideo);
    }

    public static ArrayList<CGetMeetingInfoRsp.UserInfo> convertUser(ArrayList<CNotifyMeetingStatusInfo.User> users) {
        if (users == null) {
            return null;
        }
        ArrayList<CGetMeetingInfoRsp.UserInfo> newUserList = new ArrayList<>(users.size());
        for (int i = 0; i < users.size(); i++) {
            CGetMeetingInfoRsp.UserInfo newUser = new CGetMeetingInfoRsp.UserInfo();
            CNotifyMeetingStatusInfo.User oldUser = users.get(i);
            newUser.nUserRole = oldUser.nUserRole;
            newUser.strUserID = oldUser.strUserID;
            newUser.strUserDomainCode = oldUser.strUserDomainCode;
            newUser.strUserName = oldUser.strUserName;
            newUser.strUserTokenID = oldUser.strUserTokenID;
            newUser.nDevType = oldUser.nDevType;
            if (oldUser.nPartType == CNotifyMeetingStatusInfo.User.JOIN_MEET
                    || oldUser.nPartType == CNotifyMeetingStatusInfo.User.IN_MEET) {
                newUser.nJoinStatus = 2;
            } else {
                newUser.nJoinStatus = 0;
            }
            newUser.nSequence = oldUser.nSequence;
            newUserList.add(newUser);
        }
        return newUserList;
    }


    private void stopAll() {
        if (parentView.getChildCount() > 0) {
            if (oldUserList == null) {
                TextureView[] childView = new TextureView[parentView.getChildCount()];
                for (int i = 0; i < parentView.getChildCount(); i++) {
                    ChildPlayerView childPlayerView = parentView.getChildAt(i).findViewById(R.id.child_player_view);
                    childView[i] = childPlayerView.getTextureView();
                }
                HYClient.getHYPlayer().stopPlayEx(null, childView);
            } else {
                ArrayList<VideoParams> list = new ArrayList<>();
                for (int i = 0; i < oldUserList.size(); i++) {
                    UserInfoWrapper userInfoWrapper = oldUserList.get(i);
                    VideoParams vp = userInfoWrapper.videoParams;
                    list.add(vp);
                }
                if (list.size() > 0) {
                    VideoParams[] videoParams = new VideoParams[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        videoParams[i] = list.get(i);
                    }
                    HYClient.getHYPlayer().stopPlay(null, videoParams);
                }

            }


        }
    }

    private void play(final UserInfoWrapper userInfoWrapper, final TextureView textureView, final View ivNoVideo) {
        final CGetMeetingInfoRsp.UserInfo userInfo = userInfoWrapper.userInfo;
        if (userInfo.strUserID.equals(AppAuth.get().getUserID() + "")) {
            if (userInfoWrapper.showVideo) {
                Log.d("test", "multiPlayerHelper setPreviewWindow textureView " + textureView.getWidth());
                HYClient.getHYCapture().setPreviewWindow(textureView);
                resizePreview((View) textureView.getParent(), textureView);
                ivNoVideo.setVisibility(View.GONE);
            } else {
                ivNoVideo.setVisibility(View.VISIBLE);
            }
            return;
        }

        UserReal videoParams = Player.Params.TypeUserReal()
                .setUserDomainCode(userInfo.strUserDomainCode)
                .setUserTokenID(userInfo.strUserTokenID)
                .setPreview(textureView)
                .setMixCallback(new VideoCallbackWrapper() {
                    @Override
                    public void onVideoStatusChanged(VideoParams param, SdpMessageBase msg) {
                        super.onVideoStatusChanged(param, msg);
                        userInfoWrapper.videoParams = param;
                        Logger.debug("MultiPlayerHelper onVideoStatusChanged " + msg.getClass().getName());
                        if (msg instanceof SdkMsgNotifyStreamStatus) {
                            SdkMsgNotifyStreamStatus streamStatus = (SdkMsgNotifyStreamStatus) msg;
                            Logger.debug("MultiPlayerHelper " + userInfo.strUserName + " isAudioOn " + streamStatus.isAudioOn + " videoOn " + streamStatus.isVideoOn);
                            putMapStatus(userInfo.strUserID, streamStatus.isVideoOn, "onVideoStatusChanged");
                            if (streamStatus.isVideoOn) {
                                HYClient.getHYPlayer().setPreviewWindow(param, textureView);//必须主动渲染一次,否则直接在当前页面的话 可能不会展示视频
                                ivNoVideo.setVisibility(View.GONE);
                            } else {
                                HYClient.getHYPlayer().setPreviewWindow(param, null);
                                ivNoVideo.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onSuccess(VideoParams param) {
                        super.onSuccess(param);
                        userInfoWrapper.videoParams = param;
                        if (userInfoWrapper.showVideo) {
                            HYClient.getHYPlayer().setPreviewWindow(param, textureView);
                        } else {
                            HYClient.getHYPlayer().setPreviewWindow(param, null);
                        }
                        Logger.debug("MultiPlayerHelper successd " + userInfo.strUserName);
                    }

                    @Override
                    public void onError(VideoParams param, SdkCallback.ErrorInfo errorInfo) {
                        super.onError(param, errorInfo);
                        Logger.debug("MultiPlayerHelper error " + errorInfo.getMessage() + "  name" + userInfo.strUserName);
                    }
                });

        videoParams.getHYPlayerConfig().setTranscode(false);
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            videoParams.getHYPlayerConfig().setPlayerCallID(MeetNewActivity.sessionRsp.m_nCallId);
        }
        HYClient.getHYPlayer().startPlay(videoParams);
    }


    private void resizePreview(View textureParent, TextureView textureView) {
        int parentWidth = textureParent.getWidth();
        int parentHeight = textureParent.getHeight();
        int targetWidth = parentWidth;
        int targetHeight = parentHeight;
        switch (SP.getString(AppUtils.STRING_KEY_capture)) {
            case AppUtils.STRING_KEY_VGA:
                if (parentHeight > parentWidth) {
                    targetWidth = parentWidth;
                    targetHeight = (int) (parentWidth * 640.0 / 480);
                } else {
                    targetHeight = parentHeight;
                    targetWidth = (int) (parentWidth * 480.0 / 640);
                }
                break;
            case AppUtils.STRING_KEY_HD720P:
                if (parentHeight > parentWidth) {
                    targetWidth = parentWidth;
                    targetHeight = (int) (parentWidth * 1280.0 / 720);
                } else {
                    targetHeight = parentHeight;
                    targetWidth = (int) (parentWidth * 720.0 / 1280);
                }
                break;
            case AppUtils.STRING_KEY_HD1080P:
                if (parentHeight > parentWidth) {
                    targetWidth = parentWidth;
                    targetHeight = (int) (parentWidth * 1920.0 / 1080);
                } else {
                    targetHeight = parentHeight;
                    targetWidth = (int) (parentWidth * 1080.0 / 1920);
                }
                break;

        }
        ViewGroup.LayoutParams lp = textureView.getLayoutParams();
        lp.width = targetWidth;
        lp.height = targetHeight;
        textureView.setLayoutParams(lp);
    }

    public void startPlay(SdpMessageCmStartSessionRsp sessionRsp) {

    }

    public void changeUserPreview(boolean isSetVisible) {
        for (int i = 0; i < oldUserList.size(); i++) {
            if(oldUserList.get(i).userInfo.strUserID.equals(AppAuth.get().getUserID())) {
                if(isSetVisible) {
                    if(oldUserList.get(i).targetView != null) {
                        final View targetView = oldUserList.get(i).targetView;
                        ChildPlayerView childPlayerView = targetView.findViewById(R.id.child_player_view);
                        TextureView textureView = childPlayerView.getTextureView();
                        resizePreview((View) textureView.getParent(), textureView);
                        HYClient.getHYCapture().setPreviewWindow(textureView);
                    } else {
                        HYClient.getHYCapture().setPreviewWindow(null);
                    }
                } else {
                    HYClient.getHYCapture().setPreviewWindow(null);
                }
                break;
            }
        }
    }

    public interface MultiPlayListener {
        void onClickListener(UserInfoWrapper infoWrapper);
    }

    public static class UserWithVideoStatus {
        public CGetMeetingInfoRsp.UserInfo userInfo;
        public boolean showVideo;
    }

    /**
     * 包装下用户类,添加描述等等
     */
    public static class UserInfoWrapper {
        /**
         * 什么都不要做
         */
        static final int STATUS_NORMAL = 0;
        /**
         * 视频播放变化了
         */
        static final int STATUS_NORMAL_VIDEO_CHANGE = 10;
        /**
         * 需要添加到界面上
         */
        static final int STATUS_NEW_ADD = 1;
        /**
         * 需要从界面上删除
         */
        static final int STATUS_DELETE = 2;


        CGetMeetingInfoRsp.UserInfo userInfo;
        int status;
        View targetView;
        String desc;
        boolean needShowDesc;
        boolean showVideo;
        VideoParams videoParams;
        String headPic;

        public UserInfoWrapper(CGetMeetingInfoRsp.UserInfo userInfo, String speakUserTokenIdFromServer) {
            this.userInfo = userInfo;
            this.status = STATUS_NORMAL;
            this.needShowDesc = false;
            this.showVideo = true;
            this.videoParams = null;
            this.headPic = getHeadPic(userInfo);
            if (userInfo.nUserRole == 1) {
                needShowDesc = true;
                desc = "主持人";
            }
            if (!TextUtils.isEmpty(speakUserTokenIdFromServer)) {
                if (this.userInfo.strUserTokenID.equals(speakUserTokenIdFromServer)) {
                    needShowDesc = true;
                    desc = "主讲人";
                }
            }
        }

        private String getHeadPic(CGetMeetingInfoRsp.UserInfo userInfo) {
            String headPic = null;
            if (null == userInfo) {
                return null;
            } else {
                if (!userInfo.strUserID.equals(AppAuth.get().getUserID() + "")) {
                    headPic = AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(userInfo.strUserID, userInfo.strUserDomainCode);
                } else {
                    headPic = AppDatas.Auth().getHeadUrl(AppDatas.Auth().getUserID() + SPConstant.STR_HEAD_URL);

                }
                return headPic;
            }

        }
    }

    private void setHeadViewPic(ImageView imageView, UserInfoWrapper newUserInfo) {
        if (null == imageView) {
            return;
        }

        if (!TextUtils.isEmpty(newUserInfo.headPic)) {
            Glide.with(parentView.getContext())
                    .load(AppDatas.Constants().getAddressWithoutPort() + newUserInfo.headPic)
                    .apply(((MeetNewActivity) parentView.getContext()).getRequestOptions())
                    .into(imageView);
        } else {
            String mapKey = newUserInfo.userInfo.strUserDomainCode;
            List<String> mapValue = new ArrayList<>();
            mapValue.add(newUserInfo.userInfo.strUserID);
            ContactsApi.get().requestUserInfoList(mapKey, mapValue, new ModelCallback<ContactsBean>() {
                @Override
                public void onSuccess(ContactsBean contactsBean) {

                    if (null != contactsBean && null != contactsBean.userList && contactsBean.userList.size() == 1) {
                        AppDatas.MsgDB().getFriendListDao().insertAll(contactsBean.userList);
                        String headPic = AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(newUserInfo.userInfo.strUserID, mapKey);
                        Glide.with(parentView.getContext())
                                .load(AppDatas.Constants().getAddressWithoutPort() + headPic)
                                .apply(((MeetNewActivity) parentView.getContext()).getRequestOptions())
                                .into(imageView);
                    }
                }
            });
        }

    }

}