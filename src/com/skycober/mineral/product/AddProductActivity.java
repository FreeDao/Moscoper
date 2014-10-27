package com.skycober.mineral.product;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.util.AbFileUtil;
import com.ab.util.AbStrUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.google.gson.Gson;
import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.FragmentChangeActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.account.GoodsService;
import com.skycober.mineral.account.ServerResponseParser;
import com.skycober.mineral.bean.KeyWordsListRec;
import com.skycober.mineral.bean.KeyWordsRec;
import com.skycober.mineral.bean.PicRec;
import com.skycober.mineral.bean.ProductRec;
import com.skycober.mineral.bean.TagCategoryRec;
import com.skycober.mineral.network.CityModel;
import com.skycober.mineral.network.CityModel.CityItemModel;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseAddProduct;
import com.skycober.mineral.network.ResponseUploadPic;
import com.skycober.mineral.product.KeyWordsAdapter.OnItemDelectLongClick;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.LocationUtil;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.Trace;
import com.skycober.mineral.util.Util;
import com.skycober.mineral.widget.HorizontalListView;
import com.skycober.mineral.widget.MyRemDialog;

/**
 * 添加商品 我有
 * 
 * @author Yes366
 * 
 */
public class AddProductActivity extends BaseActivity {

	private static final String TAG = "AddProductActivity";

	@ViewInject(id = R.id.title_button_left)
	ImageButton btnLeft;
	@ViewInject(id = R.id.title_button_right)
	ImageButton btnRight;
	@ViewInject(id = R.id.title_text)
	TextView tvTitle;
	@ViewInject(id = R.id.logoLayout)
	ViewGroup logoGroup;
	@ViewInject(id = R.id.iv_logo)
	ImageView logo;
	@ViewInject(id = R.id.nameLayout)
	ViewGroup nameGroup;
	@ViewInject(id = R.id.tvName)
	TextView tvName;
	@ViewInject(id = R.id.keywordsLayout)
	ViewGroup keywordsGroup;
	@ViewInject(id = R.id.tvKeyword)
	TextView tvKeyword;
	@ViewInject(id = R.id.descLayout)
	ViewGroup descGroup;
	@ViewInject(id = R.id.tvDesc)
	TextView tvDesc;
	@ViewInject(id = R.id.tvparentId)
	TextView tvparentId;
	@ViewInject(id = R.id.tvcityId)
	TextView tvcityId;
	@ViewInject(id = R.id.tvprovinceId)
	TextView tvprovinceId;
	@ViewInject(id = R.id.lv_pic)
	HorizontalListView lvPic;

	@ViewInject(id = R.id.cityIdLayout)
	RelativeLayout cityIdLayout;
	@ViewInject(id = R.id.provinceIdLayout)
	RelativeLayout provinceIdLayout;

	@ViewInject(id = R.id.parentIdLayout)
	RelativeLayout parentIdLayout;
	@ViewInject(id = R.id.iv_add_pic_guide)
	ImageView ivAddPicGuide;
	@ViewInject(id = R.id.desc_editbox)
	EditText desc_editbox;

	private List<String> categoryNameList = new ArrayList<String>();
	private Map<String, String> categoryIdMap = new HashMap<String, String>();
	private List<KeyWordsRec> selectKeyWordsList = new ArrayList<KeyWordsRec>();
	private List<String> diyKeyWordsList = new ArrayList<String>();
	private String cityid = null;
	private int pNameIndex = 0;
	private List<CityItemModel> citys = new ArrayList<CityItemModel>();
	private List<CityItemModel> provinces = new ArrayList<CityItemModel>();
	private CityModel cityModel;
	private boolean isSave;

	/**
	 * ===========城市dialog
	 */
	private void ShowLocationDialog() {
		String[] citysStr = new String[citys.size()];
		for (int i = 0; i < citys.size(); i++) {
			citysStr[i] = citys.get(i).getName();
		}
		AlertDialog dialog = new AlertDialog.Builder(AddProductActivity.this)
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

	private void ShowProvinceDialog() {
		String[] citysStr = new String[provinces.size()];
		for (int i = 0; i < provinces.size(); i++) {
			citysStr[i] = provinces.get(i).getName();
		}
		AlertDialog dialog = new AlertDialog.Builder(AddProductActivity.this)
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
			String cName = arg0.getCity();
			String temp = arg0.getProvince();
			if (temp != null) {
				String pName = temp.substring(0, 2);
				try {
					String str = Util.readStringFromFile("area",
							AddProductActivity.this);
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
				Toast.makeText(getApplicationContext(), "请检查你的网络！！！", 1).show();
			}

		}
	};
	private LocationUtil locationUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_product);

		InitTopbar();
		parentIdLayout.performClick();
		mProductRec = new ProductRec();
		// 已选标签列表
		keywords_gridview = (GridView) findViewById(R.id.keywords_gridview);
		adapter = new KeyWordsAdapter(showKeyWordsList, this);
		adapter.setOnItemDelectLongClick(new OnItemDelectLongClick() {

			@Override
			public void OnItemLongClick(int pos) {
				// TODO Auto-generated method stub
				// 删除已选标签
				showDelectKeyWordsRem(pos);
			}
		});
		keywords_gridview.setAdapter(adapter);
		// 添加图片
		InitPicData();
		List<String> category = SettingUtil.getInstance(this).getCategoryInfo();
		Map<String, String> Idcategory = SettingUtil.getInstance(this)
				.getCategoryMap();
		for (int i = 0; i <= category.size() - 1; i++) {
			if (StringUtil.getInstance().IsEmpty(category.get(i))) {
				categoryNameList.add(category.get(i));
				categoryIdMap.put(category.get(i),
						Idcategory.get(category.get(i)));
			}
		}
		saveAddKeyWordState();
	}

	public void saveAddKeyWordState() {
		SharedPreferences saveSate = getSharedPreferences(
				"saveAddProductState", 0);
		String parentId = saveSate.getString("parentId", "");
		String parentIdName = saveSate.getString("parentIdName", null);// 信息分类
		if (!"".equals(parentId)) {

			tvparentId.setText(parentIdName);
			isSelect = true;
			// compatibility_layout.setVisibility(View.VISIBLE);
			keywordsGroup.setVisibility(View.VISIBLE);
			desc_editbox.setText(saveSate.getString("desc", ""));
			mProductRec.setTagCatId(parentId);
			mProductRec.setTagCatName(null);

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
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (!isSave) {
			SharedPreferences saveAddProductState = getSharedPreferences(
					"saveAddProductState", 0);
			SharedPreferences.Editor editor = saveAddProductState.edit();
			editor.putString("parentIdName", tvparentId.getText().toString());// 信息分类,分类名
			editor.putString("desc", desc_editbox.getText().toString());
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

	// 删除已选标签
	private void showDelectKeyWordsRem(final int pos) {
		// TODO Auto-generated method stub
		final MyRemDialog dialog = new MyRemDialog(AddProductActivity.this,
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

	private void InitTopbar() {
		btnLeft.setImageResource(R.drawable.back_btn_selector);// 返回
		btnLeft.setOnClickListener(btnLeftClickListener);
		logoGroup.setOnClickListener(logoClickListener);// logo
		nameGroup.setOnClickListener(btnNameClickListener);// 没用到
		keywordsGroup.setOnClickListener(btnKeywordClickListener);// 信息标签
		parentIdLayout.setOnClickListener(parentIdLayoutclickListener);// 信息分类
		cityIdLayout.setOnClickListener(cityIdLayoutclickListener);// 所在城市
		provinceIdLayout.setOnClickListener(provinceIdLayoutclickListener);// 所在省份
		btnRight.setImageResource(R.drawable.check_btn_selector);// 确认
		btnRight.setOnClickListener(btnRightClickListener);
		tvTitle.setText(R.string.add_product_page_title);
	}

	// 返回
	private View.OnClickListener btnLeftClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			finish();
		}
	};
	// logo
	private View.OnClickListener logoClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			// 修改logo对话框
			showChangeLogoRem();
			v.setEnabled(true);
		}
	};
	// 没用到
	private View.OnClickListener btnNameClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			showChangeNameRem();
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
				skipToChangekeyWords();
			} else {
				lockScreen("正在确定您发布信息的位置");
				locationUtil = new LocationUtil(AddProductActivity.this,
						bdLocationListener);
				locationUtil.startLocation();
				showChangeKeywordsRem();
			}
			v.setEnabled(true);
		}
	};

	private Dialog changeNameDialog;

	protected void showChangeNameRem() {
		if (null == changeNameDialog) {
			changeNameDialog = new Dialog(this, R.style.dialog);
			ViewGroup mRoot = (ViewGroup) LayoutInflater.from(this).inflate(
					R.layout.product_change_name_dialog_content_view, null);
			TextView tvTitle = (TextView) mRoot.findViewById(R.id.tvTitle);
			tvTitle.setText(R.string.change_prod_name_dialog_title);
			final EditText etName = (EditText) mRoot
					.findViewById(R.id.etContent);
			Button btnOk = (Button) mRoot.findViewById(R.id.positiveButton);
			Button btnCancel = (Button) mRoot.findViewById(R.id.negativeButton);
			etName.setHint(R.string.prod_name_input_hint);
			String name = mProductRec.getName();
			etName.setText(name);
			btnOk.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					changeNameDialog.cancel();
					String prodName = etName.getText().toString();
					if (StringUtil.getInstance().IsEmpty(prodName)) {
						Toast.makeText(AddProductActivity.this,
								R.string.prod_name_not_empty,
								Toast.LENGTH_SHORT).show();
					} else {
						tvName.setText(prodName);
						mProductRec.setName(prodName);
					}
				}
			});
			btnCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					changeNameDialog.cancel();
				}
			});
			changeNameDialog.setContentView(mRoot);
			changeNameDialog.setCanceledOnTouchOutside(false);
		}

		if (null != changeNameDialog && !isFinishing()
				&& !changeNameDialog.isShowing()) {
			changeNameDialog.show();
		}
	}

	private Dialog changeBriefDialog;

	private static final int FROM_PHOTOS_GET_DATA = 0x10001;
	private static final int FROM_CAMERA_GET_DATA = 0x10002;
	private static final int AVATAR_CAMERA_CROP_DATA = 0x10003;
	private Dialog selectLogoDialog;

	/**
	 * 显示修改logo对话框
	 */
	
	
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
					// 使用相机拍照
					doPickPhotoAction(FROM_CAMERA_GET_DATA);
				}
			});
			btnAlbum.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					selectLogoDialog.cancel();
					try {
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
								null);
						intent.setType("image/*");
						startActivityForResult(intent, FROM_PHOTOS_GET_DATA);
					} catch (ActivityNotFoundException e) {
						Toast.makeText(AddProductActivity.this, "没有找到照片",
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

	/**
	 * 描述：从照相机获取
	 */
	private void doPickPhotoAction(int requestCode) {
		String status = Environment.getExternalStorageState();
		// 判断是否有SD卡,如果有sd卡存入sd卡在说，没有sd卡直接转换为图片
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			if (null == PHOTO_DIR) {
				String photo_dir = AbFileUtil.getDefaultImageDownPathDir();
				PHOTO_DIR = new File(photo_dir);
			}
			doTakePhoto(requestCode);
		} else {
			Toast.makeText(AddProductActivity.this, "没有可用的存储卡",
					Toast.LENGTH_LONG).show();
		}
	}

	/* 拍照的照片存储位置 */
	public static File PHOTO_DIR = null;
	// 照相机拍照得到的图片
	public File mCurrentPhotoFile;
	private String mFileName;

	/**
	 * 拍照获取图片
	 */
	protected void doTakePhoto(int requestCode) {
		try {
			// 使用相机拍照
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// intent.putExtra(MediaStore.EXTRA_OUTPUT,
			// Uri.fromFile(newFile(F.SD_CARD_TEMP_PHOTO_PATH)));
			startActivityForResult(intent, requestCode);
		} catch (Exception e) {
			Toast.makeText(AddProductActivity.this, "未找到系统相机程序",
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 从相册得到的url转换为SD卡中图片路径
	 */
	public String getPath(Uri uri) {
		if (null == uri || AbStrUtil.isEmpty(uri.getAuthority())) {
			return null;
		}
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(column_index);
		return path;
	}

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
	// 所在城市
	private View.OnClickListener cityIdLayoutclickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			ShowLocationDialog();
			v.setEnabled(true);
		}
	};
	// 所在省份
	private View.OnClickListener provinceIdLayoutclickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			ShowProvinceDialog();
			v.setEnabled(true);
		}
	};

	private static final int REQUEST_CODE_FOR_SELECT_CATEGORY = 0x20001;

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
						Toast.makeText(AddProductActivity.this,
								R.string.prod_detail_desc_not_empty,
								Toast.LENGTH_SHORT).show();
					} else {
						tvDesc.setText(prodDesc);
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

	// 确认
	private View.OnClickListener btnRightClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			picIndex = -1;
			// 添加我有
			getSharedPreferences("saveAddProductState", 0).edit().clear()
					.commit();
			isSave = true;
			readyToAddProduct();
			v.setEnabled(true);
		}
	};

	private static final int FROM_PHOTOS_GET_DATA_FOR_SINGLE = 0x30001;
	private static final int FROM_CAMERA_GET_DATA_FOR_SINGLE = 0x30002;
	private static final int AVATAR_CAMERA_CROP_DATA_FOR_SINGLE = 0x30003;
	private Dialog selectPhotoDialog;

	/**
	 * 显示修改图片对话框
	 */
	protected void showSelectPhotoRem() {
		if (null == selectPhotoDialog) {
			selectPhotoDialog = new Dialog(this, R.style.dialog);
			ViewGroup mRoot = (ViewGroup) LayoutInflater.from(this).inflate(
					R.layout.select_photo_dialog_content_view, null);
			int width = getWindowManager().getDefaultDisplay().getWidth();
			selectPhotoDialog.setContentView(mRoot, new LayoutParams(width,
					LayoutParams.WRAP_CONTENT));
			Button btnCamera = (Button) mRoot.findViewById(R.id.btnCamera);
			Button btnAlbum = (Button) mRoot.findViewById(R.id.btnAlbum);
			Button btnCancel = (Button) mRoot.findViewById(R.id.btnCancel);
			btnCamera.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					selectPhotoDialog.cancel();
					doPickPhotoAction(FROM_CAMERA_GET_DATA_FOR_SINGLE);// 从相机获取图片
				}
			});
			btnAlbum.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					selectPhotoDialog.cancel();
					try {
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
								null);
						intent.setType("image/*");
						startActivityForResult(intent,
								FROM_PHOTOS_GET_DATA_FOR_SINGLE);// 从相册获取图片
					} catch (ActivityNotFoundException e) {
						Toast.makeText(AddProductActivity.this, "没有找到照片",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			btnCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					selectPhotoDialog.cancel();
				}
			});
		}
		Window window = selectPhotoDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.mystyle);
		selectPhotoDialog.setCanceledOnTouchOutside(true);
		if (null != selectPhotoDialog && !selectPhotoDialog.isShowing()
				&& !isFinishing()) {
			selectPhotoDialog.show();
		}
	}

	private PicAdapter picAdapter;
	private GridView keywords_gridview;
	private KeyWordsAdapter adapter;
	private List<String> showKeyWordsList = new ArrayList<String>();

	private void InitPicData() {

		List<PicRec> picList = new ArrayList<PicRec>();
		picList.add(new PicRec());
		picAdapter = new PicAdapter(this,
				R.layout.add_product_pic_listview_item, picList);
		picAdapter
				.setOnItemCountChangedListener(new OnItemCountChangedListener() {

					@Override
					public void onItemCountChanged(int size) {
						if (size <= 1) {
							// ivAddPicGuide.setVisibility(View.VISIBLE);
						} else {
							ivAddPicGuide.setVisibility(View.GONE);
						}
					}
				});
		lvPic.setAdapter(picAdapter);
		lvPic.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				List<PicRec> picList = picAdapter.getPicRecList();
				if (null != picList && pos < picList.size()) {
					int count = picList.size();
					if (pos == count - 1) {
						// 选择用那种方式添加图片
						showSelectPhotoRem();
					} else {
						// 查看大图
						Intent intent = new Intent(AddProductActivity.this,
								BigPicImgActivity.class);
						intent.putExtra("Path", picList.get(pos).getUrl());
						startActivity(intent);
						overridePendingTransition(R.anim.my_scale_action,
								R.anim.my_alpha_action);
					}
				}
			}
		});
		lvPic.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int pos, long id) {
				int count = picAdapter.getCount();
				if (pos >= 0 && pos != count - 1) {
					PicRec picRec = picAdapter.getPicRecList().get(pos);
					// 删除图片
					showRemovePicRem(picRec);
				}
				return true;
			}
		});
	}

	private MyRemDialog removePicDialog;

	// 删除图片对话框
	protected void showRemovePicRem(final PicRec picRec) {
		if (null == removePicDialog) {
			removePicDialog = new MyRemDialog(this, R.style.dialog);
		}
		removePicDialog.setTitle(R.string.remove_pic_dialog_title);
		removePicDialog.setMessage(R.string.remove_pic_dialog_message);
		removePicDialog.setPosBtnText(R.string.remove_pic_btn_del);
		removePicDialog.setNegBtnText(R.string.remove_pic_btn_cancel);
		removePicDialog.setPosBtnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isSucRemove = picAdapter.removePic(picRec);
				if (isSucRemove) {
					Toast.makeText(AddProductActivity.this,
							R.string.remove_pic_succeed_rem, Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(AddProductActivity.this,
							R.string.remove_pic_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		removePicDialog.show();
	}

	public class PicAdapter extends BaseAdapter {
		private List<PicRec> picRecList;
		private int layoutId;
		private LayoutInflater inflater;
		private OnItemCountChangedListener onItemCountChangedListener;
		private FinalBitmap picFB;

		public PicAdapter(Context context, int layoutId, List<PicRec> picRecList) {
			this.picRecList = picRecList;
			this.layoutId = layoutId;
			this.inflater = LayoutInflater.from(context);
			picFB = FinalBitmap.create(context);
			picFB.configBitmapMaxWidth(50);
			picFB.configBitmapMaxHeight(50);
			picFB.configLoadingImage(R.drawable.default_mineral);
			picFB.configLoadfailImage(R.drawable.default_mineral);
			picFB.setDefaultLocalBitmapWidth(50);
			picFB.setDefaultLocalBitmapHeight(50);
		}

		public OnItemCountChangedListener getOnItemCountChangedListener() {
			return onItemCountChangedListener;
		}

		public void setOnItemCountChangedListener(
				OnItemCountChangedListener onItemCountChangedListener) {
			this.onItemCountChangedListener = onItemCountChangedListener;
		}

		public List<PicRec> getPicRecList() {
			return picRecList;
		}

		public void setPicRecList(List<PicRec> picRecList) {
			if (null == this.picRecList) {
				this.picRecList = picRecList;
			} else {
				this.picRecList.clear();
				if (null != picRecList && picRecList.size() > 0) {
					this.picRecList.addAll(picRecList);
				}
			}
			notifyDataSetChanged();
		}

		public void addPic(PicRec picRec) {
			int count = getCount();
			if (null != this.picRecList) {
				this.picRecList.add(count - 1, picRec);
			}
			notifyDataSetChanged();
		}

		public boolean removePic(PicRec picRec) {
			try {
				boolean isSucRemove = this.picRecList.remove(picRec);
				notifyDataSetChanged();
				return isSucRemove;
			} catch (Exception e) {
				Log.e(TAG, "RemovePic Exception.", e);
				return false;
			}
		}

		@Override
		public int getCount() {
			int size = picRecList.size();
			if (null != onItemCountChangedListener) {
				onItemCountChangedListener.onItemCountChanged(size);
			}
			return size;
		}

		@Override
		public Object getItem(int pos) {
			return picRecList.get(pos);
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}

		@Override
		public View getView(int pos, View view, ViewGroup parent) {
			if (null == view) {
				view = inflater.inflate(layoutId, null);
			}
			ImageView ivPic = (ImageView) view.findViewById(R.id.ivPic);
			int count = picRecList.size();
			if (pos == count - 1) {
				ivPic.setImageResource(R.drawable.pic_btn_add);
			} else {
				PicRec rec = picRecList.get(pos);
				picFB.display(ivPic, rec.getUrl());
			}
			return view;
		}
	}

	public interface OnItemCountChangedListener {
		void onItemCountChanged(int size);
	}

	protected void showparentSettingRem() {
		final Dialog dialog = new Dialog(this, R.style.Dialog);
		WindowManager m = this.getWindowManager();
		Display d = m.getDefaultDisplay();
		WindowManager.LayoutParams p = this.getWindow().getAttributes();
		p.width = (int) (d.getWidth() * 0.8);
		final ViewGroup root = (ViewGroup) LayoutInflater.from(this).inflate(
				R.layout.parent_id_content_view, null);
		dialog.setContentView(root, p);
		dialog.show();
	}

	public static final String KEY_PRODUCT_REC = "key_product_rec";
	private ProductRec mProductRec;

	// 添加我有
	private void readyToAddProduct() {

		// 信息详情
		mProductRec.setDescription(desc_editbox.getText().toString());
		final PicRec uploadPicRec = getNeedUploadPicRec();
		if (null != uploadPicRec) {
			readyToUploadPic(uploadPicRec);
			return;
		}

		final String currMethod = "readyToAddProduct:";
		if (null == mProductRec) {
			releaseScreen();
			return;
		}
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
		String description = mProductRec.getDescription();
		if (StringUtil.getInstance().IsEmpty(description)
				|| description.trim().length() == 0) {
			releaseScreen();
			Toast.makeText(this, R.string.add_prod_detail_desc_not_empty,
					Toast.LENGTH_SHORT).show();
			return;
		}
		String filePath = mProductRec.getImg();
		if (null == filePath)
			filePath = "";
		File logoFile = new File(filePath);
		String galleryIds = null;
		List<PicRec> picRecList = picAdapter.getPicRecList();
		if (null != picRecList && picRecList.size() > 1) {
			StringBuffer buff = new StringBuffer();
			for (int i = 0; i < picRecList.size() - 1; i++) {
				PicRec picRec = picRecList.get(i);
				buff.append(picRec.getId()).append(",");
			}
			if (buff.toString().contains(",")) {
				buff.delete(buff.length() - 1, buff.length());
			}
			galleryIds = buff.toString();
		}
		lockScreen(getString(R.string.add_product_send_ing));
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onFailure(Throwable t, String strMsg) {
				releaseScreen();
				if (BuildConfig.isDebug) {
					Log.e(TAG, currMethod + "onFail.Msg->" + strMsg, t);
					String exception = (null == strMsg ? "" : strMsg)
							+ ",Exception->" + t.toString();
					ExceptionRemHelper.showExceptionReport(
							AddProductActivity.this, exception);
				} else {
					Toast.makeText(AddProductActivity.this,
							R.string.add_product_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					Log.v("nanoha", "Add product json:" + json);
					Log.e("wangxu", "Add product json:" + json);
					ServerResponseParser parser = new ServerResponseParser();
					ResponseAddProduct response = (ResponseAddProduct) parser
							.parseAddProduct(json);
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						// 发布成功，返回前一页面，进行刷新数据
						Toast.makeText(getApplicationContext(), "发布成功", 1)
								.show();
						Intent data = getIntent();
						if (null == data)
							data = new Intent();
						data.putExtra(KEY_PRODUCT_REC, response.getProductRec());
						setResult(RESULT_OK, data);
						finish();
					} else {
						String message = getString(R.string.add_product_failed_rem);
						int errorCode = response.getErrorCode();
						SettingUtil.getInstance(AddProductActivity.this)
								.clearLoginInfo();
						FragmentChangeActivity.leftFragment
								.refreshLoginState(true);
						message = response.getMessage();
						switch (errorCode) {
						// TODO 登录的错误码
						default:
							break;
						}
						Log.e(TAG, currMethod + "onSuccess->result errorCode:"
								+ errorCode + ", message:" + message);
						if (BuildConfig.isDebug) {
							String exception = currMethod
									+ "onSuccess->fail.result errorCode:"
									+ errorCode;
							ExceptionRemHelper.showExceptionReport(
									AddProductActivity.this, exception);
						} else {
							Toast.makeText(AddProductActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Log.e(TAG, currMethod + "onSuccess->result t is null.");
					if (BuildConfig.isDebug) {
						String exception = currMethod
								+ "onSuccess->result t is null.";
						ExceptionRemHelper.showExceptionReport(
								AddProductActivity.this, exception);
					} else {
						Toast.makeText(AddProductActivity.this,
								R.string.add_product_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
		if ((keywordsID == null || "".equals(keywordsID))
				&& (keywordsName == null || "".equals(keywordsName))) {
			Toast.makeText(getApplicationContext(), "请选择标签", 1).show();
			releaseScreen();
		} else {
			gs.AddProduct(AddProductActivity.this, mProductRec, logoFile,
					keywordsID, keywordsName, galleryIds, cityid, callBack);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case FROM_PHOTOS_GET_DATA:// 从相机获取logo图片
			Uri avatarUri = data.getData();
			String filePath = getPath(avatarUri);
			if (!AbStrUtil.isEmpty(filePath)) {
				Intent intent1 = new Intent(AddProductActivity.this,
						CropImageActivity.class);
				intent1.putExtra("PATH", filePath);
				startActivityForResult(intent1, AVATAR_CAMERA_CROP_DATA);
			} else {
				Toast.makeText(AddProductActivity.this, "未在存储卡中找到这个文件",
						Toast.LENGTH_LONG).show();
			}
			break;
		case FROM_CAMERA_GET_DATA:// 从相册获取logo图片
			// 跳转剪切界面
			String path = Util.getPhotoPath(AddProductActivity.this, data);
			Intent intent2 = new Intent(AddProductActivity.this,
					CropImageActivity.class);
			intent2.putExtra("PATH", path);
			startActivityForResult(intent2, AVATAR_CAMERA_CROP_DATA);
			break;

		case AVATAR_CAMERA_CROP_DATA:
			String avatarPath = data.getStringExtra("PATH");
			mProductRec.setImg(avatarPath);
			Trace.d(TAG, "LogoPath:" + avatarPath);
			FinalBitmap fb = getLogoFinalBitmap();
			fb.display(logo, avatarPath);
			break;
		case REQUEST_CODE_FOR_SELECT_CATEGORY:// 返回信息分类
			isSelect = true;
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
				String oldCcatepory = tvparentId.getText().toString();
				if (oldCcatepory != null && !"".equals(oldCcatepory.trim())
						&& !category.equals(oldCcatepory)) {
					showKeyWordsList.clear();
					selectKeyWordsList.clear();
				}
				tvparentId.setText(category);
				keywordsGroup.setVisibility(View.VISIBLE);
				keywordsGroup.performClick();
			} else {
				lockScreen("正在确定您发布信息的位置");
				locationUtil = new LocationUtil(this, bdLocationListener);
				locationUtil.startLocation();
			}
			if("".equals(tvparentId.getText())){
				Toast.makeText(AddProductActivity.this, "请选择信息分类", Toast.LENGTH_SHORT).show();
			}
			break;
		case REQUEST_CODE_FOR_SELECT_KEYWORDS:// 返回信息标签
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
			lockScreen("正在确定您发布信息的位置");
			locationUtil = new LocationUtil(this, bdLocationListener);
			locationUtil.startLocation();
			break;

		// ===================Start Photo Album========================
		case FROM_PHOTOS_GET_DATA_FOR_SINGLE:// 从相册获取图片
			Uri avatarUri2 = data.getData();
			String filePath3 = getPath(avatarUri2);
			if (!AbStrUtil.isEmpty(filePath3)) {
				Intent intent1 = new Intent(AddProductActivity.this,
						CropImageActivity.class);
				intent1.putExtra("PATH", filePath3);
				startActivityForResult(intent1,
						AVATAR_CAMERA_CROP_DATA_FOR_SINGLE);// 跳转剪切界面
			} else {
				Toast.makeText(AddProductActivity.this, "未在存储卡中找到这个文件",
						Toast.LENGTH_LONG).show();
			}
			break;
		case FROM_CAMERA_GET_DATA_FOR_SINGLE:// 从相机获取图片
			String path4 = Util.getPhotoPath(AddProductActivity.this, data);
			Intent intent3 = new Intent(AddProductActivity.this,
					CropImageActivity.class);
			intent3.putExtra("PATH", path4);
			startActivityForResult(intent3, AVATAR_CAMERA_CROP_DATA_FOR_SINGLE);
			break;

		case AVATAR_CAMERA_CROP_DATA_FOR_SINGLE:
			String avatarPath2 = data.getStringExtra("PATH");
			PicRec currPicRec = new PicRec();
			currPicRec.setUrl(avatarPath2);
			picAdapter.addPic(currPicRec);
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private FinalBitmap logoFB;

	private FinalBitmap getLogoFinalBitmap() {
		if (null == logoFB) {
			logoFB = FinalBitmap.create(this);
			logoFB.configBitmapMaxWidth(100);
			logoFB.configBitmapMaxHeight(100);
			logoFB.configLoadfailImage(R.drawable.default_mineral);
			logoFB.configLoadingImage(R.drawable.default_mineral);
		}
		logoFB.clearCache();
		logoFB.flushCache();
		return logoFB;
	}

	private int picIndex = -1;

	/**
	 * 上载图片
	 */
	private void readyToUploadPic(final PicRec picRec) {
		final String currMethod = "readyToUploadPic:";
		picIndex++;
		String message = String.format(getString(R.string.upload_pic_send_ing),
				picIndex + 1);
		lockScreen(message);
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onFailure(Throwable t, String strMsg) {
				releaseScreen();
				picIndex = -1;
				if (BuildConfig.isDebug) {
					Log.e(TAG, currMethod + "onFail.Msg->" + strMsg, t);
					String exception = (null == strMsg ? "" : strMsg)
							+ ",Exception->" + t.toString();
					ExceptionRemHelper.showExceptionReport(
							AddProductActivity.this, exception);
				} else {
					Toast.makeText(AddProductActivity.this,
							R.string.upload_pic_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				if (null != t) {
					String json = t.toString();
					ServerResponseParser parser = new ServerResponseParser();
					ResponseUploadPic response = (ResponseUploadPic) parser
							.parseUploadPic(json);
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						PicRec tmpPicRec = response.getPicRec();
						if (null != tmpPicRec) {
							picRec.setId(tmpPicRec.getId());
							picRec.setGoodsId(tmpPicRec.getGoodsId());
							picRec.setDescription(tmpPicRec.getDescription());
							picRec.setThumb(tmpPicRec.getThumb());
							picRec.setPath(picRec.getUrl());
							picRec.setUrl(tmpPicRec.getUrl());
							// 添加我有
							readyToAddProduct();
						} else {
							Log.e(TAG, currMethod + "response PicRec is null.");
							releaseScreen();
							picIndex = -1;
						}
					} else {
						releaseScreen();
						picIndex = -1;
						String message = getString(R.string.upload_pic_failed_rem);
						int errorCode = response.getErrorCode();
						switch (errorCode) {
						default:
							break;
						}
						Log.e(TAG, currMethod + "onSuccess->result errorCode:"
								+ errorCode);
						if (BuildConfig.isDebug) {
							String exception = currMethod
									+ "onSuccess->fail.result errorCode:"
									+ errorCode;
							ExceptionRemHelper.showExceptionReport(
									AddProductActivity.this, exception);
						} else {
							Toast.makeText(AddProductActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					releaseScreen();
					picIndex = -1;
					Log.e(TAG, currMethod + "onSuccess->result t is null.");
					if (BuildConfig.isDebug) {
						String exception = currMethod
								+ "onSuccess->result t is null.";
						ExceptionRemHelper.showExceptionReport(
								AddProductActivity.this, exception);
					} else {
						Toast.makeText(AddProductActivity.this,
								R.string.upload_pic_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
		GoodsService gs = new GoodsService();
		File picFile = new File(picRec.getUrl());
		String prodId = "";
		gs.UploadProdPic(this, prodId, picFile, null, callBack);
	}

	private PicRec getNeedUploadPicRec() {
		List<PicRec> picList = picAdapter.getPicRecList();
		if (null != picList) {
			for (int i = 0; i < picList.size() - 1; i++) {
				PicRec picRec = picList.get(i);
				String id = picRec.getId();
				if (!StringUtil.getInstance().IsEmpty(id)) {
					continue;
				} else {
					return picRec;
				}
			}
		}
		return null;
	}

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
						Toast.makeText(AddProductActivity.this,
								R.string.prod_keywords_not_empty,
								Toast.LENGTH_SHORT).show();
					} else {
						mProductRec.setTagCatId(null);
						mProductRec.setTagCatName(prodKeywords);
						tvparentId.setText(prodKeywords);
						keywordsGroup.setVisibility(View.VISIBLE);
						isSelect = false;
						// setGridViewHeightBasedOnChildren(keywords_gridview);
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

	private boolean isSelect = false;

	// 跳转信息分类列表
	protected void skipToChangeCategory() {
		Intent mIntent = new Intent(AddProductActivity.this,
				SelectCategoryForAddProdActivity.class);
		mIntent.putExtra("type", "publish");

		startActivityForResult(mIntent, REQUEST_CODE_FOR_SELECT_CATEGORY);
	}

	/**
	 * --------------------------------获取标签----------
	 */

	private static final int REQUEST_CODE_FOR_SELECT_KEYWORDS = 0x20002;

	protected void skipToChangekeyWords() {
		Intent mIntent = new Intent(AddProductActivity.this,
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
		params.height = (Util.dip2px(AddProductActivity.this, 80) * (count));
		gridView.setLayoutParams(params);
	}

	private Dialog changeKeywordsDialog;

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
						Toast.makeText(AddProductActivity.this,
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
}
