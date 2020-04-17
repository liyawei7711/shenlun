package huaiye.com.vim.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huaiye.com.vim.BuildConfig;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.bus.ReafBean;
import huaiye.com.vim.bus.RefMessageList;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppBaseFragment;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.dialog.LogicDialog;
import huaiye.com.vim.common.recycle.LiteBaseAdapter;
import huaiye.com.vim.common.recycle.RecycleTouchUtils;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.BroadcastManage;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.dao.msgs.VimMessageBean;
import huaiye.com.vim.dao.msgs.VimMessageListBean;
import huaiye.com.vim.dao.msgs.VimMessageListMessages;
import huaiye.com.vim.models.contacts.bean.ContactsGroupUserListBean;
import huaiye.com.vim.models.contacts.bean.CreateGroupContactData;
import huaiye.com.vim.push.MessageNotify;
import huaiye.com.vim.push.MessageReceiver;
import huaiye.com.vim.ui.auth.SettingAddressSafeActivity;
import huaiye.com.vim.ui.chat.holder.ChatListViewHolder;
import huaiye.com.vim.ui.meet.ChatGroupActivityNew;
import huaiye.com.vim.ui.meet.ChatSingleActivity;

import static android.support.v7.widget.helper.ItemTouchHelper.Callback.makeMovementFlags;
import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: FragmentMessages
 */
@BindLayout(R.layout.fragment_messages)
public class FragmentMessages extends AppBaseFragment implements MessageNotify {

    @BindView(R.id.message_list)
    RecyclerView message_list;
    @BindView(R.id.ll_empty)
    View ll_empty;
    @BindView(R.id.fl_search)
    View fl_search;


    ArrayList<VimMessageListBean> datas = new ArrayList<>();
    Map<String, VimMessageListBean> maps = new HashMap<>();
    LiteBaseAdapter<VimMessageListBean> adapter;
    LiteBaseAdapter<VimMessageListBean> adapterSearch;
    private boolean isSOS;

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dimissChatMoreStylePopupWindow();
        EventBus.getDefault().register(this);
        MessageReceiver.get().subscribe(this);

        getNavigate().setVisibility(View.GONE);

        new RecycleTouchUtils().initTouch(new RecycleTouchUtils.ITouchEvent() {

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.LEFT);
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final LogicDialog logicDialog = new LogicDialog(getContext());
                logicDialog.setCancelable(false);
                logicDialog.setCanceledOnTouchOutside(false);
                logicDialog.setMessageText(getString(R.string.common_notice5));
                logicDialog.setConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VimMessageListBean data = datas.get(viewHolder.getAdapterPosition());
                        VimMessageListMessages.get().del(data.sessionID);
                        if (data.groupType == 1) {
                            AppDatas.MsgDB()
                                    .chatGroupMsgDao()
                                    .deleteBySessionID(data.sessionID);
                        } else {
                            AppDatas.MsgDB()
                                    .chatSingleMsgDao()
                                    .deleteBySessionID(data.sessionID);
                        }
                        if (data.sessionID.equals("0")) {
                            BroadcastManage.get().delAll();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    File file = new File(AppUtils.audiovideoPath);
                                    if (file.exists()) {
                                        File[] files = file.listFiles();
                                        for (File f : files) {
                                            AppUtils.delFile(f);
                                        }
                                        file.delete();
                                    }
                                }
                            }).start();
                        }
                        datas.remove(viewHolder.getAdapterPosition());
                        adapter.notifyDataSetChanged();

                        showEmpty();
                    }
                }).setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.notifyDataSetChanged();
                    }
                }).show();
            }

        }).attachToRecyclerView(message_list);

        adapter = new LiteBaseAdapter<>(getContext(),
                datas,
                ChatListViewHolder.class,
                R.layout.item_chat_list_view,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dealAdapterItemClick(v);
                    }
                }, "false");

        adapterSearch = new LiteBaseAdapter<>(getContext(),
                datas,
                ChatListViewHolder.class,
                R.layout.item_chat_list_view,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dealAdapterItemClick(v);
                    }
                }, "false");
        message_list.setLayoutManager(new LinearLayoutManager(getContext()));
        message_list.setAdapter(adapter);
    }

    public void onClickSearch() {
        if(isSOS) {
            return;
        }
        Log.d(this.getClass().getName(), "onClick");
        Intent intent = new Intent(getContext(), SearchChatActivity.class);
        startActivity(intent);
    }

    private void searchHuiHua(String str) {

    }

    private void dealAdapterItemClick(View v) {
        if(!HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            AppBaseActivity.showToast(getString(R.string.jiami_notice7));
            return;
        }
        VimMessageListBean bean = (VimMessageListBean) v.getTag();
        bean.isRead = 1;
        Intent intent;
        if (bean.groupType == 1) {
            intent = new Intent(getActivity(), ChatGroupActivityNew.class);
            CreateGroupContactData contactsBean = new CreateGroupContactData();
            contactsBean.strGroupDomainCode = bean.groupDomainCode;
            contactsBean.strGroupID = bean.groupID;
            contactsBean.sessionName = bean.sessionName;
            intent.putExtra("mContactsBean", contactsBean);
        } else {
            intent = new Intent(getActivity(), ChatSingleActivity.class);
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
        adapter.notifyItemChanged(datas.indexOf(bean));
    }

    public void refMessage() {
        if (isSOS) {
            showEmpty();
            return;
        }
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
                datas.clear();
                datas.addAll(data);
                adapter.notifyDataSetChanged();
                refNum();
                showEmpty();
            }
        });

    }

    /**
     * 个人头像变更
     *
     * @param user
     */
    private void refreshCurrentUserData(User user) {
        new RxUtils<ArrayList<VimMessageListBean>>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal<ArrayList<VimMessageListBean>>() {
            @Override
            public ArrayList<VimMessageListBean> doOnThread() {
                if (null != datas && datas.size() > 0 && null != user) {
                    for (VimMessageListBean vimMessageListBean : datas) {
                        if (vimMessageListBean.groupType != 1) {
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
                                    if (friend.strUserID.equals(user.strUserID) && friend.strUserDomainCode.equals(user.strUserDomainCode)) {
                                        vimMessageListBean.strHeadUrl = AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(friend.strUserID, friend.strUserDomainCode);
                                        break;
                                    }
                                }


                            }
                        }
                    }

                }
                return datas;
            }

            @Override
            public void doOnMain(ArrayList<VimMessageListBean> data) {

                if (null != adapter) {
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    public void refNum() {
        int count = 0;
        for (VimMessageListBean temp : datas) {
            if (temp.isRead == 0) {
                count++;
            }
        }
        ((MainActivity) getActivity()).changeRedCircle(count);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            refMessage();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ReafBean obj) {
        //接到群组的人员变化后刷新界面
        refMessage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(VimMessageBean obj) {
        //接到群组的人员变化后刷新界面
        refMessage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(RefMessageList obj) {
        refMessage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ContactsGroupUserListBean groupUserListBean) {
        for (VimMessageListBean temp : datas) {
            if (temp.sessionID.equals(groupUserListBean.strGroupDomainCode + groupUserListBean.strGroupID)) {
                refMessage();
                break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final MessageEvent messageEvent) {
        if (null == messageEvent) {
            return;
        }

        switch (messageEvent.what) {
            case AppUtils.EVENT_MESSAGE_MODIFY_HEAD_PIC:
                User user = (User) messageEvent.obj1;
                refreshCurrentUserData(user);
                break;
            default:
                break;
        }
    }

    private void showEmpty() {
        VimMessageListMessages.get().getMessagesUnReadNum();
        if (datas.size() > 0) {
            message_list.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            message_list.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        MessageReceiver.get().unSubscribe(this);
    }

    public void isRead() {
        AppDatas.Messages().isReadAll();
        ((MainActivity) getActivity()).resetMessageNumbers();
    }

    public void setSos(boolean isSOS) {
        this.isSOS = isSOS;
    }
}
