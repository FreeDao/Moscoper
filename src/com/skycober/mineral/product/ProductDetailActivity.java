package com.skycober.mineral.product;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.util.AbFileUtil;
import com.ab.util.AbStrUtil;
import com.skycober.mineral.BaseActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.account.GoodsService;
import com.skycober.mineral.account.HomePageActivity;
import com.skycober.mineral.account.LoginActivity;
import com.skycober.mineral.account.ServerResponseParser;
import com.skycober.mineral.account.ZoneActivity;
import com.skycober.mineral.bean.AvatarRec;
import com.skycober.mineral.bean.CommentRec;
import com.skycober.mineral.bean.PicRec;
import com.skycober.mineral.bean.ProductRec;
import com.skycober.mineral.bean.UserRec;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseFavPost;
import com.skycober.mineral.network.ResponseGetCommentList;
import com.skycober.mineral.network.ResponseGetProductDetail;
import com.skycober.mineral.network.ResponseGetSingleProduct;
import com.skycober.mineral.network.ResponsePraiseAndBlack;
import com.skycober.mineral.product.image_fragment.ImageFragment;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.CalendarUtil;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.MyListView;
import com.skycober.mineral.util.MyListView.OnAddMoreListener;
import com.skycober.mineral.util.MyListView.OnRefreshListener;
import com.skycober.mineral.util.NetworkUtil;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.Trace;
import com.skycober.mineral.util.Util;
import com.skycober.mineral.widget.HorizontalListView;
import com.skycober.mineral.widget.MyRemDialog;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.UMSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;

/**
 * ��Ϣ����
 * 
 * @author Yes366
 * 
 */
public class ProductDetailActivity extends BaseActivity {
	private static final String TAG = "ProductDetailActivity";
	/**
	 * ����ֵΪProductRec����
	 */
	public static final String KET_REC_NUMBER = "key_rec_number";
	public static final String KEY_PRODUCT_REC = "key_product_rec";
	public static final String KEY_CATEGORY_NAME = "key_category_name";
	private ProductRec productRec;
	@ViewInject(id = R.id.title_button_left)
	ImageButton btnLeft;
	@ViewInject(id = R.id.title_button_right)
	ImageButton btnRight;
	@ViewInject(id = R.id.title_text)
	TextView tvTitle;
	@ViewInject(id = R.id.lvComments)
	MyListView lvComments;
	private HorizontalListView lvPic;
	public static final String UMServiceName = "com.umeng.share";
	private UMSocialService controller;
	private Boolean isMine;
	private FinalBitmap logo;
	private ViewGroup headerView;
	private CommentAdpater commentAdpater;
	private ViewGroup btnOperation, btnReport;
	private TextView tvOperation, tvCommentCount;
	private static final int BIGPIC_FOR_RESULT = 1333;
	private static final int FROM_PHOTOS_GET_DATA_FOR_SINGLE = 0x30001;
	private static final int FROM_CAMERA_GET_DATA_FOR_SINGLE = 0x30002;
	private static final int AVATAR_CAMERA_CROP_DATA_FOR_SINGLE = 0x30003;
	private ImageView productLogo;
	private static final int FOR_LOGIN_RESULT = 1212;
	public static final String KEY_POST_NUMBER = "key_post_number";
	private static final int REQUEST_CODE_ADD_COMMENT = 0x20005;
	private static final int REQUEST_CODE_EDIT_PRODUCT = 0x20006;
	public static final int TYPE_REMOVE_PRODUCT = 40001;
	public static final int TYPE_EDIT_PRODUCT = 40002;
	public static final String KEY_EDIT_TYPE = "key_edit_type";
	private Dialog selectLogoDialog;
	private Intent dataIntent;
	private Dialog operationDialog;
	private MyRemDialog removeProdDialog;
	private TextView praise_btn, black_btn, chat_btn;

	private Dialog myOpearationDialog;
	// private String mcategoryName;
	private Intent mIntent;
	private int recNumber = 0;
	// ��ȡ���޵��û���id
	private String[] praise_ids;
	// ��ȡ�����û�id
	private String[] black_ids;
	// �Ƿ��¼
	boolean isLogin = false;
	/* ���յ���Ƭ�洢λ�� */
	public static File PHOTO_DIR = null;
	// ��������յõ���ͼƬ
	public File mCurrentPhotoFile;
	// �Ƿ���
	private boolean IsPraise = false;
	// �Ƿ�����
	private boolean IsBlackUser = false;

	private Handler handler;

	private List<ImageFragment> fragmentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_detail);
		// InitData();
		// InitTopBar();
		// InitListComments();
		// ��������SSO handler
		controller = UMServiceFactory.getUMSocialService(UMServiceName,
				RequestType.SOCIAL);
		controller.getConfig().setSinaSsoHandler(new SinaSsoHandler());
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		InitData();
		InitTopBar();
		InitListComments();
	}

	private ViewPager imageViewPage;

	public ImageFragment newInstance(String url) {
		ImageFragment fragment = null;
		if (fragment == null) {
			fragment = new ImageFragment();
			Bundle bundle = new Bundle();
			bundle.putString("imageUrl", url);
			fragment.setArguments(bundle);
		}
		return fragment;

	}

	private void InitListComments() {
		if (headerView != null) {
			if (lvComments.getHeaderViewsCount() != 0) {
				lvComments.removeHeaderView(headerView);
			}
		}
		headerView = (ViewGroup) LayoutInflater.from(this).inflate(
				R.layout.product_detail_head_view, null);

		// imageViewPage = (ViewPager)
		// headerView.findViewById(R.id.imageViewPage);

		productLogo = (ImageView) headerView.findViewById(R.id.product_logo);
		chat_btn = (TextView) headerView.findViewById(R.id.chat_btn);

		logo = FinalBitmap.create(this);
		logo.configBitmapMaxHeight(80);
		logo.configBitmapMaxWidth(80);
		logo.configLoadingImage(R.drawable.mineral_logo);
		String plogo = productRec.getThumb();
		if (!StringUtil.getInstance().IsEmpty(plogo)) {
			String url = RequestUrls.SERVER_BASIC_URL + "/" + plogo;
			logo.clearCache();
			logo.flushCache();
			logo.display(productLogo, url);
		} else {
			productLogo.setImageResource(R.drawable.mineral_logo);
		}
		// ����鿴��ͼ
		productLogo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ProductDetailActivity.this,
						BigPicImgActivity.class);
				intent.putExtra(BigPicImgActivity.KET_FOR_BIGPIC,
						productRec.getOriginalImg());
				startActivityForResult(intent, BIGPIC_FOR_RESULT);
				overridePendingTransition(R.anim.my_scale_action,
						R.anim.my_alpha_action);
			}
		});
		tvCommentCount = (TextView) headerView.findViewById(R.id.comment_count);
		// ��Ʒ����
		TextView productName = (TextView) headerView
				.findViewById(R.id.product_name);
		productName.setText(productRec.getName());
		Log.e("anshuai", productRec.getName() + "AAAA");
		// ��Ʒ����
		TextView categoryName = (TextView) headerView
				.findViewById(R.id.category_name);
		categoryName.setText("������" + productRec.getTagCatName());
		// ��Ʒ����ʱ��
		TextView addTime = (TextView) headerView
				.findViewById(R.id.product_add_time);
		addTime.setText("�����ڣ�"
				+ CalendarUtil.GetPostCommentTime(productRec.getAddTime() * 1000l));
		// ��Ʒ����
		TextView description = (TextView) headerView
				.findViewById(R.id.product_description);
		description.setText(productRec.getDescription());
		// ��Ʒ������
		TextView productSupplier = (TextView) headerView
				.findViewById(R.id.product_supplier);
		productSupplier.setText(productRec.getRealName());
		productSupplier.setOnClickListener(btnSupplierClickListener);
		// ��ӹ�ע�����ʿռ䣩
		TextView attention_supplier_btn = (TextView) headerView
				.findViewById(R.id.attention_supplier_btn);
		attention_supplier_btn.setOnClickListener(btnSupplierClickListener);
		// �ٱ�
		btnReport = (ViewGroup) headerView.findViewById(R.id.btnReport);
		// �ղ�
		btnOperation = (ViewGroup) headerView.findViewById(R.id.btnOperation);
		tvOperation = (TextView) headerView.findViewById(R.id.tvOperation);
		// ��
		praise_btn = (TextView) headerView.findViewById(R.id.praise_btn);
		// praise_btn.setVisibility(View.GONE);
		praise_btn.setOnClickListener(onPraiseClickListener);
		isPraise();
		// ���ڣ����Σ�
		black_btn = (TextView) headerView.findViewById(R.id.black_btn);
		if (!isLogin) {
			black_btn.setVisibility(View.GONE);
		}
		black_btn.setOnClickListener(onBlackClickListener);

		isBlack();

		// �ж��Ƿ����û��Լ���������Ϣ
		if (isMine) {
			praise_btn.setVisibility(View.GONE);
			black_btn.setVisibility(View.GONE);
			btnReport.setVisibility(View.GONE);
			chat_btn.setVisibility(View.GONE);
			switchOperationStatus(FavType.ChangeLogo);
		} else {
			praise_btn.setVisibility(View.VISIBLE);
			black_btn.setVisibility(View.VISIBLE);
			btnReport.setVisibility(View.VISIBLE);
			chat_btn.setVisibility(View.VISIBLE);
			handler = new Handler();
			chat_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (RongIM.getInstance() == null) {
								String userId = SettingUtil.getInstance(ProductDetailActivity.this)
										.getValue(SettingUtil.KEY_LOGIN_USER_ID,
												SettingUtil.DEFAULT_LOGIN_USER_ID);
								String rootKey = SettingUtil.SETTING_USER_PREF
										+userId;
								SharedPreferences sp = getSharedPreferences(
										rootKey, Context.MODE_PRIVATE);
								String token = sp.getString("token", null);

								RongIM.connect(token, new ConnectCallback() {

									@Override
									public void onSuccess(String arg0) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onError(ErrorCode arg0) {
										// TODO Auto-generated method stub

									}
								});
							}
							RongIM.getInstance().startPrivateChat(
									ProductDetailActivity.this,
									productRec.getUserId(),
									productRec.getRealName());
						}
					});
				}
			});
			if (productRec.isInFav()) {
				switchOperationStatus(FavType.Cancel);
			} else {
				switchOperationStatus(FavType.Add);
			}

		}
		// �ٱ�
		btnReport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				readyToreport(productRec.getId());
			}
		});
		// �ղ�/����ͼƬ
		btnOperation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isMine) {
					// ������û��Լ���������Ϣ ����ͼƬ
					showChangeLogoRem();
				} else {
					if (productRec.isInFav()) {
						// ȡ���ղ�
						readyToChanceFav(productRec.getId());
					} else {
						// ����ղ�
						if (!productRec.isBlackUser()) {
							readyToAddFav(productRec.getId());
						} else {
							Toast.makeText(getApplicationContext(), "���������˸��û�",
									1).show();
						}

					}
				}

			}
		});
		// ͼƬ�б�
		lvPic = (HorizontalListView) headerView.findViewById(R.id.lvPic);
		if (productRec.getPicList() != null) {
			final List<PicRec> picList = productRec.getPicList();

			PicAdapter adapter = new PicAdapter(this,
					R.layout.pic_listview_item, picList, false);
			lvPic.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Intent intent = new Intent(ProductDetailActivity.this,
							BigPicImgActivity.class);
					intent.putExtra(BigPicImgActivity.KET_FOR_BIGPIC, picList
							.get(arg2).getUrl());
					startActivityForResult(intent, BIGPIC_FOR_RESULT);
					overridePendingTransition(R.anim.my_scale_action,
							R.anim.my_alpha_action);
				}
			});
			lvPic.setAdapter(adapter);

		} else {
			lvPic.setVisibility(View.GONE);
		}
		lvComments.addHeaderView(headerView);// ����

		List<CommentRec> commentList = new ArrayList<CommentRec>();
		commentAdpater = new CommentAdpater(this,
				R.layout.product_comment_listview_item, commentList);
		lvComments.setAdapter(commentAdpater);
		lvComments.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// ��ȡ�����б�
				readyToLoadCommentList(true);
			}
		});
		lvComments.setAddMoreListener(new OnAddMoreListener() {

			@Override
			public void onAddMore() {
				readyToLoadCommentList(false);
			}
		});
		readyToLoadCommentList(true);
	}

	private void InitData() {
		isLogin = !SettingUtil
				.getInstance(this)
				.getValue(SettingUtil.KEY_LOGIN_USER_ID,
						SettingUtil.DEFAULT_LOGIN_USER_ID)
				.equalsIgnoreCase(SettingUtil.DEFAULT_LOGIN_USER_ID);
		mIntent = getIntent();
		if (null != mIntent && mIntent.hasExtra(KEY_PRODUCT_REC)) {
			productRec = (ProductRec) mIntent
					.getSerializableExtra(KEY_PRODUCT_REC);
			recNumber = mIntent.getExtras().getInt(KET_REC_NUMBER);
		}
		String praise_ids_str = SettingUtil.getInstance(
				ProductDetailActivity.this).getValue(
				SettingUtil.KEY_PRAISE_USER_ID, "");
		praise_ids = praise_ids_str.split(",");
		// �Ƿ��Ѿ�����
		for (int i = 0; i < praise_ids.length; i++) {
			if (praise_ids[i].equals(productRec.getUserId()))
				IsPraise = true;
		}
		String userId = SettingUtil.getInstance(ProductDetailActivity.this)
				.getValue(SettingUtil.KEY_LOGIN_USER_ID,
						SettingUtil.DEFAULT_LOGIN_USER_ID);
		if (userId.equals(productRec.getUserId())) {

			isMine = true;

		} else {

			isMine = false;
			readyToAddView(productRec.getId());
		}

		readyTogetProductDetail(productRec.getId());

	}

	private void InitTopBar() {
		btnLeft.setImageResource(R.drawable.back_btn_selector);
		btnLeft.setOnClickListener(btnLeftClickListener);
		btnRight.setImageResource(R.drawable.more_btn_selector);
		btnRight.setOnClickListener(btnRightClickListener);
		tvTitle.setText(R.string.product_detail_title);
	}

	private void isPraise() {
		praise_btn.setEnabled(!IsPraise);
		if (IsPraise) {
			praise_btn.setText(productRec.getPraise_num() + "������");
			praise_btn.setTextColor(Color.WHITE);
			praise_btn.setBackgroundResource(R.drawable.praise_shape);
		} else {
			IsPraise = false;
			praise_btn.setText(productRec.getPraise_num() + "������");
			praise_btn.setTextColor(Color.WHITE);
		}
	}

	// ==================================���β���=====================
	private void isBlack() {
		if (IsBlackUser) {
			black_btn.setText(productRec.getBlack_num() + "������");
			black_btn.setTextColor(Color.BLACK);
			black_btn.setBackgroundResource(R.drawable.black_shape);
		} else {
			if (productRec.getBlack_num() != null) {
				if (Integer.parseInt(productRec.getBlack_num()) != 0) {
					black_btn.setText(productRec.getBlack_num() + "������");
				} else {
					black_btn.setText(0 + "������");
				}

				black_btn.setTextColor(Color.WHITE);
				black_btn.setBackgroundResource(R.drawable.tag_shape);
			}

		}
	}

	// ����
	private View.OnClickListener btnCommmentClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (isMine) {
				myOpearationDialog.cancel();
			} else {
				operationDialog.cancel();
			}
			Intent mIntent = new Intent(ProductDetailActivity.this,
					AddCommentActivity.class);
			String prodId = null == productRec ? null : productRec.getId();
			mIntent.putExtra(AddCommentActivity.KEY_PRODUCT_ID, prodId);
			startActivityForResult(mIntent, REQUEST_CODE_ADD_COMMENT);
		}
	};
	// ��
	private View.OnClickListener onPraiseClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			readyToPriaseOrBlack(productRec.getUserId(), false, false);
		}
	};
	// ����
	private View.OnClickListener onBlackClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			readyToPriaseOrBlack(productRec.getUserId(), !IsBlackUser,
					IsBlackUser);
		}
	};
	// ����
	private View.OnClickListener btnShareClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (isMine) {
				myOpearationDialog.cancel();
			} else {
				operationDialog.cancel();
			}
			// �������
			// 1.��ȡ���������
			String shareContent = getString(R.string.share_content_format);
			String prodName = productRec.getName();
			String userId = SettingUtil.getInstance(ProductDetailActivity.this)
					.getValue(SettingUtil.KEY_LOGIN_USER_ID,
							SettingUtil.DEFAULT_LOGIN_USER_ID);
			UserRec user_rec = SettingUtil.getInstance(
					ProductDetailActivity.this).getUserInfo(userId, null);
			String userName = "";
			if (user_rec != null) {
				userName = user_rec.getRealName();
				if (userName == null) {
					userName = "";
				}
			}

			if (StringUtil.getInstance().IsEmpty(prodName))
				prodName = "����";
			String desc = productRec.getDescription();
			if (StringUtil.getInstance().IsEmpty(desc))
				desc = "";
			shareContent = String
					.format(shareContent, userName, prodName, desc);
			// 2.�����������
			Intent intentShare = new Intent(Intent.ACTION_SEND);
			intentShare.setType("text/plain");
			intentShare.putExtra(Intent.EXTRA_SUBJECT, "����");
			intentShare.putExtra(Intent.EXTRA_TEXT, shareContent);
			intentShare.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(Intent.createChooser(intentShare, getTitle()));

			// controller.setShareContent(shareContent);
			// controller.setShareImage(new UMImage(ProductDetailActivity.this,
			// R.drawable.share_bg));
			// controller.openShare(ProductDetailActivity.this, false);
		}
	};
	// ���ʿռ�
	private View.OnClickListener btnSupplierClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!v.isEnabled())
				return;
			v.setEnabled(false);
			int vid = v.getId();
			String userId = productRec.getUserId();
			if (vid == R.id.attention_supplier_btn) {
				// TODO: ��Ϊ�ӹ�ע
				// ���ʿռ�
				if (!StringUtil.getInstance().IsEmpty(userId) && isMine) {
					Intent mIntent = new Intent(ProductDetailActivity.this,
							HomePageActivity.class);
					mIntent.putExtra("isZone", true);
					startActivity(mIntent);
				} else {
					if (!StringUtil.getInstance().IsEmpty(userId)) {
						Intent mIntent = new Intent(ProductDetailActivity.this,
								ZoneActivity.class);
						mIntent.putExtra(ZoneActivity.KEY_USER_ID, userId);
						mIntent.putExtra("isBlack", productRec.isBlackUser());
						System.out.println("===getIn_black==="
								+ productRec.isBlackUser());
						startActivity(mIntent);
					} else {
						Toast.makeText(ProductDetailActivity.this,
								R.string.supplier_id_not_empty,
								Toast.LENGTH_SHORT).show();
					}
				}
			} else {
				if (!StringUtil.getInstance().IsEmpty(userId)) {
					Intent mIntent = new Intent(ProductDetailActivity.this,
							ZoneActivity.class);
					mIntent.putExtra(ZoneActivity.KEY_USER_ID, userId);
					startActivity(mIntent);
				} else {
					Toast.makeText(ProductDetailActivity.this,
							R.string.supplier_id_not_empty, Toast.LENGTH_SHORT)
							.show();
				}
			}

			v.setEnabled(true);
		}
	};
	// ����
	private View.OnClickListener btnLeftClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent data = getDataIntent();
			data.putExtra(KEY_PRODUCT_REC, productRec);
			data.putExtra(KEY_POST_NUMBER, recNumber);
			data.putExtra(KEY_EDIT_TYPE, TYPE_EDIT_PRODUCT);
			finish();
		}
	};
	//
	private View.OnClickListener btnRightClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// ��ǰ��Ʒ�������û����򵯳��˵����ϡ��¼ܣ��Ƴ������ۣ�������֮���򵯳��˵������ۡ�����
			if (isMine) {
				// �û��Լ�������
				showMyOpeartionRem();
			} else {
				showOpeartionRem();
			}
		}
	};

	// �ղذ�ť
	private void switchOperationStatus(FavType favType) {
		if (favType == FavType.Add) {
			tvOperation.setText(R.string.prod_detail_btn_fav);
			// tvOperation.setCompoundDrawablesWithIntrinsicBounds(
			// R.drawable.icon_profile_add_small, 0, 0, 0);
			// tvOperation.setCompoundDrawablePadding(2);
			// btnOperation.setBackgroundResource(R.drawable.near_by_red_btn);
		} else if (favType == FavType.Cancel) {
			tvOperation.setText(R.string.prod_detail_btn_chance_fav);
			// tvOperation.setCompoundDrawablesWithIntrinsicBounds(
			// R.drawable.icon_profile_subtract_small, 0, 0, 0);
			// tvOperation.setCompoundDrawablePadding(2);
			// btnOperation.setBackgroundResource(R.drawable.near_by_btn);
		} else if (favType == FavType.ChangeLogo) {
			tvOperation.setText(R.string.prod_detail_btn_change_logo);
			tvOperation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			tvOperation.setCompoundDrawablePadding(0);
			btnOperation.invalidate();
			// showChangeLogoRem();
		} else {
			btnOperation.invalidate();
		}
	}

	// �û��Լ���������Ϣ
	private void showMyOpeartionRem() {
		if (null == myOpearationDialog) {
			myOpearationDialog = new Dialog(this, R.style.qz_alertdialog);
		}
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.product_detail_dialog_content_view_1, null);
		// �༭
		Button btnEdit = (Button) contentView.findViewById(R.id.btnEdit);
		btnEdit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				myOpearationDialog.cancel();
				Intent mIntent = new Intent(ProductDetailActivity.this,
						UpdateProductActivity.class);
				mIntent.putExtra(UpdateProductActivity.KEY_PRODUCT_REC,
						productRec);
				startActivityForResult(mIntent, REQUEST_CODE_EDIT_PRODUCT);
			}
		});
		// �Ƴ�
		Button btnRemove = (Button) contentView.findViewById(R.id.btnRemove);
		btnRemove.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				myOpearationDialog.cancel();
				showRemoveProductRem();
			}
		});
		// ����
		Button btnComment = (Button) contentView.findViewById(R.id.btnRate);
		// ����
		Button btnShare = (Button) contentView.findViewById(R.id.btnShare);
		btnComment.setOnClickListener(btnCommmentClickListener);
		btnShare.setOnClickListener(btnShareClickListener);
		// ȡ��
		contentView.findViewById(R.id.btnCancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						myOpearationDialog.cancel();
					}
				});
		int width = getWindowManager().getDefaultDisplay().getWidth();
		myOpearationDialog.setContentView(contentView, new LayoutParams(width,
				LayoutParams.WRAP_CONTENT));
		Window window = myOpearationDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.mystyle);
		myOpearationDialog.setCanceledOnTouchOutside(true);
		myOpearationDialog.show();
	}

	// �Ƴ�
	protected void showRemoveProductRem() {
		if (null == removeProdDialog) {
			removeProdDialog = new MyRemDialog(this, R.style.dialog);
			removeProdDialog.setTitle(R.string.remove_prod_dialog_title);
			removeProdDialog.setMessage(R.string.remove_prod_dialog_message);
			removeProdDialog.setPosBtnText(R.string.remove_prod_dialog_btn_pos);
			removeProdDialog.setNegBtnText(R.string.remove_prod_dialog_btn_neg);
			removeProdDialog.setPosBtnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					removeProdDialog.cancel();
					readyToRemoveProduct();
				}
			});
			removeProdDialog.setNegBtnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					removeProdDialog.cancel();
				}
			});
			removeProdDialog.setCanceledOnTouchOutside(false);
		}
		if (null != removeProdDialog && !isFinishing()
				&& !removeProdDialog.isShowing()) {
			removeProdDialog.show();
		}
	}

	// ���˷�����������Ϣ
	private void showOpeartionRem() {
		if (null == operationDialog) {
			operationDialog = new Dialog(this, R.style.qz_alertdialog);
		}
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.product_detail_dialog_content_view_1, null);
		contentView.findViewById(R.id.btnEdit).setVisibility(View.GONE);
		contentView.findViewById(R.id.btnRemove).setVisibility(View.GONE);
		// ����
		Button btnComment = (Button) contentView.findViewById(R.id.btnRate);
		// ����
		Button btnShare = (Button) contentView.findViewById(R.id.btnShare);
		btnComment.setOnClickListener(btnCommmentClickListener);
		btnShare.setOnClickListener(btnShareClickListener);
		// ȡ��
		contentView.findViewById(R.id.btnCancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						operationDialog.cancel();
					}
				});

		int width = getWindowManager().getDefaultDisplay().getWidth();
		operationDialog.setContentView(contentView, new LayoutParams(width,
				LayoutParams.WRAP_CONTENT));
		Window window = operationDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.mystyle);
		operationDialog.setCanceledOnTouchOutside(true);
		operationDialog.show();
	}

	protected void refreshList() {
		lvComments.onRefreshComplete();
		// if(null != commentAdpater){
		// int commentNum = commentAdpater.getCount();
		// String commentDes =
		// String.format(getString(R.string.comment_count_format), commentNum);
		// tvCommentCount.setText(commentDes);
		// }
	}

	/**
	 * ����������
	 * 
	 * @author Cool
	 * 
	 */
	public class CommentAdpater extends BaseAdapter {
		private List<CommentRec> commentList;
		private int layoutId;
		private LayoutInflater inflater;
		public int offset = 0;
		public int count = 20;
		private FinalBitmap avatarFB;

		public CommentAdpater(Context context, int layoutId,
				List<CommentRec> commentList) {
			inflater = LayoutInflater.from(context);
			this.layoutId = layoutId;
			this.commentList = commentList;
			avatarFB = FinalBitmap.create(context);
			avatarFB.configBitmapMaxHeight(30);
			avatarFB.configBitmapMaxHeight(30);
			avatarFB.configLoadingImage(R.drawable.default_head);
			avatarFB.configLoadfailImage(R.drawable.default_head);
		}

		public List<CommentRec> getCommentList() {
			return commentList;
		}

		public void setCommentList(List<CommentRec> commentList) {
			if (null == this.commentList) {
				this.commentList = commentList;
			} else {
				this.commentList.clear();
				if (null != commentList && commentList.size() > 0) {
					this.commentList.addAll(commentList);
				}
			}
			notifyDataSetChanged();
		}

		public void appendCommentList(List<CommentRec> commentList) {
			if (null == this.commentList) {
				this.commentList = commentList;
			} else {
				if (null != commentList && commentList.size() > 0) {
					this.commentList.addAll(commentList);
				}
			}
			notifyDataSetChanged();
		}

		private int commentCount = -1;

		@Override
		public int getCount() {
			int size = commentList.size();
			if (commentCount != size) {
				commentCount = size;
				String commentDes = String.format(
						getString(R.string.comment_count_format), size);
				tvCommentCount.setText(commentDes);
			}
			return size;
		}

		@Override
		public Object getItem(int position) {
			return commentList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView) {
				convertView = inflater.inflate(layoutId, null);
			}
			TextView tvContent = (TextView) convertView
					.findViewById(R.id.reply_description);
			TextView tvUserName = (TextView) convertView
					.findViewById(R.id.user_name);
			TextView tvSendDate = (TextView) convertView
					.findViewById(R.id.reply_send_date);
			ImageView ivAvatar = (ImageView) convertView
					.findViewById(R.id.user_avatar);
			CommentRec rec = commentList.get(position);
			tvContent.setText(rec.getContent());
			tvUserName.setText(rec.getUserName());
			tvSendDate.setText(CalendarUtil.GetPostCommentTime(rec
					.getSendTime() * 1000l));
			AvatarRec avatarRec = rec.getAvatar();
			if (null != avatarRec
					&& !StringUtil.getInstance().IsEmpty(avatarRec.getBig())) {
				String url = avatarRec.getBig();
				if (null != url) {
					url = RequestUrls.SERVER_BASIC_URL + "/" + url;
					Trace.e(TAG, "comment:url->" + url);
					avatarFB.display(ivAvatar, url);
				}
			} else {
				ivAvatar.setImageResource(R.drawable.default_head);
			}
			return convertView;
		}

	}

	/**
	 * ˢ�²�Ʒ��Ϣ
	 */
	private void refreshProductInfo() {
		if (null == productRec)
			return;
		productLogo = (ImageView) headerView.findViewById(R.id.product_logo);
		String plogo = productRec.getThumb();
		if (!StringUtil.getInstance().IsEmpty(plogo)) {
			String url = RequestUrls.SERVER_BASIC_URL + "/" + plogo;
			logo.clearCache();
			logo.flushCache();
			logo.display(productLogo, url);
		} else {
			productLogo.setImageResource(R.drawable.default_mineral);
		}
		TextView productName = (TextView) headerView
				.findViewById(R.id.product_name);
		productName.setText(productRec.getName());
		TextView categoryName = (TextView) headerView
				.findViewById(R.id.category_name);
		categoryName.setText("������" + productRec.getTagCatName());
		TextView addTime = (TextView) headerView
				.findViewById(R.id.product_add_time);
		addTime.setText("�����ڣ�"
				+ CalendarUtil.GetPostCommentTime(productRec.getAddTime() * 1000l));
		TextView description = (TextView) headerView
				.findViewById(R.id.product_description);
		description.setText(productRec.getDescription());
		TextView productSupplier = (TextView) headerView
				.findViewById(R.id.product_supplier);
		productSupplier.setText(productRec.getRealName());
		if (isMine) {
			switchOperationStatus(FavType.ChangeLogo);
		} else {
			if (productRec.isInFav()) {
				switchOperationStatus(FavType.Cancel);
			} else {
				switchOperationStatus(FavType.Add);
			}
		}
		if (productRec.getPicList() != null) {
			final List<PicRec> picList = productRec.getPicList();
			PicAdapter adapter = (PicAdapter) lvPic.getAdapter();
			adapter.setPicList(picList);
			lvPic.setVisibility(View.VISIBLE);
		} else {
			lvPic.setVisibility(View.GONE);
		}
	}

	/**
	 * ---------------------------------------------------����������----------------
	 * --------��ʼ--------------------
	 */
	/**
	 * ȡ������
	 */

	/**
	 * �Ƴ���Ʒ
	 */
	protected void readyToRemoveProduct() {
		if (!NetworkUtil.getInstance().existNetwork(this)) {
			Toast.makeText(this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		lockScreen(getString(R.string.remove_prod_send_ing));
		final String currMethod = "readyToRemoveProduct:";
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
					ExceptionRemHelper.showExceptionReport(
							ProductDetailActivity.this, msg);
				} else {
					Toast.makeText(ProductDetailActivity.this,
							R.string.remove_prod_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					Log.e(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseRemoveProduct(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						Intent data = getDataIntent();
						data.putExtra(KEY_PRODUCT_REC, productRec);
						data.putExtra(KEY_POST_NUMBER, recNumber);
						data.putExtra(KEY_EDIT_TYPE, TYPE_REMOVE_PRODUCT);
						setResult(RESULT_OK, data);
						finish();
					} else {
						String message = getString(R.string.remove_prod_failed_rem);
						String msg = getString(R.string.remove_prod_failed_rem);
						if (null == br) {
							msg = "readyToRemoveProd: Result:BaseResponse is null.";
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
									ProductDetailActivity.this, msg);
						} else {
							Toast.makeText(ProductDetailActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								ProductDetailActivity.this, msg);
					} else {
						Toast.makeText(ProductDetailActivity.this,
								R.string.remove_prod_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
		String prodId = productRec.getId();
		gs.RemoveProduct(this, prodId, callBack);
	}

	/**
	 * ��ȡ�����б�
	 * 
	 * @param isFirst
	 */
	private void readyToLoadCommentList(final boolean isFirst) {
		if (!NetworkUtil.getInstance().existNetwork(this)) {
			Toast.makeText(this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		lockScreen(getString(R.string.comment_list_get_ing));
		if (isFirst) {
			commentAdpater.offset = 0;
			commentAdpater.count = 20;
		}
		final String currMethod = "readyToLoadCommentList:";
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onFailure(Throwable t, String strMsg) {
				releaseScreen();
				Log.e(TAG, currMethod + "onFailure,Msg->" + strMsg, t);
				if (BuildConfig.isDebug) {
					String msg = currMethod + "onFailure,Msg->" + strMsg
							+ ",Exception->" + t.toString();
					ExceptionRemHelper.showExceptionReport(
							ProductDetailActivity.this, msg);
				} else {
					Toast.makeText(ProductDetailActivity.this,
							R.string.comment_list_get_failed,
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
					BaseResponse br = parser.parseGetCommentList(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseGetCommentList response = (ResponseGetCommentList) br;
						List<CommentRec> list = response.getCommentList();
						if (isFirst) {
							commentAdpater.setCommentList(list);
						} else {
							commentAdpater.appendCommentList(list);
						}
						commentAdpater.offset += list.size();
						Boolean ishas = (list.size() < 20) ? false : true;
						lvComments.onScrollComplete(ishas);
						refreshList();
					} else {
						String message = getString(R.string.comment_list_get_failed);
						String msg = getString(R.string.comment_list_get_failed);
						if (null == br) {
							msg = "readyToLoadCommentList: Result:BaseResponse is null.";
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
									ProductDetailActivity.this, msg);
						} else {
							Toast.makeText(ProductDetailActivity.this, message,
									Toast.LENGTH_SHORT).show();

						}
					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								ProductDetailActivity.this, msg);
					} else {
						Toast.makeText(ProductDetailActivity.this,
								R.string.comment_list_get_failed,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
		String prodId = productRec.getId();
		String offset = String.valueOf(commentAdpater.offset);
		String count = String.valueOf(commentAdpater.count);
		gs.GetCommentList(this, prodId, offset, count, callBack);
	}

	/**
	 * �޻�������
	 * 
	 * @param Id
	 *            userid
	 * @param isBlack
	 *            trueִ�����ڲ�����falseִ���޲���
	 */
	private void readyToPriaseOrBlack(final String Id, final boolean isBlack,
			final boolean IsNotBlack) {
		if (!NetworkUtil.getInstance().existNetwork(this)) {
			Toast.makeText(this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		final String currMethod = "readyToPriaseOrBlack:";
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parsePraiseAndBlack(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponsePraiseAndBlack response = (ResponsePraiseAndBlack) br;
						if (isBlack) {
							IsBlackUser = true;
							Toast.makeText(ProductDetailActivity.this,
									"�����Σ���ȥ���������İɣ�", Toast.LENGTH_SHORT).show();
							Intent data = getDataIntent();
							if (data.hasExtra(KEY_EDIT_TYPE)) {
								int editType = data.getIntExtra(KEY_EDIT_TYPE,
										-1);
								if (editType != TYPE_REMOVE_PRODUCT) {
									data.putExtra(KEY_EDIT_TYPE,
											TYPE_EDIT_PRODUCT);
								}
							} else {
								data.putExtra(KEY_EDIT_TYPE, TYPE_EDIT_PRODUCT);
							}
							data.putExtra(KEY_PRODUCT_REC, productRec);
							data.putExtra(KEY_POST_NUMBER, recNumber);
							setResult(RESULT_OK, data);
							finish();
						} else if (IsNotBlack) {
							IsBlackUser = false;
							Toast.makeText(ProductDetailActivity.this,
									"��ȡ�����Σ���", Toast.LENGTH_SHORT).show();
							readyTogetProductDetail(productRec.getId());
							isBlack();
							// readyTogetProductDetail(Id)
						} else {

							IsPraise = true;
							isPraise();
							praise_btn.setText((Integer.parseInt(productRec
									.getPraise_num()) + 1) + "������");
							String saveStrPraise = "";
							int maxLeanth = praise_ids.length > 20 ? 20
									: praise_ids.length;
							for (int i = 0; i < maxLeanth; i++) {
								saveStrPraise = saveStrPraise
										+ response.getUserId();
								saveStrPraise = saveStrPraise + ","
										+ praise_ids[i];
							}
							SettingUtil.getInstance(ProductDetailActivity.this)
									.saveValue(SettingUtil.KEY_PRAISE_USER_ID,
											saveStrPraise);
						}

					} else {
						String message = "�����е㲻������";
						String msg = "�����е㲻������";
						if (null == br) {
							msg = "readyToPriaseOrBlack: Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {
							case 7:
								Intent intent = new Intent(
										ProductDetailActivity.this,
										LoginActivity.class);
								startActivityForResult(intent, FOR_LOGIN_RESULT);
								break;
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
									ProductDetailActivity.this, msg);
						} else {
							Toast.makeText(ProductDetailActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								ProductDetailActivity.this, msg);
					} else {
						Toast.makeText(ProductDetailActivity.this, "�����е㲻������",
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}

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
							ProductDetailActivity.this, msg);
				} else {
					Toast.makeText(ProductDetailActivity.this, "�����е㲻������",
							Toast.LENGTH_SHORT).show();
				}
				super.onFailure(t, strMsg);
			}

		};
		if (isBlack) {
			gs.ToBlack(this, Id, callBack);
		} else if (IsNotBlack) {
			gs.NotBlack(this, Id, callBack);
		} else {
			gs.ToPriase(this, Id, callBack);
		}
	}

	/**
	 * ����ղ�
	 * 
	 * @param Id
	 */
	private void readyToAddFav(final String Id) {
		if (!NetworkUtil.getInstance().existNetwork(this)) {
			Toast.makeText(this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		lockScreen(this.getResources().getString(R.string.add_fav_send_ing));
		final String currMethod = "readyToAddFav:";
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseAddFav(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseFavPost response = (ResponseFavPost) br;
						String id = response.getId();
						if (Id.equals(id)) {
							productRec.setInFav(true);
							int favNum = (int) productRec.getCollectUserNum() + 1;
							productRec.setCollectUserNum(favNum);
							Intent data = getDataIntent();
							data.putExtra(KEY_EDIT_TYPE, TYPE_EDIT_PRODUCT);
							setResult(RESULT_OK, data);
							commentAdpater.notifyDataSetChanged();
							switchOperationStatus(FavType.Cancel);
						}
					} else {
						String message = getString(R.string.add_fav_failed_rem);
						String msg = getString(R.string.add_fav_failed_rem);
						if (null == br) {
							msg = "parseAddFav: Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {
							case 7:
								Intent intent = new Intent(
										ProductDetailActivity.this,
										LoginActivity.class);
								startActivityForResult(intent, FOR_LOGIN_RESULT);
								break;
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
									ProductDetailActivity.this, msg);
						} else {
							Toast.makeText(ProductDetailActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								ProductDetailActivity.this, msg);
					} else {
						Toast.makeText(ProductDetailActivity.this,
								R.string.add_fav_failed_rem, Toast.LENGTH_SHORT)
								.show();
					}
				}
				super.onSuccess(t);
			}

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
							ProductDetailActivity.this, msg);
				} else {
					Toast.makeText(ProductDetailActivity.this,
							R.string.add_fav_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
			}

		};
		gs.ToAddFav(this, Id, callBack);
	}

	/**
	 * ���ӵ����
	 * 
	 * @param Id
	 */
	private void readyToAddView(final String Id) {
		if (!NetworkUtil.getInstance().existNetwork(this)) {
			Toast.makeText(this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		// lockScreen(this.getResources().getString("����������"));
		final String currMethod = "readyToAddView:";
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseAddFav(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseFavPost response = (ResponseFavPost) br;
						String id = response.getId();
						if (Id.equals(id)) {
							// productRec.setInFav(true);
							// int favNum = (int) productRec.getCollectUserNum()
							// + 1;
							// productRec.setCollectUserNum(favNum);
							// Intent data = getDataIntent();
							// data.putExtra(KEY_EDIT_TYPE, TYPE_EDIT_PRODUCT);
							// setResult(RESULT_OK, data);
							// commentAdpater.notifyDataSetChanged();
							// switchOperationStatus(FavType.Cancel);
						}
					} else {
						String message = getString(R.string.add_fav_failed_rem);
						String msg = getString(R.string.add_fav_failed_rem);
						if (null == br) {
							msg = "parseAddFav: Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {
							case 7:
								Intent intent = new Intent(
										ProductDetailActivity.this,
										LoginActivity.class);
								startActivityForResult(intent, FOR_LOGIN_RESULT);
								break;
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
									ProductDetailActivity.this, msg);
						} else {
							Toast.makeText(ProductDetailActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								ProductDetailActivity.this, msg);
					} else {
						Toast.makeText(ProductDetailActivity.this,
								R.string.add_fav_failed_rem, Toast.LENGTH_SHORT)
								.show();
					}
				}
				super.onSuccess(t);
			}

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
							ProductDetailActivity.this, msg);
				} else {
					Toast.makeText(ProductDetailActivity.this, "",
							Toast.LENGTH_SHORT).show();
				}
				super.onFailure(t, strMsg);
			}

		};
		gs.ToAddView(this, Id, callBack);
	}

	private Intent getDataIntent() {
		if (null == dataIntent) {
			dataIntent = new Intent();
		}
		return dataIntent;
	}

	/**
	 * ȡ���ղ�
	 * 
	 * @param Id
	 */
	private void readyToChanceFav(final String Id) {
		if (!NetworkUtil.getInstance().existNetwork(this)) {
			Toast.makeText(this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		lockScreen(this.getResources().getString(R.string.chance_fav_send_ing));
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
					Log.e(TAG, "do");
					BaseResponse br = parser.parseAddFav(json);
					Log.e(TAG, "do");
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseFavPost response = (ResponseFavPost) br;
						String id = response.getId();
						if (Id.equals(id)) {
							productRec.setInFav(false);
							int favNum = (int) productRec.getCollectUserNum() - 1;
							productRec.setCollectUserNum(favNum);
							Intent data = getDataIntent();
							data.putExtra(KEY_EDIT_TYPE, TYPE_EDIT_PRODUCT);
							setResult(RESULT_OK, data);
							commentAdpater.notifyDataSetChanged();
							switchOperationStatus(FavType.Add);
						}

					} else {
						String message = getString(R.string.chance_fav_failed_rem);
						String msg = getString(R.string.chance_fav_failed_rem);
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
									ProductDetailActivity.this, msg);
						} else {
							Toast.makeText(ProductDetailActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								ProductDetailActivity.this, msg);
					} else {
						Toast.makeText(ProductDetailActivity.this,
								R.string.chance_fav_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}

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
							ProductDetailActivity.this, msg);
				} else {
					Toast.makeText(ProductDetailActivity.this,
							R.string.chance_fav_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
			}

		};
		gs.ToCancelFav(this, Id, callBack);
	}

	/**
	 * �ϴ�ͼƬ
	 * 
	 * @param Id
	 * @param file
	 */
	private void readyToUploadImage(final String Id, String file) {
		if (!NetworkUtil.getInstance().existNetwork(this)) {
			Toast.makeText(this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		lockScreen(this.getResources()
				.getString(R.string.upload_image_send_ing));
		final String currMethod = "readyToUploadImage:";
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {
					String json = t.toString();
					Log.e(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseGetSingleProductInfo(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseGetSingleProduct response = (ResponseGetSingleProduct) br;
						ProductRec prodRec = response.getProductRec();
						if (null != prodRec) {
							productRec = prodRec;
							String url = RequestUrls.SERVER_BASIC_URL + "/"
									+ productRec.getThumb();
							// logo.clearCache();
							// logo.flushCache();
							// logo.display(productLogo, url);
						}
					} else {
						String message = getString(R.string.upload_image_failed_rem);
						String msg = getString(R.string.upload_image_failed_rem);
						if (null == br) {
							msg = "readyToUploadImage: Result:BaseResponse is null.";
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
									ProductDetailActivity.this, msg);
						} else {

							Toast.makeText(ProductDetailActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								ProductDetailActivity.this, msg);
					} else {
						Toast.makeText(ProductDetailActivity.this,
								R.string.upload_image_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}

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
							ProductDetailActivity.this, msg);
				} else {
					Toast.makeText(ProductDetailActivity.this,
							R.string.upload_image_failed_rem,
							Toast.LENGTH_SHORT).show();
				}
				super.onFailure(t, strMsg);
			}

		};
		File files = new File(file);
		gs.uploadImage(this, Id, files, callBack);
	}

	/**
	 * �ٱ�
	 * 
	 * @param Id
	 */
	private void readyToreport(String Id) {
		if (!NetworkUtil.getInstance().existNetwork(this)) {
			Toast.makeText(this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		lockScreen("���ھٱ���Ϣ");
		final String currMethod = "readyToreport:";
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object t) {

				releaseScreen();
				if (null != t) {
					String json = t.toString();
					Log.e(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseReport(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						Toast.makeText(ProductDetailActivity.this,
								"�ٱ��ɹ������ǽ�����24Сʱ�ڽ��д���лл����֧��",
								Toast.LENGTH_SHORT).show();

					} else {
						String message = getString(R.string.chance_fav_failed_rem);
						String msg = getString(R.string.chance_fav_failed_rem);
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
									ProductDetailActivity.this, msg);
						} else {
							Toast.makeText(ProductDetailActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								ProductDetailActivity.this, msg);
					} else {
						Toast.makeText(ProductDetailActivity.this,
								R.string.chance_fav_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}

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
							ProductDetailActivity.this, msg);
				} else {
					Toast.makeText(ProductDetailActivity.this,
							R.string.chance_fav_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
			}

		};
		gs.ToReport(this, Id, callBack);
	}

	/**
	 * ��ȡ��Ʒ����
	 * 
	 * @param Id
	 */
	private void readyTogetProductDetail(String Id) {
		if (!NetworkUtil.getInstance().existNetwork(this)) {
			Toast.makeText(this, R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		lockScreen("���ڻ�ȡ����");
		final String currMethod = "readyTogetProductDetail:";
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object t) {

				releaseScreen();
				if (null != t) {
					String json = t.toString();
					Log.e(TAG, currMethod + ",Json->" + json);
					ServerResponseParser parser = new ServerResponseParser();
					BaseResponse br = parser.parseGetProductDetail(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseGetProductDetail rs = (ResponseGetProductDetail) br;
						productRec = rs.getProductRec();
						if (productRec != null) {
							IsBlackUser = productRec.isBlackUser();
							refreshProductInfo();
							isBlack();
							isPraise();

							// ����һ�ε��
							// GoodsService service = new GoodsService();
							// service.addViewCount(this, productRec.getId());
							// �˴��õ���productRec.getUserId()�Ƿ����˵�id

						}

					} else {
						String message = "��ȡʧ��";
						String msg = "��ȡʧ��";
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
									ProductDetailActivity.this, msg);
						} else {
							Toast.makeText(ProductDetailActivity.this, message,
									Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								ProductDetailActivity.this, msg);
					} else {
						Toast.makeText(ProductDetailActivity.this, "��ȡʧ��",
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}

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
							ProductDetailActivity.this, msg);
				} else {
					Toast.makeText(ProductDetailActivity.this, "��ȡʧ��",
							Toast.LENGTH_SHORT).show();
				}
				super.onFailure(t, strMsg);
			}

		};
		gs.GetGoodsDetail(this, Id, callBack);
	}

	/**
	 * ---------------------------------------------------����������----------------
	 * --------����--------------------
	 */
	/**
	 * ����------------------��ʼ
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
					doPickPhotoAction(FROM_CAMERA_GET_DATA_FOR_SINGLE);
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
						startActivityForResult(intent,
								FROM_PHOTOS_GET_DATA_FOR_SINGLE);
					} catch (ActivityNotFoundException e) {
						Toast.makeText(ProductDetailActivity.this, "û���ҵ���Ƭ",
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
	 * ���������������ȡ
	 */
	private void doPickPhotoAction(int requestCode) {
		String status = Environment.getExternalStorageState();
		// �ж��Ƿ���SD��,�����sd������sd����˵��û��sd��ֱ��ת��ΪͼƬ
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			if (null == PHOTO_DIR) {
				String photo_dir = AbFileUtil.getDefaultImageDownPathDir();
				PHOTO_DIR = new File(photo_dir);
			}
			doTakePhoto(requestCode);
		} else {
			Toast.makeText(ProductDetailActivity.this, "û�п��õĴ洢��",
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * ���ջ�ȡͼƬ
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
			Toast.makeText(ProductDetailActivity.this, "δ�ҵ�ϵͳ�������",
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * �����õ���urlת��ΪSD����ͼƬ·��
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

	/**
	 * ����------------------����
	 */
	/**
	 * ACTIVITY����
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		UMSocialService controller = UMServiceFactory.getUMSocialService(
				UMServiceName, RequestType.SOCIAL);
		UMSsoHandler sinaSsoHandler = controller.getConfig()
				.getSinaSsoHandler();
		if (sinaSsoHandler != null
				&& requestCode == UMSsoHandler.DEFAULT_AUTH_ACTIVITY_CODE) {
			sinaSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}

		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case REQUEST_CODE_ADD_COMMENT:
			Trace.d(TAG, "succeed add comment");
			readyToLoadCommentList(true);
			break;
		case REQUEST_CODE_EDIT_PRODUCT:
			if (null != data
					&& data.hasExtra(UpdateProductActivity.KEY_PRODUCT_REC)) {
				ProductRec prodRec = (ProductRec) data
						.getSerializableExtra(KEY_PRODUCT_REC);
				if (null != prodRec) {
					productRec = prodRec;
					readyTogetProductDetail(prodRec.getId());
					// refreshProductInfo();
				} else {
					Log.e(TAG,
							"update Product Info failure:Exception->Result ProductRec is null.");
				}
			} else {
				Log.e(TAG,
						"update Product Info failure:Exception->Intent is null or has not extra for productRec.");
			}
			break;
		case FROM_PHOTOS_GET_DATA_FOR_SINGLE:
			Uri avatarUri2 = data.getData();
			String filePath3 = getPath(avatarUri2);
			if (!AbStrUtil.isEmpty(filePath3)) {
				Intent intent1 = new Intent(ProductDetailActivity.this,
						CropImageActivity.class);
				intent1.putExtra("PATH", filePath3);
				startActivityForResult(intent1,
						AVATAR_CAMERA_CROP_DATA_FOR_SINGLE);
			} else {
				Toast.makeText(ProductDetailActivity.this, "δ�ڴ洢�����ҵ�����ļ�",
						Toast.LENGTH_LONG).show();
			}
			break;
		case FROM_CAMERA_GET_DATA_FOR_SINGLE:
			// String filePath4 = mCurrentPhotoFile.getPath();

			String path = Util.getPhotoPath(ProductDetailActivity.this, data);
			Intent intent3 = new Intent(ProductDetailActivity.this,
					CropImageActivity.class);
			intent3.putExtra("PATH", path);
			startActivityForResult(intent3, AVATAR_CAMERA_CROP_DATA_FOR_SINGLE);
			break;

		case AVATAR_CAMERA_CROP_DATA_FOR_SINGLE:
			String avatarPath2 = data.getStringExtra("PATH");
			readyToUploadImage(productRec.getId(), avatarPath2);
			// PicRec currPicRec = new PicRec();
			// currPicRec.setUrl(avatarPath2);
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_BACK:
			Intent data = getDataIntent();
			if (data.hasExtra(KEY_EDIT_TYPE)) {
				int editType = data.getIntExtra(KEY_EDIT_TYPE, -1);
				if (editType != TYPE_REMOVE_PRODUCT) {
					data.putExtra(KEY_EDIT_TYPE, TYPE_EDIT_PRODUCT);
				}
			} else {
				data.putExtra(KEY_EDIT_TYPE, TYPE_EDIT_PRODUCT);
			}
			data.putExtra(KEY_PRODUCT_REC, productRec);
			data.putExtra(KEY_POST_NUMBER, recNumber);
			setResult(RESULT_OK, data);
			finish();
			return true;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	public enum FavType {
		Add, Cancel, ChangeLogo, None
	}
}
