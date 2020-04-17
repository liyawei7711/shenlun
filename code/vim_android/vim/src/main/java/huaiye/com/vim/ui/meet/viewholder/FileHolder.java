package huaiye.com.vim.ui.meet.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vim.R;
import huaiye.com.vim.common.recycle.LiteViewHolder;
import huaiye.com.vim.models.meet.bean.FileBean;

/**
 * Created by Administrator on 2018\2\27 0027.
 */

public class FileHolder extends LiteViewHolder {

    @BindView(R.id.iv_type)
    ImageView iv_type;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.cb_status)
    CheckBox cb_status;
    @BindView(R.id.cb_view)
    View cb_view;
    @BindView(R.id.view_divider)
    View view_divider;

    public FileHolder(Context context, View view, View.OnClickListener ocl) {
        super(context, view, ocl);

        itemView.setOnClickListener(ocl);
        cb_view.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        FileBean bean = (FileBean) data;
        itemView.setTag(bean);
        cb_view.setTag(bean);

        if (bean.isFile) {
            iv_type.setImageResource(R.drawable.icon_wendang);
            cb_status.setVisibility(View.VISIBLE);
        } else {
            iv_type.setImageResource(R.drawable.icon_wenjianjia);
            cb_status.setVisibility(View.INVISIBLE);
        }

        cb_status.setChecked(bean.isChecked);

        tv_name.setText(bean.showName);

        if (position == size - 1) {
            view_divider.setVisibility(View.GONE);
        } else {
            view_divider.setVisibility(View.VISIBLE);
        }
    }
}
