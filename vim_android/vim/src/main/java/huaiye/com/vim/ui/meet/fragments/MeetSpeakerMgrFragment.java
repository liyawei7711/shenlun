package huaiye.com.vim.ui.meet.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CMeetingSpeakSetReq;
import com.huaiye.sdk.sdpmsgs.meet.CMeetingSpeakSetRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppBaseFragment;
import huaiye.com.vim.common.ErrorMsg;
import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;

/**
 * author: admin
 * date: 2018/01/08
 * version: 0
 * mail: secret
 * desc: MeetSpeakerMgrFragment
 * 会议禁言
 */
@BindLayout(R.layout.fragment_meet_speaker)
public class MeetSpeakerMgrFragment extends AppBaseFragment {

    String strMeetDomaincode;
    int nMeetID;

    public void setMeetDomaincode(String domain) {
        this.strMeetDomaincode = domain;
    }

    public void setMeetID(int id) {
        this.nMeetID = id;
    }

    @BindView(R.id.rct_view)
    RecyclerView rct_view;
    EXTRecyclerAdapter<CGetMeetingInfoRsp.UserInfo> adapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getNavigate().setVisibility(View.GONE);

        rct_view.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EXTRecyclerAdapter<CGetMeetingInfoRsp.UserInfo>(R.layout.item_meet_speaker) {
            @Override
            public void onBindViewHolder(EXTViewHolder extViewHolder, int i, CGetMeetingInfoRsp.UserInfo userInfo) {
                extViewHolder.setText(R.id.tv_name, userInfo.strUserName);

                if (userInfo.isSpeakerMute()) {
                    extViewHolder.setImageResouce(R.id.iv_state, R.drawable.ic_meet_mic_checked);
                } else {
                    extViewHolder.setImageResouce(R.id.iv_state, R.drawable.ic_meet_mic);
                }
            }
        };

        adapter.setOnItemClickListener(new EXTRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, final int i) {
                final CGetMeetingInfoRsp.UserInfo userInfo = adapter.getDataForItemPosition(i);

                CMeetingSpeakSetReq.User user = new CMeetingSpeakSetReq.User();
                user.setMode(userInfo.isSpeakerMute() ? SdkBaseParams.MuteStatus.UnMute : SdkBaseParams.MuteStatus.Mute);
                user.strUserDomainCode = userInfo.strUserDomainCode;
                user.strUserID = userInfo.strUserID;
                HYClient.getModule(ApiMeet.class)
                        .mgrUserSpeaker(SdkParamsCenter.Meet.MgrUsrSpeaker()
                                .setMeetDomainCode(strMeetDomaincode)
                                .setMeetID(nMeetID)
                                .addUsers(user), new SdkCallback<CMeetingSpeakSetRsp>() {
                            @Override
                            public void onSuccess(CMeetingSpeakSetRsp cMeetingSpeakSetRsp) {
                                CGetMeetingInfoRsp.UserInfo userInfo = adapter.getDataForItemPosition(i);
                                userInfo.nMuteStatus = (userInfo.nMuteStatus + 1) % 2;
                                adapter.notifyItemChanged(i);
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
        });

        rct_view.setAdapter(adapter);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            requestDatas();
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
                        adapter.setDatas(cGetMeetingInfoRsp.listUser);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        AppBaseActivity.showToast(ErrorMsg.getMsg(ErrorMsg.get_meet_info_err_code));
                    }
                });
    }
}
