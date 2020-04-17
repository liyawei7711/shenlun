package huaiye.com.vim.dao;

import huaiye.com.vim.R;
import huaiye.com.vim.VIMApp;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.common.constant.SPConstant;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: AppConstants
 */

public class AppConstants {

    String strAddress = AppUtils.getString(R.string.default_server_ip);
    int nPort = 8000;
    int nFilePort = 80;
    //    String strEnd = "ecs-war";
    String strEnd = "vss";
    String strVimEnd = "vim";


    String fileServerIp;//文件服务器ip
    int fileServerPort = -1;//文件服务器端口
    String fileUploadUri;//文件上传地址
    String keyFileUploadUri;//文件下载地址
    String uploadUri;//app更新地址
    String headerUri;//app更新地址

    /* 是否显示所有的联系人 */
    private boolean mShowAllContacts = false;

    protected AppConstants() {
        strAddress = SP.getString("IP", strAddress);
        strEnd = SP.getString("strEnd", strEnd);
        nPort = SP.getInteger("Port", nPort);
    }

    public String getAddressIP() {
        return strAddress;
    }

    public int getAddressPort() {
        return nPort;
    }

    public String getAddressEnd() {
        return strEnd;
    }

    public AppConstants setAddress(String ip, int port) {
        strAddress = ip;
        nPort = port;

        SP.putString("IP", strAddress);
        SP.putInteger("Port", nPort);

        return this;
    }

    public void setAddressEnd(String end) {
        strEnd = end;
        SP.putString("strEnd", strEnd);
    }

    public String getAddressBaseURL() {
        return "http://" + strAddress + ":" + nPort + "/" + strEnd + "/";
    }
    public String getAddressBaseURLFile() {
        return "http://" + fileServerIp + ":" + fileServerPort+ "/" + strEnd + "/";
    }

    public String getVimAddressBaseURL() {
        return "http://" + strAddress + ":" + nPort + "/" + strVimEnd + "/";
    }

    public String getChatBaseURL() {
        return "http://" + strAddress + ":" + nPort + "/";
    }

    public String getAddressBaseURLTarget() {
        return "http://" + strAddress + ":" + nPort + "/ECSFileServer/";
    }

    public String getFileAddressURL() {
        return "http://" + strAddress + ":" + nFilePort + "/";
    }

    public String getAddressWithoutPort() {
        return "http://" + fileServerIp + ":" + fileServerPort;// + "/"+headerUri;
//        return "http://" + strAddress + ":" + nPort + "/" + strEnd;
//        return "http://" + strAddress;
    }

    public void setFileServerIp(String ip) {
        fileServerIp = ip;
    }

    public void setFileServerPort(int port) {
        fileServerPort = port;
    }

    public void setFileUploadUri(String fileUploadUri) {
        this.fileUploadUri = fileUploadUri;
    }

    public void setKeyFileUploadUri(String keyFileUploadUri) {
        this.keyFileUploadUri = keyFileUploadUri;
    }

    public String getKeyFileUploadUri() {
        return "http://" + fileServerIp + ":" + fileServerPort + "/" + keyFileUploadUri + "/" + SP.getString(AppUtils.mDeviceIM) + "/json";
    }

    public String getCommonUri(String str) {
        return "http://" + fileServerIp + ":" + fileServerPort + "/" + keyFileUploadUri + "/" + SP.getString(AppUtils.mDeviceIM) + "/" + str;
    }
    public String getAppUpdateUri() {
        return "http://" + fileServerIp + ":" + fileServerPort + "/" + uploadUri+ "/android_whatsapp.version";
    }

    public String getFileUploadUri() {
        return fileUploadUri;
    }
    public String getHeaderUri() {
        return headerUri;
    }

    public String getFileServerURL() {
        if (fileServerIp == null || fileServerIp.length() == 0 || fileServerPort == -1) {
            return null;
        }

        return "http://" + fileServerIp + ":" + fileServerPort + "/";
    }

    public String getFileServerIp() {
        return fileServerIp;
    }

    public int getSiePort() {
        return SP.getInteger(SPConstant.SIE_PORT);
    }


    public int getFileServerPort() {
        return fileServerPort;
    }

    /***********************************************************
     * 描述: 切换是否显示所有联系人
     * 入参: 无
     * 出参: 无
     * 返回: 无
     ************************************************************/
    public void switchShowAllContacts() {
        mShowAllContacts = !mShowAllContacts;
    }

    /***********************************************************
     * 描述: 获取是否显示所有联系人
     * 入参: 无
     * 出参: 无
     * 返回: 无
     ************************************************************/
    public boolean isShowAllContacts() {
        return mShowAllContacts;
    }

    public void setAppUploadUri(String strVssConfigParaValue) {
        uploadUri = strVssConfigParaValue;
    }
    public void setHeaderUri(String strVssConfigParaValue) {
        headerUri = strVssConfigParaValue;
    }
}
