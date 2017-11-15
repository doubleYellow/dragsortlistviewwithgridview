package com.adouble.dragsortlistviewwithgridview.activity;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adouble.dragsortlistviewwithgridview.R;
import com.adouble.dragsortlistviewwithgridview.adapter.WorkDragSortAdapter;
import com.adouble.dragsortlistviewwithgridview.asynchttp.HttpToolDragSort;
import com.adouble.dragsortlistviewwithgridview.consts.ConstWorkType;
import com.adouble.dragsortlistviewwithgridview.control.dragsort.DragSortListView;
import com.adouble.dragsortlistviewwithgridview.model.WorkDragChildModel;
import com.adouble.dragsortlistviewwithgridview.model.WorkDragSortModel;
import com.adouble.dragsortlistviewwithgridview.sp.SPWokrDrag;
import com.adouble.dragsortlistviewwithgridview.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout mRlToolSet;
    private TextView mTvToolSet;
    private TextView mTvCancel;//取消设置排序
    private ImageView mIvToolSet;
    private DragSortListView mLvDragSort;
    private WorkDragSortAdapter mDsAdapter;

    private List<WorkDragSortModel> mToolList = new ArrayList<>();//工具列表数据

    public final static int LIST_SIZE = 3;//工具列表分三块
    private final static int TOOLS_SIZE = 14;//工具列表一共14项
    public static int uid = 123456;//用户id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initView() {
        mRlToolSet = (RelativeLayout) findViewById(R.id.rl_tool_set);
        mTvToolSet = (TextView) findViewById(R.id.tv_tool_set);
        mIvToolSet = (ImageView) findViewById(R.id.iv_tool_set);
        mTvCancel = (TextView) findViewById(R.id.title_cancel);
        mLvDragSort = (DragSortListView) findViewById(R.id.lv_dragSort);

        mRlToolSet.setOnClickListener(new DispatchRequest());

        View footView = new View(this);
        footView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this, 20)));
        footView.setBackgroundColor(getResources().getColor(R.color.huise_5));
        mLvDragSort.addFooterView(footView);
        initDragSort();
    }

    private void initData() {
        String workDrag = SPWokrDrag.getWorkDrag(this, uid);
        if (!TextUtils.isEmpty(workDrag)) {
            mToolList = HttpToolDragSort.getWorkDratgModels(this, workDrag);
        } else {
            initToolList();
        }

        mDsAdapter = new WorkDragSortAdapter(this, getWorkList(mToolList), false);
        mLvDragSort.setAdapter(mDsAdapter);
    }

    private void initDragSort() {
        mLvDragSort.setFadingEdgeLength(0);
        mLvDragSort.setCacheColorHint(Color.TRANSPARENT);
        mLvDragSort.setDivider(getResources().getDrawable(
                R.drawable.listview_division));
//        mLvDragSort.setDividerHeight(DensityUtil.dip2px(context, 10));
        mLvDragSort.setDropListener(onDrop);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mLvDragSort.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (from != to) {
                mDsAdapter.changePosition(from, to);
            }
        }
    };

    private void initToolList() {
        List<WorkDragChildModel>[] mChild = new List[LIST_SIZE];
        for (int i = 0; i < LIST_SIZE; i++) {
            mChild[i] = new ArrayList<>();
        }
        mChild[0].add(new WorkDragChildModel(false, true, ConstWorkType.WORK_MyDailyPaper));
        mChild[0].add(new WorkDragChildModel(false, true, ConstWorkType.WORK_SalePlan));
        mChild[0].add(new WorkDragChildModel(false, true, ConstWorkType.WORK_WebCustomer));
        mChild[1].add(new WorkDragChildModel(false, true, ConstWorkType.WORK_startH5));
        mChild[1].add(new WorkDragChildModel(false, true, ConstWorkType.WORK_WeiXinShare));
        mChild[1].add(new WorkDragChildModel(false, true, ConstWorkType.WORK_startSmsAssists));
        mChild[1].add(new WorkDragChildModel(false, true, ConstWorkType.WORK_FileHelper));
        mChild[1].add(new WorkDragChildModel(false, true, ConstWorkType.WORK_ScanCardCamera));
        mChild[1].add(new WorkDragChildModel(false, true, ConstWorkType.WORK_SellAdviser));
        mChild[2].add(new WorkDragChildModel(false, true, ConstWorkType.WORK_TeamCustomer));
        mChild[2].add(new WorkDragChildModel(false, true, ConstWorkType.WORK_TeamWorkManager));
        mChild[2].add(new WorkDragChildModel(false, true, ConstWorkType.WORK_StatisWorkRank));
        mChild[2].add(new WorkDragChildModel(false, true, ConstWorkType.WORK_SaleOrders));
        mChild[2].add(new WorkDragChildModel(false, true, ConstWorkType.WORK_SalePlanRank));
        mToolList.clear();
        String groupName = "";
        for (int i = 0; i < LIST_SIZE; i++) {
            if (i == ConstWorkType.DEFAULT_OFFICE_NEW) {
                groupName = getResources().getString(R.string.str_sort_work_daily);
            } else if (i == ConstWorkType.MOVE_OFFICE_NEW) {
                groupName = getResources().getString(R.string.str_sort_work_move);
            } else if (i == ConstWorkType.SALE_MANAGE_NEW) {
                groupName = getResources().getString(R.string.str_sort_work_sale);
            }
            mToolList.add(new WorkDragSortModel(i, groupName, mChild[i]));
        }
    }

    private class DispatchRequest implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rl_tool_set:
                    if (mIvToolSet.getVisibility() == VISIBLE) {
                        //点击设置
                        isSetting(true);
                        // 显示不可见列表和全部checkbox和可滑动控件
                        mDsAdapter.update(getmToolList(), true);
                    } else {
                        //保存JSON到本地
                        SPWokrDrag.setWorkDrag(MainActivity.this, HttpToolDragSort.listToJson(mDsAdapter.getmList()), uid);
                        //成功了则返回工具界面并更新mToolList
                        isSetting(false);
                        mToolList.clear();
                        mToolList.addAll(mDsAdapter.getmList());
                        mDsAdapter.update(getWorkList(mToolList), false);
                    }
                    break;
                case R.id.title_cancel:
                    change();
                    break;
            }
        }
    }

    //用于列表的展示，不直接用mToolList是防止拖动列表改动后按返回mToolList也被修改
    private List<WorkDragSortModel> getmToolList() {
        List<WorkDragSortModel> workDragSortModelList = new ArrayList<>();
        if (mToolList != null) {
            workDragSortModelList = getWorkDragSortModel();
        }
        return workDragSortModelList;
    }

    //这里用于工具界面显示的list,因为如果某个workChildren中的item都不可见或者都没有权限，则工具界面不需要显示并且外部list不能有DividerHeight
    private List<WorkDragSortModel> getWorkList(List<WorkDragSortModel> workDragSortModels) {
        List<WorkDragSortModel> workDragSortModelList = new ArrayList<>();
        if (mToolList.size() <= 0) {
            return workDragSortModelList;
        }
        workDragSortModelList = getWorkDragSortModel();
        int deletePosition = 0;
        for (int i = 0; i < workDragSortModels.size(); i++) {
            int uncheckSize = 0;
            for (int j = 0; j < workDragSortModels.get(i).workChildren.size(); j++) {
                if (!workDragSortModels.get(i).workChildren.get(j).workIsCheck) {
                    workDragSortModelList.get(i - deletePosition).workChildren.remove(j - uncheckSize);
                    uncheckSize++;
                }
                if (uncheckSize == workDragSortModels.get(i).workChildren.size()) {
                    workDragSortModelList.remove(i - deletePosition);
                    deletePosition++;
                }
            }
        }
        return workDragSortModelList;
    }

    //这里workDragSortModelList不直接用addAll来添加列表是因为workChildren会被引用到地址，导致工具设置界面跳动子列表后按返回导致工具界面也被改变
    private List<WorkDragSortModel> getWorkDragSortModel() {
        List<WorkDragSortModel> workDragSortModelList = new ArrayList<>();
        for (int i = 0; i < mToolList.size(); i++) {
            WorkDragSortModel workDragSortModel = new WorkDragSortModel();
            workDragSortModel.workChildren = new ArrayList<>();
            workDragSortModel.groupId = mToolList.get(i).groupId;
            workDragSortModel.groupName = mToolList.get(i).groupName;
            workDragSortModel.workChildren.addAll(mToolList.get(i).workChildren);
            workDragSortModelList.add(workDragSortModel);
        }
        return workDragSortModelList;
    }

    //如果当前在工具fragment并且是设置状态时
    public void change() {
        isSetting(false);
        mDsAdapter.update(getWorkList(mToolList), false);
    }

    /**
     * 当前是否在排序
     *
     * @param setting
     */
    public void isSetting(boolean setting) {
        mIvToolSet.setVisibility(setting ? View.GONE : VISIBLE);
        mTvToolSet.setVisibility(setting ? View.VISIBLE : View.GONE);
        mTvCancel.setVisibility(setting ? View.VISIBLE : View.GONE);
    }
}
