package com.skycober.mineral.welcom;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.skycober.mineral.FragmentChangeActivity;
import com.skycober.mineral.MyApplication;
import com.skycober.mineral.R;
import com.skycober.mineral.updateApk.UpDataApkAcitvity;

public class WelcomActivity extends FragmentActivity {
	private ViewPager welcomViewpage;
	private List<WelcomFragment> fragmentList;
	private MyViewPageAdapter viewPageAdapter;
	private ImageView dot1, dot2, dot3, dot4;
	private ImageButton startBut;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.acitivity_welcom);
		MyApplication.getInstance().addActivity(this);
		
		SharedPreferences sp = getSharedPreferences("first", 0);
		if (sp.getBoolean("first", false)) {
			Intent intent = new Intent(WelcomActivity.this,
					FragmentChangeActivity.class);
			startActivity(intent);
		}

		welcomViewpage = (ViewPager) findViewById(R.id.welcomViewPage);

		fragmentList = new ArrayList<WelcomFragment>();
		dot1 = (ImageView) findViewById(R.id.dot1);
		dot2 = (ImageView) findViewById(R.id.dot2);
		dot3 = (ImageView) findViewById(R.id.dot3);
		dot4 = (ImageView) findViewById(R.id.dot4);
		startBut = (ImageButton) findViewById(R.id.start);
		startBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences sp = getSharedPreferences("first", 0);
				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean("first", true);
				editor.commit();
				Intent intent = new Intent(WelcomActivity.this,
						FragmentChangeActivity.class);
				startActivity(intent);
			}
		});

		WelcomFragment welcom1 = newIntance(R.drawable.welcom1);
		WelcomFragment welcom2 = newIntance(R.drawable.welcom2);
		WelcomFragment welcom3 = newIntance(R.drawable.welcom3);
		WelcomFragment welcom4 = newIntance(R.drawable.welcom_last);
		fragmentList.add(welcom1);
		fragmentList.add(welcom2);
		fragmentList.add(welcom3);
		fragmentList.add(welcom4);
		viewPageAdapter = new MyViewPageAdapter(getSupportFragmentManager());
		welcomViewpage.setAdapter(viewPageAdapter);
		welcomViewpage.setOnPageChangeListener(viewPageAdapter);

	}

	public class MyViewPageAdapter extends FragmentStatePagerAdapter implements
			OnPageChangeListener {

		public MyViewPageAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return fragmentList.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fragmentList.size();
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			switch (arg0) {
			case 0:
				dot1.setImageResource(R.drawable.step_selected);
				dot2.setImageResource(R.drawable.step);
				dot3.setImageResource(R.drawable.step);
				dot4.setImageResource(R.drawable.step);
				break;

			case 1:
				dot1.setImageResource(R.drawable.step);
				dot2.setImageResource(R.drawable.step_selected);
				dot3.setImageResource(R.drawable.step);
				dot4.setImageResource(R.drawable.step);
				break;
			case 2:
				dot1.setImageResource(R.drawable.step);
				dot2.setImageResource(R.drawable.step);
				dot4.setImageResource(R.drawable.step);
				dot3.setImageResource(R.drawable.step_selected);
				startBut.setVisibility(View.GONE);
				break;
			case 3:
				dot1.setImageResource(R.drawable.step);
				dot2.setImageResource(R.drawable.step);
				dot3.setImageResource(R.drawable.step);
				dot4.setImageResource(R.drawable.step_selected);
				startBut.setVisibility(View.VISIBLE);
				break;
			}

		}

	}

	public WelcomFragment newIntance(int imageId) {
		WelcomFragment welcomFragment = null;
		if (welcomFragment == null) {
			welcomFragment = new WelcomFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("ImageId", imageId);
			welcomFragment.setArguments(bundle);

		}

		return welcomFragment;
	}

}
