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
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import huaiye.com.vim.R;
import huaiye.com.vim.VIMApp;
import huaiye.com.vim.common.views.FastRetrievalBar;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.bean.ContactsGroupChatListBean;
import huaiye.com.vim.models.contacts.bean.CreateGroupContactData;
import huaiye.com.vim.models.contacts.bean.CustomContacts;
import huaiye.com.vim.models.contacts.bean.DomainInfoList;
import huaiye.com.vim.models.contacts.bean.GroupInfo;
import huaiye.com.vim.ui.home.adapter.GroupContactsItemAdapter;
import huaiye.com.vim.ui.meet.ChatGroupActivityNew;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;
import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.tags.TagsAdapter;

/**
 * Created by Administrator on 2018\3\14 0014.
 */

public class SearchGroupActivity extends AppCompatActivity {
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
    GroupContactsItemAdapter mGroupitemAdapter;
    private ArrayList<GroupInfo> mlstGroupInfo = new ArrayList<>();
    long time;
    private int mPage = 1;
    private int mTotalSize = 0;
    private String mSearchKey;
    private int mSource;//1--选择用户

    private int requestCount = 0;
    private int currentRequestTime = 0;

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
        mGroupitemAdapter = new GroupContactsItemAdapter(SearchGroupActivity.this, mlstGroupInfo, false, null);
        mGroupitemAdapter.setOnItemClickLinstener(new GroupContactsItemAdapter.OnItemClickLinstener() {
            @Override
            public void onClick(int position, GroupInfo user) {
                Intent intent = new Intent(SearchGroupActivity.this, ChatGroupActivityNew.class);
                CreateGroupContactData contactsBean = new CreateGroupContactData();
                contactsBean.strGroupID = mlstGroupInfo.get(position).strGroupID;
                contactsBean.strGroupDomainCode = mlstGroupInfo.get(position).strGroupDomainCode;
                intent.putExtra("mContactsBean", contactsBean);
                startActivity(intent);
            }
        });
        rct_view.setLayoutManager(new LinearLayoutManager(this));
        rct_view.setAdapter(mGroupitemAdapter);

    }

    private void initListener() {
        et_search_cancel.setOnClickListener(mOnClickListener);
        close.setOnClickListener(mOnClickListener);
        tv_choose_confirm.setOnClickListener(mOnClickListener);
        et_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                /*if (System.currentTimeMillis() - time > 1000) {
                    time = System.currentTimeMillis();

                }*/
                if (s != null && s.length() > 0) {
                    refreshDatas(s.toString());
                } else {
                    mGroupitemAdapter.setDatas(null);
                    mGroupitemAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void refreshDatas(String key) {
        mSearchKey = key;
        mPage = 1;

        if(null!= VIMApp.getInstance().mDomainInfoList&&VIMApp.getInstance().mDomainInfoList.size()>0){
            refresh_view.setRefreshing(true);
            requestCount = VIMApp.getInstance().mDomainInfoList.size();
            currentRequestTime = 0;
            for(DomainInfoList.DomainInfo domainInfo:VIMApp.getInstance().mDomainInfoList){
                ModelApis.Contacts().requestGroupBuddyContacts(-1, 0, 0, key,domainInfo.strDomainCode, new ModelCallback<ContactsGroupChatListBean>() {
                    @Override
                    public void onSuccess(final ContactsGroupChatListBean contactsBean) {
                        if(currentRequestTime==0){
                            mlstGroupInfo.clear();
                        }
                        ++currentRequestTime;
                        if(null!=contactsBean&&null!=contactsBean.lstGroupInfo&&contactsBean.lstGroupInfo.size()>0){
                            mlstGroupInfo.addAll(contactsBean.lstGroupInfo);
                            rct_view.setAdapter(mGroupitemAdapter);
                            mGroupitemAdapter.setDatas(mlstGroupInfo);
                            mGroupitemAdapter.notifyDataSetChanged();
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
                        if(requestCount==currentRequestTime){
                            refresh_view.setRefreshing(false);

                        }
                    }
                });
            }
        }


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
