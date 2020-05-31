package huaiye.com.vim.ui.meet;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;

import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.models.contacts.bean.ContactsGroupUserListBean;
import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 */
@BindLayout(R.layout.activity_notice_choose)
public class NoticeChooseActivity extends AppBaseActivity {

    @BindView(R.id.fl_root)
    View fl_root;
    @BindView(R.id.rct_view)
    RecyclerView rct_view;
    @BindView(R.id.navigate_container)
    View navigate_container;
    @BindView(R.id.ll_search)
    View ll_search;
    @BindView(R.id.et_key)
    EditText et_key;

    @BindExtra
    ContactsGroupUserListBean mGroupInfoListBean;
    EXTRecyclerAdapter<ContactsGroupUserListBean.LstGroupUser> adapter;
    RequestOptions requestOptions;

    ArrayList<ContactsGroupUserListBean.LstGroupUser> datas = new ArrayList<>();

    @Override
    protected void initActionBar() {
        EventBus.getDefault().register(this);
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void doInitDelay() {

        requestOptions = new RequestOptions();
        requestOptions.centerCrop()
                .dontAnimate()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.default_image_personal)
                .error(R.drawable.default_image_personal)
                .optionalTransform(new CircleCrop());

        et_key.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Editable s = et_key.getText();
                    if (s != null && s.length() > 0) {
                        serach(s.toString());
                    } else {
                        serach("");
                    }
                    return true;
                }
                return false;
            }
        });
        et_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0) {
                    serach(s.toString());
                } else {
                    serach("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rct_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EXTRecyclerAdapter<ContactsGroupUserListBean.LstGroupUser>(R.layout.item_group_notice_member) {
            @Override
            public void onBindViewHolder(EXTViewHolder extViewHolder, int i, ContactsGroupUserListBean.LstGroupUser contactData) {
                extViewHolder.setText(R.id.tv_user_name, contactData.strUserName);
                ImageView imageView = extViewHolder.itemView.findViewById(R.id.iv_user_head);
                Glide.with(getBaseContext())
                        .load(AppDatas.Constants().getAddressWithoutPort() + contactData.strHeadUrl)
                        .apply(requestOptions)
                        .into(imageView);
            }
        };
        adapter.setOnItemClickListener(new EXTRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int i) {
                ContactsGroupUserListBean.LstGroupUser user = datas.get(i);
                Intent intent = new Intent();
                intent.putExtra("notice_user", (Serializable) user);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        adapter.setDatas(datas);
        rct_view.setAdapter(adapter);
        showAll();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backFinish();
    }

    @OnClick({R.id.view_left, R.id.top_search, R.id.et_search_cancel, R.id.close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_left:
                backFinish();
                break;
            case R.id.top_search:
                navigate_container.setVisibility(View.GONE);
                ll_search.setVisibility(View.VISIBLE);
                break;
            case R.id.et_search_cancel:
                showEnterView();
                break;
            case R.id.close:
                et_key.setText("");
                break;
        }
    }

    private void serach(String str) {
        datas.clear();
        for (ContactsGroupUserListBean.LstGroupUser temp : mGroupInfoListBean.lstGroupUser) {
            if (!temp.strUserName.equals(AppDatas.Auth().getUserName()) &&
                    temp.strUserName.contains(str)) {
                datas.add(temp);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showAll() {
        datas.clear();
        for (ContactsGroupUserListBean.LstGroupUser temp : mGroupInfoListBean.lstGroupUser) {
            if (!temp.strUserName.equals(AppDatas.Auth().getUserName())) {
                datas.add(temp);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showEnterView() {
        showAll();
        navigate_container.setVisibility(View.VISIBLE);
        ll_search.setVisibility(View.GONE);
    }

    private void backFinish() {
        if(navigate_container.getVisibility() == View.GONE) {
            showEnterView();
        } else {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final MessageEvent messageEvent) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
