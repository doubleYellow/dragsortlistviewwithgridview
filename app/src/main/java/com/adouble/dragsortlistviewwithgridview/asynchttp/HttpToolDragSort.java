package com.adouble.dragsortlistviewwithgridview.asynchttp;

import android.content.Context;

import com.adouble.dragsortlistviewwithgridview.activity.MainActivity;
import com.adouble.dragsortlistviewwithgridview.consts.ConstWorkType;
import com.adouble.dragsortlistviewwithgridview.model.WorkDragChildModel;
import com.adouble.dragsortlistviewwithgridview.model.WorkDragSortModel;
import com.adouble.dragsortlistviewwithgridview.sp.SPWokrDrag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gy on 2016/6/2.
 */
public class HttpToolDragSort {
    public static String listToJson(List<WorkDragSortModel> workDragSortModels) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray();
            for (int i = 0; i < workDragSortModels.size(); i++) {
                JSONObject jsObject = new JSONObject();
                JSONArray jsonArray1 = new JSONArray();
                WorkDragSortModel workDragSortModel = workDragSortModels.get(i);
                for (int j = 0; j < workDragSortModels.get(i).workChildren.size(); j++) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("nameid", workDragSortModel.workChildren.get(j).workNameId);
                    jsonObject1.put("ischeck", workDragSortModel.workChildren.get(j).workIsCheck);
                    jsonArray1.put(j, jsonObject1);
                }
                jsObject.put("type", i);
                jsObject.put("groupId", workDragSortModel.groupId);
                jsObject.put("groupName", workDragSortModel.groupName);
                jsObject.put("item", jsonArray1);
                jsonArray.put(i, jsObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    public static ArrayList<WorkDragSortModel> getWorkDratgModels(Context context ,String jsonString) {
        //6.4新增销售计划统计,所以需要判断sp只是否有销售计划
        boolean isSalePlanExits = false;
        int salemanage_pos = 0; //销售管理模块position，方便销售计划统计插入此模块
        final ArrayList<WorkDragSortModel> workDragSortModels = new ArrayList<WorkDragSortModel>();
        try {
            JSONArray jsonArray = null;
            jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                WorkDragSortModel workDragSortModel = new WorkDragSortModel();
                workDragSortModel.workChildren = new ArrayList<WorkDragChildModel>();
                int type = jsonArray.getJSONObject(i).optInt("type");
                int groupId = jsonArray.getJSONObject(i).optInt("groupId");
                String groupName = jsonArray.getJSONObject(i).optString("groupName");
                String item = jsonArray.getJSONObject(i).optString("item");
                JSONArray jsonArray1 = new JSONArray(item);
                workDragSortModel.groupId = groupId;
                workDragSortModel.groupName = groupName;
                if (groupId == ConstWorkType.SALE_MANAGE_NEW) {
                    salemanage_pos = i;
                }
                for (int j = 0; j < jsonArray1.length(); j++) {
                    WorkDragChildModel workDragChildModel = new WorkDragChildModel();
                    int nameid = jsonArray1.getJSONObject(j).optInt("nameid");
                    boolean ischeck = jsonArray1.getJSONObject(j).optBoolean("ischeck");
                    boolean hasPersssion = jsonArray1.getJSONObject(j).optBoolean("haspermission");
                    workDragChildModel.workIsCheck = ischeck;
                    workDragChildModel.workNameId = nameid;
                    workDragSortModel.workChildren.add(workDragChildModel);
                    if (nameid == ConstWorkType.WORK_SalePlanRank)
                        isSalePlanExits = true;
                }
                workDragSortModels.add(workDragSortModel);
            }
            if (!isSalePlanExits) {
                //sp存入销售计划
                workDragSortModels.get(salemanage_pos).workChildren.add(new WorkDragChildModel(false, true, ConstWorkType.WORK_SalePlanRank));
                SPWokrDrag.setWorkDrag(context, HttpToolDragSort.listToJson(workDragSortModels), MainActivity.uid);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return workDragSortModels;
    }
}
