package com.adouble.dragsortlistviewwithgridview.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adouble.dragsortlistviewwithgridview.R;
import com.adouble.dragsortlistviewwithgridview.consts.ConstWorkType;
import com.adouble.dragsortlistviewwithgridview.control.draggrid.DragSortGridView;
import com.adouble.dragsortlistviewwithgridview.model.WorkDragSortModel;
import com.adouble.dragsortlistviewwithgridview.util.StringUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hh on 2016/5/25.
 */
public class WorkDragSortAdapter extends ArrayAdapter<WorkDragSortModel> {
    private boolean isSeting;//是在工具还是在工具设置，用来判断列表的显示
    private List<WorkDragSortModel> mList = new ArrayList<>();
    private Context context;
    private WorkDragSortGridChildAdapter[] mDSChildAdapter;

    public WorkDragSortAdapter(Context context, List<WorkDragSortModel> list, boolean isSeting) {
        super(context, R.layout.adapter_work_dargsort, list);
        mList.clear();
        mList.addAll(list);
        this.context = context;
        this.isSeting = isSeting;
        mDSChildAdapter = new WorkDragSortGridChildAdapter[mList.size()];
        for (int i = 0; i < list.size(); i++) {
            mDSChildAdapter[i] = new WorkDragSortGridChildAdapter(context, mList.get(i).workChildren, isSeting);
        }
    }

    public void update(List<WorkDragSortModel> mList, boolean isSeting) {
        if (mList != null) {
            this.mList .clear();
            this.mList.addAll(mList);
            this.isSeting = isSeting;
            mDSChildAdapter = new WorkDragSortGridChildAdapter[mList.size()];
            for (int i = 0; i < mList.size(); i++) {
                if (mDSChildAdapter[i] == null) {
                    mDSChildAdapter[i] = new WorkDragSortGridChildAdapter(context, mList.get(i).workChildren, isSeting);
                } else {
                    mDSChildAdapter[i].update(mList.get(i).workChildren, isSeting);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void changePosition(int from, int to) {
        WorkDragSortModel workDragSortModel = mList.get(from);
        mList.remove(from);
        mList.add(to, workDragSortModel);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public WorkDragSortModel getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 由于复用convertView导致切换item位置后不能复写，所以这里不复用item，
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (isSeting) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_work_dargsort, null);
            holder.mLlDrag = (LinearLayout) convertView.findViewById(R.id.drag_handle);
            holder.mDragGridView = (DragSortGridView) convertView.findViewById(R.id.mDragGridView);
            holder.mViewDivider = convertView.findViewById(R.id.view_divider);
            holder.mTvModuleName = (TextView) convertView.findViewById(R.id.tv_moduleName);
            holder.mFrameLineBg = (FrameLayout) convertView.findViewById(R.id.frame_line_bg);
        } else {
            if (convertView != null && convertView.getTag() instanceof ViewHolder) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.adapter_work_dargsort, null);
                holder.mLlDrag = (LinearLayout) convertView.findViewById(R.id.drag_handle);
                holder.mDragGridView = (DragSortGridView) convertView.findViewById(R.id.mDragGridView);
                holder.mViewDivider = convertView.findViewById(R.id.view_divider);
                holder.mTvModuleName = (TextView) convertView.findViewById(R.id.tv_moduleName);
                holder.mFrameLineBg = (FrameLayout) convertView.findViewById(R.id.frame_line_bg);
                convertView.setTag(holder);
            }
        }
        holder.mDragGridView.setSetting(isSeting);
        holder.mLlDrag.setVisibility(isSeting ? View.VISIBLE : View.GONE);
        holder.mViewDivider.setVisibility(isSeting ? View.VISIBLE : View.GONE);
        holder.mTvModuleName.setText(mList.get(position).groupName);
        if (position == 0 && isSeting) {
            holder.mTvModuleName.setText(StringUtil.addString(mList.get(position).groupName, context.getResources().getString(R.string.str_longclick_drag)));
        }
        initDragSort(holder.mDragGridView, position);
        holder.mDragGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                switch (mList.get(position).workChildren.get(pos).workNameId) {
                    case ConstWorkType.WORK_WebCustomer:
                        break;
                    case ConstWorkType.WORK_MyDailyPaper:
                        break;
                    case ConstWorkType.WORK_SalePlan:
                        break;
                    case ConstWorkType.WORK_ScanCardCamera:
                        break;
                    case ConstWorkType.WORK_FileHelper:
                        break;
                    case ConstWorkType.WORK_startSmsAssists:
                        break;
                    case ConstWorkType.WORK_startH5:
                        break;
                    case ConstWorkType.WORK_WeiXinShare:
                        break;
                    case ConstWorkType.WORK_SellAdviser:
                        break;
                    case ConstWorkType.WORK_TeamCustomer:
                        break;
                    case ConstWorkType.WORK_TeamWorkManager:
                        break;
                    case ConstWorkType.WORK_StatisWorkRank:
                        break;
                    case ConstWorkType.WORK_SaleOrders:
                        break;
                    case ConstWorkType.WORK_SalePlanRank:
                        break;
                    default:
                        break;
                }
            }
        });
        return convertView;
    }

    public class ViewHolder {
        private LinearLayout mLlDrag;
        private DragSortGridView mDragGridView;
        private View mViewDivider;
        private TextView mTvModuleName;
        private FrameLayout mFrameLineBg;
    }

    private void initDragSort(final DragSortGridView mLvDragSort, final int position) {
        //1.因为setAdapter中注册了观察者，所以之后使用setAdapter1方法不在注册2.这里先update是可能子列表有item拖动所以先update数据
        if (position < 0 || position >= mDSChildAdapter.length)
            return;
        mDSChildAdapter[position].update(mList.get(position).workChildren, isSeting);
        mLvDragSort.setAdapter(mDSChildAdapter[position]);
    }

    public int getDragSortSize(int position) {
        int itemSize = 0;
        for (int i = 0; i < mList.get(position).workChildren.size(); i++) {
            if (mList.get(position).workChildren.get(i).workIsCheck) {
                itemSize++;
            }
        }
        return itemSize;
    }

    public List<WorkDragSortModel> getmList() {
        return mList;
    }
}
