package huaiye.com.vim.ui.auth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.dao.AppDatas;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: SettingAddressActivity
 */
@BindLayout(R.layout.activity_settings_address_safe)
public class SettingAddressSafeActivity extends AppBaseActivity {

    @BindView(R.id.edt_address_port)
    EditText edt_address_port;
    @BindView(R.id.edt_address_ip)
    EditText edt_address_ip;
    @BindView(R.id.edt_address_end)
    EditText edt_address_end;
    @BindView(R.id.input_server_end)
    View input_server_end;
    @BindView(R.id.input_server_end_title)
    View input_server_end_title;
    @BindView(R.id.setting_ok)
    TextView setting_ok;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(getString(R.string.setting_notice5))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                })
                /*.setRightText("保存")
                .setRightTextColor(Color.RED)
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateAddress();
                    }
                })*/;
        if (AppUtils.is9) {
            input_server_end.setVisibility(View.VISIBLE);
            input_server_end_title.setVisibility(View.VISIBLE);
        } else {
            input_server_end.setVisibility(View.GONE);
            input_server_end_title.setVisibility(View.GONE);
        }
    }

    @Override
    public void doInitDelay() {
        TextView textView = (TextView)findViewById(R.id.server_ip_sample);
        textView.setText(AppUtils.getString(R.string.input_sample) + AppUtils.getString(R.string.default_server_ip));
        edt_address_ip.setText(AppDatas.Constants().getAddressIP());
        edt_address_port.setText(AppDatas.Constants().getAddressPort() + "");
        edt_address_end.setText(AppDatas.Constants().getAddressEnd() + "");
    }

    @OnClick(R.id.setting_ok)
    void updateAddress() {

        if (TextUtils.isEmpty(edt_address_ip.getText())
                || TextUtils.isEmpty(edt_address_port.getText())) {
            showToast(getString(R.string.setting_notice1));
            return;
        }
        if (!AppUtils.isIpAddress(edt_address_ip.getText().toString().replaceAll(" ", ""))) {
            showToast(getString(R.string.setting_notice2));
            return;
        }
        if (edt_address_port.getText().toString().length() != 4) {
            showToast(getString(R.string.setting_notice3));
            return;
        }
        if (AppUtils.is9 && TextUtils.isEmpty(edt_address_end.getText())) {
            showToast(getString(R.string.setting_notice4));
            return;
        }
        String ip = edt_address_ip.getText().toString().replaceAll(" ", "");
        String end = edt_address_end.getText().toString().replaceAll(" ", "");
        int port = Integer.parseInt(edt_address_port.getText().toString());

        AppDatas.Constants().setAddress(ip, port);
        if (AppUtils.is9)
            AppDatas.Constants().setAddressEnd(end);

        finish();
    }
}
