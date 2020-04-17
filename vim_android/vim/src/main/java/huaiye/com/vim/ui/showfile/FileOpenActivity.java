package huaiye.com.vim.ui.showfile;

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
public class FileOpenActivity extends AppBaseActivity implements TbsReaderView.ReaderCallback {

    @BindView(R.id.rl_root)
    RelativeLayout rl_root;
    TbsReaderView mTbsReaderView;
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
        mTbsReaderView = new TbsReaderView(this, this);
        rl_root.addView(mTbsReaderView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        displayFile();
    }

    private void displayFile() {
        if (TextUtils.isEmpty(file)) {
            file = getIntent().getStringExtra("file");
        }
        fileType = file.substring(file.lastIndexOf(".") + 1);
        Bundle bundle = new Bundle();
        bundle.putString("filePath", file);
        bundle.putString("tempPath", Environment.getExternalStorageDirectory().getPath());
        boolean result = mTbsReaderView.preOpen(fileType, false);
        if (result) {
            mTbsReaderView.openFile(bundle);
        }
    }

    @Override
    protected void onDestroy() {
        mTbsReaderView.onStop();
        super.onDestroy();
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }
}
