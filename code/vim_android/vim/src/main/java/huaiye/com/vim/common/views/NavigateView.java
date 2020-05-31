package huaiye.com.vim.common.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.ttyy.commonanno.Injectors;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.OnLongClick;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppUtils;

/**
 * author: admin
 * date: 2017/09/07
 * version: 0
 * mail: secret
 * desc: NavigateView
 */
@BindLayout(R.layout.view_navigator)
public class NavigateView extends LinearLayout {

    @BindView(R.id.navigate_statusbar_height)
    View navigate_statusbar_height;
    @BindView(R.id.navigate_container)
    View navigate_container;

    @BindView(R.id.tv_con_status)
    TextView tv_con_status;

    @BindView(R.id.view_left)
    View view_left;
    @BindView(R.id.iv_left)
    ImageView iv_left;
    OnClickListener mLeftClickListener;

    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_title1)
    TextView tv_title1;
    @BindView(R.id.top_search)
    View top_search;
    @BindView(R.id.top_add)
    View top_add;
    @BindView(R.id.view_load)
    View view_load;

    @BindView(R.id.view_right)
    View view_right;
    @BindView(R.id.iv_right)
    ImageView iv_right;
    @BindView(R.id.tv_right)
    TextView tv_right;

    @BindView(R.id.view_right2)
    View view_right2;
    @BindView(R.id.iv_right2)
    ImageView iv_right2;
    @BindView(R.id.tv_right2)
    TextView tv_right2;
    @BindView(R.id.view_right3)
    View view_right3;
    @BindView(R.id.iv_right3)
    ImageView iv_right3;


    OnClickListener mRightClickListener;
    OnClickListener mRightClickListener2;
    OnClickListener mRightClickListener3;
    OnClickListener mTopSearchClickListener;
    OnClickListener mTopAddClickListener;

    OnLongClickListener mRightLongClickListener;
    OnLongClickListener mTitleLongClickListener;
    OnClickListener mTitl1ClickListener;
    OnClickListener mTitlClickListener;
    public NavigateView(Context context) {
        this(context, null);
    }

    public NavigateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Injectors.get().injectView(this);
    }

    @OnClick({R.id.view_left, R.id.view_right, R.id.view_right2, R.id.view_right3, R.id.top_search,R.id.top_add, R.id.tv_title1, R.id.tv_title})
    void onViewClicked(View v) {

        switch (v.getId()) {
            case R.id.view_left:
                if (mLeftClickListener != null) {
                    mLeftClickListener.onClick(v);
                }
                break;
            case R.id.view_right:
                if (mRightClickListener != null) {
                    mRightClickListener.onClick(v);
                }
                break;
            case R.id.view_right2:
                if (mRightClickListener2 != null) {
                    mRightClickListener2.onClick(v);
                }
                break;
            case R.id.view_right3:
                if (mRightClickListener3 != null) {
                    mRightClickListener3.onClick(v);
                }
                break;
            case R.id.top_search:
                if (mTopSearchClickListener != null) {
                    mTopSearchClickListener.onClick(v);
                }
                break;
            case R.id.top_add:
                if (mTopAddClickListener != null) {
                    mTopAddClickListener.onClick(v);
                }
                break;

            case R.id.tv_title1:
                if (mTitl1ClickListener != null) {
                    mTitl1ClickListener.onClick(v);
                    tv_title.setTextSize(14);
                    tv_title1.setTextSize(20);
                    tv_title.setTextColor(0xbbbbbbbb);
                    tv_title1.setTextColor(0xffffffff);
                    tv_title1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    tv_title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                }
                break;
            case R.id.tv_title:
                if (mLeftClickListener != null) {
                    mLeftClickListener.onClick(v);
                }
                if (mTitlClickListener != null) {
                    mTitlClickListener.onClick(v);
                    tv_title.setTextSize(20);
                    tv_title1.setTextSize(14);
                    tv_title1.setTextColor(0xbbbbbbbb);
                    tv_title.setTextColor(0xffffffff);
                    tv_title1.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    tv_title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
                break;
        }
    }

    @OnLongClick({R.id.view_right, R.id.tv_title})
    void onViewLongClicked(View v) {
        switch (v.getId()) {
            case R.id.view_right:

                if (mRightLongClickListener != null) {
                    mRightLongClickListener.onLongClick(v);
                }

                break;
            case R.id.tv_title:

                if (mTitleLongClickListener != null) {
                    mTitleLongClickListener.onLongClick(v);
                }

                break;
        }
    }

    public NavigateView showLoading() {
        view_load.setVisibility(View.VISIBLE);
        return this;
    }

    public NavigateView showTopSearch(){
        top_search.setVisibility(View.VISIBLE);
        return this;
    }
    public NavigateView showTopAdd(){
        top_add.setVisibility(View.VISIBLE);
        return this;
    }

    public NavigateView dismissLoading() {
        view_load.setVisibility(View.GONE);
        return this;
    }

    public NavigateView setTitlText(String text) {
        tv_title.setText(text);
        return this;
    }
    public NavigateView setTitl1Text(String text) {
        tv_title1.setVisibility(View.VISIBLE);
        tv_title1.setText(text);
        return this;
    }
    public NavigateView setTitleColor(int color) {
        tv_title.setTextColor(color);
        return this;
    }

    public NavigateView setTitleColor(ColorStateList color) {
        tv_title.setTextColor(color);
        return this;
    }

    public NavigateView setLeftIcon(int id) {
        view_left.setVisibility(View.VISIBLE);
        iv_left.setVisibility(View.VISIBLE);
        iv_left.setImageDrawable(AppUtils.getResourceDrawable(id));
        return this;
    }

    public NavigateView setRightIcon(int id) {
        view_right.setVisibility(View.VISIBLE);
        iv_right.setVisibility(View.VISIBLE);
        iv_right.setImageDrawable(AppUtils.getResourceDrawable(id));
        return this;
    }

    public NavigateView setRight2Icon(int id) {
        view_right2.setVisibility(View.VISIBLE);
        iv_right2.setVisibility(View.VISIBLE);
        iv_right2.setImageDrawable(AppUtils.getResourceDrawable(id));
        return this;
    }

    public NavigateView setRight3Icon(int id) {
        view_right3.setVisibility(View.VISIBLE);
        iv_right3.setVisibility(View.VISIBLE);
        iv_right3.setImageDrawable(AppUtils.getResourceDrawable(id));

        return this;
    }

    public NavigateView setRightText(String text) {
        view_right.setVisibility(View.VISIBLE);
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText(text);
        return this;
    }

    public String getRightText() {
        return tv_right.getText().toString();
    }

    public TextView getRightTextView() {
        return tv_right;
    }

    public NavigateView setRight2Text(String text) {
        view_right2.setVisibility(View.VISIBLE);
        tv_right2.setVisibility(View.VISIBLE);
        tv_right2.setText(text);
        return this;
    }

    public NavigateView setRightTextColor(int color) {
        tv_right.setTextColor(color);
        return this;
    }

    public NavigateView setRight2TextColor(int color) {
        tv_right2.setTextColor(color);
        return this;
    }

    public NavigateView hideLeftIcon() {
        view_left.setVisibility(View.GONE);
        return this;
    }

    public NavigateView hideRightIcon() {
        view_right.setVisibility(View.GONE);
        return this;
    }

    public NavigateView hideRight2Icon() {
        view_right2.setVisibility(View.GONE);
        return this;
    }

    public NavigateView setContentColor(int color) {
        navigate_container.setBackgroundColor(color);
        return this;
    }

    public NavigateView setLeftClickListener(OnClickListener listener) {
        mLeftClickListener = listener;
        return this;
    }

    public NavigateView setRightClickListener(OnClickListener listener) {
        mRightClickListener = listener;
        return this;
    }

    public NavigateView setRight2ClickListener(OnClickListener listener) {
        mRightClickListener2 = listener;
        return this;
    }

    public NavigateView setRight3ClickListener(OnClickListener listener) {
        mRightClickListener3 = listener;
        return this;
    }

    public NavigateView setTopSearchClickListener(OnClickListener listener) {
        mTopSearchClickListener = listener;
        return this;
    }

    public NavigateView setTopAddClickListener(OnClickListener listener) {
        mTopAddClickListener = listener;
        return this;
    }




    public NavigateView setTitl1ClickListener(OnClickListener listener) {
        mTitl1ClickListener = listener;
        return this;
    }

    public NavigateView setTitlClickListener(OnClickListener listener) {
        mTitlClickListener = listener;
        return this;
    }
    public NavigateView setRightLongClickListener(OnLongClickListener listener) {
        mRightLongClickListener = listener;
        return this;
    }
    public NavigateView setTitleLongClickListener(OnLongClickListener listener) {
        mTitleLongClickListener = listener;
        return this;
    }

    public void setConnStatus(SdkBaseParams.ConnectionStatus status) {
        switch (status) {
            case Connected:
                tv_con_status.setVisibility(View.VISIBLE);
                tv_con_status.setText("已连接");
                tv_con_status.setTextColor(AppUtils.getResourceColor(R.color.green_online));
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv_con_status.setVisibility(View.GONE);
                    }
                }, 2200);
                break;
            case Connecting:
                tv_con_status.setVisibility(View.VISIBLE);
                tv_con_status.setText("正在连接");
                tv_con_status.setTextColor(AppUtils.getResourceColor(R.color.purple_connecting));
                break;
            case Disconnected:
                tv_con_status.setVisibility(View.VISIBLE);
                tv_con_status.setText("已断连");
                tv_con_status.setTextColor(AppUtils.getResourceColor(R.color.red_disconnect));
                break;
        }
    }

    public void setRightEnable(boolean b) {
        view_right.setEnabled(b);
    }

    public View getRightView() {
        return view_right;
    }

    public NavigateView setReserveStatusbarPlace() {
        navigate_statusbar_height.setVisibility(View.VISIBLE);
        return this;
    }
}
