package huaiye.com.vim.ui.meet.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.meet.ParamsMeetKickout;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CkickMeetingUserRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import java.util.ArrayList;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppBaseFragment;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.views.CheckableLinearLayout;
import huaiye.com.vim.ui.meet.MeetActivity;
import huaiye.com.vim.ui.meet.MeetWatchActivity;
import ttyy.com.recyclerexts.base.EXTViewHolder;
import ttyy.com.recyclerexts.tags.TagsAdapter;

/**
 * author: admin
 * date: 2018/01/08
 * version: 0
 * mail: secret
 * desc: MeetMembersFragment
 * 会议人员列表
 */
@BindLayout(R.layout.fragment_meet_members)
public class MeetMembersFragment extends AppBaseFragment {

    boolean isMeetStarter;
    String strMeetDomaincode;
    int nMeetID;

    public void setIsMeetStarter(boolean value) {
        isMeetStarter = value;
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
    View tv_one_key_voice;
    TagsAdapter<CGetMeetingInfoRsp.UserInfo> adapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getNavigate().setVisibility(View.GONE);

        rct_view.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TagsAdapter<CGetMeetingInfoRsp.UserInfo>(R.layout.item_meet_members) {

            @Override
            public EXTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                EXTViewHolder holder = super.onCreateViewHolder(parent, viewType);

                if (getMode() == Mode.MultiChoice) {
                    CheckableLinearLayout ll = (CheckableLinearLayout) holder.getItemView();
                    ll.setOnCheckedListener(new CheckableLinearLayout.OnCheckedChangedListener() {
                        @Override
                        public void onCheckedChanged(View parent, boolean isChecked) {
                            ImageView iv_state = (ImageView) parent.findViewById(R.id.iv_state);
                            if (isChecked) {
                                iv_state.setImageResource(R.drawable.ic_choice_checked);
                            } else {
                                iv_state.setImageResource(R.drawable.ic_choice);
                            }
                        }
                    });
                } else {
                    holder.getItemView().findViewById(R.id.iv_state).setVisibility(View.GONE);
                }
                return holder;
            }

            @Override
            public void onBindTagViewHolder(EXTViewHolder extViewHolder, int i, CGetMeetingInfoRsp.UserInfo userInfo) {
                extViewHolder.setText(R.id.tv_name, userInfo.strUserName);
            }
        };
        adapter.setChoiceMode(isMeetStarter ? TagsAdapter.Mode.MultiChoice : TagsAdapter.Mode.None);
        if (isMeetStarter) {
//            getContentView().findViewById(R.id.tv_kickout).setVisibility(View.VISIBLE);
        } else {
//            getContentView().findViewById(R.id.tv_kickout).setVisibility(View.GONE);
        }
        rct_view.setAdapter(adapter);

    }

    @OnClick({R.id.tv_one_key_voice})
    void onKickoutClicked(View v) {

        if (adapter.getSelectedPositions().isEmpty()) {
            AppBaseActivity.showToast(getString(R.string.meet_xuanze_tichu));
            return;
        }

        ArrayList<ParamsMeetKickout.User> users = new ArrayList<>();

        for (int i : adapter.getSelectedPositions()) {
            ParamsMeetKickout.User user = new ParamsMeetKickout.User();
            CGetMeetingInfoRsp.UserInfo tmp = adapter.getDataForItemPosition(i);

            user.setDomainCode(tmp.strUserDomainCode);
            user.setUserID(tmp.strUserID);

            users.add(user);
        }

        HYClient.getModule(ApiMeet.class).kickoutUser(SdkParamsCenter.Meet.KickoutMeet()
                .setMeetDomainCode(strMeetDomaincode)
                .setMeetID(nMeetID)
                .setKickoutUsers(users), new SdkCallback<CkickMeetingUserRsp>() {
            @Override
            public void onSuccess(CkickMeetingUserRsp ckickMeetingUserRsp) {
                AppBaseActivity.showToast(getString(R.string.meet_tichuchenggong));
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
                AppBaseActivity.showToast(ErrorMsg.getMsg(ErrorMsg.kitout_err_code));
            }
        });
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
                        .setnListMode(1)
                        .setMeetDomainCode(strMeetDomaincode)
                        .setMeetID(nMeetID), new SdkCallback<CGetMeetingInfoRsp>() {
                    @Override
                    public void onSuccess(CGetMeetingInfoRsp cGetMeetingInfoRsp) {
                        adapter.clearChooseStatus();

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
