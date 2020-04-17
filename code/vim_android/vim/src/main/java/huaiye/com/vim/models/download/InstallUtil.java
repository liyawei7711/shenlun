package huaiye.com.vim.models.download;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;
import java.io.File;

import huaiye.com.vim.BuildConfig;

/**
 * Created by dove on 2017/4/28.
 */

public class InstallUtil {
    public static boolean installNormal(Activity context, File file, boolean isForceInstall) {
        if (file == null || !file.exists() || !file.isFile() || file.length() <= 0) {
            return false;
        }

        try {
            String[] args1 = {"chmod", "771", file.getPath().substring(0, file.getPath().lastIndexOf("/"))};
            Process p1 = Runtime.getRuntime().exec(args1);
            p1.waitFor();
            p1.destroy();

            String[] args2 = {"chmod", "777", file.getPath()};
            Process p2 = Runtime.getRuntime().exec(args2);
            p2.waitFor();
            p2.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        startInstall(context, file, isForceInstall);

        return true;
    }


    private static void startInstall(Activity context, File file, boolean isForceInstall) {
        if (null == context || file == null) {
            return;
        }
        file.setExecutable(true,false);
        file.setReadable(true,false);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        String type = mime.getMimeTypeFromExtension(ext);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, "huaiye.com.vim.fileprovider", file);
            intent.setDataAndType(contentUri, /*"application/vnd.android.package-archive"*/type);
        } else {
            intent.setDataAndType(Uri.fromFile(file), /*"application/vnd.android.package-archive"*/type);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!context.getPackageManager().queryIntentActivities(intent, 0).isEmpty()) {
            try {
                if (isForceInstall){
                    context.startActivityForResult(intent, 1003);
                }else {
                    context.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
