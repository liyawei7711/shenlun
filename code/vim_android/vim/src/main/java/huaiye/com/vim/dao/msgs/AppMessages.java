package huaiye.com.vim.dao.msgs;

import java.util.List;

import huaiye.com.vim.dao.AppDatas;

/**
 * author: admin
 * date: 2018/01/09
 * version: 0
 * mail: secret
 * desc: AppMessages
 */

public class AppMessages {

    private AppMessages() {

    }

    static class Holder {
        static final AppMessages SINGLETON = new AppMessages();
    }

    public static AppMessages get() {
        return Holder.SINGLETON;
    }

    public void clear() {
        AppDatas.DB().deleteQuery(MessageData.class).delete();
    }

    public List<MessageData> getMessages() {
        List<MessageData> list = AppDatas.DB().findQuery(MessageData.class)
                .addWhereColumn("userId", AppDatas.Auth().getUserID())
                .addWhereColumn("domainCode", AppDatas.Auth().getDomainCode())
                .orderBy("nMillions", "desc")
                .selectAll();

        return list;
    }

    public void isReadAll() {
        AppDatas.DB().updateQuery(MessageData.class)
                .addWhereColumn("userId", AppDatas.Auth().getUserID())
                .addWhereColumn("domainCode", AppDatas.Auth().getDomainCode())
                .addUpdateColumn("isRead", 1)
                .update();
    }

    public int getUnReadMessageNumber() {
        try {
            return AppDatas.DB().findQuery(MessageData.class)
                    .addWhereColumn("userId", AppDatas.Auth().getUserID())
                    .addWhereColumn("domainCode", AppDatas.Auth().getDomainCode())
                    .addWhereColumn("isRead", 0)
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    public void add(MessageData data) {
        if (data.getMessageType() == MessageData.AUTH_KICKOUT
                || data.getMessageType() == MessageData.MEET_KICKOUT
                || data.getMessageType() == MessageData.MEET_SPEAKER_CONTROL
                || data.getMessageType() == MessageData.TALK_SPEAKER_CONTROL) {
            // 暂时忽略这类消息插入到本地数据库
            return;
        }
        com.huaiye.sdk.logger.Logger.debug("AppMessages add ");
        AppDatas.DB().insertQuery(MessageData.class)
                .insert(data);
    }

    public void del(MessageData data) {
        if (data == null) {
            clear();
        } else {
            del(data.nMillions);
        }
    }

    public void del(long key) {
        AppDatas.DB().deleteQuery(MessageData.class)
                .addWhereColumn("nMillions", key)
                .delete();
    }

    public void read(long nMillions) {
        com.huaiye.sdk.logger.Logger.debug("AppMessages read " + nMillions);
        AppDatas.DB().updateQuery(MessageData.class)
                .addWhereColumn("userId", AppDatas.Auth().getUserID())
                .addWhereColumn("domainCode", AppDatas.Auth().getDomainCode())
                .addWhereColumn("nMillions", nMillions)
                .addUpdateColumn("isRead", 1)
                .update();
    }

    public void del(String key) {
        AppDatas.DB().deleteQuery(MessageData.class)
                .addWhereColumn("key", key)
                .delete();
    }

}
