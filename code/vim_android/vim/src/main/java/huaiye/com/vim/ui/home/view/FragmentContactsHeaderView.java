package huaiye.com.vim.ui.home.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ttyy.commonanno.Injectors;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import huaiye.com.vim.R;
import huaiye.com.vim.ui.contacts.ContactsFrequentActivity;
import huaiye.com.vim.ui.home.SearchActivity;

/**
 * Created by ywt on 2019/2/23.
 */
@BindLayout(R.layout.fragment_contacts_header)
public class FragmentContactsHeaderView extends RelativeLayout implements View.OnClickListener {
    @BindView(R.id.contacts_header_search)
    LinearLayout contacts_header_search;


    private Context mContext;

    public FragmentContactsHeaderView(Context context) {
        this(context, null);
        mContext = context;
        contacts_header_search.setOnClickListener(this);

    }

    public FragmentContactsHeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FragmentContactsHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Injectors.get().injectView(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contacts_header_search:
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra(ContactsFrequentActivity.SOURCE, 0);
                getContext().startActivity(intent);
                break;
            default:
                break;
        }
    }
}
