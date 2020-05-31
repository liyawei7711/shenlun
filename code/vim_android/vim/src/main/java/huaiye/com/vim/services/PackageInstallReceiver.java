package huaiye.com.vim.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import huaiye.com.vim.VIMApp;

/**
 * 监听软件程序安装卸载的广播
 */

public class PackageInstallReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        VIMApp.getInstance().getDaoHangAppList();
    }


}
