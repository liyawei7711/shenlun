package huaiye.com.vim.ui.guide;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import java.util.ArrayList;
import java.util.Locale;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.ui.auth.ActivationActivity;
import huaiye.com.vim.ui.auth.StartActivity;

@BindLayout(R.layout.activity_welcome)
public class WelcomeActivity extends AppBaseActivity {

    int REQUEST_EXTERNAL_STORAGE = 1;
    String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @BindView(R.id.iv_imge)
    View iv_imge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNavigate().setVisibility(View.GONE);

        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = Locale.ENGLISH;
        config.locale = Locale.US;
        resources.updateConfiguration(config, dm);

        new RxUtils<>().doDelay(1000, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                iv_imge.setVisibility(View.GONE);

                checkPermission();
            }
        }, "WelcomeActivity1");
//        AuthApi.get().uploadLog(false);
    }

    @Override
    protected void initActionBar() {

    }

    @Override
    public void doInitDelay() {

    }

    boolean checkPermission1() {
        ArrayList<String> permissions = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }

        return permissions.size() == 0;

    }

    /**
     * 检测权限
     */
    private void checkPermission() {
        boolean needReq = false;
        for (int i = 0; i < PERMISSIONS_STORAGE.length; i++) {
            if (PackageManager.PERMISSION_GRANTED !=
                    ContextCompat.checkSelfPermission(WelcomeActivity.this, PERMISSIONS_STORAGE[i])) {
                needReq = true;
            }
        }
        if (needReq) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            gotoNext();
        }
    }

    @OnClick(R.id.tv_next)
    public void onClick(View view) {
        gotoNext();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isAllAgree = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                isAllAgree = false;
            }
        }

        if (!isAllAgree) {
            showToast(getString(R.string.notice_txt_1));
            checkPermission();
        } else {
            gotoNext();
        }
    }

    private void gotoNext() {
        if (!isTaskRoot()) {
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            // 是否从Launcher启动
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                //finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception
                finish();
                return;
            }
        }

        getNavigate().setVisibility(View.GONE);

        // 权限不足
        boolean isPermissionGranted = checkPermission1();
        // 自动登录
        String account = AppDatas.Auth().getUserLoginName();
        String password = AppDatas.Auth().getPassword();

        boolean hasActived = SP.getBoolean("actived", false);
        boolean autoLogin = AppDatas.Auth().getAutoLogin();

        startLogin();

//        if (TextUtils.isEmpty(account)
//                || TextUtils.isEmpty(password)
//                || !isPermissionGranted || !autoLogin || !hasActived) {
//            startLogin();
//        } else {
//            AuthApi.get().login(this, false, account, password, new ModelCallback<AuthUser>() {
//
//                @Override
//                public void onSuccess(AuthUser authUser) {
//                    /*new RxUtils().doDelay(1000, new RxUtils.IMainDelay() {
//                        @Override
//                        public void onMainDelay() {
//                            startActivity(new Intent(getSelf(), MainActivity.class));
//                            finish();
//                        }
//                    }, "start_main");*/
//                    startActivity(new Intent(getSelf(), MainActivity.class));
//                    finish();
//                    StartActivity.encryptInit();
//                }
//
//                @Override
//                public void onFailure(HTTPResponse httpResponse) {
//                    super.onFailure(httpResponse);
//                    startLogin();
//                }
//            });
//        }
        /*if (SP.getBoolean("isAppFirstStarted", true)) {
            // 第一次启动
            SP.putBoolean("isAppFirstStarted", false);
            new RxUtils().doDelay(1500, new RxUtils.IMainDelay() {
                @Override
                public void onMainDelay() {
                    startActivity(new Intent(getSelf(), GuideActivity.class));
                    finish();
                }
            }, "start");
        } else {

            // 权限不足
            boolean isPermissionGranted = checkPermission1();
            // 自动登录
            String account = AppDatas.Auth().getAccount();
            String password = AppDatas.Auth().getPassword();

            boolean autoLogin = AppDatas.Auth().getAutoLogin();
            if (TextUtils.isEmpty(account)
                    || TextUtils.isEmpty(password)
                    || !isPermissionGranted || !autoLogin) {
                startLogin();
            } else {
                AuthApi.get().login(this, false, account, password, new ModelCallback<AuthUser>() {

                    @Override
                    public void onSuccess(AuthUser authUser) {
                        new RxUtils().doDelay(1000, new RxUtils.IMainDelay() {
                            @Override
                            public void onMainDelay() {
                                startActivity(new Intent(getSelf(), MainActivity.class));
                                finish();
                            }
                        }, "start_main");
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        startLogin();
                    }
                });
            }
        }*/
    }

    boolean isJump = false;

    private void startLogin() {

        if (isJump) {
            return;
        }
        isJump = true;
        
        final boolean hasActived = SP.getBoolean("actived", false);
        new RxUtils().doDelay(1500, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
//                startActivity(new Intent(getSelf(), LoginActivity.class));
                startActivity(new Intent(getSelf(), (!hasActived && AppUtils.nEncryptIMEnable) ? ActivationActivity.class : StartActivity.class).putExtra("from", ""));
                finish();
            }
        }, "start_login");
    }

}
