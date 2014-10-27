package com.skycober.mineral.product;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.http.AjaxCallBack;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.skycober.mineral.FragBaseActivity;
import com.skycober.mineral.FragmentChangeActivity;
import com.skycober.mineral.R;
import com.skycober.mineral.account.GoodsService;
import com.skycober.mineral.account.LoginActivity;
import com.skycober.mineral.account.ServerResponseParser;
import com.skycober.mineral.bean.ProductRec;
import com.skycober.mineral.network.BaseResponse;
import com.skycober.mineral.network.ErrorCodeStant;
import com.skycober.mineral.network.ResponseFavPost;
import com.skycober.mineral.network.ResponseGetMyFavsList;
import com.skycober.mineral.product.ProductAdapter.OnItemClickListener;
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
 * ���ղص���Ϣ
 * 
 * @author �±�
 * 
 */
public class MyFavProductActivity extends FragBaseActivity {
	private static final String TAG = "MyFavProductActivity";
	private MyListView lvFav;
	private ProductAdapter favAdapter;
	private List<ProductRec> prodList = new ArrayList<ProductRec>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		FragmentChangeActivity.slideMenu.setTag("back");
		View v = inflater.inflate(R.layout.activity_my_fav_product, null);
		TextView tvTitle = (TextView) v.findViewById(R.id.title_text);
		tvTitle.setText(R.string.my_fav_product_page_title);
		ImageButton btnLeft = (ImageButton) v
				.findViewById(R.id.title_button_left);
		btnLeft.setOnClickListener(btnListClickListener);
		ImageButton btnRight = (ImageButton) v
				.findViewById(R.id.title_button_right);
		btnRight.setVisibility(View.INVISIBLE);
		lvFav = (MyListView) v.findViewById(R.id.lv_fav);
		init();
		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if (resultCode != getActivity().RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case REQUEST_CODE_SHOW_PRODUCT:
			if (data != null) {
				ProductRec productRec = (ProductRec) data
						.getSerializableExtra(ProductDetailActivity.KEY_PRODUCT_REC);
				int pos = (Integer) data
						.getSerializableExtra(ProductDetailActivity.KEY_POST_NUMBER);
				prodList.get(pos).setInFav(productRec.isInFav());
				startToGetMyFavsList(true);
			}
			break;
		default:
			break;
		}
	}

	private void init() {
		boolean hasLogin = !SettingUtil
				.getInstance(getActivity())
				.getValue(SettingUtil.KEY_LOGIN_USER_ID,
						SettingUtil.DEFAULT_LOGIN_USER_ID)
				.equalsIgnoreCase(SettingUtil.DEFAULT_LOGIN_USER_ID);
		if (hasLogin) {
			// ��ȡ�ղ���Ϣ
			readyToGetMyFavsList();
		} else {
			// ��ת��¼����
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivityForResult(intent, FOR_LOGIN_RESULT);
		}
	}

	// ȡ���ղضԻ���
	private void chanceMyFvaDialog(final ProductRec rec, final int pos) {
		final MyRemDialog chanceMyFvaDialog = new MyRemDialog(getActivity(),
				R.style.dialog);

		chanceMyFvaDialog.setHeaderVisility(View.GONE);
		chanceMyFvaDialog.setMessage(R.string.chance_rem);
		chanceMyFvaDialog.setPosBtnText(R.string.chance_btn_pos);
		chanceMyFvaDialog.setNegBtnText(R.string.chance_btn_neg);
		chanceMyFvaDialog.show();
		chanceMyFvaDialog.setPosBtnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// ȡ���ղ�
				readyToChanceFav(rec.getId(), pos);
				chanceMyFvaDialog.dismiss();
			}
		});
		chanceMyFvaDialog.setCancelClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				chanceMyFvaDialog.dismiss();
			}
		});
	}

	// ��ȡ�ղ���Ϣ
	private void readyToGetMyFavsList() {
		startToGetMyFavsList(true);
		lvFav.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				startToGetMyFavsList(true);
			}
		});
		lvFav.setAddMoreListener(new OnAddMoreListener() {

			@Override
			public void onAddMore() {
				// TODO Auto-generated method stub
				startToGetMyFavsList(false);
			}
		});
		favAdapter = new ProductAdapter(getActivity(),
				R.layout.category_product_listview_item, prodList);
		// �ٱ�
		favAdapter.setReportClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(ProductRec rec, int pos) {
				// TODO Auto-generated method stub
				readyToreport(rec.getId());
			}
		});
		// ȡ���ղ�
		favAdapter.setSubFavClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(ProductRec rec, int pos) {
				// TODO Auto-generated method stub

				chanceMyFvaDialog(rec, pos);
			}
		});

		// ���������Ϣ����
		favAdapter.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(ProductRec rec, int pos) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(getActivity(),
						ProductDetailActivity.class);
				mIntent.putExtra(ProductDetailActivity.KEY_PRODUCT_REC, rec);
				startActivityForResult(mIntent, REQUEST_CODE_SHOW_PRODUCT);
			}
		});
		lvFav.setAdapter(favAdapter);
	}

	private static final int FOR_LOGIN_RESULT = 1212;
	private View.OnClickListener btnListClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			FragmentChangeActivity.slideMenu.toggle();
		}
	};

	private static final int REQUEST_CODE_SHOW_PRODUCT = 201;

	// ��ʼ��ȡ�ղ���Ϣ
	private void startToGetMyFavsList(final Boolean isfirst) {

		if (!NetworkUtil.getInstance().existNetwork(getActivity())) {
			Toast.makeText(getActivity(), R.string.network_disable_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (isfirst) {
			offset = 0;
			count = 20;
		}

		lockScreen(this.getResources().getString(R.string.get_myfavslist_ing1));
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
						ResponseGetMyFavsList response = (ResponseGetMyFavsList) br;
						List<ProductRec> list = response.getProductRecs();
						if (list != null && list.size() > 0) {
							if (!isfirst) {
								prodList.addAll(list);
								offset += prodList.size();
								Boolean ishas = (list.size() < 20) ? false
										: true;
								lvFav.onScrollComplete(ishas);
								refreshList();
							} else {
								prodList.clear();
								prodList.addAll(list);
								offset += list.size();
								Boolean ishas = (list.size() < 20) ? false
										: true;
								lvFav.onScrollComplete(ishas);
								refreshList();

							}
						}else{
							prodList.clear();
							refreshList();
						}
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
		gs.GetMyFavsList(getActivity(), moffset, mcount, callBack);

	}

	private int offset = 0;
	private int count = 20;

	private void refreshList() {
		favAdapter.notifyDataSetChanged();
		lvFav.onRefreshComplete();
	}

	private void refreshListForFav() {
		favAdapter.notifyDataSetChanged();
		// Ŀǰ�˴���һ�����жϣ�Ӧ�ü���liastview
//		if (prodList.size() == 5) {
			startToGetMyFavsList(true);
//		}
	}

	/**
	 * ȡ���ղ�
	 * 
	 * @param Id
	 * @param Pos
	 */
	private void readyToChanceFav(final String Id, final int Pos) {
		if (!NetworkUtil.getInstance().existNetwork(getActivity())) {
			Toast.makeText(getActivity(), R.string.network_disable_error,
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
							prodList.remove(prodList.get(Pos));

						}
						refreshListForFav();
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
								R.string.chance_fav_failed_rem,
								Toast.LENGTH_SHORT).show();
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
					ExceptionRemHelper.showExceptionReport(getActivity(), msg);
				} else {
					Toast.makeText(getActivity(),
							R.string.chance_fav_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
			}
		};
		gs.ToCancelFav(getActivity(), Id, callBack);
	}

	// �ٱ�
	private void readyToreport(String Id) {
		if (!NetworkUtil.getInstance().existNetwork(this.getActivity())) {
			Toast.makeText(this.getActivity(), R.string.network_disable_error,
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
						Toast.makeText(getActivity(),
								"�ٱ��ɹ������ǽ�����24Сʱ�ڽ��д���лл����֧��", Toast.LENGTH_LONG)
								.show();

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
									MyFavProductActivity.this.getActivity(),
									msg);
						} else {
							Toast.makeText(
									MyFavProductActivity.this.getActivity(),
									message, Toast.LENGTH_SHORT).show();
						}

					}
				} else {
					if (BuildConfig.isDebug) {
						String msg = "result t is null.";
						ExceptionRemHelper.showExceptionReport(
								MyFavProductActivity.this.getActivity(), msg);
					} else {
						Toast.makeText(MyFavProductActivity.this.getActivity(),
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
							MyFavProductActivity.this.getActivity(), msg);
				} else {
					Toast.makeText(MyFavProductActivity.this.getActivity(),
							R.string.chance_fav_failed_rem, Toast.LENGTH_SHORT)
							.show();
				}
				super.onFailure(t, strMsg);
			}

		};
		gs.ToReport(this.getActivity(), Id, callBack);
	}

}
