package huaiye.com.vim.ui.contacts.viewholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vim.R;
import huaiye.com.vim.common.recycle.LiteViewHolder;
import huaiye.com.vim.models.contacts.bean.ContactOrganization;

/**
 * Created by Administrator on 2018\3\10 0010.
 */

public class Dept extends LiteViewHolder {
    @BindView(R.id.tv_group_name)
    TextView tv_group_name;
    @BindView(R.id.tv_number)
    TextView tv_number;

    public Dept(Context context, View view, View.OnClickListener ocl) {
        super(context, view, ocl);
        itemView.setOnClickListener(ocl);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        itemView.setTag(position);
        if (data instanceof ContactOrganization.Enterprise) {
            ContactOrganization.Enterprise bean = (ContactOrganization.Enterprise) data;
            tv_group_name.setText(bean.entName + "                                                                                                                                                                                                              ");
        } else {
            ContactOrganization.Department bean = (ContactOrganization.Department) data;
            tv_group_name.setText(bean.name + "                                                                                                                                                                                                                   ");
        }
        tv_number.setText("");
    }
}
