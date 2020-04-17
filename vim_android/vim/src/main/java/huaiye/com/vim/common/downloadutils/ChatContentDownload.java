package huaiye.com.vim.common.downloadutils;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ChatContentDownload {

    /**
     * 下载
     *
     * @param urlLoadPath
     * @param fileName
     * @param type
     * @return
     */
    public static boolean downloadFileByUrl(final String urlLoadPath, final String fileName, int type) {
        Log.i("MCApp_tt", "urlLoadPath: " + urlLoadPath + "  fileName:" + fileName);
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        HttpURLConnection httpURLConnection = null;

        //创建 这个文件名 命名的 file 对象
        File file = new File(fileName);
        // Log.i(TAG,"file: " + file);
        if (!file.exists()) {     //倘若没有这个文件
            // Log.i(TAG,"创建文件");
            //file.createNewFile();  //创建这个文件
        } else {
            //文件已存在，不重新下载
            return true;
        }
        try {

            String nFileName = urlLoadPath.substring(urlLoadPath.lastIndexOf("/") + 1);
            String urlHost = urlLoadPath.substring(0, urlLoadPath.lastIndexOf("/") + 1);
            URL url = new URL(urlHost + URLEncoder.encode(nFileName, "utf-8"));
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
            httpURLConnection.setRequestProperty("Accept-Language", "zh-CN");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5 * 1000);
            httpURLConnection.connect();
            int code = httpURLConnection.getResponseCode();
            if (code == 200) {
                //网络连接成功
                //根据响应获取文件大小
                int fileSize = httpURLConnection.getContentLength();
                // Log.i(TAG,"文件大小： " + fileSize);
                inputStream = httpURLConnection.getInputStream();
                fileOutputStream = new FileOutputStream(file);
                byte[] b = new byte[1024];
                int tem = 0;
                while ((tem = inputStream.read(b)) != -1) {
                    fileOutputStream.write(b, 0, tem);
                }

            } else {
                return false;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}
