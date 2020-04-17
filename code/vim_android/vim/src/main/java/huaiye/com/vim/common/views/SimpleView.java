package huaiye.com.vim.common.views;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppUtils;

/**
 * author: admin
 * date: 2018/09/13
 * version: 0
 * mail: secret
 * desc: SimpleView
 */

public class SimpleView extends LinearLayout {
    ImageView logo;
    TextView title;
    TextView time;

    public SimpleView(Context context) {
        super(context);
        setPadding(AppUtils.getSize(5), AppUtils.getSize(10), AppUtils.getSize(5), AppUtils.getSize(10));
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setBackgroundResource(R.drawable.shape_white_concer);

        logo = new ImageView(context);
        addView(logo, new LinearLayoutCompat.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        title = new TextView(context);
        title.setSingleLine(true);
        title.setTextSize(16);
        title.setTextColor(Color.parseColor("#3eb1fe"));
        addView(title, new LinearLayoutCompat.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        time = new TextView(context);
        time.setSingleLine(true);
        time.setTextSize(14);
        time.setTextColor(Color.parseColor("#3eb1fe"));
        addView(time, new LinearLayoutCompat.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        showTimeText(View.GONE);

    }

    public void setLogo(int img_id) {
        logo.setImageResource(img_id);
    }

    public void setTileName(String name) {
        title.setText(name);
    }

    public void showTimeText(int visibility){
        time.setVisibility(visibility);
    }

    public void setTimeText(String value) {
        time.setText(value);
    }

    public void setOnClickListeners(OnClickListener clickListener) {
        setTimeText("00:00:00");
        setOnClickListener(clickListener);
    }

}
