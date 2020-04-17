package huaiye.com.vim.ui.meet;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import java.io.File;
import java.util.ArrayList;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * author: zhangzhen
 * date: 2019/07/26
 * version: 0
 * mail: secret
 * desc: ImageShowActivity
 */

@BindLayout(R.layout.activity_image_show)
public class ImageShowActivity extends AppBaseActivity {
    private RequestListener requestListener;
    @BindView(R.id.iv_photo)
    ViewPager mViewPager;
    @BindExtra
    ArrayList<String> imageUrls;
    @BindExtra
    boolean isLocal;

    private RequestOptions mOptions = new RequestOptions()
            .fitCenter()
            .dontAnimate()
            .format(DecodeFormat.PREFER_RGB_565)
            .placeholder(R.drawable.icon_image_default)
            .error(R.drawable.icon_image_error);

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(getString(R.string.look_big_image))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        mViewPager.setAdapter(new SamplePagerAdapter(imageUrls));
        mViewPager.setCurrentItem(2);
    }

    class SamplePagerAdapter extends PagerAdapter {
        private ArrayList<String> nImageUrls;

        public SamplePagerAdapter(ArrayList<String> imageUrls) {
            nImageUrls = imageUrls;
        }

        @Override
        public int getCount() {
            return nImageUrls.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            final PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);

            if(requestListener == null) {
                requestListener = new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (resource instanceof GifDrawable) {
                            //加载一次
                            ((GifDrawable) resource).setLoopCount(100);
                        }
                        return false;
                    }
                };
            }

            if (isLocal) {
                if(nImageUrls.get(position).endsWith(".gif")) {
                    Glide.with(container.getContext()).load(new File(nImageUrls.get(position))).listener(requestListener).into(photoView);
                } else {
                    Glide.with(container.getContext())
                            .load(new File(nImageUrls.get(position)))
                            .apply(mOptions).into(photoView);
                }
            } else {
                if(nImageUrls.get(position).endsWith(".gif")) {
                    Glide.with(container.getContext()).load(nImageUrls.get(position)).listener(requestListener).into(photoView);
                } else {
                    Glide.with(container.getContext())
                            .load(nImageUrls.get(position))
                            .apply(mOptions).into(photoView);
                }

            }

            //attacher.update();

            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
