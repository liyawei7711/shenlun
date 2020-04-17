package huaiye.com.vim.ui.guide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import java.util.ArrayList;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.ui.auth.StartActivity;
import ttyy.com.jinviews.pagers.BeizerCircleIndicatedViewPager;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: GuideActivity
 */
@BindLayout(R.layout.activity_guide)
public class GuideActivity extends AppBaseActivity {

    @BindView(R.id.pager)
    BeizerCircleIndicatedViewPager pager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void doInitDelay() {
        ImagesAdapter adapter = new ImagesAdapter();
        adapter.views = new ArrayList<>();

        ImageView img_guide_0 = new ImageView(this);
        img_guide_0.setScaleType(ImageView.ScaleType.FIT_XY);
        img_guide_0.setImageResource(R.drawable.img_guide_0);
        adapter.views.add(img_guide_0);

        ImageView img_guide_1 = new ImageView(this);
        img_guide_1.setScaleType(ImageView.ScaleType.FIT_XY);
        img_guide_1.setImageResource(R.drawable.img_guide_1);
        adapter.views.add(img_guide_1);

        ImageView img_guide_2 = new ImageView(this);
        img_guide_2.setScaleType(ImageView.ScaleType.FIT_XY);
        img_guide_2.setImageResource(R.drawable.img_guide_2);
        adapter.views.add(img_guide_2);

        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    findViewById(R.id.btn_next).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    static class ImagesAdapter extends PagerAdapter {

        ArrayList<ImageView> views;

        @Override
        public int getCount() {
            return views == null ? 0 : views.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }
    }

    @OnClick(R.id.btn_next)
    void onNextBtnClicked() {
        startActivity(new Intent(this, StartActivity.class));
        finish();
    }

}