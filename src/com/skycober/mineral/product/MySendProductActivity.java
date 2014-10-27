package com.skycober.mineral.product;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.http.AjaxCallBack;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.skycober.mineral.FragBaseActivity;
import com.skycober.mineral.FragmentChangeActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.account.GoodsService;
import com.skycober.mineral.account.ServerResponseParser;
import com.skycober.mineral.bean.ProductRec;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseGetUserGoodsList;
import com.skycober.mineral.product.MyProductAdapter.OnItemLongClickListener;
import com.skycober.mineral.product.MyProductAdapter.onClick;
import com.skycober.mineral.product.MyProductAdapter.onItemClickListenner;
import com.skycober.mineral.util.BuildConfig;
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
 * 我发布的信息 我有列表
 * 
 * @author 新彬
 * 
 */
public class MySendProductActivity extends FragBaseActivity {
	private static final String TAG = "MySendProductActivity";
	private MyListView lvSendPublished;
	private MyProductAdapter productAdapter;
	// private SwitchBar switchBar;
	private List<ProductRec> prodList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		FragmentChangeActivity.slideMenu.setTag("back");
		View v = inflater.inflate(R.layout.activity_my_send_product, null);
		TextView tvTitle = (TextView) v.findViewById(R.id.title_text);
		tvTitle.setText(R.string.my_send_product_page_title);
		ImageButton btnLeft = (ImageButton) v
				.findViewById(R.id.title_button_left);
		btnLeft.setOnClickListener(btnListClickListener);
		ImageButton btnRight = (ImageButton) v
				.findViewById(R.id.title_button_right);
		// 添加信息
		btnRight.setImageResource(R.drawable.btn_box_add_selector);
		btnRight.setOnClickListener(btnAddProductClickListener);
		// 信息列表
		lvSendPublished = (MyListView) v.findViewById(R.id.lvSendPublished);
		init();

		return v;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		readyToLoadSendPublished(true, true, userId);
	}

	private String userId;

	private void init() {

		prodList = new ArrayList<ProductRec>();

		productAdapter = new MyProductAdapter(getActivity(),
				R.layout.my_send_product_listview_item, prodList);
		lvSendPublished.setAdapter(productAdapter);
		productAdapter
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public void onItemClick(final String ID) {

						MyRemDialog exitDialog = new MyRemDialog(getActivity(),
								R.style.Dialog);
						exitDialog.setTitle("移除信息");
						exitDialog.setMessage("是否移除该条信息");
						exitDialog.setPosBtnText("确定");
						exitDialog
								.setNegBtnText(R.string.exit_app_dialog_btn_cancel);
						exitDialog
								.setPosBtnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										// 移除信息
										readyToRemoveProduct(ID);
									}
								});
						exitDialog.show();

					}
				});
		lvSendPublished.setonRefreshListener(new OnRefreshListener() {
			@Override
			protected Object clone() throws CloneNotSupportedException {
				// TODO Auto-generated method stub
				return super.clone();
			}

			@Override
			public void onRefresh() {
				readyToLoadSendPublished(true, true, userId);
			}
		});
		lvSendPublished.setAddMoreListener(new OnAddMoreListener() {

			@Override
			public void onAddMore() {
				readyToLoadSendPublished(true, false, userId);
			}
		});
		productAdapter.setMakeOffSaleListenner(new onClick() {

			@Override
			public void onProdOperationBtnClick(ProductRec rec) {
			}

		});
		productAdapter.setItemClickListenner(new onItemClickListenner() {

			@Override
			public void onItemClick(ProductRec pRec) {
				// 查看信息详情
				Intent mIntent = new Intent(getActivity(),
						ProductDetailActivity.class);
				mIntent.putExtra(ProductDetailActivity.KEY_PRODUCT_REC, pRec);
				startActivityForResult(mIntent, REQUEST_CODE_SHOW_PRODUCT);
			}
		});

		userId = SettingUtil.getInstance(getActivity()).getValue(
				SettingUtil.KEY_LOGIN_USER_ID,
				SettingUtil.DEFAULT_LOGIN_USER_ID);
		readyToLoadSendPublished(true, true, userId);
	}

	private View.OnClickListener btnListClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			FragmentChangeActivity.slideMenu.toggle();
		}
	};
	private static final int REQUEST_CODE_ADD_PRODUCT = 1004;
	// 添加信息
	private View.OnClickListener btnAddProductClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent mIntent = new Intent(getActivity(), AddProductActivity.class);
			getActivity().startActivityForResult(mIntent,
					REQUEST_CODE_ADD_PRODUCT);
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Trace.d(TAG, "requestCode:" + requestCode + ",resultCode:" + resultCode
				+ ",data:" + (null == data ? "null" : data.toString()));
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
		case REQUEST_CODE_ADD_PRODUCT:
			// 添加产品成功，刷新列表数据，如果是已上架Tab选项卡，则直接重置列表信息，反之不做操作
			break;
		case REQUEST_CODE_SHOW_PRODUCT:
			// TODO 根据当前产品是否被删除、编辑，选择是否更新列表数据
			readyToLoadSendPublished(true, true, userId);

			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	};

	private static final int REQUEST_CODE_SHOW_PRODUCT = 201;
	private int offset = 0;
	private int count = 20;

	// 下载我发布的信息
	private void readyToLoadSendPublished(final Boolean isOnSale,
			final Boolean isfirst, String Id) {
		if (!NetworkUtil.getInstance().existNetwork(getActivity())) {
			Toast.makeText(getActivity(), R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (isfirst) {
			offset = 0;
			count = 20;
		}
		lockScreen(this.getResources().getString(
				R.string.get_prod_send_list_ing));
		final String currMethod = "readyToLoadSendPublished:";
		GoodsService gs = new GoodsService();
		AjaxCallBack<Object> callBack = new AjaxCallBack<Object>() {
			@Override
			public void onFailure(Throwable t, String strMsg) {
				releaseScreen();
				Log.e(TAG, currMethod + "onFailure,Msg->" + strMsg, t);
				if (BuildConfig.isDebug) {
					String msg = currMethod + "onFailure,Msg->" + strMsg
							+ ",Exception->" + t.toString();
					ExceptionRemHelper.showExceptionReport(getActivity(), msg);
				} else {
					Toast.makeText(getActivity(),
							R.string.get_prod_send_list_failed,
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
					BaseResponse br = parser.parseGetUserGoodsList(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						ResponseGetUserGoodsList response = (ResponseGetUserGoodsList) br;
						List<ProductRec> list = response.getProductRecs();
						Log.e("wangxu", "list=" + list.size());
						list.size();
						if (isfirst) {
							prodList.clear();
							prodList.addAll(list);
							offset += list.size();
							Boolean ishas = (list.size() < 20) ? false : true;
							lvSendPublished.onScrollComplete(ishas);
							refreshList(isOnSale);
						} else {
							prodList.addAll(list);
							offset += prodList.size();
							Boolean ishas = (list.size() < 20) ? false : true;
							lvSendPublished.onScrollComplete(ishas);
							refreshList(isOnSale);
						}

					} else {
						String message = getString(R.string.get_prod_send_list_failed);
						String msg = getString(R.string.get_prod_send_list_failed);
						if (null == br) {
							msg = "startToGetMyFavsList: Result:BaseResponse is null.";
						} else {
							int errorCode = br.getErrorCode();
							switch (errorCode) {

							default:
								msg = br.getMessage();
								message = br.getMessage();
								SettingUtil.getInstance(getActivity())
										.clearLoginInfo();
								FragmentChangeActivity.leftFragment
										.refreshLoginState(true);
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
								R.string.get_prod_send_list_failed,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};

		String moffset = String.valueOf(offset);
		String mcount = String.valueOf(count);
		String misOnSale = isOnSale ? "1" : "0";
		gs.GetUsersGoodsList(getActivity(), Id, moffset, mcount, misOnSale,
				callBack);
	}

	/**
	 * 移除藏品
	 */
	protected void readyToRemoveProduct(String prodId) {
		if (!NetworkUtil.getInstance().existNetwork(getActivity())) {
			Toast.makeText(getActivity(), R.string.network_disable_error,
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
					ExceptionRemHelper.showExceptionReport(getActivity(), msg);
				} else {
					Toast.makeText(getActivity(),
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

						readyToLoadSendPublished(true, true, userId);

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
								R.string.remove_prod_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};

		gs.RemoveProduct(getActivity(), prodId, callBack);
	}

	protected void refreshList(Boolean isOnsale) {
		productAdapter.notifyDataSetChanged();
		lvSendPublished.onRefreshComplete();
	}

	protected void refreshListForSale(Boolean isOnsale) {
		readyToLoadSendPublished(isOnsale, true, userId);
		productAdapter.notifyDataSetChanged();

	}
}
