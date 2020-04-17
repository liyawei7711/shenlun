package huaiye.com.vim.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huaiye.sdk.logger.Logger;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huaiye.com.vim.R;
import huaiye.com.vim.VIMApp;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseFragment;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.common.helper.ChatContactsGroupUserListHelper;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.views.FastRetrievalBar;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.dao.msgs.VimMessageListMessages;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.ContactsApi;
import huaiye.com.vim.models.contacts.bean.ContactsBean;
import huaiye.com.vim.models.contacts.bean.ContactsGroupChatListBean;
import huaiye.com.vim.models.contacts.bean.ContactsGroupUserListBean;
import huaiye.com.vim.models.contacts.bean.CreateGroupContactData;
import huaiye.com.vim.models.contacts.bean.CustomContacts;
import huaiye.com.vim.models.contacts.bean.DomainInfoList;
import huaiye.com.vim.models.contacts.bean.GroupInfo;
import huaiye.com.vim.ui.contacts.ContactDetailNewActivity;
import huaiye.com.vim.ui.contacts.ContactsFrequentActivity;
import huaiye.com.vim.ui.contacts.GroupListActivity;
import huaiye.com.vim.ui.home.adapter.ContactsItemAdapter;
import huaiye.com.vim.ui.home.adapter.GroupContactsItemAdapter;
import huaiye.com.vim.ui.meet.ChatGroupActivityNew;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;
import ttyy.com.recyclerexts.base.EXTViewHolder;
import ttyy.com.recyclerexts.tags.TagsAdapter;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: FragmentContacts
 */
@BindLayout(R.layout.fragment_contacts)
public class FragmentContacts extends AppBaseFragment {

    public static final String TAG = "FragmentContacts";

    @BindView(R.id.refresh_view)
    SwipeRefreshLayout refresh_view;
    @BindView(R.id.contacts_retrieval_bar)
    FastRetrievalBar contacts_retrieval_bar;
    /*@BindView(R.id.rct_view_layout)
    RelativeLayout rct_view_layout;*/
    @BindView(R.id.rct_view)
    RecyclerView rct_view;
    @BindView(R.id.view_list)
    RecyclerView view_list;
    @BindView(R.id.iv_empty_view)
    View iv_empty_view;
    @BindView(R.id.tv_title)
    View tv_title;
    @BindView(R.id.tv_letter_high_fidelity_item)
    TextView tv_letter_high_fidelity_item;

    TagsAdapter<CustomContacts.LetterStructure> adapter;
    GroupContactsItemAdapter mGroupitemAdapter;

    private ArrayList<CustomContacts.LetterStructure> mCustomContacts;
    private ArrayList<User> mAllContacts = new ArrayList<>();

    private ArrayList<GroupInfo> mlstGroupInfo = new ArrayList<>();

    private ArrayList<User> mOnlineContacts = new ArrayList<>();

    private boolean isFreadList = true;
    private int requestCount = 0;
    private int currentRequestTime = 0;
    private boolean isSOS;

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        getNavigate().hideLeftIcon()
                .setReserveStatusbarPlace()
                .setTitlText("联系人")
                .setTitlClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View V) {
                        if(isSOS) {
                            return;
                        }
                        isFreadList = true;
                        contacts_retrieval_bar.setVisibility(View.VISIBLE);
                        if (null != mAllContacts && mAllContacts.size() > 0) {
                            updateContacts(true);
                        } else {
                            requestContacts();
                        }
                    }
                })
//                .setTitl1Text("群聊")
//                .setTitl1ClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View V) {
//                        contacts_retrieval_bar.setVisibility(View.GONE);
//                        if(null!=mlstGroupInfo&&mlstGroupInfo.size()>0){
//                            updateGroupContacts();
//                        }else{
//                            requestGroupContacts();
//                        }
//                        isFreadList = false;
//                    }
//                })
                .showTopSearch()
                .showTopAdd()
                .setTopSearchClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isSOS) {
                            return;
                        }
                        if (isFreadList) {
                            Log.d(this.getClass().getName(), "onClick");
                            Intent intent = new Intent(getContext(), SearchActivity.class);
                            intent.putExtra(ContactsFrequentActivity.SOURCE, 0);
                            startActivity(intent);
                        } else {
                            Log.d(this.getClass().getName(), "onClick");
                            Intent intent = new Intent(getContext(), SearchGroupActivity.class);
                            intent.putExtra(ContactsFrequentActivity.SOURCE, 0);
                            startActivity(intent);
                        }
                    }
                }).setTopAddClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSOS) {
                    return;
                }
                showChatMoreStylePopupWindow(v);
            }
        });

        refresh_view.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.blue),
                ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        rct_view.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                ContactsItemAdapter itemAdapter = new ContactsItemAdapter(
                        getActivity(),
                        data.users, mCustomContacts.get(i - getHeaderViewsCount()).letter,
                        false,
                        null);
                itemAdapter.setOnItemClickLinstener((position, user) -> {
                    Intent intent = new Intent(getActivity(), ContactDetailNewActivity.class);
                    intent.putExtra("nUser", user);
                    startActivity(intent);
                });

                RecyclerView recyclerView = extViewHolder.findViewById(R.id.letter_item_recycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(itemAdapter);
            }
        };

        mGroupitemAdapter = new GroupContactsItemAdapter(getActivity(), mlstGroupInfo, false, null);
        mGroupitemAdapter.setOnItemClickLinstener(new GroupContactsItemAdapter.OnItemClickLinstener() {
            @Override
            public void onClick(int position, GroupInfo user) {
                Intent intent = new Intent(getActivity(), ChatGroupActivityNew.class);
                CreateGroupContactData contactsBean = new CreateGroupContactData();
                contactsBean.strGroupID = mlstGroupInfo.get(position).strGroupID;
                contactsBean.strGroupDomainCode = mlstGroupInfo.get(position).strGroupDomainCode;
                intent.putExtra("mContactsBean", contactsBean);
                startActivity(intent);
            }
        });

        refresh_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isFreadList) {
                    requestContacts();
                } else {
                    requestGroupContacts();
                }
            }
        });

        contacts_retrieval_bar.setTextView(tv_letter_high_fidelity_item);
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

        if(!isSOS) {
            initData();
        }
    }

    private void initData() {
        requestContacts();
        requestGroupContacts();

    }

    private void requestContacts() {
        Log.i(this.getClass().getName(), "requestContacts");

        /* -1表示不分页，即获取所有联系人 */
        ModelApis.Contacts().requestBuddyContacts(-1, 0, 0, new ModelCallback<ContactsBean>() {
            @Override
            public void onSuccess(final ContactsBean contactsBean) {
                if (null != contactsBean && null != contactsBean.userList && contactsBean.userList.size() > 0) {
                    mAllContacts.clear();
                    mAllContacts.addAll(contactsBean.userList);
                    if (isFreadList) {
                        updateContacts(true);
                    }
                }
            }

            @Override
            public void onFinish(HTTPResponse httpResponse) {
                super.onFinish(httpResponse);
                refresh_view.setRefreshing(false);
            }
        });
    }

    private void getUserInfos(ArrayList<User> userList) {
        new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal<Map<String, List<String>>>() {
            @Override
            public Map<String, List<String>> doOnThread() {
                Map<String, List<String>> groups = groupBystrDomainCode(userList);
                return groups;
            }

            @Override
            public void doOnMain(Map<String, List<String>> data) {
                for (Map.Entry<String, List<String>> entry : data.entrySet()) {
                    String mapKey = entry.getKey();
                    List<String> mapValue = entry.getValue();
                    ContactsApi.get().requestUserInfoList(mapKey, mapValue, new ModelCallback<ContactsBean>() {
                        @Override
                        public void onSuccess(ContactsBean contactsBean) {

                            if (null != contactsBean && null != contactsBean.userList && contactsBean.userList.size() > 0) {
                                AppDatas.MsgDB().getFriendListDao().insertAll(contactsBean.userList);
                                refreshUserData(contactsBean.userList);
                            }
                        }
                    });
                }
            }
        });


    }

    private void refreshUserData(List<User> users) {
        new RxUtils<List<User>>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal<List<User>>() {
            @Override
            public List<User> doOnThread() {
                if (null != mAllContacts && mAllContacts.size() > 0 && null != users && users.size() > 0) {
                    for (User userAll : mAllContacts) {
                        for (User user : users) {
                            if (userAll.strDomainCode.equals(user.strDomainCode) && userAll.strUserID.equals(user.strUserID)) {
                                mAllContacts.set(mAllContacts.indexOf(userAll), user);
                                continue;
                            }
                        }
                    }

                }
                return mAllContacts;
            }

            @Override
            public void doOnMain(List<User> data) {

                if (null != adapter) {
                    adapter.notifyDataSetChanged();
                    if (isFreadList) {
                        updateContacts(false);
                    }
                }
            }
        });

    }

    private void refreshCurrentUserData(User user) {
        new RxUtils<List<User>>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal<List<User>>() {
            @Override
            public List<User> doOnThread() {
                if (null != mAllContacts && mAllContacts.size() > 0 && null != user) {
                    for (User userAll : mAllContacts) {
                        if (userAll.strDomainCode.equals(user.strDomainCode) && userAll.strUserID.equals(user.strUserID)) {
                            mAllContacts.set(mAllContacts.indexOf(userAll), user);
                            continue;
                        }
                    }

                }
                return mAllContacts;
            }

            @Override
            public void doOnMain(List<User> data) {

                if (null != adapter) {
                    adapter.notifyDataSetChanged();
                    if (isFreadList) {
                        updateContacts(false);
                    }
                }
            }
        });

    }

    private Map<String, List<String>> groupBystrDomainCode(List<User> userList) {
        Map<String, List<String>> groupBy = new HashMap<>();
        for (User nUser : userList) {
            if (groupBy.containsKey(nUser.strDomainCode)) {
                groupBy.get(nUser.strDomainCode).add(nUser.strUserID);
            } else {
                List<String> users = new ArrayList<>();
                users.add(nUser.strUserID);
                groupBy.put(nUser.strDomainCode, users);
            }
        }
        return groupBy;
    }

    private void requestGroupContacts() {
        Log.i(this.getClass().getName(), "requestGroupContacts");
        /* -1表示不分页，即获取所有联系人 */
        if (null != VIMApp.getInstance().mDomainInfoList && VIMApp.getInstance().mDomainInfoList.size() > 0) {
            refresh_view.setRefreshing(true);
            requestCount = VIMApp.getInstance().mDomainInfoList.size();
            currentRequestTime = 0;
            for (DomainInfoList.DomainInfo domainInfo : VIMApp.getInstance().mDomainInfoList) {
                ModelApis.Contacts().requestGroupBuddyContacts(-1, 0, 0, null, domainInfo.strDomainCode, new ModelCallback<ContactsGroupChatListBean>() {
                    @Override
                    public void onSuccess(final ContactsGroupChatListBean contactsBean) {
                        if (currentRequestTime == 0) {
                            mlstGroupInfo.clear();
                        }
                        ++currentRequestTime;
                        mlstGroupInfo.addAll(contactsBean.lstGroupInfo);
                        updateMsgTopNoDisturb(contactsBean.lstGroupInfo);
                        if (!isFreadList) {
                            updateGroupContacts();
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        ++currentRequestTime;
                    }

                    @Override
                    public void onCancel(HTTPRequest httpRequest) {
                        super.onCancel(httpRequest);
                        ++currentRequestTime;
                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {
                        super.onFinish(httpResponse);
                        if (requestCount == currentRequestTime) {
                            refresh_view.setRefreshing(false);
                            if (null != mlstGroupInfo && mlstGroupInfo.size() > 0) {
                                AppDatas.MsgDB().getGroupListDao().insertAll(mlstGroupInfo);
                                Logger.debug(TAG, AppDatas.MsgDB().getGroupListDao().getGroupList().size() + "");
                            }
                        }
                    }
                });
            }
        } else {
            refresh_view.setRefreshing(false);
            VIMApp.getInstance().getDomainCodeList();
        }

    }

    private void updateMsgTopNoDisturb(ArrayList<GroupInfo> lstGroupInfo) {
        if (null != lstGroupInfo && lstGroupInfo.size() > 0) {
            new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
                @Override
                public Object doOnThread() {
                    for (GroupInfo groupInfo : lstGroupInfo) {
                        VimMessageListMessages.get().updateNoDisturb(groupInfo.strGroupDomainCode + groupInfo.strGroupID, groupInfo.nNoDisturb);
                        SP.putInt(groupInfo.strGroupDomainCode + groupInfo.strGroupID + AppUtils.SP_SETTING_NODISTURB, groupInfo.nNoDisturb);
                        if (groupInfo.nMsgTop == SP.getInteger(groupInfo.strGroupDomainCode + groupInfo.strGroupID + AppUtils.SP_SETTING_MSG_TOP, 0)) {
                            continue;
                        }
                        VimMessageListMessages.get().updateMsgTop(groupInfo.strGroupDomainCode + groupInfo.strGroupID, groupInfo.nMsgTop);
                        SP.putInt(groupInfo.strGroupDomainCode + groupInfo.strGroupID + AppUtils.SP_SETTING_MSG_TOP, groupInfo.nMsgTop);
                        SP.putLong(groupInfo.strGroupDomainCode + groupInfo.strGroupID + AppUtils.SP_SETTING_MSG_TOP_TIME, System.currentTimeMillis());
                    }
                    return "";
                }

                @Override
                public void doOnMain(Object data) {

                }
            });
        }
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
            String upPinYin = item.strUserNamePinYin.toUpperCase();
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

    public void updateContacts(boolean isNeedRefreshUserInfo) {
        new RxUtils<List<User>>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
            @Override
            public Object doOnThread() {
                mCustomContacts = getCustomContacts(mAllContacts);
                return mCustomContacts;
            }

            @Override
            public void doOnMain(Object data) {
                rct_view.setAdapter(adapter);
                adapter.setDatas(mCustomContacts);
                adapter.notifyDataSetChanged();
                if (isNeedRefreshUserInfo) {
                    getUserInfos(mAllContacts);
                }
            }
        });


    }

    public void updateGroupContacts() {

        rct_view.setAdapter(mGroupitemAdapter);
        mGroupitemAdapter.setDatas(mlstGroupInfo);
        mGroupitemAdapter.notifyDataSetChanged();
    }

    private void switchOnline() {
        /*切换是否显示全部联系人*/
        AppDatas.Constants().switchShowAllContacts();

        updateContacts(false);
    }

    private ArrayList<User> getOnlineContacts() {
        mOnlineContacts.clear();
        for (User item : mAllContacts) {
            if (item.nStatus > 0) {
                mOnlineContacts.add(item);
            }
        }
        return mOnlineContacts;
    }

    @Override
    public void onResume() {
        super.onResume();
       /* if (isFreadList) {
            requestContacts();
        } else {
            requestGroupContacts();
        }*/
    }

    @OnClick({R.id.tv_group})
    public void onClick(View view) {
        if(isSOS) {
            return;
        }
        Intent intent = new Intent(getContext(), GroupListActivity.class);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final MessageEvent messageEvent) {
        if (null == messageEvent) {
            return;
        }
        switch (messageEvent.what) {
            case AppUtils.EVENT_CREATE_GROUP_SUCCESS_ADDGROUP_TO_LIST:
            case AppUtils.EVENT_MESSAGE_MODIFY_GROUP:
                new RxUtils().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
                    @Override
                    public Object doOnThread() {
                        if (null != mlstGroupInfo && !TextUtils.isEmpty(messageEvent.msgContent)) {
                            ContactsGroupUserListBean nContactsGroupUserListBean = ChatContactsGroupUserListHelper.getInstance().getContactsGroupDetail(messageEvent.msgContent);
                            boolean needAddGroup = true;
                            if (null != nContactsGroupUserListBean) {
                                for (GroupInfo nGroupInfo : mlstGroupInfo) {
                                    if (nGroupInfo.strGroupID.equals(messageEvent.msgContent)) {
                                        nGroupInfo.strHeadUrl = nContactsGroupUserListBean.strHeadUrl;
                                    }
                                    if (messageEvent.msgContent.equals(nGroupInfo.strGroupID)) {
                                        needAddGroup = false;
                                        break;
                                    }
                                }
                                if (needAddGroup) {
                                    GroupInfo nGroupInfo = new GroupInfo();
                                    nGroupInfo.strGroupDomainCode = nContactsGroupUserListBean.strGroupDomainCode;
                                    nGroupInfo.strGroupID = nContactsGroupUserListBean.strGroupID;
                                    nGroupInfo.strGroupName = nContactsGroupUserListBean.strGroupName;
                                    nGroupInfo.strHeadUrl = nContactsGroupUserListBean.strHeadUrl;
                                    mlstGroupInfo.add(nGroupInfo);
                                }
                            }

                        }
                        return mlstGroupInfo;
                    }

                    @Override
                    public void doOnMain(Object data) {
                        if (null != mGroupitemAdapter && !isFreadList) {
                            mGroupitemAdapter.notifyDataSetChanged();
                        }

                    }
                });

                break;
            case AppUtils.EVENT_MESSAGE_ADD_FRIEND:
            case AppUtils.EVENT_MESSAGE_DEL_FRIEND:
                requestContacts();
                break;
            case AppUtils.EVENT_DEL_GROUP_SUCCESS:
            case AppUtils.EVENT_LEAVE_GROUP_SUCCESS:
                requestGroupContacts();
                break;
            case AppUtils.EVENT_MESSAGE_MODIFY_HEAD_PIC:
                User user = (User) messageEvent.obj1;
                refreshCurrentUserData(user);
                break;
            default:
                break;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void setSos(boolean isSOS) {
        this.isSOS = isSOS;
    }
}
