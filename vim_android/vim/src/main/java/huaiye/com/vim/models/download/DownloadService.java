package huaiye.com.vim.models.download;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;

import java.io.File;
import java.util.List;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.ui.home.MainActivity;
import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.callback.HTTPCallback;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * ******************************
 *
 * @文件名称:DownloadService.java
 * @文件作者:Administrator
 * @创建时间:2015年10月22日
 * @文件描述: *****************************
 */
public class DownloadService extends IntentService {
    private static final String TAG = "DownloadService";

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    int downloadCount = 0;

    private String apkUrl = "";

    public DownloadService() {
        super("DownloadService");
    }

    private File outputFile;
    private Intent intent;

    @Override
    protected void onHandleIntent(Intent intent) {
        this.intent = intent;
        DownloadApi.isLoad = true;

        apkUrl = intent.getStringExtra("downloadURL");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.guanyu_logo)
                .setContentTitle(getString(R.string.download_notice1))
                .setContentText(getString(R.string.download_notice2))
                .setAutoCancel(true);

        notificationManager.notify(0, notificationBuilder.build());

        download();
    }

    long startTime = 0;

    int i = 0;

    private void download() {

        outputFile = new File(Environment.getExternalStorageDirectory(), "hy_vim_update.apk");

        if(outputFile.exists()) {
            outputFile.delete();
        }
        Https.get(apkUrl)
                .setDownloadMode(outputFile)
                .setHttpCallback(new HTTPCallback() {
                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                    }

                    @Override
                    public void onProgress(HTTPResponse httpResponse, long l, long l1) {
                        if (System.currentTimeMillis() - startTime > 1000) {
                            startTime = System.currentTimeMillis();
                            //不频繁发送通知，防止通知栏下拉卡顿
                            int progress = (int) ((l * 100) / l1);
                            if ((downloadCount == 0) || progress > downloadCount) {
                                sendNotification(l1, l, progress);
                            }
                        } else {
                            return;
                        }
                    }

                    @Override
                    public void onSuccess(HTTPResponse httpResponse) {
                        downloadCompleted(getString(R.string.download_notice3));
                    }

                    @Override
                    public void onCancel(HTTPRequest httpRequest) {
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        downloadCompleted(getString(R.string.download_notice4));
                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {

                    }
                }).build().requestAsync();
    }

    private void downloadCompleted(String str) {
        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText(str);
        notificationManager.notify(0, notificationBuilder.build());

        //安装apk
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//        intent.setDataAndType(Uri.fromFile(outputFile), "application/vnd.android.package-archive");
//        startActivity(intent);
        if (!str.contains("Error")) {
            installAPKFile(outputFile);
        }
    }

    /**
     * 调起安装
     *
     * @param file
     */
    protected void installAPKFile(final File file) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> infos = am.getRunningTasks(1);
        if (!infos.isEmpty()
                && infos.size() > 0) {

            ActivityManager.RunningTaskInfo info = infos.get(0);
            String infoCLassName = info.topActivity.getClassName();
            if (infoCLassName.equals(MainActivity.class.getCanonicalName())) {

                Intent intent = new Intent(DownloadService.this, InstallDialogActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(InstallDialogActivity.APK_PATH, file.getPath());
                startActivity(intent);

                DownloadApi.isLoad = false;
                stopService(intent);
                return;
            }
        }

        Intent intent = new Intent(DownloadService.this, InstallDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(InstallDialogActivity.APK_PATH, file.getPath());

        startActivity(intent);

        DownloadApi.isLoad = false;
        stopService(intent);
    }

    private void sendNotification(long contentLength, long bytesRead, int progress) {
        notificationBuilder.setProgress(100, progress, false);
        notificationBuilder.setContentText(
                AppUtils.getDataSize(bytesRead) + "/" +
                        AppUtils.getDataSize(contentLength));
        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

}
