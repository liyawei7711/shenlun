package huaiye.com.vim.dao.msgs;

import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vim.bus.NewMessageNum;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.dao.AppDatas;

/**
 * author: admin
 * date: 2018/01/09
 * version: 0
 * mail: secret
 * desc: AppMessages
 */
public class VimMessageListMessages {
    private VimMessageListMessages() {

    }

    static class Holder {
        static final VimMessageListMessages SINGLETON = new VimMessageListMessages();
    }

    public static VimMessageListMessages get() {
        return Holder.SINGLETON;
    }

    public void clear() {
        AppDatas.DB().deleteQuery(VimMessageListBean.class).delete();
    }

    public List<VimMessageListBean> getMessages() {
        List<VimMessageListBean> list = new ArrayList<>();
        List<VimMessageListBean> list1 = AppDatas.DB().findQuery(VimMessageListBean.class)
                .addWhereColumn("ownerId", AppDatas.Auth().getUserID() + "")
                .addWhereColumn("ownerDomain", AppDatas.Auth().getDomainCode())
                .addWhereColumn("nMsgTop", 1)
                .orderBy("nMsgToptime", "desc")
                .selectAll();
        List<VimMessageListBean> list2 = AppDatas.DB().findQuery(VimMessageListBean.class)
                .addWhereColumn("ownerId", AppDatas.Auth().getUserID() + "")
                .addWhereColumn("ownerDomain", AppDatas.Auth().getDomainCode())
                .addWhereColumn("nMsgTop!", 1)
                .orderBy("time", "desc")
                .selectAll();
        if (null != list1 && list1.size() > 0) {
            list.addAll(list1);
        }
        if (null != list2 && list2.size() > 0) {
            list.addAll(list2);
        }
        return list;
    }

    public VimMessageListBean getMessages(String sessionID) {
        VimMessageListBean bean = AppDatas.DB().findQuery(VimMessageListBean.class)
                .addWhereColumn("ownerId", AppDatas.Auth().getUserID() + "")
                .addWhereColumn("ownerDomain", AppDatas.Auth().getDomainCode())
                .addWhereColumn("sessionID", sessionID)
                .orderBy("time", "desc")
                .selectFirst();

        return bean;
    }

    public boolean getMessagesUnRead() {
        try {
            List<VimMessageListBean> list = AppDatas.DB().findQuery(VimMessageListBean.class)
                    .addWhereColumn("ownerId", AppDatas.Auth().getUserID() + "")
                    .addWhereColumn("ownerDomain", AppDatas.Auth().getDomainCode())
                    .addWhereColumn("isRead", 0)
                    .selectAll();
            if (list == null) {
                return false;
            }
            if (list.size() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

    }

    public void getMessagesUnReadNum() {
        new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal<Integer>() {
            @Override
            public Integer doOnThread() {
                List<VimMessageListBean> data = VimMessageListMessages.get().getMessages();
                int total = 0;
                for (VimMessageListBean bean : data) {
                    if (bean.groupType == 1) {
                        total += AppDatas.MsgDB()
                                .chatGroupMsgDao()
                                .getGroupUnreadNum(bean.groupID);
                    } else {
                        total += AppDatas.MsgDB()
                                .chatSingleMsgDao()
                                .getUnreadNum(bean.sessionID);
                    }

                }
                return total;
            }

            @Override
            public void doOnMain(Integer num) {
                EventBus.getDefault().post(new NewMessageNum(num));
            }
        });
    }

    public int getMessagesUnReadNum(String sessionID) {
        try {
            List<VimMessageListBean> list = AppDatas.DB().findQuery(VimMessageListBean.class)
                    .addWhereColumn("ownerId", AppDatas.Auth().getUserID() + "")
                    .addWhereColumn("ownerDomain", AppDatas.Auth().getDomainCode())
                    .addWhereColumn("sessionID", sessionID)
                    .addWhereColumn("isRead", 0)
                    .selectAll();
            if (list == null) {
                return 0;
            }
            return list.size();
        } catch (Exception e) {
            return 0;
        }

    }

    public void isRead(VimMessageListBean bean) {
        AppDatas.DB().updateQuery(VimMessageListBean.class)
                .addWhereColumn("sessionID", bean.sessionID)
                .addWhereColumn("ownerId", AppDatas.Auth().getUserID())
                .addWhereColumn("ownerDomain", AppDatas.Auth().getDomainCode())
                .addUpdateColumn("isRead", 1)
                .update();
    }
    public void isRead(String sessionID) {
        AppDatas.DB().updateQuery(VimMessageListBean.class)
                .addWhereColumn("sessionID", sessionID)
                .addWhereColumn("ownerId", AppDatas.Auth().getUserID())
                .addWhereColumn("ownerDomain", AppDatas.Auth().getDomainCode())
                .addUpdateColumn("isRead", 1)
                .update();

        AppDatas.MsgDB()
                .chatGroupMsgDao()
                .updateSessionIDRead(sessionID);
        AppDatas.MsgDB()
                .chatSingleMsgDao()
                .updateReadMsgID(sessionID);
    }

    public void updateMsgTop(String sessionID, int nMsgTop) {
        AppDatas.DB().updateQuery(VimMessageListBean.class)
                .addWhereColumn("sessionID", sessionID)
                .addWhereColumn("ownerId", AppDatas.Auth().getUserID())
                .addWhereColumn("ownerDomain", AppDatas.Auth().getDomainCode())
                .addUpdateColumn("nMsgTop", nMsgTop)
                .addUpdateColumn("nMsgToptime", System.currentTimeMillis())
                .update();
    }

    public void updateNoDisturb(String sessionID, int nNoDisturb) {
        AppDatas.DB().updateQuery(VimMessageListBean.class)
                .addWhereColumn("sessionID", sessionID)
                .addWhereColumn("ownerId", AppDatas.Auth().getUserID())
                .addWhereColumn("ownerDomain", AppDatas.Auth().getDomainCode())
                .addUpdateColumn("nNoDisturb", nNoDisturb)
                .update();
    }

    public VimMessageListBean getMessagesSimple(String sessionID) {
        VimMessageListBean bean = AppDatas.DB().findQuery(VimMessageListBean.class)
                .addWhereColumn("sessionID", sessionID)
                .orderBy("time", "desc")
                .selectFirst();

        return bean;
    }

    public void updateGroupName(String sessionID, String groupName) {
        AppDatas.DB().updateQuery(VimMessageListBean.class)
                .addWhereColumn("sessionID", sessionID)
                .addUpdateColumn("sessionName", groupName)
                .update();
    }

    public void updateGroupNameByGroupID(String groupID, String groupName) {
        if (!TextUtils.isEmpty(groupName)) {
            AppDatas.DB().updateQuery(VimMessageListBean.class)
                    .addWhereColumn("groupID", groupID)
                    .addUpdateColumn("sessionName", groupName)
                    .update();
        }

    }

    public void add(VimMessageListBean data) {
        if (data == null) return;
        if (TextUtils.isEmpty(data.sessionID)) return;
        data.ownerId = AppDatas.Auth().getUserID() + "";
        data.ownerDomain = AppDatas.Auth().getDomainCode();

        AppDatas.DB().insertQuery(VimMessageListBean.class)
                .insert(data);
    }

    public void del(VimMessageListBean data) {
        if (data == null) {
            clear();
        } else {
            del(data.sessionID);
        }
    }

    public void del(String key) {
        AppDatas.DB().deleteQuery(VimMessageListBean.class)
                .addWhereColumn("sessionID", key)
                .delete();
    }

    public void clearMessage(String key) {
        AppDatas.DB().updateQuery(VimMessageListBean.class)
                .addWhereColumn("sessionID", key)
                .addUpdateColumn("type", -1)
                .update();
    }


}
