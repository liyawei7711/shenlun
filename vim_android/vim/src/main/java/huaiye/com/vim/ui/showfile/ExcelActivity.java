package huaiye.com.vim.ui.showfile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;
import com.wxiwei.office.IOffice;

import java.io.File;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;

@BindLayout(R.layout.activity_show_excel)
public class ExcelActivity extends AppBaseActivity {

    private IOffice iOffice;

    @BindView(R.id.outlayout)
    RelativeLayout outlayout;

    @BindExtra
    String file;
    @BindExtra
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iOffice = new IOffice() {
            @Override
            public Activity getActivity() {
                return ExcelActivity.this;
            }

            @Override
            public String getAppName() {
                return "ioffice";
            }

            @Override
            public File getTemporaryDirectory() {
                File file = ExcelActivity.this.getExternalFilesDir(null);
                if (file != null) {
                    return file;
                } else {
                    return ExcelActivity.this.getFilesDir();
                }
            }

            @Override
            public void openFileFinish() {
                outlayout.addView(getView(),
                        new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT
                        ));
                iOffice.changePageIndex(0);
            }
        };

    }

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
        openFile();
    }

    private void openFile() {
        iOffice.openFile("" + file);
    }

    @Override
    public void onBackPressed() {
        iOffice.destroyEngine();
        iOffice.dispose();
        super.onBackPressed();
    }
}