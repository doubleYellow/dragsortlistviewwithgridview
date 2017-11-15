package com.adouble.dragsortlistviewwithgridview.model;

/**
 * Created by hh on 2016/5/28.
 */
public class WorkDragChildModel {
    public WorkDragChildModel() {
    }

    public WorkDragChildModel(boolean workIsCheck, int workNameId) {
        this.workIsCheck = workIsCheck;
        this.workNameId = workNameId;
    }

    public WorkDragChildModel(boolean workPermission, boolean workIsCheck, int workNameId) {
        this.workIsCheck = workIsCheck;
        this.workNameId = workNameId;
    }

    public int workNameId;//相应功能id
    public boolean workIsCheck;//是否被选中,即是否可见
}
