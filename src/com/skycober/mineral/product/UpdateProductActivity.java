package com.skycober.mineral.product;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.InputEvent;
import android.view.KeyEvent;
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
import com.google.gson.Gson;
import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.account.GoodsService;
import com.skycober.mineral.account.HomePageActivity;
import com.skycober.mineral.account.ServerResponseParser;
import com.skycober.mineral.bean.KeyWordsListRec;
import com.skycober.mineral.bean.KeyWordsRec;
import com.skycober.mineral.bean.PicRec;
import com.skycober.mineral.bean.ProductRec;
import com.skycober.mineral.bean.TagCategoryRec;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.CityModel;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseAddProduct;
import com.skycober.mineral.network.ResponseGetSingleProduct;
import com.skycober.mineral.network.ResponseUploadPic;
import com.skycober.mineral.network.CityModel.CityItemModel;
import com.skycober.mineral.product.KeyWordsAdapter.OnItemDelectLongClick;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.NetworkUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.Trace;
import com.skycober.mineral.util.Util;
import com.skycober.mineral.widget.HorizontalListView;
import com.skycober.mineral.widget.MyRemDialog;

/**
 * 编辑信息
 * 
 * @author Yes366
 * 
 */
public class UpdateProductActivity extends BaseActivity {

	private static final String TAG = "UpdateProductActivity";

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
	@ViewInject(id = R.id.lv_pic)
	HorizontalListView lvPic;
	@ViewInject(id = R.id.parentIdLayout)
	RelativeLayout parentIdLayout;
	@ViewInject(id = R.id.iv_add_pic_guide)
	ImageView ivAddPicGuide;
	@ViewInject(id = R.id.desc_editbox)
	EditText desc_editbox;
	@ViewInject(id = R.id.tvprovinceId)
	TextView tvprovinceId;
	@ViewInject(id = R.id.provinceIdLayout)
	RelativeLayout provinceIdLayout;
	@ViewInject(id = R.id.tvcityId)
	TextView tvcityId;
	@ViewInject(id = R.id.cityIdLayout)
	RelativeLayout cityIdLayout;
	private GridView keywords_gridview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_product);
		InitTopbar();
		InitPicData();
		InitProdData();

	}

	// 标签列表
	private void InitProdData() {
		keywords_gridview = (GridView) findViewById(R.id.keywords_gridview);
		adapter = new KeyWordsAdapter(showKeyWordsList, this);
		adapter.setOnItemDelectLongClick(new OnItemDelectLongClick() {

			@Override
			public void OnItemLongClick(int pos) {
				// TODO Auto-generated method stub
				showDelectKeyWordsRem(pos);
			}
		});
		keywords_gridview.setAdapter(adapter);
		Intent startIntent = getIntent();
		if (null != startIntent && startIntent.hasExtra(KEY_PRODUCT_REC)) {
			mProductRec = (ProductRec) startIntent
					.getSerializableExtra(KEY_PRODUCT_REC);
			readyToLoadProductInfo();
		}
		if (null == mProductRec) {
			mProductRec = new ProductRec();
		}

	}

	// 删除关键字
	private void showDelectKeyWordsRem(final int pos) {
		// TODO Auto-generated method stub
		final MyRemDialog dialog = new MyRemDialog(UpdateProductActivity.this,
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
		btnLeft.setImageResource(R.drawable.back_btn_selector);
		btnLeft.setOnClickListener(btnLeftClickListener);
		logoGroup.setOnClickListener(logoClickListener);
		nameGroup.setOnClickListener(btnNameClickListener);// 没用到
		keywordsGroup.setOnClickListener(btnKeywordClickListener);
		parentIdLayout.setOnClickListener(parentIdLayoutclickListener);// 信息分类
		provinceIdLayout.setOnClickListener(provinceIdLayoutClickListener);
		cityIdLayout.setOnClickListener(cityIdLayoutClickListener);

		descGroup.setOnClickListener(btnDetailDescClickListener);
		btnRight.setImageResource(R.drawable.check_btn_selector);
		btnRight.setOnClickListener(btnRightClickListener);
		tvTitle.setText(R.string.update_product_page_title);
	}

	private View.OnClickListener btnLeftClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (hasChanged) {
				showExitRemDialog();
			} else {
				setResult(RESULT_OK);
				finish();
			}
		}
	};

	private View.OnClickListener cityIdLayoutClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			ShowLocationDialog();
			v.setEnabled(true);
		}
	};

	private View.OnClickListener provinceIdLayoutClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			ShowProvinceDialog();
			v.setEnabled(true);
		}
	};

	private View.OnClickListener logoClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			showChangeLogoRem();
			v.setEnabled(true);
		}
	};
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
	private View.OnClickListener btnKeywordClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			if (isSelect) {
				skipToChangekeyWords();
			} else {
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
						Toast.makeText(UpdateProductActivity.this,
								R.string.prod_name_not_empty,
								Toast.LENGTH_SHORT).show();
					} else {
						tvName.setText(prodName);
						mProductRec.setName(prodName);
						hasChanged = true;
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
						Toast.makeText(UpdateProductActivity.this, "没有找到照片",
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
			Toast.makeText(UpdateProductActivity.this, "没有可用的存储卡",
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
			// mFileName = System.currentTimeMillis() + ".jpg";
			// mCurrentPhotoFile = new File(PHOTO_DIR, mFileName);
			// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE,
			// null);
			// intent.putExtra(MediaStore.EXTRA_OUTPUT,
			// Uri.fromFile(mCurrentPhotoFile));
			// startActivityForResult(intent, requestCode);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, requestCode);
		} catch (Exception e) {
			Toast.makeText(UpdateProductActivity.this, "未找到系统相机程序",
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
			// skipToChangeCategory();
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
	private EditText etDetailDesc;

	protected void showDetailDescRem() {
		if (null == changeDetailDescDialog) {
			changeDetailDescDialog = new Dialog(this, R.style.dialog);
			ViewGroup mRoot = (ViewGroup) LayoutInflater.from(this).inflate(
					R.layout.product_change_name_dialog_content_view, null);
			TextView tvTitle = (TextView) mRoot.findViewById(R.id.tvTitle);
			tvTitle.setText(R.string.change_detail_desc_dialog_title);
			etDetailDesc = (EditText) mRoot.findViewById(R.id.etContent);
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
						Toast.makeText(UpdateProductActivity.this,
								R.string.prod_detail_desc_not_empty,
								Toast.LENGTH_SHORT).show();
					} else {
						tvDesc.setText(prodDesc);
						mProductRec.setDescription(prodDesc);
						hasChanged = true;
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
		etDetailDesc.setText(null == mProductRec ? "" : mProductRec
				.getDescription());
		if (null != changeDetailDescDialog && !isFinishing()
				&& !changeDetailDescDialog.isShowing()) {
			changeDetailDescDialog.show();
		}
	}

	private View.OnClickListener btnRightClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			readyToUpdateProduct();
			// if (null == waitRemoveList || waitRemoveList.size() == 0) {
			// picIndex = -1;
			// readyToUpdateProduct();
			// } else {
			// removePicIndex = -1;
			// readyToRemovePic();
			// }
			v.setEnabled(true);
		}
	};

	private static final int FROM_PHOTOS_GET_DATA_FOR_SINGLE = 0x30001;
	private static final int FROM_CAMERA_GET_DATA_FOR_SINGLE = 0x30002;
	private static final int AVATAR_CAMERA_CROP_DATA_FOR_SINGLE = 0x30003;
	private Dialog selectPhotoDialog;

	/**
	 * 显示修改logo对话框
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
					doPickPhotoAction(FROM_CAMERA_GET_DATA_FOR_SINGLE);
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
								FROM_PHOTOS_GET_DATA_FOR_SINGLE);
					} catch (ActivityNotFoundException e) {
						Toast.makeText(UpdateProductActivity.this, "没有找到照片",
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

	// 图片列表
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
							ivAddPicGuide.setVisibility(View.VISIBLE);
						} else {
							ivAddPicGuide.setVisibility(View.GONE);
						}
					}
				});
		lvPic.setAdapter(picAdapter);

		// 点击看大图
		lvPic.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				List<PicRec> picList = picAdapter.getPicRecList();
				if (null != picList && pos < picList.size()) {
					int count = picList.size();
					if (pos == count - 1) {
						showSelectPhotoRem();
					} else {
						// PicRec picRec = picList.get(pos);
						// TODO 显示图片
					}
				}
			}
		});

		// 删除图片
		lvPic.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int pos, long id) {
				int count = picAdapter.getCount();
				if (pos >= 0 && pos != count - 1) {
					PicRec picRec = picAdapter.getPicRecList().get(pos);
					showRemovePicRem(picRec);
				}
				return false;
			}
		});
	}

	private List<PicRec> waitRemoveList = new ArrayList<PicRec>();
	private MyRemDialog removePicDialog;

	// 删除图片
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
				boolean isSucRemove = false;
				if (null != picRec) {
					String picId = picRec.getId();
					if (!StringUtil.getInstance().IsEmpty(picId)) {
						waitRemoveList.add(picRec);
					}
					isSucRemove = picAdapter.removePic(picRec);
				}
				if (isSucRemove) {
					hasChanged = true;
					Toast.makeText(UpdateProductActivity.this,
							R.string.remove_pic_succeed_rem, Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(UpdateProductActivity.this,
							R.string.remove_pic_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		removePicDialog.show();
	}

	private int removePicIndex = -1;

	private void readyToRemovePic() {
		if (!NetworkUtil.getInstance().existNetwork(this)) {
			releaseScreen();
			removePicIndex = -1;
			Toast.makeText(this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		removePicIndex++;
		PicRec picRec = null;
		if (null != waitRemoveList && waitRemoveList.size() > 0) {
			picRec = waitRemoveList.get(0);
			waitRemoveList.remove(0);
		}
		if (null == picRec) {
			releaseScreen();
			removePicIndex = -1;
			Toast.makeText(this, R.string.remove_pic_failed_rem,
					Toast.LENGTH_SHORT).show();
			return;
		}
		final PicRec tmpPicRec = picRec;
		final String currMethod = "readyToRemovePic:";
		lockScreen(String.format(getString(R.string.remove_pic_sending),
				(removePicIndex + 1)));
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onFailure(Throwable t, String strMsg) {
				releaseScreen();
				removePicIndex = -1;
				if (BuildConfig.isDebug) {
					Log.e(TAG, currMethod + "onFail.Msg->" + strMsg, t);
					String exception = (null == strMsg ? "" : strMsg)
							+ ",Exception->" + t.toString();
					ExceptionRemHelper.showExceptionReport(
							UpdateProductActivity.this, exception);
				} else {
					Toast.makeText(UpdateProductActivity.this,
							R.string.remove_pic_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				if (null != t) {
					String json = t.toString();
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseRemovePic(json);
					if (ErrorCodeStant.getInstance().isSucceed(
							br.getErrorCode())) {
						waitRemoveList.remove(tmpPicRec);
						if (null != waitRemoveList && waitRemoveList.size() > 0) {
							readyToRemovePic();
						} else {
							releaseScreen();
							removePicIndex = -1;
							picIndex = -1;
							readyToUpdateProduct();
						}
					} else {
						releaseScreen();
						removePicIndex = -1;
						String message = getString(R.string.remove_pic_failed_rem);
						int errorCode = br.getErrorCode();
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
									UpdateProductActivity.this, exception);
						} else {
							Toast.makeText(UpdateProductActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					releaseScreen();
					removePicIndex = -1;
					Log.e(TAG, currMethod + "onSuccess->result t is null.");
					if (BuildConfig.isDebug) {
						String exception = currMethod
								+ "onSuccess->result t is null.";
						ExceptionRemHelper.showExceptionReport(
								UpdateProductActivity.this, exception);
					} else {
						Toast.makeText(UpdateProductActivity.this,
								R.string.remove_pic_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
		String picId = picRec.getId();
		if (null == picId)
			picId = "";
		gs.RemovePic(this, picId, callBack);
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
			picFB.configLoadingImage(R.drawable.mineral_logo);
			picFB.configLoadfailImage(R.drawable.mineral_logo);
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
				String url = rec.getUrl();
				if (null == url
						|| (null != url && url.startsWith(Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()))) {
					picFB.display(ivPic, url);
				} else {
					url = RequestUrls.SERVER_BASIC_URL + "/" + url;
					picFB.display(ivPic, url);
				}
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
		// ListView listView = (ListView)
		// root.findViewById(R.id.parent_id_list);
		dialog.setContentView(root, p);
		dialog.show();
	}

	public static final String KEY_PRODUCT_REC = "key_product_rec";
	private ProductRec mProductRec;

	private void readyToUpdateProduct() {
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
				keywordsName = keywordsID + "," + diyKeyWordsList.get(i);
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
		if (StringUtil.getInstance().IsEmpty(description)) {
			releaseScreen();
			Toast.makeText(this, R.string.add_prod_detail_desc_not_empty,
					Toast.LENGTH_SHORT).show();
			return;
		}
		String name = mProductRec.getName();
		if (StringUtil.getInstance().IsEmpty(name)) {
			releaseScreen();
			Toast.makeText(this, R.string.add_prod_detail_name_not_empty,
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
		lockScreen(getString(R.string.update_product_send_ing));
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onFailure(Throwable t, String strMsg) {
				releaseScreen();
				System.out.println("===strMsg===="+strMsg);
				if (BuildConfig.isDebug) {
					Log.e(TAG, currMethod + "onFail.Msg->" + strMsg, t);
					String exception = (null == strMsg ? "" : strMsg)
							+ ",Exception->" + t.toString();
					ExceptionRemHelper.showExceptionReport(
							UpdateProductActivity.this, exception);
				} else {
					System.out.println("=====update_product_failed_rem=======");
					Toast.makeText(UpdateProductActivity.this,
							R.string.update_product_failed_rem,
							Toast.LENGTH_SHORT).show();
				}
				super.onFailure(t, strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					System.out.println("======onSuccess=====");
					ServerResponseParser parser = new ServerResponseParser();
					ResponseAddProduct response = (ResponseAddProduct) parser
							.parseAddProduct(json);
					Log.e("wangxu", response.getMessage());
					if (ErrorCodeStant.getInstance().isSucceed(
							response.getErrorCode())) {
						// 发布成功，返回前一页面，进行刷新数据

						Intent data = getIntent();
						if (null == data)
							data = new Intent();
						data.putExtra(KEY_PRODUCT_REC, mProductRec);
						setResult(RESULT_OK, data);
						finish();
					} else {
						String message = getString(R.string.update_product_failed_rem);
						int errorCode = response.getErrorCode();
						switch (errorCode) {
						// TODO 登录的错误码
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
									UpdateProductActivity.this, exception);
						} else {
							Toast.makeText(UpdateProductActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Log.e(TAG, currMethod + "onSuccess->result t is null.");
					if (BuildConfig.isDebug) {
						String exception = currMethod
								+ "onSuccess->result t is null.";
						ExceptionRemHelper.showExceptionReport(
								UpdateProductActivity.this, exception);
					} else {
						Toast.makeText(UpdateProductActivity.this,
								R.string.update_product_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
		if (keywordsID == null || "".equals(keywordsID) && keywordsName == null
				|| "".equals(keywordsName)) {
			Toast.makeText(getApplicationContext(), "请选择标签", 1).show();
			releaseScreen();
		} else {
			gs.UpdateProduct(this, mProductRec, logoFile, keywordsID,
					keywordsName, galleryIds, callBack);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case FROM_PHOTOS_GET_DATA:
			Uri avatarUri = data.getData();
			String filePath = getPath(avatarUri);
			if (!AbStrUtil.isEmpty(filePath)) {
				Intent intent1 = new Intent(UpdateProductActivity.this,
						CropImageActivity.class);
				intent1.putExtra("PATH", filePath);
				startActivityForResult(intent1, AVATAR_CAMERA_CROP_DATA);
			} else {
				Toast.makeText(UpdateProductActivity.this, "未在存储卡中找到这个文件",
						Toast.LENGTH_LONG).show();
			}
			break;
		case FROM_CAMERA_GET_DATA:
			// String filePath2 = mCurrentPhotoFile.getPath();
			String filePath2 = Util.getPhotoPath(UpdateProductActivity.this,
					data);
			Intent intent2 = new Intent(UpdateProductActivity.this,
					CropImageActivity.class);
			intent2.putExtra("PATH", filePath2);
			startActivityForResult(intent2, AVATAR_CAMERA_CROP_DATA);
			break;

		case AVATAR_CAMERA_CROP_DATA:
			hasChanged = true;
			String avatarPath = data.getStringExtra("PATH");
			mProductRec.setImg(avatarPath);
			Trace.d(TAG, "LogoPath:" + avatarPath);
			FinalBitmap fb = getLogoFinalBitmap();
			fb.display(logo, avatarPath);
			break;
		case REQUEST_CODE_FOR_SELECT_CATEGORY:
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
				tvparentId.setText(category);
				keywordsGroup.setVisibility(View.VISIBLE);
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
			break;
		// ===================Start Photo Album========================
		case FROM_PHOTOS_GET_DATA_FOR_SINGLE:
			Uri avatarUri2 = data.getData();
			String filePath3 = getPath(avatarUri2);
			if (!AbStrUtil.isEmpty(filePath3)) {
				Intent intent1 = new Intent(UpdateProductActivity.this,
						CropImageActivity.class);
				intent1.putExtra("PATH", filePath3);
				startActivityForResult(intent1,
						AVATAR_CAMERA_CROP_DATA_FOR_SINGLE);
			} else {
				Toast.makeText(UpdateProductActivity.this, "未在存储卡中找到这个文件",
						Toast.LENGTH_LONG).show();
			}
			break;
		case FROM_CAMERA_GET_DATA_FOR_SINGLE:
			// String filePath4 = mCurrentPhotoFile.getPath();
			String path = Util.getPhotoPath(UpdateProductActivity.this, data);
			Intent intent3 = new Intent(UpdateProductActivity.this,
					CropImageActivity.class);
			intent3.putExtra("PATH", path);
			startActivityForResult(intent3, AVATAR_CAMERA_CROP_DATA_FOR_SINGLE);
			break;

		case AVATAR_CAMERA_CROP_DATA_FOR_SINGLE:
			String avatarPath2 = data.getStringExtra("PATH");
			PicRec currPicRec = new PicRec();
			currPicRec.setUrl(avatarPath2);
			picAdapter.addPic(currPicRec);
			hasChanged = true;
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
			logoFB.configBitmapMaxWidth(50);
			logoFB.configBitmapMaxHeight(50);
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
							UpdateProductActivity.this, exception);
				} else {
					Toast.makeText(UpdateProductActivity.this,
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
							readyToUpdateProduct();
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
									UpdateProductActivity.this, exception);
						} else {
							Toast.makeText(UpdateProductActivity.this, message,
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
								UpdateProductActivity.this, exception);
					} else {
						Toast.makeText(UpdateProductActivity.this,
								R.string.upload_pic_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
		GoodsService gs = new GoodsService();
		File picFile = new File(picRec.getUrl());
		String prodId = null == mProductRec ? "" : mProductRec.getId();
		if (null == prodId)
			prodId = "";
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

	private void readyToLoadProductInfo() {
		if (!NetworkUtil.getInstance().existNetwork(this)) {
			Toast.makeText(this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		final String currMethod = "readyToLoadProductInfo:";
		lockScreen(getString(R.string.update_product_load_ing));
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
							UpdateProductActivity.this, exception);
				} else {
					Toast.makeText(UpdateProductActivity.this,
							R.string.load_product_info_failed,
							Toast.LENGTH_SHORT).show();
				}
				super.onFailure(t, strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseGetSingleProductInfo(json);
					if (ErrorCodeStant.getInstance().isSucceed(
							br.getErrorCode())) {
						ResponseGetSingleProduct response = (ResponseGetSingleProduct) br;
						ProductRec prodRec = response.getProductRec();
						if (null != prodRec) {
							mProductRec = prodRec;
							initProductInfo();
						}
					} else {
						String message = getString(R.string.load_product_info_failed);
						int errorCode = br.getErrorCode();
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
									UpdateProductActivity.this, exception);
						} else {
							Toast.makeText(UpdateProductActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Log.e(TAG, currMethod + "onSuccess->result t is null.");
					if (BuildConfig.isDebug) {
						String exception = currMethod
								+ "onSuccess->result t is null.";
						ExceptionRemHelper.showExceptionReport(
								UpdateProductActivity.this, exception);
					} else {
						Toast.makeText(UpdateProductActivity.this,
								R.string.load_product_info_failed,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
		String prodId = null == mProductRec ? "" : mProductRec.getId();
		gs.GetSingleProductInfo(this, prodId, callBack);
	}

	private CityModel cityModel;
	private List<CityItemModel> provinces = new ArrayList<CityItemModel>();
	private String pId;
	private String cityPid;
	private List<CityItemModel> citys = new ArrayList<CityItemModel>();

	// private List<String>

	// 初始化我有信息
	private void initProductInfo() {
		if (null == mProductRec)
			return;

		pId = mProductRec.getProId();
		try {
			String str = Util.readStringFromFile("area",
					UpdateProductActivity.this);
			Gson gson = new Gson();
			cityModel = gson.fromJson(str, CityModel.class);
			for (int i = 0; i < cityModel.getResult().size(); i++) {
				if (cityModel.getResult().get(i).getPid().equals("0")) {
					String locaId = cityModel.getResult().get(i).getId();
					provinces.add(cityModel.getResult().get(i));
					if (pId.equals(locaId)) {
						cityPid = cityModel.getResult().get(i).getId();
					}
				}

			}
			if (cityPid != null) {
				for (int j = 0; j < cityModel.getResult().size(); j++) {
					if (cityModel.getResult().get(j).getPid().equals(cityPid)) {
						citys.add(cityModel.getResult().get(j));
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO: handle exception
		}

		tvprovinceId.setText(mProductRec.getProName());
		tvcityId.setText(mProductRec.getCityName());

		String logoUrl = mProductRec.getThumb();
		if (!StringUtil.getInstance().IsEmpty(logoUrl)) {
			FinalBitmap fb = getLogoFinalBitmap();
			String url = RequestUrls.SERVER_BASIC_URL + "/" + logoUrl;
			fb.display(logo, url);
		} else {
			logo.setImageResource(R.drawable.mineral_logo);
		}
		String name = mProductRec.getName();
		tvName.setText(name);
		String description = mProductRec.getDescription();
		// tvDesc.setVisibility(View.GONE);
		desc_editbox.setText(description);
		List<PicRec> picRecList = mProductRec.getPicList();
		if (null != picRecList && picRecList.size() > 0) {
			PicRec emptyPicRec = new PicRec();
			emptyPicRec.setId("-1");
			picRecList.add(emptyPicRec);
			picAdapter.setPicRecList(picRecList);
		}
		tvparentId.setText(mProductRec.getTagCatName());
		isSelect = true;
		keywordsGroup.setVisibility(View.VISIBLE);
		List<KeyWordsRec> keywords = mProductRec.getTags();
		if (null != keywords) {
			for (int i = 0; i < keywords.size(); i++) {
				showKeyWordsList.add(keywords.get(i).getTagName());
				adapter.setKeyWordsList(showKeyWordsList);
				selectKeyWordsList.addAll(keywords);
				adapter.notifyDataSetChanged();
				setGridViewHeightBasedOnChildren(keywords_gridview);
			}
		}
	}

	/**
	 * ===========城市dialog
	 */
	private void ShowLocationDialog() {
		String[] citysStr = new String[citys.size()];
		for (int i = 0; i < citys.size(); i++) {
			citysStr[i] = citys.get(i).getName();
		}
		AlertDialog dialog = new AlertDialog.Builder(UpdateProductActivity.this)
				.setTitle("请选择市")
				.setItems(citysStr, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mProductRec.setCityId(citys.get(which).getName());
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
		AlertDialog dialog = new AlertDialog.Builder(UpdateProductActivity.this)
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
						mProductRec.setProId(provinces.get(which).getId());

						tvcityId.setText(provinces.get(which).getName());
						tvprovinceId.setText(provinces.get(which).getName());
					}
				}).create();
		dialog.show();
	}

	private MyRemDialog exitRemDialog;

	private void showExitRemDialog() {
		if (null == exitRemDialog) {
			exitRemDialog = new MyRemDialog(this, R.style.dialog);
			exitRemDialog.setHeaderVisility(View.GONE);
			exitRemDialog.setMessage(R.string.update_exit_save_rem);
			exitRemDialog.setPosBtnText(R.string.update_exit_dialog_btn_pos);
			exitRemDialog.setNegBtnText(R.string.update_exit_dialog_btn_neg);
			exitRemDialog.setPosBtnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					btnRight.performClick();
				}
			});
			exitRemDialog.setNegBtnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
		if (null != exitRemDialog && !isFinishing()
				&& !exitRemDialog.isShowing()) {
			exitRemDialog.cancel();
		}
	}

	private boolean hasChanged = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_BACK:
			if (hasChanged) {
				showExitRemDialog();
				return true;
			} else {
				setResult(RESULT_OK);
				finish();
			}
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * --------------------------------显示分类dialog------------------------------
	 * ---------------
	 */
	private Dialog selectParentIdDialog;
	private List<String> showKeyWordsList = new ArrayList<String>();
	private List<String> diyKeyWordsList = new ArrayList<String>();

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
						Toast.makeText(UpdateProductActivity.this,
								R.string.prod_keywords_not_empty,
								Toast.LENGTH_SHORT).show();
					} else {
						mProductRec.setTagCatId(null);
						mProductRec.setTagCatName(prodKeywords);
						tvparentId.setText(prodKeywords);
						keywordsGroup.setVisibility(View.VISIBLE);
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

	private boolean isSelect = false;
	private KeyWordsAdapter adapter;
	private List<KeyWordsRec> selectKeyWordsList = new ArrayList<KeyWordsRec>();

	// 信息分类
	protected void skipToChangeCategory() {
		Intent mIntent = new Intent(UpdateProductActivity.this,
				SelectCategoryForAddProdActivity.class);
		startActivityForResult(mIntent, REQUEST_CODE_FOR_SELECT_CATEGORY);
	}

	/**
	 * --------------------------------获取标签----------
	 */

	private static final int REQUEST_CODE_FOR_SELECT_KEYWORDS = 0x20002;

	protected void skipToChangekeyWords() {
		Intent mIntent = new Intent(UpdateProductActivity.this,
				SelectKeyWordsActivity.class);
		ArrayList<String> listName = new ArrayList<String>();
		ArrayList<String> listId = new ArrayList<String>();

		for (int i = 0; i < selectKeyWordsList.size(); i++) {
			listName.add(selectKeyWordsList.get(i).getTagName());
			listId.add(selectKeyWordsList.get(i).getTagID());
		}
		mIntent.putStringArrayListExtra("listName", listName);
		mIntent.putStringArrayListExtra("listId", listId);
		mIntent.putExtra("TAG_CAT_ID", mProductRec.getTagCatId());
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
		params.height = (Util.dip2px(UpdateProductActivity.this, 60) * (count));
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
						Toast.makeText(UpdateProductActivity.this,
								R.string.prod_keywords_not_empty,
								Toast.LENGTH_SHORT).show();
					} else {
						diyKeyWordsList.add(prodKeywords);
						showKeyWordsList.add(prodKeywords);
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
