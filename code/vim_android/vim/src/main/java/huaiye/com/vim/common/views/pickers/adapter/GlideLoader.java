package huaiye.com.vim.common.views.pickers.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.lcw.library.imagepicker.utils.ImageLoader;

import huaiye.com.vim.R;
import huaiye.com.vim.VIMApp;

public class GlideLoader implements ImageLoader {
    private RequestOptions mOptions = new RequestOptions()
            .centerCrop()
            .dontAnimate()
            .format(DecodeFormat.PREFER_RGB_565)
            .placeholder(R.drawable.icon_image_default)
            .error(R.drawable.icon_image_error);

    private RequestOptions mPreOptions = new RequestOptions()
            .skipMemoryCache(true)
            .error(R.drawable.icon_image_error);

    @Override
    public void loadImage(ImageView imageView, String imagePath) {
        //小图加载
        Glide.with(imageView.getContext()).load(imagePath).apply(mOptions).into(imageView);
    }

    @Override
    public void loadPreImage(ImageView imageView, String imagePath) {
        //大图加载
        Glide.with(imageView.getContext()).load(imagePath).apply(mPreOptions).into(imageView);

    }

    @Override
    public void clearMemoryCache() {
        //清理缓存
        Glide.get(VIMApp.getInstance().getApplicationContext()).clearMemory();
    }
}