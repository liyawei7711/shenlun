package huaiye.com.vim.common;

import android.content.Context;
import android.os.storage.StorageManager;
import android.util.Log;

import java.lang.reflect.Method;

import static android.content.Context.STORAGE_SERVICE;

public class SDCardUtils {
    // 获取主存储卡路径
    public String getPrimaryStoragePath(Context context) {
        try {
            StorageManager sm = (StorageManager) context.getSystemService(STORAGE_SERVICE);
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", (Class<?>[]) null);
            String[] paths = (String[]) getVolumePathsMethod.invoke(sm, (Object[]) null);
            // first element in paths[] is primary storage path
            return paths[0];
        } catch (Exception e) {
        }
        return null;
    }

    // 获取次存储卡路径,一般就是外置 TF 卡了. 不过也有可能是 USB OTG 设备...
    // 其实只要判断第二章卡在挂载状态,就可以用了.
    public String getSecondaryStoragePath(Context context) {
        try {
            StorageManager sm = (StorageManager) context.getSystemService(STORAGE_SERVICE);
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", (Class<?>[]) null);
            String[] paths = (String[]) getVolumePathsMethod.invoke(sm, (Object[]) null);
            // second element in paths[] is secondary storage path
            return paths.length <= 1 ? null : paths[1];
        } catch (Exception e) {
        }
        return null;
    }

    // 获取存储卡的挂载状态. path 参数传入上两个方法得到的路径
    public String getStorageState(Context context, String path) {
        try {
            StorageManager sm = (StorageManager) context.getSystemService(STORAGE_SERVICE);
            Method getVolumeStateMethod = StorageManager.class.getMethod("getVolumeState", new Class[] {String.class});
            String state = (String) getVolumeStateMethod.invoke(sm, path);
            return state;
        } catch (Exception e) {
        }
        return null;
    }
}
