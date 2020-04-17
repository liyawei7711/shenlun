package huaiye.com.vim.ui.contacts;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.social.CQueryUserListRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.models.contacts.bean.ContactData;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContacts;
import huaiye.com.vim.ui.meet.views.MeetCreateHeaderView;
import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;

/**
 * author: admin
 * date: 2018/01/16
 * version: 0
 * mail: secret
 * desc: GroupCreateActivity
 */
@BindLayout(R.layout.activity_group_create)
public class GroupCreateActivity extends AppBaseActivity {

    @BindView(R.id.rct_view)
    RecyclerView rct_view;

    MeetCreateHeaderView header;
    EXTRecyclerAdapter<ContactData> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(getString(R.string.group_notice12))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                })
                .setRightText(getString(R.string.group_notice13))
                .setRightTextColor(Color.RED)
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        header.createGroup(GroupCreateActivity.this);
                    }
                });
    }

    @Override
    public void doInitDelay() {
        adapter = new EXTRecyclerAdapter<ContactData>(R.layout.item_meetcreate_member) {
            @Override
            public void onBindViewHolder(EXTViewHolder extViewHolder, int i, ContactData contactData) {
                if (i < getHeaderViewsCount()) {
                    return;
                }

                extViewHolder.setText(R.id.tv_user_name, contactData.name);
                if (map.containsKey(contactData.loginName)) {
                    if (map.get(contactData.loginName).nState == 2 ||
                            map.get(contactData.loginName).nState == 3 ||
                            map.get(contactData.loginName).nState == 4) {
                        extViewHolder.setImageResouce(R.id.tv_user_status, R.drawable.dian_mang);
                    } else {
                        extViewHolder.setImageResouce(R.id.tv_user_status, R.drawable.dian_zaixian);
                    }
                } else {
                    extViewHolder.setImageResouce(R.id.tv_user_status, R.drawable.dian_lixian);
                }
            }
        };

        header = new MeetCreateHeaderView(this, false, false);
        rct_view.setAdapter(adapter);
        adapter.addHeaderView(header);
        rct_view.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            requestOnLine(false);
        }
    }

    @Override
    protected void afterOnLineUser(boolean value) {
        super.afterOnLineUser(value);
        adapter.setDatas(ChoosedContacts.get().getContacts(false));
        adapter.notifyDataSetChanged();
    }

}
