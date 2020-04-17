package huaiye.com.vim.ui.meet.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.google.gson.Gson;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.io.NotifyFileConvertStatus;
import com.huaiye.sdk.sdpmsgs.whiteboard.CEnterWhiteboardRsp;
import com.huaiye.sdk.sdpmsgs.whiteboard.CExitWhiteboardRsp;
import com.huaiye.sdk.sdpmsgs.whiteboard.CNotifyUpdateWhiteboard;
import com.huaiye.sdk.sdpmsgs.whiteboard.CUpdateWhiteboardRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseFragment;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.views.CustomerWebView;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.ui.meet.MeetActivity;
import huaiye.com.vim.ui.meet.MeetWatchActivity;

/**
 * author: admin
 * date: 2018/01/08
 * version: 0
 * mail: secret
 * desc: MeetMembersFragment
 * 白板
 */
@BindLayout(R.layout.fragment_meet_board)
public class MeetBoardFragment extends AppBaseFragment {

    boolean isMeetStarter;
    String strMeetDomaincode;
    int nMeetID;
    boolean isOpen;
    boolean needShow = false;
    private int webviewContentWidth;
    private float scale = 0;
    private float scalePreNex = 0;
    public boolean isEnt;

    public void setIsMeetStarter(boolean value) {
        isMeetStarter = value;
    }

    public void setMeetDomaincode(String domain) {
        this.strMeetDomaincode = domain;
    }

    public void setMeetID(int id) {
        this.nMeetID = id;
    }

    @BindView(R.id.wv_view)
    CustomerWebView wv_view;
    @BindView(R.id.iv_draw_tool)
    ImageView iv_draw_tool;
    @BindView(R.id.ll_tool_menu)
    View ll_tool_menu;
    @BindView(R.id.ll_yanse_all)
    View ll_yanse_all;

    @BindView(R.id.cb_jiguangbi)
    RadioButton cb_jiguangbi;
    @BindView(R.id.cb_gangbi)
    RadioButton cb_gangbi;
    @BindView(R.id.cb_yanse)
    RadioButton cb_yanse;
    @BindView(R.id.cb_xiangpica)
    RadioButton cb_xiangpica;
    @BindView(R.id.cb_qingchu)
    RadioButton cb_qingchu;

    ArrayList<RadioButton> allBtn = new ArrayList<>();
    Gson gson = new Gson();
    boolean touchEvent;

    float lastX, lastY, baseValue = 0;
    String url = "";
    boolean isLeft;
    boolean isRight;
    boolean isLoadFinish;
    ConcurrentLinkedQueue linkedList = new ConcurrentLinkedQueue();

    RxUtils rxUtils;

    boolean isPageFinish = true;

    @Override
    public void onResume() {
        super.onResume();
        if (isOpen && needShow) {
            new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                @Override
                public void onMainDelay() {
                    openWhiteBoard(false, null, false);
                }
            }, "open_board");
        }
        needShow = true;
        wv_view.getContentHeight();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getNavigate().setVisibility(View.GONE);

        WebSettings settings = wv_view.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBlockNetworkImage(false);
        if (Build.VERSION.SDK_INT >= 19) {
            settings.setLoadsImagesAutomatically(true);
        } else {
            settings.setLoadsImagesAutomatically(false);
        }
        settings.setJavaScriptEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setDomStorageEnabled(true);

        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        allBtn.add(cb_qingchu);
        allBtn.add(cb_xiangpica);
        allBtn.add(cb_yanse);
        allBtn.add(cb_gangbi);
        allBtn.add(cb_jiguangbi);

        rxUtils = new RxUtils<>();
        // 设置setWebChromeClient对象
        wv_view.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

        });

        wv_view.setWebViewClient(new WebViewClient() {
            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                super.onScaleChanged(view, oldScale, newScale);
                if (isPageFinish) {
                    isPageFinish = false;
                    if (oldScale < 1) {
                        scale = newScale;
                    } else {
                        scale = oldScale;
                    }
                }
                float webViewContentWidth = webviewContentWidth * wv_view.getScale();
                int screenWidth = AppUtils.getScreenWidth();
                wv_view.loadUrl("javascript:mobileFingers('" + (webViewContentWidth / screenWidth) + "')");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                scalePreNex = wv_view.getScale();
                isPageFinish = true;
                wv_view.loadUrl("javascript:window.JSBridge.getContentWidth(document.getElementsByTagName('html')[0].scrollWidth);");
            }

        });

        wv_view.addJavascriptInterface(new JavaScriptInterface(), "JSBridge");
        url = "http://" + AppDatas.Constants().getAddressIP()
                + ":"
                + AppDatas.Constants().getAddressPort()
                + "/mchtml/canvas0/about.html";

        wv_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (ll_tool_menu.getVisibility() == View.VISIBLE)
                    return false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getX();
                        lastY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float webViewContentWidth = webviewContentWidth * wv_view.getScale();

                        if (Math.abs(event.getY() - lastY) < ViewConfiguration.get(getContext()).getScaledTouchSlop() * 3) {
                            if (Math.abs(event.getX() - lastX) >= ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                                if (event.getX() - lastX > 0) {
                                    if (AppUtils.getScreenWidth() == webViewContentWidth) {
                                        wv_view.loadUrl("javascript:mobileTurnPage('right')");
                                        return false;
                                    }
                                } else {
                                    if (AppUtils.getScreenWidth() == webViewContentWidth) {
                                        wv_view.loadUrl("javascript:mobileTurnPage('left')");
                                        return false;
                                    }
                                }

                                return true;
                            }
                        }

                        break;

                }
                return false;
            }
        });

        wv_view.setOnCustomScroolChangeListener(new CustomerWebView.ScrollInterface() {
            @Override
            public void onSChanged(int l, int t, int oldl, int oldt) {
                isLeft = false;
                isRight = false;
                //WebView的总高度
                float webViewContentWidth = webviewContentWidth * wv_view.getScale();
                //WebView的现高度
                float webViewCurrentWidth = (wv_view.getWidth() + wv_view.getScrollX());
                if ((webViewContentWidth - webViewCurrentWidth) == 0) {
                    isLeft = true;
                    isRight = false;
                } else if ((webViewContentWidth - webViewCurrentWidth) < webViewContentWidth / 2 + 10 ||
                        (webViewContentWidth - webViewCurrentWidth) > webViewContentWidth / 2 - 10) {
                    isLeft = false;
                    isRight = true;
                }
            }

            @Override
            public void After() {
            }
        });

    }

    private void closeColor(int visible) {
        ll_yanse_all.setVisibility(visible);
    }

    public void changeOpenStatus(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public void notiyUpdate(final CNotifyUpdateWhiteboard info) {
        if (!isLoadFinish) {
            linkedList.add(info);
        } else {
            if (linkedList.size() == 0) {
                dealWhitJs(info);
            } else {
                linkedList.add(info);
                dealMissMsg();
            }
        }
    }

    /**
     * 调用js
     *
     * @param info
     */
    private void dealWhitJs(final CNotifyUpdateWhiteboard info) {
        rxUtils.doDelayOn(100, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                try {
                    String str = gson.toJson(info);
                    String str1 = "\\\\";
                    String str2 = "\\\\\\\\\\\\";
                    if (info.lstOperations.isEmpty()) {
                        wv_view.loadUrl("javascript:getMsgFromMobile('" + str + "')");
                    } else if (info.lstOperations.get(0).nClientType != 1) {
                        wv_view.loadUrl("javascript:getMsgFromMobile('" + str + "')");
                    } else {
                        wv_view.loadUrl("javascript:getMsgFromMobile('" + str.replaceAll(str1, str2) + "')");
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    class JavaScriptInterface {
        @JavascriptInterface
        public void post(String strJson, int oper, int currentIndex, int total, String url) {
            updateWhiteBoard(strJson, oper, currentIndex, total, url, "JavascriptInterface");
        }

        @JavascriptInterface
        public void preventParentTouchEvent() {
            touchEvent = true;
        }

        @JavascriptInterface
        public void getContentWidth(String value) {

            if (value != null) {
                webviewContentWidth = Integer.parseInt(value);
            }

        }

        @JavascriptInterface
        public void onloadMobile() {
            isLoadFinish = true;
            dealMissMsg();
        }

        @JavascriptInterface
        public void end() {
            rxUtils.doDelayOn(200, new RxUtils.IMainDelay() {
                @Override
                public void onMainDelay() {
                    HYClient.getModule(ApiMeet.class)
                            .closeWhiteBoard(SdkParamsCenter.Meet.CloseWhiteBoard().setnMeetingID(nMeetID),
                                    new SdkCallback<CExitWhiteboardRsp>() {
                                        @Override
                                        public void onSuccess(CExitWhiteboardRsp cExitWhiteboardRsp) {
                                            isEnt = false;
                                        }

                                        @Override
                                        public void onError(ErrorInfo errorInfo) {
                                        }
                                    });
                    if (getActivity() != null) {
                        if (getActivity() instanceof MeetActivity) {
                            ((MeetActivity) getActivity()).hideAll();
                        } else {
                            ((MeetWatchActivity) getActivity()).hideAll();
                        }
                    }
                }
            });
        }

    }

    private void dealMissMsg() {
        rxUtils.doDelayOn(100, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                while (!linkedList.isEmpty()) {
                    CNotifyUpdateWhiteboard info = (CNotifyUpdateWhiteboard) linkedList.remove();
                    try {
                        String str = gson.toJson(info);
                        String str1 = "\\\\";
                        String str2 = "\\\\\\\\\\\\";
                        if (info.lstOperations.isEmpty()) {
                            wv_view.loadUrl("javascript:getMsgFromMobile('" + str + "')");
                        } else if (info.lstOperations.get(0).nClientType != 1) {
                            wv_view.loadUrl("javascript:getMsgFromMobile('" + str + "')");
                        } else {
                            wv_view.loadUrl("javascript:getMsgFromMobile('" + str.replaceAll(str1, str2) + "')");
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });

    }

    /**
     * @param strJson
     */
    public void updateWhiteBoard(String strJson, int oper, int currentIndex, int total, String url, String from) {
        HYClient.getModule(ApiMeet.class)
                .updateWhiteboard(SdkParamsCenter.Meet.UpdateWhiteBoard()
                                .setnMeetingID(nMeetID)
                                .setnCurPage(currentIndex)
                                .setnTotalPage(total)
                                .setnOperate(oper)
                                .setnClientType(2)
                                .setStrContent(strJson)
                                .setStrPicHttpAddr(url),
                        new SdkCallback<CUpdateWhiteboardRsp>() {
                            @Override
                            public void onSuccess(CUpdateWhiteboardRsp cEnterWhiteboardRsp) {
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
//                                showToast(ErrorMsg.getMsg(ErrorMsg.update_white_board_code));
                            }
                        });
    }

    /**
     * 关闭白板
     */
    public void exiteWhiteBoard() {
        clearJs();
    }

    public void clearJs() {
        isLoadFinish = false;
        wv_view.loadUrl("javascript:clearRectSelf()");
    }

    public void openWhiteBoard(final boolean isShare, final NotifyFileConvertStatus status, boolean isConvert) {
        if (!isConvert) {
            wv_view.setInitialScale(100);
            wv_view.loadUrl(url);
        }

        HYClient.getModule(ApiMeet.class)
                .openWhiteBoard(SdkParamsCenter.Meet.OpenWhiteBoard().setnMeetingID(nMeetID),
                        new SdkCallback<CEnterWhiteboardRsp>() {
                            @Override
                            public void onSuccess(CEnterWhiteboardRsp cExitWhiteboardRsp) {
                                isEnt = true;
                                if (status == null)
                                    return;

                                if (isShare) {
                                    updateWhiteBoard(gson.toJson(status),
                                            5,
                                            1,
                                            status.nTotalPageNum,
                                            AppDatas.Constants().getAddressBaseURLTarget() + status.strPicRelativePath,
                                            "openWhiteBoard");
                                }
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
//                                showToast(ErrorMsg.getMsgWhiterBoard(errorInfo.getCode()));
                                if (getActivity() != null) {
                                    if (getActivity() instanceof MeetActivity) {
                                        ((MeetActivity) getActivity()).hideAll();
                                    } else {
                                        ((MeetWatchActivity) getActivity()).hideAll();
                                    }
                                }
                            }
                        });

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            showMenu(false);
            closeColor(View.GONE);
        }
    }

    @OnClick({R.id.view_red_f34235,
            R.id.view_red_e91e63,
            R.id.view_blue_2095f2,
            R.id.view_green_8ac249,
            R.id.view_zise_9c27b0,
            R.id.view_yellow_fec006,
            R.id.view_grey_9e9e9e,
            R.id.view_black_000000,
            R.id.cb_qingchu,
            R.id.cb_xiangpica,
            R.id.cb_yanse,
            R.id.cb_gangbi,
            R.id.cb_jiguangbi,
            R.id.iv_draw_tool})
    public void click(View view) {
        String color = "#ffffff";
        switch (view.getId()) {
            case R.id.iv_draw_tool:
                showMenu(ll_tool_menu.getVisibility() != View.VISIBLE);
                return;
            case R.id.cb_jiguangbi:
                changeChecked(cb_jiguangbi);
                wv_view.loadUrl("javascript:mobileHighLighter()");
                return;
            case R.id.cb_gangbi:
                changeChecked(cb_gangbi);
                wv_view.loadUrl("javascript:mobilePen()");
                return;
            case R.id.cb_yanse:
                changeChecked(cb_yanse);
                if (ll_yanse_all.getVisibility() == View.VISIBLE) {
                    closeColor(View.GONE);
                } else {
                    closeColor(View.VISIBLE);
                }
                return;
            case R.id.cb_xiangpica:
                changeChecked(cb_xiangpica);
                wv_view.loadUrl("javascript:mobilEraser()");
                return;
            case R.id.cb_qingchu:
                changeChecked(cb_qingchu);
                wv_view.loadUrl("javascript:mobileClearRect()");
                return;
            case R.id.view_red_f34235:
                color = "#f34235";
                break;
            case R.id.view_red_e91e63:
                color = "#e91e63";
                break;
            case R.id.view_blue_2095f2:
                color = "#2095f2";
                break;
            case R.id.view_green_8ac249:
                color = "#8ac249";
                break;
            case R.id.view_zise_9c27b0:
                color = "#9c27b0";
                break;
            case R.id.view_yellow_fec006:
                color = "#fec006";
                break;
            case R.id.view_grey_9e9e9e:
                color = "#9e9e9e";
                break;
            case R.id.view_black_000000:
                color = "#000000";
                break;
        }
        closeColor(View.GONE);
        wv_view.loadUrl("javascript:mobileColor('" + color + "')");
    }

    /**
     * 改变选中
     *
     * @param rb
     */
    private void changeChecked(RadioButton rb) {
        if (rb != cb_yanse) {
            for (RadioButton temp : allBtn) {
                if (temp != cb_yanse) {
                    if (temp != rb) {
                        temp.setChecked(false);
                    }
                }
            }
        }
    }

    private void showMenu(boolean value) {
        if (value) {
            ll_tool_menu.setVisibility(View.VISIBLE);
            iv_draw_tool.setBackgroundResource(R.drawable.shape_draw_tool_left);
            wv_view.loadUrl("javascript:mobileShow()");
        } else {
            closeColor(View.GONE);
            ll_tool_menu.setVisibility(View.GONE);
            iv_draw_tool.setBackgroundResource(R.drawable.shape_draw_tool_all);
            wv_view.loadUrl("javascript:mobileHide()");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rxUtils.clearAll();
    }
}
