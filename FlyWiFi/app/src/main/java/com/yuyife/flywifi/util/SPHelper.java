package com.yuyife.flywifi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yuyife
 *         键值对存储
 */
public class SPHelper {
    private static final String appSPKey = "fly_wifi";
    private static final String SSID_LIST_FILE_NAME = "ssid_list_file";//单独的文件名 存储SSID,密码 json
    private static final String SSID_JSON_KEY = "ssid_json_key";
    private static final String TAG = "SPHelper";

    public static final String CURRENT_SSID_KEY = "current_ssid_key";

    public static List<String> ssidList = new ArrayList<>();



    public static void setString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(appSPKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
        editor.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(appSPKey, Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    public static void setMapString(Context context, Map<String, String> map) {
        //以JSON串的形式保存
        SharedPreferences sp = context.getSharedPreferences(SSID_LIST_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String gsonString = new Gson().toJson(map);

        editor.putString(SSID_JSON_KEY, gsonString);
        editor.apply();
        editor.commit();

    }

    public static String getMapString(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SSID_LIST_FILE_NAME, Context.MODE_PRIVATE);

        return sp.getString(SSID_JSON_KEY, null);
    }

    /**
     * Json 转成 Map<>
     *
     * @param jsonStr
     * @return Map<String, Object>
     */
    public static Map<String, String> getMapForJson(String jsonStr) {
        ssidList.clear();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonStr);

            Iterator<String> keyIterator = jsonObject.keys();
            String key;
            String value;
            Map<String, String> valueMap = new HashMap<>();
            while (keyIterator.hasNext()) {
                key = keyIterator.next();
                value = (String) jsonObject.get(key);
                valueMap.put(key, value);

                ssidList.add(key);
            }
            return valueMap;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Json 转成 Map<> 异常 :" + e.toString());
        }
        return null;
    }

    /**
     * Json 转成 List<Map<>>
     *
     * @param jsonStr
     * @return List<Map<String, Object>>
     */
    public static List<Map<String, String>> getlistForJson(String jsonStr) {
        List<Map<String, String>> list = null;
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            JSONObject jsonObj;
            list = new ArrayList<Map<String, String>>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj = (JSONObject) jsonArray.get(i);
                list.add(getMapForJson(jsonObj.toString()));
            }
        } catch (Exception e) {
            Log.e(TAG, "Json 转成 List<Map<>> 异常 :" + e.toString());
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 增加一组 ssid及其密码  并更新到本地
     *
     * @param // Context context,String key,String value
     * @return boolean
     */
    public static boolean saveMapStringForAdd(Context context, String key, String value) {
        Map<String, String> map;
        String mapString = getMapString(context);

        if (isHasMapString(context)) {
            map = getMapForJson(mapString);
            if (map != null) {
                map.put(key, value);

                setMapString(context, map);
                map = getMapForJson(getMapString(context));
                return true;
            } else {

            }
        } else {
            map = new HashMap<>();
            map.put(key, value);

            setMapString(context, map);
            map = getMapForJson(getMapString(context));
            return true;
        }
        return false;
    }

    /**
     * 删除一组 ssid及其密码  并更新到本地
     *
     * @param // Context context,String key,String value
     * @return boolean
     */
    public static boolean saveMapStringForRemove(Context context, String key) {
        Map<String, String> map;
        String mapString = getMapString(context);

        if (isHasMapString(context)){
            map = getMapForJson(mapString);
            if (map != null) {
                if (ssidList.size()>0) {
                    for (int i = 0; i < ssidList.size(); i++) {
                        if (ssidList.get(i).equals(key)){
                            map.remove(key);
                        }else {

                        }
                    }
                }else {

                }

                setMapString(context, map);
                map = getMapForJson(getMapString(context));
                return true;
            } else {

            }
        }else {

        }
        return false;
    }

    /**
     * 判断本地是否存在 MapString
     *
     * @param // Context context
     * @return boolean
     */
    public static boolean isHasMapString(Context context) {
        String mapString = getMapString(context);
        if (mapString == null || TextUtils.isEmpty(mapString)) {
            return false;
        }
        return true;

    }
}
