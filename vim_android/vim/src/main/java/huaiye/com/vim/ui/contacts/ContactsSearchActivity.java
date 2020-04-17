package huaiye.com.vim.ui.contacts;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import java.util.ArrayList;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.views.CheckableLinearLayout;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.bean.CommonContacts;
import huaiye.com.vim.models.contacts.bean.ContactData;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContacts;
import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;
import ttyy.com.recyclerexts.tags.TagsAdapter;

/**
 * author: admin
 * date: 2017/12/29
 * version: 0
 * mail: secret
 * desc: GroupContactsSearchActivity
 */
@BindLayout(R.layout.activity_contacts_search)
public class ContactsSearchActivity extends AppBaseActivity {

    @BindView(R.id.iv_empty_view)
    View iv_empty_view;
    @BindView(R.id.rcv_list)
    RecyclerView rcv_list;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.ll_choose_persons)
    View ll_choose_persons;
    @BindView(R.id.rct_choosed)
    RecyclerView rct_choosed;
    @BindView(R.id.tv_choose_confirm)
    TextView tv_choose_confirm;

    ArrayList<ContactData> stricts = new ArrayList<>();
    TagsAdapter<CommonContacts.Data> adapter;
    EXTRecyclerAdapter<ContactData> mChoosedAdapter;

    @BindExtra
    boolean isSelectUser;
    @BindExtra
    boolean needAddSelf;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(getString(R.string.seach_with_emptyh))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        ll_choose_persons.setVisibility(isSelectUser ? View.VISIBLE : View.GONE);

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    refreshDatas();
                    return true;
                }
                return false;
            }
        });

        rcv_list.setLayoutManager(new LinearLayoutManager(this));
        rct_choosed.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapter = new TagsAdapter<CommonContacts.Data>(R.layout.item_contacts_person) {
            {
                if (isSelectUser) {
                    setChoiceMode(Mode.MultiChoice);
                }
            }

            @Override
            public EXTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if (viewType == HEADER
                        || viewType == FOOTER) {

                    return super.onCreateViewHolder(parent, viewType);
                }

                final EXTViewHolder holder = super.onCreateViewHolder(parent, viewType);
                if (getMode() == Mode.None) {
                    holder.getItemView().findViewById(R.id.iv_choice).setVisibility(View.GONE);
                } else {
                    holder.getItemView().findViewById(R.id.iv_choice).setVisibility(View.VISIBLE);
                    CheckableLinearLayout root = (CheckableLinearLayout) holder.getItemView();
                    root.setOnCheckedListener(new CheckableLinearLayout.OnCheckedChangedListener() {
                        @Override
                        public void onCheckedChanged(View parent, boolean isChecked) {
                            if (isChecked) {
                                holder.setImageResouce(R.id.iv_choice, R.drawable.ic_choice_checked);
                            } else {
                                holder.setImageResouce(R.id.iv_choice, R.drawable.ic_choice);
                            }
                        }
                    });
                }
                return holder;
            }

            @Override
            public void onBindTagViewHolder(EXTViewHolder extViewHolder, int i, CommonContacts.Data data) {
                if (i < getHeaderViewsCount()) {
                    return;
                }

                if (i - getHeaderViewsCount() == adapter.getDatasCount() - 1) {
                    extViewHolder.setVisibility(R.id.view_divider, View.GONE);
                } else {
                    extViewHolder.setVisibility(R.id.view_divider, View.VISIBLE);
                }
                extViewHolder.setVisibility(R.id.tv_title, View.GONE);

                extViewHolder.setText(R.id.tv_user_name, data.name);
                if (data.isOnline()) {
                    extViewHolder.setImageResouce(R.id.tv_user_status, R.drawable.dian_zaixian);
                } else {
                    extViewHolder.setImageResouce(R.id.tv_user_status, R.drawable.dian_lixian);
                }
//                extViewHolder.setImageResouce(R.id.tv_user_status, R.drawable.dian_mang);

            }
        };
        adapter.setOnItemClickListener(new EXTRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int i) {

                if (adapter.getDataForItemPosition(i) == null) {
                    return;
                }

                if (adapter.getMode() != TagsAdapter.Mode.None) {
                    ContactData data = ContactData.from(adapter.getDataForItemPosition(i));
                    if (adapter.isItemChecked(i)) {
                        mChoosedAdapter.getDatas().add(data);
                        ChoosedContacts.get().addTemp(data);
                    } else {
                        mChoosedAdapter.getDatas().remove(data);
                        ChoosedContacts.get().delTemp(data);
                    }

                    mChoosedAdapter.notifyDataSetChanged();
                    changeNum(mChoosedAdapter.getDatas().size());
                    return;
                }

                if (!isSelectUser) {
                    Intent intent = new Intent(ContactsSearchActivity.this, ContactDetailActivity.class);
                    CommonContacts.Data data = adapter.getDataForItemPosition(i);
                    intent.putExtra("nId", data.id);
                    intent.putExtra("nContactEntCode", data.entCode);
                    startActivity(intent);
                }
            }
        });
        rcv_list.setAdapter(adapter);


        mChoosedAdapter = new EXTRecyclerAdapter<ContactData>(R.layout.item_contacts_person_choosed) {
            @Override
            public void onBindViewHolder(EXTViewHolder extViewHolder, int i, ContactData contactData) {
                extViewHolder.setText(R.id.tv_contact_name, contactData.name);
            }
        };
        mChoosedAdapter.setOnItemClickListener(new EXTRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int i) {
                String loginName = mChoosedAdapter.getDatas().get(i).loginName;
                ChoosedContacts.get().delTemp(mChoosedAdapter.getDatas().get(i));
                mChoosedAdapter.getDatas().remove(i);
                mChoosedAdapter.notifyDataSetChanged();
                changeNum(mChoosedAdapter.getDatas().size());
                for (int c : adapter.getSelectedPositions()) {
                    if (loginName.equals(adapter.getDataForItemPosition(c).loginName)) {
                        adapter.setItemChecked(c, false);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });
        mChoosedAdapter.setDatas(stricts);
        rct_choosed.setAdapter(mChoosedAdapter);
        showSelected(null);
    }

    private void changeNum(int num) {
        tv_choose_confirm.setText(getString(R.string.makesure) + "(" + num + ")");
    }

    void refreshDatas() {

        ModelApis.Contacts().getUserContacts(et_search.getText().toString(),
                AppDatas.Auth().getEnterpriseCode(),
                new ModelCallback<CommonContacts>() {

                    @Override
                    public void onSuccess(CommonContacts commonContacts) {

                        adapter.setDatas(commonContacts.result);
                        adapter.notifyDataSetChanged();

                        if (adapter.getDatasCount() < 1) {
                            iv_empty_view.setVisibility(View.VISIBLE);
                        } else {
                            iv_empty_view.setVisibility(View.GONE);
                        }

                        showSelected(commonContacts.result);
                    }

                });
    }

    private void showSelected(ArrayList<CommonContacts.Data> result) {
        ArrayList<ContactData> chooseds = ChoosedContacts.get().getContactsTemp();

        stricts.clear();
        for (ContactData tmp : chooseds) {
            if (!tmp.naviKey.equals("none")) {
                stricts.add(tmp);
            }
        }
        if (mChoosedAdapter != null) {
            mChoosedAdapter.notifyDataSetChanged();
            changeNum(mChoosedAdapter.getDatas().size());
        }

        if (result != null) {
            adapter.clearChooseStatus();
            for (int i = 0; i < result.size(); i++) {
                CommonContacts.Data data = result.get(i);

                String s = data.loginName + data.domainCode;
                for (ContactData tmp : stricts) {
                    String s1 = tmp.loginName + tmp.domainCode;
                    if (s.equals(s1)) {
                        tmp.naviKey = "common";
                        adapter.setItemChecked(i + adapter.getHeaderViewsCount(), true);
                        break;
                    }
                }
            }
        }

    }

    @OnClick(R.id.tv_choose_confirm)
    void onChoosedConfirmClicked() {
        commitData();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSelectUser)
            showSelected(null);
    }

    private void commitData() {
        ChoosedContacts.get().add(ChoosedContacts.get().getContactsTemp());
        ChoosedContacts.get().clearTemp();
    }

}
