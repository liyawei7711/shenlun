package huaiye.com.vim.models.meet;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import huaiye.com.vim.common.constant.CommonConstant;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.meet.bean.MeetList;
import ttyy.com.jinnetwork.Https;

/**
 * author: admin
 * date: 2018/01/09
 * version: 0
 * mail: secret
 * desc: MeetApi
 */

public class MeetApi {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private MeetApi() {

    }

    static class Holder {
        static final MeetApi SINGLETON = new MeetApi();
    }

    public static MeetApi get() {
        return Holder.SINGLETON;
    }

    /**
     * 获取当前会议
     */
    public void requestCurrentMeets(int index, String key, ModelCallback<MeetList> callback) {
        String URL = AppAuth.get().getSieAddress() + "get_meeting_list";

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -3);
        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.DATE, 7);

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strUserDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strUserID", String.valueOf(AppDatas.Auth().getUserID()))
//                .addParam("strUserID", AppDatas.Auth().getUserID())
                .addParam("strQueryStartTime", sdf.format(c.getTime()))
                .addParam("strQueryEndTime", sdf.format(c2.getTime()))
                /*.addParam("strQueryStartTime", "")
                .addParam("strQueryEndTime", "")*/
                .addParam("strMeetingKeywords", key)
                .addParam("nPage", index)
                .addParam("nSize", CommonConstant.MEET_NUM)
                .addParam("nReverse", 1)
                .addParam("nStatus", 5)
                .setHttpCallback(callback)
                .build()
                .requestAsync();

    }

    /**
     * 获取历史会议
     */
    public void requestHistoryMeets(int index, String key, String start, String end, ModelCallback<MeetList> callback) {

        String URL = AppAuth.get().getSieAddress() + "get_meeting_list";

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -2);

        String startTime = "";// = sdf.format(c.getTime());
        String endTime = "";// = sdf.format(new Date());

        if (!TextUtils.isEmpty(start)) {
            startTime = start + " 00:00:00";
        }
        if (!TextUtils.isEmpty(end)) {
            endTime = end + " 23:59:59";
        }

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strUserDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strUserID", AppDatas.Auth().getUserLoginName())
                .addParam("strQueryStartTime", startTime)
                .addParam("strQueryEndTime", endTime)
                .addParam("strMeetingKeywords", key)
                .addParam("nPage", index)
                .addParam("nSize", 20)
                .addParam("nReverse", 2)
                .addParam("nStatus", 2)
                .setHttpCallback(callback)
                .build()
                .requestAsync();

    }

    public void requesAllMeets(int index, String key, String start, String end, ModelCallback<MeetList> callback) {

        String URL = AppAuth.get().getSieAddress() + "get_meeting_list";

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -3);
        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.DATE, 7);

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strQueryStartTime", sdf.format(c.getTime()))
                .addParam("strQueryEndTime", sdf.format(c2.getTime()))
                .addParam("nPage", index)
                .addParam("strMeetingKeywords", key)
                .addParam("nSize", 20)
                .addParam("nReverse", 2)
                .addParam("nStatus", 1)
                .setHttpCallback(callback)
                .build()
                .requestAsync();

    }

}
