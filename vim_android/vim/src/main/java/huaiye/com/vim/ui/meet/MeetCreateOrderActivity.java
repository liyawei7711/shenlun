package huaiye.com.vim.ui.meet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.meet.ParamsAppointmentSetMeet;
import com.huaiye.sdk.sdpmsgs.meet.CSendNotifyPredetermineMeetingRsp;
import com.huaiye.sdk.sdpmsgs.meet.CSetPredetermineMeetingReq;
import com.huaiye.sdk.sdpmsgs.meet.CSetPredetermineMeetingRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import java.util.ArrayList;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.recycle.RecycleTouchUtils;
import huaiye.com.vim.dao.AppDatas;
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
public class MeetCreateOrderActivity extends AppBaseActivity {

    @BindView(R.id.rct_view)
    RecyclerView rct_view;

    MeetCreateHeaderView header;
    EXTRecyclerAdapter<ContactData> adapter;
    ArrayList<ContactData> data = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(getString(R.string.create_order_meeting))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                })
                .setRightText(getString(R.string.group_notice13))
                .setRightTextColor(Color.RED)
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createMeet();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        ChoosedContacts.get().initDelete();
        rct_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EXTRecyclerAdapter<ContactData>(R.layout.item_meetcreate_member) {
            @Override
            public void onBindViewHolder(EXTViewHolder extViewHolder, int i, ContactData contactData) {
                if (i < getHeaderViewsCount()) {
                    return;
                }

                if (contactData.loginName.equals(AppDatas.Auth().getUserLoginName())) {
                    extViewHolder.setVisibility(R.id.iv_mainer, View.VISIBLE);
                } else {
                    extViewHolder.setVisibility(R.id.iv_mainer, View.GONE);
                }
                extViewHolder.setText(R.id.tv_user_name, contactData.name);
                if (map.containsKey(contactData.loginName)) {
                    if (map.get(contactData.loginName).nState == 2 ||
                            map.get(contactData.loginName).nState == 3 ||
                            map.get(contactData.loginName).nState == 4) {
                        extViewHolder.setImageResouce(R.id.tv_user_status, R.drawable.dian_mang);
                    } else {
                        extViewHolder.setImageResouce(R.id.tv_user_status, R.drawable.dian_zaixian);
                    }
                } else {
                    extViewHolder.setImageResouce(R.id.tv_user_status, R.drawable.dian_lixian);
                }
            }
        };

        header = new MeetCreateHeaderView(this, true, true);
        adapter.addHeaderView(header);
        adapter.setDatas(data);

        new RecycleTouchUtils().initTouch(new RecycleTouchUtils.ITouchEvent() {
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                ContactData temp = adapter.getDataForItemPosition(viewHolder.getAdapterPosition());
                ChoosedContacts.get().deleteSelected(temp);
                data.remove(temp);
                adapter.notifyDataSetChanged();
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder.getLayoutPosition() < adapter.getHeaderViewsCount()) {
                    return 0;
                }

                return makeMovementFlags(0, ItemTouchHelper.LEFT);
            }
        }).attachToRecyclerView(rct_view);

        rct_view.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK
                && requestCode == 1000) {
            requestOnLine(false);
        }
    }

    void createMeet() {

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
            showToast(getString(R.string.meet_notice15, MbeConfigParaValue));
            return;
        }

        ParamsAppointmentSetMeet params = SdkParamsCenter.Meet.AppointmentSetMeet();
        if (header.needAddSelfMain) {
            params.setInviteSelf(1);
        } else {
            params.setInviteSelf(ChoosedContacts.get().isDeleteSelf());
        }
        params.setUsers(convertContacts(ChoosedContacts.get().getContactsCreate()))
                .setOpenRecord(header.isMeetRecord())
                .setDtMeetingStartTime(header.getMeetStartTime())
                .setMeetMode(header.getModel())
                .setnMeetingDuration(header.getMeetLong())
                .setMeetName(header.getMeetName().trim());

        HYClient.getModule(ApiMeet.class)
                .setAppointmentMeeting(params, new SdkCallback<CSetPredetermineMeetingRsp>() {
                    @Override
                    public void onSuccess(CSetPredetermineMeetingRsp info) {
                        ChoosedContacts.get().clearTemp();
                        ChoosedContacts.get().clear();

                        showToast(getString(R.string.meet_create_success));
                        pushNotify(info.nMeetingID, info.strMeetingDomainCode);
                        finish();
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        if (errorInfo.getCode() == 1720410011) {
                            showToast(getString(R.string.meet_notice16));
                        } else {
                            showToast(ErrorMsg.getMsg(ErrorMsg.create_meet_err_code));
                        }
                    }
                });
    }

    /**
     * 发送通知
     */
    private void pushNotify(final int nMeetID, final String strMeetDomainCode) {
        HYClient.getModule(ApiMeet.class)
                .sendAppointmentMeetingNotify(SdkParamsCenter.Meet.SendMeetNotify().setnMeetingID(nMeetID)
                                .setStrMeetingDomainCode(strMeetDomainCode),
                        new SdkCallback<CSendNotifyPredetermineMeetingRsp>() {
                            @Override
                            public void onSuccess(CSendNotifyPredetermineMeetingRsp cGetMeetingInfoRsp) {

                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                showToast(ErrorMsg.getMsg(ErrorMsg.send_nofity_err_code));
                            }
                        });
    }

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

    @Override
    protected void afterOnLineUser(boolean value) {
        super.afterOnLineUser(value);
        data.clear();
        data.addAll(ChoosedContacts.get().getContacts(false));
        adapter.notifyDataSetChanged();
        header.needAddSelfMain = false;
    }
}
