package huaiye.com.vim.ui.meet.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingLayoutInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CMeetingChangeLayoutTypeRsp;
import com.huaiye.sdk.sdpmsgs.meet.CMeetingUserSwapRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppBaseFragment;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.recycle.LiteBaseAdapter;
import huaiye.com.vim.ui.meet.LayoutPopupWindow;
import huaiye.com.vim.ui.meet.MeetActivity;
import huaiye.com.vim.ui.meet.MeetWatchActivity;
import huaiye.com.vim.ui.meet.basemodel.SelectedModel;
import huaiye.com.vim.ui.meet.viewholder.MemberLayoutHolder;

/**
 * author: admin
 * date: 2018/01/08
 * version: 0
 * mail: secret
 * desc: MeetMembersFragment
 * 会议人员列表
 */
@BindLayout(R.layout.fragment_meet_members_layout)
public class MeetMembersLayoutFragment extends AppBaseFragment {

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
    @BindView(R.id.tv_title)
    View tv_title;
    @BindView(R.id.vs_2)
    View vs_2;
    @BindView(R.id.vs_2_tv_1)
    View vs_2_tv_1;
    @BindView(R.id.vs_2_tv_2)
    View vs_2_tv_2;
    @BindView(R.id.vs_2_cb_1)
    CheckBox vs_2_cb_1;
    @BindView(R.id.vs_2_cb_2)
    CheckBox vs_2_cb_2;
    @BindView(R.id.vs_2_name_1)
    TextView vs_2_name_1;
    @BindView(R.id.vs_2_name_2)
    TextView vs_2_name_2;

    @BindView(R.id.vs_4_avg)
    View vs_3_4_avg;
    @BindView(R.id.vs_4_avg_tv_1)
    View vs_4_avg_tv_1;
    @BindView(R.id.vs_4_avg_tv_2)
    View vs_4_avg_tv_2;
    @BindView(R.id.vs_4_avg_tv_3)
    View vs_4_avg_tv_3;
    @BindView(R.id.vs_4_avg_tv_4)
    View vs_4_avg_tv_4;
    @BindView(R.id.vs_4_avg_cb_1)
    CheckBox vs_4_avg_cb_1;
    @BindView(R.id.vs_4_avg_cb_2)
    CheckBox vs_4_avg_cb_2;
    @BindView(R.id.vs_4_avg_cb_3)
    CheckBox vs_4_avg_cb_3;
    @BindView(R.id.vs_4_avg_cb_4)
    CheckBox vs_4_avg_cb_4;

    @BindView(R.id.vs_4_avg_name_1)
    TextView vs_4_avg_name_1;
    @BindView(R.id.vs_4_avg_name_2)
    TextView vs_4_avg_name_2;
    @BindView(R.id.vs_4_avg_name_3)
    TextView vs_4_avg_name_3;
    @BindView(R.id.vs_4_avg_name_4)
    TextView vs_4_avg_name_4;

    @BindView(R.id.vs_6_avg)
    View vs_5_6_avg;
    @BindView(R.id.vs_6_avg_tv_1)
    View vs_6_avg_tv_1;
    @BindView(R.id.vs_6_avg_tv_2)
    View vs_6_avg_tv_2;
    @BindView(R.id.vs_6_avg_tv_3)
    View vs_6_avg_tv_3;
    @BindView(R.id.vs_6_avg_tv_4)
    View vs_6_avg_tv_4;
    @BindView(R.id.vs_6_avg_tv_5)
    View vs_6_avg_tv_5;
    @BindView(R.id.vs_6_avg_tv_6)
    View vs_6_avg_tv_6;
    @BindView(R.id.vs_6_avg_cb_1)
    CheckBox vs_6_avg_cb_1;
    @BindView(R.id.vs_6_avg_cb_2)
    CheckBox vs_6_avg_cb_2;
    @BindView(R.id.vs_6_avg_cb_3)
    CheckBox vs_6_avg_cb_3;
    @BindView(R.id.vs_6_avg_cb_4)
    CheckBox vs_6_avg_cb_4;
    @BindView(R.id.vs_6_avg_cb_5)
    CheckBox vs_6_avg_cb_5;
    @BindView(R.id.vs_6_avg_cb_6)
    CheckBox vs_6_avg_cb_6;

    @BindView(R.id.vs_6_avg_name_1)
    TextView vs_6_avg_name_1;
    @BindView(R.id.vs_6_avg_name_2)
    TextView vs_6_avg_name_2;
    @BindView(R.id.vs_6_avg_name_3)
    TextView vs_6_avg_name_3;
    @BindView(R.id.vs_6_avg_name_4)
    TextView vs_6_avg_name_4;
    @BindView(R.id.vs_6_avg_name_5)
    TextView vs_6_avg_name_5;
    @BindView(R.id.vs_6_avg_name_6)
    TextView vs_6_avg_name_6;

    @BindView(R.id.vs_4_leader)
    View vs_3_4_5_leader;
    @BindView(R.id.vs_4_leader_tv_1)
    View vs_4_leader_tv_1;
    @BindView(R.id.vs_4_leader_tv_2)
    View vs_4_leader_tv_2;
    @BindView(R.id.vs_4_leader_tv_3)
    View vs_4_leader_tv_3;
    @BindView(R.id.vs_4_leader_tv_4)
    View vs_4_leader_tv_4;
    @BindView(R.id.vs_4_leader_tv_5)
    View vs_4_leader_tv_5;
    @BindView(R.id.vs_4_leader_cb_1)
    CheckBox vs_4_leader_cb_1;
    @BindView(R.id.vs_4_leader_cb_2)
    CheckBox vs_4_leader_cb_2;
    @BindView(R.id.vs_4_leader_cb_3)
    CheckBox vs_4_leader_cb_3;
    @BindView(R.id.vs_4_leader_cb_4)
    CheckBox vs_4_leader_cb_4;
    @BindView(R.id.vs_4_leader_cb_5)
    CheckBox vs_4_leader_cb_5;

    @BindView(R.id.vs_4_leader_name_1)
    TextView vs_4_leader_name_1;
    @BindView(R.id.vs_4_leader_name_2)
    TextView vs_4_leader_name_2;
    @BindView(R.id.vs_4_leader_name_3)
    TextView vs_4_leader_name_3;
    @BindView(R.id.vs_4_leader_name_4)
    TextView vs_4_leader_name_4;
    @BindView(R.id.vs_4_leader_name_5)
    TextView vs_4_leader_name_5;

    @BindView(R.id.vs_6_leader)
    View vs_6_leader;
    @BindView(R.id.vs_6_leader_tv_1)
    View vs_6_leader_tv_1;
    @BindView(R.id.vs_6_leader_tv_2)
    View vs_6_leader_tv_2;
    @BindView(R.id.vs_6_leader_tv_3)
    View vs_6_leader_tv_3;
    @BindView(R.id.vs_6_leader_tv_4)
    View vs_6_leader_tv_4;
    @BindView(R.id.vs_6_leader_tv_5)
    View vs_6_leader_tv_5;
    @BindView(R.id.vs_6_leader_tv_6)
    View vs_6_leader_tv_6;
    @BindView(R.id.vs_6_leader_cb_1)
    CheckBox vs_6_leader_cb_1;
    @BindView(R.id.vs_6_leader_cb_2)
    CheckBox vs_6_leader_cb_2;
    @BindView(R.id.vs_6_leader_cb_3)
    CheckBox vs_6_leader_cb_3;
    @BindView(R.id.vs_6_leader_cb_4)
    CheckBox vs_6_leader_cb_4;
    @BindView(R.id.vs_6_leader_cb_5)
    CheckBox vs_6_leader_cb_5;
    @BindView(R.id.vs_6_leader_cb_6)
    CheckBox vs_6_leader_cb_6;

    @BindView(R.id.vs_6_leader_name_1)
    TextView vs_6_leader_name_1;
    @BindView(R.id.vs_6_leader_name_2)
    TextView vs_6_leader_name_2;
    @BindView(R.id.vs_6_leader_name_3)
    TextView vs_6_leader_name_3;
    @BindView(R.id.vs_6_leader_name_4)
    TextView vs_6_leader_name_4;
    @BindView(R.id.vs_6_leader_name_5)
    TextView vs_6_leader_name_5;
    @BindView(R.id.vs_6_leader_name_6)
    TextView vs_6_leader_name_6;

    ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    ArrayList<View> bgViews = new ArrayList<>();
    ArrayList<TextView> tvViews = new ArrayList<>();

    LayoutPopupWindow layoutPopupWindow;
    View currentView;
    SelectedModel<CGetMeetingInfoRsp.UserInfo> currentModel;

    LiteBaseAdapter<SelectedModel<CGetMeetingInfoRsp.UserInfo>> adapter;
    ArrayList<SelectedModel<CGetMeetingInfoRsp.UserInfo>> listUser = new ArrayList<>();

    int colorRed = Color.RED;
    int colorBlue = Color.parseColor("#9FB8E7");
    boolean isBigSmall;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getNavigate().setVisibility(View.GONE);

        rct_view.setLayoutManager(new LinearLayoutManager(getContext()));

        layoutPopupWindow = new LayoutPopupWindow(getContext());
        layoutPopupWindow.setConfirmClickListener(new LayoutPopupWindow.ConfirmClickListener() {
            @Override
            public void onClickShow(boolean value) {
                if (value == isBigSmall)
                    return;

                if (getActivity() instanceof MeetActivity) {
                    nMeetID = ((MeetActivity) getActivity()).nMeetID;
                    strMeetDomaincode = ((MeetActivity) getActivity()).strMeetDomainCode;
                } else {
                    nMeetID = ((MeetWatchActivity) getActivity()).nMeetID;
                    strMeetDomaincode = ((MeetWatchActivity) getActivity()).strMeetDomainCode;
                }
                HYClient.getModule(ApiMeet.class).switchMeetLayout(SdkParamsCenter.Meet.SwitchMeetLayout()
                                .setnMeetingID(nMeetID)
                                .setStrMeetingDomainCode(strMeetDomaincode),
                        new SdkCallback<CMeetingChangeLayoutTypeRsp>() {
                            @Override
                            public void onSuccess(CMeetingChangeLayoutTypeRsp cMeetingChangeLayoutTypeRsp) {
                                isBigSmall = !isBigSmall;
                                resetAll();
                                requestDatas();
                                requestLayoutInfo();
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                AppBaseActivity.showToast(ErrorMsg.getMsg(ErrorMsg.switch_meet_layout_code));
                            }
                        });
            }
        });
        adapter = new LiteBaseAdapter<>(getContext(),
                listUser,
                MemberLayoutHolder.class,
                R.layout.item_meet_members_layout,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SelectedModel<CGetMeetingInfoRsp.UserInfo> bean = (SelectedModel<CGetMeetingInfoRsp.UserInfo>) v.getTag();
                        if (bean.bean.nJoinStatus != 2) {
                            AppBaseActivity.showToast(getString(R.string.meet_yicanhui));
                            return;
                        }
                        bean.isChecked = !bean.isChecked;
                        if (bean.isChecked) {
                            currentModel = bean;
                            for (SelectedModel<CGetMeetingInfoRsp.UserInfo> temp : listUser) {
                                if (bean != temp) {
                                    temp.isChecked = false;
                                }
                            }
                        } else {
                            currentModel = null;
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, null);

        rct_view.setAdapter(adapter);

        addCheckBox();
    }

    private void changeVideoPage() {
        if (currentModel == null) {
            AppBaseActivity.showToast(getString(R.string.meet_jiaohuan_no_empty));
            return;
        }
        if (currentView == null) {
            AppBaseActivity.showToast(getString(R.string.meet_jiaohuan_weizhi_no_empty));
            return;
        }
        CGetMeetingLayoutInfoRsp.UserInfo currentB = (CGetMeetingLayoutInfoRsp.UserInfo) currentView.getTag();
        HYClient.getModule(ApiMeet.class)
                .changeMeetingMemberLayout(SdkParamsCenter.Meet.UserSwap().setnMeetingID(nMeetID)
                                .setStrMeetingDomainCode(strMeetDomaincode)
                                .setStrUserADomainCode(currentModel.bean.strUserDomainCode)
                                .setStrUserAID(currentModel.bean.strUserID)
                                .setStrUserBDomainCode(currentB.strUserDomainCode)
                                .setStrUserBID(currentB.strUserID),
                        new SdkCallback<CMeetingUserSwapRsp>() {
                            @Override
                            public void onSuccess(CMeetingUserSwapRsp cGetMeetingInfoRsp) {
                                AppBaseActivity.showToast(getString(R.string.meet_jiaohuan_success));
                                hideAll();
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                AppBaseActivity.showToast(ErrorMsg.getMsg(ErrorMsg.change_video_err_code));
                            }
                        });
    }

    boolean enable;

    private void hideAll() {
        enable = false;
        if (getActivity() != null) {
            if (getActivity() instanceof MeetActivity) {
                ((MeetActivity) getActivity()).hideAll();
            } else {
                ((MeetWatchActivity) getActivity()).hideAll();
            }
        }
        resetAll();
    }

    public void setEnable(boolean b) {
        enable = b;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            requestDatas();
        }
    }

    void requestDatas() {
        enable = true;
        HYClient.getModule(ApiMeet.class).requestMeetDetail(
                SdkParamsCenter.Meet.RequestMeetDetail()
                        .setnListMode(1)
                        .setMeetDomainCode(strMeetDomaincode)
                        .setMeetID(nMeetID), new SdkCallback<CGetMeetingInfoRsp>() {
                    @Override
                    public void onSuccess(CGetMeetingInfoRsp cGetMeetingInfoRsp) {
                        listUser.clear();
                        for (CGetMeetingInfoRsp.UserInfo temp : cGetMeetingInfoRsp.listUser) {
                            if (temp.nJoinStatus == 2) {
                                SelectedModel model = new SelectedModel(temp);
                                listUser.add(model);
                            }
                        }
                        Collections.sort(listUser, new Comparator<SelectedModel<CGetMeetingInfoRsp.UserInfo>>() {
                            @Override
                            public int compare(SelectedModel<CGetMeetingInfoRsp.UserInfo> o1, SelectedModel<CGetMeetingInfoRsp.UserInfo> o2) {
                                if (o1.bean.nCombineStatus == 1 && o2.bean.nCombineStatus != 1)
                                    return -1;
                                if (o1.bean.nCombineStatus != 1 && o2.bean.nCombineStatus == 1)
                                    return 1;
                                if (o1.bean.nCombineStatus == 1 && o2.bean.nCombineStatus == 1)
                                    return 0;
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
     * 获取布局
     */
    public void requestLayoutInfo() {
        HYClient.getModule(ApiMeet.class)
                .getMeetingLayoutInfo(SdkParamsCenter.Meet.GetLayoutInfo()
                        .setnMeetingID(nMeetID)
                        .setStrMeetingDomainCode(strMeetDomaincode), new SdkCallback<CGetMeetingLayoutInfoRsp>() {
                    @Override
                    public void onSuccess(CGetMeetingLayoutInfoRsp info) {
                        hideInclude();
                        isBigSmall = info.nIsWideScreen == 1;
                        switch (info.lstLayoutUserInfo.size()) {
                            case 2:
                                vs_2.setVisibility(View.VISIBLE);
                                bindDouble(info);
                                break;
                            case 3:
                                if (info.nIsWideScreen == 1) {
                                    vs_3_4_5_leader.setVisibility(View.VISIBLE);
                                    bind345Leader(info);
                                } else {
                                    vs_3_4_avg.setVisibility(View.VISIBLE);
                                    bind34Avg(info);
                                }

                                break;
                            case 4:
                                if (info.nIsWideScreen == 1) {
                                    vs_3_4_5_leader.setVisibility(View.VISIBLE);
                                    bind345Leader(info);
                                } else {
                                    vs_3_4_avg.setVisibility(View.VISIBLE);
                                    bind34Avg(info);
                                }
                                break;
                            case 5:
                                if (info.nIsWideScreen == 1) {
                                    vs_3_4_5_leader.setVisibility(View.VISIBLE);
                                    bind345Leader(info);
                                } else {
                                    vs_5_6_avg.setVisibility(View.VISIBLE);
                                    bind56Avg(info);
                                }
                                break;
                            case 6:
                                if (info.nIsWideScreen == 1) {
                                    vs_6_leader.setVisibility(View.VISIBLE);
                                    bind6Leader(info);
                                } else {
                                    vs_5_6_avg.setVisibility(View.VISIBLE);
                                    bind56Avg(info);
                                }
                                break;
                        }
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        AppBaseActivity.showToast(ErrorMsg.getMsg(ErrorMsg.getlayout_info_err_code));
                    }
                });
    }

    @OnClick({
            R.id.tv_cancel,
            R.id.tv_change_video,
            R.id.vs_2_tv_1,
            R.id.vs_2_tv_2,
            R.id.vs_4_avg_tv_1,
            R.id.vs_4_avg_tv_2,
            R.id.vs_4_avg_tv_3,
            R.id.vs_4_avg_tv_4,
            R.id.vs_6_avg_tv_1,
            R.id.vs_6_avg_tv_2,
            R.id.vs_6_avg_tv_3,
            R.id.vs_6_avg_tv_4,
            R.id.vs_6_avg_tv_5,
            R.id.vs_6_avg_tv_6,
            R.id.vs_4_leader_tv_1,
            R.id.vs_4_leader_tv_2,
            R.id.vs_4_leader_tv_3,
            R.id.vs_4_leader_tv_4,
            R.id.vs_4_leader_tv_5,
            R.id.vs_6_leader_tv_1,
            R.id.vs_6_leader_tv_2,
            R.id.vs_6_leader_tv_3,
            R.id.vs_6_leader_tv_4,
            R.id.vs_6_leader_tv_5,
            R.id.vs_6_leader_tv_6,
            R.id.tv_title
    })
    void onKickoutClicked(View view) {
        switch (view.getId()) {
            case R.id.vs_2_tv_1:
                changeSelectedView(view, vs_2_cb_1);
                break;
            case R.id.vs_2_tv_2:
                changeSelectedView(view, vs_2_cb_2);
                break;
            case R.id.vs_4_avg_tv_1:
                changeSelectedView(view, vs_4_avg_cb_1);
                break;
            case R.id.vs_4_avg_tv_2:
                changeSelectedView(view, vs_4_avg_cb_2);
                break;
            case R.id.vs_4_avg_tv_3:
                changeSelectedView(view, vs_4_avg_cb_3);
                break;
            case R.id.vs_4_avg_tv_4:
                changeSelectedView(view, vs_4_avg_cb_4);
                break;
            case R.id.vs_6_avg_tv_1:
                changeSelectedView(view, vs_6_avg_cb_1);
                break;
            case R.id.vs_6_avg_tv_2:
                changeSelectedView(view, vs_6_avg_cb_2);
                break;
            case R.id.vs_6_avg_tv_3:
                changeSelectedView(view, vs_6_avg_cb_3);
                break;
            case R.id.vs_6_avg_tv_4:
                changeSelectedView(view, vs_6_avg_cb_4);
                break;
            case R.id.vs_6_avg_tv_5:
                changeSelectedView(view, vs_6_avg_cb_5);
                break;
            case R.id.vs_6_avg_tv_6:
                changeSelectedView(view, vs_6_avg_cb_6);
                break;
            case R.id.vs_4_leader_tv_1:
                changeSelectedView(view, vs_4_leader_cb_1);
                break;
            case R.id.vs_4_leader_tv_2:
                changeSelectedView(view, vs_4_leader_cb_2);
                break;
            case R.id.vs_4_leader_tv_3:
                changeSelectedView(view, vs_4_leader_cb_3);
                break;
            case R.id.vs_4_leader_tv_4:
                changeSelectedView(view, vs_4_leader_cb_4);
                break;
            case R.id.vs_4_leader_tv_5:
                changeSelectedView(view, vs_4_leader_cb_5);
                break;
            case R.id.vs_6_leader_tv_1:
                changeSelectedView(view, vs_6_leader_cb_1);
                break;
            case R.id.vs_6_leader_tv_2:
                changeSelectedView(view, vs_6_leader_cb_2);
                break;
            case R.id.vs_6_leader_tv_3:
                changeSelectedView(view, vs_6_leader_cb_3);
                break;
            case R.id.vs_6_leader_tv_4:
                changeSelectedView(view, vs_6_leader_cb_4);
                break;
            case R.id.vs_6_leader_tv_5:
                changeSelectedView(view, vs_6_leader_cb_5);
                break;
            case R.id.vs_6_leader_tv_6:
                changeSelectedView(view, vs_6_leader_cb_6);
                break;
            case R.id.tv_cancel:
                hideAll();
                break;
            case R.id.tv_change_video:
                changeVideoPage();
                break;
            case R.id.tv_title:
                if (enable)
                    layoutPopupWindow.showView(tv_title);
                break;
        }
    }

    /**
     * 改变了位置选择哦
     *
     * @param view
     */
    private void changeSelectedView(View view, CheckBox cb) {
        if (view.getTag() != null) {
            cb.setChecked(!cb.isChecked());
            if (cb.isChecked()) {
                currentView = view;
                boxChange(cb);

                boxBg();
                currentView.setBackgroundColor(colorRed);
            } else {
                currentView = null;
                boxBg();
            }
        } else {
            AppBaseActivity.showToast(getString(R.string.meet_weizhi_empty));
        }
    }

    private void bind6Leader(CGetMeetingLayoutInfoRsp info) {
        for (int i = 0; i < info.lstLayoutUserInfo.size(); i++) {
            switch (info.lstLayoutUserInfo.get(i).nLocateID) {
                case 1:
                    vs_6_leader_tv_1.setTag(info.lstLayoutUserInfo.get(i));
                    vs_6_leader_tv_1.setVisibility(View.VISIBLE);
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_6_leader_name_1.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
                case 2:
                    vs_6_leader_tv_2.setTag(info.lstLayoutUserInfo.get(i));
                    vs_6_leader_tv_2.setVisibility(View.VISIBLE);
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_6_leader_name_2.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
                case 3:
                    vs_6_leader_tv_3.setTag(info.lstLayoutUserInfo.get(i));
                    vs_6_leader_tv_3.setVisibility(View.VISIBLE);
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_6_leader_name_3.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
                case 4:
                    vs_6_leader_tv_4.setTag(info.lstLayoutUserInfo.get(i));
                    vs_6_leader_tv_4.setVisibility(View.VISIBLE);
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_6_leader_name_4.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
                case 5:
                    vs_6_leader_tv_5.setTag(info.lstLayoutUserInfo.get(i));
                    vs_6_leader_tv_5.setVisibility(View.VISIBLE);
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_6_leader_name_5.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
                case 6:
                    vs_6_leader_tv_6.setTag(info.lstLayoutUserInfo.get(i));
                    vs_6_leader_tv_6.setVisibility(View.VISIBLE);
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_6_leader_name_6.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
            }
        }
    }

    private void bind56Avg(CGetMeetingLayoutInfoRsp info) {
        vs_6_avg_tv_1.setVisibility(View.VISIBLE);
        vs_6_avg_tv_2.setVisibility(View.VISIBLE);
        vs_6_avg_tv_3.setVisibility(View.VISIBLE);
        vs_6_avg_tv_4.setVisibility(View.VISIBLE);
        vs_6_avg_tv_5.setVisibility(View.VISIBLE);
        if (info.lstLayoutUserInfo.size() == 6) {
            vs_6_avg_tv_6.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < info.lstLayoutUserInfo.size(); i++) {
            switch (info.lstLayoutUserInfo.get(i).nLocateID) {
                case 1:
                    vs_6_avg_tv_1.setTag(info.lstLayoutUserInfo.get(i));
                    vs_6_avg_tv_1.setVisibility(View.VISIBLE);
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_6_avg_name_1.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
                case 2:
                    vs_6_avg_tv_2.setTag(info.lstLayoutUserInfo.get(i));
                    vs_6_avg_tv_2.setVisibility(View.VISIBLE);
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_6_avg_name_2.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
                case 3:
                    vs_6_avg_tv_3.setTag(info.lstLayoutUserInfo.get(i));
                    vs_6_avg_tv_3.setVisibility(View.VISIBLE);
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_6_avg_name_3.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
                case 4:
                    vs_6_avg_tv_4.setTag(info.lstLayoutUserInfo.get(i));
                    vs_6_avg_tv_4.setVisibility(View.VISIBLE);
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_6_avg_name_4.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
                case 5:
                    vs_6_avg_tv_5.setTag(info.lstLayoutUserInfo.get(i));
                    vs_6_avg_tv_5.setVisibility(View.VISIBLE);
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_6_avg_name_5.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
                case 6:
                    vs_6_avg_tv_6.setTag(info.lstLayoutUserInfo.get(i));
                    vs_6_avg_tv_6.setVisibility(View.VISIBLE);
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_6_avg_name_6.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
            }
        }
    }

    private void bind34Avg(CGetMeetingLayoutInfoRsp info) {
        vs_4_avg_tv_1.setVisibility(View.VISIBLE);
        vs_4_avg_tv_2.setVisibility(View.VISIBLE);
        vs_4_avg_tv_3.setVisibility(View.VISIBLE);
        if (info.lstLayoutUserInfo.size() == 4) {
            vs_4_avg_tv_4.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < info.lstLayoutUserInfo.size(); i++) {
            switch (info.lstLayoutUserInfo.get(i).nLocateID) {
                case 1:
                    vs_4_avg_tv_1.setTag(info.lstLayoutUserInfo.get(i));
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_4_avg_name_1.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
                case 2:
                    vs_4_avg_tv_2.setTag(info.lstLayoutUserInfo.get(i));
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_4_avg_name_2.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
                case 3:
                    vs_4_avg_tv_3.setTag(info.lstLayoutUserInfo.get(i));
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_4_avg_name_3.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
                case 4:
                    vs_4_avg_tv_4.setTag(info.lstLayoutUserInfo.get(i));
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_4_avg_name_4.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
            }
        }
    }

    private void bind345Leader(CGetMeetingLayoutInfoRsp info) {
        vs_4_leader_tv_1.setVisibility(View.VISIBLE);
        vs_4_leader_tv_2.setVisibility(View.VISIBLE);
        vs_4_leader_tv_3.setVisibility(View.VISIBLE);
        if (info.lstLayoutUserInfo.size() == 4) {
            vs_4_leader_tv_4.setVisibility(View.VISIBLE);
        } else if (info.lstLayoutUserInfo.size() == 5) {
            vs_4_leader_tv_4.setVisibility(View.VISIBLE);
            vs_4_leader_tv_5.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < info.lstLayoutUserInfo.size(); i++) {
            switch (info.lstLayoutUserInfo.get(i).nLocateID) {
                case 1:
                    vs_4_leader_tv_1.setTag(info.lstLayoutUserInfo.get(i));
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_4_leader_name_1.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
                case 2:
                    vs_4_leader_tv_2.setTag(info.lstLayoutUserInfo.get(i));
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_4_leader_name_2.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
                case 3:
                    vs_4_leader_tv_3.setTag(info.lstLayoutUserInfo.get(i));
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_4_leader_name_3.setText(info.lstLayoutUserInfo.get(i).strUserName);
                        vs_4_leader_name_3.setVisibility(View.VISIBLE);
                    }
                    break;
                case 4:
                    vs_4_leader_tv_4.setTag(info.lstLayoutUserInfo.get(i));
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_4_leader_name_4.setText(info.lstLayoutUserInfo.get(i).strUserName);
                        vs_4_leader_name_4.setVisibility(View.VISIBLE);
                    }
                    break;
                case 5:
                    vs_4_leader_tv_5.setTag(info.lstLayoutUserInfo.get(i));
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_4_leader_name_5.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
            }
        }
    }

    private void bindDouble(CGetMeetingLayoutInfoRsp info) {
        for (int i = 0; i < info.lstLayoutUserInfo.size(); i++) {
            switch (info.lstLayoutUserInfo.get(i).nLocateID) {
                case 1:
                    vs_2_tv_1.setTag(info.lstLayoutUserInfo.get(i));
                    vs_2_tv_1.setVisibility(View.VISIBLE);
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_2_name_1.setText(info.lstLayoutUserInfo.get(i).strUserName);
                        vs_4_leader_name_5.setVisibility(View.VISIBLE);
                    }
                    break;
                case 2:
                    vs_2_tv_2.setTag(info.lstLayoutUserInfo.get(i));
                    vs_2_tv_2.setVisibility(View.VISIBLE);
                    if (info.lstLayoutUserInfo.get(i) != null) {
                        vs_2_name_2.setText(info.lstLayoutUserInfo.get(i).strUserName);
                    }
                    break;
            }
        }
    }

    private void hideInclude() {
        vs_2.setVisibility(View.GONE);
        vs_3_4_avg.setVisibility(View.GONE);
        vs_5_6_avg.setVisibility(View.GONE);
        vs_3_4_5_leader.setVisibility(View.GONE);
        vs_6_leader.setVisibility(View.GONE);
    }

    private void addCheckBox() {
        vs_2_cb_1.setVisibility(View.INVISIBLE);
        vs_2_cb_2.setVisibility(View.INVISIBLE);
        vs_4_avg_cb_1.setVisibility(View.INVISIBLE);
        vs_4_avg_cb_2.setVisibility(View.INVISIBLE);
        vs_4_avg_cb_3.setVisibility(View.INVISIBLE);
        vs_4_avg_cb_4.setVisibility(View.INVISIBLE);
        vs_6_avg_cb_1.setVisibility(View.INVISIBLE);
        vs_6_avg_cb_2.setVisibility(View.INVISIBLE);
        vs_6_avg_cb_3.setVisibility(View.INVISIBLE);
        vs_6_avg_cb_4.setVisibility(View.INVISIBLE);
        vs_6_avg_cb_5.setVisibility(View.INVISIBLE);
        vs_6_avg_cb_6.setVisibility(View.INVISIBLE);
        vs_4_leader_cb_1.setVisibility(View.INVISIBLE);
        vs_4_leader_cb_2.setVisibility(View.INVISIBLE);
        vs_4_leader_cb_3.setVisibility(View.INVISIBLE);
        vs_4_leader_cb_4.setVisibility(View.INVISIBLE);
        vs_4_leader_cb_5.setVisibility(View.INVISIBLE);
        vs_6_leader_cb_1.setVisibility(View.INVISIBLE);
        vs_6_leader_cb_2.setVisibility(View.INVISIBLE);
        vs_6_leader_cb_3.setVisibility(View.INVISIBLE);
        vs_6_leader_cb_4.setVisibility(View.INVISIBLE);
        vs_6_leader_cb_5.setVisibility(View.INVISIBLE);
        vs_6_leader_cb_6.setVisibility(View.INVISIBLE);

        checkBoxes.add(vs_2_cb_1);
        checkBoxes.add(vs_2_cb_2);

        checkBoxes.add(vs_4_avg_cb_1);
        checkBoxes.add(vs_4_avg_cb_2);
        checkBoxes.add(vs_4_avg_cb_3);
        checkBoxes.add(vs_4_avg_cb_4);

        checkBoxes.add(vs_6_avg_cb_1);
        checkBoxes.add(vs_6_avg_cb_2);
        checkBoxes.add(vs_6_avg_cb_3);
        checkBoxes.add(vs_6_avg_cb_4);
        checkBoxes.add(vs_6_avg_cb_5);
        checkBoxes.add(vs_6_avg_cb_6);

        checkBoxes.add(vs_4_leader_cb_1);
        checkBoxes.add(vs_4_leader_cb_2);
        checkBoxes.add(vs_4_leader_cb_3);
        checkBoxes.add(vs_4_leader_cb_4);
        checkBoxes.add(vs_4_leader_cb_5);

        checkBoxes.add(vs_6_leader_cb_1);
        checkBoxes.add(vs_6_leader_cb_2);
        checkBoxes.add(vs_6_leader_cb_3);
        checkBoxes.add(vs_6_leader_cb_4);
        checkBoxes.add(vs_6_leader_cb_5);
        checkBoxes.add(vs_6_leader_cb_6);


        bgViews.add(vs_2_tv_1);
        bgViews.add(vs_2_tv_2);

        bgViews.add(vs_4_avg_tv_1);
        bgViews.add(vs_4_avg_tv_2);
        bgViews.add(vs_4_avg_tv_3);
        bgViews.add(vs_4_avg_tv_4);

        bgViews.add(vs_6_avg_tv_1);
        bgViews.add(vs_6_avg_tv_2);
        bgViews.add(vs_6_avg_tv_3);
        bgViews.add(vs_6_avg_tv_4);
        bgViews.add(vs_6_avg_tv_5);
        bgViews.add(vs_6_avg_tv_6);

        bgViews.add(vs_4_leader_tv_1);
        bgViews.add(vs_4_leader_tv_2);
        bgViews.add(vs_4_leader_tv_3);
        bgViews.add(vs_4_leader_tv_4);
        bgViews.add(vs_4_leader_tv_5);

        bgViews.add(vs_6_leader_tv_1);
        bgViews.add(vs_6_leader_tv_2);
        bgViews.add(vs_6_leader_tv_3);
        bgViews.add(vs_6_leader_tv_4);
        bgViews.add(vs_6_leader_tv_5);
        bgViews.add(vs_6_leader_tv_6);

        tvViews.add(vs_6_leader_name_1);

        tvViews.add(vs_2_name_1);
        tvViews.add(vs_2_name_2);

        tvViews.add(vs_4_avg_name_1);
        tvViews.add(vs_4_avg_name_2);
        tvViews.add(vs_4_avg_name_3);
        tvViews.add(vs_4_avg_name_4);

        tvViews.add(vs_6_avg_name_1);
        tvViews.add(vs_6_avg_name_2);
        tvViews.add(vs_6_avg_name_3);
        tvViews.add(vs_6_avg_name_4);
        tvViews.add(vs_6_avg_name_5);
        tvViews.add(vs_6_avg_name_6);

        tvViews.add(vs_4_leader_name_1);
        tvViews.add(vs_4_leader_name_2);
        tvViews.add(vs_4_leader_name_3);
        tvViews.add(vs_4_leader_name_4);
        tvViews.add(vs_4_leader_name_5);

        tvViews.add(vs_6_leader_name_1);
        tvViews.add(vs_6_leader_name_2);
        tvViews.add(vs_6_leader_name_3);
        tvViews.add(vs_6_leader_name_4);
        tvViews.add(vs_6_leader_name_5);
        tvViews.add(vs_6_leader_name_6);
    }

    private void boxChange(CheckBox checkBox) {
        for (CheckBox temm : checkBoxes) {
            if (checkBox != temm) {
                temm.setChecked(false);
            }
        }
    }

    private void boxBg() {
        for (View temm : bgViews) {
            temm.setBackgroundColor(colorBlue);
        }
    }

    private void boxTag() {
        for (View temm : bgViews) {
            temm.setTag(null);
        }
    }

    public void resetAll() {
        for (CheckBox temm : checkBoxes) {
            temm.setChecked(false);
        }
        currentView = null;
        currentModel = null;

        bgHidel();
        boxBg();
        boxTag();

        for (SelectedModel<CGetMeetingInfoRsp.UserInfo> temp : listUser) {
            temp.isChecked = false;
        }

        for (TextView temp : tvViews) {
            temp.setText("");
        }

        adapter.notifyDataSetChanged();
    }

    private void bgHidel() {
        for (View view : bgViews) {
            view.setVisibility(View.GONE);
        }
    }

}
