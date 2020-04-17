package huaiye.com.vim.ui.setting;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import java.util.ArrayList;
import java.util.Collection;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.JinJiLianXiRenBean;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.ui.contacts.ContactsAddOrDelActivity;
import huaiye.com.vim.ui.meet.adapter.UserDetailUserListAdapter;
import huaiye.com.vim.ui.setting.adapter.SetLianXiRenListAdapter;

/**
 * author: zhangzhen
 * date: 2019/7/23
 * version: 0
 * desc: UserDetailActivity
 */
@BindLayout(R.layout.activity_user_jinji)
public class SetLianXiRenActivity extends AppBaseActivity implements SetLianXiRenListAdapter.OnItemClickListener {

    @BindView(R.id.ll_root)
    View ll_root;
    @BindView(R.id.rv_data)
    RecyclerView rv_data;

    ArrayList<User> mUserList = new ArrayList<>();

    private SetLianXiRenListAdapter adapter;
    private boolean isDeling;
    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.VISIBLE);
        getNavigate().setTitlText(getString(R.string.common_notice47))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                })
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeOther();

                        if (AppDatas.MsgDB()
                                .getJinJiLianXiRenDao().queryOneItem(AppAuth.get().getUserID(), AppAuth.get().getDomainCode()) != null) {
                            AppDatas.MsgDB()
                                    .getJinJiLianXiRenDao()
                                    .updateData(AppAuth.get().getUserID(), AppAuth.get().getDomainCode(), new Gson().toJson(mUserList), AppAuth.get().getUserLoginName());
                        } else {
                            AppDatas.MsgDB()
                                    .getJinJiLianXiRenDao()
                                    .insertAll(new JinJiLianXiRenBean(AppAuth.get().getUserID(), AppAuth.get().getDomainCode(), mUserList).setExtend4(AppAuth.get().getUserLoginName()));
                        }
                        finish();
                    }
                });

        getNavigate().getRightTextView().setPadding(AppUtils.dp2px(this, 8f), AppUtils.dp2px(this, 4f), AppUtils.dp2px(this, 8f), AppUtils.dp2px(this, 4f));
        getNavigate().getRightTextView().setBackgroundResource(R.drawable.shape_choosed_confirm);
        getNavigate().setRightText(AppUtils.getString(R.string.makesure));
    }

    @Override
    public void doInitDelay() {
        initdata();
        initView();
    }

    private void initView() {
        adapter = new SetLianXiRenListAdapter(this);
        adapter.setOnItemClickListener(this);
        rv_data.setLayoutManager(new GridLayoutManager(this, 5));
        rv_data.setAdapter(adapter);
        adapter.setData(mUserList);

        ll_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isDeling) {
                    return;
                }
                isDeling = false;
                for (User temp : mUserList) {
                    temp.canDel = false;
                }
                initAdd();
            }
        });
    }

    private void initdata() {
        mUserList.clear();
        if (AppDatas.MsgDB()
                .getJinJiLianXiRenDao().queryOneItem(AppAuth.get().getUserID(), AppAuth.get().getDomainCode()) != null) {
            mUserList.addAll(AppDatas.MsgDB()
                    .getJinJiLianXiRenDao().queryOneItem(AppAuth.get().getUserID(), AppAuth.get().getDomainCode()).getUserRel());
        }
        initAdd();

    }

    private void initAdd() {
        int userNum = mUserList.size();
        if (userNum < 2) {
            User add = new User();
            add.strUserID = UserDetailUserListAdapter.TYPE_ADD;
            mUserList.add(add);
        }

        if (userNum > 0) {
            User add = new User();
            add.strUserID = UserDetailUserListAdapter.TYPE_DEL;
            mUserList.add(add);
        }

        if (null != adapter) {
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1000) {
            mUserList.clear();
            for (User temp : (Collection<? extends User>) data.getSerializableExtra("users")) {
                if (!temp.strUserID.equals(AppAuth.get().getUserID())) {
                    mUserList.add(temp);
                }
            }
            initAdd();
        }
    }

    @Override
    public void onItemClick(User item) {
        if (UserDetailUserListAdapter.TYPE_ADD.equals(item.strUserID)) {
            Intent intent = new Intent(getSelf(), ContactsAddOrDelActivity.class);
            intent.putExtra("titleName", getString(R.string.common_notice47));
            intent.putExtra("isSelectUser", true);
            intent.putExtra("isJinJiMore", true);
            intent.putExtra("max", 2);

            removeOther();
            intent.putExtra("mUserList", mUserList);
            startActivityForResult(intent, 1000);
        } else if (UserDetailUserListAdapter.TYPE_DEL.equals(item.strUserID)) {
            isDeling = true;
            removeOther();
            for (User temp : mUserList) {
                temp.canDel = true;
            }
            adapter.notifyDataSetChanged();
        } else if(isDeling){
            mUserList.remove(item);
            if(mUserList.isEmpty()) {
                for (User temp : mUserList) {
                    temp.canDel = false;
                }
                initAdd();
                return;
            }
            adapter.notifyDataSetChanged();
        }

    }

    private void removeOther() {
        int addIndex = -1;
        int delIndex = -1;
        int i = 0;
        for(User temp : mUserList) {
            if(temp.strUserID == UserDetailUserListAdapter.TYPE_DEL) {
                delIndex = i;
            }
            if(temp.strUserID == UserDetailUserListAdapter.TYPE_ADD) {
                addIndex = i;
            }
            i++;
        }
        if(delIndex != -1) {
            mUserList.remove(delIndex);
        }
        if(addIndex != -1) {
            mUserList.remove(addIndex);
        }
    }

    private ArrayList<User> getCanKickoutUser(ArrayList<User> mUserList) {
        ArrayList<User> userList = new ArrayList<>();
        if (null != mUserList && mUserList.size() > 0) {
            for (User item : mUserList) {
                if (!item.strUserID.equals(AppDatas.Auth().getUserID())) {
                    userList.add(item);
                }
            }
        }
        return userList;
    }

}
