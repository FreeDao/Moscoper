package com.skycober.mineral.widget;

import com.skycober.mineral.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SwitchBar extends LinearLayout {
	// private static final String TAG = SwitchBar.class.getSimpleName();
	private TextView tvLeft;
	private TextView tvRight;

	private TranslateAnimation transLeftAnim;
	private TranslateAnimation transRightAnim;

	private View.OnClickListener tabClickListener;

	private OnTabSelectListener mOnTabSelectListener;
	private int selectPos;

	public int getSelectPos() {
		return selectPos;
	}

	public void setSelectPos(int selectPos) {
		if (this.selectPos != selectPos) {
			this.selectPos = selectPos;
			Select(selectPos);
		}
	}

	public OnTabSelectListener getOnTabSelectListener() {
		return mOnTabSelectListener;
	}

	public void setOnTabSelectListener(OnTabSelectListener mOnTabSelectListener) {
		this.mOnTabSelectListener = mOnTabSelectListener;
	}

	public SwitchBar(Context context) {
		super(context);
		Init(context);
	}

	public SwitchBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		Init(context);
	}

	private void Init(Context context) {
		this.tabClickListener = new OnTabClickListener(this);
		ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.switch_bar, this, true);
		this.tvLeft = (TextView) root.findViewById(R.id.switch1);
		this.tvLeft.setOnClickListener(tabClickListener);
		this.tvLeft.setText(R.string.sub_tab_published);
		this.tvRight = (TextView) root.findViewById(R.id.switch2);
		this.tvRight.setText(R.string.sub_tab_unpublished);
		this.tvRight.setOnClickListener(tabClickListener);
		this.tvLeft.setTextColor(Color.WHITE);
		this.tvRight.setTextColor(Color.WHITE);
		Select(1);
	}

	public void Select(int pos) {
		boolean hasChanged = false;
		switch (pos) {
		case 1:
			this.tvLeft.setBackgroundResource(R.color.blue);
			this.tvRight.setBackgroundResource(R.drawable.color_transparent);
			hasChanged = true;
			break;
		case 2:
			this.tvLeft.setBackgroundResource(R.drawable.color_transparent);
			this.tvRight.setBackgroundResource(R.color.blue);
			hasChanged = true;
			break;
		default:
			return;
		}
		if (null != this.mOnTabSelectListener && hasChanged) {
			this.mOnTabSelectListener.onTabSelect(pos);
		}
	}

	public void TransLeft(Animation.AnimationListener listener, View view) {
		int switchBarHeight = 1 * getContext().getResources()
				.getDimensionPixelSize(R.dimen.switch_bar_height);
		if (this.transRightAnim == null) {
			float yFromDelta = -1 * switchBarHeight;
			this.transRightAnim = new TranslateAnimation(0.0F, 0.0F,
					yFromDelta, 0.0F);
			this.transRightAnim.setDuration(300L);
			TransRightAnimListener trListener = new TransRightAnimListener(
					this, listener, view, switchBarHeight);
			transRightAnim.setAnimationListener(trListener);
		}
		startAnimation(this.transRightAnim);
	}

	public void TransRight(Animation.AnimationListener listener, View view) {
		int switchBarHeight = getContext().getResources()
				.getDimensionPixelSize(R.dimen.switch_bar_height);
		if (this.transLeftAnim == null) {
			float toYDelta = -switchBarHeight;
			this.transLeftAnim = new TranslateAnimation(0.0F, 0.0F, 0.0F,
					toYDelta);
			this.transLeftAnim.setDuration(300L);
			TransLeftAnimListener tlListener = new TransLeftAnimListener(this,
					listener, view, switchBarHeight);
			this.transLeftAnim.setAnimationListener(tlListener);
		}
		startAnimation(this.transLeftAnim);
	}

	public class OnTabClickListener implements View.OnClickListener {
		public OnTabClickListener(SwitchBar switchBar) {

		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.switch1:
				Select(1);
				return;
			case R.id.switch2:
				Select(2);
				return;
			default:
				return;
			}
		}
	}

	public class TransRightAnimListener implements Animation.AnimationListener {
		Animation.AnimationListener listener;
		View view;
		SwitchBar parent;
		int xFromDelta;

		public TransRightAnimListener(SwitchBar switchBar,
				Animation.AnimationListener listener, View view, int xFromDelta) {
			this.listener = listener;
			this.view = view;
			this.parent = switchBar;
			this.xFromDelta = xFromDelta;
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (null != this.listener)
				this.listener.onAnimationEnd(animation);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			if (null != this.listener)
				this.listener.onAnimationRepeat(animation);
		}

		@Override
		public void onAnimationStart(Animation animation) {
			if (null != this.listener)
				this.listener.onAnimationStart(animation);
			if (null != this.parent)
				this.parent.setVisibility(View.VISIBLE);
			if (null == view)
				return;
			TransAnim anim = new TransAnim(view, xFromDelta, 0);
			anim.setDuration(320L);
			this.view.startAnimation(anim);
		}
	}

	public class TransAnim extends Animation {
		private View view;
		private int xFromDelta;
		private int xToDelta;
		private int topMargin;
		private RelativeLayout.LayoutParams params;

		public TransAnim(View view, int xFromDelta, int xToDelta) {
			this.view = view;
			this.xFromDelta = xFromDelta;
			this.xToDelta = xToDelta;
			this.params = (android.widget.RelativeLayout.LayoutParams) view
					.getLayoutParams();
			this.topMargin = this.params.topMargin;
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			if (interpolatedTime < 1.0F) {
				int k = 0;
				if (this.xToDelta == 1) {
					k = this.topMargin
							- (int) (this.xFromDelta * interpolatedTime);
				}
				RelativeLayout.LayoutParams tempParams = null;
				int tmpTopMargin;
				for (this.params.topMargin = k;; tempParams.topMargin = tmpTopMargin) {
					tempParams = this.params;
					tmpTopMargin = this.topMargin
							+ (int) (this.xFromDelta * interpolatedTime);
				}
			}
			if (this.xToDelta == 1) {
				this.params.topMargin = this.topMargin - this.xFromDelta;
			} else {
				this.params.topMargin = this.topMargin + this.xFromDelta;
			}
			this.view.requestLayout();
		}
	}

	public class TransLeftAnimListener implements Animation.AnimationListener {
		Animation.AnimationListener listener;
		View view;
		SwitchBar parent;
		int xFromDelta;

		public TransLeftAnimListener(SwitchBar switchBar,
				Animation.AnimationListener listener, View view, int xFromDelta) {
			this.listener = listener;
			this.view = view;
			this.parent = switchBar;
			this.xFromDelta = xFromDelta;
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (null != this.listener)
				this.listener.onAnimationEnd(animation);
			// this.parent.setVisibility(View.INVISIBLE);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			if (null != this.listener)
				this.listener.onAnimationRepeat(animation);
		}

		@Override
		public void onAnimationStart(Animation animation) {
			if (null != this.listener)
				this.listener.onAnimationStart(animation);
			if (null == view)
				return;
			TransAnim anim = new TransAnim(view, xFromDelta, 1);
			anim.setDuration(280L);
			this.view.startAnimation(anim);
		}
	}

	public interface OnTabSelectListener {
		void onTabSelect(int pos);
	}

	@Override
	protected void onDetachedFromWindow() {
		clearAnimation();
		super.onDetachedFromWindow();
	}
}
