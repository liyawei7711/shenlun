package huaiye.com.vim.ui.chat;

import android.view.View;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.dao.msgs.ChatMessageBase;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;


@BindLayout(R.layout.activity_chat_yuehoujifeng_text)
public class YueHouJiFengTextActivity extends AppBaseActivity {
    @BindView(R.id.text_yuehoujifeng_content)
    TextView textYuehoujifengContent;
    @BindView(R.id.text_yuehoujifeng_time)
    TextView textYuehoujifengTime;

    @BindExtra
    ChatMessageBase chatMessage;

    private Disposable mDisposable;

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void doInitDelay() {

        if (null != chatMessage) {
            textYuehoujifengContent.setText(chatMessage.msgTxt);
            textYuehoujifengTime.setText(chatMessage.fireTime + "''");
            coutTime();
        }
    }

    private void coutTime() {

        mDisposable = Flowable.intervalRange(0, chatMessage.fireTime+1, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        textYuehoujifengTime.setText(String.valueOf(chatMessage.fireTime - aLong) + "''");
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        textYuehoujifengTime.setText("0''");
                        finish();
                    }
                })
                .subscribe();
    }

    @Override
    protected void onDestroy() {
        showToast(AppUtils.getString(R.string.string_name_yuehoujifeng_tip));
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        MessageEvent nMessageEvent=new MessageEvent(AppUtils.EVENT_MESSAGE_YUEHOUJIFENG);
        nMessageEvent.obj1=chatMessage;
        EventBus.getDefault().post(nMessageEvent);
    }
}
