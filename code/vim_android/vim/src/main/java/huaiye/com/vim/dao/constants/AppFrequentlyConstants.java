package huaiye.com.vim.dao.constants;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.msgs.User;

/**
 * Created by ywt on 2019/2/28.
 */

public class AppFrequentlyConstants {
    private AppFrequentlyConstants() {
    }

    static class Holder {
        static final AppFrequentlyConstants SINGLETON = new AppFrequentlyConstants();
    }

    public static AppFrequentlyConstants get() {
        return AppFrequentlyConstants.Holder.SINGLETON;
    }

    public void clear() {
        AppDatas.DB().deleteQuery(FrequentlyConstantsData.class).delete();
    }

    public List<FrequentlyConstantsData> getConstants() {
        List<FrequentlyConstantsData> list = AppDatas.DB().findQuery(FrequentlyConstantsData.class)
                .orderBy("count", "desc")
                .selectAllAt(0, 20);
        return list;
    }

    private void add(FrequentlyConstantsData data) {
        if(data.userId.equals(String.valueOf(AppDatas.Auth().getUserID()))){
            return;
        }
        FrequentlyConstantsData item = find(data);
        if (item == null) {
            AppDatas.DB().insertQuery(FrequentlyConstantsData.class)
                    .insert(data);
        } else {
            data.count = item.count + 1;
            updateCount(data);
        }
    }

    public FrequentlyConstantsData find(FrequentlyConstantsData data) {
        try {
            return AppDatas.DB().findQuery(FrequentlyConstantsData.class)
                    .addWhereColumn("userId", data.userId)
                    .selectFirst();
        } catch (Exception e) {
            return null;
        }
    }

    private void updateCount(FrequentlyConstantsData data) {
        AppDatas.DB().updateQuery(FrequentlyConstantsData.class)
                .addWhereColumn("userId", data.userId)
                .addUpdateColumn("count", data.count)
                .update();
    }



    public void AddContacts(ArrayList<User> list){
        if(list == null || list.size() <= 0){
            return;
        }
        for(User item : list){
            FrequentlyConstantsData data = new FrequentlyConstantsData();
            data.userId = item.strUserID;
            data.count = 1;
            add(data);
        }
    }
}
