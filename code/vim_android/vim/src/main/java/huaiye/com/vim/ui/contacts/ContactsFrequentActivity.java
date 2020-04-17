package huaiye.com.vim.ui.contacts;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.constants.AppFrequentlyConstants;
import huaiye.com.vim.dao.constants.FrequentlyConstantsData;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.bean.ContactsBean;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContactsNew;
import huaiye.com.vim.ui.home.SearchActivity;
import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;
import ttyy.com.recyclerexts.tags.TagsAdapter;

/**
 * Created by ywt on 2019/2/25.
 */
@BindLayout(R.layout.activity_contact_frequent)
public class ContactsFrequentActivity extends AppBaseActivity {
    public static final String SOURCE = "mSource";
    @BindView(R.id.contacts_header_search)
    LinearLayout contacts_header_search;
    @BindView(R.id.frequent_recycler)
    RecyclerView frequent_recycler;
    @BindView(R.id.ll_choosed_persons)
    LinearLayout ll_choosed_persons;
    @BindView(R.id.rct_choosed)
    RecyclerView rct_choosed;
    @BindView(R.id.tv_choose_confirm)
    TextView tv_choose_confirm;

    @BindExtra
    int mSource;//1--选择用户

    private TagsAdapter mTagsAdapter;
    EXTRecyclerAdapter<User> mChosenAdapter;

    private ArrayList<User> mAllFrequentContactList;//所有的常用联系人
    private ArrayList<User> mOnlineFrequentContactList = new ArrayList<>();//在线的常用联系人

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(getString(R.string.frequent_contacts))
            .setLeftClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            })
            .setRightClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(getLocalClassName(), "onClick:" +getNavigate().getRightText());

                    /*先保存是否显示全部联系人到SP中*/
                    AppDatas.Constants().switchShowAllContacts();

                    /*再更新联系人*/
                    updateContacts();
                }
            });

        if (mSource == 1) {
            ll_choosed_persons.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void doInitDelay() {
        mTagsAdapter = new TagsAdapter<User>(R.layout.item_contacts_person) {
            @Override
            public void onBindTagViewHolder(EXTViewHolder extViewHolder, int i, User user) {
                extViewHolder.setText(R.id.tv_user_name, user.strUserName);
                if (user.nStatus > 0) {
                    //在线
                    extViewHolder.setImageResouce(R.id.iv_user_head, R.drawable.default_image_personal);
                } else {
                    extViewHolder.setImageResouce(R.id.iv_user_head, R.drawable.default_image_personal);
                }
                if(mSource == 1){
                    extViewHolder.setVisibility(R.id.iv_choice, View.VISIBLE);
                    if (ChoosedContactsNew.get().isContain(user)) {
                        extViewHolder.setImageResouce(R.id.iv_choice, R.drawable.ic_choice_checked);
                    } else {
                        extViewHolder.setImageResouce(R.id.iv_choice, R.drawable.ic_choice);
                    }
                } else {
                    extViewHolder.setVisibility(R.id.iv_choice, View.GONE);
                }
                if (i == mAllFrequentContactList.size() - 1) {
                    extViewHolder.setVisibility(R.id.view_divider, View.GONE);
                } else {
                    extViewHolder.setVisibility(R.id.view_divider, View.VISIBLE);
                }
            }
        };
        mTagsAdapter.setOnItemClickListener(new EXTRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int i) {
                if (mSource == 1 ) {
                    if(AppDatas.Constants().isShowAllContacts())
                        handleChoice(mAllFrequentContactList.get(i));
                    else
                        handleChoice(mOnlineFrequentContactList.get(i));
                } else {
                    Intent intent = new Intent(ContactsFrequentActivity.this, ContactDetailNewActivity.class);
                    intent.putExtra("nUser", mAllFrequentContactList.get(i));
                    startActivity(intent);
                }
            }
        });
        frequent_recycler.setLayoutManager(new LinearLayoutManager(this));
        frequent_recycler.setAdapter(mTagsAdapter);

        mChosenAdapter = new EXTRecyclerAdapter<User>(R.layout.item_contacts_person_choosed) {
            @Override
            public void onBindViewHolder(EXTViewHolder extViewHolder, int i, User contactData) {
                extViewHolder.setText(R.id.tv_contact_name, contactData.strUserName);
            }
        };
        mChosenAdapter.setDatas(ChoosedContactsNew.get().getContacts());
        mChosenAdapter.setOnItemClickListener(new EXTRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int i) {
                ChoosedContactsNew.get().removeContacts(mChosenAdapter.getDatas().get(i));
                mChosenAdapter.notifyDataSetChanged();
                mTagsAdapter.notifyDataSetChanged();
                changeNum(mChosenAdapter.getDatas().size());
            }
        });
        rct_choosed.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rct_choosed.setAdapter(mChosenAdapter);

        requestContacts();
    }

    private void requestContacts() {
        List<FrequentlyConstantsData> list = AppFrequentlyConstants.get().getConstants();
        if (list == null || list.size() <= 0) {
            return;
        }
        ArrayList<String> tempList = new ArrayList<>();
        for(FrequentlyConstantsData item : list){
            tempList.add(item.userId);
        }
        ModelApis.Contacts().requestSpecificContacts(tempList, new ModelCallback<ContactsBean>() {
            @Override
            public void onSuccess(final ContactsBean contactsBean) {
                new RxUtils<ArrayList<User>>()
                        .doOnThreadObMain(new RxUtils.IThreadAndMainDeal<ArrayList<User>>() {
                            @Override
                            public ArrayList<User> doOnThread() {
                                mAllFrequentContactList = contactsBean.userList;
                                return mAllFrequentContactList;
                            }

                            @Override
                            public void doOnMain(ArrayList<User> data) {
                                updateContacts();
                            }
                        });
            }
        });
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
        mTagsAdapter.notifyDataSetChanged();
        mChosenAdapter.notifyDataSetChanged();
        changeNum(mChosenAdapter.getDatas().size());
    }

    private void changeNum(int num) {
        tv_choose_confirm.setText("确定(" + num + ")");
    }


    @OnClick(R.id.contacts_header_search)
    void onSearchClicked(){
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(ContactsFrequentActivity.SOURCE, 0);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private ArrayList<User> getOnlineContacts() {
        mOnlineFrequentContactList.clear();
        for (User item : mAllFrequentContactList) {
            if (item.nStatus > 0) {
                mOnlineFrequentContactList.add(item);
            }
        }
        return mOnlineFrequentContactList;
    }

    /***********************************************************
     * 描述: 首次进入、切换显示全部时调用
     * 入参:无
     * 出参:无
     * 返回:无
     ************************************************************/
    public void updateContacts(){
        List shownContactList;

        /*如果当前是显示全部联系人，则显示所有联系人，同时界面显示“仅显示在线”
        否则显示在线联系人，同时界面显示“显示全部” */
        if ( AppDatas.Constants().isShowAllContacts()) {
            shownContactList = mAllFrequentContactList;
            getNavigate()
                    .setRightText(getString(R.string.show_online))
                    .setRightTextColor(ContextCompat.getColor(this, R.color.blue_2E67FE));
        } else {
            getOnlineContacts();
            shownContactList = mOnlineFrequentContactList;
            getNavigate()
                    .setRightText(getString(R.string.show_all))
                    .setRightTextColor(ContextCompat.getColor(this, R.color.blue_2E67FE));
        }

        mTagsAdapter.setDatas(shownContactList);
        mTagsAdapter.notifyDataSetChanged();
    }
}
