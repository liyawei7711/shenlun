package huaiye.com.vim.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppBaseFragment;
import huaiye.com.vim.common.recycle.LiteBaseAdapter;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.meet.bean.MeetList;
import huaiye.com.vim.ui.home.holder.MeetHolder;
import huaiye.com.vim.ui.meet.MeetActivity;
import huaiye.com.vim.ui.meet.MeetNewActivity;
import huaiye.com.vim.ui.meet.OrderMeetDetailActivity;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: FragmentCurrentMeetings
 */
@BindLayout(R.layout.fragment_home_meetings)
public class FragmentCurrentMeetings extends AppBaseFragment {

    @BindView(R.id.refresh_view)
    SwipeRefreshLayout refresh_view;
    @BindView(R.id.et_key)
    EditText et_key;

    @BindView(R.id.rct_view)
    RecyclerView rct_view;
    @BindView(R.id.iv_empty_view)
    View iv_empty_view;

    LiteBaseAdapter<MeetList.Data> adapter;
    List<MeetList.Data> datas = new ArrayList<>();
    private boolean isLoad = false;
    private int index = 1;
    private long time;

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getNavigate().setVisibility(View.GONE);


        et_key.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    refreshDatas(true);
                }
                return false;
            }
        });
        et_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (System.currentTimeMillis() - time > 1000) {
                    time = System.currentTimeMillis();
                    refreshDatas(true);
                }
            }
        });

        adapter = new LiteBaseAdapter<>(getContext(),
                datas,
                MeetHolder.class,
                R.layout.item_home_meetings_current,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MeetList.Data data = (MeetList.Data) v.getTag();

                        if (data.nStatus == 1) {

                            Intent intent = new Intent(getContext(), MeetNewActivity.class);
                            intent.putExtra("strMeetDomainCode", data.strMainUserDomainCode);
                            intent.putExtra("isMeetStarter", data.strMainUserID.equals(AppDatas.Auth().getUserLoginName()));
                            intent.putExtra("nMeetID", data.nMeetingID);
                            intent.putExtra("isWatch", data.isWatch());
                            intent.putExtra("mMediaMode", SdkBaseParams.MediaMode.AudioAndVideo);
                            intent.putExtra("strInviteUserId", data.strMainUserID);
                            intent.putExtra("strInviteUserDomainCode", data.strMainUserDomainCode);
                            startActivity(intent);

                        } else if (data.nStatus == 2) {
                            AppBaseActivity.showToast(getString(R.string.meet_has_end));
                        } else if (data.nStatus == 4) {
                            Intent intent = new Intent(getContext(), OrderMeetDetailActivity.class);
                            intent.putExtra("strMeetDomainCode", data.strMainUserDomainCode);
                            intent.putExtra("nMeetID", data.nMeetingID);
                            intent.putExtra("strMainUserDomainCode", data.strMainUserDomainCode);
                            intent.putExtra("strInviteUserId", data.strMainUserID);
                            startActivity(intent);
                        }
                    }
                }, true);
        adapter.setLoadListener(new LiteBaseAdapter.LoadListener() {
            @Override
            public boolean isLoadOver() {
                return !isLoad;
            }

            @Override
            public boolean isEnd() {
                return datas.size() < index * 20;
            }

            @Override
            public void lazyLoad() {
                refreshDatas(false);
            }
        });
        rct_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        rct_view.setAdapter(adapter);

        refresh_view.setColorSchemeColors(R.color.blue, R.color.colorPrimary);
        refresh_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDatas(true);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshDatas(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDatas(true);
    }

    void refreshDatas(final boolean isRef) {

        if (isRef) {
            index = 1;
        } else {
            index++;
        }

        ModelApis.Meet().requestCurrentMeets(index, et_key.getText().toString(), new ModelCallback<MeetList>() {
            @Override
            public void onPreStart(HTTPRequest httpRequest) {
                super.onPreStart(httpRequest);
                isLoad = true;
            }

            @Override
            public void onSuccess(MeetList meetList) {
                if (isRef) datas.clear();
                datas.addAll(meetList.lstMeetingInfo);
                adapter.notifyDataSetChanged();

                if (datas.size() < 1) {
                    iv_empty_view.setVisibility(View.VISIBLE);
                } else {
                    iv_empty_view.setVisibility(View.GONE);
                }

                isLoad = false;
            }

            @Override
            public void onFinish(HTTPResponse httpResponse) {
                super.onFinish(httpResponse);
                isLoad = false;
                refresh_view.setRefreshing(false);
            }
        });
    }

}
