package huaiye.com.vim.models.download;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import huaiye.com.vim.R;
import huaiye.com.vim.common.AppUtils;

/**
 * Created by liyawei on 15-12-24.
 * phone 18952280597
 * QQ    751804582
 */
public class ErrorDialogActivity extends Activity {

    @BindView(R.id.tv_cancel)
    TextView tv_cancel;
    @BindView(R.id.tv_confirm)
    TextView tv_confirm;
    @BindView(R.id.tv_errinfo)
    TextView tv_errinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int) (AppUtils.getScreenWidth() * 0.8f);
        getWindow().setAttributes(params);

        setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_infodialog);

        ButterKnife.bind(this);

        initListener();
    }

    protected void initListener() {
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final String url = getIntent().getStringExtra("downloadURL");
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DownloadApi.isLoad) {
//                    MyApplication.showToast("下载中");
                    return;
                }

                Intent intent = new Intent(ErrorDialogActivity.this, DownloadService.class);
                intent.putExtra("fileName", "hy_mc_update.apk");
                intent.putExtra("downloadURL", url);
                ErrorDialogActivity.this.startService(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
