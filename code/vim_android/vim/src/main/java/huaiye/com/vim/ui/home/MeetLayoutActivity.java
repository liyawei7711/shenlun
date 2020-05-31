package huaiye.com.vim.ui.home;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.cmf.sdp.SdpUITask;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingLayoutInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CMeetingChangeLayoutTypeRsp;
import com.huaiye.sdk.sdpmsgs.meet.CMeetingUserSwapRsp;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingStatusInfo;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyPeerUserMeetingInfo;
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

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.recycle.LiteBaseAdapter;
import huaiye.com.vim.ui.meet.LayoutPopupWindow;
import huaiye.com.vim.ui.meet.basemodel.SelectedModel;
import huaiye.com.vim.ui.meet.viewholder.MemberLayoutHolder;

/**
 * author: admin
 * date: 2018/07/25
 * version: 0
 * mail: secret
 * desc: MeetControllActivity
 */
@BindLayout(R.layout.activity_meet_layout)
public class MeetLayoutActivity extends AppBaseActivity implements SdpUITask.SdpUIListener {

    @BindView(R.id.refresh_view)
    SwipeRefreshLayout refresh_view;

    @BindView(R.id.tv_notice)
    View tv_notice;
    @BindView(R.id.rct_view)
    RecyclerView rct_view;
    @BindView(R.id.ll_title)
    View ll_title;
    @BindView(R.id.tv_title)
    TextView tv_title;

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

    @BindExtra
    String strMeetDomainCode;
    @BindExtra
    int nMeetID;

    int colorRed = Color.RED;
    int colorBlue = Color.parseColor("#9FB8E7");
    boolean isBigSmall;
    boolean enable;

    Drawable drawableUp;
    Drawable drawableDown;

    @Override
    protected void initActionBar() {
        EventBus.getDefault().register(this);
        getNavigate().setVisibility(View.GONE);
        mSdpUITask = new SdpUITask();
        mSdpUITask.setSdpMessageListener(this);
        mSdpUITask.registerSdpNotify(CNotifyPeerUserMeetingInfo.SelfMessageId);
    }

    @Override
    public void doInitDelay() {

        drawableUp = getResources().getDrawable(R.drawable.shouqi);
        drawableUp.setBounds(0, 0, drawableUp.getMinimumWidth(), drawableUp.getMinimumHeight());//对图片进行压缩
        drawableDown = getResources().getDrawable(R.drawable.zhankai);
        drawableDown.setBounds(0, 0, drawableUp.getMinimumWidth(), drawableUp.getMinimumHeight());//对图片进行压缩

        rct_view.setLayoutManager(new LinearLayoutManager(this));

        layoutPopupWindow = new LayoutPopupWindow(this);
        layoutPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                tv_title.setCompoundDrawables(null, null, drawableDown, null);
            }
        });
        layoutPopupWindow.setConfirmClickListener(new LayoutPopupWindow.ConfirmClickListener() {
            @Override
            public void onClickShow(boolean value) {
                if (value == isBigSmall) {
                    return;
                }

                HYClient.getModule(ApiMeet.class).switchMeetLayout(SdkParamsCenter.Meet.SwitchMeetLayout()
                                .setnMeetingID(nMeetID)
                                .setStrMeetingDomainCode(strMeetDomainCode),
                        new SdkCallback<CMeetingChangeLayoutTypeRsp>() {
                            @Override
                            public void onSuccess(CMeetingChangeLayoutTypeRsp cMeetingChangeLayoutTypeRsp) {
                                resetAll();
                                requestDatas();
                                requestLayoutInfo();
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                showToast(ErrorMsg.getMsg(ErrorMsg.switch_meet_layout_code));
                            }
                        });
            }
        });
        adapter = new LiteBaseAdapter<>(this,
                listUser,
                MemberLayoutHolder.class,
                R.layout.item_meet_members_layout,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SelectedModel<CGetMeetingInfoRsp.UserInfo> bean = (SelectedModel<CGetMeetingInfoRsp.UserInfo>) v.getTag();
                        if (bean.bean.nJoinStatus != 2) {
                            showToast("该用户未参会，不能选择");
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

        refresh_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                addCheckBox();

                requestDatas();
                requestLayoutInfo();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        addCheckBox();

        requestDatas();
        requestLayoutInfo();
    }

    private void changeVideoPage() {
        if (currentModel == null) {
            showToast("交换人员不能为空");
            return;
        }
        if (currentView == null) {
            showToast("交换位置不能为空");
            return;
        }
        CGetMeetingLayoutInfoRsp.UserInfo currentB = (CGetMeetingLayoutInfoRsp.UserInfo) currentView.getTag();
        HYClient.getModule(ApiMeet.class)
                .changeMeetingMemberLayout(SdkParamsCenter.Meet.UserSwap().setnMeetingID(nMeetID)
                                .setStrMeetingDomainCode(strMeetDomainCode)
                                .setStrUserADomainCode(currentModel.bean.strUserDomainCode)
                                .setStrUserAID(currentModel.bean.strUserID)
                                .setStrUserBDomainCode(currentB.strUserDomainCode)
                                .setStrUserBID(currentB.strUserID),
                        new SdkCallback<CMeetingUserSwapRsp>() {
                            @Override
                            public void onSuccess(CMeetingUserSwapRsp cGetMeetingInfoRsp) {
                                showToast("交换成功,请稍后");
                                finish();
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                showToast(ErrorMsg.getMsg(ErrorMsg.change_video_err_code));
                            }
                        });
    }

    void requestDatas() {
        enable = true;
        HYClient.getModule(ApiMeet.class).requestMeetDetail(
                SdkParamsCenter.Meet.RequestMeetDetail()
                        .setnListMode(1)
                        .setMeetDomainCode(strMeetDomainCode)
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
                        showToast(ErrorMsg.getMsg(ErrorMsg.get_meet_info_err_code));
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
                        .setStrMeetingDomainCode(strMeetDomainCode), new SdkCallback<CGetMeetingLayoutInfoRsp>() {
                    @Override
                    public void onSuccess(CGetMeetingLayoutInfoRsp info) {
                        hideInclude();
                        isBigSmall = info.nIsWideScreen == 1;

                        if (isBigSmall) {
                            tv_title.setText("大小布局");
                        } else {
                            tv_title.setText("等分布局");
                        }

                        tv_notice.setVisibility(View.GONE);
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
                            default:
                                tv_notice.setVisibility(View.VISIBLE);
                                break;
                        }
                        refresh_view.setRefreshing(false);
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast(ErrorMsg.getMsg(ErrorMsg.getlayout_info_err_code));
                    }
                });
    }

    @OnClick({
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
            R.id.ll_title,
            R.id.tv_right,
            R.id.tv_left
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
            case R.id.tv_left:
                onBackPressed();
                break;
            case R.id.tv_right:
                changeVideoPage();
                break;
            case R.id.ll_title:
                if (listUser.size() <= 1) {
                    showToast("人员不足无法改变布局");
                    return;
                }
                if (enable) {
                    tv_title.setCompoundDrawables(null, null, drawableUp, null);
                    layoutPopupWindow.showView(ll_title);
                } else {
                    if (layoutPopupWindow.isShowing()) {
                        layoutPopupWindow.dismiss();
                    }
                }
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
            showToast("该位置没有人哦~");
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyMeetingStatusInfo info) {
        if (info.nMeetingStatus == 2) {
            showToast("会议已结束");
            onBackPressed();
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
