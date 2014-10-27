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
 * ����������ˢ����������ҳ��GridView.
 *
 * @author zhaoqp
 * @date��2013-2-19 ����10:58:51
 * @version v1.0
 */
public class AbPullToRefreshGridNoHanderView extends AbGridView implements OnScrollListener,OnTouchListener {

	/** The tag. */
	private String TAG = "AbPullToRefreshGridView";
	
	/** ���ֲ���. */
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
	
	/** ����ˢ��. */
	private final static int REFRESHING = 2;
	
	/** ˢ�����. */
	private final static int DONE = 3;
	
	/** The Constant LOADING. */
	private final static int LOADING = 4;
	
	/** The Constant RATIO. */
	private final static int RATIO = 3;
	
	/** The m grid view. */
	private GridView mGridView = null;
	
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
	
	/** �ײ�ˢ����. */
	private LinearLayout footerView;
	
	/** The footer textview. */
	private TextView footerTextview;
	
	/** The footer progress bar. */
	private ProgressBar footerProgressBar;
	
	/** ������ʱ��ʾ��һ��. */
	private LinearLayout nodataView;
	
	/** �������. */
	private BaseAdapter mAdapter = null;
	
	/** The net get. */
	private AbHttpQueue netGet = null;
	
	/** The item refresh. */
	private AbHttpItem itemRefresh = null;
	
	/** The item scroll. */
	private AbHttpItem itemScroll = null;

	/** ����������. */
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
	
	/** The is refreshable. */
	private boolean isRefreshable;
	
	//private int i = 1;
	
	//������һ�ε�ˢ��ʱ��
	/** The last refresh time. */
	private String lastRefreshTime = null;

	/**
	 * Instantiates a new ab pull to refresh grid view.
	 *
	 * @param context the context
	 */
	public AbPullToRefreshGridNoHanderView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * Instantiates a new ab pull to refresh grid view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public AbPullToRefreshGridNoHanderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * ��ʼ������.
	 *
	 * @param context the context
	 */
	private void init(Context context) {
		mGridView = this.getGridView();
		mGridView.setCacheColorHint(context.getResources().getColor(android.R.color.transparent));
		//Ĭ��ֵ �Զ�����Ե�Activity���޸�
		mGridView.setColumnWidth(100);
		mGridView.setGravity(Gravity.CENTER);
		mGridView.setHorizontalSpacing(5);
		AlphaAnimation animationAlpha = new AlphaAnimation(0.0f,1.0f);  
	    //�õ�һ��LayoutAnimationController����;
	    LayoutAnimationController lac = new LayoutAnimationController(animationAlpha);
	    //���ÿؼ���ʾ��˳��;
	    lac.setOrder(LayoutAnimationController.ORDER_RANDOM);
	    //���ÿؼ���ʾ���ʱ��;
	    lac.setDelay(0.5f);
	    //ΪView����LayoutAnimationController����;
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
		
		
		//��ʾ��ͷ�����
		FrameLayout headImage =  new FrameLayout(context);
		arrowImageView = new ImageView(context);
		//�Ӱ����ȡ�ļ�ͷͼƬ
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
		
		//����ˢ�����ı�����
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
		
		//�ײ�ˢ��
		footerView  = new LinearLayout(context);  
		//���ò��� ˮƽ����  
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
		footerTextview.setText("����..."); 
		footerView.setVisibility(View.GONE);
		
		measureView(footerView);
		footContentHeight = footerView.getMeasuredHeight();
		footContentWidth = footerView.getMeasuredWidth();
		footerView.setPadding(0,0, 0, 0);
		footerView.invalidate();
		addFooterView(footerView);
		
		//������ʱ��View
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
		
		//����ˢ��,����������������
	}

	/**
	 * ������TODO.
	 *
	 * @param arg0 the arg0
	 * @param firstVisiableItem the first visiable item
	 * @param visibleItemCount the visible item count
	 * @param totalItemCount the total item count
	 * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.AbsListView, int, int, int)
	 * @author: zhaoqp
	 * @date��2013-6-17 ����9:04:48
	 * @version v1.0
	 */
	public void onScroll(AbsListView arg0, int firstVisiableItem, int visibleItemCount,int totalItemCount) {
		firstItemIndex = firstVisiableItem;
		//int lastItem = firstVisiableItem + visibleItemCount - 1;  
		//Log.v(TAG, "MyListView-->onScroll" );
	}
	
	/**
	 * ������TODO.
	 *
	 * @param view the view
	 * @param scrollState the scroll state
	 * @see android.widget.AbsListView.OnScrollListener#onScrollStateChanged(android.widget.AbsListView, int)
	 * @author: zhaoqp
	 * @date��2013-6-17 ����9:04:48
	 * @version v1.0
	 */
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		//Log.i(TAG, "view.getFirstVisiblePosition() = "+view.getFirstVisiblePosition());
		switch (scrollState) {
		    // ��������ʱ
		    case OnScrollListener.SCROLL_STATE_IDLE:
		    	Log.i(TAG, "SCROLL_STATE_IDLE");
		    	if(view.getFirstVisiblePosition() == 0){
		    		Log.i(TAG, "�жϹ���������");
		    	}else if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
		    		Log.i(TAG, "�жϹ������ײ�");
					//����ˢ�µ�չʾ,��progress��ʾ����
			    	footerProgressBar.setVisibility(View.VISIBLE);
			    	footerView.setVisibility(View.VISIBLE);
			    	footerTextview.setText(" ������..."); 
					//�������ȡ����
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
	 * ������TODO.
	 *
	 * @param v the v
	 * @param event the event
	 * @return true, if successful
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 * @author: zhaoqp
	 * @date��2013-6-17 ����9:04:48
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
					//Log.v(TAG, "ACTION_DOWN ���ǵ�  "+i+++"��" +1 );
				}
				break;
			case MotionEvent.ACTION_UP:
				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
					}
					if (state == PULL_To_REFRESH) {
						state = DONE;
						//Log.v(TAG, "ACTION_UP PULL_To_REFRESH and changeHeaderViewByState()" +" ���ǵ�  "+i+++"��ǰ"+2 );
						//Log.v(TAG, "ACTION_UP PULL_To_REFRESH and changeHeaderViewByState() " +"���ǵ�  "+i+++"����"+2 );
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						//Log.v(TAG, "ACTION_UP RELEASE_To_REFRESH changeHeaderViewByState() " +"���ǵ�  "+i+++"��" +3);
						//Log.v(TAG, "ACTION_UP RELEASE_To_REFRESH changeHeaderViewByState()" +" ���ǵ�  "+i+++"��" +3);
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
					//Log.v(TAG, "ACTION_MOVE ���ǵ�  "+i+++"��" +4);
				}
				if (state != REFRESHING && isRecored && state != LOADING) {
					if (state == RELEASE_To_REFRESH) {
						mGridView.setSelection(0);
						if (((tempY - startY) / RATIO < headContentHeight)&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							//Log.v(TAG, "changeHeaderViewByState() ���ǵ�  "+i+++"��"+5 );
						}else if (tempY - startY <= 0) {
							state = DONE;
							//Log.v(TAG, "ACTION_MOVE RELEASE_To_REFRESH 2  changeHeaderViewByState " +"���ǵ�  "+i+++"��" +6);
						}
					}
					if (state == PULL_To_REFRESH) {
						mGridView.setSelection(0);
						if ((tempY - startY) / RATIO >= headContentHeight) {
							state = RELEASE_To_REFRESH;
							isBack = true;
							//Log.v(TAG, "changeHeaderViewByState " +"���ǵ�  "+i+++"��ǰ"+7 );
							//Log.v(TAG, "changeHeaderViewByState " +"���ǵ�  "+i+++"����"+7 );
						}else if (tempY - startY <= 0) {
							state = DONE;
							//Log.v(TAG, "ACTION_MOVE changeHeaderViewByState PULL_To_REFRESH 2" +" ���ǵ�  "+i+++"��" +8);
						}
					}
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							//Log.v(TAG, "ACTION_MOVE DONE changeHeaderViewByState " +"���ǵ�  "+i+++"��ǰ" +9);
							//Log.v(TAG, "ACTION_MOVE DONE changeHeaderViewByState " +"���ǵ�  "+i+++"����" +9);
						}
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

	/**
	 * Sets the on refresh listener.
	 *
	 * @param refreshListener the new on refresh listener
	 */

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

	/**
	 * ����������������ɱ��뱻ListView�������ã���ʾheaderView.
	 */
	
	/**
	 * ����������������������ɱ��뱻ListView�������ã���ʾfooterView.
	 *
	 * @param have �Ƿ���������
	 */
	public void onScrollComplete(int have) {
		if(have == AbConstant.HAVE){
			mAdapter.notifyDataSetChanged();
			footerProgressBar.setVisibility(View.VISIBLE);
	    	footerView.setVisibility(View.GONE);
	    	footerTextview.setText(" ������..."); 
		}else if(have == AbConstant.NOTHAVE){
			footerProgressBar.setVisibility(View.GONE);
			footerTextview.setText("ȫ���������");
		}
	}

	/**
	 * On refresh.
	 */

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
	 * ���������������б��������.
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
	 * ����������ˢ���¼�����.
	 *
	 * @param item the new refresh item
	 */
	public void setRefreshItem(AbHttpItem item) {
		itemRefresh = item;
	}
	
	/**
	 * ����������ˢ���¼�����.
	 *
	 * @param item the new scroll item
	 */
	public void setScrollItem(AbHttpItem item) {
		itemScroll = item;
	}

	/**
	 * ������TODO.
	 *
	 * @return the grid view
	 * @see com.ab.view.AbGridView#getGridView()
	 * @author: zhaoqp
	 * @date��2013-6-17 ����9:04:48
	 * @version v1.0
	 */
	@Override
	public GridView getGridView() {
		return super.getGridView();
	}
	
	
}
