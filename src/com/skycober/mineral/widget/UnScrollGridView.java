package com.skycober.mineral.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

public class UnScrollGridView extends GridView {
	public UnScrollGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		setVerticalSpacing(16);
	}

	public UnScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public UnScrollGridView(Context context) {
		super(context);
		init();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		System.out.println("touchEvent:" + event.getAction());
		switch (event.getAction()) {
		// 按下
		case MotionEvent.ACTION_DOWN:
			System.out.println("touch...down:" + isLongClickable());
			return super.onTouchEvent(event);
			// 滑动
		case MotionEvent.ACTION_MOVE:
			System.out.println("touch...move" + isLongClickable());
			break;
		// 离开
		case MotionEvent.ACTION_UP:
			System.out.println("touch...up" + isLongClickable());
			return super.onTouchEvent(event);
		default:
			return super.onTouchEvent(event);
		}
		return false;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
