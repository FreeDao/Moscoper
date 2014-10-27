package com.skycober.mineral.account;

import java.io.File;
import java.util.Calendar;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.bitmap.core.BitmapCommonUtils;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.FragmentChangeActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.bean.AvatarRec;
import com.skycober.mineral.bean.UserRec;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseUploadAvatar;
import com.skycober.mineral.network.ResponseUser;
import com.skycober.mineral.product.BigPicImgActivity;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.CalendarUtil;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.NetworkUtil;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.Trace;
import com.skycober.mineral.widget.CommonRemDialog;
import com.skycober.mineral.widget.MyRemDialog;

/**
 * 个人空间
 * 
 * @author 新彬
 * 
 */
public class ZoneActivity extends BaseActivity {
	private static final String TAG = "ZoneActivity";
	private View view;
	private boolean isBlack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = LayoutInflater.from(this).inflate(R.layout.activity_home_page,
				null);
		view = v;
		Intent intent = getIntent();
		isBlack = intent.getBooleanExtra("isBlack", false);
		TextView tvTitle = (TextView) v.findViewById(R.id.title_text);
		tvTitle.setText(R.string.zone_page_title);
		ImageButton btnLeft = (ImageButton) v
				.findViewById(R.id.title_button_left);
		btnLeft.setImageResource(R.drawable.back_btn_selector);
		btnLeft.setOnClickListener(btnListClickListener);
		btnRight = (ImageButton) v.findViewById(R.id.title_button_right);
		btnRight.setImageResource(R.drawable.edit_btn_selector);
		btnRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isLocalUserZone()) {
					if (isUpdate) {
						final MyRemDialog dialog = new MyRemDialog(
								ZoneActivity.this, R.style.dialog);
						dialog.setTitle(R.string.user_basic_dialog_save_title);
						dialog.setMessage(R.string.user_basic_dialog_to_save_message);
						dialog.setPosBtnText(R.string.user_basic_dialog_save_rem_btn_save);
						dialog.setNegBtnText(R.string.user_basic_dialog_save_rem_btn_cancel);
						dialog.setPosBtnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.cancel();
								isUpdate = false;
								btnRight.setImageResource(R.drawable.edit_btn_selector);
								afterToUpdateUserInfo();

							}
						});
						dialog.setNegBtnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.cancel();
							}
						});
						dialog.setCanceledOnTouchOutside(false);
						dialog.show();
					} else {
						isUpdate = true;
						btnRight.setImageResource(R.drawable.titlebar_btn_share_selector);
						reayToUpdateUserInfo();
					}
				}
			}
		});
		init(v);
		setContentView(v);
	}

	private ImageButton btnRight;
	private ImageView ivAvatar;
	private TextView tvUserName, tvSendCount, tvFavCount, tvAttentionCount;
	private TextView tvVisitCount;
	private TextView btnOperation;
	private RelativeLayout headLayout, middleLayout;
	private ViewGroup emailGroup, realNameGroup, sexGroup, birthdayGroup,
			msnGroup, qqGroup, officePhoneGroup, mobilePhoneGroup,
			homePhoneGroup;
	private EditText etEmail, etRealName, etSex, etBirthday, etMsn, etQQ,
			etOfficePhone, etMobilePhone, etHomePhone;
	private boolean isLogin = false;
	private UserRec rec;
	public static boolean isUpdate = false;
	private FinalBitmap avatarFB;

	public static final String KEY_USER_ID = "key_user_id";
	private String currUserId = null;

	private void init(View v) {
		rootView = v;
		Intent startIntent = getIntent();
		if (null != startIntent && startIntent.hasExtra(KEY_USER_ID)) {
			currUserId = startIntent.getStringExtra(KEY_USER_ID);
			if (!isLocalUserZone()) {
				btnRight.setVisibility(View.INVISIBLE);
			}
		}
		readyToLoadUserInfo();
	}

	private void readyToLoadUserInfo() {
		if (!NetworkUtil.getInstance().existNetwork(ZoneActivity.this)) {
			Toast.makeText(ZoneActivity.this, R.string.network_disable_error,
					Toast.LENGTH_LONG).show();
			return;
		}
		final String currMethod = "readyToLoadUserInfo:";
		lockScreen(getString(R.string.user_info_loading));
		UserService us = new UserService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onFailure(Throwable t, String strMsg) {
				releaseScreen();
				Log.e(TAG, currMethod + "onFailure,Msg->" + strMsg, t);
				if (BuildConfig.isDebug) {
					String msg = strMsg;
					if (StringUtil.getInstance().IsEmpty(msg)) {
						msg = null == t ? "Exception t is null." : t.toString();
					}
					ExceptionRemHelper.showExceptionReport(ZoneActivity.this,
							msg);
				} else {
					Toast.makeText(ZoneActivity.this,
							R.string.user_info_load_failed_rem,
							Toast.LENGTH_SHORT).show();
				}
				super.onFailure(t, strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					Trace.d(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseGetUserInfo(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseUser user = (ResponseUser) br;
						UserRec userRec = user.getUserRec();
						if (isLocalUserZone()) {
							SettingUtil.getInstance(ZoneActivity.this)
									.saveUserInfo(userRec.getUserId(), userRec);
						}
						rec = userRec;
						InitPersonalInfo(rootView);
					} else {
						String message = getString(R.string.user_info_load_failed_rem);
						String msg = getString(R.string.user_info_load_failed_rem);
						if (null == br) {
							msg = "parseGetUserInfo: Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {
							default:
								msg = br.getMessage();
								break;
							}
						}
						Log.e(TAG,
								currMethod + "errorCode:" + br.getErrorCode()
										+ "--Message:" + br.getMessage());
						if (BuildConfig.isDebug) {
							ExceptionRemHelper.showExceptionReport(
									ZoneActivity.this, msg);
						} else {
							Toast.makeText(ZoneActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Log.d(TAG, currMethod + "Result t is null");
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								ZoneActivity.this, msg);
					} else {
						Toast.makeText(ZoneActivity.this,
								R.string.user_info_load_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);

			}

		};
		String userId = currUserId;
		us.GetUserInfo(ZoneActivity.this, userId, callBack);
	}

	private boolean hasLogin() {
		String accountUserId = SettingUtil.getInstance(ZoneActivity.this)
				.getValue(SettingUtil.KEY_LOGIN_USER_ID,
						SettingUtil.DEFAULT_LOGIN_USER_ID);
		boolean hasLogin = !accountUserId
				.equalsIgnoreCase(SettingUtil.DEFAULT_LOGIN_USER_ID);
		return hasLogin;
	}

	private boolean isLocalUserZone() {
		String accountUserId = SettingUtil.getInstance(ZoneActivity.this)
				.getValue(SettingUtil.KEY_LOGIN_USER_ID,
						SettingUtil.DEFAULT_LOGIN_USER_ID);
		return hasLogin() && null != currUserId
				&& accountUserId.equalsIgnoreCase(currUserId);
	}

	private View rootView;

	private static final int FOR_LOGIN_RESULT = 1212;
	private static final int BIGPIC_FOR_RESULT = 1222;

	/**
	 * @param root
	 */
	private void InitPersonalInfo(View root) {
		rootView = root;
		middleLayout = (RelativeLayout) root.findViewById(R.id.middle_layout);
		headLayout = (RelativeLayout) root.findViewById(R.id.headLayout);
		ivAvatar = (ImageView) root.findViewById(R.id.user_avatar);

		Trace.d(TAG, "InitPersonalInfo:" + rec.getAvatar().toString());
		if (!StringUtil.getInstance().IsEmpty(rec.getAvatar().getBig())) {
			String url = RequestUrls.SERVER_BASIC_URL + "/"
					+ rec.getAvatar().getBig();
			try {
				avatarFB = FinalBitmap.create(ZoneActivity.this);
				avatarFB.flushCache();
				avatarFB.closeCache();
				avatarFB.display(ivAvatar, url, true);
			} catch (Exception e) {
				Log.e(TAG, "refreshAvatarView:display user avatar error.", e);
			}
		} else {
			Log.e(TAG, "refreshAvatarView:url is empty.");
		}
		ivAvatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ZoneActivity.this,
						BigPicImgActivity.class);
				intent.putExtra(BigPicImgActivity.KET_FOR_BIGPIC, rec
						.getAvatar().getBig());
				startActivityForResult(intent, BIGPIC_FOR_RESULT);
				ZoneActivity.this.overridePendingTransition(
						R.anim.my_scale_action, R.anim.my_alpha_action);
			}
		});

		tvUserName = (TextView) root.findViewById(R.id.user_name);
		tvUserName.setText(rec.getUserName());
		tvSendCount = (TextView) root.findViewById(R.id.send_count);
		long sendCount = rec.getOnSaleNum() + rec.getOffSaleNum();
		tvSendCount.setText(String.valueOf(sendCount));
		tvFavCount = (TextView) root.findViewById(R.id.fav_count);
		long favCount = rec.getFavNum();
		tvFavCount.setText(String.valueOf(favCount));
		// long attend = rec.getAttendNum();

		tvAttentionCount = (TextView) root.findViewById(R.id.attention_count);
		tvAttentionCount.setText(String.valueOf(rec.getAttendNum()));

		tvVisitCount = (TextView) root.findViewById(R.id.visit_count);
		tvVisitCount.setText(String.format(
				getString(R.string.visit_count_format), rec.getVisitCount()));
		btnOperation = (TextView) root.findViewById(R.id.btnOperation);

		initOpeartionButtonStatus();
		btnOperation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isLocalUserZone()) {
					showUploadAvatarRem();
				} else {
					// 添加关注、取消关注
					if (!isBlack) {
						boolean isFollow = rec.isFollowed();
						if (isFollow) {
							readyToCancelAttentionPeople();
						} else {
							readyToAddAttionPeople();
						}
					}else{
						showToast(ZoneActivity.this, "您已屏蔽了该用户，必须取消屏蔽才能关注！");
					}

				}
			}
		});
		emailGroup = (ViewGroup) root.findViewById(R.id.email);
		TextView tvEmailTitle = (TextView) emailGroup
				.findViewById(R.id.info_title);
		tvEmailTitle.setText(R.string.home_page_item_title_email);
		etEmail = (EditText) emailGroup.findViewById(R.id.info_content);

		if (!StringUtil.getInstance().IsEmpty(rec.getEmail())
				&& rec.getEmail() != null) {
			etEmail.setText(rec.getEmail());
		}

		((TextView) emailGroup.findViewById(R.id.info_hint))
				.setVisibility(View.VISIBLE);

		realNameGroup = (ViewGroup) root.findViewById(R.id.real_name);
		TextView tvRealNameTitle = (TextView) realNameGroup
				.findViewById(R.id.info_title);
		tvRealNameTitle.setText(R.string.home_page_item_title_real_name);
		etRealName = (EditText) realNameGroup.findViewById(R.id.info_content);
		if (!StringUtil.getInstance().IsEmpty(rec.getRealName())
				&& rec.getRealName() != null) {
			etRealName.setText(rec.getRealName());
		}

		((TextView) realNameGroup.findViewById(R.id.info_hint))
				.setVisibility(View.VISIBLE);

		sexGroup = (ViewGroup) root.findViewById(R.id.gender);
		TextView tvSexTitle = (TextView) sexGroup.findViewById(R.id.info_title);
		tvSexTitle.setText(R.string.home_page_item_title_sex);
		etSex = (EditText) sexGroup.findViewById(R.id.info_content);
		sexGroup.setBackgroundResource(R.drawable.light_item_bg_selector);

		switch (rec.getSex()) {
		case 0:
			etSex.setText(R.string.sex_secrecy);
			break;
		case 1:
			etSex.setText(R.string.sex_male);
			break;
		case 2:
			etSex.setText(R.string.sex_female);
			break;
		default:
			break;
		}
		((TextView) sexGroup.findViewById(R.id.info_hint))
				.setVisibility(View.VISIBLE);

		birthdayGroup = (ViewGroup) root.findViewById(R.id.birthday);
		TextView tvBirthdayTitle = (TextView) birthdayGroup
				.findViewById(R.id.info_title);
		tvBirthdayTitle.setText(R.string.home_page_item_title_birthday);
		etBirthday = (EditText) birthdayGroup.findViewById(R.id.info_content);

		if (!StringUtil.getInstance().IsEmpty(rec.getBirthday())
				&& rec.getBirthday() != null
				&& !rec.getBirthday().equalsIgnoreCase("0000-00-00")) {
			etBirthday.setText(rec.getBirthday());
		}
		birthdayGroup.setBackgroundResource(R.drawable.light_item_bg_selector);

		((TextView) birthdayGroup.findViewById(R.id.info_hint))
				.setVisibility(View.VISIBLE);
		msnGroup = (ViewGroup) root.findViewById(R.id.msn);
		TextView tvMSNTitle = (TextView) msnGroup.findViewById(R.id.info_title);
		tvMSNTitle.setText(R.string.home_page_item_title_msn);
		etMsn = (EditText) msnGroup.findViewById(R.id.info_content);

		if (!StringUtil.getInstance().IsEmpty(rec.getMsn())
				&& rec.getMsn() != null) {
			etMsn.setText(rec.getMsn());
		}
		((TextView) msnGroup.findViewById(R.id.info_hint))
				.setVisibility(View.VISIBLE);

		qqGroup = (ViewGroup) root.findViewById(R.id.qq);
		TextView tvQQ = (TextView) qqGroup.findViewById(R.id.info_title);
		tvQQ.setText(R.string.home_page_item_title_qq);
		etQQ = (EditText) qqGroup.findViewById(R.id.info_content);
		if (!StringUtil.getInstance().IsEmpty(rec.getQq())
				&& rec.getQq() != null) {
			etQQ.setText(rec.getQq());
		}
		((TextView) qqGroup.findViewById(R.id.info_hint))
				.setVisibility(View.VISIBLE);

		officePhoneGroup = (ViewGroup) root.findViewById(R.id.office_phone);
		TextView tvOfficePhone = (TextView) officePhoneGroup
				.findViewById(R.id.info_title);
		tvOfficePhone.setText(R.string.home_page_item_title_office_phone);
		etOfficePhone = (EditText) officePhoneGroup
				.findViewById(R.id.info_content);
		if (!StringUtil.getInstance().IsEmpty(rec.getOfficePhone())
				&& rec.getOfficePhone() != null) {
			etOfficePhone.setText(rec.getOfficePhone());
		}
		((TextView) officePhoneGroup.findViewById(R.id.info_hint))
				.setVisibility(View.VISIBLE);

		mobilePhoneGroup = (ViewGroup) root.findViewById(R.id.mobile_phone);
		TextView tvMobilePhone = (TextView) mobilePhoneGroup
				.findViewById(R.id.info_title);
		tvMobilePhone.setText(R.string.home_page_item_title_mobile_phone);
		etMobilePhone = (EditText) mobilePhoneGroup
				.findViewById(R.id.info_content);
		if (!StringUtil.getInstance().IsEmpty(rec.getMobilePhone())
				&& rec.getMobilePhone() != null) {
			etMobilePhone.setText(rec.getMobilePhone());
		}
		((TextView) mobilePhoneGroup.findViewById(R.id.info_hint))
				.setVisibility(View.VISIBLE);

		homePhoneGroup = (ViewGroup) root.findViewById(R.id.home_phone);
		TextView tvHomePhone = (TextView) homePhoneGroup
				.findViewById(R.id.info_title);
		tvHomePhone.setText(R.string.home_page_item_title_home_phone);
		etHomePhone = (EditText) homePhoneGroup.findViewById(R.id.info_content);
		if (!StringUtil.getInstance().IsEmpty(rec.getHomePhone())
				&& rec.getHomePhone() != null) {
			etHomePhone.setText(rec.getHomePhone());
		}
		((TextView) homePhoneGroup.findViewById(R.id.info_hint))
				.setVisibility(View.VISIBLE);

	}

	// 当按下编辑资料按钮时操作
	private void reayToUpdateUserInfo() {
		String flag = "updating";
		FragmentChangeActivity.slideMenu.setTag(flag);
		FragmentChangeActivity.slideMenu.setSlidingEnabled(false);
		headLayout.setVisibility(View.GONE);
		middleLayout.setVisibility(View.GONE);
		sexGroup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!v.isEnabled())
					return;
				v.setEnabled(false);
				if (isLogin) {
					showSexSettingRem();
					v.setEnabled(true);
				}

			}
		});
		birthdayGroup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!v.isEnabled())
					return;
				v.setEnabled(false);
				if (isLogin) {
					showBirthdaySettingRem();
					v.setEnabled(true);
				}

			}
		});

		etEmail.setEnabled(isUpdate);
		if (etEmail.getText() != null) {
			etEmail.setHint(R.string.email_hint_text);
		}
		String email = etEmail.getText().toString();
		rec.setEmail(email);
		etRealName.setEnabled(isUpdate);
		if (etRealName.getText() != null) {
			etRealName.setHint(R.string.real_name_hint_text);
		}
		String RealName = etRealName.getText().toString();
		rec.setRealName(RealName);
		etMsn.setEnabled(isUpdate);
		if (etMsn.getText() != null) {
			etMsn.setHint(R.string.msn_hint_text);
		}
		String Msn = etMsn.getText().toString();
		rec.setMsn(Msn);
		etQQ.setEnabled(isUpdate);
		if (etQQ.getText() != null) {
			etQQ.setHint(R.string.qq_hint_text);
		}
		String QQ = etQQ.getText().toString();
		rec.setQq(QQ);
		etOfficePhone.setEnabled(isUpdate);
		if (etOfficePhone.getText() != null) {
			etOfficePhone.setHint(R.string.office_phone_hint_text);
		}
		String OfficePhone = etOfficePhone.getText().toString();
		rec.setOfficePhone(OfficePhone);
		etMobilePhone.setEnabled(isUpdate);
		if (etMobilePhone.getText() != null) {
			etMobilePhone.setHint(R.string.mobile_phone_hint_text);
		}
		String MobilePhone = etMobilePhone.getText().toString();
		rec.setMobilePhone(MobilePhone);
		etHomePhone.setEnabled(isUpdate);
		if (etHomePhone.getText() != null) {
			etHomePhone.setHint(R.string.home_phone_hint_text);
		}
		String HomePhone = etHomePhone.getText().toString();
		rec.setHomePhone(HomePhone);

	}

	// 当保存资料时操作
	private void afterToUpdateUserInfo() {
		FragmentChangeActivity.slideMenu.setSlidingEnabled(true);

		headLayout.setVisibility(View.VISIBLE);
		middleLayout.setVisibility(View.VISIBLE);
		sexGroup.setOnClickListener(null);
		birthdayGroup.setOnClickListener(null);
		etEmail.setEnabled(isUpdate);
		etEmail.setHint(null);
		etRealName.setEnabled(isUpdate);
		etRealName.setHint(null);
		etMsn.setEnabled(isUpdate);
		etMsn.setHint(null);
		etQQ.setEnabled(isUpdate);
		etQQ.setHint(null);
		etOfficePhone.setEnabled(isUpdate);
		etOfficePhone.setHint(null);
		etMobilePhone.setEnabled(isUpdate);
		etMobilePhone.setHint(null);
		etHomePhone.setEnabled(isUpdate);
		etHomePhone.setHint(null);
		reayToSaveUserInfo();

	}

	// 对性别的特殊处理
	private void showSexSettingRem() {
		final Dialog dialog = new Dialog(ZoneActivity.this, R.style.dialog);
		WindowManager m = ZoneActivity.this.getWindowManager();
		Display d = m.getDefaultDisplay();
		WindowManager.LayoutParams p = ZoneActivity.this.getWindow()
				.getAttributes();
		p.width = (int) (d.getWidth() * 0.8);
		final ViewGroup root = (ViewGroup) LayoutInflater.from(
				ZoneActivity.this).inflate(R.layout.sex_setting_content_view,
				null);
		RadioGroup sex = (RadioGroup) root.findViewById(R.id.sex_select);
		Button chance = (Button) root.findViewById(R.id.btn_chance);
		chance.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		sex.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int radioButtonId = group.getCheckedRadioButtonId();
				RadioButton rb = (RadioButton) root.findViewById(radioButtonId);
				if (rb.getText().toString().equals(getText(R.string.sex_male))) {
					rec.setSex(1);
					etSex.setText(R.string.sex_male);
					dialog.dismiss();
				} else if (rb.getText().toString()
						.equals(getText(R.string.sex_female))) {
					rec.setSex(2);
					etSex.setText(R.string.sex_female);
					dialog.dismiss();
				} else {
					rec.setSex(0);
					etSex.setText(R.string.sex_secrecy);
					dialog.dismiss();
				}
			}
		});
		dialog.setContentView(root, p);
		dialog.show();
	}

	// 对生日的特殊处理
	private Calendar birthdayCal = Calendar.getInstance();

	private void showBirthdaySettingRem() {
		DatePickerDialog dialog = new DatePickerDialog(ZoneActivity.this,
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						isUpdate = true;
						Calendar cal = Calendar.getInstance();
						cal.set(Calendar.YEAR, year);
						cal.set(Calendar.MONTH, monthOfYear);
						cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						birthdayCal = cal;
						String birthday = CalendarUtil
								.GetFormatDateStrByCalendar(cal);
						etBirthday.setText(birthday);
						if (null != rec) {
							rec.setBirthday(birthday);
						}
					}
				}, birthdayCal.get(Calendar.YEAR),
				birthdayCal.get(Calendar.MONTH),
				birthdayCal.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}

	private View.OnClickListener btnListClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
		}
	};

	// 更新服务端数据
	private void reayToSaveUserInfo() {
		if (!NetworkUtil.getInstance().existNetwork(ZoneActivity.this)) {
			Toast.makeText(ZoneActivity.this, R.string.network_disable_error,
					Toast.LENGTH_LONG).show();
			return;
		}
		final String currMethod = "readyToSaveUserInfo:";
		if (null == rec) {
			Log.e(TAG, currMethod + "userRec == null");
			Toast.makeText(ZoneActivity.this,
					R.string.user_basic_send_failed_rem, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		lockScreen(getString(R.string.user_basic_send_ing));
		UserService us = new UserService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onFailure(Throwable t, String strMsg) {
				releaseScreen();
				Log.e(TAG, currMethod + "onFailure,Msg->" + strMsg, t);
				if (BuildConfig.isDebug) {
					String msg = strMsg;
					if (StringUtil.getInstance().IsEmpty(msg)) {
						msg = null == t ? "Exception t is null." : t.toString();
					}
					ExceptionRemHelper.showExceptionReport(ZoneActivity.this,
							msg);
				} else {
					Toast.makeText(ZoneActivity.this,
							R.string.user_basic_send_failed_rem,
							Toast.LENGTH_SHORT).show();
				}
				super.onFailure(t, strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					Trace.d(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseUpdateUserInfo(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						Toast.makeText(ZoneActivity.this,
								R.string.user_basic_succeed_rem,
								Toast.LENGTH_SHORT).show();
						ResponseUser user = (ResponseUser) br;
						UserRec userRec = user.getUserRec();
						SettingUtil.getInstance(ZoneActivity.this)
								.saveUserInfo(userRec.getUserId(), userRec);
						String flag = "back";
						FragmentChangeActivity.slideMenu.setTag(flag);
					} else {
						String message = getString(R.string.user_basic_send_failed_rem);
						String msg = getString(R.string.user_basic_send_failed_rem);
						if (null == br) {
							msg = "parseUploadPic: Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {
							default:
								msg = br.getMessage();
								break;
							}
						}
						Log.e(TAG,
								currMethod + "errorCode:" + br.getErrorCode()
										+ "--Message:" + br.getMessage());
						if (BuildConfig.isDebug) {
							ExceptionRemHelper.showExceptionReport(
									ZoneActivity.this, msg);
						} else {
							Toast.makeText(ZoneActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Log.d(TAG, currMethod + "Result t is null");
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								ZoneActivity.this, msg);
					} else {
						Toast.makeText(ZoneActivity.this,
								R.string.get_user_info_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);

			}

		};
		String email = etEmail.getText().toString();
		String realName = etRealName.getText().toString();
		String sex = String.valueOf(rec.getSex());
		String birthday = rec.getBirthday();
		String msn = etMsn.getText().toString();
		String QQ = etQQ.getText().toString();
		String officePhone = etOfficePhone.getText().toString();
		String homePhone = etHomePhone.getText().toString();
		String mobilePhone = etMobilePhone.getText().toString();
		us.updateUserInfo(ZoneActivity.this, email, realName, sex, birthday,
				msn, QQ, officePhone, homePhone, mobilePhone, callBack);
	}

	/**
	 * 上传头像操作
	 */
	private static final int AVATAR_PHOTO_PICKED_WITH_DATA = 4001;

	private void showUploadAvatarRem() {
		CommonRemDialog dialog = new CommonRemDialog(ZoneActivity.this);
		dialog.setTopBtnText(this.getResources().getString(
				R.string.add_photo_from_camera));
		dialog.setDownBtnText(this.getResources().getString(
				R.string.add_photo_from_pics));
		dialog.setCancelBtnText(this.getResources().getString(
				R.string.add_photo_btn_cancel));
		dialog.setOnButtonTopClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				doPickPhotoAction();
			}
		});
		dialog.setOnButtonDownClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 从相册中去获取
				try {
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
					intent.setType("image/*");
					startActivityForResult(intent,
							AVATAR_PHOTO_PICKED_WITH_DATA);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(ZoneActivity.this, "没有找到照片",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		if (null != dialog && !dialog.isShowing()) {
			dialog.show();
		}
	}

	/**
	 * 描述：从照相机获取
	 */
	private void doPickPhotoAction() {
		String status = Environment.getExternalStorageState();
		// 判断是否有SD卡,如果有sd卡存入sd卡在说，没有sd卡直接转换为图片
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			if (null == PHOTO_DIR) {
				PHOTO_DIR = BitmapCommonUtils
						.getExternalCacheDir(ZoneActivity.this);

			}
			doTakePhoto();
		} else {
			Toast.makeText(ZoneActivity.this, "没有可用的存储卡", Toast.LENGTH_LONG)
					.show();
		}
	}

	private static final int AVATAR_CAMERA_WITH_DATA = 4002;

	protected void doTakePhoto() {
		try {
			mFileName = System.currentTimeMillis() + ".jpg";
			mCurrentPhotoFile = new File(PHOTO_DIR, mFileName);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(mCurrentPhotoFile));
			startActivityForResult(intent, AVATAR_CAMERA_WITH_DATA);
		} catch (Exception e) {
			Toast.makeText(ZoneActivity.this, "未找到系统相机程序", Toast.LENGTH_LONG)
					.show();
		}
	}

	/* 拍照的照片存储位置 */
	public static File PHOTO_DIR = null;
	// 照相机拍照得到的图片
	public File mCurrentPhotoFile;
	private String mFileName;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case AVATAR_PHOTO_PICKED_WITH_DATA:
			Uri avatarUri = data.getData();
			String filePath = getPath(avatarUri);
			startToUploadAvatar(filePath);
			break;
		case AVATAR_CAMERA_WITH_DATA:
			String filePath2 = mCurrentPhotoFile.getPath();
			startToUploadAvatar(filePath2);
			break;

		case FOR_LOGIN_RESULT:
			init(view);
			break;
		}
	}

	public void startToUploadAvatar(String path) {
		if (!NetworkUtil.getInstance().existNetwork(ZoneActivity.this)) {
			Toast.makeText(ZoneActivity.this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		lockScreen(this.getResources().getString(
				R.string.upload_avatar_send_ing));
		final String currMethod = "startToUploadAvatar:";
		UserService us = new UserService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onFailure(Throwable t, String strMsg) {
				releaseScreen();
				Log.e(TAG, currMethod + "onFailure,Msg->" + strMsg, t);
				if (BuildConfig.isDebug) {
					String msg = strMsg;
					if (StringUtil.getInstance().IsEmpty(msg)) {
						msg = null == t ? "Exception t is null." : t.toString();
					}
					ExceptionRemHelper.showExceptionReport(ZoneActivity.this,
							msg);
				} else {
					Toast.makeText(ZoneActivity.this,
							R.string.upload_avatar_failed_rem,
							Toast.LENGTH_SHORT).show();
				}
				super.onFailure(t, strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					Trace.d(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseUploadAvatar(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseUploadAvatar response = (ResponseUploadAvatar) br;
						AvatarRec prec = response.getAvatarRec();
						if (null != prec
								&& !StringUtil.getInstance().IsEmpty(
										prec.getNormal())) {
							String url = RequestUrls.SERVER_BASIC_URL + "/"
									+ prec.getBig();
							refreshAvatarView(url);
							rec.setAvatar(prec);
							SettingUtil.getInstance(ZoneActivity.this)
									.saveUserAvatar(rec.getUserId(), prec);

						} else {
							if (BuildConfig.isDebug) {
								String msg = "prec == null:"
										+ (prec == null)
										+ ",middle:"
										+ (null == prec ? "null" : prec
												.getNormal());
								ExceptionRemHelper.showExceptionReport(
										ZoneActivity.this, msg);
							} else {
								Toast.makeText(ZoneActivity.this,
										R.string.upload_avatar_failed_rem,
										Toast.LENGTH_SHORT).show();
							}
						}
					} else {
						String message = ZoneActivity.this.getResources()
								.getString(R.string.upload_avatar_failed_rem);
						String msg = ZoneActivity.this.getResources()
								.getString(R.string.upload_avatar_failed_rem);
						if (null == br) {
							msg = "parseUploadAvatar: Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {

							default:
								msg = br.getMessage();
								message = br.getMessage();
								break;
							}
						}
						Log.e(TAG,
								currMethod + "errorCode:" + br.getErrorCode()
										+ "--Message:" + br.getMessage());
						if (BuildConfig.isDebug) {
							ExceptionRemHelper.showExceptionReport(
									ZoneActivity.this, msg);
						} else {
							Toast.makeText(ZoneActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Log.d(TAG, currMethod + "Result t is null");
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								ZoneActivity.this, msg);
					} else {
						Toast.makeText(ZoneActivity.this,
								R.string.upload_avatar_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};

		File userFile = new File(path);
		String fileName = userFile.getName();
		us.UploadAvatar(ZoneActivity.this, fileName, userFile, callBack);
	}

	public void refreshAvatarView(String url) {
		Trace.d(TAG, "refreshAvatarView:" + rec.getAvatar().toString());
		if (!StringUtil.getInstance().IsEmpty(url)) {
			try {
				avatarFB = FinalBitmap.create(ZoneActivity.this);
				avatarFB.flushCache();
				avatarFB.closeCache();
				avatarFB.configCalculateBitmapSizeWhenDecode(true);
				avatarFB.display(ivAvatar, url, true);
			} catch (Exception e) {
				Log.e(TAG, "refreshAvatarView:display user avatar error.", e);
			}
		} else {
			Log.e(TAG, "refreshAvatarView:url is empty.");
		}
	}

	public String getPath(Uri uri) {
		if (null == uri || uri.getAuthority().equals("")) {
			return null;
		}
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = ZoneActivity.this.managedQuery(uri, projection, null,
				null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(column_index);
		return path;
	}

	protected void readyToAddAttionPeople() {
		if (!NetworkUtil.getInstance().existNetwork(ZoneActivity.this)) {
			releaseScreen();
			Toast.makeText(ZoneActivity.this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		final String currMethod = "readyToAddAttionCategory:";
		lockScreen(getString(R.string.add_attention_people_send_ing));
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onFailure(Throwable t, String strMsg) {
				releaseScreen();
				Log.e(TAG, currMethod + "onFailure,Msg->" + strMsg, t);
				if (BuildConfig.isDebug) {
					String msg = strMsg;
					if (StringUtil.getInstance().IsEmpty(msg)) {
						msg = null == t ? "Exception t is null." : t.toString();
					}
					ExceptionRemHelper.showExceptionReport(ZoneActivity.this,
							msg);
				} else {
					Toast.makeText(ZoneActivity.this,
							R.string.add_attention_people_failed_rem,
							Toast.LENGTH_SHORT).show();
				}
				super.onFailure(t, strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					Trace.d(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseAddAttentionTags(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						if (null != rec) {
							rec.setFollowed(true);
							long attentionCount = Long
									.parseLong(tvAttentionCount.getText()
											.toString()) + 1;
							tvAttentionCount.setText(String
									.valueOf(attentionCount));
						}
						initOpeartionButtonStatus();
						Toast.makeText(ZoneActivity.this,
								R.string.add_attention_people_succeed_rem,
								Toast.LENGTH_SHORT).show();
					} else {
						String message = getString(R.string.add_attention_people_failed_rem);
						String msg = getString(R.string.add_attention_people_failed_rem);
						if (null == br) {
							msg = currMethod + ": Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {

							default:
								msg = br.getMessage();
								message = br.getMessage();
								break;
							}
						}
						Log.e(TAG,
								currMethod + "errorCode:" + br.getErrorCode()
										+ "--Message:" + br.getMessage());
						if (BuildConfig.isDebug) {
							ExceptionRemHelper.showExceptionReport(
									ZoneActivity.this, msg);
						} else {
							Toast.makeText(ZoneActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								ZoneActivity.this, msg);
					} else {
						Toast.makeText(ZoneActivity.this,
								R.string.add_attention_people_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
		String type = String.valueOf(1);
		String followId = rec.getUserId();
		if (null == followId)
			followId = "";
		gs.AddMyAttentionTags(ZoneActivity.this, type, followId, callBack);
	}

	protected void readyToCancelAttentionPeople() {
		if (!NetworkUtil.getInstance().existNetwork(ZoneActivity.this)) {
			Toast.makeText(ZoneActivity.this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		final String currMethod = "readyToRemoveAttention:";
		lockScreen(getString(R.string.cancel_attention_people_send_ing));
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onFailure(Throwable t, String strMsg) {
				releaseScreen();
				Log.e(TAG, currMethod + "onFailure,Msg->" + strMsg, t);
				if (BuildConfig.isDebug) {
					String msg = strMsg;
					if (StringUtil.getInstance().IsEmpty(msg)) {
						msg = null == t ? "Exception t is null." : t.toString();
					}
					ExceptionRemHelper.showExceptionReport(ZoneActivity.this,
							msg);
				} else {
					Toast.makeText(ZoneActivity.this,
							R.string.cancel_attention_people_failed_rem,
							Toast.LENGTH_SHORT).show();
				}
				super.onFailure(t, strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				if (null != t) {
					String json = t.toString();
					Trace.d(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseRemoveTag(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						releaseScreen();
						if (null != rec) {
							rec.setFollowed(false);
							long attentionCount = Long
									.parseLong(tvAttentionCount.getText()
											.toString()) - 1;
							tvAttentionCount.setText(String
									.valueOf(attentionCount));
						}
						initOpeartionButtonStatus();
						Toast.makeText(ZoneActivity.this,
								R.string.cancel_attention_people_succeed_rem,
								Toast.LENGTH_SHORT).show();

					} else {
						releaseScreen();
						String message = getString(R.string.cancel_attention_people_failed_rem);
						String msg = getString(R.string.cancel_attention_people_failed_rem);
						if (null == br) {
							msg = "CancelAttention: Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {

							default:
								msg = br.getMessage();
								message = br.getMessage();
								break;
							}
						}
						Log.e(TAG,
								currMethod + "errorCode:" + br.getErrorCode()
										+ "--Message:" + br.getMessage());
						if (BuildConfig.isDebug) {
							ExceptionRemHelper.showExceptionReport(
									ZoneActivity.this, msg);
						} else {
							Toast.makeText(ZoneActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					releaseScreen();
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								ZoneActivity.this, msg);
					} else {
						Toast.makeText(ZoneActivity.this,
								R.string.cancel_attention_people_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
		String typeId = String.valueOf(1);
		String followId = rec.getUserId();
		if (null == followId)
			followId = "";
		gs.RemoveTag(ZoneActivity.this, typeId, followId, callBack);
	}

	private void initOpeartionButtonStatus() {
		if (hasLogin()) {
			if (!isLocalUserZone()) {
				boolean isFollowed = rec.isFollowed();
				if (isFollowed) {
					// btnOperation.setBackgroundResource(R.drawable.near_by_btn);
					btnOperation.setText(R.string.zone_btn_minus_attention);
				} else {
					// btnOperation.setBackgroundResource(R.drawable.near_by_red_btn);
					btnOperation.setText(R.string.zone_btn_add_attention);
				}
			}
		} else {
			if (!isLocalUserZone()) {
				btnOperation.setVisibility(View.INVISIBLE);
			}
		}
	}
}
