package huaiye.com.vim.ui.auth;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import huaiye.com.vim.BuildConfig;
import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.SP;

import static huaiye.com.vim.common.AppUtils.mDeviceIM;
import static huaiye.com.vim.common.AppUtils.mJiaMiMiMa;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: SettingAddressActivity
 */
@BindLayout(R.layout.activity_activation)
public class ActivationActivity extends AppBaseActivity {
    @BindView(R.id.tv_hint1)
    TextView tv_hint1;
    @BindView(R.id.tv_hint2)
    TextView tv_hint2;
    @BindView(R.id.edt_old_pwd)
    EditText edt_dev_id;
    @BindView(R.id.edt_new_pwd)
    EditText edt_new_pwd;
    @BindView(R.id.tv_sure)
    View tv_sure;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(getString(R.string.device_notice1))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        tv_hint1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_dev_id.requestFocus();
                AppUtils.showKeyboard(edt_dev_id);
                edt_dev_id.setSelection(edt_dev_id.getText().toString().length());
            }
        });

        tv_hint2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_new_pwd.requestFocus();
                AppUtils.showKeyboard(edt_new_pwd);
                edt_new_pwd.setSelection(edt_new_pwd.getText().toString().length());
            }
        });

        edt_dev_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    tv_hint1.setHint(getString(R.string.device_notice2));
                } else {
                    tv_hint1.setHint("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        edt_dev_id.setText(getIMEI());
        if(BuildConfig.DEBUG) {
            edt_dev_id.setText("000000000000000003F");
            edt_new_pwd.setText("87654321");
        }
        edt_new_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    tv_hint2.setHint(getString(R.string.hint_auth_password));
                } else {
                    tv_hint2.setHint("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePwd();
            }
        });
    }

    String getIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return telephonyManager.getDeviceId();
    }

    void updatePwd() {
        if (TextUtils.isEmpty(edt_dev_id.getText())) {
            showToast(getString(R.string.device_notice3));
            return;
        }
        if (TextUtils.isEmpty(edt_new_pwd.getText())) {
            showToast(getString(R.string.login_password_empty));
            return;
        }
        String strDevId = edt_dev_id.getText().toString();
        if (strDevId.length() < 20) {
            String strPadding = String.format("%020d", 0);
            strDevId = strPadding.substring(0, 20 - strDevId.length()) + strDevId;
        }
        SP.putString(mDeviceIM, strDevId);
        SP.putString(mJiaMiMiMa, edt_new_pwd.getText().toString());
        SP.putBoolean("actived", true);
        startActivity(new Intent(getSelf(), StartActivity.class).putExtra("from", ""));
        finish();
    }
}
