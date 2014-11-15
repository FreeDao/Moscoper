package com.skycober.mineral.product;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.skycober.mineral.FragBaseActivity;
import com.skycober.mineral.FragmentChangeActivity;
import com.skycober.mineral.MyApplication;
import com.skycober.mineral.R;
import com.skycober.mineral.account.LoginActivity;
import com.skycober.mineral.company.CompanyActivity;
import com.skycober.mineral.company.head_page_company.HomePageFragment;
import com.skycober.mineral.util.AndroidIdUtil;
import com.skycober.mineral.util.AndroidIdUtil.onAndroidIdGetFailure;
import com.skycober.mineral.util.AndroidIdUtil.onAndroidIdGetSuccess;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.widget.MyRemDialog;

/**
 * 首页两个button 首页
 * 
 * @author Yes366
 * 
 */
public class AppHomePageActivity extends FragBaseActivity {
	private ImageView requirements, information, inCompany;
	private boolean isLogin;
	private MyRemDialog logOutRemDialog;
	private ViewPager viewpage;
	private List<HomePageFragment> fragmentList;
	private Button myMoscoper;
	private TextView categoryTv;
	private String category = null;
	private boolean isMy;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	// 检测网络
	private void showLogOutRem(final AndroidIdUtil androidIdUtil) {
		if (null == logOutRemDialog) {
			logOutRemDialog = new MyRemDialog(
					AppHomePageActivity.this.getActivity(), R.style.dialog,
					false);
			logOutRemDialog.setHeaderVisility(View.GONE);
			logOutRemDialog.setMessage("连接服务器失败了，是否重试？");
			logOutRemDialog.setPosBtnText("确定");
			logOutRemDialog.setNegBtnText("取消");
			logOutRemDialog.setPosBtnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					androidIdUtil.sendAndroidId(AppHomePageActivity.this
							.getActivity());
				}
			});
			logOutRemDialog.setNegBtnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MyApplication myApp = MyApplication.getInstance();
					myApp.exitActivity();

				}
			});
			logOutRemDialog.setCanceledOnTouchOutside(false);
		}
		if (null != logOutRemDialog
				&& !AppHomePageActivity.this.getActivity().isFinishing()
				&& !logOutRemDialog.isShowing()) {
			logOutRemDialog.show();
		}
	}

	// 企业直通车
	// public void getFragment() {
	// fragmentList = new ArrayList<HomePageFragment>();
	// HomePageFragment homepagment = new HomePageFragment();
	// fragmentList.add(homepagment);
	// }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		FragmentChangeActivity.slideMenu.setTag("back");

		View v = inflater.inflate(R.layout.activity_page_home, null);
		Bundle bundle = getArguments();
		if (bundle != null) {
			category = bundle.getString("category");
		}

		// viewpage = (ViewPager) v.findViewById(R.id.companyGridView);
		// getFragment();//企业直通车
		// ViewPageAdapter viewpageAdaper = new ViewPageAdapter(getActivity()
		// .getSupportFragmentManager());
		// viewpage.setAdapter(viewpageAdaper);
		categoryTv = (TextView) v.findViewById(R.id.catgory);
		TextView tvTitle = (TextView) v.findViewById(R.id.title_text);
		tvTitle.setVisibility(View.GONE);
		v.findViewById(R.id.title_icon).setVisibility(View.VISIBLE);
		// tvTitle.setText(R.string.sidebar_home_page_text);
		ImageButton btnLeft = (ImageButton) v
				.findViewById(R.id.title_button_left);
		btnLeft.setOnClickListener(btnListClickListener);
		ImageButton btnRight = (ImageButton) v
				.findViewById(R.id.title_button_right);// 没用到
		btnRight.setVisibility(View.INVISIBLE);
		requirements = (ImageView) v.findViewById(R.id.requirements);// 我要
		requirements.setOnClickListener(requirementsClickListener);
		information = (ImageView) v.findViewById(R.id.information);
		information.setOnClickListener(informationClickListener);// 我有
		inCompany = (ImageView) v.findViewById(R.id.ruzhu);// 入住企业
		myMoscoper = (Button) v.findViewById(R.id.moscoper);

		inCompany.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isLogin = !SettingUtil
						.getInstance(getActivity())
						.getValue(SettingUtil.KEY_LOGIN_USER_ID,
								SettingUtil.DEFAULT_LOGIN_USER_ID)
						.equalsIgnoreCase(SettingUtil.DEFAULT_LOGIN_USER_ID);
				if (isLogin) {
					if (getActivity() == null)
						return;

					if (getActivity() instanceof FragmentChangeActivity) {
						Intent intent = new Intent(getActivity(),
								CompanyActivity.class);
						startActivity(intent);
						// FragmentChangeActivity fca = (FragmentChangeActivity)
						// getActivity();
						// fca.switchContent(new CompanyActivity());
					}
				} else {
					// Toast.makeText(getActivity(), "亲，请您先登录！",
					// Toast.LENGTH_SHORT).show();
					showLoginRem(R.string.review_login_rem_for_my_send);
				}

			}
		});

		myMoscoper.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (!isMy) {
					category = "myMoscoper";
					categoryTv.setVisibility(View.VISIBLE);
					isMy = true;
				} else {
					category = null;
					categoryTv.setVisibility(View.GONE);
					isMy = false;
				}

			}
		});
		final AndroidIdUtil androidIdUtil = new AndroidIdUtil();
		androidIdUtil.sendAndroidId(AppHomePageActivity.this.getActivity());
		androidIdUtil.setAndroidIdGetFailure(new onAndroidIdGetFailure() {

			@Override
			public void onFailure() {
				// TODO Auto-generated method stub
				showLogOutRem(androidIdUtil);
			}
		});
		androidIdUtil.setAndroidIdGetSuccse(new onAndroidIdGetSuccess() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub

			}
		});
		return v;
	}

	private View.OnClickListener btnListClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			FragmentChangeActivity.slideMenu.toggle();
		}
	};

	static final int REQUEST_CODE_LOGIN = 100;
	private MyRemDialog loginRemDialog;

	// 提示登陆
	private void showLoginRem(int msgId) {
		if (null == loginRemDialog) {
			loginRemDialog = new MyRemDialog(getActivity(), R.style.Dialog);
			loginRemDialog.setHeaderVisility(View.GONE);
			loginRemDialog.setCancelable(false);
			loginRemDialog.setPosBtnText(R.string.login);
			loginRemDialog.setNegBtnText(R.string.cancel);
			loginRemDialog.setPosBtnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent mIntent = new Intent(getActivity(),
							LoginActivity.class);
					getActivity().startActivityForResult(mIntent,
							REQUEST_CODE_LOGIN);
				}
			});
		}
		loginRemDialog.setMessage(msgId);
		if (null != loginRemDialog && !getActivity().isFinishing()
				&& !loginRemDialog.isShowing()) {
			loginRemDialog.show();
		}
	}

	// 我要
	private View.OnClickListener requirementsClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			isLogin = !SettingUtil
					.getInstance(getActivity())
					.getValue(SettingUtil.KEY_LOGIN_USER_ID,
							SettingUtil.DEFAULT_LOGIN_USER_ID)
					.equalsIgnoreCase(SettingUtil.DEFAULT_LOGIN_USER_ID);
			if (isLogin) {

				Intent intent = new Intent(
						AppHomePageActivity.this.getActivity(),
						AddKeywordsActivity.class);
				intent.putExtra("category", category);
				startActivity(intent);
			} else {
				showLoginRem(R.string.review_login_rem_for_my_send);
				// Toast.makeText(getActivity(), "亲，请您先登录！", Toast.LENGTH_SHORT)
				// .show();
			}
		}
	};
	// 我有
	private View.OnClickListener informationClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			isLogin = !SettingUtil
					.getInstance(getActivity())
					.getValue(SettingUtil.KEY_LOGIN_USER_ID,
							SettingUtil.DEFAULT_LOGIN_USER_ID)
					.equalsIgnoreCase(SettingUtil.DEFAULT_LOGIN_USER_ID);
			if (isLogin) {
				Intent intent = new Intent(
						AppHomePageActivity.this.getActivity(),
						AddProductActivity.class);
				intent.putExtra("category", category);
				startActivity(intent);
			} else {
				showLoginRem(R.string.review_login_rem_for_my_send);
				// Toast.makeText(getActivity(), "亲，请您先登录！", Toast.LENGTH_SHORT)
				// .show();
			}
		}
	};

	// public class ViewPageAdapter extends FragmentStatePagerAdapter {
	// public ViewPageAdapter(FragmentManager fm) {
	// super(fm);
	// // TODO Auto-generated constructor stub
	// }
	//
	// @Override
	// public Fragment getItem(int arg0) {
	// // TODO Auto-generated method stub
	// return fragmentList.get(arg0);
	// }
	//
	// @Override
	// public int getCount() {
	// // TODO Auto-generated method stub
	// return fragmentList.size();
	// }
	//
	// }

}
