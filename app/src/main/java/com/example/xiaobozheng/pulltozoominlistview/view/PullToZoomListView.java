package com.example.xiaobozheng.pulltozoominlistview.view;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created by xiaobozheng on 5/16/2016.
 */
public class PullToZoomListView extends ListView{

    private static final int INVALID_VALUE = -1;
    private static final String TAG = "PullToZoomListView";
    private FrameLayout mHeaderContainer;
    private int mHeadHeight;//头部视图的高
    private ImageView mHeaderImage;

    //偏移量
    float mLastMotionY = -1.0F;
    float mLastScale = -1.0F;
    float mMaxScale = -1.0F;

    //抽象的listView
    private AbsListView.OnScrollListener mOnScrollListener;
    private ScalingRunnalable mScalingRunnalable;
    private int mScreenHeight;  //屏幕的高
    private ImageView mShadow;

    //定义动画变化的速率
    /**
     * Interpolator定义了动画变化的速率，在Animations框架当中定义了一下几种Interpolator
     Ø         AccelerateDecelerateInterpolator：在动画开始与结束的地方速率改变比较慢，在中间的时候速率快。
     Ø         AccelerateInterpolator：在动画开始的地方速率改变比较慢，然后开始加速
     Ø         CycleInterpolator：动画循环播放特定的次数，速率改变沿着正弦曲线
     Ø         DecelerateInterpolator：在动画开始的地方速率改变比较慢，然后开始减速
     Ø         LinearInterpolator：动画以均匀的速率改变
     */
    private static final Interpolator sInterpolatar = new Interpolator(){
        @Override
        public float getInterpolation(float input) {
            float f = input - 1.0F;
            return 1.0F + f * (f*(f*(f*f)));
        }
    };
    public PullToZoomListView(Context context) {
        super(context);
        init(context);
    }

    public PullToZoomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullToZoomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void endScraling(){
        //下拉开始
    if (this.mHeaderContainer.getBottom() >= this.mHeadHeight)
        Log.d("mmm","endScraling");
        this.mScalingRunnalable.startAnimation(200L);
    }

    private void init(Context paramContext){
        //屏幕的像素
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();

        ///将当前窗口的一些信息放在DisplayMetrics类中，
        ((Activity)paramContext).getWindowManager().getDefaultDisplay()
                .getMetrics(localDisplayMetrics);

        //屏幕高度
        this.mScreenHeight = localDisplayMetrics.heightPixels;
        this.mHeaderContainer  = new FrameLayout(paramContext);
        this.mHeaderImage = new ImageView(paramContext);
        //屏幕宽度
        int i = localDisplayMetrics.widthPixels;
        setHeaderV


        //指定了该布局的宽和高(-1为宽,-2为高)
        FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(-1,-2);
    };

    public void setHeaderViewSize(int paramInt1, int paramInt2){
        Object localObject = this.mHeaderContainer.getLayoutParams();
        if (localObject == null){
            //如果没有的话，设置listview的宽高
            localObject = new AbsListView.LayoutParams(paramInt1, paramInt2);
            ((ViewGroup.LayoutParams)localObject).width = paramInt1;
            ((ViewGroup.LayoutParams)localObject).height = paramInt2;
            //这里代码有点乱
            this.mHeaderContainer.setLayoutParams((ViewGroup.LayoutParams)localObject);
            this.mHeadHeight = paramInt2;
        }
    }

    //启用一个动画来使HeaderView平滑的恢复到放大之前的状态。
    class ScalingRunnalable implements Runnable{
        long mDuration;  //持续时间
        boolean mIsFinished = true;  //是否结束
        float mScale;  //测量规模
        long mStartTime; //开始时间

        ScalingRunnalable(){}

        public void abortAnimation(){
            this.mIsFinished = true;
        }

        public boolean isFinished(){
            return this.mIsFinished;
        }
        @Override
        public void run() {
            float f2;
            ViewGroup.LayoutParams locallayoutParams;
            if ((!this.mIsFinished) && (this.mScale > 1.0D)){
                float f1 = ((float) SystemClock.currentThreadTimeMillis() - (float) this.mStartTime)
                        /this.mDuration;

                //每次执行run的时候mHeadContainer的高度改变了多少
                //偏移量-超出的值（速度快慢）
                f2 = this.mScale - (this.mScale - 1.0F)
                        * PullToZoomListView.sInterpolatar.getInterpolation(f1);
                //设计控件的布局
                locallayoutParams = PullToZoomListView.this.mHeaderContainer.getLayoutParams();
                //当超过高度的时候，开始变形
                if (f2 > 1.0F){

                    //设置图片的布局的 高度
                    locallayoutParams.height = PullToZoomListView.this.mHeadHeight;
                    locallayoutParams.height = (int)(f2* PullToZoomListView.this.mHeadHeight);

                    //设置变化
                    PullToZoomListView.this.mHeaderContainer.setLayoutParams(locallayoutParams);
                    PullToZoomListView.this.post(this);
                    return;
                }
                    this.mIsFinished = true;
            }
        }
        public void startAnimation(long paramLong){
            this.mStartTime = SystemClock.currentThreadTimeMillis();
            this.mDuration = paramLong;
            //mLastScale是根据垂直偏移计算出来的
            this.mScale = (float) ((PullToZoomListView.this.mHeaderContainer.getBottom())/PullToZoomListView.this.mHeadHeight);
            this.mIsFinished = false;
            //post调用ScalingRunnalable的run方法，而ScalingRunnalable run方法中再次调用了post，就这样不断的更新UI，直到达到一定的条件退出这个循环
            PullToZoomListView.this.post(this);
        }
    }

}
