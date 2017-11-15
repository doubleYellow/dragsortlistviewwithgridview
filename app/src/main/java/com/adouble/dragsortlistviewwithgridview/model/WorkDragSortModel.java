package com.adouble.dragsortlistviewwithgridview.model;

import java.util.List;

/**
 * Created by gy on 2016/5/26.
 */
public class WorkDragSortModel {
    public WorkDragSortModel() {
    }

    public WorkDragSortModel(int groupId, String groupName, List<WorkDragChildModel> workChildren) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.workChildren = workChildren;
    }

    public int groupId;//大module的id
    public String groupName;//大module的名称
    public List<WorkDragChildModel> workChildren;
}
