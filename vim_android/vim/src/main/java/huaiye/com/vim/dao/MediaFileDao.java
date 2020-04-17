package huaiye.com.vim.dao;

import com.huaiye.cmf.JniIntf;
import com.huaiye.sdk.HYClient;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.SP;
import ttyy.com.datasdao.Datas;
import ttyy.com.datasdao.annos.Column;

import static huaiye.com.vim.common.AppUtils.CAPTURE_TYPE;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_false;
import static huaiye.com.vim.dao.AppDatas.DBNAME;


/**
 * author: admin
 * date: 2017/09/15
 * version: 0
 * mail: secret
 * desc: LocalMediaDao
 */
public class MediaFileDao {
    private static final SimpleDateFormat sdf;

    static {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }


    File mVideoDir;
    File mImageDir;

    private MediaFileDao() {
        mVideoDir = HYClient.getContext().getExternalFilesDir("videos");
        mImageDir = HYClient.getContext().getExternalFilesDir("images");

        mVideoDir.mkdirs();
        mImageDir.mkdirs();
    }

    static class Holder {
        static final MediaFileDao INSTANCE = new MediaFileDao();
    }

    public static MediaFileDao get() {
        return Holder.INSTANCE;
    }

    public List<MediaFile> getAllVideos() {
        List<MediaFile> list = Datas.from(DBNAME)
                .findQuery(MediaFile.class)
                .addWhereColumn("nMediaType", 1)
                .selectAll();
        return list;
    }

    public List<MediaFile> getAllImgs() {
        List<MediaFile> list = Datas.from(DBNAME)
                .findQuery(MediaFile.class)
                .addWhereColumn("nMediaType", 0)
                .selectAll();
        return list;
    }

    public void del(MediaFile... datas) {
        if (datas == null
                || datas.length < 1) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("key in ( ");
        for (int i = 0; i < datas.length; i++) {
            if (i == datas.length - 1) {
                sb.append(datas[i].key).append(" )");
            } else {
                sb.append(datas[i].key).append(", ");
            }

            datas[i].del();
        }

        Datas.from(DBNAME)
                .deleteQuery(MediaFile.class)
                .where(sb.toString())
                .delete();
    }

    public MediaFile getVideoRecordFile() {
        MediaFile data = new MediaFile();
        data.nMediaType = 1;

        boolean captureType = Boolean.parseBoolean(SP.getParam(CAPTURE_TYPE, STRING_KEY_false).toString());
        if (captureType) {
            data.strRecordFilePath = new File(mVideoDir, data.getRecordName()).getPath();

            Datas.from(DBNAME)
                    .insertQuery(MediaFile.class)
                    .insert(data);
        }

        return data;
    }

    public String getImgRecordFile() {
        MediaFile data = new MediaFile();
        data.nMediaType = 0;
        data.strRecordFilePath = new File(mImageDir, data.getRecordName()).getPath();

        Datas.from(DBNAME)
                .insertQuery(MediaFile.class)
                .insert(data);

        return data.getRecordPath();
    }

    public static class MediaFile {
        @Column
        private long key;
        @Column
        public long nRecordStartTimeMillions;// 开始时间的Millions
        @Column
        private long nRecordEndTimeMillions;// 当前时间的Millions1
        @Column
        private String strRecordFilePath;// 文件路径
        @Column
        private int nMediaType;// 0:图片 1:视频

        MediaFile() {
            key = System.currentTimeMillis();
            nRecordStartTimeMillions = key;
        }

        public void recordEnd() {

            nRecordEndTimeMillions = System.currentTimeMillis();
            if (strRecordFilePath != null) {
                Datas.from(DBNAME)
                        .updateQuery(MediaFile.class)
                        .addWhereColumn("nRecordStartTimeMillions", nRecordStartTimeMillions)
                        .addUpdateColumn("nRecordEndTimeMillions", nRecordEndTimeMillions)
                        .update();
            }

        }

        public String getRecordPath() {

            return strRecordFilePath;
        }

        public String getRecordName() {
            StringBuilder fileName = new StringBuilder();
            fileName.append(getDateDetail().replace(" ", "_").replaceAll(":", "-"))
                    .append("_android");
            if (nMediaType == 0) {
                // 图片
                // yyyy-MM-dd_HH:mm:ss_android_image.jpg
                fileName.append("_image.jpg");
            } else {
                // 视频
                // yyyy-MM-dd_HH:mm:ss_android_video.dat
                fileName.append("_video.dat");
            }
            return fileName.toString();
        }

        private String getTimeLength() {
            long diff = nRecordEndTimeMillions - nRecordStartTimeMillions;
            int seconds = (int) (diff / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int hour = minutes / 60;
            minutes = minutes % 60;

            StringBuilder sb = new StringBuilder();
            if (hour > 0) {
                sb.append(hour).append(AppUtils.getString(R.string.hour));
            }

            if (minutes > 0) {
                sb.append(minutes).append(AppUtils.getString(R.string.minute));
            }

            if (seconds > 0) {
                sb.append(seconds).append(AppUtils.getString(R.string.second));
            }

            return sb.toString();
        }

        private String getTimeLength(int seconds) {
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int hour = minutes / 60;
            minutes = minutes % 60;

            StringBuilder sb = new StringBuilder();
            if (hour > 0) {
                sb.append(hour).append(AppUtils.getString(R.string.hour));
            }

            if (minutes > 0) {
                sb.append(minutes).append(AppUtils.getString(R.string.minute));
            }

            if (seconds > 0) {
                sb.append(seconds).append(AppUtils.getString(R.string.second));
            }

            return sb.toString();
        }

        public String getRecordNameUI() {
            StringBuilder fileName = new StringBuilder();
            fileName
//                    .append("日期: ")
                    .append(getDateDetail())
                    .append(AppUtils.getString(R.string.time_long))
                    .append(getTimeLength(JniIntf.GetRecordFileDuration(strRecordFilePath)));//getTimeLength());
            return fileName.toString();
        }

        public String getDateSimple() {

            return sdf.format(new Date(nRecordStartTimeMillions)).split(" ")[0];
        }

        public String getDateDetail() {

            return sdf.format(new Date(nRecordStartTimeMillions));
        }

        protected void del() {
            File file = new File(strRecordFilePath);
            if (file.exists())
                file.delete();

            int i = 1;
            while (true) {
                String path = strRecordFilePath + i;
                file = new File(path);
                if (file.exists()) {
                    file.delete();
                } else {
                    break;
                }
            }
        }
    }
}
