package com.skycober.mineral.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.skycober.mineral.R;

/**
 * 自定义下拉上拉list
 * 
 * @author Yes366
 * 
 */
public class MyListView extends ListView implements OnScrollListener {

	private static final String TAG = "listview";

	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;

	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;

	private LayoutInflater inflater;

	private LinearLayout headView, footView;

	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private TextView moreTextView;
	private ProgressBar loadProgressBar;
	private ImageView arrowImageView;
	private ProgressBar progressBar;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecored;

	private int headContentWidth;
	private int headContentHeight;

	private int startY;
	private int firstItemIndex;

	private int state;

	private boolean isBack;

	private OnRefreshListener refreshListener;
	private OnAddMoreListener addMoreListener;
	private boolean isRefreshable;

	public MyListView(Context context) {
		super(context);
		init(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		setCacheColorHint(context.getResources().getColor(R.color.transparent));
		inflater = LayoutInflater.from(context);
		headView = (LinearLayout) inflater.inflate(R.layout.mylist_head_view,
				null);
		footView = (LinearLayout) inflater.inflate(R.layout.mylist_foot_view,
				null);
		moreTextView = (TextView) footView.findViewById(R.id.more_id);
		loadProgressBar = (ProgressBar) footView.findViewById(R.id.load_id);
		arrowImageView = (ImageView) headView
				.findViewById(R.id.head_arrowImageView);
		arrowImageView.setMinimumWidth(70);
		arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) headView
				.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView
				.findViewById(R.id.head_lastUpdatedTextView);

		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();

		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

		Log.v("size", "width:" + headContentWidth + " height:"
				+ headContentHeight);
		addHeaderView(headView, null, false);
		footView.setVisibility(View.GONE);
		measureView(footView);
		addFooterView(footView, null, false);
		setOnScrollListener(this);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		state = DONE;
		isRefreshable = false;
	}

	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,
			int arg3) {
		firstItemIndex = firstVisiableItem;
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// Log.v(TAG, "MyListView-->onScrollStateChanged" );
		switch (scrollState) {
		// 当不滚动时
		case OnScrollListener.SCROLL_STATE_IDLE:
			Log.i(TAG, "SCROLL_STATE_IDLE");
			if (view.getFirstVisiblePosition() == 0) {
				Log.i(TAG, "判断滚动到顶部");

			} else if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
				Log.i(TAG, "判断滚动到底部");
				// 进行刷新的展示,让progress显示出来
				if (ishas) {
					loadProgressBar.setVisibility(View.VISIBLE);
					footView.setVisibility(View.VISIBLE);
					moreTextView.setText(" 加载中...");
					// 在这里获取数据
					addMoreListener.onAddMore();
				} else {
					// footView.setVisibility(View.VISIBLE);
					loadProgressBar.setVisibility(View.GONE);
					// moreTextView.setText("全部加载完成");
				}
			}
			break;
		case OnScrollListener.SCROLL_STATE_FLING:
			Log.i(TAG, "SCROLL_STATE_FLING");
			if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
				Log.i(TAG, "判断滚动到底部");
				// 进行刷新的展示,让progress显示出来
				if (ishas) {
					loadProgressBar.setVisibility(View.VISIBLE);
					footView.setVisibility(View.VISIBLE);
					moreTextView.setText(" 加载中...");
					// 在这里获取数据
					addMoreListener.onAddMore();
				} else {
					// footView.setVisibility(View.VISIBLE);
					loadProgressBar.setVisibility(View.GONE);
					// moreTextView.setText("全部加载完成");
				}
			}
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
				Log.i(TAG, "判断滚动到底部");
				// 进行刷新的展示,让progress显示出来
				if (ishas) {
					loadProgressBar.setVisibility(View.VISIBLE);
					footView.setVisibility(View.VISIBLE);
					moreTextView.setText(" 加载中...");
					// 在这里获取数据
					addMoreListener.onAddMore();
				} else {
					// footView.setVisibility(View.VISIBLE);
					loadProgressBar.setVisibility(View.GONE);
					// moreTextView.setText("全部加载完成");
				}
			}
			break;
		}

	}

	public boolean onTouchEvent(MotionEvent event) {

		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startY = (int) event.getY();
					Log.v(TAG, "在down时候记录当前位置‘");
				}
				break;

			case MotionEvent.ACTION_UP:

				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
						// 什么都不做
					}
					if (state == PULL_To_REFRESH) {
						state = DONE;
						changeHeaderViewByState();

						Log.v(TAG, "由下拉刷新状态，到done状态");
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						onRefresh();

						Log.v(TAG, "由松开刷新状态，到done状态");
					}
				}

				isRecored = false;
				isBack = false;

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();

				if (!isRecored && firstItemIndex == 0) {
					Log.v(TAG, "在move时候记录下位置");
					isRecored = true;
					startY = tempY;
				}

				if (state != REFRESHING && isRecored && state != LOADING) {

					// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

					// 可以松手去刷新了
					if (state == RELEASE_To_REFRESH) {

						setSelection(0);

						// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
						if (((tempY - startY) / RATIO < headContentHeight)
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();

							Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
						}
						// 一下子推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();

							Log.v(TAG, "由松开刷新状态转变到done状态");
						}
						// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
						else {
							// 不用进行特别的操作，只用更新paddingTop的值就行了
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (state == PULL_To_REFRESH) {

						setSelection(0);

						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - startY) / RATIO >= headContentHeight) {
							state = RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();

							Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
						}
						// 上推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();

							Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
						}
					}

					// done状态下
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}

					// 更新headView的size
					if (state == PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);

					}

					// 更新headView的paddingTop
					if (state == RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
					}

				}

				break;
			}
		}

		return super.onTouchEvent(event);
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);

			tipsTextview.setText("松开刷新");

			Log.v(TAG, "当前状态，松开刷新");
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);

				tipsTextview.setText("下拉刷新");
			} else {
				tipsTextview.setText("下拉刷新");
			}
			Log.v(TAG, "当前状态，下拉刷新");
			break;

		case REFRESHING:

			headView.setPadding(0, 0, 0, 0);

			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText("正在刷新...");
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			Log.v(TAG, "当前状态,正在刷新...");
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			tipsTextview.setText("下拉刷新");
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			Log.v(TAG, "当前状态，done");
			break;
		}
	}

	boolean ishas = true;

	public void onScrollComplete(Boolean has) {
		ishas = has;
		if (has) {

			loadProgressBar.setVisibility(View.VISIBLE);
			footView.setVisibility(View.GONE);
			moreTextView.setText(" 加载中...");
		} else {
			loadProgressBar.setVisibility(View.GONE);
			moreTextView.setText("全部加载完成");
		}
	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public OnAddMoreListener getAddMoreListener() {
		return addMoreListener;
	}

	public void setAddMoreListener(OnAddMoreListener addMoreListener) {
		this.addMoreListener = addMoreListener;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public interface OnAddMoreListener {
		public void onAddMore();
	}

	public void onRefreshComplete() {
		state = DONE;
		Calendar calendar = Calendar.getInstance();
		String time = calendar.get(Calendar.YEAR) + "/"  
	            + (calendar.get(Calendar.MONTH)+1) + "/"//从0计算  
	            + calendar.get(Calendar.DAY_OF_MONTH) + " "  
	            + calendar.get(Calendar.HOUR_OF_DAY) + ":" +
	            + calendar.get(Calendar.MINUTE) + ":"+calendar.get(Calendar.SECOND); 
		lastUpdatedTextView.setText("最近更新:" + time);
		changeHeaderViewByState();
	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	// 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
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
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setAdapter(BaseAdapter adapter) {
		lastUpdatedTextView.setText("最近更新:" + new Date().toLocaleString());
		super.setAdapter(adapter);
	}
}
