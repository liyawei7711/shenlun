package huaiye.com.vim.ui.showfile;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.ui.showfile.utils.SwanTextView;


/**
 * @author dd
 * @describe 加载网页界面
 * @date 2018-5-07 上午10:55:42
 */
@BindLayout(R.layout.activity_showfile_web)
public class WebPageFileActivity extends AppBaseActivity implements View.OnClickListener, SwanTextView.OnTextChangedListener {

    @BindExtra
    public String name; // 标题WebPageFileActivity
    @BindExtra
    public String file;// 网页地址

    @BindView(R.id.wv_view)//显示图文详情
    public WebView webView;
    @BindView(R.id.text_show_scroll)//显示图文详情
    public ScrollView text_show_scroll;
    @BindView(R.id.tv_txt)//显示图文详情
    public SwanTextView tv_txt;

    private View mCustomView;
    private int mOriginalSystemUiVisibility;
    private int mOriginalOrientation;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    protected FrameLayout mFullscreenContainer;

    @Override
    protected void initActionBar() {
        getNavigate().setLeftClickListener(v -> {
            onBackPressed();
        }).setTitlText(name);
    }

    @Override
    public void doInitDelay() {

        if (file.endsWith(".txt")) {
            initTxt();
        } else {
            initWebView();
        }

    }

    private void initTxt() {
        text_show_scroll.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);

        tv_txt.setOnTextChangedListener(this);

        readFromRxJava();
    }

    private void initWebView() {
        text_show_scroll.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);

        WebSettings s = webView.getSettings();
        s.setAllowFileAccessFromFileURLs(true);
        s.setAllowUniversalAccessFromFileURLs(true);
        s.setBuiltInZoomControls(true);
        s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        s.setSaveFormData(true);
        s.setJavaScriptEnabled(true);
        s.setGeolocationEnabled(true);
//        s.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");
        s.setDomStorageEnabled(true);
        s.setCacheMode(WebSettings.LOAD_NO_CACHE);
        s.setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            s.setMediaPlaybackRequiresUserGesture(false);
        }

        s.setLoadWithOverviewMode(true);//适应屏幕
        s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        s.setLoadsImagesAutomatically(true);//自动加载图片
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            s.setMixedContentMode(webView.getSettings().MIXED_CONTENT_ALWAYS_ALLOW);  //注意安卓5.0以上的权限
        }

        webView.setWebChromeClient(new WebChromeClient() {

            //配置权限（同样在WebChromeClient中实现）
//            @Override
//            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
//                callback.invoke(origin, true, false);
//                super.onGeolocationPermissionsShowPrompt(origin, callback);
//            }
            @Override
            public Bitmap getDefaultVideoPoster() {
                if (this == null) {
                    return null;
                }

                //这个地方是加载h5的视频列表 默认图   点击前的视频图
                return BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.mipmap.ic_launcher);
            }

            @Override
            public void onShowCustomView(View view,
                                         CustomViewCallback callback) {
                // if a view already exists then immediately terminate the new one
                if (mCustomView != null) {
                    onHideCustomView();
                    return;
                }

                // 1. Stash the current state
                mCustomView = view;
                mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
                mOriginalOrientation = getRequestedOrientation();

                // 2. Stash the custom view callback
                mCustomViewCallback = callback;

                // 3. Add the custom view to the view hierarchy
                FrameLayout decor = (FrameLayout) getWindow().getDecorView();
                decor.addView(mCustomView, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));


                // 4. Change the state of the window
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_IMMERSIVE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onHideCustomView() {
                // 1. Remove the custom view
                FrameLayout decor = (FrameLayout) getWindow().getDecorView();
                decor.removeView(mCustomView);
                mCustomView = null;

                // 2. Restore the state to it's original form
                getWindow().getDecorView()
                        .setSystemUiVisibility(mOriginalSystemUiVisibility);
                setRequestedOrientation(mOriginalOrientation);

                // 3. Call the custom view callback
                mCustomViewCallback.onCustomViewHidden();
                mCustomViewCallback = null;

            }

        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return false;
                }
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (ActivityNotFoundException a) {
                    a.getMessage();
                }
//  下面这一行保留的时候，原网页仍报错，新网页正常.所以注释掉后，也就没问题了
//          view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//                try {
//                    if (url.startsWith("http") || url.startsWith("https")) {
//                        return super.shouldInterceptRequest(view, url);
//                    } else {
//                        Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                        startActivity(in);
//                        webView.goBack();
//                        return null;
//                    }
//                } catch (Exception e) {
//
//                }
//                return super.shouldInterceptRequest(view, url);
//            }
        });
        webView.requestFocus();

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        File fileEnd = new File(file);
        if(file.endsWith(".pdf")) {
            webView.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=" + fileEnd.getAbsolutePath());
        } else {
            webView.loadUrl("file:///android_asset/officejs/index.html?" + fileEnd.getAbsolutePath());
        }
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    @Override
    public void onClick(View v) {
        finishActivity();
    }

    private void finishActivity() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
//            webView.destroy();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.removeAllViews();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.setTag(null);
            webView.clearHistory();
            webView.destroy();
            webView = null;
        }
    }

    private int mCurBottom = -1;
    private int mNum = -1;
    private boolean mContinueRead = true;
    private boolean mHaveNewText = false;
    private String mStringShow = null;

    @Override
    public void onPreOnDraw(int bottom) {
        mCurBottom = bottom - text_show_scroll.getHeight();

        if (mHaveNewText && !TextUtils.isEmpty(mStringShow)) {
            mHaveNewText = false;

            tv_txt.setText(CharBuffer.wrap(mStringShow));
        }
    }

    private void readFromRxJava() {
        new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal<String>() {
            @Override
            public String doOnThread() {
                try {
                    return getFileOutputString(file, getCharset(file));
                } catch (Exception e) {
                    return "读取文件出错";
                }
            }

            @Override
            public void doOnMain(String data) {
                tv_txt.setText(data);
            }
        });
    }
    public String getFileOutputString(String filePath, String charset) {
        try {
            File file = new File(filePath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset), 8192);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            bufferedReader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getCharset(String filePath) {
        BufferedInputStream bis = null;
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            bis = new BufferedInputStream(new FileInputStream(filePath));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.mark(0);
            if (!checked) {
                while ((read = bis.read()) != -1) {
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
                            // (0x80 - 0xBF),也可能在GB编码内
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return charset;
    }

}
