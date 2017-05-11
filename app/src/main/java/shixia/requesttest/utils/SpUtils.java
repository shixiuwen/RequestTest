package shixia.requesttest.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by ShiXiuwen on 2016/10/11.
 * <p>
 * Description:SharedPreferences工具类
 */

public class SpUtils {

    public static final String API_COUNT = "api_count";
    public static final String API_INDEX_ = "api_index_";

    public static final String API_SELECTED = "api_selected";

    public static final String PARAMS_COUNT = "params_count";
    public static final String PARAMS_KEY_VALUE_ = "params_key_value_";

    public static final String DEFAILT_REQUEST_TYPE = "default_request_type";
    public static final String AUTO_SAVE_TO_PLATE = "auto_save_to_plate";

    public static final String SP_KEY_USER_ID = "user_id";
    public static final String SP_KEY_TOKEN = "token";

    private SpUtils() {

    }

    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME = "share_data";

    private static SharedPreferences getSharedPreferences(Context context) {
        if (null == context) {
            return null;
        }
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getSharedPreferencesEditor(Context context) {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit();
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     */
    public static void put(Context context, String key, Object object) {

        SharedPreferences.Editor editor = getSharedPreferencesEditor(context);

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        editor.commit();
    }

    public static String getString(Context context, String key, String value) {
        return getSharedPreferences(context).getString(key, value);
    }

    public static int getInt(Context context, String key, int value) {
        return getSharedPreferences(context).getInt(key, value);
    }

    public static boolean getBoolean(Context context, String key, boolean value) {
        return getSharedPreferences(context).getBoolean(key, value);
    }

    public static float getFloat(Context context, String key, float value) {
        return getSharedPreferences(context).getFloat(key, value);
    }

    public static long getLong(Context context, String key, long value) {
        return getSharedPreferences(context).getLong(key, value);
    }

    /**
     * 保存服务器返回的user_id
     *
     * @param context
     * @param user_id
     */
    public static void putUserId(Context context, String user_id) {
        if (!TextUtils.isEmpty(user_id)) {
            put(context, SP_KEY_USER_ID, user_id);
        }
    }

    /**
     * 从本地获取user_id
     *
     * @param context
     * @return
     */
    public static String getUserId(Context context) {
        try {
            if (context == null) {
                return null;
            }
            String user_id = getString(context, SP_KEY_USER_ID, "");
            if (TextUtils.isEmpty(user_id)) {
                return null;
            }
            return user_id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getToken(Context context) {
        try {
            if (context == null) {
                return null;
            }
            String token = getString(context, SP_KEY_TOKEN, "");
            if (TextUtils.isEmpty(token)) {
                return null;
            }
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 是否已经登录
     *
     * @param context
     * @return
     */
    public static boolean isOnLogin(Context context) {
        return context != null && !(TextUtils.isEmpty(getString(context, SP_KEY_TOKEN, "")));
    }

    /**
     * 清除本地user_id
     *
     * @param context
     */
    public static void clearUserId(Context context) {
        put(context, SP_KEY_USER_ID, "");
    }

    /**
     * 清除本地Token
     *
     * @param context
     */
    public static void clearUserToken(Context context) {
        put(context, SP_KEY_TOKEN, "");
    }

    /**
     * 移除某个key值已经对应的值
     */
    public static void remove(Context context, String key) {
        getSharedPreferencesEditor(context).remove(key).commit();
    }

    /**
     * 清除所有数据
     */
    public static void clear(Context context) {
        getSharedPreferencesEditor(context).clear().commit();
    }

    /**
     * 查询某个key是否已经存在
     */
    public static boolean contains(Context context, String key) {
        return getSharedPreferences(context).contains(key);
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat {

        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }

    /**
     * desc:保存对象
     *
     * @param obj 要保存的对象，只能保存实现了serializable的对象 modified:
     */
    public static void saveObject(Context context, String key, Object obj) {
        try {
            // 保存对象
            SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
            //先将序列化结果写到byte缓存中，其实就分配一个内存空间
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            //将对象序列化写入byte缓存
            os.writeObject(obj);
            //将序列化的数据转为16进制保存
            String bytesToHexString = bytesToHexString(bos.toByteArray());
            //保存该16进制数组
            editor.putString(key, bytesToHexString);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * desc:将数组转为16进制
     *
     * @return modified:
     */
    private static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return null;
        }
        if (bArray.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * desc:获取保存的Object对象
     *
     * @return modified:
     */
    public static Object readObject(Context context, String key) {
        try {
            SharedPreferences sp = getSharedPreferences(context);
            if (sp.contains(key)) {
                String string = sp.getString(key, "");
                if (TextUtils.isEmpty(string)) {
                    return null;
                } else {
                    //将16进制的数据转为数组，准备反序列化
                    byte[] stringToBytes = StringToBytes(string);
                    ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
                    ObjectInputStream is = new ObjectInputStream(bis);
                    //返回反序列化得到的对象
                    Object readObject = is.readObject();
                    return readObject;
                }
            }
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //所有异常返回null
        return null;

    }

    /**
     * desc:将16进制的数据转为数组
     *
     * @return modified:
     */
    private static byte[] StringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i++) {
            int int_ch;  // 两位16进制数转化后的10进制数
            char hex_char1 = hexString.charAt(i); ////两位16进制数中的第一位(高位*16)
            int int_ch1;
            if (hex_char1 >= '0' && hex_char1 <= '9') {
                int_ch1 = (hex_char1 - 48) * 16;   //// 0 的Ascll - 48
            } else if (hex_char1 >= 'A' && hex_char1 <= 'F') {
                int_ch1 = (hex_char1 - 55) * 16; //// A 的Ascll - 65
            } else {
                return null;
            }
            i++;
            char hex_char2 = hexString.charAt(i); ///两位16进制数中的第二位(低位)
            int int_ch2;
            if (hex_char2 >= '0' && hex_char2 <= '9') {
                int_ch2 = (hex_char2 - 48); //// 0 的Ascll - 48
            } else if (hex_char2 >= 'A' && hex_char2 <= 'F') {
                int_ch2 = hex_char2 - 55; //// A 的Ascll - 65
            } else {
                return null;
            }
            int_ch = int_ch1 + int_ch2;
            retData[i / 2] = (byte) int_ch;//将转化后的数放入Byte里
        }
        return retData;
    }
}
