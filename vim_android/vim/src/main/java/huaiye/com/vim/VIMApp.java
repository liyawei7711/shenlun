package huaiye.com.vim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.baidu.mapapi.SDKInitializer;
import com.huaiye.cmf.JniIntf;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._options.symbols.SDKAccelerateMethod;
import com.huaiye.sdk.sdkabi._options.symbols.SDKAudioAEC;
import com.huaiye.sdk.sdkabi._options.symbols.SDKAudioAGC;
import com.huaiye.sdk.sdkabi._options.symbols.SDKAudioNS;
import com.huaiye.sdk.sdkabi._options.symbols.SDKCaptureQuality;
import com.huaiye.sdk.sdkabi._options.symbols.SDKTransformMethod;
import com.huaiye.sdk.sdpmsgs.social.COfflineMsgToUserReq;
import com.tencent.smtt.sdk.QbSdk;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.CaptureMessage;
import huaiye.com.vim.dao.msgs.JieSuoBean;
import huaiye.com.vim.map.baidu.LocationService;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.ContactsApi;
import huaiye.com.vim.models.contacts.bean.DomainInfoList;
import huaiye.com.vim.models.contacts.bean.MenuListRole;
import huaiye.com.vim.models.map.bean.DaoHangAppInfo;
import huaiye.com.vim.ui.auth.StartActivity;
import huaiye.com.vim.ui.guide.WelcomeActivity;
import huaiye.com.vim.ui.jiesuo.JieSuoActivity;
import huaiye.com.vim.ui.jiesuo.JieSuoResultActivity;
import huaiye.com.vim.ui.jiesuo.JieSuoSetActivity;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vim.common.AppUtils.STRING_KEY_HD;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_HD720P;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_VGA;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_aec;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_agc;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_camera;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_capture;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_capturebianma;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_mPublishPresetoption;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_ns;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_player;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_playerjiema;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_qos;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_recapture;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_tcp;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_trans;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_udp;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_ying;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: VIMApp
 */

public class VIMApp extends MultiDexApplication {

    static VIMApp instance;

    boolean isLogin;//是否已登陆成功

    public LocationService locationService;
    public List<DomainInfoList.DomainInfo> mDomainInfoList;
    public List<DaoHangAppInfo> daoHangAppInfoList;
    public List<DaoHangAppInfo> alldaoHangAppInfoList;

    public List<String> chatLinShiWenJian = new ArrayList<>();

    /**
     * 离线数据
     */
    public List<COfflineMsgToUserReq> linXianBuChang = new ArrayList<>();

    public static List<String> daohangAppPackageNameList = Arrays.asList("com.baidu.BaiduMap", "com.autonavi.minimap", "com.tencent.map", "com.google.android.apps.maps");
    public static List<String> daohangAppNameList = null;

    /**
     * pc 客户端会多次发消息,采集开始的时候缓存起来
     */
    public static ArrayList<String> userId = new ArrayList<>();
    public static ArrayList<CaptureMessage> pendingMsg = new ArrayList<>();

    private MenuListRole menuListRole;

    public MenuListRole getMenuListRole() {
        return menuListRole;
    }

    public void setMenuListRole(MenuListRole menuListRole) {
        this.menuListRole = menuListRole;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        HYClient.initSdk(this);
        SP.init(this);
        AppDatas.init(this);
        AppUtils.init(this);
        initDaoHangData();
        initSetted();
        SDKInitializer.initialize(this);
        locationService = new LocationService(getApplicationContext());

        Logger.setRuntimeExceptionCallback(new Logger.RuntimeExceptionCallback() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
//                AuthApi.get().uploadLogOnCrash(false);
            }
        });

        //初始化X5内核
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);

        registerActivityLifecycleCallbacks(new ActivityLifecycleListener());

        if (BuildConfig.DEBUG) {
//            if (LeakCanary.isInAnalyzerProcess(this)) {
//                return;
//            }
//            LeakCanary.install(this);
        }

        getDomainCodeList();

    }

    private void initDaoHangData() {
        daohangAppNameList = Arrays.asList(this.getString(R.string.string_name_daohang_baidu), AppUtils.getString(R.string.string_name_daohang_gaode), AppUtils.getString(R.string.string_name_daohang_tengxun), AppUtils.getString(R.string.string_name_daohang_google));
    }

    public List<COfflineMsgToUserReq> getLinXianBuChang() {
        return linXianBuChang;
    }

    public void setLinXianBuChang(List<COfflineMsgToUserReq> linXianBuChang) {
        this.linXianBuChang = linXianBuChang;
    }

    public void getDomainCodeList() {
        ContactsApi.get().requestGetDomainInfo(new ModelCallback<DomainInfoList>() {
            @Override
            public void onSuccess(DomainInfoList domainInfoList) {
                if (null != domainInfoList && null != domainInfoList.domainInfoList && domainInfoList.domainInfoList.size() > 0) {
                    mDomainInfoList = domainInfoList.domainInfoList;
                }
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
            }
        });
    }

    public void createFileCacheDir() {
        File fileDir = new File(getExternalFilesDir(null) + File.separator + "Vim");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
    }

    public static VIMApp getInstance() {
        return instance;
    }

    /**
     * 初始化设置
     */
    private void initSetted() {
        //不显示osd
        HYClient.getSdkOptions().Capture().setOSDCommandTemplateStr("");
        HYClient.getSdkOptions().Capture().setOSDCustomCommand("");

        if (TextUtils.isEmpty(SP.getString(STRING_KEY_capture))) {
            SP.putString(STRING_KEY_capture, HYClient.getSdkOptions().Capture().getCaptureQuality().name());
            SP.putInteger(STRING_KEY_mPublishPresetoption, 1);
        }

        changePublishPresetoption();

        if (SP.getInteger(STRING_KEY_qos, -1) == -1) {
            SP.putInteger(STRING_KEY_qos, HYClient.getSdkOptions().Capture().isQOSOpened() ? 1 : 0);
        } else {
            HYClient.getSdkOptions().Capture().setOpenQOS(SP.getInteger(STRING_KEY_qos, -1) == 1);
        }

        if (TextUtils.isEmpty(SP.getString(STRING_KEY_trans))) {
            SP.setParam(STRING_KEY_trans, STRING_KEY_udp);
        }
        if (SP.getString(STRING_KEY_trans).equals(STRING_KEY_tcp)) {
            HYClient.getSdkOptions().Capture().setTransformMethod(SDKTransformMethod.TCP);
            HYClient.getSdkOptions().Player().setTransformMethod(SDKTransformMethod.TCP);
        } else {
            HYClient.getSdkOptions().Capture().setTransformMethod(SDKTransformMethod.UDP);
            HYClient.getSdkOptions().Player().setTransformMethod(SDKTransformMethod.UDP);
        }

        if (TextUtils.isEmpty(SP.getString(STRING_KEY_player))) {
            SP.putString(STRING_KEY_player, STRING_KEY_VGA);
        }

        if (!TextUtils.isEmpty(SP.getString(STRING_KEY_capturebianma))) {
            if (SP.getString(STRING_KEY_capturebianma).equals(STRING_KEY_ying)) {
                HYClient.getSdkOptions().Capture().setAccelerateMethod(SDKAccelerateMethod.Hardware);
            } else {
                HYClient.getSdkOptions().Capture().setAccelerateMethod(SDKAccelerateMethod.Software);
            }
        }
        if (!TextUtils.isEmpty(SP.getString(STRING_KEY_playerjiema))) {
            if (SP.getString(STRING_KEY_playerjiema).equals(STRING_KEY_ying)) {
                HYClient.getSdkOptions().Player().setAccelerateMethod(SDKAccelerateMethod.Hardware);
            } else {
                HYClient.getSdkOptions().Player().setAccelerateMethod(SDKAccelerateMethod.Software);
            }
        }

        if (SP.getLong(STRING_KEY_recapture, (long) -1) == -1) {
            SP.putLong(STRING_KEY_recapture, 0);
        }
        if (SP.getLong(STRING_KEY_recapture, (long) -1) == 1) {
            JniIntf.SetSystemProperty(JniIntf.SYSTEM_PROPERTY_ENABLE_RESAMPLE, 1);
        } else {
            JniIntf.SetSystemProperty(JniIntf.SYSTEM_PROPERTY_ENABLE_RESAMPLE, 0);
        }


        if (SP.getInteger(STRING_KEY_aec, -1) == -1) {
            SP.getInteger(STRING_KEY_aec, HYClient.getSdkOptions().Capture().getAudioEnableAEC().value());
        }
        if (SP.getInteger(STRING_KEY_aec, -1) == 0) {
            HYClient.getSdkOptions().Capture().setAudioEnableAEC(SDKAudioAEC.CLOSE);
        } else {
            HYClient.getSdkOptions().Capture().setAudioEnableAEC(SDKAudioAEC.OPEN);
        }

        if (SP.getInteger(STRING_KEY_agc, -1) == -1) {
            SP.getInteger(STRING_KEY_agc, 0);
        }
        if (SP.getInteger(STRING_KEY_agc, -1) == 0) {
            HYClient.getSdkOptions().Capture().setAudioEnableAGC(SDKAudioAGC.CLOSE);
        } else {
            HYClient.getSdkOptions().Capture().setAudioEnableAGC(SDKAudioAGC.OPEN);
        }

        if (SP.getInteger(STRING_KEY_ns, -1) == -1) {
            SP.getInteger(STRING_KEY_ns, HYClient.getSdkOptions().Capture().getAudioNS().value());
        }
        if (SP.getInteger(STRING_KEY_ns, -1) == 0) {
            HYClient.getSdkOptions().Capture().setAudioNS(SDKAudioNS.CLOSE);
        } else {
            HYClient.getSdkOptions().Capture().setAudioNS(SDKAudioNS.OPEN);
        }

        if (SP.getInteger(STRING_KEY_camera, -1) == -1) {
            SP.putInteger(STRING_KEY_camera, 1);
        }

    }

    /**
     * 改变mPublishPresetoption
     */
    private void changePublishPresetoption() {
        int current = SP.getInteger("mPublishPresetoption");

        switch (SP.getString(STRING_KEY_capture)) {
            case STRING_KEY_VGA:

                if (current == -1) {
                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.VGA)
                                    .setmPublishPresetoption(current)
                                    .setBitrate(SP.getInteger("bitrate") * 8 * 1000)
                    );
                } else {
                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.VGA)
                                    .setmPublishPresetoption(current)
                    );
                }
                break;
            case STRING_KEY_HD:

                if (current == -1) {
                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.HDVGA)
                                    .setmPublishPresetoption(current)
                                    .setBitrate(SP.getInteger("bitrate") * 8 * 1000)
                    );
                } else {

                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.HDVGA)
                                    .setmPublishPresetoption(current)
                    );
                }
                break;
            case STRING_KEY_HD720P:

                if (current == -1) {
                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.HD720P)
                                    .setmPublishPresetoption(current)
                                    .setBitrate(SP.getInteger("bitrate") * 8 * 1000)
                    );
                } else {

                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.HD720P)
                                    .setmPublishPresetoption(current)
                    );
                }
                break;
            case "HD1080P":

                if (current == -1) {
                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.HD1080P)
                                    .setmPublishPresetoption(current)
                                    .setBitrate(SP.getInteger("bitrate") * 8 * 1000)
                    );
                } else {

                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.HD1080P)
                                    .setmPublishPresetoption(current)
                    );
                }
                break;
        }
    }

    Activity current;
    public Context getCurrentActivity() {
        if(current == null){
            return instance;
        } else {
            return current;
        }
    }
    class ActivityLifecycleListener implements ActivityLifecycleCallbacks {

        private int refCount = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            current = activity;
            refCount++;
//            WindowManagerUtils.justReShow();
            if (AppUtils.isHide) {
                AppUtils.isHide = false;
                //应用从后台回到前台 需要做的操作
                if (activity instanceof WelcomeActivity ||
                        activity instanceof StartActivity ||
                        activity instanceof JieSuoActivity ||
                        activity instanceof JieSuoResultActivity ||
                        activity instanceof JieSuoSetActivity) {
                } else {
                    JieSuoBean bean = AppDatas.MsgDB().getJieSuoDao().queryOneItem(AppAuth.get().getUserID(), AppAuth.get().getDomainCode());
                    if (bean != null && bean.isJieSuo) {
                        activity.startActivity(new Intent(activity, JieSuoActivity.class));
                    }
                }
            }

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            refCount--;
            AppUtils.isHide = false;
            if (refCount == 0) {
//                WindowManagerUtils.justRemove();
                AppUtils.isHide = true;
            }


        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }

    public void getDaoHangAppList() {
        new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal<List<DaoHangAppInfo>>() {
            @Override
            public List<DaoHangAppInfo> doOnThread() {
                if (null == alldaoHangAppInfoList) {
                    alldaoHangAppInfoList = AppUtils.getAllUnInstallDaoHangAppList();
                }
                return AppUtils.getDaoHangAppList();
            }

            @Override
            public void doOnMain(List<DaoHangAppInfo> data) {
                daoHangAppInfoList = AppUtils.getDaoHangAppList();
            }
        });
    }

    public void addLinShiFile(String str) {
        if (chatLinShiWenJian.contains(str)) {
            chatLinShiWenJian.add(str);
        }
    }

    public void removeLinShiFile() {
        for (String str : chatLinShiWenJian) {
            File file = new File(str);
            if (file.exists()) {
                deleteFile(str);
            }
        }
        chatLinShiWenJian.clear();
    }

}
