package huaiye.com.vim.ui.meet.presenter;

import android.content.Context;

public class UserDetailPresenterImpl implements UserDetailPresenterHelper.Presenter{

    private UserDetailPresenterHelper.View view;
    private Context context;


    public UserDetailPresenterImpl(UserDetailPresenterHelper.View view, Context context) {
        this.view = view;
        this.context = context;
    }

}
