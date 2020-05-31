package huaiye.com.vim.common.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;

import huaiye.com.vim.R;

public class ChildPlayerView extends FrameLayout {
    private final String TAG = ChildPlayerView.class.getSimpleName();
    VideoParams currentParams;
    TextureView textureView;
    ChildViewListener childViewCloseListener;
    private GestureDetector gestureDetector;

    public ChildPlayerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ChildPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChildPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View viewRoot = LayoutInflater.from(context).inflate(R.layout.player_child_view, this, true);
        textureView = viewRoot.findViewById(R.id.texture);

        gestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (childViewCloseListener != null){
                    childViewCloseListener.onChildViewDoubleClick(ChildPlayerView.this);
                }
                return true;
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }


    public void setChildViewCloseListener(ChildViewListener childViewCloseListener) {
        this.childViewCloseListener = childViewCloseListener;
    }


    public TextureView getTextureView() {
        return textureView;
    }

    public interface ChildViewListener {
        void onChildViewClose(ChildPlayerView childView);
        void onChildViewDoubleClick(ChildPlayerView view);
    }
}