package huaiye.com.vim.ui.meet.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.huaiye.cmf.sdp.SdpMessageCmStartSessionRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.meet.ParamsMeetKickout;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CInviteUserMeetingRsp;
import com.huaiye.sdk.sdpmsgs.meet.CMeetingSpeakSetReq;
import com.huaiye.sdk.sdpmsgs.meet.CMeetingSpeakSetRsp;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingRaiseInfo;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;
import com.huaiye.sdk.sdpmsgs.meet.CkickMeetingUserRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppBaseFragment;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.recycle.LiteBaseAdapter;
import huaiye.com.vim.ui.meet.viewholder.MemberHolder;

import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * author: admin
 * date: 2018/01/08
 * version: 0
 * mail: secret
 * desc: MeetMembersFragment
 * 会议人员列表
 */
@BindLayout(R.layout.fragment_meet_members)
public class MeetMembersNewFragment extends AppBaseFragment {

    boolean isMeetStarter;
    String strMeetDomaincode;
    int nMeetID;

    public void setIsMeetStarter(boolean value, String masterLoginName) {
        isMeetStarter = value;
        MemberHolder.isMeetStarter = isMeetStarter;
        MemberHolder.masterLogoinName = masterLoginName;
        if (tv_one_key_voice == null)
            return;
        if (isMeetStarter) {
            tv_one_key_voice.setVisibility(View.VISIBLE);
        } else {
            tv_one_key_voice.setVisibility(View.GONE);
        }
    }

    public void setMeetDomaincode(String domain) {
        this.strMeetDomaincode = domain;
    }

    public void setMeetID(int id) {
        this.nMeetID = id;
    }

    @BindView(R.id.rct_view)
    RecyclerView rct_view;
    @BindView(R.id.tv_one_key_voice)
    TextView tv_one_key_voice;
    LiteBaseAdapter<CGetMeetingInfoRsp.UserInfo> adapter;
    ArrayList<CGetMeetingInfoRsp.UserInfo> listUser = new ArrayList<>();


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getNavigate().setVisibility(View.GONE);

        rct_view.setLayoutManager(new LinearLayoutManager(getContext()));

        MemberHolder.isMeetStarter = isMeetStarter;
        MemberHolder.handUser.clear();
        if (isMeetStarter) {
            tv_one_key_voice.setVisibility(View.VISIBLE);
        } else {
            tv_one_key_voice.setVisibility(View.GONE);
        }
        adapter = new LiteBaseAdapter<>(getContext(),
                listUser,
                MemberHolder.class,
                R.layout.item_meet_members_new,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CGetMeetingInfoRsp.UserInfo bean = (CGetMeetingInfoRsp.UserInfo) v.getTag();
                        switch (v.getId()) {
                            case R.id.iv_jinyan:
                            case R.id.fl_speak:
                                jinyan(bean);
                                break;
                            case R.id.iv_tichu:
                            case R.id.fl_kickout:
                                kitOut(bean);
                                break;
                            case R.id.iv_chonghu:
                            case R.id.fl_recall:
                                reCall(bean);
                                break;
                        }
                    }
                }, null);


        rct_view.setAdapter(adapter);
    }

    @OnClick({R.id.tv_one_key_voice})
    void onKickoutClicked(View v) {
        switch (v.getId()) {
            case R.id.tv_one_key_voice:
                onekeyJinYan();
                break;
        }
    }

    private void onekeyJinYan() {
        ArrayList<CMeetingSpeakSetReq.User> users = new ArrayList<>();
        int type = 1;
        if (tv_one_key_voice.getText().toString().equals("一键禁言")) {
            type = 0;
        }

        final int finalType = type;
        HYClient.getModule(ApiMeet.class)
                .mgrUserSpeaker(SdkParamsCenter.Meet.MgrUsrSpeaker()
                        .setMeetDomainCode(strMeetDomaincode)
                        .setMeetID(nMeetID)
                        .setnSetSpeakForAll(type)
                        .setUsers(users), new SdkCallback<CMeetingSpeakSetRsp>() {
                    @Override
                    public void onSuccess(CMeetingSpeakSetRsp cMeetingSpeakSetRsp) {
                        if (finalType == 0) {
                            AppBaseActivity.showToast("全体禁言成功");
                            for (CGetMeetingInfoRsp.UserInfo temp : listUser) {
                                temp.nMuteStatus = 1;
                            }
                        } else {
                            AppBaseActivity.showToast("全体解禁成功");
                            for (CGetMeetingInfoRsp.UserInfo temp : listUser) {
                                temp.nMuteStatus = 0;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        if (finalType == 0) {
                            AppBaseActivity.showToast(ErrorMsg.getMsg(ErrorMsg.jinyan_close_err_code));
                        } else {
                            AppBaseActivity.showToast(ErrorMsg.getMsg(ErrorMsg.jinyan_open_err_code));
                        }

                    }
                });
    }

    /**
     * 禁言
     */
    private void jinyan(final CGetMeetingInfoRsp.UserInfo userInfo) {
        ArrayList<CMeetingSpeakSetReq.User> users = new ArrayList<>();

        CMeetingSpeakSetReq.User user = new CMeetingSpeakSetReq.User();
        user.setMode(userInfo.isSpeakerMute() ? SdkBaseParams.MuteStatus.UnMute : SdkBaseParams.MuteStatus.Mute);
        user.strUserDomainCode = userInfo.strUserDomainCode;
        user.strUserID = userInfo.strUserID;
        users.add(user);

        HYClient.getModule(ApiMeet.class)
                .mgrUserSpeaker(SdkParamsCenter.Meet.MgrUsrSpeaker()
                        .setMeetDomainCode(strMeetDomaincode)
                        .setMeetID(nMeetID)
                        .setUsers(users), new SdkCallback<CMeetingSpeakSetRsp>() {
                    @Override
                    public void onSuccess(CMeetingSpeakSetRsp cMeetingSpeakSetRsp) {
                        if (userInfo.isSpeakerMute()) {
                        } else {
                            AppBaseActivity.showToast("禁言成功");
                        }
                        userInfo.nMuteStatus = userInfo.isSpeakerMute() ? 0 : 1;
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        if (userInfo.isSpeakerMute()) {
                            AppBaseActivity.showToast(ErrorMsg.getMsg(ErrorMsg.jinyan_open_err_code));
                        } else {
                            AppBaseActivity.showToast(ErrorMsg.getMsg(ErrorMsg.jinyan_close_err_code));
                        }
                    }
                });
    }

    /**
     * 踢出
     */
    private void kitOut(final CGetMeetingInfoRsp.UserInfo info) {
        ParamsMeetKickout.User user = new ParamsMeetKickout.User();
        user.setDomainCode(info.strUserDomainCode);
        user.setUserID(info.strUserID);
        HYClient.getModule(ApiMeet.class).kickoutUser(SdkParamsCenter.Meet.KickoutMeet()
                .setMeetDomainCode(strMeetDomaincode)
                .setMeetID(nMeetID)
                .addKickoutUser(user), new SdkCallback<CkickMeetingUserRsp>() {
            @Override
            public void onSuccess(CkickMeetingUserRsp ckickMeetingUserRsp) {
                AppBaseActivity.showToast("踢出成功");
                listUser.remove(info);
                adapter.notifyDataSetChanged();
//                if (getActivity() != null) {
//                    ((MeetActivity) getActivity()).hideAll();
//                }
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                AppBaseActivity.showToast(ErrorMsg.getMsg(ErrorMsg.kitout_err_code));
            }
        });
    }

    /**
     * 重呼
     */
    private void reCall(CGetMeetingInfoRsp.UserInfo userInfo) {
        CStartMeetingReq.UserInfo user = new CStartMeetingReq.UserInfo();
        user.setDevTypeUser();
        user.strUserName = userInfo.strUserName;
        user.strUserID = userInfo.strUserID;
        user.strUserDomainCode = userInfo.strUserDomainCode;

        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable && !HYClient.getSdkOptions().User().getUserId().equals(user.strUserID)) {
            EncryptUtil.startEncrypt(true, user.strUserID, user.strUserDomainCode,
                    nMeetID + "", strMeetDomaincode, new SdkCallback<SdpMessageCmStartSessionRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmStartSessionRsp sessionRsp) {
                            reCallReal(user);
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            AppBaseActivity.showToast("邀请失败");
                        }
                    });
        } else {
            if(nEncryptIMEnable && !HYClient.getSdkOptions().encrypt().isEncryptBind()) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                AppBaseActivity.showToast("加密模块未初始化完成，邀请失败");
                return;
            }
            reCallReal(user);
        }

    }

    void reCallReal(CStartMeetingReq.UserInfo user) {
        HYClient.getModule(ApiMeet.class).inviteUser(SdkParamsCenter.Meet.InviteMeet()
                .setMeetDomainCode(strMeetDomaincode)
                .setMeetID(nMeetID)
                .addUsers(user), new SdkCallback<CInviteUserMeetingRsp>() {
            @Override
            public void onSuccess(CInviteUserMeetingRsp cInviteUserMeetingRsp) {
                AppBaseActivity.showToast("邀请成功");
//                if (getActivity() != null) {
//                    ((MeetActivity) getActivity()).hideAll();
//                }
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                AppBaseActivity.showToast(ErrorMsg.getMsg(ErrorMsg.invite_user_err_code));
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            requestDatas();
        } else {
            MemberHolder.handUser.clear();
            adapter.notifyDataSetChanged();
        }
    }

    void requestDatas() {
        HYClient.getModule(ApiMeet.class).requestMeetDetail(
                SdkParamsCenter.Meet.RequestMeetDetail()
                        .setMeetDomainCode(strMeetDomaincode)
                        .setnListMode(1)
                        .setMeetID(nMeetID), new SdkCallback<CGetMeetingInfoRsp>() {
                    @Override
                    public void onSuccess(CGetMeetingInfoRsp cGetMeetingInfoRsp) {
                        listUser.clear();
                        listUser.addAll(cGetMeetingInfoRsp.listUser);
                        Collections.sort(listUser, new Comparator<CGetMeetingInfoRsp.UserInfo>() {
                            @Override
                            public int compare(CGetMeetingInfoRsp.UserInfo o1, CGetMeetingInfoRsp.UserInfo o2) {
                                if (o1.nJoinStatus == 2 && o2.nJoinStatus != 2) return -1;
                                if (o1.nJoinStatus != 2 && o2.nJoinStatus == 2) return 1;
                                if (o1.nJoinStatus == 2 && o2.nJoinStatus == 2) return 0;
                                return 0;
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        AppBaseActivity.showToast(ErrorMsg.getMsg(ErrorMsg.get_meet_info_err_code));
                    }
                });
    }

    /**
     * 有人举手
     *
     * @param info
     */
    public void changeDataHandUp(CNotifyMeetingRaiseInfo info) {
        if (!MemberHolder.handUser.contains(info.strUserID)) {
            MemberHolder.handUser.add(info.strUserID);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 有人入会
     *
     * @param
     */
    public void refUser() {
        if (isVisible()) {
            requestDatas();
        }
    }

    public void changeOneKey(boolean meetMute) {
        if (meetMute) {
            tv_one_key_voice.setText("一键解禁");
        } else {
            tv_one_key_voice.setText("一键禁言");
        }
    }
}
