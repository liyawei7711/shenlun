package huaiye.com.vim.common.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.media.MediaStatus;
import com.huaiye.sdk.media.capture.Capture;
import com.huaiye.sdk.sdpmsgs.video.CStartMobileCaptureRsp;
import com.huaiye.sdk.sdpmsgs.video.CStopMobileCaptureRsp;

import java.io.File;
import java.io.IOException;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.rx.RxUtils;

public class ButtonFocusChangeGroupView extends RelativeLayout {


    private View middleView;
    private TextView middleView0;
    private View middleView1;
    private int middleXX;
    private int middleYY;
    private int middleWidth;
    private int middleHeigh;
    private View leftView;
    private View leftView0;
    private int leftXX;
    private int leftYY;
    private int leftWidth;
    private int leftHeigh;
    private View rightView;
    private View rightView0;
    private int rightXX;
    private int rightYY;
    private int rightWidth;
    private int rightHeigh;

    private ButtonFocusChangeListener mButtonFocusChangeListener;
    private boolean isBeginWithMiddleView = false;
    private boolean isTouchLeftView;
    private boolean isTouchRightView;

    private final int WHAT_TIME = 101;
    private final int WHAT_RECORD_OVER = 102;
    private String mFilePath = null;
    /**
     * 最短录音时间
     **/
    private int MIN_INTERVAL_TIME = 1000;
    /**
     * 最长录音时间
     **/
    private int MAX_INTERVAL_TIME = 1000 * 60;
    private long mStartTime;

    private ObtainDecibelThreadNew mthread;
    private Handler mShowTimeHandler;

    private Point startPoint = new Point();
    private Point assistPoint = new Point();
    private Point endPoint = new Point();
    private Paint mPaint = new Paint();
    private Path mPath = new Path();


    public void setOnButtonFocusChangeListener(ButtonFocusChangeListener buttonFocusChangeListener) {
        this.mButtonFocusChangeListener = buttonFocusChangeListener;
    }

    public ButtonFocusChangeGroupView(Context context) {
        super(context);
        mShowTimeHandler = new ShowTimeHandler();
    }

    public ButtonFocusChangeGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ButtonFocusChangeGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        middleView = getChildAt(1);
        middleView0 = (TextView) ((ViewGroup) middleView).getChildAt(0);
        middleView1 = ((ViewGroup) middleView).getChildAt(1);
        middleXX = (int) middleView.getX() + (int) middleView1.getX();
        middleYY = (int) middleView.getY() + (int) middleView1.getY();
        middleWidth = middleView1.getWidth();
        middleHeigh = middleView1.getHeight();

        leftView = getChildAt(0);
        leftView0 = ((ViewGroup) leftView).getChildAt(0);

        assistPoint.x = middleXX + middleWidth / 2;
        assistPoint.y = middleYY + middleHeigh;


        leftXX = (int) leftView.getX() + (int) leftView0.getX();
        leftYY = (int) leftView.getY() + (int) leftView0.getY();
        leftWidth = leftView0.getWidth();
        leftHeigh = leftView0.getHeight();
        startPoint.x = leftXX + leftWidth / 2;
        startPoint.y = leftYY + leftHeigh / 2;


        rightView = getChildAt(2);
        rightView0 = ((ViewGroup) getChildAt(2)).getChildAt(0);
        rightXX = (int) rightView.getX() + (int) rightView0.getX();
        rightYY = (int) rightView.getY() + (int) rightView0.getY();
        rightWidth = rightView0.getWidth();
        rightHeigh = rightView0.getHeight();
        endPoint.x = rightXX + rightWidth / 2;
        endPoint.y = rightYY + rightHeigh / 2;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isTouchLeftView = false;
                isTouchRightView = false;
                int beginX = (int) event.getX();
                int beginY = (int) event.getY();
                isBeginWithMiddleView = false;
                if (beginX >= middleXX && beginX <= middleXX + middleWidth && beginY >= middleYY && beginY <= middleYY + middleHeigh) {
                    isBeginWithMiddleView = true;
                    middleView0.setText("00:00");

                    middleView1.setEnabled(true);
                    leftView0.setVisibility(View.VISIBLE);
                    rightView0.setVisibility(View.VISIBLE);
                    startRecord();
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:

                if (!isBeginWithMiddleView) {
                    break;
                }
                mShowTimeHandler.sendEmptyMessage(WHAT_RECORD_OVER);
                isBeginWithMiddleView = false;
                invalidate();
                int endX = (int) event.getX();
                int endY = (int) event.getY();
                if (endX >= middleXX && endX <= middleXX + middleWidth && endY >= middleYY && endY <= middleYY + middleHeigh) {
//                    if (null != mButtonFocusChangeListener) {
//                        mButtonFocusChangeListener.onFocusSend();
//                    }
                } else if (endX >= leftXX && endX <= leftXX + leftWidth && endY >= leftYY && endY <= leftYY + leftHeigh) {
//                    if (null != mButtonFocusChangeListener) {
//                        mButtonFocusChangeListener.onFocusLeft();
//                    }
                } else if (endX >= rightXX && endX <= rightXX + rightWidth && endY >= rightYY && endY <= rightYY + rightHeigh) {
//                    if (null != mButtonFocusChangeListener) {
//                        mButtonFocusChangeListener.onFocusRight();
//                    }
                } else {
//                    if (null != mButtonFocusChangeListener) {
//                        mButtonFocusChangeListener.onFocusSend();
//                    }
                }
                leftView0.setEnabled(false);
                rightView0.setEnabled(false);
                middleView1.setEnabled(false);
                leftView0.setVisibility(View.INVISIBLE);
                rightView0.setVisibility(View.INVISIBLE);
                if (isTouchRightView || isTouchRightView) {
                    middleView0.setText(AppUtils.getString(R.string.press_speak));
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isBeginWithMiddleView) {
                    break;
                }
                int tempNowX = (int) event.getX();
                int tempNowY = (int) event.getY();
                if (tempNowX >= middleXX && tempNowX <= middleXX + middleWidth && tempNowY >= middleYY && tempNowY <= middleYY + middleHeigh) {
                    middleView1.setEnabled(true);
                    leftView0.setEnabled(false);
                    rightView0.setEnabled(false);
                    isTouchLeftView = false;
                    isTouchRightView = false;

                } else if (tempNowX >= leftXX && tempNowX <= leftXX + leftWidth && tempNowY >= leftYY && tempNowY <= leftYY + leftHeigh) {
                    leftView0.setEnabled(true);
                    isTouchLeftView = true;
                    isTouchRightView =false;
                    middleView0.setText(getContext().getString(R.string.songkai_cancel));

                } else if (tempNowX >= rightXX && tempNowX <= rightXX + rightWidth && tempNowY >= rightYY && tempNowY <= rightYY + rightHeigh) {
                    rightView0.setEnabled(true);
                    isTouchLeftView = false;
                    isTouchRightView =true;
                    middleView0.setText(getContext().getString(R.string.songkai_destrory));

                } else {
                    leftView0.setEnabled(false);
                    rightView0.setEnabled(false);
                    isTouchLeftView = false;
                    isTouchRightView = false;
                }

                break;
            case MotionEvent.ACTION_CANCEL:
                isBeginWithMiddleView = false;
                invalidate();
                leftView0.setVisibility(View.INVISIBLE);
                rightView0.setVisibility(View.INVISIBLE);
                break;
        }
        return true;
    }

    /**
     * action up 所触发的事件
     */
    public interface ButtonFocusChangeListener {
        void onFocusLeft();

        void onFocusRight();

        void onFocusSend(String voiceRecordPath);
    }

    public void cancelRecord() {
        if (mthread != null) {
            mthread.exit();
            mthread = null;
        }
        File file = new File(mFilePath);
        if (file.exists()) {
            file.delete();
        }
        if (HYClient.getHYCapture().isCapturing()) {
            HYClient.getHYCapture().stopCapture(new SdkCallback<CStopMobileCaptureRsp>() {
                @Override
                public void onSuccess(CStopMobileCaptureRsp resp) {
                }

                @Override
                public void onError(ErrorInfo errorInfo) {
                    // 停止采集失败
                }

            });
        }
    }

    private void setDefaultFilePath() {
        File fileDir = new File(getContext().getExternalFilesDir(null) + File.separator + "Vim");
        File file = new File(getContext().getExternalFilesDir(null) + File.separator + "Vim", System.currentTimeMillis() + ".amr");

        try {
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mFilePath = file.getAbsolutePath();
    }

    private void startRecord() {
        setDefaultFilePath();
        if (HYClient.getHYCapture().isCapturing()) {
            HYClient.getHYCapture().stopCapture(new SdkCallback<CStopMobileCaptureRsp>() {
                @Override
                public void onSuccess(CStopMobileCaptureRsp resp) {
                    // 停止采集成功
                    startRecordNow();

                }

                @Override
                public void onError(ErrorInfo errorInfo) {
                    // 停止采集失败
                }

            });
        } else {
            startRecordNow();
        }

    }

    private void startRecordNow() {
        if (mthread != null) {
            mthread.exit();
            mthread = null;
        }
        if (null == mShowTimeHandler) {
            mShowTimeHandler = new ShowTimeHandler();
        }
        HYClient.getSdkOptions().Capture().setCaptureOfflineMode(false);
        HYClient.getHYCapture().startCapture(Capture.Params.get()
                .setEnableServerRecord(true)
                .setWaitRecordId(true)
                .setAudioAmplitude(true)
                .setAudioOn(true)
                .setVideoOn(false)
                .setRecordPath(mFilePath), new Capture.Callback() {
            @Override
            public void onRepeatCapture() {
                // 重复采集
                // 走到此回调，不会再走onSuccess
            }

            @Override
            public void onSuccess(CStartMobileCaptureRsp resp) {
                // 采集成功
            }

            @Override
            public void onError(ErrorInfo error) {
                // 采集出错
            }

            @Override
            public void onCaptureStatusChanged(SdpMessageBase msg) {
                if (null == msg) {
                    return;
                }
                switch (MediaStatus.get(msg)) {
                    case CAPTURE_MEMORY:
                        // 若开启了本地录像选项，则会有此回调消息
                        // 阈值：MediaStatus.CaptureMemory.getLimitSizeMB(msg))
                        // 空闲：MediaStatus.CaptureMemory.getFreeSizeMB(msg)
                        // 总共：MediaStatus.CaptureMemory.getTotalSizeMB(msg)
                        // 空间是否足够录像: MediaStatus.CaptureMemory.isMemoryEnough(msg)
                        break;
                    case CAPTURE_QUALITY:
                        // 采集质量：通过MediaStatus方法获取采集质量

                        break;
                    case CAPTURE_NETWORK:
                        // 采集时与服务器的连接状态
                        break;
                    case CAPTURE_CAMERA:
                        // 采集时摄像头切换
                        break;
                }
            }
        });

        mthread = new ObtainDecibelThreadNew();
        mthread.start();
        mStartTime = System.currentTimeMillis();
    }


    private void stopRecordNow() {
        if (mthread != null) {
            mthread.exit();
            mthread = null;
        }
        if (HYClient.getHYCapture().isCapturing()) {
            HYClient.getHYCapture().stopCapture(new SdkCallback<CStopMobileCaptureRsp>() {
                @Override
                public void onSuccess(CStopMobileCaptureRsp resp) {
                    // 停止采集成功
                    if (null != mButtonFocusChangeListener) {
                        if (isTouchLeftView) {
                            mButtonFocusChangeListener.onFocusLeft();
                        } else if (isTouchRightView) {
                            mButtonFocusChangeListener.onFocusRight();
                        } else {
                            mButtonFocusChangeListener.onFocusSend(mFilePath);
                        }
                    }


                }

                @Override
                public void onError(ErrorInfo errorInfo) {
                    // 停止采集失败
                }

            });

            HYClient.getSdkOptions().Capture().setCaptureOfflineMode(false);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isBeginWithMiddleView) {
            return;
        }
        mPaint.setColor(getResources().getColor(R.color.color_DCDCDC)); //画笔颜色
        mPaint.setStrokeWidth(2); //画笔宽度
        mPaint.setStyle(Paint.Style.STROKE);

        mPath.reset();
        //起点
        mPath.moveTo(startPoint.x, startPoint.y);
        //mPath
        mPath.quadTo(assistPoint.x, assistPoint.y, endPoint.x, endPoint.y);
        //画path
        canvas.drawPath(mPath, mPaint);
        //画控制点
        canvas.drawPoint(assistPoint.x, assistPoint.y, mPaint);


    }

    private class ObtainDecibelThreadNew extends Thread {

        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {

                if (!HYClient.getHYCapture().isCapturing() && !running) {
                    break;
                }
                //发送时间
                mShowTimeHandler.sendEmptyMessage(WHAT_TIME);
                if (System.currentTimeMillis() - mStartTime >= MAX_INTERVAL_TIME) {
                    // 如果超过最长录音时间
                    mShowTimeHandler.sendEmptyMessage(WHAT_RECORD_OVER);
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    class ShowTimeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_TIME:
                    if (null == middleView0 || isTouchLeftView || isTouchRightView) {
                        break;
                    }
                    long nowTime = System.currentTimeMillis();
                    int time = ((int) (nowTime - mStartTime) / 1000);
                    int second = time % 60;
                    int mil = time / 60;
                    if (mil < 10) {
                        if (second < 10)
                            middleView0.setText("0" + mil + ":0" + second);
                        else
                            middleView0.setText("0" + mil + ":" + second);
                    } else if (mil >= 10 && mil < 60) {
                        if (second < 10)
                            middleView0.setText(mil + ":0" + second);
                        else
                            middleView0.setText(mil + ":" + second);
                    }
                    break;
                case WHAT_RECORD_OVER:
                    finishRecord();
                    break;
            }
        }
    }

    private void finishRecord() {
        if (null != leftView0) {
            leftView0.setEnabled(false);
            leftView0.setVisibility(View.INVISIBLE);

        }
        if (null != middleView1) {
            middleView1.setEnabled(false);

        }
        if (null != middleView0 && (isTouchRightView || isTouchLeftView)) {
            middleView0.setText(AppUtils.getString(R.string.press_speak));
        }
        if (null != rightView0) {
            rightView0.setEnabled(false);
            rightView0.setVisibility(View.INVISIBLE);
        }

        long intervalTime = System.currentTimeMillis() - mStartTime;
        if (intervalTime < MIN_INTERVAL_TIME) {
            AppBaseActivity.showToast(getContext().getResources().getString(R.string.time_too_short));
            new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                @Override
                public void onMainDelay() {
                    cancelRecord();//时间太短,关闭并删除本地文件

                }
            }, "stopRecording");

            return;
        } else {
            stopRecordNow();//录制完毕,需要回调UI界面处理业务逻辑
        }
    }
}
