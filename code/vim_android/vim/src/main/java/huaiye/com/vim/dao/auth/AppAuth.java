package huaiye.com.vim.dao.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.sdkabi._api.ApiAuth;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

import huaiye.com.vim.BuildConfig;
import huaiye.com.vim.VIMApp;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.common.constant.SPConstant;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.msgs.FileLocalNameBean;
import huaiye.com.vim.dao.msgs.VimMessageBean;
import huaiye.com.vim.dao.msgs.VimMessageListMessages;
import huaiye.com.vim.models.auth.bean.AuthUser;
import huaiye.com.vim.models.contacts.bean.MenuListRole;
import huaiye.com.vim.push.MessageReceiver;
import huaiye.com.vim.ui.auth.StartActivity;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContacts;
import huaiye.com.vim.ui.contacts.sharedata.VimChoosedContacts;
import huaiye.com.vim.ui.home.MainActivity;

import static huaiye.com.vim.ui.home.FragmentSettings.deleteDir;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: AppAuth
 */

public class AppAuth {

    private AppAuth() {

    }

    public String getSieAddress() {
        if (TextUtils.isEmpty(get(SPConstant.SIE_IP)) || TextUtils.isEmpty(get(SPConstant.SIE_HTTP_PORT)))
            return "http://" + AppDatas.Constants().getAddressIP() + ":9200" + "/sie/httpjson/";
        return "http://" + get(SPConstant.SIE_IP) + ":" + get(SPConstant.SIE_HTTP_PORT) + "/sie/httpjson/";
        /*if (TextUtils.isEmpty(get("sieHttpUrl")))
            return "http://" + AppDatas.Constants().getAddressIP() + ":9200" + "/sie/httpjson/";
        return get("sieHttpUrl");*/
    }

    public void setData(String key, String strUserTokenID) {
        put(key, strUserTokenID);
    }

    public String getData(String key) {
        return get(key);
    }


    static class Holder {
        static final AppAuth SINGLETON = new AppAuth();
    }

    public static AppAuth get() {
        return Holder.SINGLETON;
    }


    public void setHeaderTokenID(String tokenId) {
        put("tokenId", tokenId);
    }

    public void setAuthUser(AuthUser user) {

        // 缓存
        put("Account", user.strUserName);
        put("Password", user.password);
//        put("entCode", user.result.entCode + "");
        put(SPConstant.USER_ID, user.strUserID);
        put(SPConstant.USER_NAME, user.strUserName);
        put(SPConstant.LOGIN_NAME, user.loginName);
//        put("schedulingServerCode", user.result.schedulingServerCode);
        put(SPConstant.DOMAIN_CODE, user.strDomainCode);
//        put("sieHttpUrl", user.result.sieHttpUrl);
        put(SPConstant.SIE_IP, user.strSieIP);
        put(SPConstant.SIE_PORT, user.nSiePort);
        put(SPConstant.SIE_HTTP_PORT, String.valueOf(user.nSieHttpPort));
        put(SPConstant.X_TOKEN, String.valueOf(user.strToken));
        put(SPConstant.STR_ROLE_ID,user.nRoleID);
        put(SPConstant.STR_NROLE_TYPE,user.nRoleType);

        ChoosedContacts.get().changeSelf();

    }

    public void put(String key, String code) {
        SP.putString(key, code);
    }

    public void put(String key, int value) {
        SP.putInt(key, value);
    }

    private String get(String key) {
        return SP.getString(key);
    }

    public String getHeadUrl(String key) {
        return SP.getString(key);
    }

    public int getRoleID() {
        return SP.getInteger(SPConstant.STR_ROLE_ID);
    }

    /**
     * 获取能否开启群功能的菜单权限
     * @return
     */
    public boolean getCreateGroupChatRole(){
        boolean canCreateGroupChatRole = false;
        MenuListRole menuListRole=VIMApp.getInstance().getMenuListRole();
        if(null!=menuListRole&&null!=menuListRole.menuList&&menuListRole.menuList.size()>0){
            for(MenuListRole.Menu menu:menuListRole.menuList){
                if(menu.nMenuType== MenuListRole.MenuType.MENU_TYPE_CREATE_GROUP_CHAT.ordinal()){
                    canCreateGroupChatRole = true;
                }
            }
        }
        return canCreateGroupChatRole;
    }
    /**
     * 获取能否开启会议功能的菜单权限
     * @return
     */
    public boolean getCreateMeetRole(){
        boolean canCreateGroupChatRole = false;
        MenuListRole menuListRole=VIMApp.getInstance().getMenuListRole();
        if(null!=menuListRole&&null!=menuListRole.menuList&&menuListRole.menuList.size()>0){
            for(MenuListRole.Menu menu:menuListRole.menuList){
                if(menu.nMenuType== MenuListRole.MenuType.MENU_TYPE_CREATE_MEET_CHAT.ordinal()){
                    canCreateGroupChatRole = true;
                }
            }
        }
        return canCreateGroupChatRole;
    }

    public String getAccount() {
        return get("Account");
    }

    public String getPassword() {
        return get("Password");
    }

    public Long getEnterpriseCode() {
        try {
            return Long.parseLong(get("entCode"));
        } catch (Exception e) {
            return 0L;
        }

    }

    public void setAutoLogin(boolean autoLogin){
        put("autoLogin",autoLogin ? "yes":"no");
    }


    public boolean getAutoLogin(){
        String autoLogin = get("autoLogin");
        if (!TextUtils.isEmpty(autoLogin) && autoLogin.equals("yes")){
            return true;
        }else {
            return false;
        }
    }

    public String getUserID() {
        return get(SPConstant.USER_ID);
    }

    public String getUserName() {
        return get(SPConstant.USER_NAME);
    }

    public String getUserLoginName() {
        return get(SPConstant.LOGIN_NAME);
    }

    public String getDomainCode() {
        return get(SPConstant.DOMAIN_CODE);
    }

    public String getHeaderTokenID() {
        return get("tokenId");
    }

    public String getToken(){
        return get(SPConstant.X_TOKEN);
    }

    public void clearData(Activity activity) {
        if(BuildConfig.DEBUG) {
            AppBaseActivity.showToast("销毁了");
            return;
        }
        MessageReceiver.destoryKey(null, false);
        new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
            @Override
            public Object doOnThread() {
                ArrayList<FileLocalNameBean> allFile = (ArrayList<FileLocalNameBean>) AppDatas.MsgDB().getFileLocalListDao().getFileLocalList();
                for (FileLocalNameBean bean : allFile) {
                    File file = new File(bean.localFile);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                AppDatas.MsgDB().chatGroupMsgDao().clearData();
                AppDatas.MsgDB().chatSingleMsgDao().clearData();
                AppDatas.MsgDB().getFriendListDao().clearData();
                AppDatas.MsgDB().getGroupListDao().clearData();
                AppDatas.MsgDB().getSendUserListDao().clearData();
                AppDatas.MsgDB().getFileLocalListDao().clearData();
                AppDatas.Messages().clear();
                VimMessageListMessages.get().clear();

                SP.clear();//安全退出的时候才清除配置信息

                Glide.get(VIMApp.getInstance().getApplicationContext()).clearDiskCache();
                //删除缓存文件夹
                File fC = new File(VIMApp.getInstance().getApplicationContext().getExternalFilesDir(null) + File.separator + "Vim/");
                if (fC.exists()) {
                    deleteDir(fC.getPath());
                }
                return "";
            }

            @Override
            public void doOnMain(Object data) {
                Glide.get(VIMApp.getInstance().getApplicationContext()).clearMemory();
                customLogout(activity);
            }
        });
    }

    private void customLogout(Activity activity) {
        HYClient.getModule(ApiAuth.class).logout(null);
        AppAuth.get().setAutoLogin(false);
        VimChoosedContacts.get().destory();
        activity.startActivity(new Intent(activity, MainActivity.class).putExtra("isSOS", true).putExtra("isFromLogin", true));
    }

}
