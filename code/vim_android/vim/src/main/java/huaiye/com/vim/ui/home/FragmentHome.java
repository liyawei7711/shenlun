package huaiye.com.vim.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseFragment;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContacts;
import huaiye.com.vim.ui.home.dialog.SelectedDialog;
import huaiye.com.vim.ui.meet.CreateMeetPopupWindow;
import huaiye.com.vim.ui.meet.MeetCreateByAllFriendActivity;
import huaiye.com.vim.ui.meet.MeetCreateOrderActivity;
import huaiye.com.vim.ui.meet.MeetJoineActivity;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: FragmentHome
 */
@BindLayout(R.layout.fragment_home)
public class FragmentHome extends AppBaseFragment {

    FragmentCurrentMeetings fgCurrentMeetings;
    FragmentHistoryMeetings fgHistoryMeetings;
    FragmentAllMeetings fgAllMeetings;

    @BindView(R.id.ll_root)
    ViewGroup ll_root;
    @BindView(R.id.rg_home_menu)
    RadioGroup rg_home_menu;
    @BindView(R.id.rg_home_menu_admin)
    RadioGroup rg_home_menu_admin;
    @BindView(R.id.more)
    View more;
    @BindView(R.id.left)
    View left;
    @BindView(R.id.view_cover)
    View view_cover;

    CreateMeetPopupWindow createMeetPopupWindow;
    SelectedDialog selectedDialog;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getNavigate().setVisibility(View.GONE);

        if (AppDatas.Auth().getUserLoginName().equals("admin")) {
            rg_home_menu_admin.setVisibility(View.VISIBLE);
            rg_home_menu.setVisibility(View.GONE);
        } else {
            rg_home_menu_admin.setVisibility(View.GONE);
            rg_home_menu.setVisibility(View.VISIBLE);
        }

        selectedDialog = new SelectedDialog(getContext());

        createMeetPopupWindow = new CreateMeetPopupWindow(getContext());
        createMeetPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                view_cover.setVisibility(View.GONE);
            }
        });
        createMeetPopupWindow.setConfirmClickListener(new CreateMeetPopupWindow.ConfirmClickListener() {

            @Override
            public void onCreateJiShi() {
                ChoosedContacts.get().clear();
                startActivity(new Intent(getContext(), MeetCreateByAllFriendActivity.class));
            }

            @Override
            public void onCreateYuYue() {
                ChoosedContacts.get().clear();
                startActivity(new Intent(getContext(), MeetCreateOrderActivity.class));
            }

            @Override
            public void onJoine() {
                startActivity(new Intent(getContext(), MeetJoineActivity.class));
            }

            @Override
            public void onCancel() {

            }
        });

        rg_home_menu_admin.setOnCheckedChangeListener(listener);
        rg_home_menu.setOnCheckedChangeListener(listener);

        addFragments(savedInstanceState);
    }

    RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton rbtn = (RadioButton) getContentView().findViewById(checkedId);
            if (!rbtn.isChecked()) {
                return;
            }

            FragmentTransaction fts = getChildFragmentManager().beginTransaction();

            fts.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(fgCurrentMeetings)
                    .hide(fgAllMeetings)
                    .hide(fgHistoryMeetings);

            switch (checkedId) {
                case R.id.rbtn_home_current:
                case R.id.rbtn_home_current_admin:
                    left.setVisibility(View.GONE);
                    fts.show(fgCurrentMeetings);

                    break;
                case R.id.rbtn_home_history:
                case R.id.rbtn_home_history_admin:
                    left.setVisibility(View.VISIBLE);

                    fts.show(fgHistoryMeetings);

                    break;
                case R.id.rbtn_home_all:
                    left.setVisibility(View.GONE);

                    fts.show(fgAllMeetings);

                    break;
            }
            try {
                fts.commitAllowingStateLoss();
            } catch (Exception e) {

            }
        }
    };

    void addFragments(Bundle savedInstanceState) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction fts = fm.beginTransaction();
        if (savedInstanceState != null) {
            fgCurrentMeetings = (FragmentCurrentMeetings) fm.findFragmentByTag(FragmentCurrentMeetings.class.getSimpleName());
            fgHistoryMeetings = (FragmentHistoryMeetings) fm.findFragmentByTag(FragmentHistoryMeetings.class.getSimpleName());
            fgAllMeetings = (FragmentAllMeetings) fm.findFragmentByTag(FragmentAllMeetings.class.getSimpleName());
        } else {
            fgCurrentMeetings = new FragmentCurrentMeetings();
            fgHistoryMeetings = new FragmentHistoryMeetings();
            fgAllMeetings = new FragmentAllMeetings();
            selectedDialog.setListener(fgHistoryMeetings.getOnCheckedListener());

            fts.add(R.id.meet_content, fgCurrentMeetings, FragmentCurrentMeetings.class.getSimpleName())
                    .hide(fgCurrentMeetings)
                    .add(R.id.meet_content, fgHistoryMeetings, FragmentHistoryMeetings.class.getSimpleName())
                    .hide(fgHistoryMeetings)
                    .add(R.id.meet_content, fgAllMeetings, FragmentAllMeetings.class.getSimpleName())
                    .hide(fgAllMeetings);
        }

        switch (rg_home_menu.getCheckedRadioButtonId()) {
            case R.id.rbtn_home_current:
            case R.id.rbtn_home_current_admin:

                fts.show(fgCurrentMeetings);

                break;
            case R.id.rbtn_home_history:
            case R.id.rbtn_home_history_admin:

                fts.show(fgHistoryMeetings);

                break;
            case R.id.rbtn_home_all:

                fts.show(fgAllMeetings);

                break;
        }

        try {
            fts.commitAllowingStateLoss();
        } catch (Exception e) {

        }

    }

    @SuppressLint("NewApi")
    @OnClick(R.id.more)
    void onViewClick() {
        createMeetPopupWindow.showView(more);
    }

    @OnClick(R.id.left)
    void onViewClickLeft() {
        selectedDialog.showDialog();
    }

}
