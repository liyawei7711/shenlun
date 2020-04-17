package huaiye.com.vim.ui.meet.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import huaiye.com.vim.ui.meet.fragments.MeetMemberNewFragment;
import huaiye.com.vim.ui.meet.fragments.SpeakerFragment;

/**
 * Created by ywt on 2019/3/4.
 */

public class MeetAdapter extends FragmentPagerAdapter {
    private SpeakerFragment mSpeakerFragment;
//    private MeetMemberFragment mMeetMemberFragment;
    private MeetMemberNewFragment mMeetMemberNewFragment;

    public MeetAdapter(FragmentManager fm, String meetId) {
        super(fm);
        mSpeakerFragment = new SpeakerFragment();
//        mSpeakerFragment.setMeetId(meetId);
//        mMeetMemberFragment = new MeetMemberFragment();
        mMeetMemberNewFragment = new MeetMemberNewFragment();
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return mSpeakerFragment;
        } else {
            return mMeetMemberNewFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
