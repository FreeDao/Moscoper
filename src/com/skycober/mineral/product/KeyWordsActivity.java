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
import android.widget.AdapterView.OnItemLongClickListener;
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
import com.skycober.mineral.network.ResponseGetMyFavsList;
import com.skycober.mineral.product.KeyWordsListAdapter.OnDelectItemClick;
import com.skycober.mineral.product.KeyWordsListAdapter.OnEditItemClick;
import com.skycober.mineral.util.BuildConfig;
import com.skycober.mineral.util.ExceptionRemHelper;
import com.skycober.mineral.util.MyListView;
import com.skycober.mineral.util.SettingUtil;
import com.skycober.mineral.util.MyListView.OnAddMoreListener;
import com.skycober.mineral.util.MyListView.OnRefreshListener;
import com.skycober.mineral.util.NetworkUtil;
import com.skycober.mineral.util.StringUtil;
import com.skycober.mineral.util.Trace;
import com.skycober.mineral.widget.MyRemDialog;

/**
 * 关键字浏览+搜索 （我要列表）
 * 
 * @author Yes366
 * 
 */
public class KeyWordsActivity extends FragBaseActivity {
	private MyListView keyList;
	private static final String TAG = "KeyWordsActivity";
	private List<ProductRec> list = new ArrayList<ProductRec>();
	private KeyWordsListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		FragmentChangeActivity.slideMenu.setTag("back");
		View v = inflater.inflate(R.layout.activity_key_words_manager, null);
		TextView tvTitle = (TextView) v.findViewById(R.id.title_text);
		tvTitle.setText(R.string.key_words_manager_page_title);
		ImageButton btnLeft = (ImageButton) v
				.findViewById(R.id.title_button_left);
		btnLeft.setOnClickListener(btnListClickListener);
		ImageButton btnRight = (ImageButton) v
				.findViewById(R.id.title_button_right);
		btnRight.setVisibility(View.VISIBLE);// 添加需求
		btnRight.setImageResource(R.drawable.btn_box_add_selector);
		btnRight.setOnClickListener(btnAddKeyWordsClickListener);
		// 需求列表
		keyList = (MyListView) v.findViewById(R.id.key_words_list);
		keyList.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				startToGetKeyWordsList(true);
			}
		});
		keyList.setAddMoreListener(new OnAddMoreListener() {

			@Override
			public void onAddMore() {
				// TODO Auto-generated method stub
				startToGetKeyWordsList(false);

			}
		});
		adapter = new KeyWordsListAdapter(list,
				KeyWordsActivity.this.getActivity());

		keyList.setAdapter(adapter);
		// keyList.setOnItemLongClickListener(new OnItemLongClickListener() {
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// // TODO Auto-generated method stub
		// String idRec = adapter.getItem(position).getId();
		// showRemoveProductRem(idRec, position);
		// return false;
		// }
		// });
		adapter.setOnDelectItemClick(new OnDelectItemClick() {

			@Override
			public void onClick(String id, int pos) {
				// TODO Auto-generated method stub
				showRemoveProductRem(id, pos);

			}
		});

		adapter.setOnEditItem(new OnEditItemClick() {

			@Override
			public void onClick(ProductRec productRec) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						AddKeywordsActivity.class);
				intent.putExtra("isEdit", true);
				intent.putExtra("productRec", productRec);
				startActivity(intent);
			}
		});
		startToGetKeyWordsList(true);
		return v;

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		startToGetKeyWordsList(true);
	}

	private static final int REQUEST_CODE_ADD_KEYWORDS = 1004;
	// 添加需求
	private View.OnClickListener btnAddKeyWordsClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(KeyWordsActivity.this.getActivity(),
					AddKeywordsActivity.class);
			startActivityForResult(intent, REQUEST_CODE_ADD_KEYWORDS);

		}
	};
	private View.OnClickListener btnListClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			FragmentChangeActivity.slideMenu.toggle();
		}
	};

	private int offset = 0;
	private int count = 20;

	private void startToGetKeyWordsList(final Boolean isfirst) {

		if (!NetworkUtil.getInstance().existNetwork(getActivity())) {
			Toast.makeText(getActivity(), R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (isfirst) {
			offset = 0;
			count = 20;
		}

		lockScreen(this.getResources().getString(R.string.get_myfavslist_ing));
		final String currMethod = "startToGetMyFavsList:";
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
							R.string.get_myfavslist_failed_rem,
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
					BaseResponse br = parser.parseGetMyFavsList(json);
					if (null != br
							&& ErrorCodeStant.getInstance().isSucceed(
									br.getErrorCode())) {
						Boolean ishas = false;
						ResponseGetMyFavsList response = (ResponseGetMyFavsList) br;
						List<ProductRec> list = response.getProductRecs();
						if (list != null && list.size() > 0) {
							if (!isfirst) {
								KeyWordsActivity.this.list.addAll(list);
								offset += list.size();
								ishas = (list.size() < 20) ? false : true;
								// keyList.onScrollComplete(ishas);
								// refreshList(KeyWordsActivity.this.list);
							} else {
								KeyWordsActivity.this.list.clear();
								KeyWordsActivity.this.list.addAll(list);
								offset += list.size();
								ishas = (list.size() < 20) ? false : true;
								// keyList.onScrollComplete(ishas);
								// refreshList(KeyWordsActivity.this.list);

							}
						}
						keyList.onScrollComplete(ishas);
						refreshList(KeyWordsActivity.this.list);
					} else {

						String message = getString(R.string.get_myfavslist_failed_rem);
						String msg = getString(R.string.get_myfavslist_failed_rem);
						if (null == br) {
							msg = "startToGetMyFavsList: Result:BaseResponse is null.";
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
								R.string.get_myfavslist_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}

		};
		String moffset = String.valueOf(offset);
		String mcount = String.valueOf(count);
		gs.keyWordsManager(getActivity(), moffset, mcount, callBack);

	}

	private void refreshList(List<ProductRec> list) {
		keyList.onRefreshComplete();
		adapter.setList(list);
		adapter.notifyDataSetChanged();

	}

	// -------------------------------------------------------------------删除

	private MyRemDialog removeProdDialog;

	// 删除需求信息
	protected void showRemoveProductRem(final String id, final int pos) {
		removeProdDialog = new MyRemDialog(getActivity(), R.style.dialog);
		removeProdDialog.setTitle(R.string.remove_prod_dialog_title);
		removeProdDialog.setMessage(R.string.remove_prod_dialog_message);
		removeProdDialog.setPosBtnText(R.string.remove_prod_dialog_btn_pos);
		removeProdDialog.setNegBtnText(R.string.remove_prod_dialog_btn_neg);
		removeProdDialog.setPosBtnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				removeProdDialog.cancel();
				readyToRemoveProduct(id, pos);
			}
		});
		removeProdDialog.setNegBtnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				removeProdDialog.cancel();
			}
		});
		removeProdDialog.setCanceledOnTouchOutside(false);
		removeProdDialog.show();
	}

	protected void readyToRemoveProduct(String id, final int pos) {
		if (!NetworkUtil.getInstance().existNetwork(this.getActivity())) {
			Toast.makeText(this.getActivity(), R.string.network_disable_error,
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
							KeyWordsActivity.this.getActivity(), msg);
				} else {
					Toast.makeText(KeyWordsActivity.this.getActivity(),
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
						// 删除成功
						list.remove(pos);
						adapter.notifyDataSetChanged();

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
									KeyWordsActivity.this.getActivity(), msg);
						} else {
							Toast.makeText(KeyWordsActivity.this.getActivity(),
									message, Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								KeyWordsActivity.this.getActivity(), msg);
					} else {
						Toast.makeText(KeyWordsActivity.this.getActivity(),
								R.string.remove_prod_failed_rem,
								Toast.LENGTH_SHORT).show();
					}
				}
				super.onSuccess(t);
			}
		};
		gs.RemoveProduct(KeyWordsActivity.this.getActivity(), id, callBack);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
		case REQUEST_CODE_ADD_KEYWORDS:
			startToGetKeyWordsList(true);
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
