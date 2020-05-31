package huaiye.com.vim.ui.home;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.cmf.sdp.SdpMessageCmStartSessionRsp;
import com.huaiye.cmf.sdp.SdpUITask;
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
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingStatusInfo;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyPeerUserMeetingInfo;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;
import com.huaiye.sdk.sdpmsgs.meet.CkickMeetingUserRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.dialog.LogicDialog;
import huaiye.com.vim.common.recycle.LiteBaseAdapter;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.ui.contacts.ContactsChoiceByAllFriendActivity;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContacts;
import huaiye.com.vim.ui.meet.MeetWatchActivity;
import huaiye.com.vim.ui.meet.viewholder.MemberAdminHolder;
import huaiye.com.vim.ui.meet.viewholder.MemberHolder;

import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * author: admin
 * date: 2018/07/25
 * version: 0
 * mail: secret
 * desc: MeetControllActivity
 */
@BindLayout(R.layout.activity_meet_controll)
public class MeetControlActivity extends AppBaseActivity implements SdpUITask.SdpUIListener {

    @BindView(R.id.refresh_view)
    SwipeRefreshLayout refresh_view;

    @BindView(R.id.rct_view)
    RecyclerView rct_view;
    @BindView(R.id.iv_one_key_jinyan)
    TextView iv_one_key_jinyan;

    LiteBaseAdapter<CGetMeetingInfoRsp.UserInfo> adapter;
    ArrayList<CGetMeetingInfoRsp.UserInfo> listUser = new ArrayList<>();

    @BindExtra
    String strMeetDomainCode;
    @BindExtra
    int nMeetID;

    int count = 0;//邀请总数
    int cuntCount = 0;//返回数
    int cuntCountSuccess = 0;//返回成功数

    @Override
    protected void initActionBar() {
        EventBus.getDefault().register(this);
        getNavigate().setTitlText("管理模式")
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
        mSdpUITask = new SdpUITask();
        mSdpUITask.setSdpMessageListener(this);
        mSdpUITask.registerSdpNotify(CNotifyPeerUserMeetingInfo.SelfMessageId);
    }

    @OnClick({R.id.tv_watch, R.id.iv_add_user, R.id.iv_one_key_jinyan, R.id.iv_change_layout, R.id.iv_cancel})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.tv_watch:

                intent.setClass(MeetControlActivity.this, MeetWatchActivity.class);
                intent.putExtra("strMeetDomainCode", strMeetDomainCode);
                intent.putExtra("nMeetID", nMeetID);

                startActivity(intent);

                break;
            case R.id.iv_add_user:
                toInvistor();
                break;
            case R.id.iv_one_key_jinyan:
                onekeyJinYan();
                break;
            case R.id.iv_change_layout:
                intent.setClass(MeetControlActivity.this, MeetLayoutActivity.class);
                intent.putExtra("strMeetDomainCode", strMeetDomainCode);
                intent.putExtra("nMeetID", nMeetID);

                startActivity(intent);
                break;
            case R.id.iv_cancel:
                quitMeet();
                break;
        }
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
    public void doInitDelay() {
        rct_view.setLayoutManager(new LinearLayoutManager(this));

        MemberHolder.handUser.clear();
        adapter = new LiteBaseAdapter<>(this,
                listUser,
                MemberAdminHolder.class,
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

        refresh_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestDatas();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestDatas();
    }

    public void quitMeet() {

        final LogicDialog dialog = getLogicDialog().setMessageText("确定解散会议?");

        dialog.setConfirmText("取消");
        dialog.setCancelText("解散");

        dialog.setConfirmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HYClient.getModule(ApiMeet.class)
                        .quitMeeting(SdkParamsCenter.Meet.QuitMeet()
                                .setQuitMeetType(SdkBaseParams.QuitMeetType.Finish)
                                .setMeetDomainCode(strMeetDomainCode)
                                .setStopCapture(true)
                                .setMeetID(nMeetID), null);

                finish();
            }
        }).show();

    }

    void toInvistor() {
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

    /**
     * 邀请返回
     */
    void onInviteClicked() {
        final ArrayList<CStartMeetingReq.UserInfo> all = new ArrayList<>();
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            for (CStartMeetingReq.UserInfo temp : ChoosedContacts.get().convertContacts(ChoosedContacts.get().getContacts(false))) {
                if (!HYClient.getSdkOptions().User().getUserId().equals(temp.strUserID)) {
                    EncryptUtil.startEncrypt(true, temp.strUserID, temp.strUserDomainCode,
                            nMeetID + "", strMeetDomainCode, new SdkCallback<SdpMessageCmStartSessionRsp>() {
                                @Override
                                public void onSuccess(SdpMessageCmStartSessionRsp sessionRsp) {
                                    all.add(temp);
                                    if (ChoosedContacts.get().convertContacts(ChoosedContacts.get().getContacts(false)).indexOf(temp) == ChoosedContacts.get().convertContacts(ChoosedContacts.get().getContacts(false)).size() - 1) {
                                        invisitoMulite(all);
                                    }
                                }

                                @Override
                                public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                    if (ChoosedContacts.get().convertContacts(ChoosedContacts.get().getContacts(false)).indexOf(temp) == ChoosedContacts.get().convertContacts(ChoosedContacts.get().getContacts(false)).size() - 1) {
                                        invisitoMulite(all);
                                    }
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
            invisitoMulite(ChoosedContacts.get().convertContacts(ChoosedContacts.get().getContacts(false)));
        }
    }

    void invisitoMulite(ArrayList<CStartMeetingReq.UserInfo> all) {
        count = 0;
        cuntCount = 0;
        cuntCountSuccess = 0;

        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            for (CStartMeetingReq.UserInfo temp : all) {
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
            realInvisitor(all);
        }

    }

    private void realInvisitor(ArrayList<CStartMeetingReq.UserInfo> all) {
        HYClient.getModule(ApiMeet.class).inviteUser(SdkParamsCenter.Meet.InviteMeet()
                        .setMeetDomainCode(strMeetDomainCode)
                        .setMeetID(nMeetID)
                        .setUsers(all),
                new SdkCallback<CInviteUserMeetingRsp>() {
                    @Override
                    public void onSuccess(CInviteUserMeetingRsp cInviteUserMeetingRsp) {
                        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                            cuntCount++;
                            cuntCountSuccess++;
                            if (count == cuntCount) {
                                showToast("已邀请选中人员");
                                requestDatas();
                            }
                        } else {
                            if(nEncryptIMEnable) {
                                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                finish();
                                return;
                            }
                            showToast("已邀请选中人员");
                            requestDatas();
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
        ChoosedContacts.get().clear();
        ChoosedContacts.get().clearMeetUsers();
    }

    private void onekeyJinYan() {
        ArrayList<CMeetingSpeakSetReq.User> users = new ArrayList<>();
        int type = 1;
        if (iv_one_key_jinyan.getText().toString().equals("一键禁言")) {
            type = 0;
        }

        final int finalType = type;
        HYClient.getModule(ApiMeet.class)
                .mgrUserSpeaker(SdkParamsCenter.Meet.MgrUsrSpeaker()
                        .setMeetDomainCode(strMeetDomainCode)
                        .setMeetID(nMeetID)
                        .setnSetSpeakForAll(type)
                        .setUsers(users), new SdkCallback<CMeetingSpeakSetRsp>() {
                    @Override
                    public void onSuccess(CMeetingSpeakSetRsp cMeetingSpeakSetRsp) {
                        if (finalType == 0) {
                            showToast("全体禁言成功");
                            for (CGetMeetingInfoRsp.UserInfo temp : listUser) {
                                temp.nMuteStatus = 1;
                            }
                            iv_one_key_jinyan.setText("一键解禁");
                        } else {
                            showToast("全体解禁成功");
                            for (CGetMeetingInfoRsp.UserInfo temp : listUser) {
                                temp.nMuteStatus = 0;
                            }
                            iv_one_key_jinyan.setText("一键禁言");
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        if (finalType == 0) {
                            showToast(ErrorMsg.getMsg(ErrorMsg.jinyan_close_err_code));
                        } else {
                            showToast(ErrorMsg.getMsg(ErrorMsg.jinyan_open_err_code));
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
                        .setMeetDomainCode(strMeetDomainCode)
                        .setMeetID(nMeetID)
                        .setUsers(users), new SdkCallback<CMeetingSpeakSetRsp>() {
                    @Override
                    public void onSuccess(CMeetingSpeakSetRsp cMeetingSpeakSetRsp) {
                        if (userInfo.isSpeakerMute()) {
                        } else {
                            showToast("禁言成功");
                        }
                        userInfo.nMuteStatus = userInfo.isSpeakerMute() ? 0 : 1;
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        if (userInfo.isSpeakerMute()) {
                            showToast(ErrorMsg.getMsg(ErrorMsg.jinyan_open_err_code));
                        } else {
                            showToast(ErrorMsg.getMsg(ErrorMsg.jinyan_close_err_code));
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
                .setMeetDomainCode(strMeetDomainCode)
                .setMeetID(nMeetID)
                .addKickoutUser(user), new SdkCallback<CkickMeetingUserRsp>() {
            @Override
            public void onSuccess(CkickMeetingUserRsp ckickMeetingUserRsp) {
                showToast("踢出成功");
                listUser.remove(info);
                adapter.notifyDataSetChanged();
                requestDatas();
//                if (getActivity() != null) {
//                    ((MeetActivity) getActivity()).hideAll();
//                }
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                showToast(ErrorMsg.getMsg(ErrorMsg.kitout_err_code));
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

        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable &&
                !HYClient.getSdkOptions().User().getUserId().equals(user.strUserID)) {
            EncryptUtil.startEncrypt(true, user.strUserID, user.strUserDomainCode,
                    nMeetID + "", strMeetDomainCode, new SdkCallback<SdpMessageCmStartSessionRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmStartSessionRsp sessionRsp) {
                            reCallReal(user);
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            showToast("邀请失败");
                        }
                    });
        } else {
            if(nEncryptIMEnable && !HYClient.getSdkOptions().encrypt().isEncryptBind()) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                finish();
                return;
            }
            reCallReal(user);
        }

    }

    private void reCallReal(CStartMeetingReq.UserInfo user) {
        HYClient.getModule(ApiMeet.class).inviteUser(SdkParamsCenter.Meet.InviteMeet()
                .setMeetDomainCode(strMeetDomainCode)
                .setMeetID(nMeetID)
                .addUsers(user), new SdkCallback<CInviteUserMeetingRsp>() {
            @Override
            public void onSuccess(CInviteUserMeetingRsp cInviteUserMeetingRsp) {
                showToast("邀请成功");
//                if (getActivity() != null) {
//                    ((MeetActivity) getActivity()).hideAll();
//                }
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                showToast(ErrorMsg.getMsg(ErrorMsg.invite_user_err_code));
            }
        });
    }

    void requestDatas() {
        HYClient.getModule(ApiMeet.class).requestMeetDetail(
                SdkParamsCenter.Meet.RequestMeetDetail()
                        .setMeetDomainCode(strMeetDomainCode)
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
                        refresh_view.setRefreshing(false);
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast(ErrorMsg.getMsg(ErrorMsg.get_meet_info_err_code));
                        refresh_view.setRefreshing(false);
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyMeetingStatusInfo info) {
        if (info.nMeetingStatus == 2) {
            showToast("会议已结束");
            onBackPressed();
        } else {
            requestDatas();
        }
    }


    SdpUITask mSdpUITask;

    @Override
    public void onSdpMessage(SdpMessageBase sdpMessageBase, int i) {
        switch (sdpMessageBase.GetMessageType()) {
            case CNotifyPeerUserMeetingInfo.SelfMessageId:
                requestDatas();
                break;
        }
    }

    private void destruct() {
        if (mSdpUITask != null) {
            mSdpUITask.exit();
            mSdpUITask = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destruct();
        EventBus.getDefault().unregister(this);
    }
}
