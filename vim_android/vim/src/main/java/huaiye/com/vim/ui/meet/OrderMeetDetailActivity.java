package huaiye.com.vim.ui.meet;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;

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
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.dialog.LogicDialog;
import huaiye.com.vim.common.recycle.RecycleTouchUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.models.contacts.bean.ContactData;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContacts;
import huaiye.com.vim.ui.meet.views.MeetCreateHeaderView;
import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;

import static android.support.v7.widget.helper.ItemTouchHelper.Callback.makeMovementFlags;

/**
 * author: admin
 * date: 2018/01/15
 * version: 0
 * mail: secret
 * desc: MeetCreateActivity
 */
@BindLayout(R.layout.activity_meet_create)
public class OrderMeetDetailActivity extends AppBaseActivity {

    @BindView(R.id.rct_view)
    RecyclerView rct_view;

    @BindView(R.id.tv_player)
    View tv_player;
    @BindView(R.id.tv_enter)
    View tv_enter;
    @BindView(R.id.tv_delete)
    View tv_delete;
    @BindView(R.id.tv_edit)
    View tv_edit;
    @BindView(R.id.tv_notify)
    View tv_notify;

    @BindExtra
    String strInviteUserId;
    @BindExtra
    String strMainUserDomainCode;
    @BindExtra
    String strMeetDomainCode;
    @BindExtra
    int nMeetID;

    boolean isMaster;
    CGetMeetingInfoRsp info;

    MeetCreateHeaderView header;
    EXTRecyclerAdapter<ContactData> adapter;
    ArrayList<ContactData> data = new ArrayList<>();

    boolean canEdit;

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(getString(R.string.meet_detail))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                })
                /*.setRightIcon(R.drawable.ico_share)
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (info == null) {
                            showToast(getString(R.string.meet_has_not));
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
                        AppUtils.copyAndPass(OrderMeetDetailActivity.this, copy);
                        showToast(getString(R.string.common_notice30));
                    }
                })*/;

    }

    @Override
    public void doInitDelay() {
        mZeusLoadView.loadingText(getString(R.string.common_notice28)).setLoading();
        rct_view.setLayoutManager(new LinearLayoutManager(OrderMeetDetailActivity.this));
        adapter = new EXTRecyclerAdapter<ContactData>(R.layout.item_meetcreate_member) {
            @Override
            public void onBindViewHolder(EXTViewHolder extViewHolder, int i, ContactData contactData) {
                if (i < getHeaderViewsCount()) {
                    return;
                }

                /*if (contactData.loginName.equals(info.strMainUserID)) {
                    extViewHolder.setVisibility(R.id.iv_mainer, View.VISIBLE);
                } else {
                    extViewHolder.setVisibility(R.id.iv_mainer, View.GONE);
                }*/

                extViewHolder.setText(R.id.tv_user_name, contactData.name);
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

        header = new MeetCreateHeaderView(OrderMeetDetailActivity.this, true, true);
        adapter.addHeaderView(header);
        adapter.setDatas(data);
        rct_view.setAdapter(adapter);

        new RecycleTouchUtils().initTouch(new RecycleTouchUtils.ITouchEvent() {
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                if (!canEdit)
                    return;
                ContactData temp = adapter.getDataForItemPosition(viewHolder.getAdapterPosition());
                ChoosedContacts.get().deleteSelected(temp);
                data.remove(temp);
                adapter.notifyDataSetChanged();
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (!canEdit)
                    return 0;
                return makeMovementFlags(0, ItemTouchHelper.LEFT);
            }
        }).attachToRecyclerView(rct_view);

        requestOnLine(true);
    }

    /**
     * 发送通知
     */
    private void pushNotify(final boolean value) {
        HYClient.getModule(ApiMeet.class)
                .sendAppointmentMeetingNotify(SdkParamsCenter.Meet.SendMeetNotify().setnMeetingID(nMeetID)
                                .setStrMeetingDomainCode(strMeetDomainCode),
                        new SdkCallback<CSendNotifyPredetermineMeetingRsp>() {
                            @Override
                            public void onSuccess(CSendNotifyPredetermineMeetingRsp cGetMeetingInfoRsp) {
                                if (value)
                                    showToast(getString(R.string.common_notice29));
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                if (value)
                                    showToast(ErrorMsg.getMsg(ErrorMsg.send_nofity_err_code));
                            }
                        });
    }

    @OnClick({R.id.tv_delete, R.id.tv_edit, R.id.tv_notify, R.id.tv_enter, R.id.tv_player})
    public void clickView(View view) {
        switch (view.getId()) {
            case R.id.tv_delete:
                deleteMeet();
                break;
            case R.id.tv_edit:
                editMeet();
                break;
            case R.id.tv_notify:
                pushNotify(true);
                break;
            case R.id.tv_enter:
                entMeeting();
                break;
            case R.id.tv_player:
                startPlayer();
                break;
        }
    }

    /**
     * 播放录像
     */
    private void startPlayer() {
        if (info.nRecordID == 0) {
            showToast(getString(R.string.meet_notice22));
            return;
        }
        Intent intent = new Intent(this, MeetPlaybackActivity.class);
        intent.putExtra("nMeetID", nMeetID);
        intent.putExtra("strMeetDomainCode", strMeetDomainCode);
        startActivity(intent);
    }

    /**
     * 进入会议
     */
    private void entMeeting() {
        Intent intent = new Intent(this, MeetNewActivity.class);
        intent.putExtra("strMeetDomainCode", strMeetDomainCode);
        intent.putExtra("isMeetStarter", isMaster);
        intent.putExtra("nMeetID", nMeetID);
        intent.putExtra("mMediaMode", SdkBaseParams.MediaMode.AudioAndVideo);
        intent.putExtra("strInviteUserId", strInviteUserId);
        intent.putExtra("strInviteUserDomainCode", strMainUserDomainCode);
        startActivity(intent);
    }

    /**
     * 删除会议
     */
    void deleteMeet() {
        final LogicDialog logicDialog = new LogicDialog(this);
        logicDialog.setMessageText(getString(R.string.meet_notice17));
        logicDialog.setConfirmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HYClient.getModule(ApiMeet.class)
                        .deleteAppointmentMeeting(SdkParamsCenter.Meet.AppointmentDeleteMeet()
                                .setnMeetingID(nMeetID)
                                .setStrDomainCode(strMeetDomainCode), new SdkCallback<CDelMeetingInfoRsp>() {
                            @Override
                            public void onSuccess(CDelMeetingInfoRsp info) {
                                showToast(getString(R.string.common_notice2));
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

    /**
     * 编辑会议
     */
    void editMeet() {

        if (TextUtils.isEmpty(header.getMeetName())) {
            showToast(getString(R.string.meet_notice11));
            return;
        }
        if (TextUtils.isEmpty(header.getMeetStartTime())) {
            showToast(getString(R.string.meet_notice12));
            return;
        }
        if (header.getMeetLong() <= 0) {
            showToast(getString(R.string.meet_time_duration));
            return;
        }
        if (header.getMeetLong() > 24 * 60 * 60) {
            showToast(getString(R.string.meet_notice13));
            return;
        }
        if (MbeConfigParaValue != -1 && adapter.getDatasCount() - adapter.getHeaderViewsCount() > MbeConfigParaValue) {
            showToast(getString(R.string.meet_notice1, MbeConfigParaValue));
            return;
        }

        ParamsAppointmentSetMeet params = SdkParamsCenter.Meet.AppointmentSetMeet()
                .setInviteSelf(ChoosedContacts.get().isDeleteSelf())
                .setUsers(convertContacts(ChoosedContacts.get().getContactsCreate()))
                .setOpenRecord(header.isMeetRecord())
                .setnMeetingID(nMeetID)
                .setDtMeetingStartTime(header.getMeetStartTime())
                .setMeetMode(header.getModel())
                .setnMeetingDuration(header.getMeetLong())
                .setMeetName(header.getMeetName().trim());


        HYClient.getModule(ApiMeet.class)
                .setAppointmentMeeting(params, new SdkCallback<CSetPredetermineMeetingRsp>() {
                    @Override
                    public void onSuccess(CSetPredetermineMeetingRsp cStartMeetingRsp) {
                        pushNotify(false);
                        showToast(getString(R.string.common_notice31));
                        finish();
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast(ErrorMsg.getMsg(ErrorMsg.update_meet_err_code));
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK
                && requestCode == 1000) {
            requestOnLine(false);
        }
    }

    /**
     * 转换会议人员信息
     *
     * @param contacts
     * @return
     */
    ArrayList<CSetPredetermineMeetingReq.UserInfo> convertContacts(ArrayList<ContactData> contacts) {
        ArrayList<CSetPredetermineMeetingReq.UserInfo> users = new ArrayList<>();

        for (ContactData tmp : contacts) {
            CSetPredetermineMeetingReq.UserInfo user = new CSetPredetermineMeetingReq.UserInfo();

            user.setDevTypeUser();
            user.strUserDomainCode = tmp.domainCode;
            user.strUserID = tmp.loginName;
            user.strUserName = tmp.name;

            users.add(user);
        }

        return users;
    }

    ArrayList<ContactData> convertToAdapter(ArrayList<CGetMeetingInfoRsp.UserInfo> contacts) {
        ArrayList<ContactData> users = new ArrayList<>();

        for (CGetMeetingInfoRsp.UserInfo tmp : contacts) {
            addToAll(users, tmp);
        }

        return users;
    }

    /**
     * 加入所有人中
     *
     * @param users
     * @param tmp
     */
    private void addToAll(ArrayList<ContactData> users, CGetMeetingInfoRsp.UserInfo tmp) {
        ContactData user = new ContactData();
        user.loginName = tmp.strUserID;
        user.name = tmp.strUserName;
//            user.userId = Integer.parseInt(tmp.strUserID);
        user.domainCode = tmp.strUserDomainCode;
        user.naviKey = "";

        users.add(user);
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
                                mZeusLoadView.dismiss();

                                isMaster = cGetMeetingInfoRsp.strMainUserID.equals(AppAuth.get().getUserLoginName());
                                info = cGetMeetingInfoRsp;

                                if (TextUtils.isEmpty(cGetMeetingInfoRsp.strMainUserDomainCode)) {
                                    ChoosedContacts.get().deleteSelf();
                                }

                                if (cGetMeetingInfoRsp.nStatus == 4 && isMaster) {
                                    tv_delete.setVisibility(View.VISIBLE);
                                    tv_edit.setVisibility(View.VISIBLE);
                                    tv_notify.setVisibility(View.VISIBLE);
                                } else if (cGetMeetingInfoRsp.nStatus == 1) {
                                    tv_enter.setVisibility(View.VISIBLE);
                                } else if (cGetMeetingInfoRsp.nStatus == 2) {
                                    tv_player.setVisibility(View.VISIBLE);
                                }

                                for (CGetMeetingInfoRsp.UserInfo temp : cGetMeetingInfoRsp.listUser) {
                                    if (temp.strUserID.equals(AppDatas.Auth().getUserLoginName()) &&
                                            temp.nJoinStatus == 99) {
                                        ChoosedContacts.get().deleteSelf();
                                        cGetMeetingInfoRsp.listUser.remove(temp);
                                        break;
                                    }
                                }

                                canEdit = cGetMeetingInfoRsp.strMainUserID.equals(AppAuth.get().getUserLoginName())
                                        && cGetMeetingInfoRsp.nStatus == 4;
                                header.setMaster(canEdit);
                                header.showInfo(cGetMeetingInfoRsp);
                                ChoosedContacts.get().setContacts(convertToAdapter(cGetMeetingInfoRsp.listUser));
                                data.clear();
                                data.addAll(ChoosedContacts.get().getContacts(false));
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                mZeusLoadView.dismiss();
                                showToast(ErrorMsg.getMsg(errorInfo.getCode()));
                                header.setMaster(false);
                            }
                        });
    }

    @Override
    protected void afterOnLineUser(final boolean value) {
        if (value) {
            requestInfo();
        } else {
            data.clear();
            data.addAll(ChoosedContacts.get().getContacts(false));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChoosedContacts.get().clear();
        ChoosedContacts.get().clearTemp();
    }
}
