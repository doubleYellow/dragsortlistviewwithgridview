package com.adouble.dragsortlistviewwithgridview.sp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hh on 2017/6/16.
 */

public class SPWokrDrag {

    public static void setWorkDrag(Context context, String value, int uuid) {
        // 取得SharedPreferences对象
        SharedPreferences getPrefer_String = context.getSharedPreferences(
                "workdrag", Context.MODE_PRIVATE);
        // 取得可编辑对象
        SharedPreferences.Editor editor_String = getPrefer_String.edit();
        editor_String.putString(getKeyName(uuid), value);
        // 保存提交
        editor_String.commit();
    }

    // 获取SharedPreferences值
    public static String getWorkDrag(Context context, int uuid) {
        // 获取值
        SharedPreferences sharPrefer = context.getSharedPreferences("workdrag",
                Context.MODE_PRIVATE);
        String value = sharPrefer.getString(getKeyName(uuid), "");
        return value;
    }

    public static String getKeyName(int uuid) {
        StringBuilder str = new StringBuilder();
        str.append("work_drag_second").append(uuid);
        return str.toString();
    }

}
