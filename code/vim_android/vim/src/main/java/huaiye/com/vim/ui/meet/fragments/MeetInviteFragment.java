package huaiye.com.vim.ui.meet.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.huaiye.cmf.sdp.SdpMessageCmStartSessionRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CInviteUserMeetingRsp;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;
import com.huaiye.sdk.sdpmsgs.social.CQueryUserListRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import java.util.ArrayList;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppBaseFragment;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.views.CheckableLinearLayout;
import huaiye.com.vim.ui.meet.MeetActivity;
import huaiye.com.vim.ui.meet.MeetWatchActivity;
import ttyy.com.recyclerexts.base.EXTViewHolder;
import ttyy.com.recyclerexts.tags.TagsAdapter;

import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * author: admin
 * date: 2018/01/08
 * version: 0
 * mail: secret
 * desc: MeetInviteFragment
 * 会议邀请人
 */
@BindLayout(R.layout.fragment_meet_invite_person)
public class MeetInviteFragment extends AppBaseFragment {

    @BindView(R.id.rct_view)
    RecyclerView rct_view;

    String strMeetDomaincode;
    int nMeetID;

    int count = 0;//邀请总数
    int cuntCount = 0;//返回数
    int cuntCountSuccess = 0;//返回成功数

    TagsAdapter<CQueryUserListRsp.UserInfo> adapter;
    CGetMeetingInfoRsp mMeetDetail;

    public void setMeetDomaincode(String domain) {
        this.strMeetDomaincode = domain;
    }

    public void setMeetID(int id) {
        this.nMeetID = id;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getNavigate().setVisibility(View.GONE);

        rct_view.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TagsAdapter<CQueryUserListRsp.UserInfo>(R.layout.item_meet_invite_person) {
            {
                setChoiceMode(Mode.MultiChoice);
            }

            @Override
            public EXTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if (viewType == HEADER
                        || viewType == FOOTER) {

                    return super.onCreateViewHolder(parent, viewType);
                }

                final EXTViewHolder holder = super.onCreateViewHolder(parent, viewType);
                if (getMode() == Mode.None) {
                    holder.getItemView().findViewById(R.id.iv_choice).setVisibility(View.GONE);
                } else {
                    holder.getItemView().findViewById(R.id.iv_choice).setVisibility(View.VISIBLE);
                    CheckableLinearLayout root = (CheckableLinearLayout) holder.getItemView();
                    root.setOnCheckedListener(new CheckableLinearLayout.OnCheckedChangedListener() {
                        @Override
                        public void onCheckedChanged(View parent, boolean isChecked) {
                            if (isChecked) {
                                holder.setImageResouce(R.id.iv_choice, R.drawable.ic_choice_checked);
                            } else {
                                holder.setImageResouce(R.id.iv_choice, R.drawable.ic_choice);
                            }
                        }
                    });
                }
                return holder;
            }

            @Override
            public void onBindTagViewHolder(EXTViewHolder extViewHolder, int i, CQueryUserListRsp.UserInfo data) {
                if (i < getHeaderViewsCount()) {

                    return;
                }

                extViewHolder.setText(R.id.tv_user_name, data.strUserName);
            }
        };

        rct_view.setAdapter(adapter);

    }

    void requestOnlineUsers() {
        HYClient.getModule(ApiSocial.class).getUsers(SdkParamsCenter.Social.GetUsers()
                .setDomainCode(HYClient.getSdkOptions().User().getDomainCode()), new SdkCallback<ArrayList<CQueryUserListRsp.UserInfo>>() {
            @Override
            public void onSuccess(ArrayList<CQueryUserListRsp.UserInfo> userInfos) {
                ArrayList<CQueryUserListRsp.UserInfo> users = new ArrayList<>();

                for (CQueryUserListRsp.UserInfo user : userInfos) {
                    boolean isContains = false;
                    for (CGetMeetingInfoRsp.UserInfo tmp : mMeetDetail.listUser) {
                        if (user.strUserID.equals(tmp.strUserID)) {
                            isContains = true;
                            break;
                        }
                    }

                    if (!isContains) {
                        users.add(user);
                    }
                }

                adapter.setDatas(users);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(ErrorInfo errorInfo) {

            }
        });
    }

    void requestMeetUsers() {
        HYClient.getModule(ApiMeet.class).requestMeetDetail(SdkParamsCenter.Meet.RequestMeetDetail()
                .setMeetID(nMeetID)
                .setnListMode(1)
                .setMeetDomainCode(strMeetDomaincode), new SdkCallback<CGetMeetingInfoRsp>() {
            @Override
            public void onSuccess(CGetMeetingInfoRsp cGetMeetingInfoRsp) {
                mMeetDetail = cGetMeetingInfoRsp;
                requestOnlineUsers();
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                AppBaseActivity.showToast(ErrorMsg.getMsg(ErrorMsg.get_meet_info_err_code));
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            adapter.clearChooseStatus();
            requestMeetUsers();
        }
    }

    @OnClick(R.id.tv_invite)
    void onInviteClicked() {
        if (adapter.getSelectedPositions().isEmpty()) {

            AppBaseActivity.showToast("请选择参会人员");
            return;
        }

        ArrayList<CStartMeetingReq.UserInfo> users = new ArrayList<>();
        for (int i : adapter.getSelectedPositions()) {
            CQueryUserListRsp.UserInfo tmp = adapter.getDataForPosition(i);

            CStartMeetingReq.UserInfo user = new CStartMeetingReq.UserInfo();
            user.setDevTypeUser();
            user.strUserName = tmp.strUserName;
            user.strUserID = tmp.strUserID;
            user.strUserDomainCode = HYClient.getSdkOptions().User().getDomainCode();

            users.add(user);
        }

        count = 0;
        cuntCount = 0;
        cuntCountSuccess = 0;

        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            for (CStartMeetingReq.UserInfo temp : users) {
                if (!HYClient.getSdkOptions().User().getUserId().equals(temp.strUserID)) {
                    EncryptUtil.startEncrypt(true, temp.strUserID, temp.strUserDomainCode,
                            nMeetID + "", strMeetDomaincode, new SdkCallback<SdpMessageCmStartSessionRsp>() {
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
            realInvisitor(users);
        }

    }

    private void realInvisitor(ArrayList<CStartMeetingReq.UserInfo> users) {
        HYClient.getModule(ApiMeet.class).inviteUser(SdkParamsCenter.Meet.InviteMeet()
                .setMeetDomainCode(strMeetDomaincode)
                .setMeetID(nMeetID)
                .setUsers(users), new SdkCallback<CInviteUserMeetingRsp>() {
            @Override
            public void onSuccess(CInviteUserMeetingRsp cInviteUserMeetingRsp) {
                if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                    cuntCount++;
                    cuntCountSuccess++;
                    if (count == cuntCount) {
                        AppBaseActivity.showToast("邀请成功");
                    }
                } else {
                    AppBaseActivity.showToast("邀请成功");
                }
                if (getActivity() != null) {
                    if (getActivity() instanceof MeetActivity) {
                        ((MeetActivity) getActivity()).hideAll();
                    } else {
                        ((MeetWatchActivity) getActivity()).hideAll();
                    }
                }
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                    cuntCount++;
                    if (count == cuntCount && cuntCountSuccess > 0) {
                        AppBaseActivity.showToast("邀请成功");
                    } else {
                        AppBaseActivity.showToast(ErrorMsg.getMsg(ErrorMsg.invite_user_err_code));
                    }
                } else {
                    AppBaseActivity.showToast(ErrorMsg.getMsg(ErrorMsg.invite_user_err_code));
                }
            }
        });
    }

}
