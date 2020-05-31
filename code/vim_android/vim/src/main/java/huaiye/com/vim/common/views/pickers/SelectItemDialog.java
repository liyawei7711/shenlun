package huaiye.com.vim.common.views.pickers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vim.R;
import huaiye.com.vim.common.recycle.LiteBaseAdapter;
import huaiye.com.vim.common.views.pickers.adapter.ItemHolder;
import huaiye.com.vim.common.views.pickers.itemdivider.SimpleItemDecoration;

/**
 * author: admin
 * date: 2018/02/23
 * version: 0
 * mail: secret
 * desc: SelectItemDialog
 */

public class SelectItemDialog extends Dialog {

    private Context context;

    private RecyclerView rv;

    private List<SelectBean> datas;
    private LiteBaseAdapter<SelectBean> adapter;

    private onDialogItemClickListener listener;
    private View tv_cancel;

    public SelectItemDialog(Context context, onDialogItemClickListener listener, ArrayList<SelectBean> data) {
        super(context);
        this.context = context;
        this.listener = listener;
        datas = data;

        themeInit();
        lazyInit();
    }

    /**
     * 窗口主题初始化
     * <strong> setContentView之前调用 </strong>
     */

    private void themeInit() {
        /* 无标题窗口模式 */
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        /* 对话框下面的activity背景半透明黑 */
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        /* 对话框背景色透明 */
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(true);
        setCancelable(true);
    }

    private void lazyInit() {
        View rootView = LayoutInflater.from(context).inflate(R.layout.dialog_selected_picker, null);
        rv = (RecyclerView) rootView.findViewById(R.id.dialog_list_view);
        tv_cancel = rootView.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.addItemDecoration(new SimpleItemDecoration(context));
        setContentView(rootView);
        setWidthScale();
    }

    @Override
    public void show() {

        adapter = new LiteBaseAdapter<>(getContext(),
                datas,
                ItemHolder.class,
                R.layout.dialog_item_select,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismiss();
                        listener.onItemClick((SelectBean) v.getTag());
                    }
                }, null);
        rv.setAdapter(adapter);

        super.show();
    }

    /**
     * 设置尺寸
     */
    private void setWidthScale() {
        /* 设置对话框大小 */
        WindowManager.LayoutParams params = this.getWindow().getAttributes();

        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        params.width = metric.widthPixels;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        /* 居中显示 */
        this.getWindow().setGravity(Gravity.BOTTOM);
        this.getWindow().setAttributes(params);
    }

    public interface onDialogItemClickListener {
        void onItemClick(SelectBean branch);
    }

    /**
     * dp 转像素 px
     *
     * @param value
     * @return
     */
    protected final int dp2px(int value) {

        float density = getContext().getResources().getDisplayMetrics().density;

        return (int) (density * value);
    }

    public static class SelectBean {
        public int id;
        public String name;
    }
}

