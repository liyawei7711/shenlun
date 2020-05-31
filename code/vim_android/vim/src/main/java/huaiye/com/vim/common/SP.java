package huaiye.com.vim.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Author：liyawei
 * Time: 2016/10/24 14:29
 * Email：liyawei@haiye.com
 */
public class SP {
    /**
     * 保存在手机里面的文件名
     */
    private static final String FILE_NAME = "share_date";
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    public static void init(Context context) {
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public static void setParam(String key, Object object) {
        String type = object.getClass().getSimpleName();
        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        }

        editor.commit();
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object getParam(String key, Object defaultObject) {
        String type = defaultObject.getClass().getSimpleName();
        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    public static int getParamsInt(String key, int defaultObject) {
        return sp.getInt(key, defaultObject);
    }

    public static Long getLong(String key, Long defaultObject) {
        return sp.getLong(key, (Long) defaultObject);
    }

    public static void putInt(String key, int defaultObject) {
        editor.putInt(key, defaultObject);
        editor.commit();
    }


    public static void putLong(String key, long i) {
        editor.putLong(key, i);
        editor.commit();
    }

    /**
     * 清空sp数据
     * 可以在这边处理一些必要的数据重新存储
     */
    public static void clear() {
        /**
         * 需要持续保存的数据特殊处理
         */
        sp.edit().clear().commit();
    }

    public static int getInteger(String key, int def) {
        return sp.getInt(key, def);
    }

    public static int getInteger(String key) {
        return sp.getInt(key, -1);
    }

    public static boolean getBoolean(String key, boolean def) {
        return sp.getBoolean(key, def);
    }

    public static void putBoolean(String key, Boolean object) {
        editor.putBoolean(key, object);

        editor.commit();
    }
    public static void putString(String key, String object) {
        editor.putString(key, object);

        editor.commit();
    }

    public static void putInteger(String key, int object) {
        editor.putInt(key, object);

        editor.commit();
    }

    public static String getString(String key) {
        return sp.getString(key, "");
    }

    public static String getString(String key,String def) {
        return sp.getString(key, def);
    }
}
