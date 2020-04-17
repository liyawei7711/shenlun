package huaiye.com.vim.models.download;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import java.io.File;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;

/**
 * Created by liyawei on 15-12-24.
 * phone 18952280597
 * QQ    751804582
 */

@BindLayout(R.layout.activity_installdialog)
public class InstallDialogActivity extends AppBaseActivity {

    public static final int REQUEST_INSTALL_UNKNOW_APP = 188;
    public static String APK_PATH = "apk_path";
    public static String INSTALL_INFO = "install_info";

    @BindView(R.id.tv_cancel)
    TextView tv_cancel;
    @BindView(R.id.tv_confirm)
    TextView tv_confirm;
    @BindView(R.id.tv_info)
    TextView tv_info;
    @BindView(R.id.tv_title)
    TextView tv_title;

    private String apkPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        setFinishOnTouchOutside(false);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int) (AppUtils.getScreenWidth() * 0.8f);
        getWindow().setAttributes(params);

        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void doInitDelay() {
        initListener();

    }

    protected void initListener() {
        apkPath = getIntent().getStringExtra(APK_PATH);
        if (haveInstallPermission()) {
            showInstallInfo();
        } else {
            showAllowInstallTip();
        }

    }


    private boolean haveInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean canRequestPackageInstall = getPackageManager().canRequestPackageInstalls();
            return canRequestPackageInstall;
        }
        return true;
    }

    /**
     * 展示申请安装权限信息
     */
    private void showAllowInstallTip() {
        tv_title.setText(getString(R.string.install_notice1) + AppUtils.getString(R.string.app_name) + getString(R.string.install_notice2));
        tv_info.setText(getString(R.string.install_notice3));
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSetting();
            }
        });
    }

    private void toSetting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean canRequestPackageInstall = getPackageManager().canRequestPackageInstalls();
            if (!canRequestPackageInstall) {
                Uri packageURI = Uri.parse("package:" + getPackageName());
                //注意这个是8.0新API
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                startActivityForResult(intent, REQUEST_INSTALL_UNKNOW_APP);
                return;
            }
        }
    }


    /**
     * 暂时app安装提示界面
     */
    private void showInstallInfo() {
        tv_title.setText(getString(R.string.install_notice4));
        tv_info.setText(getString(R.string.install_notice5));
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (apkPath == null) {
                    onBackPressed();
                    return;
                }
                InstallUtil.installNormal(InstallDialogActivity.this, new File(apkPath), true);
                onBackPressed();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INSTALL_UNKNOW_APP) {
            boolean canRequestPackageInstall = false;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                canRequestPackageInstall = getPackageManager().canRequestPackageInstalls();
                if (canRequestPackageInstall) {
                    InstallUtil.installNormal(InstallDialogActivity.this, new File(apkPath), true);
                    onBackPressed();
                }
            }

        }
    }
}
