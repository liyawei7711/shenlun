package huaiye.com.vim.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.auth.AuthApi;
import huaiye.com.vim.models.auth.bean.ChangePwd;
import huaiye.com.vim.ui.home.MainActivity;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vim.common.AppUtils.STRING_KEY_resetpwd;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: SettingAddressActivity
 */
@BindLayout(R.layout.activity_change_pwd)
public class ChangePwdActivity extends AppBaseActivity {

    @BindView(R.id.tv_hint1)
    TextView tv_hint1;
    @BindView(R.id.tv_hint2)
    TextView tv_hint2;
    @BindView(R.id.tv_hint3)
    TextView tv_hint3;

    @BindView(R.id.edt_old_pwd)
    EditText edt_old_pwd;
    @BindView(R.id.edt_new_pwd)
    EditText edt_new_pwd;
    @BindView(R.id.edt_re_new_pwd)
    EditText edt_re_new_pwd;

    @BindView(R.id.tv_sure)
    View tv_sure;

    @BindExtra
    boolean isQiangZhi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initActionBar() {
        if (isQiangZhi) {
            getNavigate().setTitlText("修改密码")
                    .hideLeftIcon()
                    .setLeftClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
        } else {
            getNavigate().setTitlText("修改密码")
                    .setLeftClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
        }
    }

    @Override
    public void doInitDelay() {
        tv_hint1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_old_pwd.requestFocus();
                AppUtils.showKeyboard(edt_old_pwd);
                edt_old_pwd.setSelection(edt_old_pwd.getText().toString().length());
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
        tv_hint3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_re_new_pwd.requestFocus();
                AppUtils.showKeyboard(edt_re_new_pwd);
                edt_re_new_pwd.setSelection(edt_re_new_pwd.getText().toString().length());
            }
        });

        edt_old_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    tv_hint1.setHint("请输入原密码");
                } else {
                    tv_hint1.setHint("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edt_new_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    tv_hint2.setHint("请输入新密码");
                } else {
                    tv_hint2.setHint("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edt_re_new_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    tv_hint3.setHint("请再次输入密码");
                } else {
                    tv_hint3.setHint("");
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

    void updatePwd() {

        if (TextUtils.isEmpty(edt_old_pwd.getText())) {
            showToast("原密码不能为空");
            return;
        }
        if (TextUtils.isEmpty(edt_new_pwd.getText())) {
            showToast("新密码不能为空");
            return;
        }
        if (TextUtils.isEmpty(edt_re_new_pwd.getText())) {
            showToast("重复新密码不能为空");
            return;
        }
        if (!edt_re_new_pwd.getText().toString().equals(edt_new_pwd.getText().toString())) {
            showToast("两次新密码不一致");
            return;
        }
        if (edt_re_new_pwd.getText().toString().length() < 6) {
            showToast("密码不能小于6位");
            return;
        }
        if (edt_re_new_pwd.getText().toString().length() > 20) {
            showToast("密码不能大于20位");
            return;
        }

        if (edt_old_pwd.getText().toString().equals(edt_new_pwd.getText().toString())) {
            showToast("新密码不能与原密码相同");
            return;
        }

        if (!AppUtils.isRightPwd(edt_re_new_pwd.getText().toString())) {
            showToast("密码中必须包含大小写字母、数字");
            return;
        }

        Log.d("VIMApp", "edt_old_pwd = " + edt_old_pwd.getText().toString());
        Log.d("VIMApp", "edt_new_pwd = " + edt_new_pwd.getText().toString());
        AuthApi.get().chengpwd(edt_old_pwd.getText().toString(),
                edt_new_pwd.getText().toString(),
                new ModelCallback<ChangePwd>() {
                    @Override
                    public void onSuccess(ChangePwd authUser) {
                        if (authUser.nResultCode == 0) {
                            AppDatas.Auth().put("Password", "");
                            showToast("密码已修改");

                            if (isQiangZhi) {
                                SP.putBoolean(STRING_KEY_resetpwd, true);

                                AppAuth.get().setAutoLogin(true);
                                startActivity(new Intent(getSelf(), MainActivity.class).putExtra("isFromLogin", true));
                            } else {
                                finish();
                            }

//                            HYClient.getModule(ApiAuth.class).logout(null);

//                            Intent intent = new Intent(ChangePwdActivity.this, StartActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                            startActivity(intent);

                        } else if(authUser.nResultCode == 1010100005) {
                            showToast("原始密码错误");
                            tv_sure.setEnabled(true);
                        } else {
                            showToast(authUser.strResultDescribe);
                            tv_sure.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        tv_sure.setEnabled(true);
                        showToast("请求失败");
                    }
                });

    }

    @Override
    public void onBackPressed() {
        if (isQiangZhi) {

        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isQiangZhi) {
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
