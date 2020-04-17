package huaiye.com.vim.ui.contacts.views;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.ttyy.commonanno.Injectors;
import com.ttyy.commonanno.anno.BindLayout;

import huaiye.com.vim.R;
import huaiye.com.vim.ui.contacts.GroupListActivity;

/**
 * author: admin
 * date: 2018/01/16
 * version: 0
 * mail: secret
 * desc: HeaderContactsGroupView
 */
@BindLayout(R.layout.header_contacts_group)
public class HeaderContactsGroupView extends RelativeLayout {

    public HeaderContactsGroupView(Context context) {
        this(context, null);
    }

    public HeaderContactsGroupView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderContactsGroupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Injectors.get().injectView(this);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), GroupListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        if (!hasOnClickListeners()) {
            super.setOnClickListener(l);
        }
    }
}
