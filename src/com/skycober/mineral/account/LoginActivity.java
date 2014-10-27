package com.skycober.mineral.account;

import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.FragmentChangeActivity;
import com.skycober.mineral.MyApplication;
import com.skycober.mineral.R;
import com.skycober.mineral.bean.UserRec;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseOauth;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.NetworkUtil;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.Trace;
import com.skycober.mineral.util.Util;
import com.skycober.mineral.widget.MyRemDialog;

/**
 * 登录页面 Note:登录成功后，setResult(RESULT_OK),返回主页面
 * 
 * @author 新彬
 * 
 */
public class LoginActivity extends BaseActivity {
	private static final String TAG = "LoginActivity";
	@ViewInject(id = R.id.title_button_left)
	ImageButton btnLeft;
	@ViewInject(id = R.id.title_button_right)
	ImageButton btnRight;
	@ViewInject(id = R.id.title_text)
	TextView tvTitle;

	// 登陆
	@ViewInject(id = R.id.btnLogin, click = "onButtonLoginClick")
	Button btnLogin;
	@ViewInject(id = R.id.regist_btn)
	Button btnRegist;
	@ViewInject(id = R.id.btnCancel)
	Button btnCancel;
	@ViewInject(id = R.id.find_pass_btn)
	Button btnFindPass;
	@ViewInject(id = R.id.etAccountName)
	EditText etAccountName;
	@ViewInject(id = R.id.etAccountPwd)
	EditText etAccoutPwd;
	private boolean isAttention;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		MyApplication.getInstance().addActivity(this);
		InitTopBar();
		Intent intent = getIntent();
		isAttention = intent.getBooleanExtra("isAttention", false);
	}

	private void InitTopBar() {
		btnLeft.setImageResource(R.drawable.back_btn_selector);
		btnLeft.setOnClickListener(btnLeftClickListener);
		btnCancel.setOnClickListener(btnLeftClickListener);
		btnRight.setVisibility(View.INVISIBLE);
		tvTitle.setText(R.string.login_page_title);
		// 注册
		btnRegist.setOnClickListener(btnRegistClickListener);
		// 找回密码
		btnFindPass.setOnClickListener(btnFindPassClickListener);
	}

	// 点击登陆
	public void onButtonLoginClick(View v) {
		if (!v.isEnabled())
			return;
		v.setEnabled(false);
		if (!NetworkUtil.getInstance().existNetwork(this)) {
			Toast.makeText(this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
		} else {
			// 准备登陆
			readyToLogin();
		}

		v.setEnabled(true);

	}

	// 点击准备登陆
	private void readyToLogin() {
		String userName = etAccountName.getText().toString();
		if (StringUtil.getInstance().IsEmpty(userName)) {
			Toast.makeText(this, R.string.login_username_not_empty,
					Toast.LENGTH_LONG).show();
			return;
		}
		String passWord = etAccoutPwd.getText().toString();
		if (StringUtil.getInstance().IsEmpty(passWord)) {
			Toast.makeText(this, R.string.login_password_not_empty,
					Toast.LENGTH_LONG).show();
			return;
		}
		// 登陆
		readyToLogin(userName, passWord);
	}

	// 登陆
	private void readyToLogin(final String userName, String password) {
		lockScreen(getString(R.string.login_send_ing));
		final String currMethod = "readyToLogin:";
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
					ExceptionRemHelper.showExceptionReport(LoginActivity.this,
							msg);
				} else {
					Toast.makeText(LoginActivity.this,
							R.string.login_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
			}

			@Override
			public void onSuccess(Object t, HttpResponse response) {
				if (null != response) {
					Header[] headers = response.getHeaders("Set-Cookie");
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < headers.length; i++) {
						String cookie = headers[i].getValue();
						sb.append(cookie);
						sb.append(";");
					}
					String myCookie = sb.toString();
					SettingUtil.getInstance(LoginActivity.this).saveValue(
							SettingUtil.KEY_COOKIE, myCookie);
					Log.e("wangxu", "login-cookie=" + myCookie);

				}
				if (null != t) {
					String json = t.toString();
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseOauth(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						releaseScreen();
						ResponseOauth mOauth = (ResponseOauth) br;
						UserRec user = mOauth.getUserRec();
						if (StringUtil.getInstance().IsEmpty(user.getUserId())) {
							if (BuildConfig.isDebug) {
								ExceptionRemHelper.showExceptionReport(
										LoginActivity.this,
										"parse Json:userId is null.");
							} else {
								Toast.makeText(LoginActivity.this,
										R.string.login_failed_rem,
										Toast.LENGTH_SHORT).show();
							}
						} else {
							// 登陆成功
							SettingUtil.getInstance(LoginActivity.this)
									.saveUserInfo(user.getUserId(), user);
							SettingUtil.getInstance(LoginActivity.this)
									.saveValue(SettingUtil.KEY_LOGIN_USER_ID,
											user.getUserId());
							JPushInterface.setAliasAndTags(LoginActivity.this,
									"uid_" + user.getUserId(), null);
							setResult(Activity.RESULT_OK);
							finish();
						}
					} else {
						String message = getString(R.string.login_failed_rem);
						String msg = getString(R.string.login_failed_rem);
						if (null == br) {
							msg = "parseOauth Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {
							case 5:
								msg = getString(R.string.common_error_username_not_exists);
								message = getString(R.string.common_error_username_not_exists);
								break;
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
									LoginActivity.this, msg);
						} else {
							Toast.makeText(LoginActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					releaseScreen();
					Log.d(TAG, currMethod + "Result t is null");
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								LoginActivity.this, msg);
					} else {
						Toast.makeText(LoginActivity.this,
								R.string.login_failed_rem, Toast.LENGTH_SHORT)
								.show();
					}
				}
				super.onSuccess(t, response);
			}

		};
		String devIdentify = Util.GetAndroidId(this);
		as.Oauth(this, userName, password, devIdentify, callBack);
	}

	private View.OnClickListener btnLeftClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (isAttention) {
				Intent intent = new Intent(LoginActivity.this,
						FragmentChangeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("isLogin", "login");
				startActivity(intent);
			} else {
				finish();
			}

		}
	};

	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isAttention) {
				// showExitDialog();
				Intent intent = new Intent(LoginActivity.this,
						FragmentChangeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("isLogin", "login");
				startActivity(intent);
			} else {
				finish();
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	};

	private MyRemDialog exitDialog;

	private void showExitDialog() {
		if (null == exitDialog) {
			exitDialog = new MyRemDialog(this, R.style.Dialog);
			exitDialog.setTitle(R.string.exit_app_title);
			exitDialog.setMessage(R.string.exit_app_rem);
			exitDialog.setPosBtnText(R.string.exit_app_dialog_btn_exit);
			exitDialog.setNegBtnText(R.string.exit_app_dialog_btn_cancel);
			exitDialog.setPosBtnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MyApplication.getInstance().exitActivity();
				}
			});
		}
		if (null != exitDialog && !isFinishing() && !exitDialog.isShowing()) {
			exitDialog.show();
		}
	}

	private static final int REQUEST_CODE_REGIST = 101;
	// 注册
	private View.OnClickListener btnRegistClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent mIntent = new Intent(LoginActivity.this,
					RegistActivity.class);
			startActivityForResult(mIntent, REQUEST_CODE_REGIST);
		}
	};

	// 找回密码（暂未实现）
	private View.OnClickListener btnFindPassClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent mIntent = new Intent(LoginActivity.this,
					FindPassActivity.class);
			startActivity(mIntent);
		}
	};
	public static final String KEY_REGIST_USERNAME = "key_regist_username";
	public static final String KEY_REGIST_PASSWORD = "key_regist_password";

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case Activity.RESULT_OK:
			if (requestCode == REQUEST_CODE_REGIST && null != data
					&& data.hasExtra(KEY_REGIST_USERNAME)
					&& data.hasExtra(KEY_REGIST_PASSWORD)) {
				// 注册成功后直接登陆
				String userName = data.getStringExtra(KEY_REGIST_USERNAME);
				etAccountName.setText(userName);
				String password = data.getStringExtra(KEY_REGIST_PASSWORD);
				etAccoutPwd.setText(password);
				Trace.d(TAG, "onActivityResult:userName->" + userName
						+ ",password->" + password);
				this.btnLogin.performClick();
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
