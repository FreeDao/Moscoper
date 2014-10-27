package com.skycober.mineral.product;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.http.AjaxCallBack;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.google.gson.Gson;
import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.account.GoodsService;
import com.skycober.mineral.account.ServerResponseParser;
import com.skycober.mineral.bean.KeyWordsListRec;
import com.skycober.mineral.bean.KeyWordsRec;
import com.skycober.mineral.bean.ProductRec;
import com.skycober.mineral.bean.TagCategoryRec;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.CityModel;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseGetMatchNum;
import com.skycober.mineral.network.CityModel.CityItemModel;
import com.skycober.mineral.product.KeyWordsAdapter.OnItemDelectLongClick;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.LocationUtil;
import com.skycober.mineral.util.NetworkUtil;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.Util;
import com.skycober.mineral.widget.MyRemDialog;

/**
 * 添加需求 我要
 * 
 * @author Yes366
 * 
 */
public class AddKeywordsActivity extends BaseActivity {
	private static final String TAG = "AddKeywordsActivity";
	private String cityid = null;
	private int pNameIndex = 0;
	private List<CityItemModel> citys = new ArrayList<CityItemModel>();
	private CityModel cityModel;
	private List<CityItemModel> provinces = new ArrayList<CityItemModel>();

	/**
	 * ===========城市dialog
	 */

	/**
	 * ===========城市dialog
	 */
	private void ShowLocationDialog() {
		String[] citysStr = new String[citys.size()];
		for (int i = 0; i < citys.size(); i++) {
			citysStr[i] = citys.get(i).getName();
		}
		AlertDialog dialog = new AlertDialog.Builder(AddKeywordsActivity.this)
				.setTitle("请选择市")
				.setItems(citysStr, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						cityid = citys.get(which).getId();
						tvcityId.setText(citys.get(which).getName());
					}
				}).create();
		dialog.show();
	}

	// 省份列表对话框
	private void ShowProvinceDialog() {
		String[] citysStr = new String[provinces.size()];
		for (int i = 0; i < provinces.size(); i++) {
			citysStr[i] = provinces.get(i).getName();
		}
		AlertDialog dialog = new AlertDialog.Builder(AddKeywordsActivity.this)
				.setTitle("请选择省")
				.setItems(citysStr, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String cityPid = provinces.get(which).getId();
						citys.clear();
						for (int j = 0; j < cityModel.getResult().size(); j++) {
							if (cityModel.getResult().get(j).getPid()
									.equals(cityPid)) {
								citys.add(cityModel.getResult().get(j));
							}
						}
						if (citys.size() <= 0 || cityPid.equals("0")) {
							cityIdLayout.setVisibility(View.GONE);
						} else {
							cityIdLayout.setVisibility(View.VISIBLE);
						}
						cityid = provinces.get(which).getId();
						tvcityId.setText(provinces.get(which).getName());
						tvprovinceId.setText(provinces.get(which).getName());
					}
				}).create();
		dialog.show();
	}

	/**
	 * =====================定位
	 */
	private BDLocationListener bdLocationListener = new BDLocationListener() {

		@Override
		public void onReceivePoi(BDLocation arg0) {

		}

		@Override
		public void onReceiveLocation(BDLocation arg0) {
			String cityPid = null;
			String temp = arg0.getProvince();
			if (temp != null) {
				String pName = temp.substring(0, 2);
				String cName = arg0.getCity();
				try {
					String str = Util.readStringFromFile("area",
							AddKeywordsActivity.this);
					Gson gson = new Gson();
					cityModel = gson.fromJson(str, CityModel.class);
					for (int i = 0; i < cityModel.getResult().size(); i++) {
						if (cityModel.getResult().get(i).getPid().equals("0")) {
							String locaPName = cityModel.getResult().get(i)
									.getName().substring(0, 2);
							provinces.add(cityModel.getResult().get(i));
							if (pName.equals(locaPName)) {
								cityPid = cityModel.getResult().get(i).getId();
								pNameIndex = i;
								tvprovinceId.setText(cityModel.getResult()
										.get(i).getName());
							}
						}

					}
					String locationCity;
					if (cName.equals(pName)) {
						locationCity = locationUtil.getCityInfo(arg0);
					} else {
						locationCity = arg0.getDistrict();
					}
					if (cityPid != null) {
						for (int j = 0; j < cityModel.getResult().size(); j++) {
							if (cityModel.getResult().get(j).getPid()
									.equals(cityPid)) {
								citys.add(cityModel.getResult().get(j));
								if (cityModel.getResult().get(j).getName()
										.equals(locationCity)) {
									cityid = cityModel.getResult().get(j)
											.getId();
									tvcityId.setText(cityModel.getResult()
											.get(j).getName());
								}
							}
						}

					}
					if (!(cityid != null)) {
						citys.add(cityModel.getResult().get(pNameIndex));
						cityid = cityModel.getResult().get(pNameIndex).getId();
						tvcityId.setText(cityModel.getResult().get(pNameIndex)
								.getName());
					}
					locationUtil.stopLocation();
					releaseScreen();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				releaseScreen();
				Toast.makeText(getApplicationContext(), "请检查您的网络！！", 1).show();
			}

		}
	};
	private LocationUtil locationUtil;

	private boolean isSave;
	private boolean isEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_keywords);
		init();
		initTitle();
		Intent intent = getIntent();
		isEdit = intent.getBooleanExtra("isEdit", false);
		if (!isEdit) {
			parentIdLayout.performClick();

		} else {
			lockScreen("正在确定您发布需求的位置");
			locationUtil = new LocationUtil(this, bdLocationListener);
			locationUtil.startLocation();
		}
		saveAddKeyWordState();
		if (isEdit) {
			isSelect = true;
			ProductRec product = (ProductRec) intent
					.getSerializableExtra("productRec");
			compatibility_layout.setVisibility(View.VISIBLE);
			parentIdLayout.setEnabled(false);
			keywordsLayout.setVisibility(View.VISIBLE);
			mProductRec = product;

			tvparentId.setText(product.getTagCatName());
			if (mProductRec.getTagCatId() != null
					&& mProductRec.getTagCatName() != null) {
				if (mProductRec.getTagCatId() != null) {
					mProductRec.setTagCatName(null);
				}
			}

			tvprovinceId.setText(product.getProName());
			tvcityId.setText(product.getCityName());
			selectKeyWordsList.clear();
			showKeyWordsList.clear();
			selectKeyWordsList = product.getTags();
			for (int i = 0; i < selectKeyWordsList.size(); i++) {
				showKeyWordsList.add(selectKeyWordsList.get(i).getTagName());
			}

			adapter.setKeyWordsList(showKeyWordsList);
			adapter.notifyDataSetChanged();
			setGridViewHeightBasedOnChildren(keywords_gridview);

		}

	}

	/**
	 * 保存未编辑完成的信息
	 */
	public void saveAddKeyWordState() {
		SharedPreferences saveSate = getSharedPreferences("state", 0);
		String parentId = saveSate.getString("parentId", "");
		String parentIdName = saveSate.getString("parentIdName", null);// 信息分类
		if (!"".equals(parentId)) {

			tvparentId.setText(parentIdName);
			isSelect = true;
			compatibility_layout.setVisibility(View.VISIBLE);
			keywordsLayout.setVisibility(View.VISIBLE);

			mProductRec.setTagCatId(parentId);
			mProductRec.setTagCatName(null);

			// compatibility = saveSate.getFloat("compatibility", 0);// 匹配度
			showKeyWordsList.clear();
			selectKeyWordsList.clear();
			for (int i = 0; i < saveSate.getInt("selectNum", 0); i++) {
				KeyWordsRec keyRec = new KeyWordsRec();
				keyRec.setTagID(saveSate.getString("selectID" + i, ""));
				keyRec.setTagName(saveSate.getString("selectName" + i, ""));
				selectKeyWordsList.add(keyRec);
				showKeyWordsList.add(keyRec.getTagName());

			}

			for (int i = 0; i < saveSate.getInt("diyNum", 0); i++) {
				diyKeyWordsList.add(saveSate.getString("diy" + i, ""));
			}

			showKeyWordsList.addAll(diyKeyWordsList);

			adapter.setKeyWordsList(showKeyWordsList);
			adapter.notifyDataSetChanged();
			setGridViewHeightBasedOnChildren(keywords_gridview);

		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 保存activity的状态
		if (!isSave) {
			SharedPreferences saveState = getSharedPreferences("state", 0);
			SharedPreferences.Editor editor = saveState.edit();
			// editor.putFloat("compatibility", (float) compatibility);// 匹配度
			editor.putString("parentIdName", tvparentId.getText().toString());// 信息分类,分类名

			editor.putString("parentId", mProductRec.getTagCatId());// 信息分类，id
			editor.putInt("selectNum", selectKeyWordsList.size());// 信息标签，数量
			// 已有标签
			for (int i = 0; i < selectKeyWordsList.size(); i++) {
				editor.putString("selectID" + i, selectKeyWordsList.get(i)
						.getTagID());
				editor.putString("selectName" + i, selectKeyWordsList.get(i)
						.getTagName());
			}
			// 新建的标签
			editor.putInt("diyNum", diyKeyWordsList.size());
			for (int i = 0; i < diyKeyWordsList.size(); i++) {
				editor.putString("diy" + i, diyKeyWordsList.get(i));
			}

			editor.commit();

		}

	}

	private void initTitle() {
		// TODO Auto-generated method stub
		TextView tvTitle = (TextView) findViewById(R.id.title_text);
		tvTitle.setText(R.string.select_key_words_title1);
		ImageButton btnLeft = (ImageButton) findViewById(R.id.title_button_left);// 返回
		btnLeft.setImageResource(R.drawable.back_btn_selector);
		btnLeft.setOnClickListener(btnListClickListener);
		ImageButton btnRight = (ImageButton) findViewById(R.id.title_button_right);// 确定
		btnRight.setVisibility(View.VISIBLE);
		btnRight.setImageResource(R.drawable.check_btn_selector);
		btnRight.setOnClickListener(btnrightClickListener);
	}

	// 确定
	private View.OnClickListener btnrightClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			// 调取匹配数量api
			// getSharedPreferences("state", 0).edit().clear().commit();
			isSave = true;
			readyToGetMatchNum();
			v.setEnabled(true);

		}
	};
	// 返回
	private View.OnClickListener btnListClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
		}
	};
	private float compatibility = 1.0f;
	private RelativeLayout parentIdLayout, keywordsLayout,
			compatibility_layout, desc_Layout, cityIdLayout, provinceIdLayout;
	private KeyWordsAdapter adapter;
	private TextView tvparentId, tv_compatibility, text_desc, tvcityId,
			tvprovinceId;
	private List<String> showKeyWordsList = new ArrayList<String>();
	private GridView keywords_gridview;
	private ProductRec mProductRec;
	private List<KeyWordsRec> selectKeyWordsList = new ArrayList<KeyWordsRec>();
	private List<String> diyKeyWordsList = new ArrayList<String>();

	private void init() {
		// TODO Auto-generated method stub
		parentIdLayout = (RelativeLayout) findViewById(R.id.parentIdLayout);// 信息分类
		parentIdLayout.setOnClickListener(parentIdLayoutclickListener);
		provinceIdLayout = (RelativeLayout) findViewById(R.id.provinceIdLayout);// 所在省份
		provinceIdLayout.setOnClickListener(provinceIdLayoutclickListener);
		cityIdLayout = (RelativeLayout) findViewById(R.id.cityIdLayout);// 所在城市
		cityIdLayout.setOnClickListener(cityIdLayoutclickListener);
		keywordsLayout = (RelativeLayout) findViewById(R.id.keywordsLayout);// 信息标签
		keywordsLayout.setOnClickListener(btnKeywordClickListener);
		desc_Layout = (RelativeLayout) findViewById(R.id.desc_Layout);
		desc_Layout.setOnClickListener(btnDetailDescClickListener);
		// 匹配度
		compatibility_layout = (RelativeLayout) findViewById(R.id.compatibility_layout);
		compatibility_layout.setOnClickListener(compatibilityclickListener);
		tvparentId = (TextView) findViewById(R.id.tvparentId);
		tvprovinceId = (TextView) findViewById(R.id.tvprovinceId);
		tvcityId = (TextView) findViewById(R.id.tvcityId);
		tv_compatibility = (TextView) findViewById(R.id.tv_compatibility);
		tv_compatibility.setText("100%");
		text_desc = (TextView) findViewById(R.id.text_desc);
		mProductRec = new ProductRec();
		keywords_gridview = (GridView) findViewById(R.id.keywords_gridview);// 选中标签列表
		adapter = new KeyWordsAdapter(showKeyWordsList, this);
		adapter.setOnItemDelectLongClick(new OnItemDelectLongClick() {

			@Override
			public void OnItemLongClick(int pos) {
				// TODO Auto-generated method stub
				// 删除标签
				showDelectKeyWordsRem(pos);
			}
		});
		keywords_gridview.setAdapter(adapter);
	}

	// 删除标签
	private void showDelectKeyWordsRem(final int pos) {
		// TODO Auto-generated method stub
		final MyRemDialog dialog = new MyRemDialog(AddKeywordsActivity.this,
				R.style.dialog);
		dialog.setTitle(R.string.key_words_delect_rem_title);
		dialog.setMessage(R.string.key_words_delect_to_delect_message);
		dialog.setPosBtnText(R.string.user_basic_dialog_save_rem_btn_save);
		dialog.setNegBtnText(R.string.user_basic_dialog_save_rem_btn_cancel);
		dialog.setPosBtnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String key = showKeyWordsList.get(pos);
				for (int i = 0; i < diyKeyWordsList.size(); i++) {
					if (key.equals(diyKeyWordsList.get(i))) {
						diyKeyWordsList.remove(i);
					}
				}
				for (int j = 0; j < selectKeyWordsList.size(); j++) {
					if (key.equals(selectKeyWordsList.get(j).getTagName())) {
						selectKeyWordsList.remove(j);
					}
				}
				showKeyWordsList.remove(pos);
				adapter.setKeyWordsList(showKeyWordsList);
				adapter.notifyDataSetChanged();
				setGridViewHeightBasedOnChildren(keywords_gridview);
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

	private View.OnClickListener btnDetailDescClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			showDetailDescRem();
			v.setEnabled(true);
		}
	};
	private Dialog changeDetailDescDialog;

	protected void showDetailDescRem() {
		if (null == changeDetailDescDialog) {
			changeDetailDescDialog = new Dialog(this, R.style.dialog);
			ViewGroup mRoot = (ViewGroup) LayoutInflater.from(this).inflate(
					R.layout.product_change_name_dialog_content_view, null);
			TextView tvTitle = (TextView) mRoot.findViewById(R.id.tvTitle);
			tvTitle.setText(R.string.change_detail_desc_dialog_title);
			final EditText etDetailDesc = (EditText) mRoot
					.findViewById(R.id.etContent);
			Button btnOk = (Button) mRoot.findViewById(R.id.positiveButton);
			Button btnCancel = (Button) mRoot.findViewById(R.id.negativeButton);
			etDetailDesc.setHint(R.string.prod_detail_desc_input_hint);
			String description = mProductRec.getDescription();
			etDetailDesc.setText(description);
			btnOk.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					changeDetailDescDialog.cancel();
					String prodDesc = etDetailDesc.getText().toString();
					if (StringUtil.getInstance().IsEmpty(prodDesc)) {
						Toast.makeText(AddKeywordsActivity.this,
								R.string.prod_detail_desc_not_empty,
								Toast.LENGTH_SHORT).show();
					} else {
						text_desc.setText(prodDesc);
						mProductRec.setDescription(prodDesc);
					}
				}
			});
			btnCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					changeDetailDescDialog.cancel();
				}
			});
			changeDetailDescDialog.setContentView(mRoot);
			changeDetailDescDialog.setCanceledOnTouchOutside(false);
		}

		if (null != changeDetailDescDialog && !isFinishing()
				&& !changeDetailDescDialog.isShowing()) {
			changeDetailDescDialog.show();
		}
	}

	String[] choices = new String[11];

	// 匹配度对话框
	private void showReportTypeDialog() {
		for (int i = 10; i >= 0; i--) {
			choices[10 - i] = (i * 10) + "%";
		}
		// 包含多个选项的对话框
		AlertDialog dialog = new AlertDialog.Builder(AddKeywordsActivity.this)
				.setTitle("请选择匹配度")
				.setItems(choices, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						compatibility = (float) ((10 - which) / 10.0);
						tv_compatibility.setText(choices[which]);
					}
				}).create();
		dialog.show();
	}

	// 省份
	private View.OnClickListener provinceIdLayoutclickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			// 省份列表对话框
			ShowProvinceDialog();
			v.setEnabled(true);
		}
	};
	// 城市
	private View.OnClickListener cityIdLayoutclickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			// 城市列表对话框
			ShowLocationDialog();
			v.setEnabled(true);
		}
	};
	// 匹配度
	private View.OnClickListener compatibilityclickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			// 匹配度对话框
			showReportTypeDialog();
			v.setEnabled(true);
		}
	};
	// 信息分类
	private View.OnClickListener parentIdLayoutclickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			// 信息分类列表
			skipToChangeCategory();
			v.setEnabled(true);
		}
	};
	// 信息标签
	private View.OnClickListener btnKeywordClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			if (isSelect) {
				// 分类信息已选
				skipToChangekeyWords();
			} else {
				lockScreen("正在确定您发布需求的位置");
				locationUtil = new LocationUtil(AddKeywordsActivity.this,
						bdLocationListener);
				locationUtil.startLocation();
				showChangeKeywordsRem();
			}
			v.setEnabled(true);
		}
	};
	/**
	 * --------------------------------显示分类dialog------------------------------
	 * ---------------
	 */
	private Dialog selectParentIdDialog;

	protected void showChangePIDRem() {
		if (null == selectParentIdDialog) {
			selectParentIdDialog = new Dialog(this, R.style.dialog);
			ViewGroup mRoot = (ViewGroup) LayoutInflater.from(this).inflate(
					R.layout.product_change_name_dialog_content_view, null);
			TextView tvTitle = (TextView) mRoot.findViewById(R.id.tvTitle);
			tvTitle.setText(R.string.change_keywords_dialog_title);
			final EditText etKeywords = (EditText) mRoot
					.findViewById(R.id.etContent);
			Button btnOk = (Button) mRoot.findViewById(R.id.positiveButton);
			Button btnCancel = (Button) mRoot.findViewById(R.id.negativeButton);
			etKeywords.setHint(R.string.prod_prientid_input_hint);
			btnOk.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					selectParentIdDialog.cancel();
					String prodKeywords = etKeywords.getText().toString();
					if (StringUtil.getInstance().IsEmpty(prodKeywords)) {
						Toast.makeText(AddKeywordsActivity.this,
								R.string.prod_keywords_not_empty,
								Toast.LENGTH_SHORT).show();
					} else {
						mProductRec.setTagCatId(null);
						mProductRec.setTagCatName(prodKeywords);
						tvparentId.setText(prodKeywords);
						compatibility_layout.setVisibility(View.VISIBLE);
						keywordsLayout.setVisibility(View.VISIBLE);
						isSelect = false;
					}
				}
			});
			btnCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					selectParentIdDialog.cancel();
				}
			});
			selectParentIdDialog.setContentView(mRoot);
			selectParentIdDialog.setCanceledOnTouchOutside(false);
		}
		if (null != selectParentIdDialog && !isFinishing()
				&& !selectParentIdDialog.isShowing()) {
			selectParentIdDialog.show();
		}
	}

	private Dialog selectParentDialog;
	private boolean isSelect = false;

	protected void showParentIdRem() {
		if (null == selectParentDialog) {
			selectParentDialog = new Dialog(this, R.style.dialog);
			ViewGroup mRoot = (ViewGroup) LayoutInflater.from(this).inflate(
					R.layout.select_photo_dialog_content_view, null);
			int width = getWindowManager().getDefaultDisplay().getWidth();
			selectParentDialog.setContentView(mRoot, new LayoutParams(width,
					LayoutParams.WRAP_CONTENT));
			TextView tvTitle = (TextView) mRoot.findViewById(R.id.tvTitle);
			tvTitle.setText("选择分类方式");
			Button btnselect = (Button) mRoot.findViewById(R.id.btnCamera);
			btnselect.setText("已存在分类");
			Button btnDIY = (Button) mRoot.findViewById(R.id.btnAlbum);
			btnDIY.setText("自定义分类");
			Button btnCancel = (Button) mRoot.findViewById(R.id.btnCancel);
			btnselect.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					selectParentDialog.cancel();
					// 选择分类
					skipToChangeCategory();
				}
			});
			btnDIY.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					selectParentDialog.cancel();
					// 自定义
					showChangePIDRem();
				}
			});
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					selectParentDialog.cancel();

				}
			});
		}
		Window window = selectParentDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.mystyle);
		selectParentDialog.setCanceledOnTouchOutside(true);
		if (null != selectParentDialog && !selectParentDialog.isShowing()
				&& !isFinishing()) {
			selectParentDialog.show();
		}
	}

	// 跳转信息分类列表
	protected void skipToChangeCategory() {
		Intent mIntent = new Intent(AddKeywordsActivity.this,
				SelectCategoryForAddProdActivity.class);
		mIntent.putExtra("type", "find");

		startActivityForResult(mIntent, REQUEST_CODE_FOR_SELECT_CATEGORY);
	}

	/**
	 * --------------------------------获取标签----------
	 */

	private static final int REQUEST_CODE_FOR_SELECT_KEYWORDS = 0x20002;

	// 获取标签
	protected void skipToChangekeyWords() {
		Intent mIntent = new Intent(AddKeywordsActivity.this,
				SelectKeyWordsActivity.class);
		mIntent.putExtra("TAG_CAT_ID", mProductRec.getTagCatId());
		ArrayList<String> listName = new ArrayList<String>();
		ArrayList<String> listId = new ArrayList<String>();

		for (int i = 0; i < selectKeyWordsList.size(); i++) {
			listName.add(selectKeyWordsList.get(i).getTagName());
			listId.add(selectKeyWordsList.get(i).getTagID());
		}
		mIntent.putStringArrayListExtra("listName", listName);
		mIntent.putStringArrayListExtra("listId", listId);
		startActivityForResult(mIntent, REQUEST_CODE_FOR_SELECT_KEYWORDS);
	}

	public void setGridViewHeightBasedOnChildren(GridView gridView) {
		ListAdapter listAdapter = gridView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) gridView
				.getLayoutParams();
		int count = listAdapter.getCount() % 3 == 0 ? listAdapter.getCount() / 3
				: listAdapter.getCount() / 3 + 1;
		params.height = (Util.dip2px(AddKeywordsActivity.this, 60) * (count));
		gridView.setLayoutParams(params);
	}

	private Dialog changeKeywordsDialog;

	// 添加标签的对话框
	protected void showChangeKeywordsRem() {
		if (null == changeKeywordsDialog) {
			changeKeywordsDialog = new Dialog(this, R.style.dialog);
			ViewGroup mRoot = (ViewGroup) LayoutInflater.from(this).inflate(
					R.layout.product_change_name_dialog_content_view, null);
			TextView tvTitle = (TextView) mRoot.findViewById(R.id.tvTitle);
			tvTitle.setText(R.string.change_keywords_dialog_title);
			final EditText etKeywords = (EditText) mRoot
					.findViewById(R.id.etContent);
			Button btnOk = (Button) mRoot.findViewById(R.id.positiveButton);
			Button btnCancel = (Button) mRoot.findViewById(R.id.negativeButton);
			etKeywords.setHint(R.string.prod_keywords_input_hint);
			btnOk.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					changeKeywordsDialog.cancel();
					String prodKeywords = etKeywords.getText().toString();
					if (StringUtil.getInstance().IsEmpty(prodKeywords)) {
						Toast.makeText(AddKeywordsActivity.this,
								R.string.prod_keywords_not_empty,
								Toast.LENGTH_SHORT).show();
					} else {
						etKeywords.setText("");
						String[] s = prodKeywords.split(" ");
						for (int i = 0; i < s.length; i++) {
							diyKeyWordsList.add(s[i]);
							showKeyWordsList.add(s[i]);
						}
						adapter.setKeyWordsList(showKeyWordsList);
						adapter.notifyDataSetChanged();
						setGridViewHeightBasedOnChildren(keywords_gridview);
					}
				}
			});
			btnCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					changeKeywordsDialog.cancel();
				}
			});
			changeKeywordsDialog.setContentView(mRoot);
			changeKeywordsDialog.setCanceledOnTouchOutside(false);
		}

		if (null != changeKeywordsDialog && !isFinishing()
				&& !changeKeywordsDialog.isShowing()) {
			changeKeywordsDialog.show();
		}
	}

	private static final int REQUEST_CODE_FOR_SELECT_CATEGORY = 0x20001;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case REQUEST_CODE_FOR_SELECT_CATEGORY:

			TagCategoryRec rec = (TagCategoryRec) data
					.getSerializableExtra(SelectCategoryForAddProdActivity.KEY_CATEGORY_REC);
			if (null != rec) {
				if (!StringUtil.getInstance().IsEmpty(rec.getTagCatID())) {
					mProductRec.setTagCatName(null);
					isSelect = true;
				} else {
					mProductRec.setTagCatName(rec.getTagCatName());
					isSelect = false;
				}
				mProductRec.setTagCatId(rec.getTagCatID());
				String category = rec.getTagCatName();
				String oldCategory = tvparentId.getText().toString();
				if (oldCategory != null && !"".equals(oldCategory.trim())
						&& !category.equals(oldCategory)) {
					showKeyWordsList.clear();
					selectKeyWordsList.clear();
				}
				tvparentId.setText(category);
				compatibility_layout.setVisibility(View.VISIBLE);
				keywordsLayout.setVisibility(View.VISIBLE);
				keywordsLayout.performClick();
			} else {
				lockScreen("正在确定您发布需求的位置");
				locationUtil = new LocationUtil(this, bdLocationListener);
				locationUtil.startLocation();
			}
			if ("".equals(tvparentId.getText().toString())) {
				Toast.makeText(getApplicationContext(), "请选择信息分类", 1).show();
			}
			break;
		case REQUEST_CODE_FOR_SELECT_KEYWORDS:
			showKeyWordsList.clear();
			selectKeyWordsList.clear();
			KeyWordsListRec reclist = (KeyWordsListRec) data
					.getSerializableExtra("KYEWORDS_KEY");
			if (null != reclist.getRecs()) {
				for (int i = 0; i < reclist.getRecs().size(); i++) {
					if (null == reclist.getRecs().get(i).getTagID()) {
						diyKeyWordsList.add(reclist.getRecs().get(i)
								.getTagName());
					} else {
						selectKeyWordsList.add(reclist.getRecs().get(i));
						showKeyWordsList.add(reclist.getRecs().get(i)
								.getTagName());
					}

				}
			}
			showKeyWordsList.addAll(diyKeyWordsList);
			adapter.setKeyWordsList(showKeyWordsList);
			adapter.notifyDataSetChanged();
			setGridViewHeightBasedOnChildren(keywords_gridview);
			if (!isEdit) {
				lockScreen("正在确定您发布需求的位置");
				locationUtil = new LocationUtil(this, bdLocationListener);
				locationUtil.startLocation();
			}

			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void readyToAddProduct() {

		String keywordsID = null;
		for (int i = 0; i < selectKeyWordsList.size(); i++) {
			if (i == 0) {
				keywordsID = selectKeyWordsList.get(i).getTagID();
			} else {
				keywordsID = keywordsID + ","
						+ selectKeyWordsList.get(i).getTagID();
			}
		}

		String keywordsName = null;
		for (int i = 0; i < diyKeyWordsList.size(); i++) {
			if (i == 0) {
				keywordsName = diyKeyWordsList.get(i);
			} else {
				keywordsName = keywordsName + "," + diyKeyWordsList.get(i);
			}
		}
		String TagCatId = mProductRec.getTagCatId();
		String TagCatName = mProductRec.getTagCatName();
		if (StringUtil.getInstance().IsEmpty(TagCatId)
				&& StringUtil.getInstance().IsEmpty(TagCatName)) {
			releaseScreen();
			Toast.makeText(this, R.string.category_info_not_empty,
					Toast.LENGTH_SHORT).show();
			return;
		}
		addKeyWords(compatibility, mProductRec, keywordsID, keywordsName);
	}

	private void addKeyWords(double compatibility, ProductRec rec,
			String keywordsId, String keyWordsName) {

		System.out.println("===compatibility====" + compatibility);
		if (!NetworkUtil.getInstance().existNetwork(this)) {
			Toast.makeText(this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		lockScreen(this.getResources().getString(
				R.string.add_key_words_send_ing));
		final String currMethod = "readyToChanceFav:";
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					Log.e(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseAddKeyWords(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						Toast.makeText(AddKeywordsActivity.this, "发布成功",
								Toast.LENGTH_SHORT).show();
						setResult(RESULT_OK);
						finish();
					} else {
						String message = getString(R.string.add_key_words_rem);
						String msg = getString(R.string.add_key_words_rem);
						if (null == br) {
							msg = "parseChanceFav: Result:BaseResponse is null.";
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
									AddKeywordsActivity.this, msg);
						} else {
							Toast.makeText(AddKeywordsActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								AddKeywordsActivity.this, msg);
					} else {
						Toast.makeText(AddKeywordsActivity.this,
								R.string.add_key_words_rem, Toast.LENGTH_SHORT)
								.show();
					}
				}
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				// TODO Auto-generated method stub
				releaseScreen();
				Log.e(TAG, currMethod + "onFailure,Msg->" + strMsg, t);
				if (BuildConfig.isDebug) {
					String msg = strMsg;
					if (StringUtil.getInstance().IsEmpty(msg)) {
						msg = null == t ? "Exception t is null." : t.toString();
					}
					ExceptionRemHelper.showExceptionReport(
							AddKeywordsActivity.this, msg);
				} else {
					Toast.makeText(AddKeywordsActivity.this,
							R.string.add_key_words_rem, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
			}
		};
		gs.addKeyWords(isEdit, AddKeywordsActivity.this, compatibility, rec,
				keywordsId, keyWordsName, cityid, callBack);
	}

	// 获取匹配数目列表

	private void readyToGetMatchNum() {

		String keywordsID = null;
		for (int i = 0; i < selectKeyWordsList.size(); i++) {
			if (i == 0) {
				keywordsID = selectKeyWordsList.get(i).getTagID();
			} else {
				keywordsID = keywordsID + ","
						+ selectKeyWordsList.get(i).getTagID();
			}
		}

		String keywordsName = null;
		for (int i = 0; i < diyKeyWordsList.size(); i++) {
			if (i == 0) {
				keywordsName = diyKeyWordsList.get(i);
			} else {
				keywordsName = keywordsName + "," + diyKeyWordsList.get(i);
			}
		}

		String TagCatId = mProductRec.getTagCatId();
		String TagCatName = mProductRec.getTagCatName();
		if (StringUtil.getInstance().IsEmpty(TagCatName)
				&& StringUtil.getInstance().IsEmpty(TagCatId)) {
			Toast.makeText(this, "请您先选择分类", Toast.LENGTH_LONG).show();
			return;
		}
		if (StringUtil.getInstance().IsEmpty(keywordsID)
				&& StringUtil.getInstance().IsEmpty(keywordsName)) {
			Toast.makeText(this, "请您先选择标签", Toast.LENGTH_LONG).show();
			return;
		}
		getMatchNum(TagCatId, String.valueOf(compatibility), TagCatName,
				keywordsID, keywordsName);
	}

	private void getMatchNum(String tagCatId, String matchPercent,
			String tagCatName, String tagIds, String tagNames) {
		if (!NetworkUtil.getInstance().existNetwork(this)) {
			Toast.makeText(this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		lockScreen(this.getResources().getString(R.string.get_words_num_ing));
		final String currMethod = "getMatchNum:";
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					Log.e(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					ResponseGetMatchNum br = parser.parseGetMatchNum(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						// 获取到匹配数目
						String num = br.getMatchNum();
						ShowMumDialog(num);
					} else {
						String message = getString(R.string.get_num_rem);
						String msg = getString(R.string.get_num_rem);
						if (null == br) {
							msg = "getMatchNum: ResponseGetMatchNum is null.";
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
									AddKeywordsActivity.this, msg);
						} else {
							Toast.makeText(AddKeywordsActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								AddKeywordsActivity.this, msg);
					} else {
						Toast.makeText(AddKeywordsActivity.this,
								R.string.get_num_rem, Toast.LENGTH_SHORT)
								.show();
					}
				}
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				// TODO Auto-generated method stub
				releaseScreen();
				Log.e(TAG, currMethod + "onFailure,Msg->" + strMsg, t);
				if (BuildConfig.isDebug) {
					String msg = strMsg;
					if (StringUtil.getInstance().IsEmpty(msg)) {
						msg = null == t ? "Exception t is null." : t.toString();
					}
					ExceptionRemHelper.showExceptionReport(
							AddKeywordsActivity.this, msg);
				} else {
					Toast.makeText(AddKeywordsActivity.this,
							R.string.get_num_rem, Toast.LENGTH_SHORT).show();
				}
				super.onFailure(t, strMsg);
			}
		};
		gs.getMatchNum(this, tagCatId, matchPercent, tagCatName, tagIds,
				tagNames, callBack);
	}

	private MyRemDialog numDialog;

	// 显示中意信息条数的对话框
	private void ShowMumDialog(String num) {
		numDialog = new MyRemDialog(this, R.style.Dialog);
		numDialog.setTitle(R.string.num_dialog_title);
		numDialog.setMessage("目前与此相关联的信息有" + num + "条\n您想：");
		numDialog.setPosBtnText(R.string.num_dialog_btn_post);
		numDialog.setNegBtnText(R.string.num_dialog_btn_continue);
		numDialog.setPosBtnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 发布
				getSharedPreferences("state", 0).edit().clear().commit();
				readyToAddProduct();
			}
		});
		numDialog.setNegBtnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				numDialog.dismiss();
			}
		});
		numDialog.show();

	}
}
