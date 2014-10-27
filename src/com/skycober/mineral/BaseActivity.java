package com.skycober.mineral;

import net.tsz.afinal.FinalActivity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.widget.WaitLoadingDialog;

public class BaseActivity extends FinalActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected WaitLoadingDialog waitDialog;

	public void lockScreen(String message) {
		if (null == waitDialog) {
			waitDialog = new WaitLoadingDialog(this);
		}
		waitDialog.setMessage(message);
		if (null != waitDialog && !isFinishing() && !waitDialog.isShowing()) {
			waitDialog.show();
		}
	}

	public void releaseScreen() {
		if (null != waitDialog && waitDialog.isShowing()) {
			waitDialog.cancel();
		}
	}

	@Override
	protected void onDestroy() {
		releaseScreen();
		super.onDestroy();
	}

	public void clearLoginInfo() {
		SettingUtil.getInstance(BaseActivity.this).saveValue(
				SettingUtil.KEY_LOGIN_USER_ID,
				SettingUtil.DEFAULT_LOGIN_USER_ID);
		SettingUtil.getInstance(BaseActivity.this).saveValue(
				SettingUtil.KEY_COOKIE, null);
	}
	
	public void showToast(Context context,String text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}
