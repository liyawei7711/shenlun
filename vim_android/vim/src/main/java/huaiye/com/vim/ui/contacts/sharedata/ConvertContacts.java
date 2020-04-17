package huaiye.com.vim.ui.contacts.sharedata;

import android.text.TextUtils;

import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CSetPredetermineMeetingReq;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;

import java.util.ArrayList;

import huaiye.com.vim.dao.msgs.User;

/**
 * Created by ywt on 2019/3/4.
 */

public class ConvertContacts {

    public static ArrayList<User> ConvertMeetUserInfoToContacts(ArrayList<CGetMeetingInfoRsp.UserInfo> data) {
        ArrayList<User> list = new ArrayList<User>();
        for (CGetMeetingInfoRsp.UserInfo item : data) {
            User user = new User();
            user.strUserID = item.strUserID;
            user.strUserName = item.strUserName;
            user.strDomainCode = item.strUserDomainCode;
            user.strUserTokenID = item.strUserTokenID;
            user.nJoinStatus = item.nJoinStatus;
            list.add(user);
        }
        return list;
    }

    public static ArrayList<CStartMeetingReq.UserInfo> ConvertContactsToMeetUserInfo(ArrayList<User> datas) {
        ArrayList<CStartMeetingReq.UserInfo> list = new ArrayList<CStartMeetingReq.UserInfo>();
        for (User item : datas) {
            CStartMeetingReq.UserInfo user = new CStartMeetingReq.UserInfo();
            user.strUserID = item.strUserID;
            user.strUserName = item.strUserName;
            user.strUserDomainCode = TextUtils.isEmpty(item.strUserDomainCode) ? item.strDomainCode : item.strUserDomainCode;
            user.nDevType = item.deviceType;
            list.add(user);
        }
        return list;
    }

    public static ArrayList<CSetPredetermineMeetingReq.UserInfo> ConvertMeetingUserToPredetermineMeetMeetUser(ArrayList<CGetMeetingInfoRsp.UserInfo> datas) {
        ArrayList<CSetPredetermineMeetingReq.UserInfo> list = new ArrayList<>();
        for (CGetMeetingInfoRsp.UserInfo item : datas) {
            CSetPredetermineMeetingReq.UserInfo user = new CSetPredetermineMeetingReq.UserInfo();
            user.strUserID = item.strUserID;
            user.strUserName = item.strUserName;
            user.strUserDomainCode = item.strUserDomainCode;
            user.nDevType = item.nDevType == 3 ? 2 : 1;
            list.add(user);
        }
        return list;
    }
}
