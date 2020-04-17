package huaiye.com.vim.ui.meet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.meet.ParamsAppointmentSetMeet;
import com.huaiye.sdk.sdpmsgs.meet.CDelMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CSendNotifyPredetermineMeetingRsp;
import com.huaiye.sdk.sdpmsgs.meet.CSetPredetermineMeetingReq;
import com.huaiye.sdk.sdpmsgs.meet.CSetPredetermineMeetingRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import java.util.ArrayList;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.constant.CommonConstant;
import huaiye.com.vim.common.dialog.LogicDialog;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.bean.ContactData;
import huaiye.com.vim.models.contacts.bean.ContactsBean;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContactsNew;
import huaiye.com.vim.ui.contacts.sharedata.ConvertContacts;
import huaiye.com.vim.ui.meet.views.MeetDetailHeaderView;
import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;

/**
 * author: admin
 * date: 2018/01/15
 * version: 0
 * mail: secret
 * desc: MeetCreateActivity
 */
@BindLayout(R.layout.activity_meet_detail)
public class MeetDetailActivity extends AppBaseActivity {
    @BindView(R.id.meet_detail_share)
    ImageView meet_detail_share;
    @BindView(R.id.meet_name)
    TextView meet_name;
    @BindView(R.id.meet_edit)
    ImageView meet_edit;
    @BindView(R.id.meet_detail)
    TextView meet_detail;
    @BindView(R.id.meet_time)
    TextView meet_time;
    @BindView(R.id.meet_recycler)
    RecyclerView meet_recycler;
    @BindView(R.id.tv_voice_meet)
    TextView tv_voice_meet;
    @BindView(R.id.tv_enter)
    TextView tv_enter;

    @BindExtra
    String strInviteUserId;
    @BindExtra
    String strMeetDomainCode;
    @BindExtra
    int nMeetID;
    @BindExtra
    int nStatus;

    boolean isMaster;
    CGetMeetingInfoRsp info;

    //    MeetCreateHeaderView header;
    MeetDetailHeaderView mHeaderView;
    EXTRecyclerAdapter<User> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    protected void initActionBar() {
        /*getNavigate().setTitlText("会议详情")
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });*/
        if (nStatus == 1) {
            //会议进行中
            tv_enter.setText(R.string.join_meet);
            tv_voice_meet.setText(R.string.join_voice_meet);
//            meet_detail_share.setVisibility(View.GONE);
        } else {
            //未开始会议
            tv_enter.setText(R.string.start_meet);
            tv_voice_meet.setText(R.string.delete_meet);
//            meet_detail_share.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void doInitDelay() {
        mZeusLoadView.loadingText("正在加载").setLoading();
        meet_recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EXTRecyclerAdapter<User>(R.layout.item_meetcreate_member) {
            @Override
            public void onBindViewHolder(EXTViewHolder extViewHolder, int i, User contactData) {
                if (i < getHeaderViewsCount()) {
                    return;
                }

                if (contactData.strUserID.equals(info.strMainUserID)) {
                    extViewHolder.setVisibility(R.id.tv_master, View.VISIBLE);
                } else {
                    extViewHolder.setVisibility(R.id.tv_master, View.GONE);
                }
                extViewHolder.setText(R.id.tv_user_name, contactData.strUserName);
                if (contactData.nStatus > 0) {
//                    extViewHolder.setImageResouce(R.id.iv_user_head, R.drawable.ic_user);
                    extViewHolder.setImageResouce(R.id.iv_user_head, R.drawable.default_image_personal);
                } else {
                    extViewHolder.setImageResouce(R.id.iv_user_head, R.drawable.default_image_personal);
                }
                /*if (map.containsKey(contactData.loginName)) {
                    if (map.get(contactData.loginName).nState == 2 ||
                            map.get(contactData.loginName).nState == 3 ||
                            map.get(contactData.loginName).nState == 4) {
                        extViewHolder.setImageResouce(R.id.tv_user_status, R.drawable.dian_mang);
                    } else {
                        extViewHolder.setImageResouce(R.id.tv_user_status, R.drawable.dian_zaixian);
                    }
                } else {
                    extViewHolder.setImageResouce(R.id.tv_user_status, R.drawable.dian_lixian);
                }*/
            }
        };

        /*header = new MeetCreateHeaderView(this, true, false);
        adapter.addHeaderView(header);*/
        mHeaderView = new MeetDetailHeaderView(this, nStatus == 1);
        adapter.addHeaderView(mHeaderView);
        meet_recycler.setAdapter(adapter);

//        requestOnLine(true);
        requestInfo();
    }

    @OnClick({R.id.tv_enter, R.id.tv_voice_meet, R.id.meet_detail_back, R.id.meet_detail_share})
    public void clickView(View view) {
        switch (view.getId()) {
            case R.id.meet_detail_back:
                finish();
                break;
            case R.id.meet_detail_share:
                shareMeeting();
                break;
            case R.id.tv_enter:
                entMeeting();
                break;
            case R.id.tv_voice_meet:
                deleteOrStartVoiceMeet();
                break;
            default:
                break;
        }
    }

    /**
     * 音频入会/删除会议
     */
    private void deleteOrStartVoiceMeet() {
        if (nStatus == 1) {
            //音频入会
            Intent intent = new Intent(this, MeetNewActivity.class);
            intent.putExtra("strMeetDomainCode", strMeetDomainCode);
            intent.putExtra("isMeetStarter", isMaster);
            intent.putExtra("strInviteUserId", strInviteUserId);
            intent.putExtra("nMeetID", nMeetID);
            intent.putExtra("isWatch", false);
            intent.putExtra("mMediaMode", SdkBaseParams.MediaMode.Audio);
            intent.putExtra("isCloseVideo", true);
            startActivity(intent);
        } else {
            //删除会议
            final LogicDialog logicDialog = new LogicDialog(this);
            logicDialog.setMessageText("是否删除会议？");
            logicDialog.setConfirmClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HYClient.getModule(ApiMeet.class)
                            .deleteAppointmentMeeting(SdkParamsCenter.Meet.AppointmentDeleteMeet()
                                    .setnMeetingID(nMeetID)
                                    .setStrDomainCode(strMeetDomainCode), new SdkCallback<CDelMeetingInfoRsp>() {
                                @Override
                                public void onSuccess(CDelMeetingInfoRsp info) {
                                    showToast("删除成功");
                                    finish();
                                }

                                @Override
                                public void onError(ErrorInfo errorInfo) {
                                    showToast(ErrorMsg.getMsg(ErrorMsg.delete_meet_err_code));
                                }
                            });
                }
            }).setCancelClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logicDialog.dismiss();
                }
            }).show();
        }
        /*if (info.nRecordID == 0) {
            showToast("该会议无会议记录");
            return;
        }
        Intent intent = new Intent(this, MeetPlaybackActivity.class);
        intent.putExtra("nMeetID", nMeetID);
        intent.putExtra("strMeetDomainCode", strMeetDomainCode);
        startActivity(intent);*/
    }

    /**
     * 进入会议/开始会议
     */
    private void entMeeting() {
        if (nStatus == 1) {
            Intent intent = new Intent(this, MeetNewActivity.class);
            intent.putExtra("strMeetDomainCode", strMeetDomainCode);
            intent.putExtra("isMeetStarter", isMaster);
            intent.putExtra("nMeetID", nMeetID);
            intent.putExtra("strInviteUserId", strInviteUserId);
            intent.putExtra("mMediaMode", SdkBaseParams.MediaMode.AudioAndVideo);
            startActivity(intent);
        } else {
            //开始预约会议
            if (!isMaster) {
                showToast("只有会议主持人才能开始会议");
                return;
            }
            startOrderMeet();
        }
    }

    //开始预约会议
    private void startOrderMeet() {
        if (info == null) {
            return;
        }
        //开始预约会议
        int inviteSelf = 0;
        for(CGetMeetingInfoRsp.UserInfo user : info.listUser){
            if(user.strUserID.equals(String.valueOf(AppDatas.Auth().getUserID()))){
                inviteSelf = 1;
                break;
            }
        }
        ParamsAppointmentSetMeet params = SdkParamsCenter.Meet.AppointmentSetMeet();
        params.setInviteSelf(inviteSelf);
        params.setUsers(ConvertContacts.ConvertMeetingUserToPredetermineMeetMeetUser(info.listUser))
                .setOpenRecord(false)
                .setDtMeetingStartTime(info.strStartTime)
                .setMeetMode(info.getMeetMode())
                .setnMeetingDuration(info.nTimeDuration)
                .setMeetName(info.strMeetingName)
                .setMeetDesc(info.strMeetingDesc)
                .setnStartImmediately(1);

        HYClient.getModule(ApiMeet.class)
                .setAppointmentMeeting(params, new SdkCallback<CSetPredetermineMeetingRsp>() {
                    @Override
                    public void onSuccess(CSetPredetermineMeetingRsp info) {
                        showToast("开始会议成功");
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        if (errorInfo.getCode() == 1720410011) {
                            showToast("会议时间必须大于当前时间");
                        } else {
                            showToast(ErrorMsg.getMsg(ErrorMsg.create_meet_err_code));
                        }
                    }
                });
    }

    private void shareMeeting() {
        if (info == null) {
            showToast("会议不存在");
            return;
        }
        String copy = AppDatas.Auth().getUserName()
                + "邀请你于"
                + info.strStartTime
                + "参加"
                + info.strMeetingName
                + "，请按时参加。点击链接即可跳转: "
                + "http://" + AppDatas.Constants().getAddressIP()
                + ":"
                + AppDatas.Constants().getAddressPort()
                + "/mchtml/geturl.html?scherm=huaiyemc://?meetId="
                + nMeetID
                + "&end";
        AppUtils.copyAndPass(this, copy);
        showToast("复制到剪切板，请分享");
    }

    ArrayList<ContactData> convertToAdapter(ArrayList<CGetMeetingInfoRsp.UserInfo> contacts) {
        ArrayList<ContactData> users = new ArrayList<>();

        for (CGetMeetingInfoRsp.UserInfo tmp : contacts) {
//            if (!tmp.strUserID.equals(AppDatas.Auth().getUserLoginName())) {
            ContactData user = new ContactData();

            user.loginName = tmp.strUserID;
            user.name = tmp.strUserName;
//            user.userId = Integer.parseInt(tmp.strUserID);
            user.domainCode = tmp.strUserDomainCode;
            user.naviKey = "";

            users.add(user);
//            }
        }

        return users;
    }

    private void requestInfo() {
        Log.d("VIMApp", "requestInfo nMeetID="+nMeetID);
        Log.d("VIMApp", "requestInfo strMeetDomainCode="+strMeetDomainCode);
        HYClient.getModule(ApiMeet.class)
                .requestMeetDetail(SdkParamsCenter.Meet.RequestMeetDetail()
                                .setnListMode(1)
                                .setMeetID(nMeetID)
                                .setMeetDomainCode(strMeetDomainCode),
                        new SdkCallback<CGetMeetingInfoRsp>() {
                            @Override
                            public void onSuccess(CGetMeetingInfoRsp cGetMeetingInfoRsp) {
                                Log.d("VIMApp", "requestInfo onSuccess");
                                mZeusLoadView.dismiss();

                                isMaster = cGetMeetingInfoRsp.strMainUserID.equals(String.valueOf(AppAuth.get().getUserID()));
                                info = cGetMeetingInfoRsp;

                                /*if (cGetMeetingInfoRsp.nStatus == 1) {
                                    tv_enter.setVisibility(View.VISIBLE);
                                } else if (cGetMeetingInfoRsp.nStatus == 2) {
                                    tv_voice_meet.setVisibility(View.VISIBLE);
                                }*/

//                                header.setMaster(false);
                                mHeaderView.showInfo(cGetMeetingInfoRsp);
                                requestContacts(cGetMeetingInfoRsp.listUser);
//                                ChoosedContacts.get().setContacts(convertToAdapter(cGetMeetingInfoRsp.listUser));
//                                adapter.setDatas(ChoosedContacts.get().getContacts(null, false));
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                Log.d("VIMApp", "requestInfo onError");
                                mZeusLoadView.dismiss();

                                showToast(ErrorMsg.getMsg(errorInfo.getCode()));
//                                header.setMaster(false);
                            }
                        });
    }

    private void requestContacts(ArrayList<CGetMeetingInfoRsp.UserInfo> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        ArrayList<String> tempList = new ArrayList<String>();
        for (CGetMeetingInfoRsp.UserInfo item : list) {
            tempList.add(item.strUserID);
        }
        ModelApis.Contacts().requestSpecificContacts(tempList, new ModelCallback<ContactsBean>() {
            @Override
            public void onSuccess(final ContactsBean contactsBean) {
                new RxUtils<ArrayList<User>>()
                        .doOnThreadObMain(new RxUtils.IThreadAndMainDeal<ArrayList<User>>() {
                            @Override
                            public ArrayList<User> doOnThread() {
                                return contactsBean.userList;
                            }

                            @Override
                            public void doOnMain(ArrayList<User> data) {
                                ChoosedContactsNew.get().clear();
                                ChoosedContactsNew.get().setContacts(data);
                                adapter.setDatas(ChoosedContactsNew.get().getContacts());
                                adapter.notifyDataSetChanged();
                            }
                        });
            }
        });
    }

    @Override
    protected void afterOnLineUser(boolean value) {
        super.afterOnLineUser(value);
        if (value) {
            requestInfo();
        } else {
            /*adapter.setDatas(ChoosedContacts.get().getContacts(null, false));
            adapter.notifyDataSetChanged();*/
        }
    }

    /**
     * 编辑会议
     */
    void editMeet() {
        if (info == null) {
            return;
        }
        int inviteSelf = 1;
        if (ChoosedContactsNew.get().isContain(String.valueOf(AppDatas.Auth().getUserID()))) {
            inviteSelf = 0;
        }
        ParamsAppointmentSetMeet params = SdkParamsCenter.Meet.AppointmentSetMeet()
//                .setInviteSelf(ChoosedContacts.get().isDeleteSelf())
                .setInviteSelf(inviteSelf)
                .setUsers(convertContacts(ChoosedContactsNew.get().getContacts()))
                .setOpenRecord(false)
                .setnMeetingID(info.nMeetingID)
                .setMeetDesc(info.strMeetingDesc)
                .setDtMeetingStartTime(info.strStartTime)
                .setMeetMode(info.nMeetingMode == 2 ? SdkBaseParams.MeetMode.Host : SdkBaseParams.MeetMode.Normal)
                .setnMeetingDuration(info.nTimeDuration)
                .setMeetName(info.strMeetingName);


        HYClient.getModule(ApiMeet.class)
                .setAppointmentMeeting(params, new SdkCallback<CSetPredetermineMeetingRsp>() {
                    @Override
                    public void onSuccess(CSetPredetermineMeetingRsp cStartMeetingRsp) {
                        pushNotify(false);
                        showToast("参会人修改成功");
                        adapter.setDatas(ChoosedContactsNew.get().getContacts());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast(ErrorMsg.getMsg(ErrorMsg.update_meet_err_code));
                    }
                });
    }

    ArrayList<CSetPredetermineMeetingReq.UserInfo> convertContacts(ArrayList<User> contacts) {

        ArrayList<CSetPredetermineMeetingReq.UserInfo> users = new ArrayList<>();

        for (User tmp : contacts) {
            CSetPredetermineMeetingReq.UserInfo user = new CSetPredetermineMeetingReq.UserInfo();
            if (tmp.deviceType == 2) {
                user.setDevTypeDevice();
            } else {
                user.setDevTypeUser();
            }
            user.strUserDomainCode = tmp.strDomainCode;
            user.strUserID = tmp.strUserID;
            user.strUserName = tmp.strUserName;

            users.add(user);
        }

        return users;
    }

    /**
     * 发送通知
     */
    private void pushNotify(final boolean value) {
        HYClient.getModule(ApiMeet.class)
                .sendAppointmentMeetingNotify(SdkParamsCenter.Meet.SendMeetNotify().setnMeetingID(info.nMeetingID)
                                .setStrMeetingDomainCode(strMeetDomainCode),
                        new SdkCallback<CSendNotifyPredetermineMeetingRsp>() {
                            @Override
                            public void onSuccess(CSendNotifyPredetermineMeetingRsp cGetMeetingInfoRsp) {
                                if (value)
                                    showToast("通知成功");
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                if (value)
                                    showToast(ErrorMsg.getMsg(ErrorMsg.send_nofity_err_code));
                            }
                        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*ChoosedContacts.get().clear();
        ChoosedContacts.get().clearTemp();*/
        ChoosedContactsNew.get().clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == CommonConstant.ACTIVITY_RESULT_FAILED) {
            return;
        }
        switch (requestCode) {
            case CommonConstant.ACTIVITY_REQUEST_CODE:
//                requestOnLine(true);
                requestInfo();
                break;
            case 1000:
                editMeet();
                break;
            default:
                break;
        }
    }
}
