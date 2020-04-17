package huaiye.com.vim.ui.about;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import huaiye.com.vim.BuildConfig;
import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.auth.bean.VersionData;

/**
 * author: admin
 * date: 2017/12/29
 * version: 0
 * mail: secret
 * desc: AboutActivity
 */
@BindLayout(R.layout.activity_about)
public class AboutActivity extends AppBaseActivity {

    @BindView(R.id.tv_version)
    TextView tv_version;
    @BindView(R.id.new_version)
    TextView new_version;
    @BindView(R.id.check_update)
    View check_update;
    @BindView(R.id.iv_img)
    ImageView iv_img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(AppUtils.getString(R.string.about))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        //显示当前的版本号
        tv_version.setText(AppUtils.getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + " " + (AppUtils.nEncryptIMEnable ? "" : "m"));

        //请求版本，检查版本是否需要更新
        requestVersion();

        check_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestVersion();
            }
        });
    }

    /**
     * 传入字符串生成二维码
     *
     * @param str
     * @return
     * @throws WriterException
     */
    public Bitmap CreateQRCode(String str) throws WriterException {
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, iv_img.getWidth(), iv_img.getHeight());
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }

            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }


    void requestVersion() {
        ModelApis.Auth().requestVersion(this, new ModelCallback<VersionData>() {
            @Override
            public void onSuccess(VersionData versionData) {
                if (versionData.isNeedToUpdate()) {
                    new_version.setText(AppUtils.getString(R.string.activity_about_has_new));
                } else {
                    new_version.setText(AppUtils.getString(R.string.activity_about_already_new));
                }

                /* 显示二维码 */
                try {
                    iv_img.setImageBitmap(CreateQRCode(AppDatas.Constants().getFileServerURL() + versionData.path));
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
