package huaiye.com.vim.ui.meet.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.ttyy.commonanno.Injectors;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.views.pickers.CustomDatePicker;
import huaiye.com.vim.common.views.pickers.SelectItemDialog;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.bean.ContactsGroupUserListBean;
import huaiye.com.vim.ui.contacts.ContactsChoiceByAllFriendActivity;
import huaiye.com.vim.ui.contacts.ContactsChoiceByGroupUserActivity;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContacts;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static android.app.Activity.RESULT_OK;
import static huaiye.com.vim.common.ErrorMsg.create_group_err_code;

/**
 * author: admin
 * date: 2018/01/15
 * version: 0
 * mail: secret
 * desc: MeetCreateHeaderView
 */
@BindLayout(R.layout.header_meet_create_new)
public class MeetCreateHeaderView extends RelativeLayout implements View.OnClickListener {

    @BindView(R.id.create_meet_name)
    EditText create_meet_name;
    @BindView(R.id.create_meet_detail)
    EditText create_meet_detail;
    @BindView(R.id.create_meet_time_layout)
    LinearLayout create_meet_time_layout;
    @BindView(R.id.create_meet_time)
    TextView create_meet_time;
    @BindView(R.id.create_meet_duration_layout)
    LinearLayout create_meet_duration_layout;
    @BindView(R.id.create_meet_duration)
    EditText create_meet_duration;
    @BindView(R.id.create_meet_add_person)
    LinearLayout create_meet_add_person;

    /*@BindView(R.id.need_record)
    View need_record;
    @BindView(R.id.ll_divider)
    View ll_divider;
    @BindView(R.id.ll_order)
    View ll_order;
    @BindView(R.id.tv_meet_model)
    TextView tv_meet_model;
    @BindView(R.id.tv_meet_start)
    TextView tv_meet_start;
    @BindView(R.id.tv_meet_long)
    EditText tv_meet_long;
    @BindView(R.id.tv_member_add)
    View tv_member_add;*/

    boolean isMaster;
    boolean isReq = false;
    public boolean needAddSelfMain = true;
    /**
     * 2--主辅布局 其他--均等布局
     */
    public int mMeetModel;
    private Context mContext;

    public ContactsGroupUserListBean getmGroupInfoListBean() {
        return mGroupInfoListBean;
    }

    public void setmGroupInfoListBean(ContactsGroupUserListBean mGroupInfoListBean) {
        this.mGroupInfoListBean = mGroupInfoListBean;
    }

    private ContactsGroupUserListBean mGroupInfoListBean;

    ArrayList<SelectItemDialog.SelectBean> selectBeans = new ArrayList<>();
    String Temp = "yyyy-MM-dd HH:mm";
    SimpleDateFormat sdf = new SimpleDateFormat(Temp, Locale.CHINA);

    public MeetCreateHeaderView(Context context, final boolean isMeet, boolean isOrder) {
        this(context, null);
        mContext = context;
        setMaster(true);
        create_meet_add_person.setOnClickListener(this);
        /*tv_hint1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_meet_name.requestFocus();
                AppUtils.showKeyboard(edt_meet_name);
                edt_meet_name.setSelection(edt_meet_name.getText().toString().length());
            }
        });
        tv_hint2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_meet_long.requestFocus();
                AppUtils.showKeyboard(tv_meet_long);
                tv_meet_long.setSelection(tv_meet_long.getText().toString().length());
            }
        });
        edt_meet_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    if (isMeet) {
                        tv_hint1.setHint("请输入会议名称");
                    } else {
                        tv_hint1.setHint("请输入群聊名称");
                    }
                } else {
                    tv_hint1.setHint("");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        tv_meet_long.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    tv_hint2.setHint("请输入会议时长");
                } else {
                    tv_hint2.setHint("");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });*/

        /*if (isMeet) {
            need_record.setVisibility(VISIBLE);
            ll_divider.setVisibility(VISIBLE);
            tv_label.setText("会议名称");
            tv_hint1.setHint("请输入会议名称");
            tv_label_user.setText("参会人");
        } else {
            need_record.setVisibility(GONE);
            ll_divider.setVisibility(GONE);
            tv_label.setText("群聊名称");
            tv_hint1.setHint("请输入群聊名称");
            tv_label_user.setText("群聊成员");
        }*/


//        ll_order.setVisibility(isOrder ? VISIBLE : GONE);
        if (!isOrder) {
            create_meet_time_layout.setVisibility(View.GONE);
            create_meet_duration_layout.setVisibility(View.GONE);
            /*tv_meet_model.setOnClickListener(this);
            tv_meet_start.setOnClickListener(this);

            SelectItemDialog.SelectBean xiangce = new SelectItemDialog.SelectBean();
            xiangce.name = "主辅布局";
            selectBeans.add(xiangce);

            SelectItemDialog.SelectBean paizhao = new SelectItemDialog.SelectBean();
            paizhao.name = "均等布局";
            selectBeans.add(paizhao);

            tv_meet_model.setText("均等布局");
            tv_meet_start.setText(sdf.format(new Date(System.currentTimeMillis() + 30 * 60 * 1000)).substring(5));
            tv_meet_start.setHint(sdf.format(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) + ":00");

            tv_meet_long.setText("2");*/
        } else {
            create_meet_time_layout.setVisibility(View.VISIBLE);
//            create_meet_duration_layout.setVisibility(View.VISIBLE);
//            create_meet_time_layout.setOnClickListener(this);
            create_meet_time.setOnClickListener(this);
            create_meet_time.setText(sdf.format(new Date(System.currentTimeMillis() + 30 * 60 * 1000)).substring(5));
            create_meet_time.setHint(sdf.format(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) + ":00");
            create_meet_duration.setText("2");
        }

    }

    public MeetCreateHeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeetCreateHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Injectors.get().injectView(this);
    }

    public void setMaster(boolean isMaster) {
        this.isMaster = isMaster;
        /*if (!isMaster) {
            edt_meet_name.setEnabled(false);
            tv_hint1.setEnabled(false);
            tv_hint2.setEnabled(false);
            tv_meet_long.setEnabled(false);
            cb_record.setEnabled(false);
            tv_member_add.setVisibility(GONE);
        } else {
            tv_hint1.setEnabled(true);
            tv_hint2.setEnabled(true);
            edt_meet_name.setEnabled(true);
            tv_meet_long.setEnabled(true);
            cb_record.setEnabled(true);
            tv_member_add.setVisibility(VISIBLE);
        }*/
    }

    public void setMeetName(String name){
        if(create_meet_name == null){
            return;
        }
        create_meet_name.setText(name);
    }

    public String getMeetName() {
        return create_meet_name.getText().toString();
    }

    public EditText getNameView() {
        return create_meet_name;
    }

    public String getMeetDesc() {
        return create_meet_detail.getText().toString();
    }

    public EditText getMeetDescView() {
        return create_meet_detail;
    }

    public String getMeetStartTime() {
        if (!create_meet_time.getText().toString().contains("-")) {
            return "";
        }
        return create_meet_time.getHint().toString();
    }

    public int getMeetLong() {
        if (TextUtils.isEmpty(create_meet_duration.getText()) || !TextUtils.isDigitsOnly(create_meet_duration.getText().toString())) {
            return 0;
        }
        return Integer.parseInt(create_meet_duration.getText().toString()) * 60 * 60;
    }

    public SdkBaseParams.MeetMode getModel() {
//        return tv_meet_model.getText().toString().equals("主辅布局") ? SdkBaseParams.MeetMode.Host : SdkBaseParams.MeetMode.Normal;
        return mMeetModel == 2 ? SdkBaseParams.MeetMode.Host : SdkBaseParams.MeetMode.Normal;
    }

    public boolean isMeetRecord() {
//        return cb_record.isChecked();
        return false;
    }

    /*@OnClick(R.id.create_meet_add_person)
    void onMemberAddClick() {
//        if (!isMaster) return;
        Log.d("VIMApp", "onMemberAddClick");
        Intent intent = new Intent(getContext(), ContactsChoiceByAllFriendActivity.class);

        intent.putExtra("isSelectUser", true);
        intent.putExtra("needAddSelf", needAddSelfMain);
        intent.putExtra("titleName", getContext().getString(R.string.add_meet_person));
        *//*if(mContext instanceof MeetCreateActivity){
            MeetCreateActivity activity = (MeetCreateActivity) mContext;
            intent.putExtra(ContactsChoiceActivity.SELECTED_CONTACTS, activity.getChoicedContacts());
        }*//*
     *//*if (need_record.getVisibility() == GONE) {
            intent.putExtra("titleName", "创建群组");
        } else {
            intent.putExtra("titleName", ll_order.getVisibility() == View.VISIBLE ? "创建预约会议" : "创建即时会议");
        }*//*
        ((Activity) getContext()).startActivityForResult(intent, 1000);
        needAddSelfMain = false;
    }*/

    public void createGroup(final AppBaseActivity activity) {
        if (isReq) return;
        isReq = true;
        ModelApis.Contacts().createGroup(create_meet_name.getText().toString(),
                ChoosedContacts.get().getContacts(false),
                new ModelCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        activity.showToast("创建成功");
                        activity.setResult(RESULT_OK);
                        activity.finish();
                        isReq = false;
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        activity.showToast(ErrorMsg.getMsg(create_group_err_code));
                        isReq = false;
                    }

                });
    }

    @Override
    public void onClick(View v) {
        if (!isMaster) return;
        switch (v.getId()) {
            /*case R.id.tv_meet_model:
                SelectItemDialog selectItemDialog = new SelectItemDialog(getContext(),
                        new SelectItemDialog.onDialogItemClickListener() {
                            @Override
                            public void onItemClick(SelectItemDialog.SelectBean branch) {
                                tv_meet_model.setText(branch.name);
                            }
                        }, selectBeans);
                selectItemDialog.show();
                break;*/
            case R.id.create_meet_time:
                CustomDatePicker customDatePicker = new CustomDatePicker(2, getContext(), new CustomDatePicker.ResultHandler() {
                    @Override
                    public void handle(String time, long timelong) {
                        create_meet_time.setText(time.substring(5));
                        create_meet_time.setHint(time + ":00");
                    }
                }, System.currentTimeMillis() + 60 * 1000, System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000);
                customDatePicker.showYear(false).setIsLoop(false);
                customDatePicker.show("");
                break;
            case R.id.create_meet_add_person:
                if(null!=mGroupInfoListBean){
                    Intent intent = new Intent(getContext(), ContactsChoiceByGroupUserActivity.class);
                    intent.putExtra("isSelectUser", true);
                    intent.putExtra("needAddSelf", needAddSelfMain);
                    intent.putExtra("mGroupUserListBean",mGroupInfoListBean);
                    intent.putExtra("titleName", getContext().getString(R.string.add_meet_person));
                    ((Activity) getContext()).startActivityForResult(intent, 1000);
                }else{
                    Intent intent = new Intent(getContext(), ContactsChoiceByAllFriendActivity.class);

                    intent.putExtra("isSelectUser", true);
                    intent.putExtra("needAddSelf", needAddSelfMain);
                    intent.putExtra("titleName", getContext().getString(R.string.add_meet_person));
                    ((Activity) getContext()).startActivityForResult(intent, 1000);
                }

                break;
            default:
                break;
        }
    }

    /**
     * 展示信息
     *
     * @param cGetMeetingInfoRsp
     */
    public void showInfo(CGetMeetingInfoRsp cGetMeetingInfoRsp) {
        mMeetModel = cGetMeetingInfoRsp.nMeetingMode;
        create_meet_name.setText(cGetMeetingInfoRsp.strMeetingName);
//        edt_meet_name.setText(cGetMeetingInfoRsp.strMeetingName);
//        tv_meet_model.setText(cGetMeetingInfoRsp.nMeetingMode == 2 ? "主辅布局" : "均等布局");
        create_meet_detail.setText(cGetMeetingInfoRsp.strMeetingDesc);
        create_meet_time.setText(cGetMeetingInfoRsp.strStartTime.substring(5, 16));
        create_meet_time.setHint(cGetMeetingInfoRsp.strStartTime);
        create_meet_duration.setText(cGetMeetingInfoRsp.nTimeDuration / 3600 + "");

//        cb_record.setChecked(cGetMeetingInfoRsp.nRecordID == 0 ? false : true);
    }
}