package huaiye.com.vim.ui.contacts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.models.contacts.bean.ContactData;
import huaiye.com.vim.ui.contacts.sharedata.GroupDeleteChoosedContacts;
import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;

/**
 * author: admin
 * date: 2018/01/20
 * version: 0
 * mail: secret
 * desc: ContactsListOperationActivity
 */
@BindLayout(R.layout.activity_contacts_list_operation)
public class ContactsListOperationActivity extends AppBaseActivity {

    @BindView(R.id.rct_view)
    RecyclerView rct_view;

    EXTRecyclerAdapter<ContactData> adapter;

    boolean isDelete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(getString(R.string.common_notice1))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        rct_view.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper delMoveHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.LEFT);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // 长按 交换时 进行数据位置更换
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                isDelete = true;
                GroupDeleteChoosedContacts.get().del(adapter.getDataForItemPosition(viewHolder.getLayoutPosition()));
                adapter.removeDataForItemPosition(viewHolder.getLayoutPosition());
            }
        });
        delMoveHelper.attachToRecyclerView(rct_view);

        adapter = new EXTRecyclerAdapter<ContactData>(R.layout.item_contacts_person) {
            @Override
            public void onBindViewHolder(EXTViewHolder extViewHolder, int i, ContactData contactData) {

                if (i == adapter.getDatasCount() - 1) {
                    extViewHolder.setVisibility(R.id.view_divider, View.GONE);
                } else {
                    extViewHolder.setVisibility(R.id.view_divider, View.VISIBLE);
                }

                extViewHolder.setVisibility(R.id.iv_choice, View.GONE);
                extViewHolder.setVisibility(R.id.tv_title, View.GONE);
                extViewHolder.setVisibility(R.id.tv_user_status, View.GONE);

                extViewHolder.setText(R.id.tv_user_name, contactData.name);
            }
        };

        adapter.setDatas(GroupDeleteChoosedContacts.get().getContacts());
        rct_view.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if(isDelete)
            setResult(RESULT_OK);
        super.onBackPressed();
    }
}
