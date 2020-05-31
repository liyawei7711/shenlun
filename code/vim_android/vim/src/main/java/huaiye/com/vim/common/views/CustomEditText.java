package huaiye.com.vim.common.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;

import huaiye.com.vim.R;


@SuppressLint("AppCompatCustomView")
public class CustomEditText extends EditText {

    private static final String Tag = CustomEditText.class.getSimpleName();
    /**
     * 上下文
     */
    private Context context;

    /**
     * 清除标记
     */
    private boolean CLEAR_FLAG = true;

    /**
     * 展示密码标记
     */
    private boolean SHOW_FLAG = false;

    /**
     * 输入字符动态监听
     */
    private CustomWatcher watcher;

    /**
     * 输入字符动态监听
     **/
    private TextWatcher txtWatcher;

    /**
     * 过滤的字符串
     */
    private ArrayList<Character> filterChars = new ArrayList<>();
    /**
     * left, top, right, and bottom
     */
    public Drawable[] drawables;
    /**
     * 抖动动画
     */
    private Animation shakeAnimation;
    /**
     * 缩放动画
     */
    private Animation scaleAnimation;
    /**
     * 动画循环次数
     */
    private int cycles = 2;

    private long ANIM_DUR = 600;

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (SHOW_FLAG)
            this.setTransformationMethod(PasswordTransformationMethod.getInstance());

        lazyInit(context);
    }

    public CustomEditText(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void lazyInit(Context context) {
        this.context = context;
        watcher = new CustomWatcher();
        this.addTextChangedListener(watcher);

        Collections.addAll(filterChars, '\n', '"', '“', '”');

        drawables = this.getCompoundDrawables();
        if (drawables[2] == null && CLEAR_FLAG) {
            drawables[2] = context.getResources().getDrawable(R.drawable.cross);
            drawables[2].setBounds(0, 0, drawables[2].getMinimumWidth(), drawables[2].getMinimumHeight());
        }
        int c = dip2Px(16);
        drawables[2].setBounds(0, 0, c, c);
        Editable s = this.getText();
        if (s == null || s.toString().length() == 0) {
            if (CLEAR_FLAG) {
                CustomEditText.this.setCompoundDrawables(drawables[0], drawables[1], null, drawables[3]);
            }
        } else {
            CustomEditText.this.setSelection(s.toString().length());
        }
        shakeAnimation = new TranslateAnimation(10, -10, -10, 10);
        scaleAnimation = new ScaleAnimation(1f, 0.9f, 1f, 0.9f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
    }

    /**
     * 抖动动画
     */
    public void shake() {
        this.clearAnimation();
        shakeAnimation.setInterpolator(new CycleInterpolator(cycles));
        shakeAnimation.setDuration(ANIM_DUR);
        this.startAnimation(shakeAnimation);
    }

    public void addTextWatcher(TextWatcher watcher) {
        this.txtWatcher = watcher;
    }

    /**
     * 缩放动画
     */
    public void scale() {
        this.clearAnimation();
        scaleAnimation.setInterpolator(new CycleInterpolator(cycles));
        scaleAnimation.setDuration(ANIM_DUR);
        this.startAnimation(scaleAnimation);
    }

    public void setClearable(boolean clearable) {
        CLEAR_FLAG = clearable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int paddingRightX;
        int paddingTotalRightX;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int d_x = (int) event.getX();
                paddingRightX = this.getWidth() - this.getPaddingRight();
                paddingTotalRightX = this.getWidth() - this.getTotalPaddingRight();

                if (SHOW_FLAG) {
                    if (d_x >= paddingTotalRightX
                            && d_x <= paddingRightX) {
                        this.setEnabled(false);
                        this.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                // 触摸离开屏幕
                int x = (int) event.getX();
                paddingRightX = this.getWidth() - this.getPaddingRight();
                paddingTotalRightX = this.getWidth() - this.getTotalPaddingRight();

                if (CLEAR_FLAG) {
                    if (x >= paddingTotalRightX && x <= paddingRightX) {
                        this.setText("");
                        if (iClose != null) {
                            iClose.clearText();
                        }
                    }
                } else if (SHOW_FLAG) {
                    //手势收起 密码隐藏
                    this.setEnabled(true);
                    this.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

                break;

            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        if (focused) {
            if (CLEAR_FLAG) {
                int length = CustomEditText.this.getText().toString().length();
                if (length > 0) {
                    CustomEditText.this.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
                }
            }
        } else {
            CustomEditText.this.setCompoundDrawables(drawables[0], drawables[1], null, drawables[3]);
        }

        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    /**
     * 字符变化动态监听
     *
     * @author Administrator
     */
    class CustomWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            if (txtWatcher != null) {
                txtWatcher.beforeTextChanged(s, start, count, after);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

            if (txtWatcher != null) {
                txtWatcher.onTextChanged(s, start, before, count);
            }

            String textOn = s.toString();
            int length = textOn.length();

            if (start > length - 1) {
                start = length - 1;
            }

            if (length > 0) {
                char lastChar = s.charAt(start);
                if (filterChars.contains(lastChar)) {
                    textOn = textOn.replaceAll(String.valueOf(lastChar), "");
                    CustomEditText.this.setText(textOn);
                    CustomEditText.this.setSelection(textOn.length());
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

            if (txtWatcher != null) {
                txtWatcher.afterTextChanged(s);
            }

            int length = CustomEditText.this.getText().toString().length();
            if (length > 0) {
                if (CLEAR_FLAG) {
                    CustomEditText.this.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
                }
            } else {
                if (CLEAR_FLAG) {
                    CustomEditText.this.setCompoundDrawables(drawables[0], drawables[1], null, drawables[3]);
                }
            }
        }
    }

    private int dip2Px(float dip) {
        return (int) (dip * getContext().getResources().getDisplayMetrics().density + 0.5f);
    }

    IClose iClose;

    public void setClose(IClose iClose) {
        this.iClose = iClose;
    }

    public interface IClose {
        void clearText();
    }
}
