package huaiye.com.vim.ui.home.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.util.Calendar;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.views.pickers.CustomDatePicker;

import static huaiye.com.vim.common.AppUtils.getString;
import static huaiye.com.vim.common.AppBaseActivity.showToast;

/**
 * author: admin
 * date: 2018/04/23
 * version: 0
 * mail: secret
 * desc: SelectedDialog
 */

public class SelectedDialog extends Dialog implements View.OnClickListener {
    TextView tv_start_time;
    TextView tv_end_time;

    TextView tv_cancel;
    TextView tv_clear;
    TextView tv_sure;

    String oldStart = "";
    String oldEnd = "";
    long oldStartLong = 0;
    long oldEndLong = 0;

    boolean clearStatus;//is clear

    private IOnCheckedListener iOnCheckedListener;

    public interface IOnCheckedListener {
        void onCheckedStart(String str, long timelong);

        void onCheckedEnd(String end, long timelong);

        void comfig(String start, String end);
    }

    public SelectedDialog(@NonNull Context context) {
        super(context, R.style.time_dialog);

        setCancelable(true);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable());
        getWindow().setGravity(Gravity.CENTER);

        setContentView(R.layout.dialog_select);

        init();
    }

    private void init() {
        tv_start_time = (TextView) findViewById(R.id.tv_start_time);
        tv_end_time = (TextView) findViewById(R.id.tv_end_time);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_clear = (TextView) findViewById(R.id.tv_clear);
        tv_sure = (TextView) findViewById(R.id.tv_sure);

        tv_start_time.setOnClickListener(this);
        tv_end_time.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        tv_clear.setOnClickListener(this);
        tv_sure.setOnClickListener(this);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (clearStatus) {
                    tv_start_time.setText(oldStart);
                    if (oldStartLong != 0) {
                        tv_start_time.setTag(R.id.start_time, oldStartLong);
                    } else {
                        tv_start_time.setTag(R.id.start_time, null);
                    }

                    tv_end_time.setText(oldEnd);
                    if (oldEndLong != 0) {
                        tv_end_time.setTag(R.id.end_time, oldEndLong);
                    } else {
                        tv_end_time.setTag(R.id.end_time, null);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Calendar nowTime = Calendar.getInstance();
        Calendar startTime = Calendar.getInstance();
        startTime.add(Calendar.YEAR,-1);

        switch (v.getId()) {
            case R.id.tv_start_time:
                CustomDatePicker start = new CustomDatePicker(1, getContext(), new CustomDatePicker.ResultHandler() {
                    @Override
                    public void handle(String time, long timelong) {
                        if (checkTimeStartEnd(timelong, true)) return;
                        tv_start_time.setText(time.split(" ")[0]);
                        tv_start_time.setTag(R.id.start_time, timelong);
                        if (iOnCheckedListener != null) {
                            iOnCheckedListener.onCheckedStart(tv_start_time.getText().toString(), timelong);
                        }
                    }
                }, startTime.getTime(),
                        nowTime.getTime());
                start.showYear(true).showHour(false).showMint(false).setIsLoop(false);
                start.show(startTime.getTime().getTime());
                break;
            case R.id.tv_end_time:
                CustomDatePicker end = new CustomDatePicker(1, getContext(), new CustomDatePicker.ResultHandler() {
                    @Override
                    public void handle(String time, long timelong) {
                        if (checkTimeStartEnd(timelong, false)) return;
                        tv_end_time.setText(time.split(" ")[0]);
                        tv_end_time.setTag(R.id.end_time, timelong);
                        if (iOnCheckedListener != null) {
                            iOnCheckedListener.onCheckedEnd(tv_end_time.getText().toString(), timelong);
                        }
                    }
                }, startTime.getTime(),
                        nowTime.getTime());
                end.showYear(true).showHour(false).showMint(false).setIsLoop(false);
                end.show(nowTime.getTime().getTime());
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_clear:
                clearStatus = true;
                tv_start_time.setText("");
                tv_start_time.setTag(R.id.start_time, null);
                tv_end_time.setText("");
                tv_end_time.setTag(R.id.end_time, null);
                break;
            case R.id.tv_sure:
                clearStatus = false;
                if (iOnCheckedListener != null) {
                    iOnCheckedListener.comfig(tv_start_time.getText().toString(), tv_end_time.getText().toString());
                }
                dismiss();
                break;
        }
    }

    private boolean checkTimeStartEnd(long time, boolean isStart) {
        if (isStart) {
            if (!TextUtils.isEmpty(tv_end_time.getText().toString()) && tv_end_time.getTag(R.id.end_time) != null) {
                long timeEnd = (long) tv_end_time.getTag(R.id.end_time);
                if (time > timeEnd) {
                    showToast(getString(R.string.common_notice4));
                    return true;
                }
            } else {
                if (tv_end_time.getTag(R.id.end_time) != null && TextUtils.isEmpty(tv_end_time.getText().toString())) {
                    tv_end_time.setTag(R.id.end_time, null);
                }
            }
        } else {
            if (!TextUtils.isEmpty(tv_start_time.getText().toString()) && tv_start_time.getTag(R.id.start_time) != null) {
                long timeStart = (long) tv_start_time.getTag(R.id.start_time);
                if (time < timeStart) {
                    showToast(getString(R.string.common_notice4));
                    return true;
                }
            } else {
                if (tv_start_time.getTag(R.id.start_time) != null && TextUtils.isEmpty(tv_start_time.getText().toString())) {
                    tv_start_time.setTag(R.id.start_time, null);
                }
            }
        }
        return false;
    }

    public void setListener(IOnCheckedListener iOnCheckedListener) {
        this.iOnCheckedListener = iOnCheckedListener;
    }

    public void showDialog() {
        clearStatus = true;
        oldStart = tv_start_time.getText().toString();
        if (tv_start_time.getTag(R.id.start_time) != null)
            oldStartLong = (long) tv_start_time.getTag(R.id.start_time);

        oldEnd = tv_end_time.getText().toString();
        if (tv_end_time.getTag(R.id.end_time) != null)
            oldEndLong = (long) tv_end_time.getTag(R.id.end_time);
        show();
    }
}
