package huaiye.com.vim.ui.setting;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.huaiye.cmf.sdp.SdpMessageCmCtrlRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiEncrypt;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.JieSuoBean;
import huaiye.com.vim.dao.msgs.JinJiLianXiRenBean;
import huaiye.com.vim.ui.home.AudioSettingActivity;
import huaiye.com.vim.ui.jiesuo.JieSuoResultActivity;
import huaiye.com.vim.ui.jiesuo.JieSuoSetActivity;

/**
 * @author zhangzhen
 */
@BindLayout(R.layout.activity_setting)
public class SettingActivity extends AppBaseActivity {

    @BindView(R.id.jinjijiuzhu_checkbox)
    CheckBox jinjijiuzhu_checkbox;
    @BindView(R.id.view_checkbox)
    View view_checkbox;
    @BindView(R.id.zhiwen_checkbox)
    CheckBox zhiwen_checkbox;
    @BindView(R.id.tv_shoushi_notic)
    TextView tv_shoushi_notic;

    @BindExtra
    boolean isSOS;
    boolean isReSet;
    boolean isChecked;

    boolean isFromActivity;

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(AppUtils.getString(R.string.activity_setting_set))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        doInitDelay();
    }

    @Override
    public void doInitDelay() {
        JinJiLianXiRenBean lianxi = AppDatas.MsgDB().getJinJiLianXiRenDao().queryOneItem(AppAuth.get().getUserID(), AppAuth.get().getDomainCode());
        if (lianxi == null) {
            jinjijiuzhu_checkbox.setChecked(false);
        } else {
            jinjijiuzhu_checkbox.setChecked(lianxi.isOpen);
        }
        jinjijiuzhu_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingActivity.this.isChecked = isChecked;
                AppDatas.MsgDB().getJinJiLianXiRenDao().updateData(AppAuth.get().getUserID(), AppAuth.get().getDomainCode(), isChecked);
            }
        });

        JieSuoBean jiesuo = AppDatas.MsgDB().getJieSuoDao().queryOneItem(AppAuth.get().getUserID(), AppAuth.get().getDomainCode());
        if (jiesuo == null) {
            isReSet = false;
            zhiwen_checkbox.setChecked(false);
            tv_shoushi_notic.setText("绘制手势密码");
        } else {
            isReSet = true;
            isChecked = jiesuo.isJieSuo;
            zhiwen_checkbox.setChecked(jiesuo.isJieSuo);
            tv_shoushi_notic.setText("重置手势密码");
        }

        view_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFromActivity) {
                    isFromActivity = false;
                    return;
                }

                if(jiesuo == null) {
                    showToast("请先绘制手势密码");
                    return;
                }
                isChecked = !isChecked;
                startActivityForResult(new Intent(SettingActivity.this, JieSuoResultActivity.class), 1000);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            isFromActivity = true;
            if (data.getBooleanExtra("isSuccess", false)) {
                AppDatas.MsgDB().getJieSuoDao().updateData(AppAuth.get().getUserID(), AppAuth.get().getDomainCode(), isChecked);
                zhiwen_checkbox.setChecked(isChecked);
            } else {
                AppDatas.MsgDB().getJieSuoDao().updateData(AppAuth.get().getUserID(), AppAuth.get().getDomainCode(), !isChecked);
                zhiwen_checkbox.setChecked(!isChecked);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick(R.id.activity_setting_security)
    void setSecurity() {
        new AlertDialog.Builder(this).setTitle("解绑")
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setMessage("确定要解绑吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HYClient.getModule(ApiEncrypt.class)
                                .encryptUnbind(SdkParamsCenter.Encrypt.EncryptUnbind().setLocal(0),
                                        new SdkCallback<SdpMessageCmCtrlRsp>() {
                                            @Override
                                            public void onSuccess(SdpMessageCmCtrlRsp sdpMessageCmCtrlRsp) {
                                                HYClient.getSdkOptions().encrypt().setEncryptBind(false);
                                                showToast("解绑成功");
                                            }

                                            @Override
                                            public void onError(ErrorInfo errorInfo) {
                                                showToast("解绑失败");
                                            }
                                        });
                    }
                }).setNegativeButton("取消", null).show();
    }

    @OnClick(R.id.activity_setting_chat)
    void setChat() {
        startActivity(new Intent(this, SettingChatActivity.class).putExtra("isSOS", isSOS));
    }

    @OnClick(R.id.activity_setting_system)
    void setSystem() {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startActivity(intent);
    }

    @OnClick(R.id.activity_setting_call)
    void setCall() {
        startActivity(new Intent(this, AudioSettingActivity.class));
    }

    @OnClick(R.id.activity_setting_lianxiren)
    void setLianXiRen() {
        if (isSOS) {
            return;
        }
        startActivity(new Intent(this, SetLianXiRenActivity.class));
    }

    @OnClick(R.id.activity_setting_shoushi)
    void setShouShi() {
        if (isSOS) {
            return;
        }
        startActivity(new Intent(this, JieSuoSetActivity.class).putExtra("isReSet", isReSet));
    }


}
