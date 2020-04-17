package huaiye.com.vim.ui.home.transfile;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huaiye.cmf.sdp.SdpMessageCmProcessIMReq;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import java.io.File;
import java.util.ArrayList;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.recycle.LiteBaseAdapter;
import huaiye.com.vim.models.meet.bean.FileBean;
import huaiye.com.vim.ui.meet.viewholder.FileHolder;

/**
 * author: admin
 * date: 2018/04/23
 * version: 0
 * mail: secret
 * desc: ChoosePhotoActivity
 */

@BindLayout(R.layout.activity_choose_photo)
public class ChooseEncryptFileActivity extends AppBaseActivity {

    @BindView(R.id.rv_data)
    RecyclerView rv_data;

    LiteBaseAdapter<FileBean> adapter;

    ArrayList<FileBean> arrays = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;

    File fC;

    @Override
    protected void initActionBar() {
        fC = new File(getExternalFilesDir(null) + File.separator + "Vim/chat/");
        ArrayList<SdpMessageCmProcessIMReq.UserInfo> users = new ArrayList<>();


        getNavigate().setLeftClickListener(v -> {
            onBackPressed();
        }).setTitlText("传输文件").setRightText("确定").setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<FileBean> temps = new ArrayList<>();
                for (FileBean temp : arrays) {
                    if (temp.isChecked) {
                        temps.add(temp);
                    }
                }
                if (temps.isEmpty()) {
                    finish();
                } else {
                    doChuanShu(temps);
                }
            }

        });
    }

    private void doChuanShu(ArrayList<FileBean> allDatas) {

        mZeusLoadView.loadingText("正在上传").setLoading();
        for (FileBean temp : allDatas) {

        }
        mZeusLoadView.dismiss();
        finish();
    }

    @Override
    public void doInitDelay() {

        adapter = new LiteBaseAdapter<>(this,
                arrays,
                FileHolder.class,
                R.layout.item_file,
                v -> {
                    FileBean bean = (FileBean) v.getTag();
                    if (bean.isFile) {
                        bean.isChecked = !bean.isChecked;
                        adapter.notifyDataSetChanged();
                    } else {
                    }
                }, null);
        rv_data.setLayoutManager(linearLayoutManager = new LinearLayoutManager(this));
        rv_data.setAdapter(adapter);

        getFiles();
    }


    /**
     * 加载文档
     */
    public void getFiles() {
        arrays.clear();
        File[] files = fC.listFiles();
        if (files == null) {
            files = new File[0];
        }
        for (File f : files) {
//                if (f.canRead() && !f.isHidden() && f.canWrite() && f.length() > 0) {//可读且不是隐藏文件
            if (!f.isHidden() && f.isFile()) {//可读且不是隐藏文件
                FileBean bean = new FileBean();
                bean.name = f.getName();
                bean.showName = f.getName();
                bean.first = AppUtils.getFirstSpell(f.getName()).substring(0, 1);
                bean.path = f.getPath();
//                bean.end = f.getName().substring(f.getName().lastIndexOf("."));
                bean.parent = f.getParent();
                bean.isFile = f.isFile();
                arrays.add(bean);
            }

        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
