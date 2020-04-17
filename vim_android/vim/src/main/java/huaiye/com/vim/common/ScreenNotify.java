package huaiye.com.vim.common;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import java.util.List;

import huaiye.com.vim.R;
import huaiye.com.vim.ui.home.MainActivity;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;

/**
 * 在锁屏的情况下弹出通知和清除通知
 */
public class ScreenNotify {
    private static ScreenNotify mNotify;
    public static final  int DOWNLOAD_NOTIFICATION_ID = 12;
    public static  final int NOTIFY_ID = 22;
    public static final int FORGROUND_NOTICE_ID = 32;

    private NotificationCompat.Builder notificationBuilder;

    public static final String CHANNEL_ID_NAME = "my_channel_01";

    private ScreenNotify(){

    }


    public static ScreenNotify get(){
        if (mNotify == null){
            synchronized (ScreenNotify.class){
                if (mNotify == null){
                    mNotify = new ScreenNotify();
                }
            }
        }
        return mNotify;
    }



    public static void createNotificationChannel(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = CHANNEL_ID_NAME;
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "可视指挥";
            // 用户可以看到的通知渠道的描述
            String description = "可视指挥的通知";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = null;
            mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            mNotificationManager.createNotificationChannel(mChannel);
        }


    }


    public static void startForegroundService(Service service){
        NotificationCompat.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(service,CHANNEL_ID_NAME);
        }else {
            builder = new NotificationCompat.Builder(service);
        }
        Notification notification = builder
                .setContentTitle(AppUtils.getString(R.string.app_name))
                .setContentText(AppUtils.getString(R.string.service_in_operation))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.guanyu_logo)
                .setLargeIcon(BitmapFactory.decodeResource(service.getResources(), R.drawable.guanyu_logo))
                .build();

        service.startForeground(FORGROUND_NOTICE_ID, notification);
    }

    /**
     * 屏幕锁屏的时候才需要显示
     * @return  true  需要显示
     */
    public boolean needShowScreenNotify(){
       // KeyguardManager keyguardManager = (KeyguardManager) MCApp.getInstance().getSystemService(KEYGUARD_SERVICE);
       // return keyguardManager.isKeyguardLocked();
        return false;
    }

    public void showScreenNotify(Context context,String title,String msg){
        if (!needShowScreenNotify()){
            return;
        }
        NotificationManager notificationManager = (NotificationManager)context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context,MainActivity.class);
        PendingIntent pIntent =  PendingIntent.getActivity(context,1,intent,PendingIntent.FLAG_ONE_SHOT,null);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationBuilder = new NotificationCompat.Builder(context,CHANNEL_ID_NAME);
        }else {
            notificationBuilder = new NotificationCompat.Builder(context);
        }
         notificationBuilder.setSmallIcon(R.drawable.guanyu_logo)
                .setContentTitle(title)
                .setContentText(msg)
                .setContentIntent(pIntent)
                .setAutoCancel(true);

        notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
    }


    public void dismissNotify(Context context){
        NotificationManager notificationManager = (NotificationManager)context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFY_ID);
    }


    /**
     * 唤醒手机屏幕
     */
    public void wakeUpAndUnlock() {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager)AppUtils.ctx.getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire(10000); // 点亮屏幕
            wl.release(); // 释放
        }
    }


    /**
     * 打开应用,在后台就直接在前台展示当前界面
     * 应用退到后台时,需要等待一秒钟以上才能操作成功
     */
    public void openApplicationFromBackground() {
        ActivityManager mAm = (ActivityManager) AppUtils.ctx.getSystemService(ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<ActivityManager.AppTask > taskList = mAm.getAppTasks();
            for (ActivityManager.AppTask rti : taskList) {
                //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
                if (rti.getTaskInfo().topActivity.getPackageName().equals(AppUtils.ctx.getPackageName())) {
                    rti.moveToFront();
                    return;
                }
            }
        } else {
            //获得当前运行的task
            List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo rti : taskList) {
                //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台

                if (rti.topActivity.getPackageName().equals(AppUtils.ctx.getPackageName())) {
                    mAm.moveTaskToFront(rti.id, ActivityManager.MOVE_TASK_WITH_HOME);
                    return;
                }
            }
        }
    }

    public static void cancel(Context context,int notifyID){
        NotificationManager notificationManager = (NotificationManager)context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notifyID);
    }

}
