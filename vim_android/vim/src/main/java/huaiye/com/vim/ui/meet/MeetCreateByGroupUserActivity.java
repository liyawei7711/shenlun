package huaiye.com.vim.ui.meet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.meet.ParamsAppointmentSetMeet;
import com.huaiye.sdk.sdkabi._params.meet.ParamsCreateMeet;
import com.huaiye.sdk.sdpmsgs.meet.CSendNotifyPredetermineMeetingRsp;
import com.huaiye.sdk.sdpmsgs.meet.CSetPredetermineMeetingReq;
import com.huaiye.sdk.sdpmsgs.meet.CSetPredetermineMeetingRsp;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.recycle.RecycleTouchUtils;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.views.MeetTypeChoosePopupWindow;
import huaiye.com.vim.common.views.NavigateView;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.constants.AppFrequentlyConstants;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.models.contacts.bean.ContactsGroupUserListBean;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContactsNew;
import huaiye.com.vim.ui.meet.views.MeetCreateHeaderView;
import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;

import static android.support.v7.widget.helper.ItemTouchHelper.Callback.makeMovementFlags;
import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * author: admin
 * date: 2018/01/15
 * version: 0
 * mail: secret
 * desc: MeetCreateActivity
 */
@BindLayout(R.layout.activity_meet_create_new)
public class MeetCreateByGroupUserActivity extends AppBaseActivity implements MeetTypeChoosePopupWindow.OnMeetTypeBtnClickLinsenter {

    @BindView(R.id.create_meet_rct_view)
    RecyclerView create_meet_rct_view;

    @BindExtra
    int nMeetType;//1--即时会议 2--预约会议
    @BindExtra
    ContactsGroupUserListBean mGroupInfoListBean;

    private MeetTypeChoosePopupWindow mMeetTypeChoosePopupWindow;

    MeetCreateHeaderView header;
    EXTRecyclerAdapter<User> adapter;
    RequestOptions requestOptions;
    //    ArrayList<ContactData> data = new ArrayList<>();
//    private ArrayList<User> mChoicedContacts = new ArrayList<>();

    @Override
    protected void initActionBar() {
        NavigateView navigateView = getNavigate();
        navigateView.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (nMeetType == 1) {
            navigateView.setRightText(getString(R.string.meet_notice8));
            navigateView.setTitlText(getString(R.string.meet_notice9));
        } else {
            navigateView.setRightText(getString(R.string.makesure));
            navigateView.setTitlText(getString(R.string.create_order_meeting));
        }
        navigateView.setRightTextColor(ContextCompat.getColor(this, R.color.blue_2E67FE));
        navigateView.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mMeetTypeChoosePopupWindow.isShowing()) {
                    mMeetTypeChoosePopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 100);
                } else {
                    mMeetTypeChoosePopupWindow.dismiss();
                }
            }
        });
    }

    @Override
    public void doInitDelay() {
//        ChoosedContacts.get().initDelete();
        requestOptions = new RequestOptions();
        requestOptions.centerCrop()
                .dontAnimate()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.default_image_personal)
                .error(R.drawable.default_image_personal)
                .optionalTransform(new CircleCrop());
        ChoosedContactsNew.get().clear();
        ChoosedContactsNew.get().addSelf();
        mMeetTypeChoosePopupWindow = new MeetTypeChoosePopupWindow(this, this);
        mMeetTypeChoosePopupWindow.initView();
        create_meet_rct_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EXTRecyclerAdapter<User>(R.layout.item_meetcreate_member) {
            @Override
            public void onBindViewHolder(EXTViewHolder extViewHolder, int i, User contactData) {
                if (i < getHeaderViewsCount()) {
                    return;
                }
                if (contactData.strUserName.equals(AppDatas.Auth().getUserName())) {
//                    extViewHolder.setVisibility(R.id.iv_mainer, View.VISIBLE);
                    extViewHolder.setVisibility(R.id.tv_master, View.VISIBLE);
//                    extViewHolder.setVisibility(R.id.tv_master_set, View.GONE);
                } else {
//                    extViewHolder.setVisibility(R.id.iv_mainer, View.GONE);
                    extViewHolder.setVisibility(R.id.tv_master, View.GONE);
//                    extViewHolder.setVisibility(R.id.tv_master_set, View.VISIBLE);
                }
                extViewHolder.setText(R.id.tv_user_name, contactData.strUserName);
                ImageView imageView = extViewHolder.itemView.findViewById(R.id.iv_user_head);
                Glide.with(getBaseContext())
                        .load(AppDatas.Constants().getAddressWithoutPort() + contactData.strHeadUrl)
                        .apply(requestOptions)
                        .into(imageView);
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

        header = new MeetCreateHeaderView(this, true, nMeetType == 1 ? false : true);
        header.setmGroupInfoListBean(mGroupInfoListBean);
        adapter.addHeaderView(header);
        header.setMeetName(AppDatas.Auth().getUserName() + getString(R.string.meet_notice10));

        new RecycleTouchUtils().initTouch(new RecycleTouchUtils.ITouchEvent() {
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                User temp = adapter.getDataForItemPosition(viewHolder.getAdapterPosition());
//                ChoosedContacts.get().deleteSelected(temp);
//                mChoicedContacts.remove(temp);
                ChoosedContactsNew.get().removeContacts(temp);
                adapter.notifyDataSetChanged();
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder.getLayoutPosition() < adapter.getHeaderViewsCount()) {
                    return 0;
                }

                return makeMovementFlags(0, ItemTouchHelper.LEFT);
            }
        }).attachToRecyclerView(create_meet_rct_view);
        if (null != mGroupInfoListBean && null != mGroupInfoListBean.lstGroupUser && mGroupInfoListBean.lstGroupUser.size() > 0) {
            new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
                @Override
                public Object doOnThread() {
                    for (ContactsGroupUserListBean.LstGroupUser lstGroupUser : mGroupInfoListBean.lstGroupUser) {
                        User temp_item = new User();
                        temp_item.strUserID = lstGroupUser.strUserID;
                        temp_item.strUserName = lstGroupUser.strUserName;
                        temp_item.strDomainCode = lstGroupUser.strUserDomainCode;
                        temp_item.strHeadUrl = lstGroupUser.strHeadUrl;
                        ChoosedContactsNew.get().addContacts(temp_item);
                    }
                    return "";
                }

                @Override
                public void doOnMain(Object data) {
                    adapter.setDatas(ChoosedContactsNew.get().getContacts());
                    create_meet_rct_view.setAdapter(adapter);

                }
            });
        } else {
            adapter.setDatas(ChoosedContactsNew.get().getContacts());
            create_meet_rct_view.setAdapter(adapter);

        }

    }

    private void createMeet(int voiceIntercom) {
        if (TextUtils.isEmpty(header.getMeetName())) {
            showToast(getString(R.string.meet_notice11));
            return;
        }
        AppUtils.closeKeyboard(header.getNameView());

        /*if (TextUtils.isEmpty(header.getMeetDesc())) {
            showToast("会议详情不能为空");
            return;
        }*/
        AppUtils.closeKeyboard(header.getMeetDescView());

        if (nMeetType == 2) {
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
        }
        if(ChoosedContactsNew.get().getContacts() == null ||
                ChoosedContactsNew.get().getContacts().size() <= 1){
            showToast(getString(R.string.meet_xuanze_notice));
            return;
        }

        if (MbeConfigParaValue != -1 && adapter.getDatasCount() > MbeConfigParaValue) {
            showToast(getString(R.string.meet_notice15, MbeConfigParaValue));
            return;
        }
        if (nMeetType == 1) {
            //创建即时会议
            mZeusLoadView.loadingText(getString(R.string.common_notice27)).setLoading();
            getNavigate().setRightEnable(false);
            ParamsCreateMeet params = SdkParamsCenter.Meet.CreateMeet();
            if (header.needAddSelfMain) {
                params.setInviteSelf(1);
            } else {
                params.setInviteSelf(ChoosedContactsNew.get().isContain(String.valueOf(AppDatas.Auth().getUserID())) ? 1 : 0);
            }
            if (voiceIntercom == 0) {
                params.setMemberMediaMode(SdkBaseParams.MediaMode.AudioAndVideo);//会议模式(纯语音,还是音视频)

            } else if (voiceIntercom == 1) {
                params.setMemberMediaMode(SdkBaseParams.MediaMode.Audio);//会议模式(纯语音,还是音视频)

            }
            params.setUsers(getStartMeetingUser(ChoosedContactsNew.get().getContacts()))
                    .setOpenRecord(header.isMeetRecord())
                    .setMeetName(header.getMeetName().trim())
                    .setMeetDesc(header.getMeetDesc())
                    .setSynthesise(false);

            HYClient.getModule(ApiMeet.class)
                    .createMeeting(params, new SdkCallback<CStartMeetingRsp>() {
                        @Override
                        public void onSuccess(CStartMeetingRsp cStartMeetingRsp) {
                            if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                                for (CStartMeetingReq.UserInfo temp : getStartMeetingUser(ChoosedContactsNew.get().getContacts())) {
                                    if (!temp.equals(HYClient.getSdkOptions().User().getUserId())) {
                                        EncryptUtil.startEncrypt(true, temp.strUserID, temp.strUserDomainCode,
                                                cStartMeetingRsp.nMeetingID + "", cStartMeetingRsp.strMeetingDomainCode, null);
                                    }
                                }
                            } else {
                                if (nEncryptIMEnable) {
                                    EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                    finish();
                                    return;
                                }
                            }
                            /*ChoosedContacts.get().clearTemp();
                            ChoosedContacts.get().clear();*/
                            AppFrequentlyConstants.get().AddContacts(ChoosedContactsNew.get().getContacts());
                            ChoosedContactsNew.get().clear();
                            cancle();
                            finish();
                        }

                        @Override
                        public void onError(ErrorInfo errorInfo) {
                            cancle();
                            showToast(ErrorMsg.getMsg(ErrorMsg.create_meet_err_code));
                        }
                    });
        } else {
            //创建预约会议
            ParamsAppointmentSetMeet params = SdkParamsCenter.Meet.AppointmentSetMeet();
            /*if (header.needAddSelfMain) {
                params.setInviteSelf(1);
            } else {
                params.setInviteSelf(isContainSelf() ? 1 : 0);
            }*/
            int inviteSelf = ChoosedContactsNew.get().isContain(String.valueOf(AppDatas.Auth().getUserID())) ? 1 : 0;
            params.setInviteSelf(inviteSelf);
            params.setUsers(convertContacts(ChoosedContactsNew.get().getContacts()))
                    .setOpenRecord(header.isMeetRecord())
                    .setDtMeetingStartTime(header.getMeetStartTime())
                    .setMeetMode(header.getModel())
                    .setnMeetingDuration(header.getMeetLong())
                    .setMeetName(header.getMeetName().trim());
            params.setMeetDesc(header.getMeetDesc());

            HYClient.getModule(ApiMeet.class)
                    .setAppointmentMeeting(params, new SdkCallback<CSetPredetermineMeetingRsp>() {
                        @Override
                        public void onSuccess(CSetPredetermineMeetingRsp info) {
                            /*ChoosedContacts.get().clearTemp();
                            ChoosedContacts.get().clear();*/
                            AppFrequentlyConstants.get().AddContacts(ChoosedContactsNew.get().getContacts());
                            ChoosedContactsNew.get().clear();

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
    }

    private void cancle() {
        getNavigate().setRightEnable(true);
        mZeusLoadView.dismiss();
    }

    @Override
    protected void afterOnLineUser(boolean value) {
        super.afterOnLineUser(value);
        /*data.clear();
        data.addAll(ChoosedContacts.get().getContacts(null, false));*/
        adapter.setDatas(ChoosedContactsNew.get().getContacts());
        adapter.notifyDataSetChanged();
        header.needAddSelfMain = false;
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

    public ArrayList<CStartMeetingReq.UserInfo> getStartMeetingUser(ArrayList<User> contacts) {
        ArrayList<CStartMeetingReq.UserInfo> users = new ArrayList<>();

        for (User tmp : contacts) {
            CStartMeetingReq.UserInfo user = new CStartMeetingReq.UserInfo();
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

    /*public ArrayList<User> getChoicedContacts() {
        return mChoicedContacts;
    }*/

    @SuppressLint("NewApi")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.releaseInstance();
        }
        ChoosedContactsNew.get().clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK
                && requestCode == 1000) {
            /*Bundle bundle = data.getExtras();
            ArrayList<User> list = bundle.getParcelableArrayList(ContactsChoiceActivity.RESULT_CONTACTS);
            mChoicedContacts.clear();
            if (list != null && list.size() > 0) {
                mChoicedContacts.addAll(list);
            }*/
            // requestOnLine(false);
            adapter.notifyDataSetChanged();

        }
        else {
        }
    }

    @Override
    public void onMeetTypeAudioVideoClick() {
        createMeet(0);//0 音视频
    }

    @Override
    public void onMeetJustAudioClick() {
        createMeet(1);//1 纯音频
    }

    @Override
    public void onMeetTypecancleClick() {

    }
}
