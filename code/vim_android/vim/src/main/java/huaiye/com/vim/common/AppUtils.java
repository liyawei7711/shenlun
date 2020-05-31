package huaiye.com.vim.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.huaiye.sdk.HYClient;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import huaiye.com.vim.BuildConfig;
import huaiye.com.vim.VIMApp;
import huaiye.com.vim.models.map.bean.DaoHangAppInfo;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;

/**
 * author: admin
 * date: 2017/09/05
 * version: 0
 * mail: secret
 * desc: AppUtils
 */

public final class AppUtils {
    public static final boolean is9 = false;
    public static final int TOP = 1;
    public static final int LEFT = 2;
    public static final int BOTTOM = 3;
    public static final int RIGHT = 4;
    public static String STOP_CAPTURE_TYPE = "capture_stop_type";
    public static final int STOP_CAPTURE_TYPE_INT = 1001;
    public static String PLAYER_TYPE = "player_type";
    public static String PLAYER_TYPE_only_audio = "only_audio";
    public static String PLAYER_TYPE_audio_video = "audio_video";

    public static Context ctx;
    public static boolean isHide;

    public static boolean isCaptureLayoutShowing = false;

    public static final String XIAOMI = "HM NOTE 1LTETD";
    public static String rootPath = Environment.getExternalStorageDirectory().toString();

    public static String CAPTURE_TYPE = "capture_type";
    public static String STRING_KEY_false = "false";

    public static String STRING_KEY_jiami = "jiami";

    public static String STRING_KEY_resetpwd = "reSetPwd";//重置密码
    public static String STRING_KEY_agc = "agc";
    public static String STRING_KEY_ns = "ns";
    public static String STRING_KEY_aec = "aec";
    public static String STRING_KEY_qos = "qos";
    public static String STRING_KEY_player = "player";
    public static String STRING_KEY_capture = "capture";
    public static String STRING_KEY_bitrate = "bitrate";
    public static String STRING_KEY_recapture = "recapture";
    public static String STRING_KEY_camera = "camera";
    public static String STRING_KEY_trans = "trans";
    public static String STRING_KEY_capturebianma = "capturebianma";
    public static String STRING_KEY_playerjiema = "playerjiema";
    public static String STRING_KEY_mPublishPresetoption = "mPublishPresetoption";
    public static final String STRING_KEY_tcp = "tcp";
    public static final String STRING_KEY_udp = "udp";
    public static final String STRING_KEY_VGA = "VGA";
    public static final String STRING_KEY_HD = "HDVGA";
    public static final String STRING_KEY_HD720P = "HD720P";
    public static final String STRING_KEY_HD1080P = "HD1080P";
    public static final String STRING_KEY_ying = "ying";
    public static final String STRING_KEY_soft = "soft";
    public static final String STRING_KEY_kbps = "kbps";
    public static final String STRING_KEY_encrypt = "encrypt";

    //消息类型
    public static final int MESSAGE_TYPE_SHARE = 9988;
    public static final int MESSAGE_TYPE_JINJI = 9989;
    public static final int MESSAGE_TYPE_TEXT = 9999;
    public static final int MESSAGE_TYPE_IMG = 9998;
    public static final int MESSAGE_TYPE_FILE = 9997;
    public static final int MESSAGE_TYPE_AUDIO_FILE = 9996;
    public static final int MESSAGE_TYPE_VIDEO_FILE = 9995;
    public static final int MESSAGE_TYPE_ADDRESS = 9993;//发送位置信息
    public static final int MESSAGE_TYPE_GROUP_MEET = 9992;//群聊会议
    public static final int MESSAGE_TYPE_SINGLE_CHAT_VOICE = 9991;//单聊语音
    public static final int MESSAGE_TYPE_SINGLE_CHAT_VIDEO = 9990;//单聊视频


    //中间件过来的通知类型
    public static final int CAPTURE_TYPE_INT = 1000;
    public static final int NOTIFICATION_TYPE_GUANMO = 1002;
    public static final int NOTIFICATION_TYPE_PERSON_PUSH = 1003;
    public static final int NOTIFICATION_TYPE_DEVICE_PUSH = 1004;
    public static final int NOTIFICATION_TYPE_GET_USER_GPS = 2006;//获取GPS信息订阅
    public static final int NOTIFICATION_TYPE_PUSH_USER_GPS = 2002;//人员GPS信息推送
    public static final int NOTIFICATION_TYPE_MODIFY_USER_HEAD = 2007;//修改人员头像
    public static final int NOTIFICATION_TYPE_CRESTE_GROUP = 3000;//创建群成功的推送
    public static final int NOTIFICATION_TYPE_MODIFY_GROUP = 3001;//3.22.聊天群组修改推送
    public static final int NOTIFICATION_TYPE_DEL_GROUP = 3002;//群主删除群组
    public static final int NOTIFICATION_TYPE_ADD_MEMBER = 3003;//聊天群组人员加入
    public static final int NOTIFICATION_TYPE_LEAVE_GROUP = 3004;//退出群组
    public static final int NOTIFICATION_TYPE_ADD_FRIEND = 3006;//添加好友
    public static final int NOTIFICATION_TYPE_DEL_FRIEND = 3007;//删除好友
    public static final int NOTIFICATION_TYPE_GROUP_KICKOUT_MEMBER = 3008;//剔出队友


    //秘钥相关3100开始
    public static final int NOTIFICATION_TYPE_CLOSE_ENCRYPT = 3100;//远程解绑
    public static final int NOTIFICATION_TYPE_DESTORY_ENCRYPT = 3101;//远程毁钥
    public static final int NOTIFICATION_TYPE_ENCRYPT_BIND = 3102;//秘钥绑定通知

    //sp配置相关
    public static final String SP_SETTING_VOICE = "SP_SETTING_VOICE";//我的页面是否启用话筒播放开关
    public static final String SP_SETTING_MSG_TOP = "SP_SETTING_MSG_TOP";//聊天详情设置置顶消息
    public static final String SP_SETTING_MSG_TOP_TIME = "SP_SETTING_MSG_TOP_TIME";//设置置顶消息的时间
    public static final String SP_SETTING_NODISTURB = "SP_SETTING_NODISTURB";//消息设置免打扰
    public static final String SP_CHAT_SETTING_NOTIFICATION = "SP_CHAT_SETTING_NOTIFICATION";//聊天通知声音
    public static final String SP_CHAT_SETTING_YUEHOUJIFENG = "SP_CHAT_SETTING_YUEHOUJIFENG";//是否开启阅后即焚


    //位置相关
    public static final String STRING_KEY_LOCATION_FREQUENCY = "location_frequency";
    public static final String STRING_KEY_LOCATION_FREQUENCY_LOW = "location_frequency";
    public static final String STRING_KEY_LOCATION_FREQUENCY_MIDDLE = "location_frequency_MIDDLE";
    public static final String STRING_KEY_LOCATION_FREQUENCY_HIGH = "location_frequency_HIGH";


    //EventBus定义的类型相关
    public static final int EVENT_INIT_FAILED = 1000000;
    public static final int EVENT_INIT_KITOUT = 1000001;

    public static final int EVENT_CREATE_GROUP_SUCCESS = 100001;//建群成功
    public static final int EVENT_ADD_PEOPLE_TO_GROUP_SUCCESS = 100002;//群增加成员成功
    public static final int EVENT_KICKOUT_PEOPLE_TO_SUCCESS = 100003;//删除群成员成功
    public static final int EVENT_DEL_GROUP_SUCCESS = 100004;//删除群成功
    public static final int EVENT_LEAVE_GROUP_SUCCESS = 100005;//退群成功
    public static final int EVENT_UPDATE_GROUP_DETAIL = 100006;//通知前台UI页面群成员信息有变更
    public static final int EVENT_INTENT_CHATSINGLEACTIVITY = 100007;//进入单聊页面,通知前面页面关闭
    public static final int EVENT_MODIFY_GROUPNAME_SUCCESS = 100008;//群名修改成功
    public static final int EVENT_MODIFY_GROUP_ANNOUNCEMENT_SUCCESS = 100009;//群公告修改成功
    public static final int EVENT_COMING_NEW_MESSAGE = 100010;//来新消息刷新最新的消息
    public static final int EVENT_CLEAR_MESSAGE_SUCCESS = 100011;//消息记录清楚成功广播
    public static final int EVENT_CREATE_MEETTING_SUCCESS = 100012;//群聊会议创建成功
    public static final int EVENT_CLOSE_MEETTING = 100013;//群聊会议结束
    public static final int EVENT_VOICE_CANCLE = 100014;//单聊语音取消
    public static final int EVENT_VOICE_SUCCESS = 100015;//单聊语音成功
    public static final int EVENT_VOICE_REFUSE = 100016;//单聊语音对方拒绝
    public static final int EVENT_VIDEO_CANCLE = 100017;//视频通话取消
    public static final int EVENT_VIDEO_SUCCESS = 100018;//视频通话接听成功
    public static final int EVENT_VIDEO_REFUSE = 100019;//视频通话对方拒绝
    public static final int EVENT_CREATE_GROUP_SUCCESS_ADDGROUP_TO_LIST = 100020;//群创建成功后,将群加入群列表
    public static final int EVENT_MESSAGE_MODIFY_GROUP = 100021;//群信息有变更(名称,公告等)
    public static final int EVENT_MESSAGE_ADD_FRIEND = 100022;//群信息有变更(名称,公告等)
    public static final int EVENT_MESSAGE_DEL_FRIEND = 100023;//群信息有变更(名称,公告等)
    public static final int EVENT_REFRESH_GROUP_DETAIL = 100024;//刷新群信息
    public static final int EVENT_RPUSH_VIDEO = 100025;//创建视频推送
    public static final int EVENT_MESSAGE_YUEHOUJIFENG = 100026;//阅后即焚
    public static final int EVENT_MESSAGE_MODIFY_SELF_HEAD_PIC = 100027;//修改个人头像
    public static final int EVENT_MESSAGE_MODIFY_HEAD_PIC = 100028;//好友修改头像成功通知
    public static final int EVENT_MESSAGE_UPLOAD_BAIDU_SNAP_PIX = 100029;//地图截图上传成功
    public static final int EVENT_MESSAGE_MODIFY_GROUP_HEAD_PIC = 100030;//修改群头像
    public static final int EVENT_MESSAGE_CLOSE_ACPTURE = 100031;//关闭采集
    public static final int EVENT_MESSAGE_KEY_HOME = 100032;//home 键被点击
    public static final int EVENT_MESSAGE_CHANGE_VIDEO_STATE = 100034;//自己做了是否关闭摄像头
    public static final int EVENT_MESSAGE_CHANGE_VIDEO_STATE_OTHERS = 100035;//别人做了是否关闭摄像头


    //startActivityForResult  request code
    public static final int REQUEST_CODE_SELECT_IMAGES_CODE = 1000;//图片加载器请求码
    public static final int REQUEST_CODE_VIDEO_RECORD = 1001;//语音聊天
    public static final int REQUEST_CODE_CHOOSE_NOTICE = 1002;//选择提醒人
    public static final int REQUEST_CODE_CHOOSE_FILE = 1005;//选择图片
    public static final int REQUEST_CODE_SEND_LOCATION = 1006;//发送位置
    public static final int REQUEST_CODE_MODIFY_PIC = 1007;//修改头像


    //聊天文件下载状态
    public static final int CHAT_DOWNLOAD_FILE_STATE_UNDOWNLOAD = 0;//未下载
    public static final int CHAT_DOWNLOAD_FILE_STATE_DOWNLOADING = 1;//下载中
    public static final int CHAT_DOWNLOAD_FILE_STATE_DOWNLOADED = 2;//已下载

    public static String nEncryptPasswd = "11111111111111111111111111111111";
    public static String nEncryptDevice = "0000000000000000003F";
    public static String mDeviceIM = "CM_ID";
    public static String mJiaMiMiMa = "CM_MI_MA";
    public static boolean nEncryptIMEnable = true;

    public static String audiovideoPath = Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/audiovideo";

    /**
     * 此名单屏蔽三星某机型，此机型用AppAudioManager类库，会产生崩溃问题
     */
    public static final ArrayList<String> audioDevice = new ArrayList(Arrays.asList("HM NOTE 1LTETD"));

    /**
     * 悬浮框手机
     */
    public static final ArrayList<String> xuanfuDevice = new ArrayList(Arrays.asList("Xiaomi", "Hisense Z1", "T996S", "ALE-UL00"));

    private AppUtils() {

    }

    public static void init(Context context) {
        ctx = context.getApplicationContext();
    }

    // 获取版本号
    public static int versionCode() {
        int version = -1;
        try {
            String packageName = ctx.getPackageName();
            PackageManager packageManager = ctx.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            version = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    // 获取版本名称
    public static String versionName() {
        String versionCode = null;
        try {
            String packageName = ctx.getPackageName();
            PackageManager packageManager = ctx.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            versionCode = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getResourceString(int id) {
        return HYClient.getContext().getString(id);
    }

    public static Drawable getResourceDrawable(int id) {
        return HYClient.getContext().getResources().getDrawable(id);
    }

    public static ColorStateList getResourceColor(int id) {
        return HYClient.getContext().getResources().getColorStateList(id);
    }

    public static int getResourceDimenssion(int id) {
        return (int) (id * ctx.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int getScreenWidth() {
        return ctx.getResources().getDisplayMetrics().widthPixels;
    }

    public static Point getPoint(View view) {

        Point point = new Point();
        int width = getScreenWidth();
        point.x = -1;
        switch (SP.getString(STRING_KEY_capture)) {
            case STRING_KEY_VGA:

                point.y = (int) (width / 480.0 * 640);

                break;
            case STRING_KEY_HD:
                point.y = (int) (width / 720.0 * 1280);
                break;
            case STRING_KEY_HD720P:
                point.y = (int) (width / 720.0 * 1280);
                break;
            case STRING_KEY_HD1080P:
                point.y = (int) (width / 1080 * 1920);

                break;
        }
        return point;
    }

    public static float getScreenDens() {
        return ctx.getResources().getDisplayMetrics().density;
    }

    public static int getScreenHeight() {
        return ctx.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 关闭软键盘
     */
    public static void closeKeyboard(EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    public static void showKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    /**
     * 通过网络接口取
     *
     * @return
     */
    public static String getNewMac() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean isNetworkConnected() {
        if (ctx != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    //当前应用是否处于前台
    public static boolean isForeground() {
        if (ctx != null) {
            ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

            ComponentName cn = activityManager.getRunningTasks(1).get(0).topActivity;
            String currentPackageName = cn.getPackageName();
            if (currentPackageName.equals(ctx.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static void startInstall(Context context, Uri uri) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }

    /**
     * TODO:获取屏幕宽度
     *
     * @return 屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getApplicationContext().getResources()
                .getDisplayMetrics().widthPixels;
    }

    public static int getSize(int i) {
        return (int) (ctx.getResources().getDisplayMetrics().density * i);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 网络状态是否是wifi
     *
     * @return
     */
    public static int getNetWorkStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo info = cm.getActiveNetworkInfo();

        if (info != null
                && info.isConnected()) {
            // 有网状态

            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                // wifi 状态
                return 0;
            } else {
                // 3G/4G
                return 1;
            }

        } else {
            // 无网状态
            return -1;
        }
    }

    public static String getDataSize(long var0) {
        DecimalFormat var2 = new DecimalFormat("###.00");
        return var0 < 1024L ? var0 + "bytes" : (var0 < 1048576L ? var2.format((double) ((float) var0 / 1024.0F))
                + "KB" : (var0 < 1073741824L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F))
                + "MB" : (var0 < 0L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F / 1024.0F))
                + "GB" : "error")));
    }

    public static int dip2Px(Context context, float dip) {
        return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static void copyAndPass(Context context, String string) {
        // 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(string);
    }

    public static boolean isIpAddress(String ip) {
        boolean isIp = false;
        String str = "((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))";
        Pattern pattern = Pattern.compile(str);
        Matcher m = pattern.matcher(ip);

        isIp = m.matches();
        return isIp;
    }

    public static String getIMEIResult(Context context) {
        String iemi = "";
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            iemi = getIMEI(context);
        } else {
            iemi = JudgeSIM(context);
        }
        return iemi;
    }

    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context) {
        String imei = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                imei = tm.getDeviceId();
            } else {
                Method method = tm.getClass().getMethod("getImei");
                imei = (String) method.invoke(tm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static String JudgeSIM(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        //获取当前SIM卡槽数量
        int phoneCount = tm.getPhoneCount();
        //获取当前SIM卡数量
        @SuppressLint("MissingPermission") int activeSubscriptionInfoCount = SubscriptionManager.from(context).getActiveSubscriptionInfoCount();
        @SuppressLint("MissingPermission") List<SubscriptionInfo> activeSubscriptionInfoList = SubscriptionManager.from(context).getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList == null) {
            return "";
        }
        for (SubscriptionInfo subInfo : activeSubscriptionInfoList) {
            try {
                Method method = tm.getClass().getMethod("getImei", int.class);
                return (String) method.invoke(tm, subInfo.getSimSlotIndex());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                return "";
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return "";
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                return "";
            }
        }
        return "";
    }

    public void clearSofoKeyBord(View view, Activity content) {
        InputMethodManager inputMethodManager = (InputMethodManager) content.getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            View localView = content.getCurrentFocus();
            if (localView != null && localView.getWindowToken() != null) {
                IBinder windowToken = localView.getWindowToken();
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }

    private void showInput(EditText view, Context content) {
        InputMethodManager imm = (InputMethodManager) content.getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static void zip(File src, File dest) {
        if (dest.exists())
            dest.delete();
        try {
            dest.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //提供了一个数据项压缩成一个ZIP归档输出流
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(dest));
            //如果此文件是一个文件，否则为false。
            if (src.isFile()) {
                zipFileOrDirectory(out, src, "");
            } else {
                //返回一个文件或空阵列。
                File[] entries = src.listFiles();
                for (int i = 0; i < entries.length; i++) {
                    // 递归压缩，更新curPaths
                    zipFileOrDirectory(out, entries[i], "");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //关闭输出流
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static void zipFileOrDirectory(ZipOutputStream out,
                                           File fileOrDirectory, String curPath) throws IOException {
        //从文件中读取字节的输入流
        FileInputStream in = null;
        try {
            //如果此文件是一个目录，否则返回false。
            if (!fileOrDirectory.isDirectory()) {
                // 压缩文件
                byte[] buffer = new byte[4096];
                int bytes_read;
                in = new FileInputStream(fileOrDirectory);
                //实例代表一个条目内的ZIP归档
                ZipEntry entry = new ZipEntry(curPath
                        + fileOrDirectory.getName());
                //条目的信息写入底层流
                out.putNextEntry(entry);
                while ((bytes_read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytes_read);
                }
                out.closeEntry();
            } else {
                // 压缩目录
                File[] entries = fileOrDirectory.listFiles();
                for (int i = 0; i < entries.length; i++) {
                    // 递归压缩，更新curPaths
                    zipFileOrDirectory(out, entries[i], curPath
                            + fileOrDirectory.getName() + "/");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            // throw ex;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static String getString(int id) {
        return ctx.getString(id);
    }


    static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);

    public static String getTimeHour(long time) {
        return sdf.format(time);
    }

    public static String getTime(String strFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(strFormat, Locale.CHINA);
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    public static void delFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                delFile(f);
            }
        }
        file.delete();
    }

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    /**
     * 获取已安装的导航软件列表
     *
     * @return
     */
    public static List<DaoHangAppInfo> getDaoHangAppList() {
        List<DaoHangAppInfo> daoHangAppInfolist = new ArrayList<>();
        PackageManager manager = ctx.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> installedPackages = manager.getInstalledPackages(0);
        if (installedPackages != null) {
            for (PackageInfo packageInfo : installedPackages) {
                if (VIMApp.daohangAppPackageNameList.contains(packageInfo.packageName)) {
                    DaoHangAppInfo tmpInfo = new DaoHangAppInfo();
                    tmpInfo.appName = packageInfo.applicationInfo.loadLabel(manager).toString();
                    tmpInfo.packageName = packageInfo.packageName;
                    tmpInfo.versionName = packageInfo.versionName;
                    tmpInfo.versionCode = packageInfo.versionCode;
                    tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(manager);
                    daoHangAppInfolist.add(tmpInfo);
                }

            }
        }
        return daoHangAppInfolist;
    }


    /**
     * 获取已安装的导航软件列表
     *
     * @return
     */
    public static List<DaoHangAppInfo> getAllUnInstallDaoHangAppList() {
        List<DaoHangAppInfo> daoHangAppInfolist = new ArrayList<>();
        for (String daoPackage : VIMApp.daohangAppPackageNameList) {
            DaoHangAppInfo tmpInfo = new DaoHangAppInfo();
            tmpInfo.appName = VIMApp.daohangAppNameList.get(VIMApp.daohangAppPackageNameList.indexOf(daoPackage));
            tmpInfo.packageName = daoPackage;
            daoHangAppInfolist.add(tmpInfo);
        }
        return daoHangAppInfolist;
    }

    public static boolean isRightPwd(String str) {
        char[] c = str.toCharArray();
        boolean xiaoxie = false;
        boolean daxie = false;
        boolean num = false;
        for (int i = 0; i < c.length; i++) {
            if (c[i] >= 'a' && c[i] <= 'z') {
                xiaoxie = true;
            } else if (c[i] >= 'A' && c[i] <= 'Z') {
                daxie = true;
            } else if (c[i] >= '0' && c[i] <= '9') {
                num = true;
            }
        }
        if (xiaoxie && daxie && num) {
            return true;
        } else {
            return false;
        }
    }

    // 获取汉字的首字母大写
    public static String getFirstSpell(String string) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = string.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) { //如果已经是字母就不用转换了
                try {
                    //获取当前汉字的全拼
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(
                            arr[i], defaultFormat);
                    if (temp != null) {
                        pybf.append(temp[0].charAt(0));// 取首字母
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                if (arr[i] >= 'a' && arr[i] <= 'z') {
                    arr[i] -= 32;
                }
            /*if (arr[0] >= 'A' && arr[0] <= 'Z') {// 将大写转换为小写
                arr[0] += 32;
            }*/
                pybf.append(arr[i]);
            }
        }
        return pybf.toString();
    }


    /**
     * 判断当前应用程序处于前台还是后台
     */
    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static void goToDesktop(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

}
