package huaiye.com.vim.ui.sendBaiduLocation.function.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import java.util.Iterator;
import java.util.List;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.ui.sendBaiduLocation.function.adapter.MapSearchAdapter;
import huaiye.com.vim.ui.sendBaiduLocation.util.AppStaticVariable;

/**
 * 聊天地图查询
 * Created by xz on 2016/10/18 0018.
 * @author xz
 */
@BindLayout(R.layout.activity_map_search)
public class MapSearchActivity extends AppBaseActivity {

    @BindView(R.id.ams_rv)
    RecyclerView mRecyclerView;

    @BindView(R.id.ams_et)
    EditText mEditText;

    BDLocation mLocation;

    public SuggestionSearch mSuggestionSearch;

    private MapSearchAdapter mMapSearchAdapter;


    @Override
    protected void initActionBar() {

        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void doInitDelay() {
        initData();
        initView();
        initSearch();
        InputMethodManager inputManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(mEditText, 0);
    }

    private void initData() {
        if(null!=getIntent()){
            mLocation =getIntent().getParcelableExtra("mLocation");

        }
    }

    /**
     * 搜索
     */
    public void initSearch() {
        //关键词搜索
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                //未找到相关结果
                if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                    return;
                }
                List<SuggestionResult.SuggestionInfo> ssList = suggestionResult.getAllSuggestions();

                //关键搜索时，数据有时候没有经纬度，和地址信息,需要剔除
                Iterator<SuggestionResult.SuggestionInfo> itParent = ssList.iterator();
                while (itParent.hasNext()) {
                    SuggestionResult.SuggestionInfo ss = itParent.next();
                    if (ss.pt == null || TextUtils.isEmpty(ss.district)) {
                        itParent.remove();
                    }
                }
                mMapSearchAdapter.setDatas(ssList,true);
            }
        });
    }

    /**
     * 对搜索框进行监听
     */
    public void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mMapSearchAdapter = new MapSearchAdapter(this);
        mMapSearchAdapter.setOnItemClickListener(new MapSearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                SuggestionResult.SuggestionInfo ss = (SuggestionResult.SuggestionInfo) mMapSearchAdapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra(AppStaticVariable.MAP_SEARCH_LONGITUDE, ss.pt.longitude);
                intent.putExtra(AppStaticVariable.MAP_SEARCH_LATITUDE, ss.pt.latitude);
                intent.putExtra(AppStaticVariable.MAP_SEARCH_ADDRESS, ss.city + ss.district + ss.key);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        mRecyclerView.setAdapter(mMapSearchAdapter);


        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 这里的s表示改变之前的内容，通常start和count组合，可以在s中读取本次改变字段中被改变的内容。而after表示改变后新的内容的数量。
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 这里的s表示改变之后的内容，通常start和count组合，可以在s中读取本次改变字段中新的内容。而before表示被改变的内容的数量。
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 表示最终内容
                String mapInput = mEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(mapInput)) {
                    //搜索关键词
                    if(null!=mLocation&&!TextUtils.isEmpty(mLocation.getCity())){
                        mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                                .keyword(mapInput).city(mLocation.getCity()));
                    }else{
                        mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                                .keyword(mapInput).city(AppUtils.getString(R.string.city_beijing)));
                    }

                }
            }
        };
        mEditText.addTextChangedListener(tw);


    }

    @OnClick({R.id.chat_title_bar_title,R.id.ams_back})
    void back(){
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放POI检索实例；
        if (mSuggestionSearch != null) {
            mSuggestionSearch.destroy();
        }
    }
}
