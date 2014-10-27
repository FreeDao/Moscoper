package com.skycober.mineral.company;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.FragmentChangeActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.R.string;
import com.skycober.mineral.account.LoginActivity;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.util.SettingUtil;

/**
 * 企业信息
 * 
 * @author CF
 * 
 */
public class CompanyActivity extends FragmentActivity{

	private ImageButton topLeft;
	private TextView toptitle;
	private ImageButton topRight;
	private MyOnClickListener listener;
	private FragmentManager manager;
	// private ViewPager viewPager;

	// private List<Fragment> listFragment;
	// private CompanyViewPagerAdapter viewPagerAdapter;
	private TextView attention, noAttention;
	private CompanyFragment attentionFragment;
	private CompanyListFragment companyListFragment;

	private boolean isFirst = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.company_activity);
		isFirst = true;
		manager = getSupportFragmentManager();
		boolean hasLogin = !SettingUtil
				.getInstance(this)
				.getValue(SettingUtil.KEY_LOGIN_USER_ID,
						SettingUtil.DEFAULT_LOGIN_USER_ID)
				.equalsIgnoreCase(SettingUtil.DEFAULT_LOGIN_USER_ID);
		// 如果没登陆跳转登录界面，
		if (hasLogin) {
//			View view = inflater.inflate(R.layout.company_activity, container,
//					false);
			listener = new MyOnClickListener();
			// 标题栏
			topLeft = (ImageButton) findViewById(R.id.title_button_left);
			toptitle = (TextView) findViewById(R.id.title_text);
			toptitle.setText(string.sidebar_btn_my_company_text);
			topRight = (ImageButton) findViewById(R.id.title_button_right);
			topRight.setVisibility(View.GONE);
			topLeft.setOnClickListener(listener);
			// 关注和未关注title
			attention = (TextView) findViewById(R.id.atten);
			noAttention = (TextView)findViewById(R.id.no_atten);
			attention.setOnClickListener(listener);
			noAttention.setOnClickListener(listener);

			// viewpager
			// viewPager = (ViewPager) view.findViewById(R.id.viewpager);
			
			companyListFragment = new CompanyListFragment();
			Bundle bundle_list = new Bundle();
			bundle_list.putString("url", RequestUrls.SERVER_BASIC_URL
					+ RequestUrls.COMPANY_LIST);
			companyListFragment.setArguments(bundle_list);
			manager.beginTransaction()
					.replace(R.id.fragment, companyListFragment).commit();

			// listFragment = new ArrayList<Fragment>();
			//
			// listFragment.add(companyListFragment);
			// listFragment.add(attentionFragment);
			// viewPagerAdapter = new CompanyViewPagerAdapter(getActivity()
			// .getSupportFragmentManager());
			// viewPager.setAdapter(viewPagerAdapter);
			// viewPagerAdapter.notifyDataSetChanged();
			
		} else {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
		
	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//	
//	}

	// public CompanyFragment instanceFragment(String url) {
	// CompanyFragment fragment = null;
	// if (fragment == null) {
	// fragment = new CompanyFragment();
	// Bundle bundle = new Bundle();
	// bundle.putString("url", url);
	// fragment.setArguments(bundle);
	// }
	//
	// return fragment;
	//
	// }

	// public class CompanyViewPagerAdapter extends FragmentStatePagerAdapter {
	//
	// public CompanyViewPagerAdapter(FragmentManager fm) {
	// super(fm);
	// // TODO Auto-generated constructor stub
	// }
	//
	// @Override
	// public Fragment getItem(int arg0) {
	// // TODO Auto-generated method stub
	// // return listFragment.get(arg0);
	// }
	//
	// @Override
	// public int getCount() {
	// // TODO Auto-generated method stub
	// // return listFragment.size();
	// }
	//
	// }

	public class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.title_button_left:
				CompanyActivity.this.finish();
				break;
			case R.id.atten:
				// viewPager.setCurrentItem(1);
				attentionFragment = new CompanyFragment();
				Bundle bundle_att = new Bundle();
				bundle_att.putString("url", RequestUrls.SERVER_BASIC_URL
						+ RequestUrls.COMANY_ATTENTION_INFOS);
				attentionFragment.setArguments(bundle_att);
				manager.beginTransaction()
						.replace(R.id.fragment, attentionFragment).commit();
				attention
						.setBackgroundResource(R.drawable.textview_right_select_shape);
				noAttention
						.setBackgroundResource(R.drawable.textview_left_shape);
				attention.setTextColor(Color.WHITE);
				noAttention.setTextColor(getResources().getColor(
						R.color.text_color));
				break;
			case R.id.no_atten:
				// viewPager.setCurrentItem(0);
				manager.beginTransaction()
						.replace(R.id.fragment,companyListFragment ).commit();

				attention
						.setBackgroundResource(R.drawable.textview_right_shape);
				noAttention
						.setBackgroundResource(R.drawable.textview_left_selected_shape);
				attention.setTextColor(getResources().getColor(
						R.color.text_color));
				noAttention.setTextColor(Color.WHITE);
				break;

			}
		}

	}

}
