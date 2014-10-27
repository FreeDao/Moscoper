package com.skycober.mineral.setting;

import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;

import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.account.AccountService;
import com.skycober.mineral.account.ServerResponseParser;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.StringUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 修改密码
 * 
 * @author Yes366
 * 
 */
public class UpdatePwdActivity extends BaseActivity {
	private static final String TAG = "UpdatePwdActivity";

	@ViewInject(id = R.id.title_button_left)
	ImageButton btnLeft;
	@ViewInject(id = R.id.title_button_right)
	ImageButton btnRight;
	@ViewInject(id = R.id.title_text)
	TextView tvTitle;
	@ViewInject(id = R.id.old_password)
	EditText etOldPass;
	@ViewInject(id = R.id.new_password)
	EditText etNewPass;
	@ViewInject(id = R.id.confirm_password)
	EditText etConfirmPass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_pwd);
		initTopBar();
	}

	private void initTopBar() {
		// 返回
		btnLeft.setImageResource(R.drawable.back_btn_selector);
		btnLeft.setOnClickListener(btnLeftClickListener);
		// 确认
		btnRight.setImageResource(R.drawable.check_btn_selector);
		btnRight.setOnClickListener(btnRightClickListener);
		tvTitle.setText(R.string.setting_item_update_pwd);
	}

	private View.OnClickListener btnLeftClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
		}
	};

	// 修改密码
	private View.OnClickListener btnRightClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// 修改密码
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			readyToChangePwd();
			v.setEnabled(true);
		}
	};

	// 准备修改密码
	protected void readyToChangePwd() {
		String oldPass = etOldPass.getText().toString();
		if (StringUtil.getInstance().IsEmpty(oldPass.trim())) {
			Toast.makeText(this, R.string.old_pass_not_empty, Toast.LENGTH_LONG)
					.show();
			return;
		}
		String newPass = etNewPass.getText().toString();
		if (StringUtil.getInstance().IsEmpty(newPass)
				|| StringUtil.getInstance().IsEmpty(newPass.trim())) {
			Toast.makeText(this, R.string.new_pass_not_empty, Toast.LENGTH_LONG)
					.show();
			return;
		}
		String confirmPass = etConfirmPass.getText().toString();
		if (StringUtil.getInstance().IsEmpty(confirmPass)) {
			Toast.makeText(this, R.string.confirm_pass_not_empty,
					Toast.LENGTH_LONG).show();
			return;
		}
		if (!newPass.equalsIgnoreCase(confirmPass)) {
			Toast.makeText(this, R.string.double_pass_incorrect,
					Toast.LENGTH_LONG).show();
			return;
		}
		readyToChangePwd(oldPass, newPass);
	}

	// 修改密码
	private void readyToChangePwd(final String oldPass, final String newPass) {
		final String currMethod = "readyToChangePwd:";
		lockScreen(getString(R.string.update_pwd_send_ing));
		AccountService as = new AccountService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onFailure(Throwable t, String strMsg) {
				releaseScreen();
				Log.e(TAG, currMethod + "onFailure->Msg:" + strMsg, t);
				if (BuildConfig.isDebug) {
					String msg = strMsg;
					if (StringUtil.getInstance().IsEmpty(msg)) {
						msg = null == t ? "Exception t is null." : t.toString();
					}
					ExceptionRemHelper.showExceptionReport(
							UpdatePwdActivity.this, msg);
				} else {
					Toast.makeText(UpdatePwdActivity.this,
							R.string.update_pwd_failed, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseUpdatePwd(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						Toast.makeText(UpdatePwdActivity.this,
								R.string.update_pwd_succeed, Toast.LENGTH_SHORT)
								.show();
						finish();
					} else {
						String message = getString(R.string.update_pwd_failed);
						String msg = getString(R.string.update_pwd_failed);
						if (null == br) {
							msg = "parseUpdatePwd Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {
							default:
								msg = br.getMessage();
								message = br.getMessage();// TODO
															// 暂时显示错误信息给用户，正式版后只提示失败
								break;
							}
						}
						releaseScreen();
						Log.e(TAG,
								currMethod + "errorCode:" + br.getErrorCode()
										+ "--Message:" + br.getMessage());
						if (BuildConfig.isDebug) {
							ExceptionRemHelper.showExceptionReport(
									UpdatePwdActivity.this, msg);
						} else {
							Toast.makeText(UpdatePwdActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					releaseScreen();
					Log.d(TAG, currMethod + "Result t is null");
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								UpdatePwdActivity.this, msg);
					} else {
						Toast.makeText(UpdatePwdActivity.this,
								R.string.update_pwd_failed, Toast.LENGTH_SHORT)
								.show();
					}
				}
				super.onSuccess(t);
			}
		};
		as.UpdatePwd(this, oldPass, newPass, callBack);
	}

}
