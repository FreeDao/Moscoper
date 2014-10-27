package com.skycober.mineral.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.FragBaseActivity;
import com.skycober.mineral.FragmentChangeActivity;
import com.skycober.mineral.MenuFragment;
import com.skycober.mineral.R;
import com.skycober.mineral.account.HomePageActivity;
import com.skycober.mineral.account.LoginActivity;
import com.skycober.mineral.updateApk.UpDataApkAcitvity;
import com.skycober.mineral.util.NetworkUtil;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.util.Trace;
import com.skycober.mineral.widget.MyRemDialog;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * 设置页
 * （程序设置）
 * @author 新彬
 * 
 */
public class SettingActivity extends FragBaseActivity {
	private static final String TAG = "SettingActivity";

	private ViewGroup pushRootGroup, pushGroup, updatePwdRootGroup,
			updatePwdGroup, versionCheckGroup, feedBackGroup, aboutUsGroup,
			loginGroup, logOutGroup, qrCodeRootGroup, qrCodeGroup,
			userInfoGroup;

	private View mRoot;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRoot = inflater.inflate(R.layout.activity_setting, null);
		pushRootGroup = (ViewGroup) mRoot.findViewById(R.id.group_1);
		TextView tvTitle = (TextView) mRoot.findViewById(R.id.title_text);
		tvTitle.setText(R.string.setting_page_title);
		ImageButton btnLeft = (ImageButton) mRoot
				.findViewById(R.id.title_button_left);
		btnLeft.setOnClickListener(btnListClickListener);
		ImageButton btnRight = (ImageButton) mRoot
				.findViewById(R.id.title_button_right);
		btnRight.setVisibility(View.INVISIBLE);

		//接受推送
		pushGroup = (ViewGroup) mRoot.findViewById(R.id.settings_enable_push);
		pushGroup.setBackgroundResource(R.drawable.settings_item_selector);
		
		//修改账户密码
		updatePwdRootGroup = (ViewGroup) mRoot.findViewById(R.id.group_2);
		updatePwdGroup = (ViewGroup) mRoot
				.findViewById(R.id.settings_update_password);
		// updatePwdGroup.setBackgroundResource(R.drawable.settings_item_selector);
		
		//扫一扫
		qrCodeRootGroup = (ViewGroup) mRoot.findViewById(R.id.group_3);
		qrCodeGroup = (ViewGroup) mRoot.findViewById(R.id.settings_qrcode);
		qrCodeGroup.setBackgroundResource(R.drawable.settings_item_selector);

		//检查更新
		versionCheckGroup = (ViewGroup) mRoot
				.findViewById(R.id.settings_check_update);
		// versionCheckGroup.setBackgroundResource(R.drawable.settings_item_top_selector);
		
		//意见反馈
		feedBackGroup = (ViewGroup) mRoot.findViewById(R.id.settings_feedback);
		// feedBackGroup.setBackgroundResource(R.drawable.settings_item_middle_selector);
		
		//关于
		aboutUsGroup = (ViewGroup) mRoot.findViewById(R.id.settings_about);
		// aboutUsGroup.setBackgroundResource(R.drawable.settings_item_bottom_selector);
		
		//登陆
		loginGroup = (ViewGroup) mRoot.findViewById(R.id.settings_login);
		loginGroup.setBackgroundResource(R.drawable.settings_item_selector);
		
		//退出登录
		logOutGroup = (ViewGroup) mRoot.findViewById(R.id.settings_logout);
		logOutGroup.setBackgroundResource(R.drawable.settings_item_selector);
		
		//个人中心
		userInfoGroup = (ViewGroup) mRoot.findViewById(R.id.user_info);
		userInfoGroup.setBackgroundResource(R.drawable.settings_item_selector);
		InitDescriptionInfo();
		return mRoot;
	}

	private View.OnClickListener btnListClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			FragmentChangeActivity.leftFragment.refreshLoginState(true);
			FragmentChangeActivity.slideMenu.toggle();
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private CheckBox pushCheckBox;

	//初始化控件
	private void InitDescriptionInfo() {
		//接受推送
		ImageView ivPush = (ImageView) pushGroup
				.findViewById(R.id.menu_item_icon);
		ivPush.setImageResource(R.drawable.menu_icon_push);
		TextView tvPush = (TextView) pushGroup
				.findViewById(R.id.menu_item_name);
		tvPush.setText(R.string.setting_item_push);
		ImageView ivRightArrow = (ImageView) pushGroup
				.findViewById(R.id.menu_item_arrow);
		ivRightArrow.setVisibility(View.GONE);
		pushCheckBox = (CheckBox) pushGroup.findViewById(R.id.menu_item_switch);
		pushCheckBox.setVisibility(View.VISIBLE);
		pushCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);

		//修改账户密码
		ImageView ivUpdatePwd = (ImageView) updatePwdGroup
				.findViewById(R.id.menu_item_icon);
		ivUpdatePwd.setImageResource(R.drawable.menu_icon_star);
		TextView tvUpdatePwd = (TextView) updatePwdGroup
				.findViewById(R.id.menu_item_name);
		tvUpdatePwd.setText(R.string.setting_item_update_pwd);
		updatePwdGroup.setOnClickListener(btnUpdatePwdClickListener);

		//扫一扫
		ImageView ivQRCode = (ImageView) qrCodeGroup
				.findViewById(R.id.menu_item_icon);
		ivQRCode.setImageResource(R.drawable.menu_icon_qrcode);
		TextView tvQRCode = (TextView) qrCodeGroup
				.findViewById(R.id.menu_item_name);
		tvQRCode.setText(R.string.setting_item_qrcode);
		qrCodeGroup.setOnClickListener(btnQRCodeClickListener);

		//检查更新
		ImageView ivCheck = (ImageView) versionCheckGroup
				.findViewById(R.id.menu_item_icon);
		ivCheck.setImageResource(R.drawable.menu_icon_update);
		TextView tvCheck = (TextView) versionCheckGroup
				.findViewById(R.id.menu_item_name);
		tvCheck.setText(R.string.setting_item_check_update);
		versionCheckGroup.setOnClickListener(btnVersionCheckClickListener);

		//意见反馈
		ImageView ivFeedBack = (ImageView) feedBackGroup
				.findViewById(R.id.menu_item_icon);
		ivFeedBack.setImageResource(R.drawable.menu_icon_feedback);
		TextView tvFeedBack = (TextView) feedBackGroup
				.findViewById(R.id.menu_item_name);
		tvFeedBack.setText(R.string.setting_item_feedback);
		feedBackGroup.setOnClickListener(btnFeedBackClickListener);

		//关于
		ImageView ivAboutUs = (ImageView) aboutUsGroup
				.findViewById(R.id.menu_item_icon);
		ivAboutUs.setImageResource(R.drawable.menu_icon_about);
		TextView tvAbout = (TextView) aboutUsGroup
				.findViewById(R.id.menu_item_name);
		tvAbout.setText(R.string.setting_item_about);
		aboutUsGroup.setOnClickListener(btnAboutUsClickListener);

		//登陆
		ImageView ivLogin = (ImageView) loginGroup
				.findViewById(R.id.menu_item_icon);
		ivLogin.setImageResource(R.drawable.menu_icon_login);
		TextView tvLogin = (TextView) loginGroup
				.findViewById(R.id.menu_item_name);
		tvLogin.setText(R.string.setting_item_login);
		loginGroup.setOnClickListener(btnLoginClickListener);
		
		//个人中心
		ImageView ivUserInfo = (ImageView) userInfoGroup
				.findViewById(R.id.menu_item_icon);
		ivUserInfo.setImageResource(R.drawable.menu_icon_login);
		TextView tvUserInfo = (TextView) userInfoGroup
				.findViewById(R.id.menu_item_name);
		tvUserInfo.setText(R.string.sidebar_btn_user_text);
		userInfoGroup.setOnClickListener(btnuserInfoClickListener);

		//退出登录
		ImageView ivLogOut = (ImageView) logOutGroup
				.findViewById(R.id.menu_item_icon);
		ivLogOut.setImageResource(R.drawable.menu_icon_quit);
		TextView tvLogOut = (TextView) logOutGroup
				.findViewById(R.id.menu_item_name);
		tvLogOut.setText(R.string.setting_item_logout);
		logOutGroup.setOnClickListener(btnLogOutClickListener);
		logOutGroup.setVisibility(View.GONE);
	};

	//接受推送
	private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (!isChecked) {
				//关闭推送
				showPushNotificationRem();
			} else {
				SettingUtil.getInstance(getActivity()).saveValue(
						SettingUtil.KEY_ACCEPT_PUSH_NOTIFICATION, true);
				//打开推送
		   		JPushInterface.resumePush(SettingActivity.this.getActivity().getApplicationContext());

			}
		}
	};

	//修改账户密码
	private View.OnClickListener btnUpdatePwdClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent mIntent = new Intent(getActivity(), UpdatePwdActivity.class);
			getActivity().startActivity(mIntent);
		}
	};

	//扫一扫
	private View.OnClickListener btnQRCodeClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent mIntent = new Intent(getActivity(), SweepActivity.class);
			getActivity().startActivity(mIntent);
		}
	};

	//检查更新
	private View.OnClickListener btnVersionCheckClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(),UpDataApkAcitvity.class);
			getActivity().startActivity(intent);
			
			
			
//			if (!v.isEnabled())
//				return;
//			v.setEnabled(false);
//			UmengUpdateAgent.setUpdateOnlyWifi(false);
//			UmengUpdateAgent.setUpdateAutoPopup(true);
//			UmengUpdateAgent.update(getActivity());
//			v.setEnabled(true);
		}
	};

	//意见反馈
	private FeedbackAgent agent;
	private View.OnClickListener btnFeedBackClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			if (null == agent) {
				agent = new FeedbackAgent(getActivity());
				agent.sync();
			}
			agent.startFeedbackActivity();
			v.setEnabled(true);
		}
	};

	//关于
	private View.OnClickListener btnAboutUsClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent mIntent = new Intent(getActivity(),
					AboutSettingActivity.class);
			getActivity().startActivity(mIntent);
		}
	};

	//登陆
	private static final int REQUEST_CODE_LOGIN = 1001;
	private View.OnClickListener btnLoginClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent mIntent = new Intent(getActivity(), LoginActivity.class);
			getActivity().startActivityForResult(mIntent, REQUEST_CODE_LOGIN);
		}
	};

	//退出登录
	private View.OnClickListener btnLogOutClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			
			showLogOutRem();
		}
	};
	
	//个人中心
	private View.OnClickListener btnuserInfoClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!NetworkUtil.getInstance().existNetwork(getActivity())) {
				Toast.makeText(getActivity(), R.string.network_disable_error,
						Toast.LENGTH_SHORT).show();
				return;
			}
			Intent mIntent = new Intent(getActivity(), HomePageActivity.class);
			getActivity().startActivity(mIntent);
		}
	};

	public void onStart() {
		Trace.d(TAG, "onStart...");
		initSettingInfo();
		super.onStart();
	}

	//初始化程序设置界面
	private void initSettingInfo() {
		//KEY_LOGIN_USER_ID不等于DEFAULT_LOGIN_USER_ID说明登陆成功
		boolean isLogin = !SettingUtil
				.getInstance(getActivity())
				.getValue(SettingUtil.KEY_LOGIN_USER_ID,
						SettingUtil.DEFAULT_LOGIN_USER_ID)
				.equalsIgnoreCase(SettingUtil.DEFAULT_LOGIN_USER_ID);
		if (isLogin) {
			userInfoGroup.setVisibility(View.VISIBLE);
			pushRootGroup.setVisibility(View.VISIBLE);
			boolean isAcceptPush = SettingUtil.getInstance(getActivity())
					.getValue(SettingUtil.KEY_ACCEPT_PUSH_NOTIFICATION, true);
			pushCheckBox.setChecked(isAcceptPush);

			updatePwdRootGroup.setVisibility(View.VISIBLE);

			qrCodeRootGroup.setVisibility(View.VISIBLE);

			loginGroup.setVisibility(View.GONE);
			logOutGroup.setVisibility(View.VISIBLE);
		} else {
			userInfoGroup.setVisibility(View.GONE);

			pushRootGroup.setVisibility(View.GONE);
			updatePwdRootGroup.setVisibility(View.GONE);
			qrCodeRootGroup.setVisibility(View.GONE);
			logOutGroup.setVisibility(View.GONE);
			loginGroup.setVisibility(View.VISIBLE);
		}
	};

	private MyRemDialog pushNotificationRemDialog;

	//关闭接收推送的对话框
	private void showPushNotificationRem() {
		if (null == pushNotificationRemDialog) {
			pushNotificationRemDialog = new MyRemDialog(getActivity(),
					R.style.dialog);
			pushNotificationRemDialog.setHeaderVisility(View.GONE);
			pushNotificationRemDialog.setMessage(R.string.push_close_rem);
			pushNotificationRemDialog
					.setPosBtnText(R.string.push_close_btn_pos);
			pushNotificationRemDialog
					.setNegBtnText(R.string.push_close_btn_neg);
			pushNotificationRemDialog
					.setPosBtnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							SettingUtil.getInstance(getActivity()).saveValue(
									SettingUtil.KEY_ACCEPT_PUSH_NOTIFICATION,
									false);
							JPushInterface.stopPush(SettingActivity.this.getActivity().getApplicationContext());
						}
					});
			pushNotificationRemDialog
					.setNegBtnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							pushCheckBox.setChecked(true);
						}
					});
			pushNotificationRemDialog.setCanceledOnTouchOutside(false);
		}
		if (null != pushNotificationRemDialog && !getActivity().isFinishing()
				&& !pushNotificationRemDialog.isShowing()) {
			pushNotificationRemDialog.show();
		}
	}

	private MyRemDialog logOutRemDialog;
	//退出登录对话框
	private void showLogOutRem() {
		if (null == logOutRemDialog) {
			logOutRemDialog = new MyRemDialog(getActivity(), R.style.dialog);
			logOutRemDialog.setHeaderVisility(View.GONE);
			logOutRemDialog.setMessage(R.string.logout_rem);
			logOutRemDialog.setPosBtnText(R.string.logout_btn_pos);
			logOutRemDialog.setNegBtnText(R.string.logout_btn_neg);
			logOutRemDialog.setPosBtnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					SettingUtil.getInstance(getActivity()).clearLoginInfo();
					initSettingInfo();
					
					SettingUtil.getInstance(getActivity()).saveValue(SettingUtil.KEY_PRAISE_USER_ID, null);
					FragmentChangeActivity.leftFragment.refreshLoginState(true);
				}
			});
			logOutRemDialog.setCanceledOnTouchOutside(false);
		}
		if (null != logOutRemDialog && !getActivity().isFinishing()
				&& !logOutRemDialog.isShowing()) {
			logOutRemDialog.show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
		case REQUEST_CODE_LOGIN:
			// 更新设置界面，目前onStart内已做登录状态检测
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
