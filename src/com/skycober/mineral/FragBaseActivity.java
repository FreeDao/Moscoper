package com.skycober.mineral;

import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.widget.WaitLoadingDialog;

import android.os.Bundle;
import android.view.KeyEvent;

public class FragBaseActivity extends FragmentFinalActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected WaitLoadingDialog waitDialog;
	
	public void lockScreen(String message) {
		if(null == waitDialog){
			waitDialog = new WaitLoadingDialog(getActivity());
		}
		waitDialog.setMessage(message);
		if(null != waitDialog && !getActivity().isFinishing() && !waitDialog.isShowing()){
			waitDialog.show();
		}
	}
	
	public void releaseScreen() {
		if(null != waitDialog && waitDialog.isShowing()){
			waitDialog.cancel();
		}
	}
	
	@Override
	public void onDestroy() {
		releaseScreen();
		super.onDestroy();
	}
	
	public void clearLoginInfo() {
		SettingUtil.getInstance(getActivity()).saveValue(SettingUtil.KEY_LOGIN_USER_ID, SettingUtil.DEFAULT_LOGIN_USER_ID);
		SettingUtil.getInstance(getActivity()).saveValue(SettingUtil.KEY_COOKIE, null);
	}


}
