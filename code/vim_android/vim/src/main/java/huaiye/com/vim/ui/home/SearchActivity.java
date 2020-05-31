package huaiye.com.vim.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import huaiye.com.vim.R;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.views.FastRetrievalBar;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.bean.ContactsBean;
import huaiye.com.vim.models.contacts.bean.CustomContacts;
import huaiye.com.vim.ui.contacts.ContactDetailNewActivity;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContactsNew;
import huaiye.com.vim.ui.home.adapter.ContactsItemAdapter;
import ttyy.com.jinnetwork.core.work.HTTPResponse;
import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;
import ttyy.com.recyclerexts.tags.TagsAdapter;

/**
 * Created by Administrator on 2018\3\14 0014.
 */

public class SearchActivity extends AppCompatActivity {
    @BindView(R.id.et_key)
    EditText et_key;
    @BindView(R.id.et_search_cancel)
    TextView et_search_cancel;
    @BindView(R.id.close)
    ImageView close;
    @BindView(R.id.refresh_view)
    SwipeRefreshLayout refresh_view;
    @BindView(R.id.rct_view)
    RecyclerView rct_view;
    @BindView(R.id.contacts_retrieval_bar)
    FastRetrievalBar contacts_retrieval_bar;
    @BindView(R.id.iv_empty_view)
    RelativeLayout iv_empty_view;
    @BindView(R.id.ll_choosed_persons)
    LinearLayout ll_choosed_persons;
    @BindView(R.id.rct_choosed)
    RecyclerView rct_choosed;
    @BindView(R.id.tv_choose_confirm)
    TextView tv_choose_confirm;

    TagsAdapter<CustomContacts.LetterStructure> adapter;
    private ArrayList<CustomContacts.LetterStructure> mCustomContacts;
    private ArrayList<User> mAllContacts = new ArrayList<>();
    private EXTRecyclerAdapter<User> mChoosedAdapter;
    long time;
    private int mPage = 1;
    private int mTotalSize = 0;
    private String mSearchKey;
    private int mSource;//1--选择用户

    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_meetings_history);
        ButterKnife.bind(this);
        initListener();
        initData();
    }

    private void initData() {
        mSource = getIntent().getIntExtra("mSource", 0);
        if (mSource == 1) {
            ll_choosed_persons.setVisibility(View.VISIBLE);
        }
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
                        SearchActivity.this,
                        data.users, mCustomContacts.get(i - getHeaderViewsCount()).letter,
                        mSource == 1 ? true : false,
                        ChoosedContactsNew.get().getContacts());
                itemAdapter.setOnItemClickLinstener(new ContactsItemAdapter.OnItemClickLinstener() {
                    @Override
                    public void onClick(int position, User user) {
                        if (mSource == 1 ? true : false) {
                            handleChoice(user);
                        } else {
                            Intent intent = new Intent(SearchActivity.this, ContactDetailNewActivity.class);
                            intent.putExtra("nUser", user);
                            startActivity(intent);
                        }
                    }
                });
                itemAdapter.setOnLoadMoreListener(new ContactsItemAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(char letter) {
                        char c = mCustomContacts.get(mCustomContacts.size() - 1).letter;
                        if (c == letter) {
                            SearchActivity.this.loadMore();
                        }
                    }
                });
                RecyclerView recyclerView = extViewHolder.findViewById(R.id.letter_item_recycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false));
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(itemAdapter);
            }
        };
        rct_view.setLayoutManager(new LinearLayoutManager(this));
        rct_view.setAdapter(adapter);

        mChoosedAdapter = new EXTRecyclerAdapter<User>(R.layout.item_contacts_person_choosed) {
            @Override
            public void onBindViewHolder(EXTViewHolder extViewHolder, int i, User contactData) {
                extViewHolder.setText(R.id.tv_contact_name, contactData.strUserName);
            }
        };
        mChoosedAdapter.setDatas(ChoosedContactsNew.get().getContacts());
        mChoosedAdapter.setOnItemClickListener(new EXTRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int i) {
                ChoosedContactsNew.get().removeContacts(mChoosedAdapter.getDatas().get(i));
                mChoosedAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
                changeNum(mChoosedAdapter.getDatas().size());
            }
        });
        rct_choosed.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rct_choosed.setAdapter(mChoosedAdapter);
    }

    private void initListener() {
        et_search_cancel.setOnClickListener(mOnClickListener);
        close.setOnClickListener(mOnClickListener);
        tv_choose_confirm.setOnClickListener(mOnClickListener);
        et_key.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                 /*if (System.currentTimeMillis() - time > 1000) {
                    time = System.currentTimeMillis();
                }*/
                    Editable s = et_key.getText();
                    if (s != null && s.length() > 0) {
                        refreshDatas(s.toString());
                    } else {
                        adapter.setDatas(null);
                        adapter.notifyDataSetChanged();
                    }
                    return true;
                }
                return false;
            }
        });

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
    }

    private void refreshDatas(String key) {
        mSearchKey = key;
        mPage = 1;
        ModelApis.Contacts().searchContacts(mPage, key, new ModelCallback<ContactsBean>() {
            @Override
            public void onSuccess(final ContactsBean contactsBean) {
                mTotalSize = contactsBean.nTotalSize;
                mAllContacts.clear();
                mAllContacts.addAll(contactsBean.userList);
                mCustomContacts = getCustomContacts(mAllContacts);
                adapter.setDatas(mCustomContacts);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish(HTTPResponse httpResponse) {
                super.onFinish(httpResponse);
                refresh_view.setRefreshing(false);
            }
        });
    }

    private void loadMore() {
        if (mAllContacts.size() >= mTotalSize) {
            return;
        }
        ModelApis.Contacts().searchContacts(mPage + 1, mSearchKey, new ModelCallback<ContactsBean>() {
            @Override
            public void onSuccess(final ContactsBean contactsBean) {
                new RxUtils<ArrayList<User>>()
                        .doOnThreadObMain(new RxUtils.IThreadAndMainDeal<ArrayList<User>>() {
                            @Override
                            public ArrayList<User> doOnThread() {
                                mTotalSize = contactsBean.nTotalSize;
                                return contactsBean.userList;
                            }

                            @Override
                            public void doOnMain(ArrayList<User> data) {
                                mPage++;
                                mAllContacts.addAll(data);
                                mCustomContacts = getCustomContacts(mAllContacts);
                                adapter.setDatas(mCustomContacts);
                                adapter.notifyDataSetChanged();
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

    private void handleChoice(User user) {
        if (user == null) {
            return;
        }
        if (ChoosedContactsNew.get().isContain(user)) {
            ChoosedContactsNew.get().removeContacts(user);
        } else {
            ChoosedContactsNew.get().addContacts(user);
        }
        adapter.notifyDataSetChanged();
        mChoosedAdapter.notifyDataSetChanged();
        changeNum(mChoosedAdapter.getDatas().size());
    }

    private void changeNum(int num) {
        tv_choose_confirm.setText("确定(" + num + ")");
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.et_search_cancel:
                    finish();
                    break;
                case R.id.close:
                    et_key.setText("");
                    break;
                case R.id.tv_choose_confirm:
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };
}
