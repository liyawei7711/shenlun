package huaiye.com.vim.ui.meet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.meet.ParamsAppointmentSetMeet;
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
import huaiye.com.vim.common.constant.CommonConstant;
import huaiye.com.vim.common.views.pickers.CustomDatePicker;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.models.contacts.bean.ContactData;

/**
 * Created by ywt on 2019/2/22.
 */
@BindLayout(R.layout.activity_meet_detail_edit)
public class MeetDetailEditActivity extends AppBaseActivity {
    @BindView(R.id.meet_detail_name)
    EditText meet_detail_name;
    @BindView(R.id.meet_detail_content)
    EditText meet_detail_content;
    @BindView(R.id.meet_detail_time)
    TextView meet_detail_time;

    @BindExtra
    int nMeetingID;
    @BindExtra
    String strMeetingName;
    @BindExtra
    String strMeetingDesc;
    @BindExtra
    String strStartTime;
    @BindExtra
    String strMeetDomainCode;
    @BindExtra
    int nMeetingMode;
    @BindExtra
    int nTimeDuration;

    private ArrayList<CSetPredetermineMeetingReq.UserInfo> mMeetUsers = new ArrayList<CSetPredetermineMeetingReq.UserInfo>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNavigate().setRightText(getString(R.string.save));
        getNavigate().setRightTextColor(ContextCompat.getColor(this, R.color.blue));
        getNavigate().setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getNavigate().setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMeet();
            }
        });

        requestInfo();
    }

    @Override
    protected void initActionBar() {

    }

    @Override
    public void doInitDelay() {
        meet_detail_name.setText(strMeetingName);
        meet_detail_content.setText(strMeetingDesc);
        meet_detail_time.setText(strStartTime.substring(5, 16));
    }

    @OnClick(R.id.meet_detail_time)
    public void clickView(View view) {
        switch (view.getId()) {
            case R.id.meet_detail_time:
                CustomDatePicker customDatePicker = new CustomDatePicker(2, this, new CustomDatePicker.ResultHandler() {
                    @Override
                    public void handle(String time, long timelong) {
                        meet_detail_time.setText(time.substring(5));
                        strStartTime = time + ":00";
                    }
                }, System.currentTimeMillis() + 60 * 1000, System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000);
                customDatePicker.showYear(false).setIsLoop(false);
                customDatePicker.show("");
                break;
            default:
                break;
        }
    }

    /**
     * 编辑会议
     */
    void editMeet() {
        if (TextUtils.isEmpty(meet_detail_name.getText().toString())) {
            showToast(getString(R.string.meet_notice11));
            return;
        }
        /*if (TextUtils.isEmpty(meet_detail_content.getText().toString())) {
            showToast("会议详情不能为空");
            return;
        }*/
        if (TextUtils.isEmpty(meet_detail_time.getText().toString())) {
            showToast(getString(R.string.meet_notice12));
            return;
        }
        int inviteSelf = 0;
        for (CSetPredetermineMeetingReq.UserInfo item : mMeetUsers) {
            if (item.strUserID.equals(String.valueOf(AppDatas.Auth().getUserID()))) {
                inviteSelf = 1;
                break;
            }
        }
        String detail = meet_detail_content.getText() == null ? "" : meet_detail_content.getText().toString();
        ParamsAppointmentSetMeet params = SdkParamsCenter.Meet.AppointmentSetMeet()
//                .setInviteSelf(ChoosedContacts.get().isDeleteSelf())
                .setInviteSelf(inviteSelf)
                .setUsers(mMeetUsers)
                .setOpenRecord(false)
                .setnMeetingID(nMeetingID)
                .setMeetDesc(detail)
                .setDtMeetingStartTime(strStartTime)
                .setMeetMode(nMeetingMode == 2 ? SdkBaseParams.MeetMode.Host : SdkBaseParams.MeetMode.Normal)
                .setnMeetingDuration(nTimeDuration)
                .setMeetName(meet_detail_name.getText().toString().trim());


        HYClient.getModule(ApiMeet.class)
                .setAppointmentMeeting(params, new SdkCallback<CSetPredetermineMeetingRsp>() {
                    @Override
                    public void onSuccess(CSetPredetermineMeetingRsp cStartMeetingRsp) {
                        pushNotify(false);
                        showToast(getString(R.string.common_notice31));
                        setResult(CommonConstant.ACTIVITY_RESULT_SUCCESS);
                        finish();
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast(ErrorMsg.getMsg(ErrorMsg.update_meet_err_code));
                    }
                });
    }

    /**
     * 发送通知
     */
    private void pushNotify(final boolean value) {
        HYClient.getModule(ApiMeet.class)
                .sendAppointmentMeetingNotify(SdkParamsCenter.Meet.SendMeetNotify().setnMeetingID(nMeetingID)
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

    /**
     * 转换会议人员信息
     *
     * @param contacts
     * @return
     */
    private ArrayList<CSetPredetermineMeetingReq.UserInfo> convertContacts(ArrayList<ContactData> contacts) {
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

    private void requestInfo() {
        HYClient.getModule(ApiMeet.class)
                .requestMeetDetail(SdkParamsCenter.Meet.RequestMeetDetail()
                                .setnListMode(1)
                                .setMeetID(nMeetingID)
                                .setMeetDomainCode(strMeetDomainCode),
                        new SdkCallback<CGetMeetingInfoRsp>() {
                            @Override
                            public void onSuccess(CGetMeetingInfoRsp cGetMeetingInfoRsp) {
                                mZeusLoadView.dismiss();
                                mMeetUsers.clear();
                                for (CGetMeetingInfoRsp.UserInfo item : cGetMeetingInfoRsp.listUser) {
                                    CSetPredetermineMeetingReq.UserInfo info = new CSetPredetermineMeetingReq.UserInfo();
                                    info.strUserDomainCode = item.strUserDomainCode;
                                    info.strUserID = item.strUserID;
                                    info.strUserName = item.strUserName;
                                    info.setDevTypeUser();
                                    mMeetUsers.add(info);
                                }
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                mZeusLoadView.dismiss();
                            }
                        });
    }
}
