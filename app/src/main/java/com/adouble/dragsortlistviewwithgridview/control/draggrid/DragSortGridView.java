package com.adouble.dragsortlistviewwithgridview.control.draggrid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import com.adouble.dragsortlistviewwithgridview.ui.LineGridView;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Author：hh
 * Date：2016/7/4 15:57
 * Version V1.0
 */
public class DragSortGridView extends LineGridView {

    private String TAG = "DragSortGridView";

    private int mDownX;
    private int mDownY;
    private int mWindowX;
    private int mWindowY;

    private boolean isSetting;//是否是设置页面
    /**
     * 拖拽的条目位置
     */
    private int mDragPosition;
    /**
     * GridView列数
     */
    private int mColumnNum = 3;
    /**
     * GridView横向间距
     */
    private int mHorizontalSpacing;
    /**
     * GridView竖向间距
     */
    private int mVerticalSpacing;

    /**
     * 按下的点到所在item的上边缘的距离
     */
    private int mPoint2ItemTop;

    /**
     * 按下的点到所在item的左边缘的距离
     */
    private int mPoint2ItemLeft;

    private View mDragItemView;

    private ImageView mDragImageView;

    private int mScreenHeight;

    /**
     * 状态栏的高度
     */
    private int mStatusHeight;

    /**
     * DragGridView自动滚动的速度
     */
    private static final int speed = 20;

    /**
     * item的移动动画是否结束
     */
    private boolean mAnimationEnd = true;
    /**
     * WindowManager管理器
     */
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private Vibrator mVibrator;
    private DragGridBaseAdapter mDragAdapter;
    private List<ObjectAnimator> mItemShakeAnimList = new ArrayList<>();
    private static final int[] ATTRS = new int[]{
            android.R.attr.horizontalSpacing,
            android.R.attr.verticalSpacing
    };

    public DragSortGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DragSortGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressWarnings("ResourceType")
    private void init(Context context, AttributeSet attrs) {
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = getStatusHeight(context); //获取状态栏的高度
        mScreenHeight = getScreenHeight(context);
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        mHorizontalSpacing = a.getDimensionPixelSize(0, 0);
        mVerticalSpacing = a.getDimensionPixelSize(1, 0);
        a.recycle();

    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof DragGridBaseAdapter) {
            mDragAdapter = (DragGridBaseAdapter) adapter;
        } else {
            throw new IllegalStateException("the adapter must be implements DragGridBaseAdapter");
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mDownX = (int) ev.getX();
            mDownY = (int) ev.getY();
            mWindowX = (int) ev.getRawX();// 长安事件在屏幕上的x位置
            mWindowY = (int) ev.getRawY();// 长安事件在屏幕上的y位置
            Log.d(TAG, "mDownX:" + mDownX);
            Log.d(TAG, "mDownY:" + mDownY);
            Log.d(TAG, "mWindowX:" + mWindowX);
            Log.d(TAG, "mWindowY:" + mWindowY);
            if (isSetting)
                onItemLongClickEvent(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void onItemLongClickEvent(final MotionEvent ev) {
        setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //根据按下的X,Y坐标获取所点击item的position
                mDragPosition = pointToPosition(mDownX, mDownY);
                if (mDragPosition != AdapterView.INVALID_POSITION) {
                    mDragItemView = getChildAt(mDragPosition - getFirstVisiblePosition());
                    mPoint2ItemTop = mDownY - mDragItemView.getTop();
                    mPoint2ItemLeft = mDownX - mDragItemView.getLeft();
                    mDragItemView.destroyDrawingCache();
                    mDragItemView.setDrawingCacheEnabled(true);
                    Log.d(TAG, "mPoint2ItemTop:" + mPoint2ItemTop);
                    Log.d(TAG, "mPoint2ItemLeft:" + mPoint2ItemLeft);
                    Bitmap dragBitmap = Bitmap.createBitmap(mDragItemView.getDrawingCache());
                    mVibrator.vibrate(50);//设置震动时间
                    createDragImage(dragBitmap, (int) ev.getRawX(), (int) ev.getRawY());
                    mDragItemView.setVisibility(View.INVISIBLE);
                    requestDisallowInterceptTouchEvent(true);
                    return true;
                }
                return true;
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mDragItemView != null && mDragPosition != AdapterView.INVALID_POSITION) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = (int) ev.getX();
                    mDownY = (int) ev.getY();
                    mWindowX = (int) ev.getRawX();
                    mWindowY = (int) ev.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mDownX = (int) ev.getX();
                    mDownY = (int) ev.getY();
                    mWindowX = (int) ev.getRawX();
                    mWindowY = (int) ev.getRawY();
                    onDrag(mDownX, mDownY, mWindowX, mWindowY);
                    break;
                case MotionEvent.ACTION_UP:
                    onDrop();
                    requestDisallowInterceptTouchEvent(false);
                    break;

                default:
                    break;
            }
        }
        try {
            return super.onTouchEvent(ev);
        }catch (Exception e){

        }
        return false;
    }

    /**
     * 在拖动的情况
     */
    private void onDrag(int mDownX, int mDownY, int mWindowX, int mWindowY) {
        if (mDragImageView != null) {
            mWindowLayoutParams.alpha = 0.55f;
            mWindowLayoutParams.x = mWindowX - mPoint2ItemLeft;
            mWindowLayoutParams.y = mWindowY - mPoint2ItemTop - mStatusHeight;
            mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams);
            //GridView||ScrollView自动滚动
            mHandler.post(mScrollRunnable);
            onSwapItem(mDownX, mDownY);
        }
    }

    /**
     * 在松手下放的情况
     */
    private void onDrop() {
        View view = getChildAt(mDragPosition - getFirstVisiblePosition());
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
        mDragAdapter.setHideItem(-1);
        releaseDragImage();
    }

    private void createDragImage(Bitmap bitmap, int mWindowX, int mWindowY) {
        releaseDragImage();
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowLayoutParams.x = mWindowX - mPoint2ItemLeft;
        mWindowLayoutParams.y = mWindowY - mPoint2ItemTop - mStatusHeight;
        mWindowLayoutParams.alpha = 0.55f; //透明度
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        mDragImageView = new ImageView(getContext());
        mDragImageView.setImageBitmap(bitmap);
        mWindowManager.addView(mDragImageView, mWindowLayoutParams);
//        startShakeAnimation();
    }

    private void releaseDragImage() {
        if (mDragImageView != null) {
            try {
                if(mWindowManager!=null && mDragImageView!=null)
                    mWindowManager.removeView(mDragImageView);
            }catch (Exception e){

            }
            mDragImageView = null;
        }
    }

    private Handler mHandler = new Handler();
    private Runnable mScrollRunnable = new Runnable() {

        @Override
        public void run() {
            int scrollY;
            if (getFirstVisiblePosition() == 0 || getLastVisiblePosition() == getCount() - 1) {
                mHandler.removeCallbacks(mScrollRunnable);
            }
            Log.d(TAG, "mWindowY----" + mWindowY);
            if (mWindowY > 4 * mScreenHeight / 5) {
                scrollY = speed;
                Log.d(TAG, "滑动到底部----" + mWindowY);
            } else if (mWindowY < mScreenHeight / 5) {
                scrollY = -speed;
                Log.d(TAG, "滑动到顶部----" + mWindowY);
            } else {
                Log.d(TAG, "滑动到中间----" + mWindowY);
                scrollY = 0;
                mHandler.removeCallbacks(mScrollRunnable);
            }
            ViewParent parent = getParent().getParent();
            if (parent instanceof ScrollView) {
                ((ScrollView) parent).smoothScrollBy(0, scrollY);
            } else {
                smoothScrollBy(scrollY, 10);
            }

        }
    };

    /**
     * 交换item,并且控制item之间的显示与隐藏效果
     *
     * @param moveX
     * @param moveY
     */
    private void onSwapItem(int moveX, int moveY) {
        //获取我们手指移动到的那个item的position
        final int tempPosition = pointToPosition(moveX, moveY);

        //假如tempPosition 改变了并且tempPosition不等于-1,则进行交换
        if (tempPosition != mDragPosition && tempPosition != AdapterView.INVALID_POSITION && mAnimationEnd) {
            /**
             * 交换item
             */
            mDragAdapter.reorderItems(mDragPosition, tempPosition);
            /**
             * 设置新到的位置隐藏
             */
            mDragAdapter.setHideItem(tempPosition);

            final ViewTreeObserver observer = getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    observer.removeOnPreDrawListener(this);
                    animateReorder(mDragPosition, tempPosition);
                    mDragPosition = tempPosition;
                    return true;
                }
            });

        }
    }


    /**
     * item的交换动画效果
     *
     * @param oldPosition
     * @param newPosition
     */
    private void animateReorder(final int oldPosition, final int newPosition) {
        boolean isForward = newPosition > oldPosition;
        List<Animator> animatorList = new LinkedList<>();
        if (isForward) {
            for (int pos = oldPosition; pos < newPosition; pos++) {
                View view = getChildAt(pos - getFirstVisiblePosition());
                if ((pos + 1) % mColumnNum == 0) {
                    animatorList.add(createTranslationAnimations(view,
                            -(view.getWidth() + mHorizontalSpacing) * (mColumnNum - 1), 0,
                            view.getHeight() + mVerticalSpacing, 0));
                } else {
                    animatorList.add(createTranslationAnimations(view,
                            view.getWidth() + mHorizontalSpacing, 0, 0, 0));
                }
            }
        } else {
            for (int pos = oldPosition; pos > newPosition; pos--) {
                View view = getChildAt(pos - getFirstVisiblePosition());
                if ((pos) % mColumnNum == 0) {
                    animatorList.add(createTranslationAnimations(view,
                            (view.getWidth() + mHorizontalSpacing) * (mColumnNum - 1), 0,
                            -view.getHeight() - mVerticalSpacing, 0));
                } else {
                    animatorList.add(createTranslationAnimations(view,
                            -view.getWidth() - mHorizontalSpacing, 0, 0, 0));
                }
            }
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorList);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimationEnd = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationEnd = true;
            }
        });
        animatorSet.start();
    }

    /**
     * Desc:
     * Author：LiZhimin
     * Date：2016/7/6 14:15
     */
    private AnimatorSet createTranslationAnimations(View view, float startX, float endX, float startY,
                                                    float endY) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX", startX, endX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        return animSetXY;
    }

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    private static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject)
                        .toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    private int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public boolean isSetting() {
        return isSetting;
    }

    public void setSetting(boolean setting) {
        isSetting = setting;
    }
}
