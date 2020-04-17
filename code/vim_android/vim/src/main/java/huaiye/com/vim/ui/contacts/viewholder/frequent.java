package huaiye.com.vim.ui.contacts.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vim.R;
import huaiye.com.vim.common.recycle.LiteViewHolder;
import huaiye.com.vim.models.contacts.bean.CommonContacts;

/**
 * Created by ywt on 2019/2/25.
 */

public class frequent extends LiteViewHolder {
    @BindView(R.id.iv_user_head)
    ImageView iv_user_head;
    @BindView(R.id.tv_user_name)
    TextView tv_user_name;

    public frequent(Context context, View view, View.OnClickListener ocl) {
        super(context, view, ocl);
        itemView.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        itemView.setTag(position);
        if(data instanceof CommonContacts.Data){
            CommonContacts.Data item = (CommonContacts.Data) data;
            tv_user_name.setText(item.name);
        }
    }
}
