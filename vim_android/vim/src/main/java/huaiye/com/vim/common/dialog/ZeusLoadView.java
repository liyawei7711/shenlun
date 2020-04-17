package huaiye.com.vim.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import huaiye.com.vim.R;


/**
 * Created by justin on 16-1-21.
 * 缓冲View
 */
public class ZeusLoadView extends Dialog {

    private ImageView successView;
    private ProgressBar ringView;
    // 加载中 显示文字TextView
    private TextView textView;

    private String loading_text;// 缓冲中text

    private String success_text;// 缓冲成功text
    private long success_dismiss_delay = 1000; // 缓冲成功后消失延迟时间

    private Handler handler;

    private static GradientDrawable windowDrawable;

    static {

        windowDrawable = new GradientDrawable();
        windowDrawable.setColor(Color.parseColor("#dd000000"));

    }

    public ZeusLoadView(Context context) {
        super(context);

        handler = new Handler();
        loading_text = context.getString(R.string.reqing);
        success_text = context.getString(R.string.reqsuccess);
        success_dismiss_delay = 800;

        initFeatures();
        initWidgets(context);
    }

    private void initFeatures() {

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setGravity(Gravity.CENTER);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    private void initWidgets(Context context) {
        LinearLayout windowView = new LinearLayout(context);
        windowView.setOrientation(LinearLayout.VERTICAL);
        windowView.setGravity(Gravity.CENTER);
        int radius = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.309 * 0.0618);
        windowDrawable.setCornerRadius(radius);
        windowView.setBackground(windowDrawable);

        int horizon_padding = dp2px(20);
        int vertical_padding = dp2px(18);
        windowView.setPadding(horizon_padding, vertical_padding, horizon_padding, vertical_padding);

        FrameLayout content = new FrameLayout(context);
        LinearLayout.LayoutParams content_params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                dp2px(34)
        );
        windowView.addView(content, content_params);

        ringView = new ProgressBar(context);
        ringView.setIndeterminate(false);
        // 资源文件
        Drawable d = context.getResources().getDrawable(R.drawable.rotate_loading_ring_holo);
        ringView.setIndeterminateDrawable(d);
        FrameLayout.LayoutParams ring_params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        content.addView(ringView, ring_params);

        successView = new ImageView(context);
        FrameLayout.LayoutParams success_params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        content.addView(successView, success_params);
        successView.setVisibility(View.GONE);

        textView = new TextView(context);
        textView.setSingleLine(true);
        LinearLayout.LayoutParams text_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        text_params.topMargin = dp2px(6);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(13);
        textView.setVisibility(View.GONE);
//        textView.setText(loading_text);
        windowView.addView(textView, text_params);

        setContentView(windowView);

        WindowManager.LayoutParams win_params = getWindow().getAttributes();
        win_params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        win_params.dimAmount = 0;
        win_params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(win_params);

    }

    // 缓冲中文字
    public ZeusLoadView loadingText(String text) {
        // loading_text = TextUtils.isEmpty(text)?"正在获取...":text;
        loading_text = text;
        return this;
    }

    // 成功展示的图片
    public ZeusLoadView successImageResource(int resId) {
        successView.setImageResource(resId);
        return this;
    }

    // 成功展示文字
    public ZeusLoadView successText(String text) {
        success_text = TextUtils.isEmpty(text) ? getContext().getString(R.string.dealsuccess) : text;
        return this;
    }

    // 设置成功消失延迟
    public ZeusLoadView successDismissDelay(long time) {
        success_dismiss_delay = time;
        return this;
    }

    // 成功
    public void setSuccessful() {
        textView.setText(success_text);
        if (!TextUtils.isEmpty(success_text)) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
        ringView.setVisibility(View.GONE);
        successView.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    dismiss();
                } catch (Exception ignored) {
                }
            }
        }, success_dismiss_delay);
        show();
    }

    // 开始缓冲
    public void setLoading() {
        successView.setVisibility(View.GONE);
        ringView.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(loading_text)) {
            textView.setVisibility(View.GONE);
            textView.setText(loading_text);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(loading_text);
        }
        if (this.isShowing()) {
            return;
        }

        show();
    }

    /**
     * dp 转像素 px
     *
     * @param value
     * @return
     */
    private int dp2px(int value) {

        float density = getContext().getResources().getDisplayMetrics().density;

        return (int) (density * value);
    }

    public void setLoadingText(String value) {
        textView.setText(value);
    }

    public void setPostText(String value) {
        textView.setText(value);
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }
}
