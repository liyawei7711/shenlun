package huaiye.com.vim.ui.contacts.sharedata;

import android.text.TextUtils;

import java.util.ArrayList;

import huaiye.com.vim.common.constant.SPConstant;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.models.contacts.bean.ContacsTerminal;

/**
 * Created by ywt on 2019/2/27.
 */

public class ChoosedContactsNew {
    private ArrayList<User> mContacts = new ArrayList<>();
    private ArrayList<ContacsTerminal.Data> mTerminalDevices = new ArrayList<>();
    private User mSelf;

    private ChoosedContactsNew(){
        mSelf = new User();
        mSelf.strUserID = String.valueOf(AppDatas.Auth().getUserID());
        mSelf.strLoginName = AppDatas.Auth().getUserLoginName();
        mSelf.strUserName = AppDatas.Auth().getUserName();
        mSelf.strDomainCode = AppDatas.Auth().getDomainCode();
        mSelf.deviceType = 1;
        mSelf.nStatus = 1;
        mSelf.nJoinStatus = 2;
        mSelf.strHeadUrl = AppDatas.Auth().getHeadUrl(AppDatas.Auth().getUserID() + SPConstant.STR_HEAD_URL);

    }

    static class Holder {
        static final ChoosedContactsNew SINGLETON = new ChoosedContactsNew();
    }

    public static ChoosedContactsNew get() {
        return ChoosedContactsNew.Holder.SINGLETON;
    }

    public void setContacts(ArrayList<User> list){
        if (list == null) {
            mContacts.clear();
            return;
        }
        for(User item : list){
            if(item.strUserID.equals(mSelf.strUserID)){
                list.remove(item);
                break;
            }
        }
        mContacts.add(mSelf);//自己始终在列表头
        mContacts.addAll(list);
    }

    public void addSelf(){
        if(!mContacts.contains(mSelf)){
            mSelf.strUserID = String.valueOf(AppDatas.Auth().getUserID());
            mSelf.strLoginName = AppDatas.Auth().getUserLoginName();
            mSelf.strUserName = AppDatas.Auth().getUserName();
            mSelf.strDomainCode = AppDatas.Auth().getDomainCode();
            mSelf.deviceType = 1;
            mSelf.nStatus = 1;
            mSelf.nJoinStatus = 2;
            if(TextUtils.isEmpty(mSelf.strHeadUrl)){
                mSelf.strHeadUrl = AppDatas.Auth().getHeadUrl(AppDatas.Auth().getUserID() + SPConstant.STR_HEAD_URL);
            }
            mContacts.add(0, mSelf);//如果自己不存在就加到列表头
        }
    }

    public boolean addContacts(User user) {
        if (user == null) {
            return false;
        }
        if(!mContacts.contains(mSelf)){
            if(TextUtils.isEmpty(mSelf.strHeadUrl)){
                mSelf.strHeadUrl = AppDatas.Auth().getHeadUrl(AppDatas.Auth().getUserID() + SPConstant.STR_HEAD_URL);
            }
            mContacts.add(0, mSelf);//如果自己不存在就加到列表头
        }
        for (User item : mContacts) {
            if (item.strUserID.equals(user.strUserID)) {
                return true;
            }
        }
        mContacts.add(user);
        return true;
    }

    public boolean removeContacts(User user) {
        if (user == null || mSelf.strUserID.equals(user.strUserID)) {
            //自己不能被删除
            return false;
        }
        for (User item : mContacts) {
            if (item.strUserID.equals(user.strUserID)) {
                mContacts.remove(item);
                return true;
            }
        }
        return false;
    }

    public boolean isContain(User user){
        for (User item : mContacts) {
            if (item.strUserID.equals(user.strUserID)) {
                return true;
            }
        }
        return false;
    }

    public boolean isContain(String userId){
        for (User item : mContacts) {
            if (item.strUserID.equals(userId)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<User> getContacts(){
        return mContacts;
    }

    public void clear(){
        if(mContacts != null){
            mContacts.clear();
        }
    }
}
