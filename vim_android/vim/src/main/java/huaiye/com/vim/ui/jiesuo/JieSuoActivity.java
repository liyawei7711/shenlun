package huaiye.com.vim.ui.jiesuo;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import java.util.List;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.JieSuoBean;
import huaiye.com.vim.models.CommonResult;
import huaiye.com.vim.models.auth.AuthApi;
import huaiye.com.vim.views.GestureLockViewGroup;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: MainActivity
 */
@BindLayout(R.layout.activity_jiesuo)
public class JieSuoActivity extends AppBaseActivity {
    @BindView(R.id.id_gestureLockViewGroup)
    GestureLockViewGroup mGestureLockViewGroup;

    String qiujiu = "3215987";

    int count = 0;

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void doInitDelay() {
        JieSuoBean bean = AppDatas.MsgDB().getJieSuoDao().queryOneItem(AppAuth.get().getUserID(), AppAuth.get().getDomainCode());
        mGestureLockViewGroup
                .setOnGestureLockViewListener(new GestureLockViewGroup.OnGestureLockViewListener() {

                    @Override
                    public void onUnmatchedExceedBoundary() {
                    }

                    @Override
                    public void onGestureChoose(List<Integer> mChoose) {
                        if (qiujiu.equals(toStringArray(mChoose))) {
                            AuthApi.get().sos(JieSuoActivity.this,"", new AuthApi.ISosListener() {
                                @Override
                                public void onSuccess(CommonResult response) {
                                    finish();
                                }

                                @Override
                                public void onFailure(HTTPResponse response) {
                                    finish();
                                }
                            });
                            mGestureLockViewGroup.reset();
                        } else if (bean.jiesuo.equals(toStringArray(mChoose))) {
                            count = 0;
                            mGestureLockViewGroup.reset();
                            finish();
                        }
                    }

                    @Override
                    public void onGestureEvent(boolean matched) {
                        if (matched) {
                            mGestureLockViewGroup.reset();
                            finish();
                        } else {
                            showToast("手势密码错误");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mGestureLockViewGroup.reset();
                                    count++;
                                    if (count >= 6) {
                                        AppAuth.get().clearData(JieSuoActivity.this);
                                        showToast("手势密码多次错误，清空数据");
                                        jumpToLogin();
                                    }
                                }
                            }, 1000);
                        }
                    }

                    @Override
                    public void onBlockSelected(int cId) {
                    }
                });
    }

    int[] toIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        for (int i = 0; i < ret.length; i++)
            ret[i] = list.get(i);
        return ret;
    }

    String toStringArray(List<Integer> list) {
        String str = "";
        for (int i = 0; i < list.size(); i++)
            str += list.get(i);
        return str;
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

}
