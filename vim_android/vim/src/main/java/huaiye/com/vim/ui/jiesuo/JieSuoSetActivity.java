package huaiye.com.vim.ui.jiesuo;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.JieSuoBean;
import huaiye.com.vim.dao.msgs.JinJiLianXiRenBean;
import huaiye.com.vim.views.GestureLockViewGroup;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: MainActivity
 */
@BindLayout(R.layout.activity_jiesuo_set)
public class JieSuoSetActivity extends AppBaseActivity {

    @BindView(R.id.ll_all)
    ViewGroup ll_all;
    @BindView(R.id.ll_all1)
    ViewGroup ll_all1;
    @BindView(R.id.ll_all2)
    ViewGroup ll_all2;
    @BindView(R.id.ll_all3)
    ViewGroup ll_all3;
    @BindView(R.id.ll_reset)
    View ll_reset;
    @BindView(R.id.tv_notic)
    TextView tv_notic;
    @BindView(R.id.tv_gongneng)
    TextView tv_gongneng;
    @BindView(R.id.id_gestureLockViewGroup)
    GestureLockViewGroup mGestureLockViewGroup;

    @BindExtra
    boolean isReSet;

    ArrayList<ImageView> views = new ArrayList<>();
    JieSuoBean bean;
    boolean isAnswer;

    String qiujiu = "3215987";
    String linshi = "";

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.VISIBLE);
        getNavigate().setTitlText("")
                .hideLeftIcon()
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        getNavigate().getRightTextView().setPadding(AppUtils.dp2px(this, 8f), AppUtils.dp2px(this, 4f), AppUtils.dp2px(this, 8f), AppUtils.dp2px(this, 4f));
        getNavigate().getRightTextView().setBackgroundResource(R.drawable.shape_choosed_confirm);
        getNavigate().setRightText(AppUtils.getString(R.string.cancel));
    }

    @Override
    public void doInitDelay() {
        bean = AppDatas.MsgDB().getJieSuoDao().queryOneItem(AppAuth.get().getUserID(), AppAuth.get().getDomainCode());
//        mGestureLockViewGroup.setAnswer(new int[]{1, 2, 3, 4, 5});
        views.add(new ImageView(this));
        for (int i = 0; i < ll_all1.getChildCount(); i++) {
            views.add((ImageView) ll_all1.getChildAt(i));
        }
        for (int i = 0; i < ll_all2.getChildCount(); i++) {
            views.add((ImageView) ll_all2.getChildAt(i));
        }
        for (int i = 0; i < ll_all3.getChildCount(); i++) {
            views.add((ImageView) ll_all3.getChildAt(i));
        }

        initNotice();

        ll_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAnswer = false;
                mGestureLockViewGroup.reset();
                if(isReSet) {
                    linshi = bean.jiesuo;
                    reset();
                    changeSelect();
                } else {
                    linshi = "";
                    reset();
                }
            }
        });
        mGestureLockViewGroup
                .setOnGestureLockViewListener(new GestureLockViewGroup.OnGestureLockViewListener() {

                    @Override
                    public void onUnmatchedExceedBoundary() {
                        mGestureLockViewGroup.setUnMatchExceedBoundary(5);
                    }

                    @Override
                    public void onGestureChoose(List<Integer> mChoose) {
                        if (!isAnswer) {
                            isAnswer = true;
                            linshi = toStringArray(mChoose);
                            reset();
                            changeSelect();
                            tv_notic.setText(getString(R.string.common_notice9));
                            int[] a = toIntArray(mChoose);
                            mGestureLockViewGroup.reset();
                            mGestureLockViewGroup.setAnswer(a);
                        }
                    }

                    @Override
                    public void onGestureEvent(boolean matched) {
                        if (isAnswer) {
                            if (matched) {

                                if (bean == null) {
                                    bean = new JieSuoBean(AppAuth.get().getUserID(), AppAuth.get().getDomainCode(), toStringArray(mGestureLockViewGroup.getAnswer()), true, "", "", "", "");
                                } else {
                                    AppDatas.MsgDB().getJieSuoDao().deleteByUser(AppAuth.get().getUserID(), AppAuth.get().getDomainCode());
                                    bean.jiesuo = toStringArray(mGestureLockViewGroup.getAnswer());
                                    bean.isJieSuo = true;
                                }
                                AppDatas.MsgDB().getJieSuoDao().insertAll(bean);
                                mGestureLockViewGroup.reset();
                                showToast(getString(R.string.common_notice10));
                                finish();
                            } else {
                                showToast(getString(R.string.common_notice11));
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        isAnswer = false;
                                        mGestureLockViewGroup.reset();
                                        reset();
                                    }
                                }, 1000);
                            }
                        }
                    }

                    @Override
                    public void onBlockSelected(int cId) {
                    }
                });
    }

    private void initNotice() {
        if(isReSet) {
            tv_gongneng.setText(getString(R.string.common_notice12));
            linshi = bean.jiesuo;
            changeSelect();
        } else {
            tv_gongneng.setText(getString(R.string.common_notice13));
            reset();
        }
    }

    private void reset() {
        for (ImageView temp : views) {
            temp.setBackgroundResource(R.drawable.blue_white_point);
        }
    }

    private void changeSelect() {
        for (int i = 0; i < views.size(); i++) {
            if (linshi.contains(i + "")) {
                views.get(i)
                        .setBackgroundResource(R.drawable.blue_point);
            }
        }
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

}
