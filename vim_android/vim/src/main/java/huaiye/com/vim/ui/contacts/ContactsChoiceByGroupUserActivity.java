package huaiye.com.vim.ui.contacts;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import java.util.ArrayList;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.views.FastRetrievalBar;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.bean.ContactsBean;
import huaiye.com.vim.models.contacts.bean.ContactsGroupUserListBean;
import huaiye.com.vim.models.contacts.bean.CustomContacts;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContactsNew;
import huaiye.com.vim.ui.home.adapter.ContactsItemAdapter;
import ttyy.com.jinnetwork.core.work.HTTPResponse;
import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;
import ttyy.com.recyclerexts.tags.TagsAdapter;

/**
 * author: admin
 * date: 2018/01/15
 * version: 0
 * mail: secret
 * desc: ContactsChoiceActivity
 */
@BindLayout(R.layout.activity_contacts_root)
public class ContactsChoiceByGroupUserActivity extends AppBaseActivity {
    public static final String SELECTED_CONTACTS = "selectedContacts";
    public static final String RESULT_CONTACTS = "resultContacts";
    @BindView(R.id.refresh_view)
    SwipeRefreshLayout refresh_view;
    @BindView(R.id.rct_view)
    RecyclerView rct_view;
    @BindView(R.id.contacts_retrieval_bar)
    FastRetrievalBar contacts_retrieval_bar;
    @BindView(R.id.rct_choosed)
    RecyclerView rct_choosed;
    @BindView(R.id.tv_choose_confirm)
    TextView tv_choose_confirm;
    @BindView(R.id.tv_letter_high_fidelity_item)
    TextView tv_letter_high_fidelity_item;

    @BindExtra
    String titleName;
    @BindExtra
    boolean isSelectUser;
    @BindExtra
    boolean needAddSelf;
    @BindExtra
    ContactsGroupUserListBean  mGroupUserListBean;

    TagsAdapter<CustomContacts.LetterStructure> adapter;

    EXTRecyclerAdapter<User> mChoosedAdapter;
    //    ArrayList<User> stricts = new ArrayList<>();
    private ArrayList<CustomContacts.LetterStructure> mCustomContacts;



    private ArrayList<User> mAllContacts = new ArrayList<>();
    private ArrayList<User> mOnlineContacts = new ArrayList<>();
    private int mPage = 1;
    private int mTotalSize = 0;
    private boolean mIsShowAll = false;
    private int mJoinNum=0;

    @Override
    protected void initActionBar() {
        if (TextUtils.isEmpty(titleName)) {
            titleName = getString(R.string.user_notice1);
        }
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
                        switchOnline();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        refresh_view.setEnabled(false);
        contacts_retrieval_bar.setVisibility(View.GONE);
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

            }
        });

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
                extViewHolder.setVisibility(R.id.letter_item_txt, View.GONE);
                ContactsItemAdapter itemAdapter = new ContactsItemAdapter(
                        ContactsChoiceByGroupUserActivity.this,
                        data.users, mCustomContacts.get(i - getHeaderViewsCount()).letter,
                        true,
                        ChoosedContactsNew.get().getContacts());

                itemAdapter.setOnItemClickLinstener(new ContactsItemAdapter.OnItemClickLinstener() {
                    @Override
                    public void onClick(int position, User user) {
                        if(user.nJoinStatus !=2 ) {
                            handleChoice(user);
                            itemAdapter.notifyItemChanged(position);
                        }
                    }
                });
                RecyclerView recyclerView = extViewHolder.findViewById(R.id.letter_item_recycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(ContactsChoiceByGroupUserActivity.this, LinearLayoutManager.VERTICAL, false));
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
//        FragmentContactsHeaderView fragmentContactsHeaderView = new FragmentContactsHeaderView(this);
//        adapter.addHeaderView(fragmentContactsHeaderView);
        rct_view.setAdapter(adapter);

        rct_choosed.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mChoosedAdapter = new EXTRecyclerAdapter<User>(R.layout.item_contacts_person_choosed) {
            @Override
            public void onBindViewHolder(EXTViewHolder extViewHolder, int i, User contactData) {
                if(contactData.nJoinStatus != 2) {
                    extViewHolder.setText(R.id.tv_contact_name, contactData.strUserName);
                } else {
                    extViewHolder.setVisibility(R.id.tv_contact_name,View.GONE);
                    mJoinNum++;
                }
            }
        };
        mChoosedAdapter.setDatas(ChoosedContactsNew.get().getContacts());
        mChoosedAdapter.setOnItemClickListener(new EXTRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int i) {
//                String loginName = mChoosedAdapter.getDatas().get(i).strLoginName;
//                ChoosedContacts.get().delTemp(mChoosedAdapter.getDatas().get(i));

//                mChoosedAdapter.getDatas().remove(i);
                ChoosedContactsNew.get().removeContacts(mChoosedAdapter.getDatas().get(i));
                mChoosedAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
                changeNum(mChoosedAdapter.getDatas().size());
                /*for(User item : mChoicedContacts){
                    if (loginName.equals(item.strLoginName)) {
                        mChoicedContacts.remove(item);
//                        adapter.setItemChecked(c, false);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }*/
                /*for (int c : adapter.getSelectedPositions()) {
                    if (loginName.equals(adapter.getDataForItemPosition(c).loginName)) {
                        adapter.setItemChecked(c, false);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }*/
            }
        });
        rct_choosed.setAdapter(mChoosedAdapter);

        // requestDatas();
        updateContacts();
    }

    protected void updateContacts(){
        mCustomContacts = getCustomContacts(mGroupUserListBean.lstGroupUser);

        if(mCustomContacts != null){
            adapter.setDatas(mCustomContacts);
            adapter.notifyDataSetChanged();
            //refresh_view.setRefreshing(false);
        }
    }

    private void changeNum(int num) {
        tv_choose_confirm.setText(getString(R.string.makesure)+"(" + num + ")");
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
//        ChoosedContacts.get().deleteSelf();
//        ChoosedContacts.get().clearTemp();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        updateContacts();
    }


    private ArrayList<CustomContacts.LetterStructure> getCustomContacts(ArrayList<ContactsGroupUserListBean.LstGroupUser> data) {
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



        for (int i = 0; i < data.size(); i++) {
            int index = 0;
            if (index >= 0 && index < 1) {
                if (customContacts.letterStructures.get(index).users == null) {
                    customContacts.letterStructures.get(index).users = new ArrayList<User>();
                }
                User  temp_item = new User() ;
                temp_item.strUserID= data.get(i).strUserID;
                temp_item.strUserName = data.get(i).strUserName;
                temp_item.strDomainCode = data.get(i).strUserDomainCode;
                temp_item.strHeadUrl = data.get(i).strHeadUrl;

                customContacts.letterStructures.get(index).users.add( i , temp_item);
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


    private ArrayList<CustomContacts.LetterStructure> getGroupCustomContacts(ArrayList<User> data) {
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

    private void handleChoice(User user) {
        if (user == null) {
            return;
        }
        if (ChoosedContactsNew.get().isContain(user)) {
            ChoosedContactsNew.get().removeContacts(user);
        } else {
            ChoosedContactsNew.get().addContacts(user);
        }
        /*if (mChoicedContacts != null) {
            for (User item : mChoicedContacts) {
                if (item.strUserID.equals(user.strUserID)) {
                    mChoicedContacts.remove(item);
                    mChoosedAdapter.notifyDataSetChanged();
                    adapter.notifyDataSetChanged();
                    return;
                }
            }
        }
        mChoicedContacts.add(user);*/
//        adapter.notifyDataSetChanged();
        mChoosedAdapter.notifyDataSetChanged();
        changeNum(mChoosedAdapter.getDatas().size()-mJoinNum);
        mJoinNum=0;
    }

    private void switchOnline() {
        /*切换是否显示全部联系人*/
        AppDatas.Constants().switchShowAllContacts();

        updateContacts();
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

    /*public ArrayList<User> getChoicedContacts() {
        return mChoicedContacts;
    }*/

}
