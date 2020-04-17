package huaiye.com.vim.ui.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ttyy.commonanno.anno.BindLayout;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseFragment;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: FragmentSettings
 */
@BindLayout(R.layout.fragment_settings_new)
public class FragmentSettings extends AppBaseFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
