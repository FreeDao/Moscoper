package com.skycober.mineral;

import com.skycober.mineral.account.HomePageActivity;
import com.skycober.mineral.product.CategoryReviewActivity;
import com.skycober.mineral.product.MyAttentionProductActivity;
import com.skycober.mineral.product.MyFavProductActivity;
import com.skycober.mineral.product.MySendProductActivity;
import com.skycober.mineral.product.RandReviewActivity;
import com.skycober.mineral.setting.InterestSettingActivity;
import com.skycober.mineral.setting.SettingActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class SidebarMenuFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.menu_content, null);
		initView(v);
		return v;
	}

	private void initView(View v) {
		ImageView sidebar_btn0 = (ImageView) v.findViewById(R.id.user_avatar);
		sidebar_btn0.setOnClickListener(onClickListener);
//		Button sidebar_btn1 = (Button) v.findViewById(R.id.sidebar_btn01);
//		sidebar_btn1.setOnClickListener(onClickListener);
		Button sidebar_btn3 = (Button) v.findViewById(R.id.sidebar_btn03);
		sidebar_btn3.setOnClickListener(onClickListener);
		Button sidebar_btn4 = (Button) v.findViewById(R.id.sidebar_btn04);
		sidebar_btn4.setOnClickListener(onClickListener);
		Button sidebar_btn5 = (Button) v.findViewById(R.id.sidebar_btn05);
		sidebar_btn5.setOnClickListener(onClickListener);
		Button sidebar_btn7 = (Button) v.findViewById(R.id.sidebar_btn07);
		sidebar_btn7.setOnClickListener(onClickListener);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Fragment newContent = null;
			switch (v.getId()) {
//			case R.id.user_avatar:// 个人主页
//				newContent = new HomePageActivity();
//				break;
//			case R.id.sidebar_btn01:// 分类浏览
//				newContent = new CategoryReviewActivity();
//				break;
//			case R.id.sidebar_btn02:// 随便看看
//				newContent = new RandReviewActivity();
//				break;
			case R.id.sidebar_btn03:// 我发布的藏品
				newContent = new MySendProductActivity();
				break;
			case R.id.sidebar_btn04:// 我收藏的藏品
				newContent = new MyFavProductActivity();
				break;
			case R.id.sidebar_btn05:// 我关注的藏品
				newContent = new MyAttentionProductActivity();
				break;
//			case R.id.sidebar_btn06:// 感兴趣的
//				newContent = new InterestSettingActivity();
//				break;
			case R.id.sidebar_btn07:// 设置
				newContent = new SettingActivity();
				break;
			}
			if (newContent != null) {
				switchFragment(newContent);
			}
		}

	};

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) getActivity();
			fca.switchContent(fragment);
		}
	}

}
