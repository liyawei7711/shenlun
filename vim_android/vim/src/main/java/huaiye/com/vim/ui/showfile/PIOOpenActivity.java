package huaiye.com.vim.ui.showfile;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.tencent.smtt.sdk.TbsReaderView;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;


import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;

@BindLayout(R.layout.activity_showfile)
public class PIOOpenActivity extends AppBaseActivity {

    @BindView(R.id.rl_root)
    RelativeLayout rl_root;

    @BindExtra
    String file;
    @BindExtra
    String name;
    String fileType;


    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.VISIBLE);
        getNavigate().setTitlText(name + "")
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        displayFile();
    }

    private void displayFile() {
        if (TextUtils.isEmpty(file)) {
            file = getIntent().getStringExtra("file");
        }
        fileType = file.substring(file.lastIndexOf(".") + 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
