package com.skycober.mineral.account;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

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
import android.provider.ContactsContract.Data;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.Window;
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

import com.ab.util.AbStrUtil;
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
import com.skycober.mineral.product.AddProductActivity;
import com.skycober.mineral.product.BigPicImgActivity;
import com.skycober.mineral.product.CropImageActivity;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.CalendarUtil;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.NetworkUtil;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.Trace;
import com.skycober.mineral.util.Util;
import com.skycober.mineral.widget.CommonRemDialog;
import com.skycober.mineral.widget.MyRemDialog;

/**
 * ������ҳ ���������ģ�
 * 
 * @author �±�
 * 
 */
public class HomePageActivity extends BaseActivity {
	private static final String TAG = "HomePageActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);
		Intent intent = getIntent();

		initLayout();
		TextView tvTitle = (TextView) findViewById(R.id.title_text);
		if (intent.hasExtra("isZone")) {
			tvTitle.setText(R.string.zone_page_title);
		} else {
			tvTitle.setText(R.string.home_page_title);
		}

		// ����
		ImageButton btnLeft = (ImageButton) findViewById(R.id.title_button_left);
		btnLeft.setImageResource(R.drawable.back_btn_selector);
		btnLeft.setOnClickListener(btnListClickListener);
		// �޸�
		btnRight = (ImageButton) findViewById(R.id.title_button_right);
		btnRight.setImageResource(R.drawable.edit_btn_selector);
		btnRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isUpdate) {
					final MyRemDialog dialog = new MyRemDialog(
							HomePageActivity.this, R.style.dialog);
					dialog.setTitle(R.string.user_basic_dialog_save_title);
					dialog.setMessage(R.string.user_basic_dialog_to_save_message);
					dialog.setPosBtnText(R.string.user_basic_dialog_save_rem_btn_save);
					dialog.setNegBtnText(R.string.user_basic_dialog_save_rem_btn_cancel);
					dialog.setPosBtnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							isUpdate = false;
							btnRight.setImageResource(R.drawable.edit_btn_selector);
							// �޸�
							afterToUpdateUserInfo();

						}
					});
					dialog.setNegBtnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.show();
				} else {
					// �༭
					isUpdate = true;
					btnRight.setImageResource(R.drawable.titlebar_btn_share_selector);
					reayToUpdateUserInfo();
				}
			}
		});
		init();
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

	private void init() {

		boolean hasLogin = !SettingUtil
				.getInstance(HomePageActivity.this)
				.getValue(SettingUtil.KEY_LOGIN_USER_ID,
						SettingUtil.DEFAULT_LOGIN_USER_ID)
				.equalsIgnoreCase(SettingUtil.DEFAULT_LOGIN_USER_ID);
		if (hasLogin != isLogin) {
			isLogin = hasLogin;
			if (null != SettingUtil.getInstance(HomePageActivity.this)
					.getValue(SettingUtil.KEY_LOGIN_USER_ID,
							SettingUtil.DEFAULT_LOGIN_USER_ID)) {
				String userId = SettingUtil.getInstance(HomePageActivity.this)
						.getValue(SettingUtil.KEY_LOGIN_USER_ID,
								SettingUtil.DEFAULT_LOGIN_USER_ID);
				rec = SettingUtil.getInstance(HomePageActivity.this)
						.getUserInfo(userId, null);
			}
			// ��ȡ�û���Ϣ
			readyToLoadUserInfo();
		} else {
			Intent intent = new Intent(HomePageActivity.this,
					LoginActivity.class);
			startActivityForResult(intent, FOR_LOGIN_RESULT);
		}
	}

	// ��ȡ�û���Ϣ
	private void readyToLoadUserInfo() {
		if (!NetworkUtil.getInstance().existNetwork(HomePageActivity.this)) {
			Toast.makeText(HomePageActivity.this,
					R.string.network_disable_error, Toast.LENGTH_LONG).show();
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
					ExceptionRemHelper.showExceptionReport(
							HomePageActivity.this, msg);
				} else {
					Toast.makeText(HomePageActivity.this,
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
						SettingUtil.getInstance(HomePageActivity.this)
								.saveUserInfo(userRec.getUserId(), userRec);
						rec = userRec;
						InitPersonalInfo();
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
									HomePageActivity.this, msg);
						} else {
							Toast.makeText(HomePageActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Log.d(TAG, currMethod + "Result t is null");
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								HomePageActivity.this, msg);
					} else {
						Toast.makeText(HomePageActivity.this,
								R.string.user_info_load_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);

			}

		};
		String userId = SettingUtil.getInstance(HomePageActivity.this)
				.getValue(SettingUtil.KEY_LOGIN_USER_ID,
						SettingUtil.DEFAULT_LOGIN_USER_ID);
		us.GetUserInfo(HomePageActivity.this, userId, callBack);
	}

	private static final int FOR_LOGIN_RESULT = 1212;

	/**
	 * ����ʼ���û���Ϣ��
	 * 
	 * @param root
	 */
	private static final int BIGPIC_FOR_RESULT = 1222;

	public void initLayout() {
		middleLayout = (RelativeLayout) findViewById(R.id.middle_layout);

		headLayout = (RelativeLayout) findViewById(R.id.headLayout);
		// ͷ��
		ivAvatar = (ImageView) findViewById(R.id.user_avatar);
		// �û���
		tvUserName = (TextView) findViewById(R.id.user_name);
		// ������
		tvSendCount = (TextView) findViewById(R.id.send_count);
		// �ղ���
		tvFavCount = (TextView) findViewById(R.id.fav_count);
		// ��ע��
		tvAttentionCount = (TextView) findViewById(R.id.attention_count);
		// �ÿ�
		tvVisitCount = (TextView) findViewById(R.id.visit_count);
		// ����ͷ��
		btnOperation = (TextView) findViewById(R.id.btnOperation);
	}

	private void InitPersonalInfo() {
		// ����ͷ��

		Trace.d(TAG, "InitPersonalInfo:" + rec.getAvatar().toString());
		if (!StringUtil.getInstance().IsEmpty(rec.getAvatar().getBig())) {
			String url = RequestUrls.SERVER_BASIC_URL
					+ rec.getAvatar().getBig();
			try {
				avatarFB = FinalBitmap.create(HomePageActivity.this);
				avatarFB.flushCache();
				avatarFB.closeCache();
//				avatarFB.configLoadfailImage(R.drawable.menu_default_head);
				avatarFB.display(ivAvatar, url,true);
//				avatarFB.display(ivAvatar, url, false);
			} catch (Exception e) {
				Log.e(TAG, "refreshAvatarView:display user avatar error.", e);
			}
		} else {
			Log.e(TAG, "refreshAvatarView:url is empty.");
		}
		ivAvatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomePageActivity.this,
						BigPicImgActivity.class);
				intent.putExtra(BigPicImgActivity.KET_FOR_BIGPIC, rec
						.getAvatar().getBig());
				startActivityForResult(intent, BIGPIC_FOR_RESULT);
				HomePageActivity.this.overridePendingTransition(
						R.anim.my_scale_action, R.anim.my_alpha_action);
			}
		});

		tvUserName.setText(rec.getUserName());

		long sendCount = rec.getOnSaleNum() + rec.getOffSaleNum();
		tvSendCount.setText(String.valueOf(sendCount));

		long favCount = rec.getFavNum();
		tvFavCount.setText(String.valueOf(favCount));

		tvAttentionCount.setText(rec.getAttendNum());

		tvVisitCount.setText(String.format(
				getString(R.string.visit_count_format), rec.getVisitCount()));

		btnOperation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showChangeLogoRem();
			}
		});

		// ��������
		emailGroup = (ViewGroup) findViewById(R.id.email);
		TextView tvEmailTitle = (TextView) emailGroup
				.findViewById(R.id.info_title);
		tvEmailTitle.setText(R.string.home_page_item_title_email);
		etEmail = (EditText) emailGroup.findViewById(R.id.info_content);
		System.out.println("====etEmail==" + rec.getEmail());
		String regMacth = "[\\w]+@[\\w]+.[\\w]+";

		if (!StringUtil.getInstance().IsEmpty(rec.getEmail())
				&& rec.getEmail() != null && rec.getEmail().matches(regMacth)) {

			etEmail.setText(rec.getEmail());
		} else {
			etEmail.setText("");
		}

		((TextView) emailGroup.findViewById(R.id.info_hint))
				.setVisibility(View.VISIBLE);

		// ��ʵ����
		realNameGroup = (ViewGroup) findViewById(R.id.real_name);
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

		// �Ա�
		sexGroup = (ViewGroup) findViewById(R.id.gender);
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

		// ����
		birthdayGroup = (ViewGroup) findViewById(R.id.birthday);
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

		// MSN
		msnGroup = (ViewGroup) findViewById(R.id.msn);
		TextView tvMSNTitle = (TextView) msnGroup.findViewById(R.id.info_title);
		tvMSNTitle.setText(R.string.home_page_item_title_msn);
		etMsn = (EditText) msnGroup.findViewById(R.id.info_content);

		if (!StringUtil.getInstance().IsEmpty(rec.getMsn())
				&& rec.getMsn() != null) {
			etMsn.setText(rec.getMsn());
		}
		((TextView) msnGroup.findViewById(R.id.info_hint))
				.setVisibility(View.VISIBLE);

		// QQ
		qqGroup = (ViewGroup) findViewById(R.id.qq);
		TextView tvQQ = (TextView) qqGroup.findViewById(R.id.info_title);
		tvQQ.setText(R.string.home_page_item_title_qq);
		etQQ = (EditText) qqGroup.findViewById(R.id.info_content);
		if (!StringUtil.getInstance().IsEmpty(rec.getQq())
				&& rec.getQq() != null) {
			etQQ.setText(rec.getQq());
		}
		((TextView) qqGroup.findViewById(R.id.info_hint))
				.setVisibility(View.VISIBLE);

		// �����绰
		officePhoneGroup = (ViewGroup) findViewById(R.id.office_phone);
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

		// �ֻ�
		mobilePhoneGroup = (ViewGroup) findViewById(R.id.mobile_phone);
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

		// ����
		homePhoneGroup = (ViewGroup) findViewById(R.id.home_phone);
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

		// attentionGroup = (ViewGroup) findViewById(R.id.attention);
		// TextView tvAttention = (TextView) attentionGroup
		// .findViewById(R.id.info_title);
		// tvAttention.setText(R.string.home_page_item_title_attention);
		// etAttention = (EditText)
		// attentionGroup.findViewById(R.id.info_content);
		// ((TextView) attentionGroup.findViewById(R.id.info_hint))
		// .setText(R.string.attention_hint_text);
		// // ((TextView)
		// //
		// attentionGroup.findViewById(R.id.info_hint)).setVisibility(View.VISIBLE);
	}

	// �����±༭���ϰ�ťʱ����
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
					// �����Ա�
					showSexSettingRem();
					v.setEnabled(true);
				}

			}
		});
		// ��������
		birthdayGroup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!v.isEnabled())
					return;
				v.setEnabled(false);
				if (isLogin) {
					// ��������
					showBirthdaySettingRem();
					v.setEnabled(true);
				}

			}
		});

		// ��������
		etEmail.setEnabled(isUpdate);
		if (etEmail.getText() != null) {
			etEmail.setHint(R.string.email_hint_text);
		}
		String email = etEmail.getText().toString();
		String regMacth = "[\\w]+@[\\w]+.[\\w]+";
		if (email.matches(regMacth)) {
			rec.setEmail(email);
		}

		// ������ʵ����
		etRealName.setEnabled(isUpdate);
		if (etRealName.getText() != null) {
			etRealName.setHint(R.string.real_name_hint_text);
		}
		String RealName = etRealName.getText().toString();
		rec.setRealName(RealName);

		// ����MSN
		etMsn.setEnabled(isUpdate);
		if (etMsn.getText() != null) {
			etMsn.setHint(R.string.msn_hint_text);
		}
		String Msn = etMsn.getText().toString();
		rec.setMsn(Msn);

		// ����qq
		etQQ.setEnabled(isUpdate);
		if (etQQ.getText() != null) {
			etQQ.setHint(R.string.qq_hint_text);
		}
		String QQ = etQQ.getText().toString();
		rec.setQq(QQ);

		// ���Ĺ����绰
		etOfficePhone.setEnabled(isUpdate);
		if (etOfficePhone.getText() != null) {
			etOfficePhone.setHint(R.string.office_phone_hint_text);
		}
		String OfficePhone = etOfficePhone.getText().toString();
		rec.setOfficePhone(OfficePhone);

		// �����ֻ�
		etMobilePhone.setEnabled(isUpdate);
		if (etMobilePhone.getText() != null) {
			etMobilePhone.setHint(R.string.mobile_phone_hint_text);
		}
		String MobilePhone = etMobilePhone.getText().toString();
		rec.setMobilePhone(MobilePhone);

		// ��������
		etHomePhone.setEnabled(isUpdate);
		if (etHomePhone.getText() != null) {
			etHomePhone.setHint(R.string.home_phone_hint_text);
		}
		String HomePhone = etHomePhone.getText().toString();
		rec.setHomePhone(HomePhone);

	}

	// ����������ʱ����
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
		// �����û���Ϣ
		reayToSaveUserInfo();

	}

	// ���Ա�����⴦��
	private void showSexSettingRem() {
		final Dialog dialog = new Dialog(HomePageActivity.this, R.style.dialog);
		WindowManager m = HomePageActivity.this.getWindowManager();
		Display d = m.getDefaultDisplay();
		WindowManager.LayoutParams p = HomePageActivity.this.getWindow()
				.getAttributes();
		p.width = (int) (d.getWidth() * 0.8);
		final ViewGroup root = (ViewGroup) LayoutInflater.from(
				HomePageActivity.this).inflate(
				R.layout.sex_setting_content_view, null);
		RadioGroup sex = (RadioGroup) root.findViewById(R.id.sex_select);
		if (rec.getSex() == 1) {
			((RadioButton) root.findViewById(R.id.male)).setChecked(true);

		} else if (rec.getSex() == 2) {
			((RadioButton) root.findViewById(R.id.female)).setChecked(true);
		} else {
			((RadioButton) root.findViewById(R.id.secrecy)).setChecked(true);
		}
		Button chance = (Button) root.findViewById(R.id.btn_chance);
		chance.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		sex.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {
				case R.id.male:
					rec.setSex(1);
					etSex.setText(R.string.sex_male);
					dialog.dismiss();
					break;
				case R.id.female:
					rec.setSex(2);
					etSex.setText(R.string.sex_female);
					dialog.dismiss();
					break;
				case R.id.secrecy:
					rec.setSex(0);
					etSex.setText(R.string.sex_secrecy);
					dialog.dismiss();
					break;
				}
				// System.out.println("===="+checkedId);
				// int radioButtonId = group.getCheckedRadioButtonId();
				// RadioButton rb = (RadioButton) findViewById(radioButtonId);
				// System.out.println("====4444444 "+(rb==null));
				// System.out.println("====111111 "+checkedId);
				// System.out.println("====33333 "+rb.getText().toString());
				// if
				// (rb.getText().toString().equals(getText(R.string.sex_male)))
				// {
				//
				// System.out.println("====22222 "+checkedId);
				// rec.setSex(1);
				// etSex.setText(R.string.sex_male);
				// dialog.dismiss();
				// } else if (rb.getText().toString()
				// .equals(getText(R.string.sex_female))) {
				// rec.setSex(2);
				// etSex.setText(R.string.sex_female);
				// dialog.dismiss();
				// } else {
				// rec.setSex(0);
				// etSex.setText(R.string.sex_secrecy);
				// dialog.dismiss();
				// }
			}
		});

		dialog.setContentView(root, p);
		p.width = d.getWidth();
		dialog.show();
	}

	// �����յ����⴦��
	private Calendar birthdayCal = Calendar.getInstance();

	private void showBirthdaySettingRem() {
		DatePickerDialog dialog = new DatePickerDialog(HomePageActivity.this,
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
						Date date = new Date(System.currentTimeMillis());
						int y = date.getYear();
						int m = date.getMonth();
						int d = date.getDate();
						int yTemp = year - 1900;

						if ((y > yTemp)
								|| (y == yTemp && m > monthOfYear)
								|| (y == yTemp && m == monthOfYear && d >= dayOfMonth)) {
							String birthday = CalendarUtil
									.GetFormatDateStrByCalendar(cal);
							etBirthday.setText(birthday);
							if (null != rec) {
								rec.setBirthday(birthday);
							}
						} else {
							Toast.makeText(getApplicationContext(),
									"�ף�����û�����أ�����", 1).show();
						}

					}
				}, birthdayCal.get(Calendar.YEAR), birthdayCal
						.get(Calendar.MONTH), birthdayCal
						.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}

	// ����
	private View.OnClickListener btnListClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			if (isUpdate) {
				isUpdate = false;
				final MyRemDialog dialog = new MyRemDialog(
						HomePageActivity.this, R.style.dialog);
				dialog.setTitle(R.string.user_basic_dialog_save_rem_title);
				dialog.setMessage(R.string.user_basic_dialog_to_save_message);
				dialog.setPosBtnText(R.string.user_basic_dialog_save_rem_btn_save);
				dialog.setNegBtnText(R.string.user_basic_dialog_save_rem_btn_cancel);
				dialog.setPosBtnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						isUpdate = false;
						btnRight.setImageResource(R.drawable.edit_btn_selector);
						afterToUpdateUserInfo();
					}
				});
				dialog.setNegBtnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.show();
			} else {
				finish();
			}
		}
	};

	// ���·��������
	private void reayToSaveUserInfo() {
		if (!NetworkUtil.getInstance().existNetwork(HomePageActivity.this)) {
			Toast.makeText(HomePageActivity.this,
					R.string.network_disable_error, Toast.LENGTH_LONG).show();
			return;
		}
		final String currMethod = "readyToSaveUserInfo:";
		if (null == rec) {
			Log.e(TAG, currMethod + "userRec == null");
			Toast.makeText(HomePageActivity.this,
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
					ExceptionRemHelper.showExceptionReport(
							HomePageActivity.this, msg);
				} else {
					Toast.makeText(HomePageActivity.this,
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
						Toast.makeText(HomePageActivity.this,
								R.string.user_basic_succeed_rem,
								Toast.LENGTH_SHORT).show();
						ResponseUser user = (ResponseUser) br;
						UserRec userRec = user.getUserRec();
						SettingUtil.getInstance(HomePageActivity.this)
								.saveUserInfo(userRec.getUserId(), userRec);
						String flag = "back";
						FragmentChangeActivity.slideMenu.setTag(flag);
					} else {
						InitPersonalInfo();
						String message = getString(R.string.user_basic_send_failed_rem);
						String msg = getString(R.string.user_basic_send_failed_rem);
						if (null == br) {
							msg = "parseUploadPic: Result:BaseResponse is null.";
						} else {
							message = br.getMessage();
						}
						int errorCode = br.getErrorCode();
						switch (errorCode) {
						default:
							msg = br.getMessage();
							break;
						// }
						}
						Log.e(TAG,
								currMethod + "errorCode:" + br.getErrorCode()
										+ "--Message:" + br.getMessage());
						if (BuildConfig.isDebug) {
							ExceptionRemHelper.showExceptionReport(
									HomePageActivity.this, msg);
						} else {
							Toast.makeText(HomePageActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Log.d(TAG, currMethod + "Result t is null");
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								HomePageActivity.this, msg);
					} else {
						Toast.makeText(HomePageActivity.this,
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
		us.updateUserInfo(HomePageActivity.this, email, realName, sex,
				birthday, msn, QQ, officePhone, homePhone, mobilePhone,
				callBack);
	}

	/**
	 * �ϴ�ͷ�����
	 */
	private static final int AVATAR_PHOTO_PICKED_WITH_DATA = 4001;
	// private static final int FROM_PHOTOS_GET_DATA = 0x10001;
	// private static final int FROM_CAMERA_GET_DATA = 0x10002;
	// private static final int AVATAR_CAMERA_CROP_DATA = 0x10003;

	private Dialog selectLogoDialog;

	protected void showChangeLogoRem() {
		if (null == selectLogoDialog) {
			selectLogoDialog = new Dialog(this, R.style.dialog);
			ViewGroup mRoot = (ViewGroup) LayoutInflater.from(this).inflate(
					R.layout.select_photo_dialog_content_view, null);
			int width = getWindowManager().getDefaultDisplay().getWidth();
			selectLogoDialog.setContentView(mRoot, new LayoutParams(width,
					LayoutParams.WRAP_CONTENT));
			Button btnCamera = (Button) mRoot.findViewById(R.id.btnCamera);
			Button btnAlbum = (Button) mRoot.findViewById(R.id.btnAlbum);
			Button btnCancel = (Button) mRoot.findViewById(R.id.btnCancel);
			btnCamera.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					selectLogoDialog.cancel();
					// ʹ���������
					doPickPhotoAction();
				}
			});
			btnAlbum.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					selectLogoDialog.cancel();
					// �������ȥ��ȡ
					try {
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
								null);
						intent.setType("image/*");
						startActivityForResult(intent,
								AVATAR_PHOTO_PICKED_WITH_DATA);
					} catch (ActivityNotFoundException e) {
						Toast.makeText(HomePageActivity.this, "û���ҵ���Ƭ",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			btnCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					selectLogoDialog.cancel();
				}
			});
		}
		Window window = selectLogoDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.mystyle);
		selectLogoDialog.setCanceledOnTouchOutside(true);
		if (null != selectLogoDialog && !selectLogoDialog.isShowing()
				&& !isFinishing()) {
			selectLogoDialog.show();
		}
	}

	// private void showUploadAvatarRem() {
	// CommonRemDialog dialog = new CommonRemDialog(HomePageActivity.this);
	// Window window = dialog.getWindow();
	// window.setGravity(Gravity.BOTTOM);
	// window.setWindowAnimations(R.style.mystyle);
	// dialog.setTopBtnText(this.getResources().getString(
	// R.string.add_photo_from_camera));
	// dialog.setDownBtnText(this.getResources().getString(
	// R.string.add_photo_from_pics));
	// dialog.setCancelBtnText(this.getResources().getString(
	// R.string.add_photo_btn_cancel));
	// dialog.setOnButtonTopClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// doPickPhotoAction();
	// }
	// });
	// dialog.setOnButtonDownClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // �������ȥ��ȡ
	// try {
	// Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
	// intent.setType("image/*");
	// startActivityForResult(intent,
	// AVATAR_PHOTO_PICKED_WITH_DATA);
	// } catch (ActivityNotFoundException e) {
	// Toast.makeText(HomePageActivity.this, "û���ҵ���Ƭ",
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	// });
	// if (null != dialog && !dialog.isShowing()) {
	// dialog.show();
	// }
	// }

	/**
	 * ���������������ȡ
	 */
	private void doPickPhotoAction() {
		String status = Environment.getExternalStorageState();
		// �ж��Ƿ���SD��,�����sd������sd����˵��û��sd��ֱ��ת��ΪͼƬ
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			if (null == PHOTO_DIR) {
				PHOTO_DIR = BitmapCommonUtils
						.getExternalCacheDir(HomePageActivity.this);

			}
			doTakePhoto();
		} else {
			Toast.makeText(HomePageActivity.this, "û�п��õĴ洢��", Toast.LENGTH_LONG)
					.show();
		}
	}

	private static final int AVATAR_CAMERA_WITH_DATA = 4002;

	protected void doTakePhoto() {
		try {
			// mFileName = System.currentTimeMillis() + ".jpg";
			// mCurrentPhotoFile = new File(PHOTO_DIR, mFileName);
			// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE,
			// null);
			// intent.putExtra(MediaStore.EXTRA_OUTPUT,
			// Uri.fromFile(mCurrentPhotoFile));
			// startActivityForResult(intent, AVATAR_CAMERA_WITH_DATA);

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, AVATAR_CAMERA_WITH_DATA);
		} catch (Exception e) {
			Toast.makeText(HomePageActivity.this, "δ�ҵ�ϵͳ�������",
					Toast.LENGTH_LONG).show();
		}
	}

	/* ���յ���Ƭ�洢λ�� */
	public static File PHOTO_DIR = null;
	// ��������յõ���ͼƬ
	public File mCurrentPhotoFile;
	private String mFileName;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		switch (requestCode) {
		case AVATAR_PHOTO_PICKED_WITH_DATA:
			if (data != null) {
				Uri avatarUri = data.getData();
				String filePath = getPath(avatarUri);
				System.out.println("===filePath====" + filePath);
				// startToUploadAvatar(filePath);
				if (!AbStrUtil.isEmpty(filePath)) {
					Intent intent1 = new Intent(HomePageActivity.this,
							CropImageActivity.class);
					intent1.putExtra("PATH", filePath);
					startActivityForResult(intent1,
							AVATAR_CAMERA_CROP_DATA_FOR_SINGLE);
				} else {
					Toast.makeText(HomePageActivity.this, "δ�ڴ洢�����ҵ�����ļ�",
							Toast.LENGTH_LONG).show();
				}
			}

			break;
		case AVATAR_CAMERA_WITH_DATA:
			// String filePath2 = mCurrentPhotoFile.getPath();
			String path = Util.getPhotoPath(HomePageActivity.this, data);
			if (path != null) {
				Intent intent3 = new Intent(HomePageActivity.this,
						CropImageActivity.class);
				intent3.putExtra("PATH", path);
				startActivityForResult(intent3,
						AVATAR_CAMERA_CROP_DATA_FOR_SINGLE);
				// startToUploadAvatar(path);
			}

			break;
		case AVATAR_CAMERA_CROP_DATA_FOR_SINGLE:
			if (data != null) {
				String avatarPath2 = data.getStringExtra("PATH");
				startToUploadAvatar(avatarPath2);
			}

			break;
		case FOR_LOGIN_RESULT:
			init();
			break;
		}
	}

	private static final int AVATAR_CAMERA_CROP_DATA_FOR_SINGLE = 0x30003;

	public void startToUploadAvatar(String path) {
		if (!NetworkUtil.getInstance().existNetwork(HomePageActivity.this)) {
			Toast.makeText(HomePageActivity.this,
					R.string.network_disable_error, Toast.LENGTH_SHORT).show();
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
					ExceptionRemHelper.showExceptionReport(
							HomePageActivity.this, msg);
				} else {
					Toast.makeText(HomePageActivity.this,
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
							SettingUtil.getInstance(HomePageActivity.this)
									.saveUserAvatar(rec.getUserId(), prec);

						} else {
							if (BuildConfig.isDebug) {
								String msg = "prec == null:"
										+ (prec == null)
										+ ",middle:"
										+ (null == prec ? "null" : prec
												.getNormal());
								ExceptionRemHelper.showExceptionReport(
										HomePageActivity.this, msg);
							} else {
								Toast.makeText(HomePageActivity.this,
										R.string.upload_avatar_failed_rem,
										Toast.LENGTH_SHORT).show();
							}
						}
					} else {
						String message = HomePageActivity.this.getResources()
								.getString(R.string.upload_avatar_failed_rem);
						String msg = HomePageActivity.this.getResources()
								.getString(R.string.upload_avatar_failed_rem);
						if (null == br) {
							msg = "parseUploadAvatar: Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {

							default:
								msg = br.getMessage();
								message = br.getMessage();// TODO
															// ��ʱ��ʾ������Ϣ���û�����ʽ���ֻ��ʾʧ��
								break;
							}
						}
						Log.e(TAG,
								currMethod + "errorCode:" + br.getErrorCode()
										+ "--Message:" + br.getMessage());
						if (BuildConfig.isDebug) {
							ExceptionRemHelper.showExceptionReport(
									HomePageActivity.this, msg);
						} else {
							Toast.makeText(HomePageActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Log.d(TAG, currMethod + "Result t is null");
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								HomePageActivity.this, msg);
					} else {
						Toast.makeText(HomePageActivity.this,
								R.string.upload_avatar_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};

		File userFile = new File(path);
		String fileName = userFile.getName();
		us.UploadAvatar(HomePageActivity.this, fileName, userFile, callBack);
	}

	public void refreshAvatarView(String url) {
		Trace.d(TAG, "refreshAvatarView:" + rec.getAvatar().toString());
		if (!StringUtil.getInstance().IsEmpty(url)) {
			try {
				avatarFB = FinalBitmap.create(HomePageActivity.this);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && isUpdate) {
			final MyRemDialog dialog = new MyRemDialog(HomePageActivity.this,
					R.style.dialog);
			dialog.setTitle(R.string.user_basic_dialog_save_rem_title);
			dialog.setMessage(R.string.user_basic_dialog_to_save_message);
			dialog.setPosBtnText(R.string.user_basic_dialog_save_rem_btn_save);
			dialog.setNegBtnText(R.string.user_basic_dialog_save_rem_btn_cancel);
			dialog.setPosBtnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					isUpdate = false;
					btnRight.setImageResource(R.drawable.edit_btn_selector);
					afterToUpdateUserInfo();
				}
			});
			dialog.setNegBtnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}
		// else {
		// HomePageActivity.this.finish();
		// }
		return super.onKeyDown(keyCode, event);
	}

	public String getPath(Uri uri) {
		if (null == uri || uri.getAuthority().equals("")) {
			return null;
		}
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = HomePageActivity.this.managedQuery(uri, projection,
				null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(column_index);
		return path;
	}
}
