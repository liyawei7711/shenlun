package huaiye.com.vim.common;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vim.R;

public class IntentWrapper {

    //Android 7.0+ Doze 模式
    protected static final int DOZE = 98;
    //华为 自启管理
    protected static final int HUAWEI = 99;
    //华为 锁屏清理
    protected static final int HUAWEI_GOD = 100;
    //小米 自启动管理
    protected static final int XIAOMI = 101;
    //小米 神隐模式
    protected static final int XIAOMI_GOD = 102;
    //三星 5.0/5.1 自启动应用程序管理
    protected static final int SAMSUNG_L = 103;
    //魅族 自启动管理
    protected static final int MEIZU = 104;
    //魅族 待机耗电管理
    protected static final int MEIZU_GOD = 105;
    //Oppo 自启动管理
    protected static final int OPPO = 106;
    //三星 6.0+ 未监视的应用程序管理
    protected static final int SAMSUNG_M = 107;
    //Oppo 自启动管理(旧版本系统)
    protected static final int OPPO_OLD = 108;
    //Vivo 后台高耗电
    protected static final int VIVO_GOD = 109;
    //金立 应用自启
    protected static final int GIONEE = 110;
    //乐视 自启动管理
    protected static final int LETV = 111;
    //乐视 应用保护
    protected static final int LETV_GOD = 112;
    //酷派 自启动管理
    protected static final int COOLPAD = 113;
    //联想 后台管理
    protected static final int LENOVO = 114;
    //联想 后台耗电优化
    protected static final int LENOVO_GOD = 115;
    //中兴 自启管理
    protected static final int ZTE = 116;
    //中兴 锁屏加速受保护应用
    protected static final int ZTE_GOD = 117;

    protected static List<IntentWrapper> sIntentWrapperList;

    public static List<IntentWrapper> getIntentWrapperList() {
        if (sIntentWrapperList == null) {


            sIntentWrapperList = new ArrayList<>();

            //Android 7.0+ Doze 模式
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                PowerManager pm = (PowerManager) AppUtils.ctx.getSystemService(Context.POWER_SERVICE);
                boolean ignoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(AppUtils.ctx.getPackageName());
                if (!ignoringBatteryOptimizations) {
                    Intent dozeIntent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    dozeIntent.setData(Uri.parse("package:" + AppUtils.ctx.getPackageName()));
                    sIntentWrapperList.add(new IntentWrapper(dozeIntent, DOZE));
                }
            }

            //华为 自启管理
            Intent huaweiIntent = new Intent();
            huaweiIntent.setAction("huawei.intent.action.HSM_BOOTAPP_MANAGER");
            sIntentWrapperList.add(new IntentWrapper(huaweiIntent, HUAWEI));

            //华为 锁屏清理
            Intent huaweiGodIntent = new Intent();
            huaweiGodIntent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            sIntentWrapperList.add(new IntentWrapper(huaweiGodIntent, HUAWEI_GOD));

            //小米 自启动管理
            Intent xiaomiIntent = new Intent();
            xiaomiIntent.setAction("miui.intent.action.OP_AUTO_START");
            xiaomiIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sIntentWrapperList.add(new IntentWrapper(xiaomiIntent, XIAOMI));

            //小米 神隐模式
            Intent xiaomiGodIntent = new Intent();
            xiaomiGodIntent.setComponent(new ComponentName("com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"));
            xiaomiGodIntent.putExtra("package_name", AppUtils.ctx.getPackageName());
            xiaomiGodIntent.putExtra("package_label", getApplicationName());
            sIntentWrapperList.add(new IntentWrapper(xiaomiGodIntent, XIAOMI_GOD));

            //三星 5.0/5.1 自启动应用程序管理
            Intent samsungLIntent = AppUtils.ctx.getPackageManager().getLaunchIntentForPackage("com.samsung.android.sm");
            if (samsungLIntent != null)
                sIntentWrapperList.add(new IntentWrapper(samsungLIntent, SAMSUNG_L));

            //三星 6.0+ 未监视的应用程序管理
            Intent samsungMIntent = new Intent();
            samsungMIntent.setComponent(new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.battery.BatteryActivity"));
            sIntentWrapperList.add(new IntentWrapper(samsungMIntent, SAMSUNG_M));

            //魅族 自启动管理
            Intent meizuIntent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            meizuIntent.addCategory(Intent.CATEGORY_DEFAULT);
            meizuIntent.putExtra("packageName", AppUtils.ctx.getPackageName());
            sIntentWrapperList.add(new IntentWrapper(meizuIntent, MEIZU));

            //魅族 待机耗电管理
            Intent meizuGodIntent = new Intent();
            meizuGodIntent.setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.powerui.PowerAppPermissionActivity"));
            sIntentWrapperList.add(new IntentWrapper(meizuGodIntent, MEIZU_GOD));

            //Oppo 自启动管理
            Intent oppoIntent = new Intent();
            oppoIntent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            sIntentWrapperList.add(new IntentWrapper(oppoIntent, OPPO));

            //Oppo 自启动管理(旧版本系统)
            Intent oppoOldIntent = new Intent();
            oppoOldIntent.setComponent(new ComponentName("com.color.safecenter", "com.color.safecenter.permission.startup.StartupAppListActivity"));
            sIntentWrapperList.add(new IntentWrapper(oppoOldIntent, OPPO_OLD));

            //Vivo 后台高耗电
            Intent vivoGodIntent = new Intent();
            vivoGodIntent.setComponent(new ComponentName("com.vivo.abe", "com.vivo.applicationbehaviorengine.ui.ExcessivePowerManagerActivity"));
            sIntentWrapperList.add(new IntentWrapper(vivoGodIntent, VIVO_GOD));

            //金立 应用自启
            Intent gioneeIntent = new Intent();
            gioneeIntent.setComponent(new ComponentName("com.gionee.softmanager", "com.gionee.softmanager.MainActivity"));
            sIntentWrapperList.add(new IntentWrapper(gioneeIntent, GIONEE));

            //乐视 自启动管理
            Intent letvIntent = new Intent();
            letvIntent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            sIntentWrapperList.add(new IntentWrapper(letvIntent, LETV));

            //乐视 应用保护
            Intent letvGodIntent = new Intent();
            letvGodIntent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.BackgroundAppManageActivity"));
            sIntentWrapperList.add(new IntentWrapper(letvGodIntent, LETV_GOD));

            //酷派 自启动管理
            Intent coolpadIntent = new Intent();
            coolpadIntent.setComponent(new ComponentName("com.yulong.android.security", "com.yulong.android.seccenter.tabbarmain"));
            sIntentWrapperList.add(new IntentWrapper(coolpadIntent, COOLPAD));

            //联想 后台管理
            Intent lenovoIntent = new Intent();
            lenovoIntent.setComponent(new ComponentName("com.lenovo.security", "com.lenovo.security.purebackground.PureBackgroundActivity"));
            sIntentWrapperList.add(new IntentWrapper(lenovoIntent, LENOVO));

            //联想 后台耗电优化
            Intent lenovoGodIntent = new Intent();
            lenovoGodIntent.setComponent(new ComponentName("com.lenovo.powersetting", "com.lenovo.powersetting.ui.Settings$HighPowerApplicationsActivity"));
            sIntentWrapperList.add(new IntentWrapper(lenovoGodIntent, LENOVO_GOD));

            //中兴 自启管理
            Intent zteIntent = new Intent();
            zteIntent.setComponent(new ComponentName("com.zte.heartyservice", "com.zte.heartyservice.autorun.AppAutoRunManager"));
            sIntentWrapperList.add(new IntentWrapper(zteIntent, ZTE));

            //中兴 锁屏加速受保护应用
            Intent zteGodIntent = new Intent();
            zteGodIntent.setComponent(new ComponentName("com.zte.heartyservice", "com.zte.heartyservice.setting.ClearAppSettingsActivity"));
            sIntentWrapperList.add(new IntentWrapper(zteGodIntent, ZTE_GOD));
        }
        return sIntentWrapperList;
    }

    protected static String sApplicationName;

    public static String getApplicationName() {
        if (sApplicationName == null) {
//            if (!DaemonEnv.sInitialized) return "";
            PackageManager pm;
            ApplicationInfo ai;
            try {
                pm = AppUtils.ctx.getPackageManager();
                ai = pm.getApplicationInfo(AppUtils.ctx.getPackageName(), 0);
                sApplicationName = pm.getApplicationLabel(ai).toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                sApplicationName = AppUtils.ctx.getPackageName();
            }
        }
        return sApplicationName;
    }

    /**
     * 处理白名单.
     *
     * @return 弹过框的 IntentWrapper.
     */
    @NonNull
    public static List<IntentWrapper> whiteListMatters(final Activity a, String reason) {
        List<IntentWrapper> showed = new ArrayList<>();
        if (reason == null) reason = a.getString(R.string.jixing_hexin_run);
        List<IntentWrapper> intentWrapperList = getIntentWrapperList();
        for (final IntentWrapper iw : intentWrapperList) {
            //如果本机上没有能处理这个Intent的Activity，说明不是对应的机型，直接忽略进入下一次循环。
            if (!iw.doesActivityExists()) continue;
            switch (iw.type) {
//                case DOZE:
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        PowerManager pm = (PowerManager) a.getSystemService(Context.POWER_SERVICE);
//                        if (pm.isIgnoringBatteryOptimizations(a.getPackageName())) break;
//                        new AlertDialog.Builder(a)
//                                .setCancelable(false)
//                                .setTitle("需要忽略 " + getApplicationName() + " 的电池优化")
//                                .setMessage(reason + a.getString(R.string.jixing_allow_notice7) + getApplicationName() + " 加入到电池优化的忽略名单。\n\n" +
//                                        "请点击『确定』，在弹出的『忽略电池优化』对话框中，选择『是』。")
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface d, int w) {iw.startActivitySafely(a);}
//                                })
//                                .show();
//                        showed.add(iw);
//                    }
//                    break;
                case HUAWEI:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle(a.getString(R.string.jixing_need_allow) + getApplicationName() + a.getString(R.string.jixing_auto_start))
                            .setMessage(reason + a.getString(R.string.jixing_need_allow) + getApplicationName() + a.getString(R.string.jixing_auto_start_huanhang) + "\n\n" +
                                    a.getString(R.string.jixing_allow_notice1) + getApplicationName() + a.getString(R.string.jixing_allow_notice2))
                            .setPositiveButton(a.getString(R.string.makesure), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivitySafely(a);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case ZTE_GOD:
                case HUAWEI_GOD:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle(getApplicationName() + a.getString(R.string.jixing_allow_notice8))
                            .setMessage(reason + a.getString(R.string.jixing_allow_notice7) + getApplicationName() + a.getString(R.string.jixing_allow_notice10) + "\n\n" +
                                    a.getString(R.string.jixing_allow_notice9) + getApplicationName() + a.getString(R.string.jixing_allow_notice6))
                            .setPositiveButton(a.getString(R.string.makesure), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivitySafely(a);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case XIAOMI_GOD:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle(a.getString(R.string.jixing_allow_notice11) + getApplicationName() + a.getString(R.string.jixing_allow_notice13))
                            .setMessage(reason + a.getString(R.string.jixing_allow_notice11) + getApplicationName() + a.getString(R.string.jixing_allow_notice14) + "\n\n" +
                                    a.getString(R.string.jixing_allow_notice12) + getApplicationName() + a.getString(R.string.jixing_allow_notice15))
                            .setPositiveButton(a.getString(R.string.makesure), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivitySafely(a);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case SAMSUNG_L:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle(a.getString(R.string.jixing_need_allow) + getApplicationName() + a.getString(R.string.jixing_allow_notice16))
                            .setMessage(reason + a.getString(R.string.jixing_allow_notice7) + getApplicationName() + a.getString(R.string.jixing_allow_notice17) + "\n\n" +
                                    a.getString(R.string.jixing_allow_notice18) + getApplicationName() + a.getString(R.string.jixing_allow_notice6))
                            .setPositiveButton(a.getString(R.string.makesure), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivitySafely(a);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case SAMSUNG_M:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle(a.getString(R.string.jixing_need_allow) + getApplicationName() + a.getString(R.string.jixing_allow_notice16))
                            .setMessage(reason + a.getString(R.string.jixing_allow_notice7) + getApplicationName() + a.getString(R.string.jixing_allow_notice19) + "\n\n" +
                                    a.getString(R.string.jixing_allow_notice20) + getApplicationName() + a.getString(R.string.jixing_allow_notice21))
                            .setPositiveButton(a.getString(R.string.makesure), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivitySafely(a);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case MEIZU:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle(a.getString(R.string.jixing_need_allow) + getApplicationName() + a.getString(R.string.jixing_allow_notice22))
                            .setMessage(reason + a.getString(R.string.jixing_need_allow) + getApplicationName() + a.getString(R.string.jixing_allow_notice23) + "\n\n" +
                                    a.getString(R.string.jixing_allow_notice24))
                            .setPositiveButton(a.getString(R.string.makesure), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivitySafely(a);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case MEIZU_GOD:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle(getApplicationName() + a.getString(R.string.jixing_allow_notice25))
                            .setMessage(reason + a.getString(R.string.jixing_allow_notice7) + getApplicationName() + a.getString(R.string.jixing_allow_notice26) + "\n\n" +
                                    a.getString(R.string.jixing_allow_notice27) + getApplicationName() + a.getString(R.string.jixing_allow_notice6))
                            .setPositiveButton(a.getString(R.string.makesure), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivitySafely(a);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case ZTE:
                case LETV:
                case XIAOMI:
                case OPPO:
                case OPPO_OLD:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle(a.getString(R.string.jixing_need_allow) + getApplicationName() + a.getString(R.string.jixing_allow_notice16))
                            .setMessage(reason + a.getString(R.string.jixing_allow_notice7) + getApplicationName() + a.getString(R.string.jixing_allow_notice28) + "\n\n" +
                                    a.getString(R.string.jixing_allow_notice3) + getApplicationName() + a.getString(R.string.jixing_allow_notice6))
                            .setPositiveButton(a.getString(R.string.makesure), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivitySafely(a);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case COOLPAD:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle(a.getString(R.string.jixing_need_allow) + getApplicationName() + a.getString(R.string.jixing_allow_notice16))
                            .setMessage(reason + a.getString(R.string.jixing_need_allow) + getApplicationName() + a.getString(R.string.jixing_allow_notice16) + "\n\n" +
                                    a.getString(R.string.jixing_allow_notice29) + getApplicationName() + a.getString(R.string.jixing_allow_notice30) + getApplicationName() + a.getString(R.string.jixing_allow_notice31))
                            .setPositiveButton(a.getString(R.string.makesure), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivitySafely(a);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case VIVO_GOD:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle(a.getString(R.string.jixing_need_allow) + getApplicationName() + a.getString(R.string.jixing_allow_notice4))
                            .setMessage(reason + a.getString(R.string.jixing_need_allow) + getApplicationName() + a.getString(R.string.jixing_allow_notice32) + "\n\n" +
                                    a.getString(R.string.jixing_allow_notice5) + getApplicationName() + a.getString(R.string.jixing_allow_notice6))
                            .setPositiveButton(a.getString(R.string.makesure), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivitySafely(a);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case GIONEE:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle(getApplicationName() + a.getString(R.string.jixing_allow_notice33))
                            .setMessage(reason + a.getString(R.string.jixing_need_allow) + getApplicationName() + a.getString(R.string.jixing_allow_notice34) + "\n\n" +
                                    a.getString(R.string.jixing_allow_notice35) + getApplicationName() + a.getString(R.string.jixing_allow_notice36))
                            .setPositiveButton(a.getString(R.string.makesure), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivitySafely(a);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case LETV_GOD:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle(a.getString(R.string.jixing_allow_notice37) + getApplicationName() + a.getString(R.string.jixing_allow_notice38))
                            .setMessage(reason + a.getString(R.string.jixing_allow_notice37) + getApplicationName() + a.getString(R.string.jixing_allow_notice39) + "\n\n" +
                                    a.getString(R.string.jixing_allow_notice41) + getApplicationName() + a.getString(R.string.jixing_allow_notice40))
                            .setPositiveButton(a.getString(R.string.makesure), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivitySafely(a);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case LENOVO:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle(a.getString(R.string.jixing_need_allow) + getApplicationName() + a.getString(R.string.jixing_allow_notice4))
                            .setMessage(reason + a.getString(R.string.jixing_need_allow) + getApplicationName() + a.getString(R.string.jixing_allow_notice42) + "\n\n" +
                                    a.getString(R.string.jixing_allow_notice43) + getApplicationName() + a.getString(R.string.jixing_allow_notice6))
                            .setPositiveButton(a.getString(R.string.makesure), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivitySafely(a);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
                case LENOVO_GOD:
                    new AlertDialog.Builder(a)
                            .setCancelable(false)
                            .setTitle(a.getString(R.string.jixing_allow_notice11) + getApplicationName() + a.getString(R.string.jixing_allow_notice44))
                            .setMessage(reason + a.getString(R.string.jixing_allow_notice11) + getApplicationName() + a.getString(R.string.jixing_allow_notice45) + "\n\n" +
                                    a.getString(R.string.jixing_allow_notice46) + getApplicationName() + a.getString(R.string.jixing_allow_notice40))
                            .setPositiveButton(a.getString(R.string.makesure), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    iw.startActivitySafely(a);
                                }
                            })
                            .show();
                    showed.add(iw);
                    break;
            }
        }
        return showed;
    }

    /**
     * 防止华为机型未加入白名单时按返回键回到桌面再锁屏后几秒钟进程被杀
     */
    public static void onBackPressed(Activity a) {
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        a.startActivity(launcherIntent);
    }

    protected Intent intent;
    protected int type;

    protected IntentWrapper(Intent intent, int type) {
        this.intent = intent;
        this.type = type;
    }

    /**
     * 判断本机上是否有能处理当前Intent的Activity
     */
    protected boolean doesActivityExists() {
//        if (!DaemonEnv.sInitialized) return false;
        PackageManager pm = AppUtils.ctx.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list != null && list.size() > 0;
    }

    /**
     * 安全地启动一个Activity
     */
    protected void startActivitySafely(Activity activityContext) {
        try {
            activityContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}