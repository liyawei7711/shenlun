package huaiye.com.vim.ui.home.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ttyy.commonanno.Injectors;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import huaiye.com.vim.R;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContacts;
import huaiye.com.vim.ui.meet.MeetCreateByAllFriendActivity;
import huaiye.com.vim.ui.meet.MeetJoineActivity;

/**
 * Created by ywt on 2019/2/22.
 */
@BindLayout(R.layout.fragment_meet_header)
public class FragmentMeetHeaderView extends RelativeLayout implements View.OnClickListener {
    @BindView(R.id.home_meeting_create)
    TextView home_meeting_create;
    @BindView(R.id.home_meeting_appointment)
    TextView home_meeting_appointment;
    @BindView(R.id.home_meeting_join)
    TextView home_meeting_join;

    public FragmentMeetHeaderView(Context context) {
        this(context, null);
        home_meeting_create.setOnClickListener(this);
        home_meeting_appointment.setOnClickListener(this);
        home_meeting_join.setOnClickListener(this);
    }

    public FragmentMeetHeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FragmentMeetHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Injectors.get().injectView(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_meeting_create:
                ChoosedContacts.get().clear();
                Intent intent = new Intent(getContext(), MeetCreateByAllFriendActivity.class);
                intent.putExtra("nMeetType", 1);
                getContext().startActivity(intent);
                break;
            case R.id.home_meeting_appointment:
                ChoosedContacts.get().clear();
//                getContext().startActivity(new Intent(getContext(), MeetCreateOrderActivity.class));
                intent = new Intent(getContext(), MeetCreateByAllFriendActivity.class);
                intent.putExtra("nMeetType", 2);
                getContext().startActivity(intent);
                break;
            case R.id.home_meeting_join:
                getContext().startActivity(new Intent(getContext(), MeetJoineActivity.class));
                break;
            default:
                break;
        }
    }
}
