package huaiye.com.vim.ui.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.common.helper.ChatContactsGroupUserListHelper;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.ChatGroupMsgBean;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.dao.msgs.VimMessageBean;
import huaiye.com.vim.dao.msgs.VimMessageListBean;
import huaiye.com.vim.dao.msgs.VimMessageListMessages;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.bean.ContactsBean;
import huaiye.com.vim.models.contacts.bean.ContactsGroupUserListBean;
import huaiye.com.vim.models.contacts.bean.CustomResponse;
import huaiye.com.vim.ui.chat.ModifyGroupAnnouncementActivity;
import huaiye.com.vim.ui.chat.ModifyGroupNameActivity;
import huaiye.com.vim.ui.chat.dialog.CustomTipDialog;
import huaiye.com.vim.ui.contacts.sharedata.VimChoosedContacts;
import huaiye.com.vim.ui.meet.adapter.UserDetailUserListAdapter;
import huaiye.com.vim.ui.meet.presenter.UserDetailPresenterHelper;
import huaiye.com.vim.ui.meet.presenter.UserDetailPresenterImpl;
import huaiye.com.vim.ui.setting.ModifyHeadPicActivity;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vim.common.AppUtils.REQUEST_CODE_MODIFY_PIC;
import static huaiye.com.vim.ui.meet.adapter.ChatContentAdapter.CHAT_CONTENT_CUSTOM_NOTICE_ITEM;

/**
 * author: zhangzhen
 * date: 2019/7/23
 * version: 0
 * desc: UserDetailActivity
 */
@BindLayout(R.layout.activity_user_detail)
public class UserDetailActivity extends AppBaseActivity implements UserDetailUserListAdapter.OnItemClickListener, UserDetailPresenterHelper.View {

    @BindView(R.id.user_detail_recyclerview)
    RecyclerView userDetailRecyclerview;
    @BindView(R.id.user_detail_message_untouch)
    TextView userDetailMessageUntouch;
    @BindView(R.id.user_detail_message_untouch_checkbox)
    CheckBox userDetailMessageUntouchCheckbox;
    @BindView(R.id.user_detail_message_set_top)
    TextView userDetailMessageSetTop;
    @BindView(R.id.user_detail_message_set_top_check_box)
    CheckBox userDetailMessageSetTopCheckBox;
    @BindView(R.id.user_detail_buttom)
    LinearLayout userDetailButtom;
    @BindView(R.id.user_detail_message_clear)
    TextView userDetailMessageClear;
    @BindView(R.id.user_detail_group_user_count_rel)
    RelativeLayout user_detail_group_user_count_rel;
    @BindView(R.id.user_detail_group_user_count)
    TextView userDetailGroupUserCount;
    @BindView(R.id.user_detail_group_name_rel)
    LinearLayout user_detail_group_name_rel;
    @BindView(R.id.user_detail_group_name)
    TextView userDetailGroupName;
    @BindView(R.id.user_detail_group_notice)
    TextView user_detail_group_notice;
    @BindView(R.id.user_detail_group_del)
    TextView userDetailGroupDel;
    @BindView(R.id.user_detail_group_notice_rel)
    RelativeLayout user_detail_group_notice_rel;
    @BindView(R.id.user_detail_modify_group_head_pic)
    TextView user_detail_modify_group_head_pic;

    @BindExtra
    ArrayList<User> mUserList;

    @BindExtra
    boolean isGroupChat = false;

    @BindExtra
    String strGroupDomainCode;
    @BindExtra
    String strGroupID;
    @BindExtra
    String strGroupName;

    @BindExtra
    String sessionID;

    @BindExtra
    ContactsGroupUserListBean mContactsGroupUserListBean;


    private boolean isGroupOwner;
    private String strAnnouncement;
    ArrayList<User> mDataList = new ArrayList<>();


    private UserDetailUserListAdapter mUserDetailUserListAdapter;
    private UserDetailPresenterHelper.Presenter mPresenter;

    private CustomTipDialog mCustomTipClearDataDialog;
    private CustomTipDialog mCustomTipdelGroupDataDialog;

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.VISIBLE);
        getNavigate().setTitlText(AppUtils.getString(R.string.chat_info_title))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        mPresenter = new UserDetailPresenterImpl(this, this);
        initdata();
        initView();
    }

    private void initView() {
        mUserDetailUserListAdapter = new UserDetailUserListAdapter(this);
        mUserDetailUserListAdapter.setOnItemClickListener(this);
        userDetailRecyclerview.setLayoutManager(new GridLayoutManager(this, 5));
        userDetailRecyclerview.setAdapter(mUserDetailUserListAdapter);
        mUserDetailUserListAdapter.setData(mDataList);
        if (isGroupChat) {
            user_detail_group_name_rel.setVisibility(View.VISIBLE);
            user_detail_group_notice_rel.setVisibility(View.VISIBLE);
            userDetailGroupName.setText(strGroupName + "");
            user_detail_group_user_count_rel.setVisibility(View.VISIBLE);
            userDetailGroupDel.setVisibility(View.VISIBLE);

        } else {
            user_detail_group_notice_rel.setVisibility(View.GONE);
            user_detail_modify_group_head_pic.setVisibility(View.GONE);
            user_detail_group_name_rel.setVisibility(View.GONE);
            user_detail_group_user_count_rel.setVisibility(View.GONE);
            userDetailGroupDel.setVisibility(View.GONE);
        }

        initDialog();
        initCheckState();

    }

    private void initDialog() {
        mCustomTipClearDataDialog = new CustomTipDialog(this);
        mCustomTipClearDataDialog.setOnFunctionClickedListener(new CustomTipDialog.IFunctionClickedListener() {

            @Override
            public void onClickedLeftFunction() {

            }

            @Override
            public void onClickedRightFunction() {
                if (isGroupChat) {
                    AppDatas.MsgDB()
                            .chatGroupMsgDao()
                            .deleteBySessionID(sessionID);
                } else {
                    AppDatas.MsgDB()
                            .chatSingleMsgDao()
                            .deleteBySessionID(sessionID);
                }
                VimMessageListMessages.get().clearMessage(sessionID);
                showToast(AppUtils.getString(R.string.message_clear_success));
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_CLEAR_MESSAGE_SUCCESS));
            }
        });

        mCustomTipdelGroupDataDialog = new CustomTipDialog(this, AppUtils.getString(R.string.user_detail_group_del_content));
        mCustomTipdelGroupDataDialog.setOnFunctionClickedListener(new CustomTipDialog.IFunctionClickedListener() {

            @Override
            public void onClickedLeftFunction() {

            }

            @Override
            public void onClickedRightFunction() {
                if (isGroupChat && null != mContactsGroupUserListBean) {
                    if (mContactsGroupUserListBean.strCreaterID.equals(AppDatas.Auth().getUserID())) {
                        delGroupChat();
                    } else {
                        leaveGroupChat();
                    }
                }
            }
        });
    }

    private void initCheckState() {
        VimMessageListBean nVimMessageListBean = VimMessageListMessages.get().getMessages(sessionID);
        boolean msgTopSet = false;
        boolean noDisturbSet = false;
        if (null != nVimMessageListBean) {
            msgTopSet = nVimMessageListBean.nMsgTop == 1 ? true : false;
            noDisturbSet = nVimMessageListBean.nNoDisturb == 1 ? true : false;


        } else {
            msgTopSet = SP.getInteger(sessionID + AppUtils.SP_SETTING_MSG_TOP, 0) == 1 ? true : false;
            noDisturbSet = SP.getInteger(sessionID + AppUtils.SP_SETTING_NODISTURB, 0) == 1 ? true : false;

        }

        if (msgTopSet) {
            userDetailMessageSetTopCheckBox.setChecked(true);
        } else {
            userDetailMessageSetTopCheckBox.setChecked(false);
        }

        if (noDisturbSet) {
            userDetailMessageUntouchCheckbox.setChecked(true);
        } else {
            userDetailMessageUntouchCheckbox.setChecked(false);
        }
    }

    private void initdata() {
        mDataList.clear();
        if (!isGroupChat) {
            if (mUserList == null) {
                mUserList = new ArrayList<>();
            }
            mDataList.addAll(mUserList);
        } else {
            if (null == mUserList) {
                mUserList = new ArrayList<>();
            }
            if (null != mContactsGroupUserListBean &&
                    null != mContactsGroupUserListBean.lstGroupUser &&
                    mContactsGroupUserListBean.lstGroupUser.size() > 0) {
                mUserList.clear();
                for (ContactsGroupUserListBean.LstGroupUser mLstGroupUser : mContactsGroupUserListBean.lstGroupUser) {
                    User nUser = new User();
                    nUser.strUserName = mLstGroupUser.strUserName;
                    nUser.strDomainCode = mLstGroupUser.strUserDomainCode;
                    nUser.strUserID = mLstGroupUser.strUserID;
                    nUser.strHeadUrl = mLstGroupUser.strHeadUrl;
                    mDataList.add(nUser);
                    if (!(mLstGroupUser.strUserID.equals(AppDatas.Auth().getUserID()))) {
                        mUserList.add(nUser);
                    }
                }
            }
        }

        userDetailGroupUserCount.setText(mDataList.size() + AppUtils.getString(R.string.user_detail_perple));

        if (isGroupChat) {
            if (null != mContactsGroupUserListBean) {
                if (mContactsGroupUserListBean.strCreaterID.equals(AppDatas.Auth().getUserID())) {
                    User add = new User();
                    add.strUserID = UserDetailUserListAdapter.TYPE_ADD;
                    mDataList.add(add);
                    User del = new User();
                    del.strUserID = UserDetailUserListAdapter.TYPE_DEL;
                    mDataList.add(del);
                    isGroupOwner = true;
                } else {
                    isGroupOwner = false;
                }
                strAnnouncement = mContactsGroupUserListBean.strAnnouncement;
                strGroupName = mContactsGroupUserListBean.strGroupName;
                updateGroupNameAnnouncement(strAnnouncement);
                updateGroupName(strGroupName);
            }

        } else {
            if (AppAuth.get().getCreateGroupChatRole()) {//超级管理员才可以拉人建群
                User add = new User();
                add.strUserID = UserDetailUserListAdapter.TYPE_ADD;
                mDataList.add(add);
            }

        }

        userDetailMessageUntouchCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EventBus.getDefault().post(new VimMessageBean());
                int nMsgTop = userDetailMessageSetTopCheckBox.isChecked() ? 1 : 0;
                int nNoDisturb = isChecked ? 1 : 0;
                VimMessageListMessages.get().updateNoDisturb(sessionID, nNoDisturb);
                SP.putInt(sessionID + AppUtils.SP_SETTING_NODISTURB, nNoDisturb);

                if (isGroupChat) {
                    ModelApis.Contacts().requestSetGroupChatMsgMode(strGroupDomainCode, strGroupID, nMsgTop, nNoDisturb, new ModelCallback<CustomResponse>() {
                        @Override
                        public void onSuccess(final CustomResponse contactsBean) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mZeusLoadView != null && mZeusLoadView.isShowing())
                                        mZeusLoadView.dismiss();
                                    showToast(AppUtils.getString(R.string.request_load_over));
                                }
                            });
                        }

                        @Override
                        public void onFailure(HTTPResponse httpResponse) {
                            super.onFailure(httpResponse);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mZeusLoadView != null && mZeusLoadView.isShowing())
                                        mZeusLoadView.dismiss();
                                    showToast(AppUtils.getString(R.string.request_load_failed));
                                }
                            });
                        }
                    });
                } else {
                }

            }
        });

        userDetailMessageSetTopCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EventBus.getDefault().post(new VimMessageBean());
                int nMsgTop = isChecked ? 1 : 0;
                int nNoDisturb = userDetailMessageUntouchCheckbox.isChecked() ? 1 : 0;
                if (nMsgTop == SP.getInteger(sessionID + AppUtils.SP_SETTING_MSG_TOP, 0)) {
                    return;
                }
                VimMessageListMessages.get().updateMsgTop(sessionID, nMsgTop);
                SP.putLong(sessionID + AppUtils.SP_SETTING_MSG_TOP_TIME, System.currentTimeMillis());
                SP.putInt(sessionID + AppUtils.SP_SETTING_MSG_TOP, nMsgTop);

                if (isGroupChat) {
                    mZeusLoadView.loadingText(AppUtils.getString(R.string.request_loading)).setLoading();
                    ModelApis.Contacts().requestSetGroupChatMsgMode(strGroupDomainCode, strGroupID, nMsgTop, nNoDisturb, new ModelCallback<CustomResponse>() {
                        @Override
                        public void onSuccess(final CustomResponse contactsBean) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mZeusLoadView != null && mZeusLoadView.isShowing())
                                        mZeusLoadView.dismiss();
                                    showToast(AppUtils.getString(R.string.request_load_over));
                                }
                            });
                        }

                        @Override
                        public void onFailure(HTTPResponse httpResponse) {
                            super.onFailure(httpResponse);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mZeusLoadView != null && mZeusLoadView.isShowing())
                                        mZeusLoadView.dismiss();
                                    showToast(AppUtils.getString(R.string.request_load_failed));
                                }
                            });
                        }
                    });
                } else {

                }

            }
        });

        if (null != mUserDetailUserListAdapter) {
            mUserDetailUserListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onItemClick(User item) {
        if (UserDetailUserListAdapter.TYPE_ADD.equals(item.strUserID)) {
            Intent intent = new Intent(getSelf(), ContactsAddOrDelActivity.class);
            intent.putExtra("titleName", AppUtils.getResourceString(R.string.user_detail_add_user_title));
            intent.putExtra("isSelectUser", true);
            if (isGroupChat) {
                intent.putExtra("isCreateGroup", false);
                intent.putExtra("isAddMore", true);
            } else {
                intent.putExtra("isCreateGroup", true);
                intent.putExtra("isAddMore", false);
            }
            intent.putExtra("strGroupDomainCode", strGroupDomainCode);
            intent.putExtra("strGroupID", strGroupID);
            intent.putExtra("strGroupName", strGroupName);

            intent.putExtra("mUserList", mUserList);
            startActivityForResult(intent, 1000);
        } else if (UserDetailUserListAdapter.TYPE_DEL.equals(item.strUserID)) {
            Intent intent = new Intent(getSelf(), ContactsAddOrDelActivity.class);
            intent.putExtra("titleName", AppUtils.getResourceString(R.string.user_detail_del_user_title));
            intent.putExtra("strGroupDomainCode", strGroupDomainCode);
            intent.putExtra("strGroupID", strGroupID);
            intent.putExtra("strGroupName", strGroupName);
            intent.putExtra("isSelectUser", true);
            intent.putExtra("isCreateGroup", false);
            intent.putExtra("isAddMore", false);
            intent.putExtra("mUserList", getCanKickoutUser(mUserList));
            startActivityForResult(intent, 1000);
        } else {
            intent2ContactDetail(item);
        }

    }

    @OnClick(R.id.user_detail_group_user_count_rel)
    void go2GroupUserList() {
        Intent intent = new Intent(getSelf(), ContactsAddOrDelActivity.class);
        intent.putExtra("titleName", AppUtils.getResourceString(R.string.user_detail_del_user_title));
        intent.putExtra("isSelectUser", false);
        intent.putExtra("isCreateGroup", false);
        intent.putExtra("isAddMore", false);
        intent.putExtra("strGroupDomainCode", strGroupDomainCode);
        intent.putExtra("strGroupID", strGroupID);
        intent.putExtra("strGroupName", strGroupName);
        intent.putExtra("mUserList", getAllUserList());
        startActivityForResult(intent, 1000);
    }

    @OnClick(R.id.user_detail_group_name_rel)
    void modifyGroupName() {
        if (isGroupOwner) {
            Intent intent = new Intent(getSelf(), ModifyGroupNameActivity.class);
            intent.putExtra("strGroupDomainCode", strGroupDomainCode);
            intent.putExtra("strGroupID", strGroupID);
            intent.putExtra("groupName", strGroupName);
            startActivity(intent);
        } else {
            showToast(getString(R.string.group_owner_can_modify_groupname));
        }

    }

    @OnClick(R.id.user_detail_group_notice_rel)
    void modifyGroupnotice() {
        Intent intent = new Intent(getSelf(), ModifyGroupAnnouncementActivity.class);
        intent.putExtra("strGroupDomainCode", strGroupDomainCode);
        intent.putExtra("strGroupID", strGroupID);
        intent.putExtra("isGroupOwner", isGroupOwner);
        intent.putExtra("strAnnouncement", strAnnouncement);
        startActivity(intent);
    }

    private ArrayList<User> getAllUserList() {
        ArrayList<User> nserList = new ArrayList<>();
        boolean containSelf = false;
        if (null != mUserList && mUserList.size() > 0) {
            for (User user : mUserList) {
                nserList.add(user);
                if (user.strUserID.equals(AppDatas.Auth().getUserID())) {
                    containSelf = true;
                }
            }
        }
        if (!containSelf) {
            nserList.add(VimChoosedContacts.get().getSelf());
        }
        return nserList;
    }

    private ArrayList<User> getCanKickoutUser(ArrayList<User> mUserList) {
        ArrayList<User> userList = new ArrayList<>();
        if (null != mUserList && mUserList.size() > 0) {
            for (User item : mUserList) {
                if (!item.strUserID.equals(AppDatas.Auth().getUserID())) {
                    userList.add(item);
                }
            }
        }
        return userList;
    }

    private void intent2ContactDetail(User item) {
        Intent intent = new Intent(this, ContactDetailNewActivity.class);
        intent.putExtra("nUser", item);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        switch (messageEvent.what) {
            case AppUtils.EVENT_CREATE_GROUP_SUCCESS:
            case AppUtils.EVENT_INTENT_CHATSINGLEACTIVITY:
                finish();
                break;
            case AppUtils.EVENT_REFRESH_GROUP_DETAIL:
            case AppUtils.EVENT_ADD_PEOPLE_TO_GROUP_SUCCESS://add
            case AppUtils.EVENT_KICKOUT_PEOPLE_TO_SUCCESS://kickout
                queryGroupChatInfo();
                break;
            case AppUtils.EVENT_MODIFY_GROUPNAME_SUCCESS://群名
                VimMessageListMessages.get().updateGroupName(sessionID, messageEvent.msgContent);
                EventBus.getDefault().post(new VimMessageBean());//通知消息列表页面刷新数据
                updateGroupName(messageEvent.msgContent);
                queryGroupChatInfo();
                break;
            case AppUtils.EVENT_MODIFY_GROUP_ANNOUNCEMENT_SUCCESS://群公告
                queryGroupChatInfo();
                updateGroupNameAnnouncement(messageEvent.msgContent);
                break;
            case AppUtils.EVENT_MESSAGE_MODIFY_GROUP://群名称和公告可能会有变化
            case AppUtils.EVENT_UPDATE_GROUP_DETAIL://群成员可能会有变化
                ContactsGroupUserListBean nContactsGroupUserListBean = ChatContactsGroupUserListHelper.getInstance().getContactsGroupDetail(mContactsGroupUserListBean.strGroupID);
                mContactsGroupUserListBean = nContactsGroupUserListBean;
                initdata();
                break;
            default:
                break;
        }
        /*if (messageEvent.what == AppUtils.EVENT_CREATE_GROUP_SUCCESS || messageEvent.what == AppUtils.EVENT_INTENT_CHATSINGLEACTIVITY) {//关闭页面
            finish();
        } else if (messageEvent.what == AppUtils.EVENT_ADD_PEOPLE_TO_GROUP_SUCCESS || messageEvent.what == AppUtils.EVENT_KICKOUT_PEOPLE_TO_SUCCESS) {
            queryGroupChatInfo();
        } else if (messageEvent.what == AppUtils.EVENT_MODIFY_GROUPNAME_SUCCESS) {
            VimMessageListMessages.get().updateGroupName(sessionID, messageEvent.msgContent);
            EventBus.getDefault().post(new VimMessageBean());//通知消息列表页面刷新数据
            updateGroupName(messageEvent.msgContent);
            queryGroupChatInfo();
        } else if (messageEvent.what == AppUtils.EVENT_MODIFY_GROUP_ANNOUNCEMENT_SUCCESS) {
            queryGroupChatInfo();
            updateGroupNameAnnouncement(messageEvent.msgContent);
        }*/
    }

    private void updateGroupNameAnnouncement(String msgContent) {
        strAnnouncement = msgContent;
        if (!TextUtils.isEmpty(msgContent)) {
            user_detail_group_notice.setVisibility(View.VISIBLE);
            user_detail_group_notice.setText(msgContent);
        } else {
            user_detail_group_notice.setVisibility(View.GONE);
        }
    }

    private void updateGroupName(String groupName) {
        strGroupName = groupName;
        userDetailGroupName.setText(groupName);
    }

    private void queryGroupChatInfo() {
        ModelApis.Contacts().requestqueryGroupChatInfo(strGroupDomainCode, strGroupID, new ModelCallback<ContactsGroupUserListBean>() {
            @Override
            public void onSuccess(final ContactsGroupUserListBean contactsBean) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mContactsGroupUserListBean = contactsBean;
                        getGroupUserHead(mContactsGroupUserListBean);

                    }
                });

            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("onFailure");
                    }
                });
            }
        });
    }

    private void getGroupUserHead(ContactsGroupUserListBean contactsGroupUserListBean) {
        if (null != contactsGroupUserListBean && null != contactsGroupUserListBean.lstGroupUser && contactsGroupUserListBean.lstGroupUser.size() > 0) {
            ModelApis.Contacts().requestGetUserHead(contactsGroupUserListBean.lstGroupUser, new ModelCallback<ContactsBean>() {
                @Override
                public void onSuccess(ContactsBean contactsBean) {

                    if (null != contactsBean && null != contactsBean.userList && contactsBean.userList.size() > 0) {
                        new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
                            @Override
                            public Object doOnThread() {
                                for (User user : contactsBean.userList) {
                                    user.strDomainCode = user.strUserDomainCode;
                                }
                                AppDatas.MsgDB().getFriendListDao().insertAll(contactsBean.userList);

                                return "";
                            }

                            @Override
                            public void doOnMain(Object data) {
                                refreshView(contactsGroupUserListBean);
                            }
                        });
                    }
                }
            });
        }

    }

    private void refreshView(ContactsGroupUserListBean contactsGroupUserListBean) {
        ChatContactsGroupUserListHelper.getInstance().cacheContactsGroupDetail(contactsGroupUserListBean.strGroupID, contactsGroupUserListBean);
        initdata();
        mUserDetailUserListAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.user_detail_group_del)
    void delGroup() {
        if (null != mCustomTipdelGroupDataDialog && !mCustomTipdelGroupDataDialog.isShowing()) {
            mCustomTipdelGroupDataDialog.show();
        }
    }

    private void leaveGroupChat() {
        ModelApis.Contacts().requestUserLeaveGroupChat(strGroupDomainCode, strGroupID, new ModelCallback<CustomResponse>() {
            @Override
            public void onSuccess(final CustomResponse contactsBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        VimMessageListMessages.get().del(strGroupDomainCode + strGroupID);
                        addGroupNotice("已退出群组", sessionID, strGroupID, strGroupName);
                        EventBus.getDefault().post(new VimMessageBean());
                        EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_LEAVE_GROUP_SUCCESS));
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("加群失败");
                    }
                });
            }
        });
    }

    private void delGroupChat() {
        ModelApis.Contacts().requestDelGroupChat(strGroupDomainCode, strGroupID, new ModelCallback<CustomResponse>() {
            @Override
            public void onSuccess(final CustomResponse contactsBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addGroupNotice("群组已解散", sessionID, strGroupID, strGroupName);
                        EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_DEL_GROUP_SUCCESS));
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("加群失败");
                    }
                });
            }
        });
    }

    private void addGroupNotice(String notice, String sessionID, String groupID, String sessionName) {
        ChatGroupMsgBean bean = new ChatGroupMsgBean();
        bean.type = CHAT_CONTENT_CUSTOM_NOTICE_ITEM;
        bean.sessionID = sessionID;
        bean.groupID = groupID;
        bean.sessionName = sessionName;
        bean.msgTxt = notice;
        bean.time = System.currentTimeMillis();
        AppDatas.MsgDB()
                .chatGroupMsgDao().insert(bean);
    }

    @OnClick(R.id.user_detail_message_clear)
    void clearMessage() {
        if (isGroupChat) {
            mCustomTipClearDataDialog.setContent(AppUtils.getString(R.string.user_detail_clear_data_group_content));
        } else {
            if (null != mUserList && mUserList.size() > 0) {
                mCustomTipClearDataDialog.setContent(getString(R.string.user_detail_clear_data_single_content, mUserList.get(0).strUserName));
            }

        }
        mCustomTipClearDataDialog.show();
    }

    @OnClick(R.id.user_detail_modify_group_head_pic)
    void modifyGroupHead() {
        if (!isGroupOwner) {
            showToast("你不是群主无法操作");
            return;
        }
        Intent intent = new Intent(this, ModifyHeadPicActivity.class);
        if (null != mContactsGroupUserListBean) {
            intent.putExtra("headPic", mContactsGroupUserListBean.strHeadUrl);
        }
        intent.putExtra("isGroup", true);
        intent.putExtra("isGroupOwner", isGroupOwner);
        intent.putExtra("strGroupDomainCode", strGroupDomainCode);
        intent.putExtra("strGroupID", strGroupID);
        startActivityForResult(intent, REQUEST_CODE_MODIFY_PIC);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mCustomTipClearDataDialog && mCustomTipClearDataDialog.isShowing()) {
            mCustomTipClearDataDialog.dismiss();
            mCustomTipClearDataDialog = null;
        }

        if (null != mCustomTipdelGroupDataDialog && mCustomTipdelGroupDataDialog.isShowing()) {
            mCustomTipdelGroupDataDialog.dismiss();
            mCustomTipdelGroupDataDialog = null;
        }


        EventBus.getDefault().unregister(this);
    }

}
