package huaiye.com.vim.ui.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.views.flowlayout.base.FlowBaseAdapter;
import huaiye.com.vim.common.views.flowlayout.callback.OnFlowItemClickedListener;
import huaiye.com.vim.common.views.flowlayout.impl.SimpleTagsLabelLayout;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.bean.ContactData;
import huaiye.com.vim.models.contacts.bean.GroupContacts;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContacts;
import huaiye.com.vim.ui.contacts.sharedata.GroupDeleteChoosedContacts;
import huaiye.com.vim.ui.contacts.views.GroupMemberView;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * author: admin
 * date: 2017/12/29
 * version: 0
 * mail: secret
 * desc: MeetCreateActivity
 */
@BindLayout(R.layout.activity_group_detail)
public class GroupDetailActivity extends AppBaseActivity {

    @BindView(R.id.tv_group_name)
    TextView tv_group_name;
    @BindView(R.id.tv_all)
    TextView tv_all;

    @BindView(R.id.edt_meet_name)
    EditText edt_meet_name;
    @BindView(R.id.cb_record)
    CheckBox cb_record;

    @BindView(R.id.tags_layer)
    SimpleTagsLabelLayout tags_layer;
    @BindView(R.id.tv_quit_group)
    TextView tv_quit_group;

    MembersAdapter mUsersAdapter;

    @BindExtra
    long groupId;
    @BindExtra
    String createUserId;
    @BindExtra
    String groupName;
    GroupContacts mGroupContacts;
    private ContactData self;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText("群组信息")
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        mUsersAdapter = new MembersAdapter();
        tags_layer.setAdapter(mUsersAdapter);
        tags_layer.setOnFlowItemClickedListener(new OnFlowItemClickedListener() {
            @Override
            public void onItemClicked(int position, View view) {
                if (position == mUsersAdapter.getCount() - 1) {
                    // 删除
                    if (GroupDeleteChoosedContacts.get().getContacts().isEmpty()) {
                        ArrayList<ContactData> datas = new ArrayList<>();
                        for (GroupContacts.Data tmp : mGroupContacts.result) {
                            if (tmp.loginName.equals(AppDatas.Auth().getUserLoginName())) {
                                self = ContactData.from(tmp);
                            } else {
                                datas.add(ContactData.from(tmp));
                            }
                        }
                        GroupDeleteChoosedContacts.get().setContacts(datas);
                    }

                    Intent intent = new Intent(getSelf(), ContactsListOperationActivity.class);
                    startActivityForResult(intent, 1000);

                } else if (position == mUsersAdapter.getCount() - 2) {
                    // 添加
                    if (ChoosedContacts.get().getContacts(false).isEmpty()) {
                        ArrayList<ContactData> datas = new ArrayList<>();
                        for (GroupContacts.Data tmp : mGroupContacts.result) {
                            datas.add(ContactData.from(tmp));
                        }
                        ChoosedContacts.get().setContacts(datas);
                        ChoosedContacts.get().setOnMeetUsers(datas);
                    }

                    Intent intent = new Intent(getSelf(), ContactsChoiceByAllFriendActivity.class);
                    intent.putExtra("titleName", "创建群组");
                    intent.putExtra("isSelectUser", true);
                    startActivityForResult(intent, 1000);
                }
            }
        });

        if (createUserId.equals(AppDatas.Auth().getUserID())) {
            tv_quit_group.setText("删除群聊");
        } else {
            tv_quit_group.setText("退出群聊");
        }

        tv_group_name.setText(groupName);
        requestDetail();
    }

    void requestDetail() {
        ModelApis.Contacts().requestGroupContacts(groupId, new ModelCallback<GroupContacts>() {
            @Override
            public void onSuccess(GroupContacts groupContacts) {
                mGroupContacts = groupContacts;
                tv_all.setText("全部成员 (" + groupContacts.result.size() + "人)");
                refreshMembers();
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                showToast(ErrorMsg.getMsg(ErrorMsg.get_err_code));
            }
        });
    }

    void onDelOrQuitAction() {
        if (createUserId.equals(AppDatas.Auth().getUserID())) {
            ModelApis.Contacts().delGroup(groupId, new ModelCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    showToast("删除成功");
                    new RxUtils().doDelay(500, new RxUtils.IMainDelay() {
                        @Override
                        public void onMainDelay() {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }, "delete");
                }

                @Override
                public void onFailure(HTTPResponse httpResponse) {
                    super.onFailure(httpResponse);
                    showToast(ErrorMsg.getMsg(ErrorMsg.delete_err_code));
                }
            });
        } else {
            ModelApis.Contacts().quitGroup(groupId, new ModelCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    showToast("退出成功");
                    new RxUtils().doDelay(500, new RxUtils.IMainDelay() {
                        @Override
                        public void onMainDelay() {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }, "exit");
                }

                @Override
                public void onFailure(HTTPResponse httpResponse) {
                    super.onFailure(httpResponse);
                    showToast(ErrorMsg.getMsg(ErrorMsg.get_err_code));
                }
            });
        }
    }

    void updateGroupContacts() {
        GroupDeleteChoosedContacts.get().getContacts().add(0, self);
        ModelApis.Contacts().updateGroupInfo(groupId, GroupDeleteChoosedContacts.get().getContacts(), new ModelCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                mUsersAdapter.views.clear();

                tv_all.setText("全部成员 (" + GroupDeleteChoosedContacts.get().getContacts().size() + "人)");

                for (ContactData tmp : GroupDeleteChoosedContacts.get().getContacts()) {
                    GroupMemberView memberView = new GroupMemberView(getSelf());
                    memberView.tv_user_name.setText(tmp.name);
                    memberView.iv_user_head.setImageResource(R.drawable.default_image_personal);
                    mUsersAdapter.views.add(memberView);

                }

                GroupMemberView mUsersViewAdd = new GroupMemberView(getSelf());
                mUsersViewAdd.tv_user_name.setText("");
                mUsersViewAdd.iv_user_head.setImageResource(R.drawable.ic_add);
                mUsersAdapter.views.add(mUsersViewAdd);

                GroupMemberView mUsersViewDel = new GroupMemberView(getSelf());
                mUsersViewDel.tv_user_name.setText("");
                mUsersViewDel.iv_user_head.setImageResource(R.drawable.ic_delete);
                mUsersAdapter.views.add(mUsersViewDel);

                mUsersAdapter.notifyDatasetChanged();
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                showToast(ErrorMsg.getMsg(ErrorMsg.update_err_code));
            }
        });
    }

    void refreshMembers() {
        mUsersAdapter.views.clear();

        for (GroupContacts.Data tmp : mGroupContacts.result) {

            GroupMemberView memberView = new GroupMemberView(this);
            memberView.tv_user_name.setText(tmp.name);
            memberView.iv_user_head.setImageResource(R.drawable.default_image_personal);
            mUsersAdapter.views.add(memberView);

        }

        GroupMemberView mUsersViewAdd = new GroupMemberView(this);
        mUsersViewAdd.tv_user_name.setText("");
        mUsersViewAdd.iv_user_head.setImageResource(R.drawable.ic_add);
        mUsersAdapter.views.add(mUsersViewAdd);

        GroupMemberView mUsersViewDel = new GroupMemberView(this);
        mUsersViewDel.tv_user_name.setText("");
        mUsersViewDel.iv_user_head.setImageResource(R.drawable.ic_delete);
        mUsersAdapter.views.add(mUsersViewDel);

        mUsersAdapter.notifyDatasetChanged();
    }

    @OnClick({R.id.tv_create_meet, R.id.tv_quit_group})
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_create_meet:
                createMeeting();
                break;
            case R.id.tv_quit_group:
                onDelOrQuitAction();
                break;
        }
    }

    void createMeeting() {

        String strMeetName = edt_meet_name.getText().toString();
        if (TextUtils.isEmpty(strMeetName)) {
            showToast("会议名称不能空");
            return;
        }

        boolean blOpenRecord = cb_record.isChecked();

        ArrayList<CStartMeetingReq.UserInfo> users = new ArrayList<>();
        for (GroupContacts.Data tmp : mGroupContacts.result) {
            CStartMeetingReq.UserInfo user = new CStartMeetingReq.UserInfo();
            user.setDevTypeUser();
            user.strUserDomainCode = tmp.domainCode;
            user.strUserID = tmp.loginName;
            user.strUserName = tmp.name;

            users.add(user);
        }
        for (ContactData tmp : ChoosedContacts.get().getContacts(false)) {
            CStartMeetingReq.UserInfo user = new CStartMeetingReq.UserInfo();

            user.setDevTypeUser();
            user.strUserDomainCode = tmp.domainCode;
            user.strUserID = tmp.loginName;
            user.strUserName = tmp.name;

            users.add(user);
        }

        HYClient.getModule(ApiMeet.class)
                .createMeeting(SdkParamsCenter.Meet.CreateMeet()
                        .setUsers(users)
                        .setOpenRecord(blOpenRecord)
                        .setMeetName(strMeetName), new SdkCallback<CStartMeetingRsp>() {
                    @Override
                    public void onSuccess(CStartMeetingRsp cStartMeetingRsp) {
                        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                            for (CStartMeetingReq.UserInfo temp : users) {
                                if (!temp.equals(HYClient.getSdkOptions().User().getUserId())) {
                                    EncryptUtil.startEncrypt(true, temp.strUserID, temp.strUserDomainCode,
                                            cStartMeetingRsp.nMeetingID + "", cStartMeetingRsp.strMeetingDomainCode, null);
                                }
                            }
                        } else {
                            if(nEncryptIMEnable) {
                                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                finish();
                                return;
                            }
                        }
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast(ErrorMsg.getMsg(ErrorMsg.create_meet_err_code));
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            updateGroupContacts();
        }
    }

    static class MembersAdapter extends FlowBaseAdapter {

        ArrayList<GroupMemberView> views;

        private MembersAdapter() {
            views = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public View getView(int position) {
            return views.get(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChoosedContacts.get().clearMeetUsers();
    }
}
