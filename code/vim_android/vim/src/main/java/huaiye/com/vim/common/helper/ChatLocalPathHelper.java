package huaiye.com.vim.common.helper;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class ChatLocalPathHelper {

    private static ChatLocalPathHelper INSTANCE = new ChatLocalPathHelper();


    private Map<String, String> chatCache = new HashMap<String, String>();

    /**
     * ChatLocalPathHelper ,单例模式
     */
    public static ChatLocalPathHelper getInstance() {
        return INSTANCE;
    }

    private ChatLocalPathHelper() {

    }

    public void cacheChatLoaclPath(String key, String value) {
        if (null != chatCache) {
            chatCache.clear();
        }
        chatCache.put(key, value);
    }

    public String getChatLoaclPath(String key) {
        if (!TextUtils.isEmpty(key) && null != chatCache && chatCache.containsKey(key)) {
            return chatCache.get(key);
        } else {
            return null;
        }
    }

    public void destoryChatLoaclPath() {
        chatCache.clear();
        chatCache = null;
    }

}
