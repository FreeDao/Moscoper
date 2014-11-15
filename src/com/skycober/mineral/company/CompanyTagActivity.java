package com.skycober.mineral.company;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skycober.mineral.R;
import com.skycober.mineral.bean.EnterpriseRec;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.setting.SweepActivity;
import com.skycober.mineral.util.AndroidIdUtil;
import com.skycober.mineral.util.AndroidIdUtil.onAndroidIdGetFailure;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.widget.MyRemDialog;
import com.skycober.mineral.widget.WaitLoadingDialog;

public class CompanyTagActivity extends FragmentActivity implements
		OnClickListener {
	private String eid;
	private ViewPager tagViewPage;
	private String tags_url;// 企业标签
	private String company_url;// 企业信息

	private TextView ename;
	private ImageView elogo;

	private TextView attention_tv;// 已关注
	private TextView no_attention_tv;// 未关注

	private FinalBitmap fb;

	private List<CompanyTagListFragment> fragments = new ArrayList<CompanyTagListFragment>();

	protected WaitLoadingDialog waitDialog;

	public void lockScreen(String message) {
		if (null == waitDialog) {
			waitDialog = new WaitLoadingDialog(this);
		}
		waitDialog.setMessage(message);
		if (null != waitDialog && !this.isFinishing()
				&& !waitDialog.isShowing()) {
			waitDialog.show();
		}
	}

	public void releaseScreen() {
		if (null != waitDialog && waitDialog.isShowing()) {
			waitDialog.cancel();
		}
	}

	private TextView searchTagBut;// 搜所的button
	private AutoCompleteTextView searchEdit;
	private ExpandableListView searchList;
	private String[] searchArray;
	private TextView praise_num;
	private TextView black_num;

	private Button sweep;
	private String tag_ids = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.company_tag_activity);
		boolean hasLogin = !SettingUtil
				.getInstance(this)
				.getValue(SettingUtil.KEY_LOGIN_USER_ID,
						SettingUtil.DEFAULT_LOGIN_USER_ID)
				.equalsIgnoreCase(SettingUtil.DEFAULT_LOGIN_USER_ID);
		sweep = (Button) findViewById(R.id.sweep);
		sweep.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(CompanyTagActivity.this,
						SweepActivity.class);
				intent.putExtra("eid", eid);
				intent.putExtra("tag_ids", tag_ids);
				startActivity(intent);
			}
		});

		searchTagBut = (TextView) findViewById(R.id.searchTagBut);
		searchEdit = (AutoCompleteTextView) findViewById(R.id.searchTag);
		searchEdit.setThreshold(1);

		searchEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

				if (s.toString().equals("")) {
					// getCompanyTagInfo(tags_url);
					findViewById(R.id.searchList).setVisibility(View.GONE);
					findViewById(R.id.tag_title).setVisibility(View.VISIBLE);
					findViewById(R.id.line).setVisibility(View.VISIBLE);
					findViewById(R.id.company_tag_viewpage).setVisibility(
							View.VISIBLE);
				}
			}
		});

		searchTagBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String searchStr = searchEdit.getText().toString();
				if (searchStr == null || "".equals(searchStr)) {
					Toast.makeText(getApplicationContext(), "请输入您要搜索的内容", 1)
							.show();
				} else {
					String searchURL = RequestUrls.SERVER_BASIC_URL
							+ RequestUrls.COMPANY_SEARCH_TAG_LIST + searchStr;
					searchURL = searchURL.replace("[eid]", eid);
					getSearchTagList(searchURL);
				}

			}
		});

	}

	public void getSearchTagList(String url) {
		FinalHttp fh = new FinalHttp();
		lockScreen("正在搜索....");
		String cookie = SettingUtil.getInstance(this).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}

		fh.get(url, new AjaxCallBack<Object>() {

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				JSONObject jsonObject;

				try {
					jsonObject = new JSONObject(t.toString());
					releaseScreen();
					TagResult tagResult = CompanyJsonUtils
							.parserCateTage(jsonObject);
					if (tagResult.getDonot_tag_list() == null
							|| tagResult.getDonot_tag_list().size() == 0) {
						Toast.makeText(getApplicationContext(), "没有您想要的！！", 1)
								.show();
					} else {
						LinearLayout searchList = (LinearLayout) findViewById(R.id.searchList);
						searchList.setVisibility(View.VISIBLE);
						findViewById(R.id.tag_title).setVisibility(View.GONE);
						findViewById(R.id.line).setVisibility(View.GONE);
						findViewById(R.id.company_tag_viewpage).setVisibility(
								View.GONE);

						FragmentManager manager = CompanyTagActivity.this
								.getSupportFragmentManager();
						CompanyTagListFragment searchFragment = instanceFragment(
								jsonObject.toString(), false);
						FragmentTransaction transaction = manager
								.beginTransaction();
						transaction.replace(R.id.searchList, searchFragment,
								"searchFragment");
						transaction.commit();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, strMsg);
			}

		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Intent intent = getIntent();
		fb = FinalBitmap.create(this);

		if (intent.hasExtra("eid")) {
			eid = intent.getStringExtra("eid");
		} else {
			initdate();
		}
		company_url = RequestUrls.SERVER_BASIC_URL + RequestUrls.COMPANY_INFO
				+ eid;
		tags_url = RequestUrls.SERVER_BASIC_URL + RequestUrls.COMPANY_TAGS
				+ eid;
		init();
	}

	private EnterpriseRec enterpriseRec = new EnterpriseRec();

	private AndroidIdUtil androidIdUtil = new AndroidIdUtil();
	private onAndroidIdGetFailure androidIdGetFailure = new onAndroidIdGetFailure() {

		@Override
		public void onFailure() {
			// TODO Auto-generated method stub
			showLogOutRem(androidIdUtil);
		}
	};

	private MyRemDialog logOutRemDialog;

	private void showLogOutRem(final AndroidIdUtil androidIdUtil) {
		if (null == logOutRemDialog) {
			logOutRemDialog = new MyRemDialog(this, R.style.dialog);
			logOutRemDialog.setHeaderVisility(View.GONE);
			logOutRemDialog.setMessage("连接服务器失败了，是否重试？");
			logOutRemDialog.setPosBtnText("确定");
			logOutRemDialog.setNegBtnText("取消");
			logOutRemDialog.setPosBtnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					androidIdUtil.sendAndroidId(CompanyTagActivity.this);
				}
			});
			logOutRemDialog.setNegBtnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			logOutRemDialog.setCanceledOnTouchOutside(false);
		}
		if (null != logOutRemDialog && !this.isFinishing()
				&& !logOutRemDialog.isShowing()) {
			logOutRemDialog.show();
		}
	}

	private void initdate() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		String data = intent.getDataString();
		String jsons[] = data.split("/");
		for (int i = 0; i < jsons.length; i++) {
			if (jsons[i].equalsIgnoreCase(EnterpriseRec.EID)) {
				enterpriseRec.setEid(jsons[i + 1]);
				i = i + 1;
			} else if (jsons[i].equalsIgnoreCase(EnterpriseRec.TAG_CAT_ID)) {
				enterpriseRec.setTagCatId(jsons[i + 1]);
				i = i + 1;
			}
		}
		// tagCatId = enterpriseRec.getTagCatId();
		eid = enterpriseRec.getEid();
		androidIdUtil.sendAndroidId(this);
		androidIdUtil.setAndroidIdGetFailure(androidIdGetFailure);
	}

	public void init() {
		ename = (TextView) findViewById(R.id.company_name);
		elogo = (ImageView) findViewById(R.id.logo);
		praise_num = (TextView) findViewById(R.id.praise_num);
		black_num = (TextView) findViewById(R.id.black_num);

		attention_tv = (TextView) findViewById(R.id.tag_att);
		no_attention_tv = (TextView) findViewById(R.id.tag_no_att);
		attention_tv.setOnClickListener(this);
		no_attention_tv.setOnClickListener(this);

		tagViewPage = (ViewPager) findViewById(R.id.company_tag_viewpage);
		getComanyInfo(company_url);
		getCompanyTagInfo(tags_url);
	}

	/**
	 * 获取企业信息
	 * 
	 * @param url
	 */

	public void getComanyInfo(String url) {
		FinalHttp fh = new FinalHttp();
		String cookie = SettingUtil.getInstance(this).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.get(url, new AjaxCallBack<Object>() {

			@Override
			public void onSuccess(Object t, HttpResponse response) {
				// TODO Auto-generated method stub
				super.onSuccess(t, response);
				try {
					JSONObject jsonObject = new JSONObject(t.toString());
					CompanyInfo company = CompanyJsonUtils
							.parserCompanyInfo(jsonObject);// 获取企业信息
					ename.setText(company.getEname());
					praise_num.setText(company.getPraise_num() + "人赞");
					black_num.setText(company.getBlack_num() + "人屏蔽");
					fb.configLoadfailImage(R.drawable.mineral_logo);
					fb.display(elogo, RequestUrls.SERVER_BASIC_URL + "/"
							+ company.getElogo());

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, strMsg);
			}

		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			findViewById(R.id.searchList).setVisibility(View.GONE);
			findViewById(R.id.tag_title).setVisibility(View.VISIBLE);
			findViewById(R.id.line).setVisibility(View.VISIBLE);
			findViewById(R.id.company_tag_viewpage).setVisibility(View.VISIBLE);
		}
		return super.onKeyDown(keyCode, event);
	}

	// 获取企业标签
	public void getCompanyTagInfo(String url) {
		lockScreen("正在加载....");
		FinalHttp fh = new FinalHttp();
		String cookie = SettingUtil.getInstance(this).getValue(
				SettingUtil.KEY_COOKIE, null);
		if (null != cookie && cookie.length() > 0) {
			fh.addHeader("cookie", cookie);
		}
		fh.get(url, new AjaxCallBack<Object>() {

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				if (t != null) {
					try {
						JSONObject jsonObject = new JSONObject(t.toString());
						TagResult tagResult = CompanyJsonUtils
								.parserCateTage(jsonObject);
						List<CatgerTag> done = tagResult.getDone_tag_list();
						List<CatgerTag> notdone = tagResult.getDonot_tag_list();

						List<Tag> tagList = null;

						if (done != null) {
							tagList = new ArrayList<Tag>();
							tagList.clear();
							tag_ids = "";
							for (int i = 0; i < done.size(); i++) {
								tagList.addAll(done.get(i).getTag_list());
							}
							for (int i = 0; i < tagList.size(); i++) {
								if ("".equals(tag_ids)) {
									tag_ids = tagList.get(0).getTag_id();
								} else {
									tag_ids = tag_ids + ","
											+ tagList.get(i).getTag_id();
								}
							}
							System.out.println("===done====" + tag_ids);
						}
						if (notdone != null) {
							for (int i = 0; i < notdone.size(); i++) {
								tagList.addAll(notdone.get(i).getTag_list());
							}
						}

						if (tagList != null) {
							searchArray = new String[tagList.size()];
							for (int i = 0; i < tagList.size(); i++) {
								searchArray[i] = tagList.get(i).getTag_name();
							}
							ArrayAdapter<String> autoAdapter = new ArrayAdapter<String>(
									CompanyTagActivity.this, R.layout.autoitem,
									searchArray);
							searchEdit.setAdapter(autoAdapter);
						}

						fragments.clear();
						CompanyTagListFragment attentionFragment = instanceFragment(
								jsonObject.toString(), true);
						CompanyTagListFragment noAttentionFragment = instanceFragment(
								jsonObject.toString(), false);
						fragments.add(noAttentionFragment);
						fragments.add(attentionFragment);

						TagViewPageAdapter adapter = new TagViewPageAdapter(
								getSupportFragmentManager());
						tagViewPage.setAdapter(adapter);
						tagViewPage.setOnPageChangeListener(adapter);

						releaseScreen();
						// System.out.println("===jsonObject==="
						// + jsonObject.toString());
						// CompanyJsonUtils.parserCateTage(jsonObject);// 解析企业标签
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, strMsg);
			}

		});
	}

	/**
	 * 设置已关注标签和未关注标签
	 */

	public CompanyTagListFragment instanceFragment(String jsonString,
			boolean isAttention) {
		CompanyTagListFragment fragment = null;
		if (fragment == null) {
			fragment = new CompanyTagListFragment();
		}
		Bundle bundle = new Bundle();
		bundle.putString("jsonString", jsonString);
		bundle.putString("eid", eid);
		bundle.putBoolean("isAttention", isAttention);
		fragment.setArguments(bundle);
		return fragment;
	}

	public class TagViewPageAdapter extends FragmentStatePagerAdapter implements
			OnPageChangeListener {

		public TagViewPageAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fragments.size();
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
				attention_tv
						.setBackgroundResource(R.drawable.textview_right_shape);
				no_attention_tv
						.setBackgroundResource(R.drawable.textview_left_selected_shape);
				attention_tv.setTextColor(getResources().getColor(
						R.color.text_color));
				no_attention_tv.setTextColor(Color.WHITE);
				break;

			case 1:
				attention_tv
						.setBackgroundResource(R.drawable.textview_right_select_shape);
				no_attention_tv
						.setBackgroundResource(R.drawable.textview_left_shape);
				attention_tv.setTextColor(Color.WHITE);
				no_attention_tv.setTextColor(getResources().getColor(
						R.color.text_color));
				break;
			}
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_button_left:
			finish();
			break;
		case R.id.tag_no_att:
			tagViewPage.setCurrentItem(0);
			attention_tv.setBackgroundResource(R.drawable.textview_right_shape);
			no_attention_tv
					.setBackgroundResource(R.drawable.textview_left_selected_shape);
			attention_tv.setTextColor(getResources().getColor(
					R.color.text_color));
			no_attention_tv.setTextColor(Color.WHITE);
			break;
		case R.id.tag_att:
			tagViewPage.setCurrentItem(1);
			attention_tv
					.setBackgroundResource(R.drawable.textview_right_select_shape);
			no_attention_tv
					.setBackgroundResource(R.drawable.textview_left_shape);
			attention_tv.setTextColor(Color.WHITE);
			no_attention_tv.setTextColor(getResources().getColor(
					R.color.text_color));
			break;
		}
	}
}
