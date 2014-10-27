package com.skycober.mineral;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;

import com.skycober.mineral.account.HomePageActivity;
import com.skycober.mineral.account.LoginActivity;
import com.skycober.mineral.bean.UserRec;
import com.skycober.mineral.black.BlackListFragment;
import com.skycober.mineral.company.CompanyActivity;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.product.AppHomePageActivity;
import com.skycober.mineral.product.CategoryReviewActivity;
import com.skycober.mineral.product.CategoryReviewListActivity;
import com.skycober.mineral.product.KeyWordsActivity;
import com.skycober.mineral.product.MyAttentionProductActivity;
import com.skycober.mineral.product.MyFavProductActivity;
import com.skycober.mineral.product.MySendProductActivity;
import com.skycober.mineral.product.RandReviewActivity;
import com.skycober.mineral.product.SearchActivity;
import com.skycober.mineral.setting.InterestSettingActivity;
import com.skycober.mineral.setting.SettingActivity;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.util.Trace;
import com.skycober.mineral.widget.MyRemDialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MenuFragment extends Fragment {
	private static final String TAG = "MenuFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// View v = inflater.inflate(R.layout.menu_content, null);
		View v = inflater.inflate(R.layout.menulist, null);

		initView(v);
		return v;
	}

	private Button sidebar_btn8;

	private void initView(View v) {
		// Button sidebar_btn1 = (Button) v.findViewById(R.id.sidebar_btn01);
		// sidebar_btn1.setOnClickListener(onClickListener);
		Button sidebar_btn3 = (Button) v.findViewById(R.id.sidebar_btn03);// ����
		sidebar_btn3.setOnClickListener(onClickListener);
		Button sidebar_btn4 = (Button) v.findViewById(R.id.sidebar_btn04);// �ղ���Ϣ
		sidebar_btn4.setOnClickListener(onClickListener);
		Button sidebar_btn5 = (Button) v.findViewById(R.id.sidebar_btn05);// ������Ϣ
		sidebar_btn5.setOnClickListener(onClickListener);
		Button sidebar_btn7 = (Button) v.findViewById(R.id.sidebar_btn07);// ��������
		sidebar_btn7.setOnClickListener(onClickListener);
		sidebar_btn8 = (Button) v.findViewById(R.id.sidebar_btn08);// ��½ע��
		sidebar_btn8.setOnClickListener(onClickListener);
		Button sidebar_btn10 = (Button) v.findViewById(R.id.sidebar_btn10);// ��Ҫ
		sidebar_btn10.setOnClickListener(onClickListener);
		Button sidebar_btn11 = (Button) v.findViewById(R.id.sidebar_btn11);// ��ҳ
		sidebar_btn11.setOnClickListener(onClickListener);
		Button company = (Button) v.findViewById(R.id.company);// ��ס��ҵ
		company.setOnClickListener(onClickListener);
		Button black = (Button) v.findViewById(R.id.black);
		black.setOnClickListener(onClickListener);

	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			boolean isLogin = !SettingUtil
					.getInstance(getActivity())
					.getValue(SettingUtil.KEY_LOGIN_USER_ID,
							SettingUtil.DEFAULT_LOGIN_USER_ID)
					.equalsIgnoreCase(SettingUtil.DEFAULT_LOGIN_USER_ID);
			if (!isLogin) {
				refreshLoginState(isLogin);
			}
			Fragment newContent = null;
			switch (v.getId()) {
			case R.id.company:// ��ס��ҵ

				if (isLogin) {
					Intent intent = new Intent(getActivity(),
							CompanyActivity.class);
					startActivity(intent);
				} else {
					showLoginRem(R.string.review_login_rem_for_my_send);
				}
				break;
			case R.id.sidebar_btn11:// ��ҳ
				newContent = new AppHomePageActivity();
				break;
			// case R.id.sidebar_btn01:// �������
			//
			// newContent = new CategoryReviewActivity();
			// break;
			case R.id.sidebar_btn03:// �ҷ����Ĳ�Ʒ�����У�
				if (isLogin) {
					newContent = new MySendProductActivity();
				} else {
					showLoginRem(R.string.review_login_rem_for_my_send);
				}
				break;
			case R.id.sidebar_btn04:// ���ղصĲ�Ʒ���ղ���Ϣ��
				if (isLogin) {
					newContent = new MyFavProductActivity();
				} else {
					showLoginRem(R.string.review_login_rem_for_my_send);
				}
				break;
			case R.id.sidebar_btn05:// �ҹ�ע�Ĳ�Ʒ��������Ϣ��
				if (isLogin) {
					newContent = new MyAttentionProductActivity();
				} else {
					showLoginRem(R.string.review_login_rem_for_my_send);
				}
				break;
			case R.id.sidebar_btn07:// ���ã��������ã�
				newContent = new SettingActivity();
				// Intent startIntent1 = new Intent(getActivity(),
				// .class);
				// getActivity().startActivityForResult(startIntent1,
				// REQUEST_CODE_SETTING);

				break;
			case R.id.black:// ������
				if (isLogin) {
					newContent = new BlackListFragment();
				} else {
					showLoginRem(R.string.review_login_rem_for_my_send);
				}
				break;
			case R.id.sidebar_btn08:// ��½ע��
				Intent startIntent = new Intent(getActivity(),
						LoginActivity.class);
				getActivity().startActivityForResult(startIntent,
						REQUEST_CODE_LOGIN);
				break;
			case R.id.sidebar_btn10:// ��ǩ������Ҫ��
				if (isLogin) {
					newContent = new KeyWordsActivity();
				} else {
					showLoginRem(R.string.review_login_rem_for_my_send);
				}
				break;
			}
			if (newContent != null) {
				switchFragment(newContent);
			}
		}

	};

	static final int REQUEST_CODE_LOGIN = 100;
	static final int REQUEST_CODE_SETTING = 101;

	// ��½��û�õ���
	private View.OnClickListener btnLoginClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent startIntent = new Intent(getActivity(), LoginActivity.class);
			getActivity().startActivityForResult(startIntent,
					REQUEST_CODE_LOGIN);
		}
	};

	// private View.OnClickListener btnAvatarClickListener = new
	// View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// switchFragment(new HomePageActivity());
	// }
	// };

	// �������ã�û�õ���
	private View.OnClickListener btnSettingClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switchFragment(new SettingActivity());
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		refreshLoginState(true);
		String[] menus = getResources().getStringArray(R.array.side_menus);
		List<String> menuList = new ArrayList<String>();
		for (String menu : menus) {
			menuList.add(menu);
		}

		// MenuAdapter menuAdapter = new MenuAdapter(getActivity(),
		// R.layout.menu_item, menuList);
		// lvMenu.setAdapter(menuAdapter);
		// lvMenu.setOnItemClickListener(menuItemClickListener);
	}

	// û�õ�
	private AdapterView.OnItemClickListener menuItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int pos,
				long id) {
			boolean isLogin = !SettingUtil
					.getInstance(getActivity())
					.getValue(SettingUtil.KEY_LOGIN_USER_ID,
							SettingUtil.DEFAULT_LOGIN_USER_ID)
					.equalsIgnoreCase(SettingUtil.DEFAULT_LOGIN_USER_ID);
			Fragment newContent = null;
			switch (pos) {
			case 0:
				newContent = new CategoryReviewActivity();
				break;
			case 1:
				newContent = new RandReviewActivity();
				break;
			case 2:
				if (isLogin) {
					newContent = new MySendProductActivity();
				} else {
					showLoginRem(R.string.review_login_rem_for_my_send);
				}
				break;
			case 3:
				if (isLogin) {
					newContent = new MyFavProductActivity();
				} else {
					showLoginRem(R.string.review_login_rem_for_my_fav);
				}
				break;
			case 4:
				if (isLogin) {
					newContent = new MyAttentionProductActivity();
				} else {
					showLoginRem(R.string.review_login_rem_for_my_attention);
				}
				break;
			case 5:
				if (isLogin) {
					newContent = new InterestSettingActivity();
				} else {
					showLoginRem(R.string.review_login_rem_for_interest_setting);
				}
				break;
			case 6:
				newContent = new SearchActivity();
				break;
			}
			if (newContent != null) {
				switchFragment(newContent);
			}
		}

	};

	private MyRemDialog loginRemDialog;

	// ��ʾ��½
	private void showLoginRem(int msgId) {
		if (null == loginRemDialog) {
			loginRemDialog = new MyRemDialog(getActivity(), R.style.Dialog);
			loginRemDialog.setHeaderVisility(View.GONE);

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

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) getActivity();
			fca.switchContent(fragment);
		}
	}

	public void refreshLoginState(boolean isLogin) {
		if (SettingUtil
				.getInstance(getActivity())
				.getValue(SettingUtil.KEY_LOGIN_USER_ID,
						SettingUtil.DEFAULT_LOGIN_USER_ID).equals("-1")) {
			sidebar_btn8.setVisibility(View.VISIBLE);
		} else {
			sidebar_btn8.setVisibility(View.GONE);

		}
		// if(null == btnLogin || null == tvUserName || null == tvSignature ||
		// null == ivAvatar) return;
		// if (isLogin) {
		// String userId = SettingUtil.getInstance(getActivity()).getValue(
		// SettingUtil.KEY_LOGIN_USER_ID,
		// SettingUtil.DEFAULT_LOGIN_USER_ID);
		// UserRec rec = SettingUtil.getInstance(getActivity()).getUserInfo(
		// userId, null);

		// if (null != rec) {
		// btnLogin.setVisibility(View.INVISIBLE);
		// String userName = rec.getUserName();
		// tvUserName.setText(userName);
		// tvUserName.setVisibility(View.VISIBLE);
		// String signature = rec.getSignature();
		// tvSignature.setText(signature);
		// tvSignature.setVisibility(View.VISIBLE);
		// Trace.d(TAG, "refreshLoginState:" + rec.getAvatar().toString());
		// String avatar =RequestUrls.SERVER_BASIC_URL+
		// "/"+rec.getAvatar().getBig();
		//
		// FinalBitmap fb = FinalBitmap.create(getActivity());
		// fb.configLoadfailImage(R.drawable.menu_default_head);
		// fb.configLoadingImage(R.drawable.menu_default_head);
		// fb.display(ivAvatar, avatar, true);
		// }
		// } else {
		// ivAvatar.setImageResource(R.drawable.menu_default_head);
		// tvUserName.setVisibility(View.INVISIBLE);
		// tvUserName.setText("");
		// tvSignature.setVisibility(View.INVISIBLE);
		// tvSignature.setText("");
		// btnLogin.setVisibility(View.VISIBLE);
		// }
		// }

		// public void switchLoginState(boolean isLogin){
		// if(isLogin){
		// btnLogin.setVisibility(View.INVISIBLE);
		// tvUserName.setVisibility(View.VISIBLE);
		// tvSignature.setVisibility(View.VISIBLE);
		// }else{
		// ivAvatar.setImageResource(R.drawable.menu_default_head);
		// tvUserName.setVisibility(View.INVISIBLE);
		// tvSignature.setVisibility(View.INVISIBLE);
		// btnLogin.setVisibility(View.VISIBLE);
		// }
	}

	// û�õ�
	public class MenuAdapter extends BaseAdapter {

		private List<String> menuList;
		private int layoutId;
		private LayoutInflater inflater;
		private int[] iconIds;

		public MenuAdapter(Context context, int layoutId, List<String> menuList) {
			this.menuList = menuList;
			this.layoutId = layoutId;
			this.inflater = LayoutInflater.from(context);
			this.iconIds = initIconArr();
		}

		@Override
		public int getCount() {
			return this.menuList.size();
		}

		@Override
		public Object getItem(int position) {
			return menuList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView) {
				convertView = inflater.inflate(layoutId, null);
			}
			ImageView ivIcon = (ImageView) convertView
					.findViewById(R.id.menu_item_icon);
			TextView tvName = (TextView) convertView
					.findViewById(R.id.menu_item_name);
			ivIcon.setImageResource(iconIds[position]);
			tvName.setText(menuList.get(position));
			return convertView;
		}

		private int[] initIconArr() {
			int[] ids = new int[] { R.drawable.menu_icon_home,
					R.drawable.menu_icon_eye, R.drawable.menu_icon_push,
					R.drawable.menu_icon_star, R.drawable.menu_icon_friend,
					R.drawable.menu_icon_welfare, R.drawable.menu_icon_search };
			return ids;
		}
	}
}
