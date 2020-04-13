package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;

public class MainActivity extends AppCompatActivity implements SwanTextView.OnTextChangedListener {

    int REQUEST_EXTERNAL_STORAGE = 1;
    String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

//    public String file = "/storage/emulated/0/360/aa.txt";// 网页地址
    public String file = "/storage/emulated/0/360/bb.txt";// 网页地址
//    public String file = "/storage/emulated/0/360/cc.xlsx";// 网页地址
//    public String file = "/storage/emulated/0/360/dd.xlsx";// 网页地址
//    public String file = "/storage/emulated/0/360/gg.docx";// 网页地址

    public WebView webView;
    public ScrollView text_show_scroll;
    public SwanTextView tv_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.wv_view);
        text_show_scroll = findViewById(R.id.text_show_scroll);
        tv_txt = findViewById(R.id.tv_txt);

        checkPermission();
    }

    private void start() {
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
//        getFileOutputString(file, getCharset(file));
    }

    @SuppressLint("NewApi")
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
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

        });
        webView.requestFocus();

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        File fileEnd = new File(file);
        if (file.endsWith(".pdf")) {
            webView.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=" + fileEnd.getAbsolutePath());
        } else {
            webView.loadUrl("file:///android_asset/officejs/index.html?" + fileEnd.getAbsolutePath());
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
//                    return readTxtFile(file);
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

    public CharBuffer readTxtFile(String strFilePath) throws IOException, InterruptedException {
        InputStreamReader is = new InputStreamReader(new FileInputStream(strFilePath), getCharset(strFilePath));

        StringBuilder sb = new StringBuilder();
        char[] buf = new char[1024 * 2];
        while (true) {
            if (mCurBottom == text_show_scroll.getScrollY()) {
                mCurBottom = -1;
                mNum++;
                if (mNum % 2 == 0) {
                    mContinueRead = true;
                }
            }

            if (mContinueRead && is.read(buf) > 0) {
                mContinueRead = false;

                if (sb.length() > 4096) {
                    sb.delete(0, 2048);

                    mStringShow = sb.append(buf).toString();
                    mHaveNewText = true;

                    return CharBuffer.wrap(sb.toString());
                } else {
                    while (sb.length() < 4096) {
                        sb.append(buf);
                        is.read(buf);
                    }

//                    sb.append(buf);
                    System.out.println("ddddddddddddddddddddddddd "+CharBuffer.wrap(sb.toString()));
                    return CharBuffer.wrap(sb.toString());
                }
            }
        }
    }

    /**
     * 检测权限
     */
    private void checkPermission() {
        boolean needReq = false;
        for (int i = 0; i < PERMISSIONS_STORAGE.length; i++) {
            if (PackageManager.PERMISSION_GRANTED !=
                    ContextCompat.checkSelfPermission(MainActivity.this, PERMISSIONS_STORAGE[i])) {
                needReq = true;
            }
        }
        if (needReq) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isAllAgree = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                isAllAgree = false;
            }
        }

        if (!isAllAgree) {
            checkPermission();
        } else {
            start();
        }
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
