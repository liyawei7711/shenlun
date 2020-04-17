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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import huaiye.com.vim.R;
import huaiye.com.vim.common.recycle.LiteBaseAdapter;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.dao.msgs.VimMessageListBean;
import huaiye.com.vim.dao.msgs.VimMessageListMessages;
import huaiye.com.vim.models.contacts.bean.CreateGroupContactData;
import huaiye.com.vim.ui.chat.holder.ChatListViewHolder;
import huaiye.com.vim.ui.meet.ChatGroupActivityNew;
import huaiye.com.vim.ui.meet.ChatSingleActivity;

/**
 * Created by Administrator on 2018\3\14 0014.
 */

public class SearchChatActivity extends AppCompatActivity {
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
    @BindView(R.id.iv_empty_view)
    View iv_empty_view;

    LiteBaseAdapter<VimMessageListBean> adapter;
    ArrayList<VimMessageListBean> datas = new ArrayList<>();
    Map<String, VimMessageListBean> maps = new HashMap<>();
    String mSearchKey;

    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_meetings_search);
        ButterKnife.bind(this);
        initListener();
        initData();
    }

    private void initData() {
        adapter = new LiteBaseAdapter<>(this,
                datas,
                ChatListViewHolder.class,
                R.layout.item_chat_list_view,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dealAdapterItemClick(v);
                    }
                }, "false");
        rct_view.setLayoutManager(new LinearLayoutManager(this));
        rct_view.setAdapter(adapter);

    }

    private void dealAdapterItemClick(View v) {
        VimMessageListBean bean = (VimMessageListBean) v.getTag();
        bean.isRead = 1;
        Intent intent;
        if (bean.groupType == 1) {
            intent = new Intent(this, ChatGroupActivityNew.class);
            CreateGroupContactData contactsBean = new CreateGroupContactData();
            contactsBean.strGroupDomainCode = bean.groupDomainCode;
            contactsBean.strGroupID = bean.groupID;
            contactsBean.sessionName = bean.sessionName;
            intent.putExtra("mContactsBean", contactsBean);
        } else {
            intent = new Intent(this, ChatSingleActivity.class);
            intent.putExtra("mOtherUserName", bean.sessionName);
            User nUser = new User();
            if (!bean.sessionUserList.get(0).strUserID.equals(AppAuth.get().getUserID())) {
                intent.putExtra("mOtherUserId", bean.sessionUserList.get(0).strUserID);
                nUser.strUserName = bean.sessionUserList.get(0).strUserName;
                nUser.strUserID = bean.sessionUserList.get(0).strUserID;
                nUser.strUserDomainCode = bean.sessionUserList.get(0).strUserDomainCode;
                nUser.strDomainCode = bean.sessionUserList.get(0).strUserDomainCode;
            } else {
                intent.putExtra("mOtherUserId", bean.sessionUserList.get(1).strUserID);
                nUser.strUserName = bean.sessionUserList.get(1).strUserName;
                nUser.strUserID = bean.sessionUserList.get(1).strUserID;
                nUser.strUserDomainCode = bean.sessionUserList.get(1).strUserDomainCode;
                nUser.strDomainCode = bean.sessionUserList.get(1).strUserDomainCode;

            }

            intent.putExtra("nUser", nUser);
            intent.putExtra("sessionUserList", bean.sessionUserList);
            intent.putExtra("mOtherUserDomainCode", nUser.strUserDomainCode);
        }
        VimMessageListMessages.get().isRead(bean);

        startActivity(intent);
        adapter.notifyDataSetChanged();
    }

    private void initListener() {
        et_search_cancel.setOnClickListener(mOnClickListener);
        close.setOnClickListener(mOnClickListener);
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
    }

    private void refreshDatas(String key) {
        mSearchKey = key;
        maps.clear();
        for (VimMessageListBean temp : datas) {
            maps.put(temp.sessionID, temp);
        }
        new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal<List<VimMessageListBean>>() {
            @Override
            public List<VimMessageListBean> doOnThread() {
                List<VimMessageListBean> allBean = VimMessageListMessages.get().getMessages();
                for (VimMessageListBean vimMessageListBean : allBean) {
                    if (vimMessageListBean.groupType == 1) {
                        vimMessageListBean.strHeadUrl = AppDatas.MsgDB().getGroupListDao().getGroupHeadPic(vimMessageListBean.groupID, vimMessageListBean.groupDomainCode);
                    } else {
                        ArrayList<SendUserBean> messageUsers = vimMessageListBean.sessionUserList;
                        if (messageUsers != null && messageUsers.size() > 0) {
                            if (messageUsers.size() == 2) {
                                Logger.err("receive single chat list not 2 is " + messageUsers.size());
                                SendUserBean friend = null;
                                for (SendUserBean sendUserBean : messageUsers) {
                                    if (!sendUserBean.strUserID.equals(AppDatas.Auth().getUserID())) {
                                        friend = sendUserBean;
                                        break;
                                    }
                                }
                                vimMessageListBean.strHeadUrl = AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(friend.strUserID, friend.strUserDomainCode);
                            }


                        }

                    }
                }
                return allBean;
            }

            @Override
            public void doOnMain(List<VimMessageListBean> data) {
                for (VimMessageListBean temp : data) {
                    if (maps.containsKey(temp.sessionID)) {
                        VimMessageListBean tempLin = maps.get(temp.sessionID);
                        if (tempLin.mStrEncrypt.equals(temp.msgTxt)) {
                            temp.isUnEncrypt = tempLin.isUnEncrypt;
                            temp.msgTxt = tempLin.msgTxt;
                        }
                    }
                }

                ArrayList<VimMessageListBean> tempDatas = new ArrayList<>();
                for (VimMessageListBean temp : data) {
                    if (temp.sessionName.contains(mSearchKey)) {
                        tempDatas.add(temp);
                    }
                }
                datas.clear();
                datas.addAll(tempDatas);
                adapter.notifyDataSetChanged();
                showEmpty();

                VimMessageListMessages.get().getMessagesUnReadNum();
            }
        });
    }

    private void showEmpty() {
        VimMessageListMessages.get().getMessagesUnReadNum();
        if (datas.size() > 0) {
            rct_view.setVisibility(View.VISIBLE);
            iv_empty_view.setVisibility(View.GONE);
        } else {
            rct_view.setVisibility(View.GONE);
            iv_empty_view.setVisibility(View.VISIBLE);
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
