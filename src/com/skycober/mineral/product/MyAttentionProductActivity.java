package com.skycober.mineral.product;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skycober.mineral.FragBaseActivity;
import com.skycober.mineral.FragmentChangeActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.account.GoodsService;
import com.skycober.mineral.account.LoginActivity;
import com.skycober.mineral.account.ServerResponseParser;
import com.skycober.mineral.bean.PicRec;
import com.skycober.mineral.bean.ProductRec;
import com.skycober.mineral.constant.RequestUrls;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseGetMyAttentionList;
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
import com.skycober.mineral.widget.MyRemDialog;

/**
 * 我关注的信息 中意信息
 * 
 * @author 新彬
 * 
 */
public class MyAttentionProductActivity extends FragBaseActivity {
	private static final String TAG = "MyAttentionProductActivity:";

	private MyListView lvAttention;
	private ProductAdapter productAdapter;
	private List<ProductRec> prodList;
	private List<String> delectIds = new ArrayList<String>();
	private Button btnRight;
	private boolean isDe = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_my_attention_product, null);
		TextView tvTitle = (TextView) v.findViewById(R.id.title_text);
		tvTitle.setText(R.string.my_attention_product_page_title);
		ImageButton btnLeft = (ImageButton) v
				.findViewById(R.id.title_button_left);// 返回
		btnLeft.setOnClickListener(btnListClickListener);
		btnRight = (Button) v.findViewById(R.id.title_button_right);
		btnRight.setVisibility(View.VISIBLE);
		btnRight.setText("删除全部");// 删除所有中意信息
		btnRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (productAdapter.isDelect) {
					if (delectIds != null && delectIds.size() > 0) {
						dele();
					} else {
						productAdapter.setDelect(false);

						delectIds.clear();
						for (ProductRec rec : prodList) {
							rec.setCheck(false);
						}
						productAdapter.notifyDataSetInvalidated();
						btnRight.setText("删除全部");
					}
				} else {
					dele();
				}

			}
		});
		lvAttention = (MyListView) v.findViewById(R.id.lv_attention);
		lvAttention.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		prodList = getProductList();
		productAdapter = new ProductAdapter(getActivity(),
				R.layout.my_attention_product_listview_item, prodList);
		lvAttention.setAdapter(productAdapter);
		// lvAttention.setOnItemLongClickListener(new OnItemLongClickListener()
		// {
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
		// final int arg2, long arg3) {
		//
		// return true;
		// }
		// });

		return v;
	}
	
	public void dele(){
		MyRemDialog exitDialog = new MyRemDialog(getActivity(),
				R.style.Dialog);

		exitDialog.setTitle("删除中意信息");
		if (prodList.size() == 0) {
			exitDialog.setMessage("没有中意信息！！");
		} else {
			exitDialog.setMessage("确定"
					+ btnRight.getText().toString());
		}

		exitDialog.setPosBtnText("确定");
		exitDialog
				.setNegBtnText(R.string.exit_app_dialog_btn_cancel);
		exitDialog
				.setPosBtnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (prodList.size() != 0) {
							if (productAdapter.isDelect) {
								String ids = "";
								for (String id : delectIds) {
									if ("".equals(ids)) {
										ids = id;
									} else {
										ids = ids + "," + id;
									}

								}
								delectIds.clear();
								removeAttention(ids);
								productAdapter.setDelect(false);
							} else {
								removeAttention(null);
							}
						}

					}
				});
		exitDialog.show();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		init();
	}

	private static final int FOR_LOGIN_RESULT = 100;

	private void init() {
		boolean hasLogin = !SettingUtil
				.getInstance(getActivity())
				.getValue(SettingUtil.KEY_LOGIN_USER_ID,
						SettingUtil.DEFAULT_LOGIN_USER_ID)
				.equalsIgnoreCase(SettingUtil.DEFAULT_LOGIN_USER_ID);
		// 如果没登陆跳转登录界面，已登录获取中意信息列表
		if (hasLogin) {
			readyToGetMyAttentionList();
		} else {
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			intent.putExtra("isAttention", true);
			startActivityForResult(intent, FOR_LOGIN_RESULT);
		}
	}

	// 获取中意信息
	private void readyToGetMyAttentionList() {
		lvAttention.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				startToGetMyAttentionList(true);
			}
		});
		lvAttention.setAddMoreListener(new OnAddMoreListener() {

			@Override
			public void onAddMore() {
				startToGetMyAttentionList(false);
			}
		});
		startToGetMyAttentionList(true);
	}

	private int offset = 0;
	private int count = 20;

	// 获取中意信息列表
	private void startToGetMyAttentionList(final boolean isFirst) {
		if (!NetworkUtil.getInstance().existNetwork(getActivity())) {
			Toast.makeText(getActivity(), R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (isFirst) {
			offset = 0;
			count = 20;
			prodList.clear();
			productAdapter.notifyDataSetChanged();
		}
		lockScreen(this.getResources().getString(
				R.string.get_myAttentionlist_ing));
		final String currMethod = "startToGetMyAttentionList:";
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
					ExceptionRemHelper.showExceptionReport(getActivity(), msg);
				} else {
					Toast.makeText(getActivity(),
							R.string.get_myAttentionlist_failed_rem,
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
					BaseResponse br = parser.parseGetMyAttentionList(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseGetMyAttentionList response = (ResponseGetMyAttentionList) br;
						List<ProductRec> list = response.getProductRecs();
						if (list != null && list.size() > 0) {
							if (!isFirst) {
								prodList.addAll(list);
								offset += prodList.size();
								Boolean ishas = (list.size() < 20) ? false
										: true;
								lvAttention.onScrollComplete(ishas);
								refreshList();
							} else {
								prodList.clear();
								prodList.addAll(list);
								offset += list.size();
								Boolean ishas = (list.size() < 20) ? false
										: true;
								lvAttention.onScrollComplete(ishas);
								refreshList();
							}
						} else {
							lvAttention.onScrollComplete(false);
							refreshList();
						}
					} else {
						String message = getString(R.string.get_myAttentionlist_failed_rem);
						String msg = getString(R.string.get_myAttentionlist_failed_rem);
						if (null == br) {
							msg = "startToGetMyAttentionList: Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {

							default:
								SettingUtil.getInstance(getActivity())
										.clearLoginInfo();
								FragmentChangeActivity.leftFragment
										.refreshLoginState(true);
								msg = br.getMessage();
								message = br.getMessage();// TODO
								break;
							}
						}
						Log.e(TAG,
								currMethod + "errorCode:" + br.getErrorCode()
										+ "--Message:" + br.getMessage());
						if (BuildConfig.isDebug) {
							ExceptionRemHelper.showExceptionReport(
									getActivity(), msg);
						} else {
							Toast.makeText(getActivity(), message,
									Toast.LENGTH_SHORT).show();

						}
					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(getActivity(),
								msg);
					} else {
						Toast.makeText(getActivity(),
								R.string.get_myAttentionlist_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}

		};
		String moffset = String.valueOf(offset);
		String mcount = String.valueOf(count);
		gs.GetMyAttentionList(getActivity(), moffset, mcount, callBack);
	}

	protected void refreshList() {
		productAdapter.notifyDataSetChanged();
		lvAttention.onRefreshComplete();
		// productAdapter.setDelect(false);
		if (productAdapter.isDelect) {
			if (delectIds != null && delectIds.size() > 0) {
				btnRight.setText("删除" + delectIds.size() + "条信息");
			} else {
				btnRight.setText("取消");
			}

		} else {
			btnRight.setText("删除全部");
		}

		// delectIds.clear();
		// for (ProductRec rec : prodList) {
		// rec.setCheck(false);
		// }

	}

	private List<ProductRec> getProductList() {
		List<ProductRec> productList = new ArrayList<ProductRec>();
		return productList;
	}

	// 返回
	private View.OnClickListener btnListClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			FragmentChangeActivity.slideMenu.toggle();
		}
	};

	private static final int REQUEST_CODE_SHOW_PRODUCT = 201;

	public class ProductAdapter extends BaseAdapter {

		private int layoutId;
		private List<ProductRec> prodList;
		private LayoutInflater inflater;
		private FinalBitmap logoFB;
		private Context context;

		private boolean isDelect = false;

		public boolean isDelect() {
			return isDelect;
		}

		public void setDelect(boolean isDelect) {
			this.isDelect = isDelect;
		}

		public ProductAdapter(Context context, int layoutId,
				List<ProductRec> prodList) {
			inflater = LayoutInflater.from(context);
			this.prodList = prodList;
			this.layoutId = layoutId;
			this.context = context;
			// logoFB = FinalBitmap.create(context);
			// logoFB.configCalculateBitmapSizeWhenDecode(false);
			// logoFB.configLoadingImage(R.drawable.mineral_logo);
		}

		public List<ProductRec> getProdList() {
			return prodList;
		}

		public void setProdList(List<ProductRec> prodList) {
			if (null == this.prodList) {
				this.prodList = prodList;
			} else {
				this.prodList.clear();
				if (null != prodList && prodList.size() > 0) {
					this.prodList.addAll(prodList);
				}
			}
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return this.prodList.size();
		}

		@Override
		public Object getItem(int position) {
			return prodList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (null == convertView) {
				convertView = inflater.inflate(layoutId, null);
			}
			// 藏品logo
			ImageView ivLogo = (ImageView) convertView
					.findViewById(R.id.product_avatar);
			ImageView is_view = (ImageView) convertView
					.findViewById(R.id.is_view);

			// 藏品名称
			TextView tvName = (TextView) convertView
					.findViewById(R.id.product_name);
			// 藏品描述
			TextView tvDescription = (TextView) convertView
					.findViewById(R.id.product_description);
			// 藏品包含图片数
			TextView tvPicCount = (TextView) convertView
					.findViewById(R.id.product_photo);
			// 藏品被收藏数
			TextView tvFavCount = (TextView) convertView
					.findViewById(R.id.product_liked);
			// 藏品浏览数
			TextView tvVisitCount = (TextView) convertView
					.findViewById(R.id.product_viewed);
			// 藏品发布日期
			TextView tvSendDate = (TextView) convertView
					.findViewById(R.id.product_send_date);
			TextView praise_num = (TextView) convertView
					.findViewById(R.id.praise_num);
			TextView black_num = (TextView) convertView
					.findViewById(R.id.black_num);

			final CheckBox cb = (CheckBox) convertView.findViewById(R.id.check);

			final ProductRec rec = prodList.get(position);
			final int pos = position;
			String avatar = rec.getThumb();
			if (!StringUtil.getInstance().IsEmpty(avatar)) {
				String url = RequestUrls.SERVER_BASIC_URL + "/" + avatar;
				logoFB = FinalBitmap.create(context);
				logoFB.configCalculateBitmapSizeWhenDecode(false);
				logoFB.configLoadingImage(R.drawable.mineral_logo);
				logoFB.display(ivLogo, url);
			} else {
				ivLogo.setImageResource(R.drawable.mineral_logo);
			}

			if (isDelect) {
				cb.setVisibility(View.VISIBLE);
				is_view.setVisibility(View.GONE);
			} else {
				cb.setVisibility(View.GONE);
				// is_view.setVisibility(View.VISIBLE);
				if (rec.getIsView().equals("0")) {
					is_view.setVisibility(View.VISIBLE);
				} else {
					is_view.setVisibility(View.GONE);

				}
			}
			cb.setChecked(prodList.get(position).isCheck());
			tvName.setText(rec.getName());
			tvDescription.setText(rec.getDescription());
			List<PicRec> picList = rec.getPicList();

			int picCount = null == picList ? 0 : picList.size();
			tvPicCount.setText(String.valueOf(picCount));

			tvFavCount.setText(String.valueOf(rec.getCollectUserNum()));
			praise_num.setText(rec.getPraise_num() + "人赞");

			tvVisitCount.setText(String.valueOf(rec.getViewNum()));
			black_num.setText(rec.getBlack_num() + "人屏蔽");
			long sendDate = rec.getAddTime();
			tvSendDate.setText(CalendarUtil
					.GetPostCommentTime(sendDate * 1000l));

			// 长按删除
			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					for (ProductRec productRec : prodList) {
						productRec.setVisible(true);

					}
					isDelect = true;
					lvAttention.invalidateViews();

					// MyRemDialog exitDialog = new MyRemDialog(getActivity(),
					// R.style.Dialog);
					// exitDialog.setTitle("删除");
					// exitDialog.setMessage("是否删除该条中意信息");
					// exitDialog.setPosBtnText("确定");
					// exitDialog
					// .setNegBtnText(R.string.exit_app_dialog_btn_cancel);
					// exitDialog
					// .setPosBtnClickListener(new View.OnClickListener() {
					// @Override
					// public void onClick(View v) {
					// removeAttention(prodList.get(position)
					// .getId());
					// }
					// });
					// exitDialog.show();
					return true;
				}
			});
			// 点击进入详情
			convertView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isDelect) {
						if (prodList.get(position).isCheck()) {
							prodList.get(position).setCheck(false);
							cb.setChecked(false);
							delectIds.remove(prodList.get(position).getId());
							if (delectIds.size() == 0) {
								btnRight.setText("取消");
							} else {
								btnRight.setText("删除" + delectIds.size()
										+ "条信息");
							}
						} else {
							prodList.get(position).setCheck(true);
							cb.setChecked(true);
							delectIds.add(prodList.get(position).getId());
							btnRight.setText("删除" + delectIds.size() + "条信息");
						}

					} else {
						prodList.get(position).setIsView("1");
						notifyDataSetChanged();
						Intent mIntent = new Intent(getActivity(),
								ProductDetailActivity.class);
						String userId = SettingUtil.getInstance(getActivity())
								.getValue(SettingUtil.KEY_LOGIN_USER_ID,
										SettingUtil.DEFAULT_LOGIN_USER_ID);
						if (!userId.equals(rec.getUserId())) {
							rec.setViewNum(rec.getViewNum() + 1);
						}
						//
						mIntent.putExtra(ProductDetailActivity.KEY_PRODUCT_REC,
								rec);
						mIntent.putExtra(ProductDetailActivity.KEY_POST_NUMBER,
								pos);
						startActivityForResult(mIntent,
								REQUEST_CODE_SHOW_PRODUCT);
					}

				}
			});

			return convertView;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case REQUEST_CODE_SHOW_PRODUCT:
			// 处理显示后的操作逻辑
			Trace.e(TAG, "Show Product:result data->"
					+ (null == data ? "null" : data.toString()));
			if (data != null
					&& data.hasExtra(ProductDetailActivity.KEY_PRODUCT_REC)
					&& data.hasExtra(ProductDetailActivity.KEY_POST_NUMBER)
					&& data.hasExtra(ProductDetailActivity.KEY_EDIT_TYPE)) {
				ProductRec productRec = (ProductRec) data
						.getSerializableExtra(ProductDetailActivity.KEY_PRODUCT_REC);
				int pos = (Integer) data
						.getSerializableExtra(ProductDetailActivity.KEY_POST_NUMBER);
				int editType = data.getIntExtra(
						ProductDetailActivity.KEY_EDIT_TYPE,
						ProductDetailActivity.TYPE_EDIT_PRODUCT);
				// 移除记录满足以下条件即可：
				// （1）藏品做了移除操作；
				// （2）藏品做了下架操作；
				boolean isNeedRemoveProduct = false;
				if (editType == ProductDetailActivity.TYPE_REMOVE_PRODUCT) {
					isNeedRemoveProduct = true;
				} else {
					if (null != productRec) {
						// isNeedRemoveProduct = !productRec.isOnSale();
					}
				}
				if (isNeedRemoveProduct) {
					try {
						prodList.remove(pos);
					} catch (Exception e) {
						Log.e(TAG,
								"Remove ProductRec failure: pos outOfArrIndex Exception.",
								e);
					}
				} else {
					prodList.get(pos).setInFav(productRec.isInFav());
					prodList.get(pos).setCollectUserNum(
							productRec.getCollectUserNum());
				}
				productAdapter.notifyDataSetInvalidated();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 删除中意信息
	 * 
	 * @param Id
	 */
	private void removeAttention(final String Id) {
		if (!NetworkUtil.getInstance().existNetwork(getActivity())) {
			Toast.makeText(getActivity(), R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}

		final String currMethod = "removeAttention:";
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object t) {
				releaseScreen();
				if (null != t) {

					Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT)
							.show();
					startToGetMyAttentionList(true);
					// prodList.remove(pos);
					// productAdapter.setProdList(prodList);
					// productAdapter.notifyDataSetChanged();

				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(getActivity(),
								msg);
					} else {
						Toast.makeText(getActivity(),
								R.string.removew_attention_failed_rem,
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
					ExceptionRemHelper.showExceptionReport(getActivity(), msg);
				} else {
					Toast.makeText(getActivity(),
							R.string.removew_attention_failed_rem,
							Toast.LENGTH_SHORT).show();
				}
				super.onFailure(t, strMsg);
			}

		};

		lockScreen(this.getResources().getString(
				R.string.remove_attention_send_ing));
		gs.RemoveAttention(getActivity(), Id, callBack);

	}

}
