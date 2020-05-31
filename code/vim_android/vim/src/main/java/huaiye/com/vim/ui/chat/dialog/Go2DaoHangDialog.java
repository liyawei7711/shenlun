package huaiye.com.vim.ui.chat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import huaiye.com.vim.R;
import huaiye.com.vim.VIMApp;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.dao.msgs.ChatMessageBase;
import huaiye.com.vim.map.GPS;
import huaiye.com.vim.map.GPSConverterUtils;
import huaiye.com.vim.models.map.bean.DaoHangAppInfo;

/**
 * author: zhangzhen
 * date: 2019/07/27
 * version: 0
 * mail: secret
 * desc: ChatSendLocationDialog
 */

public class Go2DaoHangDialog extends Dialog {
    
    private RecyclerView mRecyclerView;
    private DaohangAppItemAdapter mDaohangAppItemAdapter;
    private ChatMessageBase chatMessageBase;
    private GPS gps84;
    private GPS gcj02;
    private GPS bd09;


    public Go2DaoHangDialog(@NonNull Context context) {
        super(context);

        setCancelable(true);
        setCanceledOnTouchOutside(true);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable());
        getWindow().setGravity(Gravity.CENTER);
        setContentView(R.layout.dialog_chat_go_2_daohang);
        init();
    }

    private void init() {
        mRecyclerView = findViewById(R.id.daohang_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mDaohangAppItemAdapter = new DaohangAppItemAdapter(getContext());
        mDaohangAppItemAdapter.setOnItemClickListener(new DaohangAppItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                DaoHangAppInfo daoHangAppInfo = (DaoHangAppInfo) mDaohangAppItemAdapter.getItem(position);
                switch (daoHangAppInfo.packageName){
                    case "com.baidu.BaiduMap"://百度地图
                        if(mDaohangAppItemAdapter.isDaoHang()){
                            openBaiduNavi();
                        }else{
                            installBaiduNavi();
                        }
                        break;
                    case "com.autonavi.minimap"://高德地图
                        if(mDaohangAppItemAdapter.isDaoHang()){
                            openGaoDeNavi();
                        }else{
                            installGaoDeNavi();
                        }
                        break;
                    case "com.tencent.map"://腾讯地图
                        if(mDaohangAppItemAdapter.isDaoHang()){
                            goToTencentMap();

                        }else{
                            installTencentNavi();

                        }
                        break;
                    case "com.google.android.apps.maps"://google地图
                        if(mDaohangAppItemAdapter.isDaoHang()){
                            openGoogleNavi();
                        }else{
                            installGoogleNavi();
                        }
                        break;
                }
                Intent intent = new Intent();
                dismiss();
            }
        });
        mRecyclerView.setAdapter(mDaohangAppItemAdapter);
    }

    public void setData(){
        if(null!= VIMApp.getInstance().daoHangAppInfoList&&VIMApp.getInstance().daoHangAppInfoList.size()>0){
            mDaohangAppItemAdapter.setDaoHangFlag(true);
            mDaohangAppItemAdapter.setDatas(VIMApp.getInstance().daoHangAppInfoList);

        }else{
            mDaohangAppItemAdapter.setDaoHangFlag(false);
            mDaohangAppItemAdapter.setDatas(VIMApp.getInstance().alldaoHangAppInfoList);
        }
    }

    private void installGoogleNavi() {
        AppBaseActivity.showToast(AppUtils.getString(R.string.string_name_daohang_google_uninstall));
        Uri uri = Uri.parse("market://details?id=com.google.android.apps.maps");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        getContext().startActivity(intent);
    }

    private void installTencentNavi() {
        AppBaseActivity.showToast(AppUtils.getString(R.string.string_name_daohang_tengxun_uninstall));
        Uri uri = Uri.parse("market://details?id=com.tencent.map");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        getContext().startActivity(intent);
    }

    private void installGaoDeNavi() {
        AppBaseActivity.showToast(AppUtils.getString(R.string.string_name_daohang_gaode_uninstall));
        Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        getContext().startActivity(intent);
    }

    private void installBaiduNavi() {
        AppBaseActivity.showToast(AppUtils.getString(R.string.string_name_daohang_baidu_uninstall));
        Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        getContext().startActivity(intent);
    }

    public Go2DaoHangDialog setLocationInfo(ChatMessageBase chatMessageBase){
        setData();
        this.chatMessageBase=chatMessageBase;
        gps84 = new GPS(chatMessageBase.latitude,chatMessageBase.longitude);
        gcj02= GPSConverterUtils.gps84_To_Gcj02(gps84.getLat(),gps84.getLon());
        bd09=GPSConverterUtils.gcj02_To_Bd09(gcj02.getLat(),gcj02.getLon());
        return this;
    }



    /**
     * 打开百度地图导航客户端
     * intent = Intent.getIntent("baidumap://map/navi?location=34.264642646862,108.95108518068&type=BLK&src=thirdapp.navi.you
     * location 坐标点 location与query二者必须有一个，当有location时，忽略query
     * query    搜索key   同上
     * type 路线规划类型  BLK:躲避拥堵(自驾);TIME:最短时间(自驾);DIS:最短路程(自驾);FEE:少走高速(自驾);默认DIS
     */
    private void openBaiduNavi() {
        StringBuffer stringBuffer = new StringBuffer("baidumap://map/navi?location=")
                .append(bd09.getLat()).append(",").append(bd09.getLon()).append("&type=TIME");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuffer.toString()));
        intent.setPackage("com.baidu.BaiduMap");
        getContext().startActivity(intent);
    }

    /**
     * 启动高德App进行导航
     * sourceApplication 必填 第三方调用应用名称。如 amap
     * poiname           非必填 POI 名称
     * dev               必填 是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
     * style             必填 导航方式(0 速度快; 1 费用少; 2 路程短; 3 不走高速；4 躲避拥堵；5 不走高速且避免收费；6 不走高速且躲避拥堵；7 躲避收费和拥堵；8 不走高速躲避收费和拥堵))
     */
    private void openGaoDeNavi() {
        StringBuffer stringBuffer = new StringBuffer("androidamap://navi?sourceApplication=")
                .append("yitu8_driver").append("&lat=").append(gcj02.getLat())
                .append("&lon=").append(gcj02.getLon())
                .append("&dev=").append(1)
                .append("&style=").append(0);
        ;
//        if (!TextUtils.isEmpty(poiname)) {
//            stringBuffer.append("&poiname=").append(poiname);
//        }
        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(stringBuffer.toString()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setPackage("com.autonavi.minimap");
        getContext().startActivity(intent);
    }


    /**
     * 跳转腾讯地图
     */
    private void goToTencentMap() {

        StringBuffer stringBuffer = new StringBuffer("qqmap://map/routeplan?type=drive")
                .append("&tocoord=").append(gcj02.getLat()).append(",").append(gcj02.getLon()).append("&to=" + chatMessageBase.msgTxt);
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(stringBuffer.toString()));
        getContext().startActivity(intent);
    }


    /**
     * 打开google地图客户端开始导航
     * q:目的地
     * mode：d驾车 默认
     */
    private void openGoogleNavi() {
        StringBuffer stringBuffer = new StringBuffer("google.navigation:q=").append(gps84.getLat()).append(",").append(gps84.getLon()).append("&mode=d");
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuffer.toString()));
        i.setPackage("com.google.android.apps.maps");
        getContext().startActivity(i);
    }

    /**
     * 打开google Web地图导航
     */
    private void openWebGoogleNavi() {
        StringBuffer stringBuffer = new StringBuffer("http://ditu.google.cn/maps?hl=zh&mrt=loc&q=").append(gps84.getLat()).append(",").append(gps84.getLon());
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuffer.toString()));
        getContext().startActivity(i);
    }


    

}
