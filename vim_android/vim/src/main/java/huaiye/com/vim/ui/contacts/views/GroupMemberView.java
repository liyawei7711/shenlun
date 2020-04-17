package huaiye.com.vim.ui.contacts.views;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ttyy.commonanno.Injectors;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import huaiye.com.vim.R;

/**
 * author: admin
 * date: 2018/01/09
 * version: 0
 * mail: secret
 * desc: MeetCreateUsersView
 */
@BindLayout(R.layout.item_groupdetail_member)
public class GroupMemberView extends LinearLayout {

    @BindView(R.id.iv_user_head)
    public ImageView iv_user_head;
    @BindView(R.id.tv_user_name)
    public TextView tv_user_name;

    public GroupMemberView(Context context) {
        super(context);

        Injectors.get().injectView(this);
    }
}
