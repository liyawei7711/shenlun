package huaiye.com.vim.common.utils;

import android.content.Context;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import huaiye.com.vim.dao.msgs.ContentBean;

/**
 * @author zhangzhen
 * 20190819
 */
public class ChatUtil {


    /**
     * @param context
     * @param msgTxt     文本聊天数据
     * @param strFileUrl 文件地址
     * @param nDuration  语音\视频等时长
     * @param nFileSize  文件大小
     * @param bFire      阅后即焚:0未开启,1开启
     * @param nCallState 语音或者视频聊天状态 0: 取消，1：拒绝，2：接通
     * @return String
     */
    public static String getChatContentJson(Context context, String msgTxt, String summary, String strFileUrl, long nDuration, long nFileSize, boolean bFire, int fireTime, int nCallState, double latitude, double longitude, String fileName) {

        JsonObject json = new JsonObject();

        json.addProperty("msgID", UUID.randomUUID().toString());
        json.addProperty("msgTxt", msgTxt);
        json.addProperty("fileUrl", strFileUrl);
        json.addProperty("nDuration", nDuration);
        json.addProperty("fileSize", nFileSize);
        json.addProperty("summary", summary);
        json.addProperty("fileName", fileName);
        json.addProperty("bFire", bFire ? 1 : 0);
        json.addProperty("nCallState", nCallState);
        json.addProperty("fireTime", fireTime);
        json.addProperty("latitude", latitude);
        json.addProperty("longitude", longitude);

        return json.toString();
    }

    public static ContentBean analysisChatContentJson(String content) {
        ContentBean nContentBean = new ContentBean();
        boolean analysisException = false;
        try {
            JSONObject json = new JSONObject(content);
            nContentBean.msgID = json.optString("msgID");
            nContentBean.msgTxt = json.optString("msgTxt");
            nContentBean.summary = json.optString("summary");
            nContentBean.fileUrl = json.optString("fileUrl");
            nContentBean.nDuration = json.optInt("nDuration");
            nContentBean.fileSize = json.optInt("fileSize");
            nContentBean.fileName = json.optString("fileName");
            nContentBean.bFire = json.optInt("bFire");
            nContentBean.nCallState = json.optInt("nCallState");
            nContentBean.fireTime = json.optInt("fireTime");

            nContentBean.strUserID = json.optString("strUserID");
            nContentBean.strDomainCode = json.optString("strDomainCode");
            nContentBean.strUserName = json.optString("strUserName");

            if(json.has("latitude")) {
                nContentBean.latitude = json.optDouble("latitude", 0);
            } else {
                nContentBean.latitude = json.optDouble("fLatitude", 0);
            }

            if(json.has("longitude")) {
                nContentBean.longitude = json.optDouble("longitude", 0);
            } else {
                nContentBean.longitude = json.optDouble("fLongitude", 0);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            analysisException = true;
        }
        if (analysisException) {
            nContentBean.msgTxt = content;
        }
        return nContentBean;
    }

}
