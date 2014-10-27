/*
 * 
 */
package com.skycober.mineral.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ab.global.AbConstant;
import com.ab.net.AbHttpItem;
import com.ab.net.AbHttpQueue;
import com.ab.util.AbDateUtil;
import com.ab.util.AbFileUtil;
// TODO: Auto-generated Javadoc
import com.ab.view.AbGridView;

/**
 * 描述：下拉刷新与上拉分页的GridView.
 *
 * @author zhaoqp
 * @date：2013-2-19 上午10:58:51
 * @version v1.0
 */
public class AbPullToRefreshGridView extends AbGridView implements OnScrollListener,OnTouchListener {

	/** The tag. */
	private String TAG = "AbPullToRefreshGridView";
	
	/** 布局参数. */
	public LinearLayout.LayoutParams layoutParamsFF = null;
	
	/** The layout params fw. */
	public LinearLayout.LayoutParams layoutParamsFW = null;
	
	/** The layout params wf. */
	public LinearLayout.LayoutParams layoutParamsWF = null;
	
	/** The layout params ww. */
	public LinearLayout.LayoutParams layoutParamsWW = null;
	
	
	/** The Constant RELEASE_To_REFRESH. */
	private final static int RELEASE_To_REFRESH = 0;
	
	/** The Constant PULL_To_REFRESH. */
	private final static int PULL_To_REFRESH = 1;
	
	/** 正在刷新. */
	private final static int REFRESHING = 2;
	
	/** 刷新完成. */
	private final static int DONE = 3;
	
	/** The Constant LOADING. */
	private final static int LOADING = 4;
	
	/** The Constant RATIO. */
	private final static int RATIO = 3;
	
	/** The m grid view. */
	private GridView mGridView = null;
	
	/** 顶部刷新栏. */
	private LinearLayout headerView;
	
	/** The tips textview. */
	private TextView tipsTextview;
	
	/** The last updated text view. */
	private TextView lastUpdatedTextView;
	
	/** The arrow image view. */
	private ImageView arrowImageView;
	
	/** The header progress bar. */
	private ProgressBar headerProgressBar;
	
	/** The arrow image. */
	private Bitmap arrowImage = null;
	
	/** 底部刷新栏. */
	private LinearLayout footerView;
	
	/** The footer textview. */
	private TextView footerTextview;
	
	/** The footer progress bar. */
	private ProgressBar footerProgressBar;
	
	/** 无数据时显示的一条. */
	private LinearLayout nodataView;
	
	/** 数据相关. */
	private BaseAdapter mAdapter = null;
	
	/** The net get. */
	private AbHttpQueue netGet = null;
	
	/** The item refresh. */
	private AbHttpItem itemRefresh = null;
	
	/** The item scroll. */
	private AbHttpItem itemScroll = null;

	/** 界面控制相关. */
	private RotateAnimation animation;
	
	/** The reverse animation. */
	private RotateAnimation reverseAnimation;
	
	/** The is recored. */
	private boolean isRecored;
	
	/** The head content width. */
	private int headContentWidth;
	
	/** The head content height. */
	private int headContentHeight;
	
	/** The foot content width. */
	private int footContentWidth;
	
	/** The foot content height. */
	private int footContentHeight;
	
	/** The m grid width. */
	private int mGridWidth;
	
	/** The m grid height. */
	private int mGridHeight;
	
	/** The start y. */
	private int startY;
	
	/** The first item index. */
	private int firstItemIndex;
	
	/** The state. */
	private int state;
	
	/** The is back. */
	private boolean isBack;
	
	/** The refresh listener. */
	private OnRefreshListener refreshListener;
	
	/** The is refreshable. */
	private boolean isRefreshable;
	
	//private int i = 1;
	
	//保存上一次的刷新时间
	/** The last refresh time. */
	private String lastRefreshTime = null;

	/**
	 * Instantiates a new ab pull to refresh grid view.
	 *
	 * @param context the context
	 */
	public AbPullToRefreshGridView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * Instantiates a new ab pull to refresh grid view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public AbPullToRefreshGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 初始化布局.
	 *
	 * @param context the context
	 */
	private void init(Context context) {
		mGridView = this.getGridView();
		mGridView.setCacheColorHint(context.getResources().getColor(android.R.color.transparent));
		//默认值 自定义可以到Activity中修改
		mGridView.setColumnWidth(100);
		mGridView.setGravity(Gravity.CENTER);
		mGridView.setHorizontalSpacing(5);
		AlphaAnimation animationAlpha = new AlphaAnimation(0.0f,1.0f);  
	    //得到一个LayoutAnimationController对象;
	    LayoutAnimationController lac = new LayoutAnimationController(animationAlpha);
	    //设置控件显示的顺序;
	    lac.setOrder(LayoutAnimationController.ORDER_RANDOM);
	    //设置控件显示间隔时间;
	    lac.setDelay(0.5f);
	    //为View设置LayoutAnimationController属性;
		mGridView.setLayoutAnimation(lac);
		
		mGridView.setNumColumns(2);
		mGridView.setPadding(5, 5, 5, 5);
		mGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		mGridView.setVerticalSpacing(30);
		
		layoutParamsFF = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		layoutParamsFW = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParamsWF = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		layoutParamsWW = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		lastRefreshTime = AbDateUtil.getCurrentDate(AbDateUtil.dateFormatHMS);
		
		//顶部刷新栏整体内容
		headerView = new LinearLayout(context);
		headerView.setOrientation(LinearLayout.HORIZONTAL);
		headerView.setBackgroundColor(Color.rgb(225, 225,225));
		headerView.setGravity(Gravity.CENTER); 
		
		//显示箭头与进度
		FrameLayout headImage =  new FrameLayout(context);
		arrowImageView = new ImageView(context);
		//从包里获取的箭头图片
		arrowImage = AbFileUtil.getBitmapFormSrc("image/arrow.png");
		arrowImageView.setImageBitmap(arrowImage);
		arrowImageView.setMinimumWidth(50);
		arrowImageView.setMinimumHeight(50);
		arrowImageView.setPadding(10,0,10,0);
		//style="?android:attr/progressBarStyleSmall"
		headerProgressBar = new ProgressBar(context,null,android.R.attr.progressBarStyleSmall);
		headerProgressBar.setVisibility(View.GONE);
		headImage.addView(arrowImageView,layoutParamsWW);
		headImage.addView(headerProgressBar,layoutParamsWW);
		
		//顶部刷新栏文本内容
		LinearLayout headText  = new LinearLayout(context);
		tipsTextview = new TextView(context);
		lastUpdatedTextView = new TextView(context);
		headText.setOrientation(LinearLayout.VERTICAL);
		headText.setGravity(Gravity.CENTER_VERTICAL);
		headText.setPadding(10,0,0,0);
		headText.addView(tipsTextview,layoutParamsWW);
		headText.addView(lastUpdatedTextView,layoutParamsWW);
		tipsTextview.setTextColor(Color.rgb(107, 107, 107));
		lastUpdatedTextView.setTextColor(Color.rgb(107, 107, 107));
		tipsTextview.setTextSize(15);
		lastUpdatedTextView.setTextSize(14);
		headerView.addView(headImage,layoutParamsWW);
		headerView.addView(headText,layoutParamsWW);
		
		measureView(headerView);
		headContentHeight = headerView.getMeasuredHeight();
		headContentWidth = headerView.getMeasuredWidth();
		headerView.setPadding(0, -1 * headContentHeight, 0, 0);
		headerView.invalidate();
		addHeaderView(headerView);
		
		//底部刷新
		footerView  = new LinearLayout(context);  
		//设置布局 水平方向  
		footerView.setOrientation(LinearLayout.HORIZONTAL);
		footerView.setGravity(Gravity.CENTER); 
		footerView.setBackgroundColor(Color.rgb(225, 225,225));
		
		footerTextview = new TextView(context);  
		footerTextview.setGravity(Gravity.CENTER_VERTICAL);
		footerTextview.setTextColor(Color.rgb(107, 107, 107));
		footerTextview.setTextSize(15);
		footerTextview.setMinimumHeight(50);
		footerProgressBar = new ProgressBar(context,null,android.R.attr.progressBarStyleSmall);
		footerProgressBar.setVisibility(View.GONE);
		footerView.addView(footerProgressBar,layoutParamsWW);
		footerView.addView(footerTextview,layoutParamsWW);
		footerTextview.setText("更多..."); 
		footerView.setVisibility(View.GONE);
		
		measureView(footerView);
		footContentHeight = footerView.getMeasuredHeight();
		footContentWidth = footerView.getMeasuredWidth();
		footerView.setPadding(0,0, 0, 0);
		footerView.invalidate();
		addFooterView(footerView);
		
		//无数据时的View
		nodataView = new LinearLayout(context);
		nodataView.setOrientation(LinearLayout.HORIZONTAL);
		nodataView.setBackgroundColor(Color.rgb(225, 225,225));
		nodataView.setGravity(Gravity.CENTER);
		
		measureView(mGridView);
		mGridHeight = mGridView.getMeasuredHeight();
		mGridWidth = mGridView.getMeasuredWidth();
		mGridView.setOnScrollListener(this);
		mGridView.setOnTouchListener(this);
		animation = new RotateAnimation(0, -180,RotateAnimation.RELATIVE_TO_SELF, 0.5f,RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,RotateAnimation.RELATIVE_TO_SELF, 0.5f,RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		state = DONE;
		isRefreshable = false;
		
		netGet = AbHttpQueue.getInstance();
		
		//下拉刷新,在这个里面更新数据
		setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				//下载数据
				netGet.downloadBeforeClean(itemRefresh);
				
				//mAdapter.notifyDataSetChanged();
				//onRefreshComplete();
			}
		});
	}

	/**
	 * 描述：TODO.
	 *
	 * @param arg0 the arg0
	 * @param firstVisiableItem the first visiable item
	 * @param visibleItemCount the visible item count
	 * @param totalItemCount the total item count
	 * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.AbsListView, int, int, int)
	 * @author: zhaoqp
	 * @date：2013-6-17 上午9:04:48
	 * @version v1.0
	 */
	public void onScroll(AbsListView arg0, int firstVisiableItem, int visibleItemCount,int totalItemCount) {
		firstItemIndex = firstVisiableItem;
		//int lastItem = firstVisiableItem + visibleItemCount - 1;  
		//Log.v(TAG, "MyListView-->onScroll" );
	}
	
	/**
	 * 描述：TODO.
	 *
	 * @param view the view
	 * @param scrollState the scroll state
	 * @see android.widget.AbsListView.OnScrollListener#onScrollStateChanged(android.widget.AbsListView, int)
	 * @author: zhaoqp
	 * @date：2013-6-17 上午9:04:48
	 * @version v1.0
	 */
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		//Log.i(TAG, "view.getFirstVisiblePosition() = "+view.getFirstVisiblePosition());
		switch (scrollState) {
		    // 当不滚动时
		    case OnScrollListener.SCROLL_STATE_IDLE:
		    	Log.i(TAG, "SCROLL_STATE_IDLE");
		    	if(view.getFirstVisiblePosition() == 0){
		    		Log.i(TAG, "判断滚动到顶部");
		    	}else if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
		    		Log.i(TAG, "判断滚动到底部");
					//进行刷新的展示,让progress显示出来
			    	footerProgressBar.setVisibility(View.VISIBLE);
			    	footerView.setVisibility(View.VISIBLE);
			    	footerTextview.setText(" 加载中..."); 
					//在这里获取数据
			    	netGet.downloadBeforeClean(itemScroll);
		    	}
		        break;
		    case OnScrollListener.SCROLL_STATE_FLING:
		    	Log.i(TAG, "SCROLL_STATE_FLING");
		    	footerView.setVisibility(View.GONE);
		    	break;
		    case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
		         break;
	    } 
	}
	
	

	/**
	 * 描述：TODO.
	 *
	 * @param v the v
	 * @param event the event
	 * @return true, if successful
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 * @author: zhaoqp
	 * @date：2013-6-17 上午9:04:48
	 * @version v1.0
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startY = (int) event.getY();
					//Log.v(TAG, "ACTION_DOWN 这是第  "+i+++"步" +1 );
				}
				break;
			case MotionEvent.ACTION_UP:
				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
					}
					if (state == PULL_To_REFRESH) {
						state = DONE;
						//Log.v(TAG, "ACTION_UP PULL_To_REFRESH and changeHeaderViewByState()" +" 这是第  "+i+++"步前"+2 );
						changeHeaderViewByState();
						//Log.v(TAG, "ACTION_UP PULL_To_REFRESH and changeHeaderViewByState() " +"这是第  "+i+++"步后"+2 );
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						//Log.v(TAG, "ACTION_UP RELEASE_To_REFRESH changeHeaderViewByState() " +"这是第  "+i+++"步" +3);
						changeHeaderViewByState();						
						onRefresh();
						//Log.v(TAG, "ACTION_UP RELEASE_To_REFRESH changeHeaderViewByState()" +" 这是第  "+i+++"步" +3);
					}
				}
				isRecored = false;
				isBack = false;
				break;
			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				if (!isRecored && firstItemIndex == 0) {
					isRecored = true;
					startY = tempY;
					//Log.v(TAG, "ACTION_MOVE 这是第  "+i+++"步" +4);
				}
				if (state != REFRESHING && isRecored && state != LOADING) {
					if (state == RELEASE_To_REFRESH) {
						mGridView.setSelection(0);
						if (((tempY - startY) / RATIO < headContentHeight)&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
							//Log.v(TAG, "changeHeaderViewByState() 这是第  "+i+++"步"+5 );
						}else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
							//Log.v(TAG, "ACTION_MOVE RELEASE_To_REFRESH 2  changeHeaderViewByState " +"这是第  "+i+++"步" +6);
						}
					}
					if (state == PULL_To_REFRESH) {
						mGridView.setSelection(0);
						if ((tempY - startY) / RATIO >= headContentHeight) {
							state = RELEASE_To_REFRESH;
							isBack = true;
							//Log.v(TAG, "changeHeaderViewByState " +"这是第  "+i+++"步前"+7 );
							changeHeaderViewByState();
							//Log.v(TAG, "changeHeaderViewByState " +"这是第  "+i+++"步后"+7 );
						}else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
							//Log.v(TAG, "ACTION_MOVE changeHeaderViewByState PULL_To_REFRESH 2" +" 这是第  "+i+++"步" +8);
						}
					}
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							//Log.v(TAG, "ACTION_MOVE DONE changeHeaderViewByState " +"这是第  "+i+++"步前" +9);
							changeHeaderViewByState();
							//Log.v(TAG, "ACTION_MOVE DONE changeHeaderViewByState " +"这是第  "+i+++"步后" +9);
						}
					}
					if (state == PULL_To_REFRESH) {
						headerView.setPadding(0, -1 * headContentHeight+(tempY - startY) / RATIO, 0, 0);
						//Log.v(TAG, -1 * headContentHeight+(tempY - startY) / RATIO+"ACTION_MOVE PULL_To_REFRESH 3  这是第  "+i+++"步"+10 );
					}
					if (state == RELEASE_To_REFRESH) {
						headerView.setPadding(0, (tempY - startY) / RATIO- headContentHeight, 0, 0);
						//Log.v(TAG, "ACTION_MOVE PULL_To_REFRESH 4 这是第  "+i+++"步" +11);
					}
				}
				break;
			}
		}
		return false;
	}

	/**
	 * Change header view by state.
	 */
	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			headerProgressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);
			tipsTextview.setText("松开刷新");
			//Log.v(TAG, "RELEASE_To_REFRESH 这是第  "+i+++"步"+12 +"请释放 刷新" );
			break;
		case PULL_To_REFRESH:
			headerProgressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);
				//tipsTextview.setText("isBack  is true ！！！");
			} else {
				//tipsTextview.setText("isBack  is false ！！！");
			}
			//Log.v(TAG, "PULL_To_REFRESH 这是第  "+i+++"步" +13+"  changeHeaderViewByState()");
			break;
		case REFRESHING:
			headerView.setPadding(0, 0, 0, 0);
			headerProgressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText("正在刷新");
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			//Log.v(TAG, "REFRESHING 这是第  "+i+++"步" +"正在加载中 ...REFRESHING");
			break;
		case DONE:
			headerView.setPadding(0, -1 * headContentHeight, 0, 0);
			headerProgressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageBitmap(arrowImage);
			tipsTextview.setText("刷新完成");
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			//Log.v(TAG, "DONE 这是第  "+i+++"步" +"已经加载完毕- DONE ");
			break;
		}
	}

	/**
	 * Sets the on refresh listener.
	 *
	 * @param refreshListener the new on refresh listener
	 */
	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	/**
	 * The listener interface for receiving onRefresh events.
	 * The class that is interested in processing a onRefresh
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addOnRefreshListener<code> method. When
	 * the onRefresh event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see OnRefreshEvent
	 */
	public interface OnRefreshListener {
		
		/**
		 * On refresh.
		 */
		public void onRefresh();
	}

	/**
	 * 描述：下拉数据完成必须被ListView主动调用，显示headerView.
	 */
	public void onRefreshComplete() {
		mAdapter.notifyDataSetChanged();
		state = DONE;
		lastUpdatedTextView.setText("最后刷新时间：" + lastRefreshTime);
		lastRefreshTime = AbDateUtil.getCurrentDate(AbDateUtil.dateFormatHMS);
		changeHeaderViewByState();
		//Log.v(TAG, "onRefreshComplete() 被调用");
		if(mAdapter.getCount()>0){
			footerView.setVisibility(View.GONE);
			footerProgressBar.setVisibility(View.GONE);
			footerTextview.setText("..."); 
	    	View v = this.getChildAt(2);
	    	if(v!=footerView){
	    		this.removeView(mGridView);
	    		this.addView(mGridView,1);
	    	}
		}else{
			footerView.setVisibility(View.VISIBLE);
			footerProgressBar.setVisibility(View.GONE);
			footerTextview.setText("没有数据!");
			this.removeView(mGridView);
			this.addView(mGridView);
		}
	}
	
	/**
	 * 描述：描述：上拉数据完成必须被ListView主动调用，显示footerView.
	 *
	 * @param have 是否还有新数据
	 */
	public void onScrollComplete(int have) {
		if(have == AbConstant.HAVE){
			mAdapter.notifyDataSetChanged();
			footerProgressBar.setVisibility(View.VISIBLE);
	    	footerView.setVisibility(View.GONE);
	    	footerTextview.setText(" 加载中..."); 
		}else if(have == AbConstant.NOTHAVE){
			footerProgressBar.setVisibility(View.GONE);
			footerTextview.setText("全部加载完成");
		}
	}

	/**
	 * On refresh.
	 */
	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
			//Log.v(TAG, "onRefresh被调用，这是第  "+i+++"步" );
		}
	}

	/**
	 * Measure view.
	 *
	 * @param child the child
	 */
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	/**
	 * 描述：设置数据列表的适配器.
	 *
	 * @param adapter the new adapter
	 */
	public void setAdapter(BaseAdapter adapter) {
		lastUpdatedTextView.setText(lastRefreshTime);
		lastRefreshTime = AbDateUtil.getCurrentDate(AbDateUtil.dateFormatHMS);
		mAdapter = adapter;
		mGridView.setAdapter(mAdapter);
	}
	
	/**
	 * 描述：下拉刷新事件设置.
	 *
	 * @param item the new refresh item
	 */
	public void setRefreshItem(AbHttpItem item) {
		itemRefresh = item;
	}
	
	/**
	 * 描述：上拉刷新事件设置.
	 *
	 * @param item the new scroll item
	 */
	public void setScrollItem(AbHttpItem item) {
		itemScroll = item;
	}

	/**
	 * 描述：TODO.
	 *
	 * @return the grid view
	 * @see com.ab.view.AbGridView#getGridView()
	 * @author: zhaoqp
	 * @date：2013-6-17 上午9:04:48
	 * @version v1.0
	 */
	@Override
	public GridView getGridView() {
		return super.getGridView();
	}
	
	
}
