package huaiye.com.vim.models.auth.bean;

import java.io.Serializable;

import huaiye.com.vim.BuildConfig;

/**
 * desc: 版本号信息类
 */

public class VersionData implements Serializable {

    public int versionCode;
    public String versionName;
    public String message;
    public String path;

    /*对比文件服务器上的版本号与本地版本号，当服务器上的版本号高时，提示升级*/
    public boolean isNeedToUpdate() {
        int ret = versionName.compareTo(BuildConfig.VERSION_NAME);

        if(ret > 0)
            return true;
        else
            return false;
    }
}