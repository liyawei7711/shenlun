package huaiye.com.vim.ui.meet.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.sdk.mix.VideoCallbackWrapper;
import com.huaiye.sdk.media.player.sdk.params.user.UserReal;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingStatusInfo;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseFragment;
import huaiye.com.vim.ui.meet.adapter.MeetMultipleAdapter;

/**
 * Created by ywt on 2019/3/4.
 */
@BindLayout(R.layout.meet_multiple_layout)
public class MeetMemberFragment extends AppBaseFragment {
    /*@BindView(R.id.multiViewLayout)
    MultiViewLayout multiViewLayout;*/
    @BindView(R.id.recycview)
    RecyclerView recycview;

    private MeetMultipleAdapter mMeetMultipleAdapter;
    MultiPlayHelper multiPlayHelper;
    private ArrayList<CGetMeetingInfoRsp.UserInfo> mDataList;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        multiPlayHelper = new MultiPlayHelper(multiViewLayout);
        getNavigate().setVisibility(View.GONE);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int heigth = dm.heightPixels;

        mMeetMultipleAdapter = new MeetMultipleAdapter(getActivity(), heigth/2);
        recycview.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        recycview.setAdapter(mMeetMultipleAdapter);
        recycview.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                Log.d("MeetMemberFragment", "onViewRecycled");
                /*TextureView textureView = ((MeetMultipleAdapter.CustomViewHolder) holder).multiple_textureView;
                HYClient.getHYPlayer().stopPlayEx(null, textureView);*/
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(mMeetMultipleAdapter == null){
            return;
        }
        if(getUserVisibleHint()) {
            //可见
            mMeetMultipleAdapter.setDatas(mDataList);
            mMeetMultipleAdapter.notifyDataSetChanged();
        } else {
            //不可见
            mMeetMultipleAdapter.setDatas(null);
            mMeetMultipleAdapter.notifyDataSetChanged();
        }
    }*/

    public void setMeetingInfo(CGetMeetingInfoRsp getMeetingInfoRsp, ArrayList<CGetMeetingInfoRsp.UserInfo> list) {
        /*multiPlayHelper.refreshUser(getMeetingInfoRsp.listUser);
        multiPlayHelper.notifyChanged();*/
        if (getMeetingInfoRsp == null) {
            return;
        }
        mDataList = list;
        mMeetMultipleAdapter.setDatas(list);
        mMeetMultipleAdapter.notifyDataSetChanged();
    }

    private static class MultiPlayHelper {
        //        TextureView[] playViews;
        final int MAX_VIEW_SIZE = 6;
        //线程安全的先进先出队列
        ArrayBlockingQueue<CGetMeetingInfoRsp.UserInfo> waitPlayUsers;
        ArrayList<CGetMeetingInfoRsp.UserInfo> allUsers;
        //位置对应着TexureView,值对于这播放的用户
        HashMap<Integer, CGetMeetingInfoRsp.UserInfo> playMap;
        ViewGroup parentView;

        public MultiPlayHelper(ViewGroup parentView) {
//            this.playViews = playViews  == null ?  new TextureView[0]:playViews;
            waitPlayUsers = new ArrayBlockingQueue<>(100);
            playMap = new HashMap<>();
            allUsers = new ArrayList<>(100);
            this.parentView = parentView;
        }

        private void add(ArrayList<CGetMeetingInfoRsp.UserInfo> users) {
            if (users == null || users.size() == 0) {
                return;
            }
            waitPlayUsers.addAll(users);
        }

        private void add(CGetMeetingInfoRsp.UserInfo user) {
            if (user == null) {
                return;
            }
            waitPlayUsers.add(user);
        }

        public void userJoin(String strUserID) {
            for (int i = 0; i < allUsers.size(); i++) {
                if (allUsers.get(i).strUserID.equals(strUserID)) {
                    //找到后如果正在播放就算了
                    Iterator<Map.Entry<Integer, CGetMeetingInfoRsp.UserInfo>> iterator = playMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Integer, CGetMeetingInfoRsp.UserInfo> item = iterator.next();
                        if (item.getValue() != null && item.getValue().strUserID.equals(strUserID)) {
                            return;
                        }
                    }

                    add(allUsers.get(i));
                    return;
                }
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
                newUser.nJoinStatus = oldUser.nPartType == CNotifyMeetingStatusInfo.User.QUIT_MEET ? 0 : 2;
                newUserList.add(newUser);
            }
            return newUserList;
        }

        public void refreshUser(ArrayList<CGetMeetingInfoRsp.UserInfo> users) {
            if (users == null) {
                stopAll();
                return;
            }
            allUsers = users;

            //先看看有没有与TextureView中的用户重叠,剩下的加入等待队列
            for (int i = 0; i < playMap.size(); i++) {
                CGetMeetingInfoRsp.UserInfo userInfoPlaying = playMap.get(i);
                boolean isPlaying = false;
                int findPos = -1;
                for (int j = 0; j < users.size(); j++) {
                    CGetMeetingInfoRsp.UserInfo user = users.get(j);
                    //当前播放的还在会议中,继续播放
                    if (user.isInMeeting() && user.strUserID.equals(userInfoPlaying.strUserID)) {
                        isPlaying = true;
                        findPos = j;
                        break;
                    }
                }
                //把正在播放中的用户移除,等下放入等待队列
                if (findPos != -1) {
                    users.remove(findPos);
                }

                //第i个位置的textureView的退出会议了
                if (!isPlaying) {
                    stop(i);
                }
            }
            //其他的放到队列里
            add(users);
        }

        public void notifyChanged() {

            //看看有没有空的TextureView,有的话,就到用户列表里抽一个出来播放
            for (int i = 0; i < MAX_VIEW_SIZE; i++) {
                if (playMap.get(i) == null) {
                    while (true) {
                        CGetMeetingInfoRsp.UserInfo userInfo = waitPlayUsers.poll();
                        if (userInfo == null) {
                            //没有待播放的用户,直接结束
                            return;
                        }
                        if (!userInfo.isInMeeting()) {
                            continue;
                        }

                        playMap.put(i, userInfo);
                        play(userInfo, getView(i));
                        break;
                    }

                }
            }

        }

        private TextureView getView(int pos) {
            View childView = parentView.getChildAt(pos);
            TextureView textureView;
            if (childView == null) {
                textureView = new TextureView(parentView.getContext());
                parentView.addView(textureView);
            } else {
                textureView = (TextureView) childView;
            }
            return textureView;
        }


        public void stop(int pos) {
            playMap.remove(pos);
            TextureView view = getView(pos);
            if (view != null) {
                HYClient.getHYPlayer().stopPlayEx(null, view);
                parentView.removeView(view);
            }
        }

        public void stopAll() {
            waitPlayUsers.clear();
            playMap.clear();
            if (parentView.getChildCount() > 0) {
                TextureView[] childView = new TextureView[parentView.getChildCount()];
                for (int i = 0; i < parentView.getChildCount(); i++) {
                    childView[i] = (TextureView) parentView.getChildAt(i);
                }
                HYClient.getHYPlayer().stopPlayEx(null, childView);
            }
            parentView.removeAllViews();
        }

        public void play(CGetMeetingInfoRsp.UserInfo userInfo, TextureView textureView) {
            UserReal videoParams = Player.Params.TypeUserReal()
                    .setUserDomainCode(userInfo.strUserDomainCode)
                    .setUserTokenID(userInfo.strUserTokenID)
                    .setPreview(textureView)
                    .setMixCallback(new VideoCallbackWrapper() {
                    });
            //只播放自己的画面
            if (HYClient.getSdkOptions().User().getUserId().equals(userInfo.strUserID)) {
                videoParams.setAudioOn(false);
            }
            HYClient.getHYPlayer().startPlay(videoParams);
        }

    }
}
