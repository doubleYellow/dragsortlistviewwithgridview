package com.adouble.dragsortlistviewwithgridview.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.adouble.dragsortlistviewwithgridview.R;
import com.adouble.dragsortlistviewwithgridview.consts.ConstWorkType;
import com.adouble.dragsortlistviewwithgridview.control.draggrid.DragGridBaseAdapter;
import com.adouble.dragsortlistviewwithgridview.model.WorkDragChildModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author：hh
 * Date：2016/6/30 17:53
 */
public class WorkDragSortGridChildAdapter extends BaseAdapter implements DragGridBaseAdapter {
    private List<WorkDragChildModel> mChilds = new ArrayList<>();
    private Context context;
    private int mHidePosition = -1;
    private boolean isSetting;

    public WorkDragSortGridChildAdapter(Context context, List<WorkDragChildModel> list, boolean isSetting) {
        this.isSetting = isSetting;
        this.context = context;
        this.mChilds = list;
    }

    @Override
    public int getCount() {
        return mChilds.size();
    }

    @Override
    public Object getItem(int position) {
        return mChilds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 由于复用convertView导致拖动时某些item消失了，所以这里设置状态时不复用item，
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (isSetting || convertView == null) {
            convertView = View.inflate(context, R.layout.adapter_work_dargsort_gridview_item, null);
        }
        ImageView mImageView = (ImageView) convertView.findViewById(R.id.iv_channel_icon);
        TextView mTextView = (TextView) convertView.findViewById(R.id.tv_channel_name);
        CheckBox mChkHide = (CheckBox) convertView.findViewById(R.id.chk_hide);
        final WorkDragChildModel childModel = (WorkDragChildModel) getItem(position);
        setTitle(childModel.workNameId, mTextView, mImageView);
        if (position == mHidePosition) {
            convertView.setVisibility(View.INVISIBLE);
        }
        mChkHide.setChecked(childModel.workIsCheck);
        mChkHide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ((childModel.workIsCheck && !isChecked) || (!childModel.workIsCheck && isChecked)) {
                    childModel.workIsCheck = isChecked;
                }
            }
        });
        mChkHide.setVisibility(isSetting ? View.VISIBLE : View.GONE);
        return convertView;
    }


    @Override
    public void reorderItems(int oldPosition, int newPosition) {
        WorkDragChildModel model = mChilds.get(oldPosition);
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(mChilds, i, i + 1);
            }
        } else if (oldPosition > newPosition) {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(mChilds, i, i - 1);
            }
        }
        mChilds.set(newPosition, model);
    }

    @Override
    public void setHideItem(int hidePosition) {
        this.mHidePosition = hidePosition;
        notifyDataSetChanged();
    }

    @Override
    public void removeItem(int removePosition) {
        mChilds.remove(removePosition);
        notifyDataSetChanged();

    }

    public static class ViewHolder {
        private ImageView mImageView;
        private TextView mTextView;
    }

    public void update(List<WorkDragChildModel> mChilds) {
        this.mChilds = mChilds;
        notifyDataSetChanged();
    }

    private void setTitle(int id, TextView mTvTtitle, ImageView mIvIcon) {
        switch (id) {
            case ConstWorkType.WORK_WebCustomer:
                mTvTtitle.setText(context.getResources().getString(R.string.str_visitor));
                mIvIcon.setImageResource(R.drawable.icon_web_service_small);
                break;
            case ConstWorkType.WORK_MyDailyPaper:
                mTvTtitle.setText(context.getResources().getString(R.string.str_dailayreport));
                mIvIcon.setImageResource(R.drawable.icon_web_service_small);
                break;
            case ConstWorkType.WORK_SalePlan:
                mTvTtitle.setText(context.getResources().getString(R.string.str_saleplan));
                mIvIcon.setImageResource(R.drawable.icon_web_service_small);
                break;
            case ConstWorkType.WORK_ScanCardCamera:
                mTvTtitle.setText(context.getResources().getString(R.string.str_scan_card));
                mIvIcon.setImageResource(R.drawable.icon_web_service_small);
                break;
            case ConstWorkType.WORK_FileHelper:
                mTvTtitle.setText(context.getResources().getString(R.string.str_file_assistant));
                mIvIcon.setImageResource(R.drawable.icon_web_service_small);
                break;
            case ConstWorkType.WORK_startSmsAssists:
                mTvTtitle.setText(context.getResources().getString(R.string.str_sms_assis));
                mIvIcon.setImageResource(R.drawable.icon_web_service_small);
                break;
            case ConstWorkType.WORK_startH5:
                mTvTtitle.setText(context.getResources().getString(R.string.str_from));
                mIvIcon.setImageResource(R.drawable.icon_web_service_small);
                break;
            case ConstWorkType.WORK_WeiXinShare:
                mTvTtitle.setText(context.getResources().getString(R.string.str_weixin_share_title));
                mIvIcon.setImageResource(R.drawable.icon_web_service_small);
                break;
            case ConstWorkType.WORK_SellAdviser:
                mTvTtitle.setText(context.getResources().getString(R.string.str_sell_adviser));
                mIvIcon.setImageResource(R.drawable.icon_web_service_small);
                break;
            case ConstWorkType.WORK_TeamCustomer:
                mTvTtitle.setText(context.getResources().getString(R.string.team_customer_name));
                mIvIcon.setImageResource(R.drawable.icon_web_service_small);
                break;
            case ConstWorkType.WORK_TeamWorkManager:
                mTvTtitle.setText(context.getResources().getString(R.string.str_workmanage));
                mIvIcon.setImageResource(R.drawable.icon_web_service_small);
                break;
            case ConstWorkType.WORK_SaleOrders:
                mTvTtitle.setText(context.getResources().getString(R.string.str_sale_orders));
                mIvIcon.setImageResource(R.drawable.icon_web_service_small);
                break;
            case ConstWorkType.WORK_StatisWorkRank:
                mTvTtitle.setText(context.getResources().getString(R.string.str_head_title_work_statis));
                mIvIcon.setImageResource(R.drawable.icon_web_service_small);
                break;
            case ConstWorkType.WORK_SalePlanRank:
                mTvTtitle.setText(context.getResources().getString(R.string.str_saleplan_rank));
                mIvIcon.setImageResource(R.drawable.icon_web_service_small);
                break;
            default:
                break;

        }
    }

    public void update(List<WorkDragChildModel> mChilds, boolean isSetting) {
        this.isSetting = isSetting;
        this.mChilds = mChilds;
        notifyDataSetChanged();
    }

    public List<WorkDragChildModel> getmChilds() {
        if (mChilds == null)
            mChilds = new ArrayList<>();
        return mChilds;
    }
}
