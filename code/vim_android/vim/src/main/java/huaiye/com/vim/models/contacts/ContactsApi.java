package huaiye.com.vim.models.contacts;

import android.text.TextUtils;

import com.huaiye.sdk.sdpmsgs.social.SendUserBean;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.constant.CommonConstant;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.models.CommonResult;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.ModelSDKErrorResp;
import huaiye.com.vim.models.contacts.bean.CommonContacts;
import huaiye.com.vim.models.contacts.bean.ContacsTerminal;
import huaiye.com.vim.models.contacts.bean.ContactData;
import huaiye.com.vim.models.contacts.bean.ContactDetail;
import huaiye.com.vim.models.contacts.bean.ContactGroup;
import huaiye.com.vim.models.contacts.bean.ContactOrganizationBean;
import huaiye.com.vim.models.contacts.bean.ContactsBean;
import huaiye.com.vim.models.contacts.bean.ContactsGroupChatListBean;
import huaiye.com.vim.models.contacts.bean.ContactsGroupUserListBean;
import huaiye.com.vim.models.contacts.bean.CreateGroupContactData;
import huaiye.com.vim.models.contacts.bean.CustomResponse;
import huaiye.com.vim.models.contacts.bean.DomainInfoList;
import huaiye.com.vim.models.contacts.bean.GroupContacts;
import huaiye.com.vim.models.contacts.bean.OrganizationContacts;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContacts;
import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;
import ttyy.com.jinnetwork.core.work.method_post.HTTPRequestPostBuilder;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: ContactsModel
 */

public class ContactsApi {

    ContactOrganizationBean mCachedOrganization;

    private ContactsApi() {

    }

    public static ContactsApi get() {
        return new ContactsApi();
    }

    /**
     * 获取通讯录
     */
    public void requestContacts(int page, long nDepID, ModelCallback<ContactsBean> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/get_user_list";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("nPage", page)
                .addParam("nSize", CommonConstant.MEET_NUM)
                .addParam("nOrderByID", 0)
                .addParam("nAscOrDesc", 0)
                .addParam("nDepID", nDepID)
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(callback)
                .build()
                .requestAsync();

    }

    /**
     * 获取好友列表
     */
    public void requestBuddyContacts(int page, long nDepID, int nRouteType, ModelCallback<ContactsBean> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/get_user_buddy_list";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("nPage", page)
                .addParam("nSize", CommonConstant.MEET_NUM)
                .addParam("nOrderByID", 0)
                .addParam("nAscOrDesc", 0)
                .addParam("nDepID", nDepID)
                .addParam("strUserID", AppDatas.Auth().getUserID())
                .addParam("nRouteType", nRouteType)
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .addHeader("strKeywords", "")
                .setHttpCallback(callback)
                .build()
                .requestAsync();

    }


    /**
     * 获取域列表
     */
    public void requestGetDomainInfo(ModelCallback<DomainInfoList> callback) {
        String URL = AppDatas.Constants().getChatBaseURL() + "vss/httpjson/get_domain_info";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strDomainCode", "")
                .setHttpCallback(callback)
                .build()
                .requestNowAsync();
    }

    /**
     * 获取群聊列表
     */
    public void requestGroupBuddyContacts(int page, long nDepID, int nRouteType, String strKeywords, String strGroupDomainCode, ModelCallback<ContactsGroupChatListBean> callback) {
        String URL = AppDatas.Constants().getChatBaseURL() + "vim/httpjson/query_group_chat_list";
        String mstrKeywords = "";

        if (strKeywords != null) {
            mstrKeywords = strKeywords;
        }

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strUserDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("nPage", page)
                .addParam("nSize", CommonConstant.MEET_NUM)
                .addParam("nOrderByID", 0)
                .addParam("nAscOrDesc", 0)
                .addParam("nDepID", nDepID)
                .addParam("strUserID", AppDatas.Auth().getUserID())
                .addParam("nRouteType", nRouteType)
                .addParam("strGroupDomainCode", strGroupDomainCode)
                .addParam("strKeywords", mstrKeywords)
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())

                .setHttpCallback(callback)
                .build()
                .requestAsync();

    }


    /**
     * 创建群
     */
    public void requestCreateGroupChat(String strGroupName, ArrayList<User> lstGroupUser, ModelCallback<CreateGroupContactData> callback) {
        ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
        if (null != lstGroupUser && lstGroupUser.size() > 0) {
            for (User user : lstGroupUser) {
                if (!user.strUserID.equals(AppDatas.Auth().getUserID())) {//建群的时候 不用加入自己
                    sessionUserList.add(new SendUserBean(user.strUserID, user.strDomainCode, user.strUserName));
                }
            }
        }

        if(sessionUserList.size()<=0){
            AppBaseActivity.showToast(AppUtils.getString(R.string.string_name_create_group_tip));
            return;
        }

        String URL = AppDatas.Constants().getVimAddressBaseURL() + "httpjson/create_group_chat";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strCreaterDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strCreaterID", AppDatas.Auth().getUserID())
                .addParam("strGroupDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strGroupName", strGroupName)
                .addParam("strAnnouncement", "")
                .addParam("nBeinviteMode", 0)
                .addParam("nInviteMode", 1)
                .addParam("nTeamMemberLimit", 200)
                .addParam("lstGroupUser", sessionUserList)
                .addParam("strUserDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strUserID", AppDatas.Auth().getUserID())
                .addParam("strUserName", AppDatas.Auth().getUserName())
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .addHeader("strKeywords", "")
                .addHeader("Connection", "close")
                .setHttpCallback(callback)
                .build()
                .requestAsync();

    }

    /**
     * 获取当前群组成员
     */
    public void requestqueryGroupChatInfo(String strGroupDomainCode, String strGroupID, ModelCallback<ContactsGroupUserListBean> callback) {
        String URL = AppDatas.Constants().getVimAddressBaseURL() + "httpjson/query_group_chat_info";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strGroupDomainCode", strGroupDomainCode)
                .addParam("strGroupID", strGroupID)
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .addHeader("strKeywords", "")
                .addHeader("Connection", "close")
                .setHttpCallback(callback)
                .build()
                .requestAsync();

    }


    /**
     * 搜索联系人
     */
    public void searchContacts(int page, String key, ModelCallback<ContactsBean> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/get_user_list";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("nPage", page)
                .addParam("nSize", CommonConstant.MEET_NUM)
                .addParam("nOrderByID", 0)
                .addParam("nAscOrDesc", 0)
                .addParam("strKeywords", key)
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(callback)
                .build()
                .requestAsync();

    }

    /**
     * 获取指定联系人信息
     */
    public void requestSpecificContacts(List<String> userList, ModelCallback<ContactsBean> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/get_user_info";
        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("userList", userList)
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(callback)
                .build()
                .requestAsync();

    }

    /**
     * 获取指定联系人信息
     */
    public void requestUserInfoList(String strDomainCode, List<String> userList, ModelCallback<ContactsBean> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/get_user_info";
        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strDomainCode", strDomainCode)
                .addParam("userList", userList)
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(callback)
                .build()
                .requestAsync();

    }

    /**
     * 更新用户头像
     */
    public void requestModUserHead(String strHeadUrl, ModelCallback<CustomResponse> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/mod_user_head";
        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strUserID", AppDatas.Auth().getUserID())
                .addParam("strHeadUrl", strHeadUrl)
                .addParam("nRouteType", 0)

                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(callback)
                .build()
                .requestNowAsync();

    }

    /**
     * 群组加人
     */
    public void requestInviteUserJoinGroupChat(String strGroupDomainCode, String strGroupID, String strGroupName, ArrayList<User> lstGroupUser, ModelCallback<CustomResponse> callback) {
        ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
        if (null != lstGroupUser && lstGroupUser.size() > 0) {
            for (User user : lstGroupUser) {
                sessionUserList.add(new SendUserBean(user.strUserID, user.strDomainCode, user.strUserName));
            }
        }
        String URL = AppDatas.Constants().getVimAddressBaseURL() + "httpjson/invite_user_join_group_chat";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strInviterDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strInviterID", AppDatas.Auth().getUserID())
                .addParam("strInviterName", AppDatas.Auth().getUserName())
                .addParam("strGroupDomainCode", strGroupDomainCode)
                .addParam("strGroupID", strGroupID)
                .addParam("strGroupName", strGroupName)
                .addParam("nBeinviteMode", 0)//强制加群
                .addParam("lstGroupUser", sessionUserList)

                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .addHeader("strKeywords", "")
                .addHeader("Connection", "close")
                .setHttpCallback(callback)
                .build()
                .requestAsync();

    }

    /**
     * 踢出群
     */
    public void requestKickoutGroupUser(String strGroupDomainCode, String strGroupID, ArrayList<User> lstGroupUser, ModelCallback<CustomResponse> callback) {
        ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
        if (null != lstGroupUser && lstGroupUser.size() > 0) {
            for (User user : lstGroupUser) {
                sessionUserList.add(new SendUserBean(user.strUserID, user.strDomainCode, user.strUserName));
            }
        }
        String URL = AppDatas.Constants().getVimAddressBaseURL() + "httpjson/kickout_group_user";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strKickerDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strKickerID", AppDatas.Auth().getUserID())
                .addParam("strKickerName", AppDatas.Auth().getUserName())
                .addParam("strGroupDomainCode", strGroupDomainCode)
                .addParam("strGroupID", strGroupID)
                .addParam("lstOutUser", sessionUserList)

                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .addHeader("strKeywords", "")
                .addHeader("Connection", "close")
                .setHttpCallback(callback)
                .build()
                .requestAsync();

    }

    /**
     * 删除群组
     */
    public void requestDelGroupChat(String strGroupDomainCode, String strGroupID, ModelCallback<CustomResponse> callback) {

        String URL = AppDatas.Constants().getVimAddressBaseURL() + "httpjson/del_group_chat";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strUserDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strUserID", AppDatas.Auth().getUserID())
                .addParam("strGroupDomainCode", strGroupDomainCode)
                .addParam("strGroupID", strGroupID)

                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .addHeader("strKeywords", "")
                .addHeader("Connection", "close")
                .setHttpCallback(callback)
                .build()
                .requestAsync();

    }

    /**
     * 退群
     */
    public void requestUserLeaveGroupChat(String strGroupDomainCode, String strGroupID, ModelCallback<CustomResponse> callback) {

        String URL = AppDatas.Constants().getVimAddressBaseURL() + "httpjson/user_leave_group_chat";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strUserDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strUserID", AppDatas.Auth().getUserID())
                .addParam("strUserName", AppDatas.Auth().getUserName())
                .addParam("strGroupDomainCode", strGroupDomainCode)
                .addParam("strGroupID", strGroupID)

                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .addHeader("strKeywords", "")
                .addHeader("Connection", "close")
                .setHttpCallback(callback)
                .build()
                .requestAsync();

    }


    /**
     * 获取好友头像
     */
    public void requestGetUserHead(List<ContactsGroupUserListBean.LstGroupUser> userList, ModelCallback<ContactsBean> callback){
        String URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/get_user_head";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("userList", userList)
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(callback)
                .build()
                .requestAsync();
    }

    /**
     * 更新群信息
     */
    public void requestModGroupChat(String strGroupDomainCode, String strGroupID, String strGroupName, String strAnnouncement, String strHeadUrl, ModelCallback<CustomResponse> callback) {

        String URL = AppDatas.Constants().getVimAddressBaseURL() + "httpjson/mod_group_chat";

        HTTPRequestPostBuilder mHTTPRequestPostBuilder = Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strUserDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strUserID", AppDatas.Auth().getUserID())
                .addParam("strGroupDomainCode", strGroupDomainCode)
                .addParam("strGroupID", strGroupID)
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .addHeader("strKeywords", "")
                .addHeader("Connection", "close")
                .setHttpCallback(callback);
        if (!TextUtils.isEmpty(strGroupName)) {
            mHTTPRequestPostBuilder.addParam("strGroupName", strGroupName);
        }
        if (!TextUtils.isEmpty(strAnnouncement)) {
            mHTTPRequestPostBuilder.addParam("strAnnouncement", strAnnouncement);
        }
        if (!TextUtils.isEmpty(strHeadUrl)) {
            mHTTPRequestPostBuilder.addParam("strHeadUrl", strHeadUrl);
        }


        mHTTPRequestPostBuilder.build().requestAsync();

    }


    /**
     * 群消息提醒设置
     */
    public void requestSetGroupChatMsgMode(String strGroupDomainCode, String strGroupID, int nMsgTop, int nNoDisturb, ModelCallback<CustomResponse> callback) {

        String URL = AppDatas.Constants().getVimAddressBaseURL() + "httpjson/set_group_chat_msg_mode";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strUserDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strUserID", AppDatas.Auth().getUserID())
                .addParam("strUserName", AppDatas.Auth().getUserName())
                .addParam("strGroupDomainCode", strGroupDomainCode)
                .addParam("strGroupID", strGroupID)
                .addParam("nMsgTop", nMsgTop)
                .addParam("nNoDisturb", nNoDisturb)

                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .addHeader("strKeywords", "")
                .addHeader("Connection", "close")
                .setHttpCallback(callback)
                .build()
                .requestAsync();

    }


    /**
     * 获取组织架构
     *
     * @param callback
     */
    public void requestOrganization(String eywords, final ModelCallback<ContactOrganizationBean> callback) {
        if (mCachedOrganization != null) {
            callback.onSuccess(mCachedOrganization);
            return;
        }

//        String URL = AppDatas.Constants().getAddressBaseURL() + "busidataexchange/getBusiData.action";
        String URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/get_department_info";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("nOrderByID", 0)
                .addParam("nAscOrDesc", 1)
                .addParam("strKeywords", eywords)//可选
                /*.addParam("methodName", "getEntContacts")
                .addParam("entCode", entCode)
                .addParam("userId", AppDatas.Auth().getUserID())*/
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(new ModelCallback<ContactOrganizationBean>() {
                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                        super.onPreStart(httpRequest);
                        if (callback != null) {
                            callback.onPreStart(httpRequest);
                        }
                    }

                    @Override
                    public void onSuccess(ContactOrganizationBean contactOrganization) {
                        if (contactOrganization.nResultCode == 0) {
                            mCachedOrganization = contactOrganization;
                            if (callback != null) {
                                callback.onSuccess(contactOrganization);
                            }
                        } else {
                            mCachedOrganization = null;
                            if (callback != null) {
                                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(contactOrganization.strResultDescribe));
                            }
                        }
                    }
                })
                .build()
                .requestAsync();

    }

    /**
     * 获取会议终端
     */
    public void requestTerminal(String key, ModelCallback<ContacsTerminal> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/get_conference_client_list";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("nAscOrDesc", 0)
                .addParam("strKeywords", key)//可选
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(callback)
                .build()
                .requestAsync();
    }

    /**
     * 获取常用联系人
     */
    public void requestCommonContacts(ModelCallback<CommonContacts> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "busidataexchange/getBusiData.action";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("methodName", "getCommonContents")
                .addParam("userId", AppDatas.Auth().getUserID())
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(callback)
                .build()
                .requestAsync();

    }

    /**
     * 添加常用联系人
     */
    public void addCommonContact(String account, final ModelCallback<Boolean> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "busidataexchange/postBusiData.action";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("methodName", "addCommonContents")
                .addParam("entCode", AppDatas.Auth().getEnterpriseCode()) // 被添加方单位编码
                .addParam("loginName", account) // 被添加方loginName
                .addParam("userId", AppDatas.Auth().getUserID()) // 登录人id, 该参数非必须
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(new ModelCallback<CommonResult>() {
                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                        super.onPreStart(httpRequest);
                        if (callback != null) {
                            callback.onPreStart(httpRequest);
                        }
                    }

                    @Override
                    public void onSuccess(CommonResult o) {
                        if (callback != null) {
                            callback.onSuccess(true);
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        if (callback != null) {
                            callback.onFailure(httpResponse);
                        }
                    }
                })
                .build()
                .requestAsync();
    }

    /**
     * 删除常用联系人
     */
    public void delCommonContact(String account, long groupId, long entCode, final ModelCallback<Boolean> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "busidataexchange/postBusiData.action";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("methodName", "deleteCommonContents")
                .addParam("entCode", entCode) // 被删除方单位编码
                .addParam("loginName", account) // 联系人loginName
                .addParam("groupId", groupId) // 群组id
                .addParam("groupLoginName", AppDatas.Auth().getAccount())
                .addParam("userId", AppDatas.Auth().getUserID())
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(new ModelCallback<CommonResult>() {
                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                        super.onPreStart(httpRequest);
                        if (callback != null) {
                            callback.onPreStart(httpRequest);
                        }
                    }

                    @Override
                    public void onSuccess(CommonResult o) {
                        if (o.code == 0) {
                            if (callback != null) {
                                callback.onSuccess(true);
                            }
                        } else {
                            onFailure(new ModelSDKErrorResp().setErrorMessage(o.desc));
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        if (callback != null) {
                            callback.onFailure(httpResponse);
                        }
                    }
                })
                .build()
                .requestAsync();
    }

    /**
     * 获取组织人员
     *
     * @param callback
     */
    public void requestOrganizationContacts(long entCode, long deptId, final ModelCallback<OrganizationContacts> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "busidataexchange/getBusiData.action";

        if (deptId == -1) {
            Https.post(URL)
                    .addHeader("Connection", "close")
                    .addParam("methodName", "getUserContacts")
                    .addParam("entCode", entCode)
                    .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                    .setHttpCallback(new ModelCallback<OrganizationContacts>() {
                        @Override
                        public void onPreStart(HTTPRequest httpRequest) {
                            super.onPreStart(httpRequest);
                            if (callback != null) {
                                callback.onPreStart(httpRequest);
                            }
                        }

                        @Override
                        public void onSuccess(OrganizationContacts organizationContacts) {
                            dealHideData1(organizationContacts);


                            if (callback != null) {
                                callback.onSuccess(organizationContacts);
                            }
                        }

                        @Override
                        public void onFailure(HTTPResponse httpResponse) {
                            super.onFailure(httpResponse);
                            if (callback != null) {
                                callback.onFailure(httpResponse);
                            }
                        }
                    })
                    .build()
                    .requestAsync();
        } else {
            Https.post(URL)
                    .addHeader("Connection", "close")
                    .addParam("methodName", "getUserContacts")
                    .addParam("entCode", entCode)
                    .addParam("deptId", deptId)
                    .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                    .setHttpCallback(new ModelCallback<OrganizationContacts>() {
                        @Override
                        public void onPreStart(HTTPRequest httpRequest) {
                            super.onPreStart(httpRequest);
                            if (callback != null) {
                                callback.onPreStart(httpRequest);
                            }
                        }

                        @Override
                        public void onSuccess(OrganizationContacts organizationContacts) {
                            dealHideData1(organizationContacts);

//                            if (!isShowSelf) {
//                                // 移除自己
//                                long selfId = AppDatas.Auth().getUserID();
//                                String selfDomain = AppDatas.Auth().getDomainCode();
//                                for (OrganizationContacts.Data tmp : organizationContacts.result) {
//                                    if (tmp.id == selfId
//                                            && tmp.domainCode.equals(selfDomain)) {
//                                        organizationContacts.result.remove(tmp);
//                                        break;
//                                    }
//                                }
//                            }

                            if (callback != null) {
                                callback.onSuccess(organizationContacts);
                            }
                        }

                        @Override
                        public void onFailure(HTTPResponse httpResponse) {
                            super.onFailure(httpResponse);
                            if (callback != null) {
                                callback.onFailure(httpResponse);
                            }
                        }
                    })
                    .build()
                    .requestAsync();
        }
    }

    /**
     * 获取人员详情
     *
     * @param contactId
     * @param callback
     */
    public void requestContactDetail(long contactId, long entCode, ModelCallback<ContactDetail> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "busidataexchange/getBusiData.action";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("methodName", "getUserDetailInfo")
                .addParam("id", contactId)
                .addParam("entCode", entCode)
                .addParam("userId", AppDatas.Auth().getUserID())
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(callback)
                .build()
                .requestAsync();
    }

    /**
     * 获取群组列表
     *
     * @param callback
     */
    public void requestGroupList(ModelCallback<ContactGroup> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "syscontactgrouplist/list.action";
        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("memberId", AppDatas.Auth().getUserID())
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(callback)
                .build()
                .requestAsync();
    }

    /**
     * 创建群组
     */
    public void createGroup(String groupName, ArrayList<ContactData> contacts, final ModelCallback<Boolean> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "sysconfcontactgroup/addContactGroup.action";

        contacts = new ArrayList<>(contacts);

        ContactData self = new ContactData();
        self.loginName = AppDatas.Auth().getAccount();
        self.domainCode = AppDatas.Auth().getDomainCode();
        self.entCode = AppDatas.Auth().getEnterpriseCode();
        self.userId = AppDatas.Auth().getUserID();
        self.naviKey = "122";
        self.name = "self";

        if (!contacts.contains(self)) {
            contacts.add(self);
        }

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("groupName", groupName)
                .addParam("userId", AppDatas.Auth().getUserID())
                .addParam("entCode", AppDatas.Auth().getEnterpriseCode())
                .addParam("list", contacts)
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(new ModelCallback<CommonResult>() {
                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                        super.onPreStart(httpRequest);
                        if (callback != null) {
                            callback.onPreStart(httpRequest);
                        }
                    }

                    @Override
                    public void onSuccess(CommonResult result) {
                        if (result.code == 0) {
                            if (callback != null) {
                                callback.onSuccess(true);
                            }
                        } else {
                            onFailure(new ModelSDKErrorResp().setErrorMessage(result.desc));
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        if (callback != null) {
                            callback.onFailure(httpResponse);
                        }
                    }
                })
                .build()
                .requestAsync();
    }

    /**
     *
     */
    public void requestGroupContacts(long groupId, ModelCallback<GroupContacts> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "maillist/userList.action";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("groupId", groupId)
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(callback)
                .build()
                .requestAsync();
    }

    /**
     * 退出群组
     */
    public void quitGroup(long groupId, final ModelCallback<Boolean> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "sysconfcontact/delete.action";
        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("groupId", groupId)
                .addParam("userId", AppDatas.Auth().getUserID())
                .addParam("entCode", AppDatas.Auth().getEnterpriseCode())
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(new ModelCallback<CommonResult>() {
                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                        super.onPreStart(httpRequest);
                        if (callback != null) {
                            callback.onPreStart(httpRequest);
                        }
                    }

                    @Override
                    public void onSuccess(CommonResult result) {
                        if (result.code == 0) {
                            if (callback != null) {
                                callback.onSuccess(true);
                            }
                        } else {
                            onFailure(new ModelSDKErrorResp().setErrorMessage(result.desc));
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        if (callback != null) {
                            callback.onFailure(httpResponse);
                        }
                    }
                })
                .build()
                .requestAsync();
    }

    /**
     * 删除群组
     */
    public void delGroup(long groupId, final ModelCallback<Boolean> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "sysconfcontactgroup/deleteContactGroup.action";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("id", groupId)
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(new ModelCallback<CommonResult>() {
                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                        super.onPreStart(httpRequest);
                        if (callback != null) {
                            callback.onPreStart(httpRequest);
                        }
                    }

                    @Override
                    public void onSuccess(CommonResult result) {
                        if (result.code == 0) {
                            if (callback != null) {
                                callback.onSuccess(true);
                            }
                        } else {
                            onFailure(new ModelSDKErrorResp().setErrorMessage(result.desc));
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        if (callback != null) {
                            callback.onFailure(httpResponse);
                        }
                    }
                })
                .build()
                .requestAsync();
    }

    /**
     * 更新群组信息
     */
    public void updateGroupInfo(long groupId, ArrayList<ContactData> contacts, final ModelCallback<Boolean> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "sysconfcontactgroup/modifyContactGroup.action";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("id", groupId)
                .addParam("list", contacts)
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(new ModelCallback<CommonResult>() {

                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                        super.onPreStart(httpRequest);
                        if (callback != null) {
                            callback.onPreStart(httpRequest);
                        }
                    }

                    @Override
                    public void onSuccess(CommonResult result) {
                        if (result.code == 0) {
                            if (callback != null) {
                                callback.onSuccess(true);
                            }
                        } else {
                            onFailure(new ModelSDKErrorResp().setErrorMessage(result.desc));
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        if (callback != null) {
                            callback.onFailure(httpResponse);
                        }
                    }
                })
                .build()
                .requestAsync();
    }

    /**
     * 查询人员
     */
    public void getUserContacts(String key, long entcode, final ModelCallback<CommonContacts> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "busidataexchange/getBusiData.action";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("methodName", "getUserContacts")
                .addParam("name", key)
                .addParam("entCode", entcode)
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(new ModelCallback<CommonContacts>() {
                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                        super.onPreStart(httpRequest);
                        if (callback != null) {
                            callback.onPreStart(httpRequest);
                        }
                    }

                    @Override
                    public void onSuccess(CommonContacts organizationContacts) {
                        dealHideData(organizationContacts);
//                        if (!isShowSelf) {
//                            // 移除自己
//                            long selfId = AppDatas.Auth().getUserID();
//                            String selfDomain = AppDatas.Auth().getDomainCode();
//                            for (CommonContacts.Data tmp : organizationContacts.result) {
//                                if (tmp.id == selfId
//                                        && tmp.domainCode.equals(selfDomain)) {
//                                    organizationContacts.result.remove(tmp);
//                                    break;
//                                }
//                            }
//                        }

                        if (callback != null) {
                            callback.onSuccess(organizationContacts);
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        if (callback != null) {
                            callback.onFailure(httpResponse);
                        }
                    }
                })
                .build()
                .requestAsync();
    }

    private void dealHideData(CommonContacts organizationContacts) {
        ArrayList<CommonContacts.Data> allUser = new ArrayList<>();
        for (CommonContacts.Data temp : organizationContacts.result) {
            if (!ChoosedContacts.get().contact(temp.loginName)) {
                if (ChoosedContacts.get().isOnLine) {
                    if (temp.isOnline()) allUser.add(temp);
                } else {
                    allUser.add(temp);
                }
            }
        }
        organizationContacts.result.clear();
        organizationContacts.result.addAll(allUser);
    }

    private void dealHideData1(OrganizationContacts organizationContacts) {
        ArrayList<OrganizationContacts.Data> allUser = new ArrayList<>();
        for (OrganizationContacts.Data temp : organizationContacts.result) {
            if (!ChoosedContacts.get().contact(temp.loginName)) {
                if (ChoosedContacts.get().isOnLine) {
                    if (temp.isOnline()) allUser.add(temp);
                } else {
                    allUser.add(temp);
                }
            }
        }
        organizationContacts.result.clear();
        organizationContacts.result.addAll(allUser);
    }

    /**
     * 会议终端登录
     */
    public void requestTerminalLogin(String strClientID, ModelCallback<ContacsTerminal> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/conference_client_login";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strClientID", strClientID)
                .addParam("strIP", AppDatas.Constants().getAddressIP())
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(callback)
                .build()
                .requestAsync();
    }

    /**
     * 会议终端添加
     */
    public void requestAddTerminal(String strClientID, String strClientName, ModelCallback<ContacsTerminal> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/add_conference_client";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strClientID", strClientID)
                .addParam("strClientName", strClientName)
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(callback)
                .build()
                .requestAsync();
    }

    /**
     * 会议终端登录
     */
    public void requestDeleteTerminal(ArrayList<String> clientList, ModelCallback<ContacsTerminal> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/del_conference_client_list";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("clientList", clientList)
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(callback)
                .build()
                .requestAsync();
    }



}
