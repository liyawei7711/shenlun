package huaiye.com.vim.ui.auth;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import java.util.ArrayList;

import huaiye.com.vim.BuildConfig;
import huaiye.com.vim.R;
import huaiye.com.vim.VIMApp;
import huaiye.com.vim.bus.SosBean;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.map.baidu.LocationService;
import huaiye.com.vim.models.CommonResult;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.auth.AuthApi;
import huaiye.com.vim.models.auth.bean.AuthUser;
import huaiye.com.vim.ui.home.MainActivity;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vim.common.AppUtils.STRING_KEY_jiami;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_resetpwd;
import static huaiye.com.vim.common.AppUtils.ctx;

/**
 * Created by ywt on 2019/2/21.
 */

@BindLayout(R.layout.activity_start)
public class StartActivity extends AppBaseActivity {
    @BindView(R.id.rl_root)
    RelativeLayout rl_root;
    @BindView(R.id.edt_account)
    EditText edt_account;
    @BindView(R.id.edt_password)
    EditText edt_password;
    @BindView(R.id.view_load)
    View view_load;
    @BindView(R.id.ll_login)
    View ll_login;
    @BindView(R.id.config_server)
    TextView start_config_service;
    @BindView(R.id.view1)
    View view1;
    @BindView(R.id.view2)
    View view2;
    @BindView(R.id.ll_btn_account)
    View ll_btn_account;
    @BindView(R.id.ll_btn_pwd)
    View ll_btn_pwd;

    private AlertDialog nAlertDialog;
    private LocationService locationService;

    RelativeLayout.LayoutParams lp;
    /**
     * 虚拟键盘高度
     */
    int virtualKeyboardHeight;
    /**
     * 屏幕高度
     */
    int screenHeight;
    /**
     * 屏幕6分之一的高度，作用是防止获取到虚拟键盘的高度
     */
    int screenHeight6;
    View rootView;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationService.stop(); //停止定位服务
    }

    @Override
    protected void initActionBar() {

        VIMApp.getInstance().setLogin(false);

        getNavigate().setVisibility(View.GONE);
//        ConfigApi.get().getAllConfig(null);
        locationService = ((VIMApp) getApplication()).locationService;
        locationService.start();

        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenHeight6 = screenHeight / 6;
        rootView = getWindow().getDecorView();
    }

    @Override
    public void doInitDelay() {
        SP.putBoolean(STRING_KEY_jiami, true);

        lp = (RelativeLayout.LayoutParams) ll_login.getLayoutParams();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                /**
                 * 回调该方法时rootView还未绘制，需要设置绘制完成监听
                 */
                rootView.post(new Runnable() {
                    @Override
                    public void run() {
                        Rect rect = new Rect();
                        /**
                         * 获取屏幕底部坐标
                         */
                        rootView.getWindowVisibleDisplayFrame(rect);
                        /**
                         * 获取键盘的高度
                         */
                        int heightDifference = screenHeight - rect.bottom;
                        if (heightDifference < screenHeight6) {
                            virtualKeyboardHeight = heightDifference;
                            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                lp.removeRule(RelativeLayout.BELOW);
                            }
                        } else {
                            lp.addRule(RelativeLayout.BELOW, R.id.ll_view);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                lp.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            }
                        }
                        ll_login.setLayoutParams(lp);
                    }
                });
            }
        });

        view1.setOnLongClickListener(olcl);
        view2.setOnLongClickListener(olcl);
        start_config_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getSelf(), SettingAddressActivity.class));
            }
        });
        edt_account.setText(AppDatas.Auth().getUserLoginName());
        edt_password.setText(AppDatas.Auth().getPassword());
        edt_account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edt_password.setText("");
            }
        });

        checkPermission();
    }

    @OnClick({R.id.ll_login,
//            R.id.login_account_clear,
            R.id.tv_back,
            R.id.config_server})
    void onBtnClicked(View view) {
        if (view_load.getVisibility() == View.VISIBLE) {
            showToast(getString(R.string.logining));
            return;
        }

        switch (view.getId()) {
            case R.id.ll_login:
                if (ll_btn_account.getVisibility() == View.VISIBLE) {
                    ll_btn_pwd.setVisibility(View.VISIBLE);
                    ll_btn_account.setVisibility(View.GONE);
                } else {
                    // 登录
                    login();
                }
                break;
            case R.id.tv_back:
                ll_btn_account.setVisibility(View.VISIBLE);
                ll_btn_pwd.setVisibility(View.GONE);
                break;
//            case R.id.login_account_clear:
//                edt_account.setText("");
//                break;
            case R.id.config_server:
                //设置服务器
                startActivity(new Intent(getSelf(), SettingAddressActivity.class));
                break;
            default:
                break;
        }
    }

    void login() {
        if (TextUtils.isEmpty(edt_account.getText())
                || TextUtils.isEmpty(edt_password.getText())) {
            showToast(getString(R.string.account_pwd_empty));
            return;
        }

        if (edt_password.getText().toString().trim().equalsIgnoreCase("sos")) {
            AuthApi.get().sos(this, edt_account.getText().toString(), new AuthApi.ISosListener() {
                @Override
                public void onSuccess(CommonResult response) {
                    view_load.setVisibility(View.GONE);
                    AppAuth.get().clearData(StartActivity.this);
//                    startActivity(new Intent(getSelf(), MainActivity.class).putExtra("isSOS", true).putExtra("isFromLogin", true));

                    finish();
                }

                @Override
                public void onFailure(HTTPResponse response) {

                }
            });
        } else {
            ModelApis.Auth().login(this, true, edt_account.getText().toString(),
                    edt_password.getText().toString(),
                    new ModelCallback<AuthUser>() {

                        @Override
                        public void onPreStart(HTTPRequest httpRequest) {
                            super.onPreStart(httpRequest);
                            view_load.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onSuccess(AuthUser authUser) {
//                            encryptInit();
                            view_load.setVisibility(View.GONE);
                            boolean isReSetPwd = SP.getBoolean(STRING_KEY_resetpwd, false);
                            if(BuildConfig.DEBUG) {
                                isReSetPwd = true;
                            }
                            if (isReSetPwd) {
                                AppAuth.get().setAutoLogin(true);
                                Intent intent = getIntent();
                                intent.setClass(getSelf(), MainActivity.class);
                                intent.putExtra("isFromLogin", true);
                                startActivity(intent);
                            } else {
                                startActivity(new Intent(getSelf(), ChangePwdActivity.class).putExtra("isQiangZhi", true));
                            }

                            finish();
                        }

                        @Override
                        public void onFailure(HTTPResponse httpResponse) {
                            super.onFailure(httpResponse);
                            view_load.setVisibility(View.GONE);
                            //被取消了就啥都不干
                            if (httpResponse.getHttpRequest() != null && httpResponse.getHttpRequest().isCanceled()) {
                                return;
                            }
                            if (httpResponse.getStatusCode() == 0 && !TextUtils.isEmpty(httpResponse.getErrorMessage())) {
                                showToast(httpResponse.getErrorMessage());
                            } else {
                                showToast(ErrorMsg.getMsg(ErrorMsg.login_err_code));
                            }
                        }
                    });
        }
    }

    void checkPermission() {
        ArrayList<String> permissions = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }

        if (permissions.size() > 0) {
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[]{}), 1000);
        }

    }

    long lastMillions = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

//            long currentMillions = System.currentTimeMillis();
//            long delta = currentMillions - lastMillions;
//            lastMillions = currentMillions;
//            if (delta < 2000) {
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

    View.OnLongClickListener olcl = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            if (start_config_service.getVisibility() == View.VISIBLE) {
                start_config_service.setVisibility(View.GONE);
            } else {
                start_config_service.setVisibility(View.VISIBLE);
            }
            return false;
        }
    };

}
