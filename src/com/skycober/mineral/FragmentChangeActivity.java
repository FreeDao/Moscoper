package com.skycober.mineral;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.skycober.mineral.account.HomePageActivity;
import com.skycober.mineral.product.AppHomePageActivity;
import com.skycober.mineral.product.CategoryReviewActivity;
import com.skycober.mineral.product.MyAttentionProductActivity;
import com.skycober.mineral.product.MySendProductActivity;
import com.skycober.mineral.product.TwoDimensionActivity;
import com.skycober.mineral.util.AndroidIdUtil;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.util.Trace;
import com.skycober.mineral.util.AndroidIdUtil.onAndroidIdGetFailure;
import com.skycober.mineral.widget.MyRemDialog;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

public class FragmentChangeActivity extends SlidingFragmentActivity {
	private static final String TAG = "FragmentChangeActivity";

	private Fragment mContent;
	public static SlidingMenu slideMenu;

	public static MenuFragment leftFragment;

	private FeedbackAgent agent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set the Above View
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new AppHomePageActivity();// 首页

		// set the Above View
		setContentView(R.layout.content_frame);
		MyApplication.getInstance().addActivity(this);
		Intent intent = getIntent();
		String action = intent.getAction();

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();

		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		leftFragment = new MenuFragment();// 侧滑菜单
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, leftFragment).commit();

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slideMenu = sm;
		slideMenu.setOnOpenedListener(btnOpenedClickListener);
		slideMenu.setOnOpenListener(onOpenListener);

		isLogin = !SettingUtil
				.getInstance(FragmentChangeActivity.this)
				.getValue(SettingUtil.KEY_LOGIN_USER_ID,
						SettingUtil.DEFAULT_LOGIN_USER_ID)
				.equalsIgnoreCase(SettingUtil.DEFAULT_LOGIN_USER_ID);
		if (isLogin) {
			// leftFragment.refreshLoginState(isLogin);
		}
		if (null == agent) {
			agent = new FeedbackAgent(this);
		}
		agent.sync();
		// UmengUpdateAgent.update(this);
		if (intent.hasExtra("ISPUSH")) {
			if (intent.getExtras().getBoolean("ISPUSH")) {
				switchContent(new MyAttentionProductActivity());
			}

		}

		if (intent.hasExtra("isLogin")) {
			if (intent.getStringExtra("isLogin").equals("login")) {
				if(leftFragment != null){
					leftFragment.refreshLoginState(true);
					slideMenu.toggle();
				}
				
			}
		}

	}

	private OnOpenListener onOpenListener = new OnOpenListener() {

		@Override
		public void onOpen() {
			isLogin = !SettingUtil
					.getInstance(FragmentChangeActivity.this)
					.getValue(SettingUtil.KEY_LOGIN_USER_ID,
							SettingUtil.DEFAULT_LOGIN_USER_ID)
					.equalsIgnoreCase(SettingUtil.DEFAULT_LOGIN_USER_ID);
			if (null != leftFragment) {
				// leftFragment.switchLoginState(isLogin);
			}
		}

	};
	private SlidingMenu.OnOpenedListener btnOpenedClickListener = new SlidingMenu.OnOpenedListener() {

		@Override
		public void onOpened() {
			if (null != leftFragment && isLogin) {
				// leftFragment.refreshLoginState(isLogin);
			}
		}
	};

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}

	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		getSlidingMenu().showContent();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		leftFragment.refreshLoginState(true);

		Trace.d(TAG, "requestCode:" + requestCode + ",resultCode:" + resultCode
				+ ",data:" + (null == data ? "null" : data.toString()));
		if (!slideMenu.isMenuShowing()
				&& mContent instanceof MySendProductActivity) {
			mContent.onActivityResult(requestCode, resultCode, data);
			return;
		}
		if (!slideMenu.isMenuShowing()
				&& mContent instanceof MyAttentionProductActivity) {
			mContent.onActivityResult(requestCode, resultCode, data);
			return;
		}
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
		case MenuFragment.REQUEST_CODE_LOGIN:// 登录成功
			// 更新当前页面元素
			if (null != leftFragment) {
				leftFragment.refreshLoginState(true);
			}
			break;
		case MenuFragment.REQUEST_CODE_SETTING:
			// 更新当前页面元素
			if (null != leftFragment) {
				leftFragment.refreshLoginState(true);
			}
			break;

		default:

			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (slideMenu.isMenuShowing()) {
				leftFragment.refreshLoginState(true);

				showExitDialog();
			} else if (slideMenu.getTag().equals("back")) {
				leftFragment.refreshLoginState(true);

				slideMenu.toggle();
			} else {
				leftFragment.refreshLoginState(true);

				slideMenu.toggle();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	private MyRemDialog exitDialog;

	// 退出应用对话框
	private void showExitDialog() {
		if (null == exitDialog) {
			exitDialog = new MyRemDialog(this, R.style.Dialog);
			exitDialog.setTitle(R.string.exit_app_title);
			exitDialog.setMessage(R.string.exit_app_rem);
			exitDialog.setPosBtnText(R.string.exit_app_dialog_btn_exit);
			exitDialog.setNegBtnText(R.string.exit_app_dialog_btn_cancel);
			exitDialog.setPosBtnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MyApplication.getInstance().exitActivity();
				}
			});
		}
		if (null != exitDialog && !isFinishing() && !exitDialog.isShowing()) {
			exitDialog.show();
		}
	}

	private boolean isLogin = false;
}
