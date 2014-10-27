package com.skycober.mineral.account;

import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.bean.UserRec;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseRegistUser;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.Util;

/**
 * 注册
 * 
 * @author Yes366
 * 
 */
public class RegistActivity extends BaseActivity {
	private static final String TAG = "RegistActivity";
	@ViewInject(id = R.id.title_button_left)
	ImageButton btnLeft;
	@ViewInject(id = R.id.title_button_right)
	ImageButton btnRight;
	@ViewInject(id = R.id.title_text)
	TextView tvTitle;
	@ViewInject(id = R.id.etAccountName)
	EditText etAccountName;
//	@ViewInject(id = R.id.etAccountEmail)
//	EditText etAccountEmail;
	@ViewInject(id = R.id.etAccountPwd)
	EditText etAccountPwd;
	// 注册按钮
	@ViewInject(id = R.id.btnRegist, click = "onButtonRegistClick")
	Button regist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);
		InitTopBar();
	}

	private void InitTopBar() {
		btnLeft.setImageResource(R.drawable.back_btn_selector);
		btnLeft.setOnClickListener(btnLeftClickListener);
		btnRight.setVisibility(View.INVISIBLE);
		tvTitle.setText(R.string.regist_page_title);
	}

	private View.OnClickListener btnLeftClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
		}
	};

	// 准备注册
	private void readyToRegist() {
		String userName = etAccountName.getText().toString();
		String nameMach = "^[A-Za-z0-9]+$";
		if (StringUtil.getInstance().IsEmpty(userName)) {
			Toast.makeText(this, R.string.login_username_not_empty,
					Toast.LENGTH_LONG).show();
			return;
		}else if(!userName.matches(nameMach)){
			Toast.makeText(getApplicationContext(), "用户名只能包含字母和数字", 1).show();
			return;
		}else if(userName.length()>15){
			Toast.makeText(getApplicationContext(), "用户名不超过15个字符", 1).show();
			return;
		}
		
		// String email = etAccountEmail.getText().toString();
		// String regMacth = "[\\w]+@[\\w]+.[\\w]+";
		// if (StringUtil.getInstance().IsEmpty(email)) {
		//
		// Toast.makeText(this, R.string.regist_email_not_empty,
		// Toast.LENGTH_LONG).show();
		// return;
		// } else if (!email.matches(regMacth)) {
		// Toast.makeText(this, "Email不合法！！！", Toast.LENGTH_LONG).show();
		// return;
		// }
		String password = etAccountPwd.getText().toString();
		if (StringUtil.getInstance().IsEmpty(password)||password.trim().length() == 0) {

			Toast.makeText(this, R.string.login_password_not_empty,
					Toast.LENGTH_LONG).show();
			return;
		}else if(password.trim().length()<6){
			Toast.makeText(getApplicationContext(), "亲！您的密码太短了", 1).show();
			return;
		}
		readyToRegist(userName, password);
	}

	private void readyToRegist(final String userName, final String password) {
		lockScreen(getString(R.string.regist_send_ing));
		AccountService as = new AccountService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onFailure(Throwable t, String strMsg) {
				releaseScreen();
				if (BuildConfig.isDebug) {
					String msg = strMsg;
					if (StringUtil.getInstance().IsEmpty(msg)) {
						msg = null == t ? "Exception t is null." : t.toString();
					}
					ExceptionRemHelper.showExceptionReport(RegistActivity.this,
							msg);
				} else {
					Toast.makeText(RegistActivity.this,
							R.string.regist_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				if (null != t) {
					String json = t.toString();
					Log.e(TAG, "onsuccess");
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseRegistUser(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						releaseScreen();
						ResponseRegistUser response = (ResponseRegistUser) br;
						UserRec mRec = response.getUserRec();
						if (null == mRec) {
							if (BuildConfig.isDebug) {
								ExceptionRemHelper.showExceptionReport(
										RegistActivity.this,
										"parse Json:UserRec is null.");
							} else {
								Toast.makeText(RegistActivity.this,
										R.string.regist_failed_rem,
										Toast.LENGTH_SHORT).show();
							}
						} else {
							SettingUtil.getInstance(RegistActivity.this)
									.saveUserInfo(mRec.getUserId(), mRec);
							Toast.makeText(RegistActivity.this,
									R.string.regist_succeed_rem,
									Toast.LENGTH_SHORT).show();
							Intent mIntent = getIntent();
							if (null == mIntent)
								mIntent = new Intent();

							// 注册成功向登陆界面返回用户名和密码
							mIntent.putExtra(LoginActivity.KEY_REGIST_USERNAME,
									userName);
							mIntent.putExtra(LoginActivity.KEY_REGIST_PASSWORD,
									password);
							setResult(Activity.RESULT_OK, mIntent);
							finish();
						}
					} else {
						String message = getString(R.string.regist_failed_rem);
						String msg = getString(R.string.regist_failed_rem);
						if (null == br) {
							msg = "parseRegistUser Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {
							case 14:
								msg = getString(R.string.common_error_username_registed);
								message = msg;
								break;
							case 15:
								msg = getString(R.string.common_error_email_registed);
								message = msg;
								break;
							default:
								msg = br.getMessage();
								message = br.getMessage();
								// TODO 暂时显示错误信息给用户，正式版后只提示失败
								break;
							}
						}
						releaseScreen();
						if (BuildConfig.isDebug) {
							ExceptionRemHelper.showExceptionReport(
									RegistActivity.this, msg);
						} else {
							Toast.makeText(RegistActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					releaseScreen();
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								RegistActivity.this, msg);
					} else {
						Toast.makeText(RegistActivity.this,
								R.string.regist_failed_rem, Toast.LENGTH_SHORT)
								.show();
					}
				}
				super.onSuccess(t);
			}
		};
		String devIdentify = Util.GetAndroidId(this);
		as.RegistUser(this, userName, password, devIdentify, callBack);

	}

	// 点击注册
	public void onButtonRegistClick(View view) {
//		if (!view.isEnabled())
//			return;
//		view.setEnabled(false);
		// 准备注册
		System.out.println("=onButtonRegistClick==");
		readyToRegist();
//		view.setEnabled(true);
	}
}
