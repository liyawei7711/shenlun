package huaiye.com.vim.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;

import com.huaiye.cmf.sdp.SdpMessageCmCtrlReq;
import com.huaiye.cmf.sdp.SdpMessageCmCtrlRsp;
import com.huaiye.cmf.sdp.SdpMessageCmInitRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiAuth;
import com.huaiye.sdk.sdkabi._api.ApiEncrypt;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import huaiye.com.vim.R;
import huaiye.com.vim.VIMApp;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.bus.NewMessageNum;
import huaiye.com.vim.bus.ReafBean;
import huaiye.com.vim.bus.SimpleViewBean;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppBaseFragment;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.common.views.AppRadioButton;
import huaiye.com.vim.common.views.WindowManagerUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.CaptureMessage;
import huaiye.com.vim.dao.msgs.ChatUtil;
import huaiye.com.vim.dao.msgs.JieSuoBean;
import huaiye.com.vim.dao.msgs.StopCaptureMessage;
import huaiye.com.vim.map.baidu.LocationService;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.auth.AuthApi;
import huaiye.com.vim.models.config.ConfigApi;
import huaiye.com.vim.models.config.bean.GetConfigResponse;
import huaiye.com.vim.push.MessageReceiver;
import huaiye.com.vim.services.MusicService;
import huaiye.com.vim.ui.Capture.CaptureGuanMoOrPushActivity;
import huaiye.com.vim.ui.auth.ActivationActivity;
import huaiye.com.vim.ui.auth.SettingAddressSafeActivity;
import huaiye.com.vim.ui.auth.StartActivity;
import huaiye.com.vim.ui.chat.dialog.CustomTipDialog;
import huaiye.com.vim.ui.contacts.sharedata.VimChoosedContacts;
import huaiye.com.vim.ui.fenxiang.ShareChooseActivity;
import huaiye.com.vim.ui.jiesuo.JieSuoActivity;

import static huaiye.com.vim.common.AppUtils.STRING_KEY_encrypt;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_jiami;
import static huaiye.com.vim.common.AppUtils.ctx;
import static huaiye.com.vim.common.AppUtils.mDeviceIM;
import static huaiye.com.vim.common.AppUtils.mJiaMiMiMa;
import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: MainActivity
 */
@BindLayout(R.layout.activity_main)
public class MainActivity extends AppBaseActivity {
    AppBaseFragment currentFragment;

    FragmentMessages fgMessages;
    FragmentContacts fgContacts;
    FragmentSettings fgSettings;

    @BindView(R.id.view1)
    View view1;
    @BindView(R.id.view2)
    View view2;
    @BindView(R.id.view3)
    View view3;
    @BindView(R.id.rg_menu)
    RadioGroup rg_menu;
    @BindView(R.id.rbtn_home)
    AppRadioButton rbtn_home;
    @BindView(R.id.rbtn_contacts)
    AppRadioButton rbtn_contacts;
    /*@BindView(R.id.rbtn_messages)
    AppRadioButton rbtn_messages;*/
    @BindView(R.id.rbtn_settings)
    AppRadioButton rbtn_settings;
    @BindExtra
    String from;
    @BindExtra
    boolean isFromLogin;
    @BindExtra
    boolean isSOS;
    private AlertDialog nAlertDialog;

    Bundle savedInstanceState;
    private LocationService locationService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);

        VIMApp.getInstance().setLogin(true);

        encryptInit();

        if (!isFromLogin) {
            JieSuoBean bean = AppDatas.MsgDB().getJieSuoDao().queryOneItem(AppAuth.get().getUserID(), AppAuth.get().getDomainCode());
            if (bean != null && bean.isJieSuo) {
                startActivity(new Intent(this, JieSuoActivity.class));
            }
        }

        VIMApp.getInstance().getDaoHangAppList();
        VIMApp.getInstance().createFileCacheDir();
        registerHomeKeyReceiver(this);
        EventBus.getDefault().register(this);
        rg_menu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                AppRadioButton rbtn = (AppRadioButton) findViewById(checkedId);
                if (!rbtn.isChecked()) {
                    return;
                }

                FragmentTransaction fts = getSupportFragmentManager().beginTransaction();

                fts.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .hide(fgContacts)
                        .hide(fgMessages)
                        .hide(fgSettings);
                resetPopWindow();
                switch (checkedId) {
                    case R.id.rbtn_contacts:
                        fts.show(fgContacts);
                        changeIsRead();
                        currentFragment = fgContacts;

                        view1.setVisibility(View.INVISIBLE);
                        view2.setVisibility(View.INVISIBLE);
                        view3.setVisibility(View.VISIBLE);
                        break;
                    /*case R.id.rbtn_messages:
                        fts.show(fgMessages);
                        currentFragment = fgMessages;
                        break;*/
                    case R.id.rbtn_settings:
                        fts.show(fgSettings);
                        changeIsRead();
                        currentFragment = fgSettings;

                        view1.setVisibility(View.INVISIBLE);
                        view2.setVisibility(View.VISIBLE);
                        view3.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.rbtn_home:
                        fts.show(fgMessages);
                        changeIsRead();
                        currentFragment = fgMessages;

                        view1.setVisibility(View.VISIBLE);
                        view2.setVisibility(View.INVISIBLE);
                        view3.setVisibility(View.INVISIBLE);
                        break;
                }

                try {
                    fts.commitAllowingStateLoss();
                } catch (Exception e) {

                }
            }
        });

        addFragments(savedInstanceState);

        /* 1. 从业务服务器获取配置，并检测是否需要升级
           2. crash 日志文件上传
        */
        doInitHttpService();

        if (isSOS) {
            clearSafe();
        }

        startService(new Intent(this, MusicService.class));

        if (!TextUtils.isEmpty(from)) {
            Intent intent = getIntent();
            intent.setClass(getSelf(), ShareChooseActivity.class);
            startActivity(intent);
        }
    }

    private void clearSafe() {
        MessageReceiver.destoryKey(null, true);
        showToast(getString(R.string.notice_txt_3));
    }

    private void resetPopWindow() {
        if (null != fgContacts) {
            fgContacts.dimissChatMoreStylePopupWindow();
        }
        if (null != fgMessages) {
            fgMessages.dimissChatMoreStylePopupWindow();
        }
        if (null != fgSettings) {
            fgSettings.dimissChatMoreStylePopupWindow();
        }
    }

    @Override
    protected void initActionBar() {
        locationService = ((VIMApp) getApplication()).locationService;
        locationService.start();

        getNavigate().hideLeftIcon()
                .hideRightIcon()
                .showTopSearch()
                .showTopAdd()
                .setReserveStatusbarPlace()
                .setTitlText(getString(R.string.app_name))
                .setTitleLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        startActivity(new Intent(MainActivity.this, SettingAddressSafeActivity.class));
                        return false;
                    }
                })
                .setTopSearchClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(currentFragment instanceof  FragmentMessages) {
                            fgMessages.onClickSearch();
                        } else if(currentFragment instanceof  FragmentContacts) {
                            fgContacts.onClickSearch();
                        } else if(currentFragment instanceof  FragmentSettings) {
//                            fgSettings.onClickSearch();
                        }
                    }
                }).setTopAddClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSOS) {
                    return;
                }
                currentFragment.setIsSoS(isSOS);
                currentFragment.changeMenu(currentFragment instanceof  FragmentSettings);
                currentFragment.showChatMoreStylePopupWindow(v);
            }
        });

    }

    @Override
    public void doInitDelay() {
        showSystemSetting();
    }

    private void showSystemSetting() {
        int showToWhiteVersion = SP.getInteger("showToWhiteVersion", 0);
        if (showToWhiteVersion < AppUtils.versionCode()) {
//            IntentWrapper.whiteListMatters(this, null);
            SP.putInt("showToWhiteVersion", AppUtils.versionCode());
        }
    }

    private void changeIsRead() {
//        if (currentFragment instanceof FragmentMessages)
//            fgMessages.refMessage();
    }

    /* 从业务服务器获取配置，并检测是否需要升级 */
    private void doInitHttpService() {
        ConfigApi.get().getAllConfig(new ModelCallback<GetConfigResponse>() {
            @Override
            public void onSuccess(GetConfigResponse response) {
                ModelApis.Auth().requestVersion(MainActivity.this, null);
                ModelApis.Auth().uploadLog(false, AppDatas.Constants().getFileUploadUri());
            }
        });
    }

    void addFragments(Bundle savedInstanceState) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fts = fm.beginTransaction();
        if (savedInstanceState != null) {
            fgContacts = (FragmentContacts) fm.findFragmentByTag(FragmentContacts.class.getSimpleName());
            fgMessages = (FragmentMessages) fm.findFragmentByTag(FragmentMessages.class.getSimpleName());
            fgSettings = (FragmentSettings) fm.findFragmentByTag(FragmentSettings.class.getSimpleName());
        } else {
            fgContacts = new FragmentContacts();
            fgContacts.setSos(isSOS);
            fgMessages = new FragmentMessages();
            fgMessages.setSos(isSOS);
            fgSettings = new FragmentSettings();
//            fgSettings.setSos(isSOS);

            fts.add(R.id.content, fgContacts, FragmentContacts.class.getSimpleName())
                    .hide(fgContacts)
                    .add(R.id.content, fgMessages, FragmentMessages.class.getSimpleName())
                    .hide(fgMessages)
                    .add(R.id.content, fgSettings, FragmentSettings.class.getSimpleName())
                    .hide(fgSettings);
        }

        switch (rg_menu.getCheckedRadioButtonId()) {
            case R.id.rbtn_contacts:
                currentFragment = fgContacts;
                fts.show(fgContacts);
                break;
            case R.id.rbtn_home:
                currentFragment = fgMessages;
                fts.show(fgMessages);
                break;
            /*case R.id.rbtn_messages:
                fts.show(fgMessages);
                break;*/
            case R.id.rbtn_settings:
                currentFragment = fgSettings;
                fts.show(fgSettings);
                break;
        }

        try {
            fts.commitAllowingStateLoss();
        } catch (Exception e) {

        }
    }

    public void resetMessageNumbers() {
        rbtn_home.setUnReadNumber(0);
    }

    @Override
    protected void onResume() {
        checkDrawOverlaysPermission();
        super.onResume();
    }

    protected void checkDrawOverlaysPermission() {
        if (Build.VERSION.SDK_INT >= 23) {

            if (!Settings.canDrawOverlays(ctx)) {
                if (null == nAlertDialog) {
                    nAlertDialog = new AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setTitle(AppUtils.getString(R.string.notice))
                            .setMessage(AppUtils.getString(R.string.has_connected_false))
                            .setPositiveButton(AppUtils.getString(R.string.makesure), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int w) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            })
                            .show();
                } else {
                    if (!nAlertDialog.isShowing()) {
                        nAlertDialog.show();
                    }
                }
                return;
            } else {
                if (null != nAlertDialog && nAlertDialog.isShowing()) {
                    nAlertDialog.dismiss();
                    nAlertDialog = null;
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(StopCaptureMessage bean) {
        if (HYClient.getHYCapture().isCapturing() && VIMApp.userId.size() > 0) {
            if (VIMApp.userId.contains(bean.fromUserId)) {
                VIMApp.userId.remove(bean.fromUserId);
            }
            if (VIMApp.userId.size() <= 0) {
                HYClient.getHYCapture().stopCapture(null);
                HYClient.getSdkOptions().Capture().setCaptureOfflineMode(false);
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_MESSAGE_CLOSE_ACPTURE));
                WindowManagerUtils.closeAll();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NewMessageNum bean) {
        rbtn_home.setUnReadNumber(bean.num);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CaptureMessage bean) {
        if (null == bean) {
            return;
        }

        if(!HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            AppBaseActivity.showToast(getString(R.string.common_notice67));
            return;
        }

        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable && HYClient.getHYCapture().isCapturing()) {
            if (!VIMApp.userId.contains(bean.fromUserId)) {
                VIMApp.userId.add(bean.fromUserId);
            }
            ChatUtil.get().rspGuanMo(bean.fromUserId, bean.fromUserDomain, bean.fromUserName, bean.sessionID);
        } else {
            if (HYClient.getHYCapture().isCapturing()) {
                if (!VIMApp.userId.contains(bean.fromUserId)) {
                    VIMApp.userId.add(bean.fromUserId);
                }
                ChatUtil.get().rspGuanMo(bean.fromUserId, bean.fromUserDomain, bean.fromUserName, bean.sessionID);
            } else {
                Intent intent = new Intent(this, CaptureGuanMoOrPushActivity.class);
                intent.putExtra("captureMessage", bean);
                startActivity(intent);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SimpleViewBean bean) {
        if (bean.captureModel == WindowManagerUtils.CaptureModel.CAPTURE_GUANMO_MODEL) {
            Intent intent = new Intent(ctx, CaptureGuanMoOrPushActivity.class);
            intent.putExtra("captureMessage", (CaptureMessage) bean.data);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        } else if (bean.captureModel == WindowManagerUtils.CaptureModel.CAPTURE_PUSH_MODEL) {
            Intent intent = new Intent(ctx, CaptureGuanMoOrPushActivity.class);
            intent.putExtra("userList", (ArrayList<SendUserBean>) bean.data);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent bean) {

        if (null != bean && bean.what == AppUtils.EVENT_RPUSH_VIDEO && null != bean.obj1) {
            ArrayList<SendUserBean> userList = (ArrayList<SendUserBean>) bean.obj1;
            if (HYClient.getHYCapture().isCapturing()) {
                for (SendUserBean sendUserBean : userList) {
                    if (!VIMApp.userId.contains(sendUserBean.strUserID)) {
                        VIMApp.userId.add(sendUserBean.strUserID);
                    }
                }

                ChatUtil.get().broadcastPushVideo(userList);

            } else {
                Intent intent = new Intent(this, CaptureGuanMoOrPushActivity.class);
                intent.putExtra("userList", userList);
                startActivity(intent);
            }

        } else if (null != bean && bean.what == AppUtils.EVENT_INIT_FAILED) {
            if (bean.arg0 == -15 || bean.arg0 == -4) {
                SP.putBoolean("actived", false);
            }
            showExitWarning(bean.arg0, getString(R.string.device_notice5));
        } else if (null != bean && bean.what == AppUtils.EVENT_INIT_KITOUT) {
            SP.putBoolean("actived", false);
            showExitWarning(bean.arg0, getString(R.string.device_notice4));
        }
    }

    protected void showExitWarning(int errCode, String str) {
        CustomTipDialog exitWarning;
        exitWarning = new CustomTipDialog(this, str);
        exitWarning.hideLeftFunctionText();

        exitWarning.setOnFunctionClickedListener(new CustomTipDialog.IFunctionClickedListener() {
            @Override
            public void onClickedLeftFunction() {
            }

            @Override
            public void onClickedRightFunction() {
                exitWarning.dismiss();

                HYClient.getModule(ApiAuth.class).logout(null);
                AppAuth.get().setAutoLogin(false);
                VimChoosedContacts.get().destory();

                if(errCode == -1000) {
                    SP.putBoolean("actived", true);
                    MessageReceiver.get().kitOutUser(false);
                    finish();
                    return;
                }

                Intent intent = new Intent(MainActivity.this, ((errCode == -15 || errCode == -4 || errCode == 81 || errCode == 20) && nEncryptIMEnable) ? ActivationActivity.class : StartActivity.class);
                intent.putExtra("from", "");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                MainActivity.this.startActivity(intent);
                MainActivity.this.finish();
            }
        });
        exitWarning.setCancelable(false);

        try {
            exitWarning.show();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    long lastMillions = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            long currentMillions = System.currentTimeMillis();
//            long delta = currentMillions - lastMillions;
//            lastMillions = currentMillions;
//            if (delta < 2000) {
//                isClose = true;
//                // 登出操作
//                HYClient.getModule(ApiAuth.class).logout(null);
//                sendBroadcast(new Intent("com.huaiye.mc.exitapp"));
//                finish();
//                return super.onKeyDown(keyCode, event);
//            }
//
//            showToast(getString(R.string.login_error8));
            AppUtils.goToDesktop(this);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 改变红点
     */
    public void changeRedCircle(int count) {
        rbtn_home.setUnReadNumber(count);
    }

    @Override
    protected void onDestroy() {
        HYClient.getSdkOptions().encrypt().setEncryptBind(false);
        if (null != nAlertDialog && nAlertDialog.isShowing()) {
            nAlertDialog.dismiss();
            nAlertDialog = null;
        }
        unregisterHomeKeyReceiver(this);
        WindowManagerUtils.closeAll();
        locationService.stop(); //停止定位服务
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    void encryptInit() {
        if (HYClient.getSdkOptions().encrypt().isEncryptBind()) {
            return;
        }

        if (!nEncryptIMEnable) {
            return;
        }

        encryptInit(new SdkCallback<SdpMessageCmCtrlRsp>() {
            @Override
            public void onSuccess(SdpMessageCmCtrlRsp sdpMessageCmRegisterUserRsp) {
//                HYClient.getSdkOptions().encrypt().setEncrypt(true);
                if (sdpMessageCmRegisterUserRsp.m_nCtrlId == SdpMessageCmCtrlReq.CM_CTRL_ID_BIND_USER) {
                    AuthApi.get().userBindNotify();
                }
                HYClient.getSdkOptions().encrypt().setEncryptBind(true);
                MessageReceiver.get().doOffinMsg();
                EventBus.getDefault().post(new ReafBean());
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                SP.setParam(STRING_KEY_encrypt, 1);
                HYClient.getSdkOptions().encrypt().setEncryptBind(false);
                showExitWarning(errorInfo.getCode());
            }
        });

    }

    void encryptInit(final SdkCallback<SdpMessageCmCtrlRsp> resps) {
//        if(BuildConfig.DEBUG) {
//            return;
//        }
        HYClient.getSdkOptions().encrypt().setEncryptBind(false);
        if (!SP.getBoolean(STRING_KEY_jiami, false)) {
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                HYClient.getModule(ApiEncrypt.class).encryptInit(SdkParamsCenter.Encrypt.EncryptInit()
                                .setUserId(HYClient.getSdkOptions().User().getUserId())    // 软件加密需要设置
                                .setUserDomainCode(HYClient.getSdkOptions().User().getDomainCode())  // 软件加密需要设置
                                .setPsw(SP.getString(mJiaMiMiMa, AppUtils.nEncryptPasswd))  // 密码，目前缺省密码为11111111111111111111111111111111
                                .setSoftwareEncrypt(true) //软件加密必须设置
                                .setDevId(SP.getString(mDeviceIM, AppUtils.nEncryptDevice))  // 设置为手机的IMEI  AppUtils.getIMEIResult(VIMApp.getInstance())
                        , new SdkCallback<SdpMessageCmInitRsp>() {
                            @Override
                            public void onSuccess(SdpMessageCmInitRsp resp) {
                                HYClient.getModule(ApiEncrypt.class).encryptBind(new SdkCallback<SdpMessageCmCtrlRsp>() {
                                    @Override
                                    public void onSuccess(SdpMessageCmCtrlRsp resp) {
                                        if (resps != null) {
                                            resps.onSuccess(resp);
                                        }
                                    }

                                    @Override
                                    public void onError(ErrorInfo error) {
                                        if (resps != null) {
                                            resps.onError(error);
                                            showExitWarning(error.getCode());
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(ErrorInfo error) {
                                if (resps != null) {
                                    resps.onError(error);
                                }
                                showExitWarning(error.getCode());
                            }
                        });
            }
        }, 2000);
    }

    void showExitWarning(int errCode) {
        MessageEvent nMessageEvent = new MessageEvent(AppUtils.EVENT_INIT_FAILED);
        nMessageEvent.arg0 = errCode;
        EventBus.getDefault().post(nMessageEvent);
    }

}
