package huaiye.com.vim.ui.meet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import ttyy.com.coder.scanner.QRCodeScannerView;
import ttyy.com.coder.scanner.decode.DecodeCallback;

/**
 * author: admin
 * date: 2017/11/27
 * version: 0
 * mail: secret
 * desc: FaceCodeScanActivity
 * 二维码扫描
 */
@BindLayout(R.layout.activity_codescanner)
public class CodeScanActivity extends AppBaseActivity {

    @BindView(R.id.qrcode_view)
    QRCodeScannerView qrcode_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initActionBar() {
        getNavigate()
                .setTitlText("扫描二维码")
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        qrcode_view.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecodeSuccess(String s) {
                Intent intent = new Intent();
                intent.putExtra("code", s);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onDecodeFail(String s) {
                qrcode_view.startDecodeDelay(500);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrcode_view.startDecode();
    }
}
