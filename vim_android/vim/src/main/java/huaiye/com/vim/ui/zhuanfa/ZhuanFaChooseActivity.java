package huaiye.com.vim.ui.zhuanfa;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vim.R;
import huaiye.com.vim.bus.CloseZhuanFa;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.views.FastRetrievalBar;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.msgs.ChatMessageBase;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.dao.msgs.UserInfo;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.bean.ContactsBean;
import huaiye.com.vim.models.contacts.bean.CustomContacts;
import huaiye.com.vim.ui.home.adapter.ContactsItemAdapter;
import ttyy.com.jinnetwork.core.work.HTTPResponse;
import ttyy.com.recyclerexts.base.EXTViewHolder;
import ttyy.com.recyclerexts.tags.TagsAdapter;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 */
@BindLayout(R.layout.activity_zhuanfa_choose)
public class ZhuanFaChooseActivity extends AppBaseActivity {

    @BindView(R.id.fl_root)
    View fl_root;
    @BindView(R.id.refresh_view)
    SwipeRefreshLayout refresh_view;
    @BindView(R.id.contacts_retrieval_bar)
    FastRetrievalBar contacts_retrieval_bar;
    @BindView(R.id.rct_view)
    RecyclerView rct_view;
    @BindView(R.id.tv_letter_high_fidelity_item)
    TextView tv_letter_high_fidelity_item;

    TagsAdapter<CustomContacts.LetterStructure> adapter;

    private ArrayList<CustomContacts.LetterStructure> mCustomContacts;
    private ArrayList<User> mAllContacts = new ArrayList<>();

    @BindExtra
    String strUserDomainCode;
    @BindExtra
    String strUserID;
    @BindExtra
    ChatMessageBase data;
    @BindExtra
    ArrayList<UserInfo> users;
    @BindExtra
    boolean isGroup;
    @BindExtra
    String strGroupID;
    @BindExtra
    String strGroupDomain;
    @BindExtra
    ArrayList<SendUserBean> mMessageUsersDate;
    ZhuanFaPopupWindow zhuanFaPopupWindow;

    @Override
    protected void initActionBar() {
        EventBus.getDefault().register(this);
        getNavigate().setVisibility(View.VISIBLE);
        getNavigate().setTitlText(getString(R.string.user_notice1))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        zhuanFaPopupWindow = new ZhuanFaPopupWindow(this, users, strUserID, strUserDomainCode, isGroup, strGroupID, strGroupDomain);

        refresh_view.setColorSchemeColors(ContextCompat.getColor(this, R.color.blue),
                ContextCompat.getColor(this, R.color.colorPrimary));
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
                ContactsItemAdapter itemAdapter = new ContactsItemAdapter(ZhuanFaChooseActivity.this,
                        data.users, mCustomContacts.get(i - getHeaderViewsCount()).letter,
                        false,
                        null);
                itemAdapter.setOnItemClickLinstener((position, user) -> {
                    zhuanFaPopupWindow.setSendUser(user);
                    zhuanFaPopupWindow.showAtLocation(fl_root, Gravity.CENTER, 0, 0);
                    zhuanFaPopupWindow.showData(ZhuanFaChooseActivity.this.data);
                });

                RecyclerView recyclerView = extViewHolder.findViewById(R.id.letter_item_recycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(ZhuanFaChooseActivity.this, LinearLayoutManager.VERTICAL, false));
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(itemAdapter);
            }
        };

        refresh_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestContacts();
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

        requestContacts();
    }

    private void requestContacts() {

        /* -1表示不分页，即获取所有联系人 */
        ModelApis.Contacts().requestBuddyContacts(-1, 0, 0, new ModelCallback<ContactsBean>() {
            @Override
            public void onSuccess(final ContactsBean contactsBean) {
                if (null != contactsBean && null != contactsBean.userList && contactsBean.userList.size() > 0) {
                    mAllContacts.clear();
                    for (User temp : contactsBean.userList) {
                        if (!data.sessionID.equals(TextUtils.isEmpty(temp.strDomainCode) ? temp.strUserDomainCode : temp.strDomainCode + temp.strUserID)) {
                            temp.strHeadUrl = AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(temp.strUserID, TextUtils.isEmpty(temp.strDomainCode) ? temp.strUserDomainCode : temp.strDomainCode);
                            mAllContacts.add(temp);
                        }
                    }
                    updateContacts();
                }
            }

            @Override
            public void onFinish(HTTPResponse httpResponse) {
                super.onFinish(httpResponse);
                refresh_view.setRefreshing(false);
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
                    updateContacts();
                }
            }
        });
    }

    public void updateContacts() {
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
            }
        });
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

    @OnClick({R.id.tv_group})
    public void onClick(View view) {
        Intent intent = new Intent(this, ZhuanFaGroupListActivity.class);
        intent.putExtra("data", data);
        intent.putExtra("users", users);
        intent.putExtra("strUserID", strUserID);
        intent.putExtra("strUserDomainCode", strUserDomainCode);
        intent.putExtra("isGroup", isGroup);
        intent.putExtra("strGroupID", strGroupID);
        intent.putExtra("strGroupDomain", strGroupDomain);
        intent.putExtra("mMessageUsersDate", mMessageUsersDate);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final MessageEvent messageEvent) {
        if (null == messageEvent) {
            return;
        }
        switch (messageEvent.what) {
            case AppUtils.EVENT_MESSAGE_ADD_FRIEND:
            case AppUtils.EVENT_MESSAGE_DEL_FRIEND:
                requestContacts();
                break;
            case AppUtils.EVENT_MESSAGE_MODIFY_HEAD_PIC:
                User user = (User) messageEvent.obj1;
                refreshCurrentUserData(user);
                break;
            default:
                break;
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final CloseZhuanFa messageEvent) {
       finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
