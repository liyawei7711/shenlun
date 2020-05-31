package huaiye.com.vim.dao.msgs;

import huaiye.com.vim.dao.AppDatas;

/**
 * author: admin
 * date: 2018/01/09
 * version: 0
 * mail: secret
 * desc: AppMessages
 */

public class BroadcastManage {

    private BroadcastManage() {

    }

    static class Holder {
        static final BroadcastManage SINGLETON = new BroadcastManage();
    }

    public static BroadcastManage get() {
        return Holder.SINGLETON;
    }

    public void clear() {
        AppDatas.DB().deleteQuery(BroadcastMessage.class).delete();
    }

    public BroadcastMessage getMessages(String path) {
        BroadcastMessage message = AppDatas.DB().findQuery(BroadcastMessage.class)
                .addWhereColumn("userId", AppDatas.Auth().getUserID())
                .addWhereColumn("domainCode", AppDatas.Auth().getDomainCode())
                .addWhereColumn("down_path", path)
                .selectFirst();

        return message;
    }

    public void add(BroadcastMessage data) {
        AppDatas.DB().insertQuery(BroadcastMessage.class)
                .insert(data);
    }

    public void delAll() {
        AppDatas.DB().deleteQuery(BroadcastMessage.class)
                .addWhereColumn("userId", AppDatas.Auth().getUserID())
                .addWhereColumn("domainCode", AppDatas.Auth().getDomainCode())
                .delete();
    }

    public void updateSuccess(String path,int state) {
        AppDatas.DB().updateQuery(BroadcastMessage.class)
                .addWhereColumn("userId", AppDatas.Auth().getUserID())
                .addWhereColumn("domainCode", AppDatas.Auth().getDomainCode())
                .addWhereColumn("down_path", path)
                .addUpdateColumn("state", state)
                .update();
    }

    public void del(String path) {
        AppDatas.DB().deleteQuery(BroadcastMessage.class)
                .addWhereColumn("down_path", path)
                .delete();
    }

}
