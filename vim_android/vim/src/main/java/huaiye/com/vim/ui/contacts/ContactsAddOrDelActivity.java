package huaiye.com.vim.ui.contacts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.promeg.pinyinhelper.Pinyin;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.views.FastRetrievalBar;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.bean.ContactsBean;
import huaiye.com.vim.models.contacts.bean.CreateGroupContactData;
import huaiye.com.vim.models.contacts.bean.CustomContacts;
import huaiye.com.vim.models.contacts.bean.CustomResponse;
import huaiye.com.vim.ui.contacts.sharedata.VimChoosedContacts;
import huaiye.com.vim.ui.home.adapter.VimContactsItemAdapter;
import huaiye.com.vim.ui.meet.ChatGroupActivityNew;
import ttyy.com.jinnetwork.core.work.HTTPResponse;
import ttyy.com.recyclerexts.base.EXTViewHolder;
import ttyy.com.recyclerexts.tags.TagsAdapter;

import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * author: admin
 * date: 2018/01/15
 * version: 0
 * mail: secret
 * desc: ContactsChoiceActivity
 */
@BindLayout(R.layout.activity_contacts_root)
public class ContactsAddOrDelActivity extends AppBaseActivity {
    public static final String SELECTED_CONTACTS = "selectedContacts";
    public static final String RESULT_CONTACTS = "resultContacts";
    @BindView(R.id.refresh_view)
    SwipeRefreshLayout refresh_view;
    @BindView(R.id.rct_view)
    RecyclerView rct_view;
    @BindView(R.id.contacts_retrieval_bar)
    FastRetrievalBar contacts_retrieval_bar;
    @BindView(R.id.tv_choose_confirm)
    TextView tv_choose_confirm;
    @BindView(R.id.tv_letter_high_fidelity_item)
    TextView tv_letter_high_fidelity_item;

    @BindExtra
    ArrayList<User> mUserList;
    @BindExtra
    String titleName;
    @BindExtra
    boolean isSelectUser;
    @BindExtra
    boolean needAddSelf;
    @BindExtra
    boolean isCreateGroup;
    @BindExtra
    boolean isCreateVideoPish;
    @BindExtra
    boolean isAddMore;
    @BindExtra
    boolean isJinJiMore;
    @BindExtra
    String strGroupDomainCode;
    @BindExtra
    String strGroupID;
    @BindExtra
    String strGroupName;
    @BindExtra
    int max;
    @BindView(R.id.ll_choosed_persons)
    LinearLayout llChoosedPersons;

    TagsAdapter<CustomContacts.LetterStructure> adapter;


    private ArrayList<CustomContacts.LetterStructure> mCustomContacts;

    private ArrayList<User> mAllContacts = new ArrayList<>();
    private ArrayList<User> mOnlineContacts = new ArrayList<>();
    private int mPage = 1;
    private int mTotalSize = 0;
    private boolean mIsShowAll = false;

    long currentTime;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initActionBar() {
        if (max == -1) {
            max = 1000;
        }

        if (TextUtils.isEmpty(titleName)) {
            titleName = "联系人";
        }

        if (isSelectUser) {
            getNavigate().setTitlText(titleName)
                    .setLeftClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    })
                    .setRightClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (System.currentTimeMillis() - currentTime < 1500) {
                                return;
                            }
                            currentTime = System.currentTimeMillis();
                            if (isCreateVideoPish) {
                                if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                                    if (VimChoosedContacts.get().getContacts().size() != 2) {
                                        showToast("加密分享只能为一人");
                                        currentTime = 0;
                                        return;
                                    }
                                }

                                MessageEvent nMessageEvent = new MessageEvent(AppUtils.EVENT_RPUSH_VIDEO);
                                nMessageEvent.obj1 = getSendUsers(VimChoosedContacts.get().getContacts());
                                EventBus.getDefault().post(nMessageEvent);
                                finish();
                            } else if (isCreateGroup) {//单聊拉人建群
                                createGroupChat();
                            } else if (isAddMore) {//多聊增加人员
                                addPeople2Group();
                            } else if (isJinJiMore) {//紧急联系人
                                addJinJiLianXiRen();
                            } else {//踢人
                                kickoutGroupUser();
                            }
                        }
                    });

            getNavigate().getRightTextView().setPadding(AppUtils.dp2px(this, 8f), AppUtils.dp2px(this, 4f), AppUtils.dp2px(this, 8f), AppUtils.dp2px(this, 4f));
            if (isJinJiMore || isAddMore || isCreateGroup || isCreateVideoPish) {
                getNavigate().getRightTextView().setBackgroundResource(R.drawable.shape_choosed_confirm);
                getNavigate().setRightText(AppUtils.getString(R.string.makesure));
            } else {
                getNavigate().getRightTextView().setBackgroundResource(R.drawable.shape_choosed_delete);
                getNavigate().setRightText(AppUtils.getString(R.string.user_detail_del_perple));

            }
        } else {
            getNavigate().setTitlText(titleName)
                    .setLeftClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
        }

    }

    private Object getSendUsers(ArrayList<User> contacts) {
        ArrayList<SendUserBean> nSendUserBeans = new ArrayList<>();
        for (User user : contacts) {
            if (!user.strUserID.equals(AppDatas.Auth().getUserID())) {
                SendUserBean sendUserBean = new SendUserBean(user.strUserID, user.strDomainCode, user.strUserName);
                nSendUserBeans.add(sendUserBean);
            }

        }
        return nSendUserBeans;
    }


    /**
     * 踢人
     */
    private void kickoutGroupUser() {
        ModelApis.Contacts().requestKickoutGroupUser(strGroupDomainCode, strGroupID, getKickoutPeple(), new ModelCallback<CustomResponse>() {
            @Override
            public void onSuccess(final CustomResponse contactsBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        kickoutGroupUserSuccess();
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

    private void kickoutGroupUserSuccess() {
        StringBuilder str = new StringBuilder();
        for (User temp : getKickoutPeple()) {
            str.append(temp.strUserName + ",");
        }
        MessageEvent event = new MessageEvent(AppUtils.EVENT_KICKOUT_PEOPLE_TO_SUCCESS);
        event.msgContent = str.toString().substring(0, str.length() - 1);
        EventBus.getDefault().post(event);
        finish();
    }

    private void createGroupChat() {

        ModelApis.Contacts().requestCreateGroupChat(getGroupName(), VimChoosedContacts.get().getContacts(), new ModelCallback<CreateGroupContactData>() {
            @Override
            public void onSuccess(final CreateGroupContactData contactsBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        go2ChatGroup(contactsBean);
                    }
                });
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("群创建失败");
                    }
                });
            }
        });
    }

    private void addJinJiLianXiRen() {
        ArrayList<User> mContacts = VimChoosedContacts.get().getContacts();
        Intent intent = new Intent();
        intent.putExtra("users", mContacts);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void addPeople2Group() {
        ModelApis.Contacts().requestInviteUserJoinGroupChat(strGroupDomainCode, strGroupID, strGroupName, getAddPeple(), new ModelCallback<CustomResponse>() {
            @Override
            public void onSuccess(final CustomResponse contactsBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addPeople2GroupSuccess();

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

    private void addPeople2GroupSuccess() {
        StringBuilder str = new StringBuilder();
        for (User temp : getAddPeple()) {
            str.append(temp.strUserName + ",");
        }
        MessageEvent event = new MessageEvent(AppUtils.EVENT_ADD_PEOPLE_TO_GROUP_SUCCESS);
        event.msgContent = str.toString().substring(0, str.length() - 1);
        EventBus.getDefault().post(event);
        showToast("加群成功");
        finish();
    }

    private ArrayList<User> getAddPeple() {
        ArrayList<User> users = new ArrayList<User>();
        if (null != VimChoosedContacts.get().getContacts() && null != VimChoosedContacts.get().getContacts()) {
            for (User item : VimChoosedContacts.get().getContacts()) {
                if (item.nJoinStatus != 2) {
                    users.add(item);
                }
            }
        }
        return users;
    }

    private ArrayList<User> getKickoutPeple() {
        ArrayList<User> users = new ArrayList<User>();
        if (null != VimChoosedContacts.get().getContacts() && null != VimChoosedContacts.get().getContacts()) {
            for (User item : VimChoosedContacts.get().getContacts()) {
                if (!item.strUserID.equals(AppDatas.Auth().getUserID())) {
                    users.add(item);
                }
            }
        }
        return users;
    }


    private String getGroupName() {
        StringBuilder stringGoupName = new StringBuilder();
        if (null != VimChoosedContacts.get().getContacts() && VimChoosedContacts.get().getContacts().size() > 0) {
            for (User item : VimChoosedContacts.get().getContacts()) {
                if (VimChoosedContacts.get().getContacts().indexOf(item) < 6) {
                    stringGoupName.append(item.strUserName + "、");
                }
            }
            if (null != stringGoupName && stringGoupName.indexOf("、") >= 0) {
                stringGoupName.deleteCharAt(stringGoupName.lastIndexOf("、"));
            }
        }
        if (TextUtils.isEmpty(stringGoupName)) {
            stringGoupName.append("群聊");
        }
        return stringGoupName.toString();
    }

    private void go2ChatGroup(CreateGroupContactData contactsBean) {
        EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_CREATE_GROUP_SUCCESS));
        Intent intent = new Intent(this, ChatGroupActivityNew.class);
        intent.putExtra("mContactsBean", contactsBean);
//        intent.putParcelableArrayListExtra("mMessageUsersDate", getMessageUsersDate());
        startActivity(intent);
        finish();
    }

    @Override
    public void doInitDelay() {
        initData();
        llChoosedPersons.setVisibility(View.GONE);
        refresh_view.setColorSchemeColors(ContextCompat.getColor(this, R.color.blue),
                ContextCompat.getColor(this, R.color.colorPrimary));
        refresh_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestOnLine(false);
            }
        });
        rct_view.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int[] location = new int[2];
//                header.getViewGroup().getLocationInWindow(location);

                int[] location2 = new int[2];
                rct_view.getLocationInWindow(location2);

                /*if (header.hasData()) {
                    if (location[1] != location2[1]) {
                        refresh_view.setEnabled(false);
                    } else {
                        refresh_view.setEnabled(true);
                    }
                } else {
                    refresh_view.setEnabled(true);
                }*/
            }
        });
        refresh_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isJinJiMore || isAddMore || isCreateGroup || isCreateVideoPish) {
                    requestDatas();
                } else {
                    refresh_view.setRefreshing(false);
                }
            }
        });
        if (!isJinJiMore && !isAddMore && !isCreateGroup && !isCreateVideoPish) {
            refresh_view.setEnabled(false);
        }

        rct_view.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TagsAdapter<CustomContacts.LetterStructure>(R.layout.letter_item_layout) {
            @Override
            public EXTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if (viewType == HEADER
                        || viewType == FOOTER) {

                    return super.onCreateViewHolder(parent, viewType);
                }
                final EXTViewHolder holder = super.onCreateViewHolder(parent, viewType);
                return holder;
            }

            @Override
            public void onBindTagViewHolder(EXTViewHolder extViewHolder, int i, CustomContacts.LetterStructure data) {
                if (i < getHeaderViewsCount()) {
                    return;
                }
                extViewHolder.setText(R.id.letter_item_txt, String.valueOf(data.letter));
                VimContactsItemAdapter itemAdapter = new VimContactsItemAdapter(
                        ContactsAddOrDelActivity.this,
                        data.users, mCustomContacts.get(i - getHeaderViewsCount()).letter,
                        isSelectUser,
                        VimChoosedContacts.get().getContacts());

                itemAdapter.setOnItemClickLinstener(new VimContactsItemAdapter.OnItemClickLinstener() {
                    @Override
                    public void onClick(int position, User user) {
                        if (isJinJiMore || isAddMore || isCreateGroup || isCreateVideoPish) {
                            if (user.nJoinStatus != 2) {
                                handleChoice(user);
                            }
                        } else {
                            handleChoice(user);
                        }

                    }
                });
                RecyclerView recyclerView = extViewHolder.findViewById(R.id.letter_item_recycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(ContactsAddOrDelActivity.this, LinearLayoutManager.VERTICAL, false));
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(itemAdapter);
            }
        };

        contacts_retrieval_bar.setOnTouchingLetterChangedListener(new FastRetrievalBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = -1;
                if (mCustomContacts == null || mCustomContacts.size() <= 0) {
                    return;
                }
                for (int i = 0; i < mCustomContacts.size(); i++) {
                    if (s.equals(String.valueOf(mCustomContacts.get(i).letter))) {
                        position = i;
                    }
                }
                if (position == -1) {
                    return;
                }
                if (position == 0) {
                    rct_view.scrollToPosition(0);
                } else {
                    rct_view.scrollToPosition(position + adapter.getHeaderViewsCount());
                }
            }

            @Override
            public void setSidePressed() {

            }

            @Override
            public void setSideUnPressed() {

            }
        });

        contacts_retrieval_bar.setTextView(tv_letter_high_fidelity_item);
        rct_view.setAdapter(adapter);
        if (isJinJiMore || isAddMore || isCreateGroup || isCreateVideoPish) {
            requestDatas();
        } else {
            new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
                @Override
                public Object doOnThread() {
                    if (null != mUserList && mUserList.size() > 0) {
                        for (User user : mUserList) {
                            if (TextUtils.isEmpty(user.strHeadUrl)) {
                                user.strHeadUrl = AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(user.strUserID, user.strDomainCode);
                            }
                        }
                        mAllContacts.clear();
                        mAllContacts.addAll(mUserList);
                    }

                    return "";
                }

                @Override
                public void doOnMain(Object data) {
                    updateContacts();

                }
            });

        }
    }

    private void initData() {
        VimChoosedContacts.get().getContacts().clear();
        if (isJinJiMore || isAddMore || isCreateGroup || isCreateVideoPish) {
            if (null != mUserList) {
                for (int i = 0; i < mUserList.size(); i++) {
                    mUserList.get(i).nJoinStatus = 2;
                }
                VimChoosedContacts.get().getContacts().addAll(mUserList);
            }
        } else {
            if (null != mUserList) {
                for (int i = 0; i < mUserList.size(); i++) {
                    mUserList.get(i).nJoinStatus = 0;
                }

            }
        }


    }

    /**
     * 获取数据
     */
    void requestDatas() {
        mPage = -1;
        ModelApis.Contacts().requestBuddyContacts(mPage, 0, 0, new ModelCallback<ContactsBean>() {
            @Override
            public void onSuccess(final ContactsBean contactsBean) {
                new RxUtils<ArrayList<User>>()
                        .doOnThreadObMain(new RxUtils.IThreadAndMainDeal<ArrayList<User>>() {
                            @Override
                            public ArrayList<User> doOnThread() {
                                ArrayList<User> userList = new ArrayList<>();
                                for (User item : contactsBean.userList) {
                                    if (!item.strUserID.equals(AppDatas.Auth().getUserID())) {
                                        item.strHeadUrl = AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(item.strUserID, item.strDomainCode);
                                        userList.add(item);
                                    }
                                }
                                mTotalSize = userList.size();
                                return userList;
                            }

                            @Override
                            public void doOnMain(ArrayList<User> data) {
                                mAllContacts.clear();
                                mAllContacts.addAll(data);

                                updateContacts();
                            }
                        });
            }

            @Override
            public void onFinish(HTTPResponse httpResponse) {
                super.onFinish(httpResponse);
                refresh_view.setRefreshing(false);
            }
        });
    }


    protected void updateContacts() {
        mCustomContacts = getCustomContacts(mAllContacts);
        if (mCustomContacts != null) {
            adapter.setDatas(mCustomContacts);
            adapter.notifyDataSetChanged();
        }
    }


    private void changeNum(int num) {
        getNavigate().setRightText(AppUtils.getString(R.string.makesure) + "(" + num + ")");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }


    @OnClick(R.id.tv_choose_confirm)
    void onChoosedConfirmClicked() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateContacts();
    }


    private ArrayList<CustomContacts.LetterStructure> getCustomContacts(ArrayList<User> data) {
        if (data == null || data.size() <= 0) {
            return null;
        }
        CustomContacts customContacts = new CustomContacts();
        customContacts.letterStructures = new ArrayList<CustomContacts.LetterStructure>();
        for (int i = 0; i < 27; i++) {
            CustomContacts.LetterStructure item = new CustomContacts.LetterStructure();
            if (i != 26) {
                item.letter = (char) ('A' + i);
            } else {
                item.letter = '#';
            }
            item.users = null;
            customContacts.letterStructures.add(item);
        }
        for (User item : data) {
            String upPinYin = "";
            if (TextUtils.isEmpty(item.strUserNamePinYin)) {
                item.strUserNamePinYin = Pinyin.toPinyin(item.strUserName, "_");
                upPinYin = item.strUserNamePinYin.toUpperCase();
            } else {
                upPinYin = item.strUserNamePinYin.toUpperCase();
            }
            String a = "#";
            char firstLetter = TextUtils.isEmpty(upPinYin) ? a.charAt(0) : upPinYin.charAt(0);
            int index = firstLetter - 'A';
            if (index >= 0 && index < 26) {
                if (customContacts.letterStructures.get(index).users == null) {
                    customContacts.letterStructures.get(index).users = new ArrayList<User>();
                    customContacts.letterStructures.get(index).users.add(item);
                } else {
                    customContacts.letterStructures.get(index).users.add(item);
                }
            } else {
                if (customContacts.letterStructures.get(26).users == null) {
                    customContacts.letterStructures.get(26).users = new ArrayList<User>();
                    customContacts.letterStructures.get(26).users.add(item);
                } else {
                    customContacts.letterStructures.get(26).users.add(item);
                }
            }
        }
        CustomContacts newCustomContacts = new CustomContacts();
        newCustomContacts.letterStructures = new ArrayList<CustomContacts.LetterStructure>();
        for (int i = 0; i < customContacts.letterStructures.size(); i++) {
            if (customContacts.letterStructures.get(i).users != null &&
                    customContacts.letterStructures.get(i).users.size() > 0) {
                newCustomContacts.letterStructures.add(customContacts.letterStructures.get(i));
            }
        }
        return newCustomContacts.letterStructures;
    }

    private void handleChoice(User user) {
        if (user == null) {
            return;
        }
        if (VimChoosedContacts.get().isContain(user)) {
            VimChoosedContacts.get().removeContacts(user);
        } else {
            if (VimChoosedContacts.get().getContacts().size() >= max + 1) {
                showToast("最多选" + max + "人，已达人数上限");
                return;
            }
            VimChoosedContacts.get().addContacts(user);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VimChoosedContacts.get().clear();
    }
}
