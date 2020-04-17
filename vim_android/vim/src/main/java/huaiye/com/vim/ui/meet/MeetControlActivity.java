package huaiye.com.vim.ui.meet;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.cmf.sdp.SdpMessageCmStartSessionRsp;
import com.huaiye.cmf.sdp.SdpUITask;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.meet.ParamsMeetKickout;
import com.huaiye.sdk.sdkabi._params.meet.ParamsSetMeetingKeynoteSpeaker;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CInviteUserMeetingRsp;
import com.huaiye.sdk.sdpmsgs.meet.CMeetingSpeakSetReq;
import com.huaiye.sdk.sdpmsgs.meet.CMeetingSpeakSetRsp;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingRaiseInfo;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingStatusInfo;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyPeerUserMeetingInfo;
import com.huaiye.sdk.sdpmsgs.meet.CSetMeetingKeynoteSpeakerRsp;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;
import com.huaiye.sdk.sdpmsgs.meet.CkickMeetingUserRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.constant.SPConstant;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.ContactsApi;
import huaiye.com.vim.models.contacts.bean.ContactsBean;
import huaiye.com.vim.ui.meet.basemodel.RaiseMessage;
import huaiye.com.vim.ui.meet.views.MoreControlWindow;
import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;

import static com.huaiye.sdk.HYClient.getContext;
import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * Created by ywt on 2019/3/6.
 */
@BindLayout(R.layout.activity_meet_control)
public class MeetControlActivity extends AppBaseActivity implements SdpUITask.SdpUIListener {
    @BindView(R.id.control_joined)
    TextView control_joined;
    @BindView(R.id.joined_recyclerview)
    RecyclerView joined_recyclerview;
    @BindView(R.id.control_no_join)
    TextView control_no_join;
    @BindView(R.id.no_join_recyclerview)
    RecyclerView no_join_recyclerview;

    @BindExtra
    public int nMeetID;
    @BindExtra
    public String strMeetDomainCode;
    @BindExtra
    public ArrayList<String> mRaiseUsers;

    private EXTRecyclerAdapter<CGetMeetingInfoRsp.UserInfo> mMeetingAdapter;
    private EXTRecyclerAdapter<CGetMeetingInfoRsp.UserInfo> mNoMeetingAdapter;
    private CGetMeetingInfoRsp mCGetMeetingInfoRsp;
    private ArrayList<CGetMeetingInfoRsp.UserInfo> mJoinedList = new ArrayList<>();
    private ArrayList<CGetMeetingInfoRsp.UserInfo> mNoJoinedList = new ArrayList<>();
    private boolean isAllInMute;
    SdpUITask mSdpUITask;
    private RequestOptions requestFriendHeadOptions;


    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(getString(R.string.title_notice7))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        EventBus.getDefault().register(this);
        mSdpUITask = new SdpUITask();
        mSdpUITask.setSdpMessageListener(this);
        mSdpUITask.registerSdpNotify(CNotifyPeerUserMeetingInfo.SelfMessageId);
        mSdpUITask.registerSdpNotify(CNotifyMeetingRaiseInfo.SelfMessageId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSdpUITask != null) {
            mSdpUITask.exit();
            mSdpUITask = null;
        }
        EventBus.getDefault().unregister(this);
    }

    private void initHeadRequestOption() {
        requestFriendHeadOptions = new RequestOptions();
        requestFriendHeadOptions.centerCrop()
                .dontAnimate()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.default_image_personal)
                .error(R.drawable.default_image_personal)
                .optionalTransform(new CircleCrop());
    }

    @Override
    public void doInitDelay() {
        initHeadRequestOption();
        mMeetingAdapter = new EXTRecyclerAdapter<CGetMeetingInfoRsp.UserInfo>(R.layout.meet_control_item) {
            @Override
            public void onBindViewHolder(EXTViewHolder extViewHolder, int i, final CGetMeetingInfoRsp.UserInfo userInfo) {
                if (mRaiseUsers != null && mRaiseUsers.contains(userInfo.strUserID)) {
                    extViewHolder.setVisibility(R.id.tv_user_jushou, View.VISIBLE);
                } else {
                    extViewHolder.setVisibility(R.id.tv_user_jushou, View.GONE);
                }

                extViewHolder.setText(R.id.tv_user_name, userInfo.strUserName);
                if (userInfo.nMicStatus == 0) {
                    extViewHolder.setImageResouce(R.id.tv_mic, R.drawable.kongzhi_ico_kaimai);
                } else {
                    extViewHolder.setImageResouce(R.id.tv_mic, R.drawable.kongzhi_ico_jingmai);
                }
                extViewHolder.setVisibility(R.id.tv_mic, View.VISIBLE);
                if (userInfo.nDevType == 1) {
                    //app
                    extViewHolder.setImageResouce(R.id.tv_device, R.drawable.ico_phone);
                } else if (userInfo.nDevType == 2) {
                    //pc
                    extViewHolder.setImageResouce(R.id.tv_device, R.drawable.ico_pc);
                } else if (userInfo.nDevType == 3) {
                    //固定设备
                    extViewHolder.setVisibility(R.id.tv_mic, View.GONE);
                    extViewHolder.setImageResouce(R.id.tv_device, R.drawable.ico_zhongduan);
                } else {
                    //平板
                    extViewHolder.setImageResouce(R.id.tv_device, R.drawable.ico_pad);
                }
                if (userInfo.strUserTokenID.equals(mCGetMeetingInfoRsp.strKeynoteSpeakerTokenID)) {
                    //主讲人
                    extViewHolder.setVisibility(R.id.tv_speaker, View.VISIBLE);
                } else {
                    extViewHolder.setVisibility(R.id.tv_speaker, View.GONE);
                }
//                extViewHolder.setImageResouce(R.id.tv_clear, R.drawable.kongzhi_ico_qingchu);

                /*extViewHolder.findViewById(R.id.tv_mic).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        jinyan(userInfo);
                    }
                });

                extViewHolder.findViewById(R.id.tv_clear).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        kitOut(userInfo);
                    }
                });*/

                /* 如果是主持人，则显示更多选项，否则隐藏 */
                if (mCGetMeetingInfoRsp.strMainUserID.equals(String.valueOf(AppDatas.Auth().getUserID()))) {
                    extViewHolder.setVisibility(R.id.tv_more, View.VISIBLE);
                } else {
                    extViewHolder.setVisibility(R.id.tv_more, View.GONE);
                }

                extViewHolder.setVisibility(R.id.tv_chonghu, View.GONE);
                View view = extViewHolder.findViewById(R.id.tv_more_layout);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*PopupMenu popupMenu = new PopupMenu(MeetControlActivity.this, v);
//                        popupMenu.inflate(R.menu.control_more);
//                        popupMenu.getMenuInflater().inflate(R.menu.control_more, popupMenu.getMenu());
//                        popupMenu.getMenu().findItem(R.id.control_jingyan).setTitle("取消禁言");
                        popupMenu.getMenu().add(1, 1, 1, R.string.jingyan);
                        popupMenu.getMenu().add(1, 2, 2, R.string.zhujiangren);
                        popupMenu.getMenu().add(1, 3, 3, R.string.kick_out);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.control_jingyan:
                                        jinyan(userInfo);
                                        break;
                                    case R.id.control_zhujiangren:
//                                        HYClient.getModule(ApiMeet.class).setMeetingKeynoteSpeaker();
                                        break;
                                    case R.id.control_kick_out:
                                        kitOut(userInfo);
                                        break;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();*/
                        MoreControlWindow window = new MoreControlWindow(MeetControlActivity.this, userInfo, mCGetMeetingInfoRsp.strKeynoteSpeakerTokenID);
                        window.setConfirmClickListener(new MoreControlWindow.ConfirmClickListener() {
                            @Override
                            public void onJingyanClick(CGetMeetingInfoRsp.UserInfo userInfo) {
                                jinyan(userInfo);
                            }

                            @Override
                            public void onZhujiangrenClick(CGetMeetingInfoRsp.UserInfo userInfo) {
                                setSpeaker(userInfo);
                            }

                            @Override
                            public void onKickOutClick(CGetMeetingInfoRsp.UserInfo userInfo) {
                                kitOut(userInfo);
                            }

                            @Override
                            public void onCancelSpeakerClick(CGetMeetingInfoRsp.UserInfo userInfo) {
                                cancelSpeaker(userInfo);
                            }
                        });

                        /* 显示会议控制下拉框 */
                        View tv_more = findViewById(R.id.tv_more);
                        window.showView(tv_more);
                    }
                });
                ImageView imageView = extViewHolder.findViewById(R.id.iv_user_head);
                setHeadViewPic(imageView, userInfo);
            }
        };
        mNoMeetingAdapter = new EXTRecyclerAdapter<CGetMeetingInfoRsp.UserInfo>(R.layout.meet_control_item) {
            @Override
            public void onBindViewHolder(EXTViewHolder extViewHolder, int i, final CGetMeetingInfoRsp.UserInfo userInfo) {
                extViewHolder.setText(R.id.tv_user_name, userInfo.strUserName);
                extViewHolder.setVisibility(R.id.tv_mic, View.GONE);
                extViewHolder.setVisibility(R.id.tv_user_jushou, View.GONE);
                if (userInfo.nDevType == 1) {
                    //app
                    extViewHolder.setImageResouce(R.id.tv_device, R.drawable.ico_phone);
                } else if (userInfo.nDevType == 2) {
                    //pc
                    extViewHolder.setImageResouce(R.id.tv_device, R.drawable.ico_pc);
                } else if (userInfo.nDevType == 3) {
                    //固定设备
                    extViewHolder.setImageResouce(R.id.tv_device, R.drawable.ico_zhongduan);
                } else {
                    //平板
                    extViewHolder.setImageResouce(R.id.tv_device, R.drawable.ico_pad);
                }
                if (userInfo.strUserID.equals(mCGetMeetingInfoRsp.strKeynoteSpeakerUserID)) {
                    //主讲人
                    extViewHolder.setVisibility(R.id.tv_speaker, View.VISIBLE);
                } else {
                    extViewHolder.setVisibility(R.id.tv_speaker, View.GONE);
                }
                extViewHolder.setVisibility(R.id.tv_more, View.GONE);
                extViewHolder.setVisibility(R.id.tv_chonghu, View.VISIBLE);

                extViewHolder.findViewById(R.id.tv_more_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reCall(userInfo);
                    }
                });
                ImageView imageView = extViewHolder.findViewById(R.id.iv_user_head);
                setHeadViewPic(imageView, userInfo);
            }
        };
        joined_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        joined_recyclerview.setHasFixedSize(true);
        joined_recyclerview.setNestedScrollingEnabled(false);
        joined_recyclerview.setAdapter(mMeetingAdapter);
        no_join_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        joined_recyclerview.setHasFixedSize(true);
        joined_recyclerview.setNestedScrollingEnabled(false);
        no_join_recyclerview.setAdapter(mNoMeetingAdapter);

        requestInfo();
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
                                mCGetMeetingInfoRsp = cGetMeetingInfoRsp;
                                mJoinedList.clear();
                                mNoJoinedList.clear();
                                for (CGetMeetingInfoRsp.UserInfo item : cGetMeetingInfoRsp.listUser) {
                                    if (item.nJoinStatus == 2) {
                                        mJoinedList.add(item);
                                    } else {
                                        mNoJoinedList.add(item);

                                        if (mRaiseUsers != null && mRaiseUsers.contains(item.strUserID)) {
                                            //退出会议取消举手
                                            RaiseMessage msg = new RaiseMessage();
                                            msg.code = 1;
                                            msg.userId = item.strUserID;
                                            EventBus.getDefault().post(msg);
                                            mRaiseUsers.remove(item.strUserID);
                                        }
                                    }
                                }
                                control_joined.setText(MeetControlActivity.this.getString(R.string.already_join, mJoinedList.size()));
                                control_no_join.setText(MeetControlActivity.this.getString(R.string.no_join, mNoJoinedList.size()));
                                mNoMeetingAdapter.setDatas(mNoJoinedList);
                                mNoMeetingAdapter.notifyDataSetChanged();
                                mMeetingAdapter.setDatas(mJoinedList);
                                mMeetingAdapter.notifyDataSetChanged();

                                /* 如果是主持人，则显示一键禁言/一键解禁 */
                                if (mCGetMeetingInfoRsp.strMainUserID.equals(String.valueOf(AppDatas.Auth().getUserID()))) {
                                    String text;
                                    if (cGetMeetingInfoRsp.nMuteStatus == 1) {
                                        isAllInMute = true;
                                        text = getString(R.string.meet_yijianjiejin);
                                    } else {
                                        isAllInMute = false;
                                        text = getString(R.string.meet_yijianjinyan);
                                    }
                                    getNavigate().setRightText(text)
                                            .setRightTextColor(ContextCompat.getColor(getContext(), R.color.blue_2E67FE))
                                            .setRightClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    onekeyJinYan();
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {

                            }
                        });
    }

    /**
     * 禁言或者解禁
     */
    private void jinyan(final CGetMeetingInfoRsp.UserInfo userInfo) {
        if (mCGetMeetingInfoRsp == null) {
            return;
        }

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
                            //解除禁言，需要通知会议界面删除对应的举手人
                            RaiseMessage msg = new RaiseMessage();
                            msg.code = 1;
                            msg.userId = userInfo.strUserID;
                            EventBus.getDefault().post(msg);
                            if (mRaiseUsers != null && mRaiseUsers.contains(userInfo.strUserID)) {
                                mRaiseUsers.remove(userInfo.strUserID);
                            }
                        } else {
                            showToast(getString(R.string.meet_jinyanchenggong));
                        }
                        userInfo.nMuteStatus = userInfo.isSpeakerMute() ? 0 : 1;
                        mMeetingAdapter.notifyDataSetChanged();
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
     * 设置主讲人
     */
    private void setSpeaker(CGetMeetingInfoRsp.UserInfo info) {
        if (mCGetMeetingInfoRsp == null) {
            return;
        }

        ParamsSetMeetingKeynoteSpeaker speaker = new ParamsSetMeetingKeynoteSpeaker();
        speaker.setStrMeetingDomainCode(strMeetDomainCode);
        speaker.setnMeetingID(nMeetID);
        speaker.strKeynoteSpeakerDomainCode = info.strUserDomainCode;
        speaker.strKeynoteSpeakerTokenID = info.strUserTokenID;

        HYClient.getModule(ApiMeet.class).setMeetingKeynoteSpeaker(speaker,
                new SdkCallback<CSetMeetingKeynoteSpeakerRsp>() {
                    @Override
                    public void onSuccess(CSetMeetingKeynoteSpeakerRsp o) {
                        showToast(getString(R.string.meet_notice3));
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        Log.d("VIMApp", "onError = " + errorInfo.getMessage());
                        Log.d("VIMApp", "onError code = " + errorInfo.getCode());
                        showToast(getString(R.string.meet_notice4));
                    }
                });
    }

    /**
     * 取消主讲人
     */
    private void cancelSpeaker(CGetMeetingInfoRsp.UserInfo info) {
        if (mCGetMeetingInfoRsp == null) {
            return;
        }

        ParamsSetMeetingKeynoteSpeaker speaker = new ParamsSetMeetingKeynoteSpeaker();
        speaker.setStrMeetingDomainCode(strMeetDomainCode);
        speaker.setnMeetingID(nMeetID);
        speaker.strKeynoteSpeakerDomainCode = "";
        speaker.strKeynoteSpeakerTokenID = "";

        HYClient.getModule(ApiMeet.class).setMeetingKeynoteSpeaker(speaker,
                new SdkCallback<CSetMeetingKeynoteSpeakerRsp>() {
                    @Override
                    public void onSuccess(CSetMeetingKeynoteSpeakerRsp o) {
                        showToast(getString(R.string.meet_notice5));
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        Log.d("VIMApp", "onError = " + errorInfo.getMessage());
                        Log.d("VIMApp", "onError code = " + errorInfo.getCode());
                        showToast(getString(R.string.meet_notice6));
                    }
                });
    }

    /**
     * 踢出
     */
    private void kitOut(final CGetMeetingInfoRsp.UserInfo info) {
        if (mCGetMeetingInfoRsp == null) {
            return;
        }

        if (String.valueOf(AppDatas.Auth().getUserID()).equals(info.strUserID)) {
            showToast(getString(R.string.meet_notice7));
            return;
        }
        ParamsMeetKickout.User user = new ParamsMeetKickout.User();
        user.setDomainCode(info.strUserDomainCode);
        user.setUserID(info.strUserID);
        HYClient.getModule(ApiMeet.class).kickoutUser(SdkParamsCenter.Meet.KickoutMeet()
                .setMeetDomainCode(strMeetDomainCode)
                .setMeetID(nMeetID)
                .addKickoutUser(user), new SdkCallback<CkickMeetingUserRsp>() {
            @Override
            public void onSuccess(CkickMeetingUserRsp ckickMeetingUserRsp) {
                showToast(getString(R.string.meet_tichuchenggong));
                requestInfo();
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
        if (mCGetMeetingInfoRsp == null) {
            return;
        }

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
                            showToast(getString(R.string.meet_invitor_error));
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
                showToast(getString(R.string.meet_invitor_success));
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                showToast(ErrorMsg.getMsg(ErrorMsg.invite_user_err_code));
            }
        });
    }

    private void onekeyJinYan() {
        if (mCGetMeetingInfoRsp == null) {
            return;
        }

        ArrayList<CMeetingSpeakSetReq.User> users = new ArrayList<>();
        HYClient.getModule(ApiMeet.class)
                .mgrUserSpeaker(SdkParamsCenter.Meet.MgrUsrSpeaker()
                        .setMeetDomainCode(strMeetDomainCode)
                        .setMeetID(nMeetID)
                        .setnSetSpeakForAll(isAllInMute ? 1 : 0)
                        .setUsers(users), new SdkCallback<CMeetingSpeakSetRsp>() {
                    @Override
                    public void onSuccess(CMeetingSpeakSetRsp cMeetingSpeakSetRsp) {
                        if (!isAllInMute) {
                            isAllInMute = true;
                            showToast(getString(R.string.meet_quantijinyan));
                            for (CGetMeetingInfoRsp.UserInfo temp : mJoinedList) {
                                temp.nMuteStatus = 1;
                            }
                            getNavigate().setRightText(getString(R.string.meet_yijianjiejin));
                        } else {
                            isAllInMute = false;
                            showToast(getString(R.string.meet_quantijiejin));
                            for (CGetMeetingInfoRsp.UserInfo temp : mJoinedList) {
                                temp.nMuteStatus = 0;
                            }
                            getNavigate().setRightText(getString(R.string.meet_yijianjinyan));

                            //解除禁言，需要通知会议界面删除对应的举手人
                            RaiseMessage msg = new RaiseMessage();
                            msg.code = 2;
                            msg.userId = null;
                            EventBus.getDefault().post(msg);
                            if (mRaiseUsers != null) {
                                mRaiseUsers.clear();
                            }
                        }
                        mMeetingAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        if (!isAllInMute) {
                            showToast(ErrorMsg.getMsg(ErrorMsg.jinyan_close_err_code));
                        } else {
                            showToast(ErrorMsg.getMsg(ErrorMsg.jinyan_open_err_code));
                        }

                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyMeetingStatusInfo info) {
        if (info.nMeetingStatus == 2) {
            showToast(getString(R.string.meet_has_end));
            onBackPressed();
        } else {
            requestInfo();
        }
    }

    @Override
    public void onSdpMessage(SdpMessageBase sdpMessageBase, int i) {
        switch (sdpMessageBase.GetMessageType()) {
            case CNotifyPeerUserMeetingInfo.SelfMessageId:
                //通知邀请方对方参加会议意见
                requestInfo();
                break;
            case CNotifyMeetingRaiseInfo.SelfMessageId:
                // 举手
                CNotifyMeetingRaiseInfo raiseInfo = (CNotifyMeetingRaiseInfo) sdpMessageBase;
                if (raiseInfo.nMeetingID != nMeetID
                        || !raiseInfo.strMeetingDomainCode.equals(strMeetDomainCode)) {
                    return;
                }
                if (mRaiseUsers == null || !mRaiseUsers.contains(raiseInfo.strUserID)) {
                    if (mRaiseUsers == null) {
                        mRaiseUsers = new ArrayList<>();
                    }
                    mRaiseUsers.add(raiseInfo.strUserID);
                    mMeetingAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private void setHeadViewPic(ImageView meet_head_img, CGetMeetingInfoRsp.UserInfo newUserInfo) {
        if (null == meet_head_img) {
            return;
        }
        String headPic;
        if (!newUserInfo.strUserID.equals(AppAuth.get().getUserID() + "")) {
            headPic = AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(newUserInfo.strUserID, newUserInfo.strUserDomainCode);
        } else {
            headPic = AppDatas.Auth().getHeadUrl(AppDatas.Auth().getUserID() + SPConstant.STR_HEAD_URL);
        }

        if (!TextUtils.isEmpty(headPic)) {
            Glide.with(getContext())
                    .load(AppDatas.Constants().getAddressWithoutPort() + headPic)
                    .apply(requestFriendHeadOptions)
                    .into(meet_head_img);
        } else {
            String mapKey = newUserInfo.strUserDomainCode;
            List<String> mapValue = new ArrayList<>();
            mapValue.add(newUserInfo.strUserID);
            ContactsApi.get().requestUserInfoList(mapKey, mapValue, new ModelCallback<ContactsBean>() {
                @Override
                public void onSuccess(ContactsBean contactsBean) {

                    if (null != contactsBean && null != contactsBean.userList && contactsBean.userList.size() == 1) {
                        AppDatas.MsgDB().getFriendListDao().insertAll(contactsBean.userList);
                        String headPic = AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(newUserInfo.strUserID, mapKey);
                        Glide.with(getContext())
                                .load(AppDatas.Constants().getAddressWithoutPort() + headPic)
                                .apply(requestFriendHeadOptions)
                                .into(meet_head_img);
                    }
                }
            });
        }

    }
}
