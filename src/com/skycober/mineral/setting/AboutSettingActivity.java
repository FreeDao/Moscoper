package com.skycober.mineral.setting;

import net.tsz.afinal.annotation.view.ViewInject;

import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.R;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 关于界面
 * 
 * @author Yes366
 * 
 */
public class AboutSettingActivity extends BaseActivity {

	@ViewInject(id = R.id.title_button_left)
	ImageButton btnLeft;
	@ViewInject(id = R.id.title_button_right)
	ImageButton btnRight;
	@ViewInject(id = R.id.title_text)
	TextView tvTitle;
	@ViewInject(id = R.id.version)
	TextView tvVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_setting);
		initTopBar();
	}

	private void initTopBar() {
		btnLeft.setImageResource(R.drawable.back_btn_selector);
		btnLeft.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btnRight.setVisibility(View.INVISIBLE);
		tvTitle.setText(R.string.setting_item_about);
	}

	@Override
	protected void onStart() {
		// 获取版本信息
		initAppVersion();
		super.onStart();
	}

	// 获取版本信息
	private void initAppVersion() {
		PackageManager pm = getPackageManager();
		String packageName = getPackageName();
		PackageInfo info;
		try {
			info = pm.getPackageInfo(packageName, 0);
			String version = info.versionName;
			String description = String.format(
					getString(R.string.version_format), version);
			tvVersion.setText(description);
		} catch (NameNotFoundException e) {

		}
	}
}
